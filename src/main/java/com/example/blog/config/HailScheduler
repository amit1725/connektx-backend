package com.example.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HailScheduler {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${server.url}")
    private String serverUrl;

    // Every 14 minutes
    @Scheduled(fixedRate = 840000)
    public void callServer() {
        try {
            String response = restTemplate.getForObject(serverUrl, String.class);
            System.out.println("Hail response: " + response);
        } catch (Exception e) {
            System.err.println("Failed to hail server: " + e.getMessage());
        }
    }
}
