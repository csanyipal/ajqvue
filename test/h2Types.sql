-- =============================================================
-- Test table to create the various data types that are
-- defined by the H2 database.
-- =============================================================
-- Dana Proctor 
-- Version 08/26/2013 Original Test Data Type Table.
--      
-- danap@dandymadeproductions.com
-- =============================================================

-- DROP TABLE IF EXISTS "h2types";
CREATE TABLE "h2types" (

--  Table id and creation data entries.

    "data_type_id" IDENTITY PRIMARY KEY,

--  Character, text, and blob type fields.

    "char_type" CHAR(30) DEFAULT NULL,
    "varchar_type" VARCHAR(30) DEFAULT NULL,
    "varchar_type_ignorecase" VARCHAR_IGNORECASE(30) DEFAULT NULL,
--    "binary_type" BINARY(1000) DEFAULT NULL,
    "blob_type" BLOB(1M) DEFAULT NULL,
    "clob_type" CLOB(1M) DEFAULT NULL,
--    "other_type" OTHER DEFAULT NULL,
--    "uuid_type" UUID DEFAULT NULL,

--  Numeric fields.

    "boolean_type" BOOLEAN DEFAULT NULL,
    "tinyInt_type" TINYINT DEFAULT NULL,
    "smallInt_type" SMALLINT DEFAULT NULL,
    "int_type" INT DEFAULT NULL,
    "bigInt_type" BIGINT DEFAULT NULL,
    "real_type" REAL DEFAULT NULL,
    "double_type" DOUBLE DEFAULT NULL,
    "decimal_type" DECIMAL(16,2) DEFAULT NULL,  
    
--  Date and time fields.

    "date_type" DATE DEFAULT NULL,
    "time_type" TIME DEFAULT NULL,
    "timeStamp_type" TIMESTAMP,
    
--  Geometric fields.

--    "geometry_type" GEOMETRY DEFAULT NULL,
    
--  Array fields.

    "array_type" ARRAY DEFAULT NULL
);

--
-- View for h2types
--

-- DROP VIEW IF EXISTS "myView";
CREATE VIEW "myView" AS SELECT * FROM "h2types";