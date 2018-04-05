/*
 * Copyright (c) 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.ngci.idracbootstrap.utils;

import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * CustomHttpRequestFactory with connectionTimeout, readTimeout and maxRetry setting as well as default Hostname verifier
 */
@Component
public class CustomHttpRequestFactory extends HttpComponentsClientHttpRequestFactory
{

    private static final Logger logger = LoggerFactory.getLogger(HttpComponentsClientHttpRequestFactory.class);

    @Value("${http.connection.timeout.ms:30000}")
    private int connectionTimeoutMs;

    @Value("${http.read.timeout.ms:60000}")
    private int readTimeoutMs;

    @Value("${http.max.retry:3}")
    private int maxRetry;

    public CustomHttpRequestFactory()
    {
    }

    @PostConstruct
    public void initFactory()
    {
        logger.info("Init the HttpComponentsClientHttpRequestFactory ..." );
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().useSystemProperties().setSslcontext(this.trustSelfSignedSSL())
                .setHostnameVerifier(new NullHostnameVerifier()).setRetryHandler(new DefaultHttpRequestRetryHandler(maxRetry, true));
        CloseableHttpClient httpClient = httpClientBuilder.build();
        this.setHttpClient(httpClient);
        this.setReadTimeout(readTimeoutMs);
        this.setConnectTimeout(connectionTimeoutMs);

    }

    private SSLContext trustSelfSignedSSL()
    {
        try
        {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager()
            {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException
                {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException
                {
                }

                public X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }
            };
            ctx.init((KeyManager[]) null, new TrustManager[] {tm}, (SecureRandom) null);
            return ctx;
        }
        catch (Exception e)
        {
            logger.error("Caught", e);
            return null;
        }
    }

    private class NullHostnameVerifier implements X509HostnameVerifier
    {
        private NullHostnameVerifier()
        {
        }

        public void verify(String host, SSLSocket ssl) throws IOException
        {
        }

        public void verify(String host, X509Certificate cert) throws SSLException
        {
        }

        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException
        {
        }

        public boolean verify(String s, SSLSession sslSession)
        {
            return true;
        }
    }
}
