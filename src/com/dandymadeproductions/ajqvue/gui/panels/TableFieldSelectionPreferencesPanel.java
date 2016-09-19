//=================================================================
//          TableFieldSelectionsPreferencesPanel.
//=================================================================
//
//    This class provides the ability to select the preferred
// database table fields to be display in the TableTabPanel summary
// table.
//
//         << TableFieldSelectionPreferencesPanel.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.0 09/18/2012
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
// Version 1.0 Production TableFieldPreferences Class.
//             
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The TableFieldSelectionPreferencesPanel class provides the
 * ability to select the preferred database table fields to be
 * display in the TableTabPanel summary table.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class TableFieldSelectionPreferencesPanel extends JPanel implements ActionListener, ItemListener
{
   // =============================================
   // Creation of the necessary class instance
   // variables for the JPanel.
   // =============================================

   private static final long serialVersionUID = 3200553393287146930L;

   private TableTabPanel tableTabPanel;
   private ArrayList<String> checkBoxFields;
   private JCheckBox[] columnNamesCheckBoxes;
   private HashMap<String, JCheckBox> checkBoxesHashMap;
   private HashMap<String, String> columnNamesHashMap;
   private ArrayList<String> primaryKeys;
   private JButton selectAllButton, clearAllButton, applyButton;

   //==============================================================
   // TableFieldSelectionPreferencesPanel Constructor
   //==============================================================

   public TableFieldSelectionPreferencesPanel(TableTabPanel tableTabPanel, AResourceBundle resourceBundle)
   {
      this.tableTabPanel = tableTabPanel;
      
      // Class Instances
      JPanel itemSelections, southButtonPanel;
      int rowNumber;
      String iconsDirectory, resource;
      ImageIcon keyUpIcon, keyDownIcon;

      // Setting up
      setLayout(new BorderLayout());
      checkBoxesHashMap = new HashMap <String, JCheckBox>();
      
      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();
      keyUpIcon = resourceBundle.getResourceImage(iconsDirectory + "keyUpIcon.png");
      keyDownIcon = resourceBundle.getResourceImage(iconsDirectory + "keyDownIcon.png");
      
      // Setting up table column names' checkboxes that will be used
      // to select the desired tabel fields to be displayed.
      
      checkBoxFields = tableTabPanel.getAllTableHeadings();

      rowNumber = checkBoxFields.size() / 2;
      if (checkBoxFields.size() % 2 > 0)
         rowNumber = checkBoxFields.size() / 2 + 1;

      itemSelections = new JPanel(new GridLayout(rowNumber, 4, 0, 0));
      itemSelections.setBorder(BorderFactory.createLoweredBevelBorder());

      Iterator<String> tableColumnNamesIterator = checkBoxFields.iterator();
      columnNamesCheckBoxes = new JCheckBox[checkBoxFields.size()];

      columnNamesHashMap = tableTabPanel.getColumnNamesHashMap();
      primaryKeys = tableTabPanel.getPrimaryKeys();

      int i = 0;
      while (tableColumnNamesIterator.hasNext())
      {
         String columnName = (String)tableColumnNamesIterator.next();

         if (primaryKeys.contains(columnNamesHashMap.get(columnName)))
         {
            columnNamesCheckBoxes[i] = new JCheckBox(columnName, keyUpIcon);
            columnNamesCheckBoxes[i].setSelectedIcon(keyDownIcon);
         }
         else
            columnNamesCheckBoxes[i] = new JCheckBox(columnName);

         itemSelections.add(columnNamesCheckBoxes[i]);
         checkBoxesHashMap.put(columnName, columnNamesCheckBoxes[i++]);
      }

      loadPreferences();
      JScrollPane listScrollPane = new JScrollPane(itemSelections);
      listScrollPane.getVerticalScrollBar().setUnitIncrement(10);

      add(listScrollPane, BorderLayout.CENTER);

      // Buttons to set all, clear all the checkboxes
      // or apply the changes to the selected table summary
      // view.
      southButtonPanel = new JPanel();
      southButtonPanel.setBorder(BorderFactory.createEmptyBorder());

      resource = resourceBundle.getResourceString("TableFieldSelectionPreferencesPanel.button.SelectAll",
                                                  "Select All");
      selectAllButton = new JButton(resource);
      selectAllButton.setFocusPainted(false);
      selectAllButton.addActionListener(this);
      southButtonPanel.add(selectAllButton);

      resource = resourceBundle.getResourceString("TableFieldSelectionPreferencesPanel.button.ClearAll",
                                                  "Clear All");
      clearAllButton = new JButton(resource);
      clearAllButton.setFocusPainted(false);
      clearAllButton.addActionListener(this);
      southButtonPanel.add(clearAllButton);

      resource = resourceBundle.getResourceString("TableFieldSelectionPreferencesPanel.button.Apply",
                                                  "Apply");
      applyButton = new JButton(resource);
      applyButton.setFocusPainted(false);
      applyButton.setEnabled(false);
      applyButton.addActionListener(this);
      southButtonPanel.add(applyButton);

      add(southButtonPanel, BorderLayout.SOUTH);
   }

   //==============================================================
   // ActionEvent Listener method for determining when the selections
   // have been made so an update can be performed on the summary
   // table being displayed in the tab(s).
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object panelSource = evt.getSource();

      // Apply Button Action.
      if (panelSource == applyButton)
      {
         updatePreferences();
         applyButton.setEnabled(false);
      }

      // Select All Checkboxes
      else if (panelSource == selectAllButton)
      {
         for (int i = 0; i < columnNamesCheckBoxes.length; i++)
            columnNamesCheckBoxes[i].setSelected(true);
         applyButton.setEnabled(true);
      }

      // Clear All Checkboxes.
      else if (panelSource == clearAllButton)
      {
         for (int i = 0; i < columnNamesCheckBoxes.length; i++)
         {
            if (!primaryKeys.contains(columnNamesHashMap.get(columnNamesCheckBoxes[i].getText())))
               columnNamesCheckBoxes[i].setSelected(false);
         }
         applyButton.setEnabled(true);
      }
   }

   //==============================================================
   // ItemEvent Listener method for determined when on of the
   // selections has changed that the apply button made be enabled.
   //==============================================================

   public void itemStateChanged(ItemEvent evt)
   {
      Object panelSource = evt.getSource();

      if (panelSource instanceof JCheckBox)
         applyButton.setEnabled(true);
   }

   //==============================================================
   // Class method to load the current users fields preferences.
   //==============================================================

   private void loadPreferences()
   {
      // Method Instances
      ArrayList<String> tableHeadings;
      Iterator<String> currentFieldIterator;
      String currentElementName;
      JCheckBox currentCheckBox;

      // Loading the current table fields.
      tableHeadings = tableTabPanel.getCurrentTableHeadings();

      currentFieldIterator = checkBoxFields.iterator();

      while (currentFieldIterator.hasNext())
      {
         currentElementName = (String) currentFieldIterator.next();
         currentCheckBox = checkBoxesHashMap.get(currentElementName);

         if (tableHeadings.contains(currentElementName))
            currentCheckBox.setSelected(true);

         currentCheckBox.addItemListener(this);
      }
   }

   //==============================================================
   // Class method to allow the setting of TableTabPanel preferences
   // that will be used to view the summary table of data.
   //==============================================================

   public void updatePreferences()
   {
      if (applyButton.isEnabled())
      {
         Thread updateTableTabPanelFieldsThread = new Thread(new Runnable()
         {
            public void run()
            {
               // Instances
               int checkBoxCount;
               ArrayList<String> newFields;
               Boolean viewOnlyState;

               // Determine which of the table fields have been
               // selected.
               checkBoxCount = columnNamesCheckBoxes.length;
               newFields = new ArrayList <String>();
               viewOnlyState = false;

               for (int i = 0; i < checkBoxCount; i++)
               {
                  if (columnNamesCheckBoxes[i].isSelected())
                     newFields.add((String) columnNamesCheckBoxes[i].getText());
                  else
                     if (primaryKeys.contains(columnNamesHashMap.get(columnNamesCheckBoxes[i].getText())))
                        viewOnlyState = true;
               }

               // Setting the new field preferences.
               tableTabPanel.setTableHeadings(newFields);
               tableTabPanel.setViewOnly(viewOnlyState);
            }
         }, "TableFieldSelectionPreferencesPanel.updatetableTabPanelFieldsThread");
         updateTableTabPanelFieldsThread.start();
      }
   }
}
