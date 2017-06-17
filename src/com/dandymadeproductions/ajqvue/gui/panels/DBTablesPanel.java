//=================================================================
//                      DBTablesPanel
//=================================================================
//
//    This class provides the panel that holds all the database
// tables panels, aka. TableTabPanels. In addition the class
// also provides a common mechanism for getting those panels &
// other information about the database tables.
//
//                 << DBTablesPanel.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.1 06/17/2017
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
// Version 1.0 Production DBTablesPanel Class.
//         1.1 Class javadoc Constructor Comment Correction.
//                           
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.Main_Frame;
import com.dandymadeproductions.ajqvue.structures.DataExportProperties;
import com.dandymadeproductions.ajqvue.structures.DataImportProperties;
import com.dandymadeproductions.ajqvue.structures.GeneralDBProperties;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The DbTablesPanel class provides the panel that holds all the
 * database tables panels, aka. TableTabPanels. In addition the class
 * also provides a common mechanism for getting those panels &amp; other
 * information about the database tables.
 * 
 * @author Dana M. Proctor
 * @version 1.1 06/17/2017
 */

public class DBTablesPanel extends JPanel implements ActionListener
{
   // Class Instances
   private static final long serialVersionUID = -2513425626736158682L;
   
   private static final int tableTabPanelCardLimit = 10;
   private static LinkedList<String> tableCards = new LinkedList <String>();
   private static CardLayout tablesCardLayout = new CardLayout();
   private static JPanel tablesPanel = new JPanel(tablesCardLayout);
   
   private static JLabel statusIndicator = new JLabel("", JLabel.LEFT);
   private static JLabel statusLabel = new JLabel("Idle");
   private JButton sqlQueryBucketButton;
   private static JComboBox<Object> tableSelectionComboBox = new JComboBox<Object>();
   private static HashMap<String, TableTabPanel> tableTabHashMap = new HashMap <String, TableTabPanel>();
   private static boolean disableActions = true;
   private static long statusTimer;
   private volatile static boolean stopStatusDelayThread;
   
   private static GeneralDBProperties generalDBProperties = new GeneralDBProperties();
   private static DataImportProperties dataImportProperties = new DataImportProperties();
   private static DataExportProperties dataExportProperties = new DataExportProperties();

   //==============================================================
   // DBTablesPanel Constructor
   //==============================================================

   public DBTablesPanel(Connection dbConnection, ArrayList<String> tableNames)
   {  
      // Constructor Instances
      ImageIcon statusIdleIcon, statusWorkingIcon, sqlQueryBucketIcon;
      JPanel statusControlPanel, statusPanel;
      AResourceBundle resourceBundle;
      String iconsDirectory, tableName;

      // Initializing & setting up the panel.
      
      resourceBundle = Ajqvue.getResourceBundle();
      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();
      
      statusIdleIcon = resourceBundle.getResourceImage(iconsDirectory + "statusIdleIcon.png");
      statusWorkingIcon = resourceBundle.getResourceImage(iconsDirectory + "statusWorkingIcon.png");
      sqlQueryBucketIcon = resourceBundle.getResourceImage(iconsDirectory + "addSQLQueryIcon.png");
      
      setLayout(new BorderLayout());
      
      // ===============================================
      // Create the status indicator, SQL Query Bucket,
      // and table selection comboBox components. While
      // at it load the first table that is available.
      
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();
      
      statusControlPanel = new JPanel(gridbag);
      
      // Status Indicator
      statusPanel = new JPanel(gridbag);
      statusPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
                                                               BorderFactory.createEmptyBorder(0, 2, 0, 1)));
      
      statusIndicator.setIcon(statusIdleIcon);
      statusIndicator.setDisabledIcon(statusWorkingIcon);
      statusIndicator.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
      
      Utils.buildConstraints(constraints, 0, 0, 1, 1, 20, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(statusIndicator, constraints);
      statusPanel.add(statusIndicator);
      
      statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      statusLabel.setPreferredSize(new Dimension(getFontMetrics(getFont()).stringWidth("1000.00 Seconds"),
    		                                     statusLabel.getPreferredSize().height));
      
      Utils.buildConstraints(constraints, 1, 0, 1, 1, 80, 100);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(statusLabel, constraints);
      statusPanel.add(statusLabel);
      
      Utils.buildConstraints(constraints, 0, 0, 1, 1, 6, 100);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(statusPanel, constraints);
      statusControlPanel.add(statusPanel);
      
      // SQL Query Bucket Drop Button.
      sqlQueryBucketButton = new JButton(sqlQueryBucketIcon);
      sqlQueryBucketButton.setMargin(new Insets(0, 0, 0, 0));
      sqlQueryBucketButton.setBorder(BorderFactory.createCompoundBorder(
                                                    BorderFactory.createRaisedBevelBorder(),
                                                    sqlQueryBucketButton.getBorder()));
      sqlQueryBucketButton.setFocusPainted(false);
      sqlQueryBucketButton.addActionListener(this); 
      
      Utils.buildConstraints(constraints, 1, 0, 1, 1, 1, 100);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(sqlQueryBucketButton, constraints);
      statusControlPanel.add(sqlQueryBucketButton);
      
      // Table Selector.
      tableSelectionComboBox.setBorder(BorderFactory.createRaisedBevelBorder());
      tableSelectionComboBox.addActionListener(this);
      tableSelectionComboBox.setEnabled(false);
      
      if (!tableNames.isEmpty())
      {
         //tableSelectionComboBox.setEnabled(false);
         
         Iterator<String> tableNamesIterator = tableNames.iterator();
         while (tableNamesIterator.hasNext())
            tableSelectionComboBox.addItem(tableNamesIterator.next());
         
         // Create the summary table of the first table in the
         // tables list of the database. All others are loaded
         // on the fly as needed.

         tableName = (String) tableSelectionComboBox.getItemAt(0);
         loadTable(tableName, dbConnection);
         
         tableSelectionComboBox.setEnabled(true);
      }
      
      Utils.buildConstraints(constraints, 2, 0, 1, 1, 93, 100);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(tableSelectionComboBox, constraints);
      statusControlPanel.add(tableSelectionComboBox);
      
      add(statusControlPanel, BorderLayout.NORTH);
      
      // ===================================================
      // Add center panel that holds the TableTabPanel(s).
      
      add(tablesPanel, BorderLayout.CENTER);
      
      disableActions = false;
   }

   //==============================================================
   // ActionEvent Listener method for detecting the inputs from the
   // panel and directing to the appropriate routine.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object panelSource = evt.getSource();
      
      // Droping the selected summary table table SQL statement
      // to the SQL Query Bucket.

      if (panelSource instanceof JButton && !disableActions)
      {
         if (panelSource == sqlQueryBucketButton)
         {
            TableTabPanel selectedTableTabPanel = getSelectedTableTabPanel();
            
            if (selectedTableTabPanel != null)
            {
               Main_Frame.getSQLBucket().addSQLStatement(selectedTableTabPanel.getTableSQLStatement());
            }
         }
      }
     
      // Setting current Panel card to the selected table.

      if (panelSource instanceof JComboBox && !disableActions)
      {
         String tableName;
          
         if (panelSource == tableSelectionComboBox)
         {
            tableName = (String) tableSelectionComboBox.getSelectedItem();
            
            // If table already loaded just show else
            // have to load it via a background thread.
            // Provide the status indicator update also.
            
            if (tableCards.contains(tableName))
               tablesCardLayout.show(tablesPanel, tableName);
            else
            {
               Thread loadTableThread = new Thread(new LoadTableThread(),
                  "DBTablesPanel actionPerformed() loadTableThread");
               loadTableThread.start();
            } 
         }
      }
   }
   
   //==============================================================
   // Inner Class to handle loading tables in the background.
   //==============================================================
   
   private static class LoadTableThread implements Runnable
   {
      public void run()
      {
         Connection work_dbConnection = ConnectionManager.getConnection(
            "DBTablesPanel actionPerformed()");
         
         if (work_dbConnection == null)
            return;
            
         startStatusTimer();
         
         String tableName = (String) tableSelectionComboBox.getSelectedItem();
         loadTable(tableName, work_dbConnection);
         tablesCardLayout.show(tablesPanel, tableName);
            
         ConnectionManager.closeConnection(work_dbConnection, "DBTablesPanel actionPerformed()");
         stopStatusTimer(); 
      } 
   }

   //==============================================================
   // Class Method to load a new table into the panel's cardlayout.
   //==============================================================

   private synchronized static void loadTable(String tableName, Connection dbConnection)
   {
      // Method Instances
      String dataSourceType;
      TableTabPanel tableTabPanel;
      
      dataSourceType = ConnectionManager.getDataSourceType();

      // MySQL
      if (dataSourceType.equals(ConnectionManager.MYSQL))
         tableTabPanel = new TableTabPanel_MySQL(tableName, dbConnection, false);
      // MariaDB
      else if (dataSourceType.equals(ConnectionManager.MARIADB))
         tableTabPanel = new TableTabPanel_MySQL(tableName, dbConnection, false);
      // PostgreSQL
      else if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
         tableTabPanel = new TableTabPanel_PostgreSQL(tableName, dbConnection, false);
      // HSQL
      else if (dataSourceType.indexOf(ConnectionManager.HSQL) != -1)
         tableTabPanel = new TableTabPanel_HSQL(tableName, dbConnection, false);
      // Oracle
      else if (dataSourceType.equals(ConnectionManager.ORACLE))
         tableTabPanel = new TableTabPanel_Oracle(tableName, dbConnection, false);
      // SQLite
      else if (dataSourceType.equals(ConnectionManager.SQLITE))
         tableTabPanel = new TableTabPanel_SQLite(tableName, dbConnection, false);
      // MS Access
      else if (dataSourceType.equals(ConnectionManager.MSACCESS))
         tableTabPanel = new TableTabPanel_MSAccess(tableName, dbConnection, false);
      // MSSQL
      else if (dataSourceType.equals(ConnectionManager.MSSQL))
         tableTabPanel = new TableTabPanel_MSSQL(tableName, dbConnection, false);
      // Apache Derby
      else if (dataSourceType.equals(ConnectionManager.DERBY))
         tableTabPanel = new TableTabPanel_Derby(tableName, dbConnection, false);
      // Generic
      else
         tableTabPanel = new TableTabPanel_Generic(tableName, dbConnection, false);

      if (tableTabPanel.getTableFields() != null)
      {
         tableTabPanel.addMouseListener(Ajqvue.getPopupMenuListener());
         tablesPanel.add(tableName, tableTabPanel);
         tableTabHashMap.put(tableName, tableTabPanel);
         tableCards.addFirst(tableName);
         
         // Control the number of cards that are added to the panel.
         if (tableCards.size() > tableTabPanelCardLimit)
         {
            TableTabPanel tableTabPanelToRemove;
            String tableNametoRemove;
            
            tableNametoRemove = tableCards.removeLast();
            tableTabPanelToRemove = tableTabHashMap.get(tableNametoRemove);
            
            if (tableTabPanelToRemove != null)
            {
               tablesPanel.remove(tableTabPanelToRemove);
               tablesPanel.validate();
            }
            tableTabHashMap.remove(tableNametoRemove);
         }
      }
   }
   
   //==============================================================
   // Class Method to reset the panel's table selector comboBox
   // and table cards then reload tables.
   //==============================================================
   
   public static void reloadPanel(Connection dbConnection, ArrayList<String> tableNames)
   {
      // Method Instances
      String tableName;
      
      // Insure no actions are taken during reload.
      tableSelectionComboBox.setEnabled(false);
      disableActions = true;
      
      // Clear the components of old data.
      statusLabel.setEnabled(true);
      tableSelectionComboBox.removeAllItems();
      tableTabHashMap.clear();
      tableCards.clear();
      tablesPanel.removeAll();
      tablesPanel.setLayout(tablesCardLayout);
       
      // Try reloading tables.
      if (!tableNames.isEmpty())
      {
         Iterator<String> tableNamesIterator = tableNames.iterator();
         while (tableNamesIterator.hasNext())
            tableSelectionComboBox.addItem(tableNamesIterator.next());
         
         tableSelectionComboBox.setEnabled(true);
          
         // Create the summary table of the first table in the
         // tables list of the database. All others are loaded
         // on the fly as needed.

         tableName = (String) tableSelectionComboBox.getItemAt(0);
         loadTable(tableName, dbConnection);
      } 
      disableActions = false;
   }
   
   //==============================================================
   // Class Method to start the status label timer, statusLabel set
   // to working.
   //==============================================================

   public static void startStatusTimer()
   {
      // Method Instances
      java.util.Date startDate;
      
      // Collect and create start timer data.
      startDate = new java.util.Date();
      statusTimer = startDate.getTime();
      
      // Set status.
      stopStatusDelayThread = true;
      statusIndicator.setEnabled(false);
      statusLabel.setText("Working");
   }
   
   //==============================================================
   // Class Method to stop the status label timer, statusLabel set
   // to time difference between start and stop, then to Idle after
   // 3 seconds.
   //==============================================================

   public static void stopStatusTimer()
   {
      // Method Instances
      java.util.Date stopDate;
      String statusLabelString;
      
      // Collect and create timer data.
      stopDate = new java.util.Date();
      statusTimer = (stopDate.getTime() - statusTimer);
      
      statusLabelString = Utils.nDigitChop((statusTimer * 0.001), 2) + " Seconds";
      
      if (statusLabelString.length() > 13)
         statusLabelString = statusLabelString.substring(0, 12);
         
      // Set status.
      statusIndicator.setEnabled(true);
      statusLabel.setText(statusLabelString);
         
      // Display the time for 3 seconds then set
      // label to Idle.
      Thread statusDelayThread = new Thread(new Runnable()
      {
         long timeSlice, totalElaspedTime, totalDelay; //milliseconds
         
         public void run()
         {
            timeSlice = 500;
            totalDelay = 2500;
            totalElaspedTime = 0;
            stopStatusDelayThread = false;
            
            try
            {
               while (totalElaspedTime <= totalDelay)
               {
                  if (!stopStatusDelayThread)
                  {
                     Thread.sleep(timeSlice);
                     totalElaspedTime += timeSlice;
                  }
                  else
                     return;
               }
               statusLabel.setText("Idle");
            }
            catch (InterruptedException e) {}
         }
      }, "DBTablesPanel.statusDelayThread");
      statusDelayThread.start();
   }
   
   //==============================================================
   // Class Method to return the current DataImportProperties.
   //==============================================================

   public static DataImportProperties getDataImportProperties()
   {
      return dataImportProperties;
   }

   //==============================================================
   // Class Method to return the current DataExportProperties.
   //==============================================================

   public static DataExportProperties getDataExportProperties()
   {
      return dataExportProperties;
   }
   
   //==============================================================
   // Class Method to return the current GeneralDBProperties.
   //==============================================================

   public static GeneralDBProperties getGeneralDBProperties()
   {
      return generalDBProperties;
   }
   
   //==============================================================
   // Class Method to return the current selected visible
   // TableTabPanel in the panel.
   //==============================================================

   public static TableTabPanel getSelectedTableTabPanel()
   {  
      TableTabPanel selectedTableTabPanel;
      
      // Insure the DB Panel is not empty or null selection.
      
      selectedTableTabPanel = tableTabHashMap.get(tableSelectionComboBox.getSelectedItem());
      
      if (tableSelectionComboBox.getItemCount() != 0  && selectedTableTabPanel != null)
         return selectedTableTabPanel;
      else
         return null;
   }
   
   //==============================================================
   // Class Method to return the current number of tables in the
   // database that the user has access to.
   //==============================================================

   public static int getTableCount()
   {
      if (tableSelectionComboBox.getItemCount() == 0)
         return 0;
      else
         return tableSelectionComboBox.getItemCount();
   }
   
   //==============================================================
   // Class Method to get a TableTabPanel given the table
   // tab name, aka table name. Load it if necessary via selecting
   // by the tableSelectionComboBox.
   //==============================================================
   
   public synchronized static TableTabPanel getTableTabPanel(String tableName)
   {
      // Table not loaded so load it.
      if (tableTabHashMap.get(tableName) == null)
      {
         Connection work_dbConnection = ConnectionManager.getConnection(
            "DBTablesPanel getTableTabPanel()");
         
         loadTable(tableName, work_dbConnection);
         
         ConnectionManager.closeConnection(work_dbConnection, "DBTablesPanel getTableTabPanel()");
      }

      if (tableTabHashMap.get(tableName) == null)
         return null;
      else
         return tableTabHashMap.get(tableName);
   }
   
   //==============================================================
   // Class Method to set the DataImportProperties.
   //==============================================================

   public static void setDataImportProperties(DataImportProperties newDataProperties)
   {
      dataImportProperties = newDataProperties;
   }

   //==============================================================
   // Class Method to set the DataExportProperties.
   //==============================================================

   public static void setDataExportProperties(DataExportProperties newDataProperties)
   {
      dataExportProperties = newDataProperties;
   }
   
   //==============================================================
   // Class Method to set the GeneralDBProperties.
   //==============================================================

   public static void setGeneralDBProperties(GeneralDBProperties newGeneralDBProperties)
   {
      // Method Instances
      TableTabPanel currentTableTabPanel;
      
      // Set the properties
      generalDBProperties = newGeneralDBProperties;
      
      // Refresh the table panel(s) to reflect the changes.
      
      currentTableTabPanel = getSelectedTableTabPanel();
      currentTableTabPanel.refreshButton.doClick();
   }
   
   //==============================================================
   // Class Method to set a TableTabPanel given the table tab name,
   // aka table name. Load it if necessary via selecting by the
   // tableSelectionComboBox.
   //==============================================================

   public synchronized static void setSelectedTableTabPanel(String tableName)
   {
      // Method Instances.
      boolean validTable;
      
      // Check to insure the table exists in the loaded tables.
      validTable = false;
      
      for (int i=0; i<tableSelectionComboBox.getItemCount(); i++)
         if (tableSelectionComboBox.getItemAt(i).equals(tableName))
            validTable = true;
      
      // Show that TableTabPanel 
      if (validTable)
      {
         // Table not loaded so load it.
         if (tableTabHashMap.get(tableName) == null)
         {
            String connectionString = "DBTablesPanel setSelectedTableTabPanel()";
            Connection work_dbConnection = ConnectionManager.getConnection(connectionString);
            
            loadTable(tableName, work_dbConnection);
            
            ConnectionManager.closeConnection(work_dbConnection, connectionString);
         }
         
         if (tableTabHashMap.get(tableName) == null)
            return;
         else
            tableSelectionComboBox.setSelectedItem(tableName);
      }
   }
}
