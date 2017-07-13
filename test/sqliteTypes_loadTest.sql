-- ============================================================
--   Test table to create tables with all data type fields
-- support by SQLite to test the loading of TableTabPanel
-- summary table, (JTable), TableViewForm, and TableEntryForm. 
-- =============================================================
-- Version 07/25/2006 Orignal Load Test SQL configuration for
--                    Tables of SQLite data types.
--         07/12/2017 Added Additional Testing Tables date_types, time_types,
--                    and timestamp_types.
--         07/13/2017 Updated to Correct, Have Table Creation and INSERT
--                    Statements. Currently SQLite-JDBC 3.19.3 is Broken
--                    Had to Comment the INSERTS for date_types & time_types.
--      
-- danap@dandymadeproductions.com
-- =============================================================

-- #############################################################
--
-- DROP TABLE SECTION
--
-- #############################################################

DROP TABLE IF EXISTS int_types;
DROP TABLE IF EXISTS real_types;
DROP TABLE IF EXISTS text_types;
DROP TABLE IF EXISTS blob_types;

DROP TABLE IF EXISTS date_types;
DROP TABLE IF EXISTS time_types;
DROP TABLE IF EXISTS timestamp_types;

-- #############################################################
-- 
-- CREATE TABLE SECTION
--
-- #############################################################


CREATE TABLE int_types (

--  Table id and creation data entries.
    data_type_id INTEGER PRIMARY KEY,
    int_type INTEGER
);

CREATE TABLE real_types (

--  Table id and creation data entries.
    data_type_id INTEGER PRIMARY KEY,
    real_type REAL
);

CREATE TABLE text_types (

--  Table id and creation data entries.
    data_type_id INTEGER PRIMARY KEY,
    text_type TEXT
);

CREATE TABLE blob_types (

--  Table id and creation data entries.
    data_type_id INTEGER PRIMARY KEY,
    blob_type BLOB
);

CREATE TABLE date_types (

--  Table id and creation data entries.
    data_type_id INTEGER PRIMARY KEY,
    date_type DATE
);

CREATE TABLE time_types (

--  Table id and creation data entries.
    data_type_id INTEGER PRIMARY KEY,
    time_type TIME
);

CREATE TABLE timestamp_types (

--  Table id and creation data entries.
    data_type_id INTEGER PRIMARY KEY,
    timestamp_type TIMESTAMP
);


-- #############################################################
--
-- INSERT INTO TABLE SECTION
--
-- #############################################################

INSERT INTO int_types (int_type) VALUES (-2147483648);
INSERT INTO int_types (int_type) VALUES (2147483647);
   
INSERT INTO real_types (real_type) VALUES (-1.1e-39);

INSERT INTO blob_types (blob_type) VALUES (x'0500');

INSERT INTO text_types (text_type) VALUES ('text1');

-- INSERT INTO date_types (date_type) VALUES ('1000-01-01');
-- INSERT INTO date_types (date_type) VALUES ('9999-12-31');

-- INSERT INTO time_types (time_type) VALUES ('00:00:00');
-- INSERT INTO time_types (time_type) VALUES ('23:59:59');

INSERT INTO timestamp_types (timestamp_type) VALUES (STRFTIME('%Y-%m-%d %H:%M:%S.%f', 'now', 'localtime'));
-- INSERT INTO timestamp_types (timestamp_type) VALUES ('1970-01-01 00:00:00');


