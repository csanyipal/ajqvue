//=================================================================
//                          Plugin.
//=================================================================
//   This class provides a place holder real class to instantiate
// a PluginModule. The class is used has a information  holder for
// aspects of the Plugin Frame Repository Listings of available
// Plugin Modules.
//
//                      << Plugin.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
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
// Version 1.0 Production Plugin Class.
//         1.1 Updated References to PluginModule to Plugin_Module.
//             
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import com.dandymadeproductions.ajqvue.gui.Main_Frame;

/**
 *    The Plugin class provides a place holder real class to instantiate a
 * PluginModule. The class is used has a information holder for aspects of
 * the Plugin Frame Repository Listings of Available Plugin Modules.  
 * 
 * @author Dana M. Proctor
 * @version 1.1 09/24/2016
 */

public class Plugin extends Plugin_Module
{
   // Class Instances
   private String jar;

   //==============================================================
   // Plugin Constructor.
   //==============================================================

   public Plugin()
   {
      super();
   }

   //==============================================================
   // Class method to initialize the plugin.
   //==============================================================

   public void initPlugin(Main_Frame parentFrame, String path)
   {
      // Nothing to see here or do.
   }

   //==============================================================
   // Class methods to gett the plugin's file name.
   //==============================================================
   
   public void setPath_FileName(String content)
   {
      path_FileName = content;
   }
   
   //==============================================================
   // Class method to set the plugin's name.
   //==============================================================

   public void setName(String content)
   {
      name = content;
   }
   
   //==============================================================
   // Class method to set the plugin's name.
   //==============================================================

   public void setAuthor(String content)
   {
      author = content;
   }
   
   //==============================================================
   // Class method to set the plugin's version number.
   //==============================================================

   public void setVersion(String content)
   {
      version = content;
   }
   
   //==============================================================
   // Class method to set the plugin's description.
   //==============================================================

   public void setDescription(String content)
   {
      description = content;
   }
   
   //==============================================================
   // Class method to set the plugin's category.
   //==============================================================

   public void setCategory(String content)
   {
      category = content;
   }
   
   //==============================================================
   // Class method to set the plugin's size.
   //==============================================================

   public void setSize(int value)
   {
      size = value;
   }
   
   //==============================================================
   // Class method to get/set the plugin's jar.
   //==============================================================
   
   public String getJAR()
   {
      return jar;
   }

   public void setJAR(String value)
   {
      jar = value;
   }
}
