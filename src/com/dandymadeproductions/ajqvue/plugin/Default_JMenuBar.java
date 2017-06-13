//=================================================================
//                Default_JMenuBar Class
//=================================================================
//
//    This class is used to construct a menubar that contains
// essentially only the menus File|Exit & Logo.
//
//               << Default_JMenuBar.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.1 06/13/2017
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
// Version 1.0 Production Default_JMenuBar Class.
//         1.1 Class javadoc Constructor Comment Correction.
//         
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.gui.Main_Frame;
import com.dandymadeproductions.ajqvue.gui.MenuActionCommands;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The Default_JMenuBar class is used to construct a menubar that
 * contains essentially only the menus File|Exit &amp; Logo.  
 * 
 * @author Dana M. Proctor
 * @version 1.1 06/13/2017
 */

public class Default_JMenuBar extends JMenuBar implements MenuActionCommands
{
   // Instance & Class Fields.
   private static final long serialVersionUID = -4199789357075711466L;
   private transient Main_Frame mainFrame;
   
   private AResourceBundle resourceBundle;

   //==============================================================
   // Default_JMenuBar Constructor.
   //==============================================================

   public Default_JMenuBar(Main_Frame parent)
   {
      mainFrame = parent;

      // Constructor Instances.
      String iconsDirectory;

      // Setting up a icons directory instance.
      resourceBundle = Ajqvue.getResourceBundle();
      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();

      // JMenu Bar for the tab.
      setBorder(BorderFactory.createEtchedBorder());

      // Creating your menu items here, see Main_JMenuBar.
      createFileMenu();
      
      add(Box.createHorizontalGlue());

      // Logo (Keep This)
      ImageIcon logoIcon = resourceBundle.getResourceImage(iconsDirectory + "ajqvueIcon.gif");
      JButton logoIconItem = new JButton(logoIcon);
      logoIconItem.setDisabledIcon(logoIcon);
      logoIconItem.setFocusPainted(false);
      logoIconItem.setBorder(BorderFactory.createLoweredBevelBorder());
      add(logoIconItem);
   }
   
   //==============================================================
   // Helper Method to create the File Menu.
   //==============================================================

   private void createFileMenu()
   {
      // Method Instances.
      JMenu fileMenu;
      String resource;
      
      //===========
      // File Menu
      
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.File", "File");
      fileMenu = new JMenu(resource);
      fileMenu.setFont(fileMenu.getFont().deriveFont(Font.BOLD));
      
      // Exit
      resource = resourceBundle.getResourceString("Main_JMenuBar.menu.Exit", "Exit");
      fileMenu.add(menuItem(resource, ACTION_EXIT));
      
      add(fileMenu);
   }
   
   //==============================================================
   // Instance method used for the creation of menu bar items.
   // Helper Method.
   // ==============================================================

   private JMenuItem menuItem(String label, String actionLabel)
   {
      JMenuItem item = new JMenuItem(label);
      item.addActionListener(mainFrame);
      item.setActionCommand(actionLabel);
      return item;
   }
}
