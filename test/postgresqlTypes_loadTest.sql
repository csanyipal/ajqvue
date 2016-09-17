-- ============================================================
--   Test table to create tables with all data type fields
-- support by PostgreSQL to test the loading of summary table
-- (JTable), TableViewForm, and TableEntryForm. 
-- =============================================================
-- Version 10/29/2007 Original Load Test SQL configuration for
--                    Tables of PostgreSQL data types. NOT TESTED!
--         11/08/2007 Removed Auto-Increment and Replaced INT
--                    ids to be SERIAL. Also Corrected Seperate
--                    Statements for CREATE TABLE Statements.
--                    Tested Import, Functional All, But Commented
--                    Areas, Ex. Geometric Tables.
--         11/19/2007 Modifed All Commented Areas to Be Now Included.
--                    Essentially All Geometric Types and Interval.
--                    Tested Loads Properly.
--         12/14/2007 Cleaned Up Some.
--         02/24/2007 Added Table array_types.
--         02/25/2007 Removed Table array_types, and Moved to
--                    Separate File, postgresqlTypes_array.sql
--         07/21/2010 Updated Contact, Email, Address.
--      
-- danap@dandymadeproductions.com
-- =============================================================

-- #############################################################
--
-- DROP TABLE SECTION
--
-- #############################################################

--  Numeric fields.

	DROP TABLE IF EXISTS smallInt_types;
	DROP TABLE IF EXISTS integer_types;
	DROP TABLE IF EXISTS bigInt_types;
	DROP TABLE IF EXISTS decimal_types;
	DROP TABLE IF EXISTS numeric_types;
	DROP TABLE IF EXISTS real_types;
	DROP TABLE IF EXISTS doublePrecision_types;
	DROP TABLE IF EXISTS serial_types;
	DROP TABLE IF EXISTS bigSerial_types;
	
--  Character fields.	
	
	DROP TABLE IF EXISTS varchar_types;
	DROP TABLE IF EXISTS char_types;
	DROP TABLE IF EXISTS text_types;
	
--  Binary fields.
	
    DROP TABLE IF EXISTS bytea_types;
	
--  Date and time fields.

	DROP TABLE IF EXISTS date_types;
	DROP TABLE IF EXISTS time_types;
	DROP TABLE IF EXISTS timeTZ_types;
	DROP TABLE IF EXISTS timeStamp_types;
	DROP TABLE IF EXISTS timeStampTZ_types;
	DROP TABLE IF EXISTS interval_types;

--  Boolean field.

	DROP TABLE IF EXISTS boolean_types;

--  Geometric fields.
	
	DROP TABLE IF EXISTS point_types;
--	DROP TABLE IF EXISTS line_types;
	DROP TABLE IF EXISTS lineSegment_types;
	DROP TABLE IF EXISTS box_types;
	DROP TABLE IF EXISTS path_types;
	DROP TABLE IF EXISTS polygon_types;
	DROP TABLE IF EXISTS circle_types;
	
--  Network address fields.
 
 	DROP TABLE IF EXISTS cidr_types;
 	DROP TABLE IF EXISTS inet_types;
 	DROP TABLE IF EXISTS macaddr_types;
	
--  Bit string fields.

	DROP TABLE IF EXISTS bit2_types;
	DROP TABLE IF EXISTS bitVarying5_types;
	
-- #############################################################
-- 
-- CREATE TABLE SECTION
--
-- #############################################################

CREATE TABLE smallInt_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    smallInt_type smallint DEFAULT NULL
);

CREATE TABLE integer_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    int_type integer DEFAULT NULL
);

CREATE TABLE bigInt_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    bigInt_type bigint DEFAULT NULL
);

CREATE TABLE decimal_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    decimal_type decimal(16,2) DEFAULT NULL 
);

CREATE TABLE numeric_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    numeric_type numeric DEFAULT NULL 
);

CREATE TABLE real_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    real_type real DEFAULT NULL
);

CREATE TABLE doublePrecision_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    doublePrecision_type double precision DEFAULT NULL
);

CREATE TABLE serial_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    serial_type serial
);

CREATE TABLE bigSerial_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    bigSerial_type bigserial
);

CREATE TABLE varchar_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    varchar_type varchar(30) DEFAULT NULL
);

CREATE TABLE char_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    char_type char(30) DEFAULT NULL
);

CREATE TABLE text_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    text_type text DEFAULT NULL
);

CREATE TABLE bytea_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    bytea_type bytea DEFAULT NULL
);

CREATE TABLE date_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    date_type date DEFAULT NULL
);

CREATE TABLE time_types (

--  Table id and creation data entries.
     data_type_id SERIAL NOT NULL PRIMARY KEY,
     time_type time without time zone DEFAULT NULL
);

CREATE TABLE timeTZ_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    timeTZ_type time with time zone DEFAULT NULL
);

CREATE TABLE timeStamp_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    timestamp_type timestamp without time zone DEFAULT NULL
);

CREATE TABLE timeStampTZ_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    timestampTZ_type timestamp with time zone DEFAULT NULL
);

CREATE TABLE interval_types (

--  Table id and creation data entries.
	data_type_id SERIAL NOT NULL PRIMARY KEY,
    interval_type interval DEFAULT NULL
);

CREATE TABLE boolean_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    boolean_type boolean DEFAULT NULL
);

CREATE TABLE point_types (

--  Table id and creation data entries.
	data_type_id SERIAL NOT NULL PRIMARY KEY,
	point_type point DEFAULT NULL
);

-- CREATE TABLE line_types (

--  Table id and creation data entries.
--	data_type_id SERIAL NOT NULL PRIMARY KEY,
--	line_type line DEFAULT NULL
-- );

CREATE TABLE lineSegment_types (

--  Table id and creation data entries.
	data_type_id SERIAL NOT NULL PRIMARY KEY,
	lineSegment_type lseg DEFAULT NULL
);

CREATE TABLE box_types (

--  Table id and creation data entries.
	data_type_id SERIAL NOT NULL PRIMARY KEY,
	box_type box DEFAULT NULL
);

CREATE TABLE path_types (

--  Table id and creation data entries.
	data_type_id SERIAL NOT NULL PRIMARY KEY,
	path_type path DEFAULT NULL
);

CREATE TABLE polygon_types (

--  Table id and creation data entries.
	data_type_id SERIAL NOT NULL PRIMARY KEY,
	polygon_type polygon DEFAULT NULL
);

CREATE TABLE circle_types (

--  Table id and creation data entries.
	data_type_id SERIAL NOT NULL PRIMARY KEY,
	circle_type circle DEFAULT NULL
);

CREATE TABLE cidr_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    cidr_type cidr DEFAULT NULL
);

CREATE TABLE inet_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    inet_type inet DEFAULT NULL
);

CREATE TABLE macaddr_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    macaddr_type macaddr DEFAULT NULL
);

CREATE TABLE bit2_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    bit_type bit(2) DEFAULT NULL
);

CREATE TABLE bitVarying5_types (

--  Table id and creation data entries.
    data_type_id SERIAL NOT NULL PRIMARY KEY,
    bitVarying_type bit varying(5) DEFAULT NULL
);

-- #############################################################
--
-- INSERT INTO TABLE SECTION
--
-- #############################################################
	
INSERT INTO smallInt_types
    (smallInt_type)
VALUES
    (32767),
    (-32768);

INSERT INTO integer_types
    (int_type)
VALUES
    (2147483647),
    (-2147483648);

INSERT INTO bigInt_types
    (bigInt_type)
VALUES
    (9223372036854775807),
    (-9223372036854775808);

INSERT INTO decimal_types
    (decimal_type)
VALUES
    (100);

INSERT INTO numeric_types
    (numeric_type)
VALUES
    (100);

INSERT INTO real_types
    (real_type)
VALUES
    (3.000001),
    (-3.000001);

INSERT INTO doublePrecision_types
    (doublePrecision_type)
VALUES
    (3.000000000000001),
    (-3.000000000000001);

INSERT INTO serial_types
    (serial_type)
VALUES
    (1),
    (2147483647);

INSERT INTO bigSerial_types
    (bigSerial_type)
VALUES
    (1),
    (9223372036854775807);

INSERT INTO varchar_types
    (varchar_type)
VALUES
 	('a'),
    ('aaaaaaaaaabbbbbbbbbbcccccccccc');

INSERT INTO char_types
    (char_type)
VALUES
    ('a'),
    ('aaaaaaaaaabbbbbbbbbbcccccccccc');

INSERT INTO text_types
    (text_type)
VALUES
    ('text');
    
INSERT INTO bytea_types
    (bytea_type)
VALUES
    (',,!!!##');

INSERT INTO date_types
    (date_type)
VALUES
    ('01/01/4713 BC'),
    ('01/01/5874897 AD');

INSERT INTO time_types
    (time_type)
VALUES
    ('00:00:00'),
    ('24:00:00');

INSERT INTO timeTZ_types
    (timeTZ_type)
VALUES
    ('00:00:00'),
    ('24:00:00');

INSERT INTO timeStamp_types
    (timeStamp_type)
VALUES
    (NOW());

INSERT INTO timeStampTZ_types
    (timeStampTZ_type)
VALUES
    (NOW());

INSERT INTO interval_types
    (interval_type)
VALUES
   ('178000000'),
    ('-178000000');
    
INSERT INTO boolean_types
    (boolean_type)
VALUES
    (TRUE),
    (FALSE);

INSERT INTO point_types
    (point_type)
VALUES
    ('0,0');

-- INSERT INTO line_types
--  (line_type)
-- VALUES
--    ('(0,0),(10,10)');

INSERT INTO lineSegment_types
    (lineSegment_type)
VALUES
    ('(0,0),(10,10)');

INSERT INTO box_types
   (box_type)
VALUES
   ('(0,0),(10,10)');

INSERT INTO path_types
    (path_type)
VALUES
    ('(0,0),(10,10)');

INSERT INTO polygon_types
    (polygon_type)
VALUES
    ('(0,0),(10,10)');

INSERT INTO circle_types
   (circle_type)
VALUES
    ('<(0,0),10>');

INSERT INTO cidr_types
    (cidr_type)
VALUES
    ('10.1.2.3/32');

INSERT INTO inet_types
    (inet_type)
VALUES
    ('10.1.2.0');

INSERT INTO macaddr_types
    (macaddr_type)
VALUES
    ('08:00:2b:00:01:02');

INSERT INTO bit2_types
    (bit_type)
VALUES
    (B'01');
    
INSERT INTO bitVarying5_types
    (bitVarying_type)
VALUES
    (B'01010');