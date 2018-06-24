//=================================================================
//                       TableEntryForm
//=================================================================
//
//    This class is used to provide a form panel to add, or
// edit a table entry in a SQL database table.
//
//                  << TableEntryForm.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.6 06/24/2018
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
// Version 1.0 09/18/2016 Production TableEntryForm Class.
//         1.1 02/05/2017 Method selectFunctionOperator() Correction in
//                        Spelling for message Resource.
//         1.2 07/21/2017 Constructor Included SQLite TEXT Type Fields
//                        Larger Then 255 to be Included With Browse
//                        Button. Proper Detection Therefore in addUpdate
//                        TableEntry(), isTextField, & setFormField().
//         1.3 05/10/2018 Method addUpdateTableEntry() Changed Handling of HSQL
//                        BIT Fields to Processed the Same as BIT VARYING Fields.
//                        Therefore Used Value Usage to be B'x' Format.
//         1.4 06/03/2018 Changed Class Instance columnTypeHashMape to columnTypeName
//                        HashMap. Formatted Instance Declarations, One per Line.
//                        Changed All Instances in Methods columnType to columnTypeName.
//                        Method actionPerformed(), addUpdateTableEntry(), &
//                        setFormField() Used Utils.isBlob() & Utils.isText().
//                        Method createFunctionSQLStatement() Used Utils.isBlob(),
//                        Utils.isNumeric(), & Utils.isText()
//         1.5 06/20/2018 Changed Constructor Arguments to Simply With Most Now
//                        Derived From selectedTableTabPanel, Removed Argument
//                        id. Added Class Instances columnSQLTypeHashMap & column
//                        SetHashMap. Method addUpdateTableEntry() Added Class
//                        Instance columnSQLType, Use of isNumeric(), & Change
//                        in Processing for SQLite Date Types to Detect TEXT
//                        Content.
//         1.6 06/24/2018 Method addUpdateTableEntry() Added columnSQLType to
//                        Utils.isNumeric(). Method createFunctionSQLStatementString()
//                        Added Instance columnSQLType & Used in Utils.isNumeric().
//        
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.sql.rowset.serial.SerialBlob;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.gui.panels.TableTabPanel;
import com.dandymadeproductions.ajqvue.io.ReadDataFile;
import com.dandymadeproductions.ajqvue.io.WriteDataFile;
import com.dandymadeproductions.ajqvue.utilities.BlobTextKey;
import com.dandymadeproductions.ajqvue.utilities.DateFieldCalendar;
import com.dandymadeproductions.ajqvue.utilities.InputDialog;
import com.dandymadeproductions.ajqvue.utilities.AFocusTraversalPolicy;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;
import com.dandymadeproductions.ajqvue.utilities.SetListDialog;

/**
 *    The TableEntryForm class is used to provide a form panel to add, or
 * edit a table entry in a SQL database table.
 * 
 * @author Dana M. Proctor
 * @version 1.6 06/24/2018
 */

public class TableEntryForm extends JFrame implements ActionListener
{
   // Class Instances.
   private static final long serialVersionUID = 5242558249790854884L;

   private int selectedRow;
   private TableTabPanel selectedTableTabPanel;
   private HashMap<String, JComponent> fieldHashMap;
   private HashMap<String, String> columnNamesHashMap;
   private HashMap<String, String> columnClassHashMap;
   private HashMap<String, Integer> columnSQLTypeHashMap;
   private HashMap<String, String> columnTypeNameHashMap;
   private HashMap<String, Integer> columnSizeHashMap;
   private HashMap<String, String> columnEnumHashMap;
   private HashMap<String, String> columnSetHashMap;
   private HashMap<JButton, Object> blobBytesHashMap;
   private HashMap<String, JCheckBox> blobRemoveCheckBoxesHashMap;
   private HashMap<JButton, String> calendarButtonHashMap;
   private HashMap<JButton, String> setButtonHashMap;
   private HashMap<JButton, String> functionButtonHashMap;
   private HashMap<Object, ArrayList<String>> setFieldsHashMap;
   private HashMap<Object, String> functionsHashMap;
   private AResourceBundle resourceBundle;

   private String sqlTable;
   private String dataSourceType;
   private String iconsDirectory;
   private String identifierQuoteString;
   private String resourceInvalidInput, resourceType, resourceAlert;
   private ArrayList<String> primaryKeys;
   private HashMap<String, String> autoIncrementHashMap;
   private ArrayList<String> formFields;
   private TableViewForm tableViewForm;
   private ArrayList<Component> componentFocusSequence;
   private JCheckBox limitCheckBox;
   private JTextField limitTextField;
   private boolean addItem;
   private boolean validEntry;
   private JButton cancelButton, updateButton;
   private JButton disposeButton;

   private DateFieldCalendar dateCalendar;
   private SetListDialog setDialog;

   //==============================================================
   // TableEntryForm Constructor
   //==============================================================

   @SuppressWarnings("unchecked")
   public TableEntryForm(String title, boolean addItem, String sqlTable, int selectedRow,
                         TableTabPanel selectedTableTabPanel, ArrayList<String> formFields,
                         TableViewForm tableViewForm)
   {
      this.addItem = addItem;
      this.sqlTable = sqlTable;
      this.selectedRow = selectedRow;
      this.selectedTableTabPanel = selectedTableTabPanel;
      this.formFields = formFields;
      this.tableViewForm = tableViewForm;
      
      primaryKeys = selectedTableTabPanel.getPrimaryKeys();
      autoIncrementHashMap = selectedTableTabPanel.getAutoIncrementHashMap();
      columnNamesHashMap = selectedTableTabPanel.getColumnNamesHashMap();
      columnClassHashMap = selectedTableTabPanel.getColumnClassHashMap();
      columnSQLTypeHashMap = selectedTableTabPanel.getColumnSQLTypeHashMap();
      columnTypeNameHashMap = selectedTableTabPanel.getColumnTypeNameHashMap();
      columnSizeHashMap = selectedTableTabPanel.getColumnSizeHashMap();
      columnEnumHashMap = selectedTableTabPanel.getColumnEnumHashMap();
      columnSetHashMap = selectedTableTabPanel.getColumnSetHashMap();
      
      // Constructor Instances

      AFocusTraversalPolicy focusSequence;
      Iterator<String> columnNamesIterator;
      ImageIcon removeUpIcon, removeDownIcon;
      ImageIcon functionIcon, calendarIcon, setIcon;
      validEntry = false;
      String resource;
      String columnName;
      String columnClass;
      String columnTypeName;
      Object currentField;

      fieldHashMap = new HashMap <String, JComponent>();
      blobBytesHashMap = new HashMap <JButton, Object>();
      blobRemoveCheckBoxesHashMap = new HashMap <String, JCheckBox>();
      calendarButtonHashMap = new HashMap <JButton, String>();
      setButtonHashMap = new HashMap <JButton, String>();
      functionButtonHashMap = new HashMap <JButton, String>();
      setFieldsHashMap = new HashMap <Object, ArrayList<String>>();
      functionsHashMap = new HashMap <Object, String>();
      componentFocusSequence = new ArrayList <Component>();
      resourceBundle = Ajqvue.getResourceBundle();

      // Setting up a icons directory identifier quote character,
      // & other instances.
      
      dataSourceType = ConnectionManager.getDataSourceType();
      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();
      identifierQuoteString = ConnectionManager.getIdentifierQuoteString();
      
      resource = resourceBundle.getResourceString("TableEntryForm.dialogtitle.Alert", "Alert");
      resourceAlert = resource;
      
      resource = resourceBundle.getResourceString("TableEntryForm.dialogmessage.InvalidInput",
                                                  "Invalid Input for Field");
      resourceInvalidInput = resource;
      
      resource = resourceBundle.getResourceString("TableEntryForm.dialogmessage.Type", "Type");
      resourceType = resource;
      
      // Setting up the frame's title & main panel.

      if (title.indexOf("Add") != -1)
         resource = resourceBundle.getResourceString("TableEntryForm.message.TitleAdd", "Add");
      else if (title.indexOf("Edit") != -1)
         resource = resourceBundle.getResourceString("TableEntryForm.message.TitleEdit", "Edit");
      else
         resource = title;

      setTitle(resource + ": " + sqlTable);
      setIconImage(Utils.getFrameIcon());

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createEtchedBorder());

      // Setting up and Creating the panel.

      JPanel formPanel = new JPanel();
      formPanel.setLayout(null);
      formPanel.setBorder(BorderFactory.createRaisedBevelBorder());
      formPanel.addMouseListener(Ajqvue.getPopupMenuListener());

      removeUpIcon = resourceBundle.getResourceImage(iconsDirectory + "removeNotIcon.png");
      removeDownIcon = resourceBundle.getResourceImage(iconsDirectory + "removeIcon.png");
      functionIcon = resourceBundle.getResourceImage(iconsDirectory + "functionIcon.png");
      calendarIcon = resourceBundle.getResourceImage(iconsDirectory + "calendarIcon.png");
      setIcon = resourceBundle.getResourceImage(iconsDirectory + "setIcon.png");

      // Start Position for components
      int x = 15;
      int y = 20;

      columnNamesIterator = formFields.iterator();

      while (columnNamesIterator.hasNext())
      {
         columnName = columnNamesIterator.next();
         columnClass = columnClassHashMap.get(columnName);
         columnTypeName = columnTypeNameHashMap.get(columnName);
         // System.out.println(columnNamesHashMap.get(columnName) + " " +
         //                    columnClassHashMap.get(columnName) + " " +
         //                    columnTypeNameHashMap.get(columnName) + " " +
         //                    columnSizeHashMap.get(columnName));

         // BFile types not supported.
         if (columnTypeName.equals("BFILE") && !addItem)
            continue;

         // =================================
         // Labels
         // =================================

         JLabel currentLabel = new JLabel(columnName);
         currentLabel.setBounds(x, y, 120, 20);
         formPanel.add(currentLabel);

         // =================================
         // Function Buttons
         // =================================

         currentField = new JButton(functionIcon);
         ((JButton) currentField).setBounds(x + 120, y, 20, 20);
         ((JButton) currentField).addActionListener(this);
         functionButtonHashMap.put((JButton) currentField, columnName);
         formPanel.add((JButton) currentField);

         // ===============================
         // TextFields & ComboBoxFields
         // ===============================

         // Auto Increment
         if (autoIncrementHashMap.containsValue(columnName))
         {
            currentField = new JTextField();
            ((JTextField) currentField).setHorizontalAlignment(JTextField.RIGHT);
            ((JTextField) currentField).setBounds(x + 145, y, 220, 20);
            componentFocusSequence.add((JTextField) currentField);
            formPanel.add((JTextField) currentField);
         }

         // ENUM Type Fields
         else if (columnEnumHashMap.containsKey(columnName))
         {
            currentField = new JComboBox<Object>();
            ((JComboBox<Object>) currentField).setBounds(x + 145, y, 220, 20);
            componentFocusSequence.add((JComboBox<Object>) currentField);
            formPanel.add((JComboBox<Object>) currentField);
         }

         // SET Type Fields
         else if (columnSetHashMap.containsKey(columnName))
         {
            currentField = new JButton(setIcon);
            ((JButton) currentField).setBounds(x + 345, y, 20, 20);
            ((JButton) currentField).addActionListener(this);
            setButtonHashMap.put((JButton) currentField, columnName);
            formPanel.add((JButton) currentField);

            currentField = new JTextField();
            ((JTextField) currentField).setText("");
            ((JTextField) currentField).setBounds(x + 145, y, 195, 20);
            componentFocusSequence.add((JTextField) currentField);
            formPanel.add((JTextField) currentField);
         }

         // TINYINT, SMALLINT, MEDIUMINT, INT, & BIGINT Type Fields
         else if ((columnClass.indexOf("Byte") != -1 && columnTypeName.indexOf("BIT DATA") == -1)
                  || columnClass.indexOf("Short") != -1
                  || columnClass.indexOf("Integer") != -1 || columnClass.indexOf("Long") != -1)
         {
            currentField = new JTextField();
            ((JTextField) currentField).setHorizontalAlignment(JTextField.RIGHT);
            ((JTextField) currentField).setText("000");
            ((JTextField) currentField).setBounds(x + 145, y, 220, 20);
            componentFocusSequence.add((JTextField) currentField);
            formPanel.add((JTextField) currentField);
         }

         // FLOAT, DOUBLE, and DECIMAL Type Fields
         else if (columnClass.indexOf("Float") != -1 || columnClass.indexOf("Double") != -1
                  || columnClass.indexOf("BigDecimal") != -1)
         {
            currentField = new JTextField();
            ((JTextField) currentField).setHorizontalAlignment(JTextField.RIGHT);
            ((JTextField) currentField).setText("00.00");
            ((JTextField) currentField).setBounds(x + 145, y, 220, 20);
            componentFocusSequence.add((JTextField) currentField);
            formPanel.add((JTextField) currentField);
         }

         // BLOB, BYTEA, BINARY, RAW, CLOB, IMAGE, & BIT DATA Type Fields
         else if ((columnClass.indexOf("String") == -1 && columnTypeName.indexOf("BLOB") != -1)
                  || (columnClass.indexOf("BLOB") != -1 && columnTypeName.indexOf("BLOB") != -1)
                  || (columnTypeName.indexOf("BYTEA") != -1) || (columnTypeName.indexOf("BINARY") != -1)
                  || (columnTypeName.indexOf("RAW") != -1) || (columnTypeName.indexOf("CLOB") != -1)
                  || (columnTypeName.indexOf("IMAGE") != -1) || (columnTypeName.indexOf("BIT DATA") != -1))
         {
            // Place the remove checkbox for eliminating
            // existing data as desired during edit.
            if (!addItem)
            {
               currentField = new JCheckBox(removeUpIcon, false);
               ((JCheckBox) currentField).setSelectedIcon(removeDownIcon);
               ((JCheckBox) currentField).setBounds(x + 345, y, 20, 20);
               blobRemoveCheckBoxesHashMap.put(columnName, (JCheckBox) currentField);
               formPanel.add((JCheckBox) currentField);
            }

            // Normal field component.
            currentField = new JButton("Browse");
            ((JButton) currentField).addActionListener(this);

            if (!addItem)
               ((JButton) currentField).setBounds(x + 145, y, 195, 20);
            else
               ((JButton) currentField).setBounds(x + 145, y, 220, 20);
            componentFocusSequence.add((JButton) currentField);
            formPanel.add((JButton) currentField);
         }

         // TEXT Type Fields
         else if ((columnClass.indexOf("String") != -1 && !columnTypeName.equals("CHAR")
                   && ((Integer) columnSizeHashMap.get(columnName)).intValue() > 255)
                  || (columnClass.indexOf("Object") != -1 && columnTypeName.equals("TEXT")
                      && ((Integer) columnSizeHashMap.get(columnName)).intValue() > 255) 
                  || (columnClass.indexOf("String") != -1 && columnTypeName.equals("LONG")))
         {
            // Place the remove checkbox for eliminating
            // existing text data as desired during edit.
            if (!addItem)
            {
               currentField = new JCheckBox(removeUpIcon, false);
               ((JCheckBox) currentField).setSelectedIcon(removeDownIcon);
               ((JCheckBox) currentField).setBounds(x + 345, y, 20, 20);
               blobRemoveCheckBoxesHashMap.put(columnName, (JCheckBox) currentField);
               formPanel.add((JCheckBox) currentField);
            }

            // Normal text field component.
            currentField = new JButton("TEXT");
            ((JButton) currentField).addActionListener(this);
            if (!addItem)
               ((JButton) currentField).setBounds(x + 145, y, 195, 20);
            else
               ((JButton) currentField).setBounds(x + 145, y, 220, 20);
            componentFocusSequence.add((JButton) currentField);
            formPanel.add((JButton) currentField);
         }

         // ARRAY Type Fields
         else if ((columnClass.indexOf("Array") != -1 || columnClass.indexOf("Object") != -1)
                  && columnTypeName.indexOf("_") != -1)
         {
            // Place the remove checkbox for eliminating
            // existing text data as desired during edit.
            if (!addItem)
            {
               currentField = new JCheckBox(removeUpIcon, false);
               ((JCheckBox) currentField).setSelectedIcon(removeDownIcon);
               ((JCheckBox) currentField).setBounds(x + 345, y, 20, 20);
               blobRemoveCheckBoxesHashMap.put(columnName, (JCheckBox) currentField);
               formPanel.add((JCheckBox) currentField);
            }

            // Normal text field component.
            currentField = new JButton("ARRAY");
            ((JButton) currentField).addActionListener(this);
            if (!addItem)
               ((JButton) currentField).setBounds(x + 145, y, 195, 20);
            else
               ((JButton) currentField).setBounds(x + 145, y, 220, 20);
            componentFocusSequence.add((JButton) currentField);
            formPanel.add((JButton) currentField);
         }

         // TIMESTAMP Type Fields.
         else if (columnTypeName.equals("TIMESTAMP") || columnTypeName.equals("TIMESTAMPTZ")
                  || columnTypeName.equals("TIMESTAMPLTZ") || columnTypeName.equals("TIMESTAMP WITH TIME ZONE")
                  || columnTypeName.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
         {
            currentField = new JTextField();
            if (addItem)
               ((JTextField) currentField).setEnabled(false);
            ((JTextField) currentField).setBounds(x + 145, y, 220, 20);
            componentFocusSequence.add((JTextField) currentField);
            formPanel.add((JTextField) currentField);
         }

         // DATE, DATETIME, YEAR, CHAR, VARCHAR,
         // TINYTEXT, & All Other Type Fields
         else
         {
            if (columnTypeName.indexOf("DATE") != -1 && !columnTypeName.equals("DATETIMEOFFSET"))
            {
               currentField = new JButton(calendarIcon);
               ((JButton) currentField).setBounds(x + 345, y, 20, 20);
               ((JButton) currentField).addActionListener(this);
               calendarButtonHashMap.put((JButton) currentField, columnName);
               formPanel.add((JButton) currentField);
            }

            currentField = new JTextField();
            ((JTextField) currentField).setText("");
            if (columnTypeName.indexOf("DATE") != -1)
               ((JTextField) currentField).setBounds(x + 145, y, 195, 20);
            else
               ((JTextField) currentField).setBounds(x + 145, y, 220, 20);
            componentFocusSequence.add((JTextField) currentField);
            formPanel.add((JTextField) currentField);
         }

         // Build a list and moving to next object's position.
         fieldHashMap.put(columnName, (JComponent) currentField);
         if (y > (((formFields.size() + 1) / 2) * 28) - 20)
         {
            x = 395;
            y = 20;
         }
         else
            y += 28;
      }
      // Setup a scrollpane just in case sizing does not properly give
      // a large enough frame. Also in case user resizes frame.
      formPanel.setPreferredSize(new Dimension(780, formFields.size() / 2 * 32));

      JScrollPane formScrollPane = new JScrollPane(formPanel);
      formScrollPane.getVerticalScrollBar().setUnitIncrement(28);
      mainPanel.add(formScrollPane, BorderLayout.CENTER);

      // Creating Action Buttons & Limit Components.

      JPanel actionButtonPanel = new JPanel();
      formPanel.setBorder(BorderFactory.createRaisedBevelBorder());

      // Cancel Button
      resource = resourceBundle.getResourceString("TableEntryForm.button.Cancel", "Cancel");
      cancelButton = new JButton(resource);
      cancelButton.addActionListener(this);
      componentFocusSequence.add(cancelButton);
      actionButtonPanel.add(cancelButton);

      // Add/Update Button & Limit Components
      if (addItem)
      {
         resource = resourceBundle.getResourceString("TableEntryForm.button.Add", "Add");
         updateButton = new JButton(resource);
      }
      else
      {
         resource = resourceBundle.getResourceString("TableEntryForm.button.Update", "Update");
         updateButton = new JButton(resource);
      }

      updateButton.addActionListener(this);
      componentFocusSequence.add(updateButton);
      actionButtonPanel.add(updateButton);

      if (!addItem)
      {
         JPanel limitPanel = new JPanel();
         limitPanel.setBorder(BorderFactory.createLoweredBevelBorder());

         resource = resourceBundle.getResourceString("TableEntryForm.checkbox.Limit", "Limit");
         limitCheckBox = new JCheckBox(resource, false);
         limitCheckBox.addActionListener(this);
         componentFocusSequence.add(limitCheckBox);
         limitPanel.add(limitCheckBox);

         limitTextField = new JTextField(4);
         limitTextField.setText("1");
         limitTextField.setEnabled(false);
         componentFocusSequence.add(limitTextField);
         limitPanel.add(limitTextField);

         actionButtonPanel.add(limitPanel);
      }

      mainPanel.add(actionButtonPanel, BorderLayout.SOUTH);
      getContentPane().add(mainPanel);
      (this.getRootPane()).setDefaultButton(updateButton);

      // Dummy Button to Fire Events.
      disposeButton = new JButton();

      // Setting the FocusTraversalPolicy
      focusSequence = new AFocusTraversalPolicy(componentFocusSequence);
      this.setFocusTraversalPolicy(focusSequence);
      this.addWindowListener(tableEntryFormFrameListener);
   }

   //==============================================================
   // WindowListener for insuring that the frame is closed, (x), &
   // various objects are prompely disposed.
   //==============================================================

   private transient WindowListener tableEntryFormFrameListener = new WindowAdapter()
   {
      public void windowClosing(WindowEvent e)
      {
         if (dateCalendar != null)
            dateCalendar.dispose();
         if (setDialog != null)
            setDialog.dispose();
         dispose();
      }
   };

   //==============================================================
   // ActionEvent Listener method for detecting the inputs from
   // the panel and directing to the appropriate routine.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object formSource = evt.getSource();

      if (formSource instanceof JButton)
      {
         // Function Button Action
         if (functionButtonHashMap.containsKey((JButton) formSource))
         {
            // Collect the function operator.
            String columnName = functionButtonHashMap.get((JButton) formSource);

            // 2.76 Blob Function Warning.
            if (Utils.isBlob(columnClassHashMap.get(columnName), columnTypeNameHashMap.get(columnName)))
            {
               String message;

               message = resourceBundle.getResourceString("TableEntryForm.dialogmessage.WarningFunction",
                  "Warning Function Operations on Blob Data NOT Tested! Likely Data Corruption!");

               JOptionPane.showMessageDialog(null, message, resourceAlert, JOptionPane.ERROR_MESSAGE);
            }
            selectFunctionOperator(columnName);
         }

         // Blob, Text, MediumText, & LongText Button Actions
         else if (fieldHashMap.containsValue((JButton) formSource))
         {
            // Open Blob, Raw, & Image File Directly.
            if (((JButton) evt.getSource()).getText().indexOf("BLOB") != -1
                || ((JButton) evt.getSource()).getText().indexOf("BYTEA") != -1
                || ((JButton) evt.getSource()).getText().indexOf("BINARY") != -1
                || ((JButton) evt.getSource()).getText().indexOf("RAW") != -1
                || ((JButton) evt.getSource()).getText().indexOf("IMAGE") != -1
                || ((JButton) evt.getSource()).getText().indexOf("BIT DATA") != -1)
               openBlobTextField(formSource);

            // Open Text entry or open file if desired.
            else
            {
               JEditorPane editorPane;
               InputDialog textDialog;
               String textContent;
               JMenuBar editorMenuBar;
               
               // Create an EditorPane to view/edit content.
               
               textContent = ((String) blobBytesHashMap.get((JButton) formSource));
               editorPane = new JEditorPane("text/plain", textContent);
               editorPane.addMouseListener(Ajqvue.getPopupMenuListener());

               textDialog = Utils.createTextDialog(true, editorPane);

               editorMenuBar = Utils.createEditMenu(true);
               textDialog.setJMenuBar(editorMenuBar);
               textDialog.pack();
               textDialog.center();
               textDialog.setVisible(true);

               // Check to see if save data is desired.
               if (textDialog.isActionResult())
               {
                  blobBytesHashMap.put((JButton) formSource, editorPane.getText());
                  if (((JButton) evt.getSource()).getText().indexOf("TEXT") != -1)
                     ((JButton) formSource).setText("TEXT " + editorPane.getText().length() + " Bytes");
                  else if (((JButton) evt.getSource()).getText().indexOf("CLOB") != -1)
                     ((JButton) formSource).setText("CLOB " + editorPane.getText().length() + " Bytes");
                  // Array
                  else
                     ((JButton) formSource).setText("ARRAY Data");
               }
               else if (!textDialog.getActionResult().equals("close"))
                  openBlobTextField(formSource);

               textDialog.dispose();
            }
         }

         // Date Field Button Action
         else if (calendarButtonHashMap.containsKey((JButton) formSource))
         {
            // Collect the info needed to pass to the DateFieldCalendar class.
            Object columnName = calendarButtonHashMap.get((JButton) formSource);
            String columnTypeName = columnTypeNameHashMap.get(columnName);

            // Date selection frame.
            dateCalendar = new DateFieldCalendar(this, columnName, columnTypeName);
            dateCalendar.setResizable(false);
            dateCalendar.pack();
            dateCalendar.center();
            dateCalendar.setVisible(true);
         }

         // Set Field Button Action
         else if (setButtonHashMap.containsKey((JButton) formSource))
         {
            // Collect the info needed to pass to the DateFieldCalendar class.
            Object columnName = setButtonHashMap.get((JButton) formSource);

            // Set list selection frame.
            setDialog = new SetListDialog(this, columnName, setFieldsHashMap.get(columnName));
            setDialog.setSize(new Dimension(325, 200));
            setDialog.center();
            setDialog.setVisible(true);
         }

         // Cancel Button Action
         else if (formSource == cancelButton)
         {
            if (dateCalendar != null)
               dateCalendar.dispose();
            if (setDialog != null)
               setDialog.dispose();
            dispose();
         }

         // Update Button Action
         else if (formSource == updateButton)
         {
            setVisible(false);
            validEntry = false;

            if (dateCalendar != null)
               dateCalendar.dispose();
            if (setDialog != null)
               setDialog.dispose();

            DBTablesPanel.startStatusTimer();

            Thread processEntryThread = new Thread(new Runnable()
            {
               public void run()
               {
                  addUpdateTableEntry();

                  if (validEntry)
                  {
                     // Make sure start clean
                     // in the viewForm for blob/text.
                     tableViewForm.clearBlobBytesHashMap();

                     // Notify Calling Panel to
                     // update item table.
                     disposeButton.doClick();
                     dispose();
                  }
                  DBTablesPanel.stopStatusTimer();
               }
            }, "TableEntryForm.processEntryThread");
            processEntryThread.start();
         }
      }

      // Limit TextField Access
      if (formSource instanceof JCheckBox)
      {
         if (formSource == limitCheckBox)
         {
            if (limitCheckBox.isSelected())
               limitTextField.setEnabled(true);
            else
            {
               limitTextField.setText("1");
               limitTextField.setEnabled(false);
            }
         }
      }
   }

   //==============================================================
   // Class method to obtain data for a blog or text field from a
   // selected input file.
   //==============================================================

   protected void openBlobTextField(Object formSource)
   {
      // Class Method Instance
      String fileName;
      String message;
      byte[] inBytes;

      // Choosing the file to import data from.
      JFileChooser importData = new JFileChooser();
      int result = importData.showOpenDialog(null);

      // Looks like might be good file name so lets check
      // and then set the blob data
      if (result == JFileChooser.APPROVE_OPTION)
      {
         fileName = importData.getSelectedFile().getName();
         fileName = importData.getCurrentDirectory() + "/" + fileName;
         // System.out.println(fileName);

         if (!fileName.equals(""))
         {
            // Blob data
            if (((JButton) formSource).getText().indexOf("BLOB") != -1
                 || ((JButton) formSource).getText().indexOf("BYTEA") != -1
                 || ((JButton) formSource).getText().indexOf("BINARY") != -1
                 || ((JButton) formSource).getText().indexOf("RAW") != -1
                 || ((JButton) formSource).getText().indexOf("IMAGE") != -1
                 || ((JButton) formSource).getText().indexOf("BIT DATA") != -1)
            {
               FileInputStream fileStream = null;
               BufferedInputStream filebuff = null;
               
               try
               {
                  // Setting up InputStreams
                  fileStream = new FileInputStream(fileName);
                  filebuff = new BufferedInputStream(fileStream);
                  inBytes = new byte[filebuff.available()];

                  // Reading the Specified Input File and Placing
                  // Data Into a Byte Array.

                  int bytesRead = filebuff.read(inBytes);
                  
                  if (bytesRead != -1)
                  {
                     blobBytesHashMap.put((JButton) formSource, inBytes);
                     
                     if (((JButton) formSource).getText().indexOf("BLOB") != -1)
                        ((JButton) formSource).setText("BLOB " + bytesRead + " Bytes");
                     else if (((JButton) formSource).getText().indexOf("BYTEA") != -1)
                        ((JButton) formSource).setText("BYTEA " + bytesRead + " Bytes");
                     else if (((JButton) formSource).getText().indexOf("BINARY") != -1)
                        ((JButton) formSource).setText("BINARY " + bytesRead + " Bytes");
                     else if (((JButton) formSource).getText().indexOf("RAW") != -1)
                        ((JButton) formSource).setText("RAW " + bytesRead + " Bytes");
                     else if (((JButton) formSource).getText().indexOf("IMAGE") != -1)
                        ((JButton) formSource).setText("IMAGE " + bytesRead + " Bytes");
                     else
                        ((JButton) formSource).setText("BIT DATA " + bytesRead + " Bytes");
                  } 
               }
               catch (IOException e)
               {
                  message = resourceBundle.getResourceString("TableEntryForm.dialogmessage.ErrorReading",
                        "Error Reading Data File");
                  JOptionPane.showMessageDialog(null, message + ": " + fileName, resourceAlert,
                                                JOptionPane.ERROR_MESSAGE);
               }
               finally
               {
                  try
                  {
                     if (filebuff != null)
                        filebuff.close();
                  }
                  catch (IOException ioe)
                  {
                     if (Ajqvue.getDebug())
                        System.out.println("TableEntryForm openBlobTextField() "
                                           + "Failed to Close BufferedInputStream. "
                                           + ioe);
                  }
                  finally
                  {
                     try
                     {
                        if (fileStream != null)
                           fileStream.close();
                     }
                     catch (IOException ioe)
                     {
                        if (Ajqvue.getDebug())
                           System.out.println("TableEntryForm openBlobTextField() "
                                              + "Failed to Close FileStream. "
                                              + ioe);
                     }
                  }  
               }  
            }
            // Text data
            else
            {
               FileReader fileReader = null;
               BufferedReader filebuff = null;
               
               try
               {
                  // Setting up InputReader
                  fileReader = new FileReader(fileName);
                  filebuff = new BufferedReader(fileReader);
                  StringBuffer textString;
                  String inputString;

                  // Reading the Specified Input File and Placing
                  // Data Into a StringBuffer.

                  textString = new StringBuffer();

                  while ((inputString = filebuff.readLine()) != null)
                     textString.append(inputString + "\n");

                  blobBytesHashMap.put((JButton) formSource, textString.toString());
                  
                  if (((JButton) formSource).getText().indexOf("CLOB") != -1)
                     ((JButton) formSource).setText("CLOB " + textString.length() + " Bytes");
                  else
                     ((JButton) formSource).setText("TEXT " + textString.length() + " Bytes");
               }
               catch (IOException e)
               {
                  message = resourceBundle.getResourceString("TableEntryForm.dialogmessage.ErrorReading",
                                                                 "Error Reading Data File");
                  JOptionPane.showMessageDialog(null, message + ": " + fileName, resourceAlert,
                                                JOptionPane.ERROR_MESSAGE);
               }
               finally
               {
                  try
                  {
                     if (filebuff != null)
                        filebuff.close();
                  }
                  catch (IOException ioe)
                  {
                     if (Ajqvue.getDebug())
                        System.out.println("TableEntryForm openBlobTextField() "
                                           + "Failed to Close BufferedReader. "
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
                           System.out.println("TableEntryForm openBlobTextField() "
                                              + "Failed to Close FileReader. "
                                              + ioe);
                     }
                  }  
               }
            }
         }
      }
   }

   //==============================================================
   // Class method to process the data in the table form to add or
   // update an item.
   //==============================================================

   private void addUpdateTableEntry()
   {
      // Method Instances
      Statement sqlStatement;
      PreparedStatement prepared_sqlStatement;
      Iterator<String> keyIterator, columnNamesIterator;
      
      String schemaName;
      String tableName;
      String columnName;
      String columnClass;
      int columnSQLType;
      String columnTypeName;
      StringBuffer sqlStatementString;
      String sqlFieldNamesString;
      String sqlValuesString;

      String currentKey_ColumnName;
      String currentDB_ColumnName;
      Object currentContentData;
      String dateString, timeString;
      String message;
      boolean isTextField;
      boolean isBlobField;
      boolean isArrayField;
      int columnSize;
      int keyColumn = 0;

      // Get Connection to Database.
      Connection db_Connection = ConnectionManager.getConnection("TableEntryForm addUpdateTableEntry()");

      if (db_Connection == null)
      {
         validEntry = false;
         return;
      }

      // Form Processing.
      try
      {
         db_Connection.setAutoCommit(false);
         sqlStatement = db_Connection.createStatement();
         sqlStatementString = new StringBuffer();

         // Only MySQL & PostgreSQL support.
         if (dataSourceType.equals(ConnectionManager.MYSQL)
             || dataSourceType.equals(ConnectionManager.MARIADB)
             || dataSourceType.equals(ConnectionManager.POSTGRESQL))
            sqlStatement.executeUpdate("BEGIN");

         // ====================
         // Adding an entry.

         if (addItem)
         {
            // Beginner SQL statement creation.
            sqlStatementString.append("INSERT INTO " + sqlTable + " ");
            sqlFieldNamesString = "(";
            sqlValuesString = "VALUES (";

            columnNamesIterator = formFields.iterator();

            while (columnNamesIterator.hasNext())
            {
               // Prepping some instances for making things more clear
               // and easier.

               columnName = columnNamesIterator.next();
               columnClass = columnClassHashMap.get(columnName);
               columnSQLType = columnSQLTypeHashMap.get(columnName);
               columnTypeName = columnTypeNameHashMap.get(columnName);
               columnSize = (columnSizeHashMap.get(columnName)).intValue();
               isTextField = Utils.isText(columnClass, columnTypeName, true, columnSize);
               isBlobField = Utils.isBlob(columnClass, columnTypeName);
               isArrayField = (columnClass.indexOf("Array") != -1 || columnClass.indexOf("Object") != -1)
                               && columnTypeName.indexOf("_") != -1;

               // Composing intial SQL prepareStatement with special
               // consideration
               // for certain types of entries/fields.

               // Empty entry field, or MS Access Autoincrement field.
               
               if ((!isTextField && !isBlobField && !isArrayField && !functionsHashMap.containsKey(columnName))
                     && ((getFormField(columnName).equals(""))
                          || ((dataSourceType.equals(ConnectionManager.MSACCESS)
                               || dataSourceType.equals(ConnectionManager.MSSQL))
                              && getFormField(columnName).toLowerCase(Locale.ENGLISH).equals("auto"))))
               {
                  // Do Nothing, Field Takes Default.
               }

               // Explicit Default Entry Field

               else if (!isTextField && !isBlobField && !isArrayField
                        && getFormField(columnName).toLowerCase(Locale.ENGLISH).equals("default"))
               {
                  sqlFieldNamesString += identifierQuoteString + columnNamesHashMap.get(columnName)
                                         + identifierQuoteString + ", ";
                  sqlValuesString += "default, ";
               }

               // Null Entry Field

               else if (!isTextField && !isBlobField && !isArrayField
                        && getFormField(columnName).toLowerCase(Locale.ENGLISH).equals("null"))
               {
                  sqlFieldNamesString += identifierQuoteString + columnNamesHashMap.get(columnName)
                                         + identifierQuoteString + ", ";
                  sqlValuesString += "null, ";
               }

               // Normal/special Data Entry Fields

               else
               {
                  // Add the current field that will be inserted.
                  sqlFieldNamesString += identifierQuoteString + columnNamesHashMap.get(columnName)
                                         + identifierQuoteString + ", ";

                  // AutoIncremnt, sequenced fields.
                  if (autoIncrementHashMap.containsKey(columnName))
                  {
                     if (getFormField(columnName).toLowerCase(Locale.ENGLISH).equals("auto"))
                     {
                        if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
                        {
                           schemaName = sqlTable.substring(0, sqlTable.indexOf(".") + 2);
                           tableName = (sqlTable.substring(sqlTable.indexOf(".") + 1)).replaceAll(
                                        identifierQuoteString, "");

                           sqlValuesString += "nextval('" + schemaName + tableName + "_"
                                              + columnNamesHashMap.get(columnName) + "_seq\"'), ";
                        }
                        else if (dataSourceType.equals(ConnectionManager.ORACLE))
                        {
                           schemaName = sqlTable.substring(0, sqlTable.indexOf(".") + 2);
                           tableName = (sqlTable.substring(sqlTable.indexOf(".") + 1)).replaceAll(
                                        identifierQuoteString, "");

                           sqlValuesString += autoIncrementHashMap.get(columnName) + ".NEXTVAL, ";
                        }
                        else if (dataSourceType.equals(ConnectionManager.DERBY))
                        {
                           sqlValuesString += "DEFAULT, ";
                        }
                        else
                           sqlValuesString += "null, ";
                     }
                     else
                        sqlValuesString += "?, ";
                  }

                  // TimeStamp fields.
                  else if (columnTypeName.indexOf("TIMESTAMP") != -1)
                  {
                     if (dataSourceType.equals(ConnectionManager.ORACLE))
                        sqlValuesString += "SYSTIMESTAMP, ";
                     else if (dataSourceType.equals(ConnectionManager.DERBY))
                        sqlValuesString += "CURRENT_TIMESTAMP, ";
                     else if (dataSourceType.equals(ConnectionManager.SQLITE))
                        sqlValuesString += "STRFTIME('%Y-%m-%d %H:%M:%S.%f', 'now', 'localtime'), ";
                     else
                        sqlValuesString += "NOW(), ";
                  }

                  // Special fields that can not be represented
                  // by java types.

                  // PostgreSQL Interval fields.
                  else if (columnTypeName.equals("INTERVAL"))
                  {
                     sqlValuesString += "'" + getFormField(columnName) + "', ";
                  }

                  // PostgreSQL Bit & HSQL2 BIT, Bit Varying fields.
                  else if ((columnTypeName.indexOf("BIT") != -1
                            && dataSourceType.equals(ConnectionManager.POSTGRESQL)
                            && columnTypeName.indexOf("_") == -1)
                           || (columnTypeName.equals("BIT") || (columnTypeName.equals("BIT VARYING"))
                               && dataSourceType.equals(ConnectionManager.HSQL2)))
                  {
                     sqlValuesString += "B'" + getFormField(columnName) + "', ";
                  }

                  // PostgreSQL Geometric fields.
                  else if (columnTypeName.equals("POINT") || columnTypeName.equals("LSEG")
                           || columnTypeName.equals("BOX") || columnTypeName.equals("PATH")
                           || columnTypeName.equals("POLYGON") || columnTypeName.equals("CIRCLE"))
                  {
                     sqlValuesString += "'" + getFormField(columnName) + "', ";
                  }

                  // PostgreSQL Network Address fields.
                  else if (columnTypeName.equals("CIDR") || columnTypeName.equals("INET")
                           || columnTypeName.equals("MACADDR"))
                  {
                     sqlValuesString += "'" + getFormField(columnName) + "', ";
                  }

                  // Oracle BFILE fields.
                  else if (columnTypeName.equals("BFILE"))
                  {
                     String directoryName, fileName;
                     int commaIndex;

                     if (getFormField(columnName).indexOf(',') != -1)
                     {
                        commaIndex = getFormField(columnName).indexOf(',');
                        directoryName = getFormField(columnName).substring(0, commaIndex - 1);
                        fileName = getFormField(columnName).substring((commaIndex + 1));

                        sqlValuesString += "BFILENAME('" + directoryName + "', '" + fileName + "'), ";
                     }
                     else
                     {
                        JOptionPane.showMessageDialog(null, resourceInvalidInput + " " + columnName
                                                            + ", " + resourceType + ": " + columnTypeName,
                                                            resourceAlert, JOptionPane.ERROR_MESSAGE);
                        validEntry = false;
                        setVisible(true);
                        db_Connection.rollback();
                        sqlStatement.close();
                        db_Connection.setAutoCommit(true);
                        ConnectionManager.closeConnection(db_Connection,
                           "TableEntryForm addUpdateTableEntry()");
                        return;
                     }
                  }
                  
                  // MS_Access Blob Empty entry field.
                  else if (dataSourceType.equals(ConnectionManager.MSACCESS) && isBlobField
                           && fieldHashMap.get(columnName) != null
                           && (blobBytesHashMap.get((JButton) fieldHashMap.get(columnName)) == null))
                  {
                     sqlValuesString += "null, ";
                  }

                  else if (isArrayField)
                  {
                     // Make sure text button is present
                     if (fieldHashMap.get(columnName) != null)
                     {
                        // Check to see if data has been put in field.
                        if (blobBytesHashMap.get((JButton) fieldHashMap.get(columnName)) != null)
                        {
                           // Finally check to see if its an update and if so
                           // then is
                           // data to be updated or removed.
                           JCheckBox currentRemoveBlobCheckBox = blobRemoveCheckBoxesHashMap.get(columnName);
                           if (currentRemoveBlobCheckBox != null)
                           {
                              if (currentRemoveBlobCheckBox.isSelected())
                                 sqlValuesString += "null, ";
                              else
                                 sqlValuesString += "'" + getFormFieldText(columnName) + "', ";
                           }
                           else
                              sqlValuesString += "'" + getFormFieldText(columnName) + "', ";
                        }
                        else
                           sqlValuesString += "null, ";
                     }
                     else
                        sqlValuesString += "null, ";
                  }

                  // Function or Normal Fields
                  else
                  {
                     // Implement function operator as needed.
                     if (functionsHashMap.containsKey(columnName))
                        sqlValuesString += createFunctionSQLStatement(columnName);
                     else
                     {
                        if (dataSourceType.equals(ConnectionManager.POSTGRESQL)
                            && columnClass.indexOf("Object") != -1)
                           sqlValuesString += "?::" + identifierQuoteString
                                              + columnTypeNameHashMap.get(columnName)
                                              + identifierQuoteString + ", ";
                        else
                           sqlValuesString += "?, ";
                     }
                  }
               }
            }
            // Concatenate everything together.
            if (sqlFieldNamesString.length() >= 3)
               sqlFieldNamesString = sqlFieldNamesString.substring(0, sqlFieldNamesString.length() - 2) + ")";
            else
               sqlFieldNamesString += ")";

            if (sqlValuesString.length() > 8)
               sqlValuesString = sqlValuesString.substring(0, sqlValuesString.length() - 2) + ")";
            else
               sqlValuesString += ")";

            sqlStatementString.append(sqlFieldNamesString + " " + sqlValuesString);
         }

         // =====================
         // Updating an entry.

         else
         {
            // Beginner SQL statement creation.
            sqlStatementString.append("UPDATE " + sqlTable + " SET ");

            columnNamesIterator = formFields.iterator();

            while (columnNamesIterator.hasNext())
            {
               // Prepping some instances for making things more clear
               // and easier.

               columnName = columnNamesIterator.next();
               columnClass = columnClassHashMap.get(columnName);
               columnSQLType = columnSQLTypeHashMap.get(columnName);
               columnTypeName = columnTypeNameHashMap.get(columnName);
               columnSize = (columnSizeHashMap.get(columnName)).intValue();
               isTextField = Utils.isText(columnClass, columnTypeName, true, columnSize);
               isBlobField = Utils.isBlob(columnClass, columnTypeName);
               isArrayField = (columnClass.indexOf("Array") != -1 || columnClass.indexOf("Object") != -1)
                              && columnTypeName.indexOf("_") != -1;

               // Empty entry field.

               if (!isTextField && !isBlobField && !isArrayField && !functionsHashMap.containsKey(columnName)
                   && getFormField(columnName).equals(""))
               {
                  // Do Nothing, Field Takes Default.
               }

               // Explicit Default Entry Field

               else if (!isTextField && !isBlobField && !isArrayField
                        && getFormField(columnName).toLowerCase(Locale.ENGLISH).equals("default"))
               {
                  sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                            + identifierQuoteString + "=default, ");
               }

               // Null Entry Field

               else if (!isTextField && !isBlobField && !isArrayField
                        && getFormField(columnName).toLowerCase(Locale.ENGLISH).equals("null"))
               {
                  sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                            + identifierQuoteString + "=null, ");
               }

               // Normal/special Data Entry Fields

               else
               {
                  // AutoIncremnt, sequenced fields.
                  if (autoIncrementHashMap.containsKey(columnName))
                  {
                     if (getFormField(columnName).toLowerCase(Locale.ENGLISH).equals(
                                                              "AUTO".toLowerCase(Locale.ENGLISH)))
                     {
                        if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
                        {
                           schemaName = sqlTable.substring(0, sqlTable.indexOf(".") + 2);
                           tableName = (sqlTable.substring(sqlTable.indexOf(".") + 1)).replaceAll(
                              identifierQuoteString, "");

                           sqlStatementString.append("nextval('" + schemaName + tableName + "_"
                                                     + columnNamesHashMap.get(columnName) + "_seq\"'), ");
                        }
                        else if (dataSourceType.equals(ConnectionManager.ORACLE))
                        {
                           schemaName = sqlTable.substring(0, sqlTable.indexOf(".") + 2);
                           tableName = (sqlTable.substring(sqlTable.indexOf(".") + 1)).replaceAll(
                              identifierQuoteString, "");

                           sqlStatementString.append(autoIncrementHashMap.get(columnName) + ".NEXTVAL, ");
                        }
                        else
                           sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                                     + identifierQuoteString + "=null, ");
                     }
                     else
                     {
                        // Can't Update autoincrement in MS SQL & Access.
                        if (!dataSourceType.equals(ConnectionManager.MSACCESS)
                            && !dataSourceType.equals(ConnectionManager.MSSQL))
                           sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                                     + identifierQuoteString + "=?, ");
                     }
                  }

                  // Special fields that can not be represented
                  // by java types.

                  // PostgreSQL Interval fields.
                  else if (columnTypeName.equals("INTERVAL"))
                  {
                     sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                               + identifierQuoteString + "='" + getFormField(columnName)
                                               + "', ");
                  }

                  // PostgreSQL Bit & HSQL2 BIT, Bit Varying fields.
                  else if ((columnTypeName.indexOf("BIT") != -1
                            && dataSourceType.equals(ConnectionManager.POSTGRESQL)
                            && columnTypeName.indexOf("_") == -1)
                            || (columnTypeName.equalsIgnoreCase("BIT") || (columnTypeName.equals("BIT VARYING"))
                                  && dataSourceType.equals(ConnectionManager.HSQL2)))
                  {
                     sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                               + identifierQuoteString + "=B'" + getFormField(columnName)
                                               + "', ");
                  }

                  // PostgreSQL Geometric fields.
                  else if (columnTypeName.equals("POINT") || columnTypeName.equals("LSEG")
                           || columnTypeName.equals("BOX") || columnTypeName.equals("PATH")
                           || columnTypeName.equals("POLYGON") || columnTypeName.equals("CIRCLE"))
                  {
                     sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                               + identifierQuoteString + "='" + getFormField(columnName)
                                               + "', ");
                  }

                  // PostgreSQL Network Address fields.
                  else if (columnTypeName.equals("CIDR") || columnTypeName.equals("INET")
                           || columnTypeName.equals("MACADDR"))
                  {
                     sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                               + identifierQuoteString + "='" + getFormField(columnName)
                                               + "', ");
                  }

                  // Oracle BFILE fields.
                  else if (columnTypeName.equals("BFILE"))
                  {
                     // Do nothing not supported.
                  }
                  
                  // MS_Access Blob Empty entry field or remove.
                  else if ((dataSourceType.equals(ConnectionManager.MSACCESS) && isBlobField) &&
                           ((fieldHashMap.get(columnName) != null && (blobBytesHashMap.get((JButton) fieldHashMap.get(columnName)) == null))
                            || (blobRemoveCheckBoxesHashMap.get(columnName) != null && blobRemoveCheckBoxesHashMap.get(columnName).isSelected())))
                  {
                     sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                               + identifierQuoteString + "=null, ");  
                  }

                  else if (isArrayField)
                  {
                     // Make sure text button is present
                     if (fieldHashMap.get(columnName) != null)
                     {
                        // Check to see if data has been put in field.
                        if (blobBytesHashMap.get((JButton) fieldHashMap.get(columnName)) != null)
                        {
                           // Finally check to see if its an update and if so
                           // then is
                           // data to be updated or removed.
                           JCheckBox currentRemoveBlobCheckBox = blobRemoveCheckBoxesHashMap.get(columnName);
                           if (currentRemoveBlobCheckBox != null)
                           {
                              if (currentRemoveBlobCheckBox.isSelected())
                                 sqlStatementString.append(identifierQuoteString
                                                           + columnNamesHashMap.get(columnName)
                                                           + identifierQuoteString + "=null, ");
                              else
                                 sqlStatementString.append(identifierQuoteString
                                                           + columnNamesHashMap.get(columnName)
                                                           + identifierQuoteString + "='"
                                                           + getFormFieldText(columnName) + "', ");
                           }
                           else
                              sqlStatementString.append(identifierQuoteString
                                                        + columnNamesHashMap.get(columnName)
                                                        + identifierQuoteString + "='"
                                                        + getFormFieldText(columnName) + "', ");
                        }
                        else
                           sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                                     + identifierQuoteString + "=null, ");
                     }
                     else
                        sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                                  + identifierQuoteString + "=null, ");
                  }

                  // Function or Normal Fields
                  else
                  {
                     // Implement function operator as needed.
                     if (functionsHashMap.containsKey(columnName))
                     {
                        sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                                  + identifierQuoteString + "=");
                        sqlStatementString.append(createFunctionSQLStatement(columnName));
                     }
                     else
                     {
                        if (dataSourceType.equals(ConnectionManager.POSTGRESQL)
                            && columnClass.indexOf("Object") != -1)
                            sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                                      + identifierQuoteString +"=?::" + identifierQuoteString
                                                      + columnTypeNameHashMap.get(columnName)
                                                      + identifierQuoteString + ", ");
                        else
                           sqlStatementString.append(identifierQuoteString + columnNamesHashMap.get(columnName)
                                                     + identifierQuoteString + "=?, ");
                     }
                  }
               }
            }
            sqlStatementString.delete((sqlStatementString.length() - 2), sqlStatementString.length());
            sqlStatementString.append(" WHERE ");

            // ==============================
            // Continuing with construction
            // of the prepareStatment.

            // Find the column holding the key entry.
            keyIterator = primaryKeys.iterator();

            while (keyIterator.hasNext())
            {
               currentKey_ColumnName = keyIterator.next();

               for (int i = 0; i < selectedTableTabPanel.getListTable().getColumnCount(); i++)
               {
                  currentDB_ColumnName = columnNamesHashMap.get(selectedTableTabPanel.getListTable()
                        .getColumnName(i));
                  if (currentDB_ColumnName.equals(currentKey_ColumnName))
                     keyColumn = i;
               }

               // Key found so obtain info.
               currentContentData = selectedTableTabPanel.getListTable().getValueAt(selectedRow, keyColumn);

               // Special case where the table defines a blob/text for the key.
               if (currentContentData instanceof BlobTextKey)
               {
                  String keyString = ((BlobTextKey) currentContentData).getContent();
                  keyString = keyString.replaceAll("'", "''");

                  sqlStatementString.append(identifierQuoteString + currentKey_ColumnName + identifierQuoteString
                                            + " LIKE '" + keyString + "%' AND ");
               }
               // Normal key.
               else
               {
                  // Handle null content properly.
                  if ((currentContentData + "").toLowerCase(Locale.ENGLISH).equals("null"))
                  {
                     currentContentData = "IS NULL";
                     sqlStatementString.append(identifierQuoteString + currentKey_ColumnName
                                               + identifierQuoteString + " " + currentContentData
                                               + " AND ");
                  }
                  else
                  {
                     // Escape single quotes.
                     columnClass = columnClassHashMap.get(selectedTableTabPanel
                           .parseColumnNameField(currentKey_ColumnName));
                     if (columnClass.indexOf("String") != -1)
                        currentContentData = ((String) currentContentData).replaceAll("'", "''");

                     columnTypeName = columnTypeNameHashMap.get(selectedTableTabPanel
                           .parseColumnNameField(currentKey_ColumnName));
                     
                     if (columnTypeName.indexOf("DATE") != -1)
                     {
                        if (dataSourceType.equals(ConnectionManager.ORACLE))
                        {
                           currentContentData = "TO_DATE('"
                              + Utils.convertViewDateString_To_DBDateString(
                                 currentContentData + "",
                                 DBTablesPanel.getGeneralDBProperties().getViewDateFormat())
                                 + "', 'YYYY-MM-dd')";
                        }
                        else
                           currentContentData = "'" + Utils.convertViewDateString_To_DBDateString(
                              currentContentData + "", DBTablesPanel.getGeneralDBProperties().getViewDateFormat())
                              + "'";
                        
                        sqlStatementString.append(identifierQuoteString + currentKey_ColumnName
                                                  + identifierQuoteString + "="
                                                  + currentContentData + " AND ");
                     }
                     else
                     {
                        // Character data gets single quotes for some databases,
                        // not numbers though.
                        
                        if (dataSourceType.equals(ConnectionManager.MSACCESS)
                            || dataSourceType.equals(ConnectionManager.DERBY)
                            || dataSourceType.indexOf(ConnectionManager.HSQL) != -1)
                        {
                           if (columnClass.toLowerCase(Locale.ENGLISH).indexOf("string") != -1)
                              sqlStatementString.append(identifierQuoteString + currentKey_ColumnName
                                                     + identifierQuoteString + "='"
                                                     + currentContentData + "' AND ");
                           else
                              sqlStatementString.append(identifierQuoteString + currentKey_ColumnName
                                                     + identifierQuoteString + "="
                                                     + currentContentData + " AND ");   
                        }
                        else
                           sqlStatementString.append(identifierQuoteString + currentKey_ColumnName
                              + identifierQuoteString + "='"
                              + currentContentData + "' AND ");
                     }  
                  }
               }
            }
            sqlStatementString.delete((sqlStatementString.length() - 5), sqlStatementString.length());

            // Adding LIMIT expression for supported databases.
            if (dataSourceType.equals(ConnectionManager.MYSQL)
                || dataSourceType.equals(ConnectionManager.MARIADB))
            {
               if (limitCheckBox.isSelected())
               {
                  try
                  {
                     int limitValue = Integer.parseInt(limitTextField.getText());
                     if (limitValue <= 0)
                        Integer.parseInt("a");
                     sqlStatementString.append(" LIMIT " + limitValue);
                  }
                  catch (NumberFormatException e)
                  {
                     message = resourceBundle.getResourceString("TableEntryForm.dialogmessage.InvalidLimit",
                        "Invalid Input for LIMIT value! Must be an UNSIGNED INTEGER Larger Than Zero");
                     
                     JOptionPane.showMessageDialog(null, message, resourceAlert, JOptionPane.ERROR_MESSAGE);
                     
                     validEntry = false;
                     setVisible(true);
                     db_Connection.rollback();
                     sqlStatement.close();
                     db_Connection.setAutoCommit(true);
                     ConnectionManager.closeConnection(db_Connection, "TableEntryForm addUpdateTableEntry()");
                     return;
                  }
               }
               else
               {
                  sqlStatementString.append(" LIMIT 1");
               }
            }
         }
         // System.out.println(sqlStatementString);

         // ======================================================
         // Accessing the database and setting values for each
         // selected entry in the prepareStatement.

         prepared_sqlStatement = db_Connection.prepareStatement(sqlStatementString.toString());
         columnNamesIterator = formFields.iterator();
         int i = 1;

         while (columnNamesIterator.hasNext())
         {
            // Prepping some instances for making things more clear
            // and easier.

            columnName = columnNamesIterator.next();
            columnClass = columnClassHashMap.get(columnName);
            columnSQLType = columnSQLTypeHashMap.get(columnName);
            columnTypeName = columnTypeNameHashMap.get(columnName);
            columnSize = (columnSizeHashMap.get(columnName)).intValue();
            isTextField = Utils.isText(columnClass, columnTypeName, true, columnSize);
            isBlobField = Utils.isBlob(columnClass, columnTypeName);
            isArrayField = (columnClass.indexOf("Array") != -1 || columnClass.indexOf("Object") != -1)
                           && columnTypeName.indexOf("_") != -1;
            // System.out.println(i + " " + columnName + " " + columnClass + " "
            //                    + columnSQLType + " " + columnTypeName);

            // Validating input and setting content to fields

            // Skip Fields That Have Already Been Set.
            if ((!isTextField && !isBlobField)
                && !isArrayField
                && (getFormField(columnName).equals("")
                    || getFormField(columnName).toLowerCase(Locale.ENGLISH).equals("null")
                    || getFormField(columnName).toLowerCase(Locale.ENGLISH).equals("default")
                    || (autoIncrementHashMap.containsKey(columnName)
                        && (dataSourceType.equals(ConnectionManager.MSACCESS)
                            || dataSourceType.equals(ConnectionManager.MSSQL)))))
            {
               // Do Nothing, Field Already Set.
            }

            // Function Operation. Wide Open No Checks.
            else if (functionsHashMap.containsKey(columnName))
            {
               // Do Nothing.
            }

            // Auto-Increment Type Field
            else if (autoIncrementHashMap.containsKey(columnName))
            {
               if (getFormField(columnName).toLowerCase(Locale.ENGLISH).equals("auto"))
               {
                  // Do Nothing.
               }
               else
               {
                  try
                  {
                     int int_value = Integer.parseInt(getFormField(columnName));
                     prepared_sqlStatement.setInt(i++, int_value);
                  }
                  catch (NumberFormatException e)
                  {
                     message = resourceBundle.getResourceString("TableEntryForm.dialogmessage.TypeAuto",
                        "Type: INTEGER or NULL(Auto-Increment)");
                     
                     JOptionPane.showMessageDialog(null, resourceInvalidInput + " " + columnName
                                                   + ", " + message, resourceAlert,
                                                   JOptionPane.ERROR_MESSAGE);
                     
                     validEntry = false;
                     setVisible(true);
                     db_Connection.rollback();
                     prepared_sqlStatement.close();
                     sqlStatement.close();
                     db_Connection.setAutoCommit(true);
                     ConnectionManager.closeConnection(db_Connection,
                                                       "TableEntryForm addUpdateTableEntry()");
                     return;
                  }
               }
            }

            // Numeric Type Fields
            else if (Utils.isNumeric(columnClass, columnSQLType, columnTypeName))
            {
               try
               {
                  String value = (getFormField(columnName)).trim();
                  // System.out.println(columnTypeName + " " + value);

                  // Byte
                  if (columnClass.indexOf("Byte") != -1)
                  {
                     byte byte_value = (Byte.valueOf(value)).byteValue();
                     prepared_sqlStatement.setByte(i++, byte_value);
                  }
                  // Short
                  else if (columnClass.indexOf("Short") != -1)
                  {
                     short short_value = Short.parseShort(value);
                     prepared_sqlStatement.setShort(i++, short_value);
                  }
                  // Integer
                  else if (columnClass.indexOf("Integer") != -1)
                  {
                     int int_value = Integer.parseInt(value);
                     prepared_sqlStatement.setInt(i++, int_value);
                  }
                  // Long
                  else if (columnClass.indexOf("Long") != -1)
                  {
                     long long_value = Long.parseLong(value);
                     prepared_sqlStatement.setLong(i++, long_value);
                  }
                  // Float
                  else if (columnClass.indexOf("Float") != -1)
                  {
                     float float_value = Float.parseFloat(value);
                     prepared_sqlStatement.setFloat(i++, float_value);
                  }
                  // Double
                  else if (columnClass.indexOf("Double") != -1)
                  {
                     double double_value = Double.parseDouble(value);
                     prepared_sqlStatement.setDouble(i++, double_value);
                  }
                  // Must Be BigDecimal
                  else
                  {
                     BigDecimal decimal_value = new BigDecimal(value);
                     prepared_sqlStatement.setBigDecimal(i++, decimal_value);
                  }
               }
               catch (NumberFormatException e)
               {
                  JOptionPane.showMessageDialog(null, resourceInvalidInput + " " + columnName + ", "
                                                      + resourceType + ": " + columnTypeName, resourceAlert,
                                                      JOptionPane.ERROR_MESSAGE);
                  validEntry = false;
                  setVisible(true);
                  db_Connection.rollback();
                  prepared_sqlStatement.close();
                  sqlStatement.close();
                  db_Connection.setAutoCommit(true);
                  ConnectionManager.closeConnection(db_Connection, "TableEntryForm addUpdateTableEntry()");
                  return;
               }
            }

            // Date, Time, DateTime, Timestamp, & Year Type Fields
            else if (columnClass.indexOf("Date") != -1
                     || (columnClass.toUpperCase(Locale.ENGLISH)).indexOf("TIME") != -1)
            {
               String dateTimeFormString = getFormField(columnName).trim();

               try
               {
                  // Date
                  if (columnTypeName.equals("DATE"))
                  {
                     java.sql.Date dateValue;

                     // Check for some kind of valid input.
                     if (!(dateTimeFormString.length() >= 10 && dateTimeFormString.length() < 12))
                        java.sql.Date.valueOf("error");

                     dateString = Utils.convertViewDateString_To_DBDateString(
                        dateTimeFormString, DBTablesPanel.getGeneralDBProperties().getViewDateFormat());
                     
                     if (dataSourceType.equals(ConnectionManager.SQLITE)
                         && columnSQLType == Types.VARCHAR)
                        prepared_sqlStatement.setString(i++, dateString);
                     else
                     {
                        dateValue = java.sql.Date.valueOf(dateString);
                        prepared_sqlStatement.setDate(i++, dateValue);
                     }
                  }
                  // Time
                  else if (columnTypeName.equals("TIME") || columnTypeName.equals("TIMETZ")
                           || columnTypeName.equals("TIME WITH TIME ZONE"))
                  {
                     java.sql.Time timeValue;

                     // Check for some kind of valid input.
                     if (dateTimeFormString.length() < 8)
                        timeValue = Time.valueOf("error");

                     // HSQL2
                     if (columnTypeName.equals("TIME WITH TIME ZONE"))
                        prepared_sqlStatement.setString(i++, dateTimeFormString);
                     else
                     {
                        timeValue = java.sql.Time.valueOf(dateTimeFormString.substring(0, 8));
                        prepared_sqlStatement.setTime(i++, timeValue);
                     }
                  }
                  // DateTime
                  else if (columnTypeName.indexOf("DATETIME") != -1)
                  {
                     java.sql.Timestamp dateTimeValue;
                     dateString = "";
                     timeString = "";

                     // Check for some kind of valid input.
                     if (dateTimeFormString.indexOf(" ") == -1 || dateTimeFormString.length() < 10)
                        java.sql.Date.valueOf("error");

                     dateString = dateTimeFormString.substring(0, dateTimeFormString.indexOf(" "));
                     dateString = Utils.convertViewDateString_To_DBDateString(dateString,
                        DBTablesPanel.getGeneralDBProperties().getViewDateFormat());
                     timeString = getFormField(columnName).substring(dateTimeFormString.indexOf(" "));
                     
                     if (columnTypeName.equals("DATETIMEOFFSET"))
                        prepared_sqlStatement.setString(i++, dateString + timeString);
                     else
                     {
                        dateTimeValue = java.sql.Timestamp.valueOf(dateString + timeString);
                        prepared_sqlStatement.setTimestamp(i++, dateTimeValue); 
                     }
                  }
                  // Timestamp
                  else if (columnTypeName.equals("TIMESTAMP") || columnTypeName.equals("TIMESTAMP WITH TIME ZONE")
                           || columnTypeName.equals("TIMESTAMPTZ") || columnTypeName.equals("TIMESTAMPLTZ")
                           || columnTypeName.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                  {
                     if (columnTypeName.equals("TIMESTAMPLTZ")
                         || columnTypeName.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
                        Utils.setLocalTimeZone(sqlStatement);

                     if (addItem)
                     {
                        // Do Nothing, Already set to NOW().
                     }
                     // Allow editing a timestamp.
                     else
                     {
                        SimpleDateFormat timeStampFormat;
                        java.sql.Timestamp dateTimeValue;
                        java.util.Date dateParse;

                        try
                        {
                           // Create a Timestamp Format.
                           if (columnTypeName.equals("TIMESTAMP"))
                           {
                              // Old MySQL Database Requirement, 4.x.
                              if (dataSourceType.equals(ConnectionManager.MYSQL)
                                  || dataSourceType.equals(ConnectionManager.MARIADB))
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
                                 // All current coloumnSizes for MySQL > 5.0 & MariaDB Should be 19.
                                 else
                                    timeStampFormat = new SimpleDateFormat(
                                       DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                                       + " HH:mm:ss");
                              }
                              else if (dataSourceType.equals(ConnectionManager.SQLITE))
                                 timeStampFormat = new SimpleDateFormat(
                                    DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                                    + " HH:mm:ss.SSS");
                              else
                                 timeStampFormat = new SimpleDateFormat(
                                    DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                                    + " HH:mm:ss");
                           }
                           else
                           {
                              if (columnTypeName.equals("TIMESTAMPLTZ")
                                  || columnTypeName.equals("TIMESTAMP WITH LOCAL TIMEZONE"))
                                 timeStampFormat = new SimpleDateFormat(
                                    DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                                    + " HH:mm:ss Z");
                              else
                                 timeStampFormat = new SimpleDateFormat(
                                    DBTablesPanel.getGeneralDBProperties().getViewDateFormat()
                                    + " HH:mm:ss z");
                           }

                           // Parse the TimeStamp Format.
                           if (columnTypeName.equals("TIMESTAMPLTZ"))
                           {
                              dateString = dateTimeFormString;
                              dateString = dateString.substring(0, dateString.lastIndexOf(':'))
                                           + dateString.substring(dateString.lastIndexOf(':') + 1);
                              dateParse = timeStampFormat.parse(dateString);
                           }
                           else
                              dateParse = timeStampFormat.parse(dateTimeFormString);

                           dateTimeValue = new java.sql.Timestamp(dateParse.getTime());
                           // System.out.println(dateTimeValue);
                           prepared_sqlStatement.setTimestamp(i++, dateTimeValue);
                        }
                        catch (ParseException e)
                        {
                           throw (new IllegalArgumentException(e + ""));
                        }
                     }
                  }
                  // Must be Year
                  else
                  {
                     dateString = dateTimeFormString;
                     java.sql.Date yearValue = java.sql.Date.valueOf(dateString + "-01-01");
                     prepared_sqlStatement.setString(i++, yearValue.toString().substring(0, 4));
                  }
               }
               catch (IllegalArgumentException e)
               {
                  message = resourceBundle.getResourceString("TableEntryForm.dialogmessage.InvalidDateTime",
                     "Invalid Date/Time Input for Field");
                  
                  JOptionPane.showMessageDialog(null, message + " " + columnName
                                                      + ", " + resourceType + ": " + columnTypeName,
                                                      resourceAlert, JOptionPane.ERROR_MESSAGE);
                  validEntry = false;
                  setVisible(true);
                  db_Connection.rollback();
                  prepared_sqlStatement.close();
                  sqlStatement.close();
                  db_Connection.setAutoCommit(true);
                  ConnectionManager.closeConnection(db_Connection, "TableEntryForm addUpdateTableEntry()");
                  return;
               }
            }

            // Interval Type Fields
            else if (columnTypeName.equals("INTERVAL"))
            {
               // Do Nothing, Already set since undefined type.
            }

            // Blob/Bytea/Binary/Raw Type Fields
            else if (isBlobField)
            {
               // Make sure blob button is present
               if (fieldHashMap.get(columnName) != null)
               {
                  // Check to see if data has been put in field.
                  if (blobBytesHashMap.get((JButton) fieldHashMap.get(columnName)) != null)
                  {
                     // Finally check to see if its an update and if so then is
                     // data to be updated or removed.
                     JCheckBox currentRemoveBlobCheckBox = blobRemoveCheckBoxesHashMap.get(columnName);
                     
                     if (currentRemoveBlobCheckBox != null)
                     {
                        if (currentRemoveBlobCheckBox.isSelected())
                        {
                           if (dataSourceType.equals(ConnectionManager.MSACCESS))
                           {
                              // Do Nothing Already Set.
                           }
                           else
                              prepared_sqlStatement.setBytes(i++, null);
                        }
                        else
                        {
                           prepared_sqlStatement.setBytes(i++, (getFormFieldBlob(columnName)));
                        }
                     }
                     else
                     {
                        if (dataSourceType.equals(ConnectionManager.HSQL2))
                        {
                           SerialBlob blobData = new SerialBlob(getFormFieldBlob(columnName));
                           prepared_sqlStatement.setBlob(i++, blobData);
                           
                        }
                        else
                           prepared_sqlStatement.setBytes(i++, (getFormFieldBlob(columnName)));
                     }
                  }
                  else
                  {
                     if (dataSourceType.equals(ConnectionManager.MSACCESS))
                     {
                        // Do Nothing Already Set
                     }
                     else
                        prepared_sqlStatement.setBytes(i++, null);
                  }
               }
               else
                  prepared_sqlStatement.setBytes(i++, null);
            }

            // PostgreSQL Geometric fields.
            else if (columnTypeName.equals("POINT") || columnTypeName.equals("LSEG") || columnTypeName.equals("BOX")
                     || columnTypeName.equals("PATH") || columnTypeName.equals("POLYGON")
                     || columnTypeName.equals("CIRCLE"))
            {
               // Do Nothing. Already set since undefined type.
            }

            // Network Type Fields
            else if (columnTypeName.equals("CIDR") || columnTypeName.equals("INET") || columnTypeName.equals("MACADDR"))
            {
               // Do Nothing, Already set since undefined type.
            }

            // Array Type Fields
            else if (isArrayField)
            {
               // Do Nothing, Already set.
            }

            // Boolean Type Fields
            else if (columnClass.indexOf("Boolean") != -1 && columnSize == 1)
            {
               boolean boolean_value;
               if (getFormField(columnName).equals("TRUE"))
                  boolean_value = true;
               else
                  boolean_value = false;
               prepared_sqlStatement.setBoolean(i++, boolean_value);
            }

            // Bit Type Fields
            else if (columnTypeName.indexOf("BIT") != -1 && columnTypeName.indexOf("_") == -1)
            {
               if ((dataSourceType.equals(ConnectionManager.POSTGRESQL)) ||
                   (dataSourceType.equals(ConnectionManager.HSQL2)
                    && (columnTypeName.equals("BIT VARYING") || columnTypeName.equals("BIT"))))
               {
                  // Do Nothing. Already set since undefined type.
               }
               else
               {
                  try
                  {
                     String bitString = getFormField(columnName);
                     int int_value = (Integer.valueOf(bitString, 2)).intValue();
                     prepared_sqlStatement.setInt(i++, int_value);

                  }
                  catch (NumberFormatException e)
                  {
                     message = resourceBundle.getResourceString("TableEntryForm.dialogmessage.TypeBit",
                                                                "Type: Bit String");
                     
                     JOptionPane.showMessageDialog(null, resourceInvalidInput + " " + columnName
                                                         + ", " + message, resourceAlert,
                                                         JOptionPane.ERROR_MESSAGE);
                     
                     validEntry = false;
                     setVisible(true);
                     db_Connection.rollback();
                     prepared_sqlStatement.close();
                     sqlStatement.close();
                     db_Connection.setAutoCommit(true);
                     ConnectionManager.closeConnection(db_Connection, "TableEntryForm addUpdateTableEntry()");
                     return;
                  }
               }
            }

            // TEXT/CLOB/ARRAY Type Fields
            else if (isTextField)
            {
               // Make sure text button is present
               if (fieldHashMap.get(columnName) != null)
               {
                  // Check to see if data has been put in field.
                  if (blobBytesHashMap.get((JButton) fieldHashMap.get(columnName)) != null)
                  {
                     // Finally check to see if its an update and if so then is
                     // data to be updated or removed.
                     JCheckBox currentRemoveBlobCheckBox = blobRemoveCheckBoxesHashMap
                           .get(columnName);
                     if (currentRemoveBlobCheckBox != null)
                     {
                        if (currentRemoveBlobCheckBox.isSelected())
                           prepared_sqlStatement.setString(i++, null);
                        else
                           prepared_sqlStatement.setString(i++, (getFormFieldText(columnName)));
                     }
                     else
                        prepared_sqlStatement.setString(i++, (getFormFieldText(columnName)));
                  }
                  else
                     prepared_sqlStatement.setString(i++, null);
               }
               else
                  prepared_sqlStatement.setString(i++, null);
            }

            // BFILE Type Fields
            else if (columnTypeName.equals("BFILE"))
            {
               // Do Nothing, Already set as locator, filename.
            }

            // Standard fall through, should catch generic
            // text table fields.
            else
               prepared_sqlStatement.setString(i++, getFormField(columnName));
         }
         prepared_sqlStatement.executeUpdate();
         db_Connection.commit();

         prepared_sqlStatement.close();
         sqlStatement.close();

         db_Connection.setAutoCommit(true);
         ConnectionManager.closeConnection(db_Connection, "TableEntryForm addUpdateTableEntry()");
         validEntry = true;
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableEntryForm addUpdateTableEntry()");
         try
         {
            validEntry = false;
            setVisible(true);
            db_Connection.rollback();
            db_Connection.setAutoCommit(true);
            ConnectionManager.closeConnection(db_Connection,
                                              "TableEntryForm addUpdateTableEntry() rollback");
         }
         catch (SQLException error)
         {
            ConnectionManager.displaySQLErrors(e, "TableEntryForm addUpdateTableEntry() rollback failed");
         }
      }
   }

   //==============================================================
   // Class method for selecting a function operator to be applied
   // to a specific field on add or edit.
   //==============================================================

   private void selectFunctionOperator(Object columnName)
   {
      // Class Instances
      String errorString, currentFunction;
      String functionsFileName = "functions.txt";
      String functionsFileString;
      String title, buttonOK, buttonCancel;
      String message;

      File functionsFile;
      FileReader fileReader;
      BufferedReader bufferedReader;
      ArrayList<String> functions;

      InputDialog functionSelectDialog;
      JComboBox<Object> functionsComboBox;
      ImageIcon functionsPaletteIcon;

      // Setting up to read the user's .ajqvue home directory
      // functions file. Creating one from the installed files
      // as needed.

      // Select database appropriate functions file.
      if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
         functionsFileName = "postgresql_" + functionsFileName;
      else if (dataSourceType.indexOf(ConnectionManager.HSQL) != -1)
         functionsFileName = "hsql_" + functionsFileName;
      else if (dataSourceType.equals(ConnectionManager.ORACLE))
         functionsFileName = "oracle_" + functionsFileName;
      else if (dataSourceType.equals(ConnectionManager.SQLITE))
         functionsFileName = "sqlite_" + functionsFileName;
      else if (dataSourceType.equals(ConnectionManager.MSACCESS))
         functionsFileName = "msaccess_" + functionsFileName;
      else if (dataSourceType.equals(ConnectionManager.MSSQL))
         functionsFileName = "mssql_" + functionsFileName;
      else if (dataSourceType.equals(ConnectionManager.DERBY))
         functionsFileName = "derby_" + functionsFileName;
      else if (dataSourceType.equals(ConnectionManager.H2))
         functionsFileName = "h2_" + functionsFileName;
      else
         functionsFileName = "mysql_" + functionsFileName;

      functionsFileString = Utils.getAjqvueConfDirectory() + Utils.getFileSeparator()
                            + functionsFileName;

      functionsFile = new File(functionsFileString);
      // System.out.println(functionsFileString);
      
      fileReader = null;
      bufferedReader = null;

      try
      {
         if (functionsFile.createNewFile())
         {
            // System.out.println("File Does Not Exist, Creating.");
            byte[] fileData = ReadDataFile.mainReadDataString("functions"
                                                              + Utils.getFileSeparator()
                                                              + functionsFileName, false);

            if (fileData != null)
               WriteDataFile.mainWriteDataString(functionsFileString, fileData, false);
            else
            { 
               message = resourceBundle.getResourceString("TableEntryForm.dialogmessage.FailedFunction",
                  "Failed to Open Sample functions.txt File");
               
               errorString = message + "\n";
               JOptionPane.showMessageDialog(null, errorString, resourceAlert, JOptionPane.ERROR_MESSAGE);
               return;
            }
         }

         // Looks like there is a functions file so
         // create a set of elements, functions, to be
         // used to create a vector object.

         fileReader = new FileReader(functionsFileString);
         bufferedReader = new BufferedReader(fileReader);

         functions = new ArrayList <String>();

         while ((currentFunction = bufferedReader.readLine()) != null)
            functions.add(currentFunction);

      }
      catch (IOException ioe)
      {
         message = resourceBundle.getResourceString("TableEntryForm.dialogmessage.ErrorDirectory",
            "Error in creating home directory .ajqvue functions file");
         
         errorString = message + "\n" + ioe;
         JOptionPane.showMessageDialog(null, errorString, resourceAlert, JOptionPane.ERROR_MESSAGE);
         return;
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
               System.out.println("TableEntryForm openBlobTextField() "
                                  + "Failed to Close BufferedReader. "
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
                  System.out.println("TableEntryForm openBlobTextField() "
                                     + "Failed to Close FileReader. "
                                     + ioe);
            }
         }  
      }

      // Create a dialog with a combobox for allowing
      // the user to select the function operator.

      functionsComboBox = new JComboBox<Object>(functions.toArray());
      functionsComboBox.setBorder(BorderFactory.createLoweredBevelBorder());
      Object[] content = {functionsComboBox};
      functionsPaletteIcon = resourceBundle.getResourceImage(iconsDirectory + "functionsPaletteIcon.gif");

      title = resourceBundle.getResourceString("TableEntryForm.dialogtitle.FunctionSelection",
                                                  "Function Selection");
      buttonOK = resourceBundle.getResourceString("TableEntryForm.dialogbutton.OK", "OK");
      
      buttonCancel = resourceBundle.getResourceString("TableEntryForm.dialogbutton.Cancel", "Cancel");
      
      functionSelectDialog = new InputDialog(null, title, buttonOK, buttonCancel, content,
                                             functionsPaletteIcon);

      functionSelectDialog.pack();
      functionSelectDialog.center();
      functionSelectDialog.setResizable(false);
      functionSelectDialog.setVisible(true);

      // If ok proceed to store the fields function to be
      // retrieved later during and add/update. Cancel
      // removes function operator.
      if (functionSelectDialog.isActionResult())
      {
         functionsHashMap.put(columnName, (String) functionsComboBox.getSelectedItem());
         // System.out.println(columnName + " " +
         // functionsComboBox.getSelectedItem());
      }
      else
         functionsHashMap.remove(columnName);

      functionSelectDialog.dispose();
   }

   //==============================================================
   // Class method for selecting a function operator to be applied
   // to a specific field on add or edit.
   //==============================================================

   private String createFunctionSQLStatement(String columnName)
   {
      // Class Method Instances
      StringBuffer sqlStatementString;
      String columnClass;
      int columnSQLType;
      String columnTypeName;
      int columnSize;
      String valueDelimiter;
      
      // Setup to accomodate the non-quoting of number
      // type fields. HSQLDB2 & MS_Access Issue.
      
      columnClass = columnClassHashMap.get(columnName);
      columnSQLType = columnSQLTypeHashMap.get(columnName);
      columnTypeName = columnTypeNameHashMap.get(columnName);
      columnSize = columnSizeHashMap.get(columnName);
      
      if (Utils.isNumeric(columnClass, columnSQLType, columnTypeName))
         valueDelimiter = "";
      else
         valueDelimiter = "'";
         

      // Collect the function operator.
      sqlStatementString = new StringBuffer();
      sqlStatementString.append((String) functionsHashMap.get(columnName));

      // Get correct form data, TEXT, Blob, or Normal entry.
      // Take into account a possible no argument for the function.

      if (Utils.isText(columnClass, columnTypeName, true, columnSize))
      {
         if (getFormFieldText(columnName) == null || getFormFieldText(columnName).length() == 0)
            sqlStatementString.append("(), ");
         else
            sqlStatementString.append("('" + getFormFieldText(columnName) + "'), ");
      }
      // What needs fixed here? What does it mean to apply a function on a
      // binary field. The Java 6.0 API allow a change of binary data to a
      // string of characters, but what operation would be performed? The
      // current 1.4 API kept here just returns a pointer to the object. Broken.
      
      else if (Utils.isBlob(columnClass, columnTypeName))
      {
         if (getFormField(columnName) == null || getFormFieldBlob(columnName).length == 0)
            sqlStatementString.append("(), ");
         else
            sqlStatementString.append("('" + Arrays.toString(getFormFieldBlob(columnName)) + "'), ");
      }
      else
      {
         if (getFormField(columnName) == null || getFormField(columnName).length() == 0)
         {
            // What other functions do not use ()?
            if (dataSourceType.equals(ConnectionManager.ORACLE)
                  && functionsHashMap.get(columnName).equals("SYSTIMESTAMP"))
               sqlStatementString.append(", ");
            else
               sqlStatementString.append("(), ");
         }
         else
         {
            // Take into count multiple arguments
            sqlStatementString.append("(");

            String[] argumentString = ((String) getFormField(columnName)).split(",");

            for (int i = 0; i < argumentString.length; i++)
            {
               if (i < (argumentString.length - 1))
                  sqlStatementString.append(valueDelimiter + argumentString[i] + valueDelimiter + ",");
               else
                  sqlStatementString.append(valueDelimiter + argumentString[i] + valueDelimiter + "), ");
            }
         }
      }
      return sqlStatementString.toString();
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
   // Class method for outside classes to obtain the dispose button
   // so a notification can place to notify that a valid update
   // has taken place so that table data can updated in summary
   // table.
   //==============================================================

   public JButton getDisposeButton()
   {
      return disposeButton;
   }

   //==============================================================
   // Class method to get the string content of a field
   //==============================================================

   @SuppressWarnings("unchecked")
   public String getFormField(String columnName)
   {
      if (fieldHashMap.get(columnName) != null)
      {
         if (columnEnumHashMap.containsKey(columnName))
            return (String) ((JComboBox<Object>) fieldHashMap.get(columnName)).getSelectedItem();
         else
            return ((JTextField) fieldHashMap.get(columnName)).getText();
      }
      else
         return "";
   }

   //==============================================================
   // Class method to get content into a selected JButton
   // hashmap as bytes.
   //==============================================================

   public byte[] getFormFieldBlob(Object itemName)
   {
      return (byte[]) blobBytesHashMap.get((JButton) fieldHashMap.get(itemName));
   }

   //==============================================================
   // Class method to get content into a selected JButton hashmap
   // as a string.
   //==============================================================

   public String getFormFieldText(Object itemName)
   {
      return (String) blobBytesHashMap.get((JButton) fieldHashMap.get(itemName));
   }

   //==============================================================
   // Class method to place string content into a selected
   // TextField.
   //==============================================================

   public void setFormField(Object columnName, Object content)
   {
      // Method Instances.
      String columnClass;
      String columnTypeName;
      int columnSize;

      if (fieldHashMap.get(columnName) != null)
      {
         columnClass = columnClassHashMap.get(columnName);
         columnTypeName = columnTypeNameHashMap.get(columnName);
         columnSize = columnSizeHashMap.get(columnName);

         // Blob/Bytea Button
         if (Utils.isBlob(columnClass, columnTypeName))
            ((JButton) fieldHashMap.get(columnName)).setText((String) content);

         // Text Button
         else if (Utils.isText(columnClass, columnTypeName, true, columnSize))
            ((JButton) fieldHashMap.get(columnName)).setText((String) content);

         // Array Button
         else if ((columnClass.indexOf("Array") != -1 || columnClass.indexOf("Object") != -1)
                  && columnTypeName.indexOf("_") != -1)
            ((JButton) fieldHashMap.get(columnName)).setText((String) content);

         // Standard TextField
         else
         {
            if (columnTypeName.equals("BFILE") && !addItem)
            {
               // Do nothing for edits not supported.
            }
            else
            {
               ((JTextField) fieldHashMap.get(columnName)).setText((String) content);
               ((JTextField) fieldHashMap.get(columnName)).setCaretPosition(0);
            }
         }
      }
   }

   //==============================================================
   // Class method to place string content into a selected
   // JComboBox.
   //==============================================================

   @SuppressWarnings("unchecked")
   public void setFormField(Object columnName, String data)
   {
      ((JComboBox<Object>) fieldHashMap.get(columnName)).addItem(data);
      ((JComboBox<Object>) fieldHashMap.get(columnName)).setSelectedItem(data);
   }

   //==============================================================
   // Class method to place content into a selected JButton
   // hashmap as bytes.
   //==============================================================

   public void setFormFieldBlob(Object itemName, byte[] content)
   {
      blobBytesHashMap.put((JButton) fieldHashMap.get(itemName), content);
   }

   //==============================================================
   // Class method to place content into a selected JButton hashmap
   // as a string.
   //==============================================================

   public void setFormFieldText(Object itemName, String content)
   {
      blobBytesHashMap.put((JButton) fieldHashMap.get(itemName), content);
   }

   //==============================================================
   // Class method to place string content into a selected
   // JComboBox.
   //==============================================================

   @SuppressWarnings("unchecked")
   public void setComboBoxField(Object columnName, ArrayList<String> content, Object data)
   {
      Iterator<String> contentsIterator = content.iterator();
      
      while (contentsIterator.hasNext())
         ((JComboBox<Object>) fieldHashMap.get(columnName)).addItem(contentsIterator.next());

      if (data == null)
         ((JComboBox<Object>) fieldHashMap.get(columnName)).setSelectedIndex(0);
      else
         ((JComboBox<Object>) fieldHashMap.get(columnName)).setSelectedItem(data);
   }

   //==============================================================
   // Class method to place string content into a selected Set
   // type fields and fill a vector with the complete set contents.
   //==============================================================

   public void setSetFields(Object columnName, ArrayList<String> content, Object data)
   {
      // Method Instances.
      ArrayList<String> setFields = new ArrayList <String>();
      
      Iterator<String> contentsIterator = content.iterator();

      while (contentsIterator.hasNext())
         setFields.add(contentsIterator.next());
      setFieldsHashMap.put(columnName, setFields);

      if (data != null)
         ((JTextField) fieldHashMap.get(columnName)).setText((String) data);
   }
}
