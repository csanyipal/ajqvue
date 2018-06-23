//=================================================================
//                   QueryFrame Class
//=================================================================
//   This class is used to provide a framework to execute
// queries on the current selected host by the user that
// has a connection established in the application.
//
//                  << QueryFrame.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.4 06/23/2018
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
// Version 1.0 09/20/2016 Production QueryFrame Class.
//         1.1 08/14/2017 Method actionPerformed() Action EDITPREFERENCES_TABLE_ROWS
//                        Removed Conditional Check for queryTabsPane Selected
//                        Component != Null. Checked in setRowPreferences(). Allows
//                        Change in Fetch Row Size Before Any Tabs Present.
//         1.2 05/31/2018 Method exportData() Changed Instance tableColumnTypeHashMap
//                        to tableColumnTypeNameHashMap.
//         1.3 06/22/2018 Removed QueryTabPanel Option.
//         1.4 06/23/2018 Method exportData() CSVQueryDataDumpThread Constructor
//                        Argument queryString Removed Semicolons.
//
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultEditorKit;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.ConnectionProperties;
import com.dandymadeproductions.ajqvue.gui.panels.SQLTabPanel;
import com.dandymadeproductions.ajqvue.io.CSVDataTableDumpThread;
import com.dandymadeproductions.ajqvue.io.CSVQueryDataDumpThread;
import com.dandymadeproductions.ajqvue.io.PDFDataTableDumpThread;
import com.dandymadeproductions.ajqvue.io.WriteDataFile;
import com.dandymadeproductions.ajqvue.utilities.InputDialog;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;
import com.dandymadeproductions.ajqvue.utilities.TableClearingThread;

//=================================================================
//                        QueryFrame
//=================================================================

/**
 *    The QueryFrame class is used to provide a framework to execute
 * queries on the current selected host by the user that has a
 * connection established in the application.
 * 
 * @author Dana M. Proctor
 * @version 1.4 06/23/2018
 */

public class QueryFrame extends JFrame implements ActionListener, ChangeListener
{
   // =============================================
   // Creation of the necessary class instance
   // variables for the QueryFrame.
   // =============================================

   private static final long serialVersionUID = 8893658072886277975L;
   
   private static JTabbedPane queryTabsPane = new JTabbedPane();
   private JPanel tabPanel;

   private static final int maxTabs = 50;
   private int currentQueryIndex;
   private int workingQueryIndex;
   private int oldQueryIndex;
   private int queryCount;
   private boolean clearingTabs, newTabState; 

   private JCheckBoxMenuItem showQueryCheckBox;
   private transient Connection query_dbConnection;

   private ImageIcon statusIdleIcon, statusWorkingIcon;
   private ImageIcon[] statusIndicatorIcon = new ImageIcon[maxTabs];
   private JLabel statusIndicator;
   private String[] status = new String[maxTabs];
   private JTextField statusLabel;
   private JButton sqlQueryBucketButton;
   private JComboBox<Object> statementTypeComboBox;
   private int[] tabStatementType = new int[maxTabs];
   
   private JTextArea queryTextArea;
   private String[] queryTextAreaData = new String[maxTabs];
   
   private int[] summaryTableRowSize = new int[maxTabs];
   
   private JButton executeButton, removeTabButton;
   private JCheckBox newTabCheckBox;
   private static JTextArea queryResultTextArea = new JTextArea(4, 40);
   private AResourceBundle resourceBundle;
   private String resourceAlert, resourceFileNOTFound;
   private String dataSourceType, fileSeparator, iconsDirectory;
   private String scriptLastDirectory, dataLastDirectory;
   
   private transient PrinterJob currentPrintJob = PrinterJob.getPrinterJob();
   private transient PageFormat mPageFormat = currentPrintJob.defaultPage();
   
   private static String FILE_OPEN_SCRIPT = "FOS";
   private static String FILE_SAVE_SCRIPT = "FSS";
   private static String FILE_PRINT = "FP";
   private static String FILE_PAGE_FORMAT = "FPG";
   private static String FILE_EXIT = "FE";
   
   private static String EDITPREFERENCES_TABLE_ROWS = "EPTR";
   
   private static String DATAEXPORT_CSV_QUERY = "DECSVQ";
   private static String DATAEXPORT_CSV_SUMMARY_TABLE = "DECSVST";
   private static String DATAEXPORT_PDF_SUMMARY_TABLE = "DEPDFST";

   //==============================================================
   // QueryFrame Constructor
   //==============================================================

   public QueryFrame(Main_Frame parent)
   {
      // Constructor Instances.
      JMenuBar queryFrameMenuBar;
      JToolBar queryFrameToolBar;
      
      JPanel framePanel, mainPanel;
      JSplitPane querySplitPane;
      JPanel queryPanel, centerPanel, queryResultPanel;
      JPanel statusControlPanel, statusPanel;
      
      ImageIcon sqlQueryBucketIcon, removeTabIcon;
      ConnectionProperties connectionProperties;
      String hostName, databaseName, resource;
      
      // Setting up title, and other instances.
      
      resourceBundle = Ajqvue.getResourceBundle();
      
      connectionProperties = ConnectionManager.getConnectionProperties();
      dataSourceType = ConnectionManager.getDataSourceType();
      hostName = connectionProperties.getProperty(ConnectionProperties.HOST);
      databaseName = connectionProperties.getProperty(ConnectionProperties.DB);
      
      resource = resourceBundle.getResourceString("QueryFrame.message.Title", "Query Frame");
      setTitle("Ajqvue " + resource + "   " + hostName + ":" + databaseName);
      setIconImage(Utils.getFrameIcon());
      
      resource = resourceBundle.getResourceString("QueryFrame.dialogtitle.Alert", "Alert");
      resourceAlert = resource;
      
      resource = resourceBundle.getResourceString("QueryFrame.dialogmessage.FileNOTFound",
                                                  "File NOT Found");
      resourceFileNOTFound = resource;
      
      currentQueryIndex = 0;
      workingQueryIndex = 0;
      oldQueryIndex = currentQueryIndex;
      queryCount = 0;
      clearingTabs = false;
      newTabState = true;
      
      fileSeparator = Utils.getFileSeparator();
      iconsDirectory = Utils.getIconsDirectory() + fileSeparator;
      scriptLastDirectory = "";
      dataLastDirectory = "";
      
      statusIdleIcon = resourceBundle.getResourceImage(iconsDirectory + "statusIdleIcon.png");
      statusWorkingIcon = resourceBundle.getResourceImage(iconsDirectory + "statusWorkingIcon.png");
      sqlQueryBucketIcon = resourceBundle.getResourceImage(iconsDirectory + "addSQLQueryIcon.png");
      removeTabIcon = resourceBundle.getResourceImage(iconsDirectory + "removeTabIcon.png");

      for (int i = 0; i < maxTabs; i++)
      {
         statusIndicatorIcon[i] = statusIdleIcon;
         status[i] = "Idle";
         summaryTableRowSize[i] = 50;
      }

      // Setting up a connection.
      query_dbConnection = ConnectionManager.getConnection("QueryFrame");
      
      //==================================================
      // Frame Window Closing Addition. Also method for
      // reactivating if desired/needed.
      //==================================================
      
      addWindowListener(new WindowAdapter()
      {
         public void windowClosing(WindowEvent e)
         {
            // Remove Memory/Temporary Table(s) for HSQL & Oracle
            if (dataSourceType.indexOf(ConnectionManager.HSQL) != -1
                || dataSourceType.equals(ConnectionManager.ORACLE))
            {
               Thread clearTables = new Thread(new TableClearingThread(queryCount), "TableClearingThread");
               clearTables.start();
            }

            // Clear out any query tab panes.
            clearingTabs = true;
            queryTabsPane.removeAll();
            
            // Clear Feedback Text Area.
            queryResultTextArea.setText("");
            
            // Save Frame Size & Position
            Ajqvue.getGeneralProperties().setQueryFramePosition(new Point(getX(), getY()));
            Ajqvue.getGeneralProperties().setQueryFrameDimension(getSize());

            // Close Connection
            if (query_dbConnection != null)
               ConnectionManager.closeConnection(query_dbConnection, "QueryFrame");
            Main_JMenuBarActions.setQueryFrameNotVisisble();
            dispose();
         }

         public void windowActivated(WindowEvent e)
         {
         }
      });

      // ===============================================
      // JMenu Bar for the Frame.
      // ===============================================

      queryFrameMenuBar = new JMenuBar();
      queryFrameMenuBar.setBorder(BorderFactory.createEtchedBorder());
      createMenuBar(parent, queryFrameMenuBar);
      setJMenuBar(queryFrameMenuBar);
      
      // ===============================================
      // JTool Bar for the Frame.
      // ===============================================
      
      queryFrameToolBar = new JToolBar("Ajqvue QueryFrame ToolBar");
      queryFrameToolBar.setBorder(BorderFactory.createLoweredBevelBorder());
      createToolBar(parent, queryFrameToolBar);

      // ===============================================
      // Setting up the various panels that are used in
      // the QueryFrame
      // ===============================================
      
      framePanel = new JPanel(new BorderLayout());
      framePanel.add(queryFrameToolBar, BorderLayout.PAGE_START);

      mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createLoweredBevelBorder());
      
      querySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      querySplitPane.setBorder(BorderFactory.createEmptyBorder());
      querySplitPane.setOneTouchExpandable(true);

      // =====================================
      // QueryFrame SQL Entry Text Area

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();
      
      queryPanel = new JPanel(new BorderLayout());
      queryPanel.setBorder(BorderFactory.createLoweredBevelBorder());
      
      statusControlPanel = new JPanel(gridbag);
      statusControlPanel.setBorder(BorderFactory.createRaisedBevelBorder());
      
      // Status Indicator
      statusPanel = new JPanel(gridbag);
      statusPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
                                                               BorderFactory.createEmptyBorder(5, 5, 5, 5)));
      
      statusIndicator = new JLabel("", JLabel.LEFT);
      statusIndicator.setIcon(statusIdleIcon);
      statusIndicator.setDisabledIcon(statusWorkingIcon);
      statusIndicator.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
      
      buildConstraints(constraints, 0, 0, 1, 1, 15, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(statusIndicator, constraints);
      statusPanel.add(statusIndicator);
      
      statusLabel = new JTextField("Idle", 10);
      statusLabel.setHorizontalAlignment(JTextField.LEFT);
      statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      statusLabel.setEditable(false);
      
      buildConstraints(constraints, 1, 0, 1, 1, 85, 100);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(statusLabel, constraints);
      statusPanel.add(statusLabel);
      
      buildConstraints(constraints, 0, 0, 1, 1, 6, 100);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(statusPanel, constraints);
      statusControlPanel.add(statusPanel);
      
      // SQL Query Bucket Drop Button.
      sqlQueryBucketButton = new JButton(sqlQueryBucketIcon);
      sqlQueryBucketButton.setMargin(new Insets(0, 0, 0, 0));
      sqlQueryBucketButton.setBorder(BorderFactory.createCompoundBorder(
                                                    BorderFactory.createRaisedBevelBorder(),
                                                    sqlQueryBucketButton.getBorder()));
      sqlQueryBucketButton.setFocusPainted(false);
      sqlQueryBucketButton.addActionListener(this);
      
      buildConstraints(constraints, 1, 0, 1, 1, 1, 100);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(sqlQueryBucketButton, constraints);
      statusControlPanel.add(sqlQueryBucketButton);
      
      // SQL Query Type
      statementTypeComboBox = new JComboBox<Object>();
      statementTypeComboBox.setBorder(BorderFactory.createRaisedBevelBorder());
      
      // SQLTabPanel.
      resource = resourceBundle.getResourceString("QueryFrame.combobox.SQLStatement", "SQL Statement");
      statementTypeComboBox.addItem(resource + " : ");
      
      buildConstraints(constraints, 2, 0, 1, 1, 72, 100);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(statementTypeComboBox, constraints);
      statusControlPanel.add(statementTypeComboBox);
      
      // New Tab Selector
      resource = resourceBundle.getResourceString("QueryFrame.checkbox.NewTab", "New Tab");
      newTabCheckBox = new JCheckBox(resource, true);
      
      newTabCheckBox.setIcon(resourceBundle.getResourceImage(iconsDirectory + "limitUpIcon.png"));
      newTabCheckBox.setSelectedIcon(resourceBundle.getResourceImage(iconsDirectory + "limitDownIcon.png"));
      newTabCheckBox.setMargin(new Insets(4, 1, 4, 1));
      newTabCheckBox.setBorder(BorderFactory.createRaisedBevelBorder());
      newTabCheckBox.setFocusPainted(false);
      
      buildConstraints(constraints, 3, 0, 1, 1, 10, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(newTabCheckBox, constraints);
      statusControlPanel.add(newTabCheckBox);
      
      // Execute Button
      resource = resourceBundle.getResourceString("QueryFrame.button.Execute", "Execute");
      executeButton = new JButton(resource);
      executeButton.setFocusPainted(false);
      executeButton.setMnemonic(KeyEvent.VK_ENTER);
      executeButton.addActionListener(this);
      
      buildConstraints(constraints, 4, 0, 1, 1, 10, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(executeButton, constraints);
      statusControlPanel.add(executeButton);
      
      // Remove Tab Button
      removeTabButton = new JButton(removeTabIcon);
      removeTabButton.setMargin(new Insets(0, 0, 0, 0));
      removeTabButton.setBorder(BorderFactory.createCompoundBorder(
                                                    BorderFactory.createRaisedBevelBorder(),
                                                    removeTabButton.getBorder()));
      removeTabButton.setFocusPainted(false);
      removeTabButton.setEnabled(false);
      removeTabButton.addActionListener(this);
      
      buildConstraints(constraints, 5, 0, 1, 1, 1, 100);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.anchor = GridBagConstraints.EAST;
      gridbag.setConstraints(removeTabButton, constraints);
      statusControlPanel.add(removeTabButton);
      
      queryPanel.add(statusControlPanel, BorderLayout.NORTH);
      
      // Query Entry Area
      queryTextArea = new JTextArea(5, 40);
      queryTextArea.setBorder(BorderFactory.createLoweredBevelBorder());
      queryTextArea.setLineWrap(true);
      queryTextArea.setDragEnabled(true);
      queryTextArea.addMouseListener(Ajqvue.getPopupMenuListener());
      
      JScrollPane queryScrollPane = new JScrollPane(queryTextArea);
      queryPanel.add(queryScrollPane, BorderLayout.CENTER);
      
      querySplitPane.setTopComponent(queryPanel);

      // =====================================
      // QueryFrame Resultant Data Set Panel

      centerPanel = new JPanel(new GridLayout(1, 1, 0, 0));
      centerPanel.setBorder(BorderFactory.createEtchedBorder());

      queryTabsPane.setTabPlacement(JTabbedPane.TOP);
      queryTabsPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      queryTabsPane.addChangeListener(this);
      centerPanel.add(queryTabsPane);

      querySplitPane.setBottomComponent(centerPanel);
      mainPanel.add(querySplitPane, BorderLayout.CENTER);

      // =====================================
      // QueryFrame SQL Feedback TextArea.

      queryResultPanel = new JPanel(new GridLayout(1, 1, 0, 0));
      queryResultPanel.setBorder(BorderFactory.createEtchedBorder());

      queryResultTextArea.setBorder(BorderFactory.createLoweredBevelBorder());
      queryResultTextArea.setLineWrap(true);
      queryResultTextArea.addMouseListener(Ajqvue.getPopupMenuListener());

      JScrollPane resultScrollPane = new JScrollPane(queryResultTextArea);
      queryResultPanel.add(resultScrollPane);

      mainPanel.add(queryResultPanel, BorderLayout.SOUTH);
      
      framePanel.add(mainPanel, BorderLayout.CENTER);

      getContentPane().add(framePanel);

      queryTextArea.requestFocus();
   }

   //==============================================================
   // ActionEvent Listener method for detecting the user's selection
   // of a Menu Bar item. Upon detection an action is the completed
   // by calling the Main_JMenuBarActions class. Created to reduce
   // clutter in this top level main class.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object panelSource = evt.getSource();

      // SQL Query Bucket Drop
      if (panelSource == sqlQueryBucketButton)
      {
         if (!queryTextArea.getText().isEmpty())
            Main_Frame.getSQLBucket().addSQLStatement(new StringBuffer(queryTextArea.getText()));
         return;
      }
      
      // Execute SQL Action
      if (panelSource == executeButton)
      {
         queryTabsPane.removeChangeListener(this);
         
         executeSQL();
         
         queryTabsPane.addChangeListener(this);
         newTabCheckBox.setSelected(false);
         removeTabButton.setEnabled(true);
         return;
      }
      
      // Remove Tab Action
      if (panelSource == removeTabButton)
      {
         if (queryTabsPane.getTabCount() >= 2)
            queryTabsPane.remove(queryTabsPane.getSelectedIndex()); 
         return;
      }

      // MenuBar Actions
      if (panelSource instanceof JMenuItem || panelSource instanceof JButton)
      {
         // Instances & Setting Up.
         String actionCommand;

         if (panelSource instanceof JMenuItem)
         {
            JMenuItem item = (JMenuItem) panelSource;
            actionCommand = item.getActionCommand();
         }
         else
         {
            JButton item = (JButton) panelSource;
            actionCommand = item.getActionCommand();
         }

         // ==================================
         // File Menu Item Selection Routing
         // ==================================

         // Open Script
         if (actionCommand.equals(FILE_OPEN_SCRIPT))
         {
            openScriptFile();
         }

         // Save Script
         if (actionCommand.equals(FILE_SAVE_SCRIPT))
         {  
            saveScriptFile();
         }

         // Print
         if (actionCommand.equals(FILE_PRINT))
         {
            printData();
         }

         // Print PageFormat Dialog
         if (actionCommand.equals(FILE_PAGE_FORMAT))
         {
            PrinterJob pj = PrinterJob.getPrinterJob();
            mPageFormat = pj.pageDialog(mPageFormat);
         }

         // Exit
         if (actionCommand.equals(FILE_EXIT))
         {
            // Remove Memory/Temp Table(s) for HSQL & Oracle
            if (dataSourceType.indexOf(ConnectionManager.HSQL) != -1
                || dataSourceType.equals(ConnectionManager.ORACLE))
            {
               Thread tableClearingThread = new Thread(new TableClearingThread(queryTabsPane.getTabCount()),
                                                       "TableClearingThread");
               tableClearingThread.start();
            }

            // Clear out any query tab panes.
            clearingTabs = true;
            queryTabsPane.removeAll();
            
            // Clear Feedback Text Area.
            queryResultTextArea.setText("");
            
            // Save Frame Size & Position
            Ajqvue.getGeneralProperties().setQueryFramePosition(new Point(getX(), getY()));
            Ajqvue.getGeneralProperties().setQueryFrameDimension(getSize());

            // Close connection and dispose.
            if (query_dbConnection != null)
               ConnectionManager.closeConnection(query_dbConnection, "QueryFrame");
            Main_JMenuBarActions.setQueryFrameNotVisisble();
            dispose();
         }

         // ==================================
         // Edit Menu Item Selection Routing
         // ==================================

         // Table Row Preferences
         if (actionCommand.equals(EDITPREFERENCES_TABLE_ROWS))
         {
            setRowPreferences();
         }

         // ==================================
         // Data Menu Item Selection Routing
         // ==================================

         // Data Export
         if (getSelectedTab() != null
             && (actionCommand.indexOf("DECSV") != -1 || actionCommand.indexOf("DEPDF") != -1
                 || actionCommand.indexOf("DESQL") != -1))
         {
            exportData(actionCommand);
         }
      }
   }

   //==============================================================
   // ChangeEvent Listener method for detecting the user's selection
   // of the JTabbedPane. Used to load the querytext field for
   // selected pane and keep track of the current active pane.
   //==============================================================

   public void stateChanged(ChangeEvent evt)
   {
      Object panelSource = evt.getSource();

      if (panelSource instanceof JTabbedPane && !clearingTabs)
      {
         // Set the query index for the pane.
         oldQueryIndex = currentQueryIndex;
         currentQueryIndex = Integer.parseInt(getSelectedTabTitle());

         // Set the tabs various paramerters.
         if (oldQueryIndex != currentQueryIndex)
         {
            queryTextArea.setText(queryTextAreaData[currentQueryIndex]);
            statementTypeComboBox.setSelectedIndex(tabStatementType[currentQueryIndex]);
            statusIndicator.setIcon(statusIndicatorIcon[currentQueryIndex]);
            statusLabel.setText(status[currentQueryIndex]);
         }
         // System.out.println("tab changed: " + currentQueryIndex);
      }
   }
   
   //==============================================================
   // Class method used to execute the given SQL input by the user
   // as defined in the query text area.
   //==============================================================

   private void executeSQL()
   {
      if (query_dbConnection != null)
      {
         // Lets clear any left over query errors.
         queryResultTextArea.setText("");

         // Get tab index to use.
         if (newTabCheckBox.isSelected())
         {
            oldQueryIndex = currentQueryIndex;
            currentQueryIndex = queryCount;
            newTabState = true;
         }
         else
            newTabState = false;
         
         // Save query text, statement type, and status.
         queryTextAreaData[currentQueryIndex] = queryTextArea.getText();
         tabStatementType[currentQueryIndex] = statementTypeComboBox.getSelectedIndex();
         workingQueryIndex = currentQueryIndex;
         
         // Set Status
         statusIndicator.setIcon(statusWorkingIcon);
         statusIndicatorIcon[currentQueryIndex] = statusWorkingIcon;
         statusLabel.setText("Working");
         status[currentQueryIndex] = "Working";
         
         // Create a thread to create the appropriate
         // panel that will be used to run the SQL.
         
         Thread executeSQLThread = new Thread(new Runnable()
         {
            boolean isNewTab = newTabState;
            
            public void run()
            {
               // SQL Statement
               
               tabPanel = new SQLTabPanel(queryTextArea.getText(),
                                          summaryTableRowSize[workingQueryIndex],
                                          resourceBundle);
               
               if (isNewTab)
                  queryTabsPane.addTab(workingQueryIndex + "", tabPanel);
               else
                  queryTabsPane.setComponentAt(queryTabsPane.getSelectedIndex(), tabPanel);
               
               // Show tab and return status to idle.
               queryTabsPane.setSelectedIndex(queryTabsPane.indexOfTab(workingQueryIndex + ""));
               statusIndicator.setIcon(statusIdleIcon);
               statusIndicatorIcon[workingQueryIndex] = statusIdleIcon;
               statusLabel.setText("Idle");
               status[currentQueryIndex] = "Idle";      
            }
         }, "QueryFrame.executeSQLThread");
         executeSQLThread.start();
         queryCount++;
      }
   }
   
   //==============================================================
   // Class method used to handle the loading of a SQL script file.
   // Note: The script file is limited to importing only 100 lines.
   //==============================================================

   private void openScriptFile()
   {
      // Method Instances.
      JFileChooser openScriptFileChooser;
      int fileChooserResult;
      int scriptLineLimit = 100;
      String fileName, resource, message;
      
      FileReader fileReader;
      BufferedReader bufferedReader;
      
      // Choosing the file to import data from.
      
      if (scriptLastDirectory.equals(""))
         openScriptFileChooser = new JFileChooser();
      else
         openScriptFileChooser = new JFileChooser(new File(scriptLastDirectory));
      
      fileChooserResult = openScriptFileChooser.showOpenDialog(null);

      // Looks like might be good so lets check and write data.
      if (fileChooserResult == JFileChooser.APPROVE_OPTION)
      {
         scriptLastDirectory = openScriptFileChooser.getCurrentDirectory().toString();
         fileName = openScriptFileChooser.getSelectedFile().getName();
         fileName = openScriptFileChooser.getCurrentDirectory() + fileSeparator + fileName;
         // System.out.println(fileName);

         if (!fileName.equals(""))
         {
            fileReader = null;
            bufferedReader = null;
            
            try
            {
               fileReader = new FileReader(fileName);
               bufferedReader = new BufferedReader(fileReader);
               String currentLine;

               int lineNumber = 1;
               queryTextArea.setText("");

               while ((currentLine = bufferedReader.readLine()) != null && lineNumber < scriptLineLimit)
               {
                  queryTextArea.append(currentLine);
                  lineNumber++;
               }
            }
            catch (IOException e)
            {
               resource = resourceBundle.getResourceString("QueryFrame.dialogmessage.InputFile",
                                                           "Unalbe to Read Input File");
               message = resource; 
               JOptionPane.showMessageDialog(null, message, resourceAlert, JOptionPane.ERROR_MESSAGE);
            }
            finally
            {
               try
               {
                  if (bufferedReader != null)
                     bufferedReader.close();
               }
               catch (IOException ioe)
               {
                  if (Ajqvue.getDebug())
                     System.out.println("QueryFrame openScriptFile() Failed to Close BufferedReader. "
                                        + ioe);
               }
               finally
               {
                  try
                  {
                     if (fileReader != null)
                        fileReader.close();
                  }
                  catch (IOException ioe)
                  {
                     if (Ajqvue.getDebug())
                        System.out.println("QueryFrame openScriptFile() Failed to Close FileReader. "
                                           + ioe);
                  }
               }
            }
         }
         else
         {
            JOptionPane.showMessageDialog(null, resourceFileNOTFound, resourceAlert,
                                          JOptionPane.ERROR_MESSAGE);
         }
      } 
   }
   
   //==============================================================
   // Class method used to handle the saving of a script file.
   //==============================================================

   private void saveScriptFile()
   {
      // Method Instances.
      JFileChooser saveScriptFileChooser;
      int fileChooserResult;
      String fileName;
      
      // Setup a file chooser and showing.
      
      if (scriptLastDirectory.equals(""))
         saveScriptFileChooser = new JFileChooser();
      else
         saveScriptFileChooser = new JFileChooser(new File(scriptLastDirectory));
         
      fileChooserResult = Utils.processFileChooserSelection(this, saveScriptFileChooser);

      // Looks like might be good so lets check and then write data.
      
      if (fileChooserResult == JFileChooser.APPROVE_OPTION)
      {
         scriptLastDirectory = saveScriptFileChooser.getCurrentDirectory().toString();
         fileName = saveScriptFileChooser.getSelectedFile().getName();
         fileName = saveScriptFileChooser.getCurrentDirectory() + fileSeparator + fileName;
         // System.out.println(fileName);

         if (!fileName.equals(""))
         {
            WriteDataFile.mainWriteDataString(fileName, ((String) queryTextArea.getText()).getBytes(),
               false);
         }
      }
   }
   
   //==============================================================
   // Class method used print the summary table data from the
   // current showing tab.
   //==============================================================

   private void printData()
   {
      // Method Instances.
      Paper customPaper;
      double margin;
      String resource, message;
      
      // Setting up the printing.

      customPaper = new Paper();
      margin = 36;
      customPaper.setImageableArea(margin, margin, customPaper.getWidth() - margin * 2,
                                   customPaper.getHeight() - margin * 2);
      mPageFormat.setPaper(customPaper);

      // Printing the selected Tab
      if (getSelectedTab() != null)
      {  
         currentPrintJob.setPrintable(((SQLTabPanel) getSelectedTab()), mPageFormat);

         // Should have graphics to print now so lets try to print.
         
         if (currentPrintJob.printDialog())
         {
            try
            {
               currentPrintJob.print();
            }
            catch (PrinterException e)
            {
               resource = resourceBundle.getResourceString("QueryFrame.dialogmessage.PrinterException",
                                                           "Printer Exception");
               message = resource;
               
               JOptionPane.showMessageDialog(null, message, e.getMessage(),
                                             JOptionPane.ERROR_MESSAGE);
            }
         }
      }
   }
   
   //==============================================================
   // Class method used set the current visible tab data panel's
   // maxium number of returned rows from the result set.
   //==============================================================

   private void setRowPreferences()
   {
      // Method Instances.
      String resource, resourceOK, resourceCancel;
      String message;
      
      // Setup and display a option pane to collect the new
      // summary table row size.

      JTextField rowSizeTextField;
      JLabel warning, warningMessage1, warningMessage2;
      
      rowSizeTextField = new JTextField();
      rowSizeTextField.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
         BorderFactory.createLoweredBevelBorder()));
      if (currentQueryIndex <= summaryTableRowSize.length)
         rowSizeTextField.setText(Integer.toString(summaryTableRowSize[currentQueryIndex]));
      
      resource = resourceBundle.getResourceString("QueryFrame.label.Warning", "Warning!");
      warning = new JLabel(resource, JLabel.CENTER);
      warning.setForeground(Color.RED);
      
      resource = resourceBundle.getResourceString("QueryFrame.label.LargeRowSize",
                                                  "A large row size may adversely effect");
      warningMessage1 = new JLabel(resource, JLabel.CENTER);
      
      resource = resourceBundle.getResourceString("QueryFrame.label.ApplicationServerPerformance",
                                                  "application/server performance");
      warningMessage2 = new JLabel(resource, JLabel.CENTER);

      Object content[] = {warning, warningMessage1, warningMessage2, rowSizeTextField};

      resource = resourceBundle.getResourceString("QueryFrame.label.SetSummaryTableRowSize",
                                                  "Set Summary Table Row Size");
      resourceOK = resourceBundle.getResourceString("QueryFrame.button.OK", "OK");
      resourceCancel = resourceBundle.getResourceString("QueryFrame.button.Cancel", "Cancel");
      
      InputDialog rowSizeDialog = new InputDialog(null, resource, resourceOK, resourceCancel,
                                                  content, null);
      rowSizeDialog.pack();
      rowSizeDialog.setResizable(false);
      rowSizeDialog.center();
      rowSizeDialog.setVisible(true);

      // Collect the new row size input and updating
      // the current selected summary table.

      if (rowSizeDialog.isActionResult())
      {
         try
         {
            summaryTableRowSize[currentQueryIndex] = Integer.parseInt(rowSizeTextField.getText());
            
            JPanel currentTab = (JPanel) queryTabsPane.getSelectedComponent();
            
            // SQL Statement
            if (currentTab != null && currentTab instanceof SQLTabPanel)
            {
               queryTabsPane.removeChangeListener(this);
               executeSQL();
               queryTabsPane.addChangeListener(this);
            }
            
            else { /* Something not right. */}
         }
         catch (NumberFormatException e)
         {
            message = resourceBundle.getResourceString("QueryFrame.dialogmessage.RowSize",
               "The Row Size Input Appears To NOT Be A Valid Integer!");
            
            JOptionPane.showMessageDialog(null, message, resourceAlert, JOptionPane.ERROR_MESSAGE);
         }
      }
      
      rowSizeDialog.dispose();
   }
   
   //==============================================================
   // Class method to execute the selected type of data export of
   // the data in summary table of the current visible tab.
   //==============================================================

   private void exportData(String actionCommand)
   {
      // Method Instances
      JFileChooser dataExportFileChooser;
      SimpleDateFormat dateFormat;
      String fileName, exportedTable;
      int fileChooserResult;
      
      HashMap<String, String> tableColumnNamesHashMap = new HashMap <String, String>();
      HashMap<String, String> tableColumnTypeNameHashMap = new HashMap <String, String>();

      // Creating and showing a file chooser based on a default file name
      // derived from the table and date.

      if (dataLastDirectory.equals(""))
         dataExportFileChooser = new JFileChooser();
      else
         dataExportFileChooser = new JFileChooser(new File(dataLastDirectory));
      
      dateFormat = new SimpleDateFormat("yyyyMMdd");
      
      exportedTable = "sqlData" + getSelectedTabTitle();

      fileName = exportedTable + "_" + dateFormat.format(new Date());

      if (actionCommand.indexOf("DECSV") != -1)
         fileName += ".csv";
      else if (actionCommand.indexOf("DEPDF") != -1)
         fileName += ".pdf";
      else
         fileName += ".sql";

      dataExportFileChooser.setSelectedFile(new File(fileName));

      fileChooserResult = Utils.processFileChooserSelection(this, dataExportFileChooser);

      // Looks like might be good so lets check and then write data.
      if (fileChooserResult == JFileChooser.APPROVE_OPTION)
      {
         dataLastDirectory = dataExportFileChooser.getCurrentDirectory().toString();
         fileName = dataExportFileChooser.getSelectedFile().getName();
         fileName = dataExportFileChooser.getCurrentDirectory() + fileSeparator + fileName;
         // System.out.println(fileName);

         if (!fileName.equals(""))
         {
            //ArrayList<String> columnNameFields = new ArrayList <String>();
            
            if (actionCommand.indexOf("DECSV") != -1 || actionCommand.indexOf("DESQL") != -1)
            {
               tableColumnNamesHashMap = ((SQLTabPanel) getSelectedTab()).getColumnNamesHashMap();
               tableColumnTypeNameHashMap = ((SQLTabPanel) getSelectedTab()).getColumnTypeNameHashMap();
            }

            // Data Export CVS Query
            if (actionCommand.equals(DATAEXPORT_CSV_QUERY))
            {
               Thread csvQueryDataTableDumpThread = new Thread(
                  new CSVQueryDataDumpThread(query_dbConnection, queryTextArea.getText().replaceAll(";", ""),
                                             fileName, true, true));
               
               csvQueryDataTableDumpThread.start();
            }

            // Data Export CVS & PDF Tab Summary Table
            else if (actionCommand.equals(DATAEXPORT_CSV_SUMMARY_TABLE) ||
                  actionCommand.equals(DATAEXPORT_PDF_SUMMARY_TABLE))
            {
               JTable summaryListTable;
               
               summaryListTable = ((SQLTabPanel) getSelectedTab()).getListTable();
               
               if (summaryListTable != null)
               {
                  if (actionCommand.equals(DATAEXPORT_CSV_SUMMARY_TABLE))
                  {
                     Thread csvDataTableDumpThread = new Thread(new CSVDataTableDumpThread(summaryListTable,
                        tableColumnNamesHashMap, tableColumnTypeNameHashMap, exportedTable, fileName));
                     
                     csvDataTableDumpThread.start();
                  }
                  else
                  {
                     Thread pdfDataTableDumpThread = new Thread(new PDFDataTableDumpThread(summaryListTable,
                        tableColumnTypeNameHashMap, exportedTable, fileName));
                     
                     pdfDataTableDumpThread.start();
                  }
               }
            }
            
            /*
            // Data Export SQL Table
            else if (actionCommand.equals("DESQLT"))
            {
                SQLDataDumpThread sqlDump = new SQLDataDumpThread(columnNameFields,
                                                                  tableColumnNamesHashMap,
                                                                  false, tableColumnClassHashMap,
                                                                  tableColumnTypeHashMap,
                                                                  exportedTable, fileName,
                                                                  version);
            }
            
            // Data Export SQL Tab Summary Table
            else if (actionCommand.equals("DESQLTST"))
            {
                columnNameFields = new Vector();
                columnNameFields = (getSelectedTab()).getTableHeadings();
                SQLDataDumpThread sqlDump = new SQLDataDumpThread(columnNameFields,
                                                                  tableColumnNamesHashMap,
                                                                  true, tableColumnClassHashMap,
                                                                  tableColumnTypeHashMap,
                                                                  exportedTable, fileName,
                                                                  version);
             }
             */
         }
         else
         {
            JOptionPane.showMessageDialog(null, resourceFileNOTFound, resourceAlert,
                                          JOptionPane.ERROR_MESSAGE);
         }
      }
   }
   
   //==============================================================
   // Private method used for creation of the menu bar that will be
   // used with the frame.
   //==============================================================

   private void createMenuBar(Main_Frame mainFrame, JMenuBar queryFrameMenuBar)
   {
      // Method Instances
      String resource;
      ImageIcon logoIcon;
      JButton logoIconItem;
      
      JMenuItem menuItem = null;
      JMenu fileMenu, editMenu, dataMenu, toolsMenu;
      JMenu preferencesMenu, exportMenu, exportCVSMenu;

      // ===============
      // File Menu
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.File", "File");
      fileMenu = new JMenu(resource);
      fileMenu.setFont(fileMenu.getFont().deriveFont(Font.BOLD));
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.OpenScript", "Open Script");
      fileMenu.add(menuItem(resource, FILE_OPEN_SCRIPT));
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.SaveScript", "Save Script");
      fileMenu.add(menuItem(resource, FILE_SAVE_SCRIPT));
      fileMenu.addSeparator();
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.Print", "Print");
      fileMenu.add(menuItem(resource, FILE_PRINT));
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.PageFormat", "Page Format");
      fileMenu.add(menuItem(resource, FILE_PAGE_FORMAT));
      fileMenu.addSeparator();
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.Exit", "Exit");
      fileMenu.add(menuItem(resource, FILE_EXIT));
      queryFrameMenuBar.add(fileMenu);

      // ===============
      // Edit Menu
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.Edit", "Edit");
      editMenu = new JMenu(resource);
      editMenu.setFont(editMenu.getFont().deriveFont(Font.BOLD));
      
      menuItem = new JMenuItem(new DefaultEditorKit.CutAction());
      resource = resourceBundle.getResourceString("QueryFrame.menu.Cut", "Cut");
      menuItem.setText(resource + "          " + "Ctrl+x");
      menuItem.setMnemonic(KeyEvent.VK_X);
      editMenu.add(menuItem);

      menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
      resource = resourceBundle.getResourceString("QueryFrame.menu.Copy", "Copy");
      menuItem.setText(resource + "       " + "Ctrl+c");
      menuItem.setMnemonic(KeyEvent.VK_C);
      editMenu.add(menuItem);

      menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
      resource = resourceBundle.getResourceString("QueryFrame.menu.Paste", "Paste");
      menuItem.setText(resource + "       " + "Ctrl+v");
      menuItem.setMnemonic(KeyEvent.VK_V);
      editMenu.add(menuItem);

      editMenu.addSeparator();

      resource = resourceBundle.getResourceString("QueryFrame.menu.Preferences", "Preferences");
      preferencesMenu = new JMenu(resource);

      resource = resourceBundle.getResourceString("QueryFrame.menu.TableRows", "Table Rows");
      preferencesMenu.add(menuItem(resource, EDITPREFERENCES_TABLE_ROWS));

      resource = resourceBundle.getResourceString("QueryFrame.menu.ShowQuery", "Show Query");
      showQueryCheckBox = new JCheckBoxMenuItem(resource, false);
      preferencesMenu.add(showQueryCheckBox);

      editMenu.add(preferencesMenu);

      queryFrameMenuBar.add(editMenu);

      // ===============
      // Data Menu
      // Only Basic Export CVS Summary Table
      // in 2.72, 2.76. Reviewed Implementation
      // of CSV Table, SQL Table, SQL Table Tab Summary,
      // all these required the connection to be passed
      // to the dump threads. The temp tables are only
      // available to this class's connection.

      resource = resourceBundle.getResourceString("QueryFrame.menu.Data", "Data");
      dataMenu = new JMenu(resource);
      dataMenu.setFont(dataMenu.getFont().deriveFont(Font.BOLD));

      resource = resourceBundle.getResourceString("QueryFrame.menu.Export", "Export");
      exportMenu = new JMenu(resource);

      resource = resourceBundle.getResourceString("QueryFrame.menu.CSV", "CSV");
      exportCVSMenu = new JMenu(resource);
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.Query", "Query");
      exportCVSMenu.add(menuItem(resource, DATAEXPORT_CSV_QUERY));
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.SummaryTable", "Summary Table");
      exportCVSMenu.add(menuItem(resource, DATAEXPORT_CSV_SUMMARY_TABLE));
      exportMenu.add(exportCVSMenu);
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.PDF", "PDF");
      exportCVSMenu = new JMenu(resource);
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.SummaryTable", "Summary Table");
      exportCVSMenu.add(menuItem(resource, DATAEXPORT_PDF_SUMMARY_TABLE));
      exportMenu.add(exportCVSMenu);

      // JMenu exportSQLMenu = new JMenu("SQL");
      // exportSQLMenu.add(menuItem("Table", "DESQLT"));
      // exportSQLMenu.add(menuItem("Tab Summary Table", "DESQLTST"));
      // exportMenu.add(exportSQLMenu);

      dataMenu.add(exportMenu);
      queryFrameMenuBar.add(dataMenu);
      
      // ===============
      // Tools Menu
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.Tools", "Tools");
      toolsMenu = new JMenu(resource);
      toolsMenu.setFont(fileMenu.getFont().deriveFont(Font.BOLD));
      
      resource = resourceBundle.getResourceString("QueryFrame.menu.SQLQueryBucket", "SQL Query Bucket");
      menuItem = new JMenuItem(resource);
      menuItem.addActionListener(mainFrame);
      menuItem.setActionCommand(MenuActionCommands.ACTION_SQL_QUERY_BUCKET);
      toolsMenu.add(menuItem);

      queryFrameMenuBar.add(toolsMenu);
      queryFrameMenuBar.add(Box.createHorizontalGlue());
      
      // ===============
      // Logo
      
      logoIcon = resourceBundle.getResourceImage(iconsDirectory + "ajqvueIcon.gif");
      logoIconItem = new JButton(logoIcon);
      logoIconItem.setDisabledIcon(logoIcon);
      logoIconItem.setFocusPainted(false);
      logoIconItem.setBorder(BorderFactory.createLoweredBevelBorder());
      queryFrameMenuBar.add(logoIconItem);
   }
   
   //==============================================================
   // Private method used for creation of the tool bar that will be
   // used with the frame.
   //==============================================================

   private void createToolBar(Main_Frame mainFrame, JToolBar queryFrameToolBar)
   {
      // Method Instances
      String resource;
      
      ImageIcon printIcon, pageFormatIcon, exitIcon;
      ImageIcon openScriptIcon, saveScriptIcon, tableRowsIcon;
      ImageIcon csvExportTabSummaryTableIcon, pdfExportTabSummaryTableIcon;
      ImageIcon sqlQueryBucketIcon;
      JButton buttonItem;
      
      // ===============
      // File Menu
      
      // Open Script
      openScriptIcon = resourceBundle.getResourceImage(iconsDirectory + "openScriptIcon_20x20.png");
      resource = resourceBundle.getResourceString("QueryFrame.tooltip.OpenScript", "Open Script");
      buttonItem = buttonItem(resource, openScriptIcon, FILE_OPEN_SCRIPT);
      queryFrameToolBar.add(buttonItem);
      
      // Save Script
      saveScriptIcon = resourceBundle.getResourceImage(iconsDirectory + "saveScriptIcon_20x20.png");
      resource = resourceBundle.getResourceString("QueryFrame.tooltip.SaveScript", "Save Script");
      buttonItem = buttonItem(resource, saveScriptIcon, FILE_SAVE_SCRIPT);
      queryFrameToolBar.add(buttonItem);
      
      // File Print
      printIcon = resourceBundle.getResourceImage(iconsDirectory + "printIcon_20x20.png");
      resource = resourceBundle.getResourceString("QueryFrame.tooltip.Print", "Print");
      buttonItem = buttonItem(resource, printIcon, FILE_PRINT);
      queryFrameToolBar.add(buttonItem);
      
      // Page Format
      pageFormatIcon = resourceBundle.getResourceImage(iconsDirectory + "pageFormatIcon_20x20.png");
      resource = resourceBundle.getResourceString("QueryFrame.tooltip.PageFormat", "Page Format");
      buttonItem = buttonItem(resource, pageFormatIcon, FILE_PAGE_FORMAT);
      queryFrameToolBar.add(buttonItem);
      
      // Exit
      exitIcon = resourceBundle.getResourceImage(iconsDirectory + "exitIcon_20x20.png");
      resource = resourceBundle.getResourceString("QueryFrame.tooltip.Exit", "Exit");
      buttonItem = buttonItem(resource, exitIcon, FILE_EXIT);
      queryFrameToolBar.add(buttonItem);
      
      queryFrameToolBar.addSeparator();
      
      // ===============
      // Edit Menu
      
      // Preferences Table Rows
      tableRowsIcon = resourceBundle.getResourceImage(iconsDirectory + "tableRowsIcon_20x20.png");
      resource = resourceBundle.getResourceString("QueryFrame.tooltip.TableRows", "Table Rows");
      buttonItem = buttonItem(resource, tableRowsIcon, EDITPREFERENCES_TABLE_ROWS);
      queryFrameToolBar.add(buttonItem);
      
      queryFrameToolBar.addSeparator();
      
      // ===============
      // Data Menu
      
      // Export CSV Summary Table
      csvExportTabSummaryTableIcon = resourceBundle.getResourceImage(iconsDirectory
                                                                   + "csvExportSummaryTableIcon_20x20.png");
      resource = resourceBundle.getResourceString("QueryFrame.tooltip.ExportCSVSummaryTable",
                                                  "Export CSV Tab Summary Table");
      buttonItem = buttonItem(resource, csvExportTabSummaryTableIcon, DATAEXPORT_CSV_SUMMARY_TABLE);
      queryFrameToolBar.add(buttonItem);
      
      // Export CSV Query
      csvExportTabSummaryTableIcon = resourceBundle.getResourceImage(iconsDirectory
                                                                   + "csvExportTableIcon_20x20.png");
      resource = resourceBundle.getResourceString("QueryFrame.tooltip.ExportCSVQuery",
                                                  "Export CSV Query");
      buttonItem = buttonItem(resource, csvExportTabSummaryTableIcon, DATAEXPORT_CSV_QUERY);
      queryFrameToolBar.add(buttonItem);
      
      queryFrameToolBar.addSeparator();
      
      // Export PDF Summary Table
      pdfExportTabSummaryTableIcon = resourceBundle.getResourceImage(iconsDirectory
                                                                   + "pdfExportSummaryTableIcon_20x20.png");
      resource = resourceBundle.getResourceString("QueryFrame.tooltip.ExportPDFSummaryTable",
                                                  "Export PDF Tab Summary Table");
      buttonItem = buttonItem(resource, pdfExportTabSummaryTableIcon, DATAEXPORT_PDF_SUMMARY_TABLE);
      queryFrameToolBar.add(buttonItem);
      
      queryFrameToolBar.addSeparator();
      
      // ===============
      // Tools Menu
      
      // SQL Query Bucket
      sqlQueryBucketIcon = resourceBundle.getResourceImage(iconsDirectory + "sqlQueryBucketIcon_20x20.png");
      resource = resourceBundle.getResourceString("QueryFrame.tooltip.SQLQueryBucket",
                                                  "SQL Query Bucket");
      
      buttonItem = new JButton(sqlQueryBucketIcon);
      buttonItem.setFocusable(false);
      buttonItem.setMargin(new Insets(0, 0, 0, 0));
      buttonItem.setToolTipText(resource);
      buttonItem.setActionCommand(MenuActionCommands.ACTION_SQL_QUERY_BUCKET);
      buttonItem.addActionListener(mainFrame);
      
      queryFrameToolBar.add(buttonItem);
   }

   //==============================================================
   // Protected instance method used for the application's
   // creation of menu bar items. Helper Method.
   //==============================================================

   private JMenuItem menuItem(String label, String actionLabel)
   {
      JMenuItem item = new JMenuItem(label);
      item.addActionListener(this);
      item.setActionCommand(actionLabel);
      return item;
   }
   
   //==============================================================
   // Instance method used for the application's creation of tool
   // bar button items. Helper Method.
   //==============================================================

   private JButton buttonItem(String toolTip, ImageIcon icon, String actionLabel)
   {
      JButton item = new JButton(icon);
      item.setFocusable(false);
      item.setMargin(new Insets(0, 0, 0, 0));
      item.setToolTipText(toolTip);
      item.setActionCommand(actionLabel);
      item.addActionListener(this);
      
      return item;
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

   //==============================================================
   // Class method to provide a mechanism to clear the
   // queryResultTextArea.
   //==============================================================

   public static void clearQueryResultTextArea()
   {
      queryResultTextArea.setText("");
   }

   //==============================================================
   // Class Method to return the current selected tab, in the
   // main center panel.
   //==============================================================
   
   public static Object getSelectedTab()
   {
      Object currentTab = queryTabsPane.getSelectedComponent();
      
      if (currentTab instanceof SQLTabPanel)
         return (SQLTabPanel) currentTab;
      else
         return null;
   }
   
   //==============================================================
   // Class Method to return the current selected tab titel, in
   // the main center panel.
   //==============================================================

   public static String getSelectedTabTitle()
   {
      return queryTabsPane.getTitleAt(queryTabsPane.getSelectedIndex());
   }

   //==============================================================
   // Class method to provide a mechanism to place resultset
   // feedback or query warnings into the queryResultTextArea.
   //==============================================================

   public static void setQueryResultTextArea(String feedback)
   {
      queryResultTextArea.append(feedback + "\n");
   }
}