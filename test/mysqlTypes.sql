-- =============================================================
-- Test table to create the various data types that are
-- defined by the MySQL/MariaDB database.
-- =============================================================
-- Dana Proctor 
-- Version 02/09/2006 Orignal Test Data Type Table.
--         08/20/2006 Correction to MediumInt Name. 
--         11/19/2006 TIMESTAMP Fields (2)-(14).
--         11/22/2006 Modified TIMESTAMP Fields Names.
--         11/29/2006 Removed DEFAULT NULL From TIMESTAMP Fields.
--         10/27/2007 Changed Comments from # to --.
--         11/09/2007 Added Boolean & BIT(5) Types.
--                    Fails on MySQL 4.xx.
--         07/21/2010 Updated Contact, Email, Address.
--         04/22/2012 Added View Tables myView.
--         10/21/2014 Commented Out All Timestamps Above (6) &
--                    Added Just timeStamp_type Table. Removed TYPE = InnoDB.
--      
-- danap@dandymadeproductions.com
-- =============================================================

DROP TABLE IF EXISTS mysqltypes;
CREATE TABLE mysqltypes (

--  Table id and creation data entries.

    data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

--  Character, text, and blob type fields.

    char_type CHAR(30) DEFAULT NULL,
    varchar_type VARCHAR(30) DEFAULT NULL,
    tinyBlob_type TINYBLOB DEFAULT NULL,
    blob_type BLOB DEFAULT NULL,
    mediumBlob_type MEDIUMBLOB DEFAULT NULL,
    longBlob_type LONGBLOB DEFAULT NULL,
    tinyText_type TINYTEXT DEFAULT NULL,
    text_type TEXT DEFAULT NULL,
    mediumText_type MEDIUMTEXT DEFAULT NULL,
    longText_type LONGTEXT DEFAULT NULL,
    enum_type ENUM("New", "Used") NOT NULL DEFAULT 'New',
    set_type SET("a", "b", "c") DEFAULT NULL,

--  Numeric fields.

    boolean_type BOOLEAN DEFAULT NULL,
    bit5_type BIT(5) DEFAULT NULL,
    tinyInt_type TINYINT DEFAULT NULL,
    smallInt_type SMALLINT UNSIGNED DEFAULT NULL,
    mediumInt_type MEDIUMINT DEFAULT NULL,
    int_type INT DEFAULT NULL,
    bigInt_type BIGINT DEFAULT NULL,
    float_type FLOAT DEFAULT NULL,
    double_type DOUBLE DEFAULT NULL,
    decimal_type DECIMAL(16,2) DEFAULT NULL,
    
--  Date and time fields.
   
    date_type DATE  DEFAULT NULL,
    time_type TIME DEFAULT NULL,
    dateTime_type DATETIME DEFAULT NULL,
    timeStamp_2 TIMESTAMP(2), 
    timeStamp_4 TIMESTAMP(4),
    timeStamp_6 TIMESTAMP(6),
    timeStamp_type TIMESTAMP,
--    timeStamp_8 TIMESTAMP(8),
--    timeStamp_10 TIMESTAMP(10),
--    timeStamp_12 TIMESTAMP(12),
--    timeStamp_14 TIMESTAMP(14),
    year_2 YEAR(2) DEFAULT NULL,
    year_4 YEAR(4) DEFAULT NULL
);

--
-- View for mysqltypes
--

DROP VIEW IF EXISTS `myView`;
CREATE VIEW `myView` AS SELECT * FROM `mysqltypes`;