
package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

public class UsageRecordServlet extends HttpServlet {

    private final UsageRecordService service = new UsageRecordService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");

        try {
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                service.delete(id(req));

            } else if ("add".equals(action) || "update".equals(action)) {

                String eqIdStr = getParam(req, "equipmentId", "equipment_id");
                String bookingIdStr = getParam(req, "bookingId", "booking_id");

                if (eqIdStr == null) {
                    throw new IllegalArgumentException(
                            "ID обладнання є обов'язковим"
                    );
                }

                if (bookingIdStr == null) {
                    throw new IllegalArgumentException(
                            "ID бронювання є обов'язковим"
                    );
                }

                long equipmentId = Long.parseLong(eqIdStr);
                long bookingId = Long.parseLong(bookingIdStr);

                String recordDate = getParam(
                        req, "recordDate", "record_date", "recordedDate"
                );

                if (recordDate == null) {
                    throw new IllegalArgumentException(
                            "Вкажіть дату запису"
                    );
                }

                double v1 = parseDouble(req.getParameter("v1"));
                double v2 = parseDouble(req.getParameter("v2"));
                double v3 = parseDouble(req.getParameter("v3"));

                String scoreStr = req.getParameter("utilizationScore");

                double score;
                if (scoreStr == null || scoreStr.isBlank()) {
                    score = (v1 + v2 + v3) / 3.0;
                } else {
                    score = parseDouble(scoreStr);
                }

                UsageRecord value = new UsageRecord(
                        "update".equals(action) ? id(req) : null,
                        equipmentId,
                        bookingId,
                        recordDate,
                        v1,
                        v2,
                        v3,
                        score
                );

                if ("add".equals(action)) {
                    service.add(value);
                } else {
                    service.update(value);
                }

            } else {
                throw new IllegalArgumentException(
                        "Невідома операція"
                );
            }

            writeJson(resp, service.getAll());

        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private String getParam(HttpServletRequest req, String... keys) {
        for (String key : keys) {
            String val = req.getParameter(key);

            if (val != null && !val.isBlank()) {
                return val.trim();
            }
        }

        return null;
    }

    private double parseDouble(String val) {
        if (val == null || val.isBlank()) {
            throw new IllegalArgumentException(
                    "Не всі числові показники заповнені"
            );
        }

        try {
            double result = Double.parseDouble(val.replace(',', '.'));

            if (!Double.isFinite(result)) {
                throw new IllegalArgumentException(
                        "Числове значення має бути скінченним"
                );
            }

            return result;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Некоректне числове значення: " + val
            );
        }
    }

    private void writeJson(HttpServletResponse resp,
                           Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp,
                            Exception e) throws IOException {

        if (e instanceof IllegalArgumentException
                || e instanceof IllegalStateException) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        writeJson(resp, Map.of(
                "error",
                e.getMessage() == null ? "Помилка" : e.getMessage()
        ));
    }

    private long id(HttpServletRequest req) {
        String value = req.getParameter("id");

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ID запису не вказаний");
        }

        return Long.parseLong(value);
    }
}
