//=================================================================
//                  PreferencesPanelSummer
//=================================================================
//
//    This class provides a generic panel used in the Preferences
// Menu to highlight the top tree element during the northern
// hemisphere's summer months, June-August.
//
//           << PreferencesPanelSummer.java >>
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
// Version 1.0 09/18/2016 Production PreferencesPanel Class.
//
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
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
import com.dandymadeproductions.ajqvue.gui.sprites.FireFly;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The PreferencesPanelSummer class provides a generic panel used in the
 * Preferences Menu to highlight the top tree element during the northern
 * hemisphere's summer months, July-September.
 * @author Dana M. Proctor
 * 
 * @version 1.0 09/18/2016
 */

public class PreferencesPanelSummer extends PreferencesPanel
{
   // Class Instances.
   private static final long serialVersionUID = -9169861459455585662L;
   private transient Image backgroundImage;
   private int backgroundImageWidth, backgroundImageHeight;
   private transient Image offScreenGraphicsImage;

   private static final int fireFlyColors = 6;
   private transient Image[] fireFlyImages = new Image[fireFlyColors];
   private int fireFlyImageWidth, fireFlyImageHeight;
   private transient ArrayList<FireFly> fireFlies;

   private volatile boolean runThread;
   private volatile boolean suspendThread;
   private static final int frameDelay = 40;

   //==============================================================
   // PreferencesPanelSummer Constructor
   //==============================================================

   public PreferencesPanelSummer()
   {
      // Class Instances
      AResourceBundle resourceBundle;
      String fileSeparator;
      String[] fireFlyImageName = {"red_firefly.gif", "green_firefly.gif", "blue_firefly.gif",
                                   "yellow_firefly.gif", "purple_firefly.gif", "white_firefly.gif"};

      // ==========================================================
      // Obtaining the background image and setting up as
      // needed instances values.

      resourceBundle = Ajqvue.getResourceBundle();
      fileSeparator = Utils.getFileSeparator();

      backgroundImage = resourceBundle.getResourceImage("images" + fileSeparator
                                                        + "PreferencesPanelSummer.jpg").getImage();
      backgroundImageWidth = backgroundImage.getWidth(null);
      backgroundImageHeight = backgroundImage.getHeight(null);

      // ===========================================================
      // Obtaing the firefly images and setting up as needed.

      for (int i = 0; i < fireFlyColors; i++)
         fireFlyImages[i] = resourceBundle.getResourceImage("images" + fileSeparator
                                                            + fireFlyImageName[i]).getImage();
      fireFlyImageWidth = fireFlyImages[0].getWidth(null);
      fireFlyImageHeight = fireFlyImages[0].getHeight(null);
      fireFlies = new ArrayList <FireFly>();

      runThread = true;
      suspendThread = false;
   }

   //==============================================================
   // Class method for starting the runnable thread.
   //==============================================================

   public void run()
   {
      // Class Instances
      int fireFlyCount;
      Random randomNumber;
      Rectangle panelBounds;

      // Create fireflies from the firefly image files.
      fireFlyCount = 15;
      randomNumber = new Random(System.currentTimeMillis());
      panelBounds = new Rectangle(0, 0, backgroundImageWidth, backgroundImageHeight);

      for (int i = 0; i < fireFlyCount; i++)
      {
         Point currentFireFlyPosition = getEmptyPosition();
         fireFlies.add(new FireFly(this, panelBounds, fireFlyImages[i % fireFlyImages.length],
                                   currentFireFlyPosition, new Point(randomNumber.nextInt() % 4,
                                   randomNumber.nextInt() % 4)));
      }

      // Cycling through the routine
      // to animate the panel.
      while (runThread)
      {
         updateFireFlies();
         render();
         timeStep();
      }
   }

   //==============================================================
   // Class method to obtain an empty postion in the panel that
   // fire fly may be placed.
   //==============================================================

   private Point getEmptyPosition()
   {
      // Class Method Instances
      Rectangle trialSpaceOccupied;
      Random randomNumber;
      boolean empty, collision;
      int numberOfTries;

      // Setting up.
      trialSpaceOccupied = new Rectangle(0, 0, fireFlyImageWidth, fireFlyImageHeight);
      randomNumber = new Random(System.currentTimeMillis());
      empty = false;
      numberOfTries = 0;

      // Begin the search for an empty position
      while (!empty && numberOfTries++ < 100)
      {
         // Obtain a random postion.
         trialSpaceOccupied.x = Math.abs(randomNumber.nextInt() % backgroundImageWidth);
         trialSpaceOccupied.y = Math.abs(randomNumber.nextInt() % backgroundImageHeight);

         // Check to see if an existing firefly occupies
         // the randomly selected postion.
         collision = false;
         for (int i = 0; i < fireFlies.size(); i++)
         {
            Rectangle testSpaceOccupied = (fireFlies.get(i)).getSpaceOccupied();
            if (trialSpaceOccupied.intersects(testSpaceOccupied))
               collision = true;
         }
         empty = !collision;
      }
      // Return the empty postion.
      return new Point(trialSpaceOccupied.x, trialSpaceOccupied.y);
   }

   // ==============================================================
   // Class method to update the fireflies' positions in the panel.
   // ==============================================================

   private void updateFireFlies()
   {
      // Class Method Instances
      FireFly currentFireFly;
      int fireFlyOccupiedIndex;
      Point tempSwapPoint;

      // Cycle through the fireflies, updating postion and
      // testing for collision.
      for (int i = 0; i < fireFlies.size(); i++)
      {
         currentFireFly = fireFlies.get(i);
         currentFireFly.updatePosition();

         // Collision check and recoil action as needed.
         fireFlyOccupiedIndex = testForCollision(currentFireFly);

         if (fireFlyOccupiedIndex >= 0)
         {
            tempSwapPoint = currentFireFly.getNextPosition();
            currentFireFly.setNextPosition((fireFlies.get(fireFlyOccupiedIndex)).getNextPosition());
            (fireFlies.get(fireFlyOccupiedIndex)).setNextPosition(tempSwapPoint);
         }
      }
   }

   //==============================================================
   // Class Method to check if the input FireFly collides with any
   // of the other fireflies. If does return the index in the vector
   // of offending firefly, else -1.
   //==============================================================

   private int testForCollision(FireFly testFireFly)
   {
      // Class Method Instances.
      FireFly currentFireFly;

      // Cycle through the fireflies, checking against input
      // firefly.
      for (int i = 0; i < fireFlies.size(); i++)
      {
         currentFireFly = fireFlies.get(i);

         // Don't need to check itself.
         if (currentFireFly == testFireFly)
            continue;

         if (testFireFly.testCollision(currentFireFly))
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

      // Draw Fireflies
      for (int i = 0; i < fireFlies.size(); i++)
      {
         g2d.drawImage((fireFlies.get(i)).getImage(),
                     (fireFlies.get(i)).getSpaceOccupied().x,
                     (fireFlies.get(i)).getSpaceOccupied().y, this);
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
