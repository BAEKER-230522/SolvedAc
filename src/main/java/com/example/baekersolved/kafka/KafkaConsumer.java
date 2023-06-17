package com.example.baekersolved.kafka;

import com.example.baekersolved.domain.SolvedApiService;
import com.example.baekersolved.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final SolvedApiService service;

    /**
     * topic : member 값 받아옴(회원가입시)
     * @param message
     * @throws IOException
     */
    @KafkaListener(topics = "${message.topic.member}", groupId = ConsumerConfig.GROUP_ID_CONFIG)
    public void consume(String message) throws IOException {
        log.info("message : {}", message);
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            map = objectMapper.readValue(message, new TypeReference<Map<Object, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new NotFoundException(e + "데이터 없음");
        }
        try {
            Integer memberId = (Integer) map.get("id");
            Long longId = memberId.longValue();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Member 데이터 없음");
        }
    }
}