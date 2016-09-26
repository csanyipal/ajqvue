//=================================================================
//                        Ajqvue Class
//=================================================================
//
//    This class is used to control the running of the application.
// Initial action is to bring up a login access frame, LoginFrame,
// to connect to a desired database. Once a valid connection has
// been made then the main framework for the application will be
// created through the Main_Frame class to setup the main user
// interface.
//
//             Arguments -debug, -lang='locale'
//
//                  << Ajqvue.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.10 09/25/2016
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
// Version 1.0  09/18/2016 Ajqvue Main Application.
//         1.1  09/20/2016 Constructor Change in Description for popupMenuResources.
//                         Updated VERSION.
//         1.10 09/25/2016 Ajqvue Release v1.10. Updated VERSION.
//                              
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultEditorKit;

import com.dandymadeproductions.ajqvue.gui.LoginFrame;
import com.dandymadeproductions.ajqvue.gui.Main_Frame;
import com.dandymadeproductions.ajqvue.gui.Main_MouseAdapter;
import com.dandymadeproductions.ajqvue.structures.GeneralProperties;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

//=================================================================
//                      Ajqvue Application
//=================================================================

/**
 *    The Ajqvue class is used to control the running of the application.
 * Initial action is to bring up a login access frame, LoginFrame, to
 * connect to a desired database. Once a valid connection has been made
 * then the main framework for the application will be created through
 * the Main_Frame class to setup the main user interface.
 * 
 * Arguments -debug, -lang='locale'
 * 
 * @author Dana M. Proctor
 * @version 1.10 09/25/2016
 */

public class Ajqvue implements ActionListener
{
   // =============================================
   // Creation of the necessary class instance
   // variables for the Ajqvue user interface.
   // =============================================

   // Ajqvue Main Frame and Panels.
   private static boolean debug;
   private static String localeString;
   private JButton validLoginButton;
   
   private LoginFrame loginFrame;
   protected Main_Frame mainFrame;
   private static MouseListener popupListener;
   private static AResourceBundle resourceBundle;

   // String for Information About the Ajqvue.
   private static String[] VERSION = {"Ajqvue", "1.10", "Build ID: 20160925"};
   private static final String WEBSITE = "http://ajqvue.com";
   
   private static GeneralProperties generalProperties = new GeneralProperties();

   //==============================================================
   // Ajqvue Constructor
   //==============================================================

   public Ajqvue()
   {
      // Constructor Instances.
      JPopupMenu popupMenu;
      JMenuItem menuItem;
      String popupMenuResource;
      StringBuilder uiManagerError;

      // ==================================================
      // Setting up the look and feel of the frame
      // by accessing the user's system configuration.

      uiManagerError = new StringBuilder();
      
      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         Utils.setUIManagerFont(generalProperties.getFontSize());
         // System.out.println("UIManager Look & Feel Set");
      }
      catch (ClassNotFoundException e){uiManagerError.append(e.toString());}
      catch (InstantiationException e){uiManagerError.append(e.toString());}
      catch (IllegalAccessException e){System.err.println("Can't set look and feel.");}
      catch (UnsupportedLookAndFeelException e){System.err.println("Can't set look and feel.");}
      
      if (!uiManagerError.toString().isEmpty())
         System.err.println("Ajqvue Constructor: " + uiManagerError);
      
      // ==================================================
      // Obtain resouce bundle for internationalization,
      // loading images, & various resources.
      
      resourceBundle = new AResourceBundle("file:" + Utils.getAjqvueDirectory() + Utils.getFileSeparator(),
                                           debug);
      resourceBundle.setLocaleResource("locale" + Utils.getFileSeparator(), "AjqvueBundle", localeString);
      
      // ==================================================
      // Setting up a PopupMenu for cut, copy, and pasting.

      popupMenu = new JPopupMenu();

      menuItem = new JMenuItem(new DefaultEditorKit.CutAction()); 
      
      popupMenuResource = resourceBundle.getResourceString("Ajqvue.popup.Cut", "Cut");
      menuItem.setText(popupMenuResource);
      menuItem.setMnemonic(KeyEvent.VK_X);
      menuItem.addActionListener(this);
      popupMenu.add(menuItem);

      menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
      
      popupMenuResource = resourceBundle.getResourceString("Ajqvue.popup.Copy", "Copy");
      menuItem.setText(popupMenuResource);
      menuItem.setMnemonic(KeyEvent.VK_C);
      menuItem.addActionListener(this);
      popupMenu.add(menuItem);

      menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
      
      popupMenuResource = resourceBundle.getResourceString("Ajqvue.popup.Paste", "Paste");
      menuItem.setText(popupMenuResource);
      menuItem.setText("Paste");
      menuItem.setMnemonic(KeyEvent.VK_V);
      menuItem.addActionListener(this);
      popupMenu.add(menuItem);

      popupListener = new Main_MouseAdapter(popupMenu);

      // ==================================================
      // Setting up a component that will allow the
      // determination of a valid login to the database.

      validLoginButton = new JButton();
      validLoginButton.addActionListener(this);

      // ==================================================
      // Show the database login access frame.
      
      loginFrame = new LoginFrame(validLoginButton);
      loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      loginFrame.addMouseListener(popupListener);
      loginFrame.pack();
      loginFrame.setResizable(true);
      loginFrame.center();
      loginFrame.setVisible(true);
   }

   //==============================================================
   // ActionEvent Listener method for detecting the user's valid
   // login to a database.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object panelSource = evt.getSource();

      // Button Actions
      if (panelSource instanceof JButton)
      {
         if (panelSource == validLoginButton)
         {
            // Dispose the login access frame.

            loginFrame.dispose();

            // Create the Ajqvue main application frame.

            mainFrame = new Main_Frame(VERSION, WEBSITE);
            mainFrame.loadQueryBucketList();
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.createGUI();
            mainFrame.addMouseListener(popupListener);
            mainFrame.setLocation(generalProperties.getPosition());
            mainFrame.setSize(generalProperties.getDimension());
            mainFrame.setVisible(true);
         }
      }
   }

   //============================================================
   // Main public access point method for instantiating the
   // application. Valid Arguments: -debug, -lang.
   //
   // Example Startup: java -jar Ajqvue.jar -debug -lang=en_US
   //==============================================================

   public static void main(String[] args)
   {
      debug = false;
      localeString = "";
      
      // Collect allowed arguments.
      if (args != null)
      {
         for (int i = 0; i < args.length; i++)
         {
            if (args[i].equals("-debug"))
               debug = true;
            if (args[i].indexOf("-lang") != -1)
            {
               if (args[i].indexOf("=") != -1)
                  localeString = args[i].substring(args[i].indexOf("=") + 1);
            }
         }
      }

      // Create the Login and Access.
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         public void run()
         {
            // Try to setup/retrieve language support
            // if not provided as an argument.
            
            if (localeString.equals("") || localeString.length() != 5)
               localeString = Utils.processLocaleLanguage();
            
            // Getter On!
            
            new Ajqvue();
         }
      });
   }
   
   //==============================================================
   // Class Method to return the debug argument.
   //==============================================================

   public static boolean getDebug()
   {
      return debug;
   }
   
   //==============================================================
   // Class Method to return the current GeneralProperties.
   //==============================================================

   public static GeneralProperties getGeneralProperties()
   {
      return generalProperties;
   }
   
   //==============================================================
   // Class Methods to return the locale, language, string selection.
   //==============================================================

   public static String getLocaleString()
   {
      return localeString;
   }
   
   public static Locale getLocale()
   {
      return new Locale(localeString.substring(0, localeString.indexOf("_")),
                        localeString.substring(localeString.indexOf("_") + 1));
   }
   
   //==============================================================
   // Class Method to return to temporary panels a JPopupMenu for
   // cutting, coping, and pasteing.
   //==============================================================

   public static MouseListener getPopupMenuListener()
   {
      return popupListener;
   }
   
   //==============================================================
   // Class Method to return the resource bundle requred for program
   // internationlization.
   //==============================================================

   public static AResourceBundle getResourceBundle()
   {
      return resourceBundle;
   }
   
   //==============================================================
   // Class Method to return to the version.
   //==============================================================

   public static String[] getVersion()
   {
      String[] versionCopy = new String[VERSION.length];
      
      for (int i = 0; i < VERSION.length; i++)
         versionCopy[i] = VERSION[i];
      
      return versionCopy;
   }
   
   //==============================================================
   // Class Method to return to the version.
   //==============================================================

   public static String getWebSite()
   {
      return WEBSITE;
   }
   
   //==============================================================
   // Class Method to set the GeneralProperties.
   //==============================================================

   public static void setGeneralProperties(GeneralProperties newGeneralProperties)
   {
      generalProperties = newGeneralProperties;
   }
}
