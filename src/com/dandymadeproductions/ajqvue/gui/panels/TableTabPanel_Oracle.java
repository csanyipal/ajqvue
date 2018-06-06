//=============================================================
//                  Table TabPanel_Oracle
//=============================================================
//
//    This class provides the means to create a default table
// summary view of data in an Oracle database that is listed
// according to a specified sort and search. Entries from the
// database table may be viewed, added, edited, or deleted by
// means of this panel. The panel also provides the mechanism
// to page through the database table's data.
//
//              << TableTabPanel_Oracle.java >>
//
//================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.2 06/06/2018
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
// Version 1.0 Production TableTabPanel_Oracle Class.
//         1.1 Method getColumnNames() Instance rs.close() Before Reuse.
//         1.2 Code Formatting Instances, One per Line. Methods getColumnNames(),
//             loadTable(), viewSelectedItem(), addItem(), & editSelectedItem().
//             Changed Class Instance columnType to columnTypeName. Changed to
//             TableTabPanel Instance columnTypeNameHashMap.
//
//-----------------------------------------------------------------
//                   danap@dandymadeproductions.com
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;
import javax.swing.table.TableColumn;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.ConnectionProperties;
import com.dandymadeproductions.ajqvue.gui.forms.TableEntryForm;
import com.dandymadeproductions.ajqvue.utilities.BlobTextKey;
import com.dandymadeproductions.ajqvue.utilities.TableModel;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The TableTabPanel_Oracle class provides the means to create a default
 * table summary view of data in an Oracle database that is listed according
 * to a specified sort and search. Entries from the database table may be
 * viewed, added, edited, or deleted by means of this panel. The panel also
 * provides the mechanism to page through the database table's data.
 * 
 * @author Dana M. Proctor
 * @version 1.2 06/06/2018
 */

public class TableTabPanel_Oracle extends TableTabPanel
{
   // Class Instances Defined in Parent.
   private static final long serialVersionUID = -8263055023708364624L;
   
   private String sqlTableFieldsStringLTZ;

   //===========================================================
   // TableTabPanel Constructor
   //===========================================================

   public TableTabPanel_Oracle(String table, Connection setup_dbConnection, boolean viewOnlyTable)
   {
      super(table, setup_dbConnection, viewOnlyTable);
   }

   //==============================================================
   // Class method to obtain the column names from the table.
   // The names are modified for display and placed into a map
   // for later use. Additional information about the column,
   // size, type, etc., are also stored away for future use.
   //==============================================================

   public boolean getColumnNames(Connection dbConnection) throws SQLException
   {
      // Method Instances
      String sqlStatementString;
      Statement sqlStatement;
      ResultSet rs, db_resultSet;
      DatabaseMetaData dbMetaData;
      ResultSetMetaData tableMetaData;

      String databaseName;
      String schemaName;
      String tableName;
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
         {
            schemaName = sqlTable.substring(0, sqlTable.indexOf("."));
            tableName = sqlTable.substring(sqlTable.indexOf(".") + 1);
         }
         else
         {
            schemaName = "";
            tableName = sqlTable;
         }
         databaseName = ConnectionManager.getConnectionProperties().getProperty(ConnectionProperties.DB);

         // ====================================================
         // Setting Up the Column Names, Form Fields, ComboBox
         // Text, Hashmaps, Special Fields, & Primary Key(s).

         sqlStatementString = "SELECT * FROM " + schemaTableName + " WHERE ROWNUM=1";
         // System.out.println(sqlStatementString);

         db_resultSet = sqlStatement.executeQuery(sqlStatementString);

         // Primary Key(s)
         
         dbMetaData = dbConnection.getMetaData();
         tableMetaData = db_resultSet.getMetaData();

         rs = dbMetaData.getPrimaryKeys(databaseName, schemaName, tableName);
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

         // Additional Indexes, Exclude VIEWS.
         
         rs = dbMetaData.getTables(databaseName, schemaName, tableName, null);
         
         if (rs.next() && !rs.getString("TABLE_TYPE").equals("VIEW"))
         {
            // Clueless why needs quotes?
            rs = dbMetaData.getIndexInfo(databaseName,
               (identifierQuoteString + schemaName + identifierQuoteString),
               (identifierQuoteString + tableName + identifierQuoteString), false, false);
            
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
         }
         rs.close();
         
         // Column Names, Form Fields, ComboBox Text, Special Fields,
         // and HashMaps.

         sqlTableFieldsString = "";
         lob_sqlTableFieldsString = "";
         sqlTableFieldsStringLTZ = "";

         for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
         {
            // Collect Information on Column.

            colNameString = tableMetaData.getColumnName(i);
            comboBoxNameString = parseColumnNameField(colNameString);
            columnClass = tableMetaData.getColumnClassName(i);
            columnTypeName = tableMetaData.getColumnTypeName(i);
            columnSize = Integer.valueOf(tableMetaData.getColumnDisplaySize(i));

            // System.out.println(i + " " + colNameString + " " +
            // comboBoxNameString + " " +
            // columnClass + " " + columnTypeName + " " +
            // columnSize);

            // This going to be a problem so skip this column.

            if (columnClass == null && columnTypeName == null)
               continue;

            if (columnClass == null)
            {
               if (columnTypeName.toUpperCase(Locale.ENGLISH).equals("BINARY_FLOAT"))
               {
                  columnClass = "java.lang.Float";
                  columnTypeName = "FLOAT";
               }
               else if (columnTypeName.toUpperCase(Locale.ENGLISH).equals("BINARY_DOUBLE"))
               {
                  columnClass = "java.lang.Double";
                  columnTypeName = "DOUBLE";
               }
               else
                  columnClass = columnTypeName;
            }

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
            if (((columnTypeName.toUpperCase(Locale.ENGLISH).indexOf("BLOB") != -1)
                  || (columnTypeName.toUpperCase(Locale.ENGLISH).indexOf("RAW") != -1)
                  || (columnTypeName.toUpperCase(Locale.ENGLISH).indexOf("LONG") != -1)
                  || (columnTypeName.toUpperCase(Locale.ENGLISH).indexOf("CLOB") != -1))
                 && !primaryKeys.contains(colNameString))
            {
               lobDataTypesHashMap.put(comboBoxNameString, colNameString);
               lob_sqlTableFieldsString += identifierQuoteString + colNameString + identifierQuoteString + " ";
            }

            // Create a second table field string that allows the collection
            // ot Timestamp Fields with Local Time Zone. Oracle JDBC doozie.
            // SESSION TIMEZONE NOT SET. Were not going to do this at the
            // connection or ALTER SESSION. Only 10, not 11.

            if (columnTypeName.toUpperCase(Locale.ENGLISH).equals("TIMESTAMPLTZ"))
            {
               sqlTableFieldsStringLTZ += "TO_CHAR(" + identifierQuoteString + colNameString
                                          + identifierQuoteString + ", 'YYYY-MM-DD HH24:MM:SS TZR') AS "
                                          + identifierQuoteString + colNameString + identifierQuoteString
                                          + ", ";
            }
            else
               sqlTableFieldsStringLTZ += identifierQuoteString + colNameString + identifierQuoteString
                                          + ", ";

            // Special Column Fields.

            if (columnClass.indexOf("Boolean") != -1 && columnSize.intValue() == 1)
               columnEnumHashMap.put(parseColumnNameField(colNameString), columnTypeName);

            if (primaryKeys.contains(colNameString))
            {
               if (columnSize == null || Integer.parseInt(columnSize.toString()) > 255)
                  columnSize = Integer.valueOf("255");
               keyLengthHashMap.put(colNameString, columnSize);
            }
         }
         // Clean up the SQL field string for later use.
         if (sqlTableFieldsString.length() > 2)
        	 sqlTableFieldsString = sqlTableFieldsString.substring(0, sqlTableFieldsString.length() - 2);
         if (sqlTableFieldsStringLTZ.length() > 2)
            sqlTableFieldsStringLTZ = sqlTableFieldsStringLTZ.substring(0, sqlTableFieldsStringLTZ.length() - 2);

         // Make a final check for possible foreign keys.

         rs = dbMetaData.getImportedKeys(databaseName, schemaName, tableName);
         
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

         // Finally aaagh!! get any, sequence, autoIncrement Fields.
         
         for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
         {
            colNameString = tableMetaData.getColumnName(i);
            comboBoxNameString = parseColumnNameField(colNameString);

            sqlStatementString = "SELECT USER_IND_COLUMNS.INDEX_NAME FROM USER_IND_COLUMNS, "
                                 + "ALL_SEQUENCES WHERE USER_IND_COLUMNS.INDEX_NAME="
                                 + "ALL_SEQUENCES.SEQUENCE_NAME AND USER_IND_COLUMNS.TABLE_NAME='"
                                 + tableName + "' AND USER_IND_COLUMNS.COLUMN_NAME='" + colNameString + "'";
            // System.out.println(sqlStatementString);

            rs = sqlStatement.executeQuery(sqlStatementString);

            if (rs.next())
            {
               // System.out.println(rs.getString("INDEX_NAME"));
               autoIncrementHashMap.put(comboBoxNameString, rs.getString("INDEX_NAME"));
            }
         }

         return true;
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_Oracle getColumnNames()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_Oracle getColumnNames()");
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
               ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_Oracle getColumnNames()");
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
      String sqlStatementString;
      String lobLessSQLStatementString;
      Statement sqlStatement;
      ResultSet rs;

      StringBuffer searchQueryString;
      String columnSearchString;
      String searchTextString;
      String lobLessFieldsString;
      String columnName;
      String columnClass;
      String columnTypeName;
      Integer keyLength;
      int columnSize;
      int preferredColumnSize;
      Object currentContentData;

      // Obtain search parameters, column names as needed and
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
         // No column specified so create WHERE for all except
         // BFILE, LONG, and BLOB. Special case with Dates.
         
         if (columnSearchString == null)
         {
            String[] tableColumns;
            tableColumns = sqlTableFieldsString.split(",");
            
            for (int i = 0; i < tableColumns.length; i++)
            {
               columnName = tableColumns[i].replaceAll(identifierQuoteString, "");
               columnTypeName = columnTypeNameHashMap.get(parseColumnNameField(columnName.trim()));
               
               String searchString = searchTextString;
               
               if (columnTypeName.equals("BFILE") || columnTypeName.equals("LONG")
                   || columnTypeName.equals("BLOB"))
                  continue;
               
               if (columnTypeName.equals("DATE"))
               {
                  searchString = Utils.processDateFormatSearch(searchString);
                  
                  // Something not right in conversion.
                  if (searchString.equals("0") || searchString.equals(searchTextString))
                     searchQueryString.append(tableColumns[i] + " LIKE '%" + searchTextString + "%'");
                  else
                     searchQueryString.append(tableColumns[i] + " LIKE TO_DATE('" + searchString
                                              + "', 'YYYY-MM-dd')");
                  
                  if (i < tableColumns.length - 1)
                     searchQueryString.append(" OR");
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
            
            if (columnTypeName.equals("DATE"))
            {
               searchTextString = Utils.processDateFormatSearch(searchTextString);
               searchQueryString.append(identifierQuoteString + columnSearchString + identifierQuoteString
                                        + " LIKE TO_DATE('" + searchTextString + "', 'YYYY-MM-dd')");  
            }
            else if (columnTypeName.equals("TIMESTAMP"))
            {
               if (searchTextString.indexOf(" ") != -1)
                  searchTextString = Utils.processDateFormatSearch(
                     searchTextString.substring(0, searchTextString.indexOf(" ")))
                     + searchTextString.substring(searchTextString.indexOf(" "));
               else if (searchTextString.indexOf("-") != -1 || searchTextString.indexOf("/") != -1)
                  searchTextString = Utils.processDateFormatSearch(searchTextString);
               
               searchQueryString.append(identifierQuoteString + columnSearchString + identifierQuoteString
                                        + " LIKE TO_TIMESTAMP('" + searchTextString
                                        + "', 'YYYY-MM-dd HH24:MI:SS') ");     
            }
            else
               searchQueryString.append(identifierQuoteString + columnSearchString + identifierQuoteString
                                        + " LIKE '%" + searchTextString + "%'");
         }
         // System.out.println(searchQueryString);
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

         if (advancedSortSearch)
         {
            String sqlWhereString = "", lobLess_sqlWhereString = "";
            String sqlOrderString = "", lobLess_sqlOrderString = "";

            // Complete With All Fields.
            sqlStatementString = advancedSortSearchFrame.getAdvancedSortSearchSQL(sqlTableFieldsString,
                                             tableRowStart, tableRowLimit).toString();
            // Summary Table Without LOBs
            lobLessSQLStatementString = advancedSortSearchFrame.getAdvancedSortSearchSQL(lobLessFieldsString,
                                                tableRowStart, tableRowLimit).toString();

            // Clean up the standard sql to meet Oracle's lack of support
            // for the key word LIMIT.

            // Collect WHERE & ORDER structure.
            if (sqlStatementString.indexOf("WHERE") != -1)
            {
               if (sqlStatementString.indexOf("ORDER") != -1)
               {
                  sqlWhereString = sqlStatementString.substring(sqlStatementString.indexOf("WHERE"),
                                                                sqlStatementString.indexOf("ORDER") - 1);
                  lobLess_sqlWhereString = lobLessSQLStatementString.substring(
                                                       lobLessSQLStatementString.indexOf("WHERE"),
                                                       lobLessSQLStatementString.indexOf("ORDER") - 1);
               }
               else
               {
                  sqlWhereString = sqlStatementString.substring(sqlStatementString.indexOf("WHERE"),
                                                                sqlStatementString.indexOf("LIMIT") - 1);
                  lobLess_sqlWhereString = lobLessSQLStatementString.substring(
                                                     lobLessSQLStatementString.indexOf("WHERE"),
                                                     lobLessSQLStatementString.indexOf("LIMIT") - 1);
               }
            }
            if (sqlStatementString.indexOf("ORDER") != -1)
            {
               sqlOrderString = sqlStatementString.substring(sqlStatementString.indexOf("ORDER"),
                                                             sqlStatementString.indexOf("LIMIT") - 1);
               lobLess_sqlOrderString = lobLessSQLStatementString.substring(
                                                     lobLessSQLStatementString.indexOf("ORDER"),
                                                     lobLessSQLStatementString.indexOf("LIMIT") - 1);
            }
            
            // Finish creating modifed SQL.
            sqlStatementString = sqlStatementString.substring(0, sqlStatementString.indexOf("FROM") + 5);
            lobLessSQLStatementString = lobLessSQLStatementString.substring(0,
                                                          lobLessSQLStatementString.indexOf("FROM") + 5);

            sqlStatementString += "(SELECT ROW_NUMBER() "
                                  + ((sqlOrderString.equals("")) ? ("OVER (ORDER BY "
                                  + (sqlTableFieldsString.indexOf(",") != -1 ?
                                                        sqlTableFieldsString.substring(0, sqlTableFieldsString.indexOf(','))
                                                                             :
                                                        sqlTableFieldsString)
                                  + ") ")
                                                                 : ("OVER (" + sqlOrderString + ") "))
                                  + "AS dmprownumber, " + sqlTableFieldsStringLTZ + " "
                                  + "FROM " + schemaTableName + " " + sqlWhereString + ") "
                                  + "WHERE dmprownumber BETWEEN " + (tableRowStart + 1) + " AND "
                                  + (tableRowStart + tableRowLimit);
            
            lobLessSQLStatementString += "(SELECT ROW_NUMBER() "
                                         + ((lobLess_sqlOrderString.equals("")) ? ("OVER (ORDER BY "
                                         + (lobLessFieldsString.indexOf(",") != -1 ?
                                                        lobLessFieldsString.substring(0, lobLessFieldsString.indexOf(','))
                                                                                  :
                                                        lobLessFieldsString)
                                         + ") ")
                                              : ("OVER (" + lobLess_sqlOrderString + ") "))
                                         + "AS dmprownumber, " + sqlTableFieldsStringLTZ + " "
                                         + "FROM " + schemaTableName + " " + lobLess_sqlWhereString + ") "
                                         + "WHERE dmprownumber BETWEEN " + (tableRowStart + 1) + " AND "
                                         + (tableRowStart + tableRowLimit);
         }
         else
         {
            sqlStatementString = "SELECT " + sqlTableFieldsString + " FROM " + "(SELECT ROW_NUMBER() OVER "
                                 + "(ORDER BY " + identifierQuoteString
                                 + columnNamesHashMap.get(sortComboBox.getSelectedItem())
                                 + identifierQuoteString + " " + ascDescString + ") " + "AS dmprownumber, "
                                 + sqlTableFieldsStringLTZ + " " + "FROM " + schemaTableName + " " + "WHERE "
                                 + searchQueryString.toString() + ") " + "WHERE dmprownumber BETWEEN "
                                 + (tableRowStart + 1) + " AND " + (tableRowStart + tableRowLimit);
            
            lobLessSQLStatementString = "SELECT " + lobLessFieldsString + " FROM "
                                        + "(SELECT ROW_NUMBER() OVER "
                                        + "(ORDER BY " + identifierQuoteString
                                        + columnNamesHashMap.get(sortComboBox.getSelectedItem())
                                        + identifierQuoteString + " " + ascDescString + ") "
                                        + "AS dmprownumber, " + sqlTableFieldsStringLTZ + " "
                                        + "FROM " + schemaTableName + " " + "WHERE "
                                        + searchQueryString.toString() + ") " + "WHERE dmprownumber BETWEEN "
                                        + (tableRowStart + 1) + " AND " + (tableRowStart + tableRowLimit);
         }
         sqlTableStatement.append(sqlStatementString);
         // System.out.println(sqlTableStatement);
         // System.out.println(lobLessSQLStatementString);
         
         if (dbConnection == null)
            return false;
         
         sqlStatement = dbConnection.createStatement();
         rs = sqlStatement.executeQuery(lobLessSQLStatementString);

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
               columnTypeName = columnTypeNameHashMap.get(currentHeading);
               columnSize = (columnSizeHashMap.get(currentHeading)).intValue();
               keyLength = keyLengthHashMap.get(columnName);
               preferredColumnSize = ((Integer) preferredColumnSizeHashMap.get(currentHeading)).intValue();

               // System.out.println(i + " " + j + " " + currentHeading + " " +
               // columnName + " " + columnClass + " " +
               // columnTypeName + " " + columnSize + " " +
               // preferredColumnSize + " " + keyLength);

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
                  // Timestamps
                  else if (columnTypeName.equals("TIMESTAMP"))
                  {
                     currentContentData = rs.getTimestamp(columnName);
                     tableData[i][j++] = (new SimpleDateFormat(
                        DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                        + " HH:mm:ss").format(currentContentData));
                  }

                  else if (columnTypeName.equals("TIMESTAMPTZ") || columnTypeName.equals("TIMESTAMP WITH TIME ZONE")
                           || columnTypeName.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                  {
                     currentContentData = rs.getTimestamp(columnName);
                     
                     tableData[i][j++] = (new SimpleDateFormat(
                        DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                        + " HH:mm:ss Z").format(currentContentData));
                  }
                  
                  else if (columnTypeName.equals("TIMESTAMPLTZ"))
                  {
                     currentContentData = rs.getString(columnName);
                     String timestampString = (String) currentContentData;
                     
                     if (timestampString.indexOf(" ") != -1)
                        tableData[i][j++] = displayMyDateString(
                                            timestampString.substring(0, timestampString.indexOf(" ")))
                                            + timestampString.substring(timestampString.indexOf(" "));
                     else
                        tableData[i][j++] = timestampString;
                  }

                  // =============================================
                  // BLOB, RAW, LONG, & CLOB
                  else if (columnTypeName.equals("BLOB") || columnTypeName.indexOf("RAW") != -1
                           || columnTypeName.indexOf("CLOB") != -1
                           || (columnClass.indexOf("String") != -1 && columnTypeName.equals("LONG")))
                  {
                     String blobName;
                     
                     if (columnTypeName.equals("BLOB"))
                        blobName = "Blob";
                     else if (columnTypeName.indexOf("RAW") != -1)
                        blobName = "Raw";
                     else if (columnTypeName.equals("LONG"))
                        blobName = "Long";
                     else
                        blobName = "Clob";
                     
                     // Handles a key BLOB ?
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
                  // BFILE
                  else if (columnTypeName.equals("BFILE"))
                  {
                     tableData[i][j++] = "BFILE";
                  }

                  // =============================================
                  // VARCHAR2/NVARCHAR2/LONG
                  else if (columnClass.indexOf("String") != -1
                           && (!columnTypeName.equals("CHAR") || !columnTypeName.equals("NCHAR"))
                           && columnSize > 255)
                  {
                     String stringName;
                     stringName = (String) currentContentData;

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
               preferredColumnSizeHashMap.put(currentHeading,
                                              Integer.valueOf(preferredColumnSize));
            }
            j = 0;
            i++;
         }
         return true;
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_Oracle loadTable()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_Oracle loadTable()");
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
               ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_Oracle loadTable()");
            }
         }
      }
   }

   //=============================================================
   // Class method to view the current selected item in the table.
   //=============================================================

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
         sqlStatementString.append("SELECT " + sqlTableFieldsStringLTZ + " FROM "
                                   + schemaTableName + " WHERE ");
         
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
                                                  + identifierQuoteString + "=TO_DATE('"
                                                  + Utils.convertViewDateString_To_DBDateString(
                                                     currentContentData + "", DBTablesPanel.getGeneralDBProperties().getViewDateFormat())
                                                  + "', 'YYYY-MM-dd') AND ");
                     }
                     else
                        sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                                  + identifierQuoteString + "='" + currentContentData
                                                  + "' AND ");
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
               
               // Skip Blob, Text, Clob, Float, BFile & Timestamps Unless NULL.
               if ((currentColumnTypeName.equals("BLOB") || currentColumnTypeName.indexOf("RAW") != -1)
                     || ((currentColumnClass.indexOf("String") != -1 && !currentColumnTypeName.equals("CHAR")
                          && columnSize > 255)
                         || (currentColumnClass.indexOf("String") != -1 && currentColumnTypeName.equals("LONG")))
                     || (currentColumnTypeName.indexOf("CLOB") != -1)
                     || (currentColumnTypeName.indexOf("FLOAT") != -1)
                     || (currentColumnTypeName.equals("BFILE"))
                     || (currentColumnTypeName.indexOf("TIMESTAMP") != -1))
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
                     
                     sqlStatementString.append(" LIKE TO_DATE('" + dateString + "', 'YYYY-MM-dd') ");
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

            // Oracle only provides a one time chance to obtain the result
            // set for LONG RAW fields so just collect BLOB & RAW content once.

            if (currentColumnTypeName.equals("BLOB") || currentColumnTypeName.indexOf("RAW") != -1)
               currentContentData = db_resultSet.getBytes(currentDB_ColumnName);
            else
               currentContentData = db_resultSet.getObject(currentDB_ColumnName);

            // System.out.println(i + " " + currentColumnName + " " +
            // currentDB_ColumnName + " " +
            // currentColumnTypeName + " " +
            // columnSizeHashMap.get(currentColumnName) +
            // " " + currentContentData);

            if (currentContentData != null)
            {
               // DATE Type Field
               if (currentColumnTypeName.equals("DATE"))
               {
                  currentContentData = db_resultSet.getDate(currentDB_ColumnName);
                  tableViewForm.setFormField(currentColumnName, displayMyDateString(currentContentData + ""));
               }

               // Timestamps Type Field
               else if (currentColumnTypeName.equals("TIMESTAMP"))
               {
                  currentContentData = db_resultSet.getTimestamp(currentDB_ColumnName);
                  tableViewForm.setFormField(currentColumnName,
                                             (new SimpleDateFormat(
                                                DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                                                + " HH:mm:ss").format(currentContentData)));
               }

               // Timestamps With Time Zone Type Field
               else if (currentColumnTypeName.equals("TIMESTAMPTZ")
                        || currentColumnTypeName.equals("TIMESTAMP WITH TIME ZONE")
                        || currentColumnTypeName.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
               {
                  currentContentData = db_resultSet.getTimestamp(currentDB_ColumnName);
                  tableViewForm.setFormField(currentColumnName,
                     (new SimpleDateFormat(DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                        + " HH:mm:ss Z").format(currentContentData)));
               }

               // Timestamps With Local Time Zone Type Field
               else if (currentColumnTypeName.equals("TIMESTAMPLTZ"))
               {
                  currentContentData = db_resultSet.getString(currentDB_ColumnName);
                  String timestampString = (String) currentContentData;
                  
                  if (timestampString.indexOf(" ") != -1)
                     timestampString = displayMyDateString(
                                       timestampString.substring(0, timestampString.indexOf(" ")))
                                       + timestampString.substring(timestampString.indexOf(" "));
                  
                  tableViewForm.setFormField(currentColumnName, timestampString);
               }

               // Blob/Raw Type Field
               else if (currentColumnTypeName.equals("BLOB") || currentColumnTypeName.indexOf("RAW") != -1)
               {
                  String binaryType;
                  if (currentColumnTypeName.equals("BLOB"))
                     binaryType = "BLOB";
                  else
                     binaryType = "RAW";

                  if (((byte[]) currentContentData).length != 0)
                  {
                     int size = ((byte[]) currentContentData).length;

                     tableViewForm.setFormField(currentColumnName,
                        (Object) (binaryType + " " + size + " Bytes"));
                     tableViewForm.setFormFieldBlob(currentColumnName, (byte[]) currentContentData);
                  }
                  else
                     tableViewForm.setFormField(currentColumnName, (Object) (binaryType + " 0 Bytes"));
               }

               // BFILE Type Field
               else if (currentColumnTypeName.equals("BFILE"))
               {
                  tableViewForm.setFormField(currentColumnName, (Object) "BFILE Views Not Supported.");
               }

               // CLOB Type Field
               else if (currentColumnTypeName.indexOf("CLOB") != -1)
               {
                  currentContentData = db_resultSet.getString(currentDB_ColumnName);

                  if (((String) currentContentData).getBytes().length != 0)
                  {
                     int size = ((String) currentContentData).getBytes().length;

                     tableViewForm.setFormField(currentColumnName, (Object) ("CLOB " + size + " Bytes"));
                     tableViewForm.setFormFieldText(currentColumnName,
                                                    (String) currentContentData);
                  }
                  else
                     tableViewForm.setFormField(currentColumnName, (Object) "CLOB 0 Bytes");
               }

               // VARCHAR2 & LONG
               else if ((currentColumnClass.indexOf("String") != -1 &&
                         (!currentColumnTypeName.equals("CHAR") || !currentColumnTypeName.equals("NCHAR")) &&
                        (columnSizeHashMap.get(currentColumnName)).intValue() > 255) ||
                        (currentColumnClass.indexOf("String") != -1 && currentColumnTypeName.equals("LONG")))
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
               {
                  currentContentData = db_resultSet.getString(currentDB_ColumnName);
                  tableViewForm.setFormField(currentColumnName, currentContentData);
               }
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
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_Oracle viewSelectedItem()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_Oracle viewSelectedItem()");
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
                                                  -1, null, primaryKeys,
                                                  autoIncrementHashMap, null,
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

         // DATE Type Field
         if (currentColumnTypeName.equals("DATE"))
         {
            currentContentData = DBTablesPanel.getGeneralDBProperties().getViewDateFormat();
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // TIMESTAMP Type Field
         if (currentColumnTypeName.equals("TIMESTAMP") || currentColumnTypeName.equals("TIMESTAMPTZ")
             || currentColumnTypeName.equals("TIMESTAMP WITH TIME ZONE")
             || currentColumnTypeName.equals("TIMESTAMPLTZ")
             || currentColumnTypeName.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
         {
            currentContentData = "NOW()";
            addForm.setFormField(currentColumnName, currentContentData);
         }

         // BLOB, & RAW Type Field
         if (currentColumnTypeName.equals("BLOB") || currentColumnTypeName.indexOf("RAW") != -1)
         {
            if (currentColumnTypeName.equals("BLOB"))
               addForm.setFormField(currentColumnName, (Object) ("BLOB Browse"));
            else
               addForm.setFormField(currentColumnName, (Object) ("RAW Browse"));
         }

         // BFILE Field
         if (currentColumnTypeName.equals("BFILE"))
         {
            addForm.setFormField(currentColumnName, (Object) ("DIRECTORY OBJECT, FILENAME"));
         }

         // VARCHAR, LONG, & CLOB Type Field
         if ((currentColumnClass.indexOf("String") != -1 && !currentColumnTypeName.equals("CHAR")
              && (columnSizeHashMap.get(currentColumnName)).intValue() > 255)
             || (currentColumnClass.indexOf("String") != -1 && currentColumnTypeName.equals("LONG"))
             || currentColumnTypeName.indexOf("CLOB") != -1)
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
                                                   formFields, tableViewForm,
                                                   columnNamesHashMap, columnClassHashMap, columnTypeNameHashMap,
                                                   columnSizeHashMap, columnEnumHashMap, columnSetHashMap);

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
         sqlStatementString.append("SELECT " + sqlTableFieldsStringLTZ + " FROM "
                                   + schemaTableName + " WHERE ");

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
                                               + identifierQuoteString + "=TO_DATE('"
                                               + Utils.convertViewDateString_To_DBDateString(
                                                  currentContentData + "", DBTablesPanel.getGeneralDBProperties().getViewDateFormat())
                                               + "', 'YYYY-MM-dd') AND ");
                  }
                  else
                     sqlStatementString.append(identifierQuoteString + currentDB_ColumnName
                                               + identifierQuoteString + "='"
                                               + currentContentData + "' AND ");
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

            // Oracle only provides a one time chance to obtain the result
            // set for LONG RAW fields so just collect all BLOB and RAW content.

            if (currentColumnTypeName.equals("BLOB") || currentColumnTypeName.indexOf("RAW") != -1)
               currentContentData = db_resultSet.getBytes(currentDB_ColumnName);
            else if (currentColumnTypeName.equals("BFILE"))
            {
               // BFILE edits not supported.
               continue;
            }
            else
               currentContentData = db_resultSet.getString(currentDB_ColumnName);
            // System.out.println(currentColumnName + " " + currentContentData);

            // Special content from other tables, ComboBoxes, maybe.
            // Also time, date or your special field formatting.

            if (((String) currentColumnName).equals("Your Special Field Name"))
               setSpecialFieldData(editForm, dbConnection, currentColumnName, currentContentData);

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

            // Timestamps Type Field
            else if (currentColumnTypeName.equals("TIMESTAMP"))
            {
               if (currentContentData != null)
               {
                  currentContentData = db_resultSet.getTimestamp(currentDB_ColumnName);
                  // System.out.println(currentContentData);
                  editForm.setFormField(currentColumnName,
                     (Object) (new SimpleDateFormat(DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                        + " HH:mm:ss").format(currentContentData)));
               }
               else
                  editForm.setFormField(currentColumnName,
                     (Object) (DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " HH:MM:SS"));
            }

            // Timestamps With Time Zone Type Fields
            else if (currentColumnTypeName.equals("TIMESTAMPTZ")
                     || currentColumnTypeName.equals("TIMESTAMP WITH TIME ZONE")
                     || currentColumnTypeName.equals("TIMESTAMPLTZ")
                     || currentColumnTypeName.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
            {
               if (currentContentData != null)
               {
                  if (currentColumnTypeName.equals("TIMESTAMPTZ")
                      || currentColumnTypeName.equals("TIMESTAMP WITH TIME ZONE")
                      || currentColumnTypeName.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                  {
                     currentContentData = db_resultSet.getTimestamp(currentDB_ColumnName);
                     // System.out.println(currentContentData);
                     editForm.setFormField(currentColumnName,
                        (Object) (new SimpleDateFormat(DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                           + " HH:mm:ss Z").format(currentContentData)));
                  }
                  else
                  {
                     String timestampString = (String) currentContentData;
                     
                     if (timestampString.indexOf(" ") != -1)
                        timestampString = displayMyDateString(
                                          timestampString.substring(0, timestampString.indexOf(" ")))
                                          + timestampString.substring(timestampString.indexOf(" "));
                     
                     editForm.setFormField(currentColumnName, (Object) timestampString);
                  }
               }
               else
                  editForm.setFormField(currentColumnName,
                     (Object) (DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " HH:MM:SS"));
            }

            // Blob & Raw Type Field
            else if (currentColumnTypeName.equals("BLOB") || currentColumnTypeName.indexOf("RAW") != -1)
            {
               String binaryType;
               if (currentColumnTypeName.indexOf("BLOB") != -1)
                  binaryType = "BLOB";
               else
                  binaryType = "RAW";

               if (currentContentData != null)
               {
                  int size = ((byte[]) currentContentData).length;

                  if (size != 0)
                     editForm.setFormField(currentColumnName, (Object) (binaryType
                                                               + " " + size + " Bytes"));
                  else
                     editForm.setFormField(currentColumnName, (Object) (binaryType
                                                               + " 0 Bytes"));

                  editForm.setFormFieldBlob(currentColumnName, (byte[]) currentContentData);
               }
               else
                  editForm.setFormField(currentColumnName, (Object) (binaryType + " NULL"));
            }

            // CLOB
            else if (currentColumnTypeName.indexOf("CLOB") != -1)
            {
               if (currentContentData != null)
               {
                  currentContentData = db_resultSet.getString(currentDB_ColumnName);
                  if (((String) currentContentData).getBytes().length != 0)
                  {
                     int size = ((String) currentContentData).getBytes().length;
                     editForm.setFormField(currentColumnName, (Object) ("CLOB " + size + " Bytes"));
                  }
                  else
                  {
                     editForm.setFormField(currentColumnName, (Object) "CLOB 0 Bytes");
                  }
                  editForm.setFormFieldText(currentColumnName, ((String) currentContentData));
               }
               else
                  editForm.setFormField(currentColumnName, (Object) "CLOB NULL");
            }

            // VARCHAR & LONG
            else if ((currentColumnClass.indexOf("String") != -1 &&
                     (!currentColumnTypeName.equals("CHAR") || !currentColumnTypeName.equals("NCHAR"))
                      && currentColumnSize > 255)
                     || (currentColumnClass.indexOf("String") != -1 && currentColumnTypeName.equals("LONG")))
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
         ConnectionManager.displaySQLErrors(e, "TableTabPanel_Oracle editSelectedItem()");
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
            ConnectionManager.displaySQLErrors(sqle, "TableTabPanel_Oracle editSelectedItem()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }
   
   //===============================================================
   // Class method to allow classes to set the table heading fields.
   //===============================================================

   public void setTableHeadings(ArrayList<String> newHeadingFields)
   {
      // Create connection, remove old summary table and
      // reload the center panel.

      Connection work_dbConnection = ConnectionManager.getConnection(
         "TableTabPanel_Oracle setTableHeadings()");
      
      if (work_dbConnection == null)
         return;

      centerPanel.remove(tableScrollPane);
      currentTableHeadings = newHeadingFields;
      sqlTableFieldsString = "";

      // Reconstitute the table field names.

      Iterator<String> headings = currentTableHeadings.iterator();

      while (headings.hasNext())
      {
         sqlTableFieldsString += identifierQuoteString + columnNamesHashMap.get(headings.next())
                                 + identifierQuoteString + ", ";
      }
      // No fields, just load empty table else
      // clean up and load.
      if (sqlTableFieldsString.equals(""))
         tableData = new Object[0][0];
      else
      {
         sqlTableFieldsString = sqlTableFieldsString.substring(0, sqlTableFieldsString.length() - 2);
         //sqlTableFieldsStringLTZ = sqlTableFieldsStringLTZ.substring(0, sqlTableFieldsStringLTZ.length() - 2);
         loadTable(work_dbConnection);
      }
      
      tableModel = new TableModel(currentTableHeadings, tableData);
      tableModel.setValues(tableData);

      listTable = new JTable(tableModel);
      listTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      listTable.getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME),
                                   TransferHandler.getCopyAction());
      listTable.getActionMap().put(TransferHandler.getPasteAction().getValue(Action.NAME),
                                   TransferHandler.getPasteAction());
      listTable.addMouseListener(summaryTablePopupListener);

      // Sizing columns
      headings = currentTableHeadings.iterator();
      TableColumn column = null;

      int i = 0;
      while (headings.hasNext())
      {
         Object currentHeading = headings.next();
         column = listTable.getColumnModel().getColumn(i++);
         column.setPreferredWidth((preferredColumnSizeHashMap.get(currentHeading)).intValue());
      }

      tableScrollPane = new JScrollPane(listTable);
      
      tableScrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, activateAdvancedSortSearchButton);
      tableScrollPane.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, activateUpdateButton);
      
      tableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      tableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
      
      centerPanel.add(sqlTable, tableScrollPane);
      centerCardLayout.show(centerPanel, sqlTable);

      ConnectionManager.closeConnection(work_dbConnection, "TableTabPanel_Oracle setTableHeadings()");
   }
}
