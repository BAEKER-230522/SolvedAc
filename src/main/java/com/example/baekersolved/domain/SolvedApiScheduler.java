//package com.example.baekersolved.domain;
//
//import com.example.baekersolved.domain.dto.BaekJoonDto;
//import com.example.baekersolved.domain.dto.MemberDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.json.simple.parser.ParseException;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.HttpClientErrorException;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.List;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class SolvedApiScheduler {
//    private final SolvedApiManager solvedApiManager;
//    private final SolvedApiService solvedApiService;
//
//
//    @Scheduled(cron = "${scheduler.cron.pull}")
//    public void getDtoList() {
//
//    }
//
//
//    @Scheduled(cron = "${scheduler.cron.member}")
//    public void checkMember() throws IOException, ParseException {
//        log.info("스케줄러 실행 day of week = {}", LocalDate.now().getDayOfWeek());
//        List<MemberDto> memberDtoList = solvedApiService.getMemberDtoList();
//        for (MemberDto member : memberDtoList) {
//            try {
//                int Bronze = solvedApiService.getSolvedCount(member, 1, 6) - member.getBronze();
//                Thread.sleep(1000);
//
//                int Silver = solvedApiService.getSolvedCount(member, 6, 11) - member.getSilver();
//                Thread.sleep(1000);
//
//                int Gold = solvedApiService.getSolvedCount(member, 11, 16) - member.getGold();
//                Thread.sleep(1000);
//
//                int Platinum = solvedApiService.getSolvedCount(member, 16, 21) - member.getPlatinum();
//                Thread.sleep(1000);
//
//                int Diamond = solvedApiService.getSolvedCount(member, 21, 26) - member.getDiamond();
//                Thread.sleep(1000);
//
//                int Ruby = solvedApiService.getSolvedCount(member, 26, 31) - member.getRuby();
//                Thread.sleep(1000);
//
//                BaekJoonDto dto = new BaekJoonDto(Bronze, Silver, Gold, Platinum, Diamond, Ruby);
////                publisher.publishEvent(new BaekJoonEvent(this, member, dto));
//
//            } catch (HttpClientErrorException | InterruptedException e) {
//                log.info("###############" + e + "###############");
//            }
//        }
//        log.info("스케줄러 {} 요일 업데이트 완료", LocalDate.now().getDayOfWeek());
//
//    }
//
////    @Scheduled(cron = "${scheduler.cron.study}")
////    public void checkStudy() {
////        log.info("스터디 스케줄러 ");
////        List<StudyRule> studyRules = studyRuleService.getAll();
////        for (StudyRule studyRule : studyRules) {
////            Long studyRuleId = studyRule.getId();
////
////            publisher.publishEvent(new StudyEvent(this, studyRuleId));
////        }
////    }
//}
