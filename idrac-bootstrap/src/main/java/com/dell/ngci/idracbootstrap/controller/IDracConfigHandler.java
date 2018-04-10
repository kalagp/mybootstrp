package com.dell.ngci.idracbootstrap.controller;

import com.dell.ngci.idracbootstrap.model.*;
import com.dell.ngci.idracbootstrap.utils.CustomRestTemplate;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bootstrap/idrac")
public class IDracConfigHandler {
    private static Logger logger = LoggerFactory.getLogger(IDracConfigHandler.class);

    @Value("${max.wait.ms:300000}")
    private long maxwait;

    @Autowired
    CustomRestTemplate customRestTemplate;

    @GetMapping("/find")
    public ResponseEntity<List<IDracInstance>> findIDracInstances(@RequestParam String ipRange)
    {
        return null;
    }

    @PostMapping("/import/{ipaddress}")
    public ResponseEntity<?> importIDracConfiguration(HttpServletRequest request, @PathVariable String ipaddress, @RequestBody Network4Setting networkSetting ) throws Exception {
        return null;
    }

    @PostMapping("/update/{ipaddress}")
    public ResponseEntity<?> updateNetworkSetting(@PathVariable String ipaddress, @RequestBody Network4Setting networkSetting ) throws Exception {
        if (networkSetting == null)
        {
            throw  new Exception("No input for NetworkSetting.");
        }
        ResponseEntity<SystemConfiguration> systemConfigurationResponseEntity = exportCurrentIDracConfiguration(ipaddress);
        // set any that has a value from the input
        SystemConfiguration systemConfiguration = systemConfigurationResponseEntity.getBody();

        String ipChanged = null;
        for (SystemComponent component : systemConfiguration.getComponents())
        {
            if ("iDRAC.Embedded.1".equals(component.getFqdd())) // found idrac
            {
                List<Attribute> attributes = new ArrayList<>();
                for (Map.Entry<String, String> entry : networkSetting.entrySet())
                {
                    Attribute attributeToUpdate = null;
                    for (Attribute attr: component.getAttributes())
                    {
                        if (entry.getKey().equals(attr.getName()))
                        {
                            attributeToUpdate = attr;
                            break;
                        }
                    }
                    if (attributeToUpdate == null) {
                        attributeToUpdate = new Attribute();
                        attributeToUpdate.setName(entry.getKey());
                        logger.info("adding new attribute: "+entry.getKey()+" "+entry.getValue());
                    }  else {
                        logger.info("updating attribute: "+entry.getKey()+" "+entry.getValue());
                    }
                    if (!entry.getValue().equals(attributeToUpdate.getValue())) {
                        attributeToUpdate.setValue(entry.getValue());
                        attributes.add(attributeToUpdate);
                        if ("IPv4Static.1#Address".equals(attributeToUpdate.getName()) && !("" + ipaddress).equals(attributeToUpdate.getValue())) {
                            ipChanged = attributeToUpdate.getValue();
                        }
                    } else {
                        logger.info("Skip attribute "+entry.getKey()+" with the same value with the existing setting:" +entry.getValue());
                    }
                }
                if (attributes.isEmpty()){
                    throw new Exception("All attributes are the same as current settings. No change made.");
                }
                component.setAttributes(attributes);
                break;
            }
        }

        // import the configuration
        XmlMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(systemConfiguration);
        logger.info("Import configuration=>\n"+xml);

        ObjectNode importRequestBody = new ObjectNode(JsonNodeFactory.instance);
        importRequestBody.put("ImportBuffer", xml);
        ObjectNode shareParameters = importRequestBody.putObject("ShareParameters");
        shareParameters.put("Target","IDRAC");

        // username/password should be from some other secure place in the real production env
        String username = "root";
        String password = "Vc3m0l@b";
        ResponseEntity<?> postResponse = callRemoteRestApi(username, password, HttpMethod.POST,
                "https://"+ipaddress+"/redfish/v1/Managers/iDRAC.Embedded.1/Actions/Oem/EID_674_Manager.ImportSystemConfiguration",
                importRequestBody, Object.class);

        if (postResponse.getStatusCode() != HttpStatus.ACCEPTED)
        {
            throw new Exception("Failed initiate import system configuration:" + postResponse.getStatusCode());
        }

        String location = (String)(postResponse.getHeaders().getFirst("location"));
        if (location == null)
        {
            throw new Exception("Failed to get the import task location:" + postResponse.getHeaders().toString());
        }


        long ms = System.currentTimeMillis();

        ResponseEntity<Object> status;
        do {
            Thread.sleep(1000L);
            status = callRemoteRestApi(username, password, HttpMethod.GET,
                    "https://" + ipaddress + location,
                    null, Object.class);
            logger.info("call "+location+" returned "+status.getStatusCode());
            // FIXME: if the ipaddress changed, this could throw exception or error out, need to handle the case
            if (status.getStatusCode() == HttpStatus.OK)
            {
                break;
            }
        }while (System.currentTimeMillis()-ms < maxwait);

        if (ipChanged != null)
        {
            logger.info("waiting system to comeback after ip changed to "+ipChanged);
            //Sleep 30s first
            Thread.sleep(30000);

            ms = System.currentTimeMillis();
            do {
                Thread.sleep(1000L);
                try {
                    status = callRemoteRestApi(username, password, HttpMethod.GET,
                            "https://" + ipChanged + location,
                            null, Object.class);
                    logger.info("call " + ipChanged + " " + location + " returned " + status.getStatusCode());
                    if (status.getStatusCode() == HttpStatus.OK) {
                        break;
                    }
                }catch(Exception e)
                {
                    logger.info("waiting system to come back: "+ipChanged+" "+e);
                }
            }while (System.currentTimeMillis()-ms < maxwait);

        }

        return new ResponseEntity<Object>(status.getBody(), status.getStatusCode());
    }

    @GetMapping("/export/{ipaddress}")
    public ResponseEntity<?> getIDracNetworkSetting(HttpServletRequest request, @PathVariable String ipaddress) throws Exception {

        ResponseEntity<SystemConfiguration> currentIDracConfiguration = exportCurrentIDracConfiguration(ipaddress);
        logger.info("Headers:");
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null){
            while(headerNames.hasMoreElements()){
                String header = headerNames.nextElement();
                String value = request.getHeader(header);
                logger.info(header + " = "+value);
            }
        }
        if ("text/xml".equals(request.getHeader("accept"))) {
            XmlMapper xmlMapper = new XmlMapper();
            String xml = xmlMapper.writeValueAsString(currentIDracConfiguration.getBody());
            logger.info("XML:" + xml);
            return new ResponseEntity<>(xml, currentIDracConfiguration.getStatusCode());
        }
        else
        {
            // json
            return new ResponseEntity<>(currentIDracConfiguration.getBody(), currentIDracConfiguration.getStatusCode());
        }
    }

    private ResponseEntity<SystemConfiguration> exportCurrentIDracConfiguration(String ipaddress) throws Exception {
        // Post: https://10.234.122.15/redfish/v1/Managers/iDRAC.Embedded.1/Actions/Oem/EID_674_Manager.ExportSystemConfiguration
        //{
        //	"ExportFormat": "XML",
        //	"ShareParameters": {
        //		"Target": "IDRAC"
        //	}
        //}
        ObjectNode exportRequestBody = new ObjectNode(JsonNodeFactory.instance);
        exportRequestBody.put("ExportFormat", "XML");
        ObjectNode shareParameters = exportRequestBody.putObject("ShareParameters");
        shareParameters.put("Target","IDRAC");

        // username/password should be from some other secure place in the real production env
        String username = "root";
        String password = "Vc3m0l@b";
        ResponseEntity<?> postResponse = callRemoteRestApi(username, password, HttpMethod.POST,
                "https://"+ipaddress+"/redfish/v1/Managers/iDRAC.Embedded.1/Actions/Oem/EID_674_Manager.ExportSystemConfiguration",
                exportRequestBody, Object.class);

        if (postResponse.getStatusCode() != HttpStatus.ACCEPTED)
        {
            throw new Exception("Failed initiate export system configuration:" + postResponse.getStatusCode());
        }

        String location = (String)(postResponse.getHeaders().getFirst("location"));
        if (location == null)
        {
            throw new Exception("Failed to get the task location:" + postResponse.getHeaders().toString());
        }


        ResponseEntity<SystemConfiguration> status;
        do {
            Thread.sleep(1000L);
            status = callRemoteRestApi(username, password, HttpMethod.GET,
                    "https://" + ipaddress + location,
                    null, SystemConfiguration.class);
            logger.info("call "+location+" returned "+status.getStatusCode());

        }while (status.getStatusCode() != HttpStatus.OK);

        return status;
    }

    private <T> ResponseEntity<T> callRemoteRestApi(String username, String password, HttpMethod method, String url, Object request, Class<T> responseType){
        String plainCreds = username+":"+password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        if (request != null){
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        HttpEntity<?> httpEntity = new HttpEntity<>(request, headers);

        return customRestTemplate.execForEntity(method, url, httpEntity, responseType);
    }
}
