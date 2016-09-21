//=================================================================
//                         About Frame 
//=================================================================
//
//    This class provides the user access to information about
// the application when the Help About selection is made in the
// menu bar.
//
//                   << AboutFrame.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.0 09/19/2016
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
// Version 1.0 Production AboutFrame Class.
//         
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.gui.panels.CreditsPanel;
import com.dandymadeproductions.ajqvue.gui.panels.GraphicsCanvasPanel;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The AboutFrame class provides the user access to information about the
 * application when the Help About selection is made in the menu bar.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/19/2016
 */

class AboutFrame extends JFrame implements ActionListener
{
   // Instance & Class Fields.
   private static final long serialVersionUID = -5650626289707582042L;
   
   private CreditsPanel creditsPanel;
   private JButton licenseButton, readmeButton, closeButton;

   //==============================================================
   // AboutFrame Constructor
   //==============================================================

   protected AboutFrame(String[] version, String webSiteString, ImageIcon logoIcon)
   {
      // Constructor Instances.
      AResourceBundle resourceBundle;
      String resource;
      
      JPanel mainPanel;
      JPanel centerPanel, southButtonPanel;
      GraphicsCanvasPanel logoPanel;

      // Setting up the frame.
      resourceBundle = Ajqvue.getResourceBundle();
      resource = resourceBundle.getResourceString("AboutFrame.message.Title", "About");
      setTitle(resource + " Ajqvue");
      setIconImage(Utils.getFrameIcon());

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();

      mainPanel = new JPanel(new BorderLayout());

      // Center panel and components
      centerPanel = new JPanel(gridbag);
      centerPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

      // Icon
      logoPanel = new GraphicsCanvasPanel(logoIcon.getImage());
      logoPanel.setBorder(BorderFactory.createLoweredBevelBorder());

      Utils.buildConstraints(constraints, 0, 0, 1, 1, 30, 100);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(logoPanel, constraints);
      centerPanel.add(logoPanel);

      // Information/Credits
      creditsPanel = new CreditsPanel(version, webSiteString);
      Thread creditsPanelThread = new Thread(creditsPanel, "Credits");
      creditsPanelThread.start();

      Utils.buildConstraints(constraints, 1, 0, 1, 1, 70, 100);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(creditsPanel, constraints);
      centerPanel.add(creditsPanel);

      mainPanel.add(centerPanel, BorderLayout.CENTER);

      // South panel license, readme, & close button
      southButtonPanel = new JPanel();
      southButtonPanel.setBorder(BorderFactory.createEtchedBorder());
      
      resource = resourceBundle.getResourceString("AboutFrame.button.ReadMe", "Read Me");
      readmeButton = new JButton(resource);
      readmeButton.setFocusPainted(false);
      readmeButton.addActionListener(this);
      southButtonPanel.add(readmeButton);
      
      resource = resourceBundle.getResourceString("AboutFrame.button.License", "License");
      licenseButton = new JButton(resource);
      licenseButton.setFocusPainted(false);
      licenseButton.addActionListener(this);
      southButtonPanel.add(licenseButton);
      
      resource = resourceBundle.getResourceString("AboutFrame.button.Close", "Close");
      closeButton = new JButton(resource);
      closeButton.setFocusPainted(false);
      closeButton.addActionListener(this);
      southButtonPanel.add(closeButton);
      
      mainPanel.add(southButtonPanel, BorderLayout.SOUTH);

      getContentPane().add(mainPanel);
      this.addWindowListener(aboutFrameListener);
   }

   //==============================================================
   // WindowListener for insuring that the dialog disapears if
   // the window is closed. (x).
   //==============================================================

   private transient WindowListener aboutFrameListener = new WindowAdapter()
   {
      public void windowClosing(WindowEvent e)
      {
         creditsPanel.suspendPanel(true);
         dispose();
      }
   };

   //==============================================================
   // ActionEvent Listener method for determining when the selections
   // have been made so an update can be performed or an appropriate
   // action taken.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object frameSource = evt.getSource();

      // Overall action buttons.
      if (frameSource instanceof JButton)
      {
         // ReadMe Button Action.
         if (frameSource == readmeButton)
         {
            
            Thread helpFrameThread = new Thread(new HelpFrameThread("Ajqvue Readme",
                                                                    "/docs/Release/readme.html"),
                                                "HelpFrameThread Readme");
            helpFrameThread.start();
            return;
         }
         
         // License Button Action.
         if (frameSource == licenseButton)
         {
            Thread helpFrameThread = new Thread(new HelpFrameThread("Ajqvue Legal",
                                                                    "/docs/Release/legal.html"),
                                                "HelpFrameThread Legal");
            helpFrameThread.start();
            return;
         }
         
         // OK Button Action.
         if (frameSource == closeButton)
         {
            creditsPanel.suspendPanel(true);
            dispose();
         }
      }
   }
   
   //==============================================================
   // Inner class to help facilitate the displaying of the Readme
   // & License documentation in the background.
   //==============================================================
   
   protected static class HelpFrameThread implements Runnable
   {
      String title;
      String document;
      
      protected HelpFrameThread(String title, String document)
      {
         this.title = title;
         this.document = document;
      }
      
      @Override
      public void run()
      {
         HelpFrame helpFrame = new HelpFrame(title, document, null);
         
         if (helpFrame.failedToLoadContents)
            helpFrame.dispose();
         else
         {
            helpFrame.setSize(Ajqvue.getGeneralProperties().getDimension());
            helpFrame.setLocation(Ajqvue.getGeneralProperties().getPosition());
            helpFrame.setVisible(true);
         }  
      } 
   }

   //==============================================================
   // Class method to center the frame.
   //==============================================================

   protected void center()
   {
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension us = getSize();
      int x = (screen.width - us.width) / 2;
      int y = (screen.height - us.height) / 2;
      setLocation(x, y);
   }
}