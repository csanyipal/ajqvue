//=================================================================
//                     LoginFrame Class 
//=================================================================
//
//   This class provides the framework for a login access dialog
// to a database. The class provides the main frame used to allow
// the user entry to the application. Several input preferences
// must be specified in this login in order to establish a valid
// connection to a database. 
//
//                   << LoginFrame.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.1 09/21/2016
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
// Version 1.0 Production AccessDialog Class.
//         1.1 Corrected logoIconPanel Resource in Constructor.
//
//-----------------------------------------------------------------
//                  danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.ConnectionProperties;
import com.dandymadeproductions.ajqvue.datasource.DatabaseProperties;
import com.dandymadeproductions.ajqvue.gui.panels.AdvancedParametersPanel;
import com.dandymadeproductions.ajqvue.gui.panels.GraphicsCanvasPanel;
import com.dandymadeproductions.ajqvue.gui.panels.SplashPanel;
import com.dandymadeproductions.ajqvue.gui.panels.StandardParametersPanel;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;
import com.dandymadeproductions.ajqvue.utilities.NormalizeString;

/**
 *    This LoginFrame class provides the framework for a login access
 * dialog to a database. The class provides the main frame used to allow
 * the user entry to the application. Several input preferences must
 * be specified in this login in order to establish a valid connection
 * to a database. 
 * 
 * @author Dana M. Proctor
 * @version 1.1 09/21/2016
 */

public class LoginFrame extends JFrame implements ActionListener
{
   // Class Instances
   private static final long serialVersionUID = 3415491604328645598L;
   
   private LoginManagerFrame loginManagerFrame;
   private AResourceBundle resourceBundle;
   private JWindow splashWindow;
   private SplashPanel splashPanel;
   
   private String resource, fileSeparator;
   
   private JMenuBar accessDialogMenuBar;
   private JButton loginManagerFrame_AccessButton;
   private JButton advancedOptionsButton;

   private StandardParametersPanel standardParametersPanel;
   private AdvancedParametersPanel advancedParametersPanel;

   private JCheckBox sshCheckBox;

   private JButton validLoginButton, loginButton, cancelButton;

   private ArrayList<String> sitesNameList, driverList, protocolList, subProtocolList,
                    hostList, portList, databaseList, userList;
   
   private transient HashMap<String, SiteParameters> sites;
   private transient SiteParameters lastSite;
   private transient XMLTranslator xmlTranslator;
   private transient NormalizeString normString;
   
   private JButton loginManagerFrame_SaveExitButton, loginManagerFrame_CancelButton;

   private boolean loggedIn = false;
   private boolean advancedOptionsShowing = false;

   //==============================================================
   // LoginFrame Constructor
   //==============================================================

   public LoginFrame(JButton validLoginButton)
   {
      this.validLoginButton = validLoginButton;
      
      // Constructor Instances.
      String iconsDirectory;
      ImageIcon loginManagerIcon, advancedConnectionsIcon;
      ImageIcon accessIcon, sshUpIcon, sshDownIcon;
      ImageIcon logoPanelIcon;
      JMenu siteSelectMenu;
      JPanel mainPanel, centerPanel, actionPanel;
      GraphicsCanvasPanel logoPanel;
      
      resourceBundle = Ajqvue.getResourceBundle();
      
      // Set Frame Parameters
      
      resource = resourceBundle.getResourceString("LoginFrame.message.Title", "Login");
      setTitle("Ajqvue " + resource);
      setIconImage(Utils.getFrameIcon());
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // Setting up Various Instances.
      
      xmlTranslator = new XMLTranslator();
      normString = new NormalizeString();
      sites = new HashMap <String, SiteParameters>();
      sitesNameList = new ArrayList <String>();
      driverList = new ArrayList <String>();
      protocolList = new ArrayList <String>();
      subProtocolList = new ArrayList <String>();
      hostList = new ArrayList <String>();
      portList = new ArrayList <String>();
      databaseList = new ArrayList <String>();
      userList = new ArrayList <String>();

      fileSeparator = Utils.getFileSeparator();
      iconsDirectory = Utils.getIconsDirectory() + fileSeparator;
      
      // Obtain & create Image Icons.
      
      loginManagerIcon = new ImageIcon(iconsDirectory + "connectionManagerIcon.png");
      advancedConnectionsIcon = new ImageIcon(iconsDirectory + "advancedConnectionsIcon.png");
      
      SimpleDateFormat format = new SimpleDateFormat("k");
      int dateHour = Integer.parseInt(format.format(new Date()));
      
      if (dateHour >= 5 && dateHour <= 10)
         accessIcon = new ImageIcon(iconsDirectory + "loginFrameIconA.gif");
      else if (dateHour >= 11 && dateHour <= 16)
         accessIcon = new ImageIcon(iconsDirectory + "loginFrameIconB.gif");
      else if (dateHour >= 17 && dateHour <= 21)
         accessIcon = new ImageIcon(iconsDirectory + "loginFrameIconC.gif");
      else
         accessIcon = new ImageIcon(iconsDirectory + "loginFrameIconD.gif");
      
      logoPanelIcon = new ImageIcon("images" + fileSeparator + "Ajqvue_Logo.png");
      sshUpIcon = new ImageIcon(iconsDirectory + "sshUpIcon.png");
      sshDownIcon = new ImageIcon(iconsDirectory + "sshDownIcon.png");
      
      // Setting up the MenuBar for the access of the
      // LoginFrame, connection selection & advanced options.

      accessDialogMenuBar = new JMenuBar();
      accessDialogMenuBar.setBorder(BorderFactory.createEtchedBorder());
      accessDialogMenuBar.setMargin(new Insets(0, 0, 0, 0));
      
      if (xmlTranslator.getXMLTranslatorResult())
      {
         // Site Connections
         sites = xmlTranslator.getSites();
         lastSite = xmlTranslator.getLastSite();
         sites.put(lastSite.getSiteName(), lastSite);
      }
      else
         fillSitesDefaults();
     
      // Setup Sites Slection JMenu.
      resource = resourceBundle.getResourceString("LoginFrame.menu.Sites", "Sites");
      siteSelectMenu = new JMenu(resource);
      
      resource = resourceBundle.getResourceString("LoginFrame.tooltip.SiteSelection", "Site Selection");
      siteSelectMenu.setToolTipText(resource);
      fillSiteDataStructures(siteSelectMenu);
      accessDialogMenuBar.add(siteSelectMenu);

      // Login Manager Frame Components.
      loginManagerFrame_AccessButton = new JButton(loginManagerIcon);
      loginManagerFrame_AccessButton.setFocusable(false);
      loginManagerFrame_AccessButton.setMargin(new Insets(0, 0, 0, 0));
      
      resource = resourceBundle.getResourceString("LoginFrame.tooltip.LoginManager", "Login Manager");
      loginManagerFrame_AccessButton.setToolTipText(resource);
      
      if (xmlTranslator.getXMLTranslatorResult())
         loginManagerFrame_AccessButton.addActionListener(this);
      else
         loginManagerFrame_AccessButton.setEnabled(false);
      
      accessDialogMenuBar.add(loginManagerFrame_AccessButton);
         
      // Advanced Options Selection
      advancedOptionsButton = new JButton(advancedConnectionsIcon);
      advancedOptionsButton.setFocusable(false);
      advancedOptionsButton.setMargin(new Insets(0, 0, 0, 0));
      resource = resourceBundle.getResourceString("LoginFrame.tooltip.AdvancedOptions", "Advanced Options");
      advancedOptionsButton.setToolTipText(resource);
      advancedOptionsButton.addActionListener(this);
      accessDialogMenuBar.add(advancedOptionsButton);

      accessDialogMenuBar.add(Box.createHorizontalGlue());

      // Access dmp Icon
      JButton accessIconItem = new JButton(accessIcon);
      accessIconItem.setPressedIcon(accessIcon);
      accessIconItem.setDisabledIcon(accessIcon);
      accessIconItem.setFocusPainted(false);
      accessIconItem.setMargin(new Insets(1, 0, 0, 0));
      accessIconItem.setBorder(BorderFactory.createEtchedBorder());
      accessDialogMenuBar.add(accessIconItem);

      setJMenuBar(accessDialogMenuBar);

      // ================================================
      // Setting up the main panel that will be needed
      // in the frame. The main panel borderlayout will
      // have most compenents in the center with actions
      // buttons in the south.
      
      mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createEtchedBorder());
      
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();

      centerPanel = new JPanel(gridbag);

      // Logo & Components
      logoPanel = new GraphicsCanvasPanel(logoPanelIcon.getImage());
      logoPanel.setBorder(BorderFactory.createEtchedBorder());
      logoPanel.add(new JLabel(logoPanelIcon));
      
      buildConstraints(constraints, 0, 0, 1, 1, 20, 100);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(logoPanel, constraints);
      centerPanel.add(logoPanel);

      // Standard Parameters Panel & Components
      standardParametersPanel = new StandardParametersPanel(resourceBundle, hostList, databaseList,
                                                            userList);
      standardParametersPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
      
      buildConstraints(constraints, 1, 0, 1, 1, 40, 100);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(standardParametersPanel, constraints);
      centerPanel.add(standardParametersPanel);

      advancedParametersPanel = new AdvancedParametersPanel(resourceBundle, driverList, protocolList,
                                                            subProtocolList, portList);
      advancedParametersPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
      
      buildConstraints(constraints, 2, 0, 1, 1, 40, 100);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(advancedParametersPanel, constraints);
      centerPanel.add(advancedParametersPanel);
      advancedParametersPanel.setVisible(false);

      mainPanel.add(centerPanel, BorderLayout.CENTER);

      // Action Button Panel
      actionPanel = new JPanel();
      actionPanel.setBorder(BorderFactory.createRaisedBevelBorder());

      resource = resourceBundle.getResourceString("LoginFrame.checkbox.SSH", "SSH");
      sshCheckBox = new JCheckBox(resource, sshUpIcon);
      sshCheckBox.setSelectedIcon(sshDownIcon);
      sshCheckBox.setHorizontalTextPosition(SwingConstants.LEADING);
      sshCheckBox.setFocusPainted(false);
      actionPanel.add(sshCheckBox);
      
      resource = resourceBundle.getResourceString("LoginFrame.button.cancel", "cancel");
      cancelButton = new JButton(resource);
      cancelButton.addActionListener(this);
      actionPanel.add(cancelButton);
      
      resource = resourceBundle.getResourceString("LoginFrame.button.login", "login");
      loginButton = new JButton(resource);
      loginButton.addActionListener(this);
      actionPanel.add(loginButton);
      
      // Setting the component, connection parameters,
      // to last site, and then adding everything to the
      // frame.
      
      setSelectedSite(lastSite);
      mainPanel.add(actionPanel, BorderLayout.SOUTH);
      getContentPane().add(mainPanel);
      (this.getRootPane()).setDefaultButton(loginButton);

      // Creating the LoginManager Action Buttons
      resource = resourceBundle.getResourceString("LoginFrame.button.saveandexit", "save and exit");
      loginManagerFrame_SaveExitButton = new JButton(resource);
      loginManagerFrame_SaveExitButton.addActionListener(this);
      
      resource = resourceBundle.getResourceString("LoginFrame.button.cancel", "cancel");
      loginManagerFrame_CancelButton = new JButton(resource);
      loginManagerFrame_CancelButton.addActionListener(this);
   }
   
   //==============================================================
   // Class Method for helping the parameters in gridbag.
   //==============================================================

   private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, double wx, double wy)
   {
      gbc.gridx = gx;
      gbc.gridy = gy;
      gbc.gridwidth = gw;
      gbc.gridheight = gh;
      gbc.weightx = wx;
      gbc.weighty = wy;
   }

   //==============================================================
   // ActionEvent Listener method for detecting the user's selection
   // of the frame's various JButtons and JMenu then directing to
   // the appropriate action..
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object panelSource = evt.getSource();

      // Button Actions
      if (panelSource instanceof JButton)
      {
         JButton actionButton = (JButton) panelSource;

         // LoginManager Option.
         if (actionButton == loginManagerFrame_AccessButton)
         {
            // Creation of the LoginManager Main Frame
            // as required.
            if (loginManagerFrame == null)
            {
               StandardParametersPanel standardPanelClone = new StandardParametersPanel(resourceBundle,
                                                                                        hostList,
                                                                                        databaseList,
                                                                                        userList);
               standardPanelClone.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
               
               AdvancedParametersPanel advancedPanelClone = new AdvancedParametersPanel(resourceBundle,
                                                                                        driverList,
                                                                                        protocolList,
                                                                                        subProtocolList,
                                                                                        portList);
               advancedPanelClone.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

               loginManagerFrame = new LoginManagerFrame(resourceBundle, sites, normString,
                                                         standardPanelClone, advancedPanelClone,
                                                         loginManagerFrame_SaveExitButton,
                                                         loginManagerFrame_CancelButton);
               loginManagerFrame.pack();
               loginManagerFrame.setResizable(true);
               loginManagerFrame.center(-50, 50);
               loginManagerFrame.setVisible(true);
            }
            else
            {
               loginManagerFrame.setVisible(false);
               loginManagerFrame.dispose();
               loginManagerFrame = null;
            }
         }

         // Advanced Options
         else if (actionButton == advancedOptionsButton)
         {
            if (advancedOptionsShowing)
            {
               advancedParametersPanel.setVisible(false);
               advancedOptionsShowing = false;
            }
            else
            {
               advancedParametersPanel.setVisible(true);
               advancedOptionsShowing = true;
            }
            pack();
         }

         // Login Attempt
         else if (actionButton == loginButton)
         {
            setVisible(false);
            
            if (accessCheck())
            {     
               // Getting and saving the current selected
               // site parameters before heading off to
               // main window. Check for problems first.
               
               if (xmlTranslator.getXMLTranslatorResult())
               {
                  lastSite.setSiteName("Last Site");
                  lastSite.setDriver(advancedParametersPanel.getDriver());
                  lastSite.setProtocol(advancedParametersPanel.getProtocol());
                  lastSite.setSubProtocol(advancedParametersPanel.getSubProtocol());
                  lastSite.setHost(standardParametersPanel.getHost());
                  lastSite.setPort(advancedParametersPanel.getPort());
                  lastSite.setDatabase(standardParametersPanel.getDatabase());
                  lastSite.setUser(standardParametersPanel.getUser());
                  lastSite.setPassword(" ".toCharArray());
                  if (sshCheckBox.isSelected())
                     lastSite.setSsh("1");
                  else
                     lastSite.setSsh("0");
                  xmlTranslator.setLastSite(lastSite);
               }

               // Making sure the LoginManager gets closed down.
               if (loginManagerFrame != null)
               {
                  loginManagerFrame.setVisible(false);
                  loginManagerFrame.dispose();
                  loginManagerFrame = null;
               }
               
               // Close Down the Splash Window.
               Thread splashDelayThread = new Thread(new Runnable()
               {
                  public void run()
                  {
                     try
                     {
                        Thread.sleep(1000);
                        splashPanel.suspendPanel(true);
                        splashWindow.dispose();
                     }
                     catch (InterruptedException e) {}
                  }
               }, "LoginFrame.splashDelayThread");
               splashDelayThread.start();
              
               validLoginButton.doClick();
            }
            else
            {
               standardParametersPanel.setPassword(" ".toCharArray());
               setVisible(true);
            }
         }

         // Cancel Action
         else if (actionButton == cancelButton)
         {
            dispose();
         }

         // LoginManager Save/Exit Action
         else if (actionButton == loginManagerFrame_SaveExitButton)
         {
            // Collect the possibly modified sites.
            sites = loginManagerFrame.getSites();

            // Temp debug.
            /*
             * Enumeration sitesKeys = sites.keys(); while
             * (sitesKeys.hasMoreElements()) { String currentKey = new
             * String((String)sitesKeys.nextElement());
             * System.out.println(currentKey); }
             */

            // Closing out LoginManagerFrame and taking
            // action to update JMenu.
            loginManagerFrame.setVisible(false);
            loginManagerFrame.dispose();
            loginManagerFrame = null;

            // Updating JMenuBar and ComboBoxes.
            JMenu siteSelectMenu;
            resource = resourceBundle.getResourceString("LoginFrame.menu.Sites", "Sites");
            siteSelectMenu = new JMenu(resource);
            
            resource = resourceBundle.getResourceString("LoginFrame.tooltip.SiteSelection", "Site Selection");
            siteSelectMenu.setToolTipText(resource);
            fillSiteDataStructures(siteSelectMenu);
            accessDialogMenuBar.remove(0);
            accessDialogMenuBar.add(siteSelectMenu, 0);
            setJMenuBar(accessDialogMenuBar);

            // Update the Local XML File.
            xmlTranslator.setSites(sites);
         }

         // LoginManagerFrame Cancel Action
         else if (actionButton == loginManagerFrame_CancelButton)
         {
            // Do take any action other than closing out
            // LoginManagerFrame.
            loginManagerFrame.setVisible(false);
            loginManagerFrame.dispose();
            loginManagerFrame = null;
         }
      }
      // JMenu Actions
      else if (panelSource instanceof JMenuItem)
      {
         JMenuItem selectedMenuItem = (JMenuItem) panelSource;
         String actionCommandName = selectedMenuItem.getActionCommand();
         // System.out.println(actionCommandName);

         if (!actionCommandName.equals("Cut") && !actionCommandName.equals("Copy")
             && !actionCommandName.equals("Paste"))
         {
            SiteParameters selectedSite = sites.get(actionCommandName);
            setSelectedSite(selectedSite);
         }
      }
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
      SiteParameters currentSiteParameter;
      String siteName;

      // Remove all previous sites.
      siteSelectMenu.removeAll();

      // Create a natural order of JMenus
      // of the given sites' names.
      
      siteEntriesIterator = sites.entrySet().iterator();
      sitesTreeSet = new TreeSet <String>();

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
      sitesJMenus = new HashMap <String, JMenu>();

      while (sitesTreeIterator.hasNext())
      {
         String currentSiteName = sitesTreeIterator.next();
         JMenu currentSiteJMenu = new JMenu(currentSiteName);
         sitesJMenus.put(currentSiteName, currentSiteJMenu);
         siteSelectMenu.add(currentSiteJMenu);
      }

      // Now that the data has been organized
      // then clear any contents from the vectors
      // used for the comboboxes and refill also
      // in the process add databases to the site
      // JMenus.

      driverList.clear();
      protocolList.clear();
      subProtocolList.clear();
      hostList.clear();
      portList.clear();
      databaseList.clear();
      userList.clear();

      siteEntriesIterator = sites.entrySet().iterator();
      
      while (siteEntriesIterator.hasNext())
      {
         String jmenuSiteName;
         String jmenuDBName;

         // Fill the JMenu first then create comboboxes
         // elements.
         siteEntry = siteEntriesIterator.next();
         
         siteName = siteEntry.getKey();
         sitesNameList.add(siteName);

         if (!siteName.equals("Last Site") && siteName.indexOf('#') != -1)
         {
            jmenuSiteName = siteName.substring(0, siteName.indexOf('#'));
            jmenuDBName = siteName.substring(siteName.indexOf('#') + 1);

            JMenu currentSiteJMenu = sitesJMenus.get(jmenuSiteName);
            currentSiteJMenu.add(createMenuItem(jmenuDBName, siteName));
         }
         
         currentSiteParameter = siteEntry.getValue();

         if (!driverList.contains(currentSiteParameter.getDriver()))
            driverList.add(currentSiteParameter.getDriver());
         if (!protocolList.contains(currentSiteParameter.getProtocol()))
            protocolList.add(currentSiteParameter.getProtocol());
         if (!subProtocolList.contains(currentSiteParameter.getSubProtocol()))
            subProtocolList.add(currentSiteParameter.getSubProtocol());
         if (!hostList.contains(currentSiteParameter.getHost()))
            hostList.add(currentSiteParameter.getHost());
         if (!portList.contains(currentSiteParameter.getPort()))
            portList.add(currentSiteParameter.getPort());
         if (!databaseList.contains(currentSiteParameter.getDatabase()))
            databaseList.add(currentSiteParameter.getDatabase());
         if (!userList.contains(currentSiteParameter.getUser()))
            userList.add(currentSiteParameter.getUser());
      }
   }
   
   //==============================================================
   // Class method used for the filling the data structures that
   // hold the site menu items in the menu bar and list parameters
   // in the combobox fields with default values. Something went
   // wrong with processing the the ajqvue.xml file.
   //==============================================================

   private void fillSitesDefaults()
   {
      // Class Method Instances
      SiteParameters currentSiteParameter;
      String siteName;
      
      // Example defaults database settings.
      String[] defaultDrivers = {"com.mysql.jdbc.Driver", "org.postgresql.Driver", "org.hsqldb.jdbcDriver",
                                 "oracle.jdbc.driver.OracleDriver", "org.sqlite.JDBC",
                                 "sun.jdbc.odbc.JdbcOdbcDriver", "org.apache.derby.jdbc.ClientDriver",
                                 "org.h2.Driver"};
      String[] defaultSubProtocols = {"mysql", "postgresql", "hsqldb:hsql", "oracle:thin", "sqlite", "odbc",
                                      "derby", "h2"};
      
      String[] defaultPorts = {"3306", "5432", "9001", "1521", "0000", "0000", "1527", "9092"};
      String[] defaultDatabases = {"mysql", "postgresql", "hsql;", "oracle", "test/sqlite_test.db",
                                   "ms_access", "test/derby_db/toursdb", "test/h2_db/h2_test"};
      
      // Clear contents to start anewed.
      driverList.clear();
      protocolList.clear();
      subProtocolList.clear();
      hostList.clear();
      portList.clear();
      databaseList.clear();
      userList.clear();
                                   
      // Create the SiteParmeters with the defaults for
      // each site name entry. Then fill the login frame
      // components with the defaults.
      
      for (int i=0; i < defaultDatabases.length; i++)
      {
         // SiteParameters
         siteName = "LocalHost#" + defaultDatabases[i];
         
         currentSiteParameter = new SiteParameters();
         
         currentSiteParameter.setSiteName("LocalHost");
         currentSiteParameter.setDriver(defaultDrivers[i]);
         currentSiteParameter.setProtocol("jdbc");
         currentSiteParameter.setSubProtocol(defaultSubProtocols[i]);
         currentSiteParameter.setHost("127.0.0.1");
         currentSiteParameter.setPort(defaultPorts[i]);
         currentSiteParameter.setDatabase(defaultDatabases[i]);
         currentSiteParameter.setUser("");
         currentSiteParameter.setPassword("".toCharArray());
         currentSiteParameter.setSsh("0");
        
         sites.put(siteName, currentSiteParameter);
         
         // Fill Login Access Components
         sitesNameList.add(siteName);
         
         if (!driverList.contains(currentSiteParameter.getDriver()))
            driverList.add(currentSiteParameter.getDriver());
         if (!protocolList.contains(currentSiteParameter.getProtocol()))
            protocolList.add(currentSiteParameter.getProtocol());
         if (!subProtocolList.contains(currentSiteParameter.getSubProtocol()))
            subProtocolList.add(currentSiteParameter.getSubProtocol());
         if (!hostList.contains(currentSiteParameter.getHost()))
            hostList.add(currentSiteParameter.getHost());
         if (!portList.contains(currentSiteParameter.getPort()))
            portList.add(currentSiteParameter.getPort());
         if (!databaseList.contains(currentSiteParameter.getDatabase()))
            databaseList.add(currentSiteParameter.getDatabase());
         if (!userList.contains(currentSiteParameter.getUser()))
            userList.add(currentSiteParameter.getUser());
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
   // Class method used for setting values in the components of
   // the login interface, aka. connection parameters.
   //==============================================================

   private void setSelectedSite(SiteParameters selectedSite)
   {
      if (selectedSite == null)
         return;
      
      // Login component setting.
      advancedParametersPanel.setDriver(selectedSite.getDriver());
      advancedParametersPanel.setProtocol(selectedSite.getProtocol());
      advancedParametersPanel.setSubProtocol(selectedSite.getSubProtocol());
      standardParametersPanel.setHostItem(selectedSite.getHost());
      advancedParametersPanel.setPort(selectedSite.getPort());
      standardParametersPanel.setDatabaseItem(selectedSite.getDatabase());
      standardParametersPanel.setUserItem(selectedSite.getUser());
      standardParametersPanel.setPassword(
         XMLTranslator.textConversion(
            normString.execute(String.valueOf(selectedSite.getPassword()), false), true));
      if (selectedSite.getSsh().equals("0"))
         sshCheckBox.setSelected(false);
      else
         sshCheckBox.setSelected(true);
   }

   //==============================================================
   // Class method to provide verification of a valid input for
   // access to the data source.
   //==============================================================

   private boolean accessCheck()
   {
      Connection dbConnection;
      
      String driver, protocol, subProtocol, host, port, db, user, passwordString, ssh;
      String connectionURLString;
      Properties connectProperties;
      
      char[] passwordCharacters;

      // Try to login in the user with the specified connection

      // Check for some kind of valid input.
      if (advancedParametersPanel.getDriver().equals("") ||
          advancedParametersPanel.getProtocol().equals("") ||
          advancedParametersPanel.getSubProtocol().equals("") ||
          standardParametersPanel.getHost().equals("") ||
          advancedParametersPanel.getPort().equals("") ||
          standardParametersPanel.getUser().equals(""))
      {
         loggedIn = false;
      }

      // All entries there so try to make a connecion to the
      // database.
      else
      {
         // =================================================
         // Checking to see if the jdbc driver is available
         // =================================================

         try
         {
            driver = advancedParametersPanel.getDriver();
            if (Ajqvue.getDebug())
               System.out.println("LoginFrame accessCheck() Driver: " + driver);
            
            // Run SQLite in pure Java mode to maintain compatibility,
            // slower, but works with older versions of JVM. Revisit
            // Later if really needed.
            
            if (advancedParametersPanel.getSubProtocol().indexOf("sqlite") != -1)
               System.setProperty("sqlite.purejava", "true");
               
            Class.forName(driver);
            if (Ajqvue.getDebug())
               System.out.println("LoginFrame accessCheck() Driver Loaded");
         }
         catch (Exception e)
         {
            // Alert Dialog Output.
            String exceptionString = e.getMessage();
            if (exceptionString != null && exceptionString.length() > 200)
               exceptionString = exceptionString.substring(0, 200);
            
            String javaExtDir = System.getProperty("java.ext.dirs");
            if (javaExtDir == null || javaExtDir.equals(""))
               javaExtDir = "Java JRE/lib/ext"; 

            String optionPaneStringErrors = "Unable to Find or Load JDBC Driver" + "\n"
                                            + "Insure the Appropriate JDBC Driver is "
                                            + "Located in the " + "\n"
                                            + javaExtDir + fileSeparator + " directory."
                                            + "\n"
                                            + "Exeception: " + exceptionString;
            JOptionPane.showMessageDialog(null, optionPaneStringErrors, "Alert", JOptionPane.ERROR_MESSAGE);

            loggedIn = false;
            return false;
         }

         // ================================================
         // Obtaining the connection parameters & password.
         // ================================================

         connectProperties = new Properties();
         
         protocol = advancedParametersPanel.getProtocol();
         subProtocol = advancedParametersPanel.getSubProtocol().toLowerCase(Locale.ENGLISH);
         host = standardParametersPanel.getHost();
         port = advancedParametersPanel.getPort();
         db = standardParametersPanel.getDatabase();
         
         user = standardParametersPanel.getUser();
         connectProperties.setProperty("user", user);
      
         if (sshCheckBox.isSelected())
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
         passwordCharacters = standardParametersPanel.getPassword();

         // Obtaining the password & clearing.
         
         StringBuffer tempBuffer = new StringBuffer();
         for (int i = 0; i < passwordCharacters.length; i++)
         {
            tempBuffer.append(passwordCharacters[i]);
            passwordCharacters[i] = '0';
         }
         passwordString = tempBuffer.toString();
         
         // The % character is interpreted as the start of a special escaped sequence,
         // two digit hexadeciaml value. So replace passwordString characters with that
         // character with that characters hexadecimal value as sequence, %37. Java
         // API URLDecoder.
         
         if (subProtocol.indexOf(ConnectionManager.HSQL) != -1
             || subProtocol.equals(ConnectionManager.DERBY)
             || subProtocol.equals(ConnectionManager.POSTGRESQL)
             || subProtocol.equals(ConnectionManager.MARIADB)
             || subProtocol.equals(ConnectionManager.MYSQL))
            passwordString = passwordString.replaceAll("%", "%" + Integer.toHexString(37));
         
         connectProperties.setProperty("password", passwordString);
         
         // Store parameters.
         ConnectionProperties connectionProperties = new ConnectionProperties();
         
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
            System.out.println("LoginFrame accessCheck() Connection URL: " + connectionURLString);
            // System.out.println("LoginFrame accessCheck() Connection Properties: "
            //                    + connectProperties.toString());
         }
         
         connectionProperties.setConnectionURLString(connectionURLString);

         // ===============================================
         // Connection Attempt.
         // ===============================================

         try
         {
            dbConnection = DriverManager.getConnection(connectionURLString, connectProperties);
            
            if (Ajqvue.getDebug())
               System.out.println("LoginFrame accessCheck() Connection Established Initializing:");
            
            // The Connection is valid if it does not throw a SQL Exception.
            // So save the connection properties and collect the associated
            // database tables and other pertinent information necessary to
            // bring up the application with the DatabaseProperties Instance.
            
            createSplashWindow();
            ConnectionManager.setConnectionProperties(connectionProperties);
            
            DatabaseProperties databaseProperties = new DatabaseProperties(connectionProperties);
            databaseProperties.init(dbConnection);
            ConnectionManager.setDatabaseProperties(databaseProperties);
            
            // Override defaults with configuration file
            // if needed & load database tables.
            databaseProperties.overideDefaults();
            databaseProperties.loadDBTables(dbConnection);
            
            // Must be good so close things out and create a
            // costant connection for memory database connections.
            
            if ((subProtocol.equals(ConnectionManager.SQLITE)
                  && db.toLowerCase(Locale.ENGLISH).equals(":memory:"))
                 || (subProtocol.indexOf(ConnectionManager.HSQL) != -1
                     && db.toLowerCase(Locale.ENGLISH).indexOf("mem:") != -1)    
                 || (subProtocol.equals(ConnectionManager.DERBY)
                     && db.toLowerCase(Locale.ENGLISH).indexOf("memory:") != -1)
                 || (subProtocol.equals(ConnectionManager.H2)
                     && db.toLowerCase(Locale.ENGLISH).indexOf("mem:") != -1))
            {
               ConnectionManager.setMemoryConnection(DriverManager.getConnection(connectionURLString,
                                                                                 connectProperties));
            }
            
            dbConnection.close();
            loggedIn = true;
         }
         catch (SQLException e)
         {
            if (splashPanel != null)
            {
               splashPanel.suspendPanel(true);
               splashWindow.dispose();
            }
            loggedIn = false;
            
            ConnectionManager.displaySQLErrors(e, "LoginFrame accessCheck()");
            return false;
            // ? Remove driver Manager
         }
      }
      return loggedIn;
   }
   
   //==============================================================
   // Class method to create a splash panel to provide a visual
   // indication of the application initialization process.
   //==============================================================

   private void createSplashWindow()
   {
      // Method Instances
      Dimension screenSize, imageSize;
      
      // Collect screen size and image size.
      
      screenSize = getToolkit().getScreenSize();
      imageSize = new Dimension(420, 315);
      
      // Create window and animated splash.
      
      splashWindow = new JWindow();
      splashWindow.getContentPane().setLayout(new BorderLayout());
      splashWindow.setSize(imageSize);
      
      splashPanel = new SplashPanel(resourceBundle);
      Thread splashPanelThread = new Thread(splashPanel, "SplashPanelThread");
      splashPanelThread.start();
      
      splashWindow.getContentPane().add(splashPanel, BorderLayout.CENTER);
      splashWindow.setLocation((screenSize.width - imageSize.width) / 2,
                               (screenSize.height - imageSize.height) / 2);
      splashWindow.setVisible(true);
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
