package com.presiskonsultse.tollcalculator.common;

import lombok.SneakyThrows;

import java.io.FileReader;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public final class PropertiesReader {
    private PropertiesReader() {}

    private static Properties loadProperties(String fileName) {
        Properties prop = new Properties();
        try (FileReader reader = new FileReader(fileName)) {
            prop.load(reader);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return prop;
    }

    @SneakyThrows
    public static Map<String, LocalDate> getPropertiesToMapDateValue(String fileName ) {

        Properties prop = loadProperties(fileName);
        return new HashMap<>(prop.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(),
                        e -> LocalDate.parse(e.getValue().toString()))));
    }
    @SneakyThrows
    public static Map<String, String> getPropertiesToMap(String fileName ) {

        Properties prop = loadProperties(fileName);
        return new HashMap<>(prop.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(),
                        e -> e.getValue().toString())));
    }
}
