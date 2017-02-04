//=================================================================
//                  PluginRepositoryInterface
//=================================================================
//    This class defines the methods that are required by all
// Plugin Repositories in order to properly function within the 
// framework as a repository definition for plugins.
//
//              << PluginRepositoryInterface.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.1 02/04/2017
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
// Version 1.0 Production PluginRepositoryInterface Class.
//         1.1 Added Interfaces setOptions() & getRepositoryOptions().
//
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import java.util.ArrayList;

/**
 *    The PluginRepositoryInterface class defines the methods that are
 * required by all Plugin Repositories in order to properly function
 * within the  framework as a repository definition for plugins.   
 * 
 * @author Dana M. Proctor
 * @version 1.1 02/04/2017
 */

public interface PluginRepositoryInterface
{  
   //==============================================================
   // Interface method to set the repository name.
   //==============================================================
   
   void setName(String repositoryName);
   
   //==============================================================
   // Interface method to set the repository path.
   //==============================================================
   
   void setPath(String repositoryPath);
   
   //==============================================================
   // Interface method to set the repository type.
   //==============================================================
   
   void setType(String repositoryType);
   
   //==============================================================
   // Interface method to set the repository options.
   //==============================================================
   
   void setOptions(String[] repositoryOptions);
   
   //==============================================================
   // Interface method to setup up the repository.
   // The argument should be a reference to a network location.
   //
   // ex. C:\Users\Documents\plugin
   // ex. \\pc101\Users\jim\Documents\plugin
   // ex. /home/user/documents/plugin
   // ex. ftp(s)://dandymadeproductions.com/
   // ex. http(s)://dandymadeproductions.com/
   //==============================================================

   boolean setRepository(String repository);
   
   //==============================================================
   // Interface method to add an plugin item to the repository.
   //==============================================================

   void addPluginItem(Plugin pluginItem);
   
   //==============================================================
   // Interface method to clear the plugin items in the repository
   // list.
   //==============================================================

   void clearPluginItems();
   
   //==============================================================
   // Interface method to allow the refreshing the collection of
   // plugin items in the repository list.
   //==============================================================
   
   void refresh();
   
   //==============================================================
   // Interface method to load the repository list and in so doing
   // populate the cache.
   //==============================================================
   
   boolean loadPluginList();
   
   //==============================================================
   // Interface method to read the repository plugin list from the
   // cache.
   //==============================================================
   
   boolean readPluginList(boolean allowRetry);
   
   //==============================================================
   // Interface method to allow the collection of a name that will
   // be associated with the repository.
   //==============================================================

   String getName();
   
   //==============================================================
   // Interface method to allow the collection of a path that will
   // be associated with the repository.
   //==============================================================
   
   String getPath();
   
   //==============================================================
   // Interface method to allow the return of some predifined type,
   // example URL, http, ftp, other?
   //==============================================================
   
   String getRepositoryType();
   
   //==============================================================
   // Interface method to allow the return of some options for
   // ftp(s).
   //==============================================================
   
   String[] getRepositoryOptions();
   
   //==============================================================
   // Interface method to allow the collection of the list of
   // plugins that are associated with the repository.
   //==============================================================

   ArrayList<Plugin> getPluginItems();
}