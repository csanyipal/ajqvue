//=================================================================
//                        GzFileFilter
//=================================================================
//
//    This class provides a custom FileFilter instance to be used
// in selecting plugin repository files, gz.
//
//                  << GzFileFilter.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.0 09/17/2016
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
// Version 1.0 Production GzFileFilter Class.
//             
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.io.File;
import java.util.Locale;

import javax.swing.filechooser.FileFilter;

/**
 *    The GzFileFilter class provides a customer FileFilter instance to
 * be used in selecting plugin repository files, GZ.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class GzFileFilter extends FileFilter
{
   //==============================================================
   // Required class methods to implement the FileFilter. Accept
   // all directories and *.gz files.
   //==============================================================
   
   public boolean accept(File file)
   {
      String extension, fileName;
      int lastIndexOfDot;
      
      // All Directories
      if (file.isDirectory())
         return true;
      
      // Only Plugin Module Files, *.gz
      extension = "";
      fileName = file.getName();
      lastIndexOfDot = fileName.lastIndexOf('.');

      if (lastIndexOfDot > 0 &&  lastIndexOfDot < fileName.length() - 1)
          extension = fileName.substring(lastIndexOfDot + 1).toLowerCase(Locale.ENGLISH);
      
      if (extension.equals("gz"))
         return true;
      
      // Nope.
      return false;
    }
   
   public String getDescription()
   {
      return "Gzipped Files";
   }
}
