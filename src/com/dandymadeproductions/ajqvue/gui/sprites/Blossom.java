//=================================================================
//                         Blossom
//=================================================================
//
//    This class provides a means to generate a blooming flower
// object along with drawing to a graphics space.
//
//                   << Blossom.java >>
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
// Version 1.0 Production Blossom Class.
//                           
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.sprites;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 *    The Blossom class provides a means to generate a blooming flower
 * object along with drawing to a graphics space.     
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class Blossom 
{
   // Class Instances.
   private Point2D.Double position;
   private Dimension2D petalSize;
   private int petalNumber;
   private double rotation;
   private double petalSizeIncrement;
   
   private Stroke stroke;
   private Paint outlinePaint;
   private Paint fillPaint;
   private Random random;
   
   private boolean petalFilled;
   
   private static float STROKE_WIDTH = 1.0f;
   private static double DEFAULT_WIDTH = 5.0;
   private static double DEFAULT_MAX_WIDTH = 5 * DEFAULT_WIDTH;
   private static double DEFAULT_HEIGHT = 15.0;
   // private static double DEFAULT_MAX_HEIGHT = 3 * DEFAULT_HEIGHT;
   private static int DEFAULT_PETAL_NUMBER = 12;
   private static Paint DEFAULT_COLOR = Color.ORANGE;
   private static Paint DEFAULT_FILL = new GradientPaint(0.0F, 0.0f, Color.BLUE,
                                                         100.0F, 100.0F, Color.GREEN, true);
   private static double DEFAULT_OFFSET_ANGLE = Math.PI / 8.0;
   
   //==============================================================
   // Blossom Constructors
   //==============================================================
   
   public Blossom()
   {
      this(null, null);
   }
   
   public Blossom(Point2D.Double position, Dimension2D petalSize)
   {
      this(position, petalSize, DEFAULT_PETAL_NUMBER,
           new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_SQUARE,
           BasicStroke.JOIN_BEVEL), DEFAULT_COLOR, DEFAULT_FILL);
   }
   
   public Blossom(Point2D.Double position, Dimension2D petalSize, Paint outlinePaint)
   {
      this(position, petalSize, DEFAULT_PETAL_NUMBER,
           new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_SQUARE,
           BasicStroke.JOIN_BEVEL), outlinePaint, DEFAULT_FILL);
   }

   public Blossom(Point2D.Double position, Dimension2D petalSize, int petalNumber, Stroke stroke,
                      Paint outlinePaint, Paint fillPaint)
   {
      // Asign Position
      if (position == null)
      {
         position = new Point2D.Double();
         position.setLocation(0.0, 0.0);
      }
      this.position = position;
      
      // Asign Petal Size
      if (petalSize == null)
      {
         petalSize = new Dimension();
         petalSize.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
         
      }
      this.petalSize = petalSize;
      
      // Asign Rotation
      this.petalNumber = petalNumber;
      
      // Asign Stroke
      if (stroke == null)
         stroke = new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_SQUARE,
                                  BasicStroke.JOIN_BEVEL);
      this.stroke = stroke;
      
      // Asign Draw Paint
      if (outlinePaint == null)
         outlinePaint = DEFAULT_COLOR;
      this.outlinePaint = outlinePaint;
      
      // Asign Fill Paint
      if (fillPaint == null)
         fillPaint = DEFAULT_FILL;
      this.fillPaint = fillPaint;
      
      // Default Fill, Rotation, Petal Size
      // Increment & Random Generator.
      random = new Random();
      petalFilled = true;
      rotation = 0;
      petalSizeIncrement = 7.0 * random.nextDouble();
      
   }

   //==============================================================
   // Class Method to actually draw the graphics involved with
   // blossom.
   //==============================================================
   
   public void draw(Graphics2D g2)
   {
      // Method Instances
      float alpha;
      AffineTransform affineTransform;
      AlphaComposite alphaComposite;
      Ellipse2D.Double petal;
      double petalWidth, petalLength;
      double rotationAngle;
      
      // Setup to draw the blossom.
      
      alpha = 0.9F;
      alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
      petalWidth = petalSize.getWidth();
      petalLength = petalSize.getHeight();
      
      petal = new Ellipse2D.Double(position.getX(), position.getY(), petalWidth, petalLength);
      rotationAngle = (2 * Math.PI) / petalNumber;
      
      // Draw decorative blossom.
      
      g2.setComposite(alphaComposite);
      g2.setStroke(stroke);
      
      // Create the rotation effect.
      rotation += DEFAULT_OFFSET_ANGLE;
      affineTransform = AffineTransform.getRotateInstance(rotation,
         position.getX() + petalWidth / 2.0, position.getY() + petalLength);
      g2.transform(affineTransform);
      
      // Create blossom from petals
      for (int i = 0; i < petalNumber; i++)
      {
         // Filled
         if (petalFilled)
         {
            g2.setPaint(fillPaint);
            g2.fill(petal);
         }
         // Outline
         g2.setPaint(outlinePaint);
         g2.draw(petal);
         
         // Rotate
         affineTransform = AffineTransform.getRotateInstance(rotationAngle,
            position.getX() + petalWidth / 2.0, position.getY() + petalLength);
         
         g2.transform(affineTransform);
      }
      
      // Restore the original transformation.
      affineTransform.setToIdentity();
      g2.setTransform(affineTransform);
      
      // Oscillate Petal Size & Randomize Draw/Fill
      if (petalSize.getWidth() > DEFAULT_MAX_WIDTH)
      {
         petalSizeIncrement = -(5.2 * random.nextDouble());
         outlinePaint = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
      }
      
      if (petalSize.getWidth() < 0)
      {
         petalSizeIncrement = 2.4 * random.nextDouble();
         fillPaint = new GradientPaint(0.0F, 0.0f, new Color(random.nextInt(256), random.nextInt(256),
                                                             random.nextInt(256)),
                                       100.0F, 100.0F, new Color(random.nextInt(256), random.nextInt(256),
                                                                 random.nextInt(256)), true);
      }
      
      petalSize.setSize(petalSize.getWidth() + petalSizeIncrement,
                        petalSize.getHeight() + petalSizeIncrement);
   }
   
   //==============================================================
   // Class Methods to get the various characteristics of the
   // blossom.
   //==============================================================
   
   public Point2D.Double getPosition()
   {
      return position;
   }
   
   public Dimension2D getPetalSize()
   {
      return petalSize;
   }
   
   public Stroke getStroke()
   {
      return stroke;
   }
   
   public Paint getOutlinePaint()
   {
      return outlinePaint;
   }
   
   public Paint getFillPaint()
   {
      return fillPaint;
   }
   
   //==============================================================
   // Class Methods to set the various characteristics of the
   // blossom.
   //==============================================================
   
   public void setPosition(Point2D.Double newPosition)
   {
      position = newPosition;
   }
   
   public void setPetalSize(Dimension2D newPetalSize)
   {
      petalSize = newPetalSize;
   }
   
   public void setStroke(Stroke newStroke)
   {
      stroke = newStroke;
   }
   
   public void setOutlinePaint(GradientPaint newOutlinePaint)
   {
      outlinePaint = newOutlinePaint;
   }
   
   public void setFillPaint(Paint newFillPaint)
   {
      fillPaint = newFillPaint;
   }
   
   public void isPetalFilled(boolean isFilled)
   {
      petalFilled = isFilled;
   }
}
