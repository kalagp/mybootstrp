package com.dell.ngci.idracbootstrap.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

public class SystemComponent {

    @JacksonXmlProperty(localName="FQDD", isAttribute = true)
    private String fqdd;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Attribute")
    private List<Attribute> attributes;


    public String getFqdd() {
        return fqdd;
    }

    public void setFqdd(String fqdd) {
        this.fqdd = fqdd;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
}
