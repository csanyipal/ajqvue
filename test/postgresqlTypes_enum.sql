-- =============================================================
-- Test table to create the ENUM data type for the postgreSQL
--  database.
-- =============================================================
-- Dana Proctor 
-- Version 11/26/2015 Initial Test ENUM Data Type Table.
--      
-- danap@dandymadeproductions.com
-- =============================================================

DROP TYPE IF EXISTS mood;
CREATE TYPE mood AS ENUM ('sad', 'ok', 'happy');

DROP TABLE IF EXISTS enum_types;
CREATE TABLE enum_types (

--  Table id and creation data entries.
	data_type_id serial NOT NULL,
	
--  ENUM fields.

	enum_type mood DEFAULT 'ok',
	
  	PRIMARY KEY  (data_type_id)
);