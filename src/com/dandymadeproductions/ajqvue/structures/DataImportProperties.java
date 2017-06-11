//=================================================================
//              DataImportProperties Class
//=================================================================
//	This class provides the structure for the data import
// properties storage.
//
//           << DataImportProperties.java >>
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
// Version 1.0 Production DataImportProperties Class.
//
//-----------------------------------------------------------------
//                  danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.structures;

import java.util.prefs.Preferences;

import com.dandymadeproductions.ajqvue.gui.panels.CSVImportPreferencesPanel;
import com.dandymadeproductions.ajqvue.gui.panels.SQLImportPreferencesPanel;

/**
 *    The DataImportProperties class provides the structure for
 * data import properties storage.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class DataImportProperties
{
   // Class Instances.
   private boolean identityInsert;
   private String dataDelimiter;
   private String dateFormat;
   
   private Preferences dataImportPreferences;
   
   // SQL
   private static final String IDENTITYINSERT = "ImportIdentityInsert";
   
   // CSV
   private static final String DATADELIMITER = "ImportDataDelimiter";
   private static final String DATEFORMAT = "ImportDateFormat";

   //==============================================================
   // DataImportProperties Constructor
   //==============================================================

   public DataImportProperties()
   {  
      // Set default state.
      
      // SQL
      identityInsert = SQLImportPreferencesPanel.DEFAULT_IDENTITY_INSERT;
      
      // CSV
      dataDelimiter = CSVImportPreferencesPanel.DEFAULT_DATA_DELIMITER;
      dateFormat = CSVImportPreferencesPanel.DEFAULT_DATE_FORMAT;
      
      // Try to retrieve state from Preferences.
      try
      {
         dataImportPreferences = Preferences.userNodeForPackage(DataImportProperties.class);
      }
      catch (SecurityException se){return;}
      
      try
      {
         identityInsert = dataImportPreferences.getBoolean(IDENTITYINSERT, identityInsert);
         dataDelimiter = dataImportPreferences.get(DATADELIMITER, dataDelimiter);
         dateFormat = dataImportPreferences.get(DATEFORMAT, dateFormat);
      }
      catch (NullPointerException npe){}
      catch (IllegalStateException ise){}
   }
   
   //==============================================================
   // Class methods to allow classes to get the data import
   // object components.
   //==============================================================

   // SQL
   public boolean getIdentityInsert()
   {
      return identityInsert;
   }
   
   // CSV
   public String getDataDelimiter()
   {
      return dataDelimiter;
   }
   
   public String getDateFormat()
   {
      return dateFormat;
   }

   //==============================================================
   // Class methods to allow classes to set the data import
   // object components.
   //==============================================================

   // SQL
   public void setIdentityInsert(boolean value)
   {
      identityInsert = value;
      savePreference(IDENTITYINSERT, value);
   }
   
   // CSV
   public void setDataDelimiter(String content)
   {
      dataDelimiter = content;
      savePreference(DATADELIMITER, content);
   }
   
   public void setDateFormat(String content)
   {
      dateFormat = content;
      savePreference(DATEFORMAT, content);
   }
   
   //==============================================================
   // Class methods to try and save the preferences state. 
   //==============================================================

   private void savePreference(String key, boolean value)
   {
      try
      {
         if (dataImportPreferences != null)
            dataImportPreferences.putBoolean(key, value);
      }
      catch (IllegalArgumentException iae){}
      catch (IllegalStateException ise){}
   }
   
   
   private void savePreference(String key, String content)
   {
      try
      {
         if (dataImportPreferences != null)
            dataImportPreferences.put(key, content);
      }
      catch (IllegalArgumentException iae){}
      catch (IllegalStateException ise){}
   }

   //==============================================================
   // Class method to properly implement the toString() method
   // for the object. Local method overides.
   //==============================================================

   public String toString()
   {
      StringBuffer parameters = new StringBuffer("[DataImportProperties: ");
      parameters.append("[identityInsert = " + identityInsert + "]");
      parameters.append("[dataDelimiter = " + dataDelimiter + "]");
      parameters.append("[dataFormat = " + dateFormat + "]");

      return parameters.toString();
   }
}
