//=================================================================
//                     Help Frame
//=================================================================
//     The HelpFrame is used to open a new JFrame window that
// displays help information in the application. The format
// displayed is of type html directly read from a file.
//
//                << HelpFrame.java >>
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
// Verison 1.0 Production HelpFrame Class.
//         
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.dandymadeproductions.ajqvue.utilities.Utils;

//==================================================================
//                          Help Frame
//==================================================================

/**
 * The HelpFrame class is used to display html help information.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/19/2016
 */

public class HelpFrame extends JFrame
{
   // Instance variables as needed.
   private static final long serialVersionUID = -4143035957786665080L;

   private JEditorPane helpHTMLPane;
   private JScrollPane helpHTMLScrollPane;
   private JButton closeButton;
   protected boolean failedToLoadContents;

   //==============================================================
   // HelpFrame Constructor.
   //==============================================================

   public HelpFrame(String frameTitle, String htmlFile, JButton closeButton)
   {
      // Setting the Frame Title & Image.
      super(frameTitle);
      setIconImage(Utils.getFrameIcon());

      this.closeButton = closeButton;
      failedToLoadContents = false;

      // Setting up a scrollable html type editor pane.

      helpHTMLPane = new JEditorPane();
      helpHTMLPane.setEditable(false);

      // Obtaining the html document and adding to the
      // editor pane.

      URL helpURL = HelpFrame.class.getResource(htmlFile);
      // System.out.println(helpURL);

      if (helpURL != null)
      {
         try
         {
            helpHTMLPane.setPage(helpURL);
         }
         catch (IOException ioe)
         {
            String optionPaneStringErrors = "Unable to to SetPage - " + helpURL + "\n" + "IOException: "
                                             + ioe.toString();
            JOptionPane.showMessageDialog(null, optionPaneStringErrors, "Alert", JOptionPane.ERROR_MESSAGE);
            failedToLoadContents = true;
         }
      }
      else
      {
         String optionPaneStringErrors = "Unable to read URL - " + htmlFile;
         JOptionPane.showMessageDialog(null, optionPaneStringErrors, "Alert", JOptionPane.ERROR_MESSAGE);
         failedToLoadContents = true;
      }

      // Adding HyperLink Listener

      helpHTMLPane.addHyperlinkListener(new HyperlinkListener()
      {
         public void hyperlinkUpdate(HyperlinkEvent ev)
         {
            try
            {
               if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
               {
                  helpHTMLPane.setPage(ev.getURL());
               }
            }
            catch (IOException ioe)
            {
               String optionPaneStringErrors = "Unable to Load HyperLink";
               JOptionPane.showMessageDialog(null, optionPaneStringErrors, "Alert", JOptionPane.ERROR_MESSAGE);
            }
         }
      });

      // Adding scrollbars to the editor pane and placing
      // in the frame.

      if (!failedToLoadContents)
      {
         helpHTMLScrollPane = new JScrollPane(helpHTMLPane);
         helpHTMLScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
         helpHTMLScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

         getContentPane().add(helpHTMLScrollPane, "Center");
      }
      
      this.addWindowListener(helpFrameListener);
   }

   //==============================================================
   // WindowListener for insuring that the dialog disapears if
   // the window is closed. (x).
   //==============================================================

   private transient WindowListener helpFrameListener = new WindowAdapter()
   {
      public void windowClosing(WindowEvent e)
      {
         if (closeButton != null)
            closeButton.doClick();
         dispose();
      }
   };

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