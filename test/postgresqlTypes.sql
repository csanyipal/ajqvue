-- =============================================================
-- Test table to create the various data types that are
-- defined by the postgreSQL database.
-- =============================================================
-- Dana Proctor 
-- Version 10/27/2007 Initial Test Data Type Table.
--         10/29/2007 Removed Type Monetary, Deprecated.
--         11/03/2007 Changed numeric_type to numeric(10,2).
--         11/09/2007 Changed bit_type to bit2_type, & bitVarying_type
--                    to bitVarying5_type.
--         11/21/2007 Uncommented All The Geometric Data Types
--                    & Interval Type.
--         02/25/2008 bitVary2_type Changed to bitVary5_type.
--         07/21/2010 Updated Contact, Email, Address.
--         04/22/2012 Added View Tables myView.
--         11/25/2015 Added Type mood to accommdate enum_types addition. 
--      
-- danap@dandymadeproductions.com
-- =============================================================

DROP TABLE IF EXISTS postgresqltypes;

DROP TYPE IF EXISTS mood;
CREATE TYPE mood AS ENUM ('sad', 'ok', 'happy');

CREATE TABLE postgresqltypes (

--  Table id and creation data entries.
	data_type_id serial NOT NULL,
	
--  Numeric fields.

	smallInt_type smallint DEFAULT NULL,
	int_type integer DEFAULT NULL,
	bigInt_type bigint DEFAULT NULL,
	decimal_type decimal(16,2) DEFAULT NULL,
	numeric_type numeric(10,2) DEFAULT NULL,
	real_type real DEFAULT NULL,
	doublePrecision_type double precision DEFAULT NULL,
	serial_type serial,
	bigSerial_type bigserial,
	
--  Character fields.	
	
	varchar_type varchar(30) DEFAULT NULL,
	char_type char(30) DEFAULT NULL,
	text_type text DEFAULT NULL,
	
--  Binary fields.
	
	bytea_type bytea DEFAULT NULL,
	
--  Date and time fields.

	date_type date DEFAULT NULL,
	time_type time without time zone DEFAULT NULL,
	timeTZ_type time with time zone DEFAULT NULL,
	timestamp_type timestamp without time zone DEFAULT '2007-10-14 00:00:00',
	timestampTZ_type timestamp with time zone,
	interval_type interval DEFAULT NULL,

--  Boolean field.

	boolean_type boolean DEFAULT NULL,

--  Geometric fields.

	point_type point DEFAULT NULL,
--	line_type line DEFAULT NULL,
	lineSegment_type lseg DEFAULT NULL,
	box_type box DEFAULT NULL,
	path_type path DEFAULT NULL,
	polygon_type polygon DEFAULT NULL,
	circle_type circle DEFAULT NULL,
	
--  Network address fields.
 
 	cidr_type cidr DEFAULT NULL,
 	inet_type inet DEFAULT NULL,
 	macaddr_type macaddr DEFAULT NULL,
	
--  Bit string fields.

	bit2_type bit(2) DEFAULT NULL,
	bitVarying5_type bit varying(5) DEFAULT NULL,
	
--  Special fields.

	enum_type mood DEFAULT 'ok',
	
  	PRIMARY KEY  (data_type_id)
);

--
-- View for postgresqltypes
--

DROP VIEW IF EXISTS "myView";
CREATE VIEW "myView" AS SELECT * FROM postgresqltypes;