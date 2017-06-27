//=================================================================
//                  PluginFrameFillerPanel
//=================================================================
//
//    This class provides a panel that is used in the PluginFrame
// to provide a generic animated filler graphic.
//
//              << PluginFrameFillerPanel.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.1 11/06/2016
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
// Version 1.0 09/18/2016 Production PluginFrameFillerPanel Class.
//         1.1 11/06/2016 Minor Format Changes.
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The PluginFrameFillerPanel class provides a panel that is used
 * in the PluginFrame to provide a generic animated filler graphic.   
 * 
 * @author Dana M. Proctor
 * @version 1.1 11/06/2016
 */

public class PluginFrameFillerPanel extends JPanel implements Runnable
{
   // Class Instances.
   private static final long serialVersionUID = 4904842189669960218L;
   
   private transient Image baseImage;
   private transient BufferedImage waveImage;
   private transient Image offScreenGraphicsImage;
   
   private int waveImageIndex;
   private Color waveBackgroundColor;
   private transient BasicStroke widePen, narrowPen;
   
   private boolean runThread;
   private boolean suspendThread;
   private int baseImageWidth, baseImageHeight;

   private static final int WAVE_FRAME_NUMBER = 12;
   private static final int FRAME_DELAY = 85;

   //==============================================================
   // PluginFrameFillerPanel Constructor
   //==============================================================

   public PluginFrameFillerPanel()
   {
      // Class Instances
      AResourceBundle resourceBundle;
      String fileSeparator;
      
      //======================================================
      // Obtaining the background image and setting up as
      // needed instances values.
      
      waveImageIndex = 0;
      waveBackgroundColor = Color.decode("0x474a05");
      widePen = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
      narrowPen = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
      
      resourceBundle = Ajqvue.getResourceBundle();
      fileSeparator = Utils.getFileSeparator();
      
      baseImage = resourceBundle.getResourceImage("images" + fileSeparator + "pluginframe.jpg").getImage();
      
      baseImageWidth = baseImage.getWidth(null);
      baseImageHeight = baseImage.getHeight(null);
      
      // System.out.println(baseImageWidth + ":" + baseImageHeight);
      
      // Failed to load background image.
      if (baseImageWidth <= 0 || baseImageHeight <= 0)
      {
         runThread = false;
         suspendThread = true;
         System.out.println("Failed to Load Background Image.");
      }
      // Create inverted image of original background image.
      else
      {
         createBaseImages();
         
         // Set to allow running.
         runThread = true;
         suspendThread = false;
      }
      
      setPreferredSize(new Dimension(baseImageWidth, (2 * baseImageHeight) - 30));
   }
   
   //==============================================================
   // Class method to handle the colection/creation of the base
   // image for the animation along with the individual wave frames.
   //==============================================================
   
   private void createBaseImages()
   {
      // Method Instances.
      BufferedImage baseBufferedImage;
      Graphics baseBufferedGraphics;
      Graphics waveImageGraphics;
      
      AffineTransform mirror_AffineTransform;
      AffineTransformOp affineTransformOp;
      
      
      baseBufferedImage = new BufferedImage(baseImageWidth, baseImageHeight + 1,
                                            BufferedImage.TYPE_INT_RGB);
      baseBufferedGraphics = baseBufferedImage.getGraphics();
      baseBufferedGraphics.drawImage(baseImage, 0, 1, this);

      mirror_AffineTransform = AffineTransform.getScaleInstance(1, -1);
      mirror_AffineTransform.translate(0, -baseImageHeight);
      affineTransformOp = new AffineTransformOp(mirror_AffineTransform, 
                                                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
      baseBufferedImage = affineTransformOp.filter(baseBufferedImage, null);

      // Create the large (WAVE_FRAME_NUMBER + 1 times the width) image
      // that will store dithered copies of the inverted original.

      waveImage = new BufferedImage((WAVE_FRAME_NUMBER + 1) * baseImageWidth, baseImageHeight,
                                    BufferedImage.TYPE_INT_RGB);
      waveImageGraphics = waveImage.getGraphics();
      waveImageGraphics.drawImage(baseBufferedImage, WAVE_FRAME_NUMBER * baseImageWidth, 0, this);

      // Create dithered copies (sine displacement up or down) of the
      // inverted original.

      for (int phase = 0; phase < WAVE_FRAME_NUMBER; phase++) 
         makeWaves(waveImageGraphics, phase);
   }
   
   //==============================================================
   // Method to take the initial (unwaved) image from the
   // left-handside of the wave graphics and make WAVE_FRAME_NUMBER
   // copies of it. The pixels rows of each one dithered up or down
   // depending upon the displacementY sine function.
   // ---------------------------------------------------------------------------

   private void makeWaves(Graphics g, int phase)
   {
      double radianPhase;
      int disp_X, disp_Y;
      
      // Convert the phase into radians (by splitting 2*PI into
      // WAVE_FRAME_NUMBER segments).
      
      radianPhase = 2 * Math.PI * (double) phase / (double) WAVE_FRAME_NUMBER;
      
      // displacementX defines how far across the image has to be
      // copied from the original LHS frame.
      
      disp_X = (WAVE_FRAME_NUMBER - phase) * baseImageWidth;
      
      // Process each horizontal line of pixels. Copy across
      // from original frame on the left-had-side and displacing
      // up or down WRT the dispy sine function.
      
      for (int i = 0; i < baseImageHeight; i++)
      {
         // displacementY defines the vertical sine displacement. It
         // attenuates higher up the image, for perspective.
         
         disp_Y = (int) ((baseImageHeight / 14.0)
                         * ((double) i + 28.0)
                         * Math.sin((double) ((baseImageHeight / 14)
                         * (baseImageHeight - i)) / (double) (i + 1)
                                   + radianPhase) / (double) baseImageHeight);
         
         // If no line dithers here then copy original.
         // --------------------------------------------------------
         
         if (i < -disp_Y)
            g.copyArea(WAVE_FRAME_NUMBER * baseImageWidth, i, baseImageWidth, 1, -disp_X, 0);
         else
            // Else copy dithered line.
            // --------------------------------------------------------
            g.copyArea(WAVE_FRAME_NUMBER * baseImageWidth, i + disp_Y,
                       baseImageWidth, 1, -disp_X, -disp_Y);
      }
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
         updateAnimation();
         render();
         timeStep();
      }
   }

   //==============================================================
   // Class method to update the image's animated elements.
   //==============================================================

   private void updateAnimation()
   {
      if (++waveImageIndex == WAVE_FRAME_NUMBER)
         waveImageIndex = 0;
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
            drawAnimation(imageGraphics);
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
   // Class Methods to actually draw the graphics involved with
   // the panel.
   //==============================================================

   private void drawAnimation(Graphics g)
   {
      // Method Instances.
      Graphics2D g2d;
      int panelWidth, panelHeight;
      AffineTransform orgAffineTransform, scaleAffineTransform;
      
      // Check to see if valid input.
      if (g == null)
         return;

      // ==================================
      // Setup various required variables.

      g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      panelWidth = getWidth();
      panelHeight = getHeight();
      orgAffineTransform = g2d.getTransform();
      
      // Do a fill.
      
      g2d.setPaint(waveBackgroundColor);
      g2d.fillRect(0, 0, panelWidth, panelHeight);
      
      // Check to see if panel size has been changed
      // so that the animation can be scaled appropriately.
      
      if (panelWidth != baseImageWidth || panelHeight != 2 * baseImageHeight)
      {
         scaleAffineTransform = AffineTransform.getScaleInstance(panelWidth / ((double) baseImageWidth),
                                                                 panelHeight / ((double) 2 * baseImageHeight));
         
         g2d.setTransform(scaleAffineTransform);
         
      }
      
      // Draw current rippled image in lower half.
      
      if (waveImage != null)
      {
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
         
         g2d.drawImage (waveImage, (-waveImageIndex * baseImageWidth), baseImageHeight, this);
         g2d.drawImage (waveImage, ((WAVE_FRAME_NUMBER - waveImageIndex) * baseImageWidth), 
                        baseImageHeight, this);
         
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
      }
      
      // Draw the original base image in the tophalf.
      
      g2d.drawImage(baseImage, 0, 0, this);
      
      // Draw a border for the panel.
      
      g2d.setTransform(orgAffineTransform);
      
      g2d.setStroke(widePen);
      g2d.setPaint(this.getForeground());
      g2d.draw(new Line2D.Double(0.0, 0.0, (double) panelWidth, 0.0));
      g2d.draw(new Line2D.Double(0.0, 0.0, 0.0, (double) panelHeight));

      g2d.setStroke(narrowPen);
      g2d.setPaint(Color.white);
      g2d.draw(new Line2D.Double((double) panelWidth, 3.0, (double) panelWidth, (double) panelHeight));
      g2d.draw(new Line2D.Double(3.0, (double) panelHeight, (double) panelWidth, (double) panelHeight));
            
      g2d.dispose();
   }

   //==============================================================
   // Class method for delaying the animation/framerate change.
   //==============================================================

   private void timeStep()
   {
      try
      {
         Thread.sleep(FRAME_DELAY);
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
   // Class method to overide the standard panel paintComponents
   // routine.
   //==============================================================

   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      drawAnimation(g);
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
