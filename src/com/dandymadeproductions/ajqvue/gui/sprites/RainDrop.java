//=================================================================
//                         RainDrop
//=================================================================
//
//    This class provides the characteristics of a rain drop
// object that is used in the preferences frame, Preferences
// panel early spring.
// 
//                    << RainDrop.java >>
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
// Version 1.0 Production RainDrop Class.
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
 *    The RainDrop class provides the characteristics of a rain drop
 * flake object that is used in the preferences frame, Preferences
 * panel winter.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class RainDrop extends Sprite
{
   // Class Instances

   //==============================================================
   // RainDrop Object Constructor
   //==============================================================

   public RainDrop(Component component, Rectangle panelBounds, Image image,
                      Point position, Point nextPosition)
   {
      super(component, panelBounds, image, position, nextPosition);
   }

   //==============================================================
   // Class method to allow the updating of this raindrop's location
   // based on a wind vector, line gravitron, and boundaries.
   //==============================================================

   public void updatePosition()
   {
      // Class Method Instances.
      Point position, tempNextPosition;

      // =========================================
      // Generate random movement.

      if (random.nextInt() % 5 == 0)
      {
         Point randomOffset = new Point(random.nextInt() % 2, random.nextInt() % 6);
         nextPosition.x += randomOffset.x;
         if (nextPosition.x >= 1)
            nextPosition.x -= 1;
         if (nextPosition.x <= -1)
            nextPosition.x += 1;

         nextPosition.y += randomOffset.y;
         if (nextPosition.y >= 24)
            nextPosition.y -= 2;
         if (nextPosition.y <= 6)
            nextPosition.y += 8;
      }

      // ========================================
      // Modify nextPosition to tend toward
      // gravity well at position y=background
      // image bound height while maintaining
      // object is within width of bounds.

      position = new Point(spaceOccupied.x, spaceOccupied.y);
      tempNextPosition = new Point(nextPosition.x, nextPosition.y);

      if (random.nextInt() % 10 == 0)
      {
         // Modify x movement.
         if (position.x >= bounds.width && nextPosition.x > 0)
         {
            position.x = bounds.width / 3;
            tempNextPosition.x = -nextPosition.x;
         }
         else if (position.x < 0 && nextPosition.x < 0)
         {
            position.x = (2 / 3) * bounds.width;
            tempNextPosition.x = -nextPosition.x;
         }

         // Gravity y movement.
         if (position.y < 0 && nextPosition.y < 0)
         {
            tempNextPosition.y = -nextPosition.y;
         }
         else if (position.y > bounds.height)
         {
            tempNextPosition.y = -10;
         }
      }
      setNextPosition(tempNextPosition);

      // Move the raindrop on the screen
      position.translate(nextPosition.x, nextPosition.y);

      // ==========================================
      // Manage raindrops that exceed the boundary
      // of the allowed space.

      // Boundary in x-dimension
      if (position.x < bounds.x)
         position.x = bounds.x + bounds.width;

      else if ((position.x + spaceOccupied.width) > (bounds.x + bounds.width))
         position.x = bounds.x - spaceOccupied.width;

      // Boundary in y-dimension
      if (position.y < bounds.y)
         position.y = bounds.y + bounds.height;

      else if ((position.y + spaceOccupied.height) > (bounds.y + bounds.height))
         position.y = bounds.y - spaceOccupied.height;

      // Set the new update position of the
      // raindrop.
      setSpaceOccupied(position);
   }
}
