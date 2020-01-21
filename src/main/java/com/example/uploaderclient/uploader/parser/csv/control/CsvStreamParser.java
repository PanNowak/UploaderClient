package com.example.uploaderclient.uploader.parser.csv.control;

import com.example.uploaderclient.uploader.parser.core.boundary.Parser;
import com.example.uploaderclient.uploader.parser.core.boundary.SupportedTypes;
import com.example.uploaderclient.uploader.parser.core.entity.FileType;
import com.example.uploaderclient.uploader.parser.core.entity.ProductCandidate;
import com.example.uploaderclient.uploader.parser.csv.entity.CsvProduct;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

@Slf4j
@Component
@SupportedTypes(FileType.CSV)
class CsvStreamParser implements Parser {

    private final char separator;

    @Autowired
    CsvStreamParser(@Value("${csv.separator}") char separator) {
        this.separator = separator;
    }

    @Override
    public Flowable<ProductCandidate> read(InputStream dataSource) {
        return Flowable.just(dataSource)
                .map(this::getBufferedReader)
                .concatMapIterable(this::getCsvProducts)
                .cast(ProductCandidate.class)
                .doFinally(dataSource::close);
    }

    private BufferedReader getBufferedReader(InputStream dataSource) {
        return new BufferedReader(new InputStreamReader(new BOMInputStream(dataSource)));
    }

    private CsvToBean<CsvProduct> getCsvProducts(Reader reader) {
        return new CsvToBeanBuilder<CsvProduct>(reader)
                .withSeparator(separator)
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .withType(CsvProduct.class)
                .build();
    }
}