//=================================================================
//            TableFieldProfiler MenuActionListener
//=================================================================
//
//    This class provides the means for controlling the required
// actions needed to execute the various activities used in the
// profiler plugin. The events are generated by the Profiler_MenuBar
// & Profiler_ToolBar classes.
//
//                 << MenuActionListener.java >>
//
//=================================================================
// Copyright (C) 2010-2016 Dana M. Proctor.
// Version 1.9 09/25/2016
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
// Version 1.0 Original DandyDataProfiler MenuActionListener Class.
//         1.1 Changed to Table Field Profiler Code.
//         1.2 Added File Menu Actions and Appropriate Methods to Handle the
//             Events via openAction() & saveAction().
//         1.3 References actionCommand in actionPerformed() to Profiler_MenuBar
//             Action Instances. Removed Action Events File | Open, Save, &
//             Save As. Removed Class Methods openAction() & saveAction().
//         1.4 Added Back Processing for actionCommand File | Open in
//             actionPerformed(). Class Method openAction() Recreated and
//             Modified to Load SQL Statements to Panels.
//         1.5 Added Class Instances chartsPanel & anslysisPanel. Added Arguments
//             TableFieldChartsPanel & TableFieldAnalysisPanel to Constructor.
//             Processing for disposeQueryForm() for Both New Class Instances
//             in actionPerformed().
//         1.6 Added Class Instance clusterPanel and Assigned to Passed Argument
//             in Constructor. Added Detection of Selection of TABLE_FIELD_CLUSTER_
//             ANALYSIS in actionPerformed(). Derived Card String in actionPerformed()
//             From TableFieldProfiler.
//         1.7 Updated Imports in Order Properly Load MyJSQLView Classes Which
//             Changed Packaging for v3.35++. Update in Methods openAction()
//             for Collection of String Resources.
//         1.8 Updated Copyright.
//         1.9 Referenced Imports to Ajquve Application.
//         
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.tablefieldprofiler;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.gui.MenuActionCommands;
import com.dandymadeproductions.ajqvue.utilities.MyJFileFilter;
import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The MenuActionListener class provides the means for controlling the
 * required actions needed to execute the various activities used in the
 * profiler plugin. The events are generated by the Profiler_MenuBar &
 * Profiler_ToolBar classes. 
 * 
 * @author Vivek Singh, Dana M. Proctor
 * @version 1.9 09/25/2016
 */

class MenuActionListener implements ActionListener
{
   private JFrame parent;
   private AResourceBundle resourceBundle;
   private TableFieldChartsPanel chartsPanel;
   private TableFieldAnalysisPanel analysisPanel;
   private TableFieldClusterAnalysisPanel clusterPanel;
   private JPanel mainPanel;
   private CardLayout panelCardLayout;
   private String lastOpenDirectory, currentSelectedPanel;

   //==============================================================
   // MenuActionListener Constructor.
   //==============================================================

   public MenuActionListener(JFrame mainFrame, AResourceBundle resourceBundle,
                             TableFieldChartsPanel chartsPanel, TableFieldAnalysisPanel analysisPanel,
                             TableFieldClusterAnalysisPanel clusterPanel, JPanel dataProfilerMainPanel,
                             CardLayout profilerCardLayout)
   {
      parent = mainFrame;
      this.resourceBundle = resourceBundle;
      this.chartsPanel = chartsPanel;
      this.analysisPanel = analysisPanel;
      this.clusterPanel = clusterPanel;
      mainPanel = dataProfilerMainPanel;
      panelCardLayout = profilerCardLayout;
      
      lastOpenDirectory = "";
      currentSelectedPanel = Profiler_MenuBar.ACTION_FIELD_INFORMATION;
   }

   //==============================================================
   // ActionEvent Listener method for detecting the inputs from the
   // application and directing to the appropriate routine.
   //==============================================================

   public void actionPerformed(ActionEvent evt)
   {
      // Setting up some needed instance variables.
      String actionCommand;
      Object item;

      // Initializing
      item = evt.getSource();

      if (item instanceof JMenuItem)
         actionCommand = ((JMenuItem) item).getActionCommand();
      else if (item instanceof JButton)
         actionCommand = ((JButton) item).getActionCommand();
      else
         actionCommand = "";
      // System.out.println(actionCommand);

      // Directing Appropriate Actions.
      
      // =============================
      // File Actions
      
      // Open
      if (actionCommand.equals(Profiler_MenuBar.ACTION_FILE_OPEN))
      {
         openAction(parent, currentSelectedPanel);
         return;
      }

      // Exit (This one caught by Ajqvue.)
      if (actionCommand.equals(MenuActionCommands.ACTION_EXIT))
      {
         // System.out.println("File Exit");
         return;
      }

      // =============================
      // Profiler Analysis Selection

      // Information/Analysis
      if (actionCommand.equals(Profiler_MenuBar.ACTION_FIELD_INFORMATION)
          || actionCommand.equals(Profiler_MenuBar.ACTION_FIELD_NUMBER_ANALYSIS)
          || actionCommand.equals(Profiler_MenuBar.ACTION_FIELD_CLUSTER_ANALYSIS))
      {
         if (actionCommand.equals(Profiler_MenuBar.ACTION_FIELD_INFORMATION))
         {
            analysisPanel.disposeQueryConditionForm();
            panelCardLayout.show(mainPanel, TableFieldProfiler.INFORMATION_CARD);
         }
         else if (actionCommand.equals(Profiler_MenuBar.ACTION_FIELD_NUMBER_ANALYSIS))
         {
            chartsPanel.disposeQueryConditionForm();
            panelCardLayout.show(mainPanel, TableFieldProfiler.NUMBER_ANALYSIS_CARD);
         }
         else
         {
            clusterPanel.disposeQueryConditionForm();
            panelCardLayout.show(mainPanel, TableFieldProfiler.CLUSTER_ANALYSIS_CARD);
         }
         
         currentSelectedPanel = actionCommand;
         return;
      }
   }
   
   //==============================================================
   // Class Method to open a saved configuration state file for
   // a database table.
   //==============================================================

   private void openAction(JFrame parent, String selectedPanel)
   {
      // Method Instances.
      JFileChooser dataFileChooser;
      String fileName, resourceTitleAlert, resourceError;

      // Choosing the directory to import data from.
      if (lastOpenDirectory.equals(""))
         dataFileChooser = new JFileChooser();
      else
         dataFileChooser = new JFileChooser(new File(lastOpenDirectory));

      // Add a FileFilter for *.myj and open dialog.
      dataFileChooser.setFileFilter(new MyJFileFilter());
      
      int result = dataFileChooser.showOpenDialog(parent);

      // Looks like might be good so lets check and read data.
      if (result == JFileChooser.APPROVE_OPTION)
      {
         // Save the selected directory so can be used again.
         lastOpenDirectory = dataFileChooser.getCurrentDirectory().toString();

         // Collect file name.
         fileName = dataFileChooser.getSelectedFile().getName();
         fileName = dataFileChooser.getCurrentDirectory() + Utils.getFileSeparator() + fileName;

         // Try Loading the State.
         if (!fileName.equals(""))
         {
            new LoadTableSQLStatementThread(fileName, resourceBundle, selectedPanel);
         }
         else
         {
            resourceTitleAlert = resourceBundle.getResourceString("MenuActionListener.dialogtitle.Alert",
                                                                  "Alert");
            resourceError = resourceBundle.getResourceString("MenuActionListener.dialogmessage.FileNOTFound",
                                                             "File NOT Found");
            JOptionPane.showMessageDialog(null, resourceError, resourceTitleAlert,
                                          JOptionPane.ERROR_MESSAGE);
         }
      }
   }
}