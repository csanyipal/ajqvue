-- =============================================================
-- Test table to create the various data types that are
-- defined by the HSQL database.
-- =============================================================
-- Dana Proctor 
-- Version 01/18/2008 Original Test hsqltypes Table.
--         01/29/2008 Field data_type_id Changed to Type IDENTITY.
--         01/31/2008 Changed Field timeStamp_type0 to timeStamp0_type.
--         07/21/2010 Updated Contact, Email, Address.
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
    binary_type BINARY DEFAULT NULL,
    varbinary_type VARBINARY DEFAULT NULL,
    longvarbinary_type LONGVARBINARY DEFAULT NULL,

--  Numeric fields.

    boolean_type BOOLEAN DEFAULT NULL,
    bit_type BIT DEFAULT NULL,
    tinyInt_type TINYINT DEFAULT NULL,
    smallInt_type SMALLINT DEFAULT NULL,
    int_type INTEGER DEFAULT NULL,
    bigInt_type BIGINT DEFAULT NULL,
    float_type FLOAT DEFAULT NULL,
    double_type DOUBLE DEFAULT NULL,
    real_type REAL DEFAULT NULL,
    decimal_type DECIMAL(16,2) DEFAULT NULL,
    numeric_type NUMERIC DEFAULT NULL,
    
--  Date and time fields.
   
    date_type DATE  DEFAULT NULL,
    time_type TIME DEFAULT NULL,
    dateTime_type DATETIME DEFAULT NULL,
    timeStamp0_type TIMESTAMP(0),
    timeStamp_type TIMESTAMP
);