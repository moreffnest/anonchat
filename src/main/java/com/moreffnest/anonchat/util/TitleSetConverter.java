package com.moreffnest.anonchat.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moreffnest.parsers.WebListParser;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Set;

@AllArgsConstructor
@Converter
public class TitleSetConverter implements AttributeConverter<Set<WebListParser.Title>, byte[]> {
    private ObjectMapper objectMapper;

    @Override
    public byte[] convertToDatabaseColumn(Set<WebListParser.Title> attribute) {
        try {
            return objectMapper.writeValueAsBytes(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<WebListParser.Title> convertToEntityAttribute(byte[] dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<Set<WebListParser.Title>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
