//=================================================================
//                 Image Graphics Frame
//=================================================================
//   The ImageGraphicsFrame class provides a canvas/frame that
// can be painted on to display a table item image.
//
//                << ImageGraphicsFrame.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.0 09/17/2016
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
// Version 1.0 09/17/2016 Production ImageGraphicsFrame Class.
//
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

/**
 *    The ImageGraphicsFrame class provides a canvas/frame that
 * can be painted on to display a item image.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class ImageGraphicsFrame extends JFrame
{
   // Creation of the necessary class instance variables
   // for the JFrame.

   private static final long serialVersionUID = 5520696961286789397L;

   private transient Image itemImage;
   private transient Image offScreenGraphicsImage;
   private int imageWidth;
   private int imageHeight;

   //==============================================================
   // ImageGraphicsFrame Constructor
   //==============================================================

   public ImageGraphicsFrame(String frameSuperString)
   {
      super(frameSuperString);
   }
   
   //==============================================================
   // Overiding the normal paint sequence for this frame.
   //==============================================================
   
   public void paint(Graphics g)
   {  
      // Draw Image
      
      if (itemImage != null)
      {  
         if (getGraphics() != null)
         {
            Dimension d = getSize();
            if (checkImage(d))
            {
               draw(offScreenGraphicsImage.getGraphics());
               getGraphics().drawImage(offScreenGraphicsImage, 0, 0, null);
            }
         } 
      }
   }
   
   //================================================================
   // Class method to setup a offscreen image.
   //================================================================

   private boolean checkImage(Dimension d)
   {
      if (d.width == 0 || d.height == 0)
         return false;
      if (offScreenGraphicsImage == null || offScreenGraphicsImage.getWidth(null) != d.width
          || offScreenGraphicsImage.getHeight(null) != d.height)
      {
         offScreenGraphicsImage = createImage(d.width, d.height);
      }
      return true;
   }
   
   //================================================================
   // Class method to paint extra content.
   //================================================================

   private void draw(Graphics g)
   {
      // Class Method Instances
      Graphics2D g2, imageGraphics;
      Image imageOffScreenGraphics;
      AffineTransform scaleAffineTransform;
      
      int width, height;

      // Graphics Setup
      g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      width = getWidth();
      height = getHeight();
      
      if (width != imageWidth || height != imageHeight)
      {
         imageOffScreenGraphics = createImage(width, height);
         imageGraphics = (Graphics2D) imageOffScreenGraphics.getGraphics();
         
         scaleAffineTransform = AffineTransform.getScaleInstance(
            width / ((double) imageWidth), height / ((double) imageHeight));
         
         imageGraphics.setTransform(scaleAffineTransform);
         imageGraphics.drawImage(itemImage, 0, 0, null);
         g2.drawImage(imageOffScreenGraphics, 0, 0, null);
         
         imageGraphics.dispose();
      }
      else
         g2.drawImage(itemImage, 0, 0, null);
   }
   

   //==============================================================
   // Class method to center the frame.
   //==============================================================

   protected void center()
   {
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension us = getSize();
      int x = (screen.width - us.width) / 2;
      int y = (screen.height - us.height) / 2;
      setLocation(x, y);
   }

   //==============================================================
   // Class method to set the image that will be painted in this
   // frame.
   //==============================================================

   protected void setImage(Image image)
   {
      if (image != null)
      {
         itemImage = image;
         imageWidth = image.getWidth(null);
         imageHeight = image.getHeight(null);
      }
   }
}
