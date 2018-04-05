package com.dell.ngci.idracbootstrap.model;

import java.util.HashMap;

public class Network4Setting extends HashMap<String, String> {
    /*
        <Attribute Name="IPv4.1#Enable">Enabled</Attribute>
        <Attribute Name="IPv4.1#DHCPEnable">Disabled</Attribute>
        <Attribute Name="IPv6.1#Enable">Disabled</Attribute>
        <Attribute Name="IPv6.1#AutoConfig">Enabled</Attribute>
        <!--  <Attribute Name="NICStatic.1#DNSDomainName"></Attribute>
-->
        <Attribute Name="NICStatic.1#DNSDomainFromDHCP">Disabled</Attribute>
        <!--  <Attribute Name="IPv4Static.1#Address">10.234.122.15</Attribute>
-->
        <Attribute Name="IPv4Static.1#Netmask">255.255.255.192</Attribute>
        <Attribute Name="IPv4Static.1#Gateway">10.234.122.1</Attribute>
        <Attribute Name="IPv4Static.1#DNS1">10.136.112.220</Attribute>
        <Attribute Name="IPv4Static.1#DNS2">0.0.0.0</Attribute>
        <Attribute Name="IPv4Static.1#DNSFromDHCP">Disabled</Attribute>
        <!--  <Attribute Name="IPv6Static.1#Address1">::</Attribute>
-->
        <Attribute Name="IPv6Static.1#Gateway">::</Attribute>
        <Attribute Name="IPv6Static.1#PrefixLength">64</Attribute>
        <Attribute Name="IPv6Static.1#DNS1">::</Attribute>
        <Attribute Name="IPv6Static.1#DNS2">::</Attribute>
        <Attribute Name="IPv6Static.1#DNSFromDHCP6">Disabled</Attribute>
     */
}
