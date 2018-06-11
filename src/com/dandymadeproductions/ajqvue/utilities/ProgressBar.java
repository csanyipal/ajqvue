//=================================================================
//                         ProgressBar
//=================================================================
//
//    This class provides the frame and components to create a basic
// independent progress bar with a cancel button.
//
//                     << ProgressBar.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
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
// Version 1.0 Production ProgressBar Class.
//
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.dandymadeproductions.ajqvue.Ajqvue;

/**
 *    The ProgressBar class provides the frame and components to
 * create a basic independent progress bar with a cancel button.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class ProgressBar extends JFrame implements ActionListener
{
   // Class Instances
   private static final long serialVersionUID = -3850262903856481389L;

   private AResourceBundle resourceBundle;
   private JProgressBar progressBar;
   private JButton cancelButton;
   private int taskLength;
   private boolean taskCanceled;

   //==============================================================
   // ProgressBar Constructor.
   //==============================================================

   public ProgressBar(String progressTitle)
   {
      super(progressTitle);
      
      // Constructor Instances
      String iconsDirectory, resource;
      ImageIcon progressBarIcon;
      
      // Setup various instances to be used in the panel.
      
      resourceBundle = Ajqvue.getResourceBundle();
      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();
      progressBarIcon = resourceBundle.getResourceImage(iconsDirectory + "progressBarIcon.gif");
      
      // Create the components.
      this.getContentPane().setLayout(new BorderLayout());

      JPanel mainPanel = new JPanel();
      mainPanel.setBorder(BorderFactory.createEtchedBorder());

      resource = resourceBundle.getResourceString("ProgressBar.button.Cancel", "Cancel");
      cancelButton = new JButton(resource);
      cancelButton.setActionCommand("cancel");
      cancelButton.setFocusable(false);
      cancelButton.addActionListener(this);
      mainPanel.add(cancelButton);

      progressBar = new JProgressBar();
      progressBar.setIndeterminate(true);
      progressBar.setValue(0);
      progressBar.setStringPainted(true);
      mainPanel.add(progressBar);

      this.getContentPane().add(mainPanel, BorderLayout.CENTER);
      
      JLabel animationLabel = new JLabel(progressBarIcon);
      
      this.getContentPane().add(animationLabel, BorderLayout.SOUTH);

      taskCanceled = false;
   }

   //==============================================================
   // ActionEvent Listener method for detecting the inputs
   // from the panel and taking the appropriate action.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      taskCanceled = true;
   }

   //==============================================================
   // Class Method to set the taskCanceled instance.
   //==============================================================

   public void setCanceled(boolean value)
   {
      taskCanceled = value;
   }

   //==============================================================
   // Class Method to set the current status bar display value.
   //==============================================================

   public void setCurrentValue(int value)
   {
      progressBar.setValue(value);
   }

   //==============================================================
   // Class Method to set the maximum value of the status bar
   // that will be displayed.
   //==============================================================

   public void setTaskLength(int value)
   {
      progressBar.setIndeterminate(false);
      taskLength = value;
      progressBar.setMaximum(taskLength);
   }

   //==============================================================
   // Class Method to return the indication of the button cancel
   // being pressed.
   //==============================================================

   public boolean isCanceled()
   {
      return taskCanceled;
   }

   //==============================================================
   // Class method to center the frame.
   //==============================================================

   public void center()
   {
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension us = getSize();
      int x = (screen.width - us.width) / 2;
      int y = (screen.height - us.height) / 2;
      setLocation(x, y);
   }
}
