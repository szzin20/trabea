INSERT INTO users (work_email, password) VALUES
('aldi.danis@trabea.co.id',     '$2a$11$AEPkRqjVftmjTWPvRvmBieyhuRSk8wbmf47a8xr6tplfk7MjKQgJW'),
('anna.danis@trabea.co.id',     '$2a$11$AEPkRqjVftmjTWPvRvmBieyhuRSk8wbmf47a8xr6tplfk7MjKQgJW'),
('muh.danis@trabea.co.id',      '$2a$11$AEPkRqjVftmjTWPvRvmBieyhuRSk8wbmf47a8xr6tplfk7MjKQgJW'),
('peter.johnson@trabea.co.id',  '$2a$11$AEPkRqjVftmjTWPvRvmBieyhuRSk8wbmf47a8xr6tplfk7MjKQgJW'),
('patra.tobias@trabea.co.id',   '$2a$11$AEPkRqjVftmjTWPvRvmBieyhuRSk8wbmf47a8xr6tplfk7MjKQgJW'),
('alan.conway@trabea.co.id',    '$2a$11$AEPkRqjVftmjTWPvRvmBieyhuRSk8wbmf47a8xr6tplfk7MjKQgJW'),
('ali.jonathan@trabea.co.id',   '$2a$11$AEPkRqjVftmjTWPvRvmBieyhuRSk8wbmf47a8xr6tplfk7MjKQgJW'),
('robert.tan@trabea.co.id',     '$2a$11$AEPkRqjVftmjTWPvRvmBieyhuRSk8wbmf47a8xr6tplfk7MjKQgJW');

INSERT INTO roles (name) VALUES
('ADMIN'), ('MANAGER'), ('PART_TIME');

INSERT INTO user_roles (user_id, role_id) VALUES
('aldi.danis@trabea.co.id',     1),
('anna.danis@trabea.co.id',     1),
('aldi.danis@trabea.co.id',     2),
('muh.danis@trabea.co.id',      2),
('peter.johnson@trabea.co.id',  3),
('patra.tobias@trabea.co.id',   3),
('alan.conway@trabea.co.id',    3),
('ali.jonathan@trabea.co.id',   3),
('robert.tan@trabea.co.id',     3);

-- PostgreSQL tidak menggunakan SET IDENTITY_INSERT ON. Data id bisa dimasukkan secara eksplisit.
INSERT INTO employees (id, first_name, last_name, work_email) VALUES
(1,     'Aldi', 'Danis',    'aldi.danis@trabea.co.id'),
(2,     'Anna', 'Danis',    'anna.danis@trabea.co.id'),
(3,     'Muh',  'Danis',    'muh.danis@trabea.co.id');

-- Menyelaraskan sequence serial agar auto-increment bekerja normal untuk insert berikutnya
SELECT setval('employees_id_seq', (SELECT MAX(id) FROM employees));

-- INSERT INTO part_time_employees (id, first_name, last_name, personal_email,
--                                 personal_phone_number, last_education,
--                                 on_going_education, join_date, work_email) VALUES
-- (1,     'Peter',            'Johnson',      'peter.johnson@gmail.com',
--         '1234567821',       'Master',       'Doctorate',    CURRENT_TIMESTAMP - INTERVAL '1 year',     'peter.johnson@trabea.co.id'),
-- (2,     'Patra',            'Tobias',       'patra.tobias@hotmail.com',
--         '8761231677',       'HighSchool',   'Bachelor',     CURRENT_TIMESTAMP - INTERVAL '2 years',    'patra.tobias@trabea.co.id'),
-- (3,     'Alan',             'Conway',       'alan.conway@gmail.com',
--         '44117123151',      'Bachelor',     NULL,           CURRENT_TIMESTAMP - INTERVAL '5 months',   'alan.conway@trabea.co.id'),
-- (4,     'Ali',              'Jonathan',     'ali.jonathan@yahoo.com',
--         '92132561725',      'Bachelor',     'Master',       CURRENT_TIMESTAMP - INTERVAL '2 months',   'ali.jonathan@trabea.co.id'),
-- (5,     'Robert',           'Tan',          'robert.tan@gmail.co.id',
--         '6281249812525',    'Bachelor',     NULL,           CURRENT_TIMESTAMP - INTERVAL '2 months',   'robert.tan@trabea.co.id');
--
-- SELECT setval('part_time_employees_id_seq', (SELECT MAX(id) FROM part_time_employees));
--
-- INSERT INTO work_shifts (id, start_time, end_time) VALUES
-- (1,     '08:00',    '12:00'),
-- (2,     '12:00',    '16:00'),
-- (3,     '16:00',    '20:00'),
-- (4,     '20:00',    '00:00')
-- ON CONFLICT (id) DO UPDATE
-- SET start_time = EXCLUDED.start_time,
--     end_time = EXCLUDED.end_time;
--
-- -- contoh telah di approve dan telah di reject
-- INSERT INTO work_schedules (work_date, work_shift_id,
--     manager_id, part_time_employee_id, is_approved) VALUES
-- (CURRENT_DATE,                      1, 1, 1, TRUE),
-- (CURRENT_DATE,                      2, 1, 1, TRUE),
-- (CURRENT_DATE,                      3, 1, 2, TRUE),
-- (CURRENT_DATE,                      4, 1, 2, TRUE),
-- (CURRENT_DATE + INTERVAL '1 day',   3, 1, 2, TRUE),
-- (CURRENT_DATE + INTERVAL '1 day',   4, 1, 2, TRUE),
-- (CURRENT_DATE + INTERVAL '4 days',  1, 3, 3, TRUE),
-- (CURRENT_DATE + INTERVAL '4 days',  1, 3, 4, TRUE),
-- (CURRENT_DATE + INTERVAL '4 days',  2, 3, 4, TRUE),
-- (CURRENT_DATE + INTERVAL '4 days',  2, 3, 5, FALSE),    -- disapproved
-- (CURRENT_DATE + INTERVAL '4 days',  3, 3, 5, TRUE),
-- (CURRENT_DATE + INTERVAL '4 days',  4, 3, 5, TRUE);
--
-- -- contoh kasus yang belum di cek sama manager:
-- INSERT INTO work_schedules
-- (work_date, work_shift_id, part_time_employee_id) VALUES
-- (CURRENT_DATE + INTERVAL '7 days',      1,      1),
-- (CURRENT_DATE + INTERVAL '7 days',      2,      1),
-- (CURRENT_DATE + INTERVAL '7 days',      1,      2),
-- (CURRENT_DATE + INTERVAL '7 days',      2,      2),
-- (CURRENT_DATE + INTERVAL '7 days',      3,      2),
-- (CURRENT_DATE + INTERVAL '7 days',      3,      3),
-- (CURRENT_DATE + INTERVAL '7 days',      4,      3),
-- (CURRENT_DATE + INTERVAL '9 days',      1,      1),
-- (CURRENT_DATE + INTERVAL '9 days',      2,      1),
-- (CURRENT_DATE + INTERVAL '9 days',      1,      2),
-- (CURRENT_DATE + INTERVAL '9 days',      2,      2),
-- (CURRENT_DATE + INTERVAL '9 days',      3,      2),
-- (CURRENT_DATE + INTERVAL '9 days',      3,      3),
-- (CURRENT_DATE + INTERVAL '9 days',      4,      3);
