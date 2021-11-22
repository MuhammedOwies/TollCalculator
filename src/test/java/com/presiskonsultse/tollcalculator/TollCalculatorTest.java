package com.presiskonsultse.tollcalculator;


import com.presiskonsultse.tollcalculator.app.TollCalculator;
import com.presiskonsultse.tollcalculator.common.Constants;
import com.presiskonsultse.tollcalculator.common.FeeFree;
import com.presiskonsultse.tollcalculator.common.FileLoader;
import com.presiskonsultse.tollcalculator.validation.TollValidation;
import com.presiskonsultse.tollcalculator.vehicles.Car;
import com.presiskonsultse.tollcalculator.vehicles.Motorbike;
import com.presiskonsultse.tollcalculator.vehicles.Vehicle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

// TODO: 10/21/2021 FreeDates and holidays depends on the old solution
@ExtendWith(SpringExtension.class)
class TollCalculatorTest {
    private final TollCalculator tollCalculator;
    FeeFree feeFree;
    TollValidation tollValidation;

    public TollCalculatorTest() {
        FileLoader.loadFiles();
        feeFree = new FeeFree();
        tollValidation = TollValidation.getInstance();
        tollCalculator = new TollCalculator(Constants.tollFeeByPeriod.stream().findFirst().get(),feeFree,tollValidation);
    }

    @Test
    void testHolidayService() {
        // Easter Monday
        Assertions.assertTrue(feeFree.isHoliday(LocalDate.of(2021, 4, 5)));
        // Epiphany
        Assertions.assertTrue(feeFree.isHoliday(LocalDate.of(2021, 1, 6)));
        // Midsummer's Eve
        Assertions.assertTrue(feeFree.isHoliday(LocalDate.of(2021, 6, 25)));
        // All Saints' Eve
        Assertions.assertTrue(feeFree.isHoliday(LocalDate.of(2021, 11, 6)));
    }

    @Test
    void testTollFeePeriod() {
        Vehicle car = new Car();
        // non-weekend, not a holiday
        LocalDate date = LocalDate.of(2021, 2, 1);
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(0, 0, 59));
        int fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(0, fee);
        time = time.plusMinutes(29); // 00:29
        fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(0, fee);
        time = time.plusHours(6); // 06:29
        fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(8, fee);
        time = time.plusMinutes(1); // 06:30
        fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(13, fee);
        time = time.plusHours(1); // 07:30
        fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(18, fee);
        time = time.plusMinutes(40); // 08:10
        fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(13, fee);
        time = time.plusHours(3); // 11:10
        fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(8, fee);
        time = time.plusHours(4); // 15:10
        fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(13, fee);
        time = time.plusHours(1); // 16:10
        fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(18, fee);
        time = time.plusHours(1); // 17:10
        fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(13, fee);
        time = time.plusHours(1); // 18:10
        fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(8, fee);
        time = time.plusHours(1); // 19:10
        fee = tollCalculator.getTollFee(car, Set.of(time));
        Assertions.assertEquals(0, fee);
    }

    @Test
    void testTollFreeVehicle() {
        // toll-free vehicle
        Vehicle vehicle = new Motorbike();
        // non-weekend, not a holiday
        LocalDate date = LocalDate.of(2021, 2, 1);
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(8, 0));
        int fee = tollCalculator.getTollFee(vehicle, Set.of(time));
        Assertions.assertEquals(0, fee);
    }

    @Test
    void testTollFeeManyFeesWithTheSameHour()
    {
        // normal vehicle
        Vehicle vehicle = new Car();
        Set<LocalDateTime> dates = new HashSet<>();
        // non-weekend, not a holiday
        LocalDate date = LocalDate.of(2021, 2, 1);
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(5, 30)); // 05:30 fee: 0
        dates.add(time);
        time = time.plusMinutes(30);    // 06:00 fee: 8 (not counted)
        dates.add(time);
        time = time.plusMinutes(40);    // 06:40 fee: 13
        dates.add(time);
        time = time.plusMinutes(10);    // 06:50 fee: 13
        dates.add(time);

        Date[] datesArray = new Date[dates.size()];
        int fee = tollCalculator.getTollFee(vehicle, dates);
        // The toll-free shall be dropped. Thus the total fee should be 13.
        Assertions.assertEquals(21, fee);
    }
    @Test
    void testWeekend() {
        // normal vehicle
        Vehicle vehicle = new Car();
        // Saturday
        LocalDate date = LocalDate.of(2021, 2, 6);
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(8, 0));
        int fee = tollCalculator.getTollFee(vehicle, Set.of(time));
        Assertions.assertEquals(0, fee);
        // Sunday
        date = date.plusDays(1);
        time = LocalDateTime.of(date, LocalTime.of(8, 0));
        fee = tollCalculator.getTollFee(vehicle, Set.of(time));
        Assertions.assertEquals(0, fee);
    }

    @Test
    void testTollCalculator() {
        // normal vehicle
        Vehicle vehicle = new Car();
        Set<LocalDateTime> dates = new HashSet<>();
        // non-weekend, not a holiday
        LocalDate date = LocalDate.of(2021, 2, 1);
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(5, 30)); // 05:30 fee: 0
        dates.add(time);
        time = time.plusMinutes(30);    // 06:00 fee: 8 (not counted)
        dates.add(time);
        time = time.plusMinutes(40);    // 06:40 fee: 13
        dates.add(time);
        int fee = tollCalculator.getTollFee(vehicle, dates);
        // The toll-free shall be dropped. Thus the total fee should be 13.
        Assertions.assertEquals(21, fee);

        time = time.plusMinutes(40);    // 07:20 fee: 18
        dates.add(time);
        time = time.plusMinutes(40);    // 08:00 fee: 13
        dates.add(time);
        time = time.plusMinutes(40);    // 08:40 fee: 8 (not counted)
        dates.add(time);
        time = time.plusHours(6);    // 14:40 fee: 8
        dates.add(time);
        time = time.plusMinutes(40);    // 15:20 fee: 13
        dates.add(time);
        fee = tollCalculator.getTollFee(vehicle, dates);
        // 13 + 18 + 8 + 13 = 52
        Assertions.assertEquals(52, fee);

        time = time.plusMinutes(40);    // 16:00 fee: 18
        dates.add(time);
        fee = tollCalculator.getTollFee(vehicle, dates);
        Assertions.assertEquals(60, fee);
    }
    @DisplayName("testTollFeeWithFreeDates")
    @Test
    void testTollFeeWithFreeDates()
    {
        Vehicle vehicle = new Car();
        Set<LocalDateTime> dates = new HashSet<>();
        // non-weekend, not a holiday
        LocalDate date = LocalDate.of(2021, 2, 1);
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(5, 30)); // 05:30 fee: 0
        dates.add(time);
         time = LocalDateTime.of(date, LocalTime.of(0, 0, 59));
        dates.add(time);
        Date[] datesArray = new Date[dates.size()];
        int fee = tollCalculator.getTollFee(vehicle, dates);
        Assertions.assertEquals(0, fee);
    }

    @Test
    void testTollFeeWithNullDateArray()
    {
        Set<LocalDateTime> dates = new HashSet<>();
        // non-weekend, not a holiday
        LocalDate date = LocalDate.of(2021, 2, 1);
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(5, 30)); // 05:30 fee: 0
        dates.add(time);
        time = LocalDateTime.of(date, LocalTime.of(0, 0, 59));
        dates.add(time);
        Date[] datesArray = new Date[dates.size()];
        assertThrows(IllegalArgumentException.class, ()->{
            int fee = tollCalculator.getTollFee(null, dates);
        });

    }

    @Test
    void testTollFeeWithNullVehicle()
    {
        Vehicle vehicle = new Car();
        int fee = tollCalculator.getTollFee(vehicle, Set.of(LocalDateTime.now()));
        Assertions.assertEquals(0, fee);
    }
    @Test
    void testDatesNotTheSameDay()
    {
        Vehicle vehicle = new Car();
        Set<LocalDateTime> dates = new HashSet<>();
        // non-weekend, not a holiday
        LocalDate date = LocalDate.of(2021, 2, 1);
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(5, 30)); // 05:30 fee: 0
        dates.add(time);
        date = LocalDate.of(2021, 2, 2);
        time = LocalDateTime.of(date, LocalTime.of(0, 0, 59));
        dates.add(time);
        Date[] datesArray = new Date[dates.size()];
        assertThrows(IllegalArgumentException.class, ()->{
            int fee = tollCalculator.getTollFee(null, dates);
        });
    }

    @Test
    void testDatesContainsNull()
    {
        Vehicle vehicle = new Car();
        Set<LocalDateTime> dates = new HashSet<>();
        // non-weekend, not a holiday
        LocalDate date = LocalDate.of(2021, 2, 1);
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(5, 30)); // 05:30 fee: 0
        dates.add(time);
        dates.add(null);
        Date[] datesArray = new Date[dates.size()];
        assertThrows(IllegalArgumentException.class, ()->{
            int fee = tollCalculator.getTollFee(null, dates);
        });
    }

    @Test
    void testTollFeeWithMaxFeePerDay() {
        // normal vehicle
        Vehicle vehicle = new Car();
        Set<LocalDateTime> dates = new HashSet<>();
        // non-weekend, not a holiday
        LocalDate date = LocalDate.of(2021, 2, 1);
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(5, 30)); // 05:30 fee: 0
        dates.add(time);
        time = time.plusMinutes(30);    // 06:00 fee: 8 (not counted)
        dates.add(time);
        time = time.plusMinutes(40);    // 06:40 fee: 13
        dates.add(time);
        time = time.plusMinutes(40);    // 07:20 fee: 18
        dates.add(time);
        time = time.plusMinutes(40);    // 08:00 fee: 13
        dates.add(time);
        time = time.plusMinutes(40);    // 08:40 fee: 8 (not counted)
        dates.add(time);
        time = time.plusHours(6);    // 14:40 fee: 8
        dates.add(time);
        time = time.plusMinutes(40);    // 15:20 fee: 13
        dates.add(time);
        time = time.plusMinutes(40);    // 16:00 fee: 18
        dates.add(time);
        int fee = tollCalculator.getTollFee(vehicle, dates);
        Assertions.assertEquals(60, fee);
    }
}
