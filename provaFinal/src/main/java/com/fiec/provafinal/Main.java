package com.fiec.provafinal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        String nomeDaFila = "Fiec2024";
        while(true){
            Thread.sleep(30000);
            try {
                ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                        .queueUrl(nomeDaFila)
                        .maxNumberOfMessages(1)
                        .build();
                List<software.amazon.awssdk.services.sqs.model.Message> responses = sqsClient.receiveMessage(receiveMessageRequest).messages();
                for(software.amazon.awssdk.services.sqs.model.Message m : responses){

                    System.out.println(m);
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(m.body());
                    String token = jsonNode.get("token").asText();
                    sendMessage(token);
                    DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                            .queueUrl(nomeDaFila)
                            .receiptHandle(m.receiptHandle())
                            .build();
                    sqsClient.deleteMessage(deleteMessageRequest);
                }

            } catch (SqsException e) {
                sqsClient.close();
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);

            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void sendMessage(String token){
        System.out.println(token);
        FirebaseSingleton.getInstance();
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle("Notificaçao da Fila")
                        .setBody("Fiec2024 - Enviando notificação para você")
                        .build())
                .build();

        String resp = null;
        try {
            resp = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Successfully sent message: " + resp);
    }
}
