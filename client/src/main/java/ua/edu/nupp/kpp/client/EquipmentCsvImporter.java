package ua.edu.nupp.kpp.client;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

public class EquipmentCsvImporter {

    private static final Logger LOG =
            Logger.getLogger(EquipmentCsvImporter.class.getName());

    private final Path file;
    private final HttpEquipmentClient api;

    public EquipmentCsvImporter(Path file, HttpEquipmentClient api) {
        this.file = file;
        this.api = api;
    }

    public int importFile() throws Exception {

        if (!Files.exists(file)) {
            throw new IllegalArgumentException(
                    "CSV file not found: " + file.toAbsolutePath()
            );
        }

        Set<String> existingCodes = new HashSet<>();

        for (Equipment equipment : api.getEquipments()) {
            if (equipment.getInventoryCode() != null) {
                existingCodes.add(
                        equipment.getInventoryCode()
                                .trim()
                                .toLowerCase(Locale.ROOT)
                );
            }
        }

        int imported = 0;
        int skipped = 0;
        int lineNumber = 0;
        boolean headerRead = false;

        try (BufferedReader reader = Files.newBufferedReader(
                file,
                StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                if (!headerRead) {
                    if (!line.trim().equalsIgnoreCase(
                            "inventory_code,name,category")) {
                        throw new IllegalArgumentException(
                                "Invalid CSV header. Expected: inventory_code,name,category"
                        );
                    }

                    headerRead = true;
                    continue;
                }

                String[] parts = line.split(",", -1);

                if (parts.length != 3) {
                    LOG.warning(
                            "Invalid CSV row number " + lineNumber
                    );
                    skipped++;
                    continue;
                }

                String inventoryCode = parts[0].trim();
                String name = parts[1].trim();
                String category = parts[2].trim();

                if (inventoryCode.isEmpty()
                        || name.isEmpty()
                        || category.isEmpty()) {
                    LOG.warning(
                            "Empty fields in CSV row number " + lineNumber
                    );
                    skipped++;
                    continue;
                }

                String normalizedCode =
                        inventoryCode.toLowerCase(Locale.ROOT);

                if (existingCodes.contains(normalizedCode)) {
                    LOG.info(
                            "Duplicate skipped: " + inventoryCode
                    );
                    skipped++;
                    continue;
                }

                Equipment equipment = new Equipment();

                equipment.setInventoryCode(inventoryCode);
                equipment.setName(name);
                equipment.setCategory(category);
                equipment.setUtilizationPercent(0.0);

                api.addEquipment(equipment);

                existingCodes.add(normalizedCode);
                imported++;
            }
        }

        if (!headerRead) {
            throw new IllegalArgumentException(
                    "CSV file is empty or does not contain a header."
            );
        }

        LOG.info(
                "Import completed. Added: " + imported
                        + ", skipped: " + skipped
        );

        return imported;
    }
}