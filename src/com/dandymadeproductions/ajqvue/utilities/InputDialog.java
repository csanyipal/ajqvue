//=================================================================
//             Custom InputDialog Builder Class 
//=================================================================
//
//    This class is used to create a custom input dialog box.
//
// !Note - This class extends JDialog and is not disposed on window
//         closing, or on action buttons. Insure to dispose this
//         object when finished.
//
//                   << InputDialog.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
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
// Version 1.0 Production InputDialog Class.         
//-----------------------------------------------------------------
//                  danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *    The InputDialog class is used to create a custom input dialog
 * box.
 * <br>
 * <br>
 * <b>Note!</b> - This class extends JDialog and is not disposed on window
 *         closing, or on action buttons. Insure to dispose this
 *         object when finished.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class InputDialog extends JDialog implements PropertyChangeListener
{
   // Instance & Class Fields.

   private static final long serialVersionUID = 4525672919728444486L;
   
   private JOptionPane dialogOptionPane;
   private Object[] content;
   private String confirmationString;
   private String nonConfirmationString;
   private String actionResult = "";

   //==============================================================
   // InputDialog Constructor
   //==============================================================
   
   public InputDialog(JFrame frame, String title, String ok, String cancel,
                         Object[] content, ImageIcon icon)
   {
      super(frame, true);

      // Setting up the common and input components for
      // the custom dialog box. The ok & cancel arguments
      // represent the two buttons' text. Ok being the
      // positive action, cancel being the negative action.

      setTitle(title);

      // Confirmation String
      if (!ok.equals(""))
         confirmationString = ok;
      else
         confirmationString = "ok";
      // Non-Confirmation String
      if (!cancel.equals(""))
         nonConfirmationString = cancel;
      else
         nonConfirmationString = "cancel";

      this.content = content.clone();
      Object[] options = {confirmationString, nonConfirmationString};

      // Creating and showing.

      dialogOptionPane = new JOptionPane(content, JOptionPane.PLAIN_MESSAGE,
                                         JOptionPane.OK_CANCEL_OPTION, icon, options);

      setContentPane(dialogOptionPane);
      setOkDefaultButton(dialogOptionPane);
      dialogOptionPane.addPropertyChangeListener(this);
      this.addWindowListener(inputDialogFrameListener);
   }

   //==============================================================
   // WindowListener for insuring that the dialog disapears if
   // the window is closed. (x).
   //==============================================================

   private transient WindowListener inputDialogFrameListener = new WindowAdapter()
   {
      public void windowClosing(WindowEvent e)
      {
         actionResult = "close";
         closeDialog();
      }
   };

   //==============================================================
   // Detecting the action of the dialog's ok or cancel button.
   //==============================================================

   public void propertyChange(PropertyChangeEvent evt)
   {
      String property = evt.getPropertyName();

      // Checking to see if the change event was one
      // of the buttons.

      if (isVisible() && (evt.getSource() == dialogOptionPane)
          && (JOptionPane.VALUE_PROPERTY.equals(property)))
      {
         // Ok lets gather data and close.

         Object value = dialogOptionPane.getValue();

         if (value.equals(confirmationString))
         {
            actionResult = "ok";
            closeDialog();
         }
         if (value.equals(nonConfirmationString))
         {
            if (content != null)
            {
               for (int i = 0; i < content.length; i++)
                  if (content[i] instanceof JTextField)
                     ((JTextField) content[i]).setText("");
            }
            actionResult = "";
            closeDialog();
         }
      }
      dialogOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
   }

   //==============================================================
   // Class method for setting OK Button to defaultButton for the
   // enter action if possible.
   //==============================================================

   private void setOkDefaultButton(JComponent rootPane)
   {
      // Instances
      Component[] rootComponents = rootPane.getComponents();
      JButton currentButton;

      // Find the buttons in the pane then see if
      // we have found the right one to be set as
      // default action for enter.

      for (int i = 0; i < rootComponents.length; i++)
      {
         if (rootComponents[i] instanceof JButton)
         {
            currentButton = (JButton) rootComponents[i];
            if (currentButton.getText().equals(confirmationString))
            {
               getRootPane().setDefaultButton(currentButton);
            }
         }
         else if (rootComponents[i] instanceof JComponent)
         {
            setOkDefaultButton((JComponent) rootComponents[i]);
         }
      }
   }

   //==============================================================
   // Class method for setting the dialog to disappear and
   // returning control back to the issueing frame.
   //==============================================================

   private void closeDialog()
   {
      setVisible(false);
      return;
   }

   //==============================================================
   // Class method to return the last Action Performed result.
   // Either the dialog was closed, canceled, or oked.
   //==============================================================

   public boolean isActionResult()
   {
      if (actionResult.equals("ok"))
         return true;
      else
         return false;
   }

   //==============================================================
   // Class method to actionResult string.
   //==============================================================

   public String getActionResult()
   {
      return actionResult;
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
