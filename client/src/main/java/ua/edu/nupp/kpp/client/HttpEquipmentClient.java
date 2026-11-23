package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpEquipmentClient {

    private static final String BASE = "http://localhost:8080/scientific-equipment-server";

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public HttpEquipmentClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String enc(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String post(String endpoint, Map<String, String> params) {
        StringBuilder body = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                body.append("&");
            }
            body.append(enc(entry.getKey()))
                    .append("=")
                    .append(enc(entry.getValue()));
            first = false;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + endpoint))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        return send(request);
    }

    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path)).GET().build();
        return send(req);
    }

    private String send(HttpRequest req) {
        HttpResponse<String> response;

        try {
            response = httpClient.send(
                    req,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
        } catch (java.io.IOException e) {
            String detail = e.getMessage();

            if (detail == null || detail.isBlank()) {
                detail = e.getClass().getSimpleName();
            }

            throw new RuntimeException(
                    "Cannot connect to server. Check that Tomcat is running. Details: "
                            + detail,
                    e
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "HTTP request was interrupted.",
                    e
            );
        }

        if (response.statusCode() >= 400) {
            throw new RuntimeException(
                    "Server error. HTTP status: " + response.statusCode()
                            + ". " + response.body()
            );
        }

        return response.body();
    }

    private <T> T read(String json, Class<T> type) {
        try {
            if (json == null || json.trim().isEmpty()) {
                json = "[]";
            }
            return mapper.readValue(json, type);
        } catch (Exception e) {
            System.err.println("INVALID JSON: " + json);
            e.printStackTrace();
            throw new RuntimeException("JSON parsing error. Check the console for details.", e);
        }
    }

    public List<Equipment> getEquipments() { return Arrays.asList(read(get("/equipment"), Equipment[].class)); }
    public List<Researcher> getResearchers() { return Arrays.asList(read(get("/researchers"), Researcher[].class)); }
    public List<Booking> getBookings() { return Arrays.asList(read(get("/bookings"), Booking[].class)); }
    public List<UsageRecord> getUsageRecords() { return Arrays.asList(read(get("/usage-records"), UsageRecord[].class)); }

    public void addEquipment(Equipment e) { post("/equipment", equipmentForm("add", e)); }
    public void updateEquipment(Equipment e) { post("/equipment", equipmentForm("update", e)); }
    public void deleteEquipment(long id) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "delete");
        map.put("id", String.valueOf(id));
        post("/equipment", map);
    }

    public void addResearcher(Researcher r) { post("/researchers", researcherForm("add", r)); }
    public void updateResearcher(Researcher r) { post("/researchers", researcherForm("update", r)); }
    public void deleteResearcher(long id) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "delete");
        map.put("id", String.valueOf(id));
        post("/researchers", map);
    }

    public void addBooking(Booking b) { post("/bookings", bookingForm("add", b)); }
    public void updateBooking(Booking b) { post("/bookings", bookingForm("update", b)); }
    public void changeBookingState(long id, BookingState state) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "transition");
        map.put("id", String.valueOf(id));
        map.put("state", state.name());
        post("/bookings", map);
    }
    public void deleteBooking(long id) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "delete");
        map.put("id", String.valueOf(id));
        post("/bookings", map);
    }

    public void addUsageRecord(UsageRecord u) { post("/usage-records", usageRecordForm("add", u)); }
    public void updateUsageRecord(UsageRecord u) { post("/usage-records", usageRecordForm("update", u)); }
    public void deleteUsageRecord(long id) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "delete");
        map.put("id", String.valueOf(id));
        post("/usage-records", map);
    }

    private Map<String, String> equipmentForm(String action, Equipment e) {
        Map<String, String> map = new HashMap<>();
        map.put("action", action);
        if ("update".equals(action)) map.put("id", String.valueOf(e.getId()));
        map.put("inventoryCode", e.getInventoryCode());
        map.put("name", e.getName());
        map.put("category", e.getCategory());
        map.put("utilizationPercent", String.valueOf(e.getUtilizationPercent()));
        return map;
    }

    private Map<String, String> researcherForm(String action, Researcher r) {
        Map<String, String> map = new HashMap<>();
        map.put("action", action);
        if ("update".equals(action)) map.put("id", String.valueOf(r.getId()));
        map.put("full_name", r.getName());
        map.put("department", r.getDepartment());
        map.put("email", r.getEmail());
        return map;
    }

    private Map<String, String> bookingForm(String action, Booking b) {
        Map<String, String> map = new HashMap<>();
        map.put("action", action);
        if ("update".equals(action)) map.put("id", String.valueOf(b.getId()));

        map.put("equipmentId", String.valueOf(b.getEquipmentId()));
        map.put("researcherId", String.valueOf(b.getResearcherId()));
        map.put("deadline", b.getDeadline());
        map.put("f3", String.valueOf(b.getF3()));

        return map;
    }


    private Map<String, String> usageRecordForm(String action, UsageRecord u) {
        Map<String, String> map = new HashMap<>();

        map.put("action", action);

        if ("update".equals(action)) {
            map.put("id", String.valueOf(u.getId()));
        }

        map.put("equipmentId", String.valueOf(u.getEquipmentId()));
        map.put("bookingId", String.valueOf(u.getBookingId()));
        map.put("recordDate", u.getRecordDate());
        map.put("v1", String.valueOf(u.getV1()));
        map.put("v2", String.valueOf(u.getV2()));
        map.put("v3", String.valueOf(u.getV3()));
        map.put("utilizationScore", String.valueOf(u.getUtilizationScore()));

        return map;
    }
}