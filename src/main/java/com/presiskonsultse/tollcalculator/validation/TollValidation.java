package com.presiskonsultse.tollcalculator.validation;


import com.presiskonsultse.tollcalculator.vehicles.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class TollValidation {

    private static final TollValidation instance = new TollValidation();

    private TollValidation(){}
    public static TollValidation getInstance(){
        return instance;
    }
    public void validate(Vehicle vehicle, Set<LocalDateTime> dates)
    {
        validateVehicle(vehicle);
        validateIfDatesContainsNull(dates);
        validateIfDatesNotTheSameDay(dates);
    }
    void validateVehicle(Vehicle vehicle)
    {
        if (vehicle == null)
            throw new IllegalArgumentException("Vehicle can't be null.");
    }

    void validateIfDatesContainsNull(Set<LocalDateTime> dates)
    {
        if (dates.stream().anyMatch(Objects::isNull)) {
            log.error("The input dates contain null: " + Arrays.toString(dates.toArray()));
            throw new IllegalArgumentException("The input dates has null entry.");
        }
    }
    void validateIfDatesNotTheSameDay(Set<LocalDateTime> dates)
    {
        // check if all dates are of the same day
        List<LocalDate> temp = dates.stream().map(LocalDateTime::toLocalDate).toList();
        LocalDate entry1 = temp.get(0);
        if (temp.stream().anyMatch(d -> !d.equals(entry1))) {
            log.error("The input dates are not of the same day: " + dates);
            throw new IllegalArgumentException("All dates shall be on the same day.");
        }
    }


}
