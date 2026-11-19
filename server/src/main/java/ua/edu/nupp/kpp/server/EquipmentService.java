package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class EquipmentService {

    private static final Logger LOG =
            Logger.getLogger(EquipmentService.class.getName());

    private final EquipmentDAO dao = new EquipmentDAO();

    public List<Equipment> getAll() throws SQLException {
        return dao.getAll();
    }

    public Equipment getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Equipment equipment) throws SQLException {
        validate(equipment);
        dao.add(equipment);
        LOG.info("Equipment added");
    }

    public void update(Equipment equipment) throws SQLException {
        validate(equipment);
        dao.update(equipment);
        LOG.info("Equipment updated");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Equipment deleted");
    }

    private void validate(Equipment equipment) {
        if (equipment == null
                || blank(equipment.getInventoryCode())
                || blank(equipment.getName())
                || blank(equipment.getCategory())) {
            throw new IllegalArgumentException(
                    "All equipment fields are required"
            );
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}