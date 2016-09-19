//=================================================================
//                      GraphicsCanvasPanel
//=================================================================
//
//    This class provides a generic panel used to paint a supplied
// image as its main component.
//
//                   << GraphicsCanvasPanel.java >>
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
// Version 1.0 09/18/2016 Production GraphicsCanvasPanel Class.
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

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The GraphicsCanvasPanel provides a generic panel used to paint a
 * supplied image as its main component.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class GraphicsCanvasPanel extends JPanel
{
   // Class Instances
   private static final long serialVersionUID = 7618323328966733483L;
   
   private transient Image backgroundImage;
   private int backgroundImageWidth;
   private int backgroundImageHeight;
   private transient Image offScreenGraphicsImage;

   //==============================================================
   // GraphicsCanvasPanel Constructor
   //==============================================================

   public GraphicsCanvasPanel(String imageFileNameString)
   {
      this(Ajqvue.getResourceBundle().getResourceImage("images" + Utils.getFileSeparator()
                                                       + imageFileNameString).getImage());
   }
   
   public GraphicsCanvasPanel(Image backgroundImage)
   {
      this.backgroundImage = backgroundImage;
      
      // Setting up the panel stuff.
      setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                BorderFactory.createLoweredBevelBorder()));
      
      backgroundImageWidth = backgroundImage.getWidth(null);
      backgroundImageHeight = backgroundImage.getHeight(null);
   }
   
   //==============================================================
   // Class method to overide the standard panel paintComponents
   // routine.
   //==============================================================

   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      
      Dimension d = getSize();
      if (checkImage(d))
      {
         drawPanel(g);
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
   // Class method to overide the standard panel paintComponents
   // routine.
   //==============================================================
   
   private void drawPanel(Graphics g)
   {
      super.paintComponent(g);
      
      // Class Methods
      Graphics2D g2d;
      int panelWidth, panelHeight;
      AffineTransform scaleAffineTransform;

      // Panel parameters.
      g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      panelWidth = this.getWidth();
      panelHeight = this.getHeight();

      // Make sure refill background color.
      g2d.setColor(this.getBackground());
      g2d.fillRect(0, 0, panelWidth, panelHeight);

      // Check to see if panel size has been changed
      // so that the animation can be scaled appropriately.
      
      if (panelWidth != backgroundImageWidth || panelHeight != backgroundImageHeight)
      {
         
         scaleAffineTransform = AffineTransform.getScaleInstance(
            panelWidth / ((double) backgroundImageWidth), panelHeight / ((double) backgroundImageHeight));
         
         Graphics2D imageGraphics = (Graphics2D) offScreenGraphicsImage.getGraphics();
         imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         imageGraphics.setTransform(scaleAffineTransform);
         imageGraphics.drawImage(backgroundImage, 0, 0, null);
         imageGraphics.dispose();
      }
      g2d.drawImage(offScreenGraphicsImage, 0, 0, this);
      
      // Clean up.
      g2d.dispose();
   }
}
