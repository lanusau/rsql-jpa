INSERT INTO manufacturer (id, name) VALUES (1, 'Ford');
INSERT INTO manufacturer (id, name) VALUES (2, 'Chevrolet');
INSERT INTO manufacturer (id, name) VALUES (3, 'Honda');
INSERT INTO manufacturer (id, name) VALUES (4, 'Tesla');

INSERT INTO model (id, manufacturer_id, year, name) VALUES (1, 1, 2020, 'Mustang');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (2, 1, 2021, 'Mustang');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (3, 1, 2020, 'F-150');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (4, 1, 2021, 'F-150');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (5, 2, 2020, 'Bolt');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (6, 2, 2021, 'Bolt');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (7, 2, 2020, 'Tahoe');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (8, 2, 2021, 'Tahoe');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (9, 3, 2020, 'Civic');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (10, 3, 2021, 'Civic');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (11, 4, 2021, 'Model S');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (12, 4, 2021, 'Model 3');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (13, 4, 2021, 'Model X');
INSERT INTO model (id, manufacturer_id, year, name) VALUES (15, 4, 2021, 'Model Y');

INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (1, 1, 'WHITE', '2020-01-11 00:00:00', 0, '2020-03-02 00:00:00');
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (2, 2, 'BLACK', '2021-03-12 00:00:00', 0, null);
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (3, 3, 'SILVER', '2020-01-13 00:00:00', 0, '2020-03-02 00:00:00');
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (4, 3, 'BLACK', '2020-01-14 00:00:00', 0, '2020-03-03 00:00:00');
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (5, 4, 'SILVER', '2020-01-13 00:00:00', 0, null);
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (6, 5, 'BLUE', '2020-03-21 00:00:00', 1, '2020-05-02 00:00:00');
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (7, 6, 'SILVER', '2020-01-13 00:00:00', 1, null);
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (8, 9, 'SILVER', '2020-01-13 00:00:00', 0, '2020-03-02 00:00:00');
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (9, 11, 'WHITE', '2021-01-13 00:00:00', 1, '2021-10-02 00:00:00');
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (10, 11, 'WHITE', '2021-01-13 00:00:00', 1, '2021-10-02 00:00:00');
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (11, 11, 'RED', '2021-01-14 00:00:00', 1, '2021-10-03 00:00:00');
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (12, 12, 'WHITE', '2021-05-13 00:00:00', 1, '2021-06-02 00:00:00');
INSERT INTO car (id, model_id, color, manufacture_date, electric, date_sold)
VALUES (13, 12, 'BLACK', '2021-05-13 00:00:00', 1, null);
