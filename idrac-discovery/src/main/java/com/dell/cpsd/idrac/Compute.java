/**
 * Copyright &copy; 2018 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.idrac;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DISCOVER")
public class Compute
{

    @XmlElement(name = "RESP")
    private InnerCompute resp;
    private String       ip;

    public String getIp()
    {
        return ip;
    }

    public void setIp(final String ip)
    {
        this.ip = ip;
    }

    public String getEndPointType()
    {
        return resp.getEndPointType();
    }

    public String getEndPointVer()
    {
        return resp.getEndPointVer();
    }

    public String getProtocolType()
    {
        return resp.getEndPointType();
    }

    public String getProtocolVer()
    {
        return resp.getEndPointVer();
    }
}

class InnerCompute
{
    @XmlElement(name = "ENDPOINTTYPE")
    private String endPointType;
    @XmlElement(name = "ENDPOINTVER")
    private String endPointVer;
    @XmlElement(name = "PROTOCOLTYPE")
    private String protocolType;
    @XmlElement(name = "PROTOCOLVER")
    private String protocolVer;

    public String getEndPointType()
    {
        return endPointType;
    }

    public String getEndPointVer()
    {
        return endPointVer;
    }

    public String getProtocolType()
    {
        return protocolType;
    }

    public String getProtocolVer()
    {
        return protocolVer;
    }
}