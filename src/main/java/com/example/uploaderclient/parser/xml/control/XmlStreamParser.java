package com.example.uploaderclient.parser.xml.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.uploaderclient.parser.core.boundary.Parser;
import com.example.uploaderclient.parser.core.boundary.SupportedTypes;
import com.example.uploaderclient.parser.core.entity.FileType;
import com.example.uploaderclient.parser.core.entity.ProductCandidate;
import com.example.uploaderclient.parser.xml.entity.XmlProduct;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.functions.BiConsumer;

import static javax.xml.stream.XMLStreamConstants.ATTRIBUTE;
import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

@Component
@SupportedTypes(FileType.XML)
class XmlStreamParser implements Parser {

    private final XMLInputFactory inputFactory;
    private final XmlMapper xmlMapper;

    @Autowired
    XmlStreamParser(XMLInputFactory inputFactory, XmlMapper xmlMapper) {
        this.inputFactory = inputFactory;
        this.xmlMapper = xmlMapper;
    }

    @Override
    public Flowable<ProductCandidate> read(InputStream dataSource) {
        return Flowable.generate(
                new XmlInitialStateProvider(dataSource),
                new XmlProductGenerator(),
                reader -> closeAll(reader, dataSource));
    }

    private void closeAll(XMLStreamReader reader, InputStream dataSource) throws IOException, XMLStreamException {
        try (InputStream ignored = dataSource) {
            reader.close();
        }
    }

    private final class XmlInitialStateProvider implements Callable<XMLStreamReader> {

        private static final String ROOT_ELEMENT_NAME = "sklep";
        private static final String WRONG_FILE_BEGIN_MESSAGE = "Not matching beginning of file. " +
                "Expected '%s' but got '%s' instead.";

        private final InputStream dataSource;

        XmlInitialStateProvider(InputStream dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public XMLStreamReader call() throws XMLStreamException {
            XMLStreamReader reader = getReader();
            readDocumentStart(reader);
            return reader;
        }

        private XMLStreamReader getReader() throws XMLStreamException {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(dataSource);
            return inputFactory.createFilteredReader(reader, this::filterUnnecessaryEvents);
        }

        private boolean filterUnnecessaryEvents(XMLStreamReader reader) {
            switch (reader.getEventType()) {
                case START_ELEMENT:
                case END_ELEMENT:
                case START_DOCUMENT:
                case END_DOCUMENT:
                case ATTRIBUTE:
                    return true;
                case CHARACTERS:
                    return StringUtils.isNotBlank(reader.getText());
                default:
                    return false;
            }
        }

        private void readDocumentStart(XMLStreamReader reader) throws XMLStreamException {
            if (reader.hasNext()) {
                checkIfStartElement(reader);
                checkIfMatchingElementName(reader);
            }
        }

        private void checkIfStartElement(XMLStreamReader reader) throws XMLStreamException {
            if (reader.next() != START_ELEMENT) {
                throw new XMLStreamException("Premature end of file!", reader.getLocation());
            }
        }

        private void checkIfMatchingElementName(XMLStreamReader reader) throws XMLStreamException {
            String startElementName = reader.getLocalName();
            if (!ROOT_ELEMENT_NAME.equals(startElementName)) {
                String exceptionMessage = String.format(WRONG_FILE_BEGIN_MESSAGE, ROOT_ELEMENT_NAME, startElementName);
                throw new XMLStreamException(exceptionMessage, reader.getLocation());
            }
        }
    }

    private final class XmlProductGenerator implements BiConsumer<XMLStreamReader, Emitter<ProductCandidate>> {

        private static final String RECORD_START_ELEMENT_NAME = "produkt";
        private static final String WRONG_RECORD_BEGIN_MESSAGE = "Not matching beginning of record. " +
                "Expected '%s' but got '%s' instead.";

        @Override
        public void accept(XMLStreamReader reader, Emitter<ProductCandidate> productEmitter) throws Exception {
            if (hasNextStartElement(reader)) {
                checkIfMatchingElementName(reader);
                XmlProduct xmlProduct = xmlMapper.readValue(reader, XmlProduct.class);
                productEmitter.onNext(xmlProduct);
            } else {
                productEmitter.onComplete();
            }
        }

        private boolean hasNextStartElement(XMLStreamReader reader) throws XMLStreamException {
            return reader.hasNext() && reader.next() == START_ELEMENT;
        }

        private void checkIfMatchingElementName(XMLStreamReader reader) throws XMLStreamException {
            String startElementName = reader.getLocalName();
            if (!RECORD_START_ELEMENT_NAME.equals(reader.getLocalName())) {
                String exceptionMessage = String.format(WRONG_RECORD_BEGIN_MESSAGE,
                        RECORD_START_ELEMENT_NAME, startElementName);
                throw new XMLStreamException(exceptionMessage, reader.getLocation());
            }
        }
    }
}