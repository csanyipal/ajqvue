//=================================================================
//                  AFocusTraversalPolicy
//=================================================================
//
//    This class provides a means for panels within the the
// application to set a desired component focus sequence for
// themselves.
//
//              << AFocusTraversalPolicy.java >>
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
// Version 1.0 Production FocusTraversalPolicy Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *    AFocusTraversalPolicy class provides a means for panels
 * within the application to set a desired component focus
 * sequence for themselves.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class AFocusTraversalPolicy extends FocusTraversalPolicy
{
   // Class Instances.
   private ArrayList<Component> componentSequence = new ArrayList <Component>();
   private int lastIndex = 0;

   //==============================================================
   // AFocusTraversalPolicy Constructor
   //==============================================================

   public AFocusTraversalPolicy(ArrayList<Component> components)
   {
      Iterator<Component> componentsIterator = components.iterator();

      while (componentsIterator.hasNext())
      {
         componentSequence.add(componentsIterator.next());
      }
   }

   //==============================================================
   // Class method to return the default component that should
   // be focused.
   //==============================================================

   public Component getDefaultComponent(Container focusCycleRoot)
   {
      return (Component) componentSequence.get(0);
   }

   //==============================================================
   // Class method to return the first component that should
   // be focused.
   //==============================================================

   public Component getFirstComponent(Container focusCycleRoot)
   {
      return (Component) componentSequence.get(0);
   }

   //==============================================================
   // Class method to return the last component that should
   // be focused.
   //==============================================================

   public Component getLastComponent(Container focusCycleRoot)
   {
      return (Component) componentSequence.get(componentSequence.size() - 1);
   }

   //==============================================================
   // Class method to return the next component given the current
   // focused component.
   //==============================================================

   public Component getComponentAfter(Container focusCycleRoot, Component aComponent)
   {
      int currentLocation = componentSequence.indexOf(aComponent);
      int nextIndex;
      // System.out.println(currentLocation +" "+ lastIndex);

      if (currentLocation != -1)
      {
         if (currentLocation >= componentSequence.size() - 1)
         {
            lastIndex = 0;
            return (Component) componentSequence.get(0);
         }
         else
         {
            lastIndex = currentLocation;
            return (Component) componentSequence.get(currentLocation + 1);
         }
      }
      else
      {
         if (lastIndex == 0)
            nextIndex = lastIndex + 1;
         else
            nextIndex = lastIndex + 2;
         // System.out.println("Fault setting index to " + nextIndex);
         lastIndex = lastIndex + 1;
         return (Component) componentSequence.get(nextIndex);
      }
   }

   //==============================================================
   // Class method to return the previous component given the
   // current focused component.
   //==============================================================

   public Component getComponentBefore(Container focusCycleRoot, Component aComponent)
   {
      int currentLocation = componentSequence.indexOf(aComponent);

      if (currentLocation <= 0)
         return (Component) componentSequence.get(componentSequence.size() - 1);
      else
         return (Component) componentSequence.get(currentLocation - 1);
   }
}
