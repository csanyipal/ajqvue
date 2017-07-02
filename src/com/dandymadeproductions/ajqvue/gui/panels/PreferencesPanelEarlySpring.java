//=================================================================
//              PreferencesPanelEarlySpring
//=================================================================
//
//    This class provides a generic panel used in the Preferences
// Menu to highlight the top tree element during the northern
// hemisphere's summer months, March-April.
//
//           << PreferencesPanelEarlySpring.java >>
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
// Version 1.0 09/18/2016 Production PreferencesPanelEarlySpring Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.gui.sprites.RainDrop;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The PreferencesPanelEarlySpring class provides a generic panel
 * used in the Preferences Menu to highlight the top tree element
 * during the northern hemisphere's early spring months, March-Arpil.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class PreferencesPanelEarlySpring extends PreferencesPanel
{
   // Class Instances.
   private static final long serialVersionUID = -500197101860684191L;
   
   private transient Image backgroundImage;
   private int backgroundImageWidth, backgroundImageHeight;
   private transient Image offScreenGraphicsImage;

   private transient Image rainDropImage;
   private int rainDropImageWidth, rainDropImageHeight;
   private transient ArrayList<RainDrop> rainDrops;
   
   private volatile boolean runThread;
   private volatile boolean suspendThread;
   private static final int frameDelay = 40;

   //==============================================================
   // PreferencesPanelWinter Constructor
   //==============================================================

   public PreferencesPanelEarlySpring()
   {
      // Class Instances
      AResourceBundle resourceBundle;
      String fileSeparator;

      // ==========================================================
      // Obtaining the background image and setting up as
      // needed instances values.

      resourceBundle = Ajqvue.getResourceBundle();
      fileSeparator = Utils.getFileSeparator();

      backgroundImage = resourceBundle.getResourceImage("images" + fileSeparator 
                                                        + "PreferencesPanelEarlySpring.jpg").getImage();
      backgroundImageWidth = backgroundImage.getWidth(null);
      backgroundImageHeight = backgroundImage.getHeight(null);

      // ===========================================================
      // Obtaing the rain drop image and setting up as needed.

      rainDropImage = resourceBundle.getResourceImage("images" + fileSeparator 
                                                      + "raindrop.gif").getImage();
      rainDropImageWidth = rainDropImage.getWidth(null);
      rainDropImageHeight = rainDropImage.getHeight(null);
      rainDrops = new ArrayList <RainDrop>();

      runThread = true;
      suspendThread = false;
   }

   //==============================================================
   // Class method for starting the runnable thread.
   //==============================================================

   public void run()
   {
      // Class Instances
      int rainDropCount;
      Random randomNumber;
      Rectangle panelBounds;

      // Create raindrops from the raindrop image file.
      rainDropCount = 75;
      randomNumber = new Random(System.currentTimeMillis());
      panelBounds = new Rectangle(0, 0, backgroundImageWidth, backgroundImageHeight);

      for (int i = 0; i < rainDropCount; i++)
      {
         Point currentRainDropPosition = getEmptyPosition();

         rainDrops.add(new RainDrop(this, panelBounds, rainDropImage, currentRainDropPosition,
                                    new Point(randomNumber.nextInt() % 4, randomNumber.nextInt() % 4)));
      }
      
      // Cycling through the routine
      // to animate the panel.
      while (runThread)
      {
         updateRainDrops();
         render();
         timeStep();
      }
   }

   //==============================================================
   // Class method to obtain an empty postion in the panel that
   // raindrop may be placed.
   //==============================================================

   private Point getEmptyPosition()
   {
      // Class Method Instances
      Rectangle trialSpaceOccupied;
      Random randomNumber;
      boolean empty, collision;
      int numberOfTries;

      // Setting up.
      trialSpaceOccupied = new Rectangle(0, 0, rainDropImageWidth, rainDropImageHeight);
      randomNumber = new Random(System.currentTimeMillis());
      empty = false;
      numberOfTries = 0;

      // Begin the search for an empty position
      while (!empty && numberOfTries++ < 100)
      {
         // Obtain a random postion.
         trialSpaceOccupied.x = Math.abs(randomNumber.nextInt() % backgroundImageWidth);
         trialSpaceOccupied.y = Math.abs(randomNumber.nextInt() % backgroundImageHeight);

         // Check to see if an existing rain drop occupies
         // the randomly selected postion.
         collision = false;
         for (int i = 0; i < rainDrops.size(); i++)
         {
            Rectangle testSpaceOccupied = (rainDrops.get(i)).getSpaceOccupied();
            if (trialSpaceOccupied.intersects(testSpaceOccupied))
               collision = true;
         }
         empty = !collision;
      }
      // Return the empty postion.
      return new Point(trialSpaceOccupied.x, trialSpaceOccupied.y);
   }

   //==============================================================
   // Class method to update the rainDrops' positions in the panel.
   //==============================================================

   private void updateRainDrops()
   {
      // Class Method Instances
      RainDrop currentRainDrop;
      int rainDropOccupiedIndex;
      Point tempSwapPoint;

      // Cycle through the raindrops, updating postion and
      // testing for collision.
      for (int i = 0; i < rainDrops.size(); i++)
      {
         currentRainDrop = rainDrops.get(i);
         currentRainDrop.updatePosition();

         // Collision check and recoil action as needed.
         rainDropOccupiedIndex = testForCollision(currentRainDrop);

         if (rainDropOccupiedIndex >= 0)
         {
            tempSwapPoint = currentRainDrop.getNextPosition();
            currentRainDrop.setNextPosition(
               (rainDrops.get(rainDropOccupiedIndex)).getNextPosition());
            (rainDrops.get(rainDropOccupiedIndex)).setNextPosition(tempSwapPoint);
         }
      }
   }

   //==============================================================
   // Class Method to check if the input RainDrop collides with any
   // of the other raindrops. If does return the index in the vector
   // of offending rain drop, else -1.
   //==============================================================

   private int testForCollision(RainDrop testRainDrop)
   {
      // Class Method Instances.
      RainDrop currentRainDrop;

      // Cycle through the raindrops, checking against input
      // rain drop.
      for (int i = 0; i < rainDrops.size(); i++)
      {
         currentRainDrop = rainDrops.get(i);

         // Don't need to check itself.
         if (currentRainDrop == testRainDrop)
            continue;

         if (testRainDrop.testCollision(currentRainDrop))
            return i;
      }
      return -1;
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

      // Draw RainDrops
      for (int i = 0; i < rainDrops.size(); i++)
      {
         g2d.drawImage((rainDrops.get(i)).getImage(),
                     (rainDrops.get(i)).getSpaceOccupied().x,
                     (rainDrops.get(i)).getSpaceOccupied().y, this);
      }
      
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
