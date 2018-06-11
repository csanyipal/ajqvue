//=================================================================
//                   SQLQueryBucketListObject Class
//=================================================================
//   This class is used to create a variant from a standard Swing
// button that exempts translucency.
//
//                  << SQLQueryBucketListObject.java >>
//
//=================================================================
// 
// Copyright (C) 2016-2018 Dana M. Proctor
// All rights reserved.
// Version 1.2 06/10/2017
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
// Version 1.0 05/01/2007 Original Sun Microsystems, Inc TransparentButton Class.
//         1.1 09/17/2016 Integration Into Ajqvue as SQLQueryBucketListObject.
//         1.2 06/10/2017 Method constructSQLQueryBucketListObject() setBorder().
//                           
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;

//=================================================================
//                    SQLQueryBucketListObject
//=================================================================

/**
 *    The SQLQueryBucketListObject class is used to a variant from a
 * standard Swing button that exempts translucency.
 * 
 * @author Chet, Dana M. Proctor
 * @version 1.2 06/10/2017
 */

public class SQLQueryBucketListObject extends JButton
{
   // Class Instances.
   private static final long serialVersionUID = 2957461545143942698L;
   private static final float alphaValue = 0.76f;
   
   private transient BufferedImage buttonImage = null;
   private StringBuffer sqlStatementString;
   private boolean isLimited;

   //==============================================================
   // SQLQueryBucketListObject Constructor
   //==============================================================
   
   public SQLQueryBucketListObject()
   {
      super();
      constructSQLQueryBucketListObject();
   }
   
   public SQLQueryBucketListObject(String label)
   {
      super(label);
      constructSQLQueryBucketListObject();
   }
   
   private void constructSQLQueryBucketListObject()
   {
      sqlStatementString = new StringBuffer();
      isLimited = false;
      
      setBorder(BorderFactory.createRaisedSoftBevelBorder());
      setFont(getFont().deriveFont(Font.BOLD));
      setContentAreaFilled(false);
      setOpaque(false);
   }
   
   //==============================================================
   // Class method to overide the standard panel paintComponents
   // routine.
   //==============================================================
   
   public void paintCompnent(Graphics g)
   {
      super.paint(g);
      drawGraphics(g);
      
   }
   
   //==============================================================
   // Overide the standard paint component method.
   //==============================================================
   
   private void drawGraphics(Graphics g)
   {
      // Create an image for the button graphics if necessary
      if (buttonImage == null || buttonImage.getWidth() != getWidth()
          || buttonImage.getHeight() != getHeight())
      {
         buttonImage = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight());
      }
      Graphics gButton = buttonImage.getGraphics();
      gButton.setClip(g.getClip());

      // Have the superclass render the button for us
      super.paint(gButton);

      // Make the graphics object sent to this paint() method translucent
      Graphics2D g2d = (Graphics2D) g;
      AlphaComposite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue);
      g2d.setComposite(newComposite);

      // Copy the button's image to the destination graphics, translucently
      g2d.drawImage(buttonImage, 0, 0, null);
   }
   
   //==============================================================
   // Class Method to set the LIMIT aspect of the SQL Statement.
   //==============================================================
   
   public boolean isLimited()
   {
      return isLimited;
   }
   
   //==============================================================
   // Class Method to get the SQL Statement String associated with
   // this translucent button. Duh, this is a special class to be
   // used with the Ajqvue SQLQueryBucketFrame Class.
   //==============================================================
   
   public StringBuffer getSQLStatementString()
   {
      if (!isLimited)
      {
         String newSQLStatementString;
         newSQLStatementString = Utils.getUnlimitedSQLStatementString(sqlStatementString.toString());
         sqlStatementString.delete(0, sqlStatementString.length());
         sqlStatementString.append(newSQLStatementString);
         return sqlStatementString;
      }
      else
         return sqlStatementString;
   }
   
   //==============================================================
   // Class Method to set the SQL Statement String associated with
   // this translucent button.
   //==============================================================
   
   public void setSQLStatementString(String value)
   {
      sqlStatementString.delete(0, sqlStatementString.length());
      sqlStatementString.append(value);
   }
   
   //==============================================================
   // Class Method to set the LIMIT aspect of the SQL Statement.
   //==============================================================
   
   public void setLimited(boolean value)
   {
      isLimited = value;
   }  
}
