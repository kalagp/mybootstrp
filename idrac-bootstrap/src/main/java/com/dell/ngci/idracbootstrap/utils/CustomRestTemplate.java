/*
 * Copyright (c) 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */
package com.dell.ngci.idracbootstrap.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

@Component
public class CustomRestTemplate extends RestTemplate {

    @Autowired
    CustomHttpRequestFactory customHttpRequestFactory;

    public CustomRestTemplate() {
        super();
    }

    @PostConstruct
    public void initTemplate()
    {
        setRequestFactory(customHttpRequestFactory);
    }

    public <T> ResponseEntity<T> execForEntity(HttpMethod method, String url, HttpEntity<?> request, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return (ResponseEntity)this.execute(url, method, requestCallback, responseExtractor, new Object[0]);
    }
}
