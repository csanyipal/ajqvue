//=================================================================
//                     Main_MouseAdapter
//=================================================================
//
//    This class provides an extension of the Mouse Adapter so
// that specfic mouse listener interfaces can be implemented for
// various panels in the application. Mouse events relate to a
// right mouse press/release popup menu are executed here.
//
//                << Main_MouseAdapter.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.0 09/20/2016
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
// Version 1.0 Production Main_MouseAdapter Class.
//         
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 *    The Main_MouseAdapter class provides an extension of the Mouse
 * Adapter so that specfic mouse listener interfaces can be implemented
 * for various panels in the application. Mouse events for relate to
 * a right mouse press/release popup menu are executed here.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/20/2016
 */

public class Main_MouseAdapter extends MouseAdapter
{
   // Instances
   JPopupMenu popup;

   //==============================================================
   // Main_MouseAdapter Constructor
   //==============================================================

   public Main_MouseAdapter(JPopupMenu popUp)
   {
      this.popup = popUp;
   }

   //==============================================================
   // MouseEvent Listener methods for detecting the user
   // right clicking the mouse within the various panels.
   //==============================================================

   public void mousePressed(MouseEvent evt)
   {
      showPopUp(evt);
   }

   public void mouseReleased(MouseEvent evt)
   {
      showPopUp(evt);
   }

   //==============================================================
   // Class method to show the IDMS's popup menu.
   //==============================================================

   public void showPopUp(MouseEvent e)
   {
      if (e.isPopupTrigger())
         popup.show(e.getComponent(), e.getX(), e.getY());
   }
}
