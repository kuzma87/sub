package com.akuzmenko.sub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("stomp/test")
@RequiredArgsConstructor
@Slf4j
public class StompSubscribeController {

    private final StompService stompService;

    @PostMapping("subscribe/topic")
    public void subscribeTopic(@RequestParam("topic") String topic,
                               @RequestParam("port") Integer port) {
        log.info("Requesting connection to STOMP subscribe topic {}", topic);
        stompService.subscribeToTopic(topic, port);
    }

    @PostMapping("subscribe/queue")
    public void subscribeQueue(@RequestParam("queue") String queue,
                               @RequestParam("port") Integer port) {
        log.info("Requesting connection to STOMP subscribe queue {}", queue);
        stompService.subscribeToQueue(queue, port);
    }

}
