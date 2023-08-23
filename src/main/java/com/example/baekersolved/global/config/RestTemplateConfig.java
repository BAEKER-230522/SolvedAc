package com.example.baekersolved.global.config;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {
    private final SSLConfig sslConfig;

    @Bean
    public RestTemplate restTemplate() {
//        System.setProperty("https.protocols", "TLSv1.3");

        RestTemplate restTemplate;
        try {
            restTemplate = new RestTemplate(trustRequestFactory());
        } catch (Exception e) {
            restTemplate = new RestTemplate();
        }
        restTemplate.setInterceptors(Collections.singletonList(
                (request, body, execution) -> {
                    HttpHeaders headers = request.getHeaders();
                    headers.setContentType(APPLICATION_JSON);
                    headers.setAccept(Collections.singletonList(APPLICATION_JSON));
                    headers.add(HttpHeaders.ACCEPT, "application/json");
                    headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0");
                    return execution.execute(request, body);
                }
        ));
        return restTemplate;
    }

    //    private HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
//        return new HttpComponentsClientHttpRequestFactory();
//    }
    private static HttpComponentsClientHttpRequestFactory trustRequestFactory() throws Exception {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf)
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient((HttpClient) httpClient);
        return requestFactory;
    }
}
