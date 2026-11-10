package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class UsageRecordService {

    private static final Logger LOG =
            Logger.getLogger(UsageRecordService.class.getName());

    private final UsageRecordDAO dao = new UsageRecordDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    public List<UsageRecord> getAll() throws SQLException {
        return dao.getAll();
    }

    public UsageRecord getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(UsageRecord record) throws SQLException {
        validate(record);
        requireBooking(record.getBookingId());
        dao.add(record);
        LOG.info("Додано запис використання обладнання");
    }

    public void update(UsageRecord record) throws SQLException {
        validate(record);
        requireBooking(record.getBookingId());
        dao.update(record);
        LOG.info("Оновлено запис використання обладнання");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено запис використання обладнання");
    }

    private void validate(UsageRecord record) {
        if (record == null
                || record.getBookingId() <= 0
                || blank(record.getDescription())
                || blank(record.getRecordedDate())) {
            throw new IllegalArgumentException(
                    "Некоректні дані запису використання обладнання"
            );
        }
    }

    private void requireBooking(long id) throws SQLException {
        if (bookingDAO.getById(id) == null) {
            throw new IllegalArgumentException("Бронювання не знайдено");
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}