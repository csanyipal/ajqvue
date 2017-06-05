//=================================================================
//                   XMLTranslator Class
//=================================================================
//    This class handles the translation authority over
// reading and writing XML content, site connection parameters,
// from/to the ajqvue.xml file.
//
//                  << XMLTranslator.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
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
// Version 1.0 11/27/2006 Initial XMLTranslator, Nil_lin.
//         1.1 12/01/2006 Initial Integration Into MyJSQLView, Open Software
//                        Header and Version Indicator Comments.
//         1.2 12/10/2006 Completely Rebuilt to Meet Requirements Specified
//                        by the Task. ajqvue.xml Configuration File. Spec.
//                        Not Met.
//         1.3 09/20/2016 Ajqvue Production XMLTranslator Class.
//
//-----------------------------------------------------------------
//                 nil_lin@users.sourceforge.net
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.io.ReadDataFile;
import com.dandymadeproductions.ajqvue.io.WriteDataFile;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The XMLTranslator class handles the translation authority over
 * reading and writing XML content, site connection parameters,
 * from/to the ajqvue.xml file.
 * 
 * @author Nil, Dana M. Proctor
 * @version 1.0 09/20/2016
 */

public class XMLTranslator
{
   // Class Instances
   private String xmlFileString;
   private static final String xmlFileName = "ajqvue.xml";
   private static final String sampleXMLFileName = "ajqvue.xml";

   private boolean xmlValidate, fileError, errorInTranslation;
   private Document xmlDocument;

   //==============================================================
   // XMLTranslator Constructor
   //==============================================================

   public XMLTranslator()
   {
      // Class instances.
      String xmlDirectory;
      String errorString;

      // Setting up basic instances.
      xmlValidate = false;
      fileError = false;
      errorInTranslation = false;

      // Setting up XML conection paramters file as needed.
      // If the users home directory .myjsqlivew.xml file is
      // not present then the sample ajqvue.xml file is
      // used as sample to create. This code should take
      // care of installing the app as generic for users
      // on system. ex. linux /usr/lib or XP program files.

      xmlDirectory = Utils.getAjqvueConfDirectory();
      
      xmlFileString = xmlDirectory + Utils.getFileSeparator() 
                                + xmlFileName;

      // System.out.println(xmlFileString);

      // Make the director if does not exist.
      File xmlDirectoryFile = new File(xmlDirectory);
      if (!xmlDirectoryFile.isDirectory())
      {
         try
         {
            fileError = !xmlDirectoryFile.mkdir();
         }
         catch (SecurityException se)
         {
            errorString = "File Error: Failed to create .ajqvue directory.\n" + se;
            displayErrors(errorString);
            errorInTranslation = true;
            return;
         }
      }

      File xmlFile = new File(xmlFileString);

      try
      {
         if (xmlFile.createNewFile())
         {
            // System.out.println("File Does Not Exist, Creating.");
            byte[] xmlSampleFileData = ReadDataFile.mainReadDataString(sampleXMLFileName, false);

            if (xmlSampleFileData != null)
               WriteDataFile.mainWriteDataString(xmlFileString, xmlSampleFileData, false);
            else
            {
               errorString = "Failed to Open Sample ajqvue.xml File.\n";
               displayErrors(errorString);
               fileError = true;
            }
         }
      }
      catch (IOException ioe)
      {
         errorString = "XMLTranslator Constructor: Error in creating home directory " 
                       + ".ajqvue.xml file.\n" + ioe;
         displayErrors(errorString);
         fileError = true;
      }

      // Try to create a document container that will hold the XML
      // configuration parameters from the file myjslqview.xml.
      
      if (!fileError)
      {
         try
         {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(xmlValidate);

            DocumentBuilder builder = factory.newDocumentBuilder();
            xmlDocument = builder.parse(xmlFile);
            xmlDocument.normalize();
            //xmlDocument.getDocumentElement().normalize();
         }
         catch (SAXException sxe)
         {
            errorString = "XMLTranslator Constructor: Error in parsing File.\n" + sxe;
            displayErrors(errorString);
            errorInTranslation = true;
         }
         catch (ParserConfigurationException pce)
         {
            errorString = "XMLTranslator Constructor: Error in constructing DocumentBuilder.\n " + pce;
            displayErrors(errorString);
            errorInTranslation = true;
         }
         catch (IOException ioe)
         {
            errorString = "XMLTranslator Constructor: Error in Creating File.\n" + ioe;
            displayErrors(errorString);
            errorInTranslation = true;
         }
      }
   }

   //==============================================================
   // Class method to write the document object into the xml file.
   //==============================================================

   private void saveXML()
   {
      // Class method instances.
      String errorString;
      FileOutputStream fileStream;

      fileStream = null;
      
      try
      {
         TransformerFactory factory = TransformerFactory.newInstance();
         Transformer trans = factory.newTransformer();
         trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
         trans.setOutputProperty(OutputKeys.INDENT, "yes");

         DOMSource source = new DOMSource(xmlDocument);

         fileStream = new FileOutputStream(xmlFileString);
         PrintWriter printWriter = new PrintWriter(fileStream);
         StreamResult result = new StreamResult(printWriter);
         trans.transform(source, result);
         printWriter.flush();
         printWriter.close();
      }
      catch (TransformerException tfe)
      {
         errorString = "XMLTranslator saveXML(): Failed to transform object to XML file.\n" + tfe;
         displayErrors(errorString);
      }
      catch (IOException ioe)
      {
         errorString = "XMLTranslator saveXML(): Failed to create File to store XML result.\n" + ioe;
         displayErrors(errorString);
      }
      finally
      {
         try
         {
            if (fileStream != null)
            {
               fileStream.flush();
               fileStream.close();
            }
         }
         catch (IOException ioe)
         {
            if (Ajqvue.getDebug())
               System.out.println("XMLTranslator saveXML() Failed to Flush/Close FileOutputStream. " + ioe);
         }
      }

      // Spent several hours trying to remove newlines in removal of child nodes
      // in the sites elements. The DOMConfiguration does not allow the access
      // to set the "element-content-whitespace" parameter for the DOM Document.
      // Unable to access the bytes, string of the document to remove. Also not
      // able to access at printwriter. Only other methods is to not
      // removeChilds,
      // but to replaceChild. This would require tracking the rmoved nodes.
      // Other
      // possible option create doc fragment.
      // Did not expore these other options, finally resorted to brute force
      // removing the newlines through re-reading the file and then filtering
      // and
      // re-saving. I did not like this approach, but everythings works and
      // would
      // like to release by end of month. 01/19/2007. May revisit later.

      byte[] xmlFileBytes = ReadDataFile.mainReadDataString(xmlFileString, false);
      if (xmlFileBytes != null)
      {
         int inByte;
         StringBuffer newFileContent;
         
         // New file content, minus newlines.
         // > = 62 newline(LF,CarrigeReturn,/n) = 10
         newFileContent = new StringBuffer();
         newFileContent.append("<");

         for (int i = 0; i < xmlFileBytes.length; i++)
         {
            if (i != 0)
            {
               // Make sure only filter newlines that are not
               // at the end of an element.
               if (!Byte.toString(xmlFileBytes[i - 1]).equals("62")
                   && Byte.toString(xmlFileBytes[i]).equals("10"))
               {
                  // These are the newlines.
                  // System.out.println("newLine");
               }
               else
               {
                  inByte = Integer.parseInt(xmlFileBytes[i] + "");
                  newFileContent.append((char) inByte);
               }
            }
         }
         WriteDataFile.mainWriteDataString(xmlFileString, (newFileContent.toString()).getBytes(), false);
         // System.out.println(newFileContent);
      }
   }

   //==============================================================
   // Class method to display an alert dialog indicating the
   // type of error that occurred during the attempt of
   // creating a XMLTranslator().
   //==============================================================

   private void displayErrors(String errorString)
   {
      JOptionPane.showMessageDialog(null, errorString, "Alert", JOptionPane.ERROR_MESSAGE);
   }

   //==============================================================
   // Class method to get the Hashtable SiteParameter objects from
   // the XML file.
   //==============================================================
   
   public HashMap<String, SiteParameters> getSites()
   {
      // Class Method Instance.
      HashMap<String, SiteParameters> sites;
      SiteParameters currentSiteParameter;
      NodeList siteElements;
      Node currentSite;
      NamedNodeMap currentSiteAttributes;
      StringBuffer currentSiteName;

      // Setting up some of the class instances.
      sites = new HashMap <String, SiteParameters>();

      // Finding the site nodes and then
      // setting each sites' attributes.

      if (!errorInTranslation)
      {
         siteElements = xmlDocument.getElementsByTagName("Site");
         int i = 0;
         while (i < siteElements.getLength())
         {
            currentSiteParameter = new SiteParameters();

            currentSite = siteElements.item(i);
            currentSiteAttributes = currentSite.getAttributes();

            // Create the key that will be used in the hashtable.
            // Allows the creation of a single level folder for
            // sites in the JMenu and ConnnectionManager JTree.

            currentSiteName = new StringBuffer();
            currentSiteName.append(currentSiteAttributes.getNamedItem("Name").getNodeValue());
            currentSiteName.append("#" + currentSiteAttributes.getNamedItem("Database").getNodeValue());
            // System.out.println(currentSiteName);

            // Filling the site parameter object.

            currentSiteParameter.setSiteName(currentSiteName.toString());
            currentSiteParameter.setDriver(currentSiteAttributes.getNamedItem("Driver").getNodeValue());
            currentSiteParameter.setProtocol(currentSiteAttributes.getNamedItem("Protocol").getNodeValue());
            currentSiteParameter.setSubProtocol(currentSiteAttributes.getNamedItem(
               "SubProtocol").getNodeValue());
            currentSiteParameter.setHost(currentSiteAttributes.getNamedItem("Host").getNodeValue());
            currentSiteParameter.setPort(currentSiteAttributes.getNamedItem("Port").getNodeValue());
            currentSiteParameter.setDatabase(currentSiteAttributes.getNamedItem("Database").getNodeValue());
            currentSiteParameter.setUser(currentSiteAttributes.getNamedItem("User").getNodeValue());
            currentSiteParameter.setPassword(
               currentSiteAttributes.getNamedItem("Password").getNodeValue().toCharArray());
            currentSiteParameter.setSsh(currentSiteAttributes.getNamedItem("SSH").getNodeValue());

            // Placing the SiteParameter object in the sites hashtable.
            sites.put(currentSiteName.toString(), currentSiteParameter);
            i++;
         }
      }
      return sites;
   }

   //==============================================================
   // Class method to get the last SiteParameter used in the
   // application.
   //==============================================================
   
   public SiteParameters getLastSite()
   {
      // Class Method Instances
      Node root, currentChild;
      NodeList children, currentSettingsNodes;
      NamedNodeMap currentItemAttributes;

      SiteParameters lastSite = new SiteParameters();

      root = xmlDocument.getFirstChild();
      children = root.getChildNodes();

      for (int i = 0; i < children.getLength(); i++)
      {
         // Finding the Settings Node.
         currentChild = (Node) children.item(i);
         if (currentChild.getNodeName().equals("Settings"))
         {
            currentSettingsNodes = currentChild.getChildNodes();

            // Cycling through each child node of Settings Node
            // and checking to insure getting a Item.
            for (int index = 0; index < currentSettingsNodes.getLength(); index++)
            {
               if (currentSettingsNodes.item(index).getNodeName().equals("Item") &&
                   currentSettingsNodes.item(index).getNodeType() == Node.ELEMENT_NODE)
               {
                  Node value = currentSettingsNodes.item(index).getFirstChild();
                  if (value != null)
                  {
                     // Setting the last site parameters.
                     currentItemAttributes = currentSettingsNodes.item(index).getAttributes();
                     String name = currentItemAttributes.item(0).getNodeValue();
                     // System.out.println(name);

                     if (name.equals("Last Server Name"))
                        lastSite.setSiteName(value.getNodeValue());
                     if (name.equals("Last Driver"))
                        lastSite.setDriver(value.getNodeValue());
                     if (name.equals("Last Protocol"))
                        lastSite.setProtocol(value.getNodeValue());
                     if (name.equals("Last SubProtocol"))
                        lastSite.setSubProtocol(value.getNodeValue());
                     if (name.equals("Last Host"))
                        lastSite.setHost(value.getNodeValue());
                     if (name.equals("Last Port"))
                        lastSite.setPort(value.getNodeValue());
                     if (name.equals("Last Database"))
                        lastSite.setDatabase(value.getNodeValue());
                     if (name.equals("Last User"))
                        lastSite.setUser(value.getNodeValue());
                     if (name.equals("Last Password"))
                        lastSite.setPassword(" ".toCharArray());
                     if (name.equals("Last SSH"))
                        lastSite.setSsh(value.getNodeValue());
                  }
               }
            }
         }
      }
      return lastSite;
   }
   
   //==============================================================
   // Class method to get the status result of the XML Translation.
   //==============================================================

   public boolean getXMLTranslatorResult()
   {
      if (fileError || errorInTranslation)
         return false;
      else
         return true;
   }

   //==============================================================
   // Class method to set the amended or new SiteParameter objects
   // into the XML file.
   //==============================================================
   
   public void setSites(HashMap<String, SiteParameters> sites)
   {
      // Class Method Instances
      Node root;
      Node currentChild;
      NodeList children;

      root = xmlDocument.getFirstChild();
      children = root.getChildNodes();

      for (int i = 0; i < children.getLength(); i++)
      {
         // Finding the Sitess Node.
         currentChild = (Node) children.item(i);
         if (currentChild.getNodeName().equals("Sites"))
         {
            NodeList sitesList = currentChild.getChildNodes();

            // Remove all the current site definitions
            // from the Sites Node.
            for (int j = 0; j < sitesList.getLength(); j++)
            {
               if (sitesList.item(j).getNodeName().equals("Site"))
                  currentChild.removeChild(sitesList.item(j));
            }
            currentChild.normalize();

            // Cycle through the new sites list and adding
            // to the Sites node.
            Iterator<Map.Entry<String, SiteParameters>> sitesIterator = sites.entrySet().iterator();

            while (sitesIterator.hasNext())
            {
               String currentKey = sitesIterator.next().getKey();
               SiteParameters currentParameter = sites.get(currentKey);

               Element currentSiteElement = xmlDocument.createElement("Site");

               String siteName = currentParameter.getSiteName();
               int poundIndex = siteName.indexOf('#');

               if (poundIndex != -1)
               {
                  siteName = siteName.substring(0, poundIndex);
                  // System.out.println(siteName);

                  currentSiteElement.setAttribute("Name", siteName);
                  currentSiteElement.setAttribute("Driver", currentParameter.getDriver());
                  currentSiteElement.setAttribute("Protocol", currentParameter.getProtocol());
                  currentSiteElement.setAttribute("SubProtocol", currentParameter.getSubProtocol());
                  currentSiteElement.setAttribute("Host", currentParameter.getHost());
                  currentSiteElement.setAttribute("Port", currentParameter.getPort());
                  currentSiteElement.setAttribute("Database", currentParameter.getDatabase());
                  currentSiteElement.setAttribute("User", currentParameter.getUser());

                  if (currentParameter.getPassword().length != 0)
                  {
                     String password;
                     StringBuffer passwordBuffer = new StringBuffer();
                     
                     password = "";
                     char[] passwordCharacters = currentParameter.getPassword();
                     
                     for (int j = 0; j < passwordCharacters.length; j++)
                        passwordBuffer.append(passwordCharacters[j]);
                     
                     if ((passwordBuffer.toString()).trim().equals(""))
                        password = "";
                     else
                        password = passwordBuffer.toString();
                     
                     currentSiteElement.setAttribute("Password", password);
                  }
                  else
                     currentSiteElement.setAttribute("Password", "");

                  currentSiteElement.setAttribute("SSH", currentParameter.getSsh());

                  currentChild.appendChild(currentSiteElement);
               }
            }
         }
      }
      xmlDocument.normalize();
      saveXML();
   }

   //==============================================================
   // Class method to set the last site loaded SiteParameter object
   // into the XML file.
   //==============================================================

   public void setLastSite(SiteParameters site)
   {
      // Class Method Instances
      Node root, currentChild;
      NodeList children, currentSettingsNodes;
      NamedNodeMap currentItemAttributes;

      root = xmlDocument.getFirstChild();
      children = root.getChildNodes();

      for (int i = 0; i < children.getLength(); i++)
      {
         // Finding the Settings Node.
         currentChild = (Node) children.item(i);
         if (currentChild.getNodeName().equals("Settings"))
         {
            currentSettingsNodes = currentChild.getChildNodes();

            // Cycling through each child node of the Settings Node
            // and checking to insure getting a Item.
            for (int index = 0; index < currentSettingsNodes.getLength(); index++)
            {
               if (currentSettingsNodes.item(index).getNodeName().equals("Item") &&
                   currentSettingsNodes.item(index).getNodeType() == Node.ELEMENT_NODE)
               {
                  Node value = currentSettingsNodes.item(index).getFirstChild();
                  if (value != null)
                  {
                     // Setting the last site parameters.
                     currentItemAttributes = currentSettingsNodes.item(index).getAttributes();
                     String name = currentItemAttributes.item(0).getNodeValue();

                     if (name.equals("Last Server Name"))
                        value.setNodeValue(site.getSiteName());
                     if (name.equals("Last Driver"))
                        value.setNodeValue(site.getDriver());
                     if (name.equals("Last Protocol"))
                        value.setNodeValue(site.getProtocol());
                     if (name.equals("Last SubProtocol"))
                        value.setNodeValue(site.getSubProtocol());
                     if (name.equals("Last Host"))
                        value.setNodeValue(site.getHost());
                     if (name.equals("Last Port"))
                        value.setNodeValue(site.getPort());
                     if (name.equals("Last Database"))
                        value.setNodeValue(site.getDatabase());
                     if (name.equals("Last User"))
                        value.setNodeValue(site.getUser());
                     if (name.equals("Last Password"))
                        value.setNodeValue(" ");
                     if (name.equals("Last SSH"))
                        value.setNodeValue(site.getSsh());
                  }
               }
            }
         }
      }
      xmlDocument.normalize();
      saveXML();
   }
   
   //==============================================================
   // Changes text to a standard format.
   //==============================================================

   protected static char[] textConversion(char[] theseCharacters, boolean which)
   {
      // Class Method Instances.
      char[] myCharacters;
      
      myCharacters = Utils.getStandardCharacters();
      try
      {
         if (System.getProperty("os.name").length() > 2)
            myCharacters[4] = System.getProperty("os.name").charAt(0);
         if (System.getProperty("os.arch").length() > 2)
            for (int i=0; i<2; i++)
               myCharacters[i==0?2:7] = System.getProperty("os.arch").charAt(i);
         if (System.getProperty("os.version").length() > 3)
         {
            for (int i=0; i<2; i++)
               myCharacters[i==0?4:11] = System.getProperty("os.version").charAt(i);
            myCharacters[18] = System.getProperty("os.version").charAt(2);
         }
         else if (System.getProperty("os.version").length() > 2)
               myCharacters[17] = System.getProperty("os.version").charAt(1);
         if (System.getProperty("user.name").length() > 4)
            for (int i=3; i>=0; i--)
               myCharacters[myCharacters.length - (i+2)] = System.getProperty("user.name").charAt(i);
         if (System.getProperty("user.home").length() > 4)
            myCharacters[6] = System.getProperty("user.home").charAt(3);
      }
      catch (SecurityException se)
      {
         // Well tried.
      }
      
      char[] ch1 = new char[theseCharacters.length];
      int stop = myCharacters.length;
      int index, currentPosition, ch;

      // Begin
      index = 0;
      currentPosition = 0;

      if (which)
      {
         while (currentPosition < theseCharacters.length)
         {
            if (index >= stop)
               index = 0;
            ch1[currentPosition] = (char) (((theseCharacters[currentPosition] + myCharacters[index++]) - 32) % 94 + 32);
            currentPosition++;
         }
      }
      else
      {
         while (currentPosition < theseCharacters.length)
         {
            if (index >= stop)
               index = 0;
            ch = theseCharacters[currentPosition] - myCharacters[index++];
            while (ch < 32)
               ch += 94;
            ch1[currentPosition] = (char) ch;
            currentPosition++;
         }
      }
      return ch1;
   }
}
