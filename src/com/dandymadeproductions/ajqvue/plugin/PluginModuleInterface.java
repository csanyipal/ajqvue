//=================================================================
//                   PluginModuleInterface
//=================================================================
//    This class defines the methods that are required by all
// Plugin Modules in order to properly function within the 
// application as a plugin.
//
//              << PluginModuleInterface.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.0 09/19/2016
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
// Version 1.0 Production PluginModuleInterface Class.
//
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuBar;

import com.dandymadeproductions.ajqvue.gui.Main_Frame;

/**
 *    The PluginModuleInterface class defines the methods that
 * are required by all Plugin Modules in order to properly function
 * within the application as a plugin.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/19/2016
 */

public interface PluginModuleInterface
{  
   //==============================================================
   // Class method to setup up the plugin.
   //==============================================================

   void initPlugin(Main_Frame mainFrame, String path);
   
   //==============================================================
   // Class method to allow the collection of the path and file
   // name that was used to for the plugin.
   //==============================================================
   
   String getPath_FileName();
   
   //==============================================================
   // Class method to allow the collection of a name that will be
   // used to identify & set the tooltip in the application tab
   // structure.
   //==============================================================

   String getName();
   
   //==============================================================
   // Class method to allow the collection of a author of the
   // plugin module.
   //==============================================================

   String getAuthor();
   
   //==============================================================
   // Class method to return the version release number of the
   // plugin module.
   //==============================================================
   
   String getVersion();
   
   //==============================================================
   // Class method to return the description of the plugin module.
   //==============================================================
   
   String getDescription();
   
   //==============================================================
   // Class method to return the category of the plugin module.
   //==============================================================
   
   String getCategory();
   
   //==============================================================
   // Class method to return the size of the plugin module.
   //==============================================================
   
   int getSize();
   
   //==============================================================
   // Class method to allow the collection of a image icon that
   // will be used as an identifier in the application tab structure.
   // NOTE: The tab icon should be no larger than 12 x 12.
   //==============================================================

   ImageIcon getTabIcon();
   
   //==============================================================
   // Class method to allow the collection of a JMenuBar to be used
   // with the plugin module.
   //==============================================================

   JMenuBar getMenuBar();
   
   //==============================================================
   // Class method to allow the collection of a JToolBar to be
   // used with the plugin module.
   //==============================================================

   JComponent getToolBar();
   
   //==============================================================
   // Class method to return the panel associated with the module.
   // ALL PluginModules MUST HAVE A JPANEL TO BE USED BY THE
   // APPLICATION TO POPULATE THE MAIN TAB TO BE ACCESSABLE!
   //==============================================================
   
   JComponent getPanel();
   
   //==============================================================
   // Class method to allow the setting the database tables.
   //==============================================================

   void setDBTables(ArrayList<String> tables);
   
   //==============================================================
   // Class method to allow the plugin to start activities back
   // up after a stop() sequence.
   // (USED FOR CONTROLLING THREADS)
   //==============================================================

   void start();
   
   //==============================================================
   // Class method to allow the plugin to temporarily stop 
   // activities that may then be started again.
   // (USED FOR CONTROLLING THREADS)
   //==============================================================

   void stop();
   
   //==============================================================
   // Class method to allow the plugin to close activities pending
   // a closing of the application.
   //==============================================================

   void shutdown();
}
