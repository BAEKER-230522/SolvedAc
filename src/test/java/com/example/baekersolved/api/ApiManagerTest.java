package com.example.baekersolved.api;

import com.example.baekersolved.domain.SolvedApiManager;
import feign.Response;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.print.attribute.standard.Media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest
public class ApiManagerTest {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    SolvedApiManager manager;
    @Test
    void apiTest() {
        ResponseEntity<String> response = null;
        try {
             response = restTemplate
                    .getForEntity(manager.getUserInformation("wy9295"), String.class);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(APPLICATION_JSON_VALUE, response.getHeaders().getContentType().getType() + "/" +
                                                    response.getHeaders().getContentType().getSubtype());

    }
}
