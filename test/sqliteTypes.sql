-- =============================================================
-- Test table to create the various data types that are
-- defined by the SQLite database.
-- =============================================================
-- Dana Proctor 
-- Version 07/21/2010 Original Test sqlitetypes Table.
--         07/23/2010 Narrowed to Just Four Types.
--         04/23/2012 Added View Tables myView.
--         08/17/2017 Added Auto Increment for Primary Key data_type_id.
--         05/15/2018 Changed Table Name sqlitetypes, to datatypes. Appears
--                    to be JDBC or SQLite Conflict With the Use of the Name,
--                    Key Word?
--
-- danap@dandymadeproductions.com
-- =============================================================

   DROP TABLE IF EXISTS datatypes;
   CREATE TABLE datatypes (

-- Table id and creation data entries.

   data_type_id INTEGER PRIMARY KEY AUTOINCREMENT,

-- Integer, real, text, and blob type fields.

   int_type INTEGER,
   real_type REAL,
   text_type TEXT,
   blob_type BLOB
);

--
-- View for datatypes
--

DROP VIEW IF EXISTS myView;
CREATE VIEW myView AS SELECT * FROM datatypes;