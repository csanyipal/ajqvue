//=================================================================
//               TableFieldProfiler PluginModule 
//=================================================================
//
//    This class provides the hook to incorporate a external plugin
// module into the Ajqvue application.
//
//                   << PluginModule.java >>
//
//=================================================================
// Copyright (C) 2010-2016 Dana M. Proctor
// Version 2.5 09/25/2016
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
// Version 1.0 Initial Dandy Data Profiler MyJSQLView_PluginModule Class.
//         1.1 Changed Name to TableFieldProfiler_PluginModule. Modified
//             the Way the Class Thread pluginThread is Handled.
//         1.2 Changed Name to PluginModule. Removed Class Method setParentFrame()
//             and Added Argument MyJSQLView_Frame to initPlugin().
//         1.3 Modified Class Method getTabIcon() to Return New Instacne tabIcon
//             That is Created in the initPlugin() Method.
//         1.4 Moved Over to TableFieldProfiler Code.
//         1.5 Implemented Interface Method getVersion().
//         1.6 Added Required Interface Argument String path to initPlugin().
//             Modified in Same tabIcon Using path and Passed to TableFieldProfiler
//             Class Constructor.
//         1.7 Update to Reflect Change In MyJSQLView Connection Now All Derived
//             From ConnectionManager Instead of MyJSQLView_Access.
//         1.8 Parameterized All Vector, & HashMap Types to Bring Code Into
//             Compliance With Java 5.0 API.
//         1.9 Change of tableNames Argument in setDBTables() from Vector Data
//             Type to ArrayList.
//         2.0 Updated Imports in Order Properly Load MyJSQLView Classes Which
//             Changed Packaging for v3.35++. Added Method setName() to Meet
//             MyJSQLView_PluginModule Interface Requirements.
//         2.1 Collected tabIcon From dataProfiler Call.
//         2.2 Removed All Overridden Method Except setDBTables(), So They the
//             Core MyJSQLView_PluginModule Handles After Just Setting Instances
//             in initPlugin().
//         2.3 Added Class Method getAuthor() & getDescription().
//         2.4 Updated Copyright.
//         2.5 Referenced Imports to Ajquve Application.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.tablefieldprofiler;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.Main_Frame;
import com.dandymadeproductions.ajqvue.plugin.Plugin_Module;

/**
 *    The PluginModule class provides the hook to incorporate a external
 * plugin module into the Ajqvue application.
 * 
 * @author Dana M. Proctor
 * @version 2.5 09/25/2016
 */

public class PluginModule extends Plugin_Module
{
   // Class Instances
   // private Main_Frame parent;
   private TableFieldProfiler dataProfiler;

   //==============================================================
   // PluginModule Constructor.
   //==============================================================

   public PluginModule()
   {
      super();
   }

   //==============================================================
   // Class method to start the classes thread.
   //==============================================================

   public void initPlugin(Main_Frame mainFrame, String path)
   {
      // Main Class
      dataProfiler = new TableFieldProfiler(mainFrame, path, ConnectionManager.getTableNames());
   }
   
   //==============================================================
   // Class method to meet the interface requirements for getting
   // the name of the module.
   //==============================================================

   public String getName()
   {
      return "Table Field Profiler";
   }
  
   //==============================================================
   // Class method to meet the interface requirements for getting
   // the author of the module.
   //==============================================================

   public String getAuthor()
   {
      return "Dandy Made Productions";
   }
   
   //==============================================================
   // Class method to return the version release number of the
   // plugin module.
   //==============================================================
   
   public String getVersion()
   {
      return TableFieldProfiler.getVersion();
   }
   
   //==============================================================
   // Class method to meet the interface requirements for getting
   // the description for the module.
   //==============================================================
   
   public String getDescription()
   {
      return "The TableFieldProfiler module encompasses aspects that give general\n"
             + "information for a database's table fields and also a cluster and number\n"
             + "analysis. The information is presented in the form of graphic charts,\n"
             + "pie, bar, & bubble for the field's record count, distribution, and\n"
             + "patterns. The analysis aspect of the profiler gives frequency, variation,\n"
             + "percentile, and clustered average information for a field.";
   }
   
   //==============================================================
   // Class method to meet the interface requirements of returning
   // a ImageIcon that will be used as the plugin's tab Icon.
   //==============================================================

   public ImageIcon getTabIcon()
   {
      return dataProfiler.getTabIcon();
   }
   
   //==============================================================
   // Class method to meet the interface requirements of returning
   // a Menu Bar that will be used as the plugin's Menu Bar.
   //==============================================================

   public JMenuBar getMenuBar()
   {
      return dataProfiler.getMenuBar();
   }
   
   //==============================================================
   // Class method to meet the interface requirements of returning
   // a Too Bar that will be used as the plugin's Tool Bar.
   //==============================================================

   public JToolBar getToolBar()
   {
      return dataProfiler.getToolBar();
   }
   
   //==============================================================
   // Class method to meet the interface requirements for returning
   // a JPanel for inclusion in the Ajqvue application's main
   // tab.
   //==============================================================

   public JPanel getPanel()
   {
      return dataProfiler.getPanel();
   }
   
   //==============================================================
   // Class method to meet the interface requirements for being
   // able to set the database tables.
   //==============================================================

   public void setDBTables(ArrayList<String> tableNames)
   {
      dataProfiler.setDBTables(tableNames);
   }
}