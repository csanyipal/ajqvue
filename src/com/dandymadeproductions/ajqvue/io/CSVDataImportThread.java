//=================================================================
//                   CSVDataImportThread
//=================================================================
//
//    This class provide the means to import a standard CSV file
// into the current selected database table via a safe thread
// method. A progress bar is offered to address the ability to
// cancel the import.
//
//                 << CSVDataImportThread.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.3 06/04/2018
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
// Version 1.0 Production CSVDataImportThread Class.
//         1.1 Method importCSVFile() Additional Check for columnClass Not
//             Null in Conditional for Date, Datetime, & Timestamp.
//         1.2 Changed/Updated Import for SQLQuery Class.
//         1.3 Method importCSVFile() Changed Class Instance columnTypeHashMap
//             to columnTypeNameHashMap, & columnType to columnTypeName. Code
//             Formatting Changes for Instances.
//
//-----------------------------------------------------------------
//                   danap@dandymadeproductions.com
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
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.gui.panels.TableTabPanel;
import com.dandymadeproductions.ajqvue.utilities.ProgressBar;
import com.dandymadeproductions.ajqvue.utilities.Utils;
import com.dandymadeproductions.ajqvue.utilities.db.SQLQuery;

/**
 *    The CSVDataImportThread class provide the means to import a
 * standard CSV file into the current selected database table
 * via a safe thread method. A progress bar is offered to
 * address the ability to cancel the import.
 * 
 * @author Dana M. Proctor
 * @version 1.3 06/04/2018
 */

public class CSVDataImportThread implements Runnable
{
   // Class Instance Fields.
   private Connection dbConnection;
   
   private String dataSourceType;
   private String importTable;
   private String fileName;
   private String csvOption;
   private boolean argConnection;
   private boolean validImport;
   private boolean useStatusDialog;
   private boolean temporaryDataFile;

   //==============================================================
   // CSVDataImportThread Constructors.
   // 
   // The first for standard database login table import
   // functionality. Use the second for generic import into
   // a specified database connection table.
   //==============================================================
   
   public CSVDataImportThread(String fileName, String csvOption, boolean temporaryDataFile)
   {
      this(null, ConnectionManager.getDataSourceType(), "", fileName, csvOption, true, temporaryDataFile);
   }
   
   public CSVDataImportThread(Connection dbConnection, String dataSourceType,
                               String tableName, boolean useStatusDialog, String fileName)
   {
      this(dbConnection, dataSourceType, tableName, fileName, "Insert", useStatusDialog, false);
   }
   
   private CSVDataImportThread(Connection dbConnection, String dataSourceType, String importTable,
                                String fileName, String csvOption, boolean useStatusDialog,
                                boolean temporaryDataFile)
   {
      // Constructor Instances
      
      this.dbConnection = dbConnection;
      this.dataSourceType = dataSourceType;
      this.importTable = importTable;
      this.fileName = fileName;
      this.csvOption = csvOption;
      this.useStatusDialog = useStatusDialog;
      this.temporaryDataFile = temporaryDataFile;
      
      argConnection = true;
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
         // Importing data from CSV file
         importCSVFile();
         
         // Refreshing table panel to see new inserted data and
         // removing the  temporary file, clipboard pastes, if
         // needed.
         
         if (validImport && !argConnection)
         {
            refreshTableTabPanel();

            if (temporaryDataFile)
            {
               try
               {
                  if (!file.delete())
                     throw new SecurityException();
               }
               catch (SecurityException se)
               {
                  if (Ajqvue.getDebug())
                   System.out.println("Failed to Delete: '" + file + "'. " + se.toString());
               }
            }
         }
      }
      else
      {
         String msg = "The file '" + fileName + "' does not exists.";
         JOptionPane.showMessageDialog(null, msg, fileName, JOptionPane.ERROR_MESSAGE);
      }
   }

   //==============================================================
   // Class method for the major processing of the import of a
   // CSV data file.
   //==============================================================

   private void importCSVFile()
   {
      // Class Method Instances.
      Statement sqlStatement;
      StringBuffer sqlFieldNamesString, sqlValuesString;
      String sqlKeyString;
      StringBuffer sqlStatementString;

      FileReader fileReader;
      BufferedReader bufferedReader;

      String schemaTableName;
      ArrayList<String> primaryKeys, tableFields, fields;
      HashMap<String, String> columnTypeNameHashMap;
      HashMap<String, String> columnClassHashMap;
      String catalogSeparator;
      String identifierQuoteString;

      String currentLine;
      String columnClass;
      String columnTypeName;
      int fileLineLength;
      int fieldNumber;
      int line;
      String[] lineContent;
      int currentBatchRows;
      int batchSize;
      boolean batchSizeEnabled;
      boolean identityInsertEnabled;
      
      ProgressBar csvImportProgressBar;
      String dateFormat;

      // Setting up.
      tableFields = new ArrayList<String>();
      fields = new ArrayList<String>();
      
      dateFormat = DBTablesPanel.getDataImportProperties().getDateFormat();
      batchSize = DBTablesPanel.getGeneralDBProperties().getBatchSize();
      batchSizeEnabled = DBTablesPanel.getGeneralDBProperties().getBatchSizeEnabled();
      
      if (dataSourceType.equals(ConnectionManager.MSSQL)
          && DBTablesPanel.getSelectedTableTabPanel() != null)
         identityInsertEnabled = DBTablesPanel.getDataImportProperties().getIdentityInsert();
      else
         identityInsertEnabled = false;
      
      fileReader = null;
      bufferedReader = null;
      sqlStatement = null;
      csvImportProgressBar = new ProgressBar("CSV Import To: " + importTable);
      fileLineLength = 0;
      line = 0;
      
      // Determine database connection.
      
      try
      {
         if (dbConnection == null)
         {
            // Login Database.
            dbConnection = ConnectionManager.getConnection("CSVDataImportThread importCSVFile()");
         
            if (dbConnection == null || DBTablesPanel.getSelectedTableTabPanel() == null)
            {
               validImport = false;
               return;
            }
            
            importTable = DBTablesPanel.getSelectedTableTabPanel().getTableName();
            identifierQuoteString = ConnectionManager.getIdentifierQuoteString();
            schemaTableName = Utils.getSchemaTableName(importTable);
            primaryKeys = DBTablesPanel.getSelectedTableTabPanel().getPrimaryKeys();
            columnTypeNameHashMap = DBTablesPanel.getSelectedTableTabPanel().getColumnTypeNameHashMap();
            columnClassHashMap = DBTablesPanel.getSelectedTableTabPanel().getColumnClassHashMap();
         
            argConnection = false;
         }
         else
         {
            // Specified Database Connection.
            
            catalogSeparator = dbConnection.getMetaData().getCatalogSeparator();
            
            if (catalogSeparator == null || catalogSeparator.isEmpty())
               catalogSeparator = ".";
            
            identifierQuoteString = dbConnection.getMetaData().getIdentifierQuoteString();
            
            if (identifierQuoteString == null || identifierQuoteString.equals(" "))
               identifierQuoteString = "";
            
            schemaTableName = Utils.getSchemaTableName(importTable, catalogSeparator,
                                                                  identifierQuoteString);
            
            SQLQuery sqlQuery = new SQLQuery("SELECT * FROM " + schemaTableName);
         
            if (sqlQuery.executeSQL(dbConnection) != 1)
            {
               validImport = false;
               return;
            }
            
            primaryKeys = new ArrayList <String>();
            columnTypeNameHashMap = sqlQuery.getColumnTypeNameHashMap();
            columnClassHashMap = sqlQuery.getColumnClassHashMap();
         }
         
         // Disable autocommit and begin the start
         // of transactions.
         dbConnection.setAutoCommit(false);
         sqlStatement = dbConnection.createStatement();
         
         // MSSQL Overide Identity_Insert.
         if (identityInsertEnabled)
            sqlStatement.executeUpdate("SET IDENTITY_INSERT "
                                       + DBTablesPanel.getSelectedTableTabPanel().getTableName()
                                       + " ON");

         // Begin the processing of the input CSV file by reading
         // each line and separating field data. Expectation
         // being that the first line will hold the field names
         // thereafter data.
         
         // Only MySQL & PostgreSQL supports.
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

            csvImportProgressBar.setTaskLength(fileLineLength);
            csvImportProgressBar.pack();
            csvImportProgressBar.center();
            csvImportProgressBar.setVisible(useStatusDialog);
            validImport = true;

            // Beginning processing the input file for insertions
            // into the database table.

            bufferedReader.close();
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);

            sqlFieldNamesString = new StringBuffer();
            sqlValuesString = new StringBuffer();
            sqlStatementString = new StringBuffer();
            currentBatchRows = 0;
            fieldNumber = 0;
            line = 1;

            while ((currentLine = bufferedReader.readLine()) != null)
            {
               // System.out.println(currentLine);

               // Check to see if user wishes to stop.
               if (csvImportProgressBar.isCanceled())
               {
                  validImport = false;
                  break;
               }

               // Create the required SQL insertation statement
               // with field names or the update statement.
               if (line == 1)
               {
                  // Create Intial Insert Statement
                  if (csvOption.equals("Insert"))
                     sqlFieldNamesString.append("INSERT INTO " + schemaTableName + " (");
                  // Create Intial Update Statement
                  else
                     sqlFieldNamesString.append("UPDATE " + schemaTableName + " SET ");

                  lineContent = separateTokens(currentLine, 0);
                  fieldNumber = lineContent.length;

                  // Inserting fields names or storing them for later
                  // use with an update statement.
                  for (int i = 0; i < fieldNumber; i++)
                  {
                     if (csvOption.equals("Insert"))
                        sqlFieldNamesString.append(identifierQuoteString + lineContent[i]
                                                   + identifierQuoteString + ", ");
                     if (argConnection)
                        tableFields.add(lineContent[i]);
                     else
                        tableFields.add(parseColumnNameField(lineContent[i]));
                     fields.add(lineContent[i]);
                  }
                  if (csvOption.equals("Insert"))
                  {
                     sqlFieldNamesString.delete((sqlFieldNamesString.length() - 2),
                                                sqlFieldNamesString.length());
                     sqlFieldNamesString.append(") VALUES (");
                  }
               }

               // Create SQL statement data field content.
               else
               {
                  // Reset strings for each line of data.
                  sqlValuesString.delete(0, sqlValuesString.length());
                  sqlStatementString.delete(0, sqlStatementString.length());
                  sqlKeyString = "";

                  lineContent = separateTokens(currentLine, fieldNumber);

                  for (int i = 0; i < lineContent.length; i++)
                  {
                     columnClass = columnClassHashMap.get(tableFields.get(i));
                     columnTypeName = columnTypeNameHashMap.get(tableFields.get(i));
                     // System.out.println("tableField:" + tableFields.get(i) + " ColumnClass: "
                     //                    + columnClass + " ColumnType: " + columnTypeName
                     //                    + " " + lineContent[i]);

                     // Make sure and catch all null default entries first.

                     if (lineContent[i].toLowerCase(Locale.ENGLISH).equals("null")
                         || lineContent[i].toLowerCase(Locale.ENGLISH).equals("default"))
                     {
                        // Do Nothing.
                     }

                     // Just set lineContent with no data to default.

                     else if (lineContent[i].equals(""))
                        lineContent[i] = "default";

                     // All Blob/Bytea, Binary Data Exported as Text
                     // 'Binary' in DataDumpThread for CSV.

                     else if ((columnClass != null && columnTypeName != null)
                              && ((columnClass.indexOf("String") == -1 && columnTypeName.indexOf("BLOB") != -1)
                                  || (columnClass.indexOf("BLOB") != -1 && columnTypeName.indexOf("BLOB") != -1)
                                  || (columnTypeName.indexOf("BYTEA") != -1)
                                  || (columnTypeName.indexOf("BINARY") != -1) || (columnTypeName.indexOf("RAW") != -1)))
                     {
                        lineContent[i] = "null";
                     }

                     // MySQL/MariaDB Bit Fields

                     else if ((dataSourceType.equals(ConnectionManager.MYSQL)
                               || dataSourceType.equals(ConnectionManager.MARIADB))
                              && columnTypeName != null
                              && columnTypeName.indexOf("BIT") != -1)
                     {
                        lineContent[i] = "B'" + lineContent[i] + "'";
                     }

                     // PostgreSQL Geometric Fields

                     else if (dataSourceType.equals(ConnectionManager.POSTGRESQL) && columnClass != null
                              && columnClass.indexOf("geometric") != -1)
                     {
                        lineContent[i] = "'" + lineContent[i] + "'::" + columnTypeName;
                     }

                     // Date, DateTime, & Timestamp Fields

                     else if ((columnTypeName != null)
                              && (columnTypeName.equals("DATE") || columnTypeName.equals("DATETIME")
                                    || (columnTypeName.indexOf("TIMESTAMP") != -1)
                                        && (columnClass != null && columnClass.indexOf("Array") == -1)))
                     {
                        if (columnTypeName.equals("DATE"))
                        {
                           if (dataSourceType.equals(ConnectionManager.ORACLE))
                              lineContent[i] = "TO_DATE('"
                                               + Utils.convertViewDateString_To_DBDateString(
                                                  lineContent[i], dateFormat) + "', 'YYYY-MM-DD')";
                           else if (dataSourceType.equals(ConnectionManager.MSACCESS))
                              lineContent[i] = "#"
                                               + Utils.convertViewDateString_To_DBDateString(
                                                  lineContent[i], dateFormat) + "#";
                           else
                              lineContent[i] = "'"
                                               + Utils.convertViewDateString_To_DBDateString(
                                                  lineContent[i], dateFormat) + "'";
                        }
                        // DateTime & Timestamps.
                        else
                        {
                           int firstSpace;
                           String time;

                           // Try to get the time separated before formatting
                           // the date.

                           if (lineContent[i].indexOf(" ") != -1)
                           {
                              firstSpace = lineContent[i].indexOf(" ");
                              time = lineContent[i].substring(firstSpace);
                              lineContent[i] = lineContent[i].substring(0, firstSpace);
                           }
                           else
                              time = "";

                           // Process accordingly.

                           // Oracle Timestamps
                           if (dataSourceType.equals(ConnectionManager.ORACLE))
                           {
                              if (columnTypeName.equals("TIMESTAMP"))
                                 lineContent[i] = "TO_TIMESTAMP('"
                                                  + Utils.convertViewDateString_To_DBDateString(
                                                     lineContent[i], dateFormat) + time
                                                  + "', 'YYYY-MM-DD HH24:MI:SS:FF')";

                              else if (columnTypeName.equals("TIMESTAMPTZ")
                                       || columnTypeName.equals("TIMESTAMP WITH TIME ZONE"))
                                 lineContent[i] = "TO_TIMESTAMP_TZ('"
                                                  + Utils.convertViewDateString_To_DBDateString(
                                                     lineContent[i], dateFormat) + time
                                                  + "', 'YYYY-MM-DD HH24:MI:SS TZHTZM')";
                              // TIMESTAMPLTZ
                              else
                                 lineContent[i] = "TO_TIMESTAMP_TZ('"
                                                  + Utils.convertViewDateString_To_DBDateString(
                                                     lineContent[i], dateFormat) + time
                                                  + "', 'YYYY-MM-DD HH24:MI:SS TZH:TZM')";
                           }
                           // MSAccess
                           else if (dataSourceType.equals(ConnectionManager.MSACCESS))
                              lineContent[i] = "#"
                                               + Utils.convertViewDateString_To_DBDateString(
                                                  lineContent[i], dateFormat) + time + "#";
                           // All others
                           else
                              lineContent[i] = "'"
                                               + Utils.convertViewDateString_To_DBDateString(
                                                  lineContent[i], dateFormat) + time + "'";
                        }
                     }

                     // Normal Fields

                     else
                     {
                        // Don't Quote Numeric Values.
                        if (columnClass != null && columnClass.indexOf("Integer") == -1
                              && columnClass.indexOf("Long") == -1 && columnClass.indexOf("Float") == -1
                              && columnClass.indexOf("Double") == -1 && columnClass.indexOf("Byte") == -1
                              && columnClass.indexOf("BigDecimal") == -1 && columnClass.indexOf("Short") == -1)
                              
                           lineContent[i] = "'" + lineContent[i] + "'";
                     }

                     // Now that the content data as been derived create the
                     // appropriate Insert or Update SQL statement.

                     // Insert SQL.
                     if (csvOption.equals("Insert"))
                        sqlValuesString.append(lineContent[i] + ", ");
                     // Update SQL
                     else
                     {
                        // Capture key data for SQL.
                        if (primaryKeys.contains(fields.get(i)))
                        {
                           if (sqlKeyString.equals(""))
                           {
                              sqlKeyString = "WHERE " + identifierQuoteString + fields.get(i)
                                             + identifierQuoteString + "=" + lineContent[i] + " AND ";
                           }
                           else
                           {
                              sqlKeyString += identifierQuoteString + fields.get(i) + identifierQuoteString
                                              + "=" + lineContent[i] + " AND ";
                           }
                        }
                        // Normal content.
                        else
                           sqlValuesString.append(identifierQuoteString + fields.get(i)
                                                  + identifierQuoteString + "=" + lineContent[i] + ", ");
                     }
                  }
                  // System.out.println(sqlValuesString);

                  // Finishing the Insert or Update SQL statement.
                  if (csvOption.equals("Insert"))
                  {
                     sqlValuesString.delete((sqlValuesString.length() - 2), sqlValuesString.length());
                     sqlValuesString.append(")");
                  }
                  else
                  {
                     if (sqlValuesString.length() >= 2)
                        sqlValuesString.delete((sqlValuesString.length() - 2), sqlValuesString.length());
                     if (sqlKeyString.length() > 5)
                        sqlValuesString.append(" " + sqlKeyString.substring(0, sqlKeyString.length() - 5));
                     else
                        sqlValuesString.append(" " + sqlKeyString);
                  }

                  sqlStatementString.append(sqlFieldNamesString.toString() + sqlValuesString.toString());
                  // System.out.println(sqlStatementString);

                  // Insert/Update current line's data.
                  sqlStatement.addBatch(sqlStatementString.toString());
                  
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
               csvImportProgressBar.setCurrentValue(line++);
            }

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

            fileReader.close();
            bufferedReader.close();
            sqlStatement.close();
            dbConnection.setAutoCommit(true);
         }
         catch (IOException e)
         {
            csvImportProgressBar.setVisible(false);
            
            JOptionPane.showMessageDialog(null, "Unable to Read Input File!", "Alert",
               JOptionPane.ERROR_MESSAGE);
            try
            {
               sqlStatement.close();
               dbConnection.rollback();
               dbConnection.setAutoCommit(true);
               
               // MSSQL Overide Identity_Insert.
               if (identityInsertEnabled)
                  sqlStatement.executeUpdate("SET IDENTITY_INSERT "
                                             + DBTablesPanel.getSelectedTableTabPanel().getTableName()
                                             + " OFF");
               
               if (!argConnection)
                  ConnectionManager.closeConnection(dbConnection,
                                                    "CSVDataImportThread importCSVFile() rollback");
            }
            catch (SQLException error)
            {
               ConnectionManager.displaySQLErrors(error,
                  "SQLDataImportThread importCSVFile() rollback failed");
            }
         }
      }
      catch (SQLException e)
      {
         csvImportProgressBar.setVisible(false);
         
         ConnectionManager.displaySQLErrors(e, "line# " + line + " CSVDataImportThread importCSVLFile()");
         try
         {
            dbConnection.rollback();
            dbConnection.setAutoCommit(true);
            
            if (!argConnection)
               ConnectionManager.closeConnection(dbConnection,
                                                 "CSVDataImportThread importCSVFile() rollback");
         }
         catch (SQLException error)
         {
            ConnectionManager.displaySQLErrors(e, "CSVDataImportThread importCSVFile() rollback failed");
         }
      }
      finally
      {
         csvImportProgressBar.dispose();
         
         try
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle, "CSVDataImportThread importCSVFile() failed close");
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
                  System.out.println("CSVDataImporthread importCSV() Failed to Close BufferedReader. "
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
                     System.out.println("CSVDataImporthread importCSV() Failed to Close FileReader. "
                                        + ioe);
               }
            }
         }
         
         if (!argConnection)
            ConnectionManager.closeConnection(dbConnection, "CSVDataImportThread importCSVFile()");
      }
   }

   //==============================================================
   // Class method to parse the table's column name fields. The
   // parsed strings creates a more user friendly format that will
   // be displayed in the sort/search ComboBoxes, summary table,
   // and TableEntryForm.
   //==============================================================

   private String parseColumnNameField(String columnString)
   {
      // Class Methods.
      StringTokenizer field;
      StringBuffer columnName;

      // Initialize.
      columnName = new StringBuffer();

      // Delimiter '_' should seperate words in a name.

      // Multiple word name.
      if (columnString.indexOf('_') != -1)
      {
         field = new StringTokenizer(columnString, "_");

         while (field.hasMoreTokens())
         {
            if (field.countTokens() > 1)
            {
               columnName.append(firstLetterToUpperCase(field.nextToken()));
               columnName.append(" ");
            }
            else
               columnName.append(firstLetterToUpperCase(field.nextToken()));
         }
         columnString = columnName.toString();
      }

      // Single word name.
      else
      {
         columnString = firstLetterToUpperCase(columnString);
      }
      return columnString;
   }

   //==============================================================
   // Class method to convert the first letter of the input string
   // to uppercase.
   //==============================================================

   private String firstLetterToUpperCase(String capitalizeString)
   {
      String firstLetter;

      if (capitalizeString.length() >= 1)
      {
         firstLetter = capitalizeString.substring(0, 1);
         firstLetter = firstLetter.toUpperCase(Ajqvue.getLocale());
         return firstLetter + capitalizeString.substring(1);
      }
      else
         return capitalizeString;
   }

   //==============================================================
   // Class method for separating tokens in the imput
   // string.
   //==============================================================

   private String[] separateTokens(String inputLine, int limit)
   {
      // Class Instances
      String delimiter = DBTablesPanel.getDataImportProperties().getDataDelimiter();

      // Check characters?
      //if (delimiter.indexOf("\"") == -1)
      //   inputLine = inputLine.replaceAll("\"", "");

      if (delimiter.indexOf("'") == -1)
         inputLine = inputLine.replaceAll("'", "''");

      if (delimiter.indexOf("`") == -1)
         inputLine = inputLine.replaceAll("`", "''");

      String[] tokens;
      tokens = inputLine.split(delimiter, limit);
      return tokens;
   }

   //==============================================================
   // Class method to refresh table tab panel.
   //==============================================================

   private void refreshTableTabPanel()
   {
      TableTabPanel currentTableTabPanel = DBTablesPanel.getSelectedTableTabPanel();
      if (currentTableTabPanel != null)
      {
         ArrayList<String> tableHeadings = currentTableTabPanel.getCurrentTableHeadings();
         currentTableTabPanel.setTableHeadings(tableHeadings);
      }
   }
}
