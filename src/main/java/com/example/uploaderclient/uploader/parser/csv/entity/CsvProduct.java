package com.example.uploaderclient.uploader.parser.csv.entity;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;

import com.example.uploaderclient.uploader.parser.core.entity.ProductCandidate;
import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class CsvProduct implements ProductCandidate {

    @CsvBindByName(column = "Key")
    private String key;

    @CsvBindByName(column = "Identifier")
    private String identifier;

    @CsvBindByName(column = "Type")
    private String type;

    @CsvBindAndJoinByName(column = ".*", elementType = String.class)
    private MultiValuedMap<String, String> multiValuedSimpleAttributes = new ArrayListValuedHashMap<>();

    @Override
    public Map<String, String> getSimpleAttributes() {
        return multiValuedSimpleAttributes.asMap().entrySet().stream()
                .map(this::getEntryWithConcatenatedValues)
                .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<String, String> getEntryWithConcatenatedValues(Map.Entry<String, Collection<String>> entry) {
        Collection<String> valuesToConcat = getValuesToConcat(entry.getValue());
        String concatenatedValue = String.join(", ", valuesToConcat);
        return new SimpleEntry<>(entry.getKey(), concatenatedValue);
    }

    private Collection<String> getValuesToConcat(Collection<String> originalValues) {
        return CollectionUtils.emptyIfNull(originalValues)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}