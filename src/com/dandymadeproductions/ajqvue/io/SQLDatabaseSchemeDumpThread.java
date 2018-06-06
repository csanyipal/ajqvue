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
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.4 06/06/2018
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
//         1.1 Method generateHeaders() Code Formatting to Clarify & Removal
//             of Dashes After Website.
//         1.2 Added Extends to SQLDump. Used That Classes Instance fileName.
//             Removed Class Instance version & Same as Argument From Constructor.
//             Removed dbConnection Argument to generateHeaders(). Removed Class
//             Methods generateHeaders() & genCommentSep(). Organized Imports.
//         1.3 Changed/Updated Import for TableDefinitionGenerator Class.
//         1.4 Method run() Removed Instance dbIdentifierQuoteString Since Code
//             to Determine exportedTable Derived From Utils.getSchemaTableName().
//                         
//-----------------------------------------------------------------
//                    danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.utilities.ProgressBar;
import com.dandymadeproductions.ajqvue.utilities.Utils;
import com.dandymadeproductions.ajqvue.utilities.db.TableDefinitionGenerator;

/**
 *    The SQLDatabaseSchemeDumpThread class provides a thread to safely
 * dump the current database scheme, all tables, to a local file in SQL
 * format. A status dialog with cancel is provided to provide the ability
 * to prematurely terminate the dump.
 * 
 * @author Dana Proctor
 * @version 1.4 06/06/2018
 */

public class SQLDatabaseSchemeDumpThread extends SQLDump implements Runnable
{
   // Class Instances.
   private ProgressBar databaseDumpProgressBar;

   //==============================================================
   // SQLDatabaseSchemeDumpThread Constructor.
   //==============================================================

   public SQLDatabaseSchemeDumpThread(String fileName)
   {
      this.fileName = fileName;
   }

   //==============================================================
   // Class method for normal start of the thread
   //==============================================================

   public void run()
   {
      // Class Method Instances.
      Iterator<String> tableNamesIterator;
      String exportedTable;
      Object dumpData;

      // Get Connection to Database.
      Connection dbConnection = ConnectionManager.getConnection("DatabaseSchemeDumpThread run()");

      if (dbConnection == null)
         return;

      // Create a progress bar for giving the user a
      // visual and cancel ability.
      databaseDumpProgressBar = new ProgressBar("SQL Database Scheme Dump");

      // Setup the dump Class and Header.
      dumpData = generateHeaders();

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
         exportedTable = Utils.getSchemaTableName(tableNamesIterator.next());
         
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
}