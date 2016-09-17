-- ============================================================
--   Test table to create tables with all data type fields
-- support by MySQL to test the loading of TableTabPanel
-- summary table, (JTable), TableViewForm, and TableEntryForm. 
-- =============================================================
-- Version 12/03/2006 Orignal Load Test SQL configuration for
--                    Tables of MySQL data types.
--         12/03/2006 Added Comments and Developer Info. 
--         12/04/2006 Changed Range on DATE and DATETIME Fields
--                    to 1000-01-01 00:00:00 9999-12-31 23:59:59
--         06/05/2007 Changed Range on TIMESTAMP Fields to NOT
--                    be 0000-00-00.
--         10/27/2007 Changed Comments from # to --.
--         10/27/2007 Changed Quotes to Single Quotes for Data.
--         10/29/2007 Changed Description.
--         10/31/2007 Added Tables boolean_types & bit_types.
--         07/21/2010 Updated Contact, Email, Address.
--         10/31/2014 Removed Timestamps Tables Above (4) & Replaced
--                    With Just New timeStamp Table. Removed TYPE = InnoDB
--                    Definition for Tables.
--      
-- danap@dandymadeproductions.com
-- gashogtoo@users.sourceforge.net
-- =============================================================

-- #############################################################
--
-- DROP TABLE SECTION
--
-- #############################################################

-- character types

DROP TABLE IF EXISTS char_types;
DROP TABLE IF EXISTS varchar_types;

-- blob types

DROP TABLE IF EXISTS tinyBlob_types;
DROP TABLE IF EXISTS blob_types;
DROP TABLE IF EXISTS mediumBlob_types;
DROP TABLE IF EXISTS longBlob_types;

-- text types

DROP TABLE IF EXISTS tinyText_types;
DROP TABLE IF EXISTS text_types;
DROP TABLE IF EXISTS mediumText_types;
DROP TABLE IF EXISTS longText_types;

-- enumeration types

DROP TABLE IF EXISTS enum_types;
DROP TABLE IF EXISTS set_types;

-- boolean & bit types

DROP TABLE IF EXISTS boolean_types;
DROP TABLE IF EXISTS bit_types;

-- integer types

DROP TABLE IF EXISTS tinyInt_types;
DROP TABLE IF EXISTS smallInt_types;
DROP TABLE IF EXISTS mediumInt_types;
DROP TABLE IF EXISTS int_types;
DROP TABLE IF EXISTS bigInt_types;

-- float types

DROP TABLE IF EXISTS float_types;
DROP TABLE IF EXISTS double_types;
DROP TABLE IF EXISTS decimal_types;

-- date types

DROP TABLE IF EXISTS date_types;
DROP TABLE IF EXISTS time_types;
DROP TABLE IF EXISTS dateTime_types;
DROP TABLE IF EXISTS timeStamp_2_types;
DROP TABLE IF EXISTS timeStamp_4_types;
DROP TABLE IF EXISTS timeStamp_types;
DROP TABLE IF EXISTS year_2_types;
DROP TABLE IF EXISTS year_4_types;


-- #############################################################
-- 
-- CREATE TABLE SECTION
--
-- #############################################################

CREATE TABLE char_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    char_type CHAR(30) DEFAULT NULL
);


CREATE TABLE varchar_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    varchar_type VARCHAR(30) DEFAULT NULL
);


CREATE TABLE tinyBlob_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tinyBlob_type TINYBLOB DEFAULT NULL
);


CREATE TABLE blob_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    blob_type BLOB DEFAULT NULL
);


CREATE TABLE mediumBlob_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    mediumBlob_type MEDIUMBLOB DEFAULT NULL
);


CREATE TABLE longBlob_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    longBlob_type LONGBLOB DEFAULT NULL
);


CREATE TABLE tinyText_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tinyText_type TINYTEXT DEFAULT NULL
);


CREATE TABLE text_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    text_type TEXT DEFAULT NULL
);


CREATE TABLE mediumText_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    mediumText_type MEDIUMTEXT DEFAULT NULL
);


CREATE TABLE longText_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    longText_type LONGTEXT DEFAULT NULL
);


CREATE TABLE enum_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    enum_type ENUM('New', 'Used') NOT NULL DEFAULT 'New'
);


CREATE TABLE set_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    set_type SET('a', 'b', 'c') DEFAULT NULL
);


CREATE TABLE boolean_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    boolean_type BOOLEAN DEFAULT NULL
);


CREATE TABLE bit_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    bit5_type BIT(5) DEFAULT NULL
);


CREATE TABLE tinyInt_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tinyInt_type TINYINT DEFAULT NULL
);


CREATE TABLE smallInt_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    smallInt_type SMALLINT DEFAULT NULL
);


CREATE TABLE mediumInt_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    mediumInt_type MEDIUMINT DEFAULT NULL
);


CREATE TABLE int_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    int_type INT DEFAULT NULL
);


CREATE TABLE bigInt_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    bigInt_type BIGINT DEFAULT NULL
);


CREATE TABLE float_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    float_type FLOAT DEFAULT NULL
);


CREATE TABLE double_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    double_type DOUBLE DEFAULT NULL
);


CREATE TABLE decimal_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    decimal_type DECIMAL(16,2) DEFAULT NULL
);


CREATE TABLE date_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    date_type DATE DEFAULT NULL
);


CREATE TABLE time_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    time_type TIME DEFAULT NULL
);


CREATE TABLE dateTime_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    dateTime_type DATETIME DEFAULT NULL
);


CREATE TABLE timeStamp_2_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    timeStamp_2 TIMESTAMP(2) NULL DEFAULT 0
);


CREATE TABLE timeStamp_4_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    timeStamp_4 TIMESTAMP(4)
);


CREATE TABLE timeStamp_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    timeStamp TIMESTAMP
);



CREATE TABLE year_2_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    year_2 YEAR(2) DEFAULT NULL
);


CREATE TABLE year_4_types (

--  Table id and creation data entries.
    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    year_4 YEAR(4) DEFAULT NULL
);



-- #############################################################
--
-- INSERT INTO TABLE SECTION
--
-- #############################################################

INSERT INTO char_types
    (char_type)
VALUES
    ('a'),
    ('ccaaaaaaaaaaaaaaaaaaaaaaaaaaa');

INSERT INTO varchar_types
    (varchar_type)
VALUES
    ('a'),
    ('ccaaaaaaaaaaaaaaaaaaaaaaaaaaa');

INSERT INTO tinyBlob_types
    (tinyBlob_type)
VALUES
    (',,!!!\\\\\\##');

INSERT INTO blob_types
    (blob_type)
VALUES
    (',,!!!\\\\\\##');

INSERT INTO mediumBlob_types
    (mediumBlob_type)
VALUES
    (',,!!!\\\\\\##');

INSERT INTO longBlob_types
    (longBlob_type)
VALUES
    (',,!!!\\\\\\##');

INSERT INTO tinyText_types
    (tinyText_type)
VALUES
    ('tiny text1');

INSERT INTO text_types
    (text_type)
VALUES
    ('text1');

INSERT INTO mediumText_types
    (mediumText_type)
VALUES
    ('mediumText1');

INSERT INTO longText_types
    (longText_type)
VALUES
    ('longText1');

INSERT INTO enum_types
    (enum_type)
VALUES
    ('New'),
    ('Used');

INSERT INTO set_types
    (set_type)
VALUES
    ('a,b,c'),
    ('a,b'),
    ('a,c');
    
INSERT INTO boolean_types
    (boolean_type)
VALUES
    ('1'),
    ('0');

INSERT INTO bit_types
    (bit5_type)
VALUES
    (B'00001'),
    (B'10000');

INSERT INTO tinyInt_types
    (tinyInt_type)
VALUES
    (-128),
    (127);

INSERT INTO smallInt_types
    (smallInt_type)
VALUES
    (-32768),
    (32767);

INSERT INTO mediumInt_types
    (mediumInt_type)
VALUES
    (-8388608),
    (8388607);

INSERT INTO int_types
    (int_type)
VALUES
    (-2147483648),
    (2147483647);

INSERT INTO bigInt_types
    (bigInt_type)
VALUES
    (-9223372036854775808),
    (9223372036854775807);

INSERT INTO float_types
    (float_type)
VALUES
    (-1.1e-39);

INSERT INTO double_types
    (double_type)
VALUES
    (-2.2e-309);

INSERT INTO decimal_types
    (decimal_type)
VALUES
    (-99999999999999.99),
    (99999999999999.99);

INSERT INTO date_types
    (date_type)
VALUES
    ('1000-01-01'),
    ('9999-12-31');

INSERT INTO time_types
    (time_type)
VALUES
    ('00:00:00'),
    ('23:59:59');

INSERT INTO dateTime_types
    (dateTime_type)
VALUES
    ('1000-01-01 00:00:00'),
    ('9999-12-31 23:59:59');

INSERT INTO timeStamp_2_types
    (timeStamp_2)
VALUES
    ('19700101000000'),
    ('20050111094301');

INSERT INTO timeStamp_4_types
    (timeStamp_4)
VALUES
    ('19700101000000'),
    ('20050111094301');

INSERT INTO timeStamp_types
    (timeStamp)
VALUES
    ('19700101000000'),
    ('20050111094301');

INSERT INTO year_2_types
    (year_2)
VALUES
    ('1947'),
    ('1901'),
    ('2155');

INSERT INTO year_4_types
    (year_4)
VALUES
    ('2047'),
    ('1901'),
    ('2155');