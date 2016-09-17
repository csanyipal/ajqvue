-- =============================================================
-- Test table to create the various data types that are
-- defined by the MS Access 97, Jet SQL, database.
-- =============================================================
-- Dana Proctor 
-- Version 06/02/2011 Orignal Test Data Type Table.
--         09/19/2011 Updated to Correctly Implement Requirements.
--         09/20/2011 Added Field Decimal, Fails, But Should be a
--                    Data Type.
--         09/23/2011 Properly Implemented Identifier String Character
--                    for Table Names & Fields.
--         04/22/2012 Added View Table myView.
--      
-- danap@dandymadeproductions.com
-- =============================================================

-- Drop Table MUST be in separate file, import.
-- DROP TABLE `msaccesstypes`;
CREATE TABLE `msaccesstypes` (

--  Table id and creation data entries.

	`data_type_id` COUNTER PRIMARY KEY,

--  Character, text, and blob type fields.

    `binary_type` BINARY,
    `longbinary_type` LONGBINARY,
    `varchar_type` TEXT(50),
    `longText_type` LONGTEXT,

--  Numeric fields.

    `bit_type` BIT,
    `byte_type` BYTE,
    `short_type` SHORT,
    `long_type` LONG,
    `single_type` SINGLE,
    `double_type` DOUBLE,
    -- `decimal_type` DECIMAL,
    `currency_type` CURRENCY,
    `guid_type` GUID,
    
--  Date and time fields.
   
    `dateTime_type` DATETIME
);

--
-- View for msaccesstypes
--

-- DROP TABLE `myView`;
CREATE VIEW `myView` AS SELECT * FROM `msaccesstypes`;