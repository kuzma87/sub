package com.akuzmenko.sub;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Getter
@Slf4j
public class StompSubscriber implements StompFrameHandler {

    private final BlockingQueue<Object> queue = new ArrayBlockingQueue<>(100);
    private final Class<?> clazz;

    public StompSubscriber(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return clazz;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (Objects.nonNull(payload)) {
            log.info("Received payload: {}", payload);
            queue.add(clazz.cast(payload));
        }
    }
}
