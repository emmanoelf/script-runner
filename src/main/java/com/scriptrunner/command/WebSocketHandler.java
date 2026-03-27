package com.scriptrunner.command;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class WebSocketHandler implements CommandOutputHandler {

    private String topic;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketHandler(String topic, SimpMessagingTemplate simpMessagingTemplate) {
        this.topic = topic;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void onStdout(String line) {
        System.out.println(line);
        simpMessagingTemplate.convertAndSend(topic, line);
    }

    @Override
    public void onStderr(String line) {
        System.out.println(line);
        simpMessagingTemplate.convertAndSend(topic, "[ERR] " + line);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable error) {
        System.out.println(error.getMessage());
        simpMessagingTemplate.convertAndSend(topic, "[ERROR] " + error.getMessage());
    }

}
