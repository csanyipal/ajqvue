//=================================================================
//                          Leaf
//=================================================================
//
//    This class provides the characteristics of a leaf
// object that is used in the preferences frame, Preferences
// panel fall.
// 
//                    << Leaf.java >>
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
// Version 1.0 Production Leaf Class.
//        
//-----------------------------------------------------------------
//               danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.sprites;

import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *    The Leaf class provides the characteristics of a autumn leaf
 * object that is used in the preferences frame, Preferences panel
 * fall.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class Leaf extends Sprite
{
   // Class Instances

   //==============================================================
   // Leaf Object Constructor
   //==============================================================

   public Leaf(Component component, Rectangle panelBounds, Image image,
                  Point position, Point nextPosition)
   {
      super(component, panelBounds, image, position, nextPosition);
   }

   //==============================================================
   // Class method to allow the updating of this Leaf's
   // location based on a downward movement, and sideways,
   // within boundaries.
   //==============================================================

   public void updatePosition()
   {
      // Class Method Instances.
      Point position, tempNextPosition;

      // =========================================
      // Generate random movement.

      if (random.nextInt() % 5 == 0)
      {
         Point randomOffset = new Point(random.nextInt() % 6, random.nextInt() % 6);
         nextPosition.x += randomOffset.x;
         if (nextPosition.x >= 18)
            nextPosition.x -= 2;
         if (nextPosition.x <= -3)
            nextPosition.x += 9;

         nextPosition.y += randomOffset.y;
         if (nextPosition.y >= 18)
            nextPosition.y -= 3;
         if (nextPosition.y <= 6)
            nextPosition.y += 6;
      }

      // ========================================
      // Modify nextPosition to tend toward a
      // gravity well at position y=background
      // height and position x=background width.

      position = new Point(spaceOccupied.x, spaceOccupied.y);
      tempNextPosition = new Point(nextPosition.x, nextPosition.y);

      if (random.nextInt() % 10 == 0)
      {
         // Modify x movement.
         if (position.x >= bounds.width && nextPosition.x < 0)
         {
            position.x = 0;
            tempNextPosition.x = -nextPosition.x;
         }
         else if (position.x < 0 && nextPosition.x < 0)
         {
            position.x = 0;
            tempNextPosition.x = -nextPosition.x;
         }

         // Modify y movement.
         if (position.y < 0 && nextPosition.y < 0)
         {
            tempNextPosition.y = -nextPosition.y;
         }
         else if (position.y > bounds.height)
         {
            tempNextPosition.y = 0;
         }
      }
      setNextPosition(tempNextPosition);

      // Move the leaf on the screen
      position.translate(nextPosition.x, nextPosition.y);

      // ==========================================
      // Manage leafs that exceed the boundary
      // of the allowed space.

      // Boundary in x-dimension
      if (position.x < bounds.x)
         position.x = bounds.x + bounds.width / 8;

      else if ((position.x + spaceOccupied.width) > (bounds.x + bounds.width))
         position.x = bounds.x - spaceOccupied.width;

      // Boundary in y-dimension
      if (position.y < bounds.y)
         position.y = bounds.y + bounds.height;

      else if ((position.y + spaceOccupied.height) > (bounds.y + bounds.height))
         position.y = bounds.y - spaceOccupied.height;

      // Set the new update position of the leaf.
      setSpaceOccupied(position);
   }
}
