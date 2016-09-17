-- =============================================================
-- Test table to create the various data types that are
-- defined by the Oracle database.
-- =============================================================
-- Dana Proctor 
-- Version 05/18/2008 Orignal Test Data Type Table for Oracle.
--         05/21/2008 Changed Field timeStamp to timeStamp_type.
--         05/25/2008 Increased raw_type Field Byte Size to 2000.
--         05/25/2008 Added longraw_type Field.
--         06/13/2008 Comments. Moved PRIMARY KEY Declaration.
--         06/14/2008 Added SEQUENCE oracletypes_data_type_id_seq
--                    & Associated With Primary Key data_type_id.
--         06/16/2008 AutoIncrement Feature Support For Primary
--                    Key, Finalized.
--         06/17/2008 Added Fields bfloat_type & bdouble_type.
--         08/13/2008 Added Comments and uncommented time timeStamp_ltz
--                    for finalializing during Ajqvue Oracle
--                    data types support.
--         08/19/2008 Added Fields interval_year & interval_day.
--         08/31/2008 Updated Comment on DROP SEQUENCE/TABLE to try
--                    and clarify.
--         07/21/2010 Updated Contact, Email, Address.
--         04/23/2012 Added View Tables myView.
--      
-- danap@dandymadeproductions.com
-- =============================================================

-- Note you may have to remove the DROP SEQUENCE/TABLE statement
-- if it is not used if using Ajqvue for the import. Oracle
-- does not appear to allow a commented SQL to be executed through
-- the JDBC. You may also just remove the semicolon from the end of
-- the statement after commenting, Ajqvue will then not treat
-- the commented line a SQL statement. Parses on ";/n". Comment
-- either LONG or LONG RAW for testing. Oracle does not support
-- two LONG types in a table.

DROP SEQUENCE oracletypes_data_type_id_seq;
CREATE SEQUENCE oracletypes_data_type_id_seq
 	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

DROP TABLE oracletypes;
CREATE TABLE oracletypes (

--  Table id and creation data entries.

	data_type_id NUMBER NOT NULL,

--  Character, text, and lob type fields.

	char_type CHAR(30) DEFAULT NULL,
	nchar_type NCHAR(30) DEFAULT NULL,
	varchar2_type VARCHAR2(30) DEFAULT NULL,
	nvarchar2_type NVARCHAR2(30) DEFAULT NULL,
	long_type LONG DEFAULT NULL,
	raw_type RAW(2000) DEFAULT NULL,	
--	longraw_type LONG RAW DEFAULT NULL,
	blob_type BLOB DEFAULT NULL,
	clob_type CLOB DEFAULT NULL,
	nclob_type NCLOB DEFAULT NULL,
	bfile_type BFILE DEFAULT NULL,

--  Numeric fields.

	number_type NUMBER DEFAULT NULL,
	numeric_type NUMBER(10,2) DEFAULT NULL,
	float_type FLOAT DEFAULT 10.5,
	bfloat_type BINARY_FLOAT DEFAULT NULL,
	bdouble_type BINARY_DOUBLE DEFAULT NULL,

--  Date and time fields.

	date_type DATE,
	timeStamp_type TIMESTAMP,
	timeStamp_tz TIMESTAMP WITH TIME ZONE,
	timeStamp_ltz TIMESTAMP WITH LOCAL TIME ZONE,
	interval_year INTERVAL YEAR TO MONTH,
	interval_day INTERVAL DAY(6) TO SECOND(5)
);
ALTER TABLE oracletypes ADD CONSTRAINT oracletypes_data_type_id_seq PRIMARY KEY (data_type_id);

--
-- View for oracletypes
--

CREATE OR REPLACE VIEW "myView" AS SELECT * FROM oracletypes;