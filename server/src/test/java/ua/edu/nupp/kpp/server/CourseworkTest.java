package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CourseworkTest {

    // 1. F1 дорівнює нулю, якщо дедлайн більше ніж через 30 днів
    @Test
    void f1WhenDeadlineIsFarAwayIsZero() {
        // Arrange
        String deadline = LocalDate.now().plusDays(40).toString();
        double expected = 0.0;

        // Act
        double actual = RiskCalculator.factor1(deadline);

        // Assert
        assertEquals(expected, actual, 0.01);
    }

    // 2. F1 дорівнює 50, якщо до дедлайну 15 днів
    @Test
    void f1WhenDeadlineIsIn15DaysIs50() {
        // Arrange
        String deadline = LocalDate.now().plusDays(15).toString();
        double expected = 50.0;

        // Act
        double actual = RiskCalculator.factor1(deadline);

        // Assert
        assertEquals(expected, actual, 0.01);
    }

    // 3. Некоректний формат дати відхиляється
    @Test
    void f1RejectsInvalidDateFormat() {
        // Arrange
        String invalidDeadline = "23.11.2026";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.factor1(invalidDeadline));
    }

    // 4. Перевірка формули інтегрального показника
    @Test
    void riskIndexCalculatedCorrectly() {
        // Arrange
        double f1 = 50.0;
        double f2 = 60.0;
        double f3 = 80.0;
        double expected = 61.0;

        // Act
        double actual = RiskCalculator.calculate(f1, f2, f3);

        // Assert
        assertEquals(expected, actual, 0.01);
    }

    // 5. Значення фактора понад 100 відхиляється
    @Test
    void riskRejectsFactorAbove100() {
        // Arrange
        double f1 = 101.0;
        double f2 = 50.0;
        double f3 = 50.0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(f1, f2, f3));
    }

    // 6. Дозволений перехід CREATED -> APPROVED
    @Test
    void createdToApprovedAllowed() {
        // Arrange
        BookingState from = BookingState.CREATED;
        BookingState to = BookingState.APPROVED;

        // Act
        boolean actual = BookingService.isAllowedTransition(from, to);

        // Assert
        assertTrue(actual);
    }

    // 7. Дозволений перехід APPROVED -> ACTIVE
    @Test
    void approvedToActiveAllowed() {
        // Arrange
        BookingState from = BookingState.APPROVED;
        BookingState to = BookingState.ACTIVE;

        // Act
        boolean actual = BookingService.isAllowedTransition(from, to);

        // Assert
        assertTrue(actual);
    }

    // 8. Дозволений перехід ACTIVE -> CLOSED
    @Test
    void activeToClosedAllowed() {
        // Arrange
        BookingState from = BookingState.ACTIVE;
        BookingState to = BookingState.CLOSED;

        // Act
        boolean actual = BookingService.isAllowedTransition(from, to);

        // Assert
        assertTrue(actual);
    }

    // 9. Заборонений перехід CREATED -> CLOSED
    @Test
    void createdToClosedRejected() {
        // Arrange
        BookingState from = BookingState.CREATED;
        BookingState to = BookingState.CLOSED;

        // Act
        boolean actual = BookingService.isAllowedTransition(from, to);

        // Assert
        assertFalse(actual);
    }

    // 10. Критичність завдання понад 100 відхиляється
    @Test
    void bookingValidationRejectsInvalidCriticality() {
        // Arrange
        BookingService service = new BookingService();
        double invalidCriticality = 150.0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.add(1L, 1L, "2026-12-01",
                        invalidCriticality));
    }

    // 11. Порожній інвентарний код обладнання відхиляється
    @Test
    void equipmentValidationRejectsBlankCode() {
        // Arrange
        EquipmentService service = new EquipmentService();
        Equipment invalid = new Equipment(
                null, "", "Мікроскоп", "Оптика", 50.0
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.add(invalid));
    }

    // 12. Порожня електронна адреса дослідника відхиляється
    @Test
    void researcherValidationRejectsBlankEmail() {
        // Arrange
        ResearcherService service = new ResearcherService();
        Researcher invalid = new Researcher(
                null, "Іван Петренко", "Фізика", ""
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.add(invalid));
    }
}
