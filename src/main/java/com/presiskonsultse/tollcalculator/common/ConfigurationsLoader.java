package com.presiskonsultse.tollcalculator.common;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationsLoader implements CommandLineRunner {
    @Override
    public void run(String...args)  {
        FileLoader.loadFiles();
    }
}

