package com.example.baekersolved.domain.event;

import com.example.baekersolved.domain.SolvedApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {
    private final SolvedApiService solvedApiService;

    @EventListener
    public void listen(StudyEvent event) {

    }
}
