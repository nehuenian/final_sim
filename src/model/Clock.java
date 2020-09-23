package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Clock {

    private static Clock singletonClock;

    private double currentMinute;

    private Clock() {
       this.currentMinute = 0;
    }

    public static Clock getInstance() {
        if (singletonClock == null) {
            singletonClock = new Clock();
        }
        return singletonClock;
    }

    public double getCurrentMinute() {
        return currentMinute;
    }

    public void setCurrentMinute(double currentMinute) {
        this.currentMinute = currentMinute;
    }
}
