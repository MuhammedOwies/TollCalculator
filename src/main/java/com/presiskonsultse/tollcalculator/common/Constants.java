package com.presiskonsultse.tollcalculator.common;

import com.presiskonsultse.tollcalculator.feePerPeriod.TollFeePeriod;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public class Constants {
    public static final int MAX_FEE_IN_ONE_DAY = 60;
    public static final String FREE_VEHICLE = "Free";
    public static final String VEHICLE = "NotFree";
    public static final String VEHICLE_PROP_NAME = "Vehicles.properties";
    public static final String FEES_PROP_NAME = "FeesByPeriod.properties";
    public static final String HOLIDAY_PROP_NAME = "holidays.properties";
    public static Set<TollFeePeriod> tollFeeByPeriod;
    public static  Map<String, String> vehicles;
    public static  Map<String, LocalDate> HOLIDAY_MAP;

    private Constants() {}

}
