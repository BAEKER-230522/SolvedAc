package com.example.baekersolved.kafka;

import com.example.baekersolved.domain.dto.common.MemberDto;
import com.example.baekersolved.domain.dto.response.StudyRuleProduceDto;
import com.example.baekersolved.domain.dto.response.UserRecentProblem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaProducer {

    /**
     * sovled-member 토픽에 전달
     */
    @Value(value = "${message.topic.solved}")
    private String solvedMember;

    @Value(value = "${message.topic.studyRule}")
    private String studyRule;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMember(MemberDto memberDto) {
        this.kafkaTemplate.send(solvedMember, memberDto);
    }

    public void sendStudy(StudyRuleProduceDto dto) {
        this.kafkaTemplate.send(studyRule, dto);
    }
    public void sendRecentProblem(UserRecentProblem recentProblem) {
        this.kafkaTemplate.send(studyRule, recentProblem);
    }
}

