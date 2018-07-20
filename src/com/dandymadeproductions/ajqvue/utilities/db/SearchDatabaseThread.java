//=================================================================
//                  SearchDatabaseThread Class
//=================================================================
//
//    This class provides a thread to search through all the
// database tables for a given input string..
//
//                << SearchDatabaseThread.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.2 07/20/2018
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
// Version 1.0 Production SearchDatabase Class.
//         1.1 Moved to utilities.db Package. Updated Import for Utils.
//         1.2 Rebuilt Method createColumnsSQLQuery() to Use SQLQuery
//             & Handle SQLite, Affinity.
//         
//-----------------------------------------------------------------
//                  danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JProgressBar;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.panels.TableTabPanel_SQLite;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The SearchDatabaseThread class provides a thread to search
 * through all the database tables for a given input string.
 * 
 * @author Dana Proctor
 * @version 1.2 07/20/2018
 */

public class SearchDatabaseThread implements Runnable
{
   // Class Instances
   private ArrayList<String> databaseTables;
   private boolean[] selectedTables;
   private String searchQueryString;
   private JProgressBar searchProgressBar;
   private JButton searchCompleteButton;
   
   private int[] tableSearchResultCounts;
   private int resultsCount;
   private Object[][] resultData;
   private boolean cancelSearch;

   //==============================================================
   // SearchDatabaseThread Constructor
   //==============================================================

   public SearchDatabaseThread(ArrayList<String> databaseTables, boolean[] selectedTables, String searchString,
                               JProgressBar progressBar, JButton searchCompleteButton)
   {
      this.databaseTables = databaseTables;
      this.selectedTables = selectedTables.clone();
      this.searchQueryString = searchString;
      this.searchProgressBar = progressBar;
      this.searchCompleteButton = searchCompleteButton;

      cancelSearch = false;
   }
   
   //==============================================================
   // Class method for normal start of the thread
   //==============================================================

   public void run()
   {
      // Method Instances
      final Connection dbConnection;

      Thread[] tableSearchThreads;
      String columnsSQLQuery, sqlTable;
      String identifierQuoteString, schemaTableName;

      dbConnection = ConnectionManager.getConnection("SearchDatabaseThread queryDatabase()");
      
      if (dbConnection == null)
         return;

      // Setting up various instances needed.
      
      identifierQuoteString = ConnectionManager.getIdentifierQuoteString();
      tableSearchThreads = new Thread[databaseTables.size()];
      tableSearchResultCounts = new int[databaseTables.size()];

      // Fill search count results array with -1 so we know when
      // there is problem with the query for the table.
      
      for (int i = 0; i < tableSearchResultCounts.length; i++)
         tableSearchResultCounts[i] = -1;

      // =====================================================
      // Begin cycling through the tables, creating the search
      // query and executing each as a separate thread.

      resultsCount = 0;
      int i = 0;
      
      do
      {
         final int index = i;
         final String searchQuery;
         
         // Optimize by not bothering with excluded
         // tables.
         
         if (selectedTables[i] == false)
         {
            resultsCount++;
            i++;
            continue;
         }
         
         // Properly format the string used in the query
         // for the table.

         sqlTable = databaseTables.get(index);
         schemaTableName = Utils.getSchemaTableName(sqlTable);

         // Create the search query.
         columnsSQLQuery = "";
         
         try
         {
            columnsSQLQuery = createColumnsSQLQuery(dbConnection, schemaTableName,
                                                    searchQueryString);
         }
         catch (SQLException e)
         {
            ConnectionManager.displaySQLErrors(e, "SearchDatabaseThread run()");
         }

         // Problems creating the search, columns, query will be 
         // return as a empty string or table to be not searched
         // so go to next table, but still allow the table result
         // to be displayed, Will be invalid -1.
         
         if (columnsSQLQuery.equals(""))
         {
            resultsCount++;
            i++;
            continue;
         }

         // Actual complete search query.
         searchQuery = "SELECT COUNT(*) AS " + identifierQuoteString + "Count"
                       + identifierQuoteString + " FROM " + schemaTableName
                       + " WHERE " + columnsSQLQuery;
         // System.out.println(searchQuery);
         searchProgressBar.setValue(index + 1);

         // Inner class to execute the query as a new thread.
         tableSearchThreads[i] = new Thread(new Runnable()
         {
            public void run()
            {
               Statement sqlStatement = null;
               ResultSet rs = null;
               
               try
               {
                  sqlStatement = dbConnection.createStatement();
                  rs = sqlStatement.executeQuery(searchQuery);
                  while (rs.next())
                  {
                     int resultCount = rs.getInt(1);
                     tableSearchResultCounts[index] = resultCount;
                     if (resultCount > 0)
                        resultsCount++;
                  }
               }
               catch (SQLException e)
               {
                  if (Ajqvue.getDebug())
                     System.out.println("SearchDatabaseThread run() " + e.toString());
                  
                  resultsCount++;
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
                     ConnectionManager.displaySQLErrors(sqle, "SearchDatabaseThread run()");
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
                        ConnectionManager.displaySQLErrors(sqle, "SearchDatabaseThread run()");
                     }
                  }
               }
            }
         });
         tableSearchThreads[i].start();
         i++;
      }
      while (i < databaseTables.size() && !cancelSearch);

      // ===============================================
      // Queries now execute via a new thread for each,
      // so wait for all to complete.
      
      for (int j = 0; j < databaseTables.size(); j++)
      {
         try
         {
            if (tableSearchThreads[j] != null)
               tableSearchThreads[j].join();
         }
         catch (InterruptedException e)
         {
            if (Ajqvue.getDebug())
               System.out.println("SearchDatabaseThread run() " + e.toString());
         }
      }

      // =================================
      // Create data object with results.
      
      resultData = new Object[resultsCount][3];
      
      int j = 0;
      int k = 0;
      
      while (j < databaseTables.size() && k < resultsCount)
      {
         if (tableSearchResultCounts[j] != 0)
         {
            resultData[k][0] = Boolean.valueOf(selectedTables[j]);
            resultData[k][1] = databaseTables.get(j);
            resultData[k++][2] = Integer.valueOf(tableSearchResultCounts[j]);
         }
         j++;
      }
      
      searchCompleteButton.doClick();
      ConnectionManager.closeConnection(dbConnection, "SearchDatabaseThread queryDatabase()");
   }
   
   //==============================================================
   // Class Method to create the given input table columns search
   // SQL query LIKE clause.
   //==============================================================

   private String createColumnsSQLQuery(Connection dbConnection, String tableName,
                                        String searchQueryString) throws SQLException
   {
      // Method Instances
      SQLQuery sqlQuery;
      StringBuffer columnsSQLQuery;
      String dataSourceType, sqlColumnSelectString;
      String identifierQuoteString;

      // Setting up.
      columnsSQLQuery = new StringBuffer();
      identifierQuoteString = ConnectionManager.getIdentifierQuoteString();

      // Beginning creating the table columns
      // search string query.
      
      try
      {
         // Create query to obtain the table's Meta Data.
         dataSourceType = ConnectionManager.getDataSourceType();
         
         // HSQL
         if (dataSourceType.equals(ConnectionManager.HSQL))
            sqlColumnSelectString = "SELECT LIMIT 0 1 * FROM " + tableName;
         // Oracle
         else if (dataSourceType.equals(ConnectionManager.ORACLE))
            sqlColumnSelectString = "SELECT * FROM " + tableName + " WHERE ROWNUM=1";
         // MS Access
         else if (dataSourceType.equals(ConnectionManager.MSACCESS))
            sqlColumnSelectString = "SELECT * FROM " + tableName;
         // MSSQL
         else if (dataSourceType.equals(ConnectionManager.MSSQL))
            sqlColumnSelectString = "SELECT TOP 1 * FROM " + tableName;
         // Derby
         else if (dataSourceType.equals(ConnectionManager.DERBY))
            sqlColumnSelectString = "SELECT * FROM " + tableName + " FETCH FIRST ROW ONLY";
         // MySQL, PostgreSQL, & Others
         else
            sqlColumnSelectString = "SELECT * FROM " + tableName + " LIMIT 1";

         // System.out.println(sqlColumnSelectString);
         
         sqlQuery = new SQLQuery(sqlColumnSelectString);
         
         if (sqlQuery.executeSQL(dbConnection) != 1)
            return "";
         
         // Cycling through the table's columns and adding
         // to the SQL search query string. Exclude any column
         // that is binary in nature.
         
         for (int k = 0; k < sqlQuery.getColumnNames().size(); k++)
         {
            // Collect Information on Column.
            String columnName = sqlQuery.getColumnNames().get(k);
            String columnClass = sqlQuery.getColumnClassHashMap().get(columnName);
            int columnSQLType = (sqlQuery.getColumnSQLTypeHashMap().get(columnName)).intValue();
            String columnTypeName = sqlQuery.getColumnTypeNameHashMap().get(columnName);
            
            // System.out.println(k + ": " + columnName + " " + columnClass + " " + columnSQLType + " "
            //                    + columnTypeName);
            
            String searchString = searchQueryString;

            // Exclude binary & file column types.
            if (!Utils.isBlob(columnClass, columnTypeName)
                && columnTypeName.indexOf("FILE") == -1)
            {
               // Convert date, datetime, timestamp search string
               // to proper format.
               if (columnTypeName.equals("DATE"))
               {
                  if (dataSourceType.equals(ConnectionManager.ORACLE))
                  {
                     searchString = Utils.processDateFormatSearch(searchString);
                     
                     // Something not right in conversion.
                     if (searchString.equals("0") || searchString.equals(searchQueryString))
                     {
                        searchString = searchQueryString;
                        // Do not process as DATE.
                        columnTypeName = "";
                     }
                  }
                  else
                  {
                     searchString = Utils.processDateFormatSearch(searchString);
                     
                     // Something not right in conversion.
                     if (searchString.equals("0"))
                        searchString = searchQueryString;
                  }
               }
               else if ((columnTypeName.indexOf("DATETIME") != -1)
                         || (columnTypeName.equals("TIMESTAMP") && !dataSourceType.equals(ConnectionManager.MSSQL))
                         || (columnTypeName.equals("TIMESTAMPTZ")))
               {
                  if (searchString.indexOf(" ") != -1)
                     searchString = Utils.processDateFormatSearch(
                        searchString.substring(0, searchString.indexOf(" ")))
                        + searchString.substring(searchString.indexOf(" "));
                  else if (searchString.indexOf("-") != -1 || searchString.indexOf("/") != -1)
                     searchString = Utils.processDateFormatSearch(searchString);
               }
               
               // Create search query.
               if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
                  columnsSQLQuery.append(identifierQuoteString + columnName + identifierQuoteString
                                     + "::TEXT LIKE \'%" + searchString + "%\' OR ");
               else if (dataSourceType.equals(ConnectionManager.DERBY))
               {
                  if (columnClass.indexOf("STRING") != -1)
                     columnsSQLQuery.append(identifierQuoteString + columnName + identifierQuoteString
                        + " LIKE \'%" + searchString + "%\' OR "); 
                  else if (columnTypeName.equals("DOUBLE") || columnTypeName.equals("REAL"))
                     columnsSQLQuery.append(identifierQuoteString + columnName + identifierQuoteString
                        + "=" + searchString + " OR ");
                  else
                     columnsSQLQuery.append("CAST(" + identifierQuoteString + columnName
                                            + identifierQuoteString + " AS CHAR(254)) LIKE \'%"
                                            + searchString + "%\' OR ");
               }
               else if (dataSourceType.equals(ConnectionManager.ORACLE))
               {
                  if (columnTypeName.equals("DATE"))
                     columnsSQLQuery.append(identifierQuoteString + columnName + identifierQuoteString
                                            + " LIKE TO_DATE(\'" + searchString + "\', "
                                            + "\'YYYY-MM-dd\') OR ");
                  else
                     columnsSQLQuery.append(identifierQuoteString + columnName + identifierQuoteString
                        + " LIKE \'%" + searchString + "%\' OR ");     
               }
               else if (dataSourceType.equalsIgnoreCase(ConnectionManager.SQLITE))
               {
                  TableTabPanel_SQLite.createSearch(columnsSQLQuery, columnClass, columnSQLType,
                                                    columnTypeName, identifierQuoteString + columnName
                                                    + identifierQuoteString, searchQueryString,
                                                    "LIKE", "%");
                  columnsSQLQuery.append(" OR ");
               }
               else
                  columnsSQLQuery.append(identifierQuoteString + columnName + identifierQuoteString
                     + " LIKE \'%" + searchString + "%\' OR ");
            }
         }
         
         if (columnsSQLQuery.length() > 4)
            return (columnsSQLQuery.delete((columnsSQLQuery.length() - 4),
                                          columnsSQLQuery.length())).toString();
         else
            return "";
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "SearchDatabaseThread createColumnSQLQuery()");
         return "";
      }
   }
   
   //==============================================================
   // Class Method to allow the cancelation of the search.
   //==============================================================
   
   public void cancel()
   {
      cancelSearch = true;;
   }
   
   //==============================================================
   // Class Method for package classes to obtain the resultant data
   // created by the search of the database. Could be NULL so make
   // a check.
   //==============================================================

   public Object[][] getResultData()
   {
      if (resultData != null)
         return resultData.clone();
      else
         return null;
   }
}