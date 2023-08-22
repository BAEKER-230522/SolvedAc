package com.example.baekersolved.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        System.setProperty("https.protocols", "TLSv1.3");
        RestTemplate restTemplate;
        try {
            restTemplate = new RestTemplate(clientHttpRequestFactory());
        }catch (Exception e){
            restTemplate = new RestTemplate();
        }
        restTemplate.setInterceptors(Collections.singletonList(
                (request, body, execution) -> {
                    HttpHeaders headers = request.getHeaders();
                    headers.setContentType(APPLICATION_JSON);
                    headers.setAccept(Collections.singletonList(APPLICATION_JSON));
                    headers.add(HttpHeaders.ACCEPT, "application/json");
                    headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0");
                    return execution.execute(request, body);
                }
        ));
        return restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory();
    }
}
