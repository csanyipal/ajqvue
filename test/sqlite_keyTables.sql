-- ============================================================
--    Test tables that represent a sample of various key
-- combinations.
-- =============================================================
-- Version 07/26/2010 Original key_tables Database tables for
--                    SQLite Database, sqliteKeyTables_test.db.
--         07/16/2018 Commented key_table3, Do Not Test for Blob Keys.
--      
-- danap@dandymadeproductions.com
-- =============================================================

--
-- Table structure for table key_table1
--

DROP TABLE IF EXISTS key_table1;
CREATE TABLE key_table1 (
  key_id1 INTEGER NOT NULL,
  key_id2 INTEGER NOT NULL DEFAULT 0,
  text TEXT,
  PRIMARY KEY  (key_id1,key_id2)
);

--
-- Table structure for table keY_tAble2
--

DROP TABLE IF EXISTS keY_tAble2;
CREATE TABLE keY_tAble2 (
  Host TEXT NOT NULL default '',
  Db TEXT NOT NULL default '',
  User TEXT NOT NULL default '',
  Select_priv TEXT NOT NULL default 'N',
  PRIMARY KEY  (Host,Db,User)
);

--
-- Table structure for table key_table3
--

-- DROP TABLE IF EXISTS key_table3;
-- CREATE TABLE key_table3 (
--  blob_col BLOB,
--  PRIMARY KEY (blob_col)
--);

--
-- Table structure for table key_table4
--

DROP TABLE IF EXISTS key_table4;
CREATE TABLE key_table4 (
  avatar_id INTEGER NOT NULL default 0,
  user_id INTEGER NOT NULL default 0,
  bing_id INTEGER NOT NULL default 0,
  PRIMARY KEY (avatar_id,user_id)
);

--
-- Table structure for table key_table5
--

DROP TABLE IF EXISTS key_table5;
CREATE TABLE key_table5 (
  name TEXT NOT NULL default '',
  color TEXT default NULL,
  price REAL default NULL,
  UNIQUE (name),
  PRIMARY KEY (color)
);

--
-- Table structure for table key_table6
--

DROP TABLE IF EXISTS key_table6;
CREATE TABLE key_table6 (
  image_id INTEGER NOT NULL,
  name TEXT default NULL,
  blob_field1 BLOB,
  blob_field2 BLOB,
  PRIMARY KEY (image_id)
);

--
-- Table structure for table key_table7
--

DROP TABLE IF EXISTS key_table7;
CREATE TABLE key_table7 (
  text_id TEXT NOT NULL,
  name TEXT default NULL,
  blob_field2 BLOB,
  PRIMARY KEY (text_id)
);

--
-- Table structure for table key_table8
--

DROP TABLE IF EXISTS key_table8;
CREATE TABLE key_table8 (
  date_id DATE NOT NULL,
  name TEXT default NULL,
  PRIMARY KEY (date_id)
);

--
-- Table structure for table parent, child
--

DROP TABLE IF EXISTS child;
DROP TABLE IF EXISTS parent;

CREATE TABLE parent (
  id INTEGER,
  name TEXT default '',
  PRIMARY KEY (id)
);

CREATE TABLE child (
  parent_id INTEGER,
  name TEXT default '',
  FOREIGN KEY (parent_id) REFERENCES parent(id) ON DELETE CASCADE
);

--
-- View for table5
--

DROP VIEW IF EXISTS key_table5_View;
CREATE VIEW key_table5_View AS SELECT * FROM key_table5;
