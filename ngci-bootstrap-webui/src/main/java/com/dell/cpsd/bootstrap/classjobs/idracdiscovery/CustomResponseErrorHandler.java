/**
 * Copyright &copy; 2018 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.bootstrap.classjobs.idracdiscovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class CustomResponseErrorHandler implements ResponseErrorHandler
{

    private static final Logger log = LoggerFactory.getLogger(CustomResponseErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse clienthttpresponse) throws IOException
    {
        if (clienthttpresponse.getStatusCode() != HttpStatus.OK)
        {
            throw new IOException("Response status was not OK");
        }
    }

    @Override
    public boolean hasError(ClientHttpResponse clienthttpresponse) throws IOException
    {
        if (clienthttpresponse.getStatusCode() != HttpStatus.OK)
        {
            log.debug("Status code: " + clienthttpresponse.getStatusCode());
            return true;
        }
        return false;
    }
}