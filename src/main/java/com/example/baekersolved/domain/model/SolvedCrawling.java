package com.example.baekersolved.domain.model;

import com.example.baekersolved.domain.dto.ProblemDto;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.*;

import static com.example.baekersolved.constants.Address.SOLVED_BASE_URL;
import static com.example.baekersolved.constants.Address.SOLVED_PROBLEM_URL;
import static com.example.baekersolved.global.config.WebDriverConfig.driverPool;
import static java.lang.Thread.sleep;

@Component
@Slf4j
public class SolvedCrawling {
    //    private static ConcurrentLinkedQueue<WebDriver> driverPool;
//    private static LinkedBlockingQueue<WebDriver> driverPool;

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
                            }catch (InterruptedException e) {
                                log.error("{}", e.getMessage());
                            }
                            break;
                        }
                    }
                }

                returnDriverToPool(driver);
            });
        }
//        new Thread(() -> {
//            try {
//                if (executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
//                    driverPool.forEach(WebDriver::quit);
//                }
//            }catch (InterruptedException e) {
//                log.error("{}", e.getMessage());
//            }
//        }).start();
    }

    /**
     * 문제 크롤링 페이징 처리
     *
     * @return
     */

//    private CompletableFuture<Integer> pagingCrawling(WebElement webElement) {
//        return CompletableFuture.supplyAsync(() ->
//                Optional.ofNullable(webElement.findElement(By.className("css-1yjorof")).getText().trim())
//                        .map(Integer::parseInt)
//                        .orElseThrow(() -> new NumberFormatException("페이지 처리 에러"))
//        );
//    }
    private static WebDriver getDriverFromPool() {
        return driverPool.poll();
    }

    private static void returnDriverToPool(WebDriver driver) {
        driverPool.add(driver);
    }

    private void wait(WebDriver driver, By name) throws TimeoutException, NoSuchElementException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(name));
    }

}


//    private static void initializeDriverPool() throws IOException, InterruptedException {
//        String os = System.getProperty("os.name").toLowerCase();
//
//        if (os.contains("win")) {
//            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver_win.exe");
//        } else if (os.contains("mac")) {
//            Process process = Runtime.getRuntime().exec("xattr -d com.apple.quarantine drivers/chromedriver_mac");
//            process.waitFor();
//            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver_mac");
//        } else if (os.contains("linux")) {
//            System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
//        }
//        driverPool = new ConcurrentLinkedQueue<>();
////        driverPool = new LinkedBlockingQueue<>();
//        for (int i = 0; i <= 30; i++) {
//            ChromeOptions chromeOptions = new ChromeOptions();
//            chromeOptions.addArguments("--disk-cache-size=0");
//            chromeOptions.addArguments("--media-cache-size=0");
//            chromeOptions.addArguments("--headless=new");
//            chromeOptions.addArguments("--no-sandbox");
//            chromeOptions.addArguments("--disable-dev-shm-usage");
//            chromeOptions.addArguments("--disable-gpu");
//            chromeOptions.addArguments("--remote-allow-origins=*");
//            // binary 는 확인해야함
//            chromeOptions.setBinary("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
//            WebDriver driver = new ChromeDriver(chromeOptions);
//            driverPool.add(driver);
//        }
//    }