-- ============================================================
--   Test table to create tables with all data type fields
-- support by SQLite to test the loading of TableTabPanel
-- summary table, (JTable), TableViewForm, and TableEntryForm. 
-- =============================================================
-- Version 07/25/2006 Orignal Load Test SQL configuration for
--                    Tables of SQLite data types.
--         06/26/2018 Added Tables datestring, timestring, datetimestring,
--                    and timestampstring.
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

CREATE TABLE datestring_types (

--  Table id and creation data entries.
    data_type_id INTEGER PRIMARY KEY,
    date_type DATE
);

CREATE TABLE timestring_types (

--  Table id and creation data entries.
    data_type_id INTEGER PRIMARY KEY,
    time_type TIME
);

CREATE TABLE datetimestring_types (

--  Table id and creation data entries.
    data_type_id INTEGER PRIMARY KEY,
    datetime_type DATETIME
);

CREATE TABLE timestampstring_types (

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

INSERT INTO datestring_types (date_type) VALUES ('2018-01-01');

INSERT INTO timestring_types (time_type) VALUES ('12:01:01');

INSERT INTO datetimestring_types (datetime_type) VALUES ('2018-01-01 12:01:01');

INSERT INTO timestampstring_types (timestamp_type) VALUES ('2018-01-01 12:01:01');