//=================================================================
//                   SearchFrame Class
//=================================================================
//   This class is used to provide a framework to execute
// searches on the current selected host database by the user that
// has a connection established in the application.
//
//                  << SearchFrame.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.0 09/20/2016
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
// Version 1.0 Production SearchFrame Class.
//                            
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultEditorKit;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.gui.panels.TableTabPanel;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.TableModel;
import com.dandymadeproductions.ajqvue.utilities.Utils;
import com.dandymadeproductions.ajqvue.utilities.SearchDatabaseThread;
import com.dandymadeproductions.ajqvue.utilities.SearchResultTableCellRenderer;

//=================================================================
//                         SearchFrame
//=================================================================

/**
 *    The SearchFrame class is used to provide a framework to execute
 * searches on the current selected host database by the user that has
 * a connection established in the application.
 * 
 * @author Dana M. Proctor
 * @version 4.6 10/05/2013
 */

class SearchFrame extends JFrame implements ActionListener, KeyListener, MouseListener
{
   // =============================================
   // Creation of the necessary class instance
   // variables for the SearchFrame.
   // =============================================

   private static final long serialVersionUID = -4730639399999597935L;

   private transient SearchDatabaseThread searchDatabase;
   private JPopupMenu resultTablePopupMenu;
   private JTextField searchTextField;
   private JButton searchButton;
   private JButton clearSearchButton;
   private JButton searchCompleteButton;

   private AResourceBundle resourceBundle;
   private Object[][] defaultTableData;
   private JTable resultTable;
   private TableModel tableModel;

   private JProgressBar searchProgressBar;
   private JButton cancelButton;
   private TableTabPanel selectedTableTab;
   private String selectedTable;
   private String iconsDirectory;
   
   //==============================================================
   // SearchFrame Constructor
   //==============================================================

   protected SearchFrame()
   {
      // Constructor Instances.
      JMenuBar searchFrameMenuBar;
      ArrayList<String> tableHeadings;
      JScrollPane tableScrollPane;
      TableColumn column;
      String resource, resourceInclude;
      
      JPanel mainPanel, centerPanel, searchPanel, statusCancelPanel;
      JLabel searchLabel;

      ImageIcon searchIcon, removeIcon;

      // Setting up the title, file separator and other needed
      // instance elements.

      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();
      resourceBundle = Ajqvue.getResourceBundle();
      
      resource = resourceBundle.getResourceString("SearchFrame.message.Title", "Search Frame");
      setTitle("Ajqvue " + resource);
      setIconImage(Utils.getFrameIcon());

      searchIcon = resourceBundle.getResourceImage(iconsDirectory + "searchIcon.png");
      removeIcon = resourceBundle.getResourceImage(iconsDirectory + "removeIcon.png");

      // ==================================================
      // Frame Window Closing Addition. Also method for
      // reactivating if desired/needed.
      // ==================================================

      WindowListener searchFrameListener = new WindowAdapter()
      {
         public void windowClosing(WindowEvent e)
         {
            Main_JMenuBarActions.setSearchFrameNotVisisble();
            dispose();
         }

         public void windowActivated(WindowEvent e)
         {
         }
      };

      this.addWindowListener(searchFrameListener);

      // ===============================================
      // JMenu Bar for the Frame.
      // ===============================================

      searchFrameMenuBar = new JMenuBar();
      searchFrameMenuBar.setBorder(BorderFactory.createEtchedBorder());
      createMenuBar(searchFrameMenuBar);
      this.setJMenuBar(searchFrameMenuBar);

      // ===============================================
      // Popup Menu for the Center JTable.
      // ===============================================

      resultTablePopupMenu = new JPopupMenu();
      createPopupMenu();

      // ===============================================
      // Setting up the various panels that are used in
      // the search frame
      // ===============================================

      mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());

      // ====================
      // Search Interface

      searchPanel = new JPanel();
      searchPanel.setBorder(BorderFactory.createRaisedBevelBorder());

      resource = resourceBundle.getResourceString("SearchFrame.label.SearchDatabaseFor",
                                                  "Search Database For");
      searchLabel = new JLabel(resource + " : ");
      searchPanel.add(searchLabel);

      searchTextField = new JTextField(15);
      searchTextField.setBorder(BorderFactory.createLoweredBevelBorder());
      searchTextField.addMouseListener(Ajqvue.getPopupMenuListener());
      searchTextField.addKeyListener(this);
      searchPanel.add(searchTextField);

      searchButton = new JButton(searchIcon);
      searchButton.setMargin(new Insets(0, 0, 0, 0));
      searchButton.setFocusPainted(false);
      searchButton.addActionListener(this);
      searchPanel.add(searchButton);

      clearSearchButton = new JButton(removeIcon);
      clearSearchButton.setMargin(new Insets(2, 2, 2, 2));
      clearSearchButton.setFocusPainted(false);
      clearSearchButton.addActionListener(this);
      searchPanel.add(clearSearchButton);

      mainPanel.add(searchPanel, BorderLayout.NORTH);

      // =========================================
      // Resultant Search Result Panel/Table

      centerPanel = new JPanel(new GridLayout(1, 1, 0, 0));
      centerPanel.setBorder(BorderFactory.createEtchedBorder());

      // Setup Headings.
      tableHeadings = new ArrayList<String>();
      
      resourceInclude = resourceBundle.getResourceString("SearchFrame.label.Include", "Include");
      tableHeadings.add(resourceInclude);
      
      resource = resourceBundle.getResourceString("SearchFrame.label.Table", "Table");
      tableHeadings.add(resource);
      
      resource = resourceBundle.getResourceString("SearchFrame.label.SearchResultCount",
                                                  "Search Result Count");
      tableHeadings.add(resource);

      // Fill the result table structure with default data.
      
      defaultTableData = new Object[DBTablesPanel.getTableCount()][3];

      Iterator<String> tableNamesIterator = ConnectionManager.getTableNames().iterator();
      int i = 0;

      while (tableNamesIterator.hasNext())
      {
         defaultTableData[i][0] = Boolean.valueOf(true);
         defaultTableData[i][1] = "   " + tableNamesIterator.next();
         defaultTableData[i++][2] = Integer.valueOf(0);
      }
      
      // Setup the table.
      
      tableModel = new TableModel(tableHeadings, defaultTableData);

      resultTable = new JTable(tableModel);
      resultTable.getTableHeader().setFont(new Font(centerPanel.getFont().getName(), Font.BOLD,
                                           centerPanel.getFont().getSize()));
      resultTable.getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME),
                                     TransferHandler.getCopyAction());
      //resultTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), "copy");
      
      column = resultTable.getColumnModel().getColumn(0);
      column.setPreferredWidth(resourceInclude.length());
      resultTable.getColumnModel().getColumn(1).setCellRenderer(new SearchResultTableCellRenderer());
      resultTable.getColumnModel().getColumn(2).setCellRenderer(new SearchResultTableCellRenderer());
      
      resultTable.addMouseListener(this);

      // Create a scrollpane for the search count result table.
      
      tableScrollPane = new JScrollPane(resultTable);
      centerPanel.add(tableScrollPane);
      mainPanel.add(centerPanel, BorderLayout.CENTER);

      // ==================================
      // SearchFrame Progress/Cancel Panel

      statusCancelPanel = new JPanel();
      statusCancelPanel.setBorder(BorderFactory.createEtchedBorder());

      searchProgressBar = new JProgressBar();
      searchProgressBar.setIndeterminate(true);
      searchProgressBar.setValue(0);
      searchProgressBar.setStringPainted(true);
      statusCancelPanel.add(searchProgressBar);

      resource = resourceBundle.getResourceString("SearchFrame.label.Cancel", "Cancel");
      cancelButton = new JButton(resource);
      cancelButton.setMargin(new Insets(0, 0, 0, 0));
      cancelButton.setFocusPainted(false);
      cancelButton.addActionListener(this);
      statusCancelPanel.add(cancelButton);

      mainPanel.add(statusCancelPanel, BorderLayout.SOUTH);
      
      getContentPane().add(mainPanel);
      getContentPane().addMouseListener(Ajqvue.getPopupMenuListener());
      
      // Dummy button for updating data from the thread
      // created with searchDatabase.
      searchCompleteButton = new JButton();
      searchCompleteButton.addActionListener(this);
      
      searchTextField.grabFocus();
   }

   //==============================================================
   // ActionEvent Listener method for detecting the user's selection
   // of various components in the frame and taking the appropriate
   // action as required. Mouse events handled in the inner class
   // mouse adapter created in the constructor.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object panelSource = evt.getSource();

      // MenuBar Actions
      if (panelSource instanceof JMenuItem)
      {
         // Instances & Setting Up.
         JMenuItem item = (JMenuItem) panelSource;
         String actionCommand = item.getActionCommand();
         //System.out.println(actionCommand);

         // ==================================
         // File Menu Item Selection Routing
         // ==================================

         // Exit
         if (actionCommand.equals("FE"))
         {
            Main_JMenuBarActions.setSearchFrameNotVisisble();
            this.dispose();
         }

         // Popup resultTable Select All
         if (actionCommand.equals("SelectAll"))
         {
            resultTable.selectAll();
            for (int i = 0; i < resultTable.getRowCount(); i++)
               resultTable.setValueAt(Boolean.valueOf(true), i, 0);
            
         }

         // Popup resultTable DeSelect All
         if (actionCommand.equals("DeSelectAll"))
         {
            resultTable.clearSelection();
            for (int i = 0; i < resultTable.getRowCount(); i++)
               resultTable.setValueAt(Boolean.valueOf(false), i, 0);
         }
         
         // Popup resultTable Copy
         else if (actionCommand.equals((String)TransferHandler.getCopyAction().getValue(Action.NAME)))
         {
            Action a = resultTable.getActionMap().get(actionCommand);
            if (a != null)
               a.actionPerformed(new ActionEvent(resultTable, ActionEvent.ACTION_PERFORMED, null));
         }
      }

      // Button Actions
      if (panelSource instanceof JButton)
      {
         // Execute search action.
         if (panelSource == searchButton)
         {
            if (!searchTextField.getText().equals(""))
            {
               // Collect the database table names directly instead of relying
               // on the names in the search frame, just in case a database
               // reload took place.
               
               ArrayList<String> databaseTables = ConnectionManager.getTableNames();
               boolean[] selectedTables = new boolean[databaseTables.size()];
               int progressBarMax = 0;
               
               // Create a list of included tables to be searched.
               
               if (!databaseTables.isEmpty())
               {
                  for (int i = 0; i < databaseTables.size(); i++)
                  {
                     if (databaseTables.contains(resultTable.getValueAt(i, 1).toString().trim())
                         && ((Boolean)resultTable.getValueAt(i, 0)).booleanValue() == true)
                     {
                        selectedTables[i] = true;
                        progressBarMax++;
                     }
                     else
                        selectedTables[i] = false;
                  }
                  
                  // Execute query
                  searchProgressBar.setMaximum(progressBarMax);
                  searchProgressBar.setValue(0);
                  searchProgressBar.setIndeterminate(false);
                  searchDatabase = new SearchDatabaseThread(databaseTables, selectedTables,
                                                            searchTextField.getText(),
                                                            searchProgressBar,
                                                            searchCompleteButton);
                  Thread searchDatabaseThread = new Thread(searchDatabase, "SearchDatabase");
                  searchDatabaseThread.start();
               }
            }
         }

         // Clear search action.
         if (panelSource == clearSearchButton)
         {
            // searchTextField.setText("");
            searchProgressBar.setValue(0);
            searchProgressBar.setIndeterminate(true);
            tableModel.setValues(defaultTableData);
            searchButton.setEnabled(true);
         }

         // Cancel search action.
         if (panelSource == cancelButton)
         {
            if (searchDatabase != null)
               searchDatabase.cancel();
         }
         
         // Database search complete action.
         if (panelSource == searchCompleteButton)
         {
            if (searchDatabase != null)
            {
               Object[][] tableData = searchDatabase.getResultData();
               if (tableData != null)
               {
                  tableModel.setValues(tableData);
                  searchButton.setEnabled(false);
               }
            }
         }
      }
   }

   //==============================================================
   // KeyEvent Listener methods for detected key pressed events to
   // full fill KeyListener Interface requirements.
   //==============================================================
   
   public void keyPressed(KeyEvent evt)
   {
      // Do Nothing
   }

   public void keyReleased(KeyEvent evt)
   {
      // Do Nothing
   }

   //==============================================================
   // KeyEvent Listener method for detecting key pressed event,
   // Enter, used with the search action text field.
   //==============================================================

   public void keyTyped(KeyEvent evt)
   {
      // Derived from the searchTextField.
      char keyChar = evt.getKeyChar();

      // Fire the search button as required.
      if (keyChar == KeyEvent.VK_ENTER)
         searchButton.doClick();
   }
   
   //==============================================================
   // MouseEvent Listener methods for detecting mouse events.
   // MounseListner Interface requirements.
   //==============================================================

   public void mouseEntered(MouseEvent evt)
   {
      // Do Nothing.
   }
   
   public void mouseExited(MouseEvent evt)
   {
      // Do Nothing.
   }
   
   public void mousePressed(MouseEvent evt)
   {
      if (evt.isPopupTrigger())
         resultTablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
   }

   public void mouseReleased(MouseEvent evt)
   {
      if (evt.isPopupTrigger())
         resultTablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
   }

   //==============================================================
   // MouseEvent Listener methods for detecting mouse clicked events.
   // Collects the row & column selected. Then either set or deselects
   // Include checkbox or shows the appropriate database table in
   // the Main_Frame, DBTablesPanel and
   //==============================================================   

   public void mouseClicked(MouseEvent e)
   {  
      Point coordinatePoint;
      int tableRow, tableColumn;

      // Collect coordinate to determine cell selected.
      coordinatePoint = e.getPoint();
      tableRow = resultTable.rowAtPoint(coordinatePoint);
      tableColumn = resultTable.columnAtPoint(coordinatePoint);

      if (tableRow >= resultTable.getRowCount() || tableRow < 0)
         return;
      else
      {
         // Include Action
         if (tableColumn == 0)
         {
            Boolean s = (Boolean) resultTable.getValueAt(tableRow, tableColumn);
            
            if (s.booleanValue() == true)
               resultTable.setValueAt(Boolean.valueOf(false), tableRow, tableColumn);
            else
               resultTable.setValueAt(Boolean.valueOf(true), tableRow, tableColumn);
            return;
         }
         
         // Show Table Action
         selectedTable = resultTable.getValueAt(tableRow, 1).toString().trim();
         //System.out.println(selectedTable);

         selectedTableTab = DBTablesPanel.getTableTabPanel(selectedTable);
         
         if (selectedTableTab != null)
         {
            DBTablesPanel.startStatusTimer();
            
            Thread showResultsTableTabPanelThread = new Thread(new Runnable()
            {
               public void run()
               {
                  selectedTableTab.setSearchTextField(searchTextField.getText());
                  DBTablesPanel.setSelectedTableTabPanel(selectedTable);
                  
                  DBTablesPanel.stopStatusTimer();
               }
            }, "SearchFrame.showResultsTableTabPanelThread");
            showResultsTableTabPanelThread.start();  
         }
      }
   }
   
   //==============================================================
   // Method used for creation of the menu bar that will be used
   // with the frame.
   //==============================================================

   private void createMenuBar(JMenuBar searchFrameMenuBar)
   {
      // Method Instances
      String resource;
      JMenu fileMenu, editMenu;
      JMenuItem menuItem = null;
      
      ImageIcon logoIcon;
      JButton logoIconItem;
      
      // File Menu
      resource = resourceBundle.getResourceString("SearchFrame.menu.File", "File");
      fileMenu = new JMenu(resource);
      fileMenu.setFont(fileMenu.getFont().deriveFont(Font.BOLD));
      fileMenu.addSeparator();
      
      resource = resourceBundle.getResourceString("SearchFrame.menu.Exit", "Exit");
      fileMenu.add(menuItem(resource, "FE"));
      searchFrameMenuBar.add(fileMenu);

      // Edit Menu
      resource = resourceBundle.getResourceString("SearchFrame.menu.Edit", "Edit");
      editMenu = new JMenu(resource);
      editMenu.setFont(editMenu.getFont().deriveFont(Font.BOLD));
      
      menuItem = new JMenuItem(new DefaultEditorKit.CutAction());
      resource = resourceBundle.getResourceString("SearchFrame.menu.Cut", "Cut");
      menuItem.setText(resource + "          " + "Ctrl+x");
      menuItem.setMnemonic(KeyEvent.VK_X);
      editMenu.add(menuItem);

      menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
      resource = resourceBundle.getResourceString("SearchFrame.menu.Copy", "Copy");
      menuItem.setText(resource + "       " + "Ctrl+c");
      menuItem.setMnemonic(KeyEvent.VK_C);
      editMenu.add(menuItem);

      menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
      resource = resourceBundle.getResourceString("SearchFrame.menu.Paste", "Past");
      menuItem.setText(resource + "       " + "Ctrl+v");
      menuItem.setMnemonic(KeyEvent.VK_V);
      editMenu.add(menuItem);

      searchFrameMenuBar.add(editMenu);

      searchFrameMenuBar.add(Box.createHorizontalGlue());

      // Logo
      logoIcon = resourceBundle.getResourceImage(iconsDirectory + "ajqvueIcon.gif");
      logoIconItem = new JButton(logoIcon);
      logoIconItem.setDisabledIcon(logoIcon);
      logoIconItem.setFocusPainted(false);
      logoIconItem.setBorder(BorderFactory.createLoweredBevelBorder());
      searchFrameMenuBar.add(logoIconItem);
   }
   
   //==============================================================
   // Method used for creation of a popup menu for the search
   // results table in the frame.
   //==============================================================

   private void createPopupMenu()
   {
      // Method Instances
      String resource;
      JMenuItem menuItem = null;
      
      resource = resourceBundle.getResourceString("SearchFrame.menu.SelectAll", "Select All");
      menuItem = menuItem(resource, "SelectAll");
      resultTablePopupMenu.add(menuItem);

      resource = resourceBundle.getResourceString("SearchFrame.menu.DeSelectAll", "DeSelect All");
      menuItem = menuItem(resource, "DeSelectAll");
      resultTablePopupMenu.add(menuItem);
      
      resultTablePopupMenu.addSeparator();
      
      resource = resourceBundle.getResourceString("SearchFrame.menu.Copy", "Copy");
      menuItem = new JMenuItem(resource);
      menuItem.setActionCommand((String)TransferHandler.getCopyAction().getValue(Action.NAME));
      menuItem.setMnemonic(KeyEvent.VK_C);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
      menuItem.addActionListener(this);
      resultTablePopupMenu.add(menuItem);
   }
   
   //==============================================================
   // Method used for the SearchFrame's creation of menu bar
   // items. Helper Method.
   //==============================================================

   private JMenuItem menuItem(String label, String actionLabel)
   {
      JMenuItem item = new JMenuItem(label);
      item.addActionListener(this);
      item.setActionCommand(actionLabel);
      return item;
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