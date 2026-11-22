package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportEquipmentTask extends Task<Integer> {

    private static final Logger LOG =
            Logger.getLogger(ImportEquipmentTask.class.getName());

    private final Path file;
    private final HttpEquipmentClient api;

    public ImportEquipmentTask(Path file, HttpEquipmentClient api) {
        this.file = file;
        this.api = api;
    }

    @Override
    protected Integer call() throws Exception {
        LOG.info("Starting CSV import: " + file.getFileName());

        if (!Files.exists(file)) {
            throw new IllegalArgumentException(
                    "CSV file not found: " + file.getFileName()
            );
        }

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

        int headerIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                headerIndex = i;
                break;
            }
        }

        if (headerIndex == -1) {
            throw new IllegalArgumentException(
                    "CSV file is empty or does not contain a header."
            );
        }

        String header = lines.get(headerIndex).replace("\uFEFF", "").trim();
        if (!header.equalsIgnoreCase("inventory_code,name,category,utilization_percent")) {
            throw new IllegalArgumentException(
                    "Invalid CSV header. Expected: inventory_code,name,category,utilization_percent"
            );
        }

        Set<String> existingCodes = new HashSet<>();

        try {
            for (Equipment equipment : api.getEquipments()) {
                if (equipment.getInventoryCode() != null) {
                    existingCodes.add(
                            equipment.getInventoryCode()
                                    .trim()
                                    .toLowerCase(Locale.ROOT)
                    );
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "HTTP error while loading existing equipment list", e);
            throw e;
        }

        int total = 0;
        for (int i = headerIndex + 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                total++;
            }
        }

        int imported = 0;
        int skipped = 0;
        int processed = 0;

        if (total == 0) {
            updateProgress(0, 1);
            updateMessage("No data rows to import.");
            LOG.info("Import completed. No data rows found.");
            return 0;
        }

        for (int i = headerIndex + 1; i < lines.size(); i++) {
            if (isCancelled()) {
                LOG.info("CSV import cancelled.");
                break;
            }

            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }

            int lineNumber = i + 1;
            processed++;

            String[] parts = line.split(",", -1);

            if (parts.length != 4) {
                LOG.warning("Invalid CSV row number " + lineNumber + ": expected 4 columns");
                skipped++;
                updateProgress(processed, total);
                updateMessage("Processed: " + processed + " of " + total);
                continue;
            }

            String inventoryCode = parts[0].trim();
            String name = parts[1].trim();
            String category = parts[2].trim();
            String utilizationText = parts[3].trim();

            if (inventoryCode.isEmpty()
                    || name.isEmpty()
                    || category.isEmpty()
                    || utilizationText.isEmpty()) {
                LOG.warning("Empty fields in CSV row number " + lineNumber);
                skipped++;
                updateProgress(processed, total);
                updateMessage("Processed: " + processed + " of " + total);
                continue;
            }

            double utilizationPercent;
            try {
                utilizationPercent = Double.parseDouble(utilizationText);
            } catch (NumberFormatException e) {
                LOG.warning("Invalid number in utilization_percent on CSV row "
                        + lineNumber + ": '" + utilizationText + "'");
                skipped++;
                updateProgress(processed, total);
                updateMessage("Processed: " + processed + " of " + total);
                continue;
            }

            if (utilizationPercent < 0.0 || utilizationPercent > 100.0) {
                LOG.warning("utilization_percent out of range [0..100] on CSV row "
                        + lineNumber + ": " + utilizationPercent);
                skipped++;
                updateProgress(processed, total);
                updateMessage("Processed: " + processed + " of " + total);
                continue;
            }

            String normalizedCode =
                    inventoryCode.toLowerCase(Locale.ROOT);

            if (existingCodes.contains(normalizedCode)) {
                LOG.info("Duplicate skipped: " + inventoryCode);
                skipped++;
                updateProgress(processed, total);
                updateMessage("Processed: " + processed + " of " + total);
                continue;
            }

            Equipment equipment = new Equipment();
            equipment.setInventoryCode(inventoryCode);
            equipment.setName(name);
            equipment.setCategory(category);
            equipment.setUtilizationPercent(utilizationPercent);

            try {
                api.addEquipment(equipment);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "HTTP error on CSV row " + lineNumber, e);
                throw e;
            }

            existingCodes.add(normalizedCode);
            imported++;

            updateProgress(processed, total);
            updateMessage(
                    "Imported: " + imported + ", skipped: " + skipped
            );
        }

        LOG.info(
                "Import completed. Added: " + imported
                        + ", skipped: " + skipped
        );

        return imported;
    }
}