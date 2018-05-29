//=================================================================
//                        DB_TO_DBThread
//=================================================================
//    This class provides a generic thread to perform a DB to DB
// transfer in an independent dissassociated manner.
//
//                   << DB_To_DBThread.java >>
//
//=================================================================
// Copyright (C) 2013-2018 Dana M. Proctor
// Version 1.0 05/28/2018
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
// Version 1.0 05/28/2018 Initial DB_To_DBThread Class.
//             
//-----------------------------------------------------------------
//                   danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dandymadeproductions.ajqvue.datasource.ConnectionInstance;
import com.dandymadeproductions.ajqvue.datasource.ConnectionProperties;
import com.dandymadeproductions.ajqvue.utilities.db.DDLGenerator;
import com.dandymadeproductions.ajqvue.utilities.db.SQLQuery;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 * The DB_To_DBThread class provides a thread to perform the DB
 * to DB transfer in an independent dissassociated manner.
 * 
 * @author Dana M. Proctor
 * @version 1.0 05/28/2018
 */

public class DB_To_DBThread implements Runnable
{
   // Class Instances
   private Logger threadLogger;

   private ConnectionInstance connectionInstanceSink;
   private Connection dbConnectionSink;
   private SQLQuery sqlQuerySink;

   private String dbSinkTypeString;
   private String sinkDBTableNameString;

   private ConnectionInstance connectionInstanceSource;
   private String sqlQueryString;
   private SQLQuery sqlQuerySource;

   private boolean dbTypeMemory;
   private boolean useSQLiteCast;
   private boolean createSinkTable;

   private int queryRowCount;

   public static final String SINK_DB_TABLE_NAME = "aqjvueSinkDBTable";
   public static final int VARCHAR_LIMIT = 65535;

   // ==============================================================
   // DB_To_DBThread Constructors.
   //
   // Constructors: Use MUST specify at a minimum a source Connection
   // and SQL Query!
   //
   // ==============================================================

   // Use Defaults, HSQL Memory DB.
   public DB_To_DBThread(ConnectionInstance connectionInstanceSource, String sqlQueryString)
   {
      this(connectionInstanceSource, null, sqlQueryString, SINK_DB_TABLE_NAME, true, false);
   }

   // Use Defaults, HSQL Memory DB, Specify Table Name.
   public DB_To_DBThread(ConnectionInstance connectionInstanceSource, String sqlQueryString,
                         String sinkDBTableNameString)
   {
      this(connectionInstanceSource, null, sqlQueryString, sinkDBTableNameString, true, false);
   }
   
   // WARNING!
   // The use of useSQLiteCast, true, will result in most cases
   // the SQLite fields stored with setString().
   
   protected DB_To_DBThread(ConnectionInstance connectionInstanceSource,
                            ConnectionInstance connectionInstanceSink, String sqlQueryString,
                            String sinkDBTableNameString, boolean createSinkTable, boolean useSQLiteCast)
   {
      this.connectionInstanceSource = connectionInstanceSource;
      this.connectionInstanceSink = connectionInstanceSink;
      this.sqlQueryString = sqlQueryString;
      this.sinkDBTableNameString = sinkDBTableNameString;

      // Determine Sink DB Properties.

      // Default to HSQL Memory Sink DB.
      if (connectionInstanceSink == null)
      {
         this.connectionInstanceSink = new ConnectionInstance(ConnectionInstance.HSQL2, true, false);
         dbTypeMemory = true;
         dbSinkTypeString = this.connectionInstanceSink.getDataSourceType();
         this.createSinkTable = true;
         this.useSQLiteCast = false;
      }
      else
      {
         String dbProperty = connectionInstanceSink.getConnectionProperties().getProperty(
            ConnectionProperties.DB);

         if (dbProperty.indexOf("mem:") != -1 || dbProperty.indexOf("memory:") != -1)
            dbTypeMemory = true;
         else
            dbTypeMemory = false;

         dbSinkTypeString = connectionInstanceSink.getDataSourceType();

         this.createSinkTable = createSinkTable;

         if (connectionInstanceSink.getDataSourceType().indexOf(ConnectionInstance.SQLITE) != -1)
            this.useSQLiteCast = useSQLiteCast;
         else
            this.useSQLiteCast = false;
      }
   }

   // ==============================================================
   // Class method for normal start of the thread
   // ==============================================================

   public void run()
   {
      // Method Instances
      boolean processError;
      int results;

      // Setup
      processError = false;

      // DB_To_FileMemoryDB Processing

      // Check for Source ConnectionInstance
      if (connectionInstanceSource == null)
      {
         log(Level.FINE, "DB_To_DBThread", "ConnectionInstance Source Null:", "");

         return;
      }
      else
      {
         log(Level.FINE, "DB_To_DBThread", "ConnectionInstance Source URL: ",
            connectionInstanceSource.getConnectionProperties().getConnectionURLString());
      }

      // Clean a Default Sink Connection
      if (dbConnectionSink == null)
         openConnectionSink();

      // Run Query Conversion to File/Memory DB.

      if (dbConnectionSink == null)
      {
         return;
      }

      if (createSinkTable)
      {
         log(Level.INFO, "\nDB_To_DBThread", "run()", "Entering executeSinkDBCreation()");
         processError = executeSinkDBCreation(dbConnectionSink);
         log(Level.INFO, "DB_To_DBThread", "run()", "Done executeSinkDBCreation(), Error:"
                                                              + processError + "\n");
      }

      // Run DB to File/Memory DB Insertions.

      if (!processError)
      {
         log(Level.INFO, "DB_To_DBThread", "run()", "Entering executeDB_To_DB_Transfer()");
         executeDB_To_DB_Transfer(connectionInstanceSink, dbConnectionSink);
         log(Level.INFO, "DB_To_DBThread", "run()", "Done executeDB_To_DB_Transfer()\n");
      }

      // Check DB File/Memory Insertions.

      if (!processError)
      {
         log(Level.INFO, "DB_To_DBThread", "run()", "Entering executeDB_CheckTransfer()");
         results = executeDB_CheckTransfer(connectionInstanceSink, dbConnectionSink, 10);
         log(Level.INFO, "DB_To_DBThread", "run()", "Done executeDB_CheckTransfer() Error: "
                                                              + (results == -1 ? true : false) + "\n");
      }
   }

   // ==============================================================
   // Class method to create the sink connection.
   // ==============================================================

   public void openConnectionSink()
   {
      log(Level.FINE, "DB_To_DBThread", "openConnectionSink()", connectionInstanceSink
            .getConnectionProperties().getConnectionURLString());

      dbConnectionSink = connectionInstanceSink
            .getConnection("DB_To_DBThread openConnectionSink()");

   }

   // ==============================================================
   // Class method to close the sink connection.
   // ==============================================================

   public void closeConnectionSink()
   {
      log(Level.FINE, "DB_To_DBThread", "closeConnectionSink()", dbSinkTypeString);

      if (dbConnectionSink != null)
      {
         log(Level.FINE, "DB_To_DBThread", "closeConnectionSink()", connectionInstanceSink
               .getConnectionProperties().getConnectionURLString());

         // Shutdown DB.

         // Note: If the connection is a memoryConnection then
         // it may not be closed down by the ConnectionInstance,
         // via closeConnection(), use shudown().

         if (dbTypeMemory)
         {
            connectionInstanceSink.shutdown("DB_To_DBThread closeConnectionSink()");
            dbConnectionSink = null;
         }
         else
            connectionInstanceSink.closeConnection(dbConnectionSink,
               "DB_To_DBThread closeConnectionSink()");
      }
      else
      {
         dbConnectionSink = null;
      }
   }

   // ==============================================================
   // Class Method to execute the creation of a new Sink file/mem db.
   // ==============================================================

   private boolean executeSinkDBCreation(Connection dbConnectionSink)
   {
      // Setup Instances.
      Connection dbConnectionSource;
      DDLGenerator ddlgen;

      String sqlStatementString;
      Statement sqlStatement;

      boolean error = false;

      // Sink, Create File/Memory Connection.
      log(Level.FINE, "DB_To_DBThread", "executeSinkDBCreation()", " dbTypeMemory: " + dbTypeMemory);

      try
      {
         // ***********************************
         // Setup the source SQL statement.

         log(Level.INFO, "DB_To_DBThread", "executeSinkDBCreation()", "Setting Up SQLQuery");

         sqlQuerySource = new SQLQuery(sqlQueryString);
         error = sqlQuerySource.executeSQL(connectionInstanceSource) == -1 ? true : false;

         if (error)
            return error;

         queryRowCount = sqlQuerySource.getRowCount(connectionInstanceSource);

         log(Level.FINE, "DB_To_DBThread", "executeSinkDBCreation()", "Query Row Count: "
                                                                                + queryRowCount);

         // ***********************************
         // Create a default DDL generator using defined DB sink.

         log(Level.INFO, "DB_To_DBThread", "executeSinkDBCreation()", "Setting Up DDL");

         ddlgen = new DDLGenerator(sqlQuerySource, connectionInstanceSource.getDataSourceType(),
                                   dbSinkTypeString, DDLGenerator.INDEXCOUNT);
         ddlgen.setUseSQLiteCast(useSQLiteCast);

         log(Level.INFO, "DB_To_DBThread", "executeSinkDBCreation()", "Getting DDL");

         dbConnectionSource = connectionInstanceSource
               .getConnection("DB_To_DBThread executeSinkDBCreation()");

         sqlStatementString = ddlgen.getDDL(dbConnectionSource, sinkDBTableNameString,
            connectionInstanceSink.getCatalogSeparator(), connectionInstanceSink.getIdentifierQuoteString());

         connectionInstanceSource.closeConnection(dbConnectionSource,
            "DB_To_DBThread executeSinkDBCreation()");

         // ***********************************
         // Create the table in the sink file/memory db.

         log(Level.INFO, "DB_To_DBThread", "executeSinkDBCreation()", "Creating Sink DB");

         sqlStatement = dbConnectionSink.createStatement();
         String[] statements = sqlStatementString.split(";\n");

         for (int i = 0; i < statements.length; i++)
            sqlStatement.execute(statements[i].trim());

         // ***********************************
         // Check the New Database SQL

         log(Level.INFO, "DB_To_DBThread", "executeSinkDBCreation()", "Checking New Database");

         sqlStatementString = "SELECT * FROM "
                              + Utils.getSchemaTableName(sinkDBTableNameString,
                                 connectionInstanceSink.getCatalogSeparator(),
                                 connectionInstanceSink.getIdentifierQuoteString());

         log(Level.FINE, "DB_To_DBThread", "executeSinkDBCreation()", sqlStatementString);

         sqlQuerySink = new SQLQuery(sqlStatementString, 1);

         error = sqlQuerySink.executeSQL(dbConnectionSink) == 1 ? false : true;

      }
      catch (SQLException e)
      {
         ConnectionInstance.displaySQLErrors(e, "DB_ToFileMemoryDBPanel executeSinkDBCreation()", true);
         error = true;
      }
      return error;
   }

   // ==============================================================
   // Class Method to execute the transfer of the DB to DB transfer,
   // insertion of data for the new sink db.
   // ==============================================================

   private void executeDB_To_DB_Transfer(ConnectionInstance localConnectionInstance,
                                         Connection localConnection)
   {
      // Method Instances.
      Thread loadThread;
      SQLLoadThread sqlLoadThread;

      Thread insertThread;
      SQLInsertPrepareThread sqlInsertPrepareThread;

      ArrayBlockingQueue<TableRowElements> arrayBlockingQueue;

      // Seting up the ArrayBlockingQueue and threads to perform
      // the database to database transfer.
      //
      // To speed up performance the load thread's priority is
      // increase. An option to control limits for multiple load
      // threads could be used also.

      log(Level.INFO, "DB_To_DBThread", "executeDB_To_DB_Transfer()", "Setting up Threads");

      arrayBlockingQueue = new ArrayBlockingQueue<TableRowElements>(50, true);
      sqlLoadThread = new SQLLoadThread(connectionInstanceSource, sqlQuerySource, arrayBlockingQueue, false,
                                        1000, true, true);
      if (threadLogger != null)
         sqlLoadThread.setLogger(threadLogger);

      loadThread = new Thread(sqlLoadThread, "sqlLoadThread");
      loadThread.setPriority(loadThread.getPriority() + 1);

      log(Level.FINE, "DB_To_DBThread", "executeDB_To_DB_Transfer()", "loadThread : " + loadThread);

      sqlInsertPrepareThread = new SQLInsertPrepareThread(sqlLoadThread, localConnectionInstance,
                                                          localConnection, sqlQuerySink, arrayBlockingQueue,
                                                          SINK_DB_TABLE_NAME, false, 50);

      if (threadLogger != null)
         sqlInsertPrepareThread.setLogger(threadLogger);

      insertThread = new Thread(sqlInsertPrepareThread, "insertThread");

      log(Level.FINE, "DB_To_DBThread", "executeDB_To_DB_Transfer()", "insertThread : "
                                                                                + insertThread);

      // ***********************************
      // Transfer data.

      log(Level.INFO, "DB_To_DBThread", "executeDB_To_DB_Transfer()", "Starting Transfer\n");

      loadThread.start();
      insertThread.start();

      // ***********************************
      // Wait for processing to complete.

      try
      {
         loadThread.join();
         insertThread.join();
      }
      catch (InterruptedException ie)
      {
         log(Level.WARNING, "DB_To_DBThread", "executeDB_To_DB_Transfer()", ie.toString());
      }
   }

   // ==============================================================
   // Class Method to execute the creation of a new Sink file/mem
   // database. Returns -1 on error or number of rows inserted.
   // ==============================================================

   private int executeDB_CheckTransfer(ConnectionInstance connInstance, Connection localConnection,
                                       int sampleRows)
   {
      // Method Instances
      StringBuilder sqlStatementString;
      ResultSetMetaData tableMetaData;
      Statement sqlStatement;
      ResultSet rs;

      SQLQuery sqlQuery;
      boolean sqlError;
      int insertedRows;
      int columnCount;

      StringBuilder resultsBuilder;

      // ***********************************
      // View data.

      sqlStatementString = new StringBuilder();

      if (sampleRows > 10)
         sampleRows = 10;

      rs = null;
      sqlStatement = null;

      log(Level.INFO, "DB_To_DBThread", "executeDB_CheckTransfer()", "Checking Data");

      try
      {
         // Check the number of rows inserted.
         sqlStatementString.append("SELECT * FROM "
                                   + Utils.getSchemaTableName(sinkDBTableNameString,
                                      connInstance.getCatalogSeparator(),
                                      connInstance.getIdentifierQuoteString()));

         sqlQuery = new SQLQuery(sqlStatementString.toString());
         sqlError = sqlQuery.executeSQL(connInstance) == -1 ? true : false;

         if (sqlError)
            return -1;

         insertedRows = sqlQuery.getRowCount(connInstance);

         log(Level.FINE, "DB_To_DBThread", "executeDB_CheckTransfer()", "Inserted Row Count: "
                                                                                  + insertedRows);

         // Sample selected rows.

         if (sampleRows > 0)
         {
            sqlStatementString.delete(0, sqlStatementString.length());
            sqlStatementString.append("SELECT * FROM "
                                      + Utils.getSchemaTableName(sinkDBTableNameString,
                                         connInstance.getCatalogSeparator(),
                                         connInstance.getIdentifierQuoteString()));

            if (connInstance.getDataSourceType().equals(ConnectionInstance.H2))
               sqlStatementString.append(" LIMIT " + sampleRows);
            else if (connInstance.getDataSourceType().equals(ConnectionInstance.DERBY))
               sqlStatementString.append("OFFSET 0 ROWS FETCH NEXT " + sampleRows + " ROWS ONLY");
            else
               sqlStatementString.append(" LIMIT " + sampleRows + " OFFSET 0");

            sqlStatement = localConnection.createStatement();

            log(Level.FINE, "DB_To_DBThread", "executeDB_CheckTransfer()", "sqlStatementString:\n"
                                                                                     + sqlStatementString);
            rs = sqlStatement.executeQuery(sqlStatementString.toString());

            columnCount = rs.getMetaData().getColumnCount();
            tableMetaData = rs.getMetaData();

            resultsBuilder = new StringBuilder("\n");

            while (rs.next())
            {
               for (int i = 1; i <= columnCount; i++)
               {
                  String results;

                  // Do not display Blob and Text.

                  if (DB_To_DBThread.isBlob(tableMetaData.getColumnClassName(i),
                     tableMetaData.getColumnTypeName(i)))
                     results = "BLOB";
                  else if (DB_To_DBThread.isText(tableMetaData.getColumnClassName(i),
                     tableMetaData.getColumnTypeName(i), tableMetaData.getColumnDisplaySize(i),
                     DB_To_DBThread.VARCHAR_LIMIT))
                     results = "TEXT";
                  else
                     results = rs.getString(i);

                  if (results == null)
                     resultsBuilder.append("Null, ");
                  else
                     resultsBuilder.append(results + ", ");
               }

               if (resultsBuilder.length() != 0)
                  resultsBuilder.delete(resultsBuilder.length() - 2, resultsBuilder.length());

               resultsBuilder.append("\n");
            }
            log(Level.FINE, "", "", resultsBuilder.toString());

            if (queryRowCount != insertedRows)
               return -1;
         }
      }
      catch (SQLException e)
      {
         ConnectionInstance.displaySQLErrors(e, "DB_To_DBThread executeDB_CheckTransfer()", true);
         insertedRows = -1;
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
            ConnectionInstance.displaySQLErrors(sqle, "DB_To_DBThread executeDB_CheckTransfer()",
               true);
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
               ConnectionInstance.displaySQLErrors(sqle,
                  "DB_To_DBThread executeDB_CheckTransfer()", true);
            }
         }
      }
      return insertedRows;
   }

   // ==============================================================
   // Class Method to allow a logger to be assigned to the thread.
   // ==============================================================

   private void log(Level level, String sourceClass, String sourceMethod, String msg)
   {
      if (threadLogger != null)
         threadLogger.logp(level, sourceClass, sourceMethod, msg);
   }

   // ==============================================================
   // Method for determing if the given meta data class and type
   // can be defined as a large binary object, LOB.
   //
   // Types: BLOB, BYTEA, BINARY, IMAGE, RAW.
   // ==============================================================

   public static boolean isBlob(String columnClass, String columnType)
   {
      columnClass = columnClass.toLowerCase(Locale.ENGLISH);
      columnType = columnType.toUpperCase(Locale.ENGLISH);

      if ((columnClass.indexOf("string") == -1 && columnType.indexOf("BLOB") != -1)
          || (columnType.indexOf("BYTEA") != -1) || (columnType.indexOf("BINARY") != -1)
          || (columnClass.indexOf("byte") != -1 && columnType.indexOf("BIT DATA") != -1)
          || (columnType.indexOf("RAW") != -1) || (columnType.indexOf("IMAGE") != -1))
      {
         return true;
      }
      else
         return false;
   }

   // ==============================================================
   // Method for determing if the given meta data class and type
   // can be defined as a large text object, LOB.
   //
   // Types: TEXT, VARCHAR > 32700, LONG, CLOB, XML.
   //
   // MySQL defines TEXT, VARCHAR size as 65535.
   // Derby defines LONG VARCHAR size as 32700.
   // ==============================================================

   public static boolean isText(String columnClass, String columnType, int columnSize, int charLimit)
   {
      // Method Instances
      int varcharLimit;

      if (charLimit == 0)
         varcharLimit = VARCHAR_LIMIT;
      else
         varcharLimit = charLimit;

      columnClass = columnClass.toLowerCase(Locale.ENGLISH);
      columnType = columnType.toUpperCase(Locale.ENGLISH);

      if ((columnClass.indexOf("string") != -1 && !columnType.equals("CHAR") && !columnType.equals("NCHAR")
           && !columnType.equals("BPCHAR") && columnSize > varcharLimit)
          || (columnClass.indexOf("string") != -1 && columnType.equals("LONG"))
          || columnType.equals("TEXT")
          || columnType.indexOf("CLOB") != -1 || columnType.equals("XML"))
         return true;
      else
         return false;
   }

   // ==============================================================
   // Class Method to allow a logger to be assigned to the thread.
   // ==============================================================

   public void setLogger(Logger logger)
   {
      threadLogger = logger;
   }
}