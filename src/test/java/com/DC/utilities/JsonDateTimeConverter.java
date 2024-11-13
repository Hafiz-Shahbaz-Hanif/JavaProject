package com.DC.utilities;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

import java.time.Instant;
import java.util.Date;

public class JsonDateTimeConverter implements Converter<Long> {

    @Override
    public void convert(Long aLong, StrictJsonWriter strictJsonWriter) {
        try {
            Instant instant = new Date(aLong).toInstant();
            strictJsonWriter.writeString(instant.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
