-- ============================================================
--   Test table to create tables with all data type fields
-- support by OracleL to test the loading of summary table
-- (JTable), TableViewForm, and TableEntryForm. 
-- =============================================================
-- Version 06/15/2008 Original Load Test SQL configuration for
--                    Tables of Oracle data types.
--         09/01/2008 Added Tables and Sequences for Intervals.
--                    Corrected Several Problems. Tested.
--         07/21/2010 Updated Contact, Email, Address.
--      
-- danap@dandymadeproductions.com
-- =============================================================

-- Note you may have to remove the DROP SEQUENCE/TABLE statement
-- if it is not used if using Ajqvue for the import. Oracle
-- does not appear to allow a commented SQL to be executed through
-- the JDBC. You may also just remove the semicolon from the end of
-- the statement after commenting, Ajqvue will then not treat
-- the commented line a SQL statement. Parses on ";/n".

-- #############################################################
--
-- DROP TABLE SECTION
--
-- #############################################################

--  Numeric fields.

	DROP TABLE number_types;
	DROP TABLE numeric_types;
	DROP TABLE float_types;
	
--  Character fields.	
	
	DROP TABLE char_types;
	DROP TABLE nchar_types;
	DROP TABLE varchar2_types;
	DROP TABLE nvarchar2_types;
	DROP TABLE long_types;
	DROP TABLE raw_types;
	DROP TABLE longraw_types;
	
--  LOB fields.
	
    DROP TABLE blob_types;
    DROP TABLE clob_types;
    DROP TABLE nclob_types;
    DROP TABLE bfile_types;
	
--  Date and time fields.

	DROP TABLE date_types;
	DROP TABLE timeStamp_types;
	DROP TABLE timeStampTZ_types;
	DROP TABLE timeStampLTZ_types;
	DROP TABLE intervalYear_types;
	DROP TABLE intervalDay_types;
	
-- #############################################################
-- 
-- CREATE SEQUENCE/TABLE SECTION
--
-- #############################################################

DROP SEQUENCE number_types_id_seq;
CREATE SEQUENCE number_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE number_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    number_type NUMBER DEFAULT NULL
);
ALTER TABLE number_types ADD CONSTRAINT number_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE numeric_types_id_seq;
CREATE SEQUENCE numeric_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE numeric_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    numeric_type NUMBER(10,2) DEFAULT NULL
);
ALTER TABLE numeric_types ADD CONSTRAINT numeric_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE float_types_id_seq;
CREATE SEQUENCE float_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE float_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    float_type FLOAT DEFAULT NULL
);
ALTER TABLE float_types ADD CONSTRAINT float_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE char_types_id_seq;
CREATE SEQUENCE char_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE char_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    char_type CHAR(30) DEFAULT NULL 
);
ALTER TABLE char_types ADD CONSTRAINT char_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE nchar_types_id_seq;
CREATE SEQUENCE nchar_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE nchar_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    nchar_type NCHAR(30) DEFAULT NULL 
);
ALTER TABLE nchar_types ADD CONSTRAINT nchar_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE varchar2_types_id_seq;
CREATE SEQUENCE varchar2_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE varchar2_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    varchar2_type VARCHAR2(30) DEFAULT NULL
);
ALTER TABLE varchar2_types ADD CONSTRAINT varchar2_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE nvarchar2_types_id_seq;
CREATE SEQUENCE nvarchar2_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE nvarchar2_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    nvarchar2_type NVARCHAR2(30) DEFAULT NULL
);
ALTER TABLE nvarchar2_types ADD CONSTRAINT nvarchar2_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE long_types_id_seq;
CREATE SEQUENCE long_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE long_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    long_type LONG
);
ALTER TABLE long_types ADD CONSTRAINT long_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE raw_types_id_seq;
CREATE SEQUENCE raw_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE raw_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    raw_type RAW(2000)
);
ALTER TABLE raw_types ADD CONSTRAINT raw_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE longraw_types_id_seq;
CREATE SEQUENCE longraw_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE longraw_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    longraw_type LONG RAW DEFAULT NULL
);
ALTER TABLE longraw_types ADD CONSTRAINT longraw_types_id_seq PRIMARY KEY (data_type_id);
   
DROP SEQUENCE blob_types_id_seq;
CREATE SEQUENCE blob_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE blob_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    blob_type BLOB DEFAULT NULL
);
ALTER TABLE blob_types ADD CONSTRAINT blob_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE clob_types_id_seq;
CREATE SEQUENCE clob_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE clob_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    clob_type CLOB DEFAULT NULL
);
ALTER TABLE clob_types ADD CONSTRAINT clob_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE nclob_types_id_seq;
CREATE SEQUENCE nclob_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE nclob_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    nclob_type NCLOB DEFAULT NULL
);
ALTER TABLE nclob_types ADD CONSTRAINT nclob_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE bfile_types_id_seq;
CREATE SEQUENCE bfile_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE bfile_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    nclob_type BFILE DEFAULT NULL
);
ALTER TABLE bfile_types ADD CONSTRAINT bfile_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE date_types_id_seq;
CREATE SEQUENCE date_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE date_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    date_type DATE DEFAULT NULL
);
ALTER TABLE date_types ADD CONSTRAINT date_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE timeStamp_types_id_seq;
CREATE SEQUENCE timeStamp_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE timeStamp_types (

--  Table id and creation data entries.
     data_type_id NUMBER NOT NULL,
     timeStamp_type TIMESTAMP DEFAULT NULL
);
ALTER TABLE timeStamp_types ADD CONSTRAINT timeStamp_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE timeStampTZ_types_id_seq;
CREATE SEQUENCE timeStampTZ_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE timeStampTZ_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    timeStampTZ_type TIMESTAMP WITH TIME ZONE DEFAULT NULL
);
ALTER TABLE timeStampTZ_types ADD CONSTRAINT timeStampTZ_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE timeStampLTZ_types_id_seq;
CREATE SEQUENCE timeStampLTZ_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE timeStampLTZ_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    timestampLTZ_type TIMESTAMP WITH LOCAL TIME ZONE DEFAULT NULL
);
ALTER TABLE timeStampLTZ_types ADD CONSTRAINT timeStampLTZ_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE intervalYear_types_id_seq;
CREATE SEQUENCE intervalYear_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE intervalYear_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    intervalYear_type INTERVAL YEAR TO MONTH DEFAULT NULL
);
ALTER TABLE intervalYear_types ADD CONSTRAINT intervalYear_types_id_seq PRIMARY KEY (data_type_id);

DROP SEQUENCE intervalDay_types_id_seq;
CREATE SEQUENCE intervalDay_types_id_seq
	START WITH 1
	INCREMENT BY 1
	NOMAXVALUE;

CREATE TABLE intervalDay_types (

--  Table id and creation data entries.
    data_type_id NUMBER NOT NULL,
    intervalDay_type INTERVAL DAY(6) TO SECOND(5) DEFAULT NULL
);
ALTER TABLE intervalDay_types ADD CONSTRAINT intervalDay_types_id_seq PRIMARY KEY (data_type_id);

-- #############################################################
--
-- INSERT INTO TABLE SECTION
--
-- #############################################################

INSERT INTO number_types (data_type_id, number_type) VALUES
    (number_types_id_seq.NEXTVAL, -1.0E-125);
INSERT INTO number_types (data_type_id, number_type) VALUES
    (number_types_id_seq.NEXTVAL, 9.9E125);

INSERT INTO numeric_types (data_type_id, numeric_type) VALUES
    (numeric_types_id_seq.NEXTVAL, -10.26);
INSERT INTO numeric_types (data_type_id, numeric_type) VALUES
    (numeric_types_id_seq.NEXTVAL, 10.26);

INSERT INTO float_types (data_type_id, float_type) VALUES
    (float_types_id_seq.NEXTVAL, -1.1E-39);
INSERT INTO float_types (data_type_id, float_type) VALUES
    (float_types_id_seq.NEXTVAL, 1.1E39);
    
INSERT INTO char_types (data_type_id, char_type) VALUES
    (char_types_id_seq.NEXTVAL, 'a');
INSERT INTO char_types (data_type_id, char_type) VALUES
    (char_types_id_seq.NEXTVAL, 'aaaaaaaaaabbbbbbbbbbcccccccccc');

INSERT INTO nchar_types (data_type_id, nchar_type) VALUES
    (nchar_types_id_seq.NEXTVAL, 'a');
INSERT INTO nchar_types (data_type_id, nchar_type) VALUES
    (nchar_types_id_seq.NEXTVAL, 'aaaaaaaaaabbbbbbbbbbcccccccccc');

INSERT INTO varchar2_types (data_type_id, varchar2_type) VALUES
    (varchar2_types_id_seq.NEXTVAL, 'a');
INSERT INTO varchar2_types (data_type_id, varchar2_type) VALUES
    (varchar2_types_id_seq.NEXTVAL, 'aaaaaaaaaabbbbbbbbbbcccccccccc');

INSERT INTO nvarchar2_types (data_type_id, nvarchar2_type) VALUES
    (nvarchar2_types_id_seq.NEXTVAL, 'a');
INSERT INTO nvarchar2_types (data_type_id, nvarchar2_type) VALUES
    (nvarchar2_types_id_seq.NEXTVAL, 'aaaaaaaaaabbbbbbbbbbcccccccccc');

INSERT INTO long_types (data_type_id, long_type) VALUES
	(long_types_id_seq.NEXTVAL, 'long text');
	
INSERT INTO raw_types (data_type_id, raw_type) VALUES
	(raw_types_id_seq.NEXTVAL, HEXTORAW('89504e470d0a1a0a0000000d494844520000000d0000000d080600000072ebe47c000000294944415428916360a002f84fa2386e09747926729c433f4d3040c83f38d50db7808001925304d10000a9050a0345b9711e0000000049454e44ae426082'));

INSERT INTO longraw_types (data_type_id, longraw_type) VALUES
	(longraw_types_id_seq.NEXTVAL, HEXTORAW('89504e470d0a1a0a0000000d494844520000000d0000000d080600000072ebe47c000000294944415428916360a002f84fa2386e09747926729c433f4d3040c83f38d50db7808001925304d10000a9050a0345b9711e0000000049454e44ae426082'));
 
INSERT INTO blob_types (data_type_id, blob_type) VALUES
	(blob_types_id_seq.NEXTVAL, HEXTORAW('89504e470d0a1a0a0000000d494844520000000d0000000d080600000072ebe47c000000294944415428916360a002f84fa2386e09747926729c433f4d3040c83f38d50db7808001925304d10000a9050a0345b9711e0000000049454e44ae426082'));

INSERT INTO clob_types (data_type_id, clob_type) VALUES
	(clob_types_id_seq.NEXTVAL, ',,!!!##');

INSERT INTO nclob_types (data_type_id, nclob_type) VALUES
	(nclob_types_id_seq.NEXTVAL, ',,!!!##');
	
-- INSERT INTO bfile_types (data_type_id, bfile_type) VALUES
--	(bfile_types_id_seq.NEXTVAL, '13-NOV-92')
	
INSERT INTO date_types (data_type_id, date_type) VALUES
	(date_types_id_seq.NEXTVAL, '13-NOV-92');
  
INSERT INTO timeStamp_types (data_type_id, timeStamp_type) VALUES
	(timeStamp_types_id_seq.NEXTVAL, SYSTIMESTAMP);
	
INSERT INTO timeStampTZ_types (data_type_id, timeStampTZ_type) VALUES
	(timeStampTZ_types_id_seq.NEXTVAL, SYSTIMESTAMP);
	
INSERT INTO timeStampLTZ_types (data_type_id, timeStampLTZ_type) VALUES
	(timeStampLTZ_types_id_seq.NEXTVAL, SYSTIMESTAMP);
	
INSERT INTO intervalYear_types (data_type_id, intervalYear_type) VALUES
	(intervalYear_types_id_seq.NEXTVAL, '1-2');
	
INSERT INTO intervalDay_types (data_type_id, intervalDay_type) VALUES
	(intervalDay_types_id_seq.NEXTVAL, '4 5:12:10.222');