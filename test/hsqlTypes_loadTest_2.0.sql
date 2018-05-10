-- ============================================================
--   Test table to create tables with all data type fields
-- support by HSQL to test the loading of TableTabPanel
-- summary table, (JTable), TableViewForm, and TableEntryForm. 
-- =============================================================
-- Version 01/28/2008 Orignal Load Test SQL configuration for
--                    Tables of HSQL data types.
--         01/31/2008 Completed Most, But INSERTS Need Work.
--         02/01/2008 Explicit INSERT Statements.
--         07/21/2010 Updated Contact, Email, Address.
--         06/23/2011 Modified to be used with HSQL2.X. Binary,
--                    & varBinary Fields size specification.
--                    Placed semicolon at the end of each create
--                    table statement.
--         07/23/2011 Added Clob, Blob, Bit Varying, Time With Time
--                    Zone, Timestamp With Time Zone, & Interval.
--         05/10/2018 Added Test Table bit2_types. Test Table bit_types,
--                    Insert Changed to Use B'x' Value Format. Test
--                    Table bitvarying_types Added Additional Insert
--                    Value.
--      
-- danap@dandymadeproductions.com
-- =============================================================

-- #############################################################
--
-- DROP TABLE SECTION
--
-- #############################################################

-- character types

DROP TABLE IF EXISTS char_types;
DROP TABLE IF EXISTS varchar_types;
DROP TABLE IF EXISTS longvarchar_types;
DROP TABLE IF EXISTS clob_types;

-- binary types

DROP TABLE IF EXISTS binary_types;
DROP TABLE IF EXISTS varBinary_types;
DROP TABLE IF EXISTS longvarBinary_types;
DROP TABLE IF EXISTS blob_types;

-- boolean & bit types

DROP TABLE IF EXISTS boolean_types;
DROP TABLE IF EXISTS bit_types;
DROP TABLE IF EXISTS bit2_types;
DROP TABLE IF EXISTS bitvarying_types;

-- integer types

DROP TABLE IF EXISTS tinyInt_types;
DROP TABLE IF EXISTS smallInt_types;
DROP TABLE IF EXISTS int_types;
DROP TABLE IF EXISTS bigInt_types;
DROP TABLE IF EXISTS decimal_types;
DROP TABLE IF EXISTS numeric_types;

-- float types

DROP TABLE IF EXISTS float_types;
DROP TABLE IF EXISTS double_types;
DROP TABLE IF EXISTS real_types;

-- date types

DROP TABLE IF EXISTS date_types;
DROP TABLE IF EXISTS time_types;
DROP TABLE IF EXISTS timeTZ_types;
DROP TABLE IF EXISTS dateTime_types;
DROP TABLE IF EXISTS timeStamp_types;
DROP TABLE IF EXISTS timeStampTZ_types;
DROP TABLE IF EXISTS interval_types;

-- #############################################################
-- 
-- CREATE TABLE SECTION
--
-- #############################################################

CREATE TABLE char_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    char_type CHAR(30) DEFAULT NULL
);

CREATE TABLE varchar_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    varchar_type VARCHAR(30) DEFAULT NULL
);

CREATE TABLE longvarchar_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    longvarchar_type LONGVARCHAR DEFAULT NULL
);

CREATE TABLE clob_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    clob_type CLOB DEFAULT NULL
);

CREATE TABLE binary_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    binary_type BINARY(200000) DEFAULT NULL
);

CREATE TABLE varBinary_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    varBinary_type VARBINARY(200000) DEFAULT NULL
);

CREATE TABLE longvarBinary_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    longvarBinary_type LONGVARBINARY DEFAULT NULL
);

CREATE TABLE blob_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    blob_type BLOB DEFAULT NULL
);

CREATE TABLE boolean_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    boolean_type BOOLEAN DEFAULT NULL
);

CREATE TABLE bit_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    bit_type BIT DEFAULT NULL
);

CREATE TABLE bit2_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    bit2_type BIT(2) DEFAULT NULL
);

CREATE TABLE bitvarying_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    bitvarying_type BIT VARYING(5) DEFAULT NULL
);

CREATE TABLE tinyInt_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    tinyInt_type TINYINT DEFAULT NULL
);

CREATE TABLE smallInt_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    smallInt_type SMALLINT DEFAULT NULL
);

CREATE TABLE int_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    int_type INT DEFAULT NULL
);

CREATE TABLE bigInt_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    bigInt_type BIGINT DEFAULT NULL
);

CREATE TABLE decimal_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    decimal_type DECIMAL(16,2) DEFAULT NULL
);

CREATE TABLE numeric_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    numeric_type NUMERIC DEFAULT NULL
);

CREATE TABLE float_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    float_type FLOAT DEFAULT NULL
);

CREATE TABLE double_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    double_type DOUBLE DEFAULT NULL
);

CREATE TABLE real_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    real_type REAL DEFAULT NULL
);

CREATE TABLE date_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    date_type DATE DEFAULT NULL
);

CREATE TABLE time_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    time_type TIME DEFAULT NULL
);

CREATE TABLE timeTZ_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    timeTZ_type TIME(6) WITH TIME ZONE DEFAULT NULL
);

CREATE TABLE dateTime_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    dateTime_type DATETIME DEFAULT NULL
);

CREATE TABLE timeStamp_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    timeStamp_type TIMESTAMP DEFAULT NULL
);

CREATE TABLE timeStampTZ_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    timeStampTZ_type TIMESTAMP(6) WITH TIME ZONE DEFAULT NULL
);

CREATE TABLE interval_types (

--  Table id and creation data entries.
    data_type_id IDENTITY PRIMARY KEY,
    interval_type INTERVAL DAY(3) TO HOUR DEFAULT NULL
);

-- #############################################################
--
-- INSERT INTO TABLE SECTION
--
-- #############################################################

INSERT INTO char_types (char_type)
	VALUES ('a');
INSERT INTO char_types (char_type)
	VALUES ('ccaaaaaaaaaaaaaaaaaaaaaaaaaaa');

INSERT INTO varchar_types (varchar_type)
	VALUES ('a');
INSERT INTO varchar_types (varchar_type)
    VALUES ('ccaaaaaaaaaaaaaaaaaaaaaaaaaaa');

INSERT INTO longvarchar_types (longvarchar_type)
	VALUES ('a');
INSERT INTO longvarchar_types (longvarchar_type)
    VALUES ('ccaaaaaaaaaaaaaaaaaaaaaaaaaaa');
    
INSERT INTO clob_types (clob_type)
	VALUES ('a');
INSERT INTO clob_types (clob_type)
    VALUES ('ccaaaaaaaaaaaaaaaaaaaaaaaaaaa');

INSERT INTO binary_types (binary_type)
	VALUES ('0x000a');

INSERT INTO varBinary_types (varBinary_type)
	VALUES ('0x000a');

INSERT INTO longVarBinary_types (longVarBinary_type)
	VALUES ('0x000a');
	
INSERT INTO blob_types (blob_type)
	VALUES (x'000a');

INSERT INTO boolean_types (boolean_type)
	VALUES (0);
INSERT INTO boolean_types (boolean_type)
	VALUES (1);

INSERT INTO bit_types (bit_type)
	VALUES (B'0');
INSERT INTO bit_types (bit_type)
    VAlUES (B'1');
    
INSERT INTO bit2_types (bit2_type)
	VALUES (B'10');
INSERT INTO bit2_types (bit2_type)
    VAlUES (B'11');
    
INSERT INTO bitvarying_types (bitvarying_type)
    VAlUES (B'10001');
INSERT INTO bitvarying_types (bitvarying_type)
    VAlUES (B'00001');

INSERT INTO tinyInt_types (tinyInt_type)
	VALUES (-128);
INSERT INTO tinyInt_types (tinyInt_type)
    VALUES (127);

INSERT INTO smallInt_types (smallInt_type)
	VALUES (-32768);
INSERT INTO smallInt_types (smallInt_type)
    VALUES (32767);

INSERT INTO int_types (int_type)
	VALUES (-2147483648);
INSERT INTO int_types (int_type)	
    VALUES (2147483647);

INSERT INTO bigInt_types (bigInt_type)
	VALUES (-9223372036854775808);
INSERT INTO bigInt_types (bigInt_type)	
    VAlUES (9223372036854775807);

INSERT INTO float_types (float_type)
	VALUES (-1.1e-39);

INSERT INTO double_types (double_type)
	VALUES (-2.2e-309);

INSERT INTO real_types (real_type)
	VALUES (-2.2e-309);

INSERT INTO decimal_types (decimal_type)
	VALUES (-99999999999999.99);
INSERT INTO decimal_types (decimal_type)
    VAlUES (99999999999999.99);

INSERT INTO numeric_types (numeric_type)
	VALUES (-99999999999999.99);
INSERT INTO numeric_types (numeric_type)
    VAlUES (99999999999999.99);

INSERT INTO date_types (date_type)
	VALUES ('1000-01-01');
INSERT INTO date_types (date_type)
	VALUES ('9999-12-31');

INSERT INTO time_types (time_type)
	VALUES ('00:00:00');
INSERT INTO time_types (time_type)
    VALUES ('23:59:59');
    
INSERT INTO timeTZ_types (timeTZ_type)
	VALUES ('00:00:00');
INSERT INTO timeTZ_types (timeTZ_type)
    VALUES ('23:59:59');

INSERT INTO dateTime_types (dateTime_type)
	VALUES ('1000-01-01 00:00:00');
INSERT INTO dateTime_types (dateTime_type)
    VALUES ('9999-12-31 23:59:59');

INSERT INTO timeStamp_types (timeStamp_type)
	VALUES ('1970-01-01 00:00:00');
INSERT INTO timeStamp_types (timeStamp_type)
    VALUES ('2005-01-11 09:43:01');

INSERT INTO timeStampTZ_types (timeStampTZ_type)
	VALUES (NOW());
	
INSERT INTO interval_types (interval_type)
	VALUES ('122 08' DAY TO HOUR);   