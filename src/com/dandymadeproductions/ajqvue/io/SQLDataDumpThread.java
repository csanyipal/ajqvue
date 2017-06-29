//=================================================================
//                    SQL DataDumpThread
//=================================================================
//   This class provides a thread to safely dump database table
// data to a local file in SQL format. A status dialog with cancel
// is provided to provide the ability to prematurely terminate the
// dump.
//
//                   << SQLDataDumpThread.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.1 06/29/2017
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
// Version 1.0 Production SQLDataDumpThread Class.
//         1.1 Added Extends SQLDump. Removed Class Instance version & Same
//             From Constructor. Removed String Argument Constructor. Removed
//             Class Methods generateHeaders(), genCommentSep(), addEscapes(),
//             & dumpChunkOfData(). Organized Imports.
//             
//-----------------------------------------------------------------
//                poisonerbg@users.sourceforge.net
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.structures.DataExportProperties;
import com.dandymadeproductions.ajqvue.utilities.ProgressBar;
import com.dandymadeproductions.ajqvue.utilities.TableDefinitionGenerator;

/**
 *    The SQLDataDumpThread provides a thread to safely dump database
 * table data to a local file in SQL format. A status dialog with
 * cancel is provided to provide the ability to prematurely terminate
 * the dump.
 * 
 * @author Borislav Gizdov a.k.a. PoisoneR, Dana Proctor
 * @version 1.1 06/29/2017
 */

public class SQLDataDumpThread extends SQLDump implements Runnable
{
   // Class Instances.
   private Object dumpData;
   private String exportedTable;
   private ArrayList<String> columnNameFields;
   private HashMap<String, String> tableColumnNames;
   private HashMap<String, String> tableColumnClassHashMap;
   private HashMap<String, String> tableColumnTypeHashMap;
   private String dataSourceType, schemaName, tableName;
   private String dbSchemaTableName, schemaTableName;
   private String dbIdentifierQuoteString, identifierQuoteString;

   private boolean limits, insertReplaceDump, updateDump;
   private int limitIncrement, pluralValueLimit;
   private DataExportProperties sqlDataExportOptions;
   private ProgressBar dumpProgressBar;
   
   //==============================================================
   // SQLDataDumpThread Constructor.
   //==============================================================

   public SQLDataDumpThread(ArrayList<String> columnNameFields, HashMap<String, String> tableColumnNames,
                            boolean limits, HashMap<String, String> tableColumnClassHashMap,
                            HashMap<String, String> tableColumnTypeHashMap, String exportedTable,
                            String fileName)
   {
      this.columnNameFields = columnNameFields;
      this.tableColumnNames = tableColumnNames;
      this.limits = limits;
      this.tableColumnClassHashMap = tableColumnClassHashMap;
      this.tableColumnTypeHashMap = tableColumnTypeHashMap;
      this.exportedTable = exportedTable;
      this.fileName = fileName;
      
      // Setup control instances, export options, & identifier String.
      updateDump = false;
      insertReplaceDump = false;
      dataSourceType = ConnectionManager.getDataSourceType();
      dbIdentifierQuoteString = ConnectionManager.getIdentifierQuoteString();
      sqlDataExportOptions = DBTablesPanel.getDataExportProperties();
      identifierQuoteString = sqlDataExportOptions.getIdentifierQuoteString();
      if (sqlDataExportOptions.getInsertReplaceUpdate().equals("Insert"))
         pluralValueLimit = sqlDataExportOptions.getInsertPluralSize();
      else
         pluralValueLimit = sqlDataExportOptions.getReplacePluralSize();
      limitIncrement = DBTablesPanel.getGeneralDBProperties().getLimitIncrement();

      // Create the appropriate SQL table name qualifier.
      if (exportedTable.indexOf(".") != -1)
      {
         dbSchemaTableName = dbIdentifierQuoteString 
                             + exportedTable.substring(0, exportedTable.indexOf("."))
                             + dbIdentifierQuoteString + "." + dbIdentifierQuoteString
                             + exportedTable.substring(exportedTable.indexOf(".") + 1)
                             + dbIdentifierQuoteString;
         schemaTableName = identifierQuoteString + exportedTable.substring(0, exportedTable.indexOf("."))
                           + identifierQuoteString + "." + identifierQuoteString
                           + exportedTable.substring(exportedTable.indexOf(".") + 1) + identifierQuoteString;
      }
      else
      {
         dbSchemaTableName = dbIdentifierQuoteString + exportedTable + dbIdentifierQuoteString;
         schemaTableName = identifierQuoteString + exportedTable + identifierQuoteString;
      }
   }

   //==============================================================
   // Class method for normal start of the thread
   //==============================================================

   public void run()
   {
      try
      {
         dumpData();
      }
      catch (Exception e)
      {
         // Failed to close resource.
      }
   }
   
   //==============================================================
   // Class method to handle the data dump.
   //==============================================================
   
   private void dumpData() throws Exception
   {
      // Class Method Instances.
      FileOutputStream fileStream;
      Statement sqlStatement;
      ResultSet rs;

      // Get Connection to Database.
      Connection dbConnection = ConnectionManager.getConnection("SQLDataDumpThread run()");
      
      if (dbConnection == null)
         return;
      
      fileStream = null;
      filebuff = null;

      // Setting up OutputStream
      try
      {
         File makeNewFile = new File(fileName);
         if (makeNewFile.exists())
         {
            try
            {
               if (!makeNewFile.delete())
                  throw new SecurityException();
            }
            catch (SecurityException se)
            {
               if (Ajqvue.getDebug())
                System.out.println("Failed to Delete: '" + fileName + "'. " + se.toString());
            }
         }
         fileStream = new FileOutputStream(fileName, true);
         filebuff = new BufferedOutputStream(fileStream);

         // Create a progress bar for giving the user a
         // visual and cancel ability.
         dumpProgressBar = new ProgressBar(exportedTable + " SQL Data Dump");

         // =========================================================
         // Begin creating the data characters to be dumped into
         // the selected file.

         // Header info.
         dumpData = generateHeaders();

         // Check & see if anything is going to be exported
         if (!sqlDataExportOptions.getTableStructure() && !sqlDataExportOptions.getTableData())
         {
            dumpChunkOfData(dumpData);
            dumpProgressBar.dispose();
            ConnectionManager.closeConnection(dbConnection, "SQLDataDumpThread run()");
            return;
         }

         // Drop and Create Table Statements As Needed.
         if (sqlDataExportOptions.getTableStructure())
         {
            dumpData = dumpData + genCommentSep("Table structure for table " + schemaTableName);

            dumpData = dumpData
                       + (new TableDefinitionGenerator(dbConnection, dbSchemaTableName)).getTableDefinition();

            // Check to see if we need to proceed with dumping
            // data. If not dump structure and clean up.

            if (!sqlDataExportOptions.getTableData())
            {
               dumpChunkOfData(dumpData);
               dumpProgressBar.dispose();
               ConnectionManager.closeConnection(dbConnection, "SQLDataDumpThread run()");
               return;
            }
         }

         // Comments for Table.
         dumpData = dumpData + genCommentSep("Dumping data for table " + schemaTableName);

         // Check to see if there is any data to actually be
         // dumped from the table and proceeding as necesary.
         
         sqlStatement = null;
         rs = null;
         
         try
         {
            sqlStatement = dbConnection.createStatement();

            if (dataSourceType.equals(ConnectionManager.ORACLE))
               rs = sqlStatement.executeQuery("SELECT * FROM " + dbSchemaTableName + " WHERE ROWNUM=1");
            else if (dataSourceType.equals(ConnectionManager.MSACCESS))
               rs = sqlStatement.executeQuery("SELECT * FROM " + dbSchemaTableName + " AS t");
            else if (dataSourceType.equals(ConnectionManager.MSSQL))
               rs = sqlStatement.executeQuery("SELECT TOP 1 * FROM " + dbSchemaTableName + " AS t");
            else if (dataSourceType.equals(ConnectionManager.DERBY))
               rs = sqlStatement.executeQuery("SELECT * FROM " + schemaTableName 
                                              + " AS t FETCH FIRST ROW ONLY");
            else
               rs = sqlStatement.executeQuery("SELECT * FROM " + dbSchemaTableName + " AS t LIMIT 1");

            if (rs.next())
            {
               // Lock.
               if (sqlDataExportOptions.getLock())
               {
                  if (dataSourceType.equals(ConnectionManager.MYSQL)
                      || dataSourceType.equals(ConnectionManager.MARIADB))
                  {
                     dumpData = dumpData + ("/*!40000 ALTER TABLE " 
                                + schemaTableName + " DISABLE KEYS */;\n");
                     dumpData = dumpData + ("LOCK TABLES " + schemaTableName + " WRITE;\n");
                  }
                  else if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
                     dumpData = dumpData + ("LOCK TABLE " + schemaTableName + ";\n");
               }

               // Create the Appropriate Insert,Replace or Update Statements
               // with data as needed.

               // Insert
               if (sqlDataExportOptions.getInsertReplaceUpdate().equals("Insert"))
               {
                  if (sqlDataExportOptions.getInsertExpression().equals("Explicit"))
                     explicitStatementData(dbConnection);
                  else
                     insertReplaceStatementData(dbConnection);     
               }
               // Replace
               else if (sqlDataExportOptions.getInsertReplaceUpdate().equals("Replace"))
               {
                  if (sqlDataExportOptions.getReplaceExpression().equals("Explicit"))
                     explicitStatementData(dbConnection);
                  else
                     insertReplaceStatementData(dbConnection);     
               }
               // Update
               else
                  explicitStatementData(dbConnection);
               
               dumpData = dumpData + ";\n";

               // Finishing up.
               if (sqlDataExportOptions.getLock())
               {
                  if (dataSourceType.equals(ConnectionManager.MYSQL)
                      || dataSourceType.equals(ConnectionManager.MARIADB))
                  {
                     dumpData = dumpData + "UNLOCK TABLES;\n";
                     dumpData = dumpData + "/*!40000 ALTER TABLE " + schemaTableName 
                                + " ENABLE KEYS */;\n";
                  }
               }
            }
         }
         catch (SQLException e)
         {
            ConnectionManager.displaySQLErrors(e, "SQLDataDumpThread run()");
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
               ConnectionManager.displaySQLErrors(sqle, "SQLDataDumpThread run()");
            }
            finally
            {
               if (sqlStatement != null)
                  sqlStatement.close();
            }
         }
         dumpChunkOfData(dumpData);
         dumpData = "";

         ConnectionManager.closeConnection(dbConnection, "SQLDataDumpThread run()");

         if (dumpProgressBar.isCanceled())
         {
            try
            {
               if (!makeNewFile.delete())
                  throw new SecurityException();
            }
            catch (SecurityException se)
            {
               if (Ajqvue.getDebug())
                System.out.println("Failed to Delete: '" + fileName + "'. " + se.toString());
            }
            
         }
         dumpProgressBar.dispose();
      }
      catch (IOException e)
      {
         String msg = "Unable to Create filestream for: '" + fileName + "'.";
         JOptionPane.showMessageDialog(null, msg, fileName, JOptionPane.ERROR_MESSAGE);
         return;
      }
      finally
      {
         try
         {
            if (filebuff != null)
               filebuff.close();
         }
         catch (IOException ioe)
         {
            if (Ajqvue.getDebug())
               System.out.println("SQLDataDumpThread dumpData() Failed to Close BufferedOutputStream. "
                                  + ioe);
         }
         finally
         {
            try
            {
               if (fileStream != null)
                  fileStream.close();
            }
            catch (IOException ioe)
            {
               if (Ajqvue.getDebug())
                  System.out.println("SQLDataDumpThread dumpData() Failed to Close FileStream. "
                                     + ioe);
            }
         } 
      }
   }
   
   //==============================================================
   // Class method to create the insert/replace statement and data.
   //==============================================================

   private void insertReplaceStatementData(Connection dbConnection) throws SQLException
   {
      // Class Method Instances
      StringBuffer columnNamesString;
      StringBuffer oracleColumnNamesString;
      Iterator<String> columnNamesIterator;
      HashMap<Integer, String> autoIncrementFieldIndexes;
      ArrayList<Integer> blobFieldIndexes;
      ArrayList<Integer> bitFieldIndexes;
      ArrayList<Integer> timeStampIndexes;
      ArrayList<Integer> oracleTimeStamp_TZIndexes;
      ArrayList<Integer> oracleTimeStamp_LTZIndexes;
      ArrayList<Integer> dateIndexes;
      ArrayList<Integer> yearIndexes;
      ArrayList<Integer> arrayIndexes;
      ArrayList<Integer> numericIndexes;
      String field, columnClass, columnType;
      String firstField, sqlFieldValuesString;
      String expressionType;
      int rowsCount, currentTableIncrement, currentRow;
      int currentPluralValueCount;
      int columnsCount;

      String sqlStatementString;
      Statement sqlStatement;
      ResultSet rs;

      // Setting up the initial dump data string with insert/replace, type,
      // and table.

      insertReplaceDump = true;
      sqlFieldValuesString = (sqlDataExportOptions.getInsertReplaceUpdate().toUpperCase(Locale.ENGLISH)
                              + sqlDataExportOptions.getType().toUpperCase(Locale.ENGLISH) 
                              + "INTO " + schemaTableName + " (");

      if (sqlDataExportOptions.getInsertReplaceUpdate().toUpperCase(Locale.ENGLISH).equals("INSERT"))
         expressionType = sqlDataExportOptions.getInsertExpression();
      else
         expressionType = sqlDataExportOptions.getReplaceExpression();

      // Obtain the table fields and obtain list of specific
      // fields that need special handling.

      columnsCount = 0;
      columnNamesString = new StringBuffer();
      oracleColumnNamesString = new StringBuffer();
      columnNamesIterator = columnNameFields.iterator();
      autoIncrementFieldIndexes = new HashMap <Integer, String>();
      blobFieldIndexes = new ArrayList <Integer>();
      bitFieldIndexes = new ArrayList <Integer>();
      timeStampIndexes = new ArrayList <Integer>();
      oracleTimeStamp_TZIndexes = new ArrayList <Integer>();
      oracleTimeStamp_LTZIndexes = new ArrayList <Integer>();
      dateIndexes = new ArrayList <Integer>();
      yearIndexes = new ArrayList <Integer>();
      arrayIndexes = new ArrayList <Integer>();
      numericIndexes = new ArrayList <Integer>();

      while (columnNamesIterator.hasNext())
      {
         field = columnNamesIterator.next();
         columnClass = tableColumnClassHashMap.get(field);
         columnType = tableColumnTypeHashMap.get(field);
         // System.out.println("field:" + field + " class:" + columnClass +
         //                   "type:" + columnType);

         // Save the index of autoIncrement entries.
         if (DBTablesPanel.getSelectedTableTabPanel().getAutoIncrementHashMap().containsKey(field))
         {
            if (dataSourceType.equals(ConnectionManager.ORACLE))
               autoIncrementFieldIndexes.put(Integer.valueOf(columnsCount + 1),
                         DBTablesPanel.getSelectedTableTabPanel().getAutoIncrementHashMap().get(field));
            else if (dataSourceType.equals(ConnectionManager.MSSQL)
                     && sqlDataExportOptions.getAutoIncrement())
               continue;
            else
               autoIncrementFieldIndexes.put(Integer.valueOf(columnsCount + 1), tableColumnNames.get(field));
         }

         // Save the index of blob/bytea/binary/image/raw entries.
         if ((columnClass.indexOf("String") == -1 && columnType.indexOf("BLOB") != -1) ||
             (columnClass.indexOf("BLOB") != -1 && columnType.indexOf("BLOB") != -1) ||
             (columnType.indexOf("BYTEA") != -1) || (columnType.indexOf("BINARY") != -1) ||
             (columnType.indexOf("IMAGE")!= -1) || (columnType.indexOf("RAW") != -1))
         {
            blobFieldIndexes.add(Integer.valueOf(columnsCount + 1));
         }

         // Save the index of bit entries.
         if (columnType.indexOf("BIT") != -1)
         {
            if (!dataSourceType.equals(ConnectionManager.MSACCESS)
                && !dataSourceType.equals(ConnectionManager.MSSQL))
               bitFieldIndexes.add(Integer.valueOf(columnsCount + 1));
         }

         // Save the index of TimeStamp Fields.
         if (columnType.indexOf("TIMESTAMP") != -1)
         {
            if (!dataSourceType.equals(ConnectionManager.MSSQL))
               timeStampIndexes.add(Integer.valueOf(columnsCount + 1));
         }

         // Save the index of Oracle TimeStamp(TZ) Fields.
         if (dataSourceType.equals(ConnectionManager.ORACLE) &&
             (columnType.equals("TIMESTAMP") || columnType.equals("TIMESTAMPTZ")
              || columnType.equals("TIMESTAMP WITH TIME ZONE")
              || columnType.equals("TIMESTAMP WITH LOCAL TIME ZONE")))
         {
            oracleTimeStamp_TZIndexes.add(Integer.valueOf(columnsCount + 1));
         }

         // Save the index of Oracle TimeStamp(LTZ) Fields.
         if (dataSourceType.equals(ConnectionManager.ORACLE) &&
             columnType.equals("TIMESTAMPLTZ"))
         {
            oracleTimeStamp_LTZIndexes.add(Integer.valueOf(columnsCount + 1));
         }

         // Save the index of date entries.
         if (columnType.equals("DATE"))
         {
            dateIndexes.add(Integer.valueOf(columnsCount + 1));
         }

         // Save the index of year entries.
         if (columnType.indexOf("YEAR") != -1)
         {
            yearIndexes.add(Integer.valueOf(columnsCount + 1));
         }

         // Save the index of array entries.
         if (columnType.indexOf("_") != -1)
         {
            arrayIndexes.add(Integer.valueOf(columnsCount + 1));
         }
         
         // Save the index of numeric entries.
         if (columnClass.indexOf("Integer") != -1 || columnClass.indexOf("Long") != -1
             || columnClass.indexOf("Float") != -1 || columnClass.indexOf("Double") != -1
             || (columnClass.indexOf("Byte") != -1 && columnType.indexOf("CHAR") == -1)
             || columnClass.indexOf("BigDecimal") != -1 || columnClass.indexOf("Short") != -1)
         {
            numericIndexes.add(Integer.valueOf(columnsCount + 1));
         }

         // Modify Statement as needed for Oracle TIMESTAMPLTZ Fields.
         if (dataSourceType.equals(ConnectionManager.ORACLE) &&
             columnType.equals("TIMESTAMPLTZ"))
         {
            oracleColumnNamesString.append("TO_CHAR(" + dbIdentifierQuoteString + tableColumnNames.get(field)
                                           + dbIdentifierQuoteString + ", 'YYYY-MM-DD HH24:MM:SS TZR') AS "
                                           + dbIdentifierQuoteString + tableColumnNames.get(field)
                                           + dbIdentifierQuoteString + ", ");
         }
         else
            oracleColumnNamesString.append(dbIdentifierQuoteString + tableColumnNames.get(field)
                                           + dbIdentifierQuoteString + ", ");
         // Unmodified Names.
         columnNamesString.append(dbIdentifierQuoteString + tableColumnNames.get(field)
                                  + dbIdentifierQuoteString + ", ");
         sqlFieldValuesString += (identifierQuoteString + tableColumnNames.get(field) 
                                  + identifierQuoteString + ", ");

         columnsCount++;
      }
      oracleColumnNamesString.delete((oracleColumnNamesString.length() - 2),
                                      oracleColumnNamesString.length());
      columnNamesString.delete((columnNamesString.length() - 2), columnNamesString.length());
      firstField = columnNamesString.substring(0, columnNamesString.indexOf(","));

      // Do an initial dump of data created so far.

      sqlFieldValuesString = sqlFieldValuesString.substring(0, sqlFieldValuesString.length() - 2);
      sqlFieldValuesString += ") VALUES";
      dumpData = dumpData + sqlFieldValuesString;

      dumpChunkOfData(dumpData);
      dumpData = "";
      
      // Collect the row count of the table and setting
      // up a progress bar for tracking/canceling.
      
      rowsCount = 0;
      
      if (limits)
         rowsCount = DBTablesPanel.getSelectedTableTabPanel().getValidDataRowCount();
      else
      {
         try
         {
            rowsCount = getRowsCount(dbConnection, dbSchemaTableName);
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle, "SQLDataDumpThread insertReplaceStatementData()");
         }
      }
      
      currentPluralValueCount = 0;
      currentTableIncrement = 0;
      currentRow = 0;

      // Start a progress bar for tracking/canceling.
      dumpProgressBar.setTaskLength(rowsCount);
      dumpProgressBar.pack();
      dumpProgressBar.center();
      dumpProgressBar.setVisible(true);
      
      // Ok now ready so beginning by connecting to database for
      // data and proceeding with building the dump data.
      
      sqlStatement = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();

         // Setting up to begin insert statements.
         do
         {
            // Finishing creating the Select statement to retrieve data.
            if (limits)
               sqlStatementString = DBTablesPanel.getSelectedTableTabPanel().getTableSQLStatement().toString();
            else
            {
               // Oracle
               if (dataSourceType.equals(ConnectionManager.ORACLE))
                  sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                       + "(SELECT ROW_NUMBER() OVER (ORDER BY " + firstField + " ASC) " 
                                       + "AS dmprownumber, " + oracleColumnNamesString.toString() + " "
                                       + "FROM " + dbSchemaTableName + ") " + "WHERE dmprownumber BETWEEN "
                                       + (currentTableIncrement + 1) + " AND " + (currentTableIncrement
                                       + limitIncrement);
               // MSAccess
               else if (dataSourceType.equals(ConnectionManager.MSACCESS))
                  sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                        + dbSchemaTableName;
               // MSSQL
               else if (dataSourceType.equals(ConnectionManager.MSSQL))
               {
                  sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                       + "(SELECT *, ROW_NUMBER() OVER (ORDER BY " + firstField + " ASC) "
                                       + "AS dmprownumber FROM " + schemaTableName + " AS t) AS t1 "
                                       + "WHERE t1.dmprownumber BETWEEN "
                                       + (currentTableIncrement + 1) + " AND " + (currentTableIncrement
                                             + limitIncrement);
               }
               // Derby
               else if (dataSourceType.equals(ConnectionManager.DERBY))
                  sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                       + schemaTableName + " OFFSET " + currentTableIncrement + " ROWS "
                                       + "FETCH NEXT " + limitIncrement + " ROWS ONLY";
               else
                  sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                       + dbSchemaTableName + " LIMIT " + limitIncrement + " OFFSET "
                                       + currentTableIncrement;
            }
            // System.out.println(sqlStatementString);
            
            rs = sqlStatement.executeQuery(sqlStatementString);
            
            // Begin the creation of insert statements.
            while (rs.next() && !dumpProgressBar.isCanceled())
            {
               dumpProgressBar.setCurrentValue(currentRow++);
               currentPluralValueCount++;

               // SQL Singular Statement
               if (expressionType.equals("Singular"))
                  dumpData = dumpData + "(";
               // SQL Plural Statement
               else
                  dumpData = dumpData + ("\n(");

               for (int i = 1; i <= columnsCount; i++)
               {
                  // System.out.print(i + " ");

                  // Determining binary types and acting appropriately.
                  if (blobFieldIndexes.contains(Integer.valueOf(i)))
                  {
                     byte[] theBytes = rs.getBytes(i);

                     if (theBytes != null)
                     {
                        if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
                           dumpData = dumpData + "E'";
                        else if (dataSourceType.equals(ConnectionManager.HSQL))
                           dumpData = dumpData + "'";
                        else if (dataSourceType.equals(ConnectionManager.HSQL2))
                           dumpData = dumpData + "x'";
                        else if (dataSourceType.equals(ConnectionManager.ORACLE))
                           dumpData = dumpData + "HEXTORAW('";
                        else if (dataSourceType.equals(ConnectionManager.SQLITE))
                           dumpData = dumpData + "x'";
                        else if (dataSourceType.equals(ConnectionManager.DERBY))
                           dumpData = dumpData + "CAST(X'";
                        else if (dataSourceType.equals(ConnectionManager.H2))
                           dumpData = dumpData + "x'";
                        else
                        {
                           if (theBytes.length != 0)
                              dumpData = dumpData + "0x";
                           else
                              dumpData = dumpData + "''";
                        }

                        // Go convert to hexadecimal/octal values
                        // and dump data as we go for blob/bytea.
                        dumpBinaryData(theBytes, false);
                     }
                     else
                        dumpData = dumpData + "NULL, ";
                  }
                  // Regular Fields
                  else
                  {
                     // Check for an AutoIncrement
                     if (autoIncrementFieldIndexes.containsKey(Integer.valueOf(i))
                         && sqlDataExportOptions.getAutoIncrement())
                     {
                        if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
                        {
                           schemaName = schemaTableName.substring(0, schemaTableName.indexOf(".") + 2);
                           tableName = (schemaTableName.substring(schemaTableName.indexOf(".") + 1)).replaceAll(
                                                                  identifierQuoteString, "");

                           dumpData = dumpData + "nextval('" + schemaName + tableName + "_"
                                      + autoIncrementFieldIndexes.get(Integer.valueOf(i)) + "_seq\"'), ";
                        }
                        else if (dataSourceType.equals(ConnectionManager.ORACLE))
                        {
                           dumpData = dumpData + identifierQuoteString
                                      + autoIncrementFieldIndexes.get(Integer.valueOf(i)) 
                                      + identifierQuoteString + ".NEXTVAL, ";
                        }
                        else if (dataSourceType.equals(ConnectionManager.DERBY))
                           dumpData = dumpData + "DEFAULT, ";
                        else
                           dumpData = dumpData + "NULL, ";
                     }
                     else
                     {
                        // Check for a TimeStamp
                        if (timeStampIndexes.contains(Integer.valueOf(i)) && sqlDataExportOptions.getTimeStamp())
                        {
                           if (arrayIndexes.contains(Integer.valueOf(i)))
                              dumpData = dumpData + "'{NOW()}', ";
                           else
                           {
                              if (dataSourceType.equals(ConnectionManager.ORACLE))
                                 dumpData = dumpData + "SYSTIMESTAMP, ";
                              else if (dataSourceType.equals(ConnectionManager.DERBY))
                                 dumpData = dumpData + "CURRENT_TIMESTAMP, ";
                              else if (dataSourceType.equals(ConnectionManager.SQLITE))
                                 dumpData = dumpData + "STRFTIME('%Y-%m-%d %H:%M:%S.%f', 'now', 'localtime'), ";
                              else
                                 dumpData = dumpData + "NOW(), ";
                           }
                        }
                        
                        // SQLite Timestamp
                        else if (timeStampIndexes.contains(Integer.valueOf(i))
                                 && dataSourceType.equals(ConnectionManager.SQLITE))
                        {
                           java.sql.Timestamp timestampValue = rs.getTimestamp(i);
                           
                           if (timestampValue != null)
                              dumpData = dumpData + ("'" + timestampValue + "', ");
                           else
                              dumpData = dumpData + "NULL, ";
                        }

                        // Check for Oracle TimeStamp(TZ)
                        else if (oracleTimeStamp_TZIndexes.contains(Integer.valueOf(i))
                                 && !sqlDataExportOptions.getTimeStamp())
                        {
                           Object currentData = rs.getTimestamp(i);
                           
                           if (currentData != null)
                              dumpData = dumpData + "TO_TIMESTAMP('" + currentData
                                         + "', 'YYYY-MM-DD HH24:MI:SS:FF'), ";
                           else
                              dumpData = dumpData + "NULL, ";
                        }

                        // Check for a Date
                        else if (dateIndexes.contains(Integer.valueOf(i)))
                        {
                           if (dataSourceType.equals(ConnectionManager.ORACLE))
                           {
                              java.sql.Date dateValue = rs.getDate(i);
                              
                              if (dateValue != null)
                                 dumpData = dumpData + "TO_DATE('" + dateValue + "', 'YYYY-MM-DD'), ";
                              else
                                 dumpData = dumpData + "NULL, ";   
                           }
                           else
                           {
                              String dateString = rs.getString(i);
                              
                              if (dateString != null)
                                 dumpData = dumpData + "'" + addEscapes(dateString) + "', ";
                              else
                                 dumpData = dumpData + "NULL, ";    
                           }
                        }

                        // Check for a Year
                        else if (yearIndexes.contains(Integer.valueOf(i)))
                        {
                           // Fix for a bug in connectorJ, I think, that returns
                           // a whole date YYYY-MM-DD. Don't know what else
                           // to do it hangs my imports, but works with
                           // mysql console.
                           
                           String yearValue = rs.getString(i);
                           
                           if (yearValue != null)
                           {
                              if (yearValue.length() > 4)
                                 dumpData = dumpData + "'" + addEscapes(yearValue.substring(0, 4)) + "', ";
                              else
                                 dumpData = dumpData + "'" + addEscapes(yearValue) + "', ";
                           }
                           else
                              dumpData = dumpData + "NULL, ";
                        }

                        // Check for Bit fields.
                        else if (bitFieldIndexes.contains(Integer.valueOf(i)))
                        {
                           String bitValue = rs.getString(i);
                           
                           if (bitValue != null)
                           {
                              if (dataSourceType.equals(ConnectionManager.POSTGRESQL)
                                  || dataSourceType.equals(ConnectionManager.HSQL2))
                              {
                                 if (arrayIndexes.contains(Integer.valueOf(i)))
                                    dumpData = dumpData + "'" + bitValue + "', ";
                                 else
                                    dumpData = dumpData + "B'" + bitValue + "', ";
                              }
                              else if (dataSourceType.equals(ConnectionManager.MYSQL)
                                       || dataSourceType.equals(ConnectionManager.MARIADB))
                              {
                                 String byteString = Byte.toString(rs.getByte(i));
                                 dumpData = dumpData
                                            + "B'" + Integer.toBinaryString(Integer.parseInt(byteString))
                                            + "', ";
                              }
                              else if (dataSourceType.equals(ConnectionManager.DERBY))
                              {
                                 dumpData = dumpData + "X'";
                                 dumpBinaryData(rs.getBytes(i), true);
                              }
                              else
                              {
                                 try
                                 {
                                    dumpData = dumpData + "B'"
                                               + Integer.toBinaryString(Integer.parseInt(bitValue))
                                               + "', ";
                                 }
                                 catch (NumberFormatException e)
                                 {
                                    dumpData = dumpData + "B'0', ";
                                 }
                              }
                           }
                           else
                              dumpData = dumpData + "NULL, ";
                        }

                        // All other fields
                        else
                        {
                           // Do not remove. Oracle LONG Types, which are
                           // processed here, only alows the resultSet get once.

                           String contentString = rs.getString(i);

                           if (contentString != null)
                           {
                              // Check for Oracle TimeStampLTZ
                              if (oracleTimeStamp_LTZIndexes.contains(Integer.valueOf(i)) &&
                                  !sqlDataExportOptions.getTimeStamp())
                                 dumpData = dumpData + "TO_TIMESTAMP_TZ('" + contentString
                                            + "', 'YYYY-MM-DD HH24:MI:SS TZH:TZM'), ";
                              // Don't Quote Numeric Values.
                              else if (numericIndexes.contains(Integer.valueOf(i))) 
                                 dumpData = dumpData + contentString + ", ";
                              else 
                                 dumpData = dumpData + "'" + addEscapes(contentString) + "', ";
                           }
                           else
                              dumpData = dumpData + "NULL, ";
                        }
                     }
                  }
               }
               dumpData = ((String) dumpData).substring(0, ((String) dumpData).length() - 2);

               // SQL Singular Statement
               if (expressionType.equals("Singular"))
                  dumpData = dumpData + ");\n";
               // SQL Plural Statement
               else
               {
                  if (currentPluralValueCount >= pluralValueLimit)
                     dumpData = dumpData + ");\n";
                  else
                     dumpData = dumpData + "),";
               }

               if (currentRow >= rowsCount)
               {
                  // SQL Singular Statement
                  if (expressionType.equals("Singular"))
                     dumpData = ((String) dumpData).substring(0, ((String) dumpData).length() - 2);
                  // SQL Plural Statement
                  else
                  {
                     if (currentPluralValueCount >= pluralValueLimit)
                        dumpData = ((String) dumpData).substring(0, ((String) dumpData).length() - 2);
                     else
                        dumpData = ((String) dumpData).substring(0, ((String) dumpData).length() - 1);
                  }
                  dumpChunkOfData(dumpData);
                  dumpData = "";
                  currentRow = 0;
               }
               else
               {
                  dumpChunkOfData(dumpData);
                  // SQL Singular Statement Resetup
                  if (expressionType.equals("Singular"))
                     dumpData = sqlFieldValuesString;
                  // SQL Plural Statement
                  else
                  {
                     if (currentPluralValueCount >= pluralValueLimit)
                     {
                        dumpData = sqlFieldValuesString;
                        currentPluralValueCount = 0;
                     }
                     else
                        dumpData = "";
                  }
               }
            }
            currentTableIncrement += limitIncrement;
         }
         while (!limits && currentTableIncrement < rowsCount && !dumpProgressBar.isCanceled());

         // Closing out
         rs.close();
         dumpProgressBar.dispose();
      }
      catch (SQLException e)
      {
         dumpProgressBar.setCanceled(true);
         ConnectionManager.displaySQLErrors(e, "SQLDataDumpThread insertReplaceStatementData()");
      }
      finally
      {
         if (sqlStatement != null)
            sqlStatement.close();
      }
   }

   //==============================================================
   // Class method to create the explicit or update statement and
   // data.
   //==============================================================

   private void explicitStatementData(Connection dbConnection) throws SQLException
   {
      // Class Method Instances
      StringBuffer columnNamesString;
      StringBuffer oracleColumnNamesString;
      Iterator<String> columnNamesIterator;
      String field, columnClass, columnType;
      String firstField;
      
      ArrayList<String> keys;
      StringBuffer keyStringStatement;
      int rowsCount, currentTableIncrement, currentRow;

      String sqlStatementString;
      Statement sqlStatement;
      ResultSet rs;

      // Setting up for possible update dump.

      keys = new ArrayList <String>();
      updateDump = false;
      keyStringStatement = new StringBuffer();
      keyStringStatement.append(" WHERE ");

      // Setting up the initial dump data string with insert/replace/update,
      // type, and table.
      
      dumpData = dumpData
                 + sqlDataExportOptions.getInsertReplaceUpdate().toUpperCase(Locale.ENGLISH);
      dumpData = dumpData
                 + sqlDataExportOptions.getType().toUpperCase(Locale.ENGLISH);

      // Explicit
      if (sqlDataExportOptions.getInsertReplaceUpdate().toUpperCase(Locale.ENGLISH).equals("INSERT")
          || sqlDataExportOptions.getInsertReplaceUpdate().toUpperCase(Locale.ENGLISH).equals("REPLACE"))
         dumpData = dumpData + "INTO ";
      // Update
      else
      {
         updateDump = true;
         keys = DBTablesPanel.getSelectedTableTabPanel().getPrimaryKeys();
      }

      dumpData = dumpData + schemaTableName + " SET ";

      // Obtain the table fields and create select statement
      // to obtain the data.

      columnNamesString = new StringBuffer();
      oracleColumnNamesString = new StringBuffer();
      columnNamesIterator = columnNameFields.iterator();

      while (columnNamesIterator.hasNext())
      {
         field = columnNamesIterator.next();

         if (dataSourceType.equals(ConnectionManager.ORACLE)
             && (tableColumnTypeHashMap.get(field)).equals("TIMESTAMPLTZ"))
         {
            oracleColumnNamesString.append("TO_CHAR(" + dbIdentifierQuoteString + tableColumnNames.get(field)
                                           + dbIdentifierQuoteString + ", 'YYYY-MM-DD HH24:MM:SS TZR') AS "
                                           + dbIdentifierQuoteString + tableColumnNames.get(field)
                                           + dbIdentifierQuoteString + ", ");
         }
         else
            oracleColumnNamesString.append(dbIdentifierQuoteString + tableColumnNames.get(field)
                                           + dbIdentifierQuoteString + ", ");
         // Unmodified Names.
         columnNamesString.append(dbIdentifierQuoteString + tableColumnNames.get(field)
                                  + dbIdentifierQuoteString + ", ");
      }
      oracleColumnNamesString.delete((oracleColumnNamesString.length() - 2),
                                      oracleColumnNamesString.length());
      columnNamesString.delete((columnNamesString.length() - 2), columnNamesString.length());
      firstField = columnNamesString.substring(0, columnNamesString.indexOf(","));
      
      // Do an initial dump of data created so far.
      dumpChunkOfData(dumpData);
      dumpData = "";
      
      // Collect the row count of the table and setting
      // up a progress bar for tracking/canceling.
      
      rowsCount = 0;
      
      if (limits)
         rowsCount = DBTablesPanel.getSelectedTableTabPanel().getValidDataRowCount();
      else
      {
         try
         {
            rowsCount = getRowsCount(dbConnection, dbSchemaTableName);
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle, "SQLDataDumpThread explicitStatementData()");
         }
      }
      
      currentTableIncrement = 0;
      currentRow = 0;

      // Start a progress bar for tracking/canceling.
      dumpProgressBar.setTaskLength(rowsCount);
      dumpProgressBar.pack();
      dumpProgressBar.center();
      dumpProgressBar.setVisible(true);
      
      // Ok now ready so beginning by connecting to database for
      // data and proceeding with building the dump data.
      
      sqlStatement = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();

         // Setting up to begin update statements.
         do
         {
            if (limits)
               sqlStatementString = DBTablesPanel.getSelectedTableTabPanel().getTableSQLStatement().toString();
            else
            {
               // Oracle
               if (dataSourceType.equals(ConnectionManager.ORACLE))
                  sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                       + "(SELECT ROW_NUMBER() OVER (ORDER BY " + firstField + " ASC) " 
                                       + "AS dmprownumber, " + oracleColumnNamesString.toString() + " "
                                       + "FROM " + dbSchemaTableName + ") " + "WHERE dmprownumber BETWEEN "
                                       + (currentTableIncrement + 1) + " AND " + (currentTableIncrement
                                       + limitIncrement);
               // MSAccess
               else if (dataSourceType.equals(ConnectionManager.MSACCESS))
                  sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                        + dbSchemaTableName;
               // MSSQL
               else if (dataSourceType.equals(ConnectionManager.MSSQL))
               {
                  sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                       + "(SELECT *, ROW_NUMBER() OVER (ORDER BY " + firstField + " ASC) "
                                       + "AS dmprownumber FROM " + schemaTableName + " AS t) AS t1 "
                                       + "WHERE t1.dmprownumber BETWEEN "
                                       + (currentTableIncrement + 1) + " AND " + (currentTableIncrement
                                             + limitIncrement);
               }
               // Derby
               else if (dataSourceType.equals(ConnectionManager.DERBY))
                  sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                       + schemaTableName + " OFFSET " + currentTableIncrement + " ROWS "
                                       + "FETCH NEXT " + limitIncrement + " ROWS ONLY"; 
               else
                  sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                       + dbSchemaTableName + " LIMIT " + limitIncrement + " OFFSET "
                                       + currentTableIncrement;
            }
            // System.out.println(sqlStatementString);
            
            rs = sqlStatement.executeQuery(sqlStatementString);
            
            // Begin the creation of statements.
            while (rs.next() && !dumpProgressBar.isCanceled())
            {
               dumpProgressBar.setCurrentValue(currentRow++);
               columnNamesIterator = columnNameFields.iterator();

               // Cycle through each field and set value.
               while (columnNamesIterator.hasNext())
               {
                  field = (String) columnNamesIterator.next();
                  columnClass = tableColumnClassHashMap.get(field);
                  columnType = tableColumnTypeHashMap.get(field);
                  // System.out.println("field:" + field + " class:" + columnClass
                  //                   + " type:" + columnType);

                  // Setting up WHERE Statement for Update Dump.
                  if (keys.contains(tableColumnNames.get(field)) && updateDump)
                  {
                     keyStringStatement.append(identifierQuoteString + tableColumnNames.get(field) 
                                               + identifierQuoteString + "=");
                     
                     String keyValue = rs.getString(tableColumnNames.get(field));

                     if (keyValue != null)
                     {
                        // Character data gets single quotes for some databases,
                        // not numbers though.
                        
                        if (dataSourceType.equals(ConnectionManager.MSACCESS)
                            || dataSourceType.equals(ConnectionManager.DERBY))
                        {
                           if (columnType.indexOf("CHAR") != -1 || columnType.indexOf("TEXT") != -1)
                              keyStringStatement.append("'" + keyValue + "' AND ");
                           else
                              keyStringStatement.append(keyValue + " AND ");   
                        }
                        else
                           keyStringStatement.append("'" + keyValue + "' AND ");    
                     }
                     else
                        keyStringStatement.append("NULL AND ");
                  }
                  else
                  {
                     dumpData = dumpData + identifierQuoteString + (tableColumnNames.get(field))
                                + identifierQuoteString + "=";

                     // Blob/Bytea/Binary data adding
                     if ((columnClass.indexOf("String") == -1 && columnType.indexOf("BLOB") != -1) ||
                         (columnClass.indexOf("BLOB") != -1 && columnType.indexOf("BLOB") != -1) ||
                         (columnType.indexOf("BYTEA") != -1) || (columnType.indexOf("BINARY") != -1) ||
                         (columnType.indexOf("IMAGE") != -1) || (columnType.indexOf("RAW") != -1))
                     {
                        byte[] theBytes = rs.getBytes(tableColumnNames.get(field));

                        if (theBytes != null)
                        {
                           // Let Oracle & SQLite LOBs fall through if not update
                           // since an explicit statement is not supported. Allows
                           // to convert these to MySQL compatible dump.

                           if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
                              dumpData = dumpData + "E'";
                           else if (dataSourceType.equals(ConnectionManager.HSQL))
                              dumpData = dumpData + "'";
                           else if (dataSourceType.equals(ConnectionManager.HSQL2))
                              dumpData = dumpData + "x'";
                           else if (dataSourceType.equals(ConnectionManager.ORACLE) && updateDump)
                              dumpData = dumpData + "HEXTORAW('";
                           else if (dataSourceType.equals(ConnectionManager.SQLITE) && updateDump)
                              dumpData = dumpData + "x'";
                           else if (dataSourceType.equals(ConnectionManager.DERBY) && updateDump)
                              dumpData = dumpData + "CAST(X'";
                           else if (dataSourceType.equals(ConnectionManager.H2))
                              dumpData = dumpData + "x'";
                           else
                           {
                              if (theBytes.length != 0)
                                 dumpData = dumpData + "0x";
                              else
                                 dumpData = dumpData + "''";
                           }

                           // Go convert to hexadecimal/octal values
                           // and dump data as we go for blob/bytea.
                           dumpBinaryData(theBytes, false);
                        }
                        else
                           dumpData = dumpData + "NULL, ";
                     }
                     // Normal field
                     else
                     {
                        // Setting Auto-Increment Fields
                        if (DBTablesPanel.getSelectedTableTabPanel().getAutoIncrementHashMap()
                              .containsKey(field)
                            && sqlDataExportOptions.getAutoIncrement())
                        {
                           if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
                           {
                              schemaName = schemaTableName.substring(0, schemaTableName.indexOf(".") + 2);
                              tableName = (schemaTableName.substring(schemaTableName.indexOf(".") + 1)).replaceAll(
                                                                     identifierQuoteString, "");

                              dumpData = dumpData + "nextval('" + schemaName + tableName + "_" + field
                                         + "_seq\"'), ";
                           }
                           else if (dataSourceType.equals(ConnectionManager.ORACLE))
                           {
                              dumpData = dumpData
                                         + identifierQuoteString
                                         + DBTablesPanel.getSelectedTableTabPanel().getAutoIncrementHashMap().get(field)
                                         + identifierQuoteString + ".NEXTVAL, ";
                           }
                           else if (dataSourceType.equals(ConnectionManager.DERBY))
                              dumpData = "DEFAULT, ";
                           else
                              dumpData = dumpData + "NULL, ";
                        }

                        // Setting TimeStamp Fields
                        else if (columnType.indexOf("TIMESTAMP") != -1 && sqlDataExportOptions.getTimeStamp())
                        {
                           if (columnType.indexOf("_") != -1)
                              dumpData = dumpData + "'{NOW()}'. ";
                           else
                           {
                              if (dataSourceType.equals(ConnectionManager.ORACLE))
                                 dumpData = dumpData + "SYSTIMESTAMP, ";
                              else if (dataSourceType.equals(ConnectionManager.DERBY))
                                 dumpData = dumpData + "CURRENT_TIMESTAMP, ";
                              else if (dataSourceType.equals(ConnectionManager.SQLITE))
                                 dumpData = dumpData + "STRFTIME(" +
                                 		"'%Y-%m-%d %H:%M:%S.%f', 'now', 'localtime'), ";
                              else
                                 dumpData = dumpData + "NOW(), ";
                           }
                        }
                        
                        // Setting SQLite Timestamp
                        else if (columnType.equals("TIMESTAMP")
                                 && dataSourceType.equals(ConnectionManager.SQLITE))
                        {
                           java.sql.Timestamp timestampValue = rs.getTimestamp(tableColumnNames.get(field));
                           
                           if (timestampValue != null)
                              dumpData = dumpData + ("'" + timestampValue + "', ");
                           else
                              dumpData = dumpData + "NULL, ";
                        }

                        // Setting Oracle TimeStamp(TZ)
                        else if ((columnType.equals("TIMESTAMP") || columnType.equals("TIMESTAMPTZ")
                                  || columnType.equals("TIMESTAMP WITH TIME ZONE")
                                  || columnType.equals("TIMESTAMP WITH LOCAL TIME ZONE")) &&
                                 dataSourceType.equals(ConnectionManager.ORACLE) &&
                                 !sqlDataExportOptions.getTimeStamp())
                        {
                           Object currentData = rs.getTimestamp(tableColumnNames.get(field));
                           
                           if (currentData != null)
                              dumpData = dumpData + "TO_TIMESTAMP('" + currentData
                                         + "', 'YYYY-MM-DD HH24:MI:SS:FF'), ";
                           else
                              dumpData = dumpData + "NULL, ";
                        }

                        // Setting Date Fields
                        else if (columnType.equals("DATE"))
                        {
                           if (dataSourceType.equals(ConnectionManager.ORACLE))
                           {
                              java.sql.Date dateValue = rs.getDate(tableColumnNames.get(field));
                              
                              if (dateValue != null)
                                 dumpData = dumpData + "TO_DATE('" + dateValue + "', 'YYYY-MM-DD'), ";
                              else
                                 dumpData = dumpData + "NULL, ";   
                           }
                           else
                           {
                              String dateString = rs.getString(tableColumnNames.get(field));
                              
                              if (dateString != null)
                                 dumpData = dumpData + "'" + addEscapes(dateString) + "', ";
                              else
                                 dumpData = dumpData + "NULL, ";    
                           }
                        }

                        // Fix for a bug in connectorJ, I think, that returns
                        // a whole date YYYY-MM-DD. Don't know what else
                        // to do it hangs my imports, but works with
                        // mysql console.
                        else if (columnType.equals("YEAR"))
                        {
                           String yearValue = rs.getString(tableColumnNames.get(field));
                           
                           if (yearValue != null)
                           {
                              if (yearValue.length() > 4)
                                 dumpData = dumpData + "'" + addEscapes(yearValue.substring(0, 4)) + "', ";
                              else
                                 dumpData = dumpData + "'" + addEscapes(yearValue) + "', ";
                           }
                           else
                              dumpData = dumpData + "NULL, ";
                        }

                        // Setting Bit Fields
                        else if (columnType.indexOf("BIT") != -1)
                        {
                           String bitValue = rs.getString(tableColumnNames.get(field));
                           
                           if (bitValue != null)
                           {
                              if (dataSourceType.equals(ConnectionManager.POSTGRESQL)
                                  || dataSourceType.equals(ConnectionManager.HSQL2))
                              {
                                 if (columnType.indexOf("_") != -1)
                                    dumpData = dumpData + "'" + bitValue + "', ";
                                 else
                                    dumpData = dumpData + "B'" + bitValue + "', ";
                              }
                              else if (dataSourceType.equals(ConnectionManager.MYSQL)
                                    || dataSourceType.equals(ConnectionManager.MARIADB))
                              {
                                 String byteString = Byte.toString(rs.getByte(tableColumnNames.get(field)));
                                 dumpData = dumpData
                                            + "B'" + Integer.toBinaryString(Integer.parseInt(byteString))
                                            + "', ";
                              }
                              else if (dataSourceType.equals(ConnectionManager.DERBY))
                              {
                                 dumpData = dumpData + "X'";
                                 dumpBinaryData(rs.getBytes(tableColumnNames.get(field)), true);
                              }
                              else if (dataSourceType.equals(ConnectionManager.MSACCESS)
                                       || dataSourceType.equals(ConnectionManager.MSSQL))
                              {
                                 dumpData = dumpData + "'" + bitValue + "', ";
                              }
                              else
                              {
                                 try
                                 {
                                    dumpData = dumpData + "B'"
                                               + Integer.toBinaryString(Integer.parseInt(bitValue)) + "', ";
                                 }
                                 catch (NumberFormatException e)
                                 {
                                    dumpData = dumpData + "B'0', ";
                                 }
                              }
                           }
                           else
                              dumpData = dumpData + "NULL, ";
                        }

                        // All other fields
                        else
                        {
                           // Do not remove. Oracle LONG Types, which are
                           // processed here, only alows the resultSet get once.
                           // Oh, Oracle doesn't support the explicit INSERT,
                           // but what the hell maybe someone will use to export
                           // from Oracle to import into a MySQL database.

                           String contentString = rs.getString(tableColumnNames.get(field));

                           if (contentString != null)
                           {
                              if (columnType.equals("TIMESTAMPLTZ") &&
                                  dataSourceType.equals(ConnectionManager.ORACLE))
                                 dumpData = dumpData + "TO_TIMESTAMP_TZ('" + contentString
                                            + "', 'YYYY-MM-DD HH24:MI:SS TZH:TZM'), ";
                              else if (columnClass.indexOf("Integer") != -1 || columnClass.indexOf("Long") != -1
                                       || columnClass.indexOf("Float") != -1 || columnClass.indexOf("Double") != -1
                                       || (columnClass.indexOf("Byte") != -1 && columnType.indexOf("CHAR") == -1)
                                       || columnClass.indexOf("BigDecimal") != -1 || columnClass.indexOf("Short") != -1)
                                 dumpData = dumpData + contentString + ", ";
                              else
                                 dumpData = dumpData + "'" + addEscapes(contentString + "") + "', ";
                           }
                           else
                              dumpData = dumpData + "NULL, ";
                        }
                     }
                  }
               }

               // Creating end of extended SQL statement and
               // setting up for the next as needed.

               if (currentRow < rowsCount)
               {
                  dumpData = ((String) dumpData).substring(0, ((String) dumpData).length() - 2);

                  if (updateDump && !keys.isEmpty())
                     dumpData = (String) dumpData
                                + keyStringStatement.delete((keyStringStatement.length() - 5),
                                                             keyStringStatement.length())
                                + ";\n";
                  else
                     dumpData = dumpData + (";\n");

                  dumpChunkOfData(dumpData);
                  dumpData = "";

                  keyStringStatement.delete(0, keyStringStatement.length());
                  keyStringStatement.append(" WHERE ");

                  dumpData = dumpData
                             + sqlDataExportOptions.getInsertReplaceUpdate().toUpperCase(Locale.ENGLISH);
                  dumpData = dumpData + sqlDataExportOptions.getType().toUpperCase(Locale.ENGLISH);

                  if (sqlDataExportOptions.getInsertReplaceUpdate().toUpperCase(
                         Locale.ENGLISH).equals("INSERT")
                      || sqlDataExportOptions.getInsertReplaceUpdate().toUpperCase(
                         Locale.ENGLISH).equals("REPLACE"))
                     dumpData = dumpData + "INTO ";

                  dumpData = dumpData + schemaTableName + " SET ";
               }
               else
               {
                  if (updateDump)
                     dumpData = ((String) dumpData).substring(0, ((String) dumpData).length() - 2)
                                + keyStringStatement.delete((keyStringStatement.length() - 5),
                                                             keyStringStatement.length());
                  else
                     dumpData = ((String) dumpData).substring(0, ((String) dumpData).length() - 2);
               }
            }
            currentTableIncrement += limitIncrement;  
         }
         while (!limits && currentTableIncrement < rowsCount && !dumpProgressBar.isCanceled());
         
         // Closing out
         rs.close();
         sqlStatement.close();
         dumpProgressBar.dispose();
      }
      catch (SQLException e)
      {
         dumpProgressBar.setCanceled(true);
         ConnectionManager.displaySQLErrors(e, "SQLDataDumpThread explicitStatementData()");
      }
      finally
      {
         if (sqlStatement != null)
            sqlStatement.close();
      }
   }

   //==============================================================
   // Class method to get the table data row count
   //==============================================================

   private int getRowsCount(Connection dbConnection, String tableName) throws SQLException
   {
      // Method Instances
      String sqlStatementString;
      Statement sqlStatement;
      ResultSet rs;
      
      // Setup
      int rowCount = 0;
      sqlStatement = null;
      rs = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();
         sqlStatementString = "SELECT COUNT(*) FROM " + tableName;
         // System.out.println(sqlStatementString);

         rs = sqlStatement.executeQuery(sqlStatementString);
         rs.next();
         rowCount = rs.getInt(1);

         return rowCount;
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "SQLDataDumpThread getRowsCount()");
         return rowCount;  
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
            ConnectionManager.displaySQLErrors(sqle, "SQLDataDumpThread getRowsCount()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }

   //==============================================================
   // Class method to get the table data row count
   //==============================================================

   private void dumpBinaryData(byte[] theBytes, boolean derbyBit)
   {
      // Class Method Instances
      int b;
      String hexadecimalString, octalString;
      BufferedInputStream in;

      // Otain byes in a stream and convert to
      // hex for dumping.
      if (theBytes != null)
      {
         in = new BufferedInputStream(new ByteArrayInputStream(theBytes));

         try
         {
            while ((b = in.read()) != -1)
            {
               // Dump as octal data.
               if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
               {
                  octalString = Integer.toString(b, 8);
                  if (octalString.length() == 1)
                     octalString = "00" + octalString;
                  if (octalString.length() > 1 && octalString.length() < 3)
                     octalString = "0" + octalString;
                  dumpData = dumpData + "\\\\" + octalString;
                  dumpChunkOfData(dumpData);
                  dumpData = "";
               }
               // Dump as hexadecimal data.
               else
               {
                  hexadecimalString = Integer.toString(b, 16);
                  if (hexadecimalString.length() < 2)
                     hexadecimalString = "0" + hexadecimalString;
                  if (hexadecimalString.length() > 2)
                     hexadecimalString = hexadecimalString.substring(hexadecimalString.length() - 2);
                  dumpData = dumpData + hexadecimalString;
                  dumpChunkOfData(dumpData);
                  dumpData = "";
               }
            }
            if (dataSourceType.equals(ConnectionManager.POSTGRESQL) ||
                dataSourceType.indexOf(ConnectionManager.HSQL) != -1 ||
                dataSourceType.equals(ConnectionManager.SQLITE) ||
                dataSourceType.equals(ConnectionManager.H2))
               dumpData = dumpData + "', ";
            else if (dataSourceType.equals(ConnectionManager.DERBY))
            {
               if (!derbyBit)
                  dumpData = dumpData + "' AS BLOB), ";
               else
                  dumpData = dumpData + "', ";
            }
            else if (dataSourceType.equals(ConnectionManager.ORACLE) &&
                     (updateDump || insertReplaceDump))
               dumpData = dumpData + "'), ";
            else
               dumpData = dumpData + ", ";
         }
         catch (IOException e)
         {
            String msg = "Unable to Create Buffered InputStream for Blob Data";
            JOptionPane.showMessageDialog(null, msg, fileName, JOptionPane.ERROR_MESSAGE);
            return;
         }
      }
      else
         dumpData = dumpData + "NULL, ";
   }
}