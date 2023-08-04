package com.example.baekersolved.domain.model;

import com.example.baekersolved.domain.dto.ProblemDto;
import com.example.baekersolved.domain.dto.common.BaekJoonDto;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.*;

import static com.example.baekersolved.constants.Address.*;
import static com.example.baekersolved.global.config.WebDriverConfig.driverPool;
import static java.lang.Thread.sleep;

@Component
@Slf4j
public class SolvedCrawling {

    /**
     * 백준에 있는 문제 크롤링 로직
     *
     */
    // 문제 크롤링 ( 주 1회? 달 1회? ) 자주할 필요 X
    @Scheduled(cron = "30 28 22 * * *")
    public void problemCrawling()
            throws NoSuchElementException, TimeoutException, StaleElementReferenceException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int level = 0; level <= 30; level++) {
            int finalI = level;
            executorService.submit(() -> {
                WebDriver driver = getDriverFromPool();
                driver.get(SOLVED_BASE_URL + SOLVED_PROBLEM_URL + finalI); // 문제 페이지 리스트
                // 페이징 체크
                WebElement element = driver.findElement(By.className("css-18lc7iz"));
                int nowPage = Integer.parseInt(element.findElement(By.className("css-kzpqsh")).getText());
                List<WebElement> pageList = element.findElements(By.className("css-1yjorof"));
                int lastPage = 2;
                if (pageList.size() != 0) {
                    lastPage = Integer.parseInt(pageList.get(pageList.size() - 1).getText().trim());
                }

                // 최대 페이지 수만큼 반복
                for (int i = 0; i < lastPage - 1; i++) {
                    // 문제 파싱
                    By problemBy = By.xpath("//*[@id=\"__next\"]/div[3]/div[2]/div[1]/table/tbody");
                    wait(driver, problemBy);

                    WebElement problemList = driver.findElement(problemBy);
                    List<WebElement> webElements = problemList.findElements(By.className("css-1ojb0xa"));

                    for (WebElement webElement : webElements) {
                        By problemIdBy = By.className("css-1cnxww9");
                        wait(driver, problemIdBy);


                        try {
                            String problemId = webElement.findElements(problemIdBy).get(0).getText().trim();

                            String subject = webElement.findElements(problemIdBy).get(1).getText().trim();
                            ProblemDto dto = new ProblemDto(Integer.parseInt(problemId), subject, finalI); // 문제 dto
                            System.out.println(dto);
                        } catch (NumberFormatException e) {
                            log.error("{}", e.getMessage());
                        } catch (Exception e) {
                            log.error("{}", e.getMessage());
                        }
                    }

                    // 페이지 바꾸기
                    By pageBy = By.className("css-1yjorof");
                    List<WebElement> newPageList = element.findElements(pageBy);
                    wait(driver, pageBy);


                    for (WebElement webElement : newPageList) {
                        int nextPage = Integer.parseInt(webElement.getText().trim());
                        if (nowPage < nextPage) {
                            nowPage = nextPage;
                            webElement.click();
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                log.error("{}", e.getMessage());
                            }
                            break;
                        }
                    }
                }

                returnDriverToPool(driver);
            });
        }
        new Thread(() -> {
            try {
                if (executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                    driverPool.forEach(WebDriver::quit);
                }
            } catch (InterruptedException e) {
                log.error("{}", e.getMessage());
            }
        }).start();
    }

    /**
     * 사용자 정보
     * 각 티어별 정답 맞춘수 출력 가능
     * @param baekjoonId
     * @return
     */
    public BaekJoonDto profileCrawling(String baekjoonId) {
        WebDriver driver = getDriverFromPool();
        driver.get(SOLVED_BASE_URL + SOLVED_PROFILE + baekjoonId);
        // 지금까지 해결한 문제 수
        WebElement element = driver.findElement(By.xpath("//*[@id=\"__next\"]/div[3]/div/div[6]/div[1]/div[2]/div/div/b"));

        int totalSolved = getUserSolvedCount(element, By.xpath("//*[@id=\"__next\"]/div[3]/div/div[6]/div[1]/div[2]/div/div/b"));

        WebElement elements = driver.findElement(By.className("css-1d9xc1d"));
        WebElement webElement = elements.findElement(By.className("css-1ojb0xa"));
        int bronze = getUserSolvedCount(webElement, By.xpath("//*[@id=\"__next\"]/div[3]/div/div[6]/div[3]/div/table/tbody/tr[1]/td[2]/b"));
        int silver = getUserSolvedCount(webElement, By.xpath("//*[@id=\"__next\"]/div[3]/div/div[6]/div[3]/div/table/tbody/tr[2]/td[2]/b"));
        int gold = getUserSolvedCount(webElement, By.xpath("//*[@id=\"__next\"]/div[3]/div/div[6]/div[3]/div/table/tbody/tr[3]/td[2]/b"));
        int platinum = getUserSolvedCount(webElement, By.xpath("//*[@id=\"__next\"]/div[3]/div/div[6]/div[3]/div/table/tbody/tr[4]/td[2]/b"));
        int diamond = getUserSolvedCount(webElement, By.xpath("//*[@id=\"__next\"]/div[3]/div/div[6]/div[3]/div/table/tbody/tr[5]/td[2]/b"));
        int ruby = getUserSolvedCount(webElement, By.xpath("//*[@id=\"__next\"]/div[3]/div/div[6]/div[3]/div/table/tbody/tr[6]/td[2]/b"));
        return new BaekJoonDto(bronze, silver, gold, platinum, diamond, ruby);
    }

    /**
     * 미션 해당문제 풀었는지 확인하는 로직
     * 최근의 푼 문제
     * ex) 1000, 1001 번 풀었는지 확인 가능
     */
    public void missionSolvedCheck(String baekJoonId) {
        WebDriver driver = getDriverFromPool();
        driver.get(BAEKJOON_BASE_URL + BAEKJOON_SOLVED_URL + baekJoonId + BAEKJOON_SOLVED_END);
        List<WebElement> problemTitle = driver.findElements(By.className("problem_title"));
        for (WebElement webElement : problemTitle) {
            System.out.println(webElement.getText());
        }
    }

    /**
     * 문제 크롤링 페이징 처리
     * @return
     */
    private static WebDriver getDriverFromPool() {
        return driverPool.poll();
    }

    private int getUserSolvedCount(WebElement webElement, By by) {
        return Integer.parseInt(webElement.findElement(by).getText().trim().replace(",", ""));
    }

    private static void returnDriverToPool(WebDriver driver) {
        driverPool.add(driver);
    }

    private void wait(WebDriver driver, By name) throws TimeoutException, NoSuchElementException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(name));
    }
}