//=================================================================
//                  PDFExportPreferencesPanel
//=================================================================
//
//    This class provides a generic panel in the appearance of
// a form for selecting the PDF data export options.
//
//             << PDFExportPreferencesPanel.java >>
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
// Version 1.0 09/18/2016 Production PDFExportPreferencesPanel Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.structures.DataExportProperties;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The PDFExportPreferencesPanel class provides a generic panel
 * in the appearance of a form for selecting the PDF data export options.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class PDFExportPreferencesPanel extends JPanel implements ActionListener, ChangeListener
{
   // Class Instances.
   private static final long serialVersionUID = 1773630314816388122L;
   
   private GridBagLayout gridbag;
   private GridBagConstraints constraints;

   private JRadioButton titleNoneRadioButton, titleDefaultRadioButton, titleCustomRadioButton;
   private JTextField customTitleTextField;
   private JSpinner titleFontSizeSpinner;
   private JButton titleColorButton;
   private JSpinner headerFontSizeSpinner, headerBorderSizeSpinner;
   private JButton headerColorButton, headerBorderColorButton;
   private JComboBox<Object> numberAlignmentComboBox;
   private JComboBox<Object> dateAlignmentComboBox, dateFormatComboBox;
   private JComboBox<Object> fontComboBox;
   private JComboBox<Object> pageLayoutComboBox;
   private JButton restoreDefaultsButton, applyButton;

   private JColorChooser panelColorChooser;
   private String fileSeparator, iconsDirectory;
   private String actionCommand;

   public static final String DEFAULT_FONT = "Undefined";
   public static final Color DEFAULT_COLOR = Color.BLACK;
   public static final String DEFAULT_TITLE = "";
   public static final int DEFAULT_TITLE_FONT_SIZE = 14;
   public static final int DEFAULT_HEADER_FONT_SIZE = 12;
   public static final int MINIMUM_FONT_SIZE = 8;
   public static final int MAX_FONT_SIZE = 32;
   public static final int DEFAULT_BORDER_SIZE = 1;
   public static final int MINIMUM_BORDER_SIZE = 1;
   public static final int MAX_BORDER_SIZE = 6;
   public static final int SPINNER_SIZE_STEP = 1;

   public static final int ALIGNMENT_LEFT = 0;
   public static final int ALIGNMENT_CENTER = 1;
   public static final int ALIGNMENT_RIGHT = 2;
   
   public static final int LAYOUT_PORTRAIT = 0;
   public static final int LAYOUT_LANDSCAPE = 1;

   //===========================================================
   // DataPreferencesPreferencesDialog Constructor
   //===========================================================

   public PDFExportPreferencesPanel(AResourceBundle resourceBundle)
   {
      // Class Instances
      JPanel mainPanel, centerPanel;
      JPanel titlePanel, headerPanel, numberPanel, datePanel, fontLayoutPanel;
      JPanel buttonPanel;

      String resource;

      // Setting up
      fileSeparator = Utils.getFileSeparator();
      iconsDirectory = Utils.getIconsDirectory() + fileSeparator;
      
      setLayout(new BorderLayout());

      gridbag = new GridBagLayout();
      constraints = new GridBagConstraints();

      mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createCompoundBorder(
                               BorderFactory.createEmptyBorder(1, 0, 0, 0),
                               BorderFactory.createLoweredBevelBorder()));
      centerPanel = new JPanel(gridbag);

      panelColorChooser = Utils.createColorChooser(this);
      actionCommand = "";

      // ==================================================
      // Title Panel.

      titlePanel = new JPanel(gridbag);
      titlePanel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(2, 2, 2, 2),
                                BorderFactory.createEtchedBorder()));
      fillTitlePanel(titlePanel, resourceBundle);

      mainPanel.add(titlePanel, BorderLayout.NORTH);

      // =====================================================
      // Header Panel.

      headerPanel = new JPanel(gridbag);
      headerPanel.setBorder(BorderFactory.createCompoundBorder(
                                 BorderFactory.createEmptyBorder(2, 2, 2, 2),
                                 BorderFactory.createEtchedBorder()));
      fillHeaderPanel(headerPanel, resourceBundle);

      buildConstraints(constraints, 0, 0, 1, 1, 100, 38);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(headerPanel, constraints);
      centerPanel.add(headerPanel);

      // =====================================================
      // Number Panel.

      numberPanel = new JPanel(gridbag);
      numberPanel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(2, 2, 2, 2),
                                BorderFactory.createEtchedBorder()));
      fillNumberPanel(numberPanel, resourceBundle);

      buildConstraints(constraints, 0, 1, 1, 1, 100, 24);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(numberPanel, constraints);
      centerPanel.add(numberPanel);

      // =====================================================
      // Date Panel.

      datePanel = new JPanel(gridbag);
      datePanel.setBorder(BorderFactory.createCompoundBorder(
                              BorderFactory.createEmptyBorder(2, 2, 2, 2),
                              BorderFactory.createEtchedBorder()));
      fillDatePanel(datePanel, resourceBundle);
      
      buildConstraints(constraints, 0, 2, 1, 1, 100, 38);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(datePanel, constraints);
      centerPanel.add(datePanel);
      
      mainPanel.add(centerPanel, BorderLayout.CENTER);

      // =====================================================
      // Font/PageLayout Panel.

      fontLayoutPanel = new JPanel(gridbag);
      fontLayoutPanel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(2, 2, 2, 2),
                                BorderFactory.createEtchedBorder()));
      fillFontPageLayoutPanel(fontLayoutPanel, resourceBundle);
      
      mainPanel.add(fontLayoutPanel, BorderLayout.SOUTH);
      
      add(mainPanel, BorderLayout.CENTER);

      // Button Action Options Panel
      buttonPanel = new JPanel();

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.button.RestoreDefaults",
                                                  "Restore Defaults");
      restoreDefaultsButton = new JButton(resource);
      restoreDefaultsButton.addActionListener(this);
      buttonPanel.add(restoreDefaultsButton);

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.button.Apply", "Apply");
      applyButton = new JButton(resource);
      applyButton.addActionListener(this);
      buttonPanel.add(applyButton);

      add(buttonPanel, BorderLayout.SOUTH);

      // Retrieve existing state and set accordingly.
      setPDFExportProperties(DBTablesPanel.getDataExportProperties());
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
         // Title Font & Header Font/Border Color Selection Action
         if (formSource == titleColorButton || formSource == headerColorButton
             || formSource == headerBorderColorButton)
         {
            JButton currentButton = (JButton) formSource;
            actionCommand = currentButton.getActionCommand();

            if (currentButton == titleColorButton)
            {
               panelColorChooser.setBorder(BorderFactory.createTitledBorder("Title Color"));
               panelColorChooser.setColor(titleColorButton.getBackground());
            }
            else if (currentButton == headerColorButton)
            {
               panelColorChooser.setBorder(BorderFactory.createTitledBorder("Header Color"));
               panelColorChooser.setColor(headerColorButton.getBackground());
            }
            else
            {
               panelColorChooser.setBorder(BorderFactory.createTitledBorder("Header Border Color"));
               panelColorChooser.setColor(headerBorderColorButton.getBackground());
            }

            // Create the color chooser dialog.
            JDialog dialog;
            dialog = JColorChooser.createDialog(this, "Color Selector", true, panelColorChooser, this, null);
            dialog.setVisible(true);
            dialog.dispose();
            return;
         }

         // Color Chooser Action
         if (((JButton) formSource).getText().equals("OK"))
         {
            applyButton.setEnabled(true);

            if (actionCommand.equals("Title Color"))
               titleColorButton.setBackground(panelColorChooser.getColor());
            else if (actionCommand.equals("Header Color"))
               headerColorButton.setBackground(panelColorChooser.getColor());
            else
               headerBorderColorButton.setBackground(panelColorChooser.getColor());

            actionCommand = "";
         }

         // Restore Defaults Button Action
         if (formSource == restoreDefaultsButton)
         {
            titleNoneRadioButton.setSelected(true);
            customTitleTextField.setText(DEFAULT_TITLE);
            customTitleTextField.setEnabled(false);
            titleFontSizeSpinner.setValue(Integer.valueOf(DEFAULT_TITLE_FONT_SIZE));
            titleColorButton.setBackground(Color.BLACK);
            headerFontSizeSpinner.setValue(DEFAULT_HEADER_FONT_SIZE);
            headerColorButton.setBackground(Color.BLACK);
            headerBorderSizeSpinner.setValue(DEFAULT_BORDER_SIZE);
            headerBorderColorButton.setBackground(Color.BLACK);
            numberAlignmentComboBox.setSelectedIndex(ALIGNMENT_RIGHT);
            dateFormatComboBox.setSelectedItem(DBTablesPanel.getGeneralDBProperties().getViewDateFormat());
            dateAlignmentComboBox.setSelectedIndex(ALIGNMENT_CENTER);
            fontComboBox.setSelectedItem(DEFAULT_FONT);
            pageLayoutComboBox.setSelectedIndex(LAYOUT_PORTRAIT);
         }

         // Apply Button Action
         else if (formSource == applyButton)
         {
            DBTablesPanel.setDataExportProperties(getPDFExportOptions());
            applyButton.setEnabled(false);
         }
      }

      // Triggering the apply button back to enabled
      // when option changes and controlling the
      // custom title textfield.
      
      if (formSource instanceof JRadioButton || formSource instanceof JComboBox)
      {
         applyButton.setEnabled(true);

         if (formSource instanceof JRadioButton)
         {
            if (titleCustomRadioButton.isSelected())
               customTitleTextField.setEnabled(true);
            else
               customTitleTextField.setEnabled(false);
         }
      }
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
   // Class Method to fill the title panel with the needed components.
   //================================================================

   private void fillTitlePanel(JPanel titlePanel, AResourceBundle resourceBundle)
   {
      // Class Method Instances
      JPanel titleSelectionPanel;
      JLabel titleLabel, titleFontSizeLabel, titleColorLabel;
      SpinnerNumberModel titleFontSizeSpinnerModel;
      String resource;

      // Title Label.

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.Title", "Title");
      titleLabel = new JLabel(resource);
      titleLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

      buildConstraints(constraints, 0, 0, 4, 1, 100, 33);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(titleLabel, constraints);
      titlePanel.add(titleLabel);

      // Title Selection.

      titleSelectionPanel = new JPanel(gridbag);
      titleSelectionPanel.setBorder(BorderFactory.createCompoundBorder(
                                          BorderFactory.createEmptyBorder(3, 0, 3, 0),
                                          BorderFactory.createLoweredBevelBorder()));

      ButtonGroup titleButtonGroup = new ButtonGroup();

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.radiobutton.None", "None");
      titleNoneRadioButton = new JRadioButton(resource, true);
      titleNoneRadioButton.setFocusPainted(false);
      titleNoneRadioButton.addActionListener(this);
      titleButtonGroup.add(titleNoneRadioButton);

      buildConstraints(constraints, 0, 0, 1, 1, 12, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(titleNoneRadioButton, constraints);
      titleSelectionPanel.add(titleNoneRadioButton);

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.radiobutton.Default",
                                                  "Default");
      titleDefaultRadioButton = new JRadioButton(resource, false);
      titleDefaultRadioButton.setFocusPainted(false);
      titleDefaultRadioButton.addActionListener(this);
      titleButtonGroup.add(titleDefaultRadioButton);

      buildConstraints(constraints, 1, 0, 1, 1, 12, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(titleDefaultRadioButton, constraints);
      titleSelectionPanel.add(titleDefaultRadioButton);

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.radiobutton.Custom",
                                                  "Custom");
      titleCustomRadioButton = new JRadioButton(resource, true);
      titleCustomRadioButton.setFocusPainted(false);
      titleCustomRadioButton.addActionListener(this);
      titleButtonGroup.add(titleCustomRadioButton);

      buildConstraints(constraints, 2, 0, 1, 1, 12, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(titleCustomRadioButton, constraints);
      titleSelectionPanel.add(titleCustomRadioButton);

      customTitleTextField = new JTextField(10);
      customTitleTextField.addMouseListener(Ajqvue.getPopupMenuListener());
      customTitleTextField.setEnabled(false);

      buildConstraints(constraints, 3, 0, 1, 1, 64, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(customTitleTextField, constraints);
      titleSelectionPanel.add(customTitleTextField);

      buildConstraints(constraints, 0, 1, 4, 1, 100, 33);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(titleSelectionPanel, constraints);
      titlePanel.add(titleSelectionPanel);

      // Title Font Size

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.TitleFontSize",
                                                  "Font Size");
      titleFontSizeLabel = new JLabel(resource);
      titleFontSizeLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 8, 0));

      buildConstraints(constraints, 0, 2, 1, 1, 12, 33);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.EAST;
      gridbag.setConstraints(titleFontSizeLabel, constraints);
      titlePanel.add(titleFontSizeLabel);

      titleFontSizeSpinnerModel = new SpinnerNumberModel(DEFAULT_TITLE_FONT_SIZE, MINIMUM_FONT_SIZE,
                                                         MAX_FONT_SIZE, SPINNER_SIZE_STEP);
      titleFontSizeSpinner = new JSpinner(titleFontSizeSpinnerModel);
      titleFontSizeSpinner.addChangeListener(this);

      buildConstraints(constraints, 1, 2, 1, 1, 38, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(titleFontSizeSpinner, constraints);
      titlePanel.add(titleFontSizeSpinner);

      // Title Font Color

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.TitleFontColor",
                                                  "Font Color");
      titleColorLabel = new JLabel(resource);
      titleColorLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 8, 0));

      buildConstraints(constraints, 2, 2, 1, 1, 12, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.EAST;
      gridbag.setConstraints(titleColorLabel, constraints);
      titlePanel.add(titleColorLabel);

      titleColorButton = new JButton(resourceBundle.getResourceImage(iconsDirectory
                                                                     + "transparentUpIcon.png"));
      titleColorButton.setActionCommand("Title Color");
      titleColorButton.setBackground(DEFAULT_COLOR);
      titleColorButton.setFocusable(false);
      titleColorButton.setMargin(new Insets(0, 0, 0, 0));
      titleColorButton.addActionListener(this);

      buildConstraints(constraints, 3, 2, 1, 1, 38, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(titleColorButton, constraints);
      titlePanel.add(titleColorButton);
   }

   //================================================================
   // Class Method to fill the header panel with the needed components.
   //================================================================

   private void fillHeaderPanel(JPanel headerPanel, AResourceBundle resourceBundle)
   {
      // Class Method Instances
      JLabel headerLabel;
      JLabel headerFontLabel, headerBorderLabel;
      SpinnerNumberModel headerFontSizeSpinnerModel;
      SpinnerNumberModel headerBorderSizeSpinnerModel;

      String resource;

      // Header Label.

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.HeaderColumns",
                                                  "Header Columns");
      headerLabel = new JLabel(resource);
      headerLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

      buildConstraints(constraints, 0, 0, 6, 1, 100, 50);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(headerLabel, constraints);
      headerPanel.add(headerLabel);

      // Header Font Size

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.HeaderFont",
                                                  "Font");
      headerFontLabel = new JLabel(resource);
      headerFontLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

      buildConstraints(constraints, 0, 1, 1, 1, 18, 50);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.EAST;
      gridbag.setConstraints(headerFontLabel, constraints);
      headerPanel.add(headerFontLabel);

      headerFontSizeSpinnerModel = new SpinnerNumberModel(DEFAULT_HEADER_FONT_SIZE, MINIMUM_FONT_SIZE,
                                                          MAX_FONT_SIZE, SPINNER_SIZE_STEP);
      headerFontSizeSpinner = new JSpinner(headerFontSizeSpinnerModel);
      headerFontSizeSpinner.addChangeListener(this);

      buildConstraints(constraints, 1, 1, 1, 1, 16, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(headerFontSizeSpinner, constraints);
      headerPanel.add(headerFontSizeSpinner);

      // Header Color

      headerColorButton = new JButton(resourceBundle.getResourceImage(iconsDirectory
                                                                      + "transparentUpIcon.png"));
      headerColorButton.setActionCommand("Header Color");
      headerColorButton.setBackground(Color.BLACK);
      headerColorButton.setFocusable(false);
      headerColorButton.setMargin(new Insets(0, 0, 0, 0));
      headerColorButton.addActionListener(this);

      buildConstraints(constraints, 2, 1, 1, 1, 16, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(headerColorButton, constraints);
      headerPanel.add(headerColorButton);

      // Header Border Size

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.HeaderBorder",
                                                  "Border");
      headerBorderLabel = new JLabel(resource);
      headerBorderLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

      buildConstraints(constraints, 3, 1, 1, 1, 18, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.EAST;
      gridbag.setConstraints(headerBorderLabel, constraints);
      headerPanel.add(headerBorderLabel);

      headerBorderSizeSpinnerModel = new SpinnerNumberModel(DEFAULT_BORDER_SIZE, MINIMUM_BORDER_SIZE,
                                                            MAX_BORDER_SIZE, SPINNER_SIZE_STEP);
      headerBorderSizeSpinner = new JSpinner(headerBorderSizeSpinnerModel);
      headerBorderSizeSpinner.addChangeListener(this);

      buildConstraints(constraints, 4, 1, 1, 1, 16, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(headerBorderSizeSpinner, constraints);
      headerPanel.add(headerBorderSizeSpinner);

      // Header Border Color

      headerBorderColorButton = new JButton(resourceBundle.getResourceImage(iconsDirectory
                                                                            + "transparentUpIcon.png"));
      headerBorderColorButton.setActionCommand("Border Color");
      headerBorderColorButton.setBackground(DEFAULT_COLOR);
      headerBorderColorButton.setFocusable(false);
      headerBorderColorButton.setMargin(new Insets(0, 0, 0, 0));
      headerBorderColorButton.addActionListener(this);

      buildConstraints(constraints, 5, 1, 1, 1, 16, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(headerBorderColorButton, constraints);
      headerPanel.add(headerBorderColorButton);
   }
   
   //================================================================
   // Class Method to fill the Number Panel with the needed components.
   //================================================================

   private void fillNumberPanel(JPanel numberPanel, AResourceBundle resourceBundle)
   {
      // Class Method Instances
      String resource;
      JLabel numberFieldsAlignmentLabel;

      // Number Fields Alignment Label.

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.NumberFieldsAlignment",
                                                  "Number Fields Alignment");
     numberFieldsAlignmentLabel = new JLabel(resource);
      numberFieldsAlignmentLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 10, 10));

      buildConstraints(constraints, 0, 1, 1, 1, 50, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.EAST;
      gridbag.setConstraints(numberFieldsAlignmentLabel, constraints);
      numberPanel.add(numberFieldsAlignmentLabel);

      numberAlignmentComboBox = new JComboBox<Object>();
      
      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.combobox.Left", "LEFT");
      numberAlignmentComboBox.addItem(resource);
      
      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.combobox.Center", "Center");
      numberAlignmentComboBox.addItem(resource);
      
      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.combobox.Right", "Right");
      numberAlignmentComboBox.addItem(resource);
         
      numberAlignmentComboBox.setSelectedIndex(ALIGNMENT_RIGHT);
      numberAlignmentComboBox.addActionListener(this);

      buildConstraints(constraints, 1, 1, 1, 1, 50, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(numberAlignmentComboBox, constraints);
      numberPanel.add(numberAlignmentComboBox);
   }

   //================================================================
   // Class Method to create the Date Options Components.
   //================================================================

   private void fillDatePanel(JPanel datePanel, AResourceBundle resourceBundle)
   {
      // Class Method Instances
      String resource;
      JLabel dateLabel, dateAlignmentLabel, dateFormatLabel;

      // Date Field Label.

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.DateFields",
                                                  "Date Fields");
      dateLabel = new JLabel(resource);
      dateLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

      buildConstraints(constraints, 0, 0, 4, 1, 100, 50);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(dateLabel, constraints);
      datePanel.add(dateLabel);

      // Date Format & Alignment.

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.DateFormat",
                                                  "Format");
      dateFormatLabel = new JLabel(resource);
      dateFormatLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

      buildConstraints(constraints, 0, 1, 1, 1, 12, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(dateFormatLabel, constraints);
      datePanel.add(dateFormatLabel);

      dateFormatComboBox = new JComboBox<Object>(Utils.getDateFormatOption());
      dateFormatComboBox.addActionListener(this);

      buildConstraints(constraints, 1, 1, 1, 1, 38, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(dateFormatComboBox, constraints);
      datePanel.add(dateFormatComboBox);

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.DateAlignment",
                                                  "Alignment");
      dateAlignmentLabel = new JLabel(resource);
      dateAlignmentLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

      buildConstraints(constraints, 2, 1, 1, 1, 12, 50);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(dateAlignmentLabel, constraints);
      datePanel.add(dateAlignmentLabel);

      dateAlignmentComboBox = new JComboBox<Object>();
      
      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.combobox.Left", "Left");
      dateAlignmentComboBox.addItem(resource);
      
      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.combobox.Center", "Center");
      dateAlignmentComboBox.addItem(resource);
      
      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.combobox.Right", "Right");
      dateAlignmentComboBox.addItem(resource);
      
      dateAlignmentComboBox.setSelectedIndex(ALIGNMENT_CENTER);
      dateAlignmentComboBox.addActionListener(this);

      buildConstraints(constraints, 3, 1, 1, 1, 38, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(dateAlignmentComboBox, constraints);
      datePanel.add(dateAlignmentComboBox);
   }
   
   //================================================================
   // Class Method to fill the Font/Page Layout Panel with the needed
   // components.
   //================================================================

   private void fillFontPageLayoutPanel(JPanel fontLayoutPanel, AResourceBundle resourceBundle)
   {
      // Class Method Instances
      String resource;
      JLabel fontLabel, pageLayoutLabel;
      
      // Font Selector.

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.Font", "Font");
      fontLabel = new JLabel(resource);
      fontLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

      buildConstraints(constraints, 0, 0, 1, 1, 12, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(fontLabel, constraints);
      fontLayoutPanel.add(fontLabel);

      fontComboBox = new JComboBox<Object>(DBTablesPanel.getDataExportProperties().getFonts());
      fontComboBox.addActionListener(this);

      buildConstraints(constraints, 1, 0, 1, 1, 38, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(fontComboBox, constraints);
      fontLayoutPanel.add(fontComboBox);

      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.label.PageLayout",
                                                  "Page Layout");
      pageLayoutLabel = new JLabel(resource);
      pageLayoutLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

      buildConstraints(constraints, 2, 0, 1, 1, 12, 50);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(pageLayoutLabel, constraints);
      fontLayoutPanel.add(pageLayoutLabel);
      
      pageLayoutComboBox = new JComboBox<Object>();
      
      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.combobox.Portrait",
                                                  "Portrait");
      pageLayoutComboBox.addItem(resource);
      
      resource = resourceBundle.getResourceString("PDFExportPreferencesPanel.combobox.Landscape",
                                                  "Landscape");
      pageLayoutComboBox.addItem(resource);
         
      pageLayoutComboBox.setSelectedIndex(LAYOUT_PORTRAIT);
      pageLayoutComboBox.addActionListener(this);

      buildConstraints(constraints, 3, 0, 1, 1, 38, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(pageLayoutComboBox, constraints);
      fontLayoutPanel.add(pageLayoutComboBox);  
   }

   //================================================================
   // Class Method for helping the parameters in gridbag.
   //================================================================

   private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, double wx, double wy)
   {
      gbc.gridx = gx;
      gbc.gridy = gy;
      gbc.gridwidth = gw;
      gbc.gridheight = gh;
      gbc.weightx = wx;
      gbc.weighty = wy;
   }

   //===============================================================
   // Class method to get the PDF Export Properties.
   //===============================================================

   public DataExportProperties getPDFExportOptions()
   {

      DataExportProperties newDataProperties = DBTablesPanel.getDataExportProperties();

      // Title Options

      if (titleNoneRadioButton.isSelected())
         newDataProperties.setTitle("");
      else if (titleDefaultRadioButton.isSelected())
         newDataProperties.setTitle("EXPORTED TABLE");
      else
         newDataProperties.setTitle(customTitleTextField.getText());

      newDataProperties.setTitleFontSize(Integer.parseInt(titleFontSizeSpinner.getValue().toString()));
      newDataProperties.setTitleColor(titleColorButton.getBackground());
      
      // Header Options

      newDataProperties.setHeaderFontSize(Integer.parseInt(headerFontSizeSpinner.getValue().toString()));
      newDataProperties.setHeaderColor(headerColorButton.getBackground());
      newDataProperties.setHeaderBorderSize(Integer.parseInt(headerBorderSizeSpinner.getValue().toString()));
      newDataProperties.setHeaderBorderColor(headerBorderColorButton.getBackground());

      // Number Alignment Option

      newDataProperties.setNumberAlignment(numberAlignmentComboBox.getSelectedIndex());

      // Date Options

      newDataProperties.setPDFDateFormat((String) dateFormatComboBox.getSelectedItem());
      newDataProperties.setDateAlignment(dateAlignmentComboBox.getSelectedIndex());
      
      // Font & Page Layout Options
      
      newDataProperties.setPageLayout(pageLayoutComboBox.getSelectedIndex());
      newDataProperties.setFontName((String) fontComboBox.getSelectedItem());
      
      return newDataProperties;
   }

   //========================================================
   // Class method to set the PDF Export Properties.
   //========================================================
   
   public void setPDFExportProperties(DataExportProperties dataProperties)
   {
      // Title Options

      if (dataProperties.getTitle().equals(""))
         titleNoneRadioButton.setSelected(true);
      else if (dataProperties.getTitle().equals("EXPORTED TABLE"))
         titleDefaultRadioButton.setSelected(true);
      else
      {
         titleCustomRadioButton.setSelected(true);
         customTitleTextField.setText(dataProperties.getTitle());
         customTitleTextField.setEnabled(true);
      }
       
      titleFontSizeSpinner.setValue(Integer.valueOf(dataProperties.getTitleFontSize()));
      titleColorButton.setBackground(dataProperties.getTitleColor());

      // Header Options

      headerFontSizeSpinner.setValue(Integer.valueOf(dataProperties.getHeaderFontSize()));
      headerColorButton.setBackground(dataProperties.getHeaderColor());
      headerBorderSizeSpinner.setValue(Integer.valueOf(dataProperties.getHeaderBorderSize()));
      headerBorderColorButton.setBackground(dataProperties.getHeaderBorderColor());

      // Number Alignment Option

      numberAlignmentComboBox.setSelectedIndex(dataProperties.getNumberAlignment());

      // Date Options

      dateFormatComboBox.setSelectedItem(dataProperties.getPDFDateFormat());
      dateAlignmentComboBox.setSelectedIndex(dataProperties.getDateAlignment());
      
      // Font & Page Layout Options
      
      pageLayoutComboBox.setSelectedIndex(dataProperties.getPageLayout());
      fontComboBox.setSelectedItem(dataProperties.getFontName());
   }
}
