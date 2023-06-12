package com.example.baekersolved.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * param
 * StudyRuleId
 */
@Getter
public class StudyEvent extends ApplicationEvent {

    private final Long id;

    public StudyEvent(Object source, Long id) {
        super(source);
        this.id = id;
    }
}
