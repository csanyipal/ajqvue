//=================================================================
//                   BlobTextKey Class
//=================================================================
//	This class provides the structure for a key in the SQL
// database that might consist of BLOB or TEXT data.
//
//                   << BlobTextKey.java >>
//
//=================================================================
// Copyright (C) 2005-2016 Dana M. Proctor
// Version 2.0 09/17/2016
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
// Version 1.0 Initial BlobTextKey Class.
//         1.1 Set name and keyContentto "" in Constructor.
//         1.2 Cleaned Up Javadoc Comments.
//         1.3 Header Update.
//         1.4 MyJSQLView Project Common Source Code Formatting.
//         1.5 Header Format Changes/Update.
//         1.6 Changed Package to Reflect Dandy Made Productions Code.
//         1.7 Copyright Update.
//         1.8 Copyright Update.
//         1.9 Changed Package Name to com.dandymadeproductions.myjsqlview.utilities.
//             Made Class, Constructor, & Getter/Setter Methods Public.
//         2.0 Changed Package Name to com.dandymadeproductions.ajqvue.utilities.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

/**
 *    The BlobTextKey class provides the structure for a key in the
 * SQL database that might consist of BLOB or TEXT data.
 * 
 * @author Dana M. Proctor
 * @version 2.0 09/17/2017
 */

public class BlobTextKey
{
   // Class Instances.
   private String name;
   private String keyContent;
   private int keyContentLength;

   //==============================================================
   // BlobTextKey Constructor
   //==============================================================

   public BlobTextKey()
   {
      name = "";
      keyContent = "";
      keyContentLength = 0;
   }

   //==============================================================
   // Class methods to allow classes to get the BlobTextKey object
   // components.
   //==============================================================

   public String getName()
   {
      return name;
   }

   public String getContent()
   {
      return keyContent;
   }

   public int getLength()
   {
      return keyContentLength;
   }

   //==============================================================
   // Class methods to allow classes to set the BlobTextKey object
   // components.
   //==============================================================

   public void setName(String content)
   {
      name = content;
   }

   public void setContent(String content)
   {
      keyContent = content;
   }

   public void setLength(int value)
   {
      keyContentLength = value;
   }

   //==============================================================
   // Class method to properly implement the toString() method
   // for the object. Local method overides.
   //==============================================================

   public String toString()
   {
      return name;
   }
}