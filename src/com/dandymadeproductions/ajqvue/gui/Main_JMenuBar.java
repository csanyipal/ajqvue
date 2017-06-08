//=================================================================
//                       Main_JMenuBar
//=================================================================
//
//    This class is used to constructed the menubar for the
// application frame.
//
//               << Main_JMenuBar.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.1 06/08/2017
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
// Version 1.0 Production Main_JMenuBar Class.
//         1.1 Method createEditMenu() Added Clear History.
//         
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.text.DefaultEditorKit;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.ConnectionProperties;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The Main_JMenuBar class is used to constructed the menubar for the
 * application frame.
 * 
 * @author Dana M. Proctor
 * @version 1.1 06/08/2017
 */

public class Main_JMenuBar extends JMenuBar implements MenuActionCommands
{
   // Instance & Class Fields.
   private static final long serialVersionUID = 4955351713253784313L;
   
   private static JMenu schemasMenu = new JMenu();
   private static Main_Frame mainFrame;
   private AResourceBundle resourceBundle;

   //==============================================================
   // Main_JMenuBar JMenuBar Constructor.
   //==============================================================

   public Main_JMenuBar(Main_Frame parent)
   {
      mainFrame = parent;

      // Constructor Instances.
      String iconsDirectory, resource;
      ArrayList<String> schemas;

      // Setting up a icons directory instance.
      resourceBundle = Ajqvue.getResourceBundle();
      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();

      // JMenu Bar for the Frame.
      setBorder(BorderFactory.createEtchedBorder());

      // Creating the File, Edit, Data, & Tools Menus
      createFileMenu();
      createEditMenu();
      createDataMenu();
      createToolsMenu();
      
      // Schemas Menu
      schemas = ConnectionManager.getSchemas();
      
      if (!schemas.isEmpty() && schemas.size() > 1)
      {
         createSchemasMenu(schemas);
      }
      
      // Help Menu
      createHelpMenu();
      
      add(Box.createHorizontalGlue());

      // Root User Flush Privileges Button
      if ((ConnectionManager.getDataSourceType().equals(ConnectionManager.MYSQL)
           || ConnectionManager.getDataSourceType().equals(ConnectionManager.MARIADB))
           && ConnectionManager.getConnectionProperties().getProperty(
                         ConnectionProperties.USER).equals("root"))
      {
         ImageIcon flushIcon = resourceBundle.getResourceImage(iconsDirectory + "flushIcon.png");
         ImageIcon flushIconPressed = resourceBundle.getResourceImage(iconsDirectory
                                                                      + "flushIconPressed.png");
         JButton flushButton = new JButton(flushIcon);
         flushButton.setPressedIcon(flushIconPressed);
         flushButton.setDisabledIcon(flushIcon);
         flushButton.setFocusPainted(false);
         flushButton.setBorder(BorderFactory.createLoweredBevelBorder());
         flushButton.setActionCommand(ACTION_FLUSH);
         resource = resourceBundle.getResourceString("Main_JMenuBar.tooltip.FlushPrivileges",
                                                     "Flush Privileges");
         flushButton.setToolTipText(resource);
         flushButton.addActionListener(mainFrame);
         add(flushButton);
      }

      // Logo
      ImageIcon logoIcon = resourceBundle.getResourceImage(iconsDirectory + "ajqvueIcon.gif");
      JButton logoIconItem = new JButton(logoIcon);
      logoIconItem.setDisabledIcon(logoIcon);
      logoIconItem.setFocusPainted(false);
      logoIconItem.setBorder(BorderFactory.createLoweredBevelBorder());
      add(logoIconItem);
   }
   
   //==============================================================
   // Helper Method to create the File Menu.
   //==============================================================

   private void createFileMenu()
   {
      // Method Instances.
      JMenu fileMenu;
      String resource;
      
      //===========
      // File Menu
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.File", "File");
      fileMenu = new JMenu(resource);
      fileMenu.setFont(fileMenu.getFont().deriveFont(Font.BOLD));
      
      // Open
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Open", "Open");
      fileMenu.add(menuItem(resource, ACTION_OPEN));
      fileMenu.addSeparator();
      
      // Save
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Save", "Save");
      fileMenu.add(menuItem(resource, ACTION_SAVE));
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.SaveAs", "Save As...");
      fileMenu.add(menuItem(resource, ACTION_SAVE_AS));
      fileMenu.addSeparator();
      
      // Print
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Print", "Print");
      fileMenu.add(menuItem(resource, ACTION_PRINT));
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.PageFormat", "Page Format");
      fileMenu.add(menuItem(resource, ACTION_PAGE_FORMAT));
      fileMenu.addSeparator();
      
      // Exit
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Exit", "Exit");
      fileMenu.add(menuItem(resource, ACTION_EXIT));
      
      add(fileMenu);
   }
   
   //==============================================================
   // Helper Method to create the Edit Menu.
   //==============================================================

   private void createEditMenu()
   {
      // Method Instances.
      JMenu editMenu;
      JMenuItem menuItem;
      String resource;
            
      //===========
      // Edit Menu
      
      menuItem = null;
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Edit", "Edit");
      editMenu = new JMenu(resource);
      editMenu.setFont(editMenu.getFont().deriveFont(Font.BOLD));
      
      // Cut, Copy, & Paste
      menuItem = new JMenuItem(new DefaultEditorKit.CutAction());
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Cut", "Cut");
      menuItem.setText(resource + "          " + "Ctrl+x");
      menuItem.setMnemonic(KeyEvent.VK_X);
      editMenu.add(menuItem);

      menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Copy", "Copy");
      menuItem.setText(resource + "       " + "Ctrl+c");
      menuItem.setMnemonic(KeyEvent.VK_C);
      editMenu.add(menuItem);

      menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Paste", "Paste");
      menuItem.setText(resource + "       " + "Ctrl+v");
      menuItem.setMnemonic(KeyEvent.VK_V);
      editMenu.add(menuItem);
      editMenu.addSeparator();
      
      // Clear Table History
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ClearHistory", "Clear History");
      editMenu.add(menuItem(resource, ACTION_CLEAR_HISTORY));
      editMenu.addSeparator();

      // Preferences
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Preferences", "Preferences");
      editMenu.add(menuItem(resource, ACTION_PREFERENCES));
      
      add(editMenu);
   }
   
   //==============================================================
   // Helper Method to create the Data Menu.
   //==============================================================

   private void createDataMenu()
   {
      // Method Instances.
      JMenu dataMenu;
      JMenu importMenu, exportMenu, exportCSVMenu, exportPDFMenu, exportSQLMenu;
      String resource;
      
      //===========
      // Data Menu
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Data", "Data");
      dataMenu = new JMenu(resource);
      dataMenu.setFont(dataMenu.getFont().deriveFont(Font.BOLD));
      
      // Import
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Import", "Import");
      importMenu = new JMenu(resource);

      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ImportSQLDump", "SQL Dump");
      importMenu.add(menuItem(resource, ACTION_IMPORT_SQL_DUMP));
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ImportCSVFile", "CSV File");
      importMenu.add(menuItem(resource, ACTION_IMPORT_CSV_FILE));
      
      dataMenu.add(importMenu);

      // Export
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Export", "Export");
      exportMenu = new JMenu(resource);
      
      // Export CSV
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ExportCSV", "CSV");
      exportCSVMenu = new JMenu(resource);
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ExportCSVTable", "Table");
      exportCSVMenu.add(menuItem(resource, ACTION_EXPORT_CSV_TABLE));
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ExportCSVSummaryTable",
                                                  "Summary Table");
      exportCSVMenu.add(menuItem(resource, ACTION_EXPORT_CSV_SUMMARY_TABLE));
      exportMenu.add(exportCSVMenu);
      
      // Export PDF
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ExportPDF", "PDF");
      exportPDFMenu = new JMenu(resource);
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ExportPDFSummaryTable",
                                                  "Summary Table");
      exportPDFMenu.add(menuItem(resource, ACTION_EXPORT_PDF_SUMMARY_TABLE));
      exportMenu.add(exportPDFMenu);
       
      // Export SQL
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ExportSQL", "SQL");
      exportSQLMenu = new JMenu(resource);
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ExportSQLTable", "Table");
      exportSQLMenu.add(menuItem(resource, ACTION_EXPORT_SQL_TABLE));
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ExportSQLSummaryTable",
                                                  "Summary Table");
      exportSQLMenu.add(menuItem(resource, ACTION_EXPORT_SQL_SUMMARY_TABLE));
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ExportSQLDatabase", "Database");
      exportSQLMenu.add(menuItem(resource, ACTION_EXPORT_SQL_DATABASE));
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ExportSQLDatabaseScheme",
                                                  "Database Scheme");
      exportSQLMenu.add(menuItem(resource, ACTION_EXPORT_SQL_DATABASE_SCHEME));
      
      exportMenu.add(exportSQLMenu);

      dataMenu.add(exportMenu);

      add(dataMenu);
   }
   
   //==============================================================
   // Helper Method to create the Tools Menu.
   //==============================================================

   private void createToolsMenu()
   {
      // Method Instances.
      JMenu toolsMenu;
      String resource;
      
      //===========
      // Tools Menu
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Tools", "Tools");
      toolsMenu = new JMenu(resource);
      toolsMenu.setFont(toolsMenu.getFont().deriveFont(Font.BOLD));
      
      // SQL Query Bucket, Query Frame, Reload Database, & Search Database.
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.SQLQueryBucket",
                                                  "SQL Query Bucket");
      toolsMenu.add(menuItem(resource, ACTION_SQL_QUERY_BUCKET));
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.QueryFrame", "Query Frame");
      toolsMenu.add(menuItem(resource, ACTION_QUERY_FRAME));
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ReloadDatabase",
                                                  "Reload Database");
      toolsMenu.add(menuItem(resource, ACTION_RELOAD_DATABASE));
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.SearchDatabase",
                                                  "Search Database");
      toolsMenu.add(menuItem(resource, ACTION_SEARCH_DATABASE));
      
      add(toolsMenu);
   }
   
   //==============================================================
   // Helper Method to create the Schemas Menu.
   //==============================================================

   private void createSchemasMenu(ArrayList<String> schemas)
   {
      // Method Instances.
      JRadioButtonMenuItem radioButtonMenuItem;
      ButtonGroup schemasButtonGroup;
      String resource;
      
      //===========
      // Schemas Menu
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Schemas", "Schemas");
      schemasMenu.setText(resource);
      schemasMenu.setFont(schemasMenu.getFont().deriveFont(Font.BOLD));
      
      // Create a drop down radio button selecter.
      schemasButtonGroup = new ButtonGroup();
      
      int radioButtonCount = 0;
      
      // Add an All schemas item as needed.
      if (schemas.size() != 1)
      {
         resource = resourceBundle.getResourceString("Main_JMenuBar.radioButton.All", "All");
         radioButtonMenuItem = new JRadioButtonMenuItem(resource, true);
         radioButtonMenuItem.setActionCommand("All");
         radioButtonMenuItem.addActionListener(mainFrame);
         schemasButtonGroup.add(radioButtonMenuItem);
         schemasMenu.add(radioButtonMenuItem);
         radioButtonCount = 1;
      }
      
      // Create elements of schemas menu items.
      Iterator<String> schemasIterator = schemas.iterator();
      
      while (schemasIterator.hasNext())
      {
         String schemasName = schemasIterator.next();
         
         if (radioButtonCount == 0)
            radioButtonMenuItem = new JRadioButtonMenuItem(schemasName, true);
         else
            radioButtonMenuItem = new JRadioButtonMenuItem(schemasName);
         radioButtonMenuItem.setActionCommand(schemasName);
         radioButtonMenuItem.addActionListener(mainFrame);
         schemasButtonGroup.add(radioButtonMenuItem);
         schemasMenu.add(radioButtonMenuItem);
         radioButtonCount++;
      }
      
      add(schemasMenu);
   }
   
   //==============================================================
   // Method to reload the schemas menu item.
   //==============================================================

   public static void reloadSchemasMenu()
   {
      if (!schemasMenu.getText().isEmpty())
      {
         // Method Instances.
         ArrayList<String> schemas;
         
         JRadioButtonMenuItem radioButtonMenuItem;
         ButtonGroup schemasButtonGroup;
         String schemasName, resource;
         
         // Collect the schemas
         schemas = ConnectionManager.getSchemas();
          
         // Clear & reset the menu.
         
         schemasMenu.removeAll();
         schemasButtonGroup = new ButtonGroup();
         
         int radioButtonCount = 0;
         
         // Add an All schemas item as needed.
         if (schemas.size() != 1)
         {
            resource = Ajqvue.getResourceBundle().getResourceString("Main_JMenuBar.radioButton.All", "All");
            radioButtonMenuItem = new JRadioButtonMenuItem(resource, true);
            radioButtonMenuItem.setActionCommand("All");
            radioButtonMenuItem.addActionListener(mainFrame);
            schemasButtonGroup.add(radioButtonMenuItem);
            schemasMenu.add(radioButtonMenuItem);
            radioButtonCount = 1;
         }
         
         // Create elements of schemas menu items.
         Iterator<String> schemasIterator = schemas.iterator();
         
         while (schemasIterator.hasNext())
         {
            schemasName = schemasIterator.next();
            
            if (radioButtonCount == 0)
               radioButtonMenuItem = new JRadioButtonMenuItem(schemasName, true);
            else
               radioButtonMenuItem = new JRadioButtonMenuItem(schemasName);
            radioButtonMenuItem.setActionCommand(schemasName);
            radioButtonMenuItem.addActionListener(mainFrame);
            schemasButtonGroup.add(radioButtonMenuItem);
            schemasMenu.add(radioButtonMenuItem);
            radioButtonCount++;
         }   
      }
   }
   
   //==============================================================
   // Helper Method to create the Help Menu.
   //==============================================================

   private void createHelpMenu()
   {
      // Method Instances.
      JMenu helpMenu;
      String resource;
      
      //===========
      // Help Menu
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Help", "Help");
      helpMenu = new JMenu(resource);
      helpMenu.setFont(helpMenu.getFont().deriveFont(Font.BOLD));
      
      // Manual
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Manual", "Manual");
      helpMenu.add(menuItem(resource, ACTION_MANUAL));
      helpMenu.addSeparator();
      
      // Legal & Release Notes.
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Legal", "Legal");
      helpMenu.add(menuItem(resource, ACTION_LEGAL));
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.ReleaseNotes",
                                                  "Release Notes");
      helpMenu.add(menuItem(resource, ACTION_RELEASE_NOTES));
      helpMenu.addSeparator();
      
      // About
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.About", "About");
      helpMenu.add(menuItem(resource, ACTION_ABOUT));
      
      add(helpMenu);
   }

   // ==============================================================
   // Instance method used for the application's creation of menu
   // bar items. Helper Method.
   // ==============================================================

   private JMenuItem menuItem(String label, String actionLabel)
   {
      JMenuItem item = new JMenuItem(label);
      item.addActionListener(mainFrame);
      item.setActionCommand(actionLabel);
      return item;
   }
}
