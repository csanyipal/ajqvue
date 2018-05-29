//=================================================================
//                    SQLInsertPrepare
//=================================================================
//   This class provides a means to create the required INSERT for
// a PrepareStatement in use with a database to H2, HSQL, Derby,
// or SQLite Memory/File database transfer.
//
//                   << SQLInsertPrepare.java >>
//
//=================================================================
// Copyright (C) 2005-2018 Dana M. Proctor
// Version 2.3 05/29/2018
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
// Version 1.0 11/21/2015 Original SQLDataDumpThread Class.
//         1.1 12/25/2015
//         1.2 04/27/2017 Updated to be Used With Ajqvue.
//         1.3 12/10/2017 Changed Package to sqltofilememorydb.
//         1.4 03/30/2018 Updated Package to Reflect Project Name Change to DB_To_FileMemoryDB.
//         1.5 04/18/2018 Added Class Instance threadLogger Along With New Method setLogger().
//                        Replaced Pertinent System.outs to Use New Method log().
//         1.6 04/19/2018 Removed Class Instance ADD_ITEM. Method run() Cleaned Up Some,
//                        Moved Closing prepared_sqlStatement, sqlStatement, setAutoCommit(false)
//                        to finally Clauses.
//         1.7 04/30/2018 Initialized resourceType/Alert in Main Constructor. Method addTableEntry()
//                        Finished Main Testing, Coding for Date, Time, DateTime, Timestamp,
//                        & Year Type Fields. Cleaned Up That Part of Code.
//         1.8 05/03/2018 Class Instance resourceInvalidInput Properly Assigned in Constructor.
//                        Method addTableEntry() All, Continued to Clean Up Code, Removed Instance
//                        isBlobField. Uncommented & Processed SQLite Object, Integer, Real Fields.
//                        Completed Preliminary Testing of Temporal, Blob, Numerical, & Boolean 
//                        Types.
//         1.9 05/12/2018 Removed Use of ConnectionManager. Method addTableEntry() Removed Cleaning
//                        Up db_Connection.rollback(), setAutoCommit(), & Closing PrepareStatement
//                        in Exceptions With Display OptionPane. Same Method Completed Preliminary
//                        Testing for Bit & Bit Varying Fields. Comment Changes.
//         2.0 05/17/2018 Method initializePrepare() Removed Instance columnType Along With Commented
//                        Code Associated With Setting Values in Prepared Statement. Method add
//                        TableEntry() Implemented H2 Array Processing. Commited Out or Removed
//                        Testing System.outs.
//         2.1 05/20/2018 Removed Use of OptionPane User Output & Replaced With log() of Same.
//                        Added SQLLoadThread to Constructors Arguments. Added Class Instance
//                        sqlLoadThread. Implemented in run() for validImport False, log()
//                        Message & Call to Producer of Error, Cancel. Removed Class Instance
//                        resourceAlert.
//         2.2 05/23/2018 Method initializePrepare() sqlFieldNamesString, sqlValuesString, &
//                        sqlStatementString From Strings to StringBuilders.
//         2.3 05/29/2018 Moved From DB_To_FileMemoryDB Project to Ajqvue, Changed Package to
//                        utilities.db in Ajqvue. Changed References to DB_To_FileMemoryDBThread
//                        to DB_To_DBThread.
//             
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.rowset.serial.SerialBlob;

import com.dandymadeproductions.ajqvue.datasource.ConnectionInstance;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The SQLInsertPrepare class provides a means to create the required
 * INSERT for a PrepareStatement in use with a database to H2, HSQL, Derby,
 * or SQLite Memory/File database transfer.
 * 
 * @version 2.3 05/29/2018
 */

public class SQLInsertPrepareThread implements Runnable
{
   // Class Instances
   private SQLLoadThread sqlLoadThread;
   private Connection db_Connection;
   private PreparedStatement prepared_sqlStatement;
   
   private SQLQuery sqlQuery;
   private ArrayBlockingQueue<TableRowElements> tableRowDataQueue;
   private TableRowElements rowElementsData;
   private String dataSinkType, sqlTable;
   private String identifierQuoteString;
   
   private String resourceInvalidInput;
   private String resourceType;
   
   private int columnIndex, batchSize;
   private boolean batchSizeEnabled;
   
   private Logger threadLogger;

   //==============================================================
   // SQLInsertPrepareThread Constructors.
   //==============================================================
   
   public SQLInsertPrepareThread(SQLLoadThread sqlLoadThread,
                                 ConnectionInstance connectionInstance, Connection db_Connection,
                                 SQLQuery sqlQuery, ArrayBlockingQueue<TableRowElements> tableRowDataQueue,
                                 String sqlTable)
   {
      this(sqlLoadThread, connectionInstance, db_Connection, sqlQuery, tableRowDataQueue, sqlTable,
           DBTablesPanel.getGeneralDBProperties().getBatchSizeEnabled(),
           DBTablesPanel.getGeneralDBProperties().getBatchSize());
   }

   public SQLInsertPrepareThread(SQLLoadThread sqlLoadThread,
                                 ConnectionInstance connectionInstance, Connection db_Connection,
                                 SQLQuery sqlQuery, ArrayBlockingQueue<TableRowElements> tableRowDataQueue,
                                 String sqlTable, boolean batchSizeEnabled, int batchSize)
   {
      this.sqlLoadThread = sqlLoadThread;
      this.db_Connection = db_Connection;
      this.sqlQuery = sqlQuery;
      this.tableRowDataQueue = tableRowDataQueue;
      this.sqlTable = Utils.getSchemaTableName(sqlTable, connectionInstance.getCatalogSeparator(),
                                               connectionInstance.getIdentifierQuoteString());
      this.batchSizeEnabled = batchSizeEnabled;
      this.batchSize = batchSize;

      // Setup.
      dataSinkType = connectionInstance.getDataSourceType();
      identifierQuoteString = connectionInstance.getIdentifierQuoteString();
      
      resourceInvalidInput = "Invalid Input";
      resourceType = "Type";
   }
   
   //==============================================================
   // Class Method for Normal Start of the Thread
   //==============================================================

   public void run()
   {
      // Method Instances
      String sqlStatementString;
      Statement sqlStatement;
      
      int currentBatchRows;
      boolean validImport;
      
      // Check if can process and if so setup.
      
      if (!dataSinkType.equals(ConnectionInstance.H2)
            && !dataSinkType.equals(ConnectionInstance.HSQL2)
            && !dataSinkType.equals(ConnectionInstance.DERBY)
            && !dataSinkType.equals(ConnectionInstance.SQLITE))
         return;
      
      sqlStatement = null;
      prepared_sqlStatement = null;
      
      currentBatchRows = 0;
      validImport = false;
      
      try
      { 
         db_Connection.setAutoCommit(false);
         sqlStatement = db_Connection.createStatement();
         
         sqlStatementString = initializePrepare();
         log(Level.INFO, "SQLInsertPrepareThread", "run()", "Prepare Statement Created:\n" + sqlStatementString);
         prepared_sqlStatement = db_Connection.prepareStatement(sqlStatementString);
                  
         for (rowElementsData = tableRowDataQueue.take();
              !rowElementsData.getMessage().equals(TableRowElements.LAST_ELEMENT);
               rowElementsData = tableRowDataQueue.take())
         {  
            columnIndex = 0;
            validImport = addTableEntry(sqlStatement, prepared_sqlStatement);
            
            if (validImport)
            {
               // Commit on Batch Size if Desired.
               if (batchSizeEnabled)
               {
                  if (currentBatchRows > batchSize)
                  {
                     prepared_sqlStatement.executeBatch();
                     db_Connection.commit();
                     currentBatchRows = 0;
                  }
                  else
                     currentBatchRows++;
               } 
            }
            else
            {
               // Something wrong.
               // Tell the producer to stop and quit transfer.
               
               log(Level.WARNING, "SQLInsertPrepareThread", "run()", "validImport: " + validImport);
               sqlLoadThread.cancelLoad("SQLInsertPrepareThread run()");
               break;
            }
         }
         
         // Commiting the transactions as necessary
         // and cleaning up.

         if (validImport)
         {
            prepared_sqlStatement.executeBatch();
            db_Connection.commit();
         }
         else
            db_Connection.rollback();
      }
      catch (Exception e)
      {
         log(Level.WARNING, "SQLInsertPrepareThread", "run()", e.toString());
         
         try
         {
            db_Connection.rollback();
         }
         catch (SQLException error)
         {
            ConnectionInstance.displaySQLErrors(error, "SQLInsertPrepare run() rollback failed", true);
         }
      }
      finally
      {
         try
         {
            if (prepared_sqlStatement != null)
               prepared_sqlStatement.close();
         }
         catch (SQLException sqle)
         {
            ConnectionInstance.displaySQLErrors(sqle, "SQLInsertPrepare run() failed to close", true);
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
               ConnectionInstance.displaySQLErrors(sqle, "SQLInsertPrepare run() failed to close", true);
            }
            finally
            {
               try
               {
                  db_Connection.setAutoCommit(true);  
               }
               catch (SQLException sqle)
               {
                  ConnectionInstance.displaySQLErrors(sqle, "SQLInsertPrepare run() failed to setAutoCommit", true);
               }
            }
         }
      }
   }
   
   //==============================================================
   // 
   //==============================================================
   
   private String initializePrepare()
   {
      // Method Instances
      StringBuilder sqlFieldNamesString;
      StringBuilder sqlValuesString;
      StringBuilder sqlStatementString;
      
      Iterator<String> columnNamesIterator;
      String columnName;

      // Beginner SQL statement creation.
      
      sqlStatementString = new StringBuilder("INSERT INTO " + sqlTable + " ");
      sqlFieldNamesString = new StringBuilder("(");
      sqlValuesString = new StringBuilder("VALUES (");

      columnIndex = 0;
      columnNamesIterator = sqlQuery.getColumnNames().iterator();

      while (columnNamesIterator.hasNext())
      {
         // Prepping some instances for making things more clear
         // and easier.

         columnName = columnNamesIterator.next();

         // Composing intial SQL prepareStatement.
         
         sqlFieldNamesString.append(identifierQuoteString + columnName + identifierQuoteString + ", ");
         sqlValuesString.append("?, ");
      }
      
      // Concatenate everything together.
      if (sqlFieldNamesString.length() >= 3)
      {
         sqlFieldNamesString.delete(sqlFieldNamesString.length() - 2, sqlFieldNamesString.length());
         sqlFieldNamesString.append(")");
      }
      else
         sqlFieldNamesString.append(")");

      if (sqlValuesString.length() > 8)
      {
         sqlValuesString.delete(sqlValuesString.length() - 2, sqlValuesString.length());
         sqlValuesString.append(")");
      }
      else
         sqlValuesString.append(")");

      sqlStatementString.append(sqlFieldNamesString + " " + sqlValuesString);
      // System.out.println("SQLInsertPrepareThread initializePrepare() " + sqlStatementString);
      
      return sqlStatementString.toString(); 
   }

   //==============================================================
   // Class method to process the data row through insertion into
   // the database.
   //==============================================================

   private boolean addTableEntry(Statement sqlStatement, PreparedStatement prepared_sqlStatement)
   {
      // Method Instances
      Iterator<String> columnNamesIterator;
      Object currentContentData;
      
      String columnName;
      String columnClass;
      String columnType;
      
      String dateString;
      String timeString;
      
      String message;
      
      boolean isArrayField;
      int columnSize;
      
      boolean validEntry = false;

      // Processing.
      try
      {
         // ======================================================
         // Accessing the database and setting values for each
         // selected entry in the prepareStatement.

         columnNamesIterator = sqlQuery.getColumnNames().iterator();
         
         int i = 1;

         while (columnNamesIterator.hasNext())
         {
            // Prepping some instances for making things more clear
            // and easier.
            
            currentContentData = rowElementsData.getRowElement(columnIndex);
            
            columnName = columnNamesIterator.next();
            columnClass = sqlQuery.getColumnClassHashMap().get(columnName);
            columnType = sqlQuery.getColumnTypeNameHashMap().get(columnName);
            columnSize = (sqlQuery.getColumnSizeHashMap().get(columnName)).intValue();
            
            isArrayField = (columnClass.indexOf("Array") != -1 || columnType.indexOf("_") != -1);
            
            // System.out.println("SIPT: " + i + " " + columnName + " " + columnClass + " "
            //                    + columnType + " " + columnSize + " " + currentContentData);

            // Setting content to fields
            
            // Null
            if (currentContentData == null)
               prepared_sqlStatement.setObject(i++, null);

            // Numeric Type Fields
            else if ((columnClass.indexOf("Byte") != -1 && columnType.indexOf("CHAR") == -1)
                     || columnClass.indexOf("Short") != -1
                     || columnClass.indexOf("Integer") != -1 || columnClass.indexOf("Long") != -1
                     || columnClass.indexOf("Float") != -1 || columnClass.indexOf("Double") != -1
                     || columnClass.indexOf("BigDecimal") != -1
                     || (columnClass.indexOf("Object") != -1 && columnType.equals("REAL"))
                     || (columnClass.indexOf("Object") != -1 && columnType.equals("INTEGER")))
            {
               String value = null;
               
               try
               {
                  value = ((String) currentContentData).trim();
                  // System.out.println("SQLInsertPrepareThread addTableEntry() value: "
                  //                    + columnType + " " + value);

                  // Byte
                  if (columnClass.indexOf("Byte") != -1)
                  {
                     // byte byte_value = ((Byte) rowElementsData.getRowElement(columnIndex)).byteValue();
                     // byte byte_value = (Byte.valueOf((String) rowElementsData.getRowElement(columnIndex))).byteValue();
                     prepared_sqlStatement.setByte(i++, Byte.parseByte(value));
                  }
                  // Short
                  else if (columnClass.indexOf("Short") != -1)
                  {
                     //short short_value = ((Short) rowElementsData.getRowElement(columnIndex)).shortValue();
                     prepared_sqlStatement.setShort(i++, Short.parseShort(value));
                  }
                  // Integer
                  else if (columnClass.indexOf("Integer") != -1
                           || columnClass.indexOf("Object") != -1 && columnType.equals("INTEGER"))
                  {
                     // Distinguish Between (H2, HSQL, Derby) or SQLite
                     
                     if (columnClass.indexOf("Integer") != -1)
                     {
                        //int int_value = ((Integer) rowElementsData.getRowElement(columnIndex)).intValue();
                        prepared_sqlStatement.setInt(i++, Integer.parseInt(value));
                     }
                     else
                     {
                        long long_value = Long.parseLong(value);
                        
                        if (long_value > columnSize || long_value < -columnSize)
                           prepared_sqlStatement.setLong(i++, long_value);
                        else
                           prepared_sqlStatement.setInt(i++, Integer.parseInt(value));
                     }
                  }
                  // Long
                  else if (columnClass.indexOf("Long") != -1)
                  {
                     //long long_value = ((Long) rowElementsData.getRowElement(columnIndex)).longValue();
                     prepared_sqlStatement.setLong(i++, Long.parseLong(value));
                  }
                  // Float
                  else if (columnClass.indexOf("Float") != -1
                           || columnClass.indexOf("Object") != -1 && columnType.equals("REAL"))
                  {
                     //float float_value = ((Float) rowElementsData.getRowElement(columnIndex)).floatValue();
                     prepared_sqlStatement.setFloat(i++, Float.parseFloat(value));
                  }
                  // Double
                  else if (columnClass.indexOf("Double") != -1)
                  {
                     //double double_value = ((Double) rowElementsData.getRowElement(
                     //   columnIndex)).doubleValue();
                     prepared_sqlStatement.setDouble(i++, Double.parseDouble(value));
                  }
                  // Must Be BigDecimal
                  else
                  {
                     BigDecimal decimal_value = new BigDecimal(value);
                     prepared_sqlStatement.setBigDecimal(i++, decimal_value);
                  }
               }
               catch (NumberFormatException e)
               {
                  log(Level.WARNING, "SQLInsertPrepareThread", "addTableEntry()", e.toString());
                  log(Level.WARNING, "SQLInsertPrepareThread", "addTableEntry()", resourceInvalidInput
                      + " " + columnName + ", " + resourceType + ": " + columnType + " value: "
                      + value);
                  
                  validEntry = false;
                  return validEntry;
               }
            }

            // Date, Time, DateTime, Timestamp, & Year Type Fields
            else if (columnClass.indexOf("Date") != -1
                     || (columnClass.toUpperCase(Locale.ENGLISH)).indexOf("TIME") != -1)
            {
               String dateTimeString = ((String) currentContentData).trim();
               // System.out.println("SQLInsertPrepareThread addTableEntry() dateTimeString: " + dateTimeString);

               try
               {
                  // Date
                  if (columnType.equals("DATE"))
                  {
                     java.sql.Date dateValue;
                     
                     // Supported Databases, H2, HSQL, Derby, & SQLite.
                     // Conversion Year is to Date.
                     
                     if (dateTimeString.length() <= 4)
                     {
                        if (dateTimeString.length() == 2)
                           dateTimeString = "20" + dateTimeString + "-01-01";
                        else
                           dateTimeString = dateTimeString + "-01-01";
                     }
                     
                     dateValue = java.sql.Date.valueOf(dateTimeString);
                     prepared_sqlStatement.setDate(i++, dateValue);
                  }
                  // Time
                  else if (columnType.equals("TIME") || columnType.equals("TIMETZ")
                           || columnType.equals("TIME WITH TIME ZONE"))
                  {
                     // HSQL2
                     if (columnType.equals("TIME WITH TIME ZONE"))
                        prepared_sqlStatement.setString(i++, dateTimeString);
                     else
                        prepared_sqlStatement.setTime(i++, java.sql.Time.valueOf(
                           dateTimeString.substring(0, 8)));
                  }
                  // DateTime
                  else if (columnType.indexOf("DATETIME") != -1)
                  {
                     dateString = dateTimeString.substring(0, dateTimeString.indexOf(" "));
                     timeString = dateTimeString.substring(dateTimeString.indexOf(" "));
                     
                     if (columnType.equals("DATETIMEOFFSET"))
                        prepared_sqlStatement.setString(i++, dateString + timeString);
                     else
                        prepared_sqlStatement.setTimestamp(i++,
                           java.sql.Timestamp.valueOf(dateString + timeString));
                  }
                  // Timestamp
                  else if (columnType.equals("TIMESTAMP") || columnType.equals("TIMESTAMP WITH TIME ZONE")
                           || columnType.equals("TIMESTAMPTZ") || columnType.equals("TIMESTAMPLTZ")
                           || columnType.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                  {
                     if (columnType.equals("TIMESTAMPLTZ")
                         || columnType.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                        Utils.setLocalTimeZone(sqlStatement);

                     SimpleDateFormat timeStampFormat;
                     java.sql.Timestamp dateTimeValue;
                     java.util.Date dateParse;

                     try
                     {
                        // Create a Timestamp Format.
                        if (columnType.equals("TIMESTAMP"))
                        {
                           // Old MySQL Database Requirement, 4.x.
                           if (dataSinkType.equals(ConnectionInstance.MYSQL)
                               || dataSinkType.equals(ConnectionInstance.MARIADB))
                           {
                              if (columnSize == 2)
                                 timeStampFormat = new SimpleDateFormat("yy");
                              else if (columnSize == 4)
                                 timeStampFormat = new SimpleDateFormat("MM-yy");
                              else if (columnSize == 6)
                                 timeStampFormat = new SimpleDateFormat("MM-dd-yy");
                              else if (columnSize == 8)
                                 timeStampFormat = new SimpleDateFormat("MM-dd-yyyy");
                              else if (columnSize == 10)
                                 timeStampFormat = new SimpleDateFormat("MM-dd-yy HH:mm");
                              else if (columnSize == 12)
                                 timeStampFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                              // All current coloumnSizes for MySQL > 5.0 & MariaDB Should be 19.
                              else
                                 timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                           }
                           else if (dataSinkType.equals(ConnectionInstance.SQLITE))
                              timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                           else
                              timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        }
                        else
                        {
                           if (columnType.equals("TIMESTAMPLTZ")
                               || columnType.equals("TIMESTAMP WITH LOCAL TIMEZONE"))
                              timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                           else
                              timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                        }

                        // Parse the TimeStamp Format.
                        if (columnType.equals("TIMESTAMPLTZ"))
                        {
                           dateString = dateTimeString;
                           dateString = dateString.substring(0, dateString.lastIndexOf(':'))
                                        + dateString.substring(dateString.lastIndexOf(':') + 1);
                           dateParse = timeStampFormat.parse(dateString);
                        }
                        else
                           dateParse = timeStampFormat.parse(dateTimeString);

                        dateTimeValue = new java.sql.Timestamp(dateParse.getTime());
                        prepared_sqlStatement.setTimestamp(i++, dateTimeValue);
                     }
                     catch (ParseException e)
                     {
                        throw (new IllegalArgumentException(e + ""));
                     }
                  }
                  // Must be Year
                  else
                  {
                     dateString = dateTimeString;
                     java.sql.Date yearValue = java.sql.Date.valueOf(dateString + "-01-01");
                     prepared_sqlStatement.setString(i++, yearValue.toString().substring(0, 4));
                  }
               }
               catch (IllegalArgumentException e)
               {
                  message = "Invalid Date/Time Input for Field";
                  
                  log(Level.WARNING, "SQLInsertPrepareThread", "addTableEntry()", e.toString());
                  log(Level.WARNING, "SQLInsertPrepareThread", "addTableEntry()", message + " " + columnName
                      + ", " + resourceType + ": " + columnType);
                  
                  validEntry = false;
                  return validEntry;
               }
            }
            
            // Blob/Bytea/Binary/Raw Type Fields
            else if (DB_To_DBThread.isBlob(columnClass, columnType))
            { 
               if (dataSinkType.equals(ConnectionInstance.HSQL2))
               {
                  SerialBlob blobData = new SerialBlob((byte[]) currentContentData);
                  prepared_sqlStatement.setBlob(i++, blobData);           
               }
               else
                  prepared_sqlStatement.setBytes(i++, ((byte[]) currentContentData));  
            }
            
            // Array Type Fields (PostgreSQL & H2)
            
            // Current does not support PostgreSQL, Identifed
            // by columnType Index of Underscore.
            else if (isArrayField)
            {
               String arrayString;
               
               arrayString = (String) currentContentData;
               
               // Remove Surrounding PosgreSQL Braces.
               arrayString = arrayString.replaceAll("\\}", "(");
               arrayString = arrayString.replaceAll("\\}", ")");
               
               // Remove First/Last Parenthesis
               if (dataSinkType.equals(ConnectionInstance.H2))
                  arrayString = arrayString.substring(1, arrayString.length() - 1);
               
               prepared_sqlStatement.setString(i++, arrayString);
            }

            // Boolean Type Fields
            else if (columnClass.indexOf("Boolean") != -1)
            {
               boolean boolean_value;
               String booleanString;
               
               booleanString = ((String) currentContentData).toUpperCase(Locale.ENGLISH);
               
               if (booleanString.equals("TRUE") || booleanString.equals("T") || booleanString.equals("1"))
                  boolean_value = true;
               else
                  boolean_value = false;
               
               prepared_sqlStatement.setBoolean(i++, boolean_value);
            }

            // Bit, Bit Varying Type Fields
            else if (columnType.indexOf("BIT") != -1 && columnType.indexOf("_") == -1)
            {  
               String bitString;
               
               bitString = (String) currentContentData;
               
               if (dataSinkType.equals(ConnectionInstance.HSQL2) && columnSize > 1)
               {
                  if (columnType.equals("BIT"))
                     prepared_sqlStatement.setBytes(i++, Utils.convertBitsToHSQL_Bits(bitString));
                  // Bit Varying
                  else
                     prepared_sqlStatement.setString(i++, bitString);
               }
               else
               {
                  try
                  {
                     int int_value = Integer.parseInt(bitString, 2);
                     prepared_sqlStatement.setInt(i++, int_value);
                  }
                  catch (NumberFormatException e)
                  {
                     message = "Type: " + columnType;
                     
                     log(Level.WARNING, "SQLInsertPrepareThread", "addTableEntry()", e.toString());
                     log(Level.WARNING, "SQLInsertPrepareThread", "addTableEntry()", resourceInvalidInput
                         + " " + columnName + ", " + message);
                     
                     validEntry = false;
                     return validEntry;
                  }
               }
            }
            
            // Text, & Clob Fields
            else if (DB_To_DBThread.isText(columnClass, columnType, columnSize,
                     DB_To_DBThread.VARCHAR_LIMIT))
            {
               // All Text & Clobs are handled by getString(),
               // provide the option of detecting byte content.
               
               if (currentContentData instanceof String)
                  prepared_sqlStatement.setString(i++, (String) currentContentData);
               else
                  prepared_sqlStatement.setBytes(i++, (byte[]) currentContentData);    
            }

            // Standard fall through, should catch generic
            // table fields.
            else
            {
               prepared_sqlStatement.setString(i, (String) currentContentData);
               i++;
            }
            columnIndex++;
         }
         
         // Process the query.
         prepared_sqlStatement.addBatch();
         validEntry = true;
      }
      catch (SQLException e)
      {
         ConnectionInstance.displaySQLErrors(e, "SQLInsertPrepare addTableEntry()", true);
         validEntry = false;
      }
      return validEntry;  
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
   // This method converts the standard java.lang.Object Column
   // Class Type Name to a more appropriate definition that can be
   // used in determining the SQL Statement set type to be used.
   //
   // Ex. setInt(), setByte()...
   //==============================================================

   public String convertSQLiteClass(String typeName)
   {
      // Method Instances
      StringBuilder className;
      
      className = new StringBuilder();
      
      log(Level.INFO, "SQLInsertPrepareThread", "convertSQLiteClass()", "typeName: " + typeName);
      
      if (typeName.equals("TEXT"))
         className.append("java.lang.String");
      else if (typeName.equals("NUMERIC"))
         className.append("java.math.BigDecimal");
      else if (typeName.equals("INTEGER"))
         className.append("java.lang.Integer");
      else if (typeName.equals("REAL"))
         className.append("java.lang.Float");
      else
         className.append("java.lang.Object");
      
      log(Level.INFO, "SQLInsertPrepareThread", "convertSQLiteClass()", "className: "
          + className.toString());
      
      return className.toString();   
   }
   
   //==============================================================
   // Class Method to allow a logger to be assigned to the thread.
   //==============================================================

   public void setLogger(Logger logger)
   {
      threadLogger = logger;
   }
}