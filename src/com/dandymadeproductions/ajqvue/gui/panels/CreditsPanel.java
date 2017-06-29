//=================================================================
//                       Credits Panel 
//=================================================================
//
//    This class provides a general container to display the basic
// information about the application's version, build, webSite, and
// credits.
//
//                   << CreditsPanel.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.1 06/29/2017
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
// Version 1.0 Production CreditsPanel Class.
//         1.1 Removed Arguments version & webSiteString From Constructor.
//         
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The CreditsPanel provides a general container to display the basic
 * information about the application's version, build, webSite, and
 * credits.
 * 
 * @author Dana M. Proctor
 * @version 1.1 06/29/2017
 */

public class CreditsPanel extends JPanel implements Runnable
{
   // Class Instances.
   private static final long serialVersionUID = 70355272186430290L;
   
   private String[] version;
   private String webSiteString;
   private volatile boolean runThread;
   private transient Image offScreenGraphicsImage;
   private transient Image backgroundImage;
   private int backgroundImageWidth, backgroundImageHeight;
   private int yOffset;

   private static final int scrollPadding = 75;
   private static final long scrollSpeed = 40;

   private static String[] developersNames = {"Dana M. Proctor", "sqlite.org", "hsqldb.org",
                                       "h2database.com", "itextpdf.com", "gnu.org/software/freefont/"};
   private static String[] developersTitles = {"Project Manager", "Sqlite", "HyperSQL",
                                        "H2 Database", "iText Library", "FreeFont"};

   //===========================================================
   // CreditsPanel Constructor
   //===========================================================

   public CreditsPanel()
   {
      version = Ajqvue.getVersion();
      webSiteString = Ajqvue.getWebSite();

      AResourceBundle resourceBundle = Ajqvue.getResourceBundle();
      String fileSeparator = Utils.getFileSeparator();
      String dataSourceType = ConnectionManager.getDataSourceType();
      
      runThread = true;

      // Get the corresponding background image.

      if (dataSourceType.equals(ConnectionManager.MYSQL))
         backgroundImage = (resourceBundle.getResourceImage("images"
                                                            + fileSeparator + "dolphin.jpg")).getImage();
      else if (dataSourceType.equals(ConnectionManager.MARIADB))
         backgroundImage = (resourceBundle.getResourceImage("images"
                                                            + fileSeparator + "seal.jpg")).getImage();
      else if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
         backgroundImage = (resourceBundle.getResourceImage("images"
                                                            + fileSeparator + "elephant.jpg")).getImage();
      else if (dataSourceType.indexOf(ConnectionManager.HSQL) != -1)
         backgroundImage = (resourceBundle.getResourceImage("images"
                                                            + fileSeparator + "spiral.jpg")).getImage();
      else if (dataSourceType.equals(ConnectionManager.ORACLE))
         backgroundImage = (resourceBundle.getResourceImage("images"
                                                            + fileSeparator + "letterO.jpg")).getImage();
      else if (dataSourceType.equals(ConnectionManager.SQLITE))
         backgroundImage = (resourceBundle.getResourceImage("images"
                                                            + fileSeparator + "feather.jpg")).getImage();
      else if (dataSourceType.equals(ConnectionManager.MSACCESS))
         backgroundImage = (resourceBundle.getResourceImage("images"
                                                            + fileSeparator + "key.jpg")).getImage();
      else if (dataSourceType.equals(ConnectionManager.MSSQL))
         backgroundImage = (resourceBundle.getResourceImage("images"
                                                            + fileSeparator + "framework.jpg")).getImage();
      else if (dataSourceType.equals(ConnectionManager.DERBY))
         backgroundImage = (resourceBundle.getResourceImage("images"
                                                            + fileSeparator + "derby.jpg")).getImage();
      else if (dataSourceType.equals(ConnectionManager.H2))
         backgroundImage = (resourceBundle.getResourceImage("images"
                                                            + fileSeparator + "h2.jpg")).getImage();
      else
         backgroundImage = (resourceBundle.getResourceImage("images"
                                                            + fileSeparator + "battleship.jpg")).getImage();
      
      backgroundImageWidth = backgroundImage.getWidth(null);
      backgroundImageHeight = backgroundImage.getHeight(null);
   }

   //================================================================
   // Class method for starting the runnable thread.
   //================================================================

   public void run()
   {
      // Cycling through the routine
      // to animate the credits.
      while (runThread)
      {
         render();
         yOffset--;
         timeStep();
      }
   }

   //================================================================
   // Class method to create a double buffered offscreen graphic.
   //================================================================

   private void render()
   {
      // Clear and redraw the graphics background then
      // draw the component offscreen.
      
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
   // Class method for delaying the animation/framerate change.
   //================================================================

   private void timeStep()
   {
      try
      {
         Thread.sleep(scrollSpeed);
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
   
   //================================================================
   // Overriding Class method to paint the panel's contents.
   //================================================================

   private void drawPanel(Graphics g)
   {
      // Class Method Instances
      Graphics2D g2, imageGraphics;
      Image imageOffScreenGraphics;
      AffineTransform scaleAffineTransform;
      
      Font nameFont, titleFont;
      FontMetrics fontMetrics;
      String fontName;
      BasicStroke pen2, pen3;
      Line2D borderLineTop, borderLineLeft, borderLineBottom, borderLineRight;
      int fontSize, stringWidth;
      int width, height, x, y;

      // Graphics Setup
      g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       
      // Setting up a font and sizes
      if (this.getFont() != null)
      {
         fontName = this.getFont().getFontName();
         fontSize = this.getFont().getSize();
         nameFont = this.getFont();
         titleFont = new Font(fontName, Font.BOLD, fontSize);
      }
      else
      {
         nameFont = new Font("Serif", Font.PLAIN, 12);
         titleFont = new Font("Serif", Font.BOLD, 12);
      }

      // Create pens to use
      pen2 = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
      pen3 = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

      // Setup general attributes to run the credits
      x = 0;
      y = 0;
      stringWidth = 0;
      width = getWidth();
      height = getHeight();

      // Draw the background detail.

      // Background
      g2.setColor(Color.WHITE);
      g2.fillRect(0, 0, width, height);

      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.22));
      
      if (width != backgroundImageWidth || height != backgroundImageHeight)
      {
         imageOffScreenGraphics = createImage(width, height);
         imageGraphics = (Graphics2D) imageOffScreenGraphics.getGraphics();
         
         scaleAffineTransform = AffineTransform.getScaleInstance(
            width / ((double) backgroundImageWidth), height / ((double) backgroundImageHeight));
         
         imageGraphics.setTransform(scaleAffineTransform);
         imageGraphics.drawImage(backgroundImage, 0, 0, null);
         g2.drawImage(imageOffScreenGraphics, 0, 0, null);
         
         imageGraphics.dispose();
      }
      else
         g2.drawImage(backgroundImage, 0, 0, null);
      
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1.0));

      // Sunken Frame Look
      g2.setStroke(pen2);
      g2.setPaint(Color.LIGHT_GRAY);

      borderLineBottom = new Line2D.Double(0, (double) height, (double) width, (double) height);
      borderLineRight = new Line2D.Double((double) width, 0, (double) width, (double) height);
      g2.draw(borderLineBottom);
      g2.draw(borderLineRight);

      g2.setStroke(pen3);
      g2.setPaint(Color.DARK_GRAY);

      borderLineTop = new Line2D.Double(0, 0, (double) width, 0);
      borderLineLeft = new Line2D.Double(0, 0, 0, (double) height);
      g2.draw(borderLineTop);
      g2.draw(borderLineLeft);

      // Draw Ajqvue
      g2.setColor(Color.BLACK);
      g2.setFont(titleFont);
      fontMetrics = g2.getFontMetrics();
      stringWidth = fontMetrics.stringWidth(version[0]);
      x = (width - stringWidth) / 2;
      y += fontMetrics.getHeight() + 1;
      g2.drawString(version[0], x, y + yOffset + height);

      // Draw Verion and Build ID
      g2.setFont(nameFont);
      stringWidth = fontMetrics.stringWidth(version[1]);
      x = (width - stringWidth) / 2;
      y += fontMetrics.getHeight() + 1;
      g2.drawString(version[1], x, y + yOffset + height);

      stringWidth = fontMetrics.stringWidth(version[2]);
      x = (width - stringWidth) / 2;
      y += fontMetrics.getHeight() + 1;
      g2.drawString(version[2], x, y + yOffset + height);

      y += 15;

      // Draw Credits
      for (int i = 0; i < developersNames.length; i++)
      {
         g2.setFont(titleFont);
         fontMetrics = g2.getFontMetrics();
         stringWidth = fontMetrics.stringWidth(developersTitles[i]);
         x = (width - stringWidth) / 2;
         y += fontMetrics.getHeight() + 1;
         g2.drawString(developersTitles[i], x, y + yOffset + height);

         g2.setFont(nameFont);
         fontMetrics = g2.getFontMetrics();
         stringWidth = fontMetrics.stringWidth(developersNames[i]);
         x = (width - stringWidth) / 2;
         y += fontMetrics.getHeight() + 1;
         g2.drawString(developersNames[i], x, y + yOffset + height);

         if (i == developersNames.length - 1)
            if (Math.abs(yOffset) >= (y + height + scrollPadding))
               yOffset = 0;
         y += 15;
      }

      // Draw Web Site String
      g2.setFont(titleFont);
      fontMetrics = g2.getFontMetrics();
      stringWidth = fontMetrics.stringWidth("WebSite");
      x = (width - stringWidth) / 2;
      y += fontMetrics.getHeight() + 1;
      g2.drawString("WebSite", x, y + yOffset + height);

      g2.setFont(nameFont);
      fontMetrics = g2.getFontMetrics();
      stringWidth = fontMetrics.stringWidth(webSiteString);
      x = (width - stringWidth) / 2;
      y += fontMetrics.getHeight() + 1;
      g2.drawString(webSiteString, x, y + yOffset + height);
   }
   
   //==============================================================
   // Class Method to let the thread run() method naturally
   // finish.
   //==============================================================

   public synchronized void suspendPanel(boolean action)
   {
      runThread = !action;
   }
}
