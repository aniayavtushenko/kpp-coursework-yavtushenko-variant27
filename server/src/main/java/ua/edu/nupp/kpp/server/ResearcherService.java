package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ResearcherService {

    private static final Logger LOG =
            Logger.getLogger(ResearcherService.class.getName());

    private final ResearcherDAO dao = new ResearcherDAO();

    public List<Researcher> getAll() throws SQLException {
        return dao.getAll();
    }

    public Researcher getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Researcher researcher) throws SQLException {
        validate(researcher);
        dao.add(researcher);
        LOG.info("Додано дослідника");
    }

    public void update(Researcher researcher) throws SQLException {
        validate(researcher);
        dao.update(researcher);
        LOG.info("Оновлено дослідника");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено дослідника");
    }

    private void validate(Researcher researcher) {
        if (researcher == null
                || blank(researcher.getFullName())
                || blank(researcher.getDepartment())
                || blank(researcher.getEmail())) {
            throw new IllegalArgumentException(
                    "Усі поля дослідника є обов'язковими"
            );
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}