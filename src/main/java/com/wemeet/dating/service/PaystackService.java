package com.wemeet.dating.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemeet.dating.config.WemeetConfig;
import com.wemeet.dating.model.request.PaymentWebhookRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.util.TimeZone;

@Service
public class PaystackService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WemeetConfig config;

    Logger logger = LoggerFactory.getLogger(getClass());

    private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("Africa/Lagos");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setTimeZone(DEFAULT_TIMEZONE);
    }

    public void validatePaystackRequest(String paystackSignature, PaymentWebhookRequest request) throws Exception {

        String result = "";
        String HMAC_SHA512 = "HmacSHA512";
        String body = OBJECT_MAPPER.writeValueAsString(request);

        byte[] byteKey = config.getPaystackKey().getBytes("UTF-8");
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
        Mac sha512_HMAC = Mac.getInstance(HMAC_SHA512);
        sha512_HMAC.init(keySpec);
        byte[] mac_data = sha512_HMAC.
                doFinal(body.getBytes("UTF-8"));
        result = DatatypeConverter.printHexBinary(mac_data);
        if (!result.toLowerCase().equals(paystackSignature)) {
            logger.error("Not a valid paystack webhook request");
            throw new Exception("Not A valid paystack webhook request");
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + config.getPaystackKey());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("User-Agent", "devwemeetagent");
        return httpHeaders;
    }

    ;

    public <T> T createPlan(Object request, Class<T> responseType) throws Exception {
        String url = config.getPaystackBaseUrl() + "/plan";
        String response = null;

        HttpEntity<Object> entity = new HttpEntity(request, createHeaders());

        final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        response = responseEntity.getBody();

        if (String.class.equals(responseType)) {
            return (T) response;
        }
        return OBJECT_MAPPER.readValue(response, responseType);
    }

    ;

    public <T> T getPlans(Class<T> responseType) throws Exception {
        String url = config.getPaystackBaseUrl() + "/plan";
        String response = null;


        HttpEntity<Object> entity = new HttpEntity(createHeaders());

        final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            response = responseEntity.getBody();
        }

        if (String.class.equals(responseType)) {
            return (T) response;
        }
        return OBJECT_MAPPER.readValue(response, responseType);

    }

    ;

    public <T> T initializeTransaction(Object request, Class<T> responseType) throws Exception {
        String url = config.getPaystackBaseUrl() + "/transaction/initialize";
        String response = null;

        HttpEntity<Object> entity = new HttpEntity(request, createHeaders());

        final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        response = responseEntity.getBody();

        if (String.class.equals(responseType)) {
            return (T) response;
        }
        return OBJECT_MAPPER.readValue(response, responseType);
    }

    ;

    public <T> T disableSubscription(Object request, Class<T> responseType) throws Exception {
        String url = config.getPaystackBaseUrl() + "/subscription/disable";
        String response = null;

        HttpEntity<Object> entity = new HttpEntity(request, createHeaders());

        final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        response = responseEntity.getBody();

        if (String.class.equals(responseType)) {
            return (T) response;
        }
        return OBJECT_MAPPER.readValue(response, responseType);
    }

    ;

    public <T> T getSubscription(String code, Class<T> responseType) throws Exception {
        String url = config.getPaystackBaseUrl() + "/subscription/" + code;
        String response = null;


        HttpEntity<Object> entity = new HttpEntity(createHeaders());

        final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            response = responseEntity.getBody();
        }

        if (String.class.equals(responseType)) {
            return (T) response;
        }
        return OBJECT_MAPPER.readValue(response, responseType);

    }

    ;

    public <T> T verifyTransaction(String reference, Class<T> responseType) throws Exception {
        String url = config.getPaystackBaseUrl() + "/transaction/verify/" + reference;
        String response = null;

        HttpEntity<Object> entity = new HttpEntity(createHeaders());

        final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            response = responseEntity.getBody();
        }

        if (String.class.equals(responseType)) {
            return (T) response;
        }
        return OBJECT_MAPPER.readValue(response, responseType);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
