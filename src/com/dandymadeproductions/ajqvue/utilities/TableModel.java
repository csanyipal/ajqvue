//=================================================================
//                     General Table Model
//=================================================================
//
//    This class provides the model that is used with the
// TableTabPanels to display a summary table of database data.
// The class provides the basic defaults and a way to update 
// the table data when sort, search, or the next/previous
// actions are generated.
//
//                     << TableModel.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.2 06/14/2017
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
// Version 1.0 09/17/2016 Production StockItemsTableModel
//         1.1 09/18/2016 Corrected Error in Assignment for serialVersionUID.
//         1.2 06/14/2017 Method getValueAt() Conditional Check on Caller
//                        [row][column] Within Bounds, Prevents Null Pointer
//                        Exception.
//                        
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 *    The TableModel class provides the model that is used with
 * the TableTabPanels to display a summary table of database data.
 * The class provides the basic defaults and a way to update the
 * table data when sort, search, or the next/previous actions are
 * generated.
 * 
 * @author Dana M. Proctor
 * @version 1.2 06/14/2017
 */

public class TableModel extends AbstractTableModel
{
   // Class Instances.
   private static final long serialVersionUID = -4573934966595200345L;
   
   private ArrayList<String> headings;
   private Object[][] data;
   private boolean[] editableColumns;

   //==============================================================
   // TableModel Constructor.
   //==============================================================

   public TableModel(ArrayList<String> headings, Object[][] tableData)
   {
      this.headings = headings;
      data = tableData.clone();
      editableColumns = new boolean[headings.size()];
   }
   
   //==============================================================
   // Required default method implementations.
   //==============================================================

   public int getRowCount()
   {
      if (data == null)
         return 0;
      else
         return data.length;
   }
   
   public ArrayList<String> getColumns()
   {
      return headings;
   }

   public int getColumnCount()
   {
      if (headings == null)
         return 0;
      else
         return headings.size();
   }

   public Object getValueAt(int row, int column)
   {
      if (row < data.length && column < data[0].length)
         return data[row][column];
      else
         return null;
   }

   //==============================================================
   // Class method to return the column names.
   //==============================================================

   public String getColumnName(int column)
   {
      return headings.get(column);
   }

   //==============================================================
   // Class method to insure default renderer/editor for each cell.
   //==============================================================

   public Class<?> getColumnClass(int col)
   {
      if (getValueAt(0, col) != null)
         return getValueAt(0, col).getClass();
      else
         return "".getClass();
   }

   //==============================================================
   // Class method to insure no cells in the table are editable.
   //==============================================================

   public boolean isCellEditable(int row, int col)
   {
      return editableColumns[col];
   }

   //==============================================================
   // Class method to set the whether the table is editable.
   //==============================================================

   public void setEditable()
   {
      for (int i = 0; i < editableColumns.length; i++)
         editableColumns[i] = true;
   }

   //==============================================================
   // Class method to set the editable columns in the table.
   //==============================================================

   public void setColumnEditable(int col, boolean value)
   {
      editableColumns[col] = value;
   }

   //==============================================================
   // Class method to change the data in the table and update
   // display.
   //==============================================================

   public void setValueAt(Object value, int row, int column)
   {
      data[row][column] = value;
      fireTableCellUpdated(row, column);
   }

   //==============================================================
   // Class method to update the entire table.
   //==============================================================

   public void setValues(Object[][] tableData)
   {
      data = tableData.clone();
      fireTableDataChanged();
   }
}
