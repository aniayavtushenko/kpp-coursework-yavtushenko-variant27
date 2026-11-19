
package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

public class BookingServlet extends HttpServlet {

    private final BookingService service = new BookingService();

    // За методичкою ObjectMapper створюється просто, без додаткових модулів
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                service.delete(id(req));
            } else if ("transition".equals(action)) {
                service.changeState(
                        id(req),
                        BookingState.valueOf(req.getParameter("state"))
                );
            } else {
                // Витягуємо параметри з HTTP-запиту (дата тепер просто String)
                long equipmentId = Long.parseLong(req.getParameter("equipmentId"));
                long researcherId = Long.parseLong(req.getParameter("researcherId"));
                String deadline = req.getParameter("deadline");
                double f3 = Double.parseDouble(req.getParameter("f3"));

                if ("add".equals(action)) {
                    service.add(equipmentId, researcherId, deadline, f3);
                } else if ("update".equals(action)) {
                    service.update(id(req), equipmentId, researcherId, deadline, f3);
                } else {
                    throw new IllegalArgumentException("Unknown operation");
                }
            }

            writeJson(resp, service.getAll());

        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private void writeJson(HttpServletResponse resp, Object data)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e)
            throws IOException {
        if (e instanceof IllegalArgumentException
                || e instanceof IllegalStateException) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        writeJson(
                resp,
                Map.of("error", e.getMessage() == null
                        ? "Error"
                        : e.getMessage())
        );
    }

    private long id(HttpServletRequest req) {
        return Long.parseLong(req.getParameter("id"));
    }
}
