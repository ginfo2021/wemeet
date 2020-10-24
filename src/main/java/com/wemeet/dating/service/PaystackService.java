package com.wemeet.dating.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.TimeZone;

@Service
public class PaystackService {

    @Value("${paystack.secret.key}")
    private String paystackKey;

    @Value("${paystack.base.url}")
    private String paystackBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("Africa/Lagos");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setTimeZone(DEFAULT_TIMEZONE);
    }

    private HttpHeaders createHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer "+ paystackKey);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    };

    public <T> T createPlan(Object request, Class<T> responseType) throws Exception {
        String url = paystackBaseUrl+ "/plan";
        String response = null;

        HttpEntity<Object> entity = new HttpEntity(request, createHeaders());

        final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        response = responseEntity.getBody();

        if(String.class.equals(responseType)){
            return (T) response;
        }
        return OBJECT_MAPPER.readValue(response, responseType);
    };

    public <T> T getPlans(Class<T> responseType) throws Exception {
        String url = paystackBaseUrl+ "/plan";
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
            
    };

    public <T> T initializeTransaction(Object request, Class<T> responseType) throws Exception{
        String url = paystackBaseUrl+ "/transaction/initialize";
        String response = null;

        HttpEntity<Object> entity = new HttpEntity(request, createHeaders());

        final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        response = responseEntity.getBody();

        if(String.class.equals(responseType)){
            return (T) response;
        }
        return OBJECT_MAPPER.readValue(response, responseType);
    };

    public <T> T verifyTransaction(String reference, Class<T> responseType) throws Exception{
        String url = paystackBaseUrl+ "/transaction/verify/" + reference;
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
