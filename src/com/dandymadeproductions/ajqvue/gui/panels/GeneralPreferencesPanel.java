//=================================================================
//                    GeneralPreferencesPanel
//=================================================================
//
//    This class provides a generic panel in the appearance of
// a form for selecting the general options.
//
//             << GeneralPreferencesPanel.java >>
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
// Version 1.0 09/18/2016 Production GeneralPreferencesPanel Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.structures.GeneralDBProperties;
import com.dandymadeproductions.ajqvue.structures.GeneralProperties;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The GeneralPreferencesPanel class provides a generic panel
 * in the appearance of a form for selecting the general
 * options.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class GeneralPreferencesPanel extends JPanel implements ActionListener, ChangeListener
{
   // Class Instances.
   private static final long serialVersionUID = -38018440091231375L;
   
   public GeneralOptionsPreferencesFiller generalOptionsPanelFiller;
   private JComboBox<Object> dateFormatComboBox;
   private JSpinner limitIncrementSpinner;
   private JSpinner batchSizeSpinner;
   private JCheckBox batchEnabledCheckBox, sequencerEnablerCheckBox;
   private JButton sequencerResetButton;
   private JButton restoreDefaultsButton, applyButton;
   
   private boolean generateSequence;
   
   public static final int DEFAULT_LIMIT_INCREMENT = 50000;
   public static final boolean DEFAULT_BATCH_SIZE_ENABLED = false;
   public static final int DEFAULT_BATCH_SIZE = 50000;
   public static final int DEFAULT_SEQUENCE_SIZE = 15;
   public static final int DEFAULT_SEQUENCE_MAX = 41;

   //===========================================================
   // GeneralPreferencesPanel Constructor
   //===========================================================

   public GeneralPreferencesPanel(AResourceBundle resourceBundle)
   {
      // Class Instances
      JPanel mainPanel;
      JPanel dateFormatPanel, limitIncrementPanel, batchPanel, sequencerPanel;
      JPanel fillerPanel;
      JPanel buttonPanel;
      JLabel dateFormatLabel, limitIncrementLabel;
      JLabel batchSizeLabel;
      
      SpinnerNumberModel limitIncrementSpinnerModel;
      final int minimumLimitIncrementSize = 2;
      final int maxLimitIncrementSize = 500000;
      final int spinnerLimitIncrementStep = 10000;
      
      SpinnerNumberModel batchSizeSpinnerModel;
      final int minimumBatchSize = 2;
      final int maxBatchSize = 500000;
      final int spinnerBatchSizeStep = 10000;
      
      ImageIcon resetOnIcon;
      String iconsDirectory, resource;

      // Setting up
      setLayout(new BorderLayout());

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();
      
      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();
      
      generateSequence = false;

      mainPanel = new JPanel(new GridLayout(4, 1, 2, 2));
      mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0),
                          BorderFactory.createLoweredBevelBorder()));
      
      // =====================================================
      // Date Format Panel & Components
      
      dateFormatPanel = new JPanel(gridbag);
      dateFormatPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(3, 3, 3, 3), BorderFactory.createEtchedBorder()));
      
      resource = resourceBundle.getResourceString("GeneralPreferencesPanel.label.DateFormat",
                                                  "Date Format");
      dateFormatLabel = new JLabel(resource);
      dateFormatLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
      
      buildConstraints(constraints, 0, 0, 1, 1, 50, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(dateFormatLabel, constraints);
      dateFormatPanel.add(dateFormatLabel);
      
      dateFormatComboBox = new JComboBox<Object>(Utils.getDateFormatOption());
      dateFormatComboBox.addActionListener(this);
      
      buildConstraints(constraints, 1, 0, 1, 1, 50, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(dateFormatComboBox, constraints);
      dateFormatPanel.add(dateFormatComboBox);
      
      mainPanel.add(dateFormatPanel);
      
      // =====================================================
      // Limit Increment Panel & Components
      
      limitIncrementPanel = new JPanel(gridbag);
      limitIncrementPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(3, 3, 3, 3), BorderFactory.createEtchedBorder()));

      resource = resourceBundle.getResourceString("GeneralPreferencesPanel.label.TableReadLimitIcrement",
                                                  "Table Read Limit Increment");
      limitIncrementLabel = new JLabel(" " + resource);

      buildConstraints(constraints, 0, 0, 1, 1, 50, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(limitIncrementLabel, constraints);
      limitIncrementPanel.add(limitIncrementLabel);
      
      
      limitIncrementSpinnerModel = new SpinnerNumberModel(DEFAULT_LIMIT_INCREMENT,
         minimumLimitIncrementSize, maxLimitIncrementSize, spinnerLimitIncrementStep);
      limitIncrementSpinner = new JSpinner(limitIncrementSpinnerModel);
      limitIncrementSpinner.addChangeListener(this);

      buildConstraints(constraints, 1, 0, 1, 1, 50, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(limitIncrementSpinner, constraints);
      limitIncrementPanel.add(limitIncrementSpinner);

      mainPanel.add(limitIncrementPanel);
      
      // =====================================================
      // Batch Size Panel & Components
      
      batchPanel = new JPanel(gridbag);
      batchPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(3, 3, 3, 3), BorderFactory.createEtchedBorder()));
      
      resource = resourceBundle.getResourceString("GeneralPreferencesPanel.label.EnableTableWriteBatch",
                                                  "Enable Table Write Batch");
      batchEnabledCheckBox = new JCheckBox(resource, false);
      batchEnabledCheckBox.setFocusPainted(false);
      batchEnabledCheckBox.addActionListener(this);

      buildConstraints(constraints, 0, 0, 1, 1, 34, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(batchEnabledCheckBox, constraints);
      batchPanel.add(batchEnabledCheckBox);
      
      resource = resourceBundle.getResourceString("GeneralPreferencesPanel.label.BatchSize",
                                                  "Batch Size");
      batchSizeLabel = new JLabel(" " + resource);

      buildConstraints(constraints, 1, 0, 1, 1, 33, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(batchSizeLabel, constraints);
      batchPanel.add(batchSizeLabel);
      
      batchSizeSpinnerModel = new SpinnerNumberModel(DEFAULT_BATCH_SIZE,
         minimumBatchSize, maxBatchSize, spinnerBatchSizeStep);
      batchSizeSpinner = new JSpinner(batchSizeSpinnerModel);
      batchSizeSpinner.addChangeListener(this);

      buildConstraints(constraints, 2, 0, 1, 1, 33, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(batchSizeSpinner, constraints);
      batchPanel.add(batchSizeSpinner);
      
      mainPanel.add(batchPanel);
      
      // =====================================================
      // Sequencer Generator Panel & Components
      
      sequencerPanel = new JPanel(gridbag);
      sequencerPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(3, 3, 3, 3), BorderFactory.createEtchedBorder()));
      
      resource = resourceBundle.getResourceString("GeneralPreferencesPanel.label.ResetPasswordSequencer",
                                                  "Reset Password Sequencer");
      sequencerEnablerCheckBox = new JCheckBox(resource, false);
      sequencerEnablerCheckBox.setFocusPainted(false);
      sequencerEnablerCheckBox.addActionListener(this);

      buildConstraints(constraints, 0, 0, 1, 1, 60, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(sequencerEnablerCheckBox, constraints);
      sequencerPanel.add(sequencerEnablerCheckBox);
      
      resource = resourceBundle.getResourceString("GeneralPreferencesPanel.label.Reset",
                                                  "Reset");
      resetOnIcon = resourceBundle.getResourceImage(iconsDirectory + "resetIcon_16x16.png");
      sequencerResetButton = new JButton(resource, resetOnIcon);
      sequencerResetButton.setEnabled(false);
      sequencerResetButton.setFocusable(false);
      sequencerResetButton.setMargin(new Insets(0, 0, 0, 0));
      sequencerResetButton.addActionListener(this);

      buildConstraints(constraints, 1, 0, 1, 1, 40, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(sequencerResetButton, constraints);
      sequencerPanel.add(sequencerResetButton);
      
      mainPanel.add(sequencerPanel);
      
      add(mainPanel, BorderLayout.NORTH);
      
      // Filler Center Panel.
      
      fillerPanel = new JPanel(new GridLayout(1, 1, 0, 0));
      fillerPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(0, 0, 0, 0),
         BorderFactory.createLoweredBevelBorder()));
      
      generalOptionsPanelFiller = new GeneralOptionsPreferencesFiller();
      generalOptionsPanelFiller.setThreadAction(true);
     
      fillerPanel.add(generalOptionsPanelFiller);
      add(fillerPanel, BorderLayout.CENTER);

      // Button Action Options Panel
      buttonPanel = new JPanel();
      
      resource = resourceBundle.getResourceString("GeneralPreferencesPanel.button.RestoreDefaults",
                                                  "Restore Defaults");
      restoreDefaultsButton = new JButton(resource);
      restoreDefaultsButton.addActionListener(this);
      buttonPanel.add(restoreDefaultsButton);

      resource = resourceBundle.getResourceString("GeneralPreferencesPanel.button.Apply", "Apply");
      applyButton = new JButton(resource);
      applyButton.addActionListener(this);
      buttonPanel.add(applyButton);

      add(buttonPanel, BorderLayout.SOUTH);
      
      // Retrieve existing state and set accordingly.
      setGeneralDBProperties(DBTablesPanel.getGeneralDBProperties());
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
         // Reset Sequencer
         if (formSource == sequencerResetButton)
         {
            generateSequence = true;
            applyButton.setEnabled(true);
         }
         
         // Restore Defaults Button Action
         else if (formSource == restoreDefaultsButton)
         {
            dateFormatComboBox.setSelectedIndex(0);
            limitIncrementSpinner.setValue(Integer.valueOf(DEFAULT_LIMIT_INCREMENT));
            batchEnabledCheckBox.setSelected(DEFAULT_BATCH_SIZE_ENABLED);
            batchSizeSpinner.setValue(Integer.valueOf(DEFAULT_BATCH_SIZE));
            sequencerEnablerCheckBox.setSelected(false);
            generateSequence = false;
            applyButton.setEnabled(true);
         }

         // Apply Button Action
         else if (formSource == applyButton)
         {
            DBTablesPanel.setGeneralDBProperties(getGeneralDBOptions());
            if (generateSequence)
               Ajqvue.setGeneralProperties(getGeneralOptions());
            applyButton.setEnabled(false);
         }
      }

      // Triggering the apply button back to enabled
      // when option changes and controlling the
      // other textfield.
      if (formSource instanceof JRadioButton || formSource instanceof JComboBox
            || formSource instanceof JCheckBox)
      {
         if (formSource == sequencerEnablerCheckBox)
         {
            sequencerResetButton.setEnabled(sequencerEnablerCheckBox.isSelected());
            if (generateSequence)
               generateSequence = sequencerEnablerCheckBox.isSelected();
         }
         else
            applyButton.setEnabled(true);
      }
   }

   //==============================================================
   // ChangeEvent Listener method for determined when the limit
   // increment spinner has changed so that the apply button can be
   // enabled.
   //==============================================================

   public void stateChanged(ChangeEvent evt)
   {
      Object panelSource = evt.getSource();

      if (panelSource instanceof JSpinner && applyButton != null)
         applyButton.setEnabled(true);
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

   public GeneralProperties getGeneralOptions()
   {
      GeneralProperties newGeneralProperties = Ajqvue.getGeneralProperties();
      
      // Sequencer
      if (generateSequence)
         newGeneralProperties.setSequenceList(Utils.getChartList(DEFAULT_SEQUENCE_SIZE,
                                                                            DEFAULT_SEQUENCE_MAX));
      return newGeneralProperties;
   }
   
   //===============================================================
   // Class method to get the panels options.
   //===============================================================

   public GeneralDBProperties getGeneralDBOptions()
   {
      GeneralDBProperties newGeneralDBProperties = DBTablesPanel.getGeneralDBProperties();
      
      // Date Format & Limit Increment
      newGeneralDBProperties.setViewDateFormat((String)dateFormatComboBox.getSelectedItem());
      newGeneralDBProperties.setLimitIncrement(Integer.parseInt(limitIncrementSpinner.getValue().toString()));
      newGeneralDBProperties.setBatchSizeEnabled(batchEnabledCheckBox.isSelected());
      newGeneralDBProperties.setBatchSize(Integer.parseInt(batchSizeSpinner.getValue().toString()));

      return newGeneralDBProperties;
   }
   
   //========================================================
   // Class method to set the panel options.
   //========================================================

   public void setGeneralDBProperties(GeneralDBProperties generalDBProperties)
   {
      // Date Format & Limit Increment
      dateFormatComboBox.setSelectedItem(generalDBProperties.getViewDateFormat());
      limitIncrementSpinner.setValue(Integer.valueOf(generalDBProperties.getLimitIncrement()));
      batchEnabledCheckBox.setSelected(generalDBProperties.getBatchSizeEnabled());
      batchSizeSpinner.setValue(Integer.valueOf(generalDBProperties.getBatchSize()));
   }
}
