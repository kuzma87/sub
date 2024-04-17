package com.akuzmenko.sub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.messaging.simp.stomp.StompHeaders.CONTENT_TYPE;

@Service
@RequiredArgsConstructor
@Slf4j
public class StompService {

    @Value("${jwt.token}")
    private String token;

    private final WebSocketStompClient stompClient;

    public void subscribeToTopic(String topic, Integer port) {
        try {
            log.info("Connecting to STOMP port {}", port);
            var session = createClientStompSession(port);
            log.info("Connection established");
            log.info("Subscribing to topic {}", topic);
            var messageQueue = subscribeToTopic(getStompHeaders(topic), session, TestMessage.class);
            log.info("Subscribed to topic {}", topic);
            log.info("Waiting for messages...");
            var message = messageQueue.poll(30, SECONDS);
            log.info("===============> Message received: {}", message);
            session.disconnect();
            log.info("Disconnected from STOMP");
        } catch (Exception e) {
            log.error("Error WS connecting");
            throw new RuntimeException(e);
        }
    }

    public void subscribeToQueue(String queue, Integer port) {
        try {
            log.info("Connecting to STOMP port {}", port);
            var session = createClientStompSession(port);
            log.info("Connection established");
            log.info("Subscribing to queue {}", queue);
            var messageQueue = subscribeToTopic(getStompHeaders(queue), session, TestMessage.class);
            log.info("Subscribed to queue {}", messageQueue);
            log.info("Waiting for messages...");
            var message = messageQueue.poll(30, SECONDS);
            log.info("===============> Message received: {}", message);
            session.disconnect();
            log.info("Disconnected from STOMP");
        } catch (Exception e) {
            log.error("Error WS connecting");
            throw new RuntimeException(e);
        }
    }

    public StompSession createClientStompSession(Integer port) throws Exception {
        var url = "ws://localhost:" + port + "/ws?token=" + token;
        return stompClient.connectAsync(url, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
    }

    private BlockingQueue<?> subscribeToTopic(StompHeaders headers, StompSession stompSession,
                                              Class<?> clazz) throws InterruptedException {
        var subscriber = new StompSubscriber(clazz);
        stompSession.subscribe(headers, subscriber);
        TimeUnit.MILLISECONDS.sleep(30);
        return subscriber.getQueue();
    }

    private StompHeaders getStompHeaders(String destination) {
        var stompHeaders = new StompHeaders();
        stompHeaders.add(CONTENT_TYPE, "application/json");
        stompHeaders.add("destination", destination);

        return stompHeaders;
    }
}
