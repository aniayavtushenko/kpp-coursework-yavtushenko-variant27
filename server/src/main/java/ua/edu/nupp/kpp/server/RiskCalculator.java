package ua.edu.nupp.kpp.server;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class RiskCalculator {

    private static final double DEADLINE_PERIOD = 30.0;

    // F1 — близькість експериментального дедлайну
    public static double factor1(String deadlineStr) { // Змінено на String

        if (deadlineStr == null || deadlineStr.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Дедлайн не може бути порожнім"
            );
        }

        LocalDate deadline;
        try {
            // Перетворюємо рядок YYYY-MM-DD на дату для математичних розрахунків
            deadline = LocalDate.parse(deadlineStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неправильний формат дати. Очікується YYYY-MM-DD");
        }

        long days = ChronoUnit.DAYS.between(
                LocalDate.now(),
                deadline
        );

        double result = (DEADLINE_PERIOD - days)
                / DEADLINE_PERIOD * 100.0;

        return round(Math.max(0, Math.min(100, result)));
    }

    // F2 — завантаженість обладнання
    public static double factor2(double utilizationPercent) {
        validateFactor(utilizationPercent);
        return round(utilizationPercent);
    }

    // F3 — критичність дослідницького завдання
    public static double factor3(double criticality) {
        validateFactor(criticality);
        return round(criticality);
    }

    // Розрахунок інтегрального показника
    public static double calculate(double f1, double f2, double f3) {

        validateFactor(f1);
        validateFactor(f2);
        validateFactor(f3);

        double result = 0.40 * f1
                + 0.35 * f2
                + 0.25 * f3;

        return round(result);
    }

    // Перевірка діапазону факторів
    private static void validateFactor(double value) {

        if (!Double.isFinite(value) || value < 0 || value > 100) {
            throw new IllegalArgumentException(
                    "Фактори мають бути в діапазоні 0..100"
            );
        }
    }

    // Округлення до двох знаків після коми
    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}