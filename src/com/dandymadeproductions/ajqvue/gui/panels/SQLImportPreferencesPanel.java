//=================================================================
//                  SQLImportPreferencesPanel
//=================================================================
//
//    This class provides a generic panel in the appearance of
// a form for selecting the SQL data import options.
//
//             << SQLImportPreferencesPanel.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.0 01/18/2016
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
// Version 1.0 09/18/2016 Production SQLImportPreferencesPanel Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.structures.DataImportProperties;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;

/**
 *    The SQLImportPreferencesPanel class provides a generic panel in
 * the appearance of a form for selecting the SQL data import options.
 * 
 * @author Dana M. Proctor
 * @version 1.0 02/01/2014
 */

public class SQLImportPreferencesPanel extends JPanel implements ActionListener
{
   // Class Instances.
   private static final long serialVersionUID = -7558028181502132490L;
   
   private JCheckBox identityInsertCheckBox;
   private JButton restoreDefaultsButton, applyButton;
   
   public static final boolean DEFAULT_IDENTITY_INSERT = false;

   //===========================================================
   // CSVImportPreferencesPanel Constructor
   //===========================================================

   public SQLImportPreferencesPanel(AResourceBundle resourceBundle)
   {
      // Class Instances
      JPanel mainPanel, fillerDataPanel;
      GraphicsCanvasPanel fillerPanel;
      JPanel dataOptionsPanel;
      JPanel buttonPanel;
      
      String resource;

      // Setting up
      setLayout(new BorderLayout());

      mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createCompoundBorder(
                                 BorderFactory.createEmptyBorder(0, 0, 0, 0),
                                 BorderFactory.createLoweredBevelBorder()));
      
      fillerDataPanel = new JPanel(new GridLayout(2, 1, 0, 0));
      fillerDataPanel.setBorder(BorderFactory.createEmptyBorder());

      // ==================================================
      // Filler Panel.
      
      fillerPanel = new GraphicsCanvasPanel("sqlImport.jpg");;
      fillerPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(0, 0, 0, 0),
         BorderFactory.createLoweredBevelBorder()));
      fillerDataPanel.add(fillerPanel);
      
      // =====================================================
      //  Data Panel & Components
      
      dataOptionsPanel = new JPanel();
      dataOptionsPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createEtchedBorder()));
 
      resource = resourceBundle.getResourceString("SQLImportPreferencesPanel.checkbox.IdentityInsert",
            "Identity Insert");
      identityInsertCheckBox = new JCheckBox(resource, DEFAULT_IDENTITY_INSERT);
      identityInsertCheckBox.setFocusPainted(false);
      identityInsertCheckBox.addActionListener(this);
      
      dataOptionsPanel.add(identityInsertCheckBox);
      fillerDataPanel.add(dataOptionsPanel);
      
      mainPanel.add(fillerDataPanel, BorderLayout.CENTER);
      
      add(mainPanel, BorderLayout.CENTER);

      // Button Action Options Panel
      buttonPanel = new JPanel();
      
      resource = resourceBundle.getResourceString("CSVImportPreferencesPanel.button.RestoreDefaults",
                                                  "Restore Defaults");
      restoreDefaultsButton = new JButton(resource);
      restoreDefaultsButton.addActionListener(this);
      buttonPanel.add(restoreDefaultsButton);

      resource = resourceBundle.getResourceString("CSVImportPreferencesPanel.button.Apply", "Apply");
      applyButton = new JButton(resource);
      applyButton.addActionListener(this);
      buttonPanel.add(applyButton);

      add(buttonPanel, BorderLayout.SOUTH);
      
      // Retrieve existing state and set accordingly.
      setSQLImportProperties(DBTablesPanel.getDataImportProperties());
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
            identityInsertCheckBox.setSelected(DEFAULT_IDENTITY_INSERT);
            applyButton.setEnabled(true);
         }

         // Apply Button Action
         else if (formSource == applyButton)
         {
            DBTablesPanel.setDataImportProperties(getSQLImportOptions());
            applyButton.setEnabled(false);
         }
      }

      // Triggering the apply button back to enabled
      // when option changes.
      if (formSource instanceof JCheckBox)
      {
         applyButton.setEnabled(true);
      }
   }

   //===============================================================
   // Class method to get the data import properties.
   //===============================================================

   public DataImportProperties getSQLImportOptions()
   {
      DataImportProperties newDataProperties = DBTablesPanel.getDataImportProperties();

      // Identity Insert
      newDataProperties.setIdentityInsert(identityInsertCheckBox.isSelected());

      return newDataProperties;
   }

   //===============================================================
   // Class method to set the data import properties.
   //===============================================================

   public void setSQLImportProperties(DataImportProperties dataProperties)
   {
      // Identitiy Insert
      identityInsertCheckBox.setSelected(dataProperties.getIdentityInsert());
   }
}