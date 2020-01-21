package com.example.uploaderclient.uploader.parser.core.control;

import javax.xml.stream.XMLInputFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Configuration
public class ParserConfiguration {

    @Bean
    public XMLInputFactory getXMLInputFactory() {
        return XMLInputFactory.newFactory();
    }

    @Bean
    public XmlMapper getXmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JaxbAnnotationModule());
        return xmlMapper;
    }
}