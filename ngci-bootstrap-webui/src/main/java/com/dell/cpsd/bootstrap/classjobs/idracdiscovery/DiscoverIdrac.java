/**
 * Copyright &copy; 2018 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.bootstrap.classjobs.idracdiscovery;

import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.dell.cpsd.bootstrap.BootStrapJob;
import com.dell.cpsd.bootstrap.classjobs.idracdiscovery.Compute;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiscoverIdrac extends BootStrapJob
{
    private static final Logger log = LoggerFactory.getLogger(DiscoverIdrac.class);

    /**
     * Provide IP addresses delimited with a comma or use * in the fourth octet field.
     * You can also specify a range using a hyphen.
     *
     * Example: 10.94.20.34, 10.94.22.*, 10.94.20.100-200

     * @param addresses
     * @return List of discovered iDracs
     */
    public List<Compute> discoverIdracs(String addresses)
    {
        Set<String> ipsToSearch = new HashSet<>();
        addresses = addresses.replaceAll(" ", "");
        String[] ips = addresses.split(",");
        for (String ip : ips)
        {
            String ipPre = ip.substring(0, ip.lastIndexOf(".") + 1);
            String[] octets = ip.split("\\.");
            if (octets[3].contains("-"))
            {
                String[] range = octets[3].split("-");
                int startRange = Integer.parseInt(range[0]);
                int endRange = Integer.parseInt(range[1]);
                for (int i = startRange; i <= endRange; i++)
                {
                    ipsToSearch.add(ipPre + i);
                }
            }
            else if (octets[3].equals("*"))
            {
                int first = 0;
                int last = 255;
                for (int i = first; i <= last; i++)
                {
                    ipsToSearch.add(ipPre + i);
                }
            }
            else
            {
                ipsToSearch.add(ip);
            }
        }
        return discoverIdracs(ipsToSearch);
    }

    /**
     * Provide collection of ips to be searched
     *
     * @param ipsToSearch
     * @return List of discovered iDracs
     */
    public List<Compute> discoverIdracs(Collection<String> ipsToSearch)
    {
        List<Compute> discovered = new ArrayList<>();

        RestTemplate restTemplate = getRestTemplate();
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        for (String ip : ipsToSearch)
        {
            try
            {
                String hostName = getHostNameForIp(ip);
                String url = String.format("https://%s/cgi-bin/discover", ip);
                try
                {
                    Compute compute = restTemplate.getForObject(url, Compute.class);
                    compute.setIp(ip);
                    compute.setHostName(hostName);
                    discovered.add(compute);

                    // progress message here
                    String consoleMessage = String.format("iDrac IP: %s, Hostname: %s, Type: %s", compute.getIp(), compute.getHostName(), compute.getEndPointType());
                    this.getMyJob().addConsoleMessage(consoleMessage);
                }
                catch (RestClientException e)
                {
                    log.debug("No iDrac response from ip: " + ip);
                }
            }
            catch (UnknownHostException e)
            {
                log.debug("Cannot reach ip: " + ip);
            }
            this.getMyJob().setProgress(this.getMyJob().getProgress() + (100/ipsToSearch.size()));
        }
        return discovered;
    }

    protected RestTemplate getRestTemplate()
    {
        HttpComponentsClientHttpRequestFactory requestFactory = getRequestFactory();

        return new RestTemplate(requestFactory);
    }

    protected HttpComponentsClientHttpRequestFactory getRequestFactory()
    {
        HttpClientBuilder closeableClientBuilder = HttpClientBuilder.create();
        closeableClientBuilder.setSSLContext(getSSlContext());
        closeableClientBuilder.setSSLHostnameVerifier(gethostnameVerifier());
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(closeableClientBuilder.build());
        requestFactory.setConnectTimeout(2000);
        return requestFactory;
    }

    protected String getHostNameForIp(String ip) throws UnknownHostException
    {
        boolean found = true;
        String hostName = null;

        String[] parts = ip.split("\\.");
        int part1 = Integer.parseInt(parts[0]);
        int part2 = Integer.parseInt(parts[1]);
        int part3 = Integer.parseInt(parts[2]);
        int part4 = Integer.parseInt(parts[3]);
        byte[] address = {(byte) part1, (byte) part2, (byte) part3, (byte) part4};

        InetAddress addr = InetAddress.getByAddress(address);
        try
        {
            if (addr.isReachable(2000))
            {
                hostName = addr.getHostName();
            }
            else
            {
                throw new UnknownHostException();
            }
        }
        catch (IOException e)
        {
            throw new UnknownHostException();
        }

        return hostName;
    }

    private static SSLContext getSSlContext()
    {
        final TrustManager[] trustAllCerts = new TrustManager[] {getTrustManager()};

        SSLContext sslContext = null;
        try
        {

            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        }
        catch (NoSuchAlgorithmException | KeyManagementException e)
        {
            e.printStackTrace();
        }
        return sslContext;

    }

    private static X509TrustManager getTrustManager()
    {

        final X509TrustManager trustManager = new X509TrustManager()
        {

            @Override
            public X509Certificate[] getAcceptedIssuers()
            {
                return new X509Certificate[0];
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
            {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
            {
            }
        };
        return trustManager;
    }

    private static HostnameVerifier gethostnameVerifier()
    {

        HostnameVerifier hostnameVerifier = new HostnameVerifier()
        {

            @Override
            public boolean verify(String arg0, SSLSession arg1)
            {
                return true;
            }
        };

        return hostnameVerifier;
    }

    @Override
    public void executeJob() throws Exception
    {
        String[] addresses = this.getMyJob().getParameters().get("ipaddress");
        
        this.getMyJob().setProgress(1);
        if(addresses.length > 0)
        {
            String ipList = addresses[0];
            this.getMyJob().addMessage("Scanning : " + ipList);
            List<Compute> discovered = this.discoverIdracs(ipList);
            
            for (Compute compute : discovered)
            {
                System.out.format("iDrac IP: %s, Hostname: %s, Type: %s\n", compute.getIp(), compute.getHostName(), compute.getEndPointType());
            }
        }
        else
        {
            this.getMyJob().addMessage("Required parameter 'ipaddress' missing");
        }
        this.getMyJob().setProgress(100);
    }
}