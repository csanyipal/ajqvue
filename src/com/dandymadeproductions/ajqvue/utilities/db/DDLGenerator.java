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
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.8 05/25/2018
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
//         1.1 09/18/2016 Corrected Import of Ajqvue Class.
//         1.2 12/26/2017 Replaced Static Class Instance IDENTIFIER_QUOTE_STRING
//                        With Same Argument in getDDL() Removed getDDL() Argument
//                        schemaName, Replaced With catalogSeparator &
//                        identifierQuoteString. Used Utils.getSchemaTableName()
//                        in Same.
//         1.3 05/05/2018 Method createHSQL_DDL() Insured Binary Assigned Size
//                        1 or columnSize is Zero.
//         1.4 05/13/2018 Method createDerby_DDL() Changed Scale for Derby When Precision
//                        Definitions Beyound Max, 31, Value. Reduced to Reasonable
//                        Size Instead of Same. Allows Larger Precision to be Displayed,
//                        Stored. HSQL Numeric Conversion.
//         1.5 05/14/2018 Method createDerby_DDL() Changed CHAR/VARCHAR BIT FOR DATA
//                        Size to Use columnPrecision.
//         1.6 05/15/2018 Method createH2_DDL() & createHSQL_DDL() Changed Binary &
//                        VarBinary Types for Derby dataSourceType to Use columnPrecision
//                        for Size.
//         1.7 05/21/2018 Method getDDL() Added Conditional to Check for Source DB of
//                        SQLite So That a SQL Type Conversion Can be Called. Added
//                        Method convertToSQLiteType().
//         1.8 05/25/2018 Code Formatting Changes for Class Instances. Main Method getDDL()
//                        Call to infoCache.getType() Added Column SQL Type From SQLQuery
//                        columnSQLTypeHashMap as Argument.
//
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

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionInstance;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.TypesInfoCache;

/**
 *    The DDLGenerator class provides the basis for creating a condensed
 * data definition language statement that bodes the results of converting
 * a given database query to an alternate database table. 
 * 
 * @author Dana M. Proctor
 * @version 1.8 05/25/2018
 */

public class DDLGenerator
{
   // Class Instances.
   private String dataSourceType;
   private String dataSinkType;
   private int indexCount, autoIncrementCount;
   
   private SQLQuery sqlQuery;
   private TypesInfoCache infoCache;
   private boolean useSQLiteCast;
   
   private String columnName;
   private String columnClass;
   private String columnType;
   
   private int columnPrecision;
   private int columnScale;
   private int columnSize;
   private int columnIsNullable;
   private boolean columnIsAutoIncrement;
   
   private StringBuffer tableDefinition;
   private ArrayList<String> indexList;
   
   public static final String DEFAULT_DATASINK_TYPE = ConnectionManager.HSQL2;
   public static final int INDEXCOUNT = 1;
   
   //==============================================================
   // SQLQuery Constructors
   //
   // Use the first six for Ajqvue login database source or
   // specify in seventh.
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
      this.dataSourceType = dataSourceType;
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
   
   public String getDDL(String tableName, String catalogSeparator, String identifierQuoteString)
   {
      // Method Instances
      Connection dbConnection;
      StringBuffer tempBuffer;
      
      // Setting up a connection.
      dbConnection = ConnectionManager.getConnection("DDLGenerator getDDL()");
      tempBuffer = new StringBuffer();
      
      tempBuffer.append(getDDL(dbConnection, tableName, catalogSeparator, identifierQuoteString));
      
      ConnectionManager.closeConnection(dbConnection, "DDLGenerator getDDL()");
      return tempBuffer.toString();
   }
   
   public String getDDL(Connection dbConnection, String tableName, String catalogSeparator,
                        String identifierQuoteString)
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
      schemaTableName = Utils.getSchemaTableName(tableName, catalogSeparator, identifierQuoteString);
         
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
         
         // Map directly from definition.
         if (dataSinkType.equals(ConnectionManager.SQLITE) && useSQLiteCast)
            columnType = sqlQuery.getColumnTypeNameHashMap().get(columnName);
         
         else if (dataSourceType.equals(ConnectionManager.SQLITE))
            columnType = infoCache.getType(sqlQuery.getColumnSQLTypeHashMap().get(columnName),
                                           convertToSQLiteType(
                                              sqlQuery.getColumnTypeNameHashMap().get(columnName)));
         else
            columnType = infoCache.getType(sqlQuery.getColumnSQLTypeHashMap().get(columnName),
                                           sqlQuery.getColumnTypeNameHashMap().get(columnName));
         
         columnPrecision = ((sqlQuery.getColumnPrecisionHashMap().get(columnName)).intValue());
         columnScale = (sqlQuery.getColumnScaleHashMap().get(columnName)).intValue();
         columnSize = (sqlQuery.getColumnSizeHashMap().get(columnName)).intValue();
         columnIsNullable = (sqlQuery.getColumnIsNullableHashMap().get(columnName).intValue());
         columnIsAutoIncrement = (sqlQuery.getColumnIsAutoIncrementHashMap().get(columnName).booleanValue());
         
         // =============
         // Column name.

         tableDefinition.append(identifierQuoteString + columnName + identifierQuoteString + " ");
         
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
                                + identifierQuoteString + indexList.get(0) + identifierQuoteString
                                + " ON " + schemaTableName + "(");
         Iterator<String> indexListIterator = indexList.iterator();
         
         while (indexListIterator.hasNext())
         {
            tableDefinition.append(identifierQuoteString + indexListIterator.next()
                                   + identifierQuoteString + ", ");
         }
         tableDefinition.delete(tableDefinition.length() - 2, tableDefinition.length());
         tableDefinition.append(");\n");
      }
      return tableDefinition.toString();
   }
   
   //==============================================================
   // Class method to try and convert a defined SQL type to one of
   // the defined SQLites types if possible.
   //
   // This is required because SQLite allows table definitions to
   // be stored directly in essentially with field type of anything,
   // "WhattZZa". It does not care because if the type does not fall
   // into a SQL type or one of its own, it just becomes NONE.
   //
   // SQLite Documentation - 2.2 Affinity Name Examples.
   //==============================================================
   
   private String convertToSQLiteType(String columnType)
   {
      if (columnType.indexOf("CHAR") != -1
          || columnType.indexOf("CLOB") != -1)
      {
         return "TEXT";
      }   
      else if (columnType.indexOf("BYTEA") != -1)
      {
         return "BLOB";
      }
      else if (columnType.indexOf("NUMERIC") != -1
            || columnType.indexOf("DECIMAL") != -1
            || columnType.indexOf("BOOLEAN") != -1
            || columnType.indexOf("DATE") != -1
            || columnType.indexOf("DATETIME") != -1
            || columnType.indexOf("TIMESTAMP") != -1
            || columnType.indexOf("YEAR") != -1)
      {
         return "NUMERIC";
      }
      else if (columnType.indexOf("INT") != -1
          || columnType.indexOf("TINYINT") != -1
          || columnType.indexOf("SMALLINT") != -1
          || columnType.indexOf("MEDIUMINT") != -1
          || columnType.indexOf("BIGINT") != -1
          || columnType.indexOf("INT2") != -1
          || columnType.indexOf("INT4") != -1
          || columnType.indexOf("INT8") != -1
          || columnType.indexOf("SERIAL") != -1
          )
      {
         return "INTEGER";
      }
      else if (columnType.indexOf("REAL") != -1
               || columnType.indexOf("DOUBLE") != -1
               || columnType.indexOf("FLOAT") != -1)
      {
         return "REAL";
      }  
      else
         return columnType;
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
         {
            if (dataSourceType.equals(ConnectionInstance.DERBY) && columnType.equals("BINARY"))
               tableDefinition.append(columnType + "(" + columnPrecision + ")");
            else
               tableDefinition.append(columnType + "(" + columnSize + ")");
         }
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
                  tableDefinition.append("VARCHAR(" + columnPrecision + ") FOR BIT DATA");
            }
            else
               tableDefinition.append("CHAR(" + columnPrecision + ") FOR BIT DATA");
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
               
               // Derby requires a reasonalble trade of where columnPrecesion
               // is the max value and precsion is higher, so pick a lower scale.
               
               if (columnScale > columnPrecision)
                  tableDefinition.append(columnType + "(" + columnPrecision + ",16)");
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
         // User Guide specifies Binary is reserved for UUID.
         
         if (columnType.equals("BINARY"))
         {
            if (columnSize == 0)
               tableDefinition.append("BINARY(1)");
            else
            {
               if (dataSourceType.equals(ConnectionInstance.DERBY))
                  tableDefinition.append("BINARY(" + columnPrecision + ")");
               else
                  tableDefinition.append("BINARY(" + columnSize + ")");
            }
         }
         else
         {
            if (columnSize >= 16777216)
               tableDefinition.append("LONGVARBINARY");
            else
            {
               if (dataSourceType.equals(ConnectionInstance.DERBY))
                  tableDefinition.append("VARBINARY(" + columnPrecision + ")");
               else
                  tableDefinition.append("VARBINARY(" + columnSize + ")");
            }
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
