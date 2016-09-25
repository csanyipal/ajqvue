//=================================================================
//           TableRecordCount TableRecordCountPanel
//=================================================================
//
//    This class provides the panel that holds components
// associated with Ajqvue basic tutorial for a plugin module.
//
//               << TableRecordCountPanel.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.0 09/21/2016
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
// Version 1.0 Production TableFieldChartsPanel Class.
//                           
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.tablerecordcount;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.gui.panels.TableTabPanel;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The TableRecordCountPanel class provides the panel that holds
 * components associated with the Ajqvue basic tutorial for a
 * plugin module.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/21/2016
 */

class TableRecordCountPanel extends JPanel implements ActionListener
{
   // Class Instances.
   private static final long serialVersionUID = 2500935698652883672L;
   
   private ImageIcon tabIcon;
   private JComboBox<Object> tableSelectionComboBox;
   private JLabel recordCountLabel;
   private boolean disableActions;
   
   private final static String VERSION = "Version 1.0";

   //==============================================================
   // TableRecordCountPanel Constructor
   //==============================================================

   TableRecordCountPanel(String path, ArrayList<String> tableNames)
   {
      // Method Instances.
      AResourceBundle resourceBundle;
      String pathDirectory, localeDirectory, imagesDirectory, resource;
      JLabel tableNameLabel;
      
      // Setup the plugin's components, do not
      // use local file system's file separator!
      
      // file & http, locale resource not in jar
      //pathDirectory = path + "/" + "TableRecordCount" + "/";
      //localeDirectory = "locale/";
      //imagesDirectory = "images/icons/";
      
      // file & http, locale resource in jar
      pathDirectory = path + "/" + "TableRecordCount.jar";
      localeDirectory = "lib/plugins/TableRecordCount/locale/";
      imagesDirectory = "lib/plugins/TableRecordCount/images/icons/";
      
      resourceBundle = new AResourceBundle(pathDirectory, true, true);
      resourceBundle.setLocaleResource(localeDirectory, "TableRecordCount", Ajqvue.getLocaleString());
      
      tabIcon = resourceBundle.getResourceImage(imagesDirectory + "tabIcon.png");

      // Create the components to select the database table,
      // identify the record count and the actual processed
      // row number.

      // Table Selector.
      tableSelectionComboBox = new JComboBox<Object>();
      tableSelectionComboBox.setEnabled(false);

      if (!tableNames.isEmpty())
      {
         Iterator<String> tableNamesIterator = tableNames.iterator();
         while (tableNamesIterator.hasNext())
            tableSelectionComboBox.addItem(tableNamesIterator.next());

         tableSelectionComboBox.setEnabled(true);
      }
      tableSelectionComboBox.addActionListener(this);
      add(tableSelectionComboBox);

      // Identify Count.
      resource = resourceBundle.getResourceString("TableRecordCountPanel.label.RecordCount",
                                                  "Record Count");
      tableNameLabel = new JLabel(resource + ": ");
      add(tableNameLabel);

      // Record Count Value.
      recordCountLabel = new JLabel("0");
      
      add(recordCountLabel);
   }

   //==============================================================
   // ActionEvent Listener method for detecting the selection of
   // a table from the tableSelectionComboBox so that the row,
   // record count can be processed.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object panelSource = evt.getSource();

      if (panelSource instanceof JComboBox && !disableActions)
      {
         Thread actionThread = new Thread(new Runnable()
         {
            public void run()
            {
               executeRecordCount();
            }
         }, "TableRecordCountPanel.actionThread");
         actionThread.start();
      }
   }
   
   //==============================================================
   // Class Method to execute the required processing the selected
   // database table to determine the record count.
   //==============================================================

   private void executeRecordCount()
   {
      // Setup Instances.
      Connection dbConnection;
      String tableName;
      String schemaTableName;

      String sqlStatementString;
      Statement sqlStatement;
      ResultSet rs;

      int rowCount = 0;
      tableSelectionComboBox.setEnabled(false);
      disableActions = true;

      // Get Connection to Database.

      dbConnection = ConnectionManager.getConnection("TableRecordCountPanel executeRecordCount()");

      try
      {
         // Create a statement object to be used with the connection.

         sqlStatement = dbConnection.createStatement();

         // Collect the selected table from the combobox.

         tableName = (String) tableSelectionComboBox.getSelectedItem();
         // System.out.println(tableName);

         // Collect the table name as referenced in the database.
         TableTabPanel selectedTablePanel = DBTablesPanel.getTableTabPanel(tableName);

         if (selectedTablePanel == null)
            return;

         tableName = selectedTablePanel.getTableName();
         // System.out.println(tableName);

         // Collect the properly formatted schema table name with identifier
         // quotes for execution in a SQL statement. 
         
         schemaTableName = Utils.getSchemaTableName(tableName);
         // System.out.println(schemaTableName);

         // Setup the statement string and execute the database query.

         sqlStatementString = "SELECT COUNT(*) FROM " + schemaTableName;
         rs = sqlStatement.executeQuery(sqlStatementString);
         rs.next();
         rowCount = rs.getInt(1);

         // Set the record count label to reflect the results
         // and close out.

         recordCountLabel.setText(Integer.toString(rowCount));

         rs.close();
         sqlStatement.close();
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableRecordCountPanel executeRecordCount()");
         recordCountLabel.setText("0");
         tableSelectionComboBox.setEnabled(true);
         disableActions = false;
      }
      
      // Close connection to database.
      
      ConnectionManager.closeConnection(dbConnection, "TableRecordCountPanel executeRecordCount()");
      tableSelectionComboBox.setEnabled(true);
      disableActions = false;
   }

   //==============================================================
   // Class Method to reset the panel's table selector comboBox
   // if Ajqvue is called to reload the database tables.
   //==============================================================

   protected void reloadPanel(ArrayList<String> tableNames)
   {
      // Insure no actions are taken during reload.

      tableSelectionComboBox.setEnabled(false);
      disableActions = true;

      // Clear the components of old data.

      tableSelectionComboBox.removeAllItems();
      recordCountLabel.setText("0");

      // Try reloading tables.
      if (!tableNames.isEmpty())
      {
         Iterator<String> tableNamesIterator = tableNames.iterator();
         while (tableNamesIterator.hasNext())
         {
            tableSelectionComboBox.addItem(tableNamesIterator.next());
         }
         tableSelectionComboBox.setEnabled(true);
      }
      disableActions = false;
   }
   
   //==============================================================
   // Class method to get the plugin's version.
   //==============================================================

   protected static String getVersion()
   {
      return VERSION;
   }
   
   //==============================================================
   // Class method to get the plugin's version.
   //==============================================================

   protected ImageIcon getTabIcon()
   {
      return tabIcon;
   }
}