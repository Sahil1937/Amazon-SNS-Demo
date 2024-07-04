package com.sns.demo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;

@Service
public class AWSSNSService {

    @Value("${aws.access.key}")
    private String AWS_ACCESS_KEY;
    @Value("${aws.secret.key}")
    private String AWS_SECRET_KEY;

    @Value("${aws.sns.topic}")
    private String AWS_SNS_TOPIC;
    @Value("${aws.sns.arn}")
    private String AWS_SNS_TOPIC_ARN;

    @Value("${aws.sns.sms.type}")
    private String AWS_SNS_SMS_TYPE;
    @Value("${aws.sns.sms.type.value}")
    private String AWS_SNS_SMS_TYPE_VALUE;
    @Value("${aws.sns.sms.data.type}")
    private String AWS_SNS_DATA_TYPE;

    private AWSCredentials awsCredentials() {
        AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);

        return credentials;
    }

    private AmazonSNSClient snsClientBuilder() {
        AmazonSNSClient snsClient = (AmazonSNSClient) AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                .withRegion(Regions.AP_SOUTH_1)
                .build();
        return snsClient;
    }

    private Map<String, MessageAttributeValue> buildSMSAttributes(String smsType) {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();

        messageAttributes.put(AWS_SNS_SMS_TYPE, new MessageAttributeValue()
                .withStringValue(smsType)
                .withDataType(AWS_SNS_DATA_TYPE));

        return messageAttributes;
    }

    public String sendMessageToCustomer(String message, String mobile) {
        try {
            AmazonSNSClient amazonSNSClient = snsClientBuilder();

            PublishRequest publishRequest = new PublishRequest()
                    .withMessage(message)
                    .withPhoneNumber(mobile)
                    .withTargetArn(AWS_SNS_TOPIC_ARN)
                    .withMessageAttributes(buildSMSAttributes(AWS_SNS_SMS_TYPE_VALUE))
                    .withSdkRequestTimeout(3000);

            amazonSNSClient.publish(publishRequest);

            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }

    public String subscribeMobile(String mobile) {
        AmazonSNSClient snsClient = snsClientBuilder();
        SubscribeRequest request = new SubscribeRequest(AWS_SNS_TOPIC_ARN, "sms", mobile);
        snsClient.subscribe(request);

        return "Successfully subscribed.";
    }
}
