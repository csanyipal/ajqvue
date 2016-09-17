-- =============================================================
-- Test table to create the various data types as arrays.
-- =============================================================
-- Dana Proctor 
-- Version 02/25/2008 Initial array_types Test Table.
--         03/01/2008 Added Default Values to Fields int_array,
--                    varchar_array, and text_array.
--         03/10/2008 Included Field boolean_array & Formatted
--                    Values Statement for Use as Reference.
--         12/29/2008 Corrected Box and Path Values.
--         07/21/2010 Updated Contact, Email, Address.
--      
-- danap@dandymadeproductions.com
-- =============================================================

DROP TABLE IF EXISTS array_types;
CREATE TABLE array_types (

--  Table id and creation data entries.
	data_type_id serial NOT NULL,
	
--  Numeric fields.

	smallInt_array smallint[] DEFAULT NULL,
	int_array integer[] DEFAULT '{2,4}',
	bigInt_array bigint[] DEFAULT NULL,
	decimal_array decimal(16,2)[] DEFAULT NULL,
	numeric_array numeric(10,2)[] DEFAULT NULL,
	real_array real[] DEFAULT NULL,
	doublePrecision_array double precision[] DEFAULT NULL,
	
--  Character fields.	
	
	varchar_array varchar(30)[] DEFAULT '{"abc","cba"}',
	char_array char(30)[][] DEFAULT NULL,
	text_array text[] DEFAULT '{"Note: 1st","What is?"}',
	
--  Binary fields.
	
--	bytea_array bytea DEFAULT NULL,
	
--  Date and time fields.

	date_array date[] DEFAULT NULL,
	time_array time[] DEFAULT NULL,
	timeTZ_array timetz[] DEFAULT NULL,
	timestamp_array timestamp[] DEFAULT '{2007-10-14 00:00:00}',
	timestampTZ_array timestamptz[],
	interval_array interval[] DEFAULT NULL,

--  Boolean field.

	boolean_array boolean[] DEFAULT NULL,

--  Geometric fields.

	point_array point[] DEFAULT NULL,
--	line_array line[] DEFAULT NULL,
	lineSegment_array lseg[] DEFAULT NULL,
	box_array box[] DEFAULT NULL,
	path_array path[] DEFAULT NULL,
	polygon_array polygon[] DEFAULT NULL,
	circle_array circle[] DEFAULT NULL,
	
--  Network address fields.
 
	cidr_array cidr[] DEFAULT NULL,
 	inet_array inet[] DEFAULT NULL,
	macaddr_array macaddr[] DEFAULT NULL,
	
--  Bit string fields.

	bit2_array bit(2)[] DEFAULT NULL,
	bitVarying5_array bit varying(5)[] DEFAULT NULL,
	
  	PRIMARY KEY  (data_type_id)
);

INSERT INTO array_types 
    (smallInt_array,int_array,bigInt_array,decimal_array,numeric_array,real_array,     
     doublePrecision_array,varchar_array,char_array,text_array,date_array,time_array,
     timeTZ_array, timestamp_array, timestampTZ_array, interval_array, boolean_array,
     point_array,lineSegment_array, box_array, path_array, polygon_array, circle_array,
     cidr_array,inet_array, macaddr_array, bit2_array, bitVarying5_array) 
VALUES
-- smallint
    ('{1,2,4,7}',
-- integer
     '{1,2,4,7}',
-- bigint
     '{3,4}',
-- decimal
     '{1.1,2.2}',
-- numeric
     '{1.1,2.2}',
-- real
     '{1.1,2.2}',
-- double
     '{1.1,2.2}',
-- varchar
     '{abc,"cba"}',
-- char
     '{{"one","two"},{"three","four"}}',
-- text
     '{"text","text2"}',
-- date 
     '{"2008-02-25"}',
-- time
     '{"12:00:00"}',
-- timetz
     '{"12:00:00"}',
-- timestamp
     '{"2008-02-25 12:00:00", NOW()}',
-- timestamptz
     '{"2008-02-25 12:00:00", NOW()}',
-- interval
     '{"178000000"}',
-- boolean
     '{TRUE,FALSE,t,f}',
-- point
     '{"(0,0)","(5,5)"}',
-- lseg
     '{"((0,0),(10,10))"}',
-- box
    '{"((0,0),(10,10))"}', 
-- path
    '{"(0,0)","(10,10)","(2,2)","(3,3)","(4,4)","(5,7)"}',  
-- polygon
     '{"((0,0),(10,10))"}',
-- circle
     '{"<(0,0),10>"}',
-- cidr
     '{"10.1.2.3/32"}',
-- inet
     '{"10.1.2.0"}',
-- macaddr
     '{"08:00:2b:00:01:02"}',
-- bit(2)
      '{"01",11}',
-- bitvary(5)
      '{"01010",11010}');