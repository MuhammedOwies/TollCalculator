package com.presiskonsultse.tollcalculator.app;

import com.presiskonsultse.tollcalculator.common.Constants;
import com.presiskonsultse.tollcalculator.common.FeeFree;
import com.presiskonsultse.tollcalculator.feePerPeriod.TollFeePeriod;
import com.presiskonsultse.tollcalculator.validation.TollValidation;
import com.presiskonsultse.tollcalculator.vehicles.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * The type Toll calculator.
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Slf4j
public class TollCalculator {

    private final TollFeePeriod getTollFee;
    private final FeeFree feeFree;
    /**
     * The Toll validation.
     */
    TollValidation tollValidation = TollValidation.getInstance();

    /**
     * Calculate the total toll fee for one day.
     *
     * @param vehicle the vehicle
     * @param dates   date and time of all passes on one day
     * @return the total toll fee for that day
     */
    public int getTollFee(Vehicle vehicle, Set<LocalDateTime> dates) {
        tollValidation.validate(vehicle, dates);
        if (feeFree.checkFeeFree(vehicle, dates))
            return 0;
        List<LocalTime> times = dates.stream().map(LocalDateTime::toLocalTime).sorted().toList();
        if (times.isEmpty())
            return 0;
        // end of one-hour interval
        LocalTime intervalEnd = times.get(0).plusHours(1);
        int totalFee = 0;
        int maxFee = 0;
        for (LocalTime time : times) {
            int tollFee = getTollFee.getTollFee(time);
            if (time.isAfter(intervalEnd)) {
                // passed one-hour period, add the highest toll in this hour to total fee.
                totalFee += maxFee;
                intervalEnd = time.plusHours(1);
                maxFee = tollFee;
                log.info("Fees with different hours");
            } else {
                log.info("Fees with the same hour" + time);
                if (maxFee < tollFee)
                    maxFee = tollFee;
            }
        }
        // add the last entry to total. Note: this entry is not counted within the loop.
        totalFee += maxFee;
        return (Math.min(totalFee, Constants.MAX_FEE_IN_ONE_DAY));
    }
}
