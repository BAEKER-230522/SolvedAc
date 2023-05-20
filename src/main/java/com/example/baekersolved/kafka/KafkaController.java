package com.example.baekersolved.kafka;

import com.example.baekersolved.domain.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducer producer;

    @Autowired
    private KafkaTemplate<String ,Object> kafkaTemplate;

    @Autowired
    KafkaController(KafkaProducer producer){
        this.producer = producer;
    }


    @PostMapping("/send")
    public String sendMessage(MemberDto memberDto) {
        log.info("message : {}", memberDto.toString());
        this.producer.sendMessage(memberDto);
        return "success";
    }
}