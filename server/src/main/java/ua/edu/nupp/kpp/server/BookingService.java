
package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class BookingService {

    private static final Logger LOG =
            Logger.getLogger(BookingService.class.getName());

    private final BookingDAO dao = new BookingDAO();
    private final EquipmentDAO equipmentDAO = new EquipmentDAO();
    private final ResearcherDAO researcherDAO = new ResearcherDAO();

    public List<Booking> getAll() throws SQLException {
        return dao.getAll();
    }

    public Booking getById(long id) throws SQLException {
        return dao.getById(id);
    }

    // Створення бронювання
    public void add(long equipmentId, long researcherId,
                    String deadline, double f3) throws SQLException {

        validateInput(equipmentId, researcherId, deadline, f3);

        Equipment equipment = equipmentDAO.getById(equipmentId);
        if (equipment == null) {
            throw new IllegalArgumentException("Equipment not found");
        }

        requireResearcher(researcherId);

        Booking booking = fromEquipment(
                null,
                equipmentId,
                researcherId,
                deadline,
                f3,
                BookingState.CREATED,
                equipment
        );

        dao.add(booking);
        LOG.info("Booking added");
    }

    // Редагування бронювання з перерахунком факторів
    public void update(long id, long equipmentId, long researcherId,
                       String deadline, double f3) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("Invalid booking ID");
        }

        Booking old = dao.getById(id);

        if (old == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        validateInput(equipmentId, researcherId, deadline, f3);

        Equipment equipment = equipmentDAO.getById(equipmentId);
        if (equipment == null) {
            throw new IllegalArgumentException("Equipment not found");
        }

        requireResearcher(researcherId);

        Booking updatedBooking = fromEquipment(
                id,
                equipmentId,
                researcherId,
                deadline,
                f3,
                old.getState(),
                equipment
        );

        dao.update(updatedBooking);
        LOG.info("Booking updated");
    }

    // Перевірка дозволеного переходу стану бронювання
    public static boolean isAllowedTransition(BookingState from,
                                              BookingState to) {
        return (from == BookingState.CREATED
                && to == BookingState.APPROVED)
                || (from == BookingState.APPROVED
                && to == BookingState.ACTIVE)
                || (from == BookingState.ACTIVE
                && to == BookingState.CLOSED);
    }

    // Зміна стану бронювання
    public void changeState(long id, BookingState next) throws SQLException {
        Booking current = dao.getById(id);

        if (current == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning(
                    "Invalid state transition: "
                            + current.getState()
                            + " -> "
                            + next
            );

            throw new IllegalArgumentException("Invalid state transition");
        }

        dao.updateState(id, next);

        LOG.info(
                "Booking state changed: "
                        + current.getState()
                        + " -> "
                        + next
        );
    }

    // Видалення бронювання
    public void delete(long id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid booking ID");
        }

        dao.delete(id);
        LOG.info("Booking deleted");
    }

    // Формування бронювання та розрахунок факторів
    private Booking fromEquipment(Long id, long equipmentId,
                                  long researcherId, String deadline,
                                  double f3, BookingState state,
                                  Equipment equipment) {

        double f1 = RiskCalculator.factor1(deadline);
        double f2 = RiskCalculator.factor2(
                equipment.getUtilizationPercent()
        );
        double f3Value = RiskCalculator.factor3(f3);

        double riskIndex = RiskCalculator.calculate(f1, f2, f3Value);

        return new Booking(
                id,
                equipmentId,
                researcherId,
                state,
                deadline,
                f1,
                f2,
                f3Value,
                riskIndex
        );
    }

    // Перевірка вхідних даних
    private void validateInput(long equipmentId, long researcherId,
                               String deadline, double f3) {

        if (equipmentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid equipment ID"
            );
        }

        if (researcherId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid researcher ID"
            );
        }

        // Перевіряємо, чи рядок не порожній
        if (deadline == null || deadline.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Deadline is required"
            );
        }

        if (!Double.isFinite(f3) || f3 < 0 || f3 > 100) {
            throw new IllegalArgumentException(
                    "Criticality must be between 0 and 100"
            );
        }
    }

    // Перевірка існування дослідника
    private void requireResearcher(long id) throws SQLException {
        if (researcherDAO.getById(id) == null) {
            throw new IllegalArgumentException("Researcher not found");
        }
    }
}
