//=============================================================
//                  Table TabPanel_MSAccess
//=============================================================
//
//    This class provides the table summary view of data in
// a MS Access database that is listed according to a specified
// sort and search. Entries from the database table may be
// viewed, added, edited, or deleted by means of this panel.
// The panel also provides the mechanism to page through the
// database table's data.
//
//              << TableTabPanel_MSAccess.java >>
//
//================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.1 06/06/2018
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
// Version 1.0 Production TableTabPanel_MSAccess Class.
//         1.1 Code Formatting Instances, One per Line. Methods getColumnNames(),
//             loadTable(), viewSelectedItem(), addItem(), & editSelectedItem().
//             Changed Class Instance columnType to columnTypeName. Changed to
//             TableTabPanel Instance columnTypeNameHashMap.
//             
//-----------------------------------------------------------------
//                  danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
 *    The TableTabPanel_MSAccess  class provides the table summary view of data
 * in a MS Access database that is listed according to a specified sort and
 * search. Entries from the database table may be viewed, added, edited, or
 * deleted by means of this panel. The panel also provides the mechanism to
 * page through the database table's data.
 * 
 * @author Dana M. Proctor
 * @version 1.1 06/06/2018
 */

public class TableTabPanel_MSAccess extends TableTabPanel
{
   // Class Instances.
   private static final long serialVersionUID = 7730465062492731628L;

   public TableTabPanel_MSAccess(String table, Connection setup_dbConnection, boolean viewOnlyTable)
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
      ResultSet rs, db_resultSet;
      DatabaseMetaData dbMetaData;
      ResultSetMetaData tableMetaData;

      String tableName;
      String tableMetaData_Catalog;
      String tableMetaData_Schema;
      String tableMetaData_Table;
      String colNameString;
      String comboBoxNameString;
      String columnClass;
      String columnTypeName;
      Integer columnSize;

      // Connecting to the data base, to obtain
      // meta data, and column names.
      
      sqlStatement = null;
      db_resultSet = null;
      rs = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();
         if (sqlTable.indexOf(".") != -1)
            tableName = sqlTable.substring(sqlTable.indexOf(".") + 1);
         else
            tableName = sqlTable;

         // ====================================================
         // Setting Up the Column Names, Form Fields, ComboBox
         // Text, Hashmaps, Special Fields, & Primary Key(s).

         sqlStatementString = "SELECT * FROM " + schemaTableName;
         // System.out.println(sqlStatementString);

         db_resultSet = sqlStatement.executeQuery(sqlStatementString);
         
         dbMetaData = dbConnection.getMetaData();
         tableMetaData = db_resultSet.getMetaData();
         
         tableMetaData_Catalog = tableMetaData.getCatalogName(1);
         if (tableMetaData_Catalog.equals(""))
            tableMetaData_Catalog = null;
         
         tableMetaData_Schema = tableMetaData.getSchemaName(1);
         if (tableMetaData_Schema.equals(""))
            tableMetaData_Schema = null;
         
         tableMetaData_Table = tableMetaData.getTableName(1);

         // Primary Key(s)
         /* Access 97 Does not support
         
         rs = dbMetaData.getPrimaryKeys(tableMetaData_Catalog, tableMetaData_Schema,
                                        tableMetaData_Table);
         
         while (rs.next())
         {
            colNameString = rs.getString("COLUMN_NAME");
            
            if (colNameString.indexOf("chunk") == -1
                && rs.getString("TABLE_NAME").equals(tableName))
            {
               primaryKeys.add(colNameString);
               System.out.println(colNameString);
            }
         }
         */
         
         // Additional Indexes
         
         rs = dbMetaData.getIndexInfo(tableMetaData_Catalog, tableMetaData_Schema,
                                      tableMetaData_Table, false, false);
         
         while (rs.next())
         {
            colNameString = rs.getString("COLUMN_NAME");
            if (colNameString != null && rs.getString("TABLE_NAME").equals(tableName))
            {
               if (!primaryKeys.contains(colNameString))
               {
                  primaryKeys.add(colNameString);
                  // System.out.println(colNameString);
               }
            }
         }
         
         // Column Names, Form Fields, ComboBox Text, Special Fields,
         // and HashMaps.

         sqlTableFieldsString = "";
         lob_sqlTableFieldsString = "";

         for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
         {
            // Collect Information on Column.

            colNameString = tableMetaData.getColumnName(i);
            comboBoxNameString = parseColumnNameField(colNameString);
            columnClass = tableMetaData.getColumnClassName(i);
            columnTypeName = tableMetaData.getColumnTypeName(i);
            columnSize = Integer.valueOf(tableMetaData.getColumnDisplaySize(i));

            // System.out.println(i + " " + colNameString + " " +
            //                     comboBoxNameString + " " +
            //                     columnClass + " " + columnTypeName + " " +
            //                     columnSize);

            // This going to be a problem so skip this column.

            if (columnClass == null && columnTypeName == null)
               continue;

            if (columnClass == null)
               columnClass = columnTypeName;
            
            // Replace Counter with Integer to standardize.
            
            if (columnTypeName.toUpperCase(Locale.ENGLISH).equals("COUNTER"))
               columnTypeName = "INTEGER";

            // Process & Store.

            columnNamesHashMap.put(comboBoxNameString, colNameString);
            columnClassHashMap.put(comboBoxNameString, columnClass);
            columnTypeNameHashMap.put(comboBoxNameString, columnTypeName.toUpperCase(Locale.ENGLISH));
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

            // Collect LOBs.
            if (((columnTypeName.toUpperCase(Locale.ENGLISH).indexOf("BINARY") != -1)
                  || (columnTypeName.toUpperCase(Locale.ENGLISH).indexOf("LONGCHAR") != -1)
                  || (columnClass.indexOf("String") != -1 && columnTypeName.toUpperCase(Locale.ENGLISH).equals("TEXT"))
                  || (columnTypeName.toUpperCase(Locale.ENGLISH).equals("IMAGE")))
                  && !primaryKeys.contains(colNameString))
            {
               lobDataTypesHashMap.put(comboBoxNameString, colNameString);
               lob_sqlTableFieldsString += identifierQuoteString + colNameString + identifierQuoteString + " ";
            }
            
            // Special Column Fields.

            if (columnClass.indexOf("Boolean") != -1 && columnSize.intValue() == 1)
               columnEnumHashMap.put(parseColumnNameField(colNameString), "BOOLEAN");

            if (primaryKeys.contains(colNameString))
            {
               if (columnSize == null || columnSize.intValue() > 255)
                  columnSize = Integer.valueOf("255");
               keyLengthHashMap.put(colNameString, columnSize);
            }

            if (tableMetaData.isAutoIncrement(i))
               autoIncrementHashMap.put(comboBoxNameString, colNameString);
         }
         // Clean up the SQL field string for later use.
         if (sqlTableFieldsString.length() > 2)
            sqlTableFieldsString = sqlTableFieldsString.substring(0, sqlTableFieldsString.length() - 2);
         
         // Make a final check to see if there are any keys columns
         // columns in the table. If not then try foreign keys. Access
         // 97 Does not support.
         
         /*
         if (primaryKeys.isEmpty())
         {
            rs = dbMetaData.getImportedKeys(tableMetaData_Catalog, tableMetaData_Schema,
                                           tableMetaData_Table);
            String foreignKey = rs.getString("FKCOLUMN_NAME");
            
            while (rs.next())
            {
               if (columnNamesHashMap.containsValue(foreignKey)
                   && !primaryKeys.contains(foreignKey))
               {
                  primaryKeys.add(foreignKey);
                  columnSize = columnSizeHashMap.get(parseColumnNameField(foreignKey));
                  if (columnSize == null || columnSize.intValue() > 255)
                     columnSize = new Integer("255");
                  keyLengthHashMap.put(foreignKey, columnSize);
               }
            }
         }
         */
         
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
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_MSAccess getColumnNames()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_MSAccess getColumnNames()");
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
               ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_MSAccess getColumnNames()");
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
      String columnTypeName;
      int columnSize;
      int preferredColumnSize;
      Integer keyLength;
      int currentRow;
      Object currentContentData;

      // Obtain search parameters column names as needed and
      // saving state for history.
      
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
               
               if (columnTypeName.equals("DATE") || columnTypeName.equals("DATETIME"))
               {
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
                  
                  if (i < tableColumns.length - 1)
                     searchQueryString.append(tableColumns[i] + " LIKE '%" + searchString + "%' OR");
                  else
                     searchQueryString.append(tableColumns[i] + " LIKE '%" + searchString + "%'");
                  
                  // if (i < tableColumns.length - 1)
                  //    searchQueryString.append(tableColumns[i] + " LIKE #" + searchString + "# OR");
                  // else
                  //    searchQueryString.append(tableColumns[i] + " LIKE #" + searchString + "#");
               }
               else
               {
                  if (i < tableColumns.length - 1)
                     searchQueryString.append(tableColumns[i] + " LIKE '%" + searchString + "%' OR");
                  else
                     searchQueryString.append(tableColumns[i] + " LIKE '%" + searchString + "%'");
               }
            }
         }
         // Field specified.
         else
         {
            columnTypeName = columnTypeNameHashMap.get(searchComboBox.getSelectedItem());
            
            if (columnTypeName.equals("DATE") || columnTypeName.equals("DATETIME"))
            {
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
               
               searchQueryString.append(identifierQuoteString + columnSearchString + identifierQuoteString
                                        + " LIKE '%" + searchTextString + "%'");
               
               // searchQueryString.append(identifierQuoteString + columnSearchString + identifierQuoteString
               //                          + " LIKE #" + searchTextString + "#");
            }
            else
               searchQueryString.append(identifierQuoteString + columnSearchString + identifierQuoteString
                                   + " LIKE '%" + searchTextString + "%'");
         }
         // System.out.println(searchTextString);
      }

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
            sqlTableStatement.append("SELECT " + sqlTableFieldsString + " FROM " + schemaTableName + " "
                                 + "WHERE " + searchQueryString.toString() + " " + "ORDER BY "
                                 + identifierQuoteString
                                 + columnNamesHashMap.get(sortComboBox.getSelectedItem())
                                 + identifierQuoteString + " " + ascDescString);
            // Summary Table Without LOBs.
            lobLessSQLStatement.append("SELECT " + lobLessFieldsString + " FROM " + schemaTableName + " "
                                        + "WHERE " + searchQueryString.toString() + " " + "ORDER BY "
                                        + identifierQuoteString
                                        + columnNamesHashMap.get(sortComboBox.getSelectedItem())
                                        + identifierQuoteString + " " + ascDescString);  
         }
         // System.out.println(sqlTableStatement);
         // System.out.println(lobLessSQLStatement.toString());
         
         if (dbConnection == null)
            return false;
         
         sqlStatement = dbConnection.createStatement();
         rs = sqlStatement.executeQuery(lobLessSQLStatement.toString());

         // Placing the results columns desired into the table that
         // will be display to the user. Access does not support the
         // LIMIT aspect of ANSI SQL so select all table data then
         // only fill table according to tableRowLimit with currentRow
         // instance.

         int i = 0;
         int j = 0;
         currentRow = 0;

         tableData = new Object[tableRowLimit][currentTableHeadings.size()];

         while (rs.next() && currentRow < (tableRowStart + tableRowLimit))
         {
            if (!(currentRow++ >= tableRowStart))
               continue;
            
            Iterator<String> headings = currentTableHeadings.iterator();
            while (headings.hasNext())
            {
               String currentHeading = headings.next();
               columnName = columnNamesHashMap.get(currentHeading);
               columnClass = columnClassHashMap.get(currentHeading);
               columnTypeName = columnTypeNameHashMap.get(currentHeading);
               columnSize = (columnSizeHashMap.get(currentHeading)).intValue();
               keyLength = keyLengthHashMap.get(columnName);
               preferredColumnSize = (preferredColumnSizeHashMap.get(currentHeading)).intValue();

               // System.out.println(i + " " + j + " " + currentHeading + " " +
               //                   columnName + " " + columnClass + " " +
               //                   columnTypeName + " " + columnSize + " " +
               //                   preferredColumnSize + " " + keyLength);

               // Storing data appropriately. If you have some date
               // or other formating, for a field here is where you
               // can take care of it.

               if (lobDataTypesHashMap.containsKey(currentHeading))
                  currentContentData = "lob";
               
               // =============================================
               // BigDecimal
               if (columnClass.indexOf("BigDecimal") != -1)
               {
                  currentContentData = rs.getString(columnName);
                  if (currentContentData == null)
                     tableData[i][j++] = "NULL";
                  else
                     tableData[i][j++] = new BigDecimal(currentContentData.toString());
               }

               // =============================================
               // Date
               else if (columnTypeName.equals("DATE"))
               {
                  currentContentData = rs.getDate(columnName);
                  if (currentContentData == null)
                     tableData[i][j++] = "NULL";
                  else
                  {
                     String displayDate = displayMyDateString(currentContentData + "");
                     tableData[i][j++] = displayDate;
                  }
               }

               // =============================================
               // Datetime
               else if (columnTypeName.equals("DATETIME"))
               {
                  currentContentData = rs.getTimestamp(columnName);
                  // System.out.println(currentContentData);
                  
                  if (currentContentData == null)
                     tableData[i][j++] = "NULL";
                  else
                  {
                     tableData[i][j++] = (new SimpleDateFormat(
                        DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                        + " HH:mm:ss").format(currentContentData));
                  }
               }
                  
               // =============================================
               // BINARY
               else if (columnTypeName.indexOf("BINARY") != -1 || columnTypeName.indexOf("IMAGE") != -1)
               {
                  // Handles a key Binary/Blob
                  if (keyLength != null)
                  {
                     BlobTextKey currentBlobElement = new BlobTextKey();
                     currentBlobElement.setName("LOB");

                     String content = rs.getString(columnName);

                     if (content.length() > keyLength.intValue())
                        content = content.substring(0, keyLength.intValue());
                     
                     currentBlobElement.setContent(content);
                     tableData[i][j++] = currentBlobElement;
                  }
                  else
                  {
                     tableData[i][j++] = "LOB";
                  }
               }

               // =============================================
               // Boolean
               else if (columnClass.indexOf("Boolean") != -1)
               {
                  currentContentData = rs.getString(columnName);
                  
                  if (currentContentData == null)
                     tableData[i][j++] = "NULL";
                  else
                     tableData[i][j++] = currentContentData.toString();
               }
               
               // =============================================
               // Text
               else if ((columnTypeName.equals("LONGCHAR") || columnTypeName.equals("TEXT"))
                         && columnSize > 255)
               {
                  String stringName = "Text";

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
                  currentContentData = rs.getString(columnName);
                  
                  if (currentContentData == null)
                     tableData[i][j++] = "NULL";
                  else
                     tableData[i][j++] = currentContentData.toString();
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
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_MSAccess loadTable()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_MSAccess loadTable()");
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
               ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_MSAccess loadTable()");
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
               currentColumnTypeName = columnTypeNameHashMap.get(parseColumnNameField(currentDB_ColumnName));

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
                                            + identifierQuoteString
                                            + " LIKE '" + keyString + "%' AND ");
               }
               // Normal keys
               else
               {
                  // Handle null content properly.
                  if ((currentContentData + "").toLowerCase(Locale.ENGLISH).equals("null"))
                  {
                     currentContentData = "IS NULL";
                     sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                               + identifierQuoteString + " "
                                               + currentContentData + " AND ");
                  }
                  else
                  {
                     // Escape single quotes.
                     currentColumnClass = columnClassHashMap.get(parseColumnNameField(currentDB_ColumnName));
                     if (currentColumnClass.indexOf("String") != -1)
                        currentContentData = ((String) currentContentData).replaceAll("'", "''");
                     
                     // Reformat date keys.
                     currentColumnTypeName = columnTypeNameHashMap.get(parseColumnNameField(currentDB_ColumnName));
                     if (currentColumnTypeName.equals("DATETIME"))
                     {
                        String dateString = (String) currentContentData;
                        
                        if (dateString.indexOf(" ") != -1)
                           currentContentData = Utils.processDateFormatSearch(
                              dateString.substring(0, dateString.indexOf(" ")))
                              + dateString.substring(dateString.indexOf(" "));
                        else if (dateString.indexOf("-") != -1 || dateString.indexOf("/") != -1)
                           currentContentData = Utils.processDateFormatSearch(dateString);
                           
                        sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                                  + identifierQuoteString + "=#"
                                                  + currentContentData
                                                  + "# AND ");
                     }
                     else
                     {
                        // Character data gets single quotes, not numbers though.
                        if (currentColumnClass.toLowerCase(Locale.ENGLISH).indexOf("string") != -1)
                           sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                                  + identifierQuoteString + "='"
                                                  + currentContentData + "' AND ");
                        else
                           sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                              + identifierQuoteString + "="
                              + currentContentData + " AND ");
                     }
                  }
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
               //                     + " type:" + currentColumnTypeName + " value:" + currentContentData);
               
               // Skip Blob, Text, & Float Unless NULL.
               if ((currentColumnTypeName.indexOf("BINARY") != -1 || currentColumnTypeName.indexOf("IMAGE") != -1)
                     || (((currentColumnTypeName.equals("LONGCHAR"))
                           || (currentColumnClass.indexOf("String") != -1
                                 && currentColumnTypeName.toUpperCase(Locale.ENGLISH).equals("TEXT")))
                            && (columnSize > 255))
                     || (currentColumnTypeName.equals("REAL")))
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
                  else if (currentColumnTypeName.equals("DATETIME"))
                  {
                     String content, dateTimeString;
                     content = (String) currentContentData;
                     
                     dateTimeString = Utils.processDateFormatSearch(content.substring(0,
                        content.indexOf(" ")))
                           + content.substring(content.indexOf(" "));
                     
                     sqlStatementString.append("=#" + dateTimeString + "# ");
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
                           || currentColumnClass.indexOf("Short") != -1
                           || currentColumnClass.indexOf("Boolean") != -1)
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
            //columnSize = (columnSizeHashMap.get(currentColumnName)).intValue();

            //currentContentData = db_resultSet.getString(currentDB_ColumnName);
            //System.out.println(i + " " + currentColumnName + " " +
            //currentDB_ColumnName + " " +
            //currentColumnTypeName + " " + columnSize + " " + currentContentData);

            // DATE Type Field
            if (currentColumnTypeName.equals("DATE"))
            {
                  currentContentData = db_resultSet.getDate(currentDB_ColumnName);
                  
                  if (currentContentData == null)
                     tableViewForm.setFormField(currentColumnName, "NULL");
                  else
                     tableViewForm.setFormField(currentColumnName,
                            (Object) displayMyDateString(currentContentData + ""));
            }

            // DATETIME Type Field
            else if (currentColumnTypeName.equals("DATETIME"))
            {
               
               currentContentData = db_resultSet.getString(currentDB_ColumnName);
               
               if (currentContentData == null)
                  tableViewForm.setFormField(currentColumnName, "NULL");
               else
               {
                  String dateString = currentContentData.toString();
                  dateString = dateString.substring(0, (dateString.indexOf(" ")));
                  dateString = displayMyDateString(dateString);

                  String timeString = currentContentData.toString();
                  timeString = timeString.substring(timeString.indexOf(" "));
                  currentContentData = dateString + timeString;
                  tableViewForm.setFormField(currentColumnName, currentContentData);  
               }
            }

            // Binary/Image Type Field
            else if (currentColumnTypeName.indexOf("BINARY") != -1 ||
                     currentColumnTypeName.indexOf("IMAGE") != -1)
            {
               currentContentData = db_resultSet.getBytes(currentDB_ColumnName);
               
               if (currentContentData == null)
                  tableViewForm.setFormField(currentColumnName, "NULL");
               else
               {
                  if (((byte[]) currentContentData).length != 0)
                  {
                     int size = ((byte[]) currentContentData).length;
                     if (currentColumnTypeName.equals("IMAGE"))
                     {
                        tableViewForm.setFormField(currentColumnName, (Object) ("IMAGE " + size + " Bytes"));
                        tableViewForm.setFormFieldBlob(currentColumnName, (byte[]) currentContentData);
                     }
                     else
                     {
                        tableViewForm.setFormField(currentColumnName, (Object) ("BINARY " + size + " Bytes"));
                        tableViewForm.setFormFieldBlob(currentColumnName, (byte[]) currentContentData);
                     }
                  }
                  else
                  {
                     if (currentColumnTypeName.equals("IMAGE"))
                        tableViewForm.setFormField(currentColumnName, (Object) "IMAGE 0 Bytes");
                     else
                        tableViewForm.setFormField(currentColumnName, (Object) "BINARY 0 Bytes");
                  }
               }
            }

            // Text, Fields
            else if (((currentColumnTypeName.equals("LONGCHAR"))
                      || (currentColumnClass.indexOf("String") != -1
                          && currentColumnTypeName.toUpperCase(Locale.ENGLISH).equals("TEXT")))
                     && (columnSizeHashMap.get(currentColumnName)).intValue() > 255)
            {
               currentContentData = db_resultSet.getString(currentDB_ColumnName);
               
               if (currentContentData == null)
                  tableViewForm.setFormField(currentColumnName, "NULL");
               else
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
            }

            // Default Content. A normal table entry should
            // fall through here, to set content.
            else
            {
               currentContentData = db_resultSet.getString(currentDB_ColumnName);
               
               if (currentContentData == null)
                  tableViewForm.setFormField(currentColumnName, "NULL");
               else
                  tableViewForm.setFormField(currentColumnName, currentContentData);
            }
            i++;
         }
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_MSAccess viewSelectedItem()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_MSAccess viewSelectedItem()");
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
                                                  -1, null, primaryKeys, autoIncrementHashMap, null,
                                                  formFields, tableViewForm, columnNamesHashMap,
                                                  columnClassHashMap, columnTypeNameHashMap,
                                                  columnSizeHashMap, columnEnumHashMap,
                                                  columnSetHashMap);

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
               ConnectionManager.displaySQLErrors(e, "TableTabPanel_MSAccess addItem()");
            }  
         }

         // DATE Type Field
         if (currentColumnTypeName.equals("DATE"))
         {
            currentContentData = DBTablesPanel.getGeneralDBProperties().getViewDateFormat();
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // TIMESTAMP Type Field
         if (currentColumnTypeName.equals("TIMESTAMP") || currentColumnTypeName.equals("TIMESTAMPTZ"))
         {
            currentContentData = "NOW()";
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // BLOB/BYTEA/BINARY Type Field
         if (currentColumnTypeName.indexOf("BINARY") != -1 || currentColumnTypeName.equals("IMAGE"))
         {
            if (currentColumnTypeName.equals("IMAGE"))
               addForm.setFormField(currentColumnName, (Object) ("IMAGE Browse"));
            else
               addForm.setFormField(currentColumnName, (Object) ("BINARY Browse"));
         }

         // All TEXT, MEDIUMTEXT & LONGTEXT Type Field
         if (((currentColumnClass.indexOf("String") != -1 && !currentColumnTypeName.equals("TEXT"))
               || (currentColumnTypeName.equals("LONGCHAR")))
             && (columnSizeHashMap.get(currentColumnName)).intValue() > 255)
         {
            addForm.setFormField(currentColumnName, (Object) ("TEXT Browse"));
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
                                                   rowToEdit, this, primaryKeys,
                                                   autoIncrementHashMap, id,
                                                   formFields, tableViewForm, columnNamesHashMap,
                                                   columnClassHashMap, columnTypeNameHashMap,
                                                   columnSizeHashMap, columnEnumHashMap,
                                                   columnSetHashMap);

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
               // Handle null content properly.
               if ((currentContentData + "").toLowerCase(Locale.ENGLISH).equals("null"))
               {
                  currentContentData = "IS NULL";
                  sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                            + identifierQuoteString + " " + currentContentData
                                            + " AND ");
               }
               else
               {
                  // Escape single quotes.
                  currentColumnClass = (String) columnClassHashMap
                        .get(parseColumnNameField(currentDB_ColumnName));
                  if (currentColumnClass.indexOf("String") != -1)
                     currentContentData = ((String) currentContentData).replaceAll("'", "''");

                  // Reformat date keys.
                  currentColumnTypeName = columnTypeNameHashMap.get(parseColumnNameField(currentDB_ColumnName));
                  if (currentColumnTypeName.equals("DATETIME"))
                  {
                     String dateString = (String) currentContentData;
                     
                     if (dateString.indexOf(" ") != -1)
                        currentContentData = Utils.processDateFormatSearch(
                           dateString.substring(0, dateString.indexOf(" ")))
                           + dateString.substring(dateString.indexOf(" "));
                     else if (dateString.indexOf("-") != -1 || dateString.indexOf("/") != -1)
                        currentContentData = Utils.processDateFormatSearch(dateString);
                        
                     sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                               + identifierQuoteString + "=#"
                                               + currentContentData
                                               + "# AND ");
                  }
                  else
                  {
                     // Character data gets single quotes, not numbers though.
                     if (currentColumnClass.toLowerCase(Locale.ENGLISH).indexOf("string") != -1)
                        sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                               + identifierQuoteString + "='"
                                               + currentContentData + "' And ");
                     else
                        sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                           + identifierQuoteString + "="
                           + currentContentData + " AND ");
                  }
               }
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

            // Special content from other tables, ComboBoxes, maybe.
            // Also time, date or your special field formatting.

            if (((String) currentColumnName).equals("Your Special Field Name"))
            {
               currentContentData = db_resultSet.getString(currentDB_ColumnName);
               setSpecialFieldData(editForm, dbConnection, currentColumnName, currentContentData);
            }

            // ENUM, BOOLEAN Single Bit, & SET Type Fields
            else if (columnEnumHashMap.containsKey(currentColumnName)
                     || columnSetHashMap.containsKey(currentColumnName))
            {
               currentContentData = db_resultSet.getString(currentDB_ColumnName);
               
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
               currentContentData = db_resultSet.getDate(currentDB_ColumnName);
               
               if (currentContentData != null)
               {
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
               currentContentData = db_resultSet.getTimestamp(currentDB_ColumnName);
               
               if (currentContentData != null)
               {
                  // System.out.println(currentContentData);
                  currentContentData = new SimpleDateFormat(
                     DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                     + " HH:mm:ss").format(currentContentData);
                  editForm.setFormField(currentColumnName, currentContentData);
               }
               else
                  editForm.setFormField(currentColumnName,
                     (Object) (DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " HH:MM:SS"));
            }

            // Binary/Image Type Field
            else if (currentColumnTypeName.indexOf("BINARY") != -1 ||
                     currentColumnTypeName.equals("IMAGE"))
            {
               String binaryType;
               if (currentColumnTypeName.equals("IMAGE"))
                  binaryType = "Image";
               else
                  binaryType = "BINARY";

               currentContentData = db_resultSet.getBytes(currentDB_ColumnName);
               
               if (currentContentData != null)
               {
                  if ((((byte[]) currentContentData)).length != 0)
                  {
                     int size = ((byte[]) currentContentData).length;
                     editForm.setFormField(currentColumnName, (Object) (binaryType + " " + size + " Bytes"));
                  }
                  else
                  {
                     editForm.setFormField(currentColumnName, (Object) (binaryType + " 0 Bytes"));
                  }
                  editForm.setFormFieldBlob(currentColumnName, (byte[]) currentContentData);

               }
               else
                  editForm.setFormField(currentColumnName, (Object) (binaryType + " NULL"));
            }

            // All Text But TinyText Type Field
            else if (currentColumnClass.indexOf("String") != -1 && !currentColumnTypeName.equals("CHAR")
                     && currentColumnSize > 255)
            {
               currentContentData = db_resultSet.getString(currentDB_ColumnName);
               
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
               currentContentData = db_resultSet.getString(currentDB_ColumnName);
               
               if (currentContentData != null)
                  editForm.setFormField(currentColumnName, currentContentData);
               else
                  editForm.setFormField(currentColumnName, (Object) "NULL");
            }
         }
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_MSAccess editSelectedItem()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_MSAccess editSelectedItem()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }
}
