//package com.example.baekersolved.batch;
//
//import com.example.baekersolved.domain.SolvedApiService;
//import com.example.baekersolved.domain.dto.MemberDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.NonTransientResourceException;
//import org.springframework.batch.item.ParseException;
//import org.springframework.batch.item.UnexpectedInputException;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//public class SolvedReader implements ItemReader<List<MemberDto>> {
//
//    private final SolvedApiService solvedApiService;
//    @Override
//    public List<MemberDto> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
//        return solvedApiService.getMemberDtoList().getData();
//    }
//}
