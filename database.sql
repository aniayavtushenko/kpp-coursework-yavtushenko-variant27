DROP DATABASE IF EXISTS scientific_equipment;
CREATE DATABASE scientific_equipment;
USE scientific_equipment;

-- Таблиця обладнання (на неї посилаються)
CREATE TABLE equipment (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           inventory_code VARCHAR(40) NOT NULL,
                           name VARCHAR(160) NOT NULL,
                           category VARCHAR(120) NOT NULL
);

-- Таблиця дослідників (на неї посилаються)
CREATE TABLE researcher (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            full_name VARCHAR(160) NOT NULL,
                            department VARCHAR(120) NOT NULL,
                            email VARCHAR(100) NOT NULL
);

-- Таблиця бронювань (містить зовнішні ключі)
CREATE TABLE booking (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         equipment_id BIGINT NOT NULL,
                         researcher_id BIGINT NOT NULL,
                         state VARCHAR(30) NOT NULL,
                         f1 DOUBLE NOT NULL,
                         f2 DOUBLE NOT NULL,
                         f3 DOUBLE NOT NULL,
                         risk_index DOUBLE NOT NULL,
                         FOREIGN KEY (equipment_id) REFERENCES equipment(id),
                         FOREIGN KEY (researcher_id) REFERENCES researcher(id)
);

-- Таблиця записів використання (містить зовнішній ключ на booking)
CREATE TABLE usage_record (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              booking_id BIGINT NOT NULL,
                              description VARCHAR(500) NOT NULL,
                              recorded_date VARCHAR(10),
                              FOREIGN KEY (booking_id) REFERENCES booking(id)
);

-- Початкові демонстраційні дані
INSERT INTO equipment (inventory_code, name, category) VALUES
                                                           ('EQ-001', 'Осцилограф цифровий', 'Вимірювальні прилади'),
                                                           ('EQ-002', 'Мікроскоп електронний', 'Оптичні прилади');

INSERT INTO researcher (full_name, department, email) VALUES
                                                          ('Іваненко І.І.', 'Кафедра прикладної фізики', 'ivanenko@example.com'),
                                                          ('Петренко П.П.', 'Кафедра хімії', 'petrenko@example.com');