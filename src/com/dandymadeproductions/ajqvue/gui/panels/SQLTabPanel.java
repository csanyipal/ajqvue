//=================================================================
//                        SQL Tab Panel
//=================================================================
//
//    This class provides the view of resultant data/results from
// the direct input of SQL commands executed on the database.
//
//                    << SQLTabPanel.java >>
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
// Version 1.0 Production SQLTabPanel Class.
//        
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.math.BigDecimal;
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

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.Main_MouseAdapter;
import com.dandymadeproductions.ajqvue.gui.QueryFrame;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;
import com.dandymadeproductions.ajqvue.utilities.TableSorter;

/**
 *    The SQLTabPanel class provides the view of resultant data/results
 * from the direct input of SQL commands executed on the database.  
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class SQLTabPanel extends JPanel implements ActionListener, Printable
{
   // Class Instances.
   private static final long serialVersionUID = 8461814212715169033L;
   
   private String sqlString;
   private boolean validQuery;

   private int tableRowLimit;
   private int columnSizeScaling;
   private String dataSourceType;
   
   private ArrayList<String> tableHeadings;
   private HashMap<String, String> columnNamesHashMap;
   private HashMap<String, String> columnClassHashMap;
   private HashMap<String, String> columnTypeHashMap;
   private HashMap<String, Integer> columnSizeHashMap;
   private HashMap<String, Integer> preferredColumnSizeHashMap;
   private AResourceBundle resourceBundle;
   
   private JPanel centerPanel;
   private transient MouseListener summaryTablePopupListener;

   private SQLTableModel tableModel;
   private JTable listTable;
   private JScrollPane tableScrollPane;
   
   private static final String ACTION_SELECT_ALL = "Select All";
   private static final String ACTION_DESELECT_ALL = "DeSelect All";
   private static final int MAX_PREFERRED_COLUMN_SIZE = 350;
   
   //==============================================================
   // SQLTabPanel Constructor
   //==============================================================

   public SQLTabPanel(String sqlString, int queryRowLimit, AResourceBundle resourceBundle)
   {
      this.sqlString = sqlString;
      tableRowLimit = queryRowLimit;
      this.resourceBundle = resourceBundle;
      
      // Setting up a data source name qualifier and other
      // instances.
      
      dataSourceType = ConnectionManager.getDataSourceType();
      validQuery = false;
      
      tableModel = new SQLTableModel();
      tableHeadings = new ArrayList <String>();
      columnNamesHashMap = new HashMap <String, String>();
      columnClassHashMap = new HashMap <String, String>();
      columnTypeHashMap = new HashMap <String, String>();
      columnSizeHashMap = new HashMap <String, Integer>();
      preferredColumnSizeHashMap = new HashMap <String, Integer>();
      
      if (Ajqvue.getGeneralProperties().getFontSize() > 16)
         columnSizeScaling = (Ajqvue.getGeneralProperties().getFontSize() - 12) + 8;
      else
         columnSizeScaling = 11;
      
      // General Panel Configurations
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createRaisedBevelBorder());
      centerPanel = new JPanel(new BorderLayout());
      
      // Connecting to the database to execute the input
      // sql to see if a valid table can be loaded.
      
      try
      {
         executeSQL();
      }
      catch (SQLException e)
      {
         String errorString = "SQLException: " + e.getMessage() + " " + "SQLState: " 
                              + e.getSQLState() + " " + "VendorError: " + e.getErrorCode();
         QueryFrame.setQueryResultTextArea(errorString);
      }
      
      // ==================================================
      // Setting up the Summary Table View.
      // ==================================================

      if (validQuery)
      {
         TableSorter tableSorter = new TableSorter(tableModel);
         listTable = new JTable(tableSorter);
         tableSorter.setTableHeader(listTable.getTableHeader());
         listTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
         listTable.getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME),
                                         TransferHandler.getCopyAction());
         createListTablePopupMenu();
         listTable.addMouseListener(summaryTablePopupListener);

         // Sizing columns
         Iterator<String> headings = tableHeadings.iterator();
         TableColumn column = null;
         int i = 0;

         while (headings.hasNext())
         {
            column = listTable.getColumnModel().getColumn(i++);
            column.setPreferredWidth((preferredColumnSizeHashMap.get(headings.next())).intValue());
         }

         // Create a scrollpane for the table.
         
         tableScrollPane = new JScrollPane(listTable);
         tableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
         centerPanel.add(tableScrollPane, BorderLayout.CENTER);
      }

      add(centerPanel, BorderLayout.CENTER);
      addMouseListener(Ajqvue.getPopupMenuListener()); 
   }
   
   //==============================================================
   // ActionEvent Listener method for detecting the inputs from
   // the panel and directing to the appropriate routine.
   //=============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object panelSource = evt.getSource();

      if ((panelSource instanceof JButton || panelSource instanceof JMenuItem) && validQuery)
      {
         // listTable Popup Menu Actions
         if (panelSource instanceof JMenuItem)
         {
            String actionCommand = ((JMenuItem) panelSource).getActionCommand();
            // System.out.println(actionCommand);

            if (actionCommand.equals(ACTION_SELECT_ALL))
               listTable.selectAll();
            else if (actionCommand.equals(ACTION_DESELECT_ALL))
               listTable.clearSelection();
            // Copy
            else if (actionCommand.equals((String)TransferHandler.getCopyAction().getValue(Action.NAME)))
            {
               Action a = listTable.getActionMap().get(actionCommand);
               if (a != null)
                  a.actionPerformed(new ActionEvent(listTable, ActionEvent.ACTION_PERFORMED, null));
            }
         }
      }
   }
         
   //==============================================================
   // Class method to execute the given user's input SQL statement.
   //==============================================================

   private void executeSQL() throws SQLException
   {
      // Method Instances
      Connection dbConnection;
      String sqlStatementString;
      Statement sqlStatement;
      int updateCount;
      ResultSet db_resultSet;
      ResultSetMetaData tableMetaData;

      String colNameString;
      String columnClass;
      String columnTypeName;
      int columnType;
      int columnScale;
      int columnPrecision;
      int columnSize;
      int preferredColumnSize;
      Object currentContentData;
      Object[] rowData;
      
      // Checking to see if anything in the input to
      // execute.
      
      if (sqlString.length() < 1)
      {
         validQuery = false;
         return;
      }

      // Setting up a connection.
      dbConnection = ConnectionManager.getConnection("SQLTabPanel executeSQL()");
      
      if (dbConnection == null)
      {
         validQuery = false;
         return;
      }
      
      // Connecting to the data base, to obtain
      // meta data, and column names.
      
      sqlStatement = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();
         sqlStatement.setMaxRows(tableRowLimit);

         sqlStatementString = sqlString;
         // System.out.println(sqlStatementString);
         
         sqlStatement.execute(sqlStatementString);
         updateCount = sqlStatement.getUpdateCount();
         
         // Collect results.
         if (updateCount == -1)
         {
            db_resultSet = sqlStatement.getResultSet();
            
            // Check to see if there are any results.
            
            if (db_resultSet == null)
            {
               // Fill information instances.
               colNameString = "Result";
               columnClass = "java.lang.String";
               columnTypeName = "VARCHAR";
               columnSize = 30;
               
               tableHeadings.add(colNameString);
               columnNamesHashMap.put(colNameString, colNameString);
               columnClassHashMap.put(colNameString, columnClass);
               columnTypeHashMap.put(colNameString, columnTypeName.toUpperCase(Locale.ENGLISH));
               columnSizeHashMap.put(colNameString, Integer.valueOf(columnSize));
               preferredColumnSizeHashMap.put(colNameString,
                                              Integer.valueOf(colNameString.length() * columnSizeScaling));
               
               // Load table model.
               tableModel.setHeader(tableHeadings.toArray());
               
               // Set data.
               rowData = new Object[1];
               rowData[0] = "(" + resourceBundle.getResourceString("SQLTabPanel.label.Empty", "Empty") + ")";
               tableModel.addRow(rowData);  
               
               validQuery = true;
               return;
            }
            
            // Have results so setting Up the column names, and collecting
            // information about columns.
            
            tableMetaData = db_resultSet.getMetaData();
            
            // System.out.println("SQLTabPanel executeSQL()\n"
            //                    + "index" + "\t" + "Name" + "\t" + "Class" + "\t"
            //                    + "Type" + "\t" + "Type Name" + "\t" + "Scale"
            //                    + "\t" + "Precision" + "\t" + "Size");
            
            for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
            {
               colNameString = tableMetaData.getColumnLabel(i);
               columnClass = tableMetaData.getColumnClassName(i);
               columnType = tableMetaData.getColumnType(i);
               columnTypeName = tableMetaData.getColumnTypeName(i);
               columnScale = tableMetaData.getScale(i);
               columnPrecision = tableMetaData.getPrecision(i);
               columnSize = tableMetaData.getColumnDisplaySize(i);
               
               // System.out.println(i + "\t" + colNameString + "\t" +
               //                    columnClass + "\t" + columnType + "\t" +
               //                    columnTypeName + "\t" + columnScale + "\t" +
               //                    columnPrecision + "\t" + columnSize);

               // This going to be a problem so skip these columns.
               // NOT TESTED. This is still problably not going to
               // help. Bound to crash later.

               if ((columnClass == null && columnTypeName == null)
                   ||(dataSourceType.equals(ConnectionManager.MSSQL)
                      && columnTypeName.toUpperCase(Locale.ENGLISH).equals("TIMESTAMP")))
                  continue;

               // Handle some Oracle data types that have a null
               // class type and possibly others.

               if (columnClass == null)
               {
                  if (columnTypeName.equals("BINARY_FLOAT")
                      && dataSourceType.equals(ConnectionManager.ORACLE))
                  {
                     columnClass = "java.lang.Float";
                     columnTypeName = "FLOAT";
                  }
                  else if (columnTypeName.equals("BINARY_DOUBLE")
                           && dataSourceType.equals(ConnectionManager.ORACLE))
                  {
                     columnClass = "java.lang.Double";
                     columnTypeName = "DOUBLE";
                  }
                  else
                     columnClass = columnTypeName;
               }

               tableHeadings.add(colNameString);
               columnNamesHashMap.put(colNameString, colNameString);
               columnClassHashMap.put(colNameString, columnClass);
               columnTypeHashMap.put(colNameString, columnTypeName.toUpperCase(Locale.ENGLISH));
               columnSizeHashMap.put(colNameString, Integer.valueOf(columnSize));
               preferredColumnSizeHashMap.put(colNameString,
                                              Integer.valueOf(colNameString.length() * columnSizeScaling));   
            }
            tableModel.setHeader(tableHeadings.toArray());
            
            // Try and Load the Data From the SQL Execution.
            
            int i = 0;
            int j = 0;
            rowData = new Object[tableHeadings.size()];

            while (db_resultSet.next())
            {
               Iterator<String> headings = tableHeadings.iterator();
               while (headings.hasNext())
               {
                  colNameString = headings.next();
                  columnClass = columnClassHashMap.get(colNameString);
                  columnTypeName = columnTypeHashMap.get(colNameString);
                  columnSize = (columnSizeHashMap.get(colNameString)).intValue();
                  preferredColumnSize = (preferredColumnSizeHashMap.get(colNameString)).intValue();

                  // System.out.println(i + " " + j + " " + colNameString + " " +
                  //                    columnClass + " " + columnType + " " +
                  //                    columnSize + " " + preferredColumnSize);

                  // Storing data appropriately. If you have some
                  // date or other formating, here is where you can
                  // take care of it.
                  
                  // =============================================
                  // BigDecimal
                  if (columnClass.indexOf("BigDecimal") != -1)
                  {
                     currentContentData = db_resultSet.getString(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                        rowData[j++] = new BigDecimal(currentContentData.toString());
                  }

                  // =============================================
                  // Date
                  else if (columnTypeName.equals("DATE"))
                  {
                     currentContentData = db_resultSet.getObject(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                     {
                        if (dataSourceType.equals(ConnectionManager.SQLITE))
                           currentContentData = db_resultSet.getDate(colNameString);
                        
                        rowData[j++] = new SimpleDateFormat(DBTablesPanel.getGeneralDBProperties()
                           .getViewDateFormat()).format(currentContentData);
                     }
                  }
                  
                  // =============================================
                  // Datetime Offset
                  else if (columnTypeName.equals("DATETIMEOFFSET"))
                  {
                     String dateString, timeString;
                     
                     currentContentData = db_resultSet.getString(colNameString);
                     
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                     {
                        dateString = currentContentData + "";
                        dateString = dateString.substring(0, (dateString.indexOf(" ")));
                        dateString = Utils.convertDBDateString_To_ViewDateString(dateString,
                           DBTablesPanel.getGeneralDBProperties().getViewDateFormat());
                        
                        timeString = currentContentData + "";
                        timeString = timeString.substring(timeString.indexOf(" "));
                        
                        rowData[j++] = dateString + timeString;
                     }
                  }

                  // =============================================
                  // Datetime
                  else if (columnTypeName.indexOf("DATETIME") != -1)
                  {
                     currentContentData = db_resultSet.getObject(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                        rowData[j++] = new SimpleDateFormat(DBTablesPanel.getGeneralDBProperties()
                           .getViewDateFormat() + " HH:mm:ss").format(currentContentData);
                  }
                  
                  // =============================================
                  // Time
                  else if (columnTypeName.equals("TIME"))
                  {
                     currentContentData = db_resultSet.getTime(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                        rowData[j++] = (new SimpleDateFormat("HH:mm:ss").format(currentContentData));
                  }
                  
                  // =============================================
                  // Time With Time Zone
                  else if (columnTypeName.equals("TIMETZ"))
                  {
                     currentContentData = db_resultSet.getTime(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                        rowData[j++] = (new SimpleDateFormat("HH:mm:ss z").format(currentContentData));
                  }

                  // =============================================
                  // Timestamps
                  else if (columnTypeName.equals("TIMESTAMP"))
                  {
                     currentContentData = db_resultSet.getTimestamp(colNameString);
                     // System.out.println(currentContentData);
                     
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                     {
                        // Old MySQL Database Requirement, 4.x.
                        if (dataSourceType.equals(ConnectionManager.MYSQL)
                            || dataSourceType.equals(ConnectionManager.MARIADB))
                        {
                           if (columnSize == 2)
                              rowData[j++] = (new SimpleDateFormat("yy").format(currentContentData));
                           else if (columnSize == 4)
                              rowData[j++] = (new SimpleDateFormat("MM-yy").format(currentContentData));
                           else if (columnSize == 6)
                              rowData[j++] = (new SimpleDateFormat("MM-dd-yy").format(currentContentData));
                           else if (columnSize == 8)
                              rowData[j++] = (new SimpleDateFormat("MM-dd-yyyy").format(currentContentData));
                           else if (columnSize == 10)
                              rowData[j++] = (new SimpleDateFormat("MM-dd-yy HH:mm")
                                    .format(currentContentData));
                           else if (columnSize == 12)
                              rowData[j++] = (new SimpleDateFormat("MM-dd-yyyy HH:mm")
                                    .format(currentContentData));
                           // All current coloumnSizes for MariaDB, MySQL > 5.0 Should be 19.
                           else
                              rowData[j++] = (new SimpleDateFormat(
                                 DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " HH:mm:ss")
                                    .format(currentContentData));
                        }
                        else if (dataSourceType.equals(ConnectionManager.SQLITE))
                        {
                           rowData[j++] = (new SimpleDateFormat(
                              DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " HH:mm:ss.SSS")
                                 .format(currentContentData));
                        }
                        else
                           rowData[j++] = (new SimpleDateFormat(
                              DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " HH:mm:ss")
                                 .format(currentContentData));  
                     }
                  }

                  else if (columnTypeName.equals("TIMESTAMPTZ")
                           || columnTypeName.equals("TIMESTAMP WITH TIME ZONE")
                           || columnTypeName.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                  {
                     currentContentData = db_resultSet.getTimestamp(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                        rowData[j++] = (new SimpleDateFormat(DBTablesPanel.getGeneralDBProperties()
                           .getViewDateFormat() + " HH:mm:ss z").format(currentContentData));
                  }

                  // =============================================
                  // Year
                  else if (columnTypeName.equals("YEAR"))
                  {
                     currentContentData = db_resultSet.getObject(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                     {
                        String displayYear = currentContentData + "";
                        displayYear = displayYear.trim();

                        if (displayYear.length() >= 4)
                           displayYear = displayYear.substring(0, 4);
                        
                        rowData[j++] = displayYear;
                     }
                  }

                  // =============================================
                  // Blob
                  else if (columnClass.indexOf("String") == -1 && columnTypeName.indexOf("BLOB") != -1)
                  {
                     if (columnSize == 255)
                        rowData[j++] = "Tiny Blob";
                     else if (columnSize == 65535)
                        rowData[j++] = "Blob";
                     else if (columnSize == 16777215)
                        rowData[j++] = "Medium Blob";
                     else if (columnSize > 16777215)
                        rowData[j++] = "Long Blob";
                     else
                        rowData[j++] = "Blob";
                  }
                  
                  //=============================================
                  // CLOB
                  else if (columnTypeName.indexOf("CLOB") != -1)
                  {
                     rowData[j++] = "Clob";
                  }

                  // =============================================
                  // BYTEA
                  else if (columnTypeName.equals("BYTEA"))
                  {
                     rowData[j++] = "Bytea";
                  }

                  // =============================================
                  // BINARY
                  else if (columnTypeName.indexOf("BINARY") != -1 || columnTypeName.indexOf("IMAGE") != -1)
                  {
                     rowData[j++] = "Binary";
                  }
                  
                  //=============================================
                  // RAW
                  else if (columnTypeName.indexOf("RAW") != -1)
                  {
                     rowData[j++] = "Raw";
                  }
                  
                  //=============================================
                  // IMAGE
                  else if (columnTypeName.indexOf("IMAGE") != -1)
                  {
                     rowData[j++] = "IMAGE";
                  }

                  // =============================================
                  // Boolean
                  else if (columnClass.indexOf("Boolean") != -1)
                  {
                     currentContentData = db_resultSet.getString(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                        rowData[j++] = currentContentData.toString();
                  }

                  // =============================================
                  // Bit
                  else if (columnTypeName.indexOf("BIT") != -1
                           && (dataSourceType.equals(ConnectionManager.MYSQL)
                               || dataSourceType.equals(ConnectionManager.MARIADB)))
                  {
                     currentContentData = db_resultSet.getString(colNameString);
                     
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                     {
                        String byteString = Byte.toString((db_resultSet.getByte(colNameString)));
                        rowData[j++] = Integer.toBinaryString(Integer.parseInt(byteString));  
                     }
                  }

                  // =============================================
                  // Text
                  else if (columnClass.indexOf("String") != -1
                           && !columnTypeName.equals("CHAR") && !columnTypeName.equals("NCHAR")
                           && columnSize > 255)
                  {
                     currentContentData = db_resultSet.getObject(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                     {
                        if (columnSize <= 65535)
                           rowData[j++] = (String) currentContentData;
                        else if (columnSize == 16777215)
                           rowData[j++] = ("Medium Text");
                        else
                        // (columnSize > 16777215)
                        {
                           if (dataSourceType.equals(ConnectionManager.MYSQL)
                               || dataSourceType.equals(ConnectionManager.MARIADB))
                              rowData[j++] = ("Long Text");
                           else
                           {
                              // Limit Table Cell Memory Usage.
                              if (((String) currentContentData).length() > 512)
                                 rowData[j++] = ((String) currentContentData).substring(0, 512);
                              else
                                 rowData[j++] = (String) currentContentData;
                           }
                        }   
                     }
                  }
                  
                  // =============================================
                  // LONG
                  else if (columnClass.indexOf("String") != -1 && columnTypeName.equals("LONG"))
                  {
                     currentContentData = db_resultSet.getObject(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                     {
                        // Limit Table Cell Memory Usage.
                        if (((String) currentContentData).length() > 512)
                           rowData[j++] = ((String) currentContentData).substring(0, 512);
                        else
                           rowData[j++] = (String) currentContentData;
                     }
                  }

                  // =============================================
                  // Array
                  else if ((columnClass.indexOf("Object") != -1 || columnClass.indexOf("Array") != -1)
                           && (columnTypeName.indexOf("_") != -1))
                  {
                     currentContentData = db_resultSet.getString(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                     {
                        String stringName = (String) currentContentData;

                        // Limit Table Cell Memory Usage.
                        if (stringName.length() > 512)
                           rowData[j++] = stringName.substring(0, 512);
                        else
                           rowData[j++] = stringName;
                     }
                  }

                  // =============================================
                  // Any Other
                  else
                  {
                     currentContentData = db_resultSet.getString(colNameString);
                     if (currentContentData == null)
                        rowData[j++] = "NULL";
                     else
                        rowData[j++] = currentContentData.toString().trim();
                  }

                  // Setup some sizing for the column in the summary table.
                  
                  if ((rowData[j - 1] + "").length() * columnSizeScaling > preferredColumnSize)
                  {
                     preferredColumnSize = (rowData[j - 1] + "").length() * columnSizeScaling;
                     if (preferredColumnSize > MAX_PREFERRED_COLUMN_SIZE)
                        preferredColumnSize = MAX_PREFERRED_COLUMN_SIZE;
                  }
                  preferredColumnSizeHashMap.put(colNameString, Integer.valueOf(preferredColumnSize));
               }
               tableModel.addRow(rowData);
               j = 0;
               i++;
            }
            db_resultSet.close();
            sqlStatement.close();
         }
         // No results, data, but was update.
         else
         {
            // Fill information instances.
            colNameString = "Update Count";
            columnClass = "java.lang.String";
            columnTypeName = "VARCHAR";
            columnSize = 30;
            
            tableHeadings.add(colNameString);
            columnNamesHashMap.put(colNameString, colNameString);
            columnClassHashMap.put(colNameString, columnClass);
            columnTypeHashMap.put(colNameString, columnTypeName.toUpperCase(Locale.ENGLISH));
            columnSizeHashMap.put(colNameString, Integer.valueOf(columnSize));
            preferredColumnSizeHashMap.put(colNameString,
                                           Integer.valueOf(colNameString.length() * columnSizeScaling));
            
            // Load table model.
            tableModel.setHeader(tableHeadings.toArray());
            
            rowData = new Object[1];
            rowData[0] = updateCount;
            tableModel.addRow(rowData);
         }
         
         // Looks good so validate.
         validQuery = true;
      }
      catch (SQLException e)
      {
         String errorString = "SQLException: " + e.getMessage() + " " + "SQLState: " 
                              + e.getSQLState() + " " + "VendorError: " + e.getErrorCode();
         QueryFrame.setQueryResultTextArea(errorString);
         validQuery = false;
         return;
      }
      finally
      {
         if (sqlStatement != null)
            sqlStatement.close();
         
         ConnectionManager.closeConnection(dbConnection, "SQLTabPanel executeSQL()");    
      }
   }
   
   //==============================================================
   // Class method to create the summary table view popup menu.
   //==============================================================

   private void createListTablePopupMenu()
   {
      // Method Instances.
      JPopupMenu summaryTablePopupMenu = new JPopupMenu();
      JMenuItem menuItem;
      String resource;
      
      // Summary Table select actions.
      
      resource = resourceBundle.getResourceString("SQLTabPanel.menu.SelectAll", "Select All");
      menuItem = new JMenuItem(resource);
      menuItem.setActionCommand(ACTION_SELECT_ALL);
      menuItem.addActionListener(this);
      summaryTablePopupMenu.add(menuItem);

      resource = resourceBundle.getResourceString("SQLTabPanel.menu.DeSelectAll", "DeSelect All");
      menuItem = new JMenuItem(resource);
      menuItem.setActionCommand(ACTION_DESELECT_ALL);
      menuItem.addActionListener(this);
      summaryTablePopupMenu.add(menuItem);
      
      // Summary Table copy/paste? actions
      
      summaryTablePopupMenu.addSeparator();
      
      resource = resourceBundle.getResourceString("SQLTabPanel.menu.Copy", "Copy");
      menuItem = new JMenuItem(resource);
      menuItem.setActionCommand((String)TransferHandler.getCopyAction().getValue(Action.NAME));
      menuItem.setMnemonic(KeyEvent.VK_C);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
      menuItem.addActionListener(this);
      summaryTablePopupMenu.add(menuItem);
      
      summaryTablePopupListener = new Main_MouseAdapter(summaryTablePopupMenu);
   }
    
   //==============================================================
   // Class Method to print the Panel's current information.
   //==============================================================

   public int print(Graphics g, PageFormat pageFormat, int pageIndex)
   {
      if (pageIndex > 0)
         return NO_SUCH_PAGE;
      Graphics2D g2 = (Graphics2D) g;
      g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

      RepaintManager currentManager = RepaintManager.currentManager(this);
      currentManager.setDoubleBufferingEnabled(false);
      final Rectangle rect = g2.getClipBounds();

      double scaleFactor = rect.getWidth() / this.getWidth();
      g2.scale(scaleFactor, scaleFactor);
      // pageFormat.setOrientation(PageFormat.LANDSCAPE);
      paintAll(g2);
      currentManager.setDoubleBufferingEnabled(true);
      return PAGE_EXISTS;
   }

   //==============================================================
   // Class method to allow classes to obtain the list of allowed
   // column names that is presently in the summary table.
   //==============================================================

   public ArrayList<String> getTableHeadings()
   {
      return tableHeadings;
   }

   //==============================================================
   // Class method to allow classes to obtain the summary list
   // table presently displayed in the tab.
   //==============================================================

   public JTable getListTable()
   {
      return listTable;
   }

   //==============================================================
   // Class method to allow classes to obtain the columnNamesHashMap.
   //==============================================================

   public HashMap<String, String> getColumnNamesHashMap()
   {
      return columnNamesHashMap;
   }

   //==============================================================
   // Class method to allow classes to obtain the columnClassHashMap.
   //==============================================================

   public HashMap<String, String> getColumnClassHashMap()
   {
      return columnClassHashMap;
   }

   //==============================================================
   // Class method to allow classes to obtain the columnTypeHashMap.
   //==============================================================

   public HashMap<String, String> getColumnTypeHashMap()
   {
      return columnTypeHashMap;
   }

   //==============================================================
   // Class method to allow classes to obtain the columnSizeHashMap.
   //==============================================================

   public HashMap<String, Integer> getColumnSizeHashMap()
   {
      return columnSizeHashMap;
   }
   
   //==============================================================
   // Class helper for the JTable, listTable, Table Model.
   //==============================================================
   
   static class SQLTableModel extends AbstractTableModel
   {
      private static final long serialVersionUID = 1229214973355124583L;
      private Object[] headers;
      private ArrayList<Object[]> rows;
      
      protected SQLTableModel()
      {
         // Just Intialize Class Instances.
         headers = new Object[0];
         rows = new ArrayList <Object[]>();
      }
      
      public void addRow(Object[] rowData)
      {
         Object[] currentRow = new Object[rowData.length];
         for (int i = 0; i < rowData.length; i++)
         {
            currentRow[i] = rowData[i];
         }
         //System.arraycopy(rowData, 0, row, 0, rowData.length);
         rows.add(currentRow);
      }
      
      public void clear(){rows.clear();}
      
      public String getColumnName(int i){return headers[i].toString();}
      public int getColumnCount(){return headers.length;}
      public ArrayList<Object[]> getData(){return rows;}
      public int getRowCount(){return rows.size();}
      public Object getValueAt(int row, int col)
      {
         if (row >= rows.size())
            return null;

         Object[] colArray = rows.get(row);

         if (col >= colArray.length)
            return null;
         
         return colArray[col];
      }
      public void setHeader(Object[] colNames)
      {
         headers = new Object[colNames.length];
         System.arraycopy(colNames, 0, headers, 0, colNames.length);
      }
   } 
}