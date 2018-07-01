//=============================================================
//                  TableTabPanel_SQLite
//=============================================================
//
//    This class provides the means to create a default table
// summary view of data in the SQLite database that is listed
// according to a specified sort and search. Entries from the
// database table may be viewed, added, edited, or deleted by
// means of this panel. The panel also provides the mechanism
// to page through the database table's data.
//
//           << TableTabPanel_SQLite.java >>
//
//================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.7 07/01/2018
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
// Version 1.0 09/19/2016 Production TableTabPanel_SQLite Class.
//         1.1 07/20/2017 Method getColumnNames() Included columnClassHashMap.
//                        Also DATETIME as java.sql.Timestamp & Minor Formatting
//                        Changes. Method loadTable() Minor Formatting Changes & Use
//                        of DATETIME Processing. Method viewSelectedItem() Added
//                        columnSize Instance, Minor Formating Changes & Added TEXT
//                        Definition Processing for columnSize > 255. Changed in Same
//                        the View of All TEXT Fields columnSize > 255 to be Displayed
//                        as Buttons. Method addItem() Minor Formatting Changes,
//                        Added Instances currentColumnClass & columnSize. Added
//                        Processing for TEXT Fields. Method editSelectedItem() Minor
//                        Formatting Changes & Proper Processing/Detection for TEXT
//                        Fields.
//         1.2 07/29/2017 Method getColumnNames() Instance rs.close() Before Reuse.
//         1.3 06/06/2018 Code Formatting Instances, One per Line. Methods getColumnNames(),
//                        loadTable(), viewSelectedItem(), addItem(), & editSelectedItem().
//                        Changed Class Instance columnType to columnTypeName. Changed
//                        to TableTabPanel Instance columnTypeNameHashMap.
//         1.4 06/16/2018 Method getColumnNames() Added Instance columnSQLType & Used to
//                        Store Value in columnSQLTypeHashMap. Method loadTable() Added
//                        Instance columnSQLType. Method viewSelectedItem() Corrected
//                        System.out to type name.
//         1.5 06/23/2018 Method getColumnNames() Change in Loading columnSQLTypeHashMap
//                        for Date, Time, Datetime, & Timestamp columnTypeNames With typeof()
//                        Information. Method loadTable() Change in Processing for Date &
//                        Time columnTypeNames to Use columnSQLType to Detect Storage
//                        of TEXT, getString(). Method viewSelectedItem() Added Class
//                        Instance currentColumnSQLType & Use of getString() For Date &
//                        Time currentColumnTypeNames When currentColumnSQLType is Not
//                        Integer. Method addItem() Change in Arguments for TableEntryForm.
//                        Method editSelectedItem() Same Changes as viewSelectedItem(), Plus
//                        TableEntryForm Arguments & Use of Utils.isBlob(), Utils.isText().
//         1.6 06/27/2018 Added Class Methods getDate(), getTime(), getTimeTZ(), &
//                        getTimestamp(). Replaced in loadTable(), viewSelectedItem(), &
//                        editSelectedItem() the Processing of Date, Time, TimeTZ, Datetime,
//                        & Timestamp Type Fields to the New Methods. Method viewSelected
//                        Item() Use of Utils.isNotQuoted(). Method addItem() Added Default
//                        Help Example Entry for Datetime.
//         1.7 07/01/2018 Method editSelectedItem() Correction for Argument to setFormField(
//                        Object, Object). Method getDate() Check for Null on dateObject
//                        Before Call to displayMyDateString(). Methods getTime/TZ() Added
//                        timeObject Instance. Method getTimestamp() Check for timestampObject
//                        Null.
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
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.forms.TableEntryForm;
import com.dandymadeproductions.ajqvue.utilities.BlobTextKey;
import com.dandymadeproductions.ajqvue.utilities.Utils;
import com.dandymadeproductions.ajqvue.utilities.db.SQLQuery;

/**
 *    The TableTabPanel_SQLite class provides the means to create a default table
 * summary view of data in an unknown database that is listed according to a
 * specified sort and search. Entries from the database table may be viewed,
 * added, edited, or deleted by means of this panel. The panel also provides
 * the mechanism to page through the database table's data.
 * 
 * @author Dana M. Proctor
 * @version 1.7 07/01/2018
 */

public class TableTabPanel_SQLite extends TableTabPanel
{
   // Class Instances.
   private static final long serialVersionUID = 1120844312402713622L;

   //===========================================================
   // TableTabPanel Constructor
   //===========================================================
   
   public TableTabPanel_SQLite(String table, Connection setup_dbConnection, boolean viewOnlyTable)
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
      String colNameString;
      String comboBoxNameString;
      String columnClass;
      Integer columnSQLType;
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

         sqlStatementString = "SELECT * FROM " + schemaTableName + " LIMIT 1";
         // System.out.println(sqlStatementString);

         db_resultSet = sqlStatement.executeQuery(sqlStatementString);

         // Primary Key(s)
         
         dbMetaData = dbConnection.getMetaData();
         tableMetaData = db_resultSet.getMetaData();

         rs = dbMetaData.getPrimaryKeys(tableMetaData.getCatalogName(1),
                                        tableMetaData.getSchemaName(1),
                                        tableMetaData.getTableName(1));
         while (rs.next())
         {
            if (rs.getString("COLUMN_NAME").indexOf("chunk") == -1
                && rs.getString("TABLE_NAME").equals(tableName))
            {
               primaryKeys.add(rs.getString("COLUMN_NAME"));
               // System.out.println(rs.getString("COLUMN_NAME"));
            }
         }
         rs.close();

         // Additional Indexes

         rs = dbMetaData.getIndexInfo(tableMetaData.getCatalogName(1),
                                      tableMetaData.getSchemaName(1),
                                      tableMetaData.getTableName(1), false, false);
         while (rs.next())
         {
            if (rs.getString("COLUMN_NAME") != null && rs.getString("TABLE_NAME").equals(tableName))
            {
               if (!primaryKeys.contains(rs.getString("COLUMN_NAME")))
               {
                  primaryKeys.add(rs.getString("COLUMN_NAME"));
                  // System.out.println(rs.getString("COLUMN_NAME"));
               }
            }
         }
         rs.close();
         
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
            columnSQLType = tableMetaData.getColumnType(i);
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
            
            if (columnTypeName.toUpperCase(Locale.ENGLISH).equals("DATE")
                || columnTypeName.toUpperCase(Locale.ENGLISH).equals("TIME")
                || columnTypeName.toUpperCase(Locale.ENGLISH).indexOf("DATETIME") != -1
                || columnTypeName.toUpperCase(Locale.ENGLISH).equals("TIMESTAMP"))
            {
               if (columnTypeName.toUpperCase(Locale.ENGLISH).equals("DATE"))
                  columnClassHashMap.put(comboBoxNameString, "java.sql.Date");
               else if (columnTypeName.toUpperCase(Locale.ENGLISH).equals("TIME"))
                  columnClassHashMap.put(comboBoxNameString, "java.sql.Time");
               else
                  columnClassHashMap.put(comboBoxNameString, "java.sql.Timestamp");
               
               columnSQLTypeHashMap.put(comboBoxNameString, SQLQuery.getTypeof(dbConnection,
                  "SELECT " + colNameString + " FROM " + schemaTableName + " LIMIT 1", colNameString));     
            }
            else
            {
               columnClassHashMap.put(comboBoxNameString, columnClass);
               columnSQLTypeHashMap.put(comboBoxNameString, columnSQLType);
            }
            
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
            if (columnTypeName.toUpperCase(Locale.ENGLISH).indexOf("BLOB") != -1
                && !primaryKeys.contains(colNameString))
            {
               lobDataTypesHashMap.put(comboBoxNameString, colNameString);
               lob_sqlTableFieldsString += identifierQuoteString + colNameString + identifierQuoteString + " ";
            }
            
            // Special Column Fields.

            if (columnClass.indexOf("Boolean") != -1 && columnSize.intValue() == 1)
               columnEnumHashMap.put(parseColumnNameField(colNameString), columnTypeName);

            if (columnTypeName.indexOf("enum") != -1)
               columnEnumHashMap.put(parseColumnNameField(colNameString), columnTypeName);

            if (columnTypeName.indexOf("set") != -1)
               columnSetHashMap.put(parseColumnNameField(colNameString), columnTypeName);

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
         
         // Make a final check for possible foreign keys.

         rs = dbMetaData.getImportedKeys(tableMetaData.getCatalogName(1),
                                         tableMetaData.getSchemaName(1),
                                         tableMetaData.getTableName(1));
         while (rs.next())
         {
            if (rs.getString("FKCOLUMN_NAME") != null
                  && columnNamesHashMap.containsValue(rs.getString("FKCOLUMN_NAME"))
                  && !primaryKeys.contains(rs.getString("FKCOLUMN_NAME")))
            {
               primaryKeys.add(rs.getString("FKCOLUMN_NAME"));
               columnSize = columnSizeHashMap.get(parseColumnNameField(rs.getString("FKCOLUMN_NAME")));
               
               if (columnSize == null || columnSize.intValue() > 255)
                   columnSize = Integer.valueOf("255");
                  
               keyLengthHashMap.put(rs.getString("FKCOLUMN_NAME"), columnSize);
            }
         }
         rs.close();
            
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
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_SQLite getColumnNames()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_SQLite getColumnNames()");
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
               ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_SQLite getColumnNames()");
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
      int columnSQLType;
      String columnTypeName;
      Integer keyLength;
      int columnSize, preferredColumnSize;
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
               
               if (columnTypeName.equals("DATE"))
               {
                  searchString = Utils.processDateFormatSearch(searchString);
                  
                  // Something not right in conversion.
                  if (searchString.equals("0"))
                     searchString = searchTextString;
               }
               else if (columnTypeName.equals("DATETIME") || columnTypeName.equals("TIMESTAMP")
                        || columnTypeName.equals("TIMESTAMPTZ"))
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
            }
         }
         // Field specified.
         else
         {
            columnTypeName = columnTypeNameHashMap.get(searchComboBox.getSelectedItem());
            
            if (columnTypeName.equals("DATE"))
               searchTextString = Utils.processDateFormatSearch(searchTextString);
            else if (columnTypeName.equals("DATETIME") || columnTypeName.equals("TIMESTAMP")
                     || columnTypeName.equals("TIMESTAMPTZ"))
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
         }
      }
      // System.out.println(searchTextString);

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
                                      + identifierQuoteString + " " + ascDescString + " " + "LIMIT "
                                      + tableRowLimit + " " + "OFFSET " + tableRowStart);
            
            // Summary Table Without LOBs.
            lobLessSQLStatement.append("SELECT " + lobLessFieldsString + " FROM " + schemaTableName + " "
                                        + "WHERE " + searchQueryString.toString() + " " + "ORDER BY "
                                        + identifierQuoteString
                                        + columnNamesHashMap.get(sortComboBox.getSelectedItem())
                                        + identifierQuoteString + " " + ascDescString + " " + "LIMIT "
                                        + tableRowLimit + " " + "OFFSET " + tableRowStart);  
         }
         // System.out.println(sqlTableStatement);
         // System.out.println(lobLessSQLStatement.toString());
         
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
               columnSQLType = columnSQLTypeHashMap.get(currentHeading);
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
               // System.out.println(currentContentData);

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
                     tableData[i][j++] = getDate(rs, columnSQLType, columnName);
                  }
                  
                  // =============================================
                  // Time
                  else if (columnTypeName.equals("TIME"))
                  {
                     tableData[i][j++] = getTime(rs, columnSQLType, columnName);
                  }

                  // =============================================
                  // Time With Time Zone
                  else if (columnTypeName.equals("TIMETZ"))
                  {
                     tableData[i][j++] = getTimeTZ(rs, columnSQLType, columnName);
                  }
                  
                  // =============================================
                  // Datetime, Timestamp, Timestamp With Time Zone.
                  else if (columnTypeName.equals("DATETIME")
                           || columnTypeName.equals("TIMESTAMP")
                           || columnTypeName.equals("TIMESTAMPTZ"))
                  {
                     tableData[i][j++] = getTimestamp(rs, columnSQLType, columnTypeName, columnName);
                  }
                  
                  // =============================================
                  // BLOB
                  else if (columnTypeName.equals("BLOB"))
                  {
                     // Handles a key Blob
                     if (keyLength != null)
                     {
                        BlobTextKey currentBlobElement = new BlobTextKey();
                        currentBlobElement.setName("BLOB");

                        String content = rs.getString(columnName);

                        if (content.length() > keyLength.intValue())
                           content = content.substring(0, keyLength.intValue());

                        currentBlobElement.setContent(content);
                        tableData[i][j++] = currentBlobElement;
                     }
                     else
                     {
                        tableData[i][j++] = "BLOB";
                     }
                  }

                  // =============================================
                  // Boolean
                  else if (columnClass.indexOf("Boolean") != -1)
                  {
                     tableData[i][j++] = rs.getString(columnName);
                     ;
                  }

                  // =============================================
                  // Text
                  else if (columnClass.indexOf("Object") != -1 && columnTypeName.equals("TEXT")
                           && columnSize > 255)
                  {
                     String stringName;
                     stringName = rs.getString(columnName);

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
                        // Limit Table Cell Memory Usage.
                        if (stringName.length() > 512)
                           tableData[i][j++] = stringName.substring(0, 512);
                        else
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
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_SQLite loadTable()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_SQLite loadTable()");
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
               ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_SQLite loadTable()");
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
      int currentColumnSQLType;
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
                        sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                                  + identifierQuoteString + "='"
                                                  + currentContentData + "' AND ");
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
               currentDB_ColumnName = columnNamesHashMap.get(listTable.getColumnName(i));
               currentColumnClass = columnClassHashMap.get(listTable.getColumnName(i));
               currentColumnSQLType = columnSQLTypeHashMap.get(listTable.getColumnName(i));
               currentColumnTypeName = columnTypeNameHashMap.get(listTable.getColumnName(i));
               columnSize = columnSizeHashMap.get(listTable.getColumnName(i)).intValue();
               
               // System.out.println("field:" + currentDB_ColumnName + " class:" + currentColumnClass
               //                    + " type name:" + currentColumnTypeName + " value:"
               //                    + currentContentData);
               
               // Skip Blob, Text, & Float Unless NULL.
               if ((currentColumnClass.indexOf("Object") != -1 && currentColumnTypeName.equals("TEXT")
                    && columnSize > 255)
                   || (currentColumnTypeName.indexOf("BLOB") != -1))
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
                  else if (currentColumnTypeName.equals("DATETIME")
                           || currentColumnTypeName.equals("TIMESTAMP"))
                  {
                     String content, dateTimeString;
                     content = (String) currentContentData;
                     
                     dateTimeString = Utils.processDateFormatSearch(content.substring(0,
                        content.indexOf(" ")))
                           + content.substring(content.indexOf(" "));
                     
                     sqlStatementString.append("='" + dateTimeString + "' ");
                  }
                  // All Others
                  else
                  {
                     if (Utils.isNotQuoted(currentColumnClass, currentColumnSQLType, currentColumnTypeName))
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
            currentColumnSQLType = columnSQLTypeHashMap.get(currentColumnName);
            currentColumnTypeName = columnTypeNameHashMap.get(currentColumnName);
            columnSize = columnSizeHashMap.get(currentColumnName).intValue();

            if (currentColumnTypeName.equals("BLOB"))
               currentContentData = db_resultSet.getBytes(currentDB_ColumnName);
            else
               currentContentData = db_resultSet.getString(currentDB_ColumnName);
            System.out.println(i + " " + currentColumnName + " " +
                                currentDB_ColumnName + " " + currentColumnSQLType + " " +
                                currentColumnTypeName + " " + columnSize + " " + currentContentData);

            if (currentContentData != null)
            {
               // DATE Type Field
               if (currentColumnTypeName.equals("DATE"))
               {
                  tableViewForm.setFormField(currentColumnName, getDate(db_resultSet, currentColumnSQLType,
                                                                        currentDB_ColumnName));
               }

               // Time
               else if (currentColumnTypeName.equals("TIME"))
               {
                  tableViewForm.setFormField(currentColumnName, getTime(db_resultSet, currentColumnSQLType,
                                                                        currentDB_ColumnName));
               }

               // Time With Time Zone
               else if (currentColumnTypeName.equals("TIMETZ"))
               {
                  tableViewForm.setFormField(currentColumnName, getTimeTZ(db_resultSet, currentColumnSQLType,
                                                                          currentDB_ColumnName));
               }
               
               // DATETIME, & TIMESTAMP Type Fields
               else if (currentColumnTypeName.equals("DATETIME")
                        || currentColumnTypeName.equals("TIMESTAMP")
                        || currentColumnTypeName.equals("TIMESTAMPTZ"))
               {
                  tableViewForm.setFormField(currentColumnName, getTimestamp(db_resultSet,
                                                                             currentColumnSQLType,
                                                                             currentColumnTypeName,
                                                                             currentDB_ColumnName));
               }

               // Blob Type Field
               else if (currentColumnTypeName.indexOf("BLOB") != -1)
               {
                  if (((byte[]) currentContentData).length != 0)
                  {
                     int size = ((byte[]) currentContentData).length;
                     tableViewForm.setFormField(currentColumnName, (Object) ("BLOB " + size + " Bytes"));
                     tableViewForm.setFormFieldBlob(currentColumnName, (byte[]) currentContentData);
                  }
                  else
                     tableViewForm.setFormField(currentColumnName, (Object) "BLOB 0 Bytes");
               }

               // Text, Fields
               else if (currentColumnClass.indexOf("Object") != -1 && currentColumnTypeName.equals("TEXT")
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
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_SQLite viewSelectedItem()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_SQLite viewSelectedItem()");
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
      int columnSize;

      // Showing the Table Entry Form
      TableEntryForm addForm = new TableEntryForm("Add Table Entry: ", true, schemaTableName, -1, this,
                                                  formFields, tableViewForm);

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
         columnSize = (columnSizeHashMap.get(currentColumnName)).intValue();

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
               ConnectionManager.displaySQLErrors(e, "TableTabPanel_SQLite addItem()");
            }  
         }

         // DATE Type Field
         if (currentColumnTypeName.equals("DATE"))
         {
            currentContentData = DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                                 + "";
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // TIME Type Field
         if (currentColumnTypeName.equals("TIME") || currentColumnTypeName.equals("TIMETZ"))
         {
            currentContentData = "hh:mm:ss";
            addForm.setFormField(currentColumnName, currentContentData);
         }
         
         // DATETIME Type Field
         if (currentColumnTypeName.equals("DATETIME"))
         {
            currentContentData = DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                                 + " HH:mm:ss";
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // TIMESTAMP Type Field
         if (currentColumnTypeName.equals("TIMESTAMP") || currentColumnTypeName.equals("TIMESTAMPTZ"))
         {
            currentContentData = "NOW()";
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // BLOB Type Field
         if (currentColumnTypeName.indexOf("BLOB") != -1)
         {
            addForm.setFormField(currentColumnName, (Object) ("BLOB Browse"));
         }
         
         // TEXT Type Field
         if (currentColumnClass.indexOf("Object") != -1 && currentColumnTypeName.equals("TEXT")
             && columnSize > 255)
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
      int currentColumnSQLType;
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
                     sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                               + identifierQuoteString + "='" + currentContentData
                                               + "' AND ");
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
            currentColumnSQLType = columnSQLTypeHashMap.get(currentColumnName);
            currentColumnTypeName = columnTypeNameHashMap.get(currentColumnName);
            currentColumnSize = (columnSizeHashMap.get(currentColumnName)).intValue();

            if (currentColumnTypeName.equals("BLOB"))
               currentContentData = db_resultSet.getBytes(currentDB_ColumnName);
            else
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
                  if (currentContentData.equals("f"))
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
                  editForm.setFormField(currentColumnName, getDate(db_resultSet, currentColumnSQLType,
                                                                   currentDB_ColumnName));
               else
                  editForm.setFormField(currentColumnName,
                                        (Object) DBTablesPanel.getGeneralDBProperties().getViewDateFormat());
            }
            
            // TIME
            else if (currentColumnTypeName.equals("TIME"))
            {
               if (currentContentData != null)
                  editForm.setFormField(currentColumnName, getTime(db_resultSet, currentColumnSQLType,
                                                                   currentDB_ColumnName));
               else
                  editForm.setFormField(currentColumnName, (Object) "HH:MM:SS");

            }

            // TIME With Time Zone
            else if (currentColumnTypeName.equals("TIMETZ"))
            {
               if (currentContentData != null)
                  editForm.setFormField(currentColumnName, getTimeTZ(db_resultSet, currentColumnSQLType,
                                                                     currentDB_ColumnName));
               else
                  editForm.setFormField(currentColumnName, (Object) "HH:MM:SS");
            }
            
            // Datetime, Timestamps Type Field
            else if (currentColumnTypeName.equals("DATETIME")
                     || currentColumnTypeName.equals("TIMESTAMP")
                     || currentColumnTypeName.equals("TIMESTAMPTZ"))
            {
               if (currentContentData != null)
                  editForm.setFormField(currentColumnName, getTimestamp(db_resultSet, currentColumnSQLType,
                                                                        currentColumnTypeName,
                                                                        currentDB_ColumnName));
               else
                  editForm.setFormField(currentColumnName,
                                        (Object) (DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                                        + " HH:MM:SS"));
            }
            
            // Blob/Bytea Type Field
            else if (Utils.isBlob(currentColumnClass, currentColumnTypeName))
            {
               String binaryType;
               if (currentColumnTypeName.indexOf("BLOB") != -1)
                  binaryType = "BLOB";
               else if (currentColumnTypeName.indexOf("BYTEA") != -1)
                  binaryType = "BYTEA";
               else
                  binaryType = "BINARY";

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
            else if (Utils.isText(currentColumnClass, currentColumnTypeName, true, currentColumnSize))
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
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_SQLite editSelectedItem()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_SQLite editSelectedItem()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }
   
   //==============================================================
   // Class methods to collect temporal data based on the defined
   // SQL Type, columnSQLType.
   //==============================================================
   
   public static Object getDate(ResultSet resultSet, int columnSQLType, String columnName)
         throws SQLException
   {
      // Method Instances.
      Object dateObject;
      
      if (columnSQLType == Types.INTEGER || columnSQLType == Types.NULL)
         dateObject = resultSet.getDate(columnName);
      else
         dateObject = resultSet.getString(columnName);
      
      if (dateObject != null)
         return displayMyDateString(dateObject + "");
      else
         return null;
   }
   
   public static Object getTime(ResultSet resultSet, int columnSQLType, String columnName)
         throws SQLException
   {
      Object timeObject;
      
      if (columnSQLType == Types.INTEGER || columnSQLType == Types.NULL)
      {
         timeObject = resultSet.getTime(columnName);
         
         if (timeObject != null)
            timeObject = (new SimpleDateFormat("HH:mm:ss").format(timeObject));
      }
      else
         timeObject = resultSet.getString(columnName);
      
      return timeObject;
   }
   
   public static Object getTimeTZ(ResultSet resultSet, int columnSQLType, String columnName)
         throws SQLException
   {
      Object timeObject;
      
      if (columnSQLType == Types.INTEGER || columnSQLType == Types.NULL)
      {
         timeObject = resultSet.getTime(columnName);
         
         if (timeObject != null)
            timeObject = new SimpleDateFormat("HH:mm:ss z").format(timeObject);
      }
      else
         timeObject = resultSet.getString(columnName);
      
      return timeObject;
   }
   
   public static Object getTimestamp(ResultSet resultSet, int columnSQLType, String columnTypeName,
                               String columnName) throws SQLException
   {
      // Method Instances.
      Object timestampObject;
      String dateString;
      String timeString;
      
      if (columnSQLType == Types.INTEGER || columnSQLType == Types.NULL)
      {
         timestampObject = resultSet.getTimestamp(columnName);
         
         if (timestampObject == null)
            return null;
         
         if (columnTypeName.equals("DATETIME"))
            return (new SimpleDateFormat(DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
               + " HH:mm:ss").format(timestampObject));
         else if (columnTypeName.equals("TIMESTAMP"))
            return (new SimpleDateFormat(DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
               + " HH:mm:ss.SSS").format(timestampObject));
         // TIMESTAMPTZ
         else
           return (new SimpleDateFormat(DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
              + " HH:mm:ss z").format(timestampObject));   
      }
      else
      {
         timestampObject = resultSet.getString(columnName);
         
         if (timestampObject == null)
            return null;
         
         if (((String) timestampObject).indexOf(" ") != -1)
         {
            dateString = timestampObject + "";
            dateString = dateString.substring(0, (dateString.indexOf(" ")));
            dateString = displayMyDateString(dateString);

            timeString = timestampObject + "";
            timeString = timeString.substring(timeString.indexOf(" "));
            timestampObject = dateString + timeString;
            
            return timestampObject;
         }
         else
            throw new SQLException("Timestamp String Invalid Format");  
      }
   }
}