package com.dell.ngci.idracbootstrap.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName="SystemConfiguration")
public class SystemConfiguration {

    @JacksonXmlProperty(localName="Model", isAttribute = true)
    private String model;

    @JacksonXmlProperty(localName="ServiceTag", isAttribute = true)
    private String serviceTag;

    @JacksonXmlProperty(localName="TimeStamp", isAttribute = true)
    private String timestamp;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Component")
    private List<SystemComponent> components;


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getServiceTag() {
        return serviceTag;
    }

    public void setServiceTag(String serviceTag) {
        this.serviceTag = serviceTag;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<SystemComponent> getComponents() {
        return components;
    }

    public void setComponents(List<SystemComponent> components) {
        this.components = components;
    }
}
