//=================================================================
//                      TableRowElements
//=================================================================
//   This class provides an object that can be used to house a
// single row of data from a table.
//
//                   << TableRowElements.java >>
//
//=================================================================
// Copyright (C) 2005-2018 Dana M. Proctor
// Version 1.5 05/29/2018
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
// Version 1.0 11/21/2015 Original TableRowElement Class.
//         1.1 01/02/2015 Added Method size(). Changed Constructor
//                        to Call setMessage() With Empty String.
//         1.2 12/10/2017 Changed Package to sqltofilememorydb.
//         1.3 03/23/2018 Changed Package Back to Original dbtofilememorydb.
//         1.4 05/23/2018 Made static Class Instance LAST_ELEMENT final.
//         1.5 05/29/2018 Moved From DB_To_FileMemoryDB Project to Ajqvue,
//                        Changed Package to utilities.db in Ajqvue.
//             
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities.db;

import java.util.ArrayList;

/**
 *    The TableRowElements class provides an object that can be used to
 * house a single row of data from a standard relation table.   
 * 
 * @author Dana Proctor
 * @version 1.5 05/29/2018
 */

public class TableRowElements
{
   // Class Instances
   private String message;
   private ArrayList<Object> rowElements;
   
   public static final String LAST_ELEMENT = "Last Element";
   
   //==============================================================
   // TableRowElements Constructor.
   //==============================================================

   public TableRowElements()
   {
      this(0);
   }
   
   public TableRowElements(int size)
   {
      if (size <= 0)
         rowElements = new ArrayList<Object>();
      else
         rowElements = new ArrayList<Object>(size);
      
      setMessage("");
   }
   
   //==============================================================
   // Getter/Setter methods for class instances.
   //==============================================================
   
   public String getMessage()
   {
      return message;
   }

   public void setMessage(String value)
   {
      message = value;
   }

   public Object getRowElement(int index)
   {
      return rowElements.get(index);
   }

   public void setRowElement(Object value)
   {
      rowElements.add(value);
   }
   
   public int size()
   {
      return rowElements.size();
   }
   
   //==============================================================
   // Class Method to clear the elements.
   //==============================================================
   
   public void clear()
   {
      rowElements.clear();
   }
}