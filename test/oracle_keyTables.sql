-- ============================================================
--    Test tables that represent a sample of various key
-- combinations.
-- =============================================================
-- Version 08/31/2008 Original oracle_keyTables Database
--                    tables.
--         11/28/2008 Added key_table8.
--         07/21/2010 Updated Contact, Email, Address.
--         03/04/2015 Updated to Correct Issues With Sequences on One
--                    Line, & Other Comments Problems With 11g.
--         03/09/2015 keY_tAble2 Changed Field Username to User to Conform
--                    With Other Database Test Key Tables.
--      
-- danap@dandymadeproductions.com
-- =============================================================

-- Note you may have to remove the DROP SEQUENCE/TABLE statement
-- if it is not used if using Ajqvue for the import. Oracle
-- does not appear to allow a commented SQL to be executed through
-- the JDBC. You may also just remove the semicolon from the end of
-- the statement after commenting, Ajqvue will then not treat
-- the commented line a SQL statement. Parses on ";/n".

--
-- Table structure for table key_table1
--

DROP SEQUENCE key_table1_key_id1_seq;
CREATE SEQUENCE key_table1_key_id1_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;

DROP TABLE key_table1;
CREATE TABLE key_table1 (
  key_id1 integer NOT NULL,
  key_id2 integer default 0 NOT NULL,
  text varchar2(255)
);
ALTER TABLE key_table1 ADD CONSTRAINT key_table1_key_id1_seq PRIMARY KEY (key_id1, key_id2);

--
-- Table structure for table key_table2
--

DROP TABLE "keY_tAble2";
CREATE TABLE "keY_tAble2" (
  "Host" char(60) default '' NOT NULL,
  "Db" char(64) default '' NOT NULL,
  "User" char(16) default '' NOT NULL,
  Select_priv number default 1 NOT NULL,
  PRIMARY KEY ("Host","Db","User")
);

--
-- Table structure for table key_table3
--

-- DROP TABLE key_table3
-- CREATE TABLE key_table3 (
--  blob_col blob,
--  PRIMARY KEY (blob_col)
-- )

--
-- Table structure for table key_table4
--

DROP TABLE key_table4;
CREATE TABLE key_table4 (
  avatar_id integer default 0 NOT NULL,
  user_id integer default 0 NOT NULL,
  bing_id smallint default 0 NOT NULL,
  PRIMARY KEY (avatar_id,user_id)
);

--
-- Table structure for table key_table5
--

DROP TABLE key_table5;
CREATE TABLE key_table5 (
  name varchar(30) default '' NOT NULL,
  color varchar(10) default NULL,
  price float(6) default NULL,
  UNIQUE (name),
  PRIMARY KEY (color)
);

--
-- Table structure for table key_table6
--

DROP SEQUENCE key_table6_image_id_seq;
CREATE SEQUENCE key_table6_image_id_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;

DROP TABLE key_table6;
CREATE TABLE key_table6 (
  image_id number NOT NULL,
  name varchar(30) default NULL,
  blob_field1 blob,
  blob_field2 blob
);
ALTER TABLE key_table6 ADD CONSTRAINT key_table6_image_id_seq PRIMARY KEY (image_id);

--
-- Table structure for table key_table7
--

DROP TABLE key_table7;
CREATE TABLE key_table7 (
  text_id nvarchar2(255) NOT NULL,
  name varchar(30) default NULL,
  raw_field2 raw(2000),
  PRIMARY KEY (text_id)
);

--
-- Table structure for table key_table8
--

DROP TABLE key_table8;
CREATE TABLE key_table8 (
  date_id DATE,
  name varchar(30) default NULL,
  PRIMARY KEY (date_id)
);

--
-- Table structure for tables parent, child
--

DROP TABLE child;
DROP TABLE parent;

DROP SEQUENCE parent_id_seq;
CREATE SEQUENCE parent_id_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;

CREATE TABLE parent (
  id number,
  name varchar(60) default ''
);
ALTER TABLE parent ADD CONSTRAINT parent_id_seq PRIMARY KEY (id);

CREATE TABLE child (
  parent_id INT NOT NULL,
  name varchar(60) default '',
  FOREIGN KEY (parent_id) REFERENCES parent(id) ON DELETE CASCADE
);

--
-- View for table5
--

CREATE OR REPLACE VIEW "key_table5_View" AS SELECT * FROM key_table5;
