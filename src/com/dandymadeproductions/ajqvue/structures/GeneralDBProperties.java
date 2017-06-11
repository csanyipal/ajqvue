//=================================================================
//                 GeneralDBProperties Class
//=================================================================
// This class provides the structure for the application general
// database parameters properties storage.
//
//              << GeneralDBProperties.java >>
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
// Version 1.0 Production GeneralProperties Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.structures;

import java.util.prefs.Preferences;

import com.dandymadeproductions.ajqvue.gui.panels.GeneralPreferencesPanel;

/**
 *    The GeneralDBProperties class provides the structure for the
 * application general database parameters properties storage.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class GeneralDBProperties
{
   // Class Instances.
   private String viewDateFormat;
   private int limitIncrement;
   private boolean batchSizeEnabled;
   private int batchSize;
   
   private Preferences generalDBPreferences;

   public static final String VIEWDATEFORMAT = "ViewDateFormat";
   public static final String LIMITINCREMENT = "LimitIncrement";
   public static final String BATCHSIZEENABLED = "BatchSizeEnabled";
   public static final String BATCHSIZE = "BatchSize";
   
   //==============================================================
   // GeneralDBProperties Constructor
   //==============================================================

   public GeneralDBProperties()
   {
      // Set Default State.
      
      viewDateFormat = "MM-dd-YYYY";
      limitIncrement = GeneralPreferencesPanel.DEFAULT_LIMIT_INCREMENT;
      batchSizeEnabled = GeneralPreferencesPanel.DEFAULT_BATCH_SIZE_ENABLED;
      batchSize = GeneralPreferencesPanel.DEFAULT_BATCH_SIZE;
      
      // Try to retrieve state from Preferences.
      try
      {
         generalDBPreferences = Preferences.userNodeForPackage(GeneralDBProperties.class);
      }
      catch (SecurityException se){return;}
      
      try
      {
         viewDateFormat = generalDBPreferences.get(VIEWDATEFORMAT, "MM-DD-YYYY");
         limitIncrement = generalDBPreferences.getInt(LIMITINCREMENT, limitIncrement);
         batchSizeEnabled = generalDBPreferences.getBoolean(BATCHSIZEENABLED, batchSizeEnabled);
         batchSize = generalDBPreferences.getInt(BATCHSIZE, batchSize);
      }
      catch (NullPointerException npe){}
      catch (IllegalStateException ise){}
   }

   //==============================================================
   // Class methods to allow classes to get the general object
   // components.
   //==============================================================

   public String getViewDateFormat()
   {
      return viewDateFormat;
   }
   
   public int getLimitIncrement()
   {
      return limitIncrement;
   }
   
   public boolean getBatchSizeEnabled()
   {
      return batchSizeEnabled;
   }
   
   public int getBatchSize()
   {
      return batchSize;
   }
   
   //==============================================================
   // Class methods to allow classes to set the data export
   // object components.
   //==============================================================
   
   public void setViewDateFormat(String content)
   {
      viewDateFormat = content;
      savePreference(VIEWDATEFORMAT, content);
   }
   
   public void setLimitIncrement(int value)
   {
      limitIncrement = value;
      savePreference(LIMITINCREMENT, value);
   }
   
   public void setBatchSizeEnabled(boolean value)
   {
      batchSizeEnabled = value;
      savePreference(BATCHSIZEENABLED, value);
   }
   
   public void setBatchSize(int value)
   {
      batchSize = value;
      savePreference(BATCHSIZE, value);
   }
   
   //==============================================================
   // Class methods to try and save the preferences state. 
   //==============================================================

   private void savePreference(String key, boolean value)
   {
      try
      {
         if (generalDBPreferences != null)
            generalDBPreferences.putBoolean(key, value);
      }
      catch (IllegalArgumentException iae){}
      catch (IllegalStateException ise){}
   }
   
   private void savePreference(String key, String content)
   {
      try
      {
         if (generalDBPreferences != null)
            generalDBPreferences.put(key, content);
      }
      catch (IllegalArgumentException iae){}
      catch (IllegalStateException ise){}
   }
   
   private void savePreference(String key, int value)
   {
      try
      {
         if (generalDBPreferences != null)
            generalDBPreferences.putInt(key, value);
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
      StringBuffer parameters = new StringBuffer("[GeneralDBProperties: ");
      
      parameters.append("[viewDataFormat = " + viewDateFormat + "]");
      parameters.append("[limitIncrement = " + limitIncrement + "]");
      parameters.append("[batchSize = " + batchSize + "]");

      return parameters.toString();
   }
}
