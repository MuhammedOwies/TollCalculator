package com.presiskonsultse.tollcalculator.common;

import com.presiskonsultse.tollcalculator.vehicles.Vehicle;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@Data
public class FeeFree {
    public boolean isTollFreeVehicle(Vehicle vehicle) {
        if (vehicle == null) return false;
        final Optional<String> first = Arrays.stream(Constants.vehicles.get(Constants.FREE_VEHICLE).split(",")).filter(s -> vehicle.getType().equals(s)).findFirst();
        return first.isPresent();
    }

    public static boolean isHoliday(LocalDate date) {
        return Constants.HOLIDAY_MAP.entrySet().stream().anyMatch(entry -> Objects.equals(entry.getValue(), date));
    }

    public Boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    public boolean validateDateNotNull(Set<LocalDateTime> dates) {
        return dates == null || dates.isEmpty();
    }

    public boolean checkFeeFree(Vehicle vehicle, Set<LocalDateTime> dates) {
        return (isTollFreeVehicle(vehicle) || validateDateNotNull(dates) || isHoliday(dates.stream().findFirst().get().toLocalDate()) || isWeekend(dates.stream().findFirst().get().toLocalDate()));
    }
}
