//=================================================================
//                 CSVExportPreferencesPanel
//=================================================================
//
// 	This class provides a generic panel in the appearance of
// a form for selecting the CSV data export options.
//
//             << CSVExportPreferencesPanel.java >>
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
// Version 1.0 09/18/2018 Production DataDeliminatorDialog Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dandymadeproductions.ajqvue.structures.DataExportProperties;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The CSVExportPreferencesPanel class provides a generic panel
 * in the appearance of a form for selecting the CSV data export
 * options.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class CSVExportPreferencesPanel extends JPanel implements ActionListener, KeyListener, ChangeListener
{
   // Class Instances.
   private static final long serialVersionUID = -2715315124472653218L;
   
   private JCheckBox includeTextCheckBox;
   private JSpinner textMaxCharsSpinner;
   private JRadioButton tabRadioButton, semicolonRadioButton, commaRadioButton, spaceRadioButton,
           otherRadioButton;
   private JTextField otherTextField;
   private JComboBox<Object> dateFormatComboBox;
   private JCheckBox summaryTableLimitCheckBox;
   private JButton restoreDefaultsButton, applyButton;
   
   public static final boolean DEFAULT_CHAR_INCLUSION = false;
   public static final int DEFAULT_CHARS_LENGTH = 50;
   public static final String DEFAULT_DATA_DELIMITER = ",";
   public static final boolean DEFAULT_SUMMARY_TABLE_USE_LIMIT = true;

   //===========================================================
   // DataPreferencesPreferencesDialog Constructor
   //===========================================================

   public CSVExportPreferencesPanel(AResourceBundle resourceBundle)
   {
      // Class Instances
      JPanel mainPanel, textOptionsPanel, delimiterPanel;
      JPanel dateLimitPanel, dateFormatPanel, summaryTableLimitPanel; 
      JPanel buttonPanel;
      JLabel dateFormatLabel;
      String resource;

      // Setting up
      setLayout(new BorderLayout());

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();

      mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0),
                          BorderFactory.createLoweredBevelBorder()));

      // ==================================================
      // TEXT Options Panel & Compoenent .

      textOptionsPanel = new JPanel(new GridLayout(2, 1, 2, 2));
      textOptionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
                                 BorderFactory.createEtchedBorder()));
      createTextOptionsPanel(textOptionsPanel, gridbag, constraints, resourceBundle);
      mainPanel.add(textOptionsPanel, BorderLayout.NORTH);

      // =====================================================
      // Delimiter Panel & Components

      delimiterPanel = new JPanel(gridbag);
      delimiterPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createEtchedBorder()));
      createDelimiterPanel(delimiterPanel, gridbag, constraints, resourceBundle);
      mainPanel.add(delimiterPanel, BorderLayout.CENTER);
      add(mainPanel, BorderLayout.CENTER);
      
      // =====================================================
      // Date/Limit Panel & Components
      
      dateLimitPanel = new JPanel(new GridLayout(2, 1, 0, 0));
      
      // Date Format
      dateFormatPanel = new JPanel(gridbag);
      dateFormatPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createEtchedBorder()));
      
      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.label.DateFormat",
                                                  "Date Format");
      dateFormatLabel = new JLabel(resource);
      dateFormatLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
      
      buildConstraints(constraints, 0, 0, 1, 1, 24, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(dateFormatLabel, constraints);
      dateFormatPanel.add(dateFormatLabel);
      
      dateFormatComboBox = new JComboBox<Object>(Utils.getDateFormatOption());
      dateFormatComboBox.addActionListener(this);
      
      buildConstraints(constraints, 1, 0, 1, 1, 76, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(dateFormatComboBox, constraints);
      dateFormatPanel.add(dateFormatComboBox);
      
      buildConstraints(constraints, 0, 0, 1, 1, 100, 65);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(dateFormatPanel, constraints);
      dateLimitPanel.add(dateFormatPanel);
      
      // Summary Table Use LIMIT
      summaryTableLimitPanel = new JPanel();
      summaryTableLimitPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createEtchedBorder()));
      
      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.checkbox.SummaryTableUseLimit",
                                                  "Summary Table Use LIMIT");
      summaryTableLimitCheckBox = new JCheckBox(resource, DEFAULT_SUMMARY_TABLE_USE_LIMIT);
      summaryTableLimitCheckBox.setBorder(BorderFactory.createEmptyBorder());
      summaryTableLimitCheckBox.setFocusPainted(false);
      summaryTableLimitCheckBox.addActionListener(this);
      
      summaryTableLimitPanel.add(summaryTableLimitCheckBox);
      
      buildConstraints(constraints, 0, 1, 1, 1, 100, 35);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(summaryTableLimitPanel, constraints);
      dateLimitPanel.add(summaryTableLimitPanel);
      
      mainPanel.add(dateLimitPanel, BorderLayout.SOUTH);
      add(mainPanel, BorderLayout.CENTER);

      // Button Action Options Panel
      buttonPanel = new JPanel();
      
      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.button.RestoreDefaults",
                                                  "Restore Defaults");
      restoreDefaultsButton = new JButton(resource);
      restoreDefaultsButton.addActionListener(this);
      buttonPanel.add(restoreDefaultsButton);

      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.button.Apply", "Apply");
      applyButton = new JButton(resource);
      applyButton.addActionListener(this);
      buttonPanel.add(applyButton);

      add(buttonPanel, BorderLayout.SOUTH);
      
      // Retrieve existing state and set accordingly.
      setCSVExportProperties(DBTablesPanel.getDataExportProperties());
      applyButton.setEnabled(false);
   }

   //========================================================
   // ActionEvent Listener method for detecting the inputs
   // from the panel and directing to the appropriate routine.
   //========================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object formSource = evt.getSource();

      if (formSource instanceof JButton)
      {
         // Restore Defaults Button Action
         if (formSource == restoreDefaultsButton)
         {
            includeTextCheckBox.setSelected(DEFAULT_CHAR_INCLUSION);
            textMaxCharsSpinner.setValue(Integer.valueOf(DEFAULT_CHARS_LENGTH));
            textMaxCharsSpinner.setEnabled(false);
            commaRadioButton.setSelected(true);
            otherTextField.setEnabled(false);
            dateFormatComboBox.setSelectedItem(DBTablesPanel.getGeneralDBProperties().getViewDateFormat());
            applyButton.setEnabled(true);
            summaryTableLimitCheckBox.setSelected(DEFAULT_SUMMARY_TABLE_USE_LIMIT);
         }

         // Apply Button Action
         else if (formSource == applyButton)
         {
            DBTablesPanel.setDataExportProperties(getCSVExportOptions());
            applyButton.setEnabled(false);
         }
      }

      // Detect the selection of TEXT inclusion
      // for enabling the chars numbers spinner.
      if (formSource instanceof JCheckBox)
      {
         if (formSource == includeTextCheckBox)
         {
            if (includeTextCheckBox.isSelected())
               textMaxCharsSpinner.setEnabled(true);
            else
               textMaxCharsSpinner.setEnabled(false);
         }
         applyButton.setEnabled(true);
      }

      // Triggering the apply button back to enabled
      // when option changes and controlling the
      // other textfield.
      if (formSource instanceof JRadioButton || formSource instanceof JComboBox)
      {
         applyButton.setEnabled(true);

         if (formSource instanceof JRadioButton)
         {
            if (otherRadioButton.isSelected())
               otherTextField.setEnabled(true);
            else
               otherTextField.setEnabled(false);
         }
      }
   }
   
   //==============================================================
   // KeyEvent Listener method for detected key pressed events to
   // full fill KeyListener Interface requirements.
   //==============================================================

   public void keyPressed(KeyEvent evt)
   {
      // Do Nothing
   }

   //==============================================================
   // KeyEvent Listener method for detecting key released events
   // to full fill KeyListener Interface requirements.
   //==============================================================

   public void keyReleased(KeyEvent evt)
   {
      // Do Nothing
   }

   //==============================================================
   // KeyEvent Listener method for detecting key pressed event,
   // in this case the otherTextField.
   //==============================================================

   public void keyTyped(KeyEvent evt)
   {
      if (evt.getSource() == otherTextField)
         applyButton.setEnabled(true);
   }

   //================================================================
   // ActionEvent Listener method for determining when the selections
   // have been made so an update can be performed on the DataExport
   // Properties.
   //================================================================

   public void stateChanged(ChangeEvent evt)
   {
      Object panelSource = evt.getSource();

      if (panelSource instanceof JSpinner && applyButton != null)
         applyButton.setEnabled(true);
   }

   //================================================================
   // Class Method to create the Text Options Components.
   //================================================================

   private void createTextOptionsPanel(JPanel textOptionsPanel,
                                       GridBagLayout gridbag, GridBagConstraints constraints,
                                       AResourceBundle resourceBundle)
   {
      // Class Method Instances
      JPanel charNumberSelectionPanel;
      SpinnerNumberModel textCharSpinnerModel;
      JLabel charNumberLabel;

      final int minimumCharsIncluded = 0;
      final int maxCharsIncluded = 16777215;
      final int spinnerCharsIncludedStep = 50;
      String resource;
      
      // Add Checkbox and Spinner

      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.checkbox.Include",
                                                  "Include");
      includeTextCheckBox = new JCheckBox(resource + " CLOB, TEXT, MEDIUMTEXT & LONGTEXT", false);
      includeTextCheckBox.setFocusPainted(false);
      includeTextCheckBox.addActionListener(this);
      textOptionsPanel.add(includeTextCheckBox);

      charNumberSelectionPanel = new JPanel(gridbag);
      charNumberSelectionPanel.setBorder(BorderFactory.createLoweredBevelBorder());

      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.label.TextCharacterNumber",
                                                  "Text Character Number");
      charNumberLabel = new JLabel(resource);
      charNumberLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

      buildConstraints(constraints, 0, 0, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(charNumberLabel, constraints);
      charNumberSelectionPanel.add(charNumberLabel);

      textCharSpinnerModel = new SpinnerNumberModel(DEFAULT_CHARS_LENGTH, minimumCharsIncluded,
                                                    maxCharsIncluded, spinnerCharsIncludedStep);
      textMaxCharsSpinner = new JSpinner(textCharSpinnerModel);
      textMaxCharsSpinner.setEnabled(false);
      textMaxCharsSpinner.addChangeListener(this);

      buildConstraints(constraints, 1, 0, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(textMaxCharsSpinner, constraints);
      charNumberSelectionPanel.add(textMaxCharsSpinner);
      textOptionsPanel.add(charNumberSelectionPanel);
   }

   //================================================================
   // Class Method to create the Delimiter Options Components.
   //================================================================

   private void createDelimiterPanel(JPanel delimiterPanel,
                                     GridBagLayout gridbag, GridBagConstraints constraints,
                                     AResourceBundle resourceBundle)
   {
      // Class Method Instances
      JPanel delimiterSelectionPanel;
      JLabel delimiterLabel;
      String resource;

      // Delimiter Label & Radio Buttons

      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.label.Delimiter",
                                                  "Delimiter");
      delimiterLabel = new JLabel(resource);

      buildConstraints(constraints, 0, 0, 1, 1, 30, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(delimiterLabel, constraints);
      delimiterPanel.add(delimiterLabel);

      delimiterSelectionPanel = new JPanel(gridbag);
      delimiterSelectionPanel.setBorder(BorderFactory.createCompoundBorder(
                              BorderFactory.createEmptyBorder(0, 0, 0, 0),
                              BorderFactory.createLoweredBevelBorder()));

      ButtonGroup delimiterButtonGroup = new ButtonGroup();

      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.radiobutton.Tab", "Tab");
      tabRadioButton = new JRadioButton(resource, false);
      tabRadioButton.setFocusPainted(false);
      tabRadioButton.addActionListener(this);
      delimiterButtonGroup.add(tabRadioButton);

      buildConstraints(constraints, 0, 0, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(tabRadioButton, constraints);
      delimiterSelectionPanel.add(tabRadioButton);

      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.radiobutton.Semicolon",
                                                  "Semicolon");
      semicolonRadioButton = new JRadioButton(resource, false);
      semicolonRadioButton.setFocusPainted(false);
      semicolonRadioButton.addActionListener(this);
      delimiterButtonGroup.add(semicolonRadioButton);

      buildConstraints(constraints, 1, 0, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(semicolonRadioButton, constraints);
      delimiterSelectionPanel.add(semicolonRadioButton);

      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.radiobutton.Comma",
                                                  "Comma");
      commaRadioButton = new JRadioButton(resource, true);
      commaRadioButton.setFocusPainted(false);
      commaRadioButton.addActionListener(this);
      delimiterButtonGroup.add(commaRadioButton);

      buildConstraints(constraints, 0, 1, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(commaRadioButton, constraints);
      delimiterSelectionPanel.add(commaRadioButton);

      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.radiobutton.Space",
                                                  "Space");
      spaceRadioButton = new JRadioButton(resource, false);
      spaceRadioButton.setFocusPainted(false);
      spaceRadioButton.addActionListener(this);
      delimiterButtonGroup.add(spaceRadioButton);

      buildConstraints(constraints, 1, 1, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(spaceRadioButton, constraints);
      delimiterSelectionPanel.add(spaceRadioButton);

      resource = resourceBundle.getResourceString("CSVExportPreferencesPanel.radiobutton.Other",
                                                  "Other");
      otherRadioButton = new JRadioButton(resource, false);
      otherRadioButton.setFocusPainted(false);
      otherRadioButton.addActionListener(this);
      delimiterButtonGroup.add(otherRadioButton);

      buildConstraints(constraints, 0, 2, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(otherRadioButton, constraints);
      delimiterSelectionPanel.add(otherRadioButton);

      otherTextField = new JTextField(10);
      otherTextField.addKeyListener(this);
      otherTextField.setEnabled(false);

      buildConstraints(constraints, 1, 2, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(otherTextField, constraints);
      delimiterSelectionPanel.add(otherTextField);

      buildConstraints(constraints, 1, 0, 1, 1, 70, 100);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(delimiterSelectionPanel, constraints);
      delimiterPanel.add(delimiterSelectionPanel);
   }

   //================================================================
   // Class Method for helping the parameters in gridbag.
   //================================================================

   private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw,
                                 int gh, double wx, double wy)
   {
      gbc.gridx = gx;
      gbc.gridy = gy;
      gbc.gridwidth = gw;
      gbc.gridheight = gh;
      gbc.weightx = wx;
      gbc.weighty = wy;
   }

   //===============================================================
   // Class method to get the panels options.
   //===============================================================

   public DataExportProperties getCSVExportOptions()
   {
      DataExportProperties newDataProperties = DBTablesPanel.getDataExportProperties();

      // TEXT Insclusion
      newDataProperties.setTextInclusion(includeTextCheckBox.isSelected());
      newDataProperties.setTextCharsNumber(Integer.parseInt(textMaxCharsSpinner.getValue().toString()));

      // Delimiter
      if (tabRadioButton.isSelected())
         newDataProperties.setDataDelimiter("\t");
      else if (semicolonRadioButton.isSelected())
         newDataProperties.setDataDelimiter(";");
      else if (commaRadioButton.isSelected())
         newDataProperties.setDataDelimiter(",");
      else if (spaceRadioButton.isSelected())
         newDataProperties.setDataDelimiter(" ");
      else
         newDataProperties.setDataDelimiter(otherTextField.getText());
      
      // Date Format
      newDataProperties.setCSVDateFormat((String)dateFormatComboBox.getSelectedItem());
      
      // Summary Table Use Limit
      newDataProperties.setCSVSummaryTableUseLimit(summaryTableLimitCheckBox.isSelected());

      return newDataProperties;
   }

   //========================================================
   // Class method to set the panels options.
   //========================================================

   public void setCSVExportProperties(DataExportProperties dataProperties)
   {
      // TEXT Inclusion
      includeTextCheckBox.setSelected(dataProperties.getTextInclusion());
      if (includeTextCheckBox.isSelected())
         textMaxCharsSpinner.setEnabled(true);

      textMaxCharsSpinner.setValue(Integer.valueOf(dataProperties.getTextCharsNumber()));

      // Delimiter
      if (dataProperties.getDataDelimiter().equals("\t"))
         tabRadioButton.setSelected(true);
      else if (dataProperties.getDataDelimiter().equals(";"))
         semicolonRadioButton.setSelected(true);
      else if (dataProperties.getDataDelimiter().equals(","))
         commaRadioButton.setSelected(true);
      else if (dataProperties.getDataDelimiter().equals(" "))
         spaceRadioButton.setSelected(true);
      else
      {
         otherRadioButton.setSelected(true);
         otherTextField.setText(dataProperties.getDataDelimiter());
         otherTextField.setEnabled(true);
      }
      
      // Date Format
      dateFormatComboBox.setSelectedItem(dataProperties.getCSVDateFormat());
      
      // Summary Table Use Limit
      summaryTableLimitCheckBox.setSelected(dataProperties.getCSVSummaryTableUseLimit());
   }
}
