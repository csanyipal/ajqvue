//=================================================================
//                          TypesInfoCache
//=================================================================
//
//    This class provides a storage cache for characterizing data
// types information for the various support databases.
//
//                     << TypesInfoCache.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.6 08/02/2018
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version
// 2 of the License, or (at your option) any later version. This
// program is distributed in the hope that it will be useful, 
// but WITHOUT ANY WARRANTY; without even the implied warranty
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
// the GNU General Public License for more details. You should
// have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation,
// Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// (http://opensource.org)
//
//=================================================================
// Revision History
// Changes to the code should be documented here and reflected
// in the present version number. Author information should
// also be included with the original copyright author.
//=================================================================
// Version 1.0 09/17/2016 Production TypesInfoCache Class.
//         1.1 05/07/2018 Changed SQLITE_TEXT Conversion for Derby to DERBY_CLOB.
//         1.2 05/14/2018 HSQL_BIT Type Changed H2, Derby, & SQLite Types to Varchar,
//                        & Text to Indicate HSQL Bit Can Have Multiple Bits, Bit(x).
//                        HSQL_BIT_VARYING Changed Derby Type to Varchar. Since is
//                        Multi-Bit String, Not Binary, Hex. DERBY_CHAR_FOR_BIT_DATA
//                        Changed HSQL to Binary, to Match SQL Type Binary.
//         1.3 05/16/2018 H2_ARRAY Changed SQLITE_NONE to SQLITE_TEXT.
//         1.4 05/25/2018 Method getType() Added Argument sourceSQLType, & Used As
//                        an Alternative to Try & Derive Return for UNSPECIFIED.
//         1.5 07/10/2018 SQLITE_TYPES Added SQLITE_NULL.
//         1.6 08/02/2018 Method getType() Changed if nameToType Does Not Contain
//                        a Key, Type Lookup, Then for SQLite Set Type Returned
//                        From NONE, to NUMERIC. SQLite 3 No Longer Supports NONE,
//                        Now BLOB, But NUMERIC Allows Any Affinity.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.datasource;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 *    The TypesInfoCache class provides a storage cache for characterizing
 * data types information for the various support databases.
 * 
 * @author Dana M. Proctor
 * @version 1.6 08/02/2018
 */

public class TypesInfoCache
{
   // Class Instances
   private String dataSourceType, dataSinkType;
   private Map<String, Integer> nameToType;
   
   // Basic types infomation:
   // 0 - Source type id
   // 1 - H2 Sink type id
   // 2 - HSQL Sink type id
   // 3 - Derby Sink type id
   // 4 - SQLite Sink type id
   
   private static final int TYPE_NAME = 0;
   private static final int H2_TYPE = 1;
   private static final int HSQL_TYPE = 2;
   private static final int DERBY_TYPE = 3;
   private static final int SQLITE_TYPE = 4;
   
   private static final String DEFAULT_DATASINK_TYPE = ConnectionManager.HSQL2; 
   
   // Conversion Instances
   
   private static final int[][] H2_TYPES = {
       {TypeID.H2_IDENTITY, TypeID.H2_IDENTITY, TypeID.HSQL_IDENTITY, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.H2_CHAR, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.H2_VARCHAR, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.H2_VARCHAR_IGNORECASE, TypeID.H2_VARCHAR_IGNORECASE, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.H2_BINARY, TypeID.H2_BINARY, TypeID.HSQL_VARBINARY, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.H2_BLOB, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.H2_CLOB, TypeID.H2_CLOB, TypeID.HSQL_CLOB, TypeID.DERBY_CLOB, TypeID.SQLITE_TEXT},
       {TypeID.H2_OTHER, TypeID.H2_OTHER, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_NONE},
       {TypeID.H2_UUID, TypeID.H2_UUID, TypeID.HSQL_BINARY, TypeID.DERBY_BLOB, TypeID.SQLITE_NONE},
       {TypeID.H2_BOOLEAN, TypeID.H2_BOOLEAN, TypeID.HSQL_BOOLEAN, TypeID.DERBY_BOOLEAN, TypeID.SQLITE_NUMERIC},
       {TypeID.H2_TINYINT, TypeID.H2_TINYINT, TypeID.HSQL_TINYINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.H2_SMALLINT, TypeID.H2_SMALLINT, TypeID.HSQL_SMALLINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.H2_INTEGER, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.H2_BIGINT, TypeID.H2_BIGINT, TypeID.HSQL_BIGINT, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.H2_REAL, TypeID.H2_REAL, TypeID.HSQL_REAL, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.H2_DOUBLE, TypeID.H2_DOUBLE, TypeID.HSQL_DOUBLE, TypeID.DERBY_DOUBLE, TypeID.SQLITE_REAL},
       {TypeID.H2_DECIMAL, TypeID.H2_DECIMAL, TypeID.HSQL_NUMERIC, TypeID.DERBY_DECIMAL, TypeID.SQLITE_NUMERIC},
       {TypeID.H2_DATE, TypeID.H2_DATE, TypeID.HSQL_DATE, TypeID.DERBY_DATE, TypeID.SQLITE_TEXT},
       {TypeID.H2_TIME, TypeID.H2_TIME, TypeID.HSQL_TIME, TypeID.DERBY_TIME, TypeID.SQLITE_TEXT},
       {TypeID.H2_TIMESTAMP, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.H2_ARRAY, TypeID.H2_ARRAY, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT}};
    
   // HSQL2
   private static final int[][] HSQL_TYPES = {
       {TypeID.HSQL_IDENTITY, TypeID.H2_IDENTITY, TypeID.HSQL_IDENTITY, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.HSQL_CHARACTER, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_VARCHAR, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_LONGVARCHAR, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_CLOB, TypeID.H2_CLOB, TypeID.HSQL_CLOB, TypeID.DERBY_CLOB, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_BINARY, TypeID.H2_BINARY, TypeID.HSQL_BINARY, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.HSQL_VARBINARY, TypeID.H2_BLOB, TypeID.HSQL_VARBINARY, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.HSQL_LONGVARBINARY, TypeID.H2_BLOB, TypeID.HSQL_LONGVARBINARY, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.HSQL_BLOB, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.HSQL_TINYINT, TypeID.H2_TINYINT, TypeID.HSQL_TINYINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.HSQL_SMALLINT, TypeID.H2_SMALLINT, TypeID.HSQL_SMALLINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.HSQL_INTEGER, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.HSQL_BIGINT, TypeID.H2_BIGINT, TypeID.HSQL_BIGINT, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.HSQL_FLOAT, TypeID.H2_REAL, TypeID.HSQL_FLOAT, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.HSQL_DOUBLE, TypeID.H2_DOUBLE, TypeID.HSQL_DOUBLE, TypeID.DERBY_DOUBLE, TypeID.SQLITE_REAL},
       {TypeID.HSQL_REAL, TypeID.H2_REAL, TypeID.HSQL_REAL, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.HSQL_DECIMAL, TypeID.H2_DECIMAL, TypeID.HSQL_DECIMAL, TypeID.DERBY_DECIMAL, TypeID.SQLITE_NUMERIC},
       {TypeID.HSQL_NUMERIC, TypeID.H2_DECIMAL, TypeID.HSQL_NUMERIC, TypeID.DERBY_DECIMAL, TypeID.SQLITE_NUMERIC},
       {TypeID.HSQL_BOOLEAN, TypeID.H2_BOOLEAN, TypeID.HSQL_BOOLEAN, TypeID.DERBY_BOOLEAN, TypeID.SQLITE_NUMERIC},
       {TypeID.HSQL_BIT, TypeID.H2_VARCHAR, TypeID.HSQL_BIT, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_BIT_VARYING, TypeID.H2_VARCHAR, TypeID.HSQL_BIT_VARYING, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_DATE, TypeID.H2_DATE, TypeID.HSQL_DATE, TypeID.DERBY_DATE, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_TIME, TypeID.H2_TIME, TypeID.HSQL_TIME, TypeID.DERBY_TIME, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_TIME_WITH_TIME_ZONE, TypeID.H2_TIME, TypeID.HSQL_TIME_WITH_TIME_ZONE, TypeID.DERBY_TIME, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_DATETIME, TypeID.H2_TIMESTAMP, TypeID.HSQL_DATETIME, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_TIMESTAMP, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_TIMESTAMP_WITH_TIME_ZONE, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP_WITH_TIME_ZONE, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.HSQL_INTERVAL, TypeID.H2_VARCHAR, TypeID.HSQL_INTERVAL, TypeID.DERBY_VARCHAR, TypeID.SQLITE_NONE},
       {TypeID.HSQL_INTERVAL_YEAR_TO_MONTH, TypeID.H2_VARCHAR, TypeID.HSQL_INTERVAL_YEAR_TO_MONTH, TypeID.DERBY_VARCHAR, TypeID.SQLITE_NONE},
       {TypeID.HSQL_INTERVAL_YEAR, TypeID.H2_VARCHAR, TypeID.HSQL_INTERVAL_YEAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_NONE},
       {TypeID.HSQL_INTERVAL_DAY_TO_HOUR, TypeID.H2_VARCHAR, TypeID.HSQL_INTERVAL_DAY_TO_HOUR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_NONE},
       {TypeID.HSQL_INTERVAL_MINUTE_TO_SECOND, TypeID.H2_VARCHAR, TypeID.HSQL_INTERVAL_MINUTE_TO_SECOND, TypeID.DERBY_VARCHAR, TypeID.SQLITE_NONE},
       {TypeID.HSQL_INTERVAL_SECOND, TypeID.H2_VARCHAR, TypeID.HSQL_INTERVAL_SECOND, TypeID.DERBY_VARCHAR, TypeID.SQLITE_NONE}};
   
   private static final int[][] DERBY_TYPES = {
       {TypeID.DERBY_IDENTITY, TypeID.H2_IDENTITY, TypeID.HSQL_IDENTITY, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.DERBY_CHAR, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.DERBY_CHAR_FOR_BIT_DATA, TypeID.H2_BINARY, TypeID.HSQL_BINARY, TypeID.DERBY_CHAR_FOR_BIT_DATA, TypeID.SQLITE_BLOB},
       {TypeID.DERBY_VARCHAR, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.DERBY_VARCHAR_FOR_BIT_DATA, TypeID.H2_BINARY, TypeID.HSQL_VARBINARY, TypeID.DERBY_VARCHAR_FOR_BIT_DATA, TypeID.SQLITE_BLOB},
       {TypeID.DERBY_BLOB, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.DERBY_LONG_VARCHAR, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.DERBY_LONG_VARCHAR_FOR_BIT_DATA, TypeID.H2_BINARY, TypeID.HSQL_LONGVARBINARY, TypeID.DERBY_LONG_VARCHAR_FOR_BIT_DATA, TypeID.SQLITE_BLOB},
       {TypeID.DERBY_CLOB, TypeID.H2_CLOB, TypeID.HSQL_CLOB, TypeID.DERBY_CLOB, TypeID.SQLITE_TEXT},
       {TypeID.DERBY_BOOLEAN, TypeID.H2_BOOLEAN, TypeID.HSQL_BOOLEAN, TypeID.DERBY_BOOLEAN, TypeID.SQLITE_NUMERIC},
       {TypeID.DERBY_SMALLINT, TypeID.H2_SMALLINT, TypeID.HSQL_SMALLINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.DERBY_INTEGER, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.DERBY_BIGINT, TypeID.H2_BIGINT, TypeID.HSQL_BIGINT, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.DERBY_FLOAT, TypeID.H2_DOUBLE, TypeID.HSQL_FLOAT, TypeID.DERBY_FLOAT, TypeID.SQLITE_REAL},
       {TypeID.DERBY_REAL, TypeID.H2_REAL, TypeID.HSQL_REAL, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.DERBY_DOUBLE, TypeID.H2_DOUBLE, TypeID.HSQL_DOUBLE, TypeID.DERBY_DOUBLE, TypeID.SQLITE_REAL},
       {TypeID.DERBY_DECIMAL, TypeID.H2_DECIMAL, TypeID.HSQL_DECIMAL, TypeID.DERBY_DECIMAL, TypeID.SQLITE_NUMERIC},
       {TypeID.DERBY_DATE, TypeID.H2_DATE, TypeID.HSQL_DATE, TypeID.DERBY_DATE, TypeID.SQLITE_TEXT},
       {TypeID.DERBY_TIME, TypeID.H2_TIME, TypeID.HSQL_TIME, TypeID.DERBY_TIME, TypeID.SQLITE_TEXT},
       {TypeID.DERBY_TIMESTAMP, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT}};
    
   private static final int[][] POSTGRESQL_TYPES = {
       {TypeID.POSTGRESQL_SERIAL, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.POSTGRESQL_BIGSERIAL, TypeID.H2_BIGINT, TypeID.HSQL_BIGINT, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.POSTGRESQL_INT2, TypeID.H2_SMALLINT, TypeID.HSQL_SMALLINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.POSTGRESQL_INT4, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.POSTGRESQL_OID, TypeID.H2_BIGINT, TypeID.HSQL_BIGINT, TypeID.DERBY_BIGINT, TypeID.SQLITE_NONE},
       {TypeID.POSTGRESQL_INT8, TypeID.H2_BIGINT, TypeID.HSQL_BIGINT, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.POSTGRESQL_MONEY, TypeID.H2_DOUBLE, TypeID.HSQL_DOUBLE, TypeID.DERBY_DOUBLE, TypeID.SQLITE_REAL},
       {TypeID.POSTGRESQL_NUMERIC, TypeID.H2_DECIMAL, TypeID.HSQL_NUMERIC, TypeID.DERBY_DECIMAL, TypeID.SQLITE_NUMERIC},
       {TypeID.POSTGRESQL_FLOAT4, TypeID.H2_REAL, TypeID.HSQL_FLOAT, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.POSTGRESQL_FLOAT8, TypeID.H2_DOUBLE, TypeID.HSQL_DOUBLE, TypeID.DERBY_DOUBLE, TypeID.SQLITE_REAL},
       {TypeID.POSTGRESQL_CHAR, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_BPCHAR, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_VARCHAR, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_TEXT, TypeID.H2_CLOB, TypeID.HSQL_CLOB, TypeID.DERBY_CLOB, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_NAME, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_BYTEA, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.POSTGRESQL_BOOL, TypeID.H2_BOOLEAN, TypeID.HSQL_BOOLEAN, TypeID.DERBY_BOOLEAN, TypeID.SQLITE_NUMERIC},
       {TypeID.POSTGRESQL_BIT, TypeID.H2_VARCHAR, TypeID.HSQL_BIT, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_VARBIT, TypeID.H2_VARCHAR, TypeID.HSQL_BIT_VARYING, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_DATE, TypeID.H2_DATE, TypeID.HSQL_DATE, TypeID.DERBY_DATE, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_TIME, TypeID.H2_TIME, TypeID.HSQL_TIME, TypeID.DERBY_TIME, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_TIMETZ, TypeID.H2_TIME, TypeID.HSQL_TIME_WITH_TIME_ZONE, TypeID.DERBY_TIME, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_TIMESTAMP, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_TIMESTAMPTZ, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP_WITH_TIME_ZONE, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_INTERVAL, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_CIDR, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_INET, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_MACADDR, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_POINT, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_LSEG, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_BOX, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_PATH, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_POLYGON, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_CIRCLE, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.POSTGRESQL_ARRAYS, TypeID.H2_ARRAY, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT}};
   
   private static final int[][] MYSQL_TYPES = {
       {TypeID.MYSQL_CHAR, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.MYSQL_VARCHAR, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.MYSQL_TINYBLOB, TypeID.H2_BLOB, TypeID.HSQL_VARBINARY, TypeID.DERBY_VARCHAR_FOR_BIT_DATA, TypeID.SQLITE_BLOB},
       {TypeID.MYSQL_BLOB, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.MYSQL_MEDIUMBLOB, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.MYSQL_LONGBLOB, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.MYSQL_TINYINT, TypeID.H2_TINYINT, TypeID.HSQL_TINYINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_SMALLINT_UNSIGNED, TypeID.H2_TINYINT, TypeID.HSQL_TINYINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_BIT, TypeID.H2_CHAR, TypeID.HSQL_BIT, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.MYSQL_SMALLINT, TypeID.H2_SMALLINT, TypeID.HSQL_SMALLINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_SMALLINT_UNSIGNED, TypeID.H2_SMALLINT, TypeID.HSQL_SMALLINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_MEDIUMINT, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_MEDIUMINT_UNSIGNED, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_INTEGER, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_INT, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_INTEGER_UNSIGNED, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_INT_UNSIGNED, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_BIGINT, TypeID.H2_BIGINT, TypeID.HSQL_BIGINT, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_BIGINT_UNSIGNED, TypeID.H2_BIGINT, TypeID.HSQL_BIGINT, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.MYSQL_FLOAT, TypeID.H2_REAL, TypeID.HSQL_FLOAT, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.MYSQL_FLOAT_UNSIGNED, TypeID.H2_REAL, TypeID.HSQL_FLOAT, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.MYSQL_DOUBLE, TypeID.H2_DOUBLE, TypeID.HSQL_DOUBLE, TypeID.DERBY_DOUBLE, TypeID.SQLITE_REAL},
       {TypeID.MYSQL_DOUBLE_UNSIGNED, TypeID.H2_DOUBLE, TypeID.HSQL_DOUBLE, TypeID.DERBY_DOUBLE, TypeID.SQLITE_REAL},
       {TypeID.MYSQL_DECIMAL, TypeID.H2_DECIMAL, TypeID.HSQL_DECIMAL, TypeID.DERBY_DECIMAL, TypeID.SQLITE_NUMERIC},
       {TypeID.MYSQL_DATE, TypeID.H2_DATE, TypeID.HSQL_DATE, TypeID.DERBY_DATE, TypeID.SQLITE_TEXT},
       {TypeID.MYSQL_TIME, TypeID.H2_TIME, TypeID.HSQL_TIME, TypeID.DERBY_TIME, TypeID.SQLITE_TEXT},
       {TypeID.MYSQL_DATETIME, TypeID.H2_TIMESTAMP, TypeID.HSQL_DATETIME, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.MYSQL_TIMESTAMP, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.MYSQL_YEAR, TypeID.H2_DATE, TypeID.HSQL_DATE, TypeID.DERBY_DATE, TypeID.SQLITE_TEXT}};
   
   private static final int[][] ORACLE_TYPES = {
       {TypeID.ORACLE_CHAR, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_NCHAR, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_VARCHAR2, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_NVARCHAR2, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_LONG, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_RAW, TypeID.H2_BLOB, TypeID.HSQL_VARBINARY, TypeID.DERBY_VARCHAR_FOR_BIT_DATA, TypeID.SQLITE_BLOB},
       {TypeID.ORACLE_LONG_RAW, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.ORACLE_BLOB, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.ORACLE_CLOB, TypeID.H2_CLOB, TypeID.HSQL_CLOB, TypeID.DERBY_CLOB, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_NCLOB, TypeID.H2_CLOB, TypeID.HSQL_CLOB, TypeID.DERBY_CLOB, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_BFILE, TypeID.H2_OTHER, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_NONE},
       {TypeID.ORACLE_ROWID, TypeID.H2_UUID, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_NONE},
       {TypeID.ORACLE_NUMBER, TypeID.H2_DECIMAL, TypeID.HSQL_NUMERIC, TypeID.DERBY_DECIMAL, TypeID.SQLITE_NUMERIC},
       {TypeID.ORACLE_FLOAT, TypeID.H2_REAL, TypeID.HSQL_FLOAT, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.ORACLE_BINARY_FLOAT, TypeID.H2_REAL, TypeID.HSQL_FLOAT, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.ORACLE_BINARY_DOUBLE, TypeID.H2_DOUBLE, TypeID.HSQL_DOUBLE, TypeID.DERBY_DOUBLE, TypeID.SQLITE_REAL},
       {TypeID.ORACEL_DOUBLE, TypeID.H2_DOUBLE, TypeID.HSQL_DOUBLE, TypeID.DERBY_DOUBLE, TypeID.SQLITE_REAL},
       {TypeID.ORACLE_DATE, TypeID.H2_DATE, TypeID.HSQL_DATE, TypeID.DERBY_DATE, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_TIMESTAMP, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_TIMESTAMPTZ, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP_WITH_TIME_ZONE, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_TIMESTAMP_WITH_TIME_ZONE, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP_WITH_TIME_ZONE, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_TIMESTAMPLTZ, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP_WITH_TIME_ZONE, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP_WITH_TIME_ZONE, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_INTERVALYM, TypeID.H2_VARCHAR, TypeID.HSQL_INTERVAL_YEAR_TO_MONTH, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.ORACLE_INTERVALDS, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT}};
   
   private static final int[][] SQLITE_TYPES = {
       {TypeID.SQLITE_INTEGER, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.SQLITE_REAL, TypeID.H2_REAL, TypeID.HSQL_REAL, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.SQLITE_NUMERIC, TypeID.H2_DECIMAL, TypeID.HSQL_NUMERIC, TypeID.DERBY_DECIMAL, TypeID.SQLITE_NUMERIC},
       
       // SQLite JDBC returns TEXT to be VARCHAR SQL type. To Insure Derby is able to support
       // a larger size, changed from DERBY_LONG_VARCHAR to DERBY_CLOB.
       
       {TypeID.SQLITE_TEXT, TypeID.H2_VARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_CLOB, TypeID.SQLITE_TEXT},
       
       {TypeID.SQLITE_BLOB, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.SQLITE_NULL, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_NULL},
       {TypeID.SQLITE_NONE, TypeID.H2_BLOB, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_NONE}};
   
   private static final int[][] MSACCESS_TYPES = {
       {TypeID.MSACCESS_COUNTER, TypeID.H2_IDENTITY, TypeID.HSQL_IDENTITY, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.MSACCESS_BINARY, TypeID.H2_BINARY, TypeID.HSQL_BINARY, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.MSACCESS_LONGBINARY, TypeID.H2_BLOB, TypeID.HSQL_VARBINARY, TypeID.DERBY_LONG_VARCHAR_FOR_BIT_DATA, TypeID.SQLITE_BLOB},
       {TypeID.MSACCESS_VARCHAR, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.MSACCESS_LONGCHAR, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.MSACCESS_BIT, TypeID.H2_BOOLEAN, TypeID.HSQL_BOOLEAN, TypeID.DERBY_BOOLEAN, TypeID.SQLITE_INTEGER},
       {TypeID.MSACCESS_BYTE, TypeID.H2_TINYINT, TypeID.HSQL_TINYINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.MSACCESS_SMALLINT, TypeID.H2_SMALLINT, TypeID.HSQL_SMALLINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.MSACCESS_INTEGER, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.MSACCESS_REAL, TypeID.H2_REAL, TypeID.HSQL_REAL, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.MSACCESS_DOUBLE, TypeID.H2_DOUBLE, TypeID.HSQL_DOUBLE, TypeID.DERBY_DOUBLE, TypeID.SQLITE_REAL},
       {TypeID.MSACCESS_CURRENCY, TypeID.H2_DECIMAL, TypeID.HSQL_NUMERIC, TypeID.DERBY_DECIMAL, TypeID.SQLITE_REAL},
       {TypeID.MSACCESS_GUID, TypeID.H2_OTHER, TypeID.HSQL_BLOB, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.MSACCESS_DATETIME, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT}};
   
   private static final int[][] MSSQL_TYPES = {
       {TypeID.MSSQL_CHAR, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_NCHAR, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_VARCHAR, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_NVARCHAR, TypeID.H2_VARCHAR, TypeID.HSQL_VARCHAR, TypeID.DERBY_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_BINARY, TypeID.H2_BINARY, TypeID.HSQL_BINARY, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.MSSQL_VARBINARY, TypeID.H2_BLOB, TypeID.HSQL_VARBINARY, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.MSSQL_IMAGE, TypeID.H2_BLOB, TypeID.HSQL_BINARY, TypeID.DERBY_BLOB, TypeID.SQLITE_BLOB},
       {TypeID.MSSQL_TEXT, TypeID.H2_CLOB, TypeID.HSQL_CLOB, TypeID.DERBY_CLOB, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_NTEXT, TypeID.H2_CLOB, TypeID.HSQL_CLOB, TypeID.DERBY_CLOB, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_UNIQUEIDENTIFIER, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_XML, TypeID.H2_LONGVARCHAR, TypeID.HSQL_LONGVARCHAR, TypeID.DERBY_LONG_VARCHAR, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_BIT, TypeID.H2_BOOLEAN, TypeID.HSQL_BIT, TypeID.DERBY_BOOLEAN, TypeID.SQLITE_INTEGER},
       {TypeID.MSSQL_TINYINT, TypeID.H2_TINYINT, TypeID.HSQL_TINYINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.MSSQL_SMALLINT, TypeID.H2_SMALLINT, TypeID.HSQL_SMALLINT, TypeID.DERBY_SMALLINT, TypeID.SQLITE_INTEGER},
       {TypeID.MSSQL_INT, TypeID.H2_INTEGER, TypeID.HSQL_INTEGER, TypeID.DERBY_INTEGER, TypeID.SQLITE_INTEGER},
       {TypeID.MSSQL_BIGINT, TypeID.H2_BIGINT, TypeID.HSQL_BIGINT, TypeID.DERBY_BIGINT, TypeID.SQLITE_INTEGER},
       {TypeID.MSSQL_FLOAT, TypeID.H2_DOUBLE, TypeID.HSQL_DOUBLE, TypeID.DERBY_DOUBLE, TypeID.SQLITE_REAL},
       {TypeID.MSSQL_REAL, TypeID.H2_REAL, TypeID.HSQL_REAL, TypeID.DERBY_REAL, TypeID.SQLITE_REAL},
       {TypeID.MSSQL_DECIMAL, TypeID.H2_DECIMAL, TypeID.HSQL_DECIMAL, TypeID.DERBY_DECIMAL, TypeID.SQLITE_REAL},
       {TypeID.MSSQL_NUMERIC, TypeID.H2_DECIMAL, TypeID.HSQL_NUMERIC, TypeID.DERBY_DECIMAL, TypeID.SQLITE_REAL},
       {TypeID.MSSQL_MONEY, TypeID.H2_DECIMAL, TypeID.HSQL_DECIMAL, TypeID.DERBY_DECIMAL, TypeID.SQLITE_REAL},
       {TypeID.MSSQL_SMALLMONEY, TypeID.H2_DECIMAL, TypeID.HSQL_DECIMAL, TypeID.DERBY_DECIMAL, TypeID.SQLITE_REAL},
       {TypeID.MSSQL_DATE, TypeID.H2_DATE, TypeID.HSQL_DATE, TypeID.DERBY_DATE, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_TIME, TypeID.H2_TIME, TypeID.HSQL_TIME, TypeID.DERBY_TIME, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_DATETIME, TypeID.H2_TIMESTAMP, TypeID.HSQL_DATETIME, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_SMALLDATETIME, TypeID.H2_TIMESTAMP, TypeID.HSQL_DATETIME, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_DATETIME2, TypeID.H2_TIMESTAMP, TypeID.HSQL_DATETIME, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_DATETIMEOFFSET, TypeID.H2_CHAR, TypeID.HSQL_CHARACTER, TypeID.DERBY_CHAR, TypeID.SQLITE_TEXT},
       {TypeID.MSSQL_TIMESTAMP, TypeID.H2_TIMESTAMP, TypeID.HSQL_TIMESTAMP, TypeID.DERBY_TIMESTAMP, TypeID.SQLITE_TEXT}};
   
   //==============================================================
   // TypesInfoCache Constructors
   //==============================================================
   
   public TypesInfoCache()
   {
      this(ConnectionManager.getDataSourceType());
   }
   
   public TypesInfoCache(String dataSourceType)
   {
      this(dataSourceType, DEFAULT_DATASINK_TYPE);
   }
   
   public TypesInfoCache(String dataSourceType, String dataSinkType)
   {
      this.dataSourceType = dataSourceType;
      this.dataSinkType = dataSinkType;
      
      nameToType = new HashMap<String, Integer>();
      
      if (dataSourceType.equals(ConnectionManager.H2))
         addSourceSinkType(H2_TYPES);
      else if (dataSourceType.indexOf(ConnectionManager.HSQL) != -1)
         addSourceSinkType(HSQL_TYPES);
      else if (dataSourceType.indexOf(ConnectionManager.DERBY) != -1)
         addSourceSinkType(DERBY_TYPES);
      else if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
         addSourceSinkType(POSTGRESQL_TYPES);
      else if (dataSourceType.equals(ConnectionManager.MYSQL))
         addSourceSinkType(MYSQL_TYPES);
      else if (dataSourceType.equals(ConnectionManager.MARIADB))
         addSourceSinkType(MYSQL_TYPES);
      else if (dataSourceType.equals(ConnectionManager.ORACLE))
         addSourceSinkType(ORACLE_TYPES);
      else if (dataSourceType.equals(ConnectionManager.SQLITE))
         addSourceSinkType(SQLITE_TYPES);
      else if (dataSourceType.equals(ConnectionManager.MSACCESS))
         addSourceSinkType(MSACCESS_TYPES);
      else if (dataSourceType.equals(ConnectionManager.MSSQL))
         addSourceSinkType(MSSQL_TYPES);
      
      // Source not found, all will become UNSPECIFIED.
   }
   
   //==============================================================
   // Class method to create the appropriate data source type
   // relationship to data sink type.
   //==============================================================
   
   private void addSourceSinkType(int[][] types)
   {
      // Method Instances
      String sourceTypeName;
      Integer sink_id;
      
      for (int i = 0; i < types.length; i++)
      {
         sourceTypeName = TypeID.toString(types[i][TYPE_NAME]);
         // System.out.println("TypesInfoCache addSourceSinkType() " + sourceTypeName);
         
         if (dataSinkType.equals(ConnectionManager.H2))
            sink_id = Integer.valueOf(types[i][H2_TYPE]);
         else if (dataSinkType.equals(ConnectionManager.DERBY))
            sink_id = Integer.valueOf(types[i][DERBY_TYPE]);
         else if (dataSinkType.equals(ConnectionManager.SQLITE))
            sink_id = Integer.valueOf(types[i][SQLITE_TYPE]);
         // Not sure so default.
         else
            sink_id = Integer.valueOf(types[i][HSQL_TYPE]);
         
         nameToType.put(sourceTypeName, sink_id);
      }
   }
   
   //==============================================================
   // Class method to obtain the appropriate sink data type as
   // related to the given source data type.
   //==============================================================
   
   public String getType(int sourceSQLType, String sourceTypeName)
   {
      // System.out.println("TypesInfoCache getType() " + sourceSQLType + " : " + sourceTypeName);
      
      // Deal with PostgreSQL Array Types
      if (dataSourceType.equals(ConnectionManager.POSTGRESQL) && sourceTypeName.startsWith("_"))
         sourceTypeName = TypeID.toString(TypeID.POSTGRESQL_ARRAYS);
      
      // System.out.println(sourceTypeName);
      
      if (nameToType.containsKey(sourceTypeName))
         return TypeID.toString(nameToType.get(sourceTypeName));
      else
      {
         if (dataSinkType.equals(ConnectionManager.SQLITE))
            return TypeID.toString(TypeID.SQLITE_NUMERIC);
         else
         {
            Field[] fields = Types.class.getFields();
            String sqlTypeName = "";
            
            try
            {
               for (int i = 0; i < fields.length; i++)
                  if (fields[i].getInt(null) == sourceSQLType)
                     sqlTypeName = fields[i].getName();
            }
            catch (IllegalAccessException e)
            {
               // never happens
            }
            
            if (sqlTypeName.isEmpty())
               return TypeID.toString(TypeID._UNSPECIFIED);
            else
               return sqlTypeName;
         }
      }
   } 
   
   //==============================================================
   // Class method to test the type arrays' sizes for proper
   // representation of each of the sink db types.
   //==============================================================
   
   public static String testTypesArraySizes()
   {
      // Method Instances
      StringBuilder testResult = new StringBuilder();
      
      // System.out.println("TypesInfoCache testTypeArraySizes() ");
      
      testResult.append("Invalid Sink DB Type Representation:");
      
      for (int i = 0; i < H2_TYPES.length; i++)
         if (H2_TYPES[i].length != 5)
            testResult.append("H2_TYPES Row:  " + i);
      for (int i = 0; i < HSQL_TYPES.length; i++)
         if (HSQL_TYPES[i].length != 5)
            testResult.append("HSQL_TYPES Row: " + i);
      for (int i = 0; i < DERBY_TYPES.length; i++)
         if (DERBY_TYPES[i].length != 5)
            testResult.append("DERBY_TYPES Row: " + i);
      for (int i = 0; i < POSTGRESQL_TYPES.length; i++)
         if (POSTGRESQL_TYPES[i].length != 5)
            testResult.append("POSTGRESQL_TYPES Row:  " + i);
      for (int i = 0; i < MYSQL_TYPES.length; i++)
         if (MYSQL_TYPES[i].length != 5)
            testResult.append("MYSQL_TYPES Row: " + i);
      for (int i = 0; i < ORACLE_TYPES.length; i++)
         if (ORACLE_TYPES[i].length != 5)
            testResult.append("ORACLE_TYPES Row:  " + i);
      for (int i = 0; i < SQLITE_TYPES.length; i++)
         if (SQLITE_TYPES[i].length != 5)
         testResult.append("SQLITE_TYPES Row: " + i);
      for (int i = 0; i < MSACCESS_TYPES.length; i++)
         if (MSACCESS_TYPES[i].length != 5)
            testResult.append("MSACCESS_TYPES Row: " + i);
      for (int i = 0; i < MSSQL_TYPES.length; i++)
         if (MSSQL_TYPES[i].length != 5)
            testResult.append("MSSQL_TYPES Row: " + i);
      
      return testResult.toString();   
   } 
}
