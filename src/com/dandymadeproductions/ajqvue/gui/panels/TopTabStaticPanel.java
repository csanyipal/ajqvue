//=================================================================
//                       TopTabStaticPanel
//=================================================================
//
//    This class provides the top tab panel in the application that
// is used to highlight the creator, Dandy Made Productions which
// does not contain any graphic animations.
//
//                   << TopTabStaticPanel.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.0 10/08/2016
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
// Version 1.0 10/08/2016 Initial TopTabStaticPanel Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.BorderLayout;
import java.util.Calendar;

import javax.swing.JPanel;

/**
 *    The TopTabStaticPanel class provides the top tab panel in the
 * application that is used to highlight the creator, Dandy Made
 * Productions which does not contain any graphic animations.
 * 
 * @author Dana M. Proctor
 * @version 1.0 10/08/2016
 */

public class TopTabStaticPanel extends JPanel
{
   // Class Instances.
   private static final long serialVersionUID = 7869793821771590975L;

   //==============================================================
   // TopTabStaticPanel Constructor
   //==============================================================

   public TopTabStaticPanel()
   {
      // Class Instances
      String mainTabImageFileName;
      
      int timeOfDay;
      Calendar calendar;

      // Setting up as needed instances values & obtaining the
      // background image.

      calendar = Calendar.getInstance();
      timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

      // {8:00pm - 4:00am} Night
      if (timeOfDay >= 20 || timeOfDay <= 4)
         mainTabImageFileName = "mainTab_night.jpg";
      // {5:00am - 9:00am} Morning
      else if (timeOfDay <= 9)
         mainTabImageFileName = "mainTab_morning.jpg";
      // {10:00am - 4:00pm} Afternoon
      else if (timeOfDay <= 16)
         mainTabImageFileName = "mainTab_day.jpg";
      // {5:00pm - 7:00pm} Evening
      else
         mainTabImageFileName = "mainTab_evening.jpg";

      setLayout(new BorderLayout());
      add(new GraphicsCanvasPanel(mainTabImageFileName), BorderLayout.CENTER);
   }
   
   //==============================================================
   // Class Method to fill reset the process, static.
   //==============================================================

   public void resetPanel()
   {
      // Do Nothing.
   }

   //==============================================================
   // Class Method to fill the process, static.
   //==============================================================

   public synchronized void setThreadAction(boolean action)
   {
      // Do Nothing.
   }

   //==============================================================
   // Class Method to fill the process, static.
   //==============================================================

   public void suspendPanel(boolean action)
   {
      // Do Nothing.
   }
}
