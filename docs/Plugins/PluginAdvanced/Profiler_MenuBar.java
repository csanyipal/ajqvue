//=================================================================
//            TableFieldProfiler Profiler_MenuBar
//=================================================================
//
//    This class is used to constructed the menubar for the Table
// Field Profiler plugin module.
//
//                << Profiler_MenuBar.java >>
//
//=================================================================
// Copyright (C) 2010-2016 Dana M. Proctor.
// Version 2.8 09/25/2016
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
// Version 1.0 Original Profiler_MenuBar Class.
//         1.1 Moved Over to Table Field Profiler Code.
//         1.2 Removed Method createDataQualityMenu() & createToolsMenu().
//         1.3 Removed DataQuality Menu. Added Field Information % Field
//             Analysis Items. Aded Class Method createToolsMenu() and
//             MyJSQLView_ResourceBundle Argument to Constructor.
//         1.4 Implemented/Reviewed Locale Instances.
//         1.5 Changed the Derivation of the Action Command for the File
//             Menu Exit to MyJSQLView_MenuActionCommands.ACTION_EXIT.
//         1.6 Added Tools SQL Query Bucket Menu Item, Also Class Instances
//             ACTION_FIELD_INFORMATION  & ACTION_FIELD_ANALYSIS. Removed
//             File | Open, Save, & Save AS.
//         1.7 Added Back File | Open, Class Instance ACTION_FILE_OPEN.
//         1.8 Changed Static Class Instance ACTION_FIELD_ANALYSIS to ACTION_
//             FIELD_NUMBER_ANALYSIS and Added ACTION_FIELD_CLUSTER_ANALYSIS.
//             Addition of Latter in createToolsMenu().
//         1.9 Commented Out the Tools Cluster Analysis for Version Release 4.0.
//         2.0 Uncommented the Tools Cluster Analysis Menu Items.
//         2.1 Added Field Menu to Tools Menu.
//         2.2 Updated Imports in Order Properly Load MyJSQLView Classes
//             Which Changed Packaging for v3.35++. Update in Methods
//             createFileMenu() & createToolsMenu() for Collection of
//             String Resources.
//         2.3 Collection of ImageIcons Through the resourceBundle Instance.
//         2.4 Changed the logoIcon in Constructor to be Obtained From the
//             MyJSQLView Resource Bundle.
//         2.5 Updated Copyright.
//         2.6 Method createToolsMenu() Added Query Frame Menu Item.
//         2.7 Corrected Default resounceBundle Argument for field in Method
//             createToolsMenu().
//         2.8 Referenced Imports to Ajquve Application.
//         
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.tablefieldprofiler;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.gui.Main_Frame;
import com.dandymadeproductions.ajqvue.gui.MenuActionCommands;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The Profiler_MenuBar class is used to constructed the menubar for the
 * Table Field Profiler plugin module.
 * 
 * @author Dana M. Proctor
 * @version 2.8 09/25/2016
 */

class Profiler_MenuBar extends JMenuBar
{
   // Instance & Class Fields.
   private static final long serialVersionUID = 1227314757701051137L;

   private Main_Frame mainFrame;
   private AResourceBundle resourceBundle;
   private transient MenuActionListener menuListener;
   
   public static final String ACTION_FILE_OPEN = "FO";
   public static final String ACTION_FIELD_INFORMATION = "TFI";
   public static final String ACTION_FIELD_NUMBER_ANALYSIS = "TFNA";
   public static final String ACTION_FIELD_CLUSTER_ANALYSIS = "TFCA";
   
   //==============================================================
   // Profiler_MenuBar JMenuBar Constructor.
   //==============================================================

   protected Profiler_MenuBar(Main_Frame parent, AResourceBundle resourceBundle,
                              MenuActionListener plugin)
   {
      mainFrame = parent;
      this.resourceBundle = resourceBundle;
      menuListener = plugin;

      // Constructor Instances.
      String iconsDirectory;

      // JMenu Bar for the plugin.
      setBorder(BorderFactory.createEtchedBorder());

      // Creating the File, & Tools Menus
      createFileMenu();
      createToolsMenu();

      add(Box.createHorizontalGlue());

      // Logo
      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();
      ImageIcon logoIcon = Ajqvue.getResourceBundle().getResourceImage(iconsDirectory + "ajqvueIcon.gif");
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
      String resource;
      JMenu fileMenu;
      JMenuItem item;

      // ===========
      // File Menu

      resource = resourceBundle.getResourceString("Profiler_MenuBar.menu.File",
                                                  "File");
      fileMenu = new JMenu(resource);
      fileMenu.setFont(fileMenu.getFont().deriveFont(Font.BOLD));
      
      // Open
      resource = resourceBundle.getResourceString("Profiler_MenuBar.menu.Open",
                                                  "Open");
      fileMenu.add(menuItem(resource, ACTION_FILE_OPEN));
      fileMenu.addSeparator();
       
      // Exit
      resource = resourceBundle.getResourceString("Profiler_MenuBar.menu.Exit",
                                                  "Exit");
      item = new JMenuItem(resource);
      item.addActionListener(mainFrame);
      item.setActionCommand(MenuActionCommands.ACTION_EXIT);
      fileMenu.add(item);

      add(fileMenu);
   }
   
   //==============================================================
   // Helper Method to create the Tool Menu.
   //==============================================================

   private void createToolsMenu()
   {
      // Method Instances.
      String resource;
      JMenu toolsMenu, fieldMenu;
      JMenuItem item;

      // ===========
      // Tools Menu
      
      resource = resourceBundle.getResourceString("Profiler_MenuBar.menu.Tools",
                                                  "Tools");
      toolsMenu = new JMenu(resource);
      toolsMenu.setFont(toolsMenu.getFont().deriveFont(Font.BOLD));
      
      // Ajqvue SQL Query Bucket
      
      resource = resourceBundle.getResourceString("Profiler_MenuBar.menu.SQLQueryBucket",
                                                  "SQL Query Bucket");
      item = new JMenuItem(resource);
      item.addActionListener(mainFrame);
      item.setActionCommand(MenuActionCommands.ACTION_SQL_QUERY_BUCKET);
      toolsMenu.add(item);
      
      // Ajqvue Query Frame
      resource = resourceBundle.getResourceString("Profiler_MenuBar.menu.QueryFrame",
                                                  "Query Frame");
      item = new JMenuItem(resource);
      item.addActionListener(mainFrame);
      item.setActionCommand(MenuActionCommands.ACTION_QUERY_FRAME);
      toolsMenu.add(item);

      toolsMenu.addSeparator();
      
      // Field Information
      
      resource = resourceBundle.getResourceString("Profiler_MenuBar.menu.Field",
                                                  "Field");
      fieldMenu = new JMenu(resource);
      
      toolsMenu.add(fieldMenu);
      
      resource = resourceBundle.getResourceString("Profiler_MenuBar.menu.Information",
                                                  "Information");
      fieldMenu.add(menuItem(resource, ACTION_FIELD_INFORMATION));
      
      // Field Analysis
      
      resource = resourceBundle.getResourceString("Profiler_MenuBar.menu.NumberAnalysis",
                                                  "Number Analysis");
      fieldMenu.add(menuItem(resource, ACTION_FIELD_NUMBER_ANALYSIS));
      
      // Field Cluster Analysis
      
      resource = resourceBundle.getResourceString("Profiler_MenuBar.menu.ClusterAnalysis",
                                                  "Cluster Analysis");
      fieldMenu.add(menuItem(resource, ACTION_FIELD_CLUSTER_ANALYSIS));
      
      add(toolsMenu);
   }
   
   //==============================================================
   // Instance method used for the TableFieldProfiler's creation
   // of menu bar items. Helper Method.
   //==============================================================

   private JMenuItem menuItem(String label, String actionLabel)
   {
      JMenuItem item = new JMenuItem(label);
      item.addActionListener(menuListener);
      item.setActionCommand(actionLabel);
      return item;
   }
}