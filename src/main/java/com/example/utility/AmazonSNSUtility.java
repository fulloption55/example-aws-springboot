package com.example.utility;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.amazonaws.services.sns.model.ConfirmSubscriptionResult;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmazonSNSUtility {

    @Autowired
    AmazonSNSClient amazonSNSClient;

    public CreateTopicResult createTopic(String topicName) {
        CreateTopicRequest createReq = new CreateTopicRequest()
                .withName(topicName);
        CreateTopicResult createResult = amazonSNSClient.createTopic(createReq);
        return createResult;
    }

    public SubscribeResult subscribeTopic(String topicArn, String protocol, String endpoint) {
        SubscribeRequest subscribeRequest = new SubscribeRequest()
                .withTopicArn(topicArn)
                .withProtocol(protocol) //http
                .withEndpoint(endpoint); //www.asdas.com:8102
        SubscribeResult subscribeResult = amazonSNSClient.subscribe(subscribeRequest);
        return subscribeResult;
    }

    public ConfirmSubscriptionResult confirmSubscribe(String topicArn, String token) {
        ConfirmSubscriptionRequest confirmRequest = new ConfirmSubscriptionRequest()
                .withTopicArn(topicArn)
                .withToken(token);
        ConfirmSubscriptionResult confirmSubscriptionResult = amazonSNSClient.confirmSubscription(confirmRequest);
        return confirmSubscriptionResult;
    }
}
