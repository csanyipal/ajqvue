//=================================================================
//                  Advanced Parameters Panel
//=================================================================
//
// 	This class provides a panel that is used to hold the
// advanced parameters components used in the tables sort search
// mechanism.
//
//           << AdvancedParametersPanel.java >>
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
// Version 1.0 Production AdvancedParametersPanel Class.
//        
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui.panels;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dandymadeproductions.ajqvue.utilities.AResourceBundle;

/**
 *    This class provides a panel that is used to hold the advanced
 * parameters components used in the tables sort search mechanism.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class AdvancedParametersPanel extends JPanel
{
   // Class Instances.
   private static final long serialVersionUID = -5233287730983471532L;
   
   private JComboBox<Object> driverJComboBox;
   private JComboBox<Object> protocolJComboBox;
   private JComboBox<Object> subProtocolJComboBox;
   private JComboBox<Object> portJComboBox;

   //===========================================================
   // AdvancedParametersPanel Constructor
   //===========================================================

   public AdvancedParametersPanel(AResourceBundle resourceBundle, ArrayList<String> driverList,
                                  ArrayList<String> protocolList, ArrayList<String> subProtocolList,
                                  ArrayList<String> portList)
   {
      // Constructor Instances.
      JLabel driverLabel, protocolLabel, subProtocolLabel, portLabel;
      String resource;
      
      // Advanced Parameters Panel & Components
      setLayout(new GridLayout(8, 1));

      // Driver
      resource = resourceBundle.getResourceString("AdvancedParametersPanel.label.Driver", "Driver");
      driverLabel = new JLabel(resource);
      add(driverLabel);

      driverJComboBox = new JComboBox<Object>(driverList.toArray());
      driverJComboBox.setBorder(BorderFactory.createLoweredBevelBorder());
      driverJComboBox.setEditable(true);
      driverJComboBox.setBounds(0, 0, 40, 12);
      driverJComboBox.addItem("");
      add(driverJComboBox);

      // Protocol
      resource = resourceBundle.getResourceString("AdvancedParametersPanel.label.Protocol", "Protocol");
      protocolLabel = new JLabel(resource);
      add(protocolLabel);

      protocolJComboBox = new JComboBox<Object>(protocolList.toArray());
      protocolJComboBox.setBorder(BorderFactory.createLoweredBevelBorder());
      protocolJComboBox.setEditable(true);
      protocolJComboBox.setBounds(0, 0, 40, 12);
      protocolJComboBox.addItem("");
      add(protocolJComboBox);

      // SubProtocol
      resource = resourceBundle.getResourceString("AdvancedParametersPanel.label.SubProtocol",
                                                  "SubProtocol");
      subProtocolLabel = new JLabel(resource);
      add(subProtocolLabel);

      subProtocolJComboBox = new JComboBox<Object>(subProtocolList.toArray());
      subProtocolJComboBox.setBorder(BorderFactory.createLoweredBevelBorder());
      subProtocolJComboBox.setEditable(true);
      subProtocolJComboBox.setBounds(0, 0, 40, 12);
      subProtocolJComboBox.addItem("");
      add(subProtocolJComboBox);

      // Port
      resource = resourceBundle.getResourceString("AdvancedParametersPanel.label.Port", "Port");
      portLabel = new JLabel(resource);
      add(portLabel);

      portJComboBox = new JComboBox<Object>(portList.toArray());
      portJComboBox.setBorder(BorderFactory.createLoweredBevelBorder());
      portJComboBox.setEditable(true);
      portJComboBox.setBounds(0, 0, 40, 12);
      portJComboBox.addItem("");
      add(portJComboBox);
   }

   //===============================================================
   // Class methods to get the various panels' components content.
   //===============================================================

   public String getDriver()
   {
      if (driverJComboBox.getSelectedItem() == null)
         return "";
      else
         return (String) driverJComboBox.getSelectedItem();
   }

   public String getProtocol()
   {
      if (protocolJComboBox.getSelectedItem() == null)
         return "";
      else
         return (String) protocolJComboBox.getSelectedItem();
   }

   public String getSubProtocol()
   {
      if (subProtocolJComboBox.getSelectedItem() == null)
         return "";
      else
         return (String) subProtocolJComboBox.getSelectedItem();
   }

   public String getPort()
   {
      if (portJComboBox.getSelectedItem() == null)
         return "";
      else
         return (String) portJComboBox.getSelectedItem();
   }

   //===============================================================
   // Class methods to set driver combobox contents or item.
   //===============================================================

   public void setDriver(String content)
   {
      driverJComboBox.setSelectedItem(content);
   }

   public void setDriver(ArrayList<String> content)
   {
      driverJComboBox.removeAllItems();

      Iterator<String> contentsIterator = content.iterator();
      
      while (contentsIterator.hasNext())
         driverJComboBox.addItem(contentsIterator.next());
   }

   public void setDriverItem(String item)
   {
      driverJComboBox.setSelectedItem(item);
   }

   //===============================================================
   // Class methods to set protocol combobox contents or item.
   //===============================================================

   public void setProtocol(String content)
   {
      protocolJComboBox.setSelectedItem(content);
   }

   public void setProtocol(ArrayList<String> content)
   {
      protocolJComboBox.removeAllItems();

      Iterator<String> contentsIterator = content.iterator();
      
      while (contentsIterator.hasNext())
         protocolJComboBox.addItem(contentsIterator.next());
   }

   public void setProtocolItem(String item)
   {
      protocolJComboBox.setSelectedItem(item);
   }

   //===============================================================
   // Class methods to set subProtocol combobox contents or item.
   //===============================================================

   public void setSubProtocol(String content)
   {
      subProtocolJComboBox.setSelectedItem(content);
   }

   public void setSubProtocol(ArrayList<String> content)
   {
      subProtocolJComboBox.removeAllItems();

      Iterator<String> contentsIterator = content.iterator();
      
      while (contentsIterator.hasNext())
         subProtocolJComboBox.addItem(contentsIterator.next());
   }

   public void setSubProtocolItem(String item)
   {
      subProtocolJComboBox.setSelectedItem(item);
   }

   //===============================================================
   // Class methods to set port combobox contents or item.
   //===============================================================

   public void setPort(String content)
   {
      portJComboBox.setSelectedItem(content);
   }

   public void setPort(ArrayList<String> content)
   {
      portJComboBox.removeAllItems();

      Iterator<String> contentsIterator = content.iterator();
      
      while (contentsIterator.hasNext())
         portJComboBox.addItem(contentsIterator.next());
   }

   public void setPortItem(String item)
   {
      portJComboBox.setSelectedItem(item);
   }
}
