DROP DATABASE IF EXISTS scientific_equipment;
CREATE DATABASE scientific_equipment
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE scientific_equipment;

CREATE TABLE equipment (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           inventory_code VARCHAR(40) NOT NULL,
                           name VARCHAR(160) NOT NULL,
                           category VARCHAR(120) NOT NULL,
                           utilization_percent DOUBLE NOT NULL DEFAULT 0
);

CREATE TABLE researcher (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            full_name VARCHAR(160) NOT NULL,
                            department VARCHAR(120) NOT NULL,
                            email VARCHAR(100) NOT NULL
);

CREATE TABLE booking (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         equipment_id BIGINT NOT NULL,
                         researcher_id BIGINT NOT NULL,
                         state VARCHAR(30) NOT NULL,
                         f1 DOUBLE NOT NULL,
                         f2 DOUBLE NOT NULL,
                         f3 DOUBLE NOT NULL,
                         risk_index DOUBLE NOT NULL,
                         deadline DATE NULL,
                         FOREIGN KEY (equipment_id) REFERENCES equipment(id),
                         FOREIGN KEY (researcher_id) REFERENCES researcher(id)
);

CREATE TABLE usage_record (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              equipment_id BIGINT NOT NULL,
                              booking_id BIGINT NOT NULL,
                              record_date VARCHAR(10) NOT NULL,
                              v1 DOUBLE NOT NULL,
                              v2 DOUBLE NOT NULL,
                              v3 DOUBLE NOT NULL,
                              utilization_score DOUBLE NOT NULL,
                              FOREIGN KEY (equipment_id) REFERENCES equipment(id),
                              FOREIGN KEY (booking_id) REFERENCES booking(id)
);

INSERT INTO equipment (
    inventory_code, name, category, utilization_percent
) VALUES
      ('EQ-001', 'Осцилограф цифровий', 'Вимірювальні прилади', 20),
      ('EQ-002', 'Мікроскоп електронний', 'Оптичні прилади', 60);

INSERT INTO researcher (
    full_name, department, email
) VALUES
      ('Іваненко І.І.', 'Кафедра прикладної фізики', 'ivanenko@example.com'),
      ('Петренко П.П.', 'Кафедра хімії', 'petrenko@example.com');

INSERT INTO booking (
    equipment_id, researcher_id, state, f1, f2, f3, risk_index, deadline
) VALUES
      (1, 1, 'APPROVED', 100.0, 20.0, 70.0, 64.5, '2026-11-17'),
      (2, 1, 'CREATED',  100.0, 60.0, 80.0, 81.0, '2026-11-08');

INSERT INTO usage_record (
    equipment_id, booking_id, record_date, v1, v2, v3, utilization_score
) VALUES
    (1, 1, '2026-11-18', 60.0, 70.0, 80.0, 70.0);