//=================================================================
//                          SQLQuery
//=================================================================
//
//    This class provides the means to collect in a generic manner
// the characteristics of a SQL query.
//
//                     << SQLQuery.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.7 07/11/2018
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
// Version 1.0 Production SQLQuery Class.
//         1.1 Method getRowCount() Corrected closeConnection() String
//             Description.
//         1.2 Added Methods executeSQL() & getRowCount() With Argument
//             ConnectionInstance.
//         1.3 Moved to utilities.db Package.
//         1.4 Added Class Instances sqlColumnNamesString & sqlOracleColumn
//             NamesString & Setup in Main executeSQL(). Same Method Added
//             Instances identifierQuoteString & isOracleDB. Code Formatting
//             for Instances, One per Line. Added Additional try to Main
//             executeSQL() for Closing db_resultSet. Added Methods getSQL
//             ColumnNamesString() & getSQLOracleColumnNamesString().
//         1.5 Main executeSQL() Changed columnType to columnSQLType. In
//             Same Method Use of Loading columnSQLTypeHashMap With Results
//             for typeof() With SQLite Date, Time, Datetime, & Timestamp
//             columnTypeName. Added Public static Method getTypeof().
//         1.6 Method executeQuery() Insured columnSQLType is Stored as
//             an Integer in columnSQLTypeHashMap. Simplified Assignment
//             of columnSQLType, Maintained Logic.
//         1.7 Added SQLite Temporal Getters, getDate(), getTime/TZ(), &
//             getTimestamp().
//             
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.dandymadeproductions.ajqvue.datasource.ConnectionInstance;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;

/**
 *    The SQLQuery class provides the means to collect in a generic manner
 * the characteristics of a SQL query.   
 * 
 * @author Dana M. Proctor
 * @version 1.7 07/11/2018
 */

public class SQLQuery
{
   // Class Instances.
   private String sqlString;
   private int validQuery;

   private int tableRowLimit;
   
   private ArrayList<String> columnNames;
   private HashMap<String, String> columnClassHashMap;
   private HashMap<String, Integer> columnSQLTypeHashMap;
   private HashMap<String, String> columnTypeNameHashMap;
   private HashMap<String, Integer> columnPrecisionHashMap;
   private HashMap<String, Integer> columnScaleHashMap;
   private HashMap<String, Integer> columnSizeHashMap;
   private HashMap<String, Integer> columnIsNullableHashMap;
   private HashMap<String, Boolean> columnIsAutoIncrementHashMap;
   
   private StringBuilder sqlColumnNamesString;
   private StringBuilder sqlOracleColumnNamesString;
   
   //==============================================================
   // SQLQuery Constructors
   //==============================================================

   public SQLQuery(String sqlString)
   {
      this(sqlString, 1);
   }
   
   public SQLQuery(String sqlString, int queryRowLimit)
   {
      this.sqlString = sqlString;
      tableRowLimit = queryRowLimit;
      
      // Setting up a data source name qualifier and other
      // instances.
      
      validQuery = -1;
      
      columnNames = new ArrayList <String>();
      columnClassHashMap = new HashMap <String, String>();
      columnSQLTypeHashMap = new HashMap <String, Integer>();
      columnTypeNameHashMap = new HashMap <String, String>();
      columnScaleHashMap = new HashMap <String, Integer>();
      columnPrecisionHashMap = new HashMap <String, Integer>();
      columnSizeHashMap = new HashMap <String, Integer>();
      columnIsNullableHashMap = new HashMap <String, Integer>();
      columnIsAutoIncrementHashMap = new HashMap <String, Boolean>();
      
      sqlColumnNamesString = new StringBuilder();
      sqlOracleColumnNamesString = new StringBuilder();
   }
         
   //==============================================================
   // Class method to execute the given user's input SQL statement.
   // Use this method if the login database is the query subject or
   // supply a Database Connection.
   //
   // Output:
   //
   // Invalid Query - (-1)
   // Update No Results - (0)
   // Results - (1)
   //==============================================================

   public int executeSQL()
   {
      // Method Instances
      
      // Setting up a connection.
      Connection dbConnection = ConnectionManager.getConnection("SQLQuery executeSQL()");
      
      try
      {
         executeSQL(dbConnection);
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "SQLQuery executeSQL()");
      }
      
      ConnectionManager.closeConnection(dbConnection, "SQLQuery executeSQL()");
      return validQuery;
   }
   
   public int executeSQL(ConnectionInstance connectionInstance)
   {
      // Method Instances
      
      // Setting up a connection.
      Connection dbConnection = connectionInstance.getConnection("SQLQuery executeSQL()");
      
      try
      {
         executeSQL(dbConnection);
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "SQLQuery executeSQL()");
      }
      
      connectionInstance.closeConnection(dbConnection, "SQLQuery executeSQL()");
      return validQuery;
   }
   
   public int executeSQL(Connection dbConnection) throws SQLException
   {  
      // Method Instances
      String sqlStatementString;
      Statement sqlStatement;
      int updateCount;
      
      String identifierQuoteString;
      boolean isOracleDB;
      
      String colNameString;
      String columnClass;
      int columnSQLType;
      String columnTypeName;
      int columnScale;
      int columnPrecision;
      int columnSize;
      int columnIsNullable;
      boolean columnIsAutoIncrement;
      
      ResultSet db_resultSet;
      ResultSetMetaData tableMetaData;

      // Checking to see if anything in the input to
      // execute or valid connection.
      
      if (sqlString.length() < 1 || dbConnection == null)
         return validQuery;
      
      // Connecting to the data base, to obtain
      // meta data, and column names.
      
      db_resultSet = null;
      sqlStatement = null;
      
      identifierQuoteString = dbConnection.getMetaData().getIdentifierQuoteString();
      
      if (dbConnection.getMetaData().getDatabaseProductName().toUpperCase(Locale.ENGLISH).indexOf(
         "ORACLE") != -1)
         isOracleDB = true;
      else
         isOracleDB = false;
      
      try
      {
         sqlStatement = dbConnection.createStatement();
         sqlStatement.setMaxRows(tableRowLimit);

         sqlStatementString = sqlString;
         // System.out.println(sqlStatementString);
         
         sqlStatement.execute(sqlStatementString);
         updateCount = sqlStatement.getUpdateCount();
         
         // Collect results.
         if (updateCount == -1)
         {
            db_resultSet = sqlStatement.getResultSet();
            
            // Check to see if there are any results.
            
            if (db_resultSet == null)
            {
               // Fill information instances.
               colNameString = "Result";
               columnClass = "java.lang.String";
               columnSQLType = Types.VARCHAR;
               columnTypeName = "VARCHAR";
               columnScale = 0;
               columnPrecision = 0;
               columnSize = 30;
               columnIsNullable = ResultSetMetaData.columnNoNulls;
               columnIsAutoIncrement = false;
               
               columnNames.add(colNameString);
               columnClassHashMap.put(colNameString, columnClass);
               columnSQLTypeHashMap.put(colNameString, Integer.valueOf(columnSQLType));
               columnTypeNameHashMap.put(colNameString, columnTypeName.toUpperCase(Locale.ENGLISH));
               columnScaleHashMap.put(colNameString, Integer.valueOf(columnScale));
               columnPrecisionHashMap.put(colNameString, Integer.valueOf(columnPrecision));
               columnSizeHashMap.put(colNameString, Integer.valueOf(columnSize));
               columnIsNullableHashMap.put(colNameString, Integer.valueOf(columnIsNullable));
               columnIsAutoIncrementHashMap.put(colNameString, Boolean.valueOf(columnIsAutoIncrement));
               
               validQuery = 0;
               return validQuery;
            }
            
            // Have results so setting Up the column names, and collecting
            // information about columns.
            
            tableMetaData = db_resultSet.getMetaData();
            
            // System.out.println("SQLQUERY executeSQL()\n"
            //                    + "index" + "\t" + "Name" + "\t" + "Class" + "\t"
            //                    + "Type" + "\t" + "Type Name" + "\t" + "Scale"
            //                    + "\t" + "Precision" + "\t" + "Size");

            for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
            {
               colNameString = tableMetaData.getColumnLabel(i);
               columnClass = tableMetaData.getColumnClassName(i);
               columnSQLType = tableMetaData.getColumnType(i);
               columnTypeName = tableMetaData.getColumnTypeName(i);
               columnScale = tableMetaData.getScale(i);
               columnPrecision = tableMetaData.getPrecision(i);
               columnSize = tableMetaData.getColumnDisplaySize(i);
               columnIsNullable = tableMetaData.isNullable(i);
               columnIsAutoIncrement = tableMetaData.isAutoIncrement(i);
               
               // System.out.println(i + "\t" + colNameString + "\t" +
               //                    columnClass + "\t" + columnSQLType + "\t" +
               //                    columnTypeName + "\t" + columnScale + "\t" +
               //                    columnPrecision + "\t" + columnSize);

               // This going to be a problem so skip these columns.
               // NOT TESTED. This is still problably not going to
               // help. Bound to crash later.

               if (columnClass == null && columnTypeName == null)
                  continue;

               // Handle some Oracle data types that have a null
               // class type and possibly others.

               if (columnClass == null)
               {
                  if (columnTypeName.toUpperCase(Locale.ENGLISH).equals("BINARY_FLOAT"))
                  {
                     columnClass = "java.lang.Float";
                     columnSQLType = Types.FLOAT;
                     columnTypeName = "FLOAT";
                  }
                  else if (columnTypeName.toUpperCase(Locale.ENGLISH).equals("BINARY_DOUBLE"))
                  {
                     columnClass = "java.lang.Double";
                     columnSQLType = Types.DOUBLE;
                     columnTypeName = "DOUBLE";
                  }
                  else
                     columnClass = "java.lang.Object";
               }

               columnNames.add(colNameString);
               
               if (dbConnection.getMetaData().getDatabaseProductName().toUpperCase(Locale.ENGLISH).indexOf(
                     "SQLITE") != -1
                   && (columnTypeName.toUpperCase(Locale.ENGLISH).equals("DATE")
                       || columnTypeName.toUpperCase(Locale.ENGLISH).equals("TIME")
                       || columnTypeName.toUpperCase(Locale.ENGLISH).indexOf("DATETIME") != -1
                       || columnTypeName.toUpperCase(Locale.ENGLISH).equals("TIMESTAMP")))
               {  
                  int type = getTypeof(dbConnection, sqlString, colNameString);
                    
                  if (type != Types.NULL)
                     columnSQLType = type;
               }
               
               columnTypeNameHashMap.put(colNameString, columnTypeName.toUpperCase(Locale.ENGLISH));
               columnClassHashMap.put(colNameString, columnClass);
               columnSQLTypeHashMap.put(colNameString, Integer.valueOf(columnSQLType));
               columnScaleHashMap.put(colNameString, Integer.valueOf(columnScale));
               columnPrecisionHashMap.put(colNameString, Integer.valueOf(columnPrecision));
               columnSizeHashMap.put(colNameString, Integer.valueOf(columnSize));
               columnIsNullableHashMap.put(colNameString, Integer.valueOf(columnIsNullable));
               columnIsAutoIncrementHashMap.put(colNameString, Boolean.valueOf(columnIsAutoIncrement));
               
               // Quoted modified column names string as needed for
               // Oracle TIMESTAMPLTZ Fields.
               
               if (isOracleDB && columnTypeName.equals("TIMESTAMPLTZ"))
               {
                  sqlOracleColumnNamesString.append("TO_CHAR(" + identifierQuoteString + colNameString
                                                 + identifierQuoteString
                                                 + ", 'YYYY-MM-DD HH24:MM:SS TZR') AS "
                                                 + identifierQuoteString + colNameString
                                                 + identifierQuoteString + ", ");
               }
               else
                  sqlOracleColumnNamesString.append(identifierQuoteString + colNameString
                                                 + identifierQuoteString + ", ");
               // Unmodified Quoted Names.
               sqlColumnNamesString.append(identifierQuoteString + colNameString
                                           + identifierQuoteString + ", ");
            }
            if (sqlColumnNamesString.length() != 0)
            {
               sqlOracleColumnNamesString.delete((sqlOracleColumnNamesString.length() - 2),
                                                  sqlOracleColumnNamesString.length());
               sqlColumnNamesString.delete((sqlColumnNamesString.length() - 2),
                                            sqlColumnNamesString.length());
            }
            
            // Looks good so validate.
            validQuery = 1; 
         }
         // No results, data, but was update, maybe.
         else
         {
            validQuery = 0;
         }
         return validQuery;
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "SQLQuery executeSQL()");
         validQuery = -1;
         return validQuery;
      }
      finally
      {
         try
         {
            if (db_resultSet != null) 
               db_resultSet.close();
            
         }
         catch (SQLException e)
         {
            ConnectionManager.displaySQLErrors(e, "SQLQuery executeSQL()");
            validQuery = -1;
            return validQuery;
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }   
      }
   }
   
   //==============================================================
   // Class method to obtain additional information about columns,
   // via the typeof() function with the SQLite database
   //==============================================================

   public static int getTypeof(Connection dbConnection, String query, String colNameString)
   {
      // Method Instances
      String sqlStatementString;
      Statement sqlStatement;
      ResultSet db_resultSet;
      
      String result;
      int type;

      // Connecting to the data base, to obtain
      // meta data, and column names.
      
      sqlStatement = null;
      db_resultSet = null;
      type = Types.NULL;
      
      try
      {
         sqlStatement = dbConnection.createStatement();
         sqlStatementString = "SELECT typeof(" + colNameString + ") FROM (" + query.replaceAll(";", "")
                              + ") LIMIT 1";
         // System.out.println(sqlStatementString);

         db_resultSet = sqlStatement.executeQuery(sqlStatementString);

         while (db_resultSet.next())
         {
            result =  db_resultSet.getString(1);
            
            if (result.equalsIgnoreCase("INTEGER"))
               type =  Types.INTEGER;
            else if (result.equalsIgnoreCase("TEXT"))
               type = Types.VARCHAR;
            else if (result.equalsIgnoreCase("REAL"))
               type = Types.REAL;
            else if (result.equalsIgnoreCase("BLOB"))
               type = Types.BLOB;
            else
               type = Types.NULL;
         }
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "SQLQuery getTypeof()");
      }
      finally
      {
         try
         {
            if (db_resultSet != null)
               db_resultSet.close();
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle, "SQLQuery getTypeof()");
         }
      }
      // System.out.println("TableTabPanel_SQLite getTypeof() columnName: " + colNameString + "-" + type);
      return type;
   }
   
   //==============================================================
   // Class methods to collect temporal data for SQLite based on
   // the defined SQL Type, columnSQLType.
   //==============================================================
   
   public static Object getDate(ResultSet resultSet, int columnSQLType, String columnName)
         throws SQLException
   {
      // Method Instances.
      Object dateObject;
      
      if (columnSQLType == Types.NULL || columnSQLType == Types.INTEGER
          || columnSQLType == Types.REAL)
         dateObject = resultSet.getDate(columnName);
      else
         dateObject = resultSet.getString(columnName);
      
      if (dateObject != null)
         return dateObject + "";
      else
         return null;
   }
   
   public static Object getTime(ResultSet resultSet, int columnSQLType, String columnName)
         throws SQLException
   {
      Object timeObject;
      
      if (columnSQLType == Types.NULL || columnSQLType == Types.INTEGER
          || columnSQLType == Types.REAL)
      {
         timeObject = resultSet.getTime(columnName);
         
         if (timeObject != null)
            timeObject = (new SimpleDateFormat("HH:mm:ss").format(timeObject));
      }
      else
         timeObject = resultSet.getString(columnName);
      
      return timeObject;
   }
   
   public static Object getTimeTZ(ResultSet resultSet, int columnSQLType, String columnName)
         throws SQLException
   {
      Object timeObject;
      
      if (columnSQLType == Types.NULL || columnSQLType == Types.INTEGER
          || columnSQLType == Types.REAL)
      {
         timeObject = resultSet.getTime(columnName);
         
         if (timeObject != null)
            timeObject = new SimpleDateFormat("HH:mm:ss z").format(timeObject);
      }
      else
         timeObject = resultSet.getString(columnName);
      
      return timeObject;
   }
   
   public static Object getTimestamp(ResultSet resultSet, int columnSQLType, String columnTypeName,
                               String columnName) throws SQLException
   {
      // Method Instances.
      Object timestampObject;
      String dateString;
      String timeString;
      
      if (columnSQLType == Types.NULL || columnSQLType == Types.INTEGER
          || columnSQLType == Types.REAL)
      {
         timestampObject = resultSet.getTimestamp(columnName);
         
         if (timestampObject == null)
            return null;
         
         if (columnTypeName.equals("DATETIME"))
            return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampObject));
         else if (columnTypeName.equals("TIMESTAMP"))
            return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(timestampObject));
         // TIMESTAMPTZ
         else
           return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(timestampObject));   
      }
      else
      {
         timestampObject = resultSet.getString(columnName);
         
         if (timestampObject == null)
            return null;
         
         if (((String) timestampObject).indexOf(" ") != -1)
         {
            dateString = timestampObject + "";
            dateString = dateString.substring(0, (dateString.indexOf(" ")));

            timeString = timestampObject + "";
            timeString = timeString.substring(timeString.indexOf(" "));
            timestampObject = dateString + timeString;
            
            return timestampObject;
         }
         else
            throw new SQLException("Timestamp String Invalid Format");  
      }
   }
   
   //==============================================================
   // Class method to allow classes to obtain the list of column
   // names that is the result of the query.
   //==============================================================

   public ArrayList<String> getColumnNames()
   {
      return columnNames;
   }

   //==============================================================
   // Class method to allow classes to obtain the column, Java
   // classes, HashMap.
   //==============================================================

   public HashMap<String, String> getColumnClassHashMap()
   {
      return columnClassHashMap;
   }
   
   //==============================================================
   // Class method to allow classes to obtain the column, SQL
   // types, HashMap.
   //==============================================================

   public HashMap<String, Integer> getColumnSQLTypeHashMap()
   {
      return columnSQLTypeHashMap;
   }

   //==============================================================
   // Class method to allow classes to obtain the column, database
   // names, HashMap.
   //==============================================================

   public HashMap<String, String> getColumnTypeNameHashMap()
   {
      return columnTypeNameHashMap;
   }
   
   //==============================================================
   // Class method to allow classes to obtain the column scales
   // HashMap.
   //==============================================================

   public HashMap<String, Integer> getColumnScaleHashMap()
   {
      return columnScaleHashMap;
   }
   
   //==============================================================
   // Class method to allow classes to obtain the column precisions
   // HashMap.
   //==============================================================

   public HashMap<String, Integer> getColumnPrecisionHashMap()
   {
      return columnPrecisionHashMap;
   }

   //==============================================================
   // Class method to allow classes to obtain the column sizes
   // HashMap.
   //==============================================================

   public HashMap<String, Integer> getColumnSizeHashMap()
   {
      return columnSizeHashMap;
   }
   
   //==============================================================
   // Class method to allow classes to obtain the column nullable
   // HashMap.
   //==============================================================

   public HashMap<String, Integer> getColumnIsNullableHashMap()
   {
      return columnIsNullableHashMap;
   }
   
   //==============================================================
   // Class method to allow classes to obtain the column auto
   // increment HashMap.
   //==============================================================

   public HashMap<String, Boolean> getColumnIsAutoIncrementHashMap()
   {
      return columnIsAutoIncrementHashMap;
   }
   
   //==============================================================
   // Class method to allow classes to obtain the identifier quoted
   // standard column names and Oracle modified same.
   //==============================================================

   public StringBuilder getSqlColumnNamesString()
   {
      return sqlColumnNamesString;
   }
   
   public StringBuilder getSqlOrcaleColumnNamesString()
   {
      return sqlOracleColumnNamesString;
   }
   
   //==============================================================
   // Class method to allow classes to obtain the row count from
   // the defined query.
   //==============================================================
   
   public int getRowCount()
   {
      // Method Instances
      int rowCount;
      
      // Setting up a connection.
      Connection dbConnection = ConnectionManager.getConnection("SQLQuery getRowCount()");
      
      rowCount = getRowCount(dbConnection, ConnectionManager.getDataSourceType());
      
      ConnectionManager.closeConnection(dbConnection, "SQLQuery getRowCount()");
      
      return rowCount;
   }
   
   public int getRowCount(ConnectionInstance connectionInstance)
   {
      // Method Instances
      int rowCount;
      
      // Setting up a connection.
      Connection dbConnection = connectionInstance.getConnection("SQLQuery getRowCount()");
      
      rowCount = getRowCount(dbConnection, connectionInstance.getDataSourceType());
      
      connectionInstance.closeConnection(dbConnection, "SQLQuery getRowCount()");
      
      return rowCount;
   }

   public int getRowCount(Connection dbConnection, String dataSourceType)
   {      
      // Method Instances
      String sqlStatementString;
      Statement sqlStatement;
      ResultSet rs;
      
      // Check if SELECT or valid query.
      if (validQuery != 1)
         return 0;
      
      // Setup
      int rowCount = 0;
      sqlStatement = null;
      rs = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();
         sqlStatementString = "SELECT COUNT(*) AS row_count FROM (" + sqlString;
         
         if (dataSourceType.equals(ConnectionManager.MYSQL)
             || dataSourceType.equals(ConnectionManager.MARIADB)
             || dataSourceType.equals(ConnectionManager.POSTGRESQL)
             || dataSourceType.equals(ConnectionManager.DERBY)
             || dataSourceType.equals(ConnectionManager.MSSQL))
              sqlStatementString += ") AS AS1";
           else
              sqlStatementString += ")";
         // System.out.println("SQLQuery getRowCount() " + sqlStatementString);
         
         rs = sqlStatement.executeQuery(sqlStatementString);
         
         rs.next();
         rowCount = rs.getInt("row_count");
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "SQLQuery getRowCount()");
      }
      finally
      {
         try
         {
            if (rs != null)
               rs.close();
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle, "SQLQuery getRowCount()");
         }
         finally
         {
            try
            {
               if (sqlStatement != null)
                  sqlStatement.close();
            }
            catch (SQLException sqle)
            {
               ConnectionManager.displaySQLErrors(sqle, "SQLQuery getRowCount()");
            }
         }
      }
      
      return rowCount;
   }
   
   //==============================================================
   // Class method to allow classes to obtain the query used for
   // the object.
   //==============================================================

   public String getSQLQuery()
   {
      return sqlString;
   }
   
   //==============================================================
   // Class method to allow classes to see if the query is valid.
   //==============================================================

   public int getValidQuery()
   {
      return validQuery;
   }
}