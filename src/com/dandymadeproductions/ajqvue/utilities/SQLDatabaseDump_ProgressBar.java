//=================================================================
//                SQLDatabaseDump_ProgressBar
//=================================================================
//
//    This class provides the frame and components to create a 
// progress bar used in the dumping of data during a  SQL database
// dump.
//
//             << SQLDatabaseDump_ProgressBar.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
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
// Version 1.0 Production SQLDatabaseDump_ProgressBar Class.
//
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.dandymadeproductions.ajqvue.Ajqvue;

/**
 *    The SQLDatabaseDump_ProgressBar class provides the frame and
 * components to create a  progress bar used in the dumping of data
 * during a SQL database dump.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class SQLDatabaseDump_ProgressBar extends JFrame implements ActionListener
{
   // Class Instances
   private static final long serialVersionUID = -7079123716177930494L;

   private AResourceBundle resourceBundle;
   private JProgressBar databaseDumpProgressBar;
   private JButton cancelButton;
   
   private JProgressBar tableDumpProgressBar;
   private JLabel tableIdentifierLabel;
   
   private int databaseTaskLength;
   private int tableTaskLength;
   private boolean taskCanceled;

   //==============================================================
   // SQLDatabaseDump_ProgressBar Constructor.
   //==============================================================

   public SQLDatabaseDump_ProgressBar(String progressTitle)
   {
      super(progressTitle);
      
      // Constructor Instances
      
      String iconsDirectory, resource;
      ImageIcon sqlDatabaseDumpIcon;
      JPanel mainPanel, databaseStatusPanel, tableStatusPanel;
      
      // Setup various instances to be used in the panel.
      
      resourceBundle = Ajqvue.getResourceBundle();
      iconsDirectory = Utils.getIconsDirectory() + Utils.getFileSeparator();
      sqlDatabaseDumpIcon = resourceBundle.getResourceImage(iconsDirectory + "sqlDatabaseDumpIcon.gif");
      
      // Begin creating the GUI.
      
      mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createEtchedBorder());

      // North database cancel and progress indicator components.
      
      databaseStatusPanel = new JPanel();
      databaseStatusPanel.setBorder(BorderFactory
                             .createCompoundBorder(BorderFactory.createEtchedBorder(),
                                                   BorderFactory.createRaisedBevelBorder()));
      
      resource = resourceBundle.getResourceString("ProgressBar.button.Cancel", "Cancel");
      cancelButton = new JButton(resource);
      cancelButton.setFocusable(false);
      cancelButton.setActionCommand("cancel");
      cancelButton.addActionListener(this);
      databaseStatusPanel.add(cancelButton);

      databaseDumpProgressBar = new JProgressBar();
      databaseDumpProgressBar.setIndeterminate(true);
      databaseDumpProgressBar.setValue(0);
      databaseDumpProgressBar.setStringPainted(true);
      databaseStatusPanel.add(databaseDumpProgressBar);
      
      mainPanel.add(databaseStatusPanel, BorderLayout.NORTH);
      
      // Center table identifier label and progress indicator
      // components.
      
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();
      
      tableStatusPanel = new JPanel(gridbag);
      tableStatusPanel.setBorder(BorderFactory
                          .createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
                                                BorderFactory.createEmptyBorder(10,10,10,10)));
 
      JLabel dumpLabel = new JLabel("Dumping Table:");
      dumpLabel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
      
      buildConstraints(constraints, 0, 0, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(dumpLabel, constraints);
      tableStatusPanel.add(dumpLabel);
      
      tableIdentifierLabel = new JLabel(" ");
      tableIdentifierLabel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
      
      buildConstraints(constraints, 0, 1, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(tableIdentifierLabel, constraints);
      tableStatusPanel.add(tableIdentifierLabel);

      tableDumpProgressBar = new JProgressBar();
      tableDumpProgressBar.setIndeterminate(true);
      tableDumpProgressBar.setValue(0);
      tableDumpProgressBar.setStringPainted(true);
      
      buildConstraints(constraints, 0, 2, 1, 1, 100, 100);
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(tableDumpProgressBar, constraints);
      tableStatusPanel.add(tableDumpProgressBar);
      
      mainPanel.add(tableStatusPanel, BorderLayout.CENTER);
      
      // South label icon animation.
      
      JLabel animationLabel = new JLabel(sqlDatabaseDumpIcon);
      mainPanel.add(animationLabel, BorderLayout.SOUTH);
      
      this.getContentPane().add(mainPanel);

      taskCanceled = false;
   }

   //==============================================================
   // ActionEvent Listener method for detecting the inputs
   // from the panel and taking the appropriate action.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      taskCanceled = true;
   }
   
   //==============================================================
   // Class Method for helping the parameters in gridbag.
   //==============================================================

   void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, double wx, double wy)
   {
      gbc.gridx = gx;
      gbc.gridy = gy;
      gbc.gridwidth = gw;
      gbc.gridheight = gh;
      gbc.weightx = wx;
      gbc.weighty = wy;
   }

   //==============================================================
   // Class Method to set the taskCanceled instance.
   //==============================================================

   public void setCanceled(boolean value)
   {
      taskCanceled = value;
   }

   //==============================================================
   // Class Method to set the database dump status bar display value.
   //==============================================================

   public void setDatabaseDumpCurrentValue(int value)
   {
      databaseDumpProgressBar.setValue(value);
   }
   
   //==============================================================
   // Class Method to set the table dump status bar display value.
   //==============================================================

   public void setTableDumpCurrentValue(String tableName, int value)
   {
      tableIdentifierLabel.setText(tableName);
      tableDumpProgressBar.setValue(value);
   }

   //==============================================================
   // Class Method to set the maximum value of the status bar
   // that will be displayed for the database dump.
   //==============================================================

   public void setDatabaseDumpTaskLength(int value)
   {
      databaseDumpProgressBar.setIndeterminate(false);
      databaseTaskLength = value;
      databaseDumpProgressBar.setMaximum(databaseTaskLength);
   }
   
   //==============================================================
   // Class Method to set the maximum value of the status bar
   // that will be displayed for the table dump.
   //==============================================================

   public void setTableDumpTaskLength(int value)
   {
      tableDumpProgressBar.setIndeterminate(false);
      tableTaskLength = value;
      tableDumpProgressBar.setMaximum(tableTaskLength);
   }

   //==============================================================
   // Class Method to return the indication of the button cancel
   // being pressed.
   //==============================================================

   public boolean isCanceled()
   {
      return taskCanceled;
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
