//=================================================================
//                        TopTabPanel
//=================================================================
//
//    This class provides the top tab panel in the application that
// is used to highlight the creator, Dandy Made Productions.
//
//                    << TopTabPanel.java >>
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
// Version 1.0 09/18/2016 Production TopTabPanel Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The TopTabPanel class provides the top tab panel in the application
 * that is used to highlight the creator, Dandy Made Productions.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class TopTabPanel extends JPanel implements MouseListener, Runnable
{
   // Class Instances.
   private static final long serialVersionUID = -6010256554607153451L;
   
   private static final int imageIconSize = 50;
   private static final int moveIncrement = imageIconSize / 5;

   private transient Image backgroundImage;
   private int backgroundImageWidth, backgroundImageHeight;
   private ImageIcon[][] imageIcons, currentImageIcons;
   private transient Image offScreenGraphicsImage;
   private Point blankPosition, movingPosition;
   private Point currentMovingPosition, destinationPosition;
   private boolean movingPositionInitialized;
   private String moveDirection;

   private boolean noBackgroundImageLoaded;
   private boolean delayAnimation;
   private int currentTime;

   private volatile boolean runThread;
   private volatile boolean suspendThread;
   
   private static final int FRAME_DELAY = 200;
   private static final int ANIMATION_DELAY = 1000;

   //==============================================================
   // TopTabPanel Constructor
   //==============================================================

   public TopTabPanel(boolean suspend)
   {
      // Class Instances
      AResourceBundle resourceBundle;
      String fileSeparator;
      String mainTabImageFileName;
      BufferedImage backgroundBufferedImage;
      Graphics2D g2D;
      int timeOfDay;
      Calendar calendar;

      // Setting up as needed instances values & obtaining the
      // background image.
      
      resourceBundle = Ajqvue.getResourceBundle();
      fileSeparator = Utils.getFileSeparator();

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

      backgroundImage = resourceBundle.getResourceImage("images" + fileSeparator + mainTabImageFileName).getImage();
      backgroundImageWidth = backgroundImage.getWidth(null);
      backgroundImageHeight = backgroundImage.getHeight(null);
      
      if (backgroundImageWidth != -1 && backgroundImageHeight != -1)
      {
         noBackgroundImageLoaded = false;
         backgroundBufferedImage = new BufferedImage(backgroundImageWidth, backgroundImageHeight,
                                                     BufferedImage.TYPE_INT_RGB);
         g2D = backgroundBufferedImage.createGraphics();
         g2D.drawImage(backgroundImage, null, null);

         // Sub-divide the background image to be used in into
         // smaller images to be animated.
         
         imageIcons = new ImageIcon[backgroundImageWidth / imageIconSize][backgroundImageHeight
                                                                          / imageIconSize];

         for (int i = 0; i < imageIcons.length; i++)
            for (int j = 0; j < imageIcons[0].length; j++)
            {
               imageIcons[i][j] = new ImageIcon(backgroundBufferedImage.getSubimage(imageIconSize * i,
                  imageIconSize * j, imageIconSize, imageIconSize));
            }

         // Load the sub-images into the working animation copy.
         currentImageIcons = new ImageIcon[backgroundImageWidth / imageIconSize][backgroundImageHeight
                                                                                 / imageIconSize];
         
         // Clean up.
         g2D.dispose();
         
         // Initialize animation parameters.
         initializeAnimation();
         selectMovingSubImage();
         currentMovingPosition = new Point();
         destinationPosition = new Point();
         movingPositionInitialized = false;
      }
      else
         noBackgroundImageLoaded = true;

      // Complete initialization.
      currentTime = 0;
      runThread = true;
      suspendThread = suspend;
      delayAnimation = true;
      addMouseListener(this);
   }
   
   //==============================================================
   // Class method to initialize the animation by randomly selecting
   // a blank position, hole, in the sub-image array. Also then
   // loads a working copy of the sub-images.
   //==============================================================

   private void initializeAnimation()
   {
      // Method Instances
      Random randomNumber;

      // Create the blank position.
      randomNumber = new Random(System.currentTimeMillis());
      blankPosition = new Point(Math.abs(randomNumber.nextInt() % imageIcons.length), Math
            .abs(randomNumber.nextInt() % imageIcons[0].length));

      // Load the work copy of the sub-images to animate.
      for (int i = 0; i < imageIcons.length; i++)
         for (int j = 0; j < imageIcons[0].length; j++)
         {
            currentImageIcons[i][j] = imageIcons[i][j];
         }
   }

   //==============================================================
   // Class method to select the moving sub-image to animate.
   //==============================================================

   private void selectMovingSubImage()
   {
      // Method Instances
      Random randomNumber;

      // Either horizontal or verstical.
      movingPosition = new Point();
      randomNumber = new Random(System.currentTimeMillis());

      // Horizonatal
      if ((randomNumber.nextInt() % 2) == 0)
      {
         if ((randomNumber.nextInt() % 2) == 0)
         {
            movingPosition.x = blankPosition.x - 1;
            moveDirection = "Right";
         }
         else
         {
            movingPosition.x = blankPosition.x + 1;
            moveDirection = "Left";
         }

         // Check Horizontal Bound.
         if (movingPosition.x < 0)
         {
            movingPosition.x += 2;
            moveDirection = "Left";
         }
         if (movingPosition.x > (imageIcons.length - 1))
         {
            movingPosition.x -= 2;
            moveDirection = "Right";
         }

         movingPosition.y = blankPosition.y;
      }
      // Vertical
      else
      {
         if ((randomNumber.nextInt() % 2) == 0)
         {
            movingPosition.y = blankPosition.y - 1;
            moveDirection = "Down";
         }
         else
         {
            movingPosition.y = blankPosition.y + 1;
            moveDirection = "Up";
         }

         // Check Vertical Bound.
         if (movingPosition.y < 0)
         {
            movingPosition.y += 2;
            moveDirection = "Up";
         }
         if (movingPosition.y > (imageIcons[0].length - 1))
         {
            movingPosition.y -= 2;
            moveDirection = "Down";
         }

         movingPosition.x = blankPosition.x;
      }
   }

   //==============================================================
   // Class method for starting the runnable thread.
   //==============================================================

   public void run()
   {
      // Cycling through the routine to animate
      // the panel.

      while (runThread)
      {
         timeStep();
         render();
      }
   }

   //==============================================================
   // Class method to create a double buffered offscreen graphic
   // for the render then painting.
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
            drawPanel(offScreenGraphicsImage.getGraphics());
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
         Thread.sleep(FRAME_DELAY);
         if (currentTime < ANIMATION_DELAY)
            currentTime += FRAME_DELAY;
         else
            delayAnimation = false;

         synchronized (this)
         {
            while (suspendThread)
               wait();
         }
      }
      catch (InterruptedException e)
      {
         System.out.println("TopTabPanel:Process Interrupted. " + e.toString());
      }
   }

   //==============================================================
   // MouseEvent Listener methods for detecting mouse events.
   // MounseListner Interface requirements.
   //==============================================================

   public void mouseEntered(MouseEvent evt)
   {
      // Do Nothing.
   }

   public void mouseExited(MouseEvent evt)
   {
      // Do Nothing.
   }

   public void mousePressed(MouseEvent evt)
   {
      // Do Nothing.
   }

   public void mouseReleased(MouseEvent evt)
   {
      // Do Nothing.
   }

   public void mouseClicked(MouseEvent e)
   {
      // Reset Panel.
      resetPanel();
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
   
   private void drawPanel(Graphics g1)
   {
      // Class Methods
      Graphics2D g2D;
      AffineTransform scaleAffineTransform;
      int panelWidth, panelHeight;
      int currentXPosition, currentYPosition;

      // Panel parameters.
      g2D = (Graphics2D) g1;
      g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      panelWidth = this.getWidth();
      panelHeight = this.getHeight();

      // Make sure refill background color.
      
      g2D.setColor(Color.BLACK);
      g2D.fillRect(0, 0, panelWidth, panelHeight);

      // Problem loading image.
      if (noBackgroundImageLoaded)
      {
         GradientPaint fontGradient;
         Random randomNumber;
         Color randomColor1, randomColor2;
         Font drawingFont = new Font("Serif", Font.BOLD, 48);
         int stringWidth;
         
         // Just draw Dandy Made Productions with changing gradient
         // colors.
         
         randomNumber = new Random(System.currentTimeMillis());
         randomColor1 = new Color(Math.abs(randomNumber.nextInt() % 254),
                                 Math.abs(randomNumber.nextInt() % 254),
                                 Math.abs(randomNumber.nextInt() % 254));
         randomColor2 = new Color(Math.abs(randomNumber.nextInt() % 254),
                                  Math.abs(randomNumber.nextInt() % 254),
                                  Math.abs(randomNumber.nextInt() % 254));
         
         fontGradient = new GradientPaint(new Point2D.Double(0.0, 0.0), randomColor1,
            new Point2D.Double(panelWidth, (panelHeight / 10.0)), randomColor2);
         g2D.setPaint(fontGradient);
         g2D.setFont(drawingFont);
         
         stringWidth = (int) drawingFont.getStringBounds("Dandy Made Productions",
                                  g2D.getFontRenderContext()).getWidth();
         
         g2D.drawString("Dandy Made Productions",
                        ((panelWidth - stringWidth)) / 2,
                        (panelHeight / 2));
      }
      // Normal animate sequence.
      else
      {
         // Scale appropriately.
         
         scaleAffineTransform = AffineTransform.getScaleInstance(
            (panelWidth / ((double) backgroundImageWidth)) + 0.1,
            (panelHeight / ((double) backgroundImageHeight) + 0.1));
         
         g2D.setTransform(scaleAffineTransform);
         
         // Setup to begin drawing.
         currentXPosition = 0;
         currentYPosition = 0;
         
         // Draw the sub-images.
         for (int i = 0; i < imageIcons.length; i++)
         {
            for (int j = 0; j < imageIcons[0].length; j++)
            {

               if ((i == blankPosition.x && j == blankPosition.y) && !delayAnimation)
               {
                  // Do nothing, this is the hole.
                  // System.out.println("not drawing:" + i + ":" + j);
               }
               else
               {
                  // Check to see if current sub-image is the one
                  // to animate.
                  if (i == movingPosition.x && j == movingPosition.y)
                  {
                     // Setup if sub-image to animate has not
                     // yet been moved. New one.

                     if (!movingPositionInitialized)
                     {
                        currentMovingPosition.x = currentXPosition;
                        currentMovingPosition.y = currentYPosition;

                        // Bound it.
                        if (moveDirection.equals("Right"))
                        {
                           destinationPosition.x = currentMovingPosition.x + imageIconSize;
                           destinationPosition.y = currentMovingPosition.y;
                        }
                        else if (moveDirection.equals("Left"))
                        {
                           destinationPosition.x = currentMovingPosition.x - imageIconSize;
                           destinationPosition.y = currentMovingPosition.y;
                        }
                        else if (moveDirection.equals("Down"))
                        {
                           destinationPosition.x = currentMovingPosition.x;
                           destinationPosition.y = currentMovingPosition.y + imageIconSize;
                        }
                        // Must be up.
                        else
                        {
                           destinationPosition.x = currentMovingPosition.x;
                           destinationPosition.y = currentMovingPosition.y - imageIconSize;
                        }
                        movingPositionInitialized = true;
                     }
                     
                     // Draw the animated sub-image and determine next
                     // position as needed.
                     
                     g2D.drawImage(currentImageIcons[i][j].getImage(), currentMovingPosition.x,
                        currentMovingPosition.y, this);

                     if (moveDirection.equals("Right"))
                     {
                        currentMovingPosition.x += moveIncrement;
                        if (currentMovingPosition.x > destinationPosition.x)
                           movingPositionInitialized = false;
                     }
                     else if (moveDirection.equals("Left"))
                     {
                        currentMovingPosition.x -= moveIncrement;
                        if (currentMovingPosition.x < destinationPosition.x)
                           movingPositionInitialized = false;
                     }
                     else if (moveDirection.equals("Down"))
                     {
                        currentMovingPosition.y += moveIncrement;
                        if (currentMovingPosition.y > destinationPosition.y)
                           movingPositionInitialized = false;
                     }
                     else
                     {
                        currentMovingPosition.y -= moveIncrement;
                        if (currentMovingPosition.y < destinationPosition.y)
                           movingPositionInitialized = false;
                     }

                     // Reached final destination so setup up new
                     // blank position with the moving position and
                     // from that determine the next sub-image to move.
                     
                     if (!movingPositionInitialized)
                     {
                        currentImageIcons[blankPosition.x][blankPosition.y] = currentImageIcons[movingPosition.x][movingPosition.y];
                        blankPosition.x = movingPosition.x;
                        blankPosition.y = movingPosition.y;
                        selectMovingSubImage();
                     }
                  }
                  // Just draw sub-image no animation.
                  else
                     g2D.drawImage(currentImageIcons[i][j].getImage(),
                                 currentXPosition,
                                 currentYPosition, this);
               }

               // Increment to next vertical position.
               currentYPosition += imageIconSize;
            }

            // Increment to next horizontal position
            // and reset to intial vertical position.
            currentXPosition += imageIconSize;
            currentYPosition = 0;
         }
      }
   }
   
   //==============================================================
   // Class Method to reset the animation.
   //==============================================================

   public void resetPanel()
   {
      currentTime = 0;
      delayAnimation = true;
      
      // Can't load some of this stuff if the
      // background image is missing.
      
      if (!noBackgroundImageLoaded)
      {
         initializeAnimation();
         selectMovingSubImage();
         movingPositionInitialized = false;
      } 
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
