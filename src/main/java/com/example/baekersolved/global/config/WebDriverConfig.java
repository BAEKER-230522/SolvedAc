package com.example.baekersolved.global.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.example.baekersolved.constants.Address.SOLVED_BASE_URL;
import static com.example.baekersolved.constants.Address.SOLVED_PROBLEM_URL;

@Configuration
public class WebDriverConfig {
    public static ConcurrentLinkedQueue<WebDriver> driverPool;
    @Bean
    public static void initializeDriverPool() throws IOException, InterruptedException {
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
        for (int i = 0; i <= 30; i++) {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--disk-cache-size=0");
            chromeOptions.addArguments("--media-cache-size=0");
//            chromeOptions.addArguments("--headless=new");
            chromeOptions.addArguments("--headless");
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

//    @Bean
//    public static WebDriver getDriverFromPool() {
//        return driverPool.poll();
//    }
//
//    @Bean
//    public static void returnDriverToPool(WebDriver driver) {
//        driverPool.add(driver);
//    }
}
