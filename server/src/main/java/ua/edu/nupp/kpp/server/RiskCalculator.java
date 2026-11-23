package ua.edu.nupp.kpp.server;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class RiskCalculator {

    private static final double DEADLINE_PERIOD = 30.0;


    public static double factor1(String deadlineStr) {

        if (deadlineStr == null || deadlineStr.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Deadline cannot be empty"
            );
        }

        LocalDate deadline;
        try {

            deadline = LocalDate.parse(deadlineStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date format. Expected YYYY-MM-DD"
            );
        }

        long days = ChronoUnit.DAYS.between(
                LocalDate.now(),
                deadline
        );

        double result = (DEADLINE_PERIOD - days)
                / DEADLINE_PERIOD * 100.0;

        return round(Math.max(0, Math.min(100, result)));
    }


    public static double factor2(double utilizationPercent) {
        validateFactor(utilizationPercent);
        return round(utilizationPercent);
    }

    public static double factor3(double criticality) {
        validateFactor(criticality);
        return round(criticality);
    }

    public static double calculate(double f1, double f2, double f3) {

        validateFactor(f1);
        validateFactor(f2);
        validateFactor(f3);

        double result = 0.40 * f1
                + 0.35 * f2
                + 0.25 * f3;

        return round(result);
    }

    private static void validateFactor(double value) {

        if (!Double.isFinite(value) || value < 0 || value > 100) {
            throw new IllegalArgumentException(
                    "Factors must be in the range 0..100"
            );
        }
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}