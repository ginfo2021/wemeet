package com.wemeet.dating.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemeet.dating.model.entity.UserDevice;
import com.wemeet.dating.model.request.NotificationRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PushNotificationService {
    @Value("${app.endpoint.arn}")
    private String APP_ENDPOINT_ARN;

    @Value("${sns.topic.arn}")
    private String snsTopicWemeetARN;

    @Value("${sns.push.queue.url}")
    private String PUSH_QUEUE_URL;

    public static final String GCM_MSG_STRUCTURE = "json";
    public static final String GCM = "GCM";
    public static final String TEXT = "text";
    public static final String NOTIFICATION = "notification";
    public static final String MSG_SUBJECT = "Subject";
    public static final String MSG_BODY = "Body";
    public static final String TOKEN = "Token";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AmazonSNS amazonSNS;

    @Autowired
    private AmazonSQS amazonSQS;

    @Autowired
    UserDeviceService userDeviceService;

    public String publishNotificationTOSQS(NotificationRequest notificationrequest) throws Exception {
        try{
            ObjectMapper mapper = new ObjectMapper();
            String notificationMessage = mapper.writeValueAsString(notificationrequest);
            MessageAttributeValue attributeValue = new MessageAttributeValue();
            attributeValue.setDataType("String");
            attributeValue.setStringValue(notificationrequest.getNotificationType());
            Map<String, MessageAttributeValue> messageAttribute = new HashMap<String, MessageAttributeValue>();
            messageAttribute.put("notification_type", attributeValue); //check this
            PublishRequest publishRequest = new PublishRequest(snsTopicWemeetARN, notificationMessage);
            publishRequest.withMessageAttributes(messageAttribute);
            PublishResult publishResult = amazonSNS.publish(publishRequest);
            logger.info("MessageId - ", publishResult.getMessageId());

            return publishResult.getMessageId();
        }catch (Exception ex){
            logger.error("Unable to publish message", ex);
        }
        return null;
    }

    public void pushNotification(String message, String deviceId){
        registerWithSNS(deviceId);
        String targetArn = createEndpoint(deviceId);
        publish(message, targetArn);
    }

    public void registerWithSNS(String deviceId){
        String endpointArn = retrieveEndpointArn(deviceId);
        String token = deviceId;

        boolean updateNeeded = false;
        boolean createNeeded = (null == endpointArn);

        if (createNeeded) {
            // No platform endpoint ARN is stored; need to call createEndpoint.
            endpointArn = createEndpoint(token);
            createNeeded = false;
        }

        try {
            GetEndpointAttributesRequest geaReq =
                    new GetEndpointAttributesRequest()
                            .withEndpointArn(endpointArn);
            GetEndpointAttributesResult geaRes =
                    amazonSNS.getEndpointAttributes(geaReq);

            updateNeeded = !geaRes.getAttributes().get("Token").equals(token)
                    || !geaRes.getAttributes().get("Enabled").equalsIgnoreCase("true");
        }catch (NotFoundException nfe){
            createNeeded = true;
        }

        if (createNeeded) {
            createEndpoint(token);
        }
        System.out.println("updateNeeded = " + updateNeeded);

        if (updateNeeded) {
            // The platform endpoint is out of sync with the current data;
            // update the token and enable it.
            System.out.println("Updating platform endpoint " + endpointArn);
            Map attribs = new HashMap();
            attribs.put("Token", token);
            attribs.put("Enabled", "true");
            SetEndpointAttributesRequest saeReq =
                    new SetEndpointAttributesRequest()
                            .withEndpointArn(endpointArn)
                            .withAttributes(attribs);
            amazonSNS.setEndpointAttributes(saeReq);
        }
    }

    public void publish(String subject, String targetArn){
        PublishRequest request = new PublishRequest();
        request.setMessageStructure(GCM_MSG_STRUCTURE);
        Map<String, Map<String, String>> androidMsg = new HashMap<>();
        Map<String, String> messageMap = new HashMap<String, String>();
        messageMap.put(TEXT, subject);
        androidMsg.put(NOTIFICATION, messageMap);
        JSONObject json = new JSONObject(androidMsg);
        String message = json.toString();
        Map<String, String> msgMap = new HashMap<String, String>();
        msgMap.put(GCM, message);
        msgMap.put("default", "default message");
        JSONObject msgMapObject = new JSONObject(msgMap);
        String sendMsg = msgMapObject.toString();
        request.setTargetArn(targetArn);
        request.setMessage(sendMsg);
        amazonSNS.publish(request);
    }

    private String createEndpoint(String deviceToken){
        String endpointArn = null;
        try {
            CreatePlatformEndpointRequest cpeReq = new CreatePlatformEndpointRequest()
                    .withPlatformApplicationArn(APP_ENDPOINT_ARN).withToken(deviceToken);
            CreatePlatformEndpointResult cpeRes = amazonSNS.createPlatformEndpoint(cpeReq);
            endpointArn = cpeRes.getEndpointArn();
        }catch (InvalidParameterException ipe){
            String message = ipe.getErrorMessage();
            Pattern p = Pattern.compile(".*Endpoint (arn:aws:sns[^ ]+) already exists " + "with the same [Tt]oken.*");
            Matcher m = p.matcher(message);
            if (m.matches()) {
                // The platform endpoint already exists for this token, but with
                // additional custom data that
                // createEndpoint doesn't want to overwrite. Just use the
                // existing platform endpoint.
                endpointArn = m.group(1);
            }else {
                // Rethrow the exception, the input is actually bad.
                throw ipe;
            }
        }
        storeEndpointArn(deviceToken, endpointArn);
        return endpointArn;
    }

    /**
     * Stores the platform endpoint ARN in permanent storage for lookup next time.
     * */
    private void storeEndpointArn(String deviceId, String endpointArn) {
        // Write the platform endpoint ARN to permanent storage.
        UserDevice userDevice = userDeviceService.findOne(deviceId);
        userDevice.setEndpointArn(endpointArn);
        userDeviceService.saveUserDevice(userDevice);
    }

    /**
     * @return the ARN the app was registered under previously, or null if no
     *         platform endpoint ARN is stored.
     */
    private String retrieveEndpointArn(String deviceId) {
        // Retrieve the platform endpoint ARN from permanent storage,
        // or return null if null is stored.
        return userDeviceService.findOne(deviceId).getEndpointArn();
    }

    @SqsListener(value = "push-notification-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void listener(String notificationRequest) throws Exception {
           logger.info("Message Received using SQS Listener " + notificationRequest);
           String deviceId = null;
            try {
                if (notificationRequest != null){
                    JSONObject messageObject = new JSONObject(notificationRequest);
                    ObjectMapper mapper = new ObjectMapper();
                    NotificationRequest pushRequest = mapper.readValue(messageObject.getString("Message"), NotificationRequest.class);
                    String templateMessage = pushRequest.getNotificationText();
                    deviceId = pushRequest.getRecipientId();
                    pushNotification(templateMessage, pushRequest.getRecipientId());
                }
            }catch (EndpointDisabledException ex){
                logger.info("Endpoint Disabled exception", ex);
                UserDevice userDevice = userDeviceService.findOne(deviceId);
                userDevice.setEndpointArn(null);
                userDeviceService.saveUserDevice(userDevice);
            }catch (Exception ex){
                logger.error("SQS Listener Exception", ex);
            }

    }
}
