//=================================================================
//                    SQLDataDumpImportThread
//=================================================================
//
//    This class provide the means to import a sql dump file into
// the current selected database via a safe thread method. A
// progress bar is offered to address the ability to cancel the
// import.
//
//              << SQLDataDumpImportThread.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
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
// Version 1.0 Production SQLDataDumpImportThread Class.
//          
//-----------------------------------------------------------------
//             poisonerbg@users.sourceforge.net
//              danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.Main_Frame;
import com.dandymadeproductions.ajqvue.gui.Ajqvue_JMenuBar;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.gui.panels.TableTabPanel;
import com.dandymadeproductions.ajqvue.utilities.ProgressBar;

/**
 *    The SQLDataDumpImportThread class provide the means to import
 * a sql dump file into the current selected database via a safe
 * thread method. A progress bar is offered to address the ability
 * to cancel the import.
 * 
 * @author Borislav Gizdov a.k.a. PoisoneR, Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class SQLDataDumpImportThread implements Runnable
{
   // Class Instance Fields.
   String fileName;
   String dataSourceType;
   boolean validImport, reloadDatabase;

   //==============================================================
   // SQLDataDumpImportThread Constructor.
   //==============================================================

   public SQLDataDumpImportThread(String fileName, boolean reloadedDatabase)
   {
      this.fileName = fileName;
      this.reloadDatabase = reloadedDatabase;
      
      dataSourceType = ConnectionManager.getDataSourceType();
   }

   //==============================================================
   // Class Method for Normal Start of the Thread
   //==============================================================

   public void run()
   {
      // Class Method Instances
      File file;

      // Begin Execution of getting the
      // file and trying an import.

      file = new File(fileName);

      if (file.exists())
      {
         // Importing data dump from SQL file
         importSQLFile();
         
         // Refreshing database tables or selected table
         // panel to see new inserted data
         if (validImport)
         {
            if (reloadDatabase)
            {
               // Make sure and load all available schemas
               // in case filtering is off in configuration
               // file.
               
               ConnectionManager.setSchemaPattern(ConnectionManager.getAllSchemasPattern());
               
               // Reload
               Main_Frame.reloadDBTables();
               Ajqvue_JMenuBar.reloadSchemasMenu();
               Main_Frame.reloadDBTables();
            }
            else
               refreshTableTabPanel();
         }
      }
      else
      {
         String msg = "The file '" + fileName + "' does not exists.";
         JOptionPane.showMessageDialog(null, msg, fileName, JOptionPane.ERROR_MESSAGE);
      }
   }

   //==============================================================
   // Class method for importing a sql dump file
   //==============================================================

   private void importSQLFile()
   {
      // Class Method Instances.
      Connection dbConnection;
      Statement sqlStatement;

      FileReader fileReader;
      BufferedReader bufferedReader;

      String currentLine;
      StringBuffer queryStatement;
      String failedQuery;
      int fileLineLength, line;
      int currentBatchRows, batchSize;
      boolean batchSizeEnabled, identityInsertEnabled;
      
      ProgressBar sqlImportProgressBar;

      // Obtain database connection & setting up.

      dbConnection = ConnectionManager.getConnection("SQLDataDumpImportThread importSQLFile()");
      
      if (dbConnection == null)
      {
         validImport = false;
         return;
      }
      
      sqlImportProgressBar = new ProgressBar("SQL Import");
      failedQuery = "";
      fileLineLength = 0;
      line = 0;
      batchSize = DBTablesPanel.getGeneralDBProperties().getBatchSize();
      batchSizeEnabled = DBTablesPanel.getGeneralDBProperties().getBatchSizeEnabled();
      
      if (dataSourceType.equals(ConnectionManager.MSSQL)
          && DBTablesPanel.getSelectedTableTabPanel() != null)
         identityInsertEnabled = DBTablesPanel.getDataImportProperties().getIdentityInsert();
      else
         identityInsertEnabled = false;
      
      // Begin the processing of the input SQL file by reading
      // each line and checking before insert if it is valid
      // not a comment.
      
      fileReader = null;
      bufferedReader = null;
      sqlStatement = null;
      
      try
      {
         // Disable autocommit and begin the start
         // of transactions.
         dbConnection.setAutoCommit(false);
         sqlStatement = dbConnection.createStatement();
         
         // MSSQL Overide Identity_Insert.
         if (identityInsertEnabled)
            sqlStatement.executeUpdate("SET IDENTITY_INSERT "
                                       + DBTablesPanel.getSelectedTableTabPanel().getTableName()
                                       + " ON");

         // Only MySQL, MariaDB, & PostgreSQL supports.
         if (dataSourceType.equals(ConnectionManager.MYSQL)
             || dataSourceType.equals(ConnectionManager.MARIADB)
             || dataSourceType.equals(ConnectionManager.POSTGRESQL))
            sqlStatement.executeUpdate("BEGIN");

         try
         {
            // Setting file reader & progress bar.
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);

            while ((currentLine = bufferedReader.readLine()) != null)
               fileLineLength++;

            sqlImportProgressBar.setTaskLength(fileLineLength);
            sqlImportProgressBar.pack();
            sqlImportProgressBar.center();
            sqlImportProgressBar.setVisible(true);
            validImport = true;

            // Beginning processing the input file for insertions
            // into the database table.

            bufferedReader.close();
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
            
            line = 1;
            currentBatchRows = 0;
            queryStatement = new StringBuffer();
            
            while ((currentLine = bufferedReader.readLine()) != null)
            {
               // System.out.println(currentLine);

               // Check to see if user wishes to stop.
               if (sqlImportProgressBar.isCanceled())
               {
                  validImport = false;
                  break;
               }
               
               // Check for some form of valid input before
               // processing.
               
               if (!currentLine.isEmpty())
               {
                  if (currentLine.length() >= 2
                        && (currentLine.startsWith("--") || currentLine.startsWith("/*")
                              || currentLine.startsWith("*") || currentLine.startsWith("*/")))
                  {
                     sqlImportProgressBar.setCurrentValue(line++);
                     continue;
                  }
                  
                  // Check to see if complete query obtained.
                  if (currentLine.endsWith(";"))
                  {
                     queryStatement.append(currentLine.substring(0, currentLine.length() - 1));
                     
                     // Save the query in case exception thrown.
                     if (queryStatement.length() > 50)
                        failedQuery = queryStatement.substring(0, 50);
                     else
                        failedQuery = queryStatement.toString();
                     
                     // Process the query.
                     // System.out.println("query: " + queryStatement);
                     sqlStatement.addBatch(queryStatement.toString());
                     
                     queryStatement.delete(0, queryStatement.length());
                     
                     // Commit on Batch Size if Desired.
                     if (batchSizeEnabled)
                     {
                        if (currentBatchRows > batchSize)
                        {
                           sqlStatement.executeBatch();
                           dbConnection.commit();
                           currentBatchRows = 0;
                        }
                        else
                           currentBatchRows++;
                     }     
                  }
                  else
                     queryStatement.append(currentLine);  
               } 
               sqlImportProgressBar.setCurrentValue(line++);
            }
            sqlImportProgressBar.dispose();

            // Commiting the transactions as necessary
            // and cleaning up.

            if (validImport)
            {
               sqlStatement.executeBatch();
               dbConnection.commit();
            }
            else
               dbConnection.rollback();
            
            // MSSQL Overide Identity_Insert.
            if (identityInsertEnabled)
               sqlStatement.executeUpdate("SET IDENTITY_INSERT "
                                          + DBTablesPanel.getSelectedTableTabPanel().getTableName()
                                          + " OFF");

            dbConnection.setAutoCommit(true);
         }
         catch (IOException e)
         {
            sqlImportProgressBar.dispose();
            JOptionPane.showMessageDialog(null, "Unable to Read Input File!", "Alert",
               JOptionPane.ERROR_MESSAGE);
            try
            {
               dbConnection.rollback();
               dbConnection.setAutoCommit(true);
            
               // MSSQL Overide Identity_Insert.
               if (identityInsertEnabled)
                  sqlStatement.executeUpdate("SET IDENTITY_INSERT "
                                             + DBTablesPanel.getSelectedTableTabPanel().getTableName()
                                             + " OFF");
               ConnectionManager
                     .closeConnection(dbConnection, "SQLDataDumpImportThread importSQLFile() rollback");
            }
            catch (SQLException error)
            {
               ConnectionManager.displaySQLErrors(error,
                  "SQLDataDumpImportThread importSQLFile() rollback failed");
            }
         }
      }
      catch (SQLException e)
      {
         sqlImportProgressBar.dispose();
         ConnectionManager.displaySQLErrors(e, "line# " + line + " " + failedQuery
                                               + " SQLDataDumpImportThread importSQLFile()");
         try
         {
            dbConnection.rollback();
            dbConnection.setAutoCommit(true);
            ConnectionManager.closeConnection(dbConnection, "SQLDataDumpImportThread importSQLFile() rollback");
         }
         catch (SQLException error)
         {
            ConnectionManager.displaySQLErrors(e, "SQLDataDumpImportThread importSQLFile() rollback failed");
         }
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
            ConnectionManager.displaySQLErrors(sqle, "SQLDataImportThread importSQLFile() failed close");
         }
         finally
         {
            try
            {
               if (bufferedReader != null)
                  bufferedReader.close();
            }
            catch (IOException ioe)
            {
               if (Ajqvue.getDebug())
                  System.out.println("SQLDataImporthread importSQL() Failed to Close BufferedReader. "
                                     + ioe);
            }
            finally
            {
               try
               {
                  if (fileReader != null)
                     fileReader.close();
               }
               catch (IOException ioe)
               {
                  if (Ajqvue.getDebug())
                     System.out.println("SQLDataImporthread importSQL() Failed to Close FileReader. "
                                        + ioe);
               }
            }
         }
         ConnectionManager.closeConnection(dbConnection, "SQLDataDumpImportThread importSQLFile()");
      }
   }
   
   //==============================================================
   // Class method to refresh table tab panel.
   //==============================================================

   private void refreshTableTabPanel()
   {
      TableTabPanel currentTableTabPanel = DBTablesPanel.getSelectedTableTabPanel();
      if (currentTableTabPanel != null)
      {
         ArrayList<String> tableFields = currentTableTabPanel.getCurrentTableHeadings();
         currentTableTabPanel.setTableHeadings(tableFields);
      }
   }
}
