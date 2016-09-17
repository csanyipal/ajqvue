-- ============================================================
--    Test tables that represent a sample of various key
-- combinations.
-- =============================================================
-- Version 12/08/2007 Original postgresql_keyTables Database
--                    tables.
--         12/19/2007 Added Tables parent & child.
--         12/22/2007 Changed key_table2 Fields host to Host,
--                    db to Db, and username to UserName.
--         12/23/2007 Changed Default for parent_id in child
--                    Table to NOT be NULL.
--         03/14/2008 key_table2 Quoted to keY_tAble2
--         06/20/2008 Added View key_table5_View.
--         08/31/2008 Commented View Table.
--         11/28/2008 added key_table8.
--         11/08/2009 Corrected key_table8 Create Statement.
--         07/21/2010 Updated Contact, Email, Address.
--      
-- danap@dandymadeproductions.com
-- =============================================================

--
-- Table structure for table key_table1
--

DROP TABLE IF EXISTS key_table1;
CREATE TABLE key_table1 (
  key_id1 serial NOT NULL,
  key_id2 integer NOT NULL default 0,
  text varchar(255),
  PRIMARY KEY  (key_id1,key_id2)
);

--
-- Table structure for table key_table2
--

DROP TABLE IF EXISTS "keY_tAble2";
CREATE TABLE "keY_tAble2" (
  "Host" char(60) NOT NULL default '',
  "Db" char(64) NOT NULL default '',
  "Username" char(16) NOT NULL default '',
  Select_priv boolean NOT NULL default TRUE,
  PRIMARY KEY ("Host","Db","Username")
);

--
-- Table structure for table key_table3
--

DROP TABLE IF EXISTS key_table3;
CREATE TABLE key_table3 (
  bytea_col bytea,
  PRIMARY KEY (bytea_col)
);

--
-- Table structure for table key_table4
--

DROP TABLE IF EXISTS key_table4;
CREATE TABLE key_table4 (
  avatar_id integer NOT NULL default 0,
  user_id integer NOT NULL default 0,
  bing_id smallint NOT NULL default 0,
  PRIMARY KEY (avatar_id,user_id)
);

--
-- Table structure for table key_table5
--

DROP TABLE IF EXISTS key_table5;
CREATE TABLE key_table5 (
  name varchar(30) NOT NULL default '',
  color varchar(10) default NULL,
  price float(6) default NULL,
  UNIQUE (name),
  PRIMARY KEY (color)
);

--
-- Table structure for table key_table6
--

DROP TABLE IF EXISTS key_table6;
CREATE TABLE key_table6 (
  image_id serial NOT NULL,
  name varchar(30) default NULL,
  bytea_field1 bytea,
  bytea_field2 bytea,
  PRIMARY KEY (image_id)
);

--
-- Table structure for table key_table7
--

DROP TABLE IF EXISTS key_table7;
CREATE TABLE key_table7 (
  text_id text NOT NULL,
  name varchar(30) default NULL,
  bytea_field2 bytea,
  PRIMARY KEY (text_id)
);

--
-- Table structure for table key_table8
--

DROP TABLE IF EXISTS key_table8;
CREATE TABLE key_table8 (
  date_id date NOT NULL,
  name varchar(30) default NULL,
  PRIMARY KEY (date_id)
);

--
-- Table structure for tables parent, child
--

DROP TABLE IF EXISTS child;
DROP TABLE IF EXISTS parent;

CREATE TABLE parent (
  id SERIAL,
  name varchar(60) default '',
  PRIMARY KEY (id)
);

CREATE TABLE child (
  parent_id INT NOT NULL,
  name varchar(60) default '',
  FOREIGN KEY (parent_id) REFERENCES parent(id) ON DELETE CASCADE
);

--
-- View for table5
--

DROP VIEW IF EXISTS "key_table5_View";
CREATE VIEW "key_table5_View" AS SELECT * FROM key_table5;