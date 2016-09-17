-- =============================================================
-- Test table to create the various data types that are
-- defined by the Microsoft SQL database.
-- =============================================================
-- Dana Proctor 
-- Version 01/22/2014 Original Test Data Type Table for MS SQL.
--         01/27/2014 Finalized Initial Test Data Types Table.
--         02/18/2014 Added Field real_type.
--      
-- danap@dandymadeproductions.com
-- =============================================================

-- DROP TABLE "mssqltypes";
CREATE TABLE "mssqltypes" (

--  Table id and creation data entries.

    "data_type_id" BIGINT IDENTITY(1,1),

--  Character, text, and blob type fields.

    "char_type" CHAR(30) DEFAULT NULL,
    "nchar_type" NCHAR(30) DEFAULT NULL,
    "varchar_type" VARCHAR(30) DEFAULT NULL,
--    "varcharmax_type" VARCHAR(MAX) DEFAULT NULL,
    "nvarchar_type" NVARCHAR(30) DEFAULT NULL,
--    "nvarcharmax_type" NVARCHAR(MAX) DEFAULT NULL,
    "binary_type" BINARY(7000) DEFAULT NULL,
    "varbinary_type" VARBINARY(7000) DEFAULT NULL,
--    "varbinarymax_type" VARBINARY(MAX) DEFAULT NULL,
    "image_type" IMAGE DEFAULT NULL,
    "text_type" TEXT DEFAULT NULL,
    "ntext_type" NTEXT DEFAULT NULL,
--    "udt_type" UDT DEFAULT NULL,
    "uniqueidentifier_type" UNIQUEIDENTIFIER DEFAULT NULL,
    "xml_type" XML DEFAULT NULL,

--  Numeric fields.

    "bit_type" BIT DEFAULT NULL,
    "tinyInt_type" TINYINT DEFAULT NULL,
    "smallInt_type" SMALLINT DEFAULT NULL,
    "int_type" INT DEFAULT NULL,
    "bigInt_type" BIGINT DEFAULT NULL,
    "float_type" FLOAT DEFAULT NULL,
    "real_type" REAL DEFAULT NULL,
    "decimal_type" DECIMAL(16,2) DEFAULT NULL,
    "numeric_type" NUMERIC(10,2) DEFAULT NULL,
    "money_type" MONEY DEFAULT NULL,
    "smallmoney_type" SMALLMONEY DEFAULT NULL,
    
--  Date and time fields.
   
    "date_type" DATE DEFAULT NULL,
    "time_type" TIME DEFAULT NULL,
    "dateTime_type" DATETIME DEFAULT NULL,
    "smalldatetime_type" SMALLDATETIME DEFAULT NULL,
    "datetime2_type" DATETIME2 DEFAULT NULL,
    "datetimeoffset_type" DATETIMEOFFSET(2) DEFAULT NULL,
--    "timestamp_type" TIMESTAMP,
    PRIMARY KEY (data_type_id)
);

--
-- View for mysqltypes
--

-- DROP VIEW IF EXISTS "myView";
CREATE VIEW "myView" AS SELECT * FROM "mssqltypes";