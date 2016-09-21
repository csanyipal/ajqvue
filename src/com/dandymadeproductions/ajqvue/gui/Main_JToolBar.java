//=================================================================
//                    Main_JToolBar Class
//=================================================================
//
//    This class is used to construct the toolbar for the
// application frame's database tables tab.
//
//                  << Main_JToolBar.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.0 09/20/2016
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
// Version 1.0 Production Main JToolBar Class.
//         
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui;

import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The Main_JToolBar class is used to construct the toolbar
 * for the application frame's database tables tab.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/20/2016
 */

class Main_JToolBar extends JToolBar implements MenuActionCommands
{
   // Instance & Class Fields.
   private static final long serialVersionUID = 6035754600481576567L;
   
   private transient Main_Frame mainFrame;

   //==============================================================
   // Main_JToolBar Constructor.
   //==============================================================

   protected Main_JToolBar(Main_Frame parent, String title)
   {
      super(title);
      mainFrame = parent;

      // Constructor Instances
      String iconsDirectory;
      JButton buttonItem;
      
      ImageIcon openIcon, saveIcon;
      ImageIcon printIcon, pageFormatIcon, exitIcon;
      ImageIcon preferencesIcon, sqlImportIcon, csvImportIcon;
      ImageIcon csvExportTableIcon, csvExportTabSummaryTableIcon;
      ImageIcon pdfExportTabSummaryTableIcon;
      ImageIcon sqlExportTableIcon, sqlExportTabSummaryTableIcon;
      ImageIcon sqlExportDatabaseIcon, sqlExportDatabaseSchemeIcon;
      ImageIcon sqlQueryBucketIcon, queryFrameIcon;
      ImageIcon reloadDatabaseIcon, searchDatabaseIcon; 
      ImageIcon manualIcon; //legalIcon, releaseIcon, aboutIcon;
      
      AResourceBundle resourceBundle;
      String resource;
      
      // Setting up icons directory  & resource instances.

      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();
      resourceBundle = Ajqvue.getResourceBundle();

      // JTool Bar for the Frame.
      buttonItem = null;
      setBorder(BorderFactory.createLoweredBevelBorder());
      setFloatable(false);

      // ===============
      // File Menu
      
      // File Open
      
      //openIcon = new ImageIcon(iconsDirectory + "openIcon.png");
      openIcon = resourceBundle.getResourceImage("images/icons/openIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.Open", "Open");
      buttonItem = buttonItem(resource, openIcon, ACTION_OPEN);
      add(buttonItem);
      
      // File Save
      saveIcon = resourceBundle.getResourceImage(iconsDirectory + "saveIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.Save", "Save");
      buttonItem = buttonItem(resource, saveIcon, ACTION_SAVE);
      add(buttonItem);
      
      // File Print
      printIcon = resourceBundle.getResourceImage(iconsDirectory + "printIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.Print", "Pring");
      buttonItem = buttonItem(resource, printIcon, ACTION_PRINT);
      add(buttonItem);
      
      // Page Format
      pageFormatIcon = resourceBundle.getResourceImage(iconsDirectory + "pageFormatIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.PageFormat", "Page Format");
      buttonItem = buttonItem(resource, pageFormatIcon, ACTION_PAGE_FORMAT);
      add(buttonItem);
      
      // Exit
      exitIcon = resourceBundle.getResourceImage(iconsDirectory + "exitIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.Exit", "Exit");
      buttonItem = buttonItem(resource, exitIcon, ACTION_EXIT);
      add(buttonItem);
      
      addSeparator();
      
      // ===============
      // Edit Menu
      
      // Preferences
      preferencesIcon = resourceBundle.getResourceImage(iconsDirectory + "preferencesIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.Preferences", "Preferences");
      buttonItem = buttonItem(resource, preferencesIcon, ACTION_PREFERENCES);
      add(buttonItem);
      
      addSeparator();
     
      // ===============
      // Data Menu
      
      // Import CSV File
      csvImportIcon = resourceBundle.getResourceImage(iconsDirectory + "csvImportIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.ImportCSVFile",
                                                  "Import CSV File");
      buttonItem = buttonItem(resource, csvImportIcon, ACTION_IMPORT_CSV_FILE);
      add(buttonItem);
      
      // Import SQL Dump
      sqlImportIcon = resourceBundle.getResourceImage(iconsDirectory + "sqlImportIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.ImportSQLDump",
                                                  "Import SQL Dump");
      buttonItem = buttonItem(resource, sqlImportIcon, ACTION_IMPORT_SQL_DUMP);
      add(buttonItem);
      
      addSeparator();
      
      // Export CSV Tab Summary Table
      csvExportTabSummaryTableIcon = resourceBundle.getResourceImage(iconsDirectory + "csvExportSummaryTableIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.ExportCSVSummaryTable",
                                                  "Export CSV Summary Table");
      buttonItem = buttonItem(resource, csvExportTabSummaryTableIcon, ACTION_EXPORT_CSV_SUMMARY_TABLE);
      add(buttonItem);
      
      // Export CSV Table
      csvExportTableIcon = resourceBundle.getResourceImage(iconsDirectory + "csvExportTableIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.ExportCSVTable",
                                                  "Export CSV Table");
      buttonItem = buttonItem(resource, csvExportTableIcon, ACTION_EXPORT_CSV_TABLE);
      add(buttonItem);
      
      addSeparator();
      
      // Export PDF Tab Summary Table
      pdfExportTabSummaryTableIcon = resourceBundle.getResourceImage(iconsDirectory + "pdfExportSummaryTableIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.ExportPDFSummaryTable",
                                                  "Export PDF Summary Table");
      buttonItem = buttonItem(resource, pdfExportTabSummaryTableIcon, ACTION_EXPORT_PDF_SUMMARY_TABLE);
      add(buttonItem);
      
      addSeparator();
      
      // Export SQL Tab Summary Table
      sqlExportTabSummaryTableIcon = resourceBundle.getResourceImage(iconsDirectory
                                                                    + "sqlExportSummaryTableIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.ExportSQLSummaryTable",
                                                  "Export SQL Summary Table");
      buttonItem = buttonItem(resource, sqlExportTabSummaryTableIcon, ACTION_EXPORT_SQL_SUMMARY_TABLE);
      add(buttonItem);
      
      // Export SQL Table
      sqlExportTableIcon = resourceBundle.getResourceImage(iconsDirectory + "sqlExportTableIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.ExportSQLTable",
                                                  "Export SQL Table");
      buttonItem = buttonItem(resource, sqlExportTableIcon, ACTION_EXPORT_SQL_TABLE);
      add(buttonItem);
      
      // Export SQL Database
      sqlExportDatabaseIcon = resourceBundle.getResourceImage(iconsDirectory
                                                              + "sqlExportDatabaseIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.ExportSQLDatabase",
                                                  "Export SQL Database");
      buttonItem = buttonItem(resource, sqlExportDatabaseIcon, ACTION_EXPORT_SQL_DATABASE);
      add(buttonItem);
      
      // Export SQL Database Scheme
      sqlExportDatabaseSchemeIcon = resourceBundle.getResourceImage(iconsDirectory
                                                                  + "sqlExportDatabaseSchemeIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.ExportSQLDatabaseScheme",
                                                  "Export SQL Database Scheme");
      buttonItem = buttonItem(resource, sqlExportDatabaseSchemeIcon, ACTION_EXPORT_SQL_DATABASE_SCHEME);
      add(buttonItem);
      
      addSeparator();
      
      // ===============
      // Tools Menu
      
      // SQL Query Bucket Frame
      sqlQueryBucketIcon = resourceBundle.getResourceImage(iconsDirectory + "sqlQueryBucketIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.SQLQueryBucket",
                                                  "SQL Query Bucket");
      buttonItem = buttonItem(resource, sqlQueryBucketIcon, ACTION_SQL_QUERY_BUCKET);
      add(buttonItem);
      
      // Query Frame
      queryFrameIcon = resourceBundle.getResourceImage(iconsDirectory + "queryFrameIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.QueryFrame",
                                                  "Query Frame");
      buttonItem = buttonItem(resource, queryFrameIcon, ACTION_QUERY_FRAME);
      add(buttonItem);
      
      // Reload Database
      reloadDatabaseIcon = resourceBundle.getResourceImage(iconsDirectory + "reloadDatabaseIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.ReloadDatabase",
                                                  "Reload Database");
      buttonItem = buttonItem(resource, reloadDatabaseIcon, ACTION_RELOAD_DATABASE);
      add(buttonItem);
      
      // Search Database
      searchDatabaseIcon = resourceBundle.getResourceImage(iconsDirectory + "searchDatabaseIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.SearchDatabase",
                                                  "Search Database");
      buttonItem = buttonItem(resource, searchDatabaseIcon, ACTION_SEARCH_DATABASE);
      add(buttonItem);
      
      addSeparator();
      
      // ===============
      // Help Menu
      
      // Manual
      manualIcon = resourceBundle.getResourceImage(iconsDirectory + "manualIcon_20x20.png");
      resource = resourceBundle.getResourceString("Main_JToolBar.tooltip.Manual", "Manual");
      buttonItem = buttonItem(resource, manualIcon, ACTION_MANUAL);
      add(buttonItem);
      
      // Legal
      //legalIcon = resourceBundle.getResourceImage(iconsDirectory + "legalIcon.png");
      //resource = resourceBundle.getResource("Main_JToolBar.tooltip.Legal");
      //if (resource.equals(""))
      //   buttonItem = buttonItem("Legal", legalIcon, ACTION_LEGAL);
      //else
      //   buttonItem = buttonItem(resource, legalIcon, ACTION_LEGAL);
      //add(buttonItem);
      
      // Release
      //releaseIcon = resourceBundle.getResourceImage(iconsDirectory + "releaseIcon.png");
      //resource = resourceBundle.getResource("Main_JToolBar.tooltip.Release");
      //if (resource.equals(""))
      //   buttonItem = buttonItem("Release", releaseIcon, ACTION_RELEASE_NOTES);
      //else
      //   buttonItem = buttonItem(resource, releaseIcon, ACTION_RELEASE_NOTES);
      //add(buttonItem);
      
      // About
      //aboutIcon = resourceBundle.getResourceImage(iconsDirectory + "aboutIcon.png");
      //resource = resourceBundle.getResource("Main_JToolBar.tooltip.About");
      //if (resource.equals(""))
      //   buttonItem = buttonItem("About", aboutIcon, ACTION_ABOUT);
      //else
      //   buttonItem = buttonItem(resource, aboutIcon, ACTION_ABOUT);
      //add(buttonItem);
   }

   //==============================================================
   // Instance method used for the creation of tool bar button
   // items. Helper Method.
   //==============================================================

   private JButton buttonItem(String toolTip, ImageIcon icon, String actionLabel)
   {
      JButton item = new JButton(icon);
      item.setFocusable(false);
      item.setMargin(new Insets(0, 0, 0, 0));
      item.setToolTipText(toolTip);
      item.setActionCommand(actionLabel);
      item.addActionListener(mainFrame);
      
      return item;
   }
}