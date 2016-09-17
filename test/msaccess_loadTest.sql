-- ============================================================
--   Test table to create tables with all data type fields
-- support by MSAccess to test the loading of TableTabPanel
-- summary table, (JTable), TableViewForm, and TableEntryForm. 
-- =============================================================
-- Version 09/22/2011 Orignal Load Test SQL configuration for
--                    Tables of MSAccess data types.
--         09/23/2011 Properly Implemented Identifier String Character
--                    for Table Names & Fields.
--      
-- danap@dandymadeproductions.com
-- =============================================================

-- #############################################################
--
-- DROP TABLE SECTION
--
-- #############################################################

-- blob types

-- DROP TABLE `binary_types`;
-- DROP TABLE `longbinary_types`;

-- text types

-- DROP TABLE `varchar_types`;
-- DROP TABLE `longText_types`;

-- boolean, bit types

-- DROP TABLE `bit_types`;

-- numeric types

-- DROP TABLE `byte_types`;
-- DROP TABLE `short_types`;
-- DROP TABLE `long_types`;
-- DROP TABLE `single_types`;
-- DROP TABLE `double_types`;
-- DROP TABLE `decimal_types`;
-- DROP TABLE `currency_types`;
-- DROP TABLE `guid_types`;

--  Date and time fields.
   
-- DROP TABLE `dateTime_types`;

-- #############################################################
-- 
-- CREATE TABLE SECTION
--
-- #############################################################

CREATE TABLE `binary_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `binary_type` BINARY
);

CREATE TABLE `longbinary_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `longbinary_type` LONGBINARY
);

CREATE TABLE `varchar_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `varchar_type` TEXT(50)
);

CREATE TABLE `longText_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `longText_type` LONGTEXT
);

CREATE TABLE `bit_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `bit_type` BIT
);

CREATE TABLE `byte_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `byte_type` BYTE
);

CREATE TABLE `short_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `short_type` SHORT
);

CREATE TABLE `long_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `long_type` LONG
);
    
CREATE TABLE `single_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `single_type` SINGLE
);

CREATE TABLE `double_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `double_type` DOUBLE
);

-- CREATE TABLE `decimal_types` (

--  Table id and creation data entries.
--     `data_type_id` COUNTER PRIMARY KEY,
--     `decimal_type` decimal
-- );

CREATE TABLE `currency_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `currency_type` CURRENCY
);

CREATE TABLE `guid_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `guid_type` GUID
);

CREATE TABLE `dateTime_types` (

--  Table id and creation data entries.
    `data_type_id` COUNTER PRIMARY KEY,
    `dateTime_type` DATETIME
);

-- #############################################################
--
-- INSERT INTO TABLE SECTION
--
-- #############################################################

INSERT INTO `binary_types` (`binary_type`) VALUES (0x44449292929292923535);

INSERT INTO `longbinary_types` (`longbinary_type`) VALUES (0x44449292929292923535);

INSERT INTO `varchar_types` (`varchar_type`) VALUES ('a');
INSERT INTO `varchar_types` (`varchar_type`) VALUES ('ccaaaaaaaaaaaaaaaaaaaaaaaaaaa');

INSERT INTO `longtext_types` (`longtext_type`) VALUES ('text1');

INSERT INTO `bit_types` (`bit_type`) VALUES ('1');
INSERT INTO `bit_types` (`bit_type`) VALUES ('0');

INSERT INTO `byte_types` (`byte_type`) VALUES (0);
INSERT INTO `byte_types` (`byte_type`) VALUES (127);

INSERT INTO `short_types` (`short_type`) VALUES (-32768);
INSERT INTO `short_types` (`short_type`) VALUES (32767);

INSERT INTO `long_types` (`long_type`) VALUES (-2147483648);
INSERT INTO `long_types` (`long_type`) VALUES (2147483647);

INSERT INTO `single_types` (`single_type`) VALUES (-3.402823E38);
INSERT INTO `single_types` (`single_type`) VALUES (3.402823E38);

INSERT INTO `double_types` (`double_type`) VALUES (-1.79769313486232E307);
INSERT INTO `double_types` (`double_type`) VALUES (1.79769313486232E307);
    
-- INSERT INTO `decimal_types` (`decimal_type`) VALUES (?);
-- INSERT INTO `decimal_types` (`decimal_type`) VALUES (?);

INSERT INTO `currency_types` (`currency_type`) VALUES (-922337203685477.5808);
INSERT INTO `currency_types` (`currency_type`) VALUES (922337203685477.5807);
    
-- INSERT INTO `guid_types` (`guid_type`) VALUES (?);
-- INSERT INTO `guid_types` (`guid_type`) VALUES (?);

INSERT INTO `dateTime_types` (`dateTime_type`) VALUES ('100-01-01 00:00:00');
INSERT INTO `dateTime_types` (`dateTime_type`) VALUES ('9999-12-31 23:59:59');