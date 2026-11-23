package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class UsageRecordService {

    private static final Logger LOG =
            Logger.getLogger(UsageRecordService.class.getName());

    private final UsageRecordDAO dao = new UsageRecordDAO();
    private final EquipmentDAO equipmentDAO = new EquipmentDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    public List<UsageRecord> getAll() throws SQLException {
        return dao.getAll();
    }

    public UsageRecord getById(long id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid record ID");
        }

        return dao.getById(id);
    }

    public void add(UsageRecord record) throws SQLException {
        validate(record);
        ensureReferencesExist(record);

        dao.add(record);

        LOG.info("Equipment usage record added");
    }

    public void update(UsageRecord record) throws SQLException {
        if (record == null || record.getId() == null
                || record.getId() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid record ID for update"
            );
        }

        validate(record);
        ensureReferencesExist(record);

        dao.update(record);

        LOG.info("Equipment usage record updated");
    }

    public void delete(long id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid record ID");
        }

        dao.delete(id);

        LOG.info("Equipment usage record deleted");
    }

    private void ensureReferencesExist(UsageRecord record) throws SQLException {
        if (equipmentDAO.getById(record.getEquipmentId()) == null) {
            LOG.warning("Equipment not found: id=" + record.getEquipmentId());
            throw new IllegalArgumentException(
                    "Обладнання з ID=" + record.getEquipmentId() + " не знайдено"
            );
        }

        if (bookingDAO.getById(record.getBookingId()) == null) {
            LOG.warning("Booking not found: id=" + record.getBookingId());
            throw new IllegalArgumentException(
                    "Бронювання з ID=" + record.getBookingId() + " не знайдено"
            );
        }
    }

    private void validate(UsageRecord record) {
        if (record == null) {
            throw new IllegalArgumentException(
                    "Usage record cannot be null"
            );
        }

        if (record.getEquipmentId() <= 0) {
            throw new IllegalArgumentException(
                    "Equipment must be selected"
            );
        }

        if (record.getBookingId() <= 0) {
            throw new IllegalArgumentException(
                    "Booking must be selected"
            );
        }

        if (record.getRecordDate() == null
                || record.getRecordDate().isBlank()) {
            throw new IllegalArgumentException(
                    "Record date is required"
            );
        }

        if (!Double.isFinite(record.getV1())
                || !Double.isFinite(record.getV2())
                || !Double.isFinite(record.getV3())
                || !Double.isFinite(record.getUtilizationScore())) {
            throw new IllegalArgumentException(
                    "Values must be finite numbers"
            );
        }
    }
}