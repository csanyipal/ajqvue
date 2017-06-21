//=================================================================
//                SQL DatabaseSchemeDumpThread
//=================================================================
//   This class provides a thread to safely dump the current
// database scheme, all tables, to a local file in SQL format. A
// status dialog with cancel is provided to provide the ability
// to prematurely terminate the dump.
//
//            << SQLDatabaseSchemeDumpThread.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.0 09/18/2016
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
// Version 1.0 Production SQLDatabaseSchemeDumpThread Class.
//                         
//-----------------------------------------------------------------
//                    danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.ConnectionProperties;
import com.dandymadeproductions.ajqvue.utilities.ProgressBar;
import com.dandymadeproductions.ajqvue.utilities.TableDefinitionGenerator;

/**
 *    The SQLDatabaseSchemeDumpThread class provides a thread to safely
 * dump the current database scheme, all tables, to a local file in SQL
 * format. A status dialog with cancel is provided to provide the ability
 * to prematurely terminate the dump.
 * 
 * @author Dana Proctor
 * @version 1.0 09/18/2016
 */

public class SQLDatabaseSchemeDumpThread implements Runnable
{
   // Class Instances.
   String fileName;
   String[] version;

   private ProgressBar databaseDumpProgressBar;

   //==============================================================
   // SQLDatabaseSchemeDumpThread Constructor.
   //==============================================================

   public SQLDatabaseSchemeDumpThread(String fileName, String[] version)
   {
      this.fileName = fileName;
      this.version = version.clone();
   }

   //==============================================================
   // Class method for normal start of the thread
   //==============================================================

   public void run()
   {
      // Class Method Instances.
      Iterator<String> tableNamesIterator;
      String exportedTable, dbIdentifierQuoteString;
      Object dumpData;

      // Get Connection to Database.
      Connection dbConnection = ConnectionManager.getConnection("DatabaseSchemeDumpThread run()");

      if (dbConnection == null)
         return;

      //identifierQuoteString = DBTablesPanel.getDataExportProperties().getIdentifierQuoteString();
      dbIdentifierQuoteString = ConnectionManager.getIdentifierQuoteString();

      // Create a progress bar for giving the user a
      // visual and cancel ability.
      databaseDumpProgressBar = new ProgressBar("SQL Database Scheme Dump");

      // Setup the dump Class and Header.
      dumpData = generateHeaders(dbConnection);

      // Start a progress bar for tracking/canceling.
      databaseDumpProgressBar.setTaskLength(ConnectionManager.getTableNames().size());
      databaseDumpProgressBar.pack();
      databaseDumpProgressBar.center();
      databaseDumpProgressBar.setVisible(true);

      // Cycle through the tables, scheme dumping.
      int i = 0;
      tableNamesIterator = ConnectionManager.getTableNames().iterator();

      while (tableNamesIterator.hasNext() && !databaseDumpProgressBar.isCanceled())
      {
         databaseDumpProgressBar.setCurrentValue(i + 1);

         // Properly construct the schema.table.

         exportedTable = tableNamesIterator.next();
         if (exportedTable.indexOf(".") != -1)
         {
            exportedTable = dbIdentifierQuoteString + exportedTable.substring(0, exportedTable.indexOf("."))
                            + dbIdentifierQuoteString + "." + dbIdentifierQuoteString
                            + exportedTable.substring(exportedTable.indexOf(".") + 1) + dbIdentifierQuoteString;
         }
         else
            exportedTable = dbIdentifierQuoteString + exportedTable + dbIdentifierQuoteString;

         dumpData = dumpData + genCommentSep("Table structure for table " + exportedTable);

         dumpData = dumpData
                    + (new TableDefinitionGenerator(dbConnection, exportedTable)).getTableDefinition();
         i++;
      }

      // Write the dump to the file.
      
      if (!databaseDumpProgressBar.isCanceled())
         WriteDataFile.mainWriteDataString(fileName, ((String) dumpData).getBytes(), false);
      databaseDumpProgressBar.dispose();

      // Closing up.
      try
      {
         dbConnection.close();
      }
      catch (SQLException e)
      {
         ConnectionManager.closeConnection(dbConnection, "DatabaseSchemeDumpThread run()");
      }

      ConnectionManager.closeConnection(dbConnection, "DatabaseSchemeDumpThread run()");
   }

   //==============================================================
   // Class method for generating dump header info
   //==============================================================

   public String generateHeaders(Connection dbConnection)
   {
      // Class Method Instances.
      ConnectionProperties connectionProperties;
      String hostName, databaseName;
      String dateTime, headers;
      SimpleDateFormat dateTimeFormat;

      // Create Header.
      
      connectionProperties = ConnectionManager.getConnectionProperties();
      hostName = connectionProperties.getProperty(ConnectionProperties.HOST);
      databaseName = connectionProperties.getProperty(ConnectionProperties.DB);
      
      dateTimeFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss z");
      dateTime = dateTimeFormat.format(new Date());

      headers = "--\n" + "-- SQL Dump\n" + "-- Version: " + version[1] + "\n"
                + "-- WebSite: " + Ajqvue.getWebSite() + "--\n" + "-- Host: "
                + hostName + "\n" + "-- Generated On: " + dateTime + "\n"
                + "-- SQL version: " + ConnectionManager.getDBProductName_And_Version() + "\n"
                + "-- Database: " + databaseName + "\n" + "--\n\n"
                + "-- ------------------------------------------\n";

      // System.out.println(headers);
      return headers;
   }

   //==============================================================
   // Class method for generating
   //==============================================================

   public String genCommentSep(String str)
   {
      String res;
      res = "\n--\n";
      res += "-- " + str;
      res += "\n--\n\n";
      return res;
   }
}
