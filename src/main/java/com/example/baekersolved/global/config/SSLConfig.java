package com.example.baekersolved.global.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Configuration
public class SSLConfig {
    @Bean
    public SSLConnectionSocketFactory getSSLSocketFactory()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        System.setProperty("https.protocols", "TLSv1.3");
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        }};

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sc,
                new NoopHostnameVerifier());

        return socketFactory;
    }

    @Bean(name = "customerHttpClient")
    public CloseableHttpClient limsHttpClient()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        int TIME_OUT = 30 * 1000;

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory> create().register("http",
                        PlainConnectionSocketFactory.getSocketFactory()).register("https", getSSLSocketFactory()).build());

        connectionManager.setMaxTotal(100);

        RequestConfig config = RequestConfig.custom().setConnectTimeout(Timeout.ofDays(TIME_OUT)).setConnectionRequestTimeout(
                Timeout.ofDays(TIME_OUT)).build();

        CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManager(
                connectionManager).setDefaultRequestConfig(config).build();

        return httpClient;
    }
}
