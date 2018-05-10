-- =============================================================
-- Test table to create the various data types that are
-- defined by the HSQL database.
-- =============================================================
-- Dana Proctor 
-- Version 01/18/2008 Original Test hsqltypes Table.
--         01/29/2008 Field data_type_id Changed to Type IDENTITY.
--         01/31/2008 Changed Field timeStamp_type0 to timeStamp0_type.
--         07/21/2010 Updated Contact, Email, Address.
--         07/15/2011 Modifed to Accomodate HSQL2, Binary Sizing,
--                    Bit Varying, Timestamps, & Interval.
--         04/22/2012 Added View Table myView.
--         06/29/2016 Modified VARBINARY Size to 200000, for Testing.
--         05/10/2018 Added bit2_type.
--      
-- danap@dandymadeproductions.com
-- =============================================================

DROP TABLE IF EXISTS hsqltypes;
CREATE TABLE hsqltypes (

--  Table id and creation data entries.

    data_type_id IDENTITY PRIMARY KEY,

--  Character, text, and blob type fields.

    char_type CHAR(30) DEFAULT NULL,
    varchar_type VARCHAR(30) DEFAULT NULL,
    longvarchar_type LONGVARCHAR DEFAULT NULL,
    clob_type CLOB DEFAULT NULL,
    
--  Binary fields.

    binary_type BINARY DEFAULT NULL,
    varbinary_type VARBINARY(200000) DEFAULT NULL,
    longvarbinary_type LONGVARBINARY DEFAULT NULL,
    blob_type BLOB DEFAULT NULL,

--  Numeric fields.

    tinyInt_type TINYINT DEFAULT NULL,
    smallInt_type SMALLINT DEFAULT NULL,
    int_type INTEGER DEFAULT NULL,
    bigInt_type BIGINT DEFAULT NULL,
    float_type FLOAT DEFAULT NULL,
    double_type DOUBLE DEFAULT NULL,
    real_type REAL DEFAULT NULL,
    decimal_type DECIMAL(16,2) DEFAULT NULL,
    numeric_type NUMERIC DEFAULT NULL,
    
--  Bit string fields.

    boolean_type BOOLEAN DEFAULT NULL,
    bit_type BIT DEFAULT NULL,
    bit2_type BIT(2) DEFAULT NULL,
    bitvarying_type BIT VARYING(5) DEFAULT NULL,
    
--  Date and time fields.
   
    date_type DATE  DEFAULT NULL,
    time_type TIME DEFAULT NULL,
    timeTMZ_type TIME(6) WITH TIME ZONE,
    dateTime_type DATETIME DEFAULT NULL,
    timeStamp_type TIMESTAMP,
    timeStampTMZ_type TIMESTAMP(6) WITH TIME ZONE,
    interval_type INTERVAL YEAR(3)
);

--
-- View for hsqltypes
--

DROP VIEW IF EXISTS "myView";
CREATE VIEW "myView" AS SELECT * FROM hsqltypes;