//=================================================================
//            TableRecordCount PluginModule Class
//=================================================================
//
//    This class provides the hook to incorporate a external plugin
// module into the Ajqvue application.
//
//                  << PluginModule.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.1 09/24/2016
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
// Version 1.0 Production TableRecordCount PluginModule.
//         1.1 Updated Reference to Ajqvue PluginModule to Plugin_Module.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.tablerecordcount;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.Main_Frame;
import com.dandymadeproductions.ajqvue.plugin.Plugin_Module;

/**
 *    The PluginModule class provides the hook to incorporate a external plugin
 * module into the Ajqvue application.
 * 
 * @author Dana M. Proctor
 * @version 1.1 09/24/2016
 */

public class PluginModule extends Plugin_Module
{
   // Class Instances
   private String pluginName, pluginAuthor;
   private TableRecordCountPanel tableRecordCountPanel;

   //==============================================================
   // PluginModule Constructor.
   //==============================================================

   public PluginModule()
   {
      super();
   }

   //==============================================================
   // Class method to initialize your plugin.
   //==============================================================

   public void initPlugin(Main_Frame parentFrame, String path)
   {
      pluginName = "Table Record Count";
      pluginAuthor = "Dandy Made Productions";
      tableRecordCountPanel = new TableRecordCountPanel(path, ConnectionManager.getTableNames());
   }

   //==============================================================
   // Class method to meet the interface requirements for getting
   // the name of the module.
   //==============================================================

   public String getName()
   {
      return pluginName;
   }
   
   //==============================================================
   // Class method to meet the interface requirements for getting
   // the author of the module.
   //==============================================================

   public String getAuthor()
   {
      return pluginAuthor;
   }
   
   //==============================================================
   // Class method to return the version release number of the
   // plugin module.
   //==============================================================
   
   public String getVersion()
   {
      return TableRecordCountPanel.getVersion();
   }
   
   //==============================================================
   // Class method to meet the interface requirements of returning
   // a ImageIcon that will be used as the plugin's tab Icon.
   //==============================================================

   public ImageIcon getTabIcon()
   {
      return tableRecordCountPanel.getTabIcon();
   }

   //==============================================================
   // Class method to meet the interface requirements for returning
   // a JPanel for inclusion in the Ajqvue application's main
   // tab.
   //==============================================================

   public JPanel getPanel()
   {
      return tableRecordCountPanel;
   }

   //==============================================================
   // Class method to meet the interface requirements for being
   // able to set the database tables.
   //==============================================================

   public void setDBTables(ArrayList<String> tableNames)
   {
      tableRecordCountPanel.reloadPanel(tableNames);
      tableRecordCountPanel.repaint();
   }
}