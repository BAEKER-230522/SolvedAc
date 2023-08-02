package com.example.baekersolved.domain.model;

import com.example.baekersolved.constants.Address;
import com.example.baekersolved.domain.dto.ProblemDto;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

import static com.example.baekersolved.constants.Address.SOLVED_BASE_URL;
import static com.example.baekersolved.constants.Address.SOLVED_PROBLEM_URL;

@Component
@Slf4j
public class SolvedCrawling {
    private static ConcurrentLinkedQueue<WebDriver> driverPool;
//    private static LinkedBlockingQueue<WebDriver> driverPool;
    private int finalI = 0;
    @Scheduled(cron = "30 48 23 * * *")
    public void solvedCrawling() {
        try {
            initializeDriverPool();
        }catch (IOException | InterruptedException e) {
            log.error("{}", e.getMessage());
            System.out.println("요기 에러임");
        }
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int level = 0; level <= 30; level++) {
            finalI = level;
            executorService.submit(() -> {
                WebDriver driver = getDriverFromPool();
                driver.get(SOLVED_BASE_URL + SOLVED_PROBLEM_URL + finalI); // 문제 페이지 리스트
                WebElement xPath = driver.findElement(By.xpath("//*[@id=\"__next\"]/div[3]/div[2]/div[1]/table/tbody"));
                List<WebElement> webElements = xPath.findElements(By.className("css-1ojb0xa"));

                for (WebElement webElement : webElements) {

                    String problemId = webElement.findElements(By.className("css-1cnxww9")).get(0).getText();
                    String subject = webElement.findElements(By.className("css-1cnxww9")).get(1).getText();
                    try {
                        ProblemDto dto = new ProblemDto(Integer.parseInt(problemId), subject, finalI); // 문제 dto
                    }catch (NumberFormatException e) {
                        log.error("{}", e.getMessage());
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
            }catch (InterruptedException e) {
                log.error("{}", e.getMessage());
            }
        }).start();
    }

    private static void initializeDriverPool() throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver_win.exe");
        } else if (os.contains("mac")) {
            Process process = Runtime.getRuntime().exec("xattr -d com.apple.quarantine drivers/chromedriver_mac");
            process.waitFor();
            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver_mac");
        } else if (os.contains("linux")) {
            System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        }
        driverPool = new ConcurrentLinkedQueue<>();
//        driverPool = new LinkedBlockingQueue<>();
        for (int i = 0; i <= 30; i++) {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--disk-cache-size=0");
            chromeOptions.addArguments("--media-cache-size=0");
            chromeOptions.addArguments("--headless=new");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--remote-allow-origins=*");
            // binary 는 확인해야함
            chromeOptions.setBinary("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
            WebDriver driver = new ChromeDriver(chromeOptions);
            driverPool.add(driver);
        }
    }

    private static WebDriver getDriverFromPool() {
        return driverPool.poll();
    }

    private static void returnDriverToPool(WebDriver driver) {
        driverPool.add(driver);
    }


}
