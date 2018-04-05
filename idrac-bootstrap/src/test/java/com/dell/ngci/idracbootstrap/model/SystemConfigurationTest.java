package com.dell.ngci.idracbootstrap.model;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class SystemConfigurationTest {

    Logger logger = LoggerFactory.getLogger(SystemConfigurationTest.class);



    @Test
    public void loadSystemConfigurationFromFile() throws IOException {
        File xmlfile = new File(getClass().getClassLoader().getResource("idrac-config.xml").getFile());

        assertTrue(xmlfile.exists());
        XmlMapper xmlMapper = new XmlMapper();
        SystemConfiguration systemConfiguration = xmlMapper.readValue(xmlfile, SystemConfiguration.class);

        assertNotNull(systemConfiguration);
        assertEquals(systemConfiguration.getComponents().size(), 1);
        assertEquals(systemConfiguration.getComponents().get(0).getAttributes().size(), 436);
    }

    @Test
    public void writeSystemConfigurationAsXMLString() throws IOException {

        XmlMapper xmlMapper = new XmlMapper();
        SystemConfiguration systemConfiguration = new SystemConfiguration();
        systemConfiguration.setModel("XYZ");
        systemConfiguration.setServiceTag("Tag");
        systemConfiguration.setTimestamp(new Date().toString());

        List<SystemComponent> systemComponents = new ArrayList<>();
        systemConfiguration.setComponents(systemComponents);
        SystemComponent systemComponent1 = new SystemComponent();
        systemComponents.add(systemComponent1);
        systemComponent1.setFqdd("FQDN1");
        List<Attribute> attributes = new ArrayList<>();
        Attribute attribute1 = new Attribute();
        attribute1.setName("Attr1");
        attribute1.setValue("Value1");
        Attribute attribute2 = new Attribute();
        attribute2.setName("Attr2");
        attribute2.setValue("Value2");
        attributes.add(attribute1);
        attributes.add(attribute2);
        systemComponent1.setAttributes(attributes);

        String xml = xmlMapper.writeValueAsString(systemConfiguration);
        logger.info("XML="+ xml);
        assertNotNull(xml);
    }
}