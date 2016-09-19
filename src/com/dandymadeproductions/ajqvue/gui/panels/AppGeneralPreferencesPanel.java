//=================================================================
//                  AppGeneralPreferencesPanel
//=================================================================
//
//    This class provides a generic panel in the appearance of a
// form for selecting application localization and font paramerters.
//
//             << AppGeneralPreferencesPanel.java >>
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
// Version 1.0 09/18/2016 Production AppGeneralPreferencesPanel Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The AppGeneralPreferencesPanel class provides a generic panel in
 * the appearance of a form for selecting application localization and
 * font paramerters.   
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/19/2016
 */

public class AppGeneralPreferencesPanel extends JPanel
{
   // Class Instances.
   //private static final long serialVersionUID = 5287224058215428943L;
   private JComboBox<Object> localizationComboBox;
   
   private JSpinner fontSizeSpinner;
   
   //===========================================================
   // AppGeneralPreferencesPanel Constructor
   //===========================================================

   public AppGeneralPreferencesPanel()
   {
      // Constructor Instances
      AResourceBundle resourceBundle;
      JPanel localizationPanel, fontSizePanel, fontSpinnerPanel;
      
      ImageIcon localeIcon, fontSizeIcon;
      JLabel localizationLabel, fontSizeLabel;
      
      int currentFontSize;
      String fileSeparator, iconsDirectory;
      String resource, resourceLanguage;
      
      // Setup
      resourceBundle = Ajqvue.getResourceBundle();
      fileSeparator = Utils.getFileSeparator();
      iconsDirectory = Utils.getIconsDirectory() + fileSeparator;
      
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();
      
      setLayout(new GridLayout(2, 1, 2, 2));
      setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0),
                BorderFactory.createLoweredBevelBorder()));
      
      Object uiObject = UIManager.get("Label.font");
      
      if (uiObject != null && uiObject instanceof Font)
      {
         Font uiManagerFont = (Font) uiObject;
         currentFontSize = uiManagerFont.getSize();
      }
      else
         currentFontSize = 12;
      
      // Create the components for the preferences selector
      // dialog.
      
      SpinnerNumberModel fontSizeSpinnerModel;
      final int minimumFontSize = 8;
      final int maxLimitFontSize = 24;
      final int spinnerFontSizeStep = 1;

      // =====================================================
      // Localization Selector Panel & Components
      
      localizationPanel = new JPanel(gridbag);
      localizationPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(3, 3, 3, 3), BorderFactory.createEtchedBorder()));
      
      localeIcon = resourceBundle.getResourceImage(iconsDirectory + "localeIcon.gif");
      JLabel localeIconLabel = new JLabel(localeIcon);
      localeIconLabel.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
      
      Utils.buildConstraints(constraints, 0, 0, 1, 1, 34, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(localeIconLabel, constraints);
      localizationPanel.add(localeIconLabel);
      
      resourceLanguage = resourceBundle.getResourceString("AppGeneralPreferencesPanel.label.Language",
                                                          "Language");
      resource = resourceBundle.getResourceString("AppGeneralPreferencesPanel.label.RestartRequired",
                                                  "Restart Required");
      
      localizationLabel = new JLabel(resourceLanguage + " ( " + resource + " ) ");
      localizationLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
      
      Utils.buildConstraints(constraints, 1, 0, 1, 1, 33, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(localizationLabel, constraints);
      localizationPanel.add(localizationLabel);

      localizationComboBox = new JComboBox<Object>(getLocaleList().toArray());
      localizationComboBox.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 4));
      localizationComboBox.setSelectedItem(Ajqvue.getLocaleString());

      Utils.buildConstraints(constraints, 2, 0, 1, 1, 33, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(localizationComboBox, constraints);
      localizationPanel.add(localizationComboBox);

      add(localizationPanel);
      
      // =====================================================
      // Font Size Selector Panel & Components
      
      fontSizePanel = new JPanel(gridbag);
      fontSizePanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(3, 3, 3, 3), BorderFactory.createEtchedBorder()));
      
      fontSizeIcon = resourceBundle.getResourceImage(iconsDirectory + "fontSizeIcon.gif");
      JLabel fontSizeIconLabel = new JLabel(fontSizeIcon);
      fontSizeIconLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 5));
      
      Utils.buildConstraints(constraints, 0, 0, 1, 1, 20, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(fontSizeIconLabel, constraints);
      fontSizePanel.add(fontSizeIconLabel);
      
      fontSpinnerPanel = new JPanel();
      
      resource = resourceBundle.getResourceString("AppGeneralPreferencesPanel.label.FontSize",
                                                          "Font Size");
      fontSizeLabel = new JLabel(resource);
      fontSizeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
      
      fontSpinnerPanel.add(fontSizeLabel);
      
      fontSizeSpinnerModel = new SpinnerNumberModel(currentFontSize, minimumFontSize,
                                                    maxLimitFontSize, spinnerFontSizeStep);
      fontSizeSpinner = new JSpinner(fontSizeSpinnerModel);
      
      fontSpinnerPanel.add(fontSizeSpinner);
      
      Utils.buildConstraints(constraints, 1, 0, 1, 1, 80, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(fontSpinnerPanel, constraints);
      fontSizePanel.add(fontSpinnerPanel);

      add(fontSizePanel);  
   }
   
   //===============================================================
   // Class method to collect the localization languages supported.
   //===============================================================

   private ArrayList<String> getLocaleList()
   {
      // Method Instances
      File localeFileDirectory;
      String[] localeFileNames;
      ArrayList<String> localesData;
      int lastIndexOfDot;

      // Setup the instances and required data to start.
      
      localeFileDirectory = new File(Utils.getAjqvueDirectory() + Utils.getFileSeparator()
                                     + "locale" + Utils.getFileSeparator());
      localesData = new ArrayList <String>();

      try
      {
         if (localeFileDirectory.exists() && localeFileDirectory.isDirectory())
         {
            // Collect the locale file names from the
            // locale directory.
            localeFileNames = localeFileDirectory.list();

            if (localeFileNames != null)
            {
               for (int i = 0; i < localeFileNames.length; i++)
               {
                  lastIndexOfDot = localeFileNames[i].lastIndexOf(".");
                  // System.out.println(localeFileNames[i]);

                  if (lastIndexOfDot > 0
                      && (localeFileNames[i].substring(lastIndexOfDot + 1).equals("properties")))
                     localesData.add(localeFileNames[i].substring((lastIndexOfDot - 5), lastIndexOfDot));
               }
            }
         }
      }
      catch (SecurityException se)
      {
         if (Ajqvue.getDebug())
            System.out.println("AppGeneralPreferencesPanel getLocaleList() " + se);
      }
      return localesData;
   }
   
   //===============================================================
   // Class method to collect the localization languages selected.
   //===============================================================
   
   public String getLocalization()
   {
      return (String) localizationComboBox.getSelectedItem();
   }
   
   //===============================================================
   // Class method to collect the font size selection.
   //===============================================================
   
   public int getFontSize()
   {
      return Integer.parseInt(fontSizeSpinner.getValue().toString());
   }
   
   //===============================================================
   // Class method to collect the localization languages supported.
   //===============================================================

   public void setLocalization(String localizationString) throws IOException
   {
      // Method Instances
      String fileSeparator;
      String localeFileName, localeFileString;
      File localeFile;
      FileWriter fileWriter;
      BufferedWriter bufferedWriter;
      
      fileSeparator = Utils.getFileSeparator();
      localeFileName = "ajqvue_locale.txt";
      localeFileString = Utils.getAjqvueConfDirectory() + fileSeparator
                         + localeFileName;
      fileWriter = null;
      bufferedWriter = null;
      
      try
      {
         localeFile = new File(localeFileString);
         fileWriter = new FileWriter(localeFile);
         bufferedWriter = new BufferedWriter(fileWriter);
         
         bufferedWriter.write(localizationString);
         bufferedWriter.flush();
         fileWriter.flush();
      }
      catch (IOException ioe)
      {
         if (Ajqvue.getDebug())
            System.out.println("AppGeneralPreferencesPanel setLocalization() " + ioe);
      }
      finally
      {
         try
         {
            if (bufferedWriter != null)
               bufferedWriter.close();
         }
         catch (IOException ioe)
         {
            if (Ajqvue.getDebug())
               System.out.println("AppGeneralPreferencesPanel setLocalization() " + ioe);
         }
         finally
         {
            try
            {
               if (fileWriter != null)
                  fileWriter.close();
            }
            catch (IOException ioe)
            {
               if (Ajqvue.getDebug())
                  System.out.println("AppGeneralPreferencesPanel setLocalization() " + ioe);
            }
         }  
      }
   }  
}