//=================================================================
//                  TableTabPanel_MySQL
//=================================================================
//
//    This class provides the table summary view of data in
// a MySQL database that is listed according to a specified sort
// and search. Entries from the database table may be viewed,
// added, edited, or deleted by means of this panel. The panel
// also provides the mechanism to page through the database table's
// data.
//
//            << TableTabPanel_MySQL.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.5 07/29/2018
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
// Version 1.0 Production TableTabPanel Class.
//         1.1 Code Formatting Instances, One per Line. Methods getColumnNames(),
//             loadTable(), viewSelectedItem(), addItem(), & editSelectedItem().
//             Changed Class Instance columnType to columnTypeName. Changed to
//             TableTabPanel Instance columnTypeNameHashMap.
//         1.2 Method getColumnNames() Added Instance columnSQLType & Used to
//             Store Value in columnSQLTypeHashMap. Method loadTable() Added
//             Instance columnSQLType. Method viewSelectedItem() Corrected
//             System.out to type name.
//         1.3 Methods addItem() & editSelectedItem() Change in Arguments for
//             TableEntryForm to Meet New Constructor Requirments.
//         1.4 Methods getColumnNames() & loadTable(), columnSQLTypeHashMap
//             Proper Loading of Integer & Extracting int.
//         1.5 Method getColumnNames() Additional Indexes Only Added Columns
//             That primaryKeys Does Not Currently Hold.
//        
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.forms.TableEntryForm;
import com.dandymadeproductions.ajqvue.utilities.BlobTextKey;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The TableTabPanel_MySQL class provides the table summary view of data
 * in a MySQL database that is listed according to a specified sort and search.
 * Entries from the database table may be viewed, added, edited, or deleted
 * by means of this panel. The panel also provides the mechanism to page
 * through the database table's data.
 * 
 * @author Dana M. Proctor
 * @version 1.5 07/29/2018
 */

public class TableTabPanel_MySQL extends TableTabPanel
{
   // Class Instances.
   private static final long serialVersionUID = -4690993550530323109L;

   public TableTabPanel_MySQL(String table, Connection setup_dbConnection, boolean viewOnlyTable)
   {
      super(table, setup_dbConnection, viewOnlyTable);
   }

   //==============================================================
   // Class method to obtain the column names from the table. The
   // names are modified for display and placed into a map for
   // later use. Additional information about the column, size,
   // type, etc., are also stored away for future use.
   //==============================================================

   public boolean getColumnNames(Connection dbConnection) throws SQLException
   {
      // Method Instances
      String sqlStatementString;
      Statement sqlStatement;
      ResultSet db_resultSet;
      ResultSetMetaData tableMetaData;

      String colNameString;
      String comboBoxNameString;
      String columnClass;
      Integer columnSQLType;
      String columnTypeName;
      String columnKey;
      String columnExtra;
      Integer columnSize;

      // Connecting to the data base, to obtain
      // meta data, and column names.
      
      sqlStatement = null;
      db_resultSet = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();

         // ====================================================
         // Setting Up the Column Names, Form Fields, ComboBox
         // Text, Hashmaps, Special Fields, & Primary Key(s).

         sqlStatementString = "SELECT * FROM " + schemaTableName + " LIMIT 1";
         // System.out.println(sqlStatementString);

         db_resultSet = sqlStatement.executeQuery(sqlStatementString);
         tableMetaData = db_resultSet.getMetaData();
         db_resultSet.close();

         // Primary key(s) & special fields.

         sqlStatementString = "DESCRIBE " + schemaTableName;
         db_resultSet = sqlStatement.executeQuery(sqlStatementString);

         while (db_resultSet.next())
         {
            colNameString = db_resultSet.getString("Field");
            columnTypeName = db_resultSet.getString("Type");
            columnKey = db_resultSet.getString("Key");
            columnExtra = db_resultSet.getString("Extra");
            // System.out.println(colNameString + " " + columnTypeName + " " +
            //                    columnKey + " " + columnExtra);

            if (columnKey.indexOf("PRI") != -1)
               primaryKeys.add(colNameString);

            // Should be only one auto_increment column per table.
            if (columnExtra.indexOf("auto_increment") != -1)
               autoIncrementHashMap.put(parseColumnNameField(colNameString), colNameString);

            // Boolean fields are set as enum.
            if (columnTypeName.indexOf("enum") != -1 || columnTypeName.equals("tinyint(1)"))
               columnEnumHashMap.put(parseColumnNameField(colNameString), columnTypeName);

            if (columnTypeName.indexOf("set") != -1)
               columnSetHashMap.put(parseColumnNameField(colNameString), columnTypeName);
         }
         db_resultSet.close();

         // Additional Indexes

         sqlStatementString = "SHOW INDEX FROM " + schemaTableName;
         db_resultSet = sqlStatement.executeQuery(sqlStatementString);

         while (db_resultSet.next())
         {
            colNameString = db_resultSet.getString("Column_name");
            
            if (!primaryKeys.contains(colNameString))
               primaryKeys.add(colNameString);
         }

         // Column Names, Form Fields, ComboBox Text and HashMaps.

         sqlTableFieldsString = "";
         lob_sqlTableFieldsString = "";

         for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
         {
            // Collect Information on Column.

            colNameString = tableMetaData.getColumnName(i);
            comboBoxNameString = parseColumnNameField(colNameString);
            columnClass = tableMetaData.getColumnClassName(i);
            columnSQLType = Integer.valueOf(tableMetaData.getColumnType(i));
            columnTypeName = tableMetaData.getColumnTypeName(i);
            columnSize = Integer.valueOf(tableMetaData.getColumnDisplaySize(i));

            // System.out.println(i + " " + colNameString + " " +
            //                    comboBoxNameString + " " +
            //                    columnClass + " " + columnSQLType + " " +
            //                    columnTypeName + " " + columnSize);

            // This going to be a problem so skip this column.

            if (columnClass == null && columnTypeName == null)
               continue;

            if (columnClass == null)
               columnClass = columnTypeName;

            // Process & Store.

            columnNamesHashMap.put(comboBoxNameString, colNameString);
            columnClassHashMap.put(comboBoxNameString, columnClass);
            columnSQLTypeHashMap.put(comboBoxNameString, columnSQLType);
            columnTypeNameHashMap.put(comboBoxNameString, columnTypeName.toUpperCase(Locale.ENGLISH));
            
            if (columnTypeName.toUpperCase(Locale.ENGLISH).equals("VARCHAR") && columnSize <= 0)
               columnSize = 2147483647;
            
            columnSizeHashMap.put(comboBoxNameString, columnSize);
            
            if (comboBoxNameString.length() < 5)
               preferredColumnSizeHashMap.put(comboBoxNameString,
                                           Integer.valueOf(6 * columnSizeScaling));
            else
               preferredColumnSizeHashMap.put(comboBoxNameString,
                  Integer.valueOf(comboBoxNameString.length() * columnSizeScaling));

            fields.add(colNameString);
            viewFormFields.add(comboBoxNameString);
            formFields.add(comboBoxNameString);
            comboBoxFields.add(comboBoxNameString);
            currentTableHeadings.add(comboBoxNameString);
            allTableHeadings.add(comboBoxNameString);
            sqlTableFieldsString += identifierQuoteString + colNameString + identifierQuoteString + ", ";
            
            //  Collect LOBs.
            if (((columnClass.indexOf("String") == -1
                  && columnTypeName.toUpperCase(Locale.ENGLISH).indexOf("BLOB") != -1)
                  || (columnClass.indexOf("String") != -1
                      && !columnTypeName.toUpperCase(Locale.ENGLISH).equals("CHAR")
                      && columnSize.intValue() > 65535)) && !primaryKeys.contains(colNameString))
            {
               lobDataTypesHashMap.put(comboBoxNameString, colNameString);
               lob_sqlTableFieldsString += identifierQuoteString + colNameString + identifierQuoteString + " ";
            }

            // Special Column Fields.
            if (primaryKeys.contains(colNameString))
            {
               if (columnSize == null || columnSize.intValue() > 255)
                   columnSize = Integer.valueOf("255");
               keyLengthHashMap.put(colNameString, columnSize);
            }
         }
         // Clean up the SQL field string for later use.
         if (sqlTableFieldsString.length() > 2)
            sqlTableFieldsString = sqlTableFieldsString.substring(0, sqlTableFieldsString.length() - 2);
         
         // Debug for key resolution varification.
         /*
         System.out.print(sqlTable + ": ");
         Iterator<String> temp = primaryKeys.iterator();
         while (temp.hasNext())
         {
            String currentKey = temp.next();
            System.out.print(currentKey + " " + keyLengthHashMap.get(currentKey) + ", ");
         }
         System.out.println();
         */

         return true;
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_MySQL getColumnNames()");
         return false;
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_MySQL getColumnNames()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }

   //==============================================================
   // Class method to load the current table's data. The routine
   // will apply the sort and search parameters.
   //==============================================================

   public boolean loadTable(Connection dbConnection)
   {
      // Method Instances
      StringBuffer lobLessSQLStatement;
      Statement sqlStatement;
      ResultSet rs;

      StringBuffer searchQueryString;
      String columnSearchString;
      String searchTextString;
      String lobLessFieldsString;
      String columnName;
      String columnClass;
      int columnSQLType;
      String columnTypeName;
      Integer keyLength;
      int columnSize, preferredColumnSize;
      Object currentContentData;
      
      // Obtain search parameters column names as needed & saving
      // state for history.
      
      columnSearchString = columnNamesHashMap.get(searchComboBox.getSelectedItem());
      searchTextString = searchTextField.getText();
      
      if (historyAction)
         saveHistory();
      
      searchQueryString = new StringBuffer();
      if (searchTextString.equals(""))
         searchQueryString.append("'1' LIKE '%'");
      else
      {         
         // No field specified so build search for all.
         if (columnSearchString == null)
         {
            String[] tableColumns;
            tableColumns = sqlTableFieldsString.split(",");
            
            for (int i = 0; i < tableColumns.length; i++)
            {
               columnName = tableColumns[i].replaceAll(identifierQuoteString, "");
               columnTypeName = columnTypeNameHashMap.get(parseColumnNameField(columnName.trim()));
               
               String searchString = searchTextString;
               
               if (columnTypeName.equals("DATE"))
               {
                  searchString = Utils.processDateFormatSearch(searchString);
                  
                  // Something not right in conversion.
                  if (searchString.equals("0"))
                     searchString = searchTextString;
               }
               else if (columnTypeName.equals("DATETIME") || columnTypeName.equals("TIMESTAMP"))
               {
                  if (searchString.indexOf(" ") != -1)
                     searchString = Utils.processDateFormatSearch(
                        searchString.substring(0, searchString.indexOf(" ")))
                        + searchString.substring(searchString.indexOf(" "));
                  else if (searchString.indexOf("-") != -1 || searchString.indexOf("/") != -1)
                     searchString = Utils.processDateFormatSearch(searchString);
               }
               else if (columnTypeName.equals("BIT"))
                  searchString = "B'" + searchString + "'";
               
               if (columnTypeName.equals("BIT"))
                  searchQueryString.append(tableColumns[i] + " LIKE " + searchString);
               else
                  searchQueryString.append(tableColumns[i] + " LIKE '%" + searchString + "%'");
                  
               if (i < tableColumns.length - 1)
                  searchQueryString.append(" OR");    
            }
         }
         // Field specified.
         else
         {
            columnTypeName = columnTypeNameHashMap.get(searchComboBox.getSelectedItem());
            
            if (columnTypeName.equals("DATE"))
               searchTextString = Utils.processDateFormatSearch(searchTextString);
            else if (columnTypeName.equals("DATETIME") || columnTypeName.equals("TIMESTAMP"))
            {
               if (searchTextString.indexOf(" ") != -1)
                  searchTextString = Utils.processDateFormatSearch(
                     searchTextString.substring(0, searchTextString.indexOf(" ")))
                     + searchTextString.substring(searchTextString.indexOf(" "));
               else if (searchTextString.indexOf("-") != -1 || searchTextString.indexOf("/") != -1)
                  searchTextString = Utils.processDateFormatSearch(searchTextString);
            }
            else if (columnTypeName.equals("BIT"))
               searchTextString = "B'" + searchTextString + "'";
            
            if (columnTypeName.equals("BIT"))
               searchQueryString.append(identifierQuoteString + columnSearchString + identifierQuoteString
                                        + " LIKE " + searchTextString);
            else
               searchQueryString.append(identifierQuoteString + columnSearchString + identifierQuoteString
                  + " LIKE '%" + searchTextString + "%'");
         }
      }
      // System.out.println(searchQueryString);

      // Connect to database to obtain the initial/new items set
      // and then sorting that set.
      
      sqlStatement = null;
      rs = null;
      
      try
      {
         lobLessFieldsString = sqlTableFieldsString;
         
         if (!lob_sqlTableFieldsString.equals(""))
         {
            String[]  lobColumns = lob_sqlTableFieldsString.split(" ");

            for (int i = 0; i < lobColumns.length; i++)
               lobLessFieldsString = lobLessFieldsString.replace(lobColumns[i], "");

            // All fields maybe lobs, so just include all. Network
            // performance hit.
            if (lobLessFieldsString.indexOf(identifierQuoteString) != -1)
               lobLessFieldsString = lobLessFieldsString.substring(lobLessFieldsString.indexOf(
                                                                          identifierQuoteString));
            else
               lobLessFieldsString = sqlTableFieldsString;
            
            lobLessFieldsString = lobLessFieldsString.replaceAll(" ,", "");
            if (lobLessFieldsString.endsWith(", "))
               lobLessFieldsString = lobLessFieldsString.substring(0, lobLessFieldsString.length() - 2);
         }
         
         sqlTableStatement = new StringBuffer();
         lobLessSQLStatement = new StringBuffer();
         
         if (advancedSortSearch)
         {
            // Complete With All Fields.
            sqlTableStatement.append(advancedSortSearchFrame.getAdvancedSortSearchSQL(sqlTableFieldsString,
                                             tableRowStart, tableRowLimit));
            // Summary Table Without LOBs
            lobLessSQLStatement.append(advancedSortSearchFrame.getAdvancedSortSearchSQL(lobLessFieldsString,
                                             tableRowStart, tableRowLimit));
         }
         else
         {
            // Complete With All Fields.
            sqlTableStatement.append("SELECT " + sqlTableFieldsString + " FROM " + schemaTableName
                                      + " " + "WHERE " + searchQueryString.toString() + " " + "ORDER BY "
                                      + identifierQuoteString
                                      + columnNamesHashMap.get(sortComboBox.getSelectedItem())
                                      + identifierQuoteString + " " + ascDescString + " " + "LIMIT "
                                      + tableRowLimit + " " + "OFFSET " + tableRowStart);
            
            // Summary Table Without LOBs
            lobLessSQLStatement.append("SELECT " + lobLessFieldsString + " FROM " + schemaTableName
                                        + " " + "WHERE " + searchQueryString.toString() + " " + "ORDER BY "
                                        + identifierQuoteString
                                        + columnNamesHashMap.get(sortComboBox.getSelectedItem())
                                        + identifierQuoteString + " " + ascDescString + " " + "LIMIT "
                                        + tableRowLimit + " " + "OFFSET " + tableRowStart);
         }
         // System.out.println(sqlTableStatement);
         // System.out.println(lobLessSQLStatement);
         
         if (dbConnection == null)
            return false;
         
         sqlStatement = dbConnection.createStatement();
         rs = sqlStatement.executeQuery(lobLessSQLStatement.toString());

         // Placing the results columns desired into the table that
         // will be display to the user.

         int i = 0;
         int j = 0;

         tableData = new Object[tableRowLimit][currentTableHeadings.size()];

         while (rs.next())
         {
            Iterator<String> headings = currentTableHeadings.iterator();
            while (headings.hasNext())
            {
               String currentHeading = headings.next();
               columnName = columnNamesHashMap.get(currentHeading);
               columnClass = columnClassHashMap.get(currentHeading);
               columnSQLType = (columnSQLTypeHashMap.get(currentHeading)).intValue();
               columnTypeName = columnTypeNameHashMap.get(currentHeading);
               columnSize = (columnSizeHashMap.get(currentHeading)).intValue();
               keyLength = keyLengthHashMap.get(columnName);
               preferredColumnSize = (preferredColumnSizeHashMap.get(currentHeading)).intValue();

               // System.out.println(i + " " + j + " " + currentHeading + " " +
               //                    columnName + " " + columnClass + " " +
               //                    columnSQLType + " " + columnTypeName + " " +
               //                    columnSize + " " + preferredColumnSize + " " + keyLength);

               // Storing data appropriately. If you have some date
               // or other formating, for a field here is where you
               // can take care of it.

               if (lobDataTypesHashMap.containsKey(currentHeading))
                  currentContentData = "lob";
               else
                  currentContentData = rs.getObject(columnName);

               if (currentContentData != null)
               {
                  // =============================================
                  // BigDecimal
                  if (columnClass.indexOf("BigDecimal") != -1)
                     tableData[i][j++] = new BigDecimal(rs.getString(columnName));

                  // =============================================
                  // Date
                  else if (columnTypeName.equals("DATE"))
                  {
                     currentContentData = rs.getDate(columnName);
                     String displayDate = displayMyDateString(currentContentData + "");
                     tableData[i][j++] = displayDate;
                  }

                  // =============================================
                  // Datetime
                  else if (columnTypeName.equals("DATETIME"))
                  {
                     currentContentData = rs.getTimestamp(columnName);
                     // System.out.println(currentContentData);
                     
                     tableData[i][j++] = (new SimpleDateFormat(
                        DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                        + " HH:mm:ss").format(currentContentData));
                  }

                  // =============================================
                  // Timestamp
                  else if (columnTypeName.equals("TIMESTAMP"))
                  {
                     currentContentData = rs.getTimestamp(columnName);
                     // System.out.println(currentContentData);

                     if (columnSize == 2)
                        tableData[i][j++] = (new SimpleDateFormat("yy").format(currentContentData));
                     else if (columnSize == 4)
                        tableData[i][j++] = (new SimpleDateFormat("MM-yy").format(currentContentData));
                     else if (columnSize == 6)
                        tableData[i][j++] = (new SimpleDateFormat("MM-dd-yy").format(currentContentData));
                     else if (columnSize == 8)
                        tableData[i][j++] = (new SimpleDateFormat("MM-dd-yyyy").format(currentContentData));
                     else if (columnSize == 10)
                        tableData[i][j++] = (new SimpleDateFormat("MM-dd-yy HH:mm")
                              .format(currentContentData));
                     else if (columnSize == 12)
                        tableData[i][j++] = (new SimpleDateFormat("MM-dd-yyyy HH:mm")
                              .format(currentContentData));
                     // All current coloumnSizes for MySQL > 5.0 Should be 19.
                     else
                        tableData[i][j++] = (new SimpleDateFormat(
                           DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                           + " HH:mm:ss").format(currentContentData));
                  }

                  // =============================================
                  // Year
                  else if (columnTypeName.equals("YEAR"))
                  {
                     String displayYear = currentContentData + "";
                     displayYear = displayYear.trim();

                     if (columnSize == 2)
                     {
                        if (displayYear.length() >= 4)
                           displayYear = displayYear.substring(2, 4);
                     }
                     else
                        displayYear = displayYear.substring(0, 4);
                     
                     tableData[i][j++] = displayYear;
                  }

                  // =============================================
                  // Blob
                  else if (columnClass.indexOf("String") == -1 && columnTypeName.indexOf("BLOB") != -1)
                  {
                     String blobName;

                     if (columnSize == 255)
                        blobName = ("Tiny Blob");
                     else if (columnSize == 65535)
                        blobName = ("Blob");
                     else if (columnSize == 16777215)
                        blobName = ("Medium Blob");
                     else if (columnSize > 16777215)
                        blobName = ("Long Blob");
                     else
                        blobName = ("Blob");
                     
                     // Handles a key Blob
                     if (keyLength != null)
                     {
                        BlobTextKey currentBlobElement = new BlobTextKey();
                        currentBlobElement.setName(blobName);

                        String content = rs.getString(columnName);

                        if (content.length() > keyLength.intValue())
                           content = content.substring(0, keyLength.intValue());

                        currentBlobElement.setContent(content);
                        tableData[i][j++] = currentBlobElement;
                     }
                     else
                     {
                        tableData[i][j++] = blobName;
                     }
                  }

                  // =============================================
                  // Boolean
                  else if (columnClass.indexOf("Boolean") != -1)
                  {
                     tableData[i][j++] = rs.getString(columnName);
                  }

                  // =============================================
                  // Bit
                  else if (columnTypeName.indexOf("BIT") != -1)
                  {
                     // The bit field has been defined as a Type [B &
                     // Class -3, VARBINARY. The only way to retrieve
                     // this is getByte() or try a function BIN, OCT,
                     // HEX or Cast. This will only work for BIT(1-8).
                     // BIT(m) Defines m 1-64. All gets Int, String
                     // will throw an exception. getBytes() will also
                     // not work because even if m > 8 returns only
                     // 1 byte.
                     
                     String byteString = Byte.toString((rs.getByte(columnName)));
                     tableData[i][j++] = Integer.toBinaryString(Integer.parseInt(byteString));
                  }
                  
                  // =============================================
                  // Text
                  else if (columnClass.indexOf("String") != -1 && !columnTypeName.equals("CHAR")
                           && columnSize > 255)
                  {
                     String stringName;

                     if (columnSize <= 65535)
                     {
                        stringName = (String) currentContentData;

                        // Limit Table Cell Memory Usage.
                        if (stringName.length() > 512)
                           stringName = stringName.substring(0, 512);

                     }
                     else if (columnSize == 16777215)
                        stringName = ("Medium Text");
                     else
                        // (columnSize > 16777215)
                        stringName = ("Long Text");

                     // Handles a key String
                     if (keyLength != null)
                     {
                        BlobTextKey currentBlobElement = new BlobTextKey();
                        currentBlobElement.setName(stringName);

                        String content = rs.getString(columnName);

                        if (content.length() > keyLength.intValue())
                           content = content.substring(0, keyLength.intValue());

                        currentBlobElement.setContent(content);
                        tableData[i][j++] = currentBlobElement;
                     }
                     else
                     {
                        tableData[i][j++] = stringName;
                     }
                  }

                  // =============================================
                  // Any Other
                  else
                  {
                     tableData[i][j++] = rs.getString(columnName);
                     // tableData[i][j++] = currentContentData;
                  }
               }
               // Null Data
               else
               {
                  tableData[i][j++] = "NULL";
               }

               // Setup some sizing for the column in the summary
               // table.
               if ((tableData[i][j - 1] + "").trim().length() * columnSizeScaling > preferredColumnSize)
               {
                  preferredColumnSize = (tableData[i][j - 1] + "").trim().length() * columnSizeScaling;
                  if (preferredColumnSize > MAX_PREFERRED_COLUMN_SIZE)
                     preferredColumnSize = MAX_PREFERRED_COLUMN_SIZE;
               }
               preferredColumnSizeHashMap.put(currentHeading, Integer.valueOf(preferredColumnSize));
            }
            j = 0;
            i++;
         }
         return true;
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_MySQL loadTable()");
         return false;
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_MySQL loadTable()");
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
               ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_MySQL loadTable()");
            }
         }
      }
   }
   
   //==============================================================
   // Class method to view the current selected item in the table.
   //==============================================================

   public void viewSelectedItem(Connection dbConnection, int rowToView) throws SQLException
   {
      // Method Instances
      StringBuffer sqlStatementString;
      Statement sqlStatement;
      ResultSet db_resultSet;

      Iterator<String> keyIterator;
      Iterator<String> textFieldNamesIterator;
      Object currentColumnName;
      Object currentContentData;
      String currentDB_ColumnName;
      String currentColumnClass;
      String currentColumnTypeName;
      int columnSize;
      int keyColumn = 0;

      // Connecting to the data base, to obtain
      // the selected entry.
      
      sqlStatement = null;
      db_resultSet = null;
      
      try
      {
         // Begin the SQL statement creation.
         sqlStatement = dbConnection.createStatement();
         sqlStatementString = new StringBuffer();
         sqlStatementString.append("SELECT * FROM " + schemaTableName + " WHERE ");

         if (!primaryKeys.isEmpty())
         {
            // Find the key column, in case it has been moved
            // in the summary table, then obtain entry content.

            keyIterator = primaryKeys.iterator();

            while (keyIterator.hasNext())
            {
               currentDB_ColumnName = keyIterator.next();

               for (int i = 0; i < listTable.getColumnCount(); i++)
                  if (listTable.getColumnName(i).equals(parseColumnNameField(currentDB_ColumnName)))
                     keyColumn = i;

               // Found now get key info.
               currentContentData = listTable.getValueAt(rowToView, keyColumn);

               // Special case of blob/text key.
               if (currentContentData instanceof BlobTextKey)
               {
                  String keyString = ((BlobTextKey) currentContentData).getContent();
                  keyString = keyString.replaceAll("'", "''");

                  // select * from t1 where a like "hello%";
                  sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                            + identifierQuoteString + " LIKE '"
                                            + keyString + "%' AND ");
               }
               // Normal keys
               else
               {
                  // Escape single quotes.
                  currentColumnClass = columnClassHashMap.get(parseColumnNameField(currentDB_ColumnName));
                  
                  if (currentColumnClass.indexOf("String") != -1)
                     currentContentData = ((String) currentContentData).replaceAll("'", "''");

                  // Reformat date keys.
                  currentColumnTypeName = columnTypeNameHashMap.get(parseColumnNameField(currentDB_ColumnName));
                  
                  if (currentColumnTypeName.equals("DATE"))
                  {
                     sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                               + identifierQuoteString + "='"
                                               + Utils.convertViewDateString_To_DBDateString(
                                                  currentContentData + "", DBTablesPanel.getGeneralDBProperties().getViewDateFormat())
                                               + "' AND ");
                  }
                  else
                     sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                               + identifierQuoteString + "='" + currentContentData
                                               + "' AND ");
               }

            }
            sqlStatementString.delete((sqlStatementString.length() - 5), sqlStatementString.length());
         }
         // See if we can brute force an all fields
         // SELECT WHERE query.
         else
         {
            // Cycle through each field and set value.
            for (int i = 0; i < listTable.getColumnCount(); i++)
            {
               currentContentData = listTable.getValueAt(rowToView, i);
               currentDB_ColumnName = (String) columnNamesHashMap.get(listTable.getColumnName(i));
               currentColumnClass = columnClassHashMap.get(listTable.getColumnName(i));
               currentColumnTypeName = columnTypeNameHashMap.get(listTable.getColumnName(i));
               columnSize = columnSizeHashMap.get(listTable.getColumnName(i)).intValue();
               
               // System.out.println("field:" + currentDB_ColumnName + " class:" + currentColumnClass
               //                    + " type name:" + currentColumnTypeName + " value:"
               //                    + currentContentData);
               
               // Skip Blob, Text, & Float Unless NULL.
               if ((currentColumnClass.indexOf("String") == -1 && currentColumnTypeName.indexOf("BLOB") != -1)
                     || (currentColumnClass.indexOf("String") != -1 && !currentColumnTypeName.equals("CHAR")
                         && columnSize > 255)
                     || (currentColumnTypeName.indexOf("FLOAT") != -1))
               {
                  if (currentContentData.toString().toUpperCase(Locale.ENGLISH).equals("NULL"))
                     sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                        + identifierQuoteString + " IS NULL AND ");
                  continue;     
               }
               
               // NULL
               if (currentContentData.toString().toUpperCase(Locale.ENGLISH).equals("NULL"))
               {
                  sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                     + identifierQuoteString + " IS NULL ");
               }
               // Try the Rest
               else
               {
                  sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                     + identifierQuoteString);
                  
                  // Process Date
                  if (currentColumnTypeName.equals("DATE"))
                  {
                     String dateString = Utils.processDateFormatSearch(
                        (String) currentContentData);
                     
                     sqlStatementString.append("='" + dateString + "' ");
                  }
                  // Process DateTime
                  else if (currentColumnTypeName.equals("DATETIME") || currentColumnTypeName.equals("TIMESTAMP"))
                  {
                     String content, dateTimeString;
                     content = (String) currentContentData;
                     
                     dateTimeString = Utils.processDateFormatSearch(content.substring(0,
                        content.indexOf(" ")))
                           + content.substring(content.indexOf(" "));
                     
                     sqlStatementString.append("='" + dateTimeString + "' ");
                  }
                  // Process BIT
                  else if (currentColumnTypeName.indexOf("BIT") != -1)
                  {
                     sqlStatementString.append("=B'" + currentContentData + "' ");
                  }
                  // All Others
                  else
                  {
                     if (currentColumnClass.indexOf("Integer") != -1
                           || currentColumnClass.indexOf("Long") != -1
                           || currentColumnClass.indexOf("Float") != -1
                           || currentColumnClass.indexOf("Double") != -1
                           || currentColumnClass.indexOf("Byte") != -1
                           || currentColumnClass.indexOf("BigDecimal") != -1
                           || currentColumnClass.indexOf("Short") != -1)
                        sqlStatementString.append("=" + currentContentData + " ");
                     else
                        sqlStatementString.append("='" + currentContentData + "' ");
                  }
               }
               sqlStatementString.append("AND ");
            }
            sqlStatementString.delete(sqlStatementString.length() - 4, sqlStatementString.length());
         }
         
         // System.out.println(sqlStatementString);
         db_resultSet = sqlStatement.executeQuery(sqlStatementString.toString());
         
         if (!db_resultSet.next())
            return;

         // Cycling through the item fields and setting
         // in the tableViewForm.

         textFieldNamesIterator = viewFormFields.iterator();
         int i = 0;

         while (textFieldNamesIterator.hasNext())
         {
            currentColumnName = textFieldNamesIterator.next();
            currentDB_ColumnName = columnNamesHashMap.get(currentColumnName);
            currentColumnClass = columnClassHashMap.get(currentColumnName);
            currentColumnTypeName = columnTypeNameHashMap.get(currentColumnName);
            columnSize = (columnSizeHashMap.get(currentColumnName)).intValue();

            currentContentData = db_resultSet.getString(currentDB_ColumnName);
            // System.out.println(i + " " + currentColumnName + " " +
            // currentDB_ColumnName + " " +
            // currentColumnTypeName + " " + columnSize + " " + currentContentData);

            if (currentContentData != null)
            {
               // DATE Type Field
               if (currentColumnTypeName.equals("DATE"))
               {
                  currentContentData = db_resultSet.getDate(currentDB_ColumnName);
                  tableViewForm.setFormField(currentColumnName,
                     (Object) displayMyDateString(currentContentData + ""));
               }

               // DATETIME Type Field
               else if (currentColumnTypeName.equals("DATETIME"))
               {
                  currentContentData = db_resultSet.getTimestamp(currentDB_ColumnName);
                  // System.out.println(currentContentData);
                  
                  tableViewForm.setFormField(currentColumnName,
                                             (new SimpleDateFormat(
                                                DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                                                + " HH:mm:ss").format(currentContentData)));
               }

               // TIMESTAMP Type Field
               else if (currentColumnTypeName.equals("TIMESTAMP"))
               {
                  currentContentData = db_resultSet.getTimestamp(currentDB_ColumnName);

                  if (columnSize == 2)
                     tableViewForm.setFormField(currentColumnName, (new SimpleDateFormat("yy")
                           .format(currentContentData)));
                  else if (columnSize == 4)
                     tableViewForm.setFormField(currentColumnName, (new SimpleDateFormat("MM-yy")
                           .format(currentContentData)));
                  else if (columnSize == 6)
                     tableViewForm.setFormField(currentColumnName, (new SimpleDateFormat("MM-dd-yy")
                           .format(currentContentData)));
                  else if (columnSize == 8)
                     tableViewForm.setFormField(currentColumnName, (new SimpleDateFormat("MM-dd-yyyy")
                           .format(currentContentData)));
                  else if (columnSize == 10)
                     tableViewForm.setFormField(currentColumnName, (new SimpleDateFormat("MM-dd-yy HH:mm")
                           .format(currentContentData)));
                  else if (columnSize == 12)
                     tableViewForm.setFormField(currentColumnName, (new SimpleDateFormat("MM-dd-yyyy HH:mm")
                           .format(currentContentData)));
                  // All current coloumnSizes for MySQL > 5.0 Should be 19.
                  else
                     tableViewForm.setFormField(currentColumnName,
                        (new SimpleDateFormat(DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                           + " HH:mm:ss").format(currentContentData)));
               }

               // YEAR Type Field
               else if (currentColumnTypeName.equals("YEAR"))
               {
                  String displayYear = currentContentData + "";
                  displayYear = displayYear.trim();

                  if (columnSize == 2)
                  {
                     if (displayYear.length() >= 4)
                        displayYear = displayYear.substring(2, 4);
                  }
                  else
                     displayYear = displayYear.substring(0, 4);

                  currentContentData = displayYear;
                  tableViewForm.setFormField(currentColumnName, currentContentData);
               }

               // Blob Type Field
               else if (currentColumnClass.indexOf("String") == -1 && currentColumnTypeName.indexOf("BLOB") != -1)
               {
                  if (((String) currentContentData).getBytes().length != 0)
                  {
                     currentContentData = db_resultSet.getBytes(currentDB_ColumnName);

                     int size = ((byte[]) currentContentData).length;
                     tableViewForm.setFormField(currentColumnName, (Object) ("BLOB " + size + " Bytes"));
                     tableViewForm.setFormFieldBlob(currentColumnName, (byte[]) currentContentData);
                  }
                  else
                     tableViewForm.setFormField(currentColumnName, (Object) "BLOB 0 Bytes");
               }

               // Bit Type Field
               else if (currentColumnTypeName.equals("BIT"))
               {
                  String byteString = Byte.toString((db_resultSet.getByte(currentDB_ColumnName)));
                  currentContentData = Integer.toBinaryString(Integer.parseInt(byteString));
                  tableViewForm.setFormField(currentColumnName, currentContentData);
               }

               // Text, MediumText & LongText Type Fields
               else if (currentColumnClass.indexOf("String") != -1 && !currentColumnTypeName.equals("CHAR")
                        && columnSize > 255)
               {
                  if (((String) currentContentData).getBytes().length != 0)
                  {
                     int size = ((String) currentContentData).getBytes().length;
                     tableViewForm.setFormField(currentColumnName, (Object) ("TEXT " + size + " Bytes"));
                     tableViewForm.setFormFieldText(currentColumnName, (String) currentContentData);
                  }
                  else
                     tableViewForm.setFormField(currentColumnName, (Object) "TEXT 0 Bytes");
               }

               // Default Content. A normal table entry should
               // fall through here, to set content.
               else
                  tableViewForm.setFormField(currentColumnName, currentContentData);
            }
            // Null fields fall through here.
            else
            {
               tableViewForm.setFormField(currentColumnName, "NULL");
            }
            i++;
         }
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_MySQL viewSelectedItem()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_MySQL viewSelectedItem()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }

   //==============================================================
   // Class method to add a table entry.
   //==============================================================

   public void addItem(Connection dbConnection)
   {
      Iterator<String> textFieldNamesIterator;
      Object currentColumnName;
      Object currentContentData;
      String currentColumnClass;
      String currentColumnTypeName;

      // Showing the Table Entry Form
      TableEntryForm addForm = new TableEntryForm("Add Table Entry: ", true, schemaTableName,
                                                  -1, this, formFields, tableViewForm);

      // Doing some sizing of the height based on the number
      // of fields in the table. The entry form will though
      // provided scrollbars.

      if ((((formFields.size() / 2) + 1) * 35) > 400)
      {
         if (((formFields.size() / 2) + 1) * 35 < 600)
            addForm.setSize(800, (((formFields.size() / 2) + 1) * 35));
         else
            addForm.setSize(800, 600);
      }
      else
         addForm.setSize(800, 400);

      addForm.getDisposeButton().addActionListener(this);
      addForm.addMouseListener(Ajqvue.getPopupMenuListener());
      addForm.center();
      addForm.setVisible(true);

      // Fields in the add form will be empty except the
      // ones where special data is specified to the
      // user. aka enum, set, date format.

      textFieldNamesIterator = formFields.iterator();

      while (textFieldNamesIterator.hasNext())
      {
         currentColumnName = textFieldNamesIterator.next();
         currentColumnClass = columnClassHashMap.get(currentColumnName);
         currentColumnTypeName = columnTypeNameHashMap.get(currentColumnName);

         // Auto-Increment Type Field
         if (autoIncrementHashMap.containsKey(currentColumnName))
         {
            currentContentData = "AUTO";
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // ENUM, BOOLEAN Single Bit, & SET Type Fields
         if (columnEnumHashMap.containsKey(currentColumnName)
             || columnSetHashMap.containsKey(currentColumnName))
         {
            try
            {
               setSpecialFieldData(addForm, dbConnection, currentColumnName, null);
            }
            catch (SQLException e)
            {
               ConnectionManager.displaySQLErrors(e, "TableTabPanel_MySQL addItem()");
            }  
         }

         // DATE Type Field
         if (currentColumnTypeName.equals("DATE"))
         {
            currentContentData = DBTablesPanel.getGeneralDBProperties().getViewDateFormat();
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // TIME Type Field
         if (currentColumnTypeName.equals("TIME"))
         {
            currentContentData = "hh:mm:ss";
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // DATETIME Type Field
         if (currentColumnTypeName.equals("DATETIME"))
         {
            currentContentData = DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " hh:mm:ss";
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // TIMESTAMP Type Field
         if (currentColumnTypeName.equals("TIMESTAMP"))
         {
            currentContentData = "NOW()";
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // BLOB Type Field
         if (currentColumnClass.indexOf("String") == -1 && currentColumnTypeName.indexOf("BLOB") != -1)
         {
            addForm.setFormField(currentColumnName, (Object) ("BLOB Browse"));
         }

         // All TEXT, MEDIUMTEXT & LONGTEXT Type Field
         if (currentColumnClass.indexOf("String") != -1 && !currentColumnTypeName.equals("CHAR")
             && ((columnSizeHashMap.get(currentColumnName)).intValue() > 255))
         {
            addForm.setFormField(currentColumnName, (Object) ("TEXT Browse"));
         }

         // YEAR Type Field
         if (currentColumnTypeName.equals("YEAR"))
         {
            currentContentData = "YYYY";
            addForm.setFormField(currentColumnName, currentContentData);
         }
      }
   }

   //==============================================================
   // Class method to edit the current selected item.
   //==============================================================

   public void editSelectedItem(Connection dbConnection, int rowToEdit, Object columnName, Object id)
                               throws SQLException
   {
      // Method Instances
      StringBuffer sqlStatementString;
      Statement sqlStatement;
      ResultSet db_resultSet;

      Iterator<String> keyIterator;
      Iterator<String> textFieldNamesIterator;
      Object currentColumnName;
      Object currentContentData;
      String currentDB_ColumnName;
      String currentColumnClass;
      String currentColumnTypeName;
      int currentColumnSize;
      int keyColumn = 0;

      // Showing the edit form and trying to size appropriately.
      TableEntryForm editForm = new TableEntryForm("Edit Table Entry: ", false, schemaTableName,
                                                   rowToEdit, this, formFields, tableViewForm);

      if ((((formFields.size() / 2) + 1) * 35) > 400)
      {
         if (((formFields.size() / 2) + 1) * 35 < 600)
            editForm.setSize(800, (((formFields.size() / 2) + 1) * 35));
         else
            editForm.setSize(800, 600);
      }
      else
         editForm.setSize(800, 400);
      editForm.getDisposeButton().addActionListener(this);
      editForm.center();
      editForm.setVisible(true);

      // Connecting to the data base, to obtain
      // the selected entries field data.
      
      sqlStatement = null;
      db_resultSet = null;

      try
      {
         sqlStatement = dbConnection.createStatement();

         // Begin the SQL statement(s) creation.
         sqlStatementString = new StringBuffer();
         sqlStatementString.append("SELECT * FROM " + schemaTableName + " WHERE ");

         keyIterator = primaryKeys.iterator();

         // Find the key column, in case it has been moved
         // in the summary table, then obtain entry content.

         while (keyIterator.hasNext())
         {
            currentDB_ColumnName = keyIterator.next();

            for (int i = 0; i < listTable.getColumnCount(); i++)
               if (listTable.getColumnName(i).equals(parseColumnNameField(currentDB_ColumnName)))
                  keyColumn = i;

            // Found the key so get info.
            currentContentData = listTable.getValueAt(rowToEdit, keyColumn);

            // Special case of blob/text key.
            if (currentContentData instanceof BlobTextKey)
            {
               String keyString = ((BlobTextKey) currentContentData).getContent();
               keyString = keyString.replaceAll("'", "''");

               sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                         + identifierQuoteString
                                         + " LIKE '" + keyString + "%' AND ");
            }
            // Normal key.
            else
            {
               // Escape single quotes.
               currentColumnClass = columnClassHashMap
                     .get(parseColumnNameField(currentDB_ColumnName));
               if (currentColumnClass.indexOf("String") != -1)
                  currentContentData = ((String) currentContentData).replaceAll("'", "''");

               // Reformat date keys.
               currentColumnTypeName = columnTypeNameHashMap.get(parseColumnNameField(currentDB_ColumnName));
               if (currentColumnTypeName.equals("DATE"))
               {
                  sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                            + identifierQuoteString + "='"
                                            + Utils.convertViewDateString_To_DBDateString(
                                               currentContentData + "",
                                               DBTablesPanel.getGeneralDBProperties().getViewDateFormat())
                                            + "' AND ");
               }
               else
                  sqlStatementString.append(identifierQuoteString + currentDB_ColumnName + identifierQuoteString
                                            + "='" + currentContentData + "' AND ");
            }
         }
         sqlStatementString.delete((sqlStatementString.length() - 5), sqlStatementString.length());
         // System.out.println(sqlStatementString);
         db_resultSet = sqlStatement.executeQuery(sqlStatementString.toString());
         db_resultSet.next();

         // Now that we have the data for the selected field entry in
         // the table fill in the edit form.

         textFieldNamesIterator = formFields.iterator();

         while (textFieldNamesIterator.hasNext())
         {
            currentColumnName = textFieldNamesIterator.next();
            currentDB_ColumnName = columnNamesHashMap.get(currentColumnName);
            currentColumnClass = columnClassHashMap.get(currentColumnName);
            currentColumnTypeName = columnTypeNameHashMap.get(currentColumnName);
            currentColumnSize = (columnSizeHashMap.get(currentColumnName)).intValue();

            currentContentData = db_resultSet.getString(currentDB_ColumnName);
            // System.out.println(currentColumnName + " " + currentContentData);

            // Special content from other tables, ComboBoxes, maybe.
            // Also time, date or your special field formatting.

            if (((String) currentColumnName).equals("Your Special Field Name"))
               setSpecialFieldData(editForm, dbConnection, currentColumnName, currentContentData);

            // ENUM, BOOLEAN Single Bit, & SET Type Fields
            else if (columnEnumHashMap.containsKey(currentColumnName)
                     || columnSetHashMap.containsKey(currentColumnName))
            {
               if (currentColumnClass.indexOf("Boolean") != -1 && currentColumnSize == 1)
               {
                  if (currentContentData.equals("0"))
                     currentContentData = "FALSE";
                  else
                     currentContentData = "TRUE";
               }
               setSpecialFieldData(editForm, dbConnection, currentColumnName, currentContentData);
            }

            // DATE Type Field
            else if (currentColumnTypeName.equals("DATE"))
            {
               if (currentContentData != null)
               {
                  currentContentData = db_resultSet.getDate(currentDB_ColumnName);
                  editForm.setFormField(currentColumnName,
                                        (Object) displayMyDateString(currentContentData + ""));
               }
               else
                  editForm.setFormField(currentColumnName,
                                        (Object) DBTablesPanel.getGeneralDBProperties().getViewDateFormat());
            }

            // DATETIME Type Field
            else if (currentColumnTypeName.equals("DATETIME"))
            {
               if (currentContentData != null)
               {
                  currentContentData = db_resultSet.getTimestamp(currentDB_ColumnName);
                  // System.out.println(currentContentData);
                  
                  currentContentData = new SimpleDateFormat(
                     DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                     + " HH:mm:ss").format(currentContentData);
                  editForm.setFormField(currentColumnName, currentContentData);
               }
               else
                  editForm.setFormField(currentColumnName,
                     (Object) DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " HH:MM:SS");
            }

            // TIMESTAMP Type Field
            else if (currentColumnTypeName.equals("TIMESTAMP"))
            {
               currentContentData = db_resultSet.getTimestamp(currentDB_ColumnName);
               // System.out.println(currentContentData);

               if (currentColumnSize == 2)
               {
                  if (currentContentData != null)
                  {
                     currentContentData = new SimpleDateFormat("yy").format(currentContentData);
                     editForm.setFormField(currentColumnName, currentContentData);
                  }
                  else
                     editForm.setFormField(currentColumnName, (Object) "YY");
               }
               else if (currentColumnSize == 4)
               {
                  if (currentContentData != null)
                  {
                     currentContentData = new SimpleDateFormat("MM-yy").format(currentContentData);
                     editForm.setFormField(currentColumnName, currentContentData);
                  }
                  else
                     editForm.setFormField(currentColumnName, (Object) "MM-YY");
               }
               else if (currentColumnSize == 6)
               {
                  if (currentContentData != null)
                  {
                     currentContentData = new SimpleDateFormat("MM-dd-yy").format(currentContentData);
                     editForm.setFormField(currentColumnName, currentContentData);
                  }
                  else
                     editForm.setFormField(currentColumnName, (Object) "MM-DD-YY");
               }
               else if (currentColumnSize == 8)
               {
                  if (currentContentData != null)
                  {
                     currentContentData = new SimpleDateFormat("MM-dd-yyyy").format(currentContentData);
                     editForm.setFormField(currentColumnName, currentContentData);
                  }
                  else
                     editForm.setFormField(currentColumnName, (Object) "MM-DD-YYYY");
               }
               else if (currentColumnSize == 10)
               {
                  if (currentContentData != null)
                  {
                     currentContentData = new SimpleDateFormat("MM-dd-yy HH:mm").format(currentContentData);
                     editForm.setFormField(currentColumnName, currentContentData);
                  }
                  else
                     editForm.setFormField(currentColumnName, (Object) "MM-DD-YY HH:MM");
               }
               else if (currentColumnSize == 12)
               {
                  if (currentContentData != null)
                  {
                     currentContentData = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(currentContentData);
                     editForm.setFormField(currentColumnName, currentContentData);
                  }
                  else
                     editForm.setFormField(currentColumnName, (Object) "MM-DD-YYYY HH:MM");
               }
               // All current coloumnSizes for MySQL > 5.0 Should be 19.
               else
               {
                  if (currentContentData != null)
                  {
                     currentContentData = new SimpleDateFormat(
                        DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                        + " HH:mm:ss").format(currentContentData);
                     editForm.setFormField(currentColumnName, currentContentData);
                  }
                  else
                     editForm.setFormField(currentColumnName,
                        (Object) DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " HH:MM:SS");
               }
            }

            // YEAR Type Field
            else if (currentColumnTypeName.equals("YEAR"))
            {
               currentContentData = db_resultSet.getObject(currentDB_ColumnName);
               String displayYear = currentContentData + "";
               displayYear = displayYear.trim();

               if (currentColumnSize == 2)
               {
                  if (currentContentData != null)
                  {
                     if (displayYear.length() >= 4)
                        displayYear = displayYear.substring(0, 4);
                     currentContentData = displayYear;
                     editForm.setFormField(currentColumnName, currentContentData);
                  }
                  else
                     editForm.setFormField(currentColumnName, (Object) "YYYY");
               }
               else
               {
                  if (currentContentData != null)
                  {
                     displayYear = displayYear.substring(0, 4);
                     currentContentData = displayYear;
                     editForm.setFormField(currentColumnName, currentContentData);
                  }
                  else
                     editForm.setFormField(currentColumnName, (Object) "YYYY");
               }
            }

            // Blob Type Field
            else if (currentColumnClass.indexOf("String") == -1 && currentColumnTypeName.indexOf("BLOB") != -1)
            {
               if (currentContentData != null)
               {
                  if (((String) currentContentData).getBytes().length != 0)
                  {
                     int size = ((String) currentContentData).getBytes().length;
                     editForm.setFormField(currentColumnName, (Object) ("BLOB " + size + " Bytes"));
                  }
                  else
                  {
                     editForm.setFormField(currentColumnName, (Object) ("BLOB 0 Bytes"));
                  }
                  editForm.setFormFieldBlob(currentColumnName, db_resultSet.getBytes(currentDB_ColumnName));

               }
               else
                  editForm.setFormField(currentColumnName, (Object) ("BLOB NULL"));
            }

            // Bit/Tinyint(1) Type Field
            else if (currentColumnTypeName.equals("BIT"))
            {
               String byteString = Byte.toString((db_resultSet.getByte(currentDB_ColumnName)));
               currentContentData = Integer.toBinaryString(Integer.parseInt(byteString));
               editForm.setFormField(currentColumnName, currentContentData);
            }

            // All Text But TinyText Type Field
            else if (currentColumnClass.indexOf("String") != -1 && !currentColumnTypeName.equals("CHAR")
                     && currentColumnSize > 255)
            {
               if (currentContentData != null)
               {
                  if (((String) currentContentData).getBytes().length != 0)
                  {
                     int size = ((String) currentContentData).getBytes().length;
                     editForm.setFormField(currentColumnName, (Object) ("TEXT " + size + " Bytes"));
                  }
                  else
                  {
                     editForm.setFormField(currentColumnName, (Object) "TEXT 0 Bytes");
                  }
                  editForm.setFormFieldText(currentColumnName, (String) currentContentData);
               }
               else
                  editForm.setFormField(currentColumnName, (Object) "TEXT NULL");
            }

            // Default Content. A normal table entry should
            // fall through here, to set content.
            else
            {
               if (currentContentData != null)
                  editForm.setFormField(currentColumnName, currentContentData);
               else
                  editForm.setFormField(currentColumnName, (Object) "NULL");
            }
         }
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_MySQL editSelectedEntry()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_MySQL editSelectedEntry()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }
}
