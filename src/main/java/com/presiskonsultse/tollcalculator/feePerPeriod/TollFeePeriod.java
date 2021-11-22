package com.presiskonsultse.tollcalculator.feePerPeriod;

import com.presiskonsultse.tollcalculator.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Component
public class TollFeePeriod {
    private final LocalTime start;
    private final LocalTime end;
    private final int fee;

    public boolean containsTime(LocalTime time) {
        time = time.minusSeconds(time.getSecond());
        return (start.isBefore(time) || start.equals(time)) && (end.isAfter(time) || end.equals(time));
    }

    public int getTollFee(LocalTime time) {
        return Constants.tollFeeByPeriod.stream().filter(t -> t.containsTime(time)).map(TollFeePeriod::getFee).findFirst().orElse(0);
    }
}
