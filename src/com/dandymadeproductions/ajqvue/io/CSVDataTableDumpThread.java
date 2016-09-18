//=================================================================
//                       CSVDataTableDumpThread
//=================================================================
//
//    This class provides a thread to safely dump a TableTabPanel
// summary table data to a local file. A status dialog with cancel
// is provided to allow the ability to prematurely terminate the
// dump.
//
//                << CSVDataTableDumpThread.java >>
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
// Version 1.0 Production DataTableDumpThread Class.
//             
//-----------------------------------------------------------------
//                    danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

import java.util.HashMap;
import java.util.Locale;

import javax.swing.JTable;

import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.utilities.ProgressBar;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The CSVDataTableDumpThread class provides a thread to safely
 * dump a TableTabPanel summary table data to a local file. A
 * status dialog with cancel is provided to allow the ability to
 * prematurely terminate the dump.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class CSVDataTableDumpThread implements Runnable
{
   // Class Instances
   private JTable summaryListTable;
   private HashMap<String, String> tableColumnNamesHashMap;
   private HashMap<String, String> tableColumnTypeHashMap;
   private String exportedTable, fileName;

   //==============================================================
   // CSVDataDumpThread Constructor.
   //==============================================================

   public CSVDataTableDumpThread(JTable summaryListTable, HashMap<String, String> tableColumnNamesHashMap,
                              HashMap<String, String> tableColumnTypeHashMap, String exportedTable,
                              String fileName)
   {
      this.summaryListTable = summaryListTable;
      this.tableColumnNamesHashMap = tableColumnNamesHashMap;
      this.tableColumnTypeHashMap = tableColumnTypeHashMap;
      this.exportedTable = exportedTable;
      this.fileName = fileName;
   }

   //==============================================================
   // Class Method for Normal Start of the Thread.
   //==============================================================

   public void run()
   {
      // Class Method Instances
      ProgressBar dumpProgressBar;
      HashMap<String, String> summaryListTableNameTypes;
      StringBuffer currentEntry;
      String currentTableFieldName, delimiterString;
      String currentType, currentString;
      int rowNumber;

      // Setting up
      summaryListTableNameTypes = new HashMap <String, String>();
      delimiterString = DBTablesPanel.getDataExportProperties().getDataDelimiter();
      currentEntry = new StringBuffer();

      // Constructing progress bar.
      rowNumber = summaryListTable.getRowCount();
      dumpProgressBar = new ProgressBar(exportedTable + " Dump");
      dumpProgressBar.setTaskLength(rowNumber);
      dumpProgressBar.pack();
      dumpProgressBar.center();
      dumpProgressBar.setVisible(true);

      // Collecting Table Headers, Column Fields.
      for (int i = 0; i < summaryListTable.getColumnCount(); i++)
      {
         currentTableFieldName = summaryListTable.getColumnName(i);
         currentEntry.append(tableColumnNamesHashMap.get(currentTableFieldName) + delimiterString);
         summaryListTableNameTypes.put(Integer.toString(i),
                                       tableColumnTypeHashMap.get(currentTableFieldName));
      }
      if (currentEntry.length() != 0)
      {
         currentEntry.delete((currentEntry.length() - delimiterString.length()), currentEntry.length());
         currentEntry.append("\n");
      }

      int i = 0;
      while ((i < rowNumber) && !dumpProgressBar.isCanceled())
      {
         dumpProgressBar.setCurrentValue(i);

         // Collecting rows of data & formatting date & timestamps
         // as needed according to the CSV Export Properties.

         if (summaryListTable.getValueAt(i, 0) != null)
         {
            for (int j = 0; j < summaryListTable.getColumnCount(); j++)
            {
               currentString = summaryListTable.getValueAt(i, j) + "";
               currentString = currentString.replaceAll("\n", "");
               currentString = currentString.replaceAll("\r", "");

               // Format Date & Timestamp fields as needed. PostgreSQL
               // Array Timestamps to NOT be processed, identified by
               // underscore.
               currentType = summaryListTableNameTypes.get(Integer.toString(j));

               if ((currentType != null)
                   && (currentType.equals("DATE") || currentType.indexOf("DATETIME") != -1
                       || (currentType.indexOf("TIMESTAMP") != -1) && currentType.indexOf("_") == -1))
               {
                  if (!currentString.toLowerCase(Locale.ENGLISH).equals("null"))
                  {
                     int firstSpace;
                     String time;

                     // Dates fall through DateTime and Timestamps try
                     // to get the time separated before formatting
                     // the date.

                     if (currentString.indexOf(" ") != -1)
                     {
                        firstSpace = currentString.indexOf(" ");
                        time = currentString.substring(firstSpace);
                        currentString = currentString.substring(0, firstSpace);
                     }
                     else
                        time = "";

                     currentString = Utils.convertViewDateString_To_DBDateString(currentString,
                        DBTablesPanel.getGeneralDBProperties().getViewDateFormat());
                     currentString = Utils.convertDBDateString_To_ViewDateString(currentString,
                        DBTablesPanel.getDataExportProperties().getCSVDateFormat()) + time;
                  }
               }
               currentEntry.append(currentString + delimiterString);
            }
            currentEntry.delete((currentEntry.length() - delimiterString.length()), currentEntry.length());
            currentEntry.append("\n");
         }
         i++;
      }
      dumpProgressBar.dispose();

      // Outputting Summary Table to Selected File.
      WriteDataFile.mainWriteDataString(fileName, (currentEntry.toString()).getBytes(), false);
   }
}
