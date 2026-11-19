
package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger LOG =
            Logger.getLogger(MainApp.class.getName());

    private final HttpEquipmentClient api = new HttpEquipmentClient();

    private final TableView<Equipment> equipments = new TableView<>();
    private final TableView<Researcher> researchers = new TableView<>();
    private final TableView<Booking> bookings = new TableView<>();
    private final TableView<UsageRecord> usageRecords = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Обладнання", equipmentPane()),
                new Tab("Дослідники", researcherPane()),
                new Tab("Бронювання", bookingPane()),
                new Tab("Записи використання", usageRecordPane())
        );

        tabs.getTabs().forEach(t -> t.setClosable(false));

        refreshAll();

        Scene scene = new Scene(tabs, 1100, 650);
        stage.setTitle("Система бронювання наукового обладнання");
        stage.setScene(scene);
        stage.show();
    }

    // Початкове читання CSV-файлу без використання Task
    private Equipment readFirstEquipment(Path file) throws IOException {

        // Читаємо CSV-файл у кодуванні UTF-8
        List<String> lines =
                Files.readAllLines(file, StandardCharsets.UTF_8);

        // Перевіряємо наявність рядків даних
        if (lines.size() < 2) {
            throw new IllegalArgumentException(
                    "CSV file contains no data rows"
            );
        }

        // Пропускаємо заголовок і беремо перший рядок даних
        String line = lines.get(1);

        // Розділяємо рядок за комою
        String[] parts = line.split(",", -1);

        // Перевіряємо кількість стовпців
        if (parts.length != 3) {
            LOG.warning(
                    "CSV row skipped due to an incorrect number of columns: "
                            + line
            );
            return null;
        }

        // Створюємо об'єкт обладнання
        Equipment equipment = new Equipment();

        equipment.setInventoryCode(parts[0].trim());
        equipment.setName(parts[1].trim());
        equipment.setCategory(parts[2].trim());
        equipment.setUtilizationPercent(0.0);

        return equipment;
    }

    private Pane equipmentPane() {
        equipments.getColumns().setAll(
                col("ID", "id"),
                col("Інв. номер", "inventoryCode"),
                col("Назва", "name"),
                col("Категорія", "category"),
                col("Відсоток вик.", "utilizationPercent")
        );

        TextField invCode = new TextField();
        TextField name = new TextField();
        TextField category = new TextField();
        TextField percent = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");
        Button importCsv = new Button("Імпортувати CSV");

        add.setOnAction(e -> safe(() -> {
            Equipment eq = new Equipment();
            eq.setInventoryCode(invCode.getText());
            eq.setName(name.getText());
            eq.setCategory(category.getText());
            eq.setUtilizationPercent(
                    Double.parseDouble(percent.getText())
            );

            api.addEquipment(eq);
            refreshEquipments();
        }));

        update.setOnAction(e -> safe(() -> {
            Equipment eq = selected(equipments);
            eq.setInventoryCode(invCode.getText());
            eq.setName(name.getText());
            eq.setCategory(category.getText());
            eq.setUtilizationPercent(
                    Double.parseDouble(percent.getText())
            );

            api.updateEquipment(eq);
            refreshEquipments();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteEquipment(selected(equipments).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshEquipments());

        // Послідовний імпорт обладнання з CSV без Task і Thread
        importCsv.setOnAction(e -> {
            try {
                EquipmentCsvImporter importer =
                        new EquipmentCsvImporter(
                                Path.of("sample-data", "equipment.csv"),
                                api
                        );

                int imported = importer.importFile();

                refreshEquipments();

                new Alert(
                        Alert.AlertType.INFORMATION,
                        "Equipment imported: " + imported
                ).showAndWait();

            } catch (Exception ex) {
                LOG.warning(
                        "CSV import error: " + ex.getMessage()
                );
                showError(ex);
            }
        });

        equipments.getSelectionModel()
                .selectedItemProperty()
                .addListener((o, a, s) -> {
                    if (s != null) {
                        invCode.setText(s.getInventoryCode());
                        name.setText(s.getName());
                        category.setText(s.getCategory());
                        percent.setText(
                                String.valueOf(s.getUtilizationPercent())
                        );
                    }
                });

        return layout(equipments, new VBox(8,
                new Label("Інвентарний номер"), invCode,
                new Label("Назва"), name,
                new Label("Категорія"), category,
                new Label("Відсоток використання"), percent,
                new HBox(8, add, update, delete, refresh, importCsv)
        ));
    }

    private Pane researcherPane() {
        researchers.getColumns().setAll(
                col("ID", "id"),
                col("ПІБ", "name"),
                col("Кафедра", "department"),
                col("Email", "email")
        );

        TextField name = new TextField();
        TextField department = new TextField();
        TextField email = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            Researcher r = new Researcher();
            r.setName(name.getText());
            r.setDepartment(department.getText());
            r.setEmail(email.getText());

            api.addResearcher(r);
            refreshResearchers();
        }));

        update.setOnAction(e -> safe(() -> {
            Researcher r = selected(researchers);
            r.setName(name.getText());
            r.setDepartment(department.getText());
            r.setEmail(email.getText());

            api.updateResearcher(r);
            refreshResearchers();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteResearcher(selected(researchers).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshResearchers());

        researchers.getSelectionModel()
                .selectedItemProperty()
                .addListener((o, a, r) -> {
                    if (r != null) {
                        name.setText(r.getName());
                        department.setText(r.getDepartment());
                        email.setText(r.getEmail());
                    }
                });

        return layout(researchers, new VBox(8,
                new Label("ПІБ"), name,
                new Label("Кафедра"), department,
                new Label("Email"), email,
                new HBox(8, add, update, delete, refresh)
        ));
    }

    private Pane bookingPane() {
        bookings.getColumns().setAll(
                col("ID", "id"),
                col("Обладнання", "equipmentId"),
                col("Дослідник", "researcherId"),
                col("Дедлайн", "deadline"),
                col("Стан", "state"),
                col("F1", "f1"),
                col("F2", "f2"),
                col("F3", "f3"),
                col("I", "riskIndex")
        );

        TextField eqId = new TextField();
        TextField resId = new TextField();
        TextField deadline = new TextField();
        TextField f3 = new TextField();

        ComboBox<BookingState> stateBox =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                BookingState.values()
                        )
                );

        Button add = new Button("Створити");
        Button update = new Button("Змінити");
        Button changeState = new Button("Змінити стан");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            Booking b = new Booking();
            b.setEquipmentId(Long.parseLong(eqId.getText()));
            b.setResearcherId(Long.parseLong(resId.getText()));
            b.setDeadline(deadline.getText());
            b.setF3(Double.parseDouble(f3.getText()));

            api.addBooking(b);
            refreshBookings();
        }));

        update.setOnAction(e -> safe(() -> {
            Booking b = selected(bookings);
            b.setEquipmentId(Long.parseLong(eqId.getText()));
            b.setResearcherId(Long.parseLong(resId.getText()));
            b.setDeadline(deadline.getText());
            b.setF3(Double.parseDouble(f3.getText()));

            api.updateBooking(b);
            refreshBookings();
        }));

        changeState.setOnAction(e -> safe(() -> {
            api.changeBookingState(
                    selected(bookings).getId(),
                    stateBox.getValue()
            );

            refreshBookings();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteBooking(selected(bookings).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshBookings());

        bookings.getSelectionModel()
                .selectedItemProperty()
                .addListener((o, a, b) -> {
                    if (b != null) {
                        eqId.setText(String.valueOf(b.getEquipmentId()));
                        resId.setText(String.valueOf(b.getResearcherId()));
                        deadline.setText(b.getDeadline());
                        f3.setText(String.valueOf(b.getF3()));
                    }
                });

        return layout(bookings, new VBox(8,
                new Label("ID обладнання"), eqId,
                new Label("ID дослідника"), resId,
                new Label("Дедлайн (YYYY-MM-DD)"), deadline,
                new Label("F3 (Критичність 0..100)"), f3,
                new HBox(8, add, update, delete),
                new Separator(),
                new Label("Новий стан"), stateBox, changeState,
                new Separator(),
                refresh
        ));
    }

    private Pane usageRecordPane() {
        usageRecords.getColumns().setAll(
                col("ID", "id"),
                col("Обладнання", "equipmentId"),
                col("Бронювання", "bookingId"),
                col("Дата", "recordDate"),
                col("V1", "v1"),
                col("V2", "v2"),
                col("V3", "v3"),
                col("Оцінка", "utilizationScore")
        );

        TextField eqId = new TextField();
        TextField bookingId = new TextField();
        TextField date = new TextField();
        TextField v1 = new TextField();
        TextField v2 = new TextField();
        TextField v3 = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            UsageRecord u = new UsageRecord();

            u.setEquipmentId(Long.parseLong(eqId.getText().trim()));
            u.setBookingId(Long.parseLong(bookingId.getText().trim()));
            u.setRecordDate(date.getText().trim());
            u.setV1(Double.parseDouble(v1.getText().trim()));
            u.setV2(Double.parseDouble(v2.getText().trim()));
            u.setV3(Double.parseDouble(v3.getText().trim()));

            u.setUtilizationScore(
                    (u.getV1() + u.getV2() + u.getV3()) / 3.0
            );

            api.addUsageRecord(u);
            refreshUsageRecords();
        }));

        update.setOnAction(e -> safe(() -> {
            UsageRecord u = selected(usageRecords);

            u.setEquipmentId(Long.parseLong(eqId.getText().trim()));
            u.setBookingId(Long.parseLong(bookingId.getText().trim()));
            u.setRecordDate(date.getText().trim());
            u.setV1(Double.parseDouble(v1.getText().trim()));
            u.setV2(Double.parseDouble(v2.getText().trim()));
            u.setV3(Double.parseDouble(v3.getText().trim()));

            u.setUtilizationScore(
                    (u.getV1() + u.getV2() + u.getV3()) / 3.0
            );

            api.updateUsageRecord(u);
            refreshUsageRecords();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteUsageRecord(selected(usageRecords).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshUsageRecords());

        usageRecords.getSelectionModel()
                .selectedItemProperty()
                .addListener((o, oldValue, u) -> {
                    if (u != null) {
                        eqId.setText(String.valueOf(u.getEquipmentId()));
                        bookingId.setText(String.valueOf(u.getBookingId()));
                        date.setText(u.getRecordDate());
                        v1.setText(String.valueOf(u.getV1()));
                        v2.setText(String.valueOf(u.getV2()));
                        v3.setText(String.valueOf(u.getV3()));
                    }
                });

        return layout(usageRecords, new VBox(6,
                new Label("ID обладнання"), eqId,
                new Label("ID бронювання"), bookingId,
                new Label("Дата (YYYY-MM-DD)"), date,
                new Label("Показник V1"), v1,
                new Label("Показник V2"), v2,
                new Label("Показник V3"), v3,
                new HBox(8, add, update, delete, refresh)
        ));
    }

    private void refreshAll() {
        refreshEquipments();
        refreshResearchers();
        refreshBookings();
        refreshUsageRecords();
    }

    private void refreshEquipments() {
        safe(() -> equipments.setItems(
                FXCollections.observableArrayList(api.getEquipments())
        ));
    }

    private void refreshResearchers() {
        safe(() -> researchers.setItems(
                FXCollections.observableArrayList(api.getResearchers())
        ));
    }

    private void refreshBookings() {
        safe(() -> bookings.setItems(
                FXCollections.observableArrayList(api.getBookings())
        ));
    }

    private void refreshUsageRecords() {
        safe(() -> usageRecords.setItems(
                FXCollections.observableArrayList(api.getUsageRecords())
        ));
    }

    private <T> T selected(TableView<T> table) {
        T v = table.getSelectionModel().getSelectedItem();

        if (v == null) {
            throw new IllegalArgumentException(
                    "Select a record from the table"
            );
        }

        return v;
    }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }

    private void showError(Throwable e) {
        Alert d = new Alert(
                Alert.AlertType.ERROR,
                e == null ? "Error" : e.getMessage(),
                ButtonType.OK
        );

        d.setHeaderText("An error occurred");
        d.showAndWait();
    }

    private Pane layout(TableView<?> table, Pane form) {
        SplitPane p = new SplitPane(table, form);
        p.setDividerPositions(0.68);

        BorderPane root = new BorderPane(p);
        root.setPadding(new Insets(10));

        return root;
    }

    private <S, T> TableColumn<S, T> col(
            String title,
            String property
    ) {
        TableColumn<S, T> c = new TableColumn<>(title);

        c.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(property)
        );

        return c;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
