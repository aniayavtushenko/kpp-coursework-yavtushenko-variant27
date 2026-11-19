package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class UsageRecordService {

    private static final Logger LOG =
            Logger.getLogger(UsageRecordService.class.getName());

    private final UsageRecordDAO dao = new UsageRecordDAO();

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