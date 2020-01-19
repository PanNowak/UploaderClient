package com.example.uploaderclient.parser.xml.entity;

import java.util.Collections;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.example.uploaderclient.parser.core.entity.ProductCandidate;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@XmlRootElement(name = "produkt")
public final class XmlProduct implements ProductCandidate {

    @XmlAttribute(name = "typ")
    private String type;

    @XmlAttribute(name = "klucz")
    private String key;

    @XmlElement(name = "identyfikator")
    private String identifier;

    @XmlElement(name = "parametry")
    private Map<String, String> simpleAttributes = Collections.emptyMap();
}