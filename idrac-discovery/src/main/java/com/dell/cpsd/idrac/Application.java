/**
 * Copyright &copy; 2018 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.idrac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Application
{
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String args[])
    {
        DiscoverIdrac discover = new DiscoverIdrac();
        try
        {
            /* Provide IP addresses delimited with a comma or use * in the fourth octet field.
               You can also specify a range using a hyphen.

               Example: 10.94.20.34, 10.94.22.*, 10.94.20.100-200
             */
            String addr = args[0];
            List<Compute> discovered = discover.discoverIdracs(addr);
            for (Compute compute : discovered)
            {
                System.out.format("iDrac IP:  %s, Type: %s\n", compute.getIp(), compute.getEndPointType());
            }
        }
        catch (Exception e)
        {
            log.error("Error", e);
        }
    }

}