-- ============================================================
--    Test tables that represent a sample of various key
-- combinations.
-- =============================================================
-- Version 12/08/2007 Original hsql_keyTables Database
--                    tables.
--         03/14/2008 key_table2 Quoted to keY_tAble2.
--         06/20/2008 Added View key_table5_View.
--         08/31/2008 Commented View Table.
--         11/28/2008 Added key_table8.
--         07/21/2010 Updated Contact, Email, Address.
--      
-- danap@dandymadeproductions.com
-- =============================================================

--
-- Table structure for table key_table1
--

DROP TABLE IF EXISTS key_table1;
CREATE TABLE key_table1 (
  key_id1 integer NOT NULL,
  key_id2 integer DEFAULT 0 NOT NULL,
  text varchar(255),
  PRIMARY KEY  (key_id1, key_id2)
);

--
-- Table structure for table "keY_tAble2"
--

DROP TABLE IF EXISTS "keY_tAble2";
CREATE TABLE "keY_tAble2" (
  "Host" char(60) DEFAULT '' NOT NULL,
  "Db" char(64) DEFAULT '' NOT NULL,
  "Username" char(16) DEFAULT '' NOT NULL,
  Select_priv boolean DEFAULT TRUE NOT NULL,
  PRIMARY KEY ("Host","Db","Username")
);

--
-- Table structure for table key_table3
--

DROP TABLE IF EXISTS key_table3;
CREATE TABLE key_table3 (
  binary_col binary,
  PRIMARY KEY (binary_col)
);

--
-- Table structure for table key_table4
--

DROP TABLE IF EXISTS key_table4;
CREATE TABLE key_table4 (
  avatar_id integer DEFAULT 0 NOT NULL,
  user_id integer DEFAULT 0 NOT NULL,
  bing_id smallint DEFAULT 0 NOT NULL,
  PRIMARY KEY (avatar_id,user_id)
);

--
-- Table structure for table key_table5
--

DROP TABLE IF EXISTS key_table5;
CREATE TABLE key_table5 (
  name varchar(30) DEFAULT '' NOT NULL,
  color varchar(10) DEFAULT NULL,
  price float DEFAULT NULL,
  UNIQUE (name),
  PRIMARY KEY (color)
);

--
-- Table structure for table key_table6
--

DROP TABLE IF EXISTS key_table6;
CREATE TABLE key_table6 (
  image_id identity NOT NULL,
  name varchar(30) DEFAULT NULL,
  binary_field1 binary,
  binary_field2 binary,
  PRIMARY KEY (image_id)
);

--
-- Table structure for table key_table7
--

DROP TABLE IF EXISTS key_table7;
CREATE TABLE key_table7 (
  text_id longvarchar NOT NULL,
  name varchar(30) DEFAULT NULL,
  binary_field2 binary,
  PRIMARY KEY (text_id)
);

--
-- Table structure for table key_table8
--

DROP TABLE IF EXISTS key_table8;
CREATE TABLE key_table8 (
  date_id DATE NOT NULL,
  name varchar(30) DEFAULT NULL,
  PRIMARY KEY (date_id)
);

--
-- Table structure for tables parent, child
--

DROP TABLE IF EXISTS child;
DROP TABLE IF EXISTS parent;

CREATE TABLE parent (
  id identity,
  name varchar(60) DEFAULT '',
  PRIMARY KEY (id)
);

CREATE TABLE child (
  parent_id INT NOT NULL,
  name varchar(60) DEFAULT '',
  FOREIGN KEY (parent_id) REFERENCES parent(id) ON DELETE CASCADE
);

--
-- View for table5
--

DROP VIEW IF EXISTS "key_table5_View";
CREATE VIEW "key_table5_View" AS SELECT * FROM key_table5;