//=================================================================
//                         DDLGenerator
//=================================================================
//
//    This class provides the basis for creating a condensed data
// definition language statement that bodes the results of converting
// a given database query to an alternate database table.
//
//                    << DDLGenerator.java >>
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
// Version 1.0 09/17/2016 Production DDLGenerator Class.
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import com.dandymadeproductions.ajqvue.Ajsvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.TypesInfoCache;

/**
 *    The DDLGenerator class provides the basis for creating a condensed
 * data definition language statement that bodes the results of converting
 * a given database query to an alternate database table. 
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class DDLGenerator
{
   // Class Instances.
   private String dataSinkType;
   private int indexCount, autoIncrementCount;
   
   private SQLQuery sqlQuery;
   private TypesInfoCache infoCache;
   private boolean useSQLiteCast;
   
   private String columnName, columnClass, columnType;
   private int columnPrecision, columnScale, columnSize, columnIsNullable;
   private boolean columnIsAutoIncrement;
   
   private StringBuffer tableDefinition;
   private ArrayList<String> indexList;
   
   public static final String DEFAULT_DATASINK_TYPE = ConnectionManager.HSQL2;
   private static final char IDENTIFIER_QUOTE_STRING = '\"';
   public static final int INDEXCOUNT = 1;
   
   //==============================================================
   // SQLQuery Constructors
   //
   // Use the first three for Ajqvue login database source or
   // specify in fourth.
   //==============================================================

   public DDLGenerator(String sqlString)
   {
      this(new SQLQuery(sqlString), ConnectionManager.getDataSourceType(), DEFAULT_DATASINK_TYPE, INDEXCOUNT);
   }
   
   public DDLGenerator(SQLQuery sqlQuery)
   {
      this(sqlQuery, ConnectionManager.getDataSourceType(), DEFAULT_DATASINK_TYPE, INDEXCOUNT);
   }
   
   public DDLGenerator(String sqlString, String dataSinkType)
   {
      this(new SQLQuery(sqlString), ConnectionManager.getDataSourceType(), dataSinkType, INDEXCOUNT);
   }
   
   public DDLGenerator(SQLQuery sqlQuery, String dataSinkType)
   {
      this(sqlQuery, ConnectionManager.getDataSourceType(), dataSinkType, INDEXCOUNT);
   }
   
   public DDLGenerator(String sqlString, String dataSinkType, int indexCount)
   {
      this(new SQLQuery(sqlString), ConnectionManager.getDataSourceType(), dataSinkType, indexCount);
   }
   
   public DDLGenerator(SQLQuery sqlQuery, String dataSinkType, int indexCount)
   {
      this(sqlQuery, ConnectionManager.getDataSourceType(), dataSinkType, indexCount);
   }
   
   public DDLGenerator(SQLQuery sqlQuery, String dataSourceType, String dataSinkType, int indexCount)
   {
      this.sqlQuery = sqlQuery;
      this.dataSinkType = dataSinkType;
      this.indexCount = indexCount;
      
      // Just setup the required instances to accomplish
      // the ddl generation.
      
      infoCache = new TypesInfoCache(dataSourceType, dataSinkType);
      useSQLiteCast = false;
      tableDefinition = new StringBuffer();
      indexList = new ArrayList<String>();
   }
   
   //==============================================================
   // Class method to obtain the data definition language, DDL, for
   // the given constructed query, data source/sink types. Either
   // use default login database or given connection database.
   //==============================================================
   
   public String getDDL(String schemaName, String tableName)
   {
      // Method Instances
      Connection dbConnection;
      StringBuffer tempBuffer;
      
      // Setting up a connection.
      dbConnection = ConnectionManager.getConnection("DDLGenerator getDDL()");
      tempBuffer = new StringBuffer();
      
      tempBuffer.append(getDDL(dbConnection, schemaName, tableName));
      
      ConnectionManager.closeConnection(dbConnection, "DDLGenerator getDDL()");
      return tempBuffer.toString();
   }
   
   public String getDDL(Connection dbConnection, String schemaName, String tableName)
   {
      // Method Instances
      String schemaTableName; 
      Iterator<String> colNameIterator;
      int currentColumnIndex;
      
      // Reset
      tableDefinition.delete(0, tableDefinition.length());
      indexList.clear();
      currentColumnIndex = 0;
      autoIncrementCount = 0;
      
      // Create given table name.
      if (schemaName.equals(""))
         schemaTableName = IDENTIFIER_QUOTE_STRING + tableName + IDENTIFIER_QUOTE_STRING;
      else
         schemaTableName = IDENTIFIER_QUOTE_STRING + schemaName + IDENTIFIER_QUOTE_STRING
                           + "." + IDENTIFIER_QUOTE_STRING + tableName + IDENTIFIER_QUOTE_STRING;
         
      tableDefinition.append("CREATE TABLE " + schemaTableName + " (\n    ");
      
      // Execute the query & collect characteristics.
      try
      {
         // See if the query has been run.
         if (sqlQuery.getValidQuery() == -1)
         {
            if (Ajqvue.getDebug())
               System.out.println("DDLGenerator getDDL() Running SQLQuery");
            sqlQuery.executeSQL(dbConnection);
         }
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "DDLGenerator getDDL()");
      }
     
      // Create the appropriate DDL.
      colNameIterator = sqlQuery.getColumnNames().iterator();
      
      while (colNameIterator.hasNext())
      {
         columnName = colNameIterator.next();
         columnClass = sqlQuery.getColumnClassHashMap().get(columnName);
         
         if (dataSinkType.equals(ConnectionManager.SQLITE) && useSQLiteCast)
            columnType = sqlQuery.getColumnTypeNameHashMap().get(columnName);
         else
            columnType = infoCache.getType(sqlQuery.getColumnTypeNameHashMap().get(columnName));
         
         columnPrecision = ((sqlQuery.getColumnPrecisionHashMap().get(columnName)).intValue());
         columnScale = (sqlQuery.getColumnScaleHashMap().get(columnName)).intValue();
         columnSize = (sqlQuery.getColumnSizeHashMap().get(columnName)).intValue();
         columnIsNullable = (sqlQuery.getColumnIsNullableHashMap().get(columnName).intValue());
         columnIsAutoIncrement = (sqlQuery.getColumnIsAutoIncrementHashMap().get(columnName).booleanValue());
         
         // =============
         // Column name.

         tableDefinition.append(IDENTIFIER_QUOTE_STRING + columnName + IDENTIFIER_QUOTE_STRING + " ");
         
         // =============
         // Field Type
         
         if (dataSinkType.equals(ConnectionManager.H2))
            createH2_DDL();
         else if (dataSinkType.equals(ConnectionManager.DERBY))
            createDerby_DDL(currentColumnIndex < indexCount);
         else if (dataSinkType.equals(ConnectionManager.SQLITE))
            tableDefinition.append(columnType);
         // Not sure so default.
         else
            createHSQL_DDL();
         
         // ==========================
         // Column NOT NULL
         
         if (columnIsNullable == ResultSetMetaData.columnNoNulls)
            tableDefinition.append(" NOT NULL,\n    ");
         else
            tableDefinition.append(",\n    ");
         
         // Trace Column Number
         if (currentColumnIndex < indexCount)
         {
            indexList.add(columnName);
            currentColumnIndex++;
         }
      }
      tableDefinition.delete(tableDefinition.length() - 6, tableDefinition.length());
      tableDefinition.append("\n);\n");
      
      // Add Index(s) if defined.
      
      if (!indexList.isEmpty())
      {
         tableDefinition.append("CREATE INDEX "
                                + IDENTIFIER_QUOTE_STRING + indexList.get(0) + IDENTIFIER_QUOTE_STRING
                                + " ON " + schemaTableName + "(");
         Iterator<String> indexListIterator = indexList.iterator();
         
         while (indexListIterator.hasNext())
         {
            tableDefinition.append(IDENTIFIER_QUOTE_STRING + indexListIterator.next()
                                   + IDENTIFIER_QUOTE_STRING + ", ");
         }
         tableDefinition.delete(tableDefinition.length() - 2, tableDefinition.length());
         tableDefinition.append(");\n");
      }
      return tableDefinition.toString();
   }
   
   //==============================================================
   // Class method to collect the specific DDL for sink data source
   // of a H2 database.
   //==============================================================
   
   private void createH2_DDL()
   {
      // =============
      // Column type.

      // Character Types
      if (columnType.indexOf("CHAR") != -1)
      {
         if (columnType.equals("LONGVARCHAR"))
            tableDefinition.append(columnType);
         else
         {
            if (columnSize <= 0)
               tableDefinition.append(columnType + "(" + Integer.MAX_VALUE + ")");
            else
               tableDefinition.append(columnType + "(" + columnSize + ")");
         }
      }
      // Integer/BigInt Types
      else if (columnType.equals("INTEGER") || columnType.equals("BIGINT"))
      {
         // Assign IDENTITY as needed.
         if (columnIsAutoIncrement && autoIncrementCount <= 0)
         {
            tableDefinition.append(columnType + " IDENTITY");
            autoIncrementCount++;
         }
         else
            tableDefinition.append(columnType);
      }
      // Double Types
      else if (columnType.equals("DOUBLE"))
      {
         // Docs indicate precision, but unable to determine.
         tableDefinition.append(columnType);
      }
      // Decimal & Numeric Types
      else if (columnType.equals("DECIMAL") || columnType.equals("NUMERIC"))
      {
         // Oracle deviance of NUMBER.
         if (columnClass.toLowerCase(Locale.ENGLISH).indexOf("double") != -1)
            tableDefinition.append("DOUBLE");
         else
         {
            if (columnPrecision <= 0)
            {
               if (columnScale == 0)
                  tableDefinition.append(columnType);
               else
                  tableDefinition.append(columnType + "(" + Math.abs(columnScale) + ")");
            }
            else
               tableDefinition.append(columnType + "(" + columnPrecision + "," + columnScale + ")");
         }
      }
      // Blob Types
      else if (columnType.equals("BLOB") || columnType.equals("BINARY"))
      {
         if (columnSize <= 0)
            tableDefinition.append(columnType);
         else
            tableDefinition.append(columnType + "(" + columnSize + ")");
      }
      // Clob Types
      else if (columnType.equals("CLOB"))
      {
         tableDefinition.append(columnType + "(" + columnSize + ")");
      }
      // All Others.
      else
      {
         tableDefinition.append(columnType);
      }
   }
   
   //==============================================================
   // Class method to collect the specific DDL for sink data source
   // of a Derby database.
   //==============================================================
   
   private void createDerby_DDL(boolean isIndexColumn)
   {
      // =============
      // Column type.
      
      // Character Types
      if (columnType.indexOf("CHAR") != -1)
      {
         if (columnType.equals("CHAR"))
            tableDefinition.append("CHAR(" + columnSize + ")");
         else if (columnType.equals("VARCHAR"))
         {
            if (columnSize >= 32700 || columnSize <= 0)
                  tableDefinition.append("LONG VARCHAR");
            else
               tableDefinition.append("VARCHAR(" + columnSize + ")");
         }
         else if (columnType.indexOf("FOR BIT DATA") != -1)
         {
            if (columnType.indexOf("VARCHAR") != -1)
            {
               if (columnType.indexOf("LONG") != -1)
                  tableDefinition.append("LONG VARCHAR FOR BIT DATA");
               else
                  tableDefinition.append("VARCHAR(" + columnSize + ") FOR BIT DATA");
            }
            else
               tableDefinition.append("CHAR(" + columnSize + ") FOR BIT DATA");
         }
         else
         {
            // SQLite TEXT
            // Keys can not be LONG VARCHAR
            if (columnType.equals("LONG VARCHAR") && isIndexColumn)
               tableDefinition.append("VARCHAR(1024)");
            else
               tableDefinition.append(columnType);
         }
      }
      // Integer/BigInt Types
      else if (columnType.equals("INTEGER") || columnType.equals("BIGINT"))
      {
         // Assign IDENTITY as needed.
         if (columnIsAutoIncrement  && autoIncrementCount <= 0)
         {
            tableDefinition.append(columnType
                                   + " GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1)");
            autoIncrementCount++;
         }
         else
            tableDefinition.append(columnType);
      }
      // Blob Types
      else if (columnType.equals("BLOB"))
      {
         if (columnSize <= 0)
            tableDefinition.append(columnType);
         else
            tableDefinition.append(columnType + "(" + columnSize + ")");
      }
      // Clob Types
      else if (columnType.equals("CLOB"))
      {
         tableDefinition.append(columnType + "(" + columnSize + ")");
      }
      // Decimal & Numeric Types
      else if (columnType.equals("DECIMAL"))
      {
         // Oracle deviance of NUMBER.
         if (columnClass.toLowerCase(Locale.ENGLISH).indexOf("double") != -1)
            tableDefinition.append("DOUBLE");
         else
         {
            if (columnPrecision > 31)
            {
               columnPrecision = 31;
               
               if (columnScale > columnPrecision)
                  tableDefinition.append(columnType + "(" + columnPrecision + ",31)");
               else
                     tableDefinition.append(columnType + "(" + columnPrecision  + "," + columnScale + ")");
            }
            else
            {
               if (columnPrecision <= 0)
               {
                  if (columnScale == 0)
                     tableDefinition.append(columnType);
                  else
                  {
                     if (Math.abs(columnScale) > 31)
                        columnScale = 31;
                     tableDefinition.append(columnType + "(" + Math.abs(columnScale) + ")");
                  }
               }
               else
                  tableDefinition.append(columnType + "(" + columnPrecision + "," + columnScale + ")");
            }
         }
      }
      // All Others.
      else
      {
         tableDefinition.append(columnType);
      }
   }
   
   //==============================================================
   // Class method to collect the specific DDL for sink data source
   // of a HyperSQL database.
   //==============================================================
   
   private void createHSQL_DDL()
   {
      // =============
      // Column type.

      // Character Types
      if (columnType.indexOf("CHAR") != -1)
      {
         if (columnType.equals("CHAR") || columnType.equals("CHARACTER"))
            tableDefinition.append("CHAR(" + columnSize + ")");
         else
         {
            if (dataSinkType.equals(ConnectionManager.HSQL))
            {
               if (columnType.equals("VARCHAR") || columnType.equals("VARCHAR_IGNORECASE"))
                  tableDefinition.append("VARCHAR(" + columnSize + ")");
               else
                  tableDefinition.append(columnType);
            }
            else
            {
               if (columnSize >= 16777216 || columnPrecision >= 16777216
                   || columnClass.indexOf("Array") != -1 || columnSize <= 0 )
                  tableDefinition.append("LONGVARCHAR");
               else
                  tableDefinition.append("VARCHAR(" + columnSize + ")");      
            }
         }
      }
      // HSQL 2.x Binary Types
      else if (columnType.indexOf("BINARY") != -1 &&
               dataSinkType.equals(ConnectionManager.HSQL2))
      {
         if (columnType.equals("BINARY"))
            tableDefinition.append("BINARY(" + columnSize + ")");
         else
         {
            if (columnSize >= 16777216)
               tableDefinition.append("LONGVARBINARY");
            else
               tableDefinition.append("VARBINARY(" + columnSize + ")");     
         }
      }
      // Blob Types
      else if (columnType.equals("BLOB"))
      {
         if (columnSize <= 0)
            tableDefinition.append(columnType);
         else
            tableDefinition.append(columnType + "(" + columnSize + ")");
      }
      // Integer/BigInt Types
      else if (columnType.equals("INTEGER") || columnType.equals("BIGINT"))
      {
         // Assign IDENTITY as needed.
         if (columnIsAutoIncrement  && autoIncrementCount <= 0)
         {
            tableDefinition.append(columnType + " IDENTITY");
            autoIncrementCount++;
         }
         else
            tableDefinition.append(columnType);  
      }
      // Double Types
      else if (columnType.equals("DOUBLE"))
      {
         // Manual Indicates a precision, but
         // can not create DOUBLE(p).
         tableDefinition.append(columnType);
      }
      // Decimal & Numeric Types
      else if (columnType.equals("DECIMAL") || columnType.equals("NUMERIC"))
      {
         // Oracle deviance of NUMBER.
         if (columnClass.toLowerCase(Locale.ENGLISH).indexOf("double") != -1)
            tableDefinition.append("DOUBLE");
         else
         {
            if (dataSinkType.equals(ConnectionManager.HSQL) && columnType.equals("NUMERIC"))
               tableDefinition.append(columnType);
            else
            {
               if (columnPrecision <= 0)
               {
                  if (columnScale == 0)
                     tableDefinition.append(columnType);
                  else
                     tableDefinition.append(columnType + "(" + Math.abs(columnScale) + ")");
               }
               else
                  tableDefinition.append(columnType + "(" + columnPrecision
                                         + "," + columnScale + ")");
            }
         }
      }
      // Time With Time Zone
      else if (columnType.equals("TIME WITH TIME ZONE"))
      {
         // HSQL2 Defines, precision but can not determine.
         // tableDefinition.append(columnType + "(" + columnPrecision + ")");
         // so....
         
         tableDefinition.append(columnType);
      }
      // Timestamp
      else if (columnType.indexOf("TIMESTAMP") != -1)
      {
         // Column_Size of 29 Seems to Indicate Timestamp(0) for HSQL.
         
         if (dataSinkType.equals(ConnectionManager.HSQL))
         {
            if (columnSize == 29)
               tableDefinition.append(columnType + "(0)");
            else
               tableDefinition.append(columnType);
         }
         else
         {
            // HSQL2 Defines, precision but can not determine.
            // tableDefinition.append(columnType + "(" + columnPrecision + ")");
            // so...
            
            tableDefinition.append(columnType);
         }           
      }
      // Interval (Note: Works, but does not cover all cases.)
      else if (columnType.indexOf("INTERVAL") != -1)
      {
         if (columnType.equals("INTERVAL YEAR"))
            tableDefinition.append(columnType + "(" + columnPrecision + ")");
         else
            tableDefinition.append(columnType);
      }
      // Bit Varying
      else if ((columnType.equals("BIT") || columnType.equals("BIT VARYING"))
               && columnSize != 0)
      {
         tableDefinition.append(columnType + "(" + columnSize + ")");
      }
      // All Others.
      else
      {
         tableDefinition.append(columnType);
      }
   }
   
   //==============================================================
   // Class method to collect/set the SQLite casting option.
   //==============================================================
   
   public boolean isUseSQLiteCast()
   {
      return useSQLiteCast;
   }
   
   public void setUseSQLiteCast(boolean value)
   {
      useSQLiteCast = value;
   }
}
