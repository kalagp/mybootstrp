
# ngci-bootstrap


## Description

This repository contains the source code for the bootstrap idrac and configuration: 


## Documentation

###1. Export idrac configuration
GET: http://SERVICE_IP:8080/bootstrap/idrac/export/{IDRAC_IP}
for example: GET localhost:8080/bootstrap/idrac/export/10.234.122.15

Note that to export configuration as xml, set request header field "accept" as "text/xml", otherwsie, the configuration will be in json format.
 
 ###2. Update idrac configuration
 POST: http://SERVICE_IP:8080/bootstrap/idrac/update/{IDRAC_IP}
 BODY (json):
 {
    "UPDATE_FILED_1": "UPDATE_NEW_VALUE_1",
    ...
    "UPDATE_FILED_n": "UPDATE_NEW_VALUE_n"
 }
 
 for example: POST localhost:8080/bootstrap/idrac/update/10.234.122.15
 
 BODY:
 {
 	  "IPv4Static.1#DNS1":"10.136.112.220",
      "IPv4Static.1#DNS2": "10.239.128.100"
 }
 
It will update the DNSs of IDRAC 10.234.122.15.

To change the ip address, the Body passed in the call would be something like:
 {
  	  "IPv4Static.1#Address":"10.234.122.8"
 }
 
That would change the ip address from 10.234.122.15 to 10.234.122.8.
Note that some action such as ip change, may take up to 10 minutes to finish.

## Before you begin


## Building
mvn clean install


## Deploying


## Contributing 


## Community 

 
 

