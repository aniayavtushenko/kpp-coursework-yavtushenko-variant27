package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

public class ResearcherServlet extends HttpServlet {

    private final ResearcherService service = new ResearcherService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                service.delete(id(req));
            } else {
                // Зчитуємо параметр fullName з можливими альтернативами, щоб уникнути помилки валідації
                String fullName = req.getParameter("fullName");
                if (fullName == null || fullName.isBlank()) {
                    fullName = req.getParameter("full_name");
                }
                if (fullName == null || fullName.isBlank()) {
                    fullName = req.getParameter("name");
                }

                // Формуємо об'єкт Researcher
                Researcher value = new Researcher(
                        "update".equals(action) ? id(req) : null,
                        fullName,
                        req.getParameter("department"),
                        req.getParameter("email")
                );

                if ("add".equals(action)) {
                    service.add(value);
                } else if ("update".equals(action)) {
                    service.update(value);
                } else {
                    throw new IllegalArgumentException("Unknown operation");
                }
            }

            writeJson(resp, service.getAll());

        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {

        if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        writeJson(resp, Map.of(
                "error",
                e.getMessage() == null ? "Error" : e.getMessage()
        ));
    }

    private long id(HttpServletRequest req) {
        return Long.parseLong(req.getParameter("id"));
    }
}