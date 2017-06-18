//=================================================================
//                       PluginThread
//=================================================================
//    This class provides a thread to set in motion the adding of
// a corrected located plugin module to the  main frame. 
//
//                   << PluginThread.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.3 11/24/2016
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
// Version 1.0 Production PluginThread Class.
//         1.1 Updated References to PluginModule to Plugin_Module.
//         1.2 Method run() pluginmodule.tabIcon Changed Scaling to 14x14.
//         1.3 Method run() pluginmodule.tabIcon Set to defaultIcon on
//             Width/Height <= 0.
//
//-----------------------------------------------------------------
//                   danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.gui.Main_Frame;

/**
 *    The PluginThread class provides a thread to set in motion the
 * adding of a correctly located plugin module to the main frame.
 * 
 * @author Dana M. Proctor
 * @version 1.3 11/24/2016
 */

class PluginThread implements Runnable
{
   // Class Instances
   Thread t;

   private Main_Frame parentFrame;
   private Plugin_Module pluginModule;
   private ImageIcon defaultIcon;

   //==============================================================
   // PluginThread Constructor.
   //==============================================================

   PluginThread(Main_Frame parent, Plugin_Module module, ImageIcon defaultModuleIcon)
   {
      parentFrame = parent;
      pluginModule = module;
      defaultIcon = defaultModuleIcon;

      // Create and start the class thread.
      t = new Thread(this, "PluginThread");
      // System.out.println("Plugin Thread");

      t.start();
   }

   //==============================================================
   // Class method for normal start of the thread. Adds the plugin
   // module to the Main_Frame.
   //==============================================================

   public void run()
   {
      // Method Instances
      String path;
      
      // Collect path, since code will always be dealing
      // with URLs for these plugins to not use a local
      // file System file separator.
      
      path = pluginModule.getPath_FileName().substring(0, pluginModule.getPath_FileName().indexOf("<$$$>"));
      
      if (path.indexOf("/") != -1)
         path = path.substring(0, path.lastIndexOf("/"));
     
      // Call the plugin's initializing code.
      pluginModule.initPlugin(parentFrame, path);

      // Check all the main aspects needed in the loaded
      // plugin module and isolate the application from
      // deviants

      // Name
      if (pluginModule.getName() == null)
         pluginModule.name = "";
      else
      {
         if ((pluginModule.getName()).length() > 50)
            pluginModule.name = (pluginModule.getName()).substring(0, 49);
         else
            pluginModule.name = pluginModule.getName();
      }
      
      // Author
      if (pluginModule.getAuthor() == null)
         pluginModule.author = "Not Identified";
      else
         pluginModule.author = pluginModule.getAuthor();
      
      // Version
      if (pluginModule.getVersion() == null)
         pluginModule.version = "Not Identified";
      else
         pluginModule.version = pluginModule.getVersion();
      
      // Description
      if (pluginModule.getDescription() == null)
         pluginModule.description = "Not Given";
      else
         pluginModule.description = pluginModule.getDescription();
      
      // Tab Icon
      if (pluginModule.getTabIcon() == null || pluginModule.getTabIcon().getIconWidth() <= 0
          || pluginModule.getTabIcon().getIconHeight() <= 0)
         pluginModule.tabIcon = defaultIcon;
      else
         pluginModule.tabIcon = new ImageIcon((pluginModule.getTabIcon()).getImage().getScaledInstance(14,
            14, Image.SCALE_FAST));

      // MenuBar
      if (pluginModule.getMenuBar() == null)
         pluginModule.menuBar = new Default_JMenuBar(parentFrame);
      else
         pluginModule.menuBar = pluginModule.getMenuBar();

      // ToolBar
      if (pluginModule.getToolBar() == null)
         pluginModule.toolBar = new Default_JToolBar("");
      else
         pluginModule.toolBar = pluginModule.getToolBar();
      
      // Main Panel
      if (pluginModule.getPanel() == null)
         pluginModule.panel = new JPanel();
      else
         pluginModule.panel = (pluginModule.getPanel());

      // Store/Add Plugin
      Main_Frame.addTab(pluginModule, parentFrame);
   }
}
