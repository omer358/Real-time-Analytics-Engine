package com.example.analyticsservice.config;

import com.example.analyticsservice.dto.Interval;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class StringToIntervalConverter implements Converter<String, Interval> {
    @Override
    public Interval convert(String source) {
        return Interval.valueOf(source.toUpperCase(Locale.ROOT));
    }
}
