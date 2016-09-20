//=================================================================
//                    DataSourceDialog Class 
//=================================================================
//
//   This class provides a dialog to access the Ajqvue defined
// data source connections as defined by the LoginManager.
//
// !Note - This class extends JDialog and is not disposed on window
//         closing, or on action buttons. Insure to dispose this
//         object when finished.
//
//                 << DataSourceDialog.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.2 09/19/2016
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
// Version 1.0 09/17/2016 Original Production DataSourceDialog Class.
//         1.1 09/18/2016 Corrected Class Instance resourceBundle Class
//                        Reference. Corrected Reference to Utils Class.
//         1.2 09/19/2016 Correction Collection of resourceBundle in
//                        Constructor.
//
//-----------------------------------------------------------------
//                  danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.ConnectionProperties;
import com.dandymadeproductions.ajqvue.gui.SiteParameters;
import com.dandymadeproductions.ajqvue.gui.XMLTranslator;

/**
 *    The DataSourceDialog class provides a dialog to access
 * the Ajqvue defined data source connections as defined by the
 * LoginManager.
 * 
 * !Note - This class extends JDialog and is not disposed on window
 *         closing, or on action buttons. Insure to dispose this
 *         object when finished.
 * 
 * @author Dana M. Proctor
 * @version 1.2 09/19/2016
 */

public class DataSourceDialog extends JDialog implements ActionListener, PropertyChangeListener
{
   // Class Instances
   private static final long serialVersionUID = -2547873067331646882L;

   private ConnectionProperties connectionProperties;
   private transient XMLTranslator xmlTranslator;
   private transient HashMap<String, SiteParameters> sites;
   private SiteParameters selectedSite;

   private JTextField hostTextField;
   private JTextField dbTextField;
   private JTextField userTextField;
   private JPasswordField passwordTextField;
   private JCheckBox validateCheckBox;

   private JOptionPane dialogOptionPane;
   private String confirmationString;
   private String nonConfirmationString;
   private String actionResult;
   
   private AResourceBundle resourceBundle;
   
   private boolean siteSelected;
   private String resourceSuccess;
   private ImageIcon successIcon;

   //==============================================================
   // DataSourceDialog Constructor
   //==============================================================

   public DataSourceDialog(JFrame frame, ConnectionProperties connectionProperties,
                                      boolean enableValidate)
   {
      super(frame, true);
      this.connectionProperties = connectionProperties;

      // Constructor Instances
      JMenuBar menuBar;
      JMenu sourceSelectMenu;

      JPanel mainPanel, parametersPanel, validatePanel;
      JLabel hostLabel, dbLabel, userLabel, passwordLabel;
      String resource;

      // Setting up
      xmlTranslator = new XMLTranslator();
      resourceBundle = Ajqvue.getResourceBundle();
      actionResult = "";
      siteSelected = false;
      
      resource = resourceBundle.getResourceString(
         "DataSourcesDialog.message.DataSourcesDialog", "Data Sources Dialog");
      setTitle("A DataSources Dialog");
      
      resourceSuccess = resourceBundle.getResourceString(
         "DataSourceDialog.message.Success", "Success");
      successIcon = resourceBundle.getResourceImage(
         Utils.getIconsDirectory() + Utils.getFileSeparator() + "successIcon.png");

      // Create a MenuBar to provide the ability to select
      // a datasource.

      menuBar = new JMenuBar();
      menuBar.setBorder(BorderFactory.createEtchedBorder());
      menuBar.setMargin(new Insets(0, 0, 0, 0));

      if (xmlTranslator.getXMLTranslatorResult())
      {
         // Site Connections
         sites = xmlTranslator.getSites();
      }

      resource = resourceBundle.getResourceString("DataSourcesDialog.menu.Sites", "Sites");
      sourceSelectMenu = new JMenu(resource);

      resource = resourceBundle.getResourceString(
         "DataSourcesDialog.tooltip.SiteSelection", "Site Selection");
      sourceSelectMenu.setToolTipText(resource);

      if (sites != null)
         fillSiteDataStructures(sourceSelectMenu);

      menuBar.add(sourceSelectMenu);
      setJMenuBar(menuBar);

      // Place content, datasource selected, display panel.
      
      mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createEtchedBorder());

      parametersPanel = new JPanel();
      parametersPanel.setLayout(new GridLayout(8, 1, 0, 2));
      parametersPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(6, 8, 6, 8)));

      // Host
      resource = resourceBundle.getResourceString("DataSourcesDialog.label.Host", "Host");
      hostLabel = new JLabel(resource, JLabel.LEFT);
      parametersPanel.add(hostLabel);

      hostTextField = new JTextField();
      hostTextField.setBorder(BorderFactory.createEtchedBorder());
      hostTextField.setEditable(false);
      parametersPanel.add(hostTextField);

      // Database
      resource = resourceBundle.getResourceString(
         "DataSourcesDialog.label.Database", "Database");
      dbLabel = new JLabel(resource);
      parametersPanel.add(dbLabel);

      dbTextField = new JTextField();
      dbTextField.setBorder(BorderFactory.createEtchedBorder());
      dbTextField.setEditable(false);
      parametersPanel.add(dbTextField);

      // User
      resource = resourceBundle.getResourceString("DataSourcesDialog.label.User", "User");
      userLabel = new JLabel(resource, JLabel.LEFT);
      parametersPanel.add(userLabel);

      userTextField = new JTextField();
      userTextField.setBorder(BorderFactory.createEtchedBorder());
      userTextField.setEditable(false);
      parametersPanel.add(userTextField);

      // Password
      resource = resourceBundle.getResourceString(
         "DataSourcesDialog.label.Password", "Password");
      passwordLabel = new JLabel(resource, JLabel.LEFT);
      parametersPanel.add(passwordLabel);

      passwordTextField = new JPasswordField();
      passwordTextField.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEtchedBorder(EtchedBorder.RAISED), BorderFactory.createLoweredBevelBorder()));
      parametersPanel.add(passwordTextField);
      
      mainPanel.add(parametersPanel, BorderLayout.CENTER);
      
      // Validate option.
      
      validatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      validatePanel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
      
      resource = resourceBundle.getResourceString(
         "DataSourcesDialog.checkbox.Validate", "Validate");
      validateCheckBox = new JCheckBox(resource, false);
      validateCheckBox.setEnabled(enableValidate);
      validatePanel.add(validateCheckBox);
      
      mainPanel.add(validatePanel, BorderLayout.SOUTH);

      Object[] content = {mainPanel};

      // Create the option, buttons for confirmation
      // of selection or cancel.

      resource = resourceBundle.getResourceString("DataSourcesDialog.dialogbutton.OK", "OK");
      confirmationString = resource;

      resource = resourceBundle.getResourceString("DataSourcesDialog.dialogbutton.Cancel", "Cancel");
      nonConfirmationString = resource;

      Object[] options = {confirmationString, nonConfirmationString};

      // Creating and showing dialog.

      dialogOptionPane = new JOptionPane(content, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
                                         null, options);
      dialogOptionPane.setBorder(BorderFactory.createRaisedBevelBorder());
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
   // ActionEvent Listener method for detecting the user's selection
   // of the frame's various JButtons and JMenu then directing to
   // the appropriate action..
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object panelSource = evt.getSource();

      // JMenu Actions
      if (panelSource instanceof JMenuItem)
      {
         JMenuItem selectedMenuItem = (JMenuItem) panelSource;
         String actionCommandName = selectedMenuItem.getActionCommand();
         // System.out.println(actionCommandName);
         
         selectedSite = sites.get(actionCommandName);
         setSelectedSite();
         siteSelected = true;
      }
   }

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
            actionResult = "";
            closeDialog();
         }
      }
      dialogOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
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
   // Class method used for the filling the data structures
   // that hold the site menu items in the menu bar and list
   // parameters in the combobox fields.
   //==============================================================

   private void fillSiteDataStructures(JMenu siteSelectMenu)
   {
      // Class Method Instances
      Iterator<Entry<String, SiteParameters>> siteEntriesIterator;
      Iterator<String> sitesTreeIterator;

      TreeSet<String> sitesTreeSet;
      HashMap<String, JMenu> sitesJMenus;

      Entry<String, SiteParameters> siteEntry;
      String siteName;

      // Remove all previous sites.
      siteSelectMenu.removeAll();

      // Create a natural order of JMenus
      // of the given sites' names.

      siteEntriesIterator = sites.entrySet().iterator();
      sitesTreeSet = new TreeSet<String>();

      while (siteEntriesIterator.hasNext())
      {
         siteName = (siteEntriesIterator.next()).getKey();

         if (!siteName.equals("Last Site") && siteName.indexOf('#') != -1)
            siteName = siteName.substring(0, siteName.indexOf('#'));
         else
            siteSelectMenu.add(createMenuItem(siteName, siteName));

         if (!sitesTreeSet.contains(siteName) && !siteName.equals("Last Site"))
            sitesTreeSet.add(siteName);
      }

      sitesTreeIterator = sitesTreeSet.iterator();
      sitesJMenus = new HashMap<String, JMenu>();

      while (sitesTreeIterator.hasNext())
      {
         String currentSiteName = sitesTreeIterator.next();
         JMenu currentSiteJMenu = new JMenu(currentSiteName);
         sitesJMenus.put(currentSiteName, currentSiteJMenu);
         siteSelectMenu.add(currentSiteJMenu);
      }

      // Now that the data has been organized,
      // add databases to the site JMenus.

      siteEntriesIterator = sites.entrySet().iterator();

      while (siteEntriesIterator.hasNext())
      {
         String jmenuSiteName;
         String jmenuDBName;

         siteEntry = siteEntriesIterator.next();
         siteName = siteEntry.getKey();

         if (!siteName.equals("Last Site") && siteName.indexOf('#') != -1)
         {
            jmenuSiteName = siteName.substring(0, siteName.indexOf('#'));
            jmenuDBName = siteName.substring(siteName.indexOf('#') + 1);

            JMenu currentSiteJMenu = sitesJMenus.get(jmenuSiteName);
            currentSiteJMenu.add(createMenuItem(jmenuDBName, siteName));
         }
      }
   }

   //==============================================================
   // Class method used for the helping of creating menu items in
   // the menu bar items. Helper Method.
   //==============================================================

   private JMenuItem createMenuItem(String label, String actionLabel)
   {
      JMenuItem item = new JMenuItem(label);
      item.addActionListener(this);
      item.setActionCommand(actionLabel);
      return item;
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
   // Class method used for setting values in the components of
   // the dialog display, aka. connection parameters.
   //==============================================================

   private void setSelectedSite()
   {
      if (selectedSite == null)
         return;

      // Site's parameters assigned.
      
      hostTextField.setText(selectedSite.getHost());
      dbTextField.setText(selectedSite.getDatabase());
      userTextField.setText(selectedSite.getUser());
   }

   //==============================================================
   // Class method to return the last Action Performed result.
   // Either the dialog was closed, canceled, or oked.
   //==============================================================

   public boolean isActionResult()
   {
      if (actionResult.equals("ok"))
      {
         if (!siteSelected)
            return false;
         else
            return createConnectionProperties();
      }
      else
         return false;
   }

   //==============================================================
   // Class method to provide setting up the Connection Properties
   // & verification as needed.
   //==============================================================

   private boolean createConnectionProperties()
   {
      // Method Instances
      Connection dbConnection;

      String driver, protocol, subProtocol, host, port, db, user, ssh;
      String connectionURLString;
      Properties connectProperties;

      String passwordString;
      char[] passwordCharacters;

      // ================================================
      // Obtaining the connection parameters & preparing
      // them for the Connection Properties.
      // ================================================

      connectProperties = new Properties();

      driver = selectedSite.getDriver();
      protocol = selectedSite.getProtocol();
      subProtocol = selectedSite.getSubProtocol();
      host = selectedSite.getHost();
      port = selectedSite.getPort();
      db = selectedSite.getDatabase();

      user = selectedSite.getUser();
      connectProperties.setProperty("user", user);

      if (selectedSite.getSsh().equals("1"))
      {
         ssh = "true";

         if (subProtocol.indexOf(ConnectionManager.HSQL) != -1
             || subProtocol.equals(ConnectionManager.MYSQL)
             || subProtocol.equals(ConnectionManager.POSTGRESQL))
            connectProperties.setProperty("useSSL", "1");
      }
      else
         ssh = "false";

      passwordString = "";
      passwordCharacters = passwordTextField.getPassword();

      // Obtaining the password & clearing.

      StringBuffer tempBuffer = new StringBuffer();
      for (int i = 0; i < passwordCharacters.length; i++)
      {
         tempBuffer.append(passwordCharacters[i]);
         passwordCharacters[i] = '0';
      }
      passwordString = tempBuffer.toString();

      // The % character is interpreted as the start of a special
      // escaped sequence, two digit hexadeciaml value. So replace
      // passwordString characters with that character with that
      // character's hexadecimal value as sequence, %37. Java API
      // URLDecoder.

      if (subProtocol.indexOf(ConnectionManager.HSQL) != -1
          || subProtocol.equals(ConnectionManager.DERBY)
          || subProtocol.equals(ConnectionManager.POSTGRESQL)
          || subProtocol.equals(ConnectionManager.MARIADB)
          || subProtocol.equals(ConnectionManager.MYSQL))
         passwordString = passwordString.replaceAll("%", "%" + Integer.toHexString(37));

      connectProperties.setProperty("password", passwordString);

      // Store parameters.

      connectionProperties.setProperty(ConnectionProperties.DRIVER, driver);
      connectionProperties.setProperty(ConnectionProperties.PROTOCOL, protocol);
      connectionProperties.setProperty(ConnectionProperties.SUBPROTOCOL, subProtocol);
      connectionProperties.setProperty(ConnectionProperties.HOST, host);
      connectionProperties.setProperty(ConnectionProperties.PORT, port);
      connectionProperties.setProperty(ConnectionProperties.DB, db);
      connectionProperties.setProperty(ConnectionProperties.USER, user);
      connectionProperties.setProperty(ConnectionProperties.PASSWORD, passwordString);
      connectionProperties.setProperty(ConnectionProperties.SSH, ssh);

      connectionURLString = ConnectionManager.createConnectionURLString(connectionProperties);

      if (Ajqvue.getDebug())
      {
         System.out.println("DataSourceDialog createConnectionProperties() Connection URL: "
                            + connectionURLString);
      }

      connectionProperties.setConnectionURLString(connectionURLString);

      // Connection Attempt.

      if (validateCheckBox.isSelected())
      {
         try
         {
            dbConnection = DriverManager.getConnection(connectionURLString, connectProperties);

            if (dbConnection != null)
               dbConnection.close();
            
            JOptionPane.showMessageDialog(
               null, resourceSuccess, "", JOptionPane.PLAIN_MESSAGE, successIcon);
            
         }
         catch (SQLException e)
         {
            ConnectionManager.displaySQLErrors(
               e, "DataSourceDialog createConnectionProperties()");
            return false;
         }
      }
      return true;
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