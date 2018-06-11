//=================================================================
//                    AResourceBundle Class
//=================================================================
//
//    The AResourceBundle class provides a method to more closely
// control the loading of locale, image, & other types of resource
// files in the program. Handles also the methods needed to retrieve
// these various resource.
//
//                  << AResourceBundle.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.1 06/14/2017
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
// Version 1.0 09/17/2016 Production AResourceBundle Class.
//         1.1 06/14/2017 Class javadoc Constructor Comment Correction.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javafx.scene.image.Image;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.Ajqvue;

/**
 *    The AResourceBundle class provides a method to more closely
 * control the loading of locale, image, &amp; other types of resource
 * files in a program. Handles also the methods needed to retrieve
 * these various resource.
 * 
 * @author Dana M. Proctor
 * @version 1.1 06/14/2017
 */

public class AResourceBundle implements Serializable
{
   // Class Instances.
   private static final long serialVersionUID = -4607428315735688372L;

   private String className;
   private URL resourceURL;
   private String resourceType;
   private String cacheDirectory, cachedJAR_FileName;
   private HashMap<String, String> localeListData;
   private HashMap<String, ImageIcon> imagesData;

   boolean debugMode;
   boolean cacheJar;

   private static final String FILE_RESOURCE = "file";
   private static final String HTTP_RESOURCE = "http";
   private static final String JAR_RESOUCE = "jar";

   //==============================================================
   // AResourceBundle Constructor
   //
   // Examples: Should be the path to the jar for plugins. A local
   // application just needs to specify the path to the
   // root directory of resources. If the path ends with
   // the jar name then all resource will be treated as
   // being in the jar file and may be cached if not local.
   // 
   // Local Files - /home/user/myplugin/, /home/user/myplugin/my.jar
   // Network Files - http://xyz.com/plugin/, http://xyz.com/my.jar
   //
   // Just instantiating this classes does not complete the process
   // of setting a resource bundle. The locale resource type must
   // also be initialized by a call to he the corresponding
   // setLocaleResource() so that data strings may be loaded.
   //==============================================================
   
   public AResourceBundle(String resourceURLString)
   {
      this(resourceURLString, false, true);
   }
   
   public AResourceBundle(String resourceURLString, boolean debugMode)
   {
      this(resourceURLString, debugMode, true);
   }
   
   public AResourceBundle(String resourceURLString, boolean debugMode, boolean cache)
   {
      // Setup to process.

      className = getClass().getSimpleName();
      cacheDirectory = Utils.getCacheDirectory();
      this.debugMode = debugMode;
      // cacheJar = cache; // v4.0
      cacheJar = false;

      // Yea, nothing here move on.

      if (resourceURLString == null || resourceURLString.equals(""))
         return;

      // Create resource URL.
      else
      {
         try
         {
            // Special handling for JARs.

            if (resourceURLString.endsWith(".jar") && resourceURLString.indexOf(":") != -1)
            {
               resourceURL = new URL("jar:" + resourceURLString + "!/");

               // Determining if caching desired.
               if (cacheJar && (resourceURL.toExternalForm().indexOf("jar:file:") == -1))
               {
                  cachedJAR_FileName = cacheDirectory
                                       + (Long.toString(System.currentTimeMillis()).substring(0, 10));
                  try
                  {
                     cacheJar = cacheJAR(resourceURLString);
                  }
                  catch (IOException ioe)
                  {
                     if (debugMode)
                        System.out.println(className + " Constructor()\n"
                                           + "Failed to Close Cache Stream.\n" + ioe.toString());
                  }
               }
            }
            else
            {
               resourceURL = new URL(resourceURLString);
               cacheJar = false;
            }

            // System.out.println(className + " Resource URL:" + resourceURL.toExternalForm());

            resourceType = resourceURL.getProtocol();
         }
         catch (MalformedURLException mfe)
         {
            displayErrors(className + " Constructor() \n"
                          + "Failed to create locale resouce URL from, " + resourceURLString + "\n"
                          + mfe.toString());
            return;
         }
      }

      // Setup a hashmap to be used in storing locale resource file
      // key, value pairs.

      localeListData = new HashMap<String, String>();
   }

   //==============================================================
   // Class Method for classes to obtain the locale resource given
   // a key.
   //==============================================================

   public String getResourceString(String resourceKey, String defaultValue)
   {
      // System.out.println(resourceKey);

      if (localeListData != null && resourceKey != null)
      {
         if (localeListData.containsKey(resourceKey))
            return localeListData.get(resourceKey);
         else
         {
            if (!localeListData.isEmpty() && debugMode)
               System.out.println(className + " getResourceString()\n"
                                  + "Invalid Resource Key: " + resourceKey);

            return getDefaultResourceString(defaultValue);
         }
      }
      else
      {
         if (debugMode)
            System.out.println(className + " getResourceString()\n"
                               + "Either Undefined Locale or Resource Key.");

         return getDefaultResourceString(defaultValue);
      }
   }

   //==============================================================
   // Class Method for returning a default resource string.
   //==============================================================

   private String getDefaultResourceString(String defaultValue)
   {
      if (defaultValue != null)
         return defaultValue;
      else
      {
         if (debugMode)
            System.out.println(className + " getDefaultResourceString()\n"
                               + "Undefined Resource Default Value.");

         return "";
      }
   }

   //==============================================================
   // Class Method for allowing classes to obtain a specified
   // image file to be used as an image icon.
   //==============================================================

   public ImageIcon getResourceImage(String imageFileName)
   {
      // Method Instances
      URL imageResourceURL = null;

      // Check some type of valid input.
      if (resourceURL != null && imageFileName != null)
      {
         // System.out.println("Image Resource: " + resourceURL.toExternalForm());
         
         try
         {
            //====
            // Handle resource from file & http locations.
            if (resourceType.equals(FILE_RESOURCE) || resourceType.equals(HTTP_RESOURCE))
            {  
               imageResourceURL = new URL(resourceURL.toExternalForm() + imageFileName);
               ImageIcon imageIcon = new ImageIcon(imageResourceURL);
               
               if (debugMode && (imageIcon.getIconWidth() == -1 || imageIcon.getIconHeight() == -1))
                  System.out.println(className + " getResourceImage()\n"
                                     + "Failed to find image file: " + imageResourceURL.getFile() + "\n");
               
               return imageIcon;
            }

            //====
            // Handle resource from a jar file.
            else if (resourceType.equals(JAR_RESOUCE))
            {
               if (cacheJar && (resourceURL.toExternalForm().indexOf("jar:file:") == -1))
                  return new ImageIcon(getJAR_Resource(
                     imageFileName, new URL("jar:file:" + cachedJAR_FileName + "!/")));
               else
                  return new ImageIcon(getJAR_Resource(imageFileName, resourceURL));
            }

            //====
            // Unknown
            else
            {
               displayErrors(className + " getResourceImage() \n"
                             + "Failed to identity URL protocol in order to process, , "
                             + resourceURL.getProtocol());
               return new ImageIcon(new byte[0]);
            }

         }
         catch (MalformedURLException mfe)
         {
            if (debugMode)
               System.out.println(className + " getResourceImage()\n"
                                  + "Failed to Create Image URL.\n" + mfe.toString());
            return new ImageIcon(new byte[0]);
         }
         catch (IOException ioe)
         {
            if (debugMode)
               System.out.println(className + " getResourceImage()\n"
                                  + "Failed to close resources or create cache URL.\n"
                                  + ioe.toString());
            return new ImageIcon(new byte[0]);
         }
      }
      else
      {
         if (debugMode)
            System.out.println(className + " getResourceImage()\n"
                               + "Either Undefined resourceURL or image file.");

         return new ImageIcon(new byte[0]);
      }
   }
   
   //==============================================================
   // Class Method for allowing classes to obtain a specified
   // image file to be used as an image icon.
   //==============================================================

   public Image getResourceFXImage(String imageFileName)
   {
      // Method Instances
      URL imageResourceURL = null;
      ByteArrayInputStream byteInputStream = null;

      // Check some type of valid input.
      if (resourceURL != null && imageFileName != null)
      {
         // System.out.println("Image Resource: " + resourceURL.toExternalForm());
         
         try
         {
            //====
            // Handle resource from file & http locations.
            if (resourceType.equals(FILE_RESOURCE) || resourceType.equals(HTTP_RESOURCE))
            {  
               imageResourceURL = new URL(resourceURL.toExternalForm() + imageFileName);
               Image image = new Image(resourceURL.toExternalForm() + imageFileName);
               
               
               if (debugMode && (image.getWidth() == -1 || image.getHeight() == -1))
                  System.out.println(className + " getResourceFXImage()\n"
                                     + "Failed to find image file: " + imageResourceURL.getFile() + "\n");
               
               return image;
            }
            
            //====
            // Handle resource from a jar file.
            else if (resourceType.equals(JAR_RESOUCE))
            {
               if (cacheJar && (resourceURL.toExternalForm().indexOf("jar:file:") == -1))
               {
                  byteInputStream = new ByteArrayInputStream(getJAR_Resource(
                     imageFileName, new URL("jar:file:" + cachedJAR_FileName + "!/")));
                  return new Image(byteInputStream);
               }
               else
               {
                  byteInputStream = new ByteArrayInputStream(getJAR_Resource(
                     imageFileName, resourceURL));
                  return new Image(byteInputStream);
               }
            }

            //====
            // Unknown
            else
            {
               displayErrors(className + " getResourceFXImage() \n"
                             + "Failed to identity URL protocol in order to process, , "
                             + resourceURL.getProtocol());
               return null;
            }
         }
         catch (MalformedURLException mfe)
         {
            if (debugMode)
               System.out.println(className + " getResourceFXImage()\n"
                                  + "Failed to Create Image URL.\n" + mfe.toString());
            return null;
         }
         catch (IOException ioe)
         {
            if (debugMode)
               System.out.println(className + " getResourceImage()\n"
                                  + "Failed to close resources or create cache URL.\n"
                                  + ioe.toString());
            return null;
         }
         finally
         {
            try
            {
               if (byteInputStream != null)
                  byteInputStream.close();
            }
            catch (IOException ioe)
            {
               if (debugMode)
                  System.out.println(className + " getResourceImage()\n"
                                     + "Failed to close resource.\n" + ioe.toString());
            }
         }
      }
      else
      {
         if (debugMode)
            System.out.println(className + " getResourceFXImage()\n"
                               + "Either Undefined resourceURL or image file.");
         return null;
      }
   }
   
   //==============================================================
   // Class Method for allowing classes to obtain a specified
   // file resource.
   //==============================================================

   public File getResourceFile(String fileName)
   {
      // Method Instances
      URL fileResourceURL = null;
      File fileResource;

      // Check some type of valid input.
      if (resourceURL != null && fileName != null)
      {
         // System.out.println("File Resource: " + resourceURL.toExternalForm());
            
         //====
         // Handle resource from file & http locations.
         if (resourceType.equals(FILE_RESOURCE))
         {  
            try
            {
               fileResourceURL = new URL(resourceURL.toExternalForm() + fileName);
               fileResource = new File(fileResourceURL.getFile());
            }
            catch (MalformedURLException e)
            {
               displayErrors(className + " getResourceFile() \n"
                             + "Failed to identity URL protocol in order to process, , "
                             + resourceURL.toExternalForm() + fileName);
               return null;
            }
            return fileResource;
         }
         
         //====
         // Does not resource from a HTTP or JARS files.
         else if (resourceType.equals(HTTP_RESOURCE) || resourceType.equals(JAR_RESOUCE))
         {
            JOptionPane.showMessageDialog(null, className + " getResourceFile() Only Supports\n"
                                                + "Local File Resources!", "Alert", JOptionPane.ERROR_MESSAGE);
            return null;
         }

         //====
         // Unknown
         else
         {
            displayErrors(className + " getResourceFile() \n"
                          + "Failed to identity URL protocol in order to process, , "
                          + resourceURL.getProtocol());
            return null;
          }   
      }
      else
      {
         if (debugMode)
            System.out.println(className + " getResourceImage()\n"
                               + "Either Undefined resourceURL or image file.");

         return null;
      }
   }
   
   //==============================================================
   // Class Method for allowing classes to obtain a specified
   // resource via a byte array. Use this for local & remote
   // resources.
   //==============================================================

   public byte[] getResourceBytes(String resourceName)
   {
      // Method Instances
      URL byteResourceURL = null;
      InputStream inputStream;
      BufferedInputStream bufferedInputStream;
      int inSize;
      byte[] resourceBytes;

      // Check some type of valid input.
      if (resourceURL != null && resourceName != null)
      {
         // System.out.println("Byte Resource: " + resourceURL.toExternalForm());
         
         try
         {
            //====
            // Handle resource from file & http locations.
            if (resourceType.equals(FILE_RESOURCE) || resourceType.equals(HTTP_RESOURCE))
            {  
               byteResourceURL = new URL(resourceURL.toExternalForm() + resourceName);
               
               inputStream = null;
               bufferedInputStream = null;
               
               try
               {
                  if (resourceType.equals(FILE_RESOURCE))
                     inputStream = new FileInputStream(new File(resourceURL.toExternalForm()
                                                       + resourceName));
                  else
                  {
                     URLConnection urlConnection = byteResourceURL.openConnection();
                     inputStream = urlConnection.getInputStream();
                     
                  }
                  bufferedInputStream = new BufferedInputStream(inputStream);
                  inSize = bufferedInputStream.available();
                  resourceBytes = new byte[inSize];
                  
                  int i = 0;
                  
                  while (i < inSize)
                     resourceBytes[i++] = (byte) bufferedInputStream.read();
                  
                  return resourceBytes;
                  
               }
               catch (IOException ioe)
               {
                  if (Ajqvue.getDebug())
                     System.out.println(className + " getResourceBytes() \n"
                                        + "Error Reading Resource. " + ioe.toString());
                  return new byte[0];
               }
               finally
               {
                  try
                  {
                     if (bufferedInputStream != null)
                        bufferedInputStream.close();
                  }
                  catch (IOException ioe)
                  {
                     if (Ajqvue.getDebug())
                        System.out.println(className + " getResourceBytes() \n"
                                           + "Failed to Close BufferedInputStream. " + ioe.toString());
                  }
                  finally
                  {
                     try
                     {
                        if (inputStream != null)
                           inputStream.close();
                     }
                     catch (IOException ioe)
                     {
                        if (Ajqvue.getDebug())
                           System.out.println("WriteDataFile writeDataFileText() \n"
                                              + "Failed to Close FileOutputStream. " + ioe.toString());
                     }     
                  }
               }
            }

            //====
            // Handle resource from a jar file.
            else if (resourceType.equals(JAR_RESOUCE))
            {
               if (cacheJar && (resourceURL.toExternalForm().indexOf("jar:file:") == -1))
                  return getJAR_Resource(resourceName, new URL("jar:file:" + cachedJAR_FileName
                                                               + "!/"));
               else
                  return getJAR_Resource(resourceName, resourceURL);
            }

            //====
            // Unknown
            else
            {
               displayErrors(className + " getResourceBytes() \n"
                             + "Failed to identity URL protocol in order to process, , "
                             + resourceURL.getProtocol());
               return new byte[0];
            }

         }
         catch (MalformedURLException mfe)
         {
            if (debugMode)
               System.out.println(className + " getResourceBytes()\n"
                                  + "Failed to Create Resource URL.\n" + mfe.toString());
            return new byte[0];
         }
         catch (IOException ioe)
         {
            if (debugMode)
               System.out.println(className + " getResourceBytes()\n"
                                  + "Failed to close resources or create cache URL.\n"
                                  + ioe.toString());
            return new byte[0];
         }
      }
      else
      {
         if (debugMode)
            System.out.println(className + " getResourceBytes()\n"
                               + "Either Undefined resourceURL or resource name.");

         return new byte[0];
      }
   }
   
   //==============================================================
   // Class Method for allowing classes to obtain a specified
   // image icon that has already been stored from setImage().
   //==============================================================

   public ImageIcon getImage(String imageKey)
   {
      if (imagesData != null && imagesData.containsKey(imageKey))
         return imagesData.get(imageKey);
      else
      {
         if (debugMode)
            System.out.println(className + " getImage()\n"
                               + "Failed to find image key: " + imageKey);
         return null;
      }
   }

   //==============================================================
   // Class Method to set the locale aspects of the resource
   // bundle. The localeDirectory should reference where the locale
   // directory resource files are located. The baseName and
   // localeString is used to compose the specific file to use.
   //==============================================================

   public void setLocaleResource(String localeDirectory, String baseName, String localeString)
   {
      // Method Instances
      String localeFileName;

      if (resourceURL == null || baseName == null || localeString == null)
         return;

      // Process the given locale file to obtain a hashmap
      // of the key, resource pairs.

      localeFileName = localeDirectory + baseName + "_" + localeString + ".properties";

      try
      {
         if (resourceType.equals(FILE_RESOURCE))
         {
            createFile_LocaleResource((resourceURL.toExternalForm()).substring(
               resourceURL.toExternalForm().indexOf("file:") + 5) + localeFileName);
         }

         else if (resourceType.equals(HTTP_RESOURCE))
         {
            createHttp_LocaleResource(new URL(resourceURL.toExternalForm() + localeFileName));
         }

         else if (resourceType.equals(JAR_RESOUCE))
         {
            createJAR_LocaleResource(localeFileName);
         }

         // Unknown
         else
         {
            displayErrors(className + " setLocaleResource() \n"
                          + "Failed to identity URL protocol in order to process, , "
                          + resourceURL.getProtocol());
         }
      }
      catch (IOException ioe)
      {
         displayErrors(className + " setLocaleResource() \n" + "Failed to close " + resourceType
                       + " in process.\n" + ioe.toString());
      }
   }
   
   //==============================================================
   // Class Method to set images data. Allows the loading of image
   // resources at initialization of program rather then collect
   // as boot occurs over startup classes requests.
   //==============================================================

   public void setImage(String imageKey, String imageFileName)
   {
      if (imagesData == null)
         imagesData = new HashMap<String, ImageIcon>();
      
      imagesData.put(imageKey, getResourceImage(imageFileName));   
   }

   //==============================================================
   // Class Method to create the localization resource source from
   // the local file system.
   //==============================================================

   private void createFile_LocaleResource(String fileName) throws IOException
   {
      // Method Instances
      InputStream inputStream;

      inputStream = null;
      // System.out.println("Creating File Locale Resource.");

      try
      {
         inputStream = new FileInputStream(fileName);
         readLocaleResource(inputStream);
      }
      catch (IOException ioe)
      {
         displayErrors(className + " createFile_LocaleResource() \n"
                       + "Failed to process the given locale file, " + resourceURL.toExternalForm() + "\n"
                       + ioe.toString());
      }
      finally
      {
         if (inputStream != null)
            inputStream.close();
      }
   }

   //==============================================================
   // Class Method to create the localization resource source from
   // the network.
   //==============================================================

   private void createHttp_LocaleResource(URL url) throws IOException
   {
      // Method Instances
      HttpURLConnection urlConnection;
      InputStream inputStream;

      inputStream = null;
      // System.out.println("Creating Http Locale Resource.");

      try
      {
         urlConnection = (HttpURLConnection) url.openConnection();
         urlConnection.connect();

         inputStream = urlConnection.getInputStream();
         readLocaleResource(inputStream);
      }
      catch (IOException ioe)
      {
         displayErrors(className + " createHTTP_Resource() \n"
                       + "Failed to process the given locale http, " + resourceURL.toExternalForm() + "\n"
                       + ioe.toString());
      }
      finally
      {
         if (inputStream != null)
            inputStream.close();
      }
   }

   //==============================================================
   // Class Method to create the localization resource source from
   // the network that is packaged into a JAR file.
   //==============================================================

   private void createJAR_LocaleResource(String localeFileName) throws IOException
   {
      // Method Instances
      JarURLConnection jarURLConnection;
      JarFile jarFile;
      ZipEntry zipEntry;
      InputStream inputStream;

      // Setup
      jarFile = null;
      inputStream = null;

      try
      {
         if (cacheJar && (resourceURL.toExternalForm().indexOf("jar:file:") == -1))
         {
            URL cachedJarURL = new URL("jar:file:" + cachedJAR_FileName + "!/");
            jarURLConnection = (JarURLConnection) (cachedJarURL).openConnection();
         }
         else
            jarURLConnection = (JarURLConnection) resourceURL.openConnection();

         jarFile = jarURLConnection.getJarFile();
         
         zipEntry = jarFile.getEntry(localeFileName);

         // Try Brute Forceing It
         if (zipEntry == null)
         {
            for (Enumeration<?> entries = jarFile.entries(); entries.hasMoreElements();)
            {
               zipEntry = (ZipEntry) entries.nextElement();
               
               // Locale File Qualifier
               if (zipEntry.getName().endsWith(localeFileName))
               {
                  break;
               }
               else
                  zipEntry = null;
            }
         }

         // Resource found so process
         if (zipEntry != null)
         {
            inputStream = jarFile.getInputStream((zipEntry));
            readLocaleResource(inputStream);
         }
         else
         {
            displayErrors(className + " createJAR_LocaleResource() \n"
                          + "Failed to find the given locale file in JAR.\n" + localeFileName);
         }
      }
      catch (IOException ioe)
      {
         displayErrors(className + " createJAR_LocaleResource() \n"
                       + "Failed to process the given locale file, " + localeFileName + "\n"
                       + ioe.toString());
      }
      finally
      {
         if (inputStream != null)
            inputStream.close();

         if (jarFile != null)
            jarFile.close();
      }
   }

   //==============================================================
   // Class Method to read the input locale data given the input
   // stream from the selected resource type.
   //==============================================================

   private void readLocaleResource(InputStream inputStream) throws IOException
   {
      // Method Instances
      InputStreamReader inputStreamReader;
      BufferedReader bufferedReader;

      String currentEntry;
      String key, resource;

      // Setup
      inputStreamReader = null;
      bufferedReader = null;

      try
      {
         inputStreamReader = new InputStreamReader(inputStream, "UTF-16");
         bufferedReader = new BufferedReader(inputStreamReader);

         while ((currentEntry = bufferedReader.readLine()) != null)
         {
            currentEntry = currentEntry.trim();

            if (currentEntry.indexOf("=") != -1)
            {
               key = currentEntry.substring(0, currentEntry.indexOf("=")).trim();
               resource = currentEntry.substring(currentEntry.indexOf("=") + 1).trim();
               // System.out.println(key + " " + resource);

               localeListData.put(key, resource);
            }
         }
         bufferedReader.close();
         inputStreamReader.close();
      }
      catch (IOException ioe)
      {
         displayErrors(className + " readLocaleResource() \n"
                       + "Failed to process the given locale file, " + resourceURL.toExternalForm() + "\n"
                       + ioe.toString());
      }
      finally
      {
         if (bufferedReader != null)
            bufferedReader.close();

         if (inputStreamReader != null)
            inputStreamReader.close();
      }
   }

   //==============================================================
   // Class method to collect an image resource from a file
   // resource type.
   //==============================================================

   private byte[] getJAR_Resource(String resourceName, URL resourceURL) throws IOException
   {
      // Method Instances
      JarURLConnection jarURLConnection;
      JarFile jarFile;
      ZipEntry zipEntry;
      InputStream inputStream;
      int fileSize;
      byte[] resourceBytes;

      // Setup
      jarFile = null;
      inputStream = null;
      resourceBytes = null;

      try
      {
         // System.out.println("Resource JAR: " + imageResourceURL.toExternalForm());
         
         jarURLConnection = (JarURLConnection) resourceURL.openConnection();
         jarFile = jarURLConnection.getJarFile();

         zipEntry = jarFile.getEntry(resourceName);

         // Try Brute Forcing it.
         if (zipEntry == null)
         {
            for (Enumeration<?> entries = jarFile.entries(); entries.hasMoreElements();)
            {
               zipEntry = (ZipEntry) entries.nextElement();

               // Image File Qualifier
               if (zipEntry.getName().equals(resourceName))
                  break;
               else
                  zipEntry = null;
            }
         }

         // Resource found so process
         if (zipEntry != null)
         {
            inputStream = jarFile.getInputStream(zipEntry);

            fileSize = (int) zipEntry.getSize();

            // Obtain bytes
            if (fileSize != -1)
            {
               resourceBytes = new byte[fileSize];

               int readPosition = 0;
               int byteChunk = 0;

               while ((fileSize - readPosition) > 0)
               {
                  byteChunk = inputStream.read(resourceBytes, readPosition, fileSize - readPosition);
                  if (byteChunk == -1)
                     break;

                  readPosition += byteChunk;
               }
               return resourceBytes;
            }
            else
            {
               if (debugMode)
                  System.out.println(className + " getJAR_Resource()\n"
                                     + "Failed to Determine Resource Size: " + resourceName);
               return new byte[0];
            }
         }
         else
         {
            if (debugMode)
               System.out.println(className + " getJAR_Resource()\n"
                                  + "Resource Entry Not Found: " + resourceName);
            return new byte[0];
         }
      }
      catch (IOException ioe)
      {
         if (debugMode)
            System.out.println(className + " getJAR_Resource()\n"
                               + "Failed Processing of Resource from Jar: " + resourceName);
         return new byte[0];
      }
      finally
      {
         String message = "";
         
         try
         {
            if (inputStream != null)
               inputStream.close();
         }
         catch (IOException ioe)
         {
            message = ioe.toString();
         }
         finally
         {
            if (jarFile != null)
               jarFile.close();
         }
         
         if (!message.isEmpty())
            throw new IOException(message);
      }
   }
   
   //==============================================================
   // Class Method to cache a JAR file if it is not a local file
   // system resource.
   //==============================================================

   private boolean cacheJAR(String resourceURLString) throws IOException
   {
      // Method Instances
      InputStream inputStream;
      BufferedInputStream inputBuffer;
      int inByte;

      File cacheDirectoryFile;
      FileOutputStream fileOutputStream;
      BufferedOutputStream fileOutputBuffer;

      boolean cached;

      // Setup
      cached = false;
      inputStream = null;
      inputBuffer = null;
      fileOutputStream = null;
      fileOutputBuffer = null;

      // Open a stream for the JAR then byte it to the
      // local cache file.
      try
      {
         inputStream = (new URL(resourceURLString)).openStream();
         inputBuffer = new BufferedInputStream(inputStream);

         cacheDirectoryFile = new File(cacheDirectory);

         if (!cacheDirectoryFile.isDirectory())
            if (!cacheDirectoryFile.mkdir())
               if (debugMode)
               {
                  System.out.println(className + " cacheJAR()\n"
                                     + "Failed to create Cache Directory");
                  return cached;
               }

         fileOutputStream = new FileOutputStream(cachedJAR_FileName);
         fileOutputBuffer = new BufferedOutputStream(fileOutputStream);

         while ((inByte = inputBuffer.read()) != -1)
            fileOutputBuffer.write(inByte);

         fileOutputBuffer.flush();
         cached = true;
      }
      catch (IOException ioe)
      {
         if (debugMode)
            System.out.println(className + " cacheJAR()\n"
                               + "Failed to Cache JAR.\n" + ioe.toString());
      }
      finally
      {
         try
         {
            if (fileOutputBuffer != null)
               fileOutputBuffer.close();
         }
         catch (IOException ioe)
         {
            if (debugMode)
               System.out.println(className + " cacheJAR() Failed to Close "
                                  + "fileOutputBuffer.\n" + ioe.toString());
         }
         finally
         {
            try
            {
               if (fileOutputStream != null)
                  fileOutputStream.close();
            }
            catch (IOException ioe)
            {
               if (debugMode)
                  System.out.println(className + " cacheJAR() Failed to Close "
                                     + "fileOutputStream.\n" + ioe.toString());
            }
            finally
            {
               try
               {
                  if (inputBuffer != null)
                     inputBuffer.close();
               }
               catch (IOException ioe)
               {
                  if (debugMode)
                     System.out.println(className + " cacheJAR() Failed to Close "
                                        + "inputBuffer.\n" + ioe.toString());
               }
               finally
               {
                  if (inputStream != null)
                     inputStream.close();
               }
            }
         }    
      }
      return cached;
   }

   //==============================================================
   // Class method to display an error to the standard output if
   // some type of resource creation/processing occured.
   //==============================================================

   private void displayErrors(String errorString)
   {
      JOptionPane.showMessageDialog(null, errorString, "Alert", JOptionPane.ERROR_MESSAGE);
   }
}
