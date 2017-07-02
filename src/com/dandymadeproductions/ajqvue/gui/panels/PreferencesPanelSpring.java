//=================================================================
//                  PreferencesPanelSpring
//=================================================================
//
//    This class provides a generic panel used in the Preferences
// Menu to highlight the top tree element during the northern
// hemisphere's spring months, May-June.
//
//           << PreferencesPanelSpring.java >>
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
// Version 1.0 09/18/2016 Production PreferencesPanelSpring Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Random;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The PreferencesPanelSpring class provides a generic panel used
 * in the Preferences Menu to highlight the top tree element during
 * the northern hemisphere's spring months, May-June.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class PreferencesPanelSpring extends PreferencesPanel
{
   private static final long serialVersionUID = -1653725363649126705L;
   
   private transient Image backgroundImage;
   private int backgroundImageWidth, backgroundImageHeight;
   private transient Image offScreenGraphicsImage;

   private static final int owlImagesNumber = 4;
   private transient Image[] owlImages = new Image[owlImagesNumber];
   private transient Image currentOwlImage;
   
   private volatile boolean runThread;
   private volatile boolean suspendThread;
   private static final int frameDelay = 3000;

   //==============================================================
   // PreferencesPanelSpring Constructor
   //==============================================================

   public PreferencesPanelSpring()
   {
      // Class Instances
      AResourceBundle resourceBundle;
      String fileSeparator;
      String[] owlImageName = {"owl1.gif", "owl2.gif", "owl3.gif", "owl4.gif"};

      // ==========================================================
      // Obtaining the background image and setting up as
      // needed instances values.

      resourceBundle = Ajqvue.getResourceBundle();
      fileSeparator = Utils.getFileSeparator();

      backgroundImage = resourceBundle.getResourceImage("images" + fileSeparator
                                                        + "PreferencesPanelSpring.jpg").getImage();
      backgroundImageWidth = backgroundImage.getWidth(null);
      backgroundImageHeight = backgroundImage.getHeight(null);

      // ===========================================================
      // Obtaing the animated panel images and setting up as needed.

      for (int i = 0; i < owlImagesNumber; i++)
         owlImages[i] = resourceBundle.getResourceImage("images" + fileSeparator
                                                        + owlImageName[i]).getImage();
      
      runThread = true;
      suspendThread = false;
   }

   //==============================================================
   // Class method for starting the runnable thread.
   //==============================================================

   public void run()
   {
      // Cycling through the routine
      // to animate the panel.
      while (runThread)
      {
         updateOwlImage();
         render();
         timeStep();
      }
   }

   //==============================================================
   // Class method to update the iamge that is used for the owl in
   // the panel.
   //==============================================================

   private void updateOwlImage()
   {
      // Class Method Instances
      Random randomNumber;
      
      randomNumber = new Random(System.currentTimeMillis());
      currentOwlImage = owlImages[Math.abs(randomNumber.nextInt() % 4)];
   }

   //==============================================================
   // Class method to create a double buffered offscreen graphic
   // then rendering to the screen.
   //==============================================================

   private void render()
   {
      // Check then draw the component offscreen before
      // to the screen.
      
      if (getGraphics() != null)
      {
         Dimension d = getSize();
         if (checkImage(d))
         {
            drawPanel(offScreenGraphicsImage.getGraphics());
            getGraphics().drawImage(offScreenGraphicsImage, 0, 0, null);
         }
      }
   }

   //==============================================================
   // Class method to setup a offscreen image.
   //==============================================================

   private boolean checkImage(Dimension d)
   {
      if (d.width <= 0 || d.height <= 0)
         return false;
      if (offScreenGraphicsImage == null || offScreenGraphicsImage.getWidth(null) != d.width
          || offScreenGraphicsImage.getHeight(null) != d.height)
      {
         offScreenGraphicsImage = createImage(d.width, d.height);
      }
      return true;
   }

   //==============================================================
   // Class method for delaying the animation/framerate change.
   //==============================================================

   private void timeStep()
   {
      try
      {
         Thread.sleep(frameDelay);
         synchronized (this)
         {
            while (suspendThread)
               wait();
         }
      }
      catch (InterruptedException e)
      {
         System.out.println("Process Interrupted.");
      }
   }
   
   //==============================================================
   // Class method to overide the standard panel paintComponents
   // routine.
   //==============================================================

   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      drawPanel(g);
   }

   //==============================================================
   // Class method to create, paint, the graphics for the panel.
   //==============================================================

   private void drawPanel(Graphics g)
   {
      // Class Methods
      Graphics2D g2d;
      int panelWidth, panelHeight;
      AffineTransform orgAffineTransform, scaleAffineTransform;

      // Panel parameters.
      g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      panelWidth = this.getWidth();
      panelHeight = this.getHeight();
      orgAffineTransform = g2d.getTransform();

      // Make sure refill background color.
      g2d.setColor(this.getBackground());
      g2d.fillRect(0, 0, panelWidth, panelHeight);

      // Check to see if panel size has been changed
      // so that the animation can be scaled appropriately.
      
      if (panelWidth != backgroundImageWidth || panelHeight != backgroundImageHeight)
      {
         scaleAffineTransform = AffineTransform.getScaleInstance(
            panelWidth / ((double) backgroundImageWidth), panelHeight / ((double) backgroundImageHeight));
         
         g2d.setTransform(scaleAffineTransform);  
      }

      g2d.drawImage(backgroundImage, 0, 0, this);

      // Draw the current date.
      g2d.setFont(fontSerifPlain_12);
      g2d.drawString(dateString, 10, 20);
      
      // Draw the animated owl gif.
      g2d.drawImage(currentOwlImage, 154, 98, this);
      
      // Clean up.
      g2d.setTransform(orgAffineTransform);
      g2d.dispose();
   }

   //==============================================================
   // Class Method to start and stop the thread.
   //==============================================================

   public synchronized void setThreadAction(boolean action)
   {
      suspendThread = action;
      if (!suspendThread)
         notifyAll();
   }

   //==============================================================
   // Class Method to let the thread run() method naturally
   // finish.
   //==============================================================

   public void suspendPanel(boolean action)
   {
      runThread = !action;
   }
}
