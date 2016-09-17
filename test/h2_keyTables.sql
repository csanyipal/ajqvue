-- ============================================================
--    Test tables that represent a sample of various key
-- combinations.
-- =============================================================
-- Version 09/05/2013 Original key_tables Database tables.
--
-- danap@dandymadeproductions.com
-- =============================================================

--
-- Table structure for table 'key_table1"
--

DROP TABLE IF EXISTS "key_table1";
CREATE TABLE "key_table1" (
  "key_id1" INT NOT NULL,
  "key_id2" INT NOT NULL DEFAULT 0,
  "text" CLOB(1M),
  PRIMARY KEY ("key_id1", "key_id2")
);

--
-- Table structure for table 'keY_tAble2'
--

DROP TABLE IF EXISTS "keY_tAble2";
CREATE TABLE "keY_tAble2" (
  "Host" CHAR(60) NOT NULL DEFAULT '',
  "Db" CHAR(64) NOT NULL DEFAULT '',
  "User" CHAR(16) NOT NULL DEFAULT '',
  "Select_priv" BOOLEAN NOT NULL DEFAULT true,
  PRIMARY KEY  ("Host","Db","User")
);

--
-- Table structure for table "key_table3'
-- Blob Keys Not Supported.

-- DROP TABLE IF EXISTS "key_table3";
-- CREATE TABLE "key_table3" (
--   "blob_col" BLOB(200K),
--   PRIMARY KEY ("blob_col")
-- );

--
-- Table structure for table 'key_table4'
--

DROP TABLE IF EXISTS "key_table4";
CREATE TABLE "key_table4" (
  "avatar_id" INT NOT NULL DEFAULT 0,
  "user_id" INT NOT NULL DEFAULT 0,
  "bing_id" INT NOT NULL DEFAULT 0,
  PRIMARY KEY ("avatar_id","user_id")
);

--
-- Table structure for table 'key_table5'
--

DROP TABLE IF EXISTS "key_table5";
CREATE TABLE "key_table5" (
  "name" VARCHAR(30) NOT NULL DEFAULT '',
  "color" VARCHAR(10) DEFAULT NULL,
  "price" DOUBLE(6) DEFAULT NULL,
  UNIQUE ("name"),
  PRIMARY KEY ("color")
);

--
-- Table structure for table 'key_table6'
--

DROP TABLE IF EXISTS "key_table6";
CREATE TABLE "key_table6" (
  "image_id" IDENTITY,
  "name" VARCHAR(30) DEFAULT NULL,
  "blob_field1" BLOB(50K),
  "blob_field2" BLOB(1M),
  PRIMARY KEY ("image_id")
);

--
-- Table structure for table 'key_table7'
-- Long Varchar or Clob Can not be indexed.

-- DROP TABLE IF EXISTS "key_table7";
-- CREATE TABLE "key_table7" (
--   "text_id" CLOB NOT NULL,
--   "name" VARCHAR(30) DEFAULT NULL,
--   "blob_field2" BLOB(1M),
--   PRIMARY KEY ("text_id")
-- );

--
-- Table structure for table 'key_table8'
--

DROP TABLE IF EXISTS "key_table8";
CREATE TABLE "key_table8" (
  "date_id" DATE NOT NULL,
  "name" VARCHAR(30) DEFAULT NULL,
  PRIMARY KEY ("date_id")
);

--
-- Table structure for table 'parent', 'child'
--

DROP TABLE IF EXISTS "child";
DROP TABLE IF EXISTS "parent";

CREATE TABLE "parent" (
  "id" INT,
  "name" VARCHAR(60) DEFAULT '',
  PRIMARY KEY ("id")
);


CREATE TABLE "child" (
  "parent_id" INT,
  "name" VARCHAR(60) DEFAULT '',
  FOREIGN KEY ("parent_id") REFERENCES "parent"("id") ON DELETE CASCADE
);

--
-- View for table5
--

-- DROP VIEW "key_table5_View";
-- CREATE VIEW "key_table5_View" AS SELECT * FROM "key_table5";