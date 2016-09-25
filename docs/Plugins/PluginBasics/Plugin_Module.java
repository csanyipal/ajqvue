//=================================================================
//                    Plugin_Module Class
//=================================================================
//
//    This class provides the abstract framework for plugin classes
// to extends in order to properly function within the application.
//
//                  << Plugin_Module.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.2 09/25/2016
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
// Version 1.0 Production PluginModule Class.
//         1.1 Renamed to Plugin_Module Class.
//         1.2 Comment Changes for getPanel() Methods.
//             
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuBar;

/**
 *    The Plugin_Module class provides the abstract framework for
 * plugin classes to extends in order to properly function within
 * the application.
 * 
 * @author Dana M. Proctor
 * @version 1.2 09/25/2016
 */

public abstract class Plugin_Module implements PluginModuleInterface
{
   // Class Instances.
   //protected Main_Frame parent;
   protected String path_FileName;
   public String name;
   public String author;
   protected String version;
   protected String description;
   protected String category;
   protected int size;
   protected ImageIcon tabIcon;
   protected JMenuBar menuBar;
   protected JComponent toolBar;
   protected JComponent panel;

   //===========================================================
   // PluginModule Constructor
   //===========================================================

   public Plugin_Module()
   {
      // Just Initialize to a NULL condition.
      
      path_FileName = null;
      name = null;
      author = null;
      version = null;
      description = null;
      category = null;
      size = 0;
      tabIcon = null;
      menuBar = null;
      toolBar = null;
      panel = null;
   }
   
   //==============================================================
   // Class method to setup up your plugin.
   // OVERIDE THIS METHOD!
   //==============================================================

   /*
   public void initPlugin(Main_Frame mainFrame, String path)
   {
      // This is where the plugin should be initialized.
      parent = mainFrame;
   }
   */
   
   //==============================================================
   // Class methods to get/set the plugin's file name.
   //==============================================================
   
   public String getPath_FileName()
   {
      return path_FileName;
   }
   
   protected String getControlledPath_FileName()
   {
      return path_FileName;
   }
   
   //==============================================================
   // Class method to get/set the plugin's name.
   // Interface requirement.
   //==============================================================

   public String getName()
   {
      return name;
   }
   
   public String getControlledName()
   {
      return name;
   }
   
   //==============================================================
   // Class method to get/set the plugin's author.
   // Interface requirement.
   //==============================================================

   public String getAuthor()
   {
      return author;
   }
   
   public String getControlledAuthor()
   {
      return author;
   }
   
   //==============================================================
   // Class method to obtain the plugin's version number.
   // Interface requirement.
   //==============================================================

   public String getVersion()
   {
      return version;
   }
   
   public String getControlledVersion()
   {
      return version;
   }
   
   //==============================================================
   // Class method to obtain the plugin's description.
   // Interface requirement.
   //==============================================================

   public String getDescription()
   {
      return description;
   }
   
   public String getControlledDescription()
   {
      return description;
   }
   
   //==============================================================
   // Class method to obtain the plugin's category.
   // Interface requirement.
   //==============================================================

   public String getCategory()
   {
      return category;
   }
   
   public String getControlledCategory()
   {
      return category;
   }
   
   //==============================================================
   // Class method to obtain the plugin's size.
   // Interface requirement.
   //==============================================================

   public int getSize()
   {
      return size;
   }
   
   public int getControlledSize()
   {
      return size;
   }
   
   //==============================================================
   // Class method to allow the collection of a image icon that
   // will be used as an identifier in the tab structure.
   //
   // NOTE: The tab icon should be no larger than 16 x 16.
   // Interface requirement.
   //==============================================================

   public ImageIcon getTabIcon()
   {
      return tabIcon;
   }
   
   public ImageIcon getControlledTabIcon()
   {
      return tabIcon;
   }
   
   //==============================================================
   // Class method to obtain the plugin's JMenuBar that can be
   // used to control various aspects of the modules functionality.
   // Interface requirement.
   //==============================================================

   public JMenuBar getMenuBar()
   {
      return menuBar;
   }
   
   public JMenuBar getControlledMenuBar()
   {
      return menuBar;
   }
   
   //==============================================================
   // Class method to allow the collection of a JToolBar to be
   // used with the plugin module.
   // Interface requirement.
   //==============================================================

   public JComponent getToolBar()
   {
      return toolBar;
   }
   
   public JComponent getControlledToolBar()
   {
      return toolBar;
   }
   
   //==============================================================
   // Class method for returning a JComponent, JPanel or JFXPanel
   // for inclusion in the application's main tab. Interface
   // requirement.
   //==============================================================

   public JComponent getPanel()
   {
      return panel;
   }
   
   public JComponent getControlledPanel()
   {
      return panel;
   }
   
   //==============================================================
   // Class method for being able to set the database tables, occurs
   // if the database is reloaded.
   // Interface requirement.
   //==============================================================

   public void setDBTables(ArrayList<String> tableNames)
   {
      // Do what you will if you need database table names.
   }
   
   //==============================================================
   // Class method to allow the plugin to start activities back
   // up after a stop() sequence.
   // (USED FOR CONTROLLING THREADS)
   //==============================================================

   public void start()
   {
      // Do what you will to start again from stop.
   }
   
   //==============================================================
   // Class method to allow the plugin to temporarily stop 
   // activities that may then be started again.
   // (USED FOR CONTROLLING THREADS)
   //==============================================================

   public void stop()
   {
      // Do what you will to notify stop.
   }
   
   //==============================================================
   // Class method to allow the plugin to close activities pending
   // a closing of the application.
   //==============================================================
   
   public void shutdown()
   {
      // Do what you will to notify pending closing.
   }
}