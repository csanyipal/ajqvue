//=================================================================
//                    LoadTableStateThread
//=================================================================
//
//    This class provides the means to import the data that has been
// saved via the SaveTableStateThread to be used to reload a summary
// table configuration state.
//
//              << LoadTableStateThread.java >>
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
// Version 1.0 Production LoadTableStateThread Class.
//          
//-----------------------------------------------------------------
//              danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.gui.panels.TableTabPanel;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *   The LoadTableStateThread class provides the means to import the data
 * that has been saved via the SaveTableStateThread to be used to reload
 * a summary table configuration state.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class LoadTableStateThread implements Runnable
{
   // Class Instance Fields.
   private String fileName;

   //==============================================================
   // LoadTableStateThread Constructor.
   //==============================================================

   public LoadTableStateThread(String fileName)
   {
      this.fileName = fileName;
   }

   //==============================================================
   // Class Method for Normal Start of the Thread
   //==============================================================

   public void run()
   {
      // Class Method Instances
      File file;

      // Begin Execution of getting the
      // file and trying a load.

      file = new File(fileName);

      if (file.exists())
      {
         // Importing data setting configuration
         // from state file
         loadState();
      }
      else
      {
         String msg = "The file '" + fileName + "' does not exists.";
         JOptionPane.showMessageDialog(null, msg, fileName, JOptionPane.ERROR_MESSAGE);
      }
   }

   //==============================================================
   // Class method for importing a table state file.
   //==============================================================

   private void loadState()
   {
      // Class Method Instances.
      byte[] dump;
      String dumpData, delimiter;
      String tableName;
      int firstDelimiterPosition;
      TableTabPanel importTableTabPanel;

      // Obtain the data bytes from the selected file.
      dump = ReadDataFile.mainReadDataString(fileName, false);

      if (dump != null)
      {
         //System.out.println(new String(dump)); 
         dumpData = Utils.stateConvert(dump, true);
         //System.out.println(dumpData);
         
         if (!dumpData.equals(""))
         {
            delimiter = TableTabPanel.getStateDelimiter(); 
            
            // Data collected from file so now process to set a table state. 
             
            if (dumpData.indexOf(delimiter) != -1) 
            {  
               firstDelimiterPosition = dumpData.indexOf(delimiter); 
               tableName = dumpData.substring(0, firstDelimiterPosition); 
                
               importTableTabPanel = DBTablesPanel.getTableTabPanel(tableName); 
                
               if (importTableTabPanel != null) 
               { 
                  DBTablesPanel.startStatusTimer();
                  
                  DBTablesPanel.setSelectedTableTabPanel(tableName);
                  importTableTabPanel.setState(dumpData);
                  refreshTableTabPanel();
                  
                  DBTablesPanel.stopStatusTimer();
               } 
            } 
            else 
            { 
               String optionPaneStringErrors = "Unable to Decode Configuration. Possible Corrupt File!"; 
               JOptionPane.showMessageDialog(null, optionPaneStringErrors, 
                                             "Alert", JOptionPane.ERROR_MESSAGE); 
            }
         }
      }
   }

   //==============================================================
   // Class method to refresh table tab panel.
   //==============================================================

   private void refreshTableTabPanel()
   {
      TableTabPanel currentTableTabPanel = DBTablesPanel.getSelectedTableTabPanel();
      
      if (currentTableTabPanel != null)
      {
         ArrayList<String> tableFields = currentTableTabPanel.getCurrentTableHeadings();
         currentTableTabPanel.setTableHeadings(tableFields);
      }
   }
}
