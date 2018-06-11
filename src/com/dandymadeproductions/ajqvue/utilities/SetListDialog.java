//=================================================================
//                      SetListDialog Frame.
//=================================================================
//   This class provides a frame for diplaying a JList to allow
// the selection of elements of a Set field.
//
//                   << SetListDialog.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.0 09/17/2016
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
// Version 1.0 Production SetListDialog Class.
//
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.gui.forms.TableEntryForm;

/**
 *    The SetListDialog class provides a frame for diplaying a
 * JList to allow the selection of elements of a Set field.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class SetListDialog extends JFrame implements ActionListener
{
   // =============================================
   // Creation of the necessary class instance
   // variables for the JFrame.
   // =============================================

   private static final long serialVersionUID = 1250119595948982933L;

   private TableEntryForm callingForm;
   private Object columnName;
   private JList<Object> setList;
   private ArrayList<String> listElements;

   private JButton okButton, cancelButton;

   //==============================================================
   // SetListDialog Constructor
   //==============================================================

   public SetListDialog(TableEntryForm callingForm, Object columnName, ArrayList<String> listElements)
   {
      this.callingForm = callingForm;
      this.columnName = columnName;
      this.listElements = listElements;

      // Constructor Instances.
      JPanel mainPanel;
      JPanel centerPanel, southButtonPanel;
      String resource, currentSetContent;
      AResourceBundle resourceBundle;

      String[] setElements;
      int[] listSelectedIndexes;

      // Setting the frame's main layout & title
      
      resourceBundle = Ajqvue.getResourceBundle();
      
      resource = resourceBundle.getResourceString("SetListDialog.title.ElementSelection",
                                                  "Element Selection");
      setTitle(columnName + " " + resource);

      mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());

      // Setting up the center panel to be filled
      // by the list compoenent.
      centerPanel = new JPanel(new GridLayout(1, 1, 2, 2));

      setList = new JList<Object>(listElements.toArray());
      JScrollPane scrollPane = new JScrollPane();
      scrollPane.getViewport().setView(setList);

      // Set the list current selected indexes as needed.
      currentSetContent = callingForm.getFormField((String) columnName);
      setElements = currentSetContent.split("\\,");

      if (setElements.length != 0)
      {
         listSelectedIndexes = new int[setElements.length];

         for (int i = 0; i < listSelectedIndexes.length; i++)
            listSelectedIndexes[i] = listElements.indexOf(setElements[i]);

         setList.setSelectedIndices(listSelectedIndexes);
      }

      // Add List/SrollPane into center main
      // panel.
      centerPanel.add(scrollPane);
      mainPanel.add(centerPanel, BorderLayout.CENTER);

      // South panel with action buttons
      southButtonPanel = new JPanel();
      southButtonPanel.setBorder(BorderFactory.createEtchedBorder());

      resource = resourceBundle.getResourceString("SetListDialog.button.OK", "OK");
      okButton = new JButton(resource);
      okButton.setFocusPainted(false);
      okButton.addActionListener(this);
      southButtonPanel.add(okButton);

      resource = resourceBundle.getResourceString("SetListDialog.button.Cancel", "Cancel");
      cancelButton = new JButton(resource);
      cancelButton.setFocusPainted(false);
      cancelButton.addActionListener(this);
      southButtonPanel.add(cancelButton);

      mainPanel.add(southButtonPanel, BorderLayout.SOUTH);

      // Add everything to frame.
      getContentPane().add(mainPanel);
      this.addWindowListener(setListFrameListener);
   }

   //==============================================================
   // WindowListener for insuring that the frame is closed. (x).
   //==============================================================

   private transient WindowListener setListFrameListener = new WindowAdapter()
   {
      public void windowClosing(WindowEvent e)
      {
         dispose();
      }
   };

   //==============================================================
   // ActionEvent Listener method for determining when the selections
   // have been made so an update can be performed on the correct
   // field in the TableEntryForm.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      Object frameSource = evt.getSource();

      // Class Instances
      int minIndex, maxIndex;
      Object setSelection;

      // Overall action buttons.
      if (frameSource instanceof JButton)
      {
         // OK Button Action.
         if (frameSource == okButton)
         {
            // Set up empty field content and get
            // model.
            setSelection = (String) "";
            ListSelectionModel listModel = setList.getSelectionModel();

            // If one or more of the elements are selected
            // then find it and store for placement in the
            // form field.
            if (!listModel.isSelectionEmpty())
            {
               minIndex = listModel.getMinSelectionIndex();
               maxIndex = listModel.getMaxSelectionIndex();
               for (int i = minIndex; i <= maxIndex; i++)
               {
                  if (listModel.isSelectedIndex(i))
                     setSelection = setSelection + (listElements.get(i) + ",");
               }
               setSelection = ((String) setSelection).substring(0, ((String) setSelection).length() - 1);
            }
            callingForm.setFormField(columnName, setSelection);
            this.dispose();
         }

         // Cancel Buttton Action.
         else if (frameSource == cancelButton)
         {
            this.dispose();
         }
      }
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
