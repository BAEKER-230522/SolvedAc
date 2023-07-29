package com.example.baekersolved.api;

import com.example.baekersolved.domain.SolvedApiManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.http.HttpClient;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {SolvedApiManager.class, RestTemplate.class})
public class ApiManagerTest {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    SolvedApiManager manager;

    @Test
    @DisplayName("사용자 정보 가져오기")
    void getUserInformation() {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(APPLICATION_JSON));
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.USER_AGENT, "Application");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            response = restTemplate
                    .exchange(manager.getUserInformation("wy9295"), HttpMethod.GET, entity, String.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(APPLICATION_JSON_VALUE, response.getHeaders().getContentType().getType() + "/" +
                response.getHeaders().getContentType().getSubtype());
    }
}
