
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
            throw new IllegalArgumentException("Некоректний ID запису");
        }

        return dao.getById(id);
    }

    public void add(UsageRecord record) throws SQLException {
        validate(record);

        dao.add(record);

        LOG.info("Додано запис використання обладнання");
    }

    public void update(UsageRecord record) throws SQLException {
        if (record == null || record.getId() == null
                || record.getId() <= 0) {
            throw new IllegalArgumentException(
                    "Некоректний ID запису для оновлення"
            );
        }

        validate(record);

        dao.update(record);

        LOG.info("Оновлено запис використання обладнання");
    }

    public void delete(long id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Некоректний ID запису");
        }

        dao.delete(id);

        LOG.info("Видалено запис використання обладнання");
    }

    private void validate(UsageRecord record) {
        if (record == null) {
            throw new IllegalArgumentException(
                    "Запис використання не може бути порожнім"
            );
        }

        if (record.getEquipmentId() <= 0) {
            throw new IllegalArgumentException(
                    "Необхідно вибрати обладнання"
            );
        }

        if (record.getBookingId() <= 0) {
            throw new IllegalArgumentException(
                    "Необхідно вибрати бронювання"
            );
        }

        if (record.getRecordDate() == null
                || record.getRecordDate().isBlank()) {
            throw new IllegalArgumentException(
                    "Необхідно вказати дату запису"
            );
        }

        if (!Double.isFinite(record.getV1())
                || !Double.isFinite(record.getV2())
                || !Double.isFinite(record.getV3())
                || !Double.isFinite(record.getUtilizationScore())) {
            throw new IllegalArgumentException(
                    "Показники повинні бути скінченними числами"
            );
        }
    }
}
