-- ============================================================
--    Test tables that represent a sample of various key
-- combinations.
-- =============================================================
-- Version 09/19/2011 Original key_tables Database tables for MSAccess.
--         09/23/2011 Properly Implemented Identifier String Character
--                    for Table Names & Fields. Change in key_table7
--                    key to TEXT(255).
--      
-- danap@dandymadeproductions.com
-- =============================================================

-- MSAccess does not seem to support via DDL the ability to set
-- default values, so eventhough this script creates the basic
-- set of tables for testing defaults must be set via one of the
-- designer tools.
--
-- Table structure for table `key_table1`
--

-- DROP TABLE `key_table1`;
CREATE TABLE `key_table1` (
  `key_id1` COUNTER,
  `key_id2` INTEGER NOT NULL,
  `name` TEXT(50),
  PRIMARY KEY (`key_id1`,`key_id2`)
  );
-- ALTER TABLE `key_table1` ALTER `key_id2` SET DEFAULT 0;

--
-- Table structure for table `keY_tAble2`
--

-- DROP TABLE `keY_tAble2`;
CREATE TABLE `keY_tAble2` (
  `Host` TEXT(60) NOT NULL,
  `Db` TEXT(64) NOT NULL,
  `User` TEXT(16) NOT NULL,
  `Select_priv` BIT NOT NULL,
  PRIMARY KEY  (`Host`,`Db`,`User`)
);
-- ALTER TABLE `keY_tAble2` ALTER `Host` SET DEFAULT '';
-- ALTER TABLE `keY_tAble2` ALTER `Db` SET DEFAULT '';
-- ALTER TABLE `keY_tAble2` ALTER `User` SET DEFAULT '';
-- ALTER TABLE `keY_tAble2` ALTER `Select_priv` SET DEFAULT '0';

--
-- Table structure for table `key_table3`
--

-- DROP TABLE `key_table3`;
--CREATE TABLE `key_table3` (
--  `blob_col` LONGBINARY,
--  PRIMARY KEY (`blob_col`)
--);

--
-- Table structure for table key_table4
--

-- DROP TABLE `key_table4`;
CREATE TABLE `key_table4` (
  `avatar_id` SHORT,
  `user_id` SHORT,
  `bing_id` SHORT,
  CONSTRAINT `avatar_user_id` UNIQUE (`avatar_id`,`user_id`)
);
-- ALTER TABLE `key_table4` ALTER `avatar_id` SET DEFAULT 0;
-- ALTER TABLE `key_table4` ALTER `user_id` SET DEFAULT 0;
-- ALTER TABLE `key_table4` ALTER `bing_id` SET DEFAULT 0;


--
-- Table structure for table `key_table5`
--

-- DROP TABLE `key_table5`;
CREATE TABLE `key_table5` (
  `name` TEXT(30) NOT NULL UNIQUE,
  `color` TEXT(10) PRIMARY KEY,
  `price` FLOAT 
);
-- ALTER TABLE `key_table5` ALTER `name` SET DEFAULT '';

--
-- Table structure for table `key_table6`
--

-- DROP TABLE `key_table6`;
CREATE TABLE `key_table6` (
  `image_id` COUNTER,
  `name` TEXT(30),
  `blob_field1` LONGBINARY,
  `blob_field2` LONGBINARY,
  PRIMARY KEY (`image_id`)
);


--
-- Table structure for table `key_table7`
--

-- DROP TABLE `key_table7`;
CREATE TABLE `key_table7` (
--  `text_id` LONGTEXT NOT NULL,
  `text_id` TEXT(255) NOT NULL,
  `name` TEXT(30),
  `blob_field2` LONGBINARY,
  PRIMARY KEY (`text_id`)
);

--
-- Table structure for table `key_table8`
--

-- DROP TABLE `key_table8`;
CREATE TABLE `key_table8` (
  `date_id` DATETIME NOT NULL,
  `name` TEXT(30),
  PRIMARY KEY (`date_id`)
);

--
-- Table structure for table `parent`, `child`
--

-- DROP TABLE `child`;
-- DROP TABLE `parent`;

CREATE TABLE `parent` (
  `id` LONG,
  `name` TEXT(60),
  PRIMARY KEY (`id`)
);
-- ALTER TABLE `parent` ALTER `name` SET DEFAULT '';

CREATE TABLE `child` (
  `parent_id` LONG,
  `name` TEXT(60),
  FOREIGN KEY (`parent_id`) REFERENCES `parent`(`id`)
);
-- ALTER TABLE `parent` ALTER `name` SET DEFAULT '';

--
-- View for table5
--

-- DROP TABLE `key_table5_View`;
CREATE VIEW `key_table5_View` AS SELECT * FROM `key_table5`;

