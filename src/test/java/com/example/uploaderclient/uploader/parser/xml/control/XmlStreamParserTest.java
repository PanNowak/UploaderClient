package com.example.uploaderclient.uploader.parser.xml.control;

import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.uploaderclient.uploader.parser.core.control.ParserConfiguration;
import com.example.uploaderclient.uploader.parser.core.entity.ProductCandidate;

import io.reactivex.Flowable;

class XmlStreamParserTest {

    private XmlStreamParser xmlStreamParser;

    @BeforeEach
    void setUp() {
        ParserConfiguration configuration = new ParserConfiguration();
        xmlStreamParser = new XmlStreamParser(configuration.getXMLInputFactory(), configuration.getXmlMapper());
    }

    @Test
    void shouldParseFullyCorrectXmlFile() {
        readDataFromClassPath("Fully-correct-sklep.xml")
                .test(1)
                .assertSubscribed()
                .assertValue(xmlProduct -> "płyta".equals(xmlProduct.getType()))
                .assertValue(xmlProduct -> "10".equals(xmlProduct.getKey()))
                .assertValue(xmlProduct -> "plyta10".equals(xmlProduct.getIdentifier()))
                .assertValue(xmlProduct -> xmlProduct.getSimpleAttributes().size() == 4)
                .assertValue(xmlProduct -> "Unloved".equals(xmlProduct.getSimpleAttributes().get("tytuł")))
                .assertValue(xmlProduct -> "Maciej Obara Quartet".equals(xmlProduct.getSimpleAttributes().get("wykonawca")))
                .assertValue(xmlProduct -> "jazz".equals(xmlProduct.getSimpleAttributes().get("gatunek")))
                .assertValue(xmlProduct -> "60".equals(xmlProduct.getSimpleAttributes().get("cena")))

                .requestMore(2)
                .assertValueCount(3)

                .requestMore(Long.MAX_VALUE)
                .assertValueCount(4)
                .assertValueAt(3, xmlProduct -> "komiks".equals(xmlProduct.getType()))
                .assertValueAt(3, xmlProduct -> "40".equals(xmlProduct.getKey()))
                .assertValueAt(3, xmlProduct -> "komiks40".equals(xmlProduct.getIdentifier()))
                .assertValueAt(3, xmlProduct -> xmlProduct.getSimpleAttributes().size() == 2)
                .assertValueAt(3, xmlProduct -> "Superman".equals(xmlProduct.getSimpleAttributes().get("tytuł")))
                .assertValueAt(3, xmlProduct -> "Superman Ostatni Syn Kryptona".equals(xmlProduct.getSimpleAttributes().get("podtytuł")))

                .assertNoErrors()
                .assertComplete();
    }

    @Test
    void shouldParseXmlFileWithMissingValues() {
        readDataFromClassPath("Missing-values-correct-sklep.xml")
                .test(1)
                .assertSubscribed()
                .assertValue(xmlProduct -> "płyta".equals(xmlProduct.getType()))
                .assertValue(xmlProduct -> xmlProduct.getKey() == null)
                .assertValue(xmlProduct -> xmlProduct.getIdentifier() == null)
                .assertValue(xmlProduct -> xmlProduct.getSimpleAttributes().size() == 4)
                .assertValue(xmlProduct -> "Unloved".equals(xmlProduct.getSimpleAttributes().get("tytuł")))
                .assertValue(xmlProduct -> "Maciej Obara Quartet".equals(xmlProduct.getSimpleAttributes().get("wykonawca")))
                .assertValue(xmlProduct -> "jazz".equals(xmlProduct.getSimpleAttributes().get("gatunek")))
                .assertValue(xmlProduct -> "60".equals(xmlProduct.getSimpleAttributes().get("cena")))

                .requestMore(2)
                .assertValueCount(3)

                .requestMore(Long.MAX_VALUE)
                .assertValueCount(4)
                .assertValueAt(3, xmlProduct -> xmlProduct.getType() == null)
                .assertValueAt(3, xmlProduct -> "40".equals(xmlProduct.getKey()))
                .assertValueAt(3, xmlProduct -> "komiks40".equals(xmlProduct.getIdentifier()))
                .assertValueAt(3, xmlProduct -> xmlProduct.getSimpleAttributes().isEmpty())

                .assertNoErrors()
                .assertComplete();
    }

    @Test
    void shouldThrowExceptionIfXmlFileDoesNotStartWithCorrectValue() {
        readDataFromClassPath("Incorrect-beginning-sklep.xml")
                .test()
                .assertSubscribed()
                .assertNoValues()
                .assertError(XMLStreamException.class);
    }

    @Test
    void shouldThrowExceptionIfXmlFileContainsIncorrectlyNamedRecord() {
        readDataFromClassPath("Incorrect-record-sklep.xml")
                .test()
                .assertSubscribed()
                .assertValueCount(2)
                .assertError(XMLStreamException.class);
    }

    private Flowable<ProductCandidate> readDataFromClassPath(String filename) {
        InputStream dataSource = getClass().getClassLoader().getResourceAsStream(filename);
        return xmlStreamParser.read(dataSource);
    }
}