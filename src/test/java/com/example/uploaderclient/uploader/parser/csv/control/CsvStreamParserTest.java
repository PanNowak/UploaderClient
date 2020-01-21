package com.example.uploaderclient.uploader.parser.csv.control;

import com.example.uploaderclient.uploader.parser.core.entity.ProductCandidate;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.reactivex.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

class CsvStreamParserTest {

    private static final char DEFAULT_SEPARATOR = ';';

    private CsvStreamParser csvStreamParser;

    @BeforeEach
    void setUp() {
        csvStreamParser = new CsvStreamParser(DEFAULT_SEPARATOR);
    }

    @Test
    void shouldParseFullyCorrectCsvFile() {
        readDataFromClassPath("Fully-correct-store.csv")
                .test(1)
                .assertSubscribed()
                .assertValue(csvProduct -> "1".equals(csvProduct.getKey()))
                .assertValue(csvProduct -> "Book-1".equals(csvProduct.getIdentifier()))
                .assertValue(csvProduct -> "Book".equals(csvProduct.getType()))
                .assertValue(csvProduct -> csvProduct.getSimpleAttributes().size() == 2)
                .assertValue(csvProduct -> "Robert C. Martin".equals(csvProduct.getSimpleAttributes().get("Author")))
                .assertValue(csvProduct -> "Clean Code".equals(csvProduct.getSimpleAttributes().get("Title")))

                .requestMore(2)
                .assertValueCount(3)

                .requestMore(Long.MAX_VALUE)
                .assertValueCount(4)
                .assertValueAt(3, csvProduct -> "4".equals(csvProduct.getKey()))
                .assertValueAt(3, csvProduct -> "Film-2".equals(csvProduct.getIdentifier()))
                .assertValueAt(3, csvProduct -> "Film".equals(csvProduct.getType()))
                .assertValueAt(3, csvProduct -> csvProduct.getSimpleAttributes().size() == 2)
                .assertValueAt(3, csvProduct -> "George Lucas".equals(csvProduct.getSimpleAttributes().get("Author")))
                .assertValueAt(3, csvProduct -> "Star Wars".equals(csvProduct.getSimpleAttributes().get("Title")))

                .assertNoErrors()
                .assertComplete();
    }

    @Test
    void shouldParseCsvFileWithEmptyFields() {
        readDataFromClassPath("Empty-fields-correct-store.csv")
                .test(3)
                .assertSubscribed()
                .assertValueCount(3)
                .assertValueAt(2, csvProduct -> "3".equals(csvProduct.getKey()))
                .assertValueAt(2, csvProduct -> "Film-1".equals(csvProduct.getIdentifier()))
                .assertValueAt(2, csvProduct -> csvProduct.getType() == null)
                .assertValueAt(2, csvProduct -> csvProduct.getSimpleAttributes().size() == 1)
                .assertValueAt(2, csvProduct -> "Lilly Wachowski, Lana Wachowski".equals(csvProduct.getSimpleAttributes().get("Author")))

                .requestMore(Long.MAX_VALUE)
                .assertValueCount(4)
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    void shouldThrowExceptionIfCsvFileContainsIncompleteHeader() {
        readDataFromClassPath("Incomplete-header-store.csv")
                .test()
                .assertSubscribed()
                .assertNoValues()
                .assertError(throwable -> getToRootException(throwable) instanceof CsvRequiredFieldEmptyException);
    }

    @Test
    void shouldThrowExceptionIfCsvFileContainsIncompleteRecord() {
        readDataFromClassPath("Incomplete-record-store.csv")
                .test()
                .assertSubscribed()
                .assertError(throwable -> getToRootException(throwable) instanceof CsvRequiredFieldEmptyException);
    }

    private Flowable<ProductCandidate> readDataFromClassPath(String filename) {
        InputStream dataSource = getClass().getClassLoader().getResourceAsStream(filename);
        return csvStreamParser.read(dataSource);
    }

    private Throwable getToRootException(Throwable throwable) {
        Throwable rootException = throwable;
        while (rootException.getCause() != null) {
            rootException = rootException.getCause();
        }
        return rootException;
    }
}