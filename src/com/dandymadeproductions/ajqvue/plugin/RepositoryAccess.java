//=================================================================
//                     RepositoryAccess Class
//=================================================================
//
//    This class is to provide and option pane to collect information
// that might be needed for accessing a plugin repository.
//
//                   << RepositoryAccess.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.0 02/07/2017
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
// Version 1.0 02/07/2017 Original Ajqvue RepositoryAccess Class.
//                           
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.InputDialog;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;

/**
*    The RepositoryAccess class is to provide and option pane to collect
*  information that might be needed for accessing a plugin repository.
* 
* @author Dana M. Proctor
* @version 1.0 02/07/2017
*/

public class RepositoryAccess
{
   // Class Instances
   private InputDialog accessDialog;
   private JTextField userTextField;
   private JPasswordField passwordField;
   
   private String user;
   private char[] password;
   private int loginAttempts;
   
   private String resourceTitle, resourceOK, resourceCancel;
   private String resourceUser, resourcePassword;
   
   private AResourceBundle resourceBundle;
   
   private final static int LOGIN_RETRYS = 3;
   
   //==============================================================
   // RepositoryAccess Constructor
   //==============================================================
   
   protected RepositoryAccess(boolean allowRetries)
   {
      // Setup resources.
      resourceBundle = Ajqvue.getResourceBundle();
      
      resourceTitle = resourceBundle.getResourceString("RepositoryAccess.dialogtitle.Login", "Login");
      resourceUser = resourceBundle.getResourceString("RepositoryAccess.label.User", "User");
      resourcePassword = resourceBundle.getResourceString("RepositoryAccess.label.Password", "Password");
      resourceOK = resourceBundle.getResourceString("RepositoryAccess.dialogbutton.OK", "OK");
      resourceCancel = resourceBundle.getResourceString("RepositoryAccess.dialogbutton.Cancel", "Cancel");
      
      // Set login attemps.
      if (allowRetries)
         loginAttempts = 0;
      else
         loginAttempts = LOGIN_RETRYS - 1;
   }
   
   //==============================================================
   // Method used to present the user with a prompt to collect the
   // user and password parameters used in the connection.
   //==============================================================
   
   protected boolean promptUserPassword()
   {
      // Method Instances
      JPanel accessPanel;
      JLabel userLabel;
      JLabel passwordLabel;
      
      boolean isOK;
      
      // Setup 
      isOK = false;
      
      if (loginAttempts >= LOGIN_RETRYS)
         return isOK;
      
      // Setup dialog.
      if (accessDialog == null)
      {
         accessPanel = new JPanel();
         accessPanel.setLayout(new GridLayout(4, 1, 100, 0));
         accessPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(4, 4, 12, 4)));
         
         // User
         userLabel = new JLabel(resourceUser, JLabel.LEFT); 
         accessPanel.add(userLabel);
         
         userTextField = new JTextField(20);
         userTextField.setBorder(BorderFactory.createLoweredBevelBorder());
         accessPanel.add(userTextField);

         // Password
         passwordLabel = new JLabel(resourcePassword, JLabel.LEFT); 
         accessPanel.add(passwordLabel);
         
         passwordField = new JPasswordField(20);
         passwordField.setBorder(BorderFactory.createLoweredBevelBorder());
         accessPanel.add(passwordField);
         
         Object content[] = {accessPanel};

         accessDialog = new InputDialog(null, resourceTitle, resourceOK, resourceCancel, content, null);
      }
      
      accessDialog.pack();
      accessDialog.center();
      accessDialog.setVisible(true);

      // Collect the new user and password on OK.

      if (accessDialog.isActionResult())
      {
         user = userTextField.getText();
         password = passwordField.getPassword();
         
         if (user.isEmpty() || password.length == 0)
            isOK = false;
         else
            isOK = true;
         
         loginAttempts++;
      }
      
      // Clean up and return;
      if (loginAttempts >= LOGIN_RETRYS)
         accessDialog.dispose();
      return isOK;
   }
   
   //==============================================================
   // Methods to determine if LOGIN_RETRYS has been achieved.
   //==============================================================
   
   protected boolean loginAttemptsExceeded()
   {
      if (loginAttempts >= LOGIN_RETRYS)
         return true;
      else
         return false;
   }
   
   //==============================================================
   // User/Password getter methods.
   //==============================================================
   
   protected String getUser()
   {
      return user;
   }
   
   protected String getPassword()
   {
      StringBuffer tempBuffer = new StringBuffer();
      
      for (int i = 0; i < password.length; i++)
      {
         tempBuffer.append(password[i]);
         password[i] = '0';
      }
      
      return tempBuffer.toString();
   }
}
