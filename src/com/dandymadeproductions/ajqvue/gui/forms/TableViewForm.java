///================================================================
//                       Table View Form
//=================================================================
//
//    This class provides a generic panel in the appearance of a
// form for viewing the current selected item in the TableTabPanel
// summary table.
//
//                 << TableViewForm.java >>
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
// Version 1.0 09/18/2016 Production TableViewForm Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.InputDialog;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The TableViewForm class provides a generic panel in the
 * appearance of a form for viewing the current selected item
 * in the TableTabPanel summary table.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class TableViewForm extends JPanel implements ActionListener, KeyListener
{
   // Class Instances.
   private static final long serialVersionUID = 8521167393446066642L;

   private HashMap<String, JComponent> fieldHashMap;
   private HashMap<JButton, Object> blobBytesHashMap;
   private HashMap<String, String> fieldTypeHashMap;
   private HashMap<String, String> fieldClassHashMap;
   private HashMap<String, Integer> fieldSizeHashMap;
   private JButton previousViewButton, closeViewButton, nextViewButton;
   private AResourceBundle resourceBundle;

   //==============================================================
   // TableViewForm Constructor
   //==============================================================

   public TableViewForm(ArrayList<String> tableColumnNames,
                           HashMap<String, String> tableColumnClass,
                           HashMap<String, String> tableColumnType,
                           HashMap<String, Integer> tableColumnSize,
                           JButton previousViewButton, JButton closeViewButton,
                           JButton nextViewButton)
   {
      this.previousViewButton = previousViewButton;
      this.closeViewButton = closeViewButton;
      this.nextViewButton = nextViewButton;
      fieldTypeHashMap = tableColumnType;
      fieldClassHashMap = tableColumnClass;
      fieldSizeHashMap = tableColumnSize;

      // Constructor Instances
      GridBagLayout gridbag;
      GridBagConstraints constraints;

      Iterator<String> columnNamesIterator;
      fieldHashMap = new HashMap <String, JComponent>();
      blobBytesHashMap = new HashMap <JButton, Object>();
      String itemName, columnClass, columnType;
      Object currentField;
      resourceBundle = Ajqvue.getResourceBundle();

      // General Panel Configurations
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createRaisedBevelBorder());

      // Creating the Labels and Text Fields/Buttons in
      // the form panel.
      
      gridbag = new GridBagLayout();
      constraints = new GridBagConstraints();

      JPanel formPanel = new JPanel();
      formPanel.setLayout(gridbag);
      formPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                                                             BorderFactory.createEmptyBorder(10, 0, 10, 12)));
      formPanel.addMouseListener(Ajqvue.getPopupMenuListener());
      
      columnNamesIterator = tableColumnNames.iterator();
      int x = 0;
      int y = 0;

      while (columnNamesIterator.hasNext())
      {
         itemName = columnNamesIterator.next();
         columnClass = fieldClassHashMap.get(itemName);
         columnType = fieldTypeHashMap.get(itemName);
         // System.out.println(x + " " + y + " " + itemName + " " + columnClass + " " + columnType);

         // =================================
         // Labels
         // =================================

         JLabel currentLabel = new JLabel(itemName);
         currentLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
         
         buildConstraints(constraints, x, y, 1, 1, 15, 100);
         constraints.fill = GridBagConstraints.HORIZONTAL;
         constraints.anchor = GridBagConstraints.WEST;
         gridbag.setConstraints(currentLabel, constraints);
         formPanel.add(currentLabel);

         // =====================================
         // Blob/Bytea/Binary/Bit Data/CLOB/Text/Array
         // Buttons & TextFields
         // =====================================

         if ((columnClass.indexOf("String") == -1 && columnType.indexOf("BLOB") != -1)
             || (columnType.indexOf("BYTEA") != -1)
             || (columnType.indexOf("BINARY") != -1)
             || (columnType.indexOf("BIT DATA") != -1)
             || (columnType.indexOf("IMAGE") != -1)
             || (columnType.indexOf("RAW") != -1)
             || (columnType.indexOf("CLOB") != -1))
         {
            currentField = new JButton();
            ((JButton) currentField).addActionListener(this);
         }

         else if ((columnClass.indexOf("String") != -1 &&
                  !columnType.equals("CHAR") &&
                  (fieldSizeHashMap.get(itemName)).intValue() > 255) ||
                   (columnClass.indexOf("String") != -1 && columnType.equals("LONG")))
         {
            currentField = new JButton();
            ((JButton) currentField).addActionListener(this);
         }
         else if ((columnClass.indexOf("Object") != -1 ||
                   columnClass.indexOf("Array") != -1) &&
                  columnType.indexOf("_") != -1)
         {
            currentField = new JButton();
            ((JButton) currentField).addActionListener(this);
         }
         else
         {
            currentField = new JTextField(formPanel.getWidth()/4);
            // ((JTextField) currentField).setEditable(false);
         }
         
         buildConstraints(constraints, x + 1, y, 1, 1, 35, 100);
         constraints.fill = GridBagConstraints.HORIZONTAL;
         constraints.anchor = GridBagConstraints.WEST;
         gridbag.setConstraints((Component)currentField, constraints);
         formPanel.add((JComponent) currentField);
         
         fieldHashMap.put(itemName, (JComponent) currentField);

         // Prepping for next position in the panel.
         if (y > (tableColumnNames.size() / 2) - 1)
         {
            x = 2;
            y = 0;
         }
         else
            y++;
      }

      // Setting the preferredSize so that a scrollpane can be
      // applied to the panel.
      
      formPanel.setPreferredSize(new Dimension(725, (tableColumnNames.size() / 2) * 32));

      JScrollPane formScrollPane = new JScrollPane(formPanel);
      formScrollPane.getVerticalScrollBar().setUnitIncrement(28);
      add(formScrollPane, BorderLayout.CENTER);

      // Creating Action Buttons.

      JPanel actionButtonPanel = new JPanel();

      previousViewButton.addKeyListener(this);
      actionButtonPanel.add(previousViewButton);

      closeViewButton.addKeyListener(this);
      actionButtonPanel.add(closeViewButton);

      nextViewButton.addKeyListener(this);
      actionButtonPanel.add(nextViewButton);

      add(actionButtonPanel, BorderLayout.SOUTH);
   }

   //==============================================================
   // ActionEvent Listener method for detecting the inputs from
   // the panel and directing to the appropriate routine.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object panelSource = evt.getSource();

      // Detecting the selection of one of the binary type
      // or text fields buttons. This allows the saving of
      // the data to a file for viewing or viewing directly
      // in the case of a text field.

      if (panelSource instanceof JButton)
      {
         if (blobBytesHashMap.get((JButton) panelSource) != null)
         {
            // Save Binary/Clob Directly.
            if (((JButton) evt.getSource()).getText().indexOf("BLOB") != -1
                || ((JButton) evt.getSource()).getText().indexOf("BYTEA") != -1
                || ((JButton) evt.getSource()).getText().indexOf("BINARY") != -1
                || ((JButton) evt.getSource()).getText().indexOf("BIT DATA") != -1
                      || ((JButton) evt.getSource()).getText().indexOf("IMAGE") != -1
                || ((JButton) evt.getSource()).getText().indexOf("RAW") != -1)
               saveBlobTextField(panelSource);

            // View Text/Array and Allow Saving if Desired.
            else
            {
               JEditorPane editorPane;
               InputDialog textDialog;
               String textContent;
               JMenuBar editorMenuBar;
               
               // Create an EditorPane to view/edit content.
               
               textContent = ((String) blobBytesHashMap.get((JButton) panelSource));
               editorPane = new JEditorPane("text/plain", textContent);
               editorPane.addMouseListener(Ajqvue.getPopupMenuListener());

               textDialog = Utils.createTextDialog(false, editorPane);

               editorMenuBar = Utils.createEditMenu(false);
               textDialog.setJMenuBar(editorMenuBar);
               textDialog.pack();
               textDialog.center();
               textDialog.setVisible(true);
               
               // Check to see if save data is desired..
               if (textDialog.isActionResult())
                  saveBlobTextField(panelSource);

               textDialog.dispose();
            }
         }
      }
   }

   //==============================================================
   // KeyEvent Listener method for detecting key pressed events of
   // left and right arrows to move to previous next entries in
   // the table.
   //==============================================================

   public void keyPressed(KeyEvent evt)
   {
      int keyCode = evt.getKeyCode();

      // Previous/Next entry views
      if (keyCode == KeyEvent.VK_LEFT)
         previousViewButton.doClick();
      else if (keyCode == KeyEvent.VK_RIGHT)
         nextViewButton.doClick();
   }

   //==============================================================
   // KeyEvent Listener method for detected key released events
   // to full fill KeyListener Interface requirements.
   //==============================================================

   public void keyReleased(KeyEvent evt)
   {
      // Do Nothing
   }

   //==============================================================
   // KeyEvent Listener method for detecting key pressed event,
   // Enter, to be used with the close action.
   //==============================================================

   public void keyTyped(KeyEvent evt)
   {
      // Derived from the searchTextField.
      char keyChar = evt.getKeyChar();

      // Fire the search button as required.
      if (keyChar == KeyEvent.VK_ENTER)
         closeViewButton.doClick();
   }
   
   //================================================================
   // Class Method for helping the parameters in gridbag.
   //================================================================

   private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw,
                                 int gh, double wx, double wy)
   {
      gbc.gridx = gx;
      gbc.gridy = gy;
      gbc.gridwidth = gw;
      gbc.gridheight = gh;
      gbc.weightx = wx;
      gbc.weighty = wy;
   }

   //==============================================================
   // Class method to save data from a binary or text field to a
   // selected output file.
   //==============================================================

   public void saveBlobTextField(Object panelSource)
   {
      // Class Method Instance
      String fileName;
      byte[] buf;
      JFileChooser exportDataChooser;
      int resultsOfFileChooser;
      
      FileOutputStream fileStream;
      BufferedOutputStream filebuff;
      
      // Setting up a file separator instance.
      String fileSeparator = Utils.getFileSeparator();

      // Don't Convert the Binary Data to a String
      if (((JButton) panelSource).getText().indexOf("BLOB") != -1
          || ((JButton) panelSource).getText().indexOf("BYTEA") != -1
          || ((JButton) panelSource).getText().indexOf("BINARY") != -1
          || ((JButton) panelSource).getText().indexOf("BIT DATA") != -1
          || ((JButton) panelSource).getText().indexOf("IMAGE") != -1
          || ((JButton) panelSource).getText().indexOf("RAW") != -1)

         buf = (byte[]) blobBytesHashMap.get((JButton) panelSource);
      else
         buf = ((String) blobBytesHashMap.get((JButton) panelSource)).getBytes();

      // Choosing the file to export data to.
      exportDataChooser = new JFileChooser();
      resultsOfFileChooser = Utils.processFileChooserSelection(null, exportDataChooser);

      // Looks like might be good file name so lets check
      // and then output the blob/text data
      if (resultsOfFileChooser == JFileChooser.APPROVE_OPTION)
      {
         fileName = exportDataChooser.getSelectedFile().getName();
         fileName = exportDataChooser.getCurrentDirectory() + fileSeparator + fileName;
         // System.out.println(fileName);

         if (!fileName.equals(""))
         {
            // Creating the buffered data of the binary/text
            // and outputing.
            
            fileStream = null;
            filebuff = null;
            
            try
            {
               // Setting up OutputStream
               fileStream = new FileOutputStream(fileName);
               filebuff = new BufferedOutputStream(fileStream);

               // Writing to the Specified Ouput File.
               for (int i = 0; i < buf.length; i++)
               {
                  filebuff.write(buf[i]);
                  // System.out.print(buf[i]);
               }
               filebuff.flush();
               fileStream.flush();
            }
            catch (IOException e)
            {
               String resourceMessage, resourceAlert;
               
               resourceAlert = resourceBundle.getResourceString("TableViewForm.dialogtitle.Alert", "Alert");
               resourceMessage = resourceBundle.getResourceString(
                  "TableViewForm.dialogmessage.ErrorWritingDataFile", "Error Writing Data File");
               
               JOptionPane.showMessageDialog(null, resourceMessage + " " + fileName, resourceAlert,
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
                     System.out.println("TableViewForm saveBlobTextField() Failed "
                                        + "to Close FileOutputStream. " + ioe);
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
                        System.out.println("TableViewForm saveBlobTextField() Failed "
                                           + "to Close FileReader. " + ioe);
                  }
               }
            }
         }
         else
         {
            // System.out.println("File NOT Found");
         }
      }
      else
      {
         // System.out.println("File Selection Canceled");
      }
   }

   //==============================================================
   // Class method to clear the blobBytesHashMap. Takes place
   // anytime an edit is made on a table entry.
   //==============================================================
   
   public void clearBlobBytesHashMap()
   {
      blobBytesHashMap = new HashMap <JButton, Object>();
   }
   
   //==============================================================
   // Class method to place text content into a selected
   // TextField/JButton.
   //==============================================================

   public void setFormField(Object itemName, Object content)
   {
      // Method Instances.
      String columnClass, columnType;

      columnClass = fieldClassHashMap.get(itemName);
      columnType = fieldTypeHashMap.get(itemName);

      // Binary Button, Note all data with buttons processed
      // the same just grouping for clarity.
      if ((columnClass.indexOf("String") == -1 && columnType.indexOf("BLOB") != -1)
          || (columnClass.indexOf("BLOB") != -1 && columnType.indexOf("BLOB") != -1)
          || (columnType.indexOf("BYTEA") != -1)
          || (columnType.indexOf("BINARY") != -1)
          || (columnType.indexOf("BIT DATA") != -1)
          || (columnType.indexOf("IMAGE") != -1)
          || (columnType.indexOf("RAW") != -1) || (columnType.indexOf("CLOB") != -1))

         ((JButton) fieldHashMap.get(itemName)).setText((String) content);

      // Text Button, TEXT, MEDIUMTEXT, & LONGTEXT
      else if ((columnClass.indexOf("String") != -1 && !columnType.equals("CHAR") &&
                (fieldSizeHashMap.get(itemName)).intValue() > 255) ||
               (columnClass.indexOf("String") != -1 && columnType.equals("LONG")))
         ((JButton) fieldHashMap.get(itemName)).setText((String) content);

      // Array Button
      else if ((columnClass.indexOf("Object") != -1 || columnClass.indexOf("Array") != -1)
               && columnType.indexOf("_") != -1)
         ((JButton) fieldHashMap.get(itemName)).setText((String) content);

      // Standard TextField
      else
      {
         // Arbitrarly set string to no more than 600 characters, just trying
         // to limit textfield content, and put caret back to beginning.

         if (((String) content).length() > 600)
            ((JTextField) fieldHashMap.get(itemName)).setText(((String) content).substring(0, 599));
         else
            ((JTextField) fieldHashMap.get(itemName)).setText((String) content);
         ((JTextField) fieldHashMap.get(itemName)).setCaretPosition(0);
      }
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
   // Class method to place content into a selected JButton
   // hashmap as a string.
   //==============================================================

   public void setFormFieldText(Object itemName, String content)
   {
      blobBytesHashMap.put((JButton) fieldHashMap.get(itemName), content);
   }

   //==============================================================
   // Class method to place the focus to the close view button.
   //==============================================================

   public void setFocus()
   {
      closeViewButton.requestFocus();
   }
}