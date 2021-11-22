package com.presiskonsultse.tollcalculator.common;

import com.presiskonsultse.tollcalculator.feePerPeriod.TollFeePeriod;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;

@Component
public class FileLoader {

    public static void loadFiles(){
        loadFeesPerPeriodFile();
        loadVehiclesFile();
        loadHolidaysFile();
    }
    private static void loadFeesPerPeriodFile(){
        Constants.tollFeeByPeriod = new HashSet<>();
        final Map<String, String> feesByPeriodEntries = PropertiesReader.getPropertiesToMap(Constants.FEES_PROP_NAME);
        feesByPeriodEntries.forEach((key, value) -> Constants.tollFeeByPeriod
                .add(new TollFeePeriod(LocalTime.of(Integer.parseInt(key.substring(0, 2)),
                        Integer.parseInt(key.substring(2, 4))),
                        LocalTime.of(Integer.parseInt(key.substring(5, 7)),
                                Integer.parseInt(key.substring(7, 9))),
                        Integer.parseInt(value))));
    }
    private static void loadVehiclesFile()
    {
        Constants.vehicles = PropertiesReader.getPropertiesToMap(Constants.VEHICLE_PROP_NAME);
    }

    private static void loadHolidaysFile()
    {
        Constants.HOLIDAY_MAP = PropertiesReader.getPropertiesToMapDateValue(Constants.HOLIDAY_PROP_NAME);
    }

    private FileLoader() {}
}
