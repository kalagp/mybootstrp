/**
 * Copyright &copy; 2018 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.bootstrap;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.boot.json.JacksonJsonParser;


@SpringBootApplication
@Controller
public class BootStrapWeb extends WebMvcConfigurerAdapter
{
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/" };
    
    private static String OS = System.getProperty("os.name").toLowerCase();
    
    static ArrayList<String> expectedResults = new ArrayList<String>();
    static ArrayList<String> execResults = new ArrayList<String>();

    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        execResults.clear();
        return "Results cleared";
    }

    @RequestMapping("/resultstxt")
    @ResponseBody
    public ArrayList<String> results()
    {
        ArrayList<String> response = new ArrayList<String>();
        response.addAll(expectedResults);
        response.add("Actual Results:");
        response.addAll(execResults);
        return response;
    }
    
    @RequestMapping("/results")
    @ResponseBody
    public String nicStatus()
    {
        StringBuffer result = new StringBuffer();
        
        String statusFormat = "<td bgcolor='%s'>%s</td>";
        
        String td1 = String.format(statusFormat, "white", "Validating");
        String td2 = String.format(statusFormat, "white", "Validating");
        
        if( execResults.size() == 0)
        {
            td1 = String.format(statusFormat, "white", "Validating");
            td2 = String.format(statusFormat, "white", "Validating");
        }
        
        if(execResults.toString().contains("ThatsAll") )
        {
            td1 = String.format(statusFormat, "red", "Error");
            td2 = String.format(statusFormat, "red", "Error");
        }
        
        if(execResults.toString().contains("a036.9fde.6ee2"))
        {
            td1 = String.format(statusFormat, "green", "Connected");
        }
        if(execResults.toString().contains("a036.9fde.6ee0"))
        {
            td2 = String.format(statusFormat, "green", "Connected");
        }
        
        result.append("<!DOCTYPE html>");
        result.append("<html>");
        result.append("<meta http-equiv='refresh' content='5' />");
        result.append("<body>");

        result.append("<h2>Connectivity Status</h2>" );
        result.append("<table border='0'>" );
        result.append("  <tr bgcolor='#C0D9D9'>" );
        result.append("    <th colspan='1'>Nexus3000 C3164PQ Chassis</th>" );
        result.append("    <th width='20'></th>" );
        result.append("    <th colspan='1' width='225'>PowerEdge R730</th>" );
        result.append("  </tr>" );
        result.append("  <tr>" );
        result.append("    <td colspan='1'>SN: SAL1951VG7E</td>" );
        result.append("    <td></td>" );
        result.append("    <td colspan='1'>SN: H492DH2</td>" );
        result.append("  </tr>" );
        result.append("  <tr>" );
        result.append("     <td colspan='3' align='center' bgcolor='#C0D9D9'>Status</td>" );
        result.append("  </tr>" );

        result.append("  <tr bgcolor='#70DBDB'>" );
        result.append("    <td align='center'>Interface</td>" );
        result.append("    <td width='100'></td>" );
        result.append("    <td align='center'>MAC Address</td>" );
        result.append("  </tr>" );
        result.append("  <tr bgcolor='#FFFFFF'>" );
        result.append("    <td>Eth1/8/3</td>" );
        //result.append("    <td bgcolor='" + status1 + "'>" + ("green".equals(status1) ? "Connected" : "Error") + "</td>" );
        result.append(td1);
        result.append("    <td>a0:36.9f:de:6e:e2</td>" );
        result.append("  </tr>" );
        result.append("  <tr bgcolor='#C0C0C0'>" );
        result.append("    <td>Eth1/9/3</td>" );
//        result.append("    <td bgcolor='" + status2 + "'>" + ("green".equals(status2) ? "Connected" : "Error") + "</td>" );
        result.append(td2);
        result.append("    <td>a0:36:9f:de:6e:e0</td>" );
        result.append("  </tr>" );
        result.append("</table>" );

        
        result.append("<br>");
        
        if(execResults.toString().contains("ThatsAll") )
        {
            result.append("<h3>Validation Completed</h3>"); 
            if(td1.contains("Error") || td2.contains("Error"))
            {
                result.append("<h3>Errors found. Please verify your network cabling</h3>"); 
            }
        }
        
        result.append("<form action=\"./check\" method=\"post\">");
        result.append("<input type=\"submit\" value=\"Re-Validate\">");
        result.append("</form>");
        
        result.append("</body>");
        result.append("</html>");
        return result.toString();
    }

    @RequestMapping("/test")
    @ResponseBody
    public String startup(Principal principal)
    {
        StringBuffer result = new StringBuffer();
        result.append("<!DOCTYPE html>");
        result.append("<html>");
        result.append("<body>");
        result.append("<font color=\"black\">");
        result.append("  <h2>NGCI System Validation</h2>");
        result.append("</font>");

        result.append("<form action=\"./check\" method=\"post\">");
        result.append("<input type=\"submit\" value=\"Validate\">");
        result.append("</form>");

        result.append("<br>");
        result.append("<form action=\"./shipit.html\" method=\"get\">");
        result.append("<input type=\"submit\" value=\"Ship it!\">");
        result.append("</form>");

        result.append("</body>");
        result.append("</html>");
        return result.toString();
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public String doWork()
    {
        System.out.println("POST CAlled on " + OS);
        execResults.clear();
        ExecProcess execProcess = new ExecProcess();
        //execProcess.setTask("do_test.sh");
        
        if(OS.indexOf("win") >= 0)
        {
            execProcess.setTask("test.cmd");
            execProcess.setShellUse(false);
        }
        else
        {
            execProcess.setTask("ansible-playbook get_lldp_data.yml | grep ngci-sysvalidate");
        }
        
        Thread thread = new Thread(execProcess);
        thread.start();
        //return "Work started";
        return "redirect:./results";

    }
    
    @RequestMapping("/")
    @ResponseBody
    public String welcome()
    {// Welcome page, non-rest
        return "Welcome to Simple Web Worker Example.";
    }
    
    @RequestMapping("/getData")
    @ResponseBody
    public String doGetData()
    {
        String response = null;
        try
        {
            String resultString = getHttpResponse("https://10.234.122.15/redfish/v1/Chassis/System.Embedded.1", "root", "Vc3m0l@b");
            
            JacksonJsonParser jsonParser = new JacksonJsonParser();
            
           Map<String, Object> data = jsonParser.parseMap(resultString);
           
           Map<String, Object> y = (Map<String, Object>) data.get("Links");
           walkResults(null, y);
           
           walkResults(null, data);

           
           System.out.println(data);
           
           for (Map.Entry<String, Object> entry : data.entrySet())
           {
               Object x = entry.getValue();
               
               System.out.println(entry.getKey() + " " + x.getClass());
           }

           StringBuffer test = new StringBuffer();
           
           hyperMediaFamilyTree("parent", test,data, true, 0);
           
           StringBuffer preamble = new StringBuffer();
           
           preamble.append("<html>");
           preamble.append("<head>");
           preamble.append("");
           preamble.append("");
           preamble.append("");
           preamble.append("<link href='css/stylesX.css' rel='stylesheet' />");
           preamble.append("</head>");
           preamble.append("<body>");
           preamble.append("<div class='tree'>");
           
           
           test.insert(0, preamble);
           
           test.append("</div>");
           test.append("</body>");
           test.append("</html>");
           response = test.toString();
           
           StringBuffer test2 =  buildList("parent", data);
           
           System.out.println( test2.toString());
           
           test2.insert(0, preamble);
           test2.append("</div>");
           test2.append("</body>");
           test2.append("</html>");
           response = test2.toString();
        }
        catch (Exception e)
        {
            response = e.getMessage();
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        return response;
    }
    
    private void hyperMediaFamilyTree(String name, StringBuffer results, Map<String, Object> data, boolean isRoot, int level)
    {
        StringBuffer local = new StringBuffer();
        
        local.append("\n<ul><li>");
        local.append("<a href='#'>");

        local.append("<table border='1'>");

        for (Map.Entry<String, Object> entry : data.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String)
            {
                local.append("<tr>");
                local.append("<td>");
                local.append(key);
                local.append("</td>");
                local.append("<td>");
                local.append(value);
                local.append("</td>");
                local.append("</tr>");
            }
        }
        local.append("</table>");
        local.append("</a>");

        for (Map.Entry<String, Object> entry : data.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map)
            {
                local.append("<a href='#'>" + key + "</a>");
                hyperMediaFamilyTree(key, local, (Map<String, Object>) value, false, level+1);
            }
        }

        results.append(local);

        local.append("</li>");
        local.append("</ul>");
    }
    
    private void getNodeData(String name, StringBuffer results, Map<String, Object> data)
    {
        StringBuffer local = new StringBuffer();
        
        for (Map.Entry<String, Object> entry : data.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();
        }
        
        if(local.length() > 0)
        {
            results.append(local);
        }
    }

    
    private StringBuffer buildList(String name, Map<String, Object> lhm1) 
    {
        Map<String, String> nodeAttributes = new LinkedHashMap<String, String>();
        Map<String, Object> children = new LinkedHashMap<String, Object>();
        
        // Sort out objects
        for (Map.Entry<String, Object> entry : lhm1.entrySet())
        {
            if(name != null )
            {
                System.out.println("------------ " + name + " ------------------");
            }
            
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String)
            {
                nodeAttributes.put(key, value.toString());
            }
            else if (value instanceof Map)
            {
                children.put(key, value);
                // // Map<String, Object> subMap = (Map<String, Object>) value;
                  // RECURSE
                //buildList(key, (Map<String, Object>)value);
            }
            else if (value instanceof ArrayList)
            {
                //(ArrayList)value)
                children.put(key, new ArrayList<String>().addAll((ArrayList)value));
            }
            else if (value instanceof Integer)
            {
                children.put(key, Integer.parseInt(value.toString()));
            }
            else
            {
                System.out.println(" NOT SUPPORTED " + value.getClass());
                //throw new IllegalArgumentException(String.valueOf(value));
            }
        }
        
        StringBuffer results = new StringBuffer();
        
        results.append("\n<ul><li>");
        results.append("<a href='#'>");
        results.append("<table border='1'>");
        
        for (Map.Entry<String, String> entry : nodeAttributes.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String)
            {
                results.append("<tr>").append("<td>");
                results.append(key);
                results.append("</td>").append("<td>");
                results.append(value);
                results.append("</td>").append("</tr>");
            }
        }
        results.append("</table>");
        results.append("</a>");
        results.append("<ul>");
        
        for (Map.Entry<String, Object> entry : children.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof Map)
            {
                StringBuffer subMap = buildList(key, (Map<String, Object>)value) ;
                results.append("<li>").append("<a href='#'>").append(key).append("</a>");
                //results.append("<ul>");
                results.append(subMap);
               // results.append("</ul>");
                results.append("</li>");
            }
//            else if (value instanceof ArrayList)
//            {
//                results.append("<ul>");
//                results.append("<li>").append("<a href='#'>").append(key).append("</a>");
//                results.append("<ul>");
//                for(String item: ((ArrayList<String>)value))
//                {
//                    results.append("<li>").append(item).append("</li>");
//                }
//                results.append("</ul>");
//                results.append("</li>");
//                results.append("</ul>");
//            }
        }
        
        results.append("</ul>");

        results.append("</li></ul>");
        
        return results;
    }

    private String walkResults(String name, Map<String, Object> lhm1)// throws ParseException 
    {
        for (Map.Entry<String, Object> entry : lhm1.entrySet())
        {
            if(name != null )
            {
                System.out.println("------------ " + name + " ------------------");
            }
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String)
            {
                System.out.println(key + " = " + value);
            }
            else if (value instanceof Map)
            {
                Map<String, Object> subMap = (Map<String, Object>) value;
                walkResults(key, subMap);
            }
            else if (value instanceof ArrayList)
            {
                System.out.println(key + " = " + value);
            }
            else if (value instanceof Integer)
            {
                System.out.println(key + " = " + Integer.parseInt(value.toString()));
            }
            else if (value instanceof HashMap)
            {
                System.out.println(key + " = " + value.toString());
            }
            else
            {
                System.out.println(value.getClass());
                //throw new IllegalArgumentException(String.valueOf(value));
            }

        }
        return null;
    }
    
    // Misc methods
    
    public String getHttpResponse(String address, String username, String password) throws Exception
    {
        URL url = new URL(address);
        URLConnection conn = url.openConnection();
        
        trustConnection((HttpsURLConnection)conn);
        
        conn.setConnectTimeout(30000); // 30 seconds time out

        if (username != null && password != null)
        {
            String user_pass = username + ":" + password;
            String encoded = Base64.getEncoder().encodeToString(user_pass.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encoded);
        }

        String line = "";
        StringBuffer sb = new StringBuffer();
        BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = input.readLine()) != null)
        {
            sb.append(line);
        }
        input.close();
        return sb.toString();
    }

    // This set the connection just to TRUST All -
    private void trustConnection(HttpURLConnection connection)
    {
        if (connection == null)
        {
            return;
        }

        try
        {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager()
            {
                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }

                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                {
                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                {
                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Tell the url connection object to use our socket factory which bypasses security checks
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
            
            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    // TODO Auto-generated method stub
                    return true;
                }
            };
            // Install the all-trusting host verifier
            ((HttpsURLConnection) connection).setHostnameVerifier(allHostsValid);
        }
        catch (final Exception oops)
        {
            oops.printStackTrace();
        }
    }    
    
    // Little better with in memory keystore
    private void getCertificateForConnection(HttpURLConnection connection)
    {
        if(connection != null )
        {
            if(connection.getURL().getProtocol().equals("https"))
            {
                HttpsURLConnection secureConnection = (HttpsURLConnection) connection;

                String hostName = connection.getURL().getHost();
                int port = connection.getURL().getPort();

                if (port == -1)
                {
                    port = 443;
                }

                try
                {
                    // KeyStore keyStore = KeyStore.getInstance("pkcs12");
                    KeyStore keyStore = KeyStore.getInstance("JKS");
                    keyStore.load(null, null);

                    // Create a trust manager that does not validate certificate chains ONLY FOR GETTING CERT
                    TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager()
                    {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers()
                        {
                            return null;
                        }

                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                        {
                        }

                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                        {
                        }
                    }};

                    SSLContext sc = SSLContext.getInstance("SSL"); // throws NoSuchAlgorithmException
                    sc.init(null, trustAllCerts, new java.security.SecureRandom()); // throws KeyManagementException
                    SSLSocketFactory plainfactory = (SSLSocketFactory) sc.getSocketFactory();

                    SSLSocket socket = (SSLSocket) plainfactory.createSocket(hostName, port); // throws UnknownHostException

                    socket.setSoTimeout(20000); // Dont wait forever if its not going to respond

                    java.security.cert.Certificate[] serverCerts = null;
                    // Connect to the server
                    try
                    {
                        socket.startHandshake();

                        // Retrieve the server's certificate chain
                        serverCerts = socket.getSession().getPeerCertificates(); // throws SSLPeerUnverifiedException
                        if (serverCerts != null)
                        {
                            if (keyStore != null)
                            {
                                System.out.println("Server sent " + serverCerts.length + " certificate(s):");
                                for (int i = 0; i < serverCerts.length; i++)
                                {
                                    X509Certificate cert = (X509Certificate) serverCerts[i];
                                    System.out.println(" " + (i + 1) + " Subject " + cert.getSubjectDN());
                                    System.out.println("   Issuer  " + cert.getIssuerDN());

                                    keyStore.setCertificateEntry(hostName + i, serverCerts[i]); // throws KeyStoreException
                                }

                                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                                tmf.init(keyStore);
                                SSLContext sc2 = SSLContext.getInstance("TLSv1.2");
                                sc2.init(null, tmf.getTrustManagers(), new SecureRandom());

                                secureConnection.setSSLSocketFactory(sc2.getSocketFactory());
                            }
                        }
                    }
                    catch (SocketTimeoutException e)
                    {
                        System.out.println("Timeout waiting for SSL handshake. SSL probably not supported on this port");
                    }
                    finally
                    {
                        if (socket != null)
                        {
                            socket.close();
                        }
                    }
                }
                catch (Exception failed)
                {
                    failed.printStackTrace();
                }
            }
        }
    }

    
    public class ExecProcess implements Runnable
    {
        private String commandLine = "ls";
        private boolean useShell = true;
        
        public void run()
        {
            Runtime r = Runtime.getRuntime();
            Process app = null;
            BufferedReader output = null;
            String line;
            
            try
            {
                String[] cmd = {
                        "/bin/sh",
                        "-c",
                        commandLine
                        };
                
                
                if (!useShell)
                {
                    app = r.exec(commandLine);
                }
                else
                {
                    app = r.exec(cmd);
                }

                System.out.println("In ExecProcess after exec");
                output = new BufferedReader(new InputStreamReader(app.getInputStream()));
                
                while(true)
                {
                    line = output.readLine();
                    if(line == null)
                    {
                        System.out.println("In ExecProcess after EOF");
                        System.out.flush();
                        break;
                    }
                    execResults.add(line);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if( output != null)
                {
                    try
                    {
                        output.close();
                    }
                    catch (IOException ignored)
                    {
                        System.currentTimeMillis();
                    }
                }
            }
            
            if( app != null)
            {
                try
                {
                    app.waitFor(); // wait for process to complete
                }
                catch (InterruptedException e)
                {
                    System.err.println(e); // "Can'tHappen"
                    return;
                }
                System.err.println("Process done, exit status was " + app.exitValue());
            }
            execResults.add("ThatsAll");
            System.out.println("ExecProcess Completed");
        }
        
        public void setTask(String commandLine)
        {
            this.commandLine = new String(commandLine);
        }
        
        public void setShellUse(boolean value)
        {
            this.useShell = value;
        }
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        System.out.println("View Controller setup");
        super.addViewControllers(registry);
        registry.addViewController("/").setViewName("forward:/index");        
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry)
    {
        System.out.println("resources Controller setup");
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }

    public static void main(String[] args)
    {
        expectedResults.add("Expected results are:");
        expectedResults.add("ngci-sysvalidate     Eth1/8/3        120        S           a036.9fde.6ee2");
        expectedResults.add("ngci-sysvalidate     Eth1/9/3        120        S           a036.9fde.6ee0");

        SpringApplication.run(BootStrapWeb.class, args);
    }

}