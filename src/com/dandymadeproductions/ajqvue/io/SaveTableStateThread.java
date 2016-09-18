//=================================================================
//                     SaveTableStateThread
//=================================================================
//    This class provides a thread to safely dump a database table's
// current state to a local file.
//
//                 << SaveTableStateThread.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.0 09/18/2016
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
// Version 1.0 Production SaveTableStateThread Class.
//             
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

/**
 *    The SaveTableStateThread class provides a thread to safely
 * dump a database table's current state to a local file.   
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class SaveTableStateThread implements Runnable
{
   // Class Instances
   private Object tableState;
   private String fileName;

   //==============================================================
   // SaveTableStateThread Constructor.
   //==============================================================

   public SaveTableStateThread(String fileName, Object dumpData)
   {
      this.fileName = fileName;
      tableState = dumpData;
   }

   //==============================================================
   // Class Method for Normal Start of the Thread
   //==============================================================
   
   public void run()
   {
      // Dumping the selected tables state.
      WriteDataFile.mainWriteDataString(fileName, tableState.toString().getBytes(), false);  
   }
}
