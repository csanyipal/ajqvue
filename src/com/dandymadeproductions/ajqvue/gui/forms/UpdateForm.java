//=================================================================
//                          UpdateForm
//=================================================================
//
//    This class provides a generic form that is used by each
// TableTabPanel to execute a SQL update statement on the current
// table.
//
//                      << UpdateForm.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.0 09/18/2016
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
// Version 1.0 09/18/2018 Production UpdateForm Class.
//                        
//=================================================================

package com.dandymadeproductions.ajqvue.gui.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.HelpFrame;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.utilities.InputDialog;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    This class provides a generic form that is used by each TableTabPanel to
 * execute a SQL update statement on the current table.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class UpdateForm extends JFrame implements ActionListener
{
   // Class Instances.
   private static final long serialVersionUID = -8704243596679371378L;

   private String sqlTable;
   private String dataSourceType;
   private String identifierQuoteString;
   private HashMap<String, String> columnNamesHashMap;
   private HashMap<String, String> columnClassHashMap;
   private HashMap<String, String> columnTypeHashMap;
   private HashMap<String, Integer> columnSizeHashMap;
   private ArrayList<String> updateComboBoxColumnNames;
   private ArrayList<String> comboBoxColumnNames;
   private AResourceBundle resourceBundle;

   private GridBagLayout gridbag;
   private GridBagConstraints constraints;

   private JPanel updateWherePanel;

   private JLabel statusIndicator;
   private JTextField statusLabel;
   private JCheckBox refreshCheckBox;
   private JButton questionButton;

   private JComboBox<Object> updateColumnComboBox;
   private JTextField updateColumnToTextField;
   private JCheckBox quoteCheckBox;

   private static final int updateFormExpressionNumber = 5;
   private JComboBox<Object>[] whereComboBox, operatorComboBox, andOrComboBox;
   private JTextField[] whereTextField;
   private ArrayList<JComponent> stateComponents;

   private JButton updateButton, closeButton, clearButton;
   private JButton findButton, disposeButton;

   private ImageIcon deleteDataIcon;

   //==============================================================
   // UpdateForm Constructor
   //==============================================================

   @SuppressWarnings("unchecked")
   public UpdateForm(String table, AResourceBundle resourceBundle,
                        HashMap<String, String> columnNamesHashMap,
                        HashMap<String, String> columnClassHashMap,
                        HashMap<String, String> columnTypeHashMap,
                        HashMap<String, Integer> columnSizeHashMap,
                        ArrayList<String> columnNames)
   {
      sqlTable = table;
      this.columnNamesHashMap = columnNamesHashMap;
      this.columnClassHashMap = columnClassHashMap;
      this.columnTypeHashMap = columnTypeHashMap;
      this.columnSizeHashMap = columnSizeHashMap;
      this.resourceBundle = resourceBundle;
      
      // Constructor Instances
      ImageIcon statusIdleIcon, statusWorkingIcon;
      JPanel mainPanel, formPanel;
      JPanel northPanel, statusPanel, optionsPanel, helpPanel;
      JPanel southPanel, actionButtonPanel, clearPanel;
      String resource, iconsDirectory;
      ImageIcon questionIcon;
      ImageIcon clearIcon;

      // Setting up vectors, icons directory and other instances.
      
      updateComboBoxColumnNames = new ArrayList<String> ();
      comboBoxColumnNames = new ArrayList<String> ();
      comboBoxColumnNames.add("");
      
      // Isolate these.
      for (int i = 0; i < columnNames.size(); i++)
      {
         updateComboBoxColumnNames.add(columnNames.get(i));
         comboBoxColumnNames.add(columnNames.get(i));
      }
      
      resource = resourceBundle.getResourceString("UpdateForm.message.Title", "Update");
      setTitle(resource + " : " + table);
      setIconImage(Utils.getFrameIcon());

      dataSourceType = ConnectionManager.getDataSourceType();
      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();
      identifierQuoteString = ConnectionManager.getIdentifierQuoteString();

      statusIdleIcon = resourceBundle.getResourceImage(iconsDirectory + "statusIdleIcon.png");
      statusWorkingIcon = resourceBundle.getResourceImage(iconsDirectory + "statusWorkingIcon.png");
      deleteDataIcon = resourceBundle.getResourceImage(iconsDirectory + "deleteDataIcon.gif");
      
      whereComboBox = new JComboBox[updateFormExpressionNumber];
      operatorComboBox = new JComboBox[updateFormExpressionNumber];
      andOrComboBox = new JComboBox[whereComboBox.length - 1];
      whereTextField = new JTextField[updateFormExpressionNumber];
      
      stateComponents = new ArrayList <JComponent>();

      // Setting up the frame's main panel.
      mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createEtchedBorder());

      // Setting up and Creating the input panel.
      gridbag = new GridBagLayout();
      constraints = new GridBagConstraints();

      formPanel = new JPanel();
      formPanel.setLayout(gridbag);
      formPanel.setBorder(BorderFactory.createRaisedBevelBorder());
      formPanel.addMouseListener(Ajqvue.getPopupMenuListener());

      // Status, Options, & Help Panel Components.

      northPanel = new JPanel();
      northPanel.setLayout(gridbag);
      northPanel.setBorder(BorderFactory.createRaisedBevelBorder());

      // Status
      statusPanel = new JPanel(gridbag);
      statusPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
                                                     BorderFactory.createEmptyBorder(0, 2, 0, 1)));

      statusIndicator = new JLabel("", JLabel.LEFT);
      statusIndicator.setIcon(statusIdleIcon);
      statusIndicator.setDisabledIcon(statusWorkingIcon);
      statusIndicator.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

      buildConstraints(constraints, 0, 0, 1, 1, 20, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(statusIndicator, constraints);
      statusPanel.add(statusIndicator);

      statusLabel = new JTextField("Idle", 8);
      statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      statusLabel.setEditable(false);

      buildConstraints(constraints, 1, 0, 1, 1, 80, 100);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(statusLabel, constraints);
      statusPanel.add(statusLabel);

      buildConstraints(constraints, 0, 0, 1, 1, 4, 100);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(statusPanel, constraints);
      northPanel.add(statusPanel);

      // Options
      optionsPanel = new JPanel();

      resource = resourceBundle.getResourceString("UpdateForm.label.Refresh",
                                                  "Refresh Summary Table on Update");
      refreshCheckBox = new JCheckBox(resource, true);
      optionsPanel.add(refreshCheckBox);

      buildConstraints(constraints, 1, 0, 1, 1, 90, 100);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(optionsPanel, constraints);
      northPanel.add(optionsPanel);

      // Help
      helpPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

      questionIcon = resourceBundle.getResourceImage(iconsDirectory + "bulbIcon.png");
      questionButton = new JButton(questionIcon);
      questionButton.setFocusPainted(false);
      questionButton.setBorder(BorderFactory.createRaisedBevelBorder());
      questionButton.addActionListener(this);
      helpPanel.add(questionButton);

      buildConstraints(constraints, 2, 0, 1, 1, 6, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.EAST;
      gridbag.setConstraints(helpPanel, constraints);
      northPanel.add(helpPanel);

      mainPanel.add(northPanel, BorderLayout.NORTH);

      // Update/Where Expression Components.

      updateWherePanel = new JPanel();
      updateWherePanel.setLayout(gridbag);
      updateWherePanel.setBorder(BorderFactory.createLoweredBevelBorder());

      createUpdateWhereInterface();

      buildConstraints(constraints, 0, 1, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(updateWherePanel, constraints);
      formPanel.add(updateWherePanel);

      mainPanel.add(formPanel, BorderLayout.CENTER);

      // Creating Action Buttons.

      southPanel = new JPanel(gridbag);
      southPanel.setBorder(BorderFactory.createEtchedBorder());

      actionButtonPanel = new JPanel();

      // Find Button
      resource = resourceBundle.getResourceString("UpdateForm.button.Find", "Find");
      findButton = new JButton(resource);
      findButton.setFocusPainted(false);
      actionButtonPanel.add(findButton);

      // Update Button
      resource = resourceBundle.getResourceString("UpdateForm.button.Update", "Update");
      updateButton = new JButton(resource);
      updateButton.setFocusPainted(false);
      updateButton.addActionListener(this);
      actionButtonPanel.add(updateButton);

      // Close Button
      resource = resourceBundle.getResourceString("UpdateForm.button.Close", "Close");
      closeButton = new JButton(resource);
      closeButton.setFocusPainted(false);
      closeButton.addActionListener(this);
      actionButtonPanel.add(closeButton);

      buildConstraints(constraints, 0, 0, 1, 1, 99, 100);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(actionButtonPanel, constraints);
      southPanel.add(actionButtonPanel);

      clearPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
      clearPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

      // Clear Button
      clearIcon = resourceBundle.getResourceImage(iconsDirectory + "deleteIcon.png");
      clearButton = new JButton(clearIcon);
      clearButton.setFocusPainted(false);
      clearButton.setBorder(BorderFactory.createRaisedBevelBorder());
      clearButton.addActionListener(this);
      clearPanel.add(clearButton);

      buildConstraints(constraints, 1, 0, 1, 1, 1, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.EAST;
      gridbag.setConstraints(clearPanel, constraints);
      southPanel.add(clearPanel);

      mainPanel.add(southPanel, BorderLayout.SOUTH);
      getContentPane().add(mainPanel);
      (this.getRootPane()).setDefaultButton(findButton);

      // Dummy Button to Fire Update Event.
      disposeButton = new JButton();

      // Adding WindowListener
      this.addWindowListener(tableEntryFormFrameListener);
   }

   //==============================================================
   // WindowListener for insuring that the frame is closed. (x).
   //==============================================================

   private transient WindowListener tableEntryFormFrameListener = new WindowAdapter()
   {
      public void windowClosing(WindowEvent e)
      {
         dispose();
      }
   };

   //==============================================================
   // ActionEvent Listener method for detecting the inputs from
   // the panel and directing to the appropriate routine. Note all
   // actions associated with Find are handled from the calling
   // class TableTabPanel.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object formSource = evt.getSource();

      if (formSource instanceof JButton)
      {
         // Question Button
         if (formSource == questionButton)
         {
            // Show a basic information file.
            HelpFrame informationFrame = new HelpFrame("Information", "/docs/Manual/AdvSortSearchInfo.html",
                                                       null);
            informationFrame.setSize(550, 360);
            informationFrame.center();
            informationFrame.setVisible(true);
         }

         // Update Button Action
         if (formSource == updateButton)
         {
            // Set status.
            statusIndicator.setEnabled(false);
            statusLabel.setText("Working");

            // Create a thread to handle the update.

            Thread processUpdateThread = new Thread(new Runnable()
            {
               public void run()
               {
                  if (updateTable())
                  {
                     // Notify Calling Panel to
                     // update table.
                     if (refreshCheckBox.isSelected())
                        disposeButton.doClick();
                  }
                  // Update status.
                  statusIndicator.setEnabled(true);
                  statusLabel.setText("Idle");
               }
            }, "UpdateForm.processUpdateThread");
            processUpdateThread.start();
         }

         // Clear Button Action
         if (formSource == clearButton)
         {
            statusIndicator.setEnabled(true);
            statusLabel.setText("Idle");
            refreshCheckBox.setSelected(true);
            statusIndicator.setEnabled(true);
            updateColumnComboBox.setSelectedIndex(0);
            updateColumnToTextField.setText("");
            quoteCheckBox.setSelected(true);
            
            int i = 0;
            do
            {
               whereComboBox[i].setSelectedIndex(0);
               operatorComboBox[i].setSelectedIndex(0);
               if (i < andOrComboBox.length)
                  andOrComboBox[i].setSelectedIndex(0);
               whereTextField[i].setText("");
               
               i++;
            }
            while (i < updateFormExpressionNumber);
         }

         // Close Button Action
         if (formSource == closeButton)
         {
            // Keep form active with data just
            // hide. The form remains active
            // until a normal update is again
            // called for from the TableTabPanel.
            setVisible(false);
         }
      }
   }

   //==============================================================
   // Class Method for helping the parameters in gridbag.
   //==============================================================

   private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh,
                                 double wx, double wy)
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
   //========================================================

   public void center()
   {
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension us = getSize();
      int x = (screen.width - us.width) / 2;
      int y = (screen.height - us.height) / 2;
      setLocation(x, y);
   }

   //==============================================================
   // Class method to setup the Update/Where GUI Componenets.
   //==============================================================

   private void createUpdateWhereInterface()
   {
      // Method Instance
      JPanel updatePanel, wherePanel;
      String resource, resourceWhere;
      
      JLabel setLabel, withLabel;
      JLabel[] whereLabel;
      //JLabel whereLabel1, whereLabel2, whereLabel3, whereLabel4, whereLabel5;
      JComponent swapEndComponent;

      Object[] whereOperators;
      Object[] mysqlWhereOperators = {"LIKE", "LIKE BINARY", "NOT LIKE", "REGEXP", "NOT REGEXP", "IS NULL",
                                      "IS NOT NULL", "=", "<=>", "!=", "<", "<=", ">=", ">"};
      Object[] postgreSQLWhereOperators = {"LIKE", "NOT LIKE", "SIMILAR TO", "NOT SIMILAR TO", "IS NULL",
                                           "IS NOT NULL", "~", "~*", "!~", "!~*", "=", "!=", "~=", "<", "<=",
                                           ">=", ">"};
      Object[] hsqlWhereOperators = {"LIKE", "NOT LIKE", "IS NULL", "IS NOT NULL", "IN", "NOT IN", "BETWEEN",
                                     "=", "<", "<=", ">", ">=", "<>", "!="};
      Object[] oracleWhereOperators = {"LIKE", "NOT LIKE", "REGEXP_LIKE", "IS NULL", "IS NOT NULL", "IS NAN",
                                       "IS NOT NAN", "IS INFINITE", "IS NOT INFINITE", "IS EMPTY",
                                       "IS NOT EMPTY", "IN", "NOT IN", "BETWEEN", "NOT BETWEEN", "EXISTS",
                                       "EQUALS_PATH", "UNDER_PATH", "=", "<", "<=", ">", ">=", "<>", "!=",
                                       "^="};
      Object[] sqliteWhereOperators = {"LIKE", "NOT LIKE", "IS NULL", "IS NOT NULL", "IS NOT", "IN", "NOT IN",
                                       "OR", "|", "AND", "&", "||", "BETWEEN", "GLOB", "REGEXP", "MATCH",  
                                       "=", "<", "<<", "<=", ">", ">>", ">=", "<>", "!=", "==", };
      Object[] derbyWhereOperators = {"LIKE", "NOT LIKE", "IS NULL", "IS NOT NULL", "IN", "NOT IN", "BETWEEN",
                                      "NOT BETWEEN", "EXISTS", "OR", "AND", "=", "<", "<=", ">", ">=", "<>"};

      // Assigning the appropriate string array WHERE operators.

      if (dataSourceType.equals(ConnectionManager.MYSQL)
          || dataSourceType.equals(ConnectionManager.MARIADB))
         whereOperators = mysqlWhereOperators;
      else if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
         whereOperators = postgreSQLWhereOperators;
      else if (dataSourceType.equals(ConnectionManager.ORACLE))
         whereOperators = oracleWhereOperators;
      else if (dataSourceType.equals(ConnectionManager.SQLITE))
         whereOperators = sqliteWhereOperators;
      else if (dataSourceType.equals(ConnectionManager.DERBY))
         whereOperators = derbyWhereOperators;
      // Make HSQL Default
      else
         whereOperators = hsqlWhereOperators;

      // ========================
      // Update Interface Setup.

      updatePanel = new JPanel();
      updatePanel.setLayout(gridbag);
      updatePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
                                           BorderFactory.createEmptyBorder(10, 6, 10, 6)));

      resource = resourceBundle.getResourceString("UpdateForm.label.Update", "Update");
      setLabel = new JLabel(resource + " : ", JLabel.LEADING);

      buildConstraints(constraints, 0, 0, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(setLabel, constraints);
      updatePanel.add(setLabel);

      updateColumnComboBox = new JComboBox<Object>(updateComboBoxColumnNames.toArray());
      updateColumnComboBox.setBorder(BorderFactory.createLoweredBevelBorder());

      buildConstraints(constraints, 1, 0, 2, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(updateColumnComboBox, constraints);
      updatePanel.add(updateColumnComboBox);

      resource = resourceBundle.getResourceString("UpdateForm.label.With", "With");
      withLabel = new JLabel(" " + resource + " ", JLabel.LEADING);

      buildConstraints(constraints, 3, 0, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(withLabel, constraints);
      updatePanel.add(withLabel);

      updateColumnToTextField = new JTextField(15);
      updateColumnToTextField.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
         BorderFactory.createLoweredBevelBorder()));

      buildConstraints(constraints, 4, 0, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(updateColumnToTextField, constraints);
      updatePanel.add(updateColumnToTextField);

      resource = resourceBundle.getResourceString("UpdateForm.checkbox.Quote", "Quote");
      quoteCheckBox = new JCheckBox(resource, true);

      buildConstraints(constraints, 5, 0, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(quoteCheckBox, constraints);
      updatePanel.add(quoteCheckBox);

      buildConstraints(constraints, 0, 0, 1, 1, 100, 20);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(updatePanel, constraints);
      updateWherePanel.add(updatePanel);

      // ============================
      // Where Interface Setup

      wherePanel = new JPanel();
      wherePanel.setLayout(gridbag);
      wherePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
                                         BorderFactory.createEmptyBorder(10, 6, 10, 6)));
      
      whereLabel = new JLabel[updateFormExpressionNumber];
      resourceWhere = resourceBundle.getResourceString("UpdateForm.label.Where", "Where");
      
      int i = 0;
      do
      {
         whereLabel[i] = new JLabel(resourceWhere + " : ", JLabel.LEFT);

         buildConstraints(constraints, 0, (i + 3), 1, 1, 100, 100);
         constraints.fill = GridBagConstraints.NONE;
         constraints.anchor = GridBagConstraints.CENTER;
         gridbag.setConstraints(whereLabel[i], constraints);
         wherePanel.add(whereLabel[i]);

         whereComboBox[i] = new JComboBox<Object>(comboBoxColumnNames.toArray());
         whereComboBox[i].setBorder(BorderFactory.createLoweredBevelBorder());
         stateComponents.add(whereComboBox[i]);

         buildConstraints(constraints, 1, (i + 3), 1, 1, 100, 100);
         constraints.fill = GridBagConstraints.NONE;
         constraints.anchor = GridBagConstraints.CENTER;
         gridbag.setConstraints(whereComboBox[i], constraints);
         wherePanel.add(whereComboBox[i]);

         operatorComboBox[i] = new JComboBox<Object>(whereOperators);
         operatorComboBox[i].setBorder(BorderFactory.createLoweredBevelBorder());
         stateComponents.add(operatorComboBox[i]);

         buildConstraints(constraints, 2, (i + 3), 1, 1, 100, 100);
         constraints.fill = GridBagConstraints.NONE;
         constraints.anchor = GridBagConstraints.CENTER;
         gridbag.setConstraints(operatorComboBox[i], constraints);
         wherePanel.add(operatorComboBox[i]);

         whereTextField[i] = new JTextField(15);
         whereTextField[i].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
            BorderFactory.createLoweredBevelBorder()));
         stateComponents.add(whereTextField[i]);

         buildConstraints(constraints, 3, (i + 3), 1, 1, 100, 100);
         constraints.fill = GridBagConstraints.NONE;
         constraints.anchor = GridBagConstraints.CENTER;
         gridbag.setConstraints(whereTextField[i], constraints);
         wherePanel.add(whereTextField[i]);

         if (i < andOrComboBox.length)
         {
            andOrComboBox[i] = new JComboBox<Object>();
            andOrComboBox[i].setBorder(BorderFactory.createLoweredBevelBorder());
            andOrComboBox[i].addItem("And");
            andOrComboBox[i].addItem("Or");
            stateComponents.add(andOrComboBox[i]);

            buildConstraints(constraints, 4, (i + 3), 1, 1, 100, 100);
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.CENTER;
            gridbag.setConstraints(andOrComboBox[i], constraints);
            wherePanel.add(andOrComboBox[i]);
         }
         i++;
      }
      while (i < updateFormExpressionNumber);
      
      // Ok whats going on here? Well the Update Form uses the AdvancedSortSearchForm
      // to perform the search. Data from this panel on FIND is sent to that form
      // via the setKeyComponents(). The string is parsed with delimiter, but does
      // not catch the whereTextField if its the last element if is empty. So the
      // panel complains since it does not think the correct number of paramerters
      // are sent to fill the form. Remember there are only updateFormExpressionNumber
      // minus one andOrComboBoxes, so the last parameter assigned will be a text
      // field. So swap the last two entries, to get text:text:combobox.
      
      swapEndComponent = stateComponents.get(stateComponents.size() - 1);
      
      stateComponents.set(stateComponents.size() - 1, stateComponents.get(stateComponents.size() - 2));
      stateComponents.set(stateComponents.size() - 2, swapEndComponent);
      
      buildConstraints(constraints, 0, 1, 1, 1, 100, 80);
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(wherePanel, constraints);
      updateWherePanel.add(wherePanel);
   }

   //==============================================================
   // Class method to obtain the data from the form to be used to
   // create the required SQL statement to update the table.
   //==============================================================

   private boolean updateTable()
   {
      // Method Instances.
      String columnName, columnClass, columnType;
      String sqlStatementString;
      Statement sqlStatement;
      ResultSet db_resultSet;

      InputDialog updateDialog;
      int updateRowCount, columnSize;
      String updateTextString, updateString;
      String dateString, timeString;
      String resourceMessage1, resourceMessage2, resourceTitle, resourceOK, resourceCancel;
      boolean tableUpdated, tryingUpdate;
      boolean quoteCheckBoxState;

      // Obtain connection to database & setup.

      Connection dbConnection = ConnectionManager.getConnection("UpdateForm updateTable()");

      if (dbConnection == null)
         return false;

      // Keep track of update and if a attempt
      // was made.
      tableUpdated = false;
      tryingUpdate = false;
      quoteCheckBoxState = quoteCheckBox.isSelected();
      
      sqlStatement = null;
      db_resultSet = null;

      try
      {
         // Create and initial UPDATE warning count dialog for
         // confirmation to continue with update.

         sqlStatement = dbConnection.createStatement();
         sqlStatementString = "SELECT COUNT(*) FROM " + sqlTable + " " + getWhereSQLExpression();
         // System.out.println(sqlStatementString);

         // Obtain row count to update.
         db_resultSet = sqlStatement.executeQuery(sqlStatementString);
         db_resultSet.next();
         updateRowCount = db_resultSet.getInt(1);
         
         db_resultSet.close();
         sqlStatement.close();

         // Show dialog as needed.
         if (updateRowCount != 0)
         {
            JLabel message;
            String messageUpdate, messageRows; 
            
            messageUpdate = resourceBundle.getResourceString("UpdateForm.dialogmessage.Update",
                                                                "Update");
            messageRows = resourceBundle.getResourceString("UpdateForm.dialogmessage.Rows",
                                                                "Rows(s)?");
            message = new JLabel(messageUpdate + " " + updateRowCount + " " + messageRows, JLabel.CENTER);
            
            Object[] content = {message};
            
            resourceTitle = resourceBundle.getResourceString("UpdateForm.dialogtitle.AlertDialog",
                                                             "Alert Dialog");
            resourceOK = resourceBundle.getResourceString("UpdateForm.dialogbutton.OK", "OK");
            resourceCancel = resourceBundle.getResourceString("UpdateForm.dialogbutton.Cancel", "Cancel");
            
            updateDialog = new InputDialog(this, resourceTitle, resourceOK, resourceCancel,
                                           content, deleteDataIcon);
            updateDialog.pack();
            updateDialog.setLocation(this.getLocation().x + this.getWidth() / 4, this.getLocation().y
                                                                                 + this.getHeight() / 4);
            // updateDialog.center();
            updateDialog.setResizable(false);
            updateDialog.setVisible(true);

            // Proceed with the UPDATE to the row(s) in the table with the
            // desired new content data.

            if (updateDialog.isActionResult())
            {
               updateDialog.dispose();
               
               sqlStatement = dbConnection.createStatement();
               dbConnection.setAutoCommit(false);
               tryingUpdate = true;

               // Only MySQL & PostgreSQL support.
               if (dataSourceType.equals(ConnectionManager.MYSQL)
                   || dataSourceType.equals(ConnectionManager.MARIADB)
                   || dataSourceType.equals(ConnectionManager.POSTGRESQL))
                  sqlStatement.executeUpdate("BEGIN");

               // Setup some instances needed for processing.
               updateTextString = updateColumnToTextField.getText();
               columnName = (String) updateColumnComboBox.getSelectedItem();
               columnClass = columnClassHashMap.get(columnName);
               columnType = columnTypeHashMap.get(columnName);
               columnSize = (columnSizeHashMap.get(columnName)).intValue();
               // System.out.println(updateTextString + " " + columnName + " " + columnClass + " " + columnType
               //                   + " " + columnSize);

               // Create basic initial SQL.
               sqlStatementString = ("UPDATE " + sqlTable + " SET " + identifierQuoteString
                                     + columnNamesHashMap.get(columnName) + identifierQuoteString + "=");

               // Obtain Update Text.

               // Default
               if (updateTextString.equals(""))
                  updateString = "DEFAULT";

               // NULL
               else if (updateTextString.toLowerCase(Locale.ENGLISH).equals("null"))
                  updateString = "NULL";

               // User Input
               else
               {
                  // Date Type Fields
                  if (columnClass.indexOf("Date") != -1
                      || (columnClass.toUpperCase(Locale.ENGLISH)).indexOf("TIME") != -1)
                  {
                     try
                     {
                        // Date
                        if (columnType.equals("DATE"))
                        {
                           Date dateValue;
                           updateTextString = updateTextString.trim();

                           // Check for some kind of valid input.
                           if (!(updateTextString.length() >= 10 && updateTextString.length() < 12))
                              java.sql.Date.valueOf("error");
                           
                           // Process
                           dateString = updateTextString.trim();
                           dateString = Utils.convertViewDateString_To_DBDateString(
                              dateString, DBTablesPanel.getGeneralDBProperties().getViewDateFormat());
                           
                           if (dataSourceType.equals(ConnectionManager.ORACLE))
                           {
                              updateString = "TO_DATE('" + dateString + "', 'YYYY-MM-dd')";
                              quoteCheckBox.setSelected(false);
                           }
                           else
                           {
                              dateValue = java.sql.Date.valueOf(dateString);
                              updateString = dateValue.toString();
                           }
                        }
                        // Time
                        else if (columnType.equals("TIME") || columnType.equals("TIMETZ"))
                        {
                           Time timeValue;
                           updateTextString = updateTextString.trim();
                           
                           // Check for some kind of valid input.
                           if (updateTextString.length() < 8)
                              Time.valueOf("error");
                           
                           // Process
                           timeValue = Time.valueOf(updateTextString.substring(0, 7));
                           updateString = timeValue.toString();
                        }
                        // DateTime
                        else if (columnType.equals("DATETIME"))
                        {
                           java.sql.Timestamp dateTimeValue;
                           dateString = "";
                           timeString = "";
                           updateTextString = updateTextString.trim();

                           // Check for some kind of valid input.
                           if (updateTextString.indexOf(" ") == -1 || updateTextString.length() < 10)
                              java.sql.Date.valueOf("error");
                        
                           // Process
                           dateString = updateTextString.substring(0, updateTextString.indexOf(" "));
                           dateString = Utils.convertViewDateString_To_DBDateString(
                              dateString, DBTablesPanel.getGeneralDBProperties().getViewDateFormat());
                           
                           timeString = updateTextString.substring(updateTextString.indexOf(" "));
                           dateTimeValue = java.sql.Timestamp.valueOf(dateString + timeString);
                           updateString = dateTimeValue.toString();
                           
                           if (updateString.indexOf(".") != -1)
                              updateString = updateString.substring(0, updateString.indexOf("."));
                        }
                        // Timestamp
                        else if (columnType.equals("TIMESTAMP") || columnType.equals("TIMESTAMPTZ")
                                 || columnType.equals("TIMESTAMP WITH TIME ZONE")
                                 || columnType.equals("TIMESTAMPLTZ")
                                 || columnType.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                        {
                           if (columnType.equals("TIMESTAMPLTZ")
                               || columnType.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                              Utils.setLocalTimeZone(sqlStatement);

                           SimpleDateFormat timeStampFormat;
                           Timestamp dateTimeValue;
                           Date dateParse;

                           try
                           {
                              // Create a Timestamp Format.
                              if (columnType.equals("TIMESTAMP"))
                              {
                                 if (columnSize == 2)
                                    timeStampFormat = new SimpleDateFormat("yy");
                                 else if (columnSize == 4)
                                    timeStampFormat = new SimpleDateFormat("MM-yy");
                                 else if (columnSize == 6)
                                    timeStampFormat = new SimpleDateFormat("MM-dd-yy");
                                 else if (columnSize == 8)
                                    timeStampFormat = new SimpleDateFormat("MM-dd-yyyy");
                                 else if (columnSize == 10)
                                    timeStampFormat = new SimpleDateFormat("MM-dd-yy HH:mm");
                                 else if (columnSize == 12)
                                    timeStampFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                                 // All current coloumnSizes for MySQL > 5.0 Should be 19.
                                 else
                                    timeStampFormat = new SimpleDateFormat(
                                       DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " HH:mm:ss");
                              }
                              else
                              {
                                 if (columnType.equals("TIMESTAMPLTZ")
                                     || columnType.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                                    timeStampFormat = new SimpleDateFormat(
                                       DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " HH:mm:ss Z");
                                 else
                                    timeStampFormat = new SimpleDateFormat(
                                       DBTablesPanel.getGeneralDBProperties().getViewDateFormat() + " HH:mm:ss z");
                              }

                              // Parse the TimeStamp Format.
                              if (columnType.equals("TIMESTAMPLTZ"))
                              {
                                 dateString = updateTextString.trim();
                                 dateString = dateString.substring(0, dateString.lastIndexOf(':'))
                                              + dateString.substring(dateString.lastIndexOf(':') + 1);
                                 dateParse = timeStampFormat.parse(dateString);
                              }
                              else
                                 dateParse = timeStampFormat.parse(updateTextString.trim());

                              dateTimeValue = new Timestamp(dateParse.getTime());
                              
                              if (dataSourceType.equals(ConnectionManager.ORACLE))
                              {
                                 updateString ="TO_TIMESTAMP('" + dateTimeValue.toString()
                                                + "', 'YYYY-MM-DD HH24:MI:SS:FF')";
                                 quoteCheckBox.setSelected(false);
                              }
                              else
                                 updateString = dateTimeValue.toString();
                           }
                           catch (ParseException e)
                           {
                              throw (new IllegalArgumentException(e + ""));
                           }
                        }
                        // Must be Year
                        else
                        {
                           dateString = updateTextString.trim();
                           java.sql.Date yearValue = java.sql.Date.valueOf(dateString + "-01-01");
                           updateString = yearValue.toString().substring(0, 4);
                        }
                     }
                     catch (IllegalArgumentException e)
                     {
                        resourceMessage1 = resourceBundle.getResourceString(
                           "UpdateForm.dialogmessage.InvalidDateTime", "Invalid Date/Time Input for Field");
                        resourceMessage2 = resourceBundle.getResourceString(
                           "UpdateForm.dialogmessage.ColumnType", "Type");
                        resourceTitle = resourceBundle.getResourceString("UpdateForm.dialogtitle.Alert",
                           "Alert");
                        
                        JOptionPane.showMessageDialog(null, resourceMessage1 + " " + columnName
                                                      + ", " + resourceMessage2 + ": " + columnType,
                                                      resourceTitle, JOptionPane.ERROR_MESSAGE);
                        dbConnection.setAutoCommit(true);
                        ConnectionManager.closeConnection(dbConnection,
                                                          "TableEntryForm addUpdateTableEntry()");
                        return false;
                     }
                  }
                  // Bit Types
                  else if (columnType.equals("BIT"))
                  {
                     if (dataSourceType.equals(ConnectionManager.MYSQL)
                         || dataSourceType.equals(ConnectionManager.MARIADB))
                     {
                        updateString = "B'" + updateTextString + "'";
                        quoteCheckBox.setSelected(false);
                     }
                     else
                        updateString = updateTextString;
                  }
                  
                  // None Process Given Input.
                  else
                     updateString = updateTextString;
               }

               // Finalizing SQL statement by addeding identifier
               // quotes as needed and adding WHERE.

               if (quoteCheckBox.isSelected() && !updateString.equals("DEFAULT")
                   && !updateString.equals("NULL"))
                  sqlStatementString += "'" + updateString + "'" + " " + getWhereSQLExpression();
               else
                  sqlStatementString += updateString + " " + getWhereSQLExpression();
              
               quoteCheckBox.setSelected(quoteCheckBoxState);
               
               // Proceed with execution and finish up.
               // System.out.println(sqlStatementString);
               sqlStatement.executeUpdate(sqlStatementString);
               dbConnection.commit();
               dbConnection.setAutoCommit(true);

               tableUpdated = true;
            }
         }
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "UpdateForm updateTable()");
         if (tryingUpdate)
         {
            try
            {
               dbConnection.rollback();
               dbConnection.setAutoCommit(true);
            }
            catch (SQLException error)
            {
               ConnectionManager.displaySQLErrors(e,
                  "UpdateForm updateTable() rollback failed");
            }
         }
      }
      finally
      {
         try
         {
            if (db_resultSet != null)
               db_resultSet.close();
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle,
               "UpdateForm updateTable() failed closing result set");
         }
         finally
         {
            try
            {
               if (sqlStatement != null)
                  sqlStatement.close();
            }
            catch (SQLException sqle)
            {
               ConnectionManager.displaySQLErrors(sqle,
                  "UpdateForm updateTable() failed closing sql statement");
            }
         }
      }

      // Close the connection and return results.
      ConnectionManager.closeConnection(dbConnection, "UpdateForm updateTable()");
      return tableUpdated;
   }

   //==============================================================
   // Class method to get the SQL statement string that corresponds
   // to the WHERE aspect of the update.
   //==============================================================

   private String getWhereSQLExpression()
   {
      // Method Instances
      StringBuffer sqlStatementString;
      String whereString;
      String columnNameString, columnClassString, columnTypeString;
      String operatorString, tempSearchString;
      String unionString;

      sqlStatementString = new StringBuffer();

      // ========================================
      // Adding the search(s), WHERE, option.
      
      int i = 0;
      whereString = "WHERE ";
      unionString = "";
      do
      {
         columnNameString = columnNamesHashMap.get(whereComboBox[i].getSelectedItem());
         columnClassString = columnClassHashMap.get(whereComboBox[i].getSelectedItem());
         columnTypeString = columnTypeHashMap.get(whereComboBox[i].getSelectedItem());
         operatorString = (String) operatorComboBox[i].getSelectedItem();
         tempSearchString = whereTextField[i].getText();

         if (columnNameString != null
             && (!tempSearchString.equals("")
                 || operatorString.toLowerCase(Locale.ENGLISH).indexOf("null") != -1))
         {
            if (i > 0)
               sqlStatementString.append(unionString.equals("") ? "WHERE " : unionString);
            
            if (operatorString.toLowerCase(Locale.ENGLISH).indexOf("null") != -1)
               sqlStatementString.append(whereString + identifierQuoteString + columnNameString
                                         + identifierQuoteString + " " + operatorString + " ");
            else
            {
               if (operatorString.equals("<=>")
                   && tempSearchString.toLowerCase(Locale.ENGLISH).equals("null"))
                  sqlStatementString.append(whereString + identifierQuoteString + columnNameString
                                            + identifierQuoteString + " " + operatorString
                                            + " " + tempSearchString + " ");
               else
               {
                  if (columnTypeString.equals("DATE"))
                  {
                     if (dataSourceType.equals(ConnectionManager.ORACLE))
                     {
                        sqlStatementString.append(whereString + identifierQuoteString + columnNameString
                                                  + identifierQuoteString + " " + operatorString
                                                  + " TO_DATE('"
                                                  + Utils.convertViewDateString_To_DBDateString(
                                                    tempSearchString,
                                                    DBTablesPanel.getGeneralDBProperties().getViewDateFormat())
                                                    + "', 'YYYY-MM-dd') ");
                     }
                     else
                     {
                        tempSearchString = Utils.processDateFormatSearch(tempSearchString);
                        sqlStatementString.append(whereString + identifierQuoteString + columnNameString
                                                  + identifierQuoteString + " " + operatorString + " '"
                                                  + tempSearchString + "' ");
                     }
                  }
                  else if (columnTypeString.equals("DATETIME") || columnTypeString.indexOf("TIMESTAMP") != -1)
                  {
                     if (tempSearchString.indexOf(" ") != -1)
                        tempSearchString = Utils.processDateFormatSearch(
                           tempSearchString.substring(0, tempSearchString.indexOf(" ")))
                           + tempSearchString.substring(tempSearchString.indexOf(" "));
                     else if (tempSearchString.indexOf("-") != -1 || tempSearchString.indexOf("/") != -1)
                        tempSearchString = Utils.processDateFormatSearch(tempSearchString);
                     
                     if (dataSourceType.equals(ConnectionManager.ORACLE) 
                           && columnTypeString.indexOf("TIMESTAMP") != -1)
                     {
                        sqlStatementString.append(whereString + identifierQuoteString + columnNameString
                                                 + identifierQuoteString + " " + operatorString
                                                 + " TO_TIMESTAMP('" + tempSearchString
                                                 + "', 'MM-dd-YYYY HH24:MI:SS') ");
                     }
                     else
                     {
                        if (dataSourceType.equals(ConnectionManager.MSACCESS))
                           sqlStatementString.append(whereString + identifierQuoteString + columnNameString
                                                     + identifierQuoteString + " " + operatorString + " #"
                                                     + tempSearchString + "# ");
                        else
                           sqlStatementString.append(whereString + identifierQuoteString + columnNameString
                                                     + identifierQuoteString + " " + operatorString + " '"
                                                     + tempSearchString + "' ");
                     }
                  }
                  else
                  {
                     // Character data gets single quotes for some databases,
                     // not numbers though.
                          
                     if ((dataSourceType.equals(ConnectionManager.MSACCESS)
                          || dataSourceType.indexOf(ConnectionManager.HSQL) != -1
                          || dataSourceType.equals(ConnectionManager.DERBY))
                         && columnClassString.toLowerCase(Locale.ENGLISH).indexOf("string") == -1)
                        sqlStatementString.append(whereString + identifierQuoteString + columnNameString
                                                  + identifierQuoteString + " " + operatorString + " "
                                                  + tempSearchString + " ");
                     else
                        sqlStatementString.append(whereString + identifierQuoteString + columnNameString
                                                  + identifierQuoteString + " " + operatorString + " '"
                                                  + tempSearchString + "' ");
                  }
               }
            }

            if (i < andOrComboBox.length)
               unionString = ((String) andOrComboBox[i].getSelectedItem()).toUpperCase(Locale.ENGLISH) + " ";
         }
         i++;
         whereString = "";
      }
      while (i < updateFormExpressionNumber);
      
      // System.out.println(sqlStatementString);
      return sqlStatementString.toString();
   }
   
   //==============================================================
   // Class method for outside classes to obtain the dispose button
   // so a notification can place to notify that a valid update
   // has taken place so that table data can updated in the summary
   // table.
   //==============================================================

   public JButton getDisposeButton()
   {
      return disposeButton;
   }
   
   //==============================================================
   // Class method for outside classes to obtain the find button
   // so a notification can place to notify that a request has
   // been made to filter the summary table according to the
   // UpdateForm's selections
   //==============================================================

   public JButton getFindButton()
   {
      return findButton;
   }

   //==============================================================
   // Class method for outside classes to obtain the current state
   // of the form. WARNING replicates the Advanced SortSearch Form,
   // returns and empty sort state and then the where state only.
   //==============================================================

   @SuppressWarnings("unchecked")
   public String getKeyComponentsState()
   {
      // Method Instances
      StringBuffer stateString;
      String delimiter;
      Iterator<JComponent> keyComponentIterator;
      Object currentComponent;

      // Initialize and obtain key components state.

      stateString = new StringBuffer("");
      delimiter = AdvancedSortSearchForm.getKeyComponentsDelimiter();

      // Replicate the AdvancedSortSearchForm SELECT, AGGREGATE, SORT,
      // & GROUP BY to an empty state form

      stateString.append("0" + delimiter + "0" + delimiter + "0" + delimiter + "0"
                         + delimiter + "0" + delimiter + "0" + delimiter + "0" + delimiter + "0"
                         + delimiter + "0" + delimiter + "0" + delimiter + "0" + delimiter + "0"
                         + delimiter + "0" + delimiter + "0" + delimiter + "0" + delimiter + "0"
                         + delimiter + "0" + delimiter + "0" + delimiter + "0" + delimiter);

      // Cycle through the WHERE components to add to the state string.
      
      keyComponentIterator = stateComponents.iterator();

      while (keyComponentIterator.hasNext())
      {
         currentComponent = keyComponentIterator.next();

         if (currentComponent instanceof JComboBox)
            stateString.append(((JComboBox<Object>) currentComponent).getSelectedIndex() + delimiter);

         if (currentComponent instanceof JTextField)
            stateString.append(((JTextField) currentComponent).getText() + delimiter);
      }
      
      // System.out.println(stateString.toString());
      return stateString.toString();
   }
}