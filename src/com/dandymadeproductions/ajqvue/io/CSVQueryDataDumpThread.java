//=================================================================
//                       CSVQueryDataDumpThread
//=================================================================
//    This class provides a thread to safely dump a given SQL query
// data to a local file in the CSV format. A status dialog with
// cancel is provided to allow the ability to prematurely terminate
// the dump.
//
//                 << CSVQueryDataDumpThread.java >>
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
// Version 1.0 Production CSVQueryDataDumpThread Class.
//             
//-----------------------------------------------------------------
//                   danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.utilities.ProgressBar;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The CSVQueryDumpThread class provides a thread to safely dump a
 * given SQL query data to a local file in the CSV format. A status
 * dialog with cancel is provided to allow the ability to prematurely
 * terminate the dump.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class CSVQueryDataDumpThread implements Runnable
{
   // Class Instances
   private Connection db_Connection;
   
   private ArrayList<String> columnNameFields;
   private HashMap<String, String> columnClassHashMap;
   private HashMap<String, String> columnTypeHashMap;
   private HashMap<String, Integer> columnSizeHashMap;
   
   private String queryString, fileName;
   private String dataSourceType;
   private boolean useStatusDialog, useLimitIncrement, argConnection;
   
   private BufferedOutputStream filebuff;
   
   //==============================================================
   // CSVQueryDataDumpThread Constructor.
   //
   // db_Connection - Optional.
   // queryString - SQL Query.
   // fileName - Where to Dump the Data.
   // useStatusDialog - Show Progress Bar.
   // useLimitIncrement - Bypass the Limit Increment, Use queryString
   //                     Directly.
   //==============================================================

   public CSVQueryDataDumpThread(String queryString, String fileName)
   {
      this(null, queryString, fileName, false, true);
   }
   
   public CSVQueryDataDumpThread(Connection db_Connection, String queryString, String fileName,
                                 boolean useStatusDialog, boolean useLimitIncrement)
   {
      this.db_Connection = db_Connection;
      this.queryString = queryString;
      this.fileName = fileName;
      this.useStatusDialog = useStatusDialog;
      this.useLimitIncrement = useLimitIncrement;
      
      columnNameFields = new ArrayList <String>();
      columnClassHashMap = new HashMap <String, String>();
      columnTypeHashMap = new HashMap <String, String>();
      columnSizeHashMap = new HashMap <String, Integer>();
      
      dataSourceType = ConnectionManager.getDataSourceType();
      argConnection = true;
   }

   //==============================================================
   // Class method for normal start of the thread
   //==============================================================
   
   public void run()
   {
      // Class Method Instances
      Object dumpData;
      FileOutputStream fileStream;
      ProgressBar dumpProgressBar;
      Iterator<String> columnNamesIterator;
      StringBuffer columnNamesString;
      String columnClass, columnType, dataDelimiter;
      String identifierQuoteString;
      String fieldContent;
      int columnSize, rowsCount, currentTableIncrement, currentRow;
      int limitIncrement;

      String sqlStatementString;
      Statement sqlStatement;
      ResultSet dbResultSet;
      
      // Setting up
      rowsCount = 0;
      dataDelimiter = DBTablesPanel.getDataExportProperties().getDataDelimiter();
      limitIncrement = DBTablesPanel.getGeneralDBProperties().getLimitIncrement();
      identifierQuoteString = ConnectionManager.getIdentifierQuoteString();
      dumpProgressBar = new ProgressBar("SQL Query Dump");

      // Get Connection to Database.
      if (db_Connection == null)
      {
         db_Connection = ConnectionManager.getConnection("CSVQueryDataDumpThread run()");
         argConnection = false;
      }
      
      if (db_Connection == null || queryString.isEmpty())
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
               if(!makeNewFile.delete())
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
         
         // Collect the column names.
         try
         {
            getColumnNames(db_Connection, queryString);
         }
         catch (SQLException sqle)
         {
            return;
         }
         
         // Compose a the column names into a quoted format.
         columnNamesString = new StringBuffer();
         columnNamesIterator = columnNameFields.iterator();
         
         while (columnNamesIterator.hasNext())
         {
            columnNamesString.append(identifierQuoteString + columnNamesIterator.next()
                                     + identifierQuoteString + ", ");
         }
         columnNamesString.delete((columnNamesString.length() - 2), columnNamesString.length());
         
         // Have a connection, columns, query, & file to write to
         // so begin dumping data.
         
         sqlStatement = null;
         dbResultSet = null;
         
         try
         {
            sqlStatement = db_Connection.createStatement();
            
            // Collect the row count of the table and setting
            // up a progress bar for tracking/canceling.
            
            if (dataSourceType.equals(ConnectionManager.ORACLE))
               sqlStatementString = "SELECT COUNT(*) FROM (" + queryString + ")";
            else
               sqlStatementString = "SELECT COUNT(*) AS " + identifierQuoteString + "rowCount"
                                    + identifierQuoteString + " FROM (" + queryString + ") AS "
                                    + identifierQuoteString + "myRowTable" + identifierQuoteString;
            
            // System.out.println(sqlStatementString);

            dbResultSet = sqlStatement.executeQuery(sqlStatementString);
         
            if (dbResultSet.next())
               rowsCount = dbResultSet.getInt(1);
            
            dumpProgressBar.setTaskLength(rowsCount);
            dumpProgressBar.pack();
            dumpProgressBar.center();
            dumpProgressBar.setVisible(useStatusDialog);
            
            // Begin Dumping Data.
            
            dumpData = "";
         
            // Constructing the column names line & dumping.
            columnNamesIterator = columnNameFields.iterator();

            while (columnNamesIterator.hasNext())
               dumpData = (String) dumpData + columnNamesIterator.next()
                          + dataDelimiter;
            dumpData = ((String) dumpData).substring(0,
                              ((String) dumpData).length() - dataDelimiter.length()) + "\n";
            
            dumpChunkOfData(dumpData);
            dumpData = "";
            
            // Setting up to begin actual field value dump.
            currentTableIncrement = 0;
            currentRow = 0;
         
            do
            {
               // Creating the Select statement to retrieve data.
               
               if (useLimitIncrement)
               {
                  // Oracle
                  if (dataSourceType.equals(ConnectionManager.ORACLE))
                  {
                     sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                          + "(SELECT ROW_NUMBER() OVER (ORDER BY " + identifierQuoteString
                                          + columnNameFields.get(0) + identifierQuoteString
                                          + " ASC) " + "AS dmprownumber, " + columnNamesString.toString()
                                          + " FROM (" + queryString + ")) " + "WHERE "
                                          + "dmprownumber BETWEEN " + (currentTableIncrement + 1) + " AND "
                                          + (currentTableIncrement + limitIncrement);
                  }
                  // MSAccess
                  else if (dataSourceType.equals(ConnectionManager.MSACCESS))
                  {
                     sqlStatementString = queryString;
                  }
                  // MSSQL
                  else if (dataSourceType.equals(ConnectionManager.MSSQL))
                  {
                     sqlStatementString = "SELECT " + columnNamesString.toString() + " FROM "
                                          + "(SELECT *, ROW_NUMBER() OVER (ORDER BY " + identifierQuoteString
                                          + columnNameFields.get(0) + identifierQuoteString + " ASC) "
                                          + "AS dmprownumber FROM (" +  queryString + ")"+ " AS t) AS t1 "
                                          + "WHERE t1.dmprownumber BETWEEN "
                                          + (currentTableIncrement + 1) + " AND " + (currentTableIncrement
                                                + limitIncrement);
                  }
                  // Derby
                  else if (dataSourceType.equals(ConnectionManager.DERBY))
                  {
                     sqlStatementString = "SELECT * FROM (" + queryString + ") AS " + identifierQuoteString
                                          + "myCSVTable" + identifierQuoteString + " OFFSET "
                                          + currentTableIncrement + " ROWS "
                                          + "FETCH NEXT " + limitIncrement + " ROWS ONLY";
                  }
                  else
                     sqlStatementString = "SELECT * FROM (" + queryString + ") AS " + identifierQuoteString
                                          + "myCSVTable" + identifierQuoteString + " LIMIT " + limitIncrement
                                          + " OFFSET " + currentTableIncrement;
               }
               else
                  sqlStatementString = queryString;
               // System.out.println(sqlStatementString);

               dbResultSet = sqlStatement.executeQuery(sqlStatementString);
               
               // Actual data dump.
               while (dbResultSet.next() && !dumpProgressBar.isCanceled())
               {
                  int i = 1;
                  dumpProgressBar.setCurrentValue(currentRow++);

                  columnNamesIterator = columnNameFields.iterator();
                  while (columnNamesIterator.hasNext())
                  {
                     // Filtering out blob & text data as needed.
                     String currentHeading = columnNamesIterator.next();
                     columnClass = columnClassHashMap.get(currentHeading);
                     columnType = columnTypeHashMap.get(currentHeading);
                     columnSize = columnSizeHashMap.get(currentHeading).intValue();

                     // Blob/Bytea/Binary/Bit Data/Raw data.
                     
                     if ((columnClass.indexOf("String") == -1 && columnType.indexOf("BLOB") != -1) ||
                         (columnClass.toUpperCase(Locale.ENGLISH).indexOf("BLOB") != -1
                          && columnType.indexOf("BLOB") != -1) ||
                         (columnType.indexOf("BYTEA") != -1) || (columnType.indexOf("BINARY") != -1) ||
                         (columnType.indexOf("BIT DATA") != -1) || (columnType.indexOf("RAW") != -1) ||
                         (columnType.indexOf("IMAGE") != -1))
                     {
                        Object binaryContent = dbResultSet.getBytes(i);
                        
                        if (binaryContent != null)
                           dumpData = dumpData + "Binary" + dataDelimiter;
                        else
                           dumpData = dumpData + "NULL" + dataDelimiter;
                     }

                     // Text, MediumText, LongText, & CLOB.
                     else if ((columnClass.indexOf("String") != -1 && !columnType.equals("CHAR") &&
                               columnSize > 255) ||
                              (columnClass.indexOf("String") != -1 && columnType.equals("LONG")) ||
                              (columnType.indexOf("CLOB") != -1) || (columnType.indexOf("XML") != -1))
                     {
                        fieldContent = dbResultSet.getString(i);
                        
                        // Check to see if a portion of the TEXT data should be
                        // included as defined in the Preferences | Export Data |
                        // CVS.
                        
                        if (fieldContent != null)
                        {
                           if (DBTablesPanel.getDataExportProperties().getTextInclusion())
                           {
                              int textLength = DBTablesPanel.getDataExportProperties().getTextCharsNumber();

                              // Obtain text and cleanup some
                              fieldContent = fieldContent.replaceAll("\n", "");
                              fieldContent = fieldContent.replaceAll("\r", "");
                              
                              if (fieldContent.length() > textLength)
                                 dumpData = dumpData + fieldContent.substring(0, textLength) + dataDelimiter;
                              else
                                 dumpData = dumpData + fieldContent + dataDelimiter;
                           }
                           else
                              dumpData = dumpData + "Text" + dataDelimiter;
                        }
                        else
                           dumpData = dumpData + "NULL" + dataDelimiter;
                     }

                     // MySQL/MariaDB Bit Fields up BIT(8) Only.
                     else if ((dataSourceType.equals(ConnectionManager.MYSQL)
                               || dataSourceType.equals(ConnectionManager.MARIADB))
                              && columnType.indexOf("BIT") != -1)
                     {
                        String byteString = Byte.toString(dbResultSet.getByte(i));
                        
                        dumpData = dumpData
                                   + Integer.toBinaryString(Integer.parseInt(byteString))
                                   + dataDelimiter;
                     }

                     // Insure MySQL/MariaDB Date/Year fields are chopped to only 4 digits.
                     else if ((dataSourceType.equals(ConnectionManager.MYSQL)
                               || dataSourceType.equals(ConnectionManager.MARIADB))
                              && columnType.indexOf("YEAR") != -1)
                     {
                        Object yearContent = dbResultSet.getObject(i);
                        
                        if (yearContent != null)
                        {
                           String yearString = yearContent + "";

                           if (yearString.length() > 4)
                              yearString = yearString.substring(0, 4);

                           dumpData = dumpData + yearString + dataDelimiter;
                        }
                        else
                           dumpData = dumpData + "NULL" + dataDelimiter;
                     }
                     
                     // Format Date & Timestamp Fields as Needed.
                     else if (columnType.equals("DATE") || columnType.indexOf("DATETIME") != -1
                              || (columnType.indexOf("TIMESTAMP") != -1 && columnClass.indexOf("Array") == -1))
                     {
                        if (columnType.equals("DATE"))
                        {
                           Object date = dbResultSet.getDate(i);
                           if (date != null)
                              fieldContent = Utils.convertDBDateString_To_ViewDateString(
                                 date + "", DBTablesPanel.getDataExportProperties().getCSVDateFormat());
                           else
                              fieldContent = "NULL";
                        }
                        else
                        {  
                           if (columnType.equals("DATETIMEOFFSET"))
                           {
                              String dateTime = dbResultSet.getString(i);
                              String date;
                              
                              if (dateTime != null)
                              {
                                 if (dateTime.indexOf(" ") != -1)
                                 {
                                    date = Utils.convertDBDateString_To_ViewDateString(
                                       dateTime.substring(0, dateTime.indexOf(" ")),
                                       DBTablesPanel.getDataExportProperties().getCSVDateFormat());
                                    fieldContent = date + dateTime.substring(dateTime.indexOf(" "));
                                 }
                                 else
                                    fieldContent = "";
                              }
                              else
                                 fieldContent = "NULL";
                           }
                           else if (columnType.indexOf("DATETIME") != -1 || columnType.equals("TIMESTAMP"))
                           {
                              Object dateTime = dbResultSet.getTimestamp(i);
                              
                              if (dateTime != null)
                                 fieldContent = (new SimpleDateFormat(
                                    DBTablesPanel.getDataExportProperties().getCSVDateFormat()
                                    + " HH:mm:ss")).format(dateTime) + "";
                              else
                                 fieldContent = "NULL";
                           }
                           else if (columnType.equals("TIMESTAMPTZ")
                                    || columnType.equals("TIMESTAMP WITH TIME ZONE")
                                    || columnType.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                           {
                              Object dateTime = dbResultSet.getTimestamp(i);
                              
                              if (dateTime != null)
                                 fieldContent = (new SimpleDateFormat(
                                    DBTablesPanel.getDataExportProperties().getCSVDateFormat()
                                    + " HH:mm:ss Z")).format(dateTime) + "";
                              else
                                 fieldContent = "NULL";
                           }
                           // TIMESTAMPLTZ, Oracle
                           else
                           {
                              String timestamp = dbResultSet.getString(i);
                              
                              if (timestamp != null)
                              {
                                 if (timestamp.indexOf(" ") != -1)
                                    fieldContent = Utils.convertDBDateString_To_ViewDateString(
                                                        timestamp.substring(0, timestamp.indexOf(" ")),
                                                        DBTablesPanel.getDataExportProperties().getCSVDateFormat())
                                                        + timestamp.substring(timestamp.indexOf(" "));
                                 else
                                    fieldContent = timestamp;
                              }
                              else
                                 fieldContent = "NULL";
                           }
                        }
                        dumpData = dumpData + fieldContent + dataDelimiter;  
                     }
                     
                     // All other fields.
                     else
                     {
                        fieldContent = dbResultSet.getString(i);
                        
                        if (fieldContent != null)
                           dumpData = dumpData + fieldContent.trim() + dataDelimiter;
                        else
                           dumpData = dumpData + "NULL" + dataDelimiter;     
                     }
                     i++;
                  }
                  dumpData = ((String) dumpData).substring(0,
                                    ((String) dumpData).length() - dataDelimiter.length()) + "\n";
                  // System.out.print(currentRow + " " + dumpData);
                  
                  dumpChunkOfData(dumpData);
                  dumpData = "";
               }
               if (useLimitIncrement)
                  currentTableIncrement += limitIncrement;
               else
                  currentTableIncrement = rowsCount;
            }
            while (currentTableIncrement < rowsCount && !dumpProgressBar.isCanceled());
            
            dumpProgressBar.dispose();
         }
         catch (SQLException e)
         {
            dumpProgressBar.dispose();
            ConnectionManager.displaySQLErrors(e, "CSVQueryDataDumpThread run()");
         }
         finally
         {
            try
            {
               if (dbResultSet != null)
                  dbResultSet.close();
            }
            catch (SQLException sqle)
            {
               ConnectionManager.displaySQLErrors(sqle,
                  "CSVQueryDataDumpThread run() failed closing result set");
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
                  ConnectionManager.displaySQLErrors(sqle,
                     "CSVQueryDataDumpThread run() failed closing sql statement");
               }
            }
         }
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
               System.out.println("CSVQueryDataDumpThread run() Failed to Close BufferedOutputStream. "
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
                  System.out.println("CSVQueryDataDumpThread run() Failed to Close FileStream. "
                                     + ioe);
            }
         } 
      }
      
      if (!argConnection)
         ConnectionManager.closeConnection(db_Connection, "CSVQueryDataDumpThread run()");
   }
   
   //==============================================================
   // Class method to obtain the column names from the table.
   // Additional information about the column, size & type, are
   // also stored away for future use.
   //==============================================================

   private void getColumnNames(Connection dbConnection, String query) throws SQLException
   {
      // Method Instances
      Statement sqlStatement;
      ResultSet db_resultSet;
      ResultSetMetaData tableMetaData;

      String colNameString, columnClass, columnType;
      Integer columnSize;

      // Connecting to the data base, to obtain
      // meta data, and column names.
      sqlStatement = null;
      db_resultSet = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();
         sqlStatement.setMaxRows(1);
         // System.out.println(query);

         // ********************************************************

         db_resultSet = sqlStatement.executeQuery(query);
         tableMetaData = db_resultSet.getMetaData();

         // Column Names, class, type, and size collection.

         for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
         {
            colNameString = tableMetaData.getColumnLabel(i);

            // Additional Information about each column.
            columnClass = tableMetaData.getColumnClassName(i);
            columnType = tableMetaData.getColumnTypeName(i);
            columnSize = Integer.valueOf(tableMetaData.getColumnDisplaySize(i));

            // System.out.println(i + " " + colNameString + " " +
            //                    columnClass + " " + columnType + " " +
            //                    columnSize);

            // This going to be a problem so skip this column.
            // NOT TESTED. This is still problably not going to
            // help. Bound to crash in loadTable().

            if (columnClass == null && columnType == null)
               continue;

            // Handle some Oracle data types that have a null
            // class type and possibly others.

            if (columnClass == null)
            {
               if (columnType.equals("BINARY_FLOAT")
                   && dataSourceType.equals(ConnectionManager.ORACLE))
               {
                  columnClass = "java.lang.Float";
                  columnType = "FLOAT";
               }
               else if (columnType.equals("BINARY_DOUBLE")
                        && dataSourceType.equals(ConnectionManager.ORACLE))
               {
                  columnClass = "java.lang.Double";
                  columnType = "DOUBLE";
               }
               else
                  columnClass = columnType;
            }

            columnNameFields.add(colNameString);
            columnClassHashMap.put(colNameString, columnClass);
            columnTypeHashMap.put(colNameString, columnType.toUpperCase(Locale.ENGLISH));
            columnSizeHashMap.put(colNameString, columnSize);
         }
      }
      catch (SQLException e)
      {
         String errorString = "CSVQueryDataDump.getColumnNames() SQLException: " + e.getMessage()
                              + " " + "SQLState: " + e.getSQLState() + " " + "VendorError: "
                              + e.getErrorCode();
         JOptionPane.showMessageDialog(null, errorString, "Query Failure", JOptionPane.ERROR_MESSAGE);
         return;
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
            ConnectionManager.displaySQLErrors(sqle,
               "CSVQueryDataDumpThread getColumnNames() failed closing result set");
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
               ConnectionManager.displaySQLErrors(sqle,
                  "CSVQueryDataDumpThread getColumnNames() failed closing sql statement");
            }
         }
      }
   }
   
   //==============================================================
   // Class Method to dump a chunk of data to the output file.
   //==============================================================

   private void dumpChunkOfData(Object dumpData)
   {
      // Class Method Instances
      byte[] currentBytes;

      // Dump the Chunk.
      try
      {
         currentBytes = dumpData.toString().getBytes();
         filebuff.write(currentBytes);
         filebuff.flush();
      }
      catch (IOException e)
      {
         String msg = "Error outputing data to: '" + fileName + "'.";
         JOptionPane.showMessageDialog(null, msg, fileName, JOptionPane.ERROR_MESSAGE);
      }
   }
}
