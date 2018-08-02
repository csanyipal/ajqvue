//=================================================================
//                        SQLLoadThread
//=================================================================
//   This class provides a means to load the required field objects
// for use with a database to H2, HSQL, Derby, or SQLite Memory/File
// database transfer.
//
//                   << SQLLoadThread.java >>
//
//=================================================================
// Copyright (C) 2013-2018 Dana M. Proctor
// Version 3.0 08/02/2018
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
// Version 1.0 12/27/2015 Original SQLLoadPrepareThread Class.
//         1.1 04/27/2017 Updated to be Used With Ajqvue.
//         1.2 12/10/2017 Changed Package to sqltofilememorydb.
//         1.3 12/18/2017 Cleaned Up. Replaced ConnectionManager With Connection
//                        Instance, New Constructor Argument. Removed Class
//                        Instances dataSourceType, sqlTable, identifierQuoteString,
//                        & All resourceXXX. Added Class Instances LIMIT_INCREMENT,
//                        & debug. Changes to Constructors, Effected Removals, Addition
//                        Instances. Changes in run() to Effect Use of ConnectionInstance.
//                        Removed manualInsertData().
//         1.4 01/15/2018 Method loadData() Corrected sqlQuery.getRowCount() to Use
//                        ConnectionInstance, Option to Select Alternative Data Source.
//         1.5 03/30/2018 Updated Package to Reflect Project Name Change to DB_To_FileMemoryDB.
//         1.6 04/17/2018 Corrected Class Name in Comments. Added Class Instance threadLogger.
//                        Replaced System.outs With Logger Through New Method log(). Added
//                        Method setLogger().
//         1.7 04/26/2018 Cleaned Up Some. Method run() Used a finally to Close Connection.
//                        Method loadData() Fixed TIME WITH TIME ZONE in loadData().
//         1.8 04/29/2018 Minor Cleanup, Comment Changes. Corrected Time With Time Zone timeString
//                        Parsing in loadData().
//         1.9 04/30/2018 Method loadData() Insured HSQL Time With Time Zone Formats to HH:mm:ss.
//         2.0 05/03/2018 Continued to Cleanup, Removed Method displayMyDateString(). Method
//                        loadData() All, Insured currentContentData is Cast Appropriately.
//                        Use DB_To_FileMemoryDBThread.isText() & isBlob(). Clarified BIT
//                        Fields do Not Collect Boolean Types. Temporary System.out for
//                        Standard Fall Through to getString().
//         2.1 05/06/2018 Method loadData() Additional Temporary Debug System.outs, Finished
//                        Preliminary Testing for Blob/Binary & Boolean Fields. Same Method
//                        Moved the Storing in tableRowDataQueue With finalElement to finally
//                        Clause.
//         2.2 05/09/2018 Method loadData() Finished Preliminary Testing for Text/Clob. Began
//                        Work With Bit Data Types.
//         2.3 05/11/2018 Method loadDAta() Cleaned Up Some, Remove System.outs, Added Comments.
//                        Completed Preliminary Testing for Bit Fields.
//         2.4 05/17/2018 Commented Out Testing System.outs. Method loadData() Corrected Conditional
//                        Logic for useLimits in while.
//         2.5 05/20/2018 Method loadData() Promoted loadProgressBar to Class Instance. Additional
//                        log() in Same for Indicating LAST_ELEMENT Item. Added Method cancelLoad().
//         2.6 05/23/2018 Method loadData() Used Specific Exceptions, SQL & Interrupted, for Generic
//                        Exception catch.
//         2.7 05/24/2018 Method loadData() Removed Instances columnNamesString & oracleColumnNames
//                        String. Same, Commented Conditional for useLimits, Implemented a Rudimentary
//                        Control for Query LIMIT/OFFSET With limitIncrement. Some Source Databases
//                        Do Not Do Well With Large ResultSet Sizes.
//         2.8 05/29/2018 Moved From DB_To_FileMemoryDB Project to Ajqvue, Changed Package to
//                        utilities.db in Ajqvue. Changed References to DB_To_FileMemoryDBThread
//                        to DB_To_DBThread.
//         2.9 06/08/2018 Method loadData() Used Identifier Quote on firstField & SQLQuery sql
//                        OracleColumnNames.
//         3.0 08/02/2018 Method loadData() Formatted Code, Instances One per Line, Changed
//                        columnType to columnTypeName & Added columnSQLType. Same Method Date,
//                        Time, & Timestamp Types Processed With getString() for SQLite When
//                        columnSQLType is VARCHAR. In such Case of String Content Insured table
//                        RowElements Set Accordingly.
//             
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dandymadeproductions.ajqvue.datasource.ConnectionInstance;
import com.dandymadeproductions.ajqvue.utilities.ProgressBar;

/**
 *    The SQLLoadThread provides a means to load the required
 * field objects for use with a database to H2, HSQL, Derby, or SQLite
 * Memory/File database transfer.
 * 
 * @author Dana M. Proctor
 * @version 3.0 08/02/2018
 */

public class SQLLoadThread implements Runnable
{
   // Class Instances
   private ConnectionInstance connectionInstance;
   private SQLQuery sqlQuery;
   private ArrayBlockingQueue<TableRowElements> tableRowDataQueue;
   
   private ProgressBar loadProgressBar;
   private int limitIncrement;
   private boolean useLimits;
   private boolean showProgressBar;
   private boolean debug;
   
   private Logger threadLogger;
   
   private static int LIMIT_INCREMENT = 10000;

   //==============================================================
   // SQLLoadThread Constructors.
   //==============================================================

   public SQLLoadThread(ConnectionInstance connectionInstance, SQLQuery sqlQuery,
                        ArrayBlockingQueue<TableRowElements> tableRowDataQueue, boolean debug)
   {
      this(connectionInstance, sqlQuery, tableRowDataQueue, false, LIMIT_INCREMENT, false,
           debug);
   }

   public SQLLoadThread(ConnectionInstance connectionInstance, SQLQuery sqlQuery,
                        ArrayBlockingQueue<TableRowElements> tableRowDataQueue,
                        boolean useLimits, int limitIncrement, boolean showProgressBar,
                        boolean debug)
   {
      this.connectionInstance = connectionInstance;
      this.sqlQuery = sqlQuery;
      this.tableRowDataQueue = tableRowDataQueue;
      this.useLimits = useLimits;
      this.limitIncrement = limitIncrement;
      this.showProgressBar = showProgressBar;
      this.debug = debug;
   }

   //==============================================================
   // Class Method for Normal Start of the Thread
   //==============================================================

   public void run()
   {
      Connection dbConnection = null;
      
      try
      {
         if (connectionInstance == null)
            throw new SQLException("Null Instance");
 
         dbConnection = connectionInstance.getConnection("SQLLoadThread run()");

         if (dbConnection == null)
            return;
         
         log(Level.INFO, "SQLLoadThread", "run()", "Entering loadData()");
         loadData(dbConnection);
         log(Level.INFO, "SQLLoadThread", "run()", "Done loadData()\n");
      }
      catch (SQLException sqle)
      {
         ConnectionInstance.displaySQLErrors(sqle, "SQLLoadThread run()", debug);
      }
      finally
      {
         if (connectionInstance != null)
            connectionInstance.closeConnection(dbConnection, "SQLLoadThread run()");
      }
   }

   //==============================================================
   // Class method to create the insert/replace statement and data.
   //==============================================================

   private void loadData(Connection dbConnection) throws SQLException
   {
      // Class Method Instances
      //StringBuffer columnNamesString;
      //StringBuffer oracleColumnNamesString;
      String firstField;

      Iterator<String> columnNamesIterator;
      
      String columnName;
      String columnClass;
      int columnSQLType;
      String columnTypeName;
      int columnSize;     
      int rowsCount;
      int currentTableIncrement;
      int currentRow;

      Object currentContentData;
      TableRowElements tableRowElements;

      String sqlStatementString;
      Statement sqlStatement;
      ResultSet db_resultSet;

      // Collect the row count of the table and setting
      // up a progress bar for tracking/canceling.

      currentTableIncrement = 0;
      currentRow = 0;

      // Setup a progress bar for tracking/canceling,
      // rowsCount and fields.
      
      loadProgressBar = new ProgressBar("SQL Load Data");
      
      rowsCount = sqlQuery.getRowCount(connectionInstance);
      log(Level.FINE, "SQLLoadThread", "loadData()", "rowsCount: " + rowsCount);
      
      if (showProgressBar)
      {
         loadProgressBar.setTaskLength(rowsCount);
         loadProgressBar.pack();
         loadProgressBar.center();   
      }
      loadProgressBar.setVisible(showProgressBar);
      
      // Ok now ready so beginning by connecting to database for
      // data and proceeding with building the data to load.

      sqlStatement = null;
      db_resultSet = null;

      try
      {
         sqlStatement = dbConnection.createStatement();

         // Setting up to query statement.
         do
         {
            if (useLimits)
            { 
               // NOTE May Need Work!
               // Tested all, but Oracle, and MSSQL.
               
               firstField = connectionInstance.getIdentifierQuoteString() + sqlQuery.getColumnNames().get(0)
                            + connectionInstance.getIdentifierQuoteString();
               
               // Oracle
               if (connectionInstance.getDataSourceType().equals(ConnectionInstance.ORACLE))
                  sqlStatementString = "SELECT * FROM "
                                       + "(SELECT ROW_NUMBER() OVER (ORDER BY " + firstField + " ASC) "
                                       + "AS dmprownumber, " + sqlQuery.getSqlOrcaleColumnNamesString()
                                       +  " FROM (" + sqlQuery.getSQLQuery() + ") AS t1) "
                                       + "WHERE dmprownumber BETWEEN "
                                       + (currentTableIncrement + 1) + " AND "
                                       + (currentTableIncrement + limitIncrement);
               
               // MSAccess
               else if (connectionInstance.getDataSourceType().equals(ConnectionInstance.MSACCESS))
                  sqlStatementString = sqlQuery.getSQLQuery();
               
               // MSSQL
               else if (connectionInstance.getDataSourceType().equals(ConnectionInstance.MSSQL))
               {
                  sqlStatementString = "SELECT * FROM "
                                       + "(SELECT *, ROW_NUMBER() OVER (ORDER BY " + firstField + " ASC) "
                                       + "AS dmprownumber FROM (" + sqlQuery.getSQLQuery() + ") AS t) AS t1 "
                                       + "WHERE t1.dmprownumber BETWEEN " + (currentTableIncrement + 1)
                                       + " AND " + (currentTableIncrement + limitIncrement);
               }
               
               // Derby
               else if (connectionInstance.getDataSourceType().equals(ConnectionInstance.DERBY))
                  sqlStatementString = "SELECT * FROM (" + sqlQuery.getSQLQuery() + ")"
                                       + " AS t1 OFFSET " + currentTableIncrement + " ROWS " + "FETCH NEXT "
                                       + limitIncrement + " ROWS ONLY";
               
               // H2, HSQL, MySQL, MariaDB, PostgreSQL, & SQLite.
               else
                  sqlStatementString = "SELECT * FROM (" + sqlQuery.getSQLQuery() + ")"
                                       + " AS t1 LIMIT " + limitIncrement + " OFFSET " + currentTableIncrement;
            }
            else
               sqlStatementString = sqlQuery.getSQLQuery();

            log(Level.FINE, "SQLLoadThread", "loadData()", sqlStatementString);

            db_resultSet = sqlStatement.executeQuery(sqlStatementString);

            // Begin loading the data into the queue.
            
            while (db_resultSet.next() && !loadProgressBar.isCanceled())
            {
               loadProgressBar.setCurrentValue(currentRow++);

               // Cycling through the item fields for storage
               // into an TableRowElement.

               tableRowElements = new TableRowElements(sqlQuery.getColumnNames().size());
               columnNamesIterator = sqlQuery.getColumnNames().iterator();

               while (columnNamesIterator.hasNext())
               {
                  columnName = columnNamesIterator.next();
                  columnClass = sqlQuery.getColumnClassHashMap().get(columnName);
                  columnSQLType = (sqlQuery.getColumnSQLTypeHashMap().get(columnName)).intValue();
                  columnTypeName = sqlQuery.getColumnTypeNameHashMap().get(columnName);
                  columnSize = sqlQuery.getColumnSizeHashMap().get(columnName).intValue();

                  // System.out.print("SLT: " + columnName + " " + columnClass + " " + columnSQLType
                  //                   + " " + columnTypeName + " " + columnSize + " ");

                  // DATE Type Field
                  if (columnTypeName.equals("DATE"))
                  {
                     if (connectionInstance.getDataSourceType().equals(ConnectionInstance.SQLITE)
                         && columnSQLType == Types.VARCHAR)
                        currentContentData = db_resultSet.getString(columnName);
                     else
                        currentContentData = db_resultSet.getDate(columnName);
                     
                     if (currentContentData == null)
                        tableRowElements.setRowElement(null);
                     else
                        tableRowElements.setRowElement(currentContentData + "");
                  }
                  
                  // DATETIME Type Field
                  else if (columnTypeName.equals("DATETIME"))
                  {
                     currentContentData = db_resultSet.getString(columnName);
                     
                     if (currentContentData == null)
                        tableRowElements.setRowElement(null);
                     else
                     {
                        String dateString = currentContentData.toString();
                        String timeString = currentContentData.toString();
                        
                        dateString = dateString.substring(0, (dateString.indexOf(" ")));
                        timeString = timeString.substring(timeString.indexOf(" "));
                        tableRowElements.setRowElement(dateString + timeString);
                     }
                  }

                  // TIME Type Field
                  else if (columnTypeName.equals("TIME"))
                  {
                     if (connectionInstance.getDataSourceType().equals(ConnectionInstance.SQLITE)
                           && columnSQLType == Types.VARCHAR)
                        currentContentData = db_resultSet.getString(columnName);
                     else
                        currentContentData = db_resultSet.getTime(columnName);
                     
                     if (currentContentData == null)
                        tableRowElements.setRowElement(null);
                     else
                     {
                        if (connectionInstance.getDataSourceType().equals(ConnectionInstance.SQLITE)
                            && columnSQLType == Types.VARCHAR)
                           tableRowElements.setRowElement(currentContentData);
                        else
                           tableRowElements.setRowElement(
                              (new SimpleDateFormat("HH:mm:ss").format(currentContentData)));
                     }
                  }
                  
                  // TIME WITH TIME ZONE
                  else if (columnTypeName.equals("TIMETZ") || columnTypeName.equals("TIME WITH TIME ZONE"))
                  {
                     currentContentData = db_resultSet.getString(columnName);
                     
                     if (currentContentData == null)
                        tableRowElements.setRowElement(null);
                     else
                     {
                        String timeString = (String) currentContentData;
                        
                        // PostgreSQL
                        if (columnTypeName.equals("TIMETZ"))
                        {
                           // Put in long format compatible
                           // with for possible HSQL sink.
                           
                           timeString = timeString.substring(0, 8) + ".0" + timeString.substring(
                              8, timeString.length()) + ":00";
                           tableRowElements.setRowElement(timeString);
                        }
                        else
                        {
                           // Insure format conforms to HH:mm:ss
                           if (timeString.indexOf(":") == 1)
                              timeString = "0" + timeString;
                           
                           tableRowElements.setRowElement(timeString);
                        }
                     }    
                  }

                  // TIMESTAMP Type Field
                  else if (columnTypeName.equals("TIMESTAMP"))
                  {
                     if (connectionInstance.getDataSourceType().equals(ConnectionInstance.SQLITE)
                           && columnSQLType == Types.VARCHAR)
                        currentContentData = db_resultSet.getString(columnName);
                     else
                        currentContentData = db_resultSet.getTimestamp(columnName);
                     
                     if (currentContentData == null)
                        tableRowElements.setRowElement(null);
                     else
                     {
                        if (connectionInstance.getDataSourceType().equals(ConnectionInstance.SQLITE)
                              && columnSQLType == Types.VARCHAR)
                           tableRowElements.setRowElement(currentContentData);
                        else
                           tableRowElements.setRowElement(
                              (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentContentData)));
                     }
                  }

                  else if (columnTypeName.equals("TIMESTAMPTZ")
                           || columnTypeName.equals("TIMESTAMP WITH TIME ZONE")
                           || columnTypeName.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                  {
                     if (connectionInstance.getDataSourceType().equals(ConnectionInstance.SQLITE)
                           && columnSQLType == Types.VARCHAR)
                        currentContentData = db_resultSet.getString(columnName);
                     else
                        currentContentData = db_resultSet.getTimestamp(columnName);
                     
                     if (currentContentData == null)
                        tableRowElements.setRowElement(null);
                     else
                     {
                        if (connectionInstance.getDataSourceType().equals(ConnectionInstance.SQLITE)
                              && columnSQLType == Types.VARCHAR)
                           tableRowElements.setRowElement(currentContentData);
                        else
                           tableRowElements.setRowElement(
                              (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(currentContentData)));
                     }
                  }

                  // TIMESTAMPS WITH LOCAL TIME ZONE Type Field
                  else if (columnTypeName.equals("TIMESTAMPLTZ"))
                  {
                     currentContentData = db_resultSet.getString(columnName);
                     
                     if (currentContentData == null)
                        tableRowElements.setRowElement(null);
                     else
                        tableRowElements.setRowElement(currentContentData);
                  }

                  // YEAR Type Field
                  else if (columnTypeName.equals("YEAR"))
                  {
                     currentContentData = db_resultSet.getString(columnName);
                     
                     if (currentContentData == null)
                        tableRowElements.setRowElement(null);
                     else
                     {
                        String displayYear = (String) currentContentData;
                        displayYear = displayYear.trim();

                        if (columnSize == 2)
                        {
                           if (displayYear.length() >= 4)
                              displayYear = displayYear.substring(2, 4);
                        }
                        else
                           displayYear = displayYear.substring(0, 4);

                        tableRowElements.setRowElement(displayYear);
                     } 
                  }

                  // BlOB, BYTEA, BINARY Type Fields
                  else if (DB_To_DBThread.isBlob(columnClass, columnTypeName))
                  {
                     currentContentData = db_resultSet.getBytes(columnName);
                     
                     if (currentContentData == null)
                        tableRowElements.setRowElement(null);
                     else
                        tableRowElements.setRowElement(currentContentData);
                  }

                  // BIT Type Field
                  else if (columnClass.indexOf("Boolean") == -1 && columnTypeName.equals("BIT"))
                  {
                     if (connectionInstance.getDataSourceType().equals(ConnectionInstance.MYSQL)
                         || connectionInstance.getDataSourceType().equals(ConnectionInstance.MARIADB))
                     {
                        currentContentData = db_resultSet.getByte(columnName);
                        
                        if (currentContentData == null)
                           tableRowElements.setRowElement(null);
                        else
                        {
                           String byteString = Byte.toString((byte) currentContentData);
                           tableRowElements.setRowElement(Integer.toBinaryString(Integer.parseInt(byteString)));
                        }
                     }
                     else
                     {
                        currentContentData = db_resultSet.getString(columnName);
                        
                        if (currentContentData == null)
                           tableRowElements.setRowElement(null);
                        else
                           tableRowElements.setRowElement(currentContentData);
                     }
                  }

                  // TEXT, & CLOB Type Fields
                  
                  // Note: Some databases, MySQL, MariaDB, HSQL translate TEXT,
                  //       LONGVARCHAR to VARCHAR. Derby in particular limits
                  //       size for that type to much less than these required.
                  //       TypesInfoCache, will not convert to a more appropriate
                  //       Clob type.
                  
                  else if (DB_To_DBThread.isText(columnClass, columnTypeName, columnSize,
                           DB_To_DBThread.VARCHAR_LIMIT))
                  {
                     currentContentData = db_resultSet.getString(columnName);
                     
                     if (currentContentData == null)
                        tableRowElements.setRowElement(null);
                     else
                        tableRowElements.setRowElement(currentContentData);
                  }
                  
                  // Default Content. A normal table entry should
                  // fall through here to collect content as STRING.
                  else
                  {
                     currentContentData = db_resultSet.getString(columnName);
                     
                     if (currentContentData == null)
                        tableRowElements.setRowElement(null);
                     else
                        tableRowElements.setRowElement(currentContentData);
                  }
                  // System.out.print("SLT currentContentData: ");
                  // System.out.print(currentContentData + "\n");
               }
               tableRowDataQueue.put(tableRowElements);
            }
            currentTableIncrement += limitIncrement;
         }
         while (useLimits && currentTableIncrement < rowsCount && !loadProgressBar.isCanceled());
      }
      catch (SQLException sqle)
      {  
         ConnectionInstance.displaySQLErrors(((SQLException) sqle), "SQLLoadThread loadData()",
                                                debug);
      }
      catch (InterruptedException ie)
      {
         if (debug)
            log(Level.WARNING, "SQLLoadThread", "loadData()", ie.toString());
      }
      finally
      { 
         loadProgressBar.setCanceled(true);
         loadProgressBar.dispose();
         
         try
         {
            TableRowElements finalElement = new TableRowElements();
            
            log(Level.INFO, "SQLLoadThread", "loadData()", "Loading Final: "
                + TableRowElements.LAST_ELEMENT + "\n");
            
            finalElement.setMessage(TableRowElements.LAST_ELEMENT);
            tableRowDataQueue.put(finalElement);
         }
         catch (InterruptedException ie)
         {
            if (debug)
               log(Level.WARNING, "SQLLoadThread", "loadData()", ie.toString());     
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
               ConnectionInstance.displaySQLErrors(((SQLException) sqle), "SQLLoadThread loadData()", debug); 
            }
            finally
            {
               if (sqlStatement != null)
                  sqlStatement.close();
            }
         }
      }
   }
   
   //==============================================================
   // Class Method to allow a logger to be assigned to the thread.
   //==============================================================

   protected void cancelLoad(String message)
   {
      if (loadProgressBar != null)
      {
         log(Level.WARNING, "SQLLoadThread", "cancelLoad()", message);
         loadProgressBar.setCanceled(true);
      }
   }
   
   //==============================================================
   // Class Method to allow a logger to be assigned to the thread.
   //==============================================================

   private void log(Level level, String sourceClass, String sourceMethod, String msg)
   {
      if (threadLogger != null)
         threadLogger.logp(level, sourceClass, sourceMethod, msg);
   }
   
   //==============================================================
   // Class Method to allow a logger to be assigned to the thread.
   //==============================================================

   public void setLogger(Logger logger)
   {
      threadLogger = logger;
   }
}