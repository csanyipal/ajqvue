//=================================================================
//                       Main_Frame Class
//=================================================================
// 
//    This class is used to setup the main user interface frame
// for the application. The class provides the basis for the
// overall application's general look and feel in addition to
// plugin creation and inclusion.
//
//                   << Main_Frame.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.1 09/24/2016
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
// Version 1.0 09/19/2016 Production Main_Frame Class.
//         1.1 09/24/2016 Updated References to PluginModule to Plugin_Module.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.ConnectionProperties;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.gui.panels.TopTabPanel;
import com.dandymadeproductions.ajqvue.plugin.Default_JToolBar;
import com.dandymadeproductions.ajqvue.plugin.Plugin_Module;
import com.dandymadeproductions.ajqvue.plugin.PluginLoader;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The Main_Frame class is used to setup the main user interface
 * frame for the application. The class provides the basis for the
 * overall application's general look and feel in addition to plugin
 * creation and inclusion.
 * 
 * @author Dana M. Proctor
 * @version 1.1 09/24/2016
 */

public class Main_Frame extends JFrame implements ActionListener, ChangeListener
{
   // Class Instances.
   private static final long serialVersionUID = 9033690016117959449L;

   private TopTabPanel mainTabPanel;
   private Main_JMenuBar menuBar;
   private Top_JMenuBar topMenuBar;
   
   private String[] version;
   private String webSiteString;
   private int lastTabIndex;
   
   private static JTabbedPane mainTabsPane = new JTabbedPane();
   private static CardLayout toolBarCardLayout = new CardLayout();
   private static JPanel toolBarPanel = new JPanel(toolBarCardLayout);
   private static DBTablesPanel dbTablesPanel;
   
   private static ArrayList<Plugin_Module> loadedPluginModules = 
                                                  new ArrayList <Plugin_Module>();
   private static SQLQueryBucketFrame sqlQueryBucketFrame = new SQLQueryBucketFrame();
   
   protected static final JButton pluginFrameListenButton = new JButton();
   public static final int FRAME_DEFAULT_WIDTH = 800;
   public static final int FRAME_DEFAULT_HEIGHT = 600;
   
   //==============================================================
   // Main_Frame Constructor
   //==============================================================

   public Main_Frame(String[] version, String webSiteString)
   {
      // Displaying title and assigning instance associations.

      super("Ajqvue   "
            + ConnectionManager.getConnectionProperties().getProperty(ConnectionProperties.HOST) + ":"
            + ConnectionManager.getConnectionProperties().getProperty(ConnectionProperties.DB));

      this.version = version.clone();
      this.webSiteString = webSiteString;
      
      //==================================================
      // Frame Window closing listener to detect the frame
      // window closing event so the SQL Query Bucket can
      // be saved, along with some other clean up activity.
      //==================================================

      final JFrame thisFrame = this;
      
      WindowListener frameListener = new WindowAdapter()
      {
         public void windowClosing(WindowEvent e)
         {
            // Save Query Bucket Data.
            sqlQueryBucketFrame.saveLastUsedList();
            
            // Shutdown Connection as Needed.
            ConnectionManager.shutdown("Main_Frame WINDOW_CLOSING");
            
            // Notify plugins to pending close.
            Iterator<Plugin_Module> pluginModulesIterator = loadedPluginModules.iterator();
            while (pluginModulesIterator.hasNext())
            {
               Plugin_Module currentPlugin = pluginModulesIterator.next();
               currentPlugin.shutdown();
            }
            
            // Clear Cash
            Utils.clearCache();
            
            // Save Frame Size & Position
            Ajqvue.getGeneralProperties().setPosition(new Point(thisFrame.getX(), thisFrame.getY()));
            Ajqvue.getGeneralProperties().setDimension(thisFrame.getSize());
            System.exit(0);
         }

         public void windowActivated(WindowEvent e){}
      };
      this.addWindowListener(frameListener);
   }

   //==============================================================
   // Class method to setup the user interface, tabbed pane.
   //==============================================================

   public void createGUI()
   {
      // Class Instances
      JPanel mainPanel;
      AResourceBundle resourceBundle;
      String fileSeparator, iconsDirectory;
      ImageIcon mainTabIcon, databaseTablesIcon;
      Default_JToolBar defaultToolBar;
      Main_JToolBar toolBar;
      
      // Setting up Various Instances.
      
      resourceBundle = Ajqvue.getResourceBundle();
      fileSeparator = Utils.getFileSeparator();
      iconsDirectory = Utils.getIconsDirectory() + fileSeparator;
      
      // Obtain & create Image Icons.
      
      mainTabIcon = resourceBundle.getResourceImage(iconsDirectory + "mainTabIcon.png");
      databaseTablesIcon = resourceBundle.getResourceImage(iconsDirectory + "databasetablesIcon.png");
      
      setIconImage(Utils.getFrameIcon());
      
      // Setup the menu bar for the frame.
      
      topMenuBar = new Top_JMenuBar(this);
      setJMenuBar(topMenuBar);
      
      // ===============================================
      // Setting up the tabbed pane with the various
      // panels that provide the functionality of the
      // application and its plugins.
      // ===============================================
      
      mainPanel = new JPanel(new BorderLayout());
      
      // Toolbar
      defaultToolBar = new Default_JToolBar("Default ToolBar");
      toolBarPanel.add("0", defaultToolBar);
      mainPanel.add(toolBarPanel, BorderLayout.PAGE_START);

      // Central Area
      mainTabsPane.setTabPlacement(JTabbedPane.RIGHT);
      mainTabsPane.setBorder(BorderFactory.createLoweredBevelBorder());
      mainTabsPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

      //=========================================
      // Standard dmp Main Tab
      
      mainTabPanel = new TopTabPanel(true);
      Thread mainTabPanelThread = new Thread(mainTabPanel, "TopTabPanelThread");
      mainTabPanelThread.start();
      
      mainTabsPane.addTab(null, mainTabIcon, mainTabPanel, "Dandy Made Productions");   
      
      //=========================================
      // Standard Database Tables Tab
      
      menuBar = new Main_JMenuBar(this);
      
      toolBar = new Main_JToolBar(this, "Ajqvue ToolBar");
      toolBarPanel.add("1", toolBar);
      
      // Insure DBTablesPanel to be at index 1.
      mainTabsPane.add(new JPanel(), databaseTablesIcon, 1);
      
      Thread databaseTablesThread = new Thread(new Runnable()
      {
         public void run()
         {
            // Thread Instances.
            Connection dbConnection;
            AResourceBundle resourceBundle;
            String resource;
            
            // Obtain a database connection & resources.
            
            dbConnection = ConnectionManager.getConnection("Main_Frame createGUI()");
            resourceBundle = Ajqvue.getResourceBundle();
            
            dbTablesPanel = new DBTablesPanel(dbConnection, ConnectionManager.getTableNames());
            mainTabsPane.setComponentAt(1, dbTablesPanel);
            
            resource = resourceBundle.getResourceString("Main_Frame.tab.DatabaseTables",
                                                        "Database Tables");
            mainTabsPane.setToolTipTextAt(1, resource);
            
            // Closing the database connection that is used
            // during the inital setup of the application.
            
            ConnectionManager.closeConnection(dbConnection, "Main_Frame createGUI()");
         }
      }, "Main_Frame.createGUI(), databaseTablesThread");
      databaseTablesThread.start();
      
      //=========================================
      // Plugins' Tabs.
      
      new PluginLoader(this);
      
      //=========================================
      // Finishing up.
      
      mainTabsPane.addChangeListener(this);
      mainPanel.add(mainTabsPane, BorderLayout.CENTER);
      getContentPane().add(mainPanel);
      lastTabIndex = 0;
   }
   
   //==============================================================
   // ActionEvent Listener method for detecting the user's selection
   // of a Menu Bar item. Upon detection an action is then completed
   // by calling the Main_JMenuBarActions class. Created to
   // reduce clutter in this top level class & consolidate actions.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Main_JMenuBarActions.actionsSelection(this, evt, sqlQueryBucketFrame, version, webSiteString);
   }
   
   //==============================================================
   // ChangeEvent Listener method for detecting the user's selection
   // of the various tabs that are loaded in the application.
   //==============================================================

   public void stateChanged(ChangeEvent evt)
   {
      // Method Instances.
      Object changeSource;
      int selectedIndex;
      
      // Collect source of event and take appropriate action.
      
      changeSource = evt.getSource();
      
      if (changeSource != null && (JTabbedPane) changeSource == mainTabsPane)
      {
         // Obtain some parameters to be used.
         
         selectedIndex = ((JTabbedPane) changeSource).getSelectedIndex();
         
         if (selectedIndex > mainTabsPane.getTabCount())
            return;
         
         // The top mainTabPanel is a runnable thread so
         // control the animation and also other plugins'
         // requirements of stoping/starting.
         
         if (selectedIndex == 0)
         {
            mainTabPanel.resetPanel();
            mainTabPanel.setThreadAction(false);
         }
         else if (selectedIndex == 1)
         {
            mainTabPanel.setThreadAction(true);
         }
         else
         {
            mainTabPanel.setThreadAction(true);
            
            if ((selectedIndex - 2 ) <= loadedPluginModules.size())
               loadedPluginModules.get(selectedIndex - 2).start();
         }
          
         if (lastTabIndex != 0 && lastTabIndex != 1)
            loadedPluginModules.get(lastTabIndex - 2).stop();
         
         lastTabIndex = selectedIndex;
         
         // Set the MenuBar required by the tab.
         
         // Top Panel
         if (selectedIndex == 0)
            setJMenuBar(topMenuBar);
         // DBTables Panel
         else if (selectedIndex == 1)
            setJMenuBar(menuBar);
         // Plugin Panel
         else
         {
            if ((selectedIndex - 2) <= loadedPluginModules.size())
               setJMenuBar((loadedPluginModules.get(selectedIndex - 2)).getMenuBar());
         }
         
         // Set the ToolBar required by the tab.
         
         if (selectedIndex == 0)
            toolBarCardLayout.show(toolBarPanel, "0");
         else if (selectedIndex == 1)
            toolBarCardLayout.show(toolBarPanel, "1");
         else
            toolBarCardLayout.show(toolBarPanel, (loadedPluginModules.get(selectedIndex - 2)).getName());
      }
   }
   
   //==============================================================
   // Class Method to add a new plugin tab to the frame interface.
   //==============================================================
   
   public static synchronized void addTab(Plugin_Module plugin, Main_Frame parent)
   {
      if (plugin != null)
      {  
         mainTabsPane.invalidate();
         mainTabsPane.removeChangeListener(parent);
         
         loadedPluginModules.add(plugin);
         mainTabsPane.addTab(null, plugin.getControlledTabIcon(), plugin.getControlledPanel(),
                             plugin.getControlledName());
         
         if (plugin.getName().isEmpty())
            plugin.name = Integer.toString(loadedPluginModules.size() + 1);
         
         toolBarPanel.add(plugin.getControlledName(), plugin.getControlledToolBar());
         
         // Lets the PluginFrame know that a new
         // plugin module was loaded.
         pluginFrameListenButton.doClick();
         
         mainTabsPane.addChangeListener(parent);
         mainTabsPane.validate();
      } 
   }
   
   //==============================================================
   // Class Method to remove a plugin tab from the frame interface.
   //==============================================================
   
   public static synchronized void removeTab(int index)
   { 
      mainTabsPane.removeTabAt(index + 2);
      if ((loadedPluginModules.get(index)).getToolBar() != null)
         toolBarPanel.remove((loadedPluginModules.get(index)).getToolBar());
      loadedPluginModules.remove(index);
   }
   
   //==============================================================
   // Class Method to reload the DBTablesPanel. Essentially the
   // panel is left intact, static, just components cleared/reset
   // then redisplayed.
   //==============================================================
   
   public static void reloadDBTables()
   {
      // Method Instances
      Connection dbConnection;
      String currentSelectedTable;
      Iterator<Plugin_Module> pluginModulesIterator;
      
      // Create a connection, load the database tables again
      // then resetup the DBTablesPanel.
      
      dbConnection = ConnectionManager.getConnection("Main_Frame reloadDBTables()");
      
      if (dbConnection == null)
         return;
      
      try
      {
         // Save the current selected table so that it may
         // be shown again, if possible, after the reload.
         
         if (DBTablesPanel.getSelectedTableTabPanel() != null)
            currentSelectedTable = DBTablesPanel.getSelectedTableTabPanel().getTableName();
         else
            currentSelectedTable = "";
         
         // Reload Database Tables.
         ConnectionManager.loadDBTables(dbConnection);
         
         DBTablesPanel.reloadPanel(dbConnection, ConnectionManager.getTableNames());
         dbTablesPanel.repaint();
         
         // Reload Plugins' Tables.
         pluginModulesIterator = loadedPluginModules.iterator();
         while (pluginModulesIterator.hasNext())
         {
            Plugin_Module currentPlugin = pluginModulesIterator.next();
            currentPlugin.setDBTables(ConnectionManager.getTableNames());
         }
         
         // Try set the table showing before the reload.
         
         if (!currentSelectedTable.equals(""))
            DBTablesPanel.setSelectedTableTabPanel(currentSelectedTable);
         
         mainTabsPane.validate();
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "Main_Frame reloadDBTables()");
      }
      
      ConnectionManager.closeConnection(dbConnection, "Main_Frame reloadDBTables()");
   }
   
   //==============================================================
   // Class Method to load the Query Buckets default last saved
   // list on closing. Based on database connection.
   //==============================================================
   
   public void loadQueryBucketList()
   {
      // Thread the setup of the Query Bucket.
      Thread loadQueryBucketList = new Thread(new Runnable()
      {
         public void run()
         {
            sqlQueryBucketFrame.setSize(600, 450);
            sqlQueryBucketFrame.setResizable(true);
            sqlQueryBucketFrame.center();
            sqlQueryBucketFrame.openLastUsedList(ConnectionManager.getConnectionProperties()
               .getProperty(ConnectionProperties.DB));
         }
      }, "Main_Frame.loadQueryBucketList");
      loadQueryBucketList.start();
   }
   
   //==============================================================
   // Class Method to return the current loaded plugins.
   //==============================================================
   
   public static ArrayList<Plugin_Module> getPlugins()
   {
      return loadedPluginModules;
   }
   
   //==============================================================
   // Class Method to return the SQL Bucket Frame.
   //==============================================================
   
   public static SQLQueryBucketFrame getSQLBucket()
   {
      return sqlQueryBucketFrame;
   }
   
   //==============================================================
   // Class Method to set the Frame and its children's font size.
   // Generally will propagate through, but should restart for full
   // changes to take place.
   //==============================================================
   
   protected void setFontSize(int fontSize)
   {
      Ajqvue.getGeneralProperties().setFontSize(fontSize);
      Utils.setUIManagerFont(fontSize);
      
      try
      {
         SwingUtilities.updateComponentTreeUI(this);
         SwingUtilities.updateComponentTreeUI(menuBar);
      }
      catch (Exception e)
      {
         System.err.println("Failed to update UI Tree.");
      }
   }
}
