//=================================================================
//                            TypeID
//=================================================================
//
//    This class provides an identification definition for data
// types that follows a prescribe naming scheme.
//
//                        << TypeID.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.0 09/17/2016
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
// Version 1.0 Production TypeID Class.
//        
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.datasource;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 *    The TypeID class provides an identification definition for data
 * types that follows a prescribe naming scheme.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class TypeID
{
   // Default Unknown Type.
   public static final int _UNSPECIFIED = 0;
   
   // H2 Data Type IDs
   public static final int H2_IDENTITY = -254;
   public static final int H2_CHAR = -252;
   public static final int H2_VARCHAR = -250;
   public static final int H2_VARCHAR_IGNORECASE = -248;
   public static final int H2_LONGVARCHAR = -247;
   public static final int H2_BINARY = -246;
   public static final int H2_BLOB = -244;
   public static final int H2_CLOB = -242;
   public static final int H2_OTHER = -240;
   public static final int H2_UUID = -238;
   public static final int H2_BOOLEAN = -236;
   public static final int H2_TINYINT = -234;
   public static final int H2_SMALLINT = -232;
   public static final int H2_INTEGER = -230;
   public static final int H2_BIGINT = -228;
   public static final int H2_REAL = -226;
   public static final int H2_DOUBLE = -224;
   public static final int H2_DECIMAL = -222;
   public static final int H2_DATE = -220;
   public static final int H2_TIME = -218;
   public static final int H2_TIMESTAMP = -216;
   public static final int H2_ARRAY = -214;
   
   // HSQL 2.0 Data Type IDs
   public static final int HSQL_IDENTITY = -212;
   public static final int HSQL_CHARACTER = -210;
   public static final int HSQL_VARCHAR = -208;
   public static final int HSQL_LONGVARCHAR = -206;
   public static final int HSQL_CLOB = -204;
   public static final int HSQL_BINARY = -202;
   public static final int HSQL_VARBINARY = -200;
   public static final int HSQL_LONGVARBINARY = -198;
   public static final int HSQL_BLOB = -196;
   public static final int HSQL_TINYINT = -194;
   public static final int HSQL_SMALLINT = -192;
   public static final int HSQL_INTEGER = -190;
   public static final int HSQL_BIGINT = -188;
   public static final int HSQL_FLOAT = -186;
   public static final int HSQL_DOUBLE = -184;
   public static final int HSQL_REAL = -182;
   public static final int HSQL_DECIMAL = -180;
   public static final int HSQL_NUMERIC = -178;
   public static final int HSQL_BOOLEAN = -176;
   public static final int HSQL_BIT = -174;
   public static final int HSQL_BIT_VARYING = -172;
   public static final int HSQL_DATE = -170;
   public static final int HSQL_TIME = -168;
   public static final int HSQL_TIME_WITH_TIME_ZONE = -166;
   public static final int HSQL_DATETIME = -164;
   public static final int HSQL_TIMESTAMP = -162;
   public static final int HSQL_TIMESTAMP_WITH_TIME_ZONE = -160;
   public static final int HSQL_INTERVAL = -158;
   public static final int HSQL_INTERVAL_YEAR_TO_MONTH = -156;
   public static final int HSQL_INTERVAL_YEAR = -154;
   public static final int HSQL_INTERVAL_DAY_TO_HOUR = -152;
   public static final int HSQL_INTERVAL_MINUTE_TO_SECOND = -150;
   public static final int HSQL_INTERVAL_SECOND = -148;

   // Derby Data Type IDs
   public static final int DERBY_IDENTITY = -146;
   public static final int DERBY_CHAR = -144;
   public static final int DERBY_CHAR_FOR_BIT_DATA = -142;
   public static final int DERBY_VARCHAR = -140;
   public static final int DERBY_VARCHAR_FOR_BIT_DATA = -138;
   public static final int DERBY_BLOB = -136;
   public static final int DERBY_LONG_VARCHAR = -134;
   public static final int DERBY_LONG_VARCHAR_FOR_BIT_DATA = -132;
   public static final int DERBY_CLOB = -130;
   public static final int DERBY_BOOLEAN = -128;
   public static final int DERBY_SMALLINT = -126;
   public static final int DERBY_INTEGER = -124;
   public static final int DERBY_BIGINT = -122;
   public static final int DERBY_FLOAT = -120;
   public static final int DERBY_REAL = -118;
   public static final int DERBY_DOUBLE = -116;
   public static final int DERBY_DECIMAL = -114;
   public static final int DERBY_DATE = -112;
   public static final int DERBY_TIME = -110;
   public static final int DERBY_TIMESTAMP = -108;
   
   // PostgreSQL Data Type IDs
   public static final int POSTGRESQL_SERIAL = -106;
   public static final int POSTGRESQL_BIGSERIAL = -104;
   public static final int POSTGRESQL_INT2 = -102;
   public static final int POSTGRESQL_INT4 = -100;
   public static final int POSTGRESQL_OID = -98;
   public static final int POSTGRESQL_INT8 = -96;
   public static final int POSTGRESQL_MONEY = -94;
   public static final int POSTGRESQL_NUMERIC = -92;
   public static final int POSTGRESQL_FLOAT4 = -90;
   public static final int POSTGRESQL_FLOAT8 = -88;
   public static final int POSTGRESQL_CHAR = -86;
   public static final int POSTGRESQL_BPCHAR = -84;
   public static final int POSTGRESQL_VARCHAR = -82;
   public static final int POSTGRESQL_TEXT = -80;
   public static final int POSTGRESQL_NAME = -76;
   public static final int POSTGRESQL_BYTEA = -74;
   public static final int POSTGRESQL_BOOL = -72;
   public static final int POSTGRESQL_BIT = -70;
   public static final int POSTGRESQL_VARBIT = -68;
   public static final int POSTGRESQL_DATE = -66;
   public static final int POSTGRESQL_TIME = -64;
   public static final int POSTGRESQL_TIMETZ = -62;
   public static final int POSTGRESQL_TIMESTAMP = -60;
   public static final int POSTGRESQL_TIMESTAMPTZ = -58;
   public static final int POSTGRESQL_INTERVAL = -56;
   public static final int POSTGRESQL_CIDR = -54;
   public static final int POSTGRESQL_INET = -52;
   public static final int POSTGRESQL_MACADDR = -50;
   public static final int POSTGRESQL_POINT = -48;
   public static final int POSTGRESQL_LSEG = -46;
   public static final int POSTGRESQL_BOX = -44;
   public static final int POSTGRESQL_PATH = -42;
   public static final int POSTGRESQL_POLYGON = -40;
   public static final int POSTGRESQL_CIRCLE = -38;
   // Dummy for all arrays, ie. _INT2, _INT4, etc.
   public static final int POSTGRESQL_ARRAYS = -36;
   
   // MySQL Data Type IDs
   public static final int MYSQL_CHAR = -34;
   public static final int MYSQL_VARCHAR = -32;
   public static final int MYSQL_TINYBLOB = -30;
   public static final int MYSQL_BLOB = -28;
   public static final int MYSQL_MEDIUMBLOB = -26;
   public static final int MYSQL_LONGBLOB = -24;
   public static final int MYSQL_TINYINT = -22;
   public static final int MYSQL_TINYINT_UNSIGNED = -20;
   public static final int MYSQL_BIT = -18;
   public static final int MYSQL_SMALLINT = -16;
   public static final int MYSQL_SMALLINT_UNSIGNED = -14;
   public static final int MYSQL_MEDIUMINT = -12;
   public static final int MYSQL_MEDIUMINT_UNSIGNED = -10;
   public static final int MYSQL_INTEGER = -9;
   public static final int MYSQL_INT = -8;
   public static final int MYSQL_INTEGER_UNSIGNED = -7;
   public static final int MYSQL_INT_UNSIGNED = -6;
   public static final int MYSQL_BIGINT = -4;
   public static final int MYSQL_BIGINT_UNSIGNED = -2;
   public static final int MYSQL_FLOAT = 2;
   public static final int MYSQL_FLOAT_UNSIGNED = 4;
   public static final int MYSQL_DOUBLE = 6;
   public static final int MYSQL_DOUBLE_UNSIGNED = 8;
   public static final int MYSQL_DECIMAL = 10;
   public static final int MYSQL_DATE = 12;
   public static final int MYSQL_TIME = 14;
   public static final int MYSQL_DATETIME = 16;
   public static final int MYSQL_TIMESTAMP = 18;    
   public static final int MYSQL_YEAR = 20;
   
   // Oracle Data Type IDs
   public static final int ORACLE_CHAR = 22;
   public static final int ORACLE_NCHAR = 23;
   public static final int ORACLE_VARCHAR2 = 24;
   public static final int ORACLE_NVARCHAR2 = 25;
   public static final int ORACLE_LONG = 26;
   public static final int ORACLE_RAW = 28;
   public static final int ORACLE_LONG_RAW = 29;
   public static final int ORACLE_BLOB = 30;
   public static final int ORACLE_CLOB = 32;
   public static final int ORACLE_NCLOB = 33;
   public static final int ORACLE_BFILE = 34;
   public static final int ORACLE_ROWID = 35;
   public static final int ORACLE_NUMBER = 36;
   public static final int ORACLE_BINARY_FLOAT = 38;
   public static final int ORACLE_FLOAT = 39; // Virtual
   public static final int ORACLE_BINARY_DOUBLE = 40;
   public static final int ORACEL_DOUBLE = 41; // Virtual
   public static final int ORACLE_DATE = 42;
   public static final int ORACLE_TIMESTAMP = 44;
   public static final int ORACLE_TIMESTAMPTZ = 46;
   public static final int ORACLE_TIMESTAMP_WITH_TIME_ZONE = 47;
   public static final int ORACLE_TIMESTAMPLTZ = 48;
   public static final int ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE = 49;
   public static final int ORACLE_INTERVALYM = 50;
   public static final int ORACLE_INTERVALDS = 51;
   
   // SQLite Data Type IDs
   public static final int SQLITE_INTEGER = 52;
   public static final int SQLITE_REAL = 54;
   public static final int SQLITE_NUMERIC = 55;
   public static final int SQLITE_TEXT = 56;
   public static final int SQLITE_BLOB = 58;
   public static final int SQLITE_NONE = 59;
   
   // MS Access Data Type IDs
   public static final int MSACCESS_COUNTER = 60;
   public static final int MSACCESS_BINARY = 62;
   public static final int MSACCESS_LONGBINARY = 64;
   public static final int MSACCESS_VARCHAR = 66;
   public static final int MSACCESS_LONGCHAR = 68;
   public static final int MSACCESS_BIT = 70;
   public static final int MSACCESS_BYTE = 72;
   public static final int MSACCESS_SMALLINT = 74;
   public static final int MSACCESS_INTEGER = 76;
   public static final int MSACCESS_REAL = 78;
   public static final int MSACCESS_DOUBLE = 80;
   public static final int MSACCESS_CURRENCY = 82;
   public static final int MSACCESS_GUID = 84;
   public static final int MSACCESS_DATETIME = 86;
   
   // MSSQL Data Type IDs
   public static final int MSSQL_CHAR = 88;
   public static final int MSSQL_NCHAR = 90;
   public static final int MSSQL_VARCHAR = 92;
   public static final int MSSQL_NVARCHAR = 94;
   public static final int MSSQL_BINARY = 96;
   public static final int MSSQL_VARBINARY = 98;
   public static final int MSSQL_IMAGE = 100;
   public static final int MSSQL_TEXT = 102;
   public static final int MSSQL_NTEXT = 104;
   public static final int MSSQL_UNIQUEIDENTIFIER = 106;
   public static final int MSSQL_XML = 108;
   public static final int MSSQL_BIT = 110;
   public static final int MSSQL_TINYINT = 112;
   public static final int MSSQL_SMALLINT = 114;
   public static final int MSSQL_INT = 116;
   public static final int MSSQL_BIGINT = 118;
   public static final int MSSQL_FLOAT = 120;
   public static final int MSSQL_REAL = 122;
   public static final int MSSQL_DECIMAL = 124;
   public static final int MSSQL_NUMERIC = 126;
   public static final int MSSQL_MONEY = 128;
   public static final int MSSQL_SMALLMONEY = 130;
   public static final int MSSQL_DATE = 132;
   public static final int MSSQL_TIME = 134;
   public static final int MSSQL_DATETIME = 136;
   public static final int MSSQL_SMALLDATETIME = 138;
   public static final int MSSQL_DATETIME2 = 140;
   public static final int MSSQL_DATETIMEOFFSET = 142;
   public static final int MSSQL_TIMESTAMP = 144;    
   
   //==============================================================
   // Class method to allow classes to try and obtain the field
   // name with the given TypeID value.
   //
   // ** NOTE **
   // All the fields should adhere to the prescribe naming scheme
   // of having the DB name followed by an underscore and then the
   // data type. Underscores in the name will be replaced by a
   // space except for H2 & Oracle.
   //==============================================================
    
   public static String toString(int typeid)
   {
      // Method Instances
      Field[] fields;
      String prefix, name;
      
      try
      {
         fields = TypeID.class.getFields();
         
         // Cycle through instance names.
         for (int i = 0; i < fields.length; ++i)
         {
            if (fields[i].getInt(null) == typeid)
            {
               if (fields[i].getName().indexOf("_") != -1)
               {
                  prefix = fields[i].getName().substring(0, fields[i].getName().indexOf("_"));
                  name = fields[i].getName().substring(fields[i].getName().indexOf("_") + 1);
                  
                  if (name.indexOf("_") != -1 && (!prefix.equals("H2")))
                  {
                     if (prefix.equals("ORACLE"))
                     {
                        if (name.indexOf("TIME_ZONE") != -1 || name.indexOf("LONG_RAW") != -1)
                           return name.replaceAll("_", " ");
                        else
                           return name;
                     }
                     else
                        return name.replaceAll("_", " ");
                  }
                  else
                     return name;
               }
               else
                  return fields[i].getName();
            }
         }
      }
      catch (IllegalAccessException e)
      {
         // never happens
      }
      return "<unknown:" + typeid + ">";
   }
   
   //==============================================================
   // Class method to allow classes to try and obtain the field
   // integer value given the TypeID name.
   //==============================================================

   public static int valueOf(String typeidName) throws Exception
   {
      // Method Instances
      Field[] fields;
      
      // Just return the number given an input
      // number.
      try
      {
         return (int) Long.parseLong(typeidName);
      }
      catch (NumberFormatException ex)
      {
      }
      
      try
      {
         typeidName = typeidName.toUpperCase(Locale.ENGLISH);
         
         fields = TypeID.class.getFields();
         
         for (int i = 0; i < fields.length; ++i)
         {
            if (fields[i].getName().toUpperCase(Locale.ENGLISH).equals(typeidName))
            {
               return fields[i].getInt(null);
            }
         }
      }
      catch (IllegalAccessException e)
      {
         // never happens
      }
      throw new Exception("TypeID name type {0} not known and not a number");
   }
   
   //==============================================================
   // Class method to test for duplicate IDs.
   //==============================================================

   public static boolean testDuplicate_IDs()
   {
      // Method Instances
      boolean duplicateID;
      String name_i, name_j;
      
      duplicateID = false;
      
      try
      {
         Field[] fields = TypeID.class.getFields();
         
         // Cycle through instance names.
         for (int i = 0; i < fields.length; ++i)
         {
            name_i = fields[i].getName();
            
            for (int j = 0; j < fields.length; j++)
            {
               name_j = fields[j].getName();
               
               if (!name_i.equals(name_j))
               {
                  if (valueOf(name_i) == valueOf(name_j))
                  {
                     duplicateID = true;
                     System.out.println("Duplicate Values: " + name_i + ":" + name_j);
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         // never happens
      }
      return duplicateID;
   }
}