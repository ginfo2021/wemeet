package com.wemeet.dating.util.converters;

import com.wemeet.dating.model.enums.Gender;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class GenderListConverter implements AttributeConverter<List<Gender>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<Gender> enumList) {
        if (enumList == null)
            return null;
        List<String> stringList = new ArrayList<>();
        for (Enum currentEnum : enumList) {
            stringList.add(currentEnum.name());
        }
        return String.join(SPLIT_CHAR, stringList);
    }

    @Override
    public List<Gender> convertToEntityAttribute(String string) {
        if (string == null)
            return null;
        List<String> stringList = Arrays.asList(string.split(SPLIT_CHAR));
        List<Gender> genderList = new ArrayList<>();
        for (String currentString : stringList) {
            genderList.add(Gender.valueOf(currentString));
        }
        return genderList;
    }
}