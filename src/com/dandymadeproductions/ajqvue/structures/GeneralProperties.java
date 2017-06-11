//=================================================================
//                 GeneralProperties Class
//=================================================================
// This class provides the structure for the application general
// properties storage.
//
//              << GeneralProperties.java >>
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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import com.dandymadeproductions.ajqvue.gui.Main_Frame;
import com.dandymadeproductions.ajqvue.gui.panels.GeneralPreferencesPanel;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The GeneralProperties class provides the structure for the
 * application general properties storage.
 * 
 * @author Dana M. Proctor
 * @version 2.3 05/19/2016
 */

public class GeneralProperties
{
   // Class Instances.
   private int fontSize;
   private int framePosition_X, framePosition_Y;
   private int frameWidth, frameHeight;
   private int queryFramePosition_X, queryFramePosition_Y;
   private int queryFrameWidth, queryFrameHeight;
   private String sequenceList;
   private boolean enableProxy;
   private String proxyAddress;
   private int proxyPort;
   
   private Preferences generalPreferences;
   
   public static final String APPFONTSIZE = "AppFontSize";
   public static final String APPFRAMEPOSITIONX = "AppFramePositionX";
   public static final String APPFRAMEPOSITIONY = "AppFramePositionY";
   public static final String APPFRAMEWIDTH = "AppFrameWidth";
   public static final String APPFRAMEHEIGHT = "AppFrameHeight";
   public static final String APPQUERYFRAMEPOSITIONX = "AppQueryFramePositionX";
   public static final String APPQUERYFRAMEPOSITIONY = "AppQueryFramePositionY";
   public static final String APPQUERYFRAMEWIDTH = "AppQueryFrameWidth";
   public static final String APPQUERYFRAMEHEIGHT = "AppQueryFrameHeight";
   public static final String APPSEQUENCELIST = "AppSequenceList";
   public static final String APPENABLEPROXY = "AppEnableProxy";
   public static final String APPPROXYADDRESS = "AppProxyAddress";
   public static final String APPPROXYPORT = "AppProxyPort";
   
   //==============================================================
   // GeneralProperties Constructor
   //==============================================================

   public GeneralProperties()
   {
      // Set Default State.
      
      // Font Size
      Object uiObject = null;
      uiObject = UIManager.get("Label.font");
      
      if (uiObject != null && uiObject instanceof Font)
      {
         Font uiManagerFont = (Font) uiObject;
         fontSize = uiManagerFont.getSize();
      }
      else
         fontSize = 12;
      
      framePosition_X = 0;
      framePosition_Y = 0;
      frameWidth = Main_Frame.FRAME_DEFAULT_WIDTH;
      frameHeight = Main_Frame.FRAME_DEFAULT_HEIGHT;
      queryFramePosition_X = 0;
      queryFramePosition_Y = 0;
      queryFrameWidth = Main_Frame.FRAME_DEFAULT_WIDTH;
      queryFrameHeight = Main_Frame.FRAME_DEFAULT_HEIGHT;
      sequenceList = null;
      enableProxy = false;
      proxyAddress = "";
      proxyPort = 0;
      
      // Try to retrieve state from Preferences.
      try
      {
         generalPreferences = Preferences.userNodeForPackage(GeneralProperties.class);
      }
      catch (SecurityException se){return;}
      
      try
      {
         fontSize = generalPreferences.getInt(APPFONTSIZE, fontSize);
         framePosition_X = generalPreferences.getInt(APPFRAMEPOSITIONX, framePosition_X);
         framePosition_Y = generalPreferences.getInt(APPFRAMEPOSITIONY, framePosition_Y);
         frameWidth = generalPreferences.getInt(APPFRAMEWIDTH, frameWidth);
         frameHeight = generalPreferences.getInt(APPFRAMEHEIGHT, frameHeight);
         queryFramePosition_X = generalPreferences.getInt(APPQUERYFRAMEPOSITIONX, queryFramePosition_X);
         queryFramePosition_Y = generalPreferences.getInt(APPQUERYFRAMEPOSITIONY, queryFramePosition_Y);
         queryFrameWidth = generalPreferences.getInt(APPQUERYFRAMEWIDTH, queryFrameWidth);
         queryFrameHeight = generalPreferences.getInt(APPQUERYFRAMEHEIGHT, queryFrameHeight);
         sequenceList = generalPreferences.get(APPSEQUENCELIST, sequenceList);
         enableProxy = generalPreferences.getBoolean(APPENABLEPROXY, enableProxy);
         proxyAddress = generalPreferences.get(APPPROXYADDRESS, proxyAddress);
         proxyPort = generalPreferences.getInt(APPPROXYPORT, proxyPort);
         
         if (framePosition_X < 0 || framePosition_Y < 0)
         {
            framePosition_X = 0;
            framePosition_Y = 0;
         }
         
         if (frameWidth <= 0 || frameHeight <= 0)
         {
            frameWidth = Main_Frame.FRAME_DEFAULT_WIDTH;
            frameHeight = Main_Frame.FRAME_DEFAULT_HEIGHT;
         }
         
         if (queryFramePosition_X < 0 || queryFramePosition_Y < 0)
         {
            queryFramePosition_X = 0;
            queryFramePosition_Y = 0;
         }
         
         if (queryFrameWidth <= 0 || queryFrameHeight <= 0)
         {
            queryFrameWidth = Main_Frame.FRAME_DEFAULT_WIDTH;
            queryFrameHeight = Main_Frame.FRAME_DEFAULT_HEIGHT;
         }
         
         if (sequenceList == null || sequenceList.isEmpty())
         {
            setSequenceList(Utils.getChartList(GeneralPreferencesPanel.DEFAULT_SEQUENCE_SIZE,
                                                          GeneralPreferencesPanel.DEFAULT_SEQUENCE_MAX));
            savePreference(APPSEQUENCELIST, sequenceList);
         }
      }
      catch (NullPointerException npe){}
      catch (IllegalStateException ise){}
   }

   //==============================================================
   // Class methods to allow classes to get the general object
   // components.
   //==============================================================
   
   public int getFontSize()
   {
      return fontSize;
   }
   
   public Point getPosition()
   {
      return new Point(framePosition_X, framePosition_Y);
   }
   
   public Dimension getDimension()
   {
      return new Dimension(frameWidth, frameHeight);
   }
   
   public Point getQueryFramePosition()
   {
      return new Point(queryFramePosition_X, queryFramePosition_Y);
   }
   
   public Dimension getQueryFrameDimension()
   {
      return new Dimension(queryFrameWidth, queryFrameHeight);
   }
   
   public int[] getSequenceList()
   {
      String[] sequence = sequenceList.split(":");
      int[] sequenceListArray = new int[sequence.length];
      try
      {
         for (int i=0; i<sequence.length; i++)
            sequenceListArray[i] = Integer.parseInt(sequence[i]);
         return sequenceListArray;
      }
      catch (NumberFormatException nfe)
      {
         return new int[0];
      }
   }
   
   public boolean getEnableProxy()
   {
      return enableProxy;
   }
   
   public String getProxyAddress()
   {
      return proxyAddress;
   }
   
   public int getProxyPort()
   {
      return proxyPort;
   }
   
   //==============================================================
   // Class methods to allow classes to set the general object
   // components.
   //==============================================================
   
   public void setFontSize(int value)
   {
      fontSize = value;
      savePreference(APPFONTSIZE, value);
   }
   
   public void setPosition(Point value)
   {
      if (value.x < 0 || value.y <= 0)
      {
         framePosition_X = 0;
         framePosition_Y = 0;
      }
      else
      {
         framePosition_X = value.x;
         framePosition_Y = value.y; 
      }
      savePreference(APPFRAMEPOSITIONX, value.x);
      savePreference(APPFRAMEPOSITIONY, value.y);
   }
   
   public void setDimension(Dimension value)
   {
      if (value.width <= 0 || value.height <= 0)
      {
         frameWidth = Main_Frame.FRAME_DEFAULT_WIDTH;
         frameHeight = Main_Frame.FRAME_DEFAULT_HEIGHT;
      }
      else
      {
         frameWidth = value.width;
         frameHeight = value.height; 
      }
      savePreference(APPFRAMEWIDTH, value.width);
      savePreference(APPFRAMEHEIGHT, value.height);
   }
   
   public void setQueryFramePosition(Point value)
   {
      if (value.x < 0 || value.y <= 0)
      {
         queryFramePosition_X = 0;
         queryFramePosition_Y = 0;
      }
      else
      {
         queryFramePosition_X = value.x;
         queryFramePosition_Y = value.y; 
      }
      savePreference(APPQUERYFRAMEPOSITIONX, value.x);
      savePreference(APPQUERYFRAMEPOSITIONY, value.y);
   }
   
   public void setQueryFrameDimension(Dimension value)
   {
      if (value.width <= 0 || value.height <= 0)
      {
         queryFrameWidth = Main_Frame.FRAME_DEFAULT_WIDTH;
         queryFrameHeight = Main_Frame.FRAME_DEFAULT_HEIGHT;
      }
      else
      {
         queryFrameWidth = value.width;
         queryFrameHeight = value.height; 
      }
      savePreference(APPQUERYFRAMEWIDTH, value.width);
      savePreference(APPQUERYFRAMEHEIGHT, value.height);
   }
   
   public void setSequenceList(int[] value)
   {
      StringBuffer sequenceBuffer = new StringBuffer();
      
      for (int i=0; i<value.length; i++)
         sequenceBuffer.append(value[i] + ":");
      sequenceList = sequenceBuffer.substring(0, sequenceBuffer.length() - 1);
      savePreference(APPSEQUENCELIST, sequenceList);
   }
   
   public void setEnableProxy(boolean value)
   {
      enableProxy = value;
      savePreference(APPENABLEPROXY, value);
   }
   
   public void setProxyAddress(String value)
   {
      proxyAddress = value;
      savePreference(APPPROXYADDRESS, value);
   }
   
   public void setProxyPort(int value)
   {
      proxyPort = value;
      savePreference(APPPROXYPORT, value);
   }
   
   //==============================================================
   // Class methods to try and save the preferences state. 
   //==============================================================

   private void savePreference(String key, boolean value)
   {
      try
      {
         if (generalPreferences != null)
            generalPreferences.putBoolean(key, value);
      }
      catch (IllegalArgumentException iae){}
      catch (IllegalStateException ise){}
   }
   
   private void savePreference(String key, String content)
   {
      try
      {
         if (generalPreferences != null)
            generalPreferences.put(key, content);
      }
      catch (IllegalArgumentException iae){}
      catch (IllegalStateException ise){}
   }
   
   private void savePreference(String key, int value)
   {
      try
      {
         if (generalPreferences != null)
            generalPreferences.putInt(key, value);
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
      StringBuffer parameters = new StringBuffer("[GeneralProperties: ");
      
      parameters.append("[fontSize = " + fontSize + "]");
      parameters.append("[framePosition_X = " + framePosition_X + "]");
      parameters.append("[framePosition_Y = " + framePosition_Y + "]");
      parameters.append("[framewidth = " + frameWidth + "]");
      parameters.append("[frameheight = " + frameHeight + "]");
      parameters.append("[queryFramePosition_X = " + queryFramePosition_X + "]");
      parameters.append("[queryFramePosition_Y = " + queryFramePosition_Y + "]");
      parameters.append("[queryFramewidth = " + queryFrameWidth + "]");
      parameters.append("[queryFrameheight = " + queryFrameHeight + "]");
      parameters.append("[enableProxy = " + enableProxy + "]");
      parameters.append("[proxyAddress = " + proxyAddress + "]");
      parameters.append("proxyPort = " + proxyPort + "]");

      return parameters.toString();
   }
}
