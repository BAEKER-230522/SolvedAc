package com.example.baekersolved.kafka;

import com.example.baekersolved.domain.dto.MemberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class KafkaProducer {

    /**
     * sovled-member 토픽에 전달
     */
    @Value(value = "${message.topic.name}")
    private String topicName;

    private final KafkaTemplate<String, MemberDto> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(MemberDto memberDto) {
        this.kafkaTemplate.send(topicName, memberDto);
    }
}

