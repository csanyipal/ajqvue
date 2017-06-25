//=================================================================
//                CSVImportPreferencesFiller
//=================================================================
//
//    This class provides a generic canvas used in the Preferences
// Menu CSV Import to provide a generic animated filler graphic.
//
//          << CSVImportPreferencesFiller.java >>
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
// Version 1.0 01/18/2018 Production CSVImportPreferencesFiller Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.Random;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The CSVImportPreferencesFiller class provides a generic panel used in
 * the Preferences Menu CSV Import to provide a generic filler animated graphic.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class CSVImportPreferencesFiller extends Canvas implements Runnable, KeyListener
{
   // Class Instances.
   private static final long serialVersionUID = 5857069898307163066L;
   
   private transient Image backgroundImage;
   private transient Image mountainsImage, shipImage;

   private int backgroundImageWidth, backgroundImageHeight;
   private int mountainsImageWidth;
   private int shipImageWidth, shipImageHeight;
   private transient Image offScreenGraphicsImage;
   
   private Color star1_Color, star2_Color;

   private Point shipPositionHome, shipPosition;
   private Point mountainsPosition;

   private boolean runThread;
   private boolean suspendThread;
   private static final int frameDelay = 30;

   //==============================================================
   // CSVImportPreferencesFiller Constructor
   //==============================================================

   public CSVImportPreferencesFiller()
   {
      // Class Instances
      AResourceBundle resourceBundle;
      String fileSeparator;

      // ======================================================
      // Obtaining the background image(s) and setting up as
      // needed instances values.

      resourceBundle = Ajqvue.getResourceBundle();
      fileSeparator = Utils.getFileSeparator();

      // Background
      backgroundImage = resourceBundle.getResourceImage("images"
                                                        + fileSeparator + "csvImport_sky.jpg").getImage();
      backgroundImageWidth = backgroundImage.getWidth(null);
      backgroundImageHeight = backgroundImage.getHeight(null);
      setSize(backgroundImageWidth, backgroundImageHeight);

      // Mountains
      mountainsImage = resourceBundle.getResourceImage("images"
                                                       + fileSeparator + "csvImport_wosky.png").getImage();
      mountainsImageWidth = mountainsImage.getWidth(null);
      mountainsPosition = new Point(0, 0);
      
      // Ship
      shipImage = resourceBundle.getResourceImage("images"
                                                  + fileSeparator + "csvImport_ship.png").getImage();
      shipImageWidth = shipImage.getWidth(null);
      shipImageHeight = shipImage.getHeight(null);
      shipPositionHome = new Point();
      shipPosition = new Point();
      
      if (getWidth() >= 0 && getHeight() >= 0)
      {
         shipPositionHome.x = backgroundImageWidth / 3;
         shipPositionHome.y = backgroundImageHeight / 3;
         shipPosition = shipPositionHome;
      }
      else
      {
         shipPositionHome.x = 100;
         shipPositionHome.y = 40;
         shipPosition = shipPositionHome;
      }

      addKeyListener(this);

      // ======================================================
      // Run the panel's thread.

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
         updateAnimatedObjects();
         render();
         timeStep();
      }
   }

   //==============================================================
   // Class method to update the iamge's animated elements.
   //==============================================================

   private void updateAnimatedObjects()
   {
      // Class Method Instances
      Random randomNumber;
      int offsetX, offsetY;

      // Create a random number to be used to generate random
      // characteristics and motion for some objects 
      
      randomNumber = new Random(System.currentTimeMillis());
      
      // Change character of stars color.
      
      if (Math.abs(randomNumber.nextInt() % 20) == 3)
      {
         star1_Color = new Color(Math.abs(randomNumber.nextInt() % 255),
                                 Math.abs(randomNumber.nextInt() % 255),
                                 Math.abs(randomNumber.nextInt() % 255));
      }
      
      if (Math.abs(randomNumber.nextInt() % 30) == 21)
      {
         star2_Color = new Color(Math.abs(randomNumber.nextInt() % 255),
                                 Math.abs(randomNumber.nextInt() % 255),
                                 Math.abs(randomNumber.nextInt() % 255));
      }
      
      // Create minor motion in ship.
      
      if (Math.abs(randomNumber.nextInt() % 40) == 8)
      {
         offsetX = randomNumber.nextInt() % 2;
         if (shipPosition.x < shipPositionHome.x - 10)
            shipPosition.x += Math.abs(offsetX);
         else if (shipPosition.x > shipPositionHome.x + 10) 
            shipPosition.x -= Math.abs(offsetX);
         else
            shipPosition.x += offsetX;
      }
      
      if (Math.abs(randomNumber.nextInt() % 25) == 17)
      {
         offsetY = randomNumber.nextInt() % 3;
         if (shipPosition.y < shipPositionHome.y - 12)
            shipPosition.y += Math.abs(offsetY);
         else if (shipPosition.y > shipPositionHome.y + 15)
            shipPosition.y -= Math.abs(offsetY);
         else
            shipPosition.y += offsetY;
      }
     
      // Change the position of mountains.
      
      if (mountainsPosition.x <= - mountainsImageWidth)
         mountainsPosition.x += mountainsImageWidth; 
      else
         mountainsPosition.x -= 3;
      
   }

   //==============================================================
   // Class method to create a double buffered offscreen graphic.
   //==============================================================

   private void render()
   {
      // Class Instances
      Graphics g2 = (Graphics2D) getGraphics();
      Graphics2D imageGraphics;

      // Clear and redraw the graphics background then
      // draw the component offscreen.
      if (g2 != null)
      {
         Dimension d = getSize();
         if (checkImage(d))
         {
            imageGraphics = (Graphics2D) offScreenGraphicsImage.getGraphics();

            // Draw this component offscreen then to screen.
            paint(imageGraphics);
            g2.drawImage(offScreenGraphicsImage, 0, 0, null);

            imageGraphics.dispose();
         }
         g2.dispose();
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
   // Overiding public update method that the panel will not
   // be cleared then refilled.
   //==============================================================

   public void update(Graphics g)
   {
      paint(g);
   }

   //==============================================================
   // Overiding public paint method so that a the filler animation
   // may be produced.
   //==============================================================

   public void paint(Graphics g)
   {
      // Class Methods
      Graphics2D g2;
      AffineTransform orgAffineTransform, scaleAffineTransform;
      AlphaComposite alphaComposite;
      float alpha;
      
      Point star1_Center, star2_Center;
      int panelWidth, panelHeight;
      
      // Panel parameters.
      g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      panelWidth = this.getWidth();
      panelHeight = this.getHeight();
      orgAffineTransform = g2.getTransform();

      // Make sure refill background color.
      g2.setColor(this.getBackground());
      g2.fillRect(0, 0, panelWidth, panelHeight);
      
      // Check to see if panel size has been changed
      // so that the animation can be scaled appropriately.
      
      if (panelWidth != backgroundImageWidth || panelHeight != backgroundImageHeight)
      {
         scaleAffineTransform = AffineTransform.getScaleInstance(
            panelWidth / ((double) backgroundImageWidth), panelHeight / ((double) backgroundImageHeight));
         
         g2.setTransform(scaleAffineTransform);  
      }
      
      // ==============================
      // Draw the background sky.
      
      g2.drawImage(backgroundImage, 0, 0, this);
      
      // ==============================
      // Drawing a couple of faded primary stars.
      
      alpha = 0.70F;
      alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
      g2.setComposite(alphaComposite);
      
      // Star One
      g2.setColor(star1_Color);
      g2.setStroke(new BasicStroke(1.0F));
      
      star1_Center = new Point(25, 26);
      
      g2.drawLine(star1_Center.x, star1_Center.y - 3, star1_Center.x, star1_Center.y + 3);
      g2.drawLine(star1_Center.x - 3, star1_Center.y, star1_Center.x + 3, star1_Center.y);
      
      g2.setStroke(new BasicStroke(0.5F));
      
      g2.drawLine(star1_Center.x, star1_Center.y - 6, star1_Center.x, star1_Center.y + 6);
      g2.drawLine(star1_Center.x - 6, star1_Center.y, star1_Center.x + 6, star1_Center.y);
      
      // Star Two
      g2.setColor(star2_Color);
      g2.setStroke(new BasicStroke(1.0F));
      
      star2_Center = new Point(100, 15);
      
      g2.drawLine(star2_Center.x, star2_Center.y - 3, star2_Center.x, star2_Center.y + 3);
      g2.drawLine(star2_Center.x - 3, star2_Center.y, star2_Center.x + 3, star2_Center.y);
      
      g2.setStroke(new BasicStroke(0.5F));
      
      g2.drawLine(star2_Center.x, star2_Center.y - 6, star2_Center.x, star2_Center.y + 6);
      g2.drawLine(star2_Center.x - 6, star2_Center.y, star2_Center.x + 6, star2_Center.y);
      
      // Reset graphics.
      alpha = 1.0F;
      alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
      g2.setComposite(alphaComposite);
      
      // ==============================
      // Draw the mountains.
      
      g2.drawImage(mountainsImage, mountainsPosition.x, mountainsPosition.y, this);
      
      if (mountainsPosition.x + mountainsImageWidth < getWidth())
         g2.drawImage(mountainsImage, (mountainsPosition.x + mountainsImageWidth),
                                     mountainsPosition.y, this);
       
      // ==============================
      // Draw the ship.
     
      g2.drawImage(shipImage, shipPosition.x, shipPosition.y, this);
      
      // Clean up.
      g2.setTransform(orgAffineTransform);
      g2.dispose();
   }

   //==============================================================
   // KeyEvent Listener method for detecting key pressed events of
   // left, right, up, & down arrows to move the ship image.
   //==============================================================

   public void keyPressed(KeyEvent evt)
   {
      int keyCode = evt.getKeyCode();

      // Previous/Next entry views
      if (keyCode == KeyEvent.VK_LEFT)
      {
         if (shipPosition.x != 0)
            --shipPosition.x;
      }
      if (keyCode == KeyEvent.VK_RIGHT)
      {
         if (shipPosition.x < (backgroundImageWidth - shipImageWidth))
            ++shipPosition.x;
      }
      if (keyCode == KeyEvent.VK_UP)
      {
         if (shipPosition.y != 0)
            --shipPosition.y;
      }
      if (keyCode == KeyEvent.VK_DOWN)
      {
         if (shipPosition.y < (backgroundImageWidth - shipImageHeight))
            ++shipPosition.y;
      }
   }

   //==============================================================
   // KeyEvent Listener method for detected key released events
   // to full fill KeyListener Interface requirements.
   //==============================================================

   public void keyReleased(KeyEvent evt)
   {
      // Do Nothing
   }

   //==============================================================
   // KeyEvent Listener method for detecting key pressed event
   // to full fill KeyListener Interface requirements.
   //==============================================================

   public void keyTyped(KeyEvent evt)
   {
      // Do Nothing
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
