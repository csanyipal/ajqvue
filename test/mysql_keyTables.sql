-- ============================================================
--    Test tables that represent a sample of various key
-- combinations, MySQL & MariaDB.
-- =============================================================
-- Version 05/21/2007 Original key_tables Database tables.
--         06/05/2007 key_table6 & 7 Added. Generated from
--                    Ajqvue.
--         06/18/2007 key_table7 text_id, key, NOT NULL.
--                    Removed parent & child tables.
--         10/27/2007 Changed Comments Indicator # to --.
--                    Also renamed to mysql_keyTables.sql.
--         11/05/2007 Changed key_table7's key to type MEDIUMTEXT
--                    To Properly Test.
--         12/19/2007 Added Tables parent & child.
--         03/14/2008 key_table2 Quoted to keY_tAble2
--         06/20/2008 Added View key_table5_View.
--         08/31/2008 Commented View Table.
--         10/18/2008 Created Table for Testing Date Key Fields.
--         11/28/2008 Reformat of Key Table Order.
--         07/21/2010 Updated Contact, Email, Address.
--         06/08/2017 Removed Engine and Charaset Definitions. Also
--                    Clarified the Use of Primary Keys Only, 
--      
-- danap@dandymadeproductions.com
-- =============================================================

--
-- Table structure for table 'key_table1'
--

DROP TABLE IF EXISTS `key_table1`;
CREATE TABLE `key_table1` (
  `key_id1` int(10) unsigned NOT NULL auto_increment,
  `key_id2` int(10) unsigned NOT NULL default '0',
  `text` tinytext,
  PRIMARY KEY  (`key_id1`,`key_id2`)
);

--
-- Table structure for table 'keY_tAble2'
--

DROP TABLE IF EXISTS `keY_tAble2`;
CREATE TABLE `keY_tAble2` (
  `Host` char(60) character set latin1 collate latin1_bin NOT NULL default '',
  `Db` char(64) character set latin1 collate latin1_bin NOT NULL default '',
  `User` char(16) character set latin1 collate latin1_bin NOT NULL default '',
  `Select_priv` enum('N','Y') NOT NULL default 'N',
  PRIMARY KEY  (`Host`,`Db`,`User`),
);

--
-- Table structure for table 'key_table3'
--

DROP TABLE IF EXISTS `key_table3`;
CREATE TABLE `key_table3` (
  `blob_col` blob,
  PRIMARY KEY `blob_col` (`blob_col`(10))
);

--
-- Table structure for table 'key_table4'
--

DROP TABLE IF EXISTS `key_table4`;
CREATE TABLE `key_table4` (
  `avatar_id` mediumint(8) unsigned NOT NULL default '0',
  `user_id` mediumint(8) unsigned NOT NULL default '0',
  `bing_id` mediumint(8) unsigned NOT NULL default '0',
  PRIMARY KEY `avatar_user_id` (`avatar_id`,`user_id`)
);

--
-- Table structure for table 'key_table5'
--

DROP TABLE IF EXISTS `key_table5`;
CREATE TABLE `key_table5` (
  `name` varchar(30) NOT NULL default '',
  `color` varchar(10) default NULL,
  `price` float(6,2) default NULL,
  UNIQUE KEY `name` (`name`),
  PRIMARY KEY `color` (`color`)
);

--
-- Table structure for table 'key_table6'
--

DROP TABLE IF EXISTS `key_table6`;
CREATE TABLE `key_table6` (
  `image_id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(30) default NULL,
  `blob_field1` blob,
  `blob_field2` blob,
  PRIMARY KEY  (`image_id`)
);

--
-- Table structure for table 'key_table7'
--

DROP TABLE IF EXISTS `key_table7`;
CREATE TABLE `key_table7` (
  `text_id` mediumtext NOT NULL,
  `name` varchar(30) default NULL,
  `blob_field2` blob,
  PRIMARY KEY `text_id` (`text_id`(20))
);

--
-- Table structure for table 'key_table8'
--

DROP TABLE IF EXISTS `key_table8`;
CREATE TABLE `key_table8` (
  `date_id` DATE NOT NULL,
  `name` varchar(30) default NULL,
  PRIMARY KEY (`date_id`)
);

--
-- Table structure for table 'parent', 'child'
--

DROP TABLE IF EXISTS `child`;
DROP TABLE IF EXISTS `parent`;

CREATE TABLE `parent` (
  `id` int unsigned,
  `name` varchar(60) default '',
  PRIMARY KEY (`id`)
);


CREATE TABLE `child` (
  `parent_id` int unsigned,
  `name` varchar(60) default '',
  FOREIGN KEY (`parent_id`) REFERENCES parent(id) ON DELETE CASCADE
);

--
-- View for table5
--

DROP VIEW IF EXISTS `key_table5_View`;
CREATE VIEW `key_table5_View` AS SELECT * FROM `key_table5`;

