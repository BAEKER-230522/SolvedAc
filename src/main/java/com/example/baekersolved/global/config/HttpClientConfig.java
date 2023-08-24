//package com.example.baekersolved.global.config;
//
//
//import org.apache.hc.client5.http.classic.HttpClient;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.ssl.SSLContexts;
//import org.apache.http.ssl.TrustStrategy;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//
//import javax.net.ssl.SSLContext;
//import java.security.cert.X509Certificate;
//
//@Configuration
//public class HttpClientConfig {
//
//    public static HttpComponentsClientHttpRequestFactory trustRequestFactory() throws Exception {
//        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
//        SSLContext sslContext = SSLContexts.custom()
//                .loadTrustMaterial(null,acceptingTrustStrategy)
//                .build();
//        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
//        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf)
//                .build();
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//        requestFactory.setHttpClient((HttpClient) httpClient);
//        return requestFactory;
//    }
//}
