-- =============================================================
-- Test table to create the various data types that are
-- defined by the SQLite database.
-- =============================================================
-- Dana Proctor 
-- Version 07/21/2010 Original Test sqlitetypes Table.
--         07/23/2010 Narrowed to Just Four Types.
--         04/23/2012 Added View Tables myView.
--
-- danap@dandymadeproductions.com
-- =============================================================

   DROP TABLE IF EXISTS sqlitetypes;
   CREATE TABLE sqlitetypes (

-- Table id and creation data entries.

   data_type_id INTEGER PRIMARY KEY,

-- Integer, real, text, and blob type fields.

   int_type INTEGER,
   real_type REAL,
   text_type TEXT,
   blob_type BLOB
);

--
-- View for sqlitetypes
--

DROP VIEW IF EXISTS myView;
CREATE VIEW myView AS SELECT * FROM sqlitetypes;