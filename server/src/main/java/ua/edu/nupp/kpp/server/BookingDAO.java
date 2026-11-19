package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // Додавання бронювання
    public void add(Booking booking) throws SQLException {

        String sql = "INSERT INTO booking " +
                "(equipment_id, researcher_id, state, deadline, f1, f2, f3, risk_index) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, booking.getEquipmentId());
            statement.setLong(2, booking.getResearcherId());
            statement.setString(3, booking.getState().name());
            statement.setString(4, booking.getDeadline()); // Змінено на setString для тексту
            statement.setDouble(5, booking.getF1());
            statement.setDouble(6, booking.getF2());
            statement.setDouble(7, booking.getF3());
            statement.setDouble(8, booking.getRiskIndex());

            statement.executeUpdate();
        }
    }

    // Отримання всіх бронювань
    public List<Booking> getAll() throws SQLException {

        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking ORDER BY id";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                bookings.add(mapResultSet(resultSet));
            }
        }

        return bookings;
    }

    // Отримання бронювання за ID
    public Booking getById(long id) throws SQLException {

        String sql = "SELECT * FROM booking WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSet(resultSet);
                }
            }
        }

        return null;
    }

    // Оновлення бронювання
    public void update(Booking booking) throws SQLException {

        String sql = "UPDATE booking SET " +
                "equipment_id = ?, researcher_id = ?, state = ?, deadline = ?, " +
                "f1 = ?, f2 = ?, f3 = ?, risk_index = ? " +
                "WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, booking.getEquipmentId());
            statement.setLong(2, booking.getResearcherId());
            statement.setString(3, booking.getState().name());
            statement.setString(4, booking.getDeadline()); // Змінено на setString для тексту
            statement.setDouble(5, booking.getF1());
            statement.setDouble(6, booking.getF2());
            statement.setDouble(7, booking.getF3());
            statement.setDouble(8, booking.getRiskIndex());
            statement.setLong(9, booking.getId());

            statement.executeUpdate();
        }
    }

    // Зміна стану бронювання
    public void updateState(long id, BookingState state) throws SQLException {

        String sql = "UPDATE booking SET state = ? WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, state.name());
            statement.setLong(2, id);

            statement.executeUpdate();
        }
    }

    // Видалення бронювання
    public void delete(long id) throws SQLException {

        String sql = "DELETE FROM booking WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Перетворення результату SQL-запиту в об'єкт Booking
    private Booking mapResultSet(ResultSet resultSet) throws SQLException {
        return new Booking(
                resultSet.getLong("id"),
                resultSet.getLong("equipment_id"),
                resultSet.getLong("researcher_id"),
                BookingState.valueOf(resultSet.getString("state")),
                resultSet.getString("deadline"), // Змінено на getString для тексту
                resultSet.getDouble("f1"),
                resultSet.getDouble("f2"),
                resultSet.getDouble("f3"),
                resultSet.getDouble("risk_index")
        );
    }
}