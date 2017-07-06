//=================================================================
//             TableRowSelectionPreferencesPanel.
//=================================================================
//
//    This class provides the ability to select the preferred table
// row size to be display in the TableTabPanel summary table.
//
//        << TableRowSelectionPreferencesPanel.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
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
// Version 1.0 Production TableRowSelectionPanel Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;

/**
 *    The TableRowSelectionPreferencesPanel class provides the
 * ability to select the preferred table row size to be display
 * in the TableTabPanel summary table.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class TableRowSelectionPreferencesPanel extends JPanel implements ActionListener, ChangeListener
{
   // =============================================
   // Creation of the necessary class instance
   // variables for the JPanel.
   // =============================================
   
   private static final long serialVersionUID = 184853367437167075L;
   
   private String tableName;
   private JSpinner rowSizeSpinner;
   private static final int defaultRowSize = 50;
   private JButton restoreDefaultsButton, applyButton;

   //==============================================================
   // TableFieldSelectionPreferencesPanel Constructor
   //==============================================================

   public TableRowSelectionPreferencesPanel(String tableName, AResourceBundle resourceBundle)
   {
      this.tableName = tableName;

      // Class Instances
      JPanel mainPanel, southButtonPanel;
      JLabel rowSizeLabel;
      SpinnerNumberModel rowSizeSpinnerModel;

      final int minimumRowSize = 0;
      final int maxRowSize = 20000;
      final int spinnerRowSizeStep = 25;
      String resource;

      // Setting up
      setLayout(new BorderLayout());

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();

      // Setting up a label and textfield that will used
      // to select the desired table row size to be displayed.
      mainPanel = new JPanel(gridbag);
      mainPanel.setBorder(BorderFactory.createLoweredBevelBorder());

      resource = resourceBundle.getResourceString(
         "TableRowSelectionPreferencesPanel.label.SummaryTableRowSize", "Summary Table Row Size");
      rowSizeLabel = new JLabel(resource);

      buildConstraints(constraints, 0, 0, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(rowSizeLabel, constraints);
      mainPanel.add(rowSizeLabel);

      rowSizeSpinnerModel = new SpinnerNumberModel(defaultRowSize, minimumRowSize, maxRowSize,
                                                   spinnerRowSizeStep);
      rowSizeSpinner = new JSpinner(rowSizeSpinnerModel);
      rowSizeSpinner.addChangeListener(this);

      buildConstraints(constraints, 1, 0, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(rowSizeSpinner, constraints);
      mainPanel.add(rowSizeSpinner);

      loadRowSize();

      add(mainPanel, BorderLayout.CENTER);

      // Buttons to set restore defaults or apply the changes
      // to the selected table summary view.

      southButtonPanel = new JPanel();
      southButtonPanel.setBorder(BorderFactory.createEmptyBorder());

      southButtonPanel = new JPanel();
      
      resource = resourceBundle.getResourceString(
         "TableRowSelectionPreferencesPanel.button.RestoreDefaults", "Restore Defaults");
      restoreDefaultsButton = new JButton(resource);
      restoreDefaultsButton.addActionListener(this);
      southButtonPanel.add(restoreDefaultsButton);

      resource = resourceBundle.getResourceString("TableRowSelectionPreferencesPanel.button.Apply",
                                                  "Apply");
      applyButton = new JButton(resource);
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

      if (panelSource instanceof JButton)
      {
         // Restore Defaults Button Action
         if (panelSource == restoreDefaultsButton)
         {
            rowSizeSpinner.setValue(Integer.valueOf(defaultRowSize));
            applyButton.setEnabled(true);
         }

         // Apply Button Action
         else if (panelSource == applyButton)
         {
            updatePreferences();
            applyButton.setEnabled(false);
         }
      }
   }

   //==============================================================
   // ChangeEvent Listener method for determined when on of the
   // selections has changed that the apply button made be enabled.
   //==============================================================

   public void stateChanged(ChangeEvent evt)
   {
      Object panelSource = evt.getSource();

      if (panelSource instanceof JSpinner && applyButton != null)
         applyButton.setEnabled(true);
   }
   
   //==============================================================
   // Class Method for helping the parameters in gridbag.
   //==============================================================

   private void buildConstraints(GridBagConstraints gbc, int gx, int gy,
                                 int gw, int gh, double wx, double wy)
   {
      gbc.gridx = gx;
      gbc.gridy = gy;
      gbc.gridwidth = gw;
      gbc.gridheight = gh;
      gbc.weightx = wx;
      gbc.weighty = wy;
   }

   //==============================================================
   // Class method to load the current users fields preferences.
   //==============================================================

   private void loadRowSize()
   {
      // Method Instances
      TableTabPanel summaryTableTab;
      int rowSize;

      // Loading the current row size and setting
      // in the spinner.
      summaryTableTab = (TableTabPanel) DBTablesPanel.getTableTabPanel(tableName);
      
      if (summaryTableTab != null)
         rowSize = summaryTableTab.getTableRowSize();
      else
         rowSize = defaultRowSize;
      
      rowSizeSpinner.setValue(Integer.valueOf(rowSize));
   }

   //==============================================================
   // Class method to allow the setting of TableTabPanel preferences
   // that will be used to view the summary table of data.
   //==============================================================

   public void updatePreferences()
   {
      if (applyButton.isEnabled())
      {
         DBTablesPanel.startStatusTimer();
         
         Thread updateTableTabPanelRowsThread = new Thread(new Runnable()
         {
            public void run()
            {
               // Instances
               TableTabPanel summaryTableTab;
               int rowSize;

               // Setting the new row size preferences and calling main class
               // to redisplay table tab.
                  
               rowSize = Integer.parseInt(rowSizeSpinner.getValue().toString());
               summaryTableTab = DBTablesPanel.getTableTabPanel(tableName);
               summaryTableTab.setTableRowSize(rowSize);
               DBTablesPanel.setSelectedTableTabPanel(tableName);
               
               DBTablesPanel.stopStatusTimer();
            }
         }, "TableRowSelectionPreferencesPanel.updateTableTabPanelRowsThread");
         updateTableTabPanelRowsThread.start();
      }
   }
}
