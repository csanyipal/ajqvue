//=================================================================
//                    PluginLoader Class
//=================================================================
// 
//    This class is used to cycle through the jar/zip files located
// in the plugin directory under the framework's installation directory
// lib to find Plugin Modules. Only classes that match the interface
// Plugin_Module will be loaded.
//
//                     << PluginLoader.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.1 09/24/2016
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
// Version 1.0 09/19/2016 Production PluginLoader Class.
//         1.1 09/24/2016 Updated References to PluginModule to Plugin_Module.
//                        
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.gui.Main_Frame;
import com.dandymadeproductions.ajqvue.gui.PluginFrame;
import com.dandymadeproductions.ajqvue.io.WriteDataFile;
import com.dandymadeproductions.ajqvue.structures.GeneralProperties;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The PluginLoader class is used to cycle through the jar/zip files
 * located in the plugin directory under the framework's installation
 * directory lib to find Plugin Modules. Only classes that match the
 * interface Plugin_Module will be loaded.
 * 
 * @author Dana M. Proctor
 * @version 1.1 09/24/2016
 */

public class PluginLoader implements Runnable
{
   // Class Instances.
   Thread pluginLoaderThread;
   private Main_Frame parentFrame;
   private URL pluginURL;
   private String repositoryName;
   
   private String fileSeparator;
   private String pluginDirectoryString;
   private String pluginFileName;
   private String pluginConfigFileString;
   private HashMap<String, String> pluginEntriesHashMap;
   
   private GeneralProperties generalProperties;
   
   private static final String CONFIGURATION_FILENAME = "ajqvue_plugin.conf";
   private static final String VALID_PLUGIN_MODULENAME = "PluginModule.class";
   public static final String pathClassSeparator = "<$$$>";

   //==============================================================
   // PluginLoader Constructor(s)
   //
   // One Argument Constructor is used by the framework during
   // startup to load default plugins that installed from the
   // lib/plugins directory.
   //
   // Two Argument Constructor should be used to either load plugins
   // from the Plugin Management tool, or possibly externally by
   // outside classes.
   //==============================================================

   public PluginLoader(Main_Frame parent)
   {
      try
      {
         init(parent, new URL("file:"), "");
      }
      catch (MalformedURLException mfe)
      {
         displayErrors("PluginLoader Constructor failed to create empty file:URL\n"
                       + mfe.toString());
      }
   }
   
   public PluginLoader(Main_Frame parent, URL pluginURL, String repositoryName)
   {
      init(parent, pluginURL, repositoryName);
   }
   
   //==============================================================
   // Class method for initialization and starting the class thread.
   //==============================================================
   
   private void init(Main_Frame parent, URL pluginURL, String repositoryName)
   {
      parentFrame = parent;
      this.pluginURL = pluginURL;
      this.repositoryName = repositoryName;
      
      if (pluginURL == null)
         return;
      
      // Setup.
      fileSeparator = Utils.getFileSeparator();
      pluginConfigFileString = Utils.getAjqvueConfDirectory()
                               + Utils.getFileSeparator() + CONFIGURATION_FILENAME;
      generalProperties = Ajqvue.getGeneralProperties();
      
      pluginFileName = pluginURL.getFile();
      
      if (pluginFileName.indexOf(fileSeparator) != -1)
         pluginFileName = pluginFileName.substring(pluginFileName.lastIndexOf(fileSeparator) + 1,
                                                   pluginFileName.length());
      // System.out.println("PluginLoader init() pluginFileName: '" + pluginFileName + "'");
      
      // Default install directory lib/plugins
      if (pluginFileName.equals(""))
         pluginDirectoryString = Utils.getAjqvueDirectory() + fileSeparator
                                 + "lib" + fileSeparator + "plugins" + fileSeparator;
      // Specified plugin
      else
         pluginDirectoryString = "";
         
      pluginEntriesHashMap = new HashMap<String, String>();
      
      // Create and start the class thread.
      pluginLoaderThread = new Thread(this, "PluginLoader Thread");
      // System.out.println("PluginLoader Thread");
      
      pluginLoaderThread.start();
   }

   //==============================================================
   // Class method for normal start of the thread.
   //==============================================================

   public void run()
   {
      // Obtain the plugin module(s) & install into application.
      
      // Plugin Management Tool Load
      if (!pluginFileName.equals(""))
      {
         try
         {
            loadPluginEntry();
         }
         catch (IOException ioe){};
      }
      
      // Default lib/plugins and configuration load.
      else
      {
         try
         {
            loadDefaultPluginEntries();
            loadConfigurationFilePluginEntries();
         }
         catch (IOException ioe)
         {
            displayErrors("PluginLoader run() \n" + ioe.toString());
         }
      }
      loadPluginModules();
   }
   
   //==============================================================
   // Class Method for loading a plugin module manually through
   // the framework Plugin Management tool in the top tab.
   //
   // File URL - file:lib/plugins/TableRecordCount.jar
   // Http URL - http://dandymadeproductions.com/temp/TableRecordCount.jar
   // JAR URL - jar:file:/home/duke/duke.jar!
   // FTP URL - ftp:/dandymadeproductions.com/
   //
   // Other Valid Java URL protocols, gopher, mailto, appletresource,
   // doc, netdoc, systemresource, & verbatim.
   //==============================================================

   private void loadPluginEntry() throws IOException
   {
      // Method Instances
      String className, currentFileName, pluginEntry;
      URL loadingPluginURL;
      
      ZipFile jarFile;
      File configurationFile;
      FileWriter fileWriter;
      
      // Check for a a valid jar file to be processed
      // then search for a plugin module.
      
      if (!pluginFileName.toLowerCase(Locale.ENGLISH).endsWith(".jar"))
         return;
      
      loadingPluginURL = pluginURL;
      jarFile = null;
      fileWriter = null;
      
      try
      {
         // Create a URL & then file to the JAR file
         // so that it can be searched.
         
         // Local File system plugin
         if (pluginURL.getProtocol().equals(PluginRepository.FILE))
         {
            jarFile = new ZipFile(new File((pluginURL.toExternalForm()).substring(
               pluginURL.toExternalForm().indexOf("file:") + 5)));
         }
         
         // Http(s) plugin
         else if (pluginURL.getProtocol().equals(PluginRepository.HTTP)
                  || pluginURL.getProtocol().equals(PluginRepository.HTTPS))
         { 
            Proxy httpProxy = null;
            String cachedJAR_FileName;
            
            // Try proxy
            if (generalProperties.getEnableProxy())
               httpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                  generalProperties.getProxyAddress(), generalProperties.getProxyPort()));
            
            // Try to Cache
            cachedJAR_FileName = Utils.getCacheDirectory() + repositoryName
                                 + fileSeparator + pluginFileName;
               
            try
            {
               if (cacheJAR(pluginURL.toExternalForm(), cachedJAR_FileName, httpProxy, true))
               {
                  loadingPluginURL = new URL("file:" + cachedJAR_FileName);
                  System.out.println("PluginLoader loadPluginEntry() JAR Cached: " + cachedJAR_FileName);
               }
               else
               {
                  if (Ajqvue.getDebug())
                     JOptionPane.showMessageDialog(null, "PluginLoader loadPluginEntry() "
                                                   + "Failed Cache of Network JAR", "Alert",
                                                   JOptionPane.ERROR_MESSAGE);
                  // !Review ?????
                  // If is http/https without authorization then
                  // can proceed, because the resource bundle does
                  // not need to be cached.
                  
                  // For now aprove all http/https on
                  // failure of caching.
                  // return;
               }
            }
            catch (IOException ioe)
            {
               if (Ajqvue.getDebug())
                  System.out.println("PluginLoader loadPluginEntry()\n"
                                     + "Failed to Close Cache Stream.\n" + ioe.toString());
            }
            
            URL jarUrl = new URL(loadingPluginURL, "jar:" + loadingPluginURL + "!/");
            JarURLConnection conn = (JarURLConnection) jarUrl.openConnection();
            jarFile = conn.getJarFile();
         }
         
         // Unknown
         else
            return;
          
         // Search
         className = "";
         
         for (Enumeration<?> entries = jarFile.entries(); entries.hasMoreElements();)
         {
            currentFileName = ((ZipEntry) entries.nextElement()).getName();

            // Plugin Qualifier
            if (currentFileName.endsWith(".class") && currentFileName.indexOf("$") == -1
                && currentFileName.indexOf(VALID_PLUGIN_MODULENAME) != -1)
            {
               currentFileName = currentFileName.replaceAll("/", ".");
               currentFileName = currentFileName.substring(0, currentFileName.indexOf(".class"));
               className = currentFileName;
               
               if (className.startsWith("java.") || className.startsWith("javax."))
                  continue;
               
               pluginEntriesHashMap.put(loadingPluginURL.toExternalForm(), className);
               // System.out.println("PluginLoader loadPluginEntry() Located: "
               //                    + loadingPluginURL.toExternalForm() + " " + className);
            }
         }
         
         // Update the configuration file indicating valid
         // plugin modules that have been loaded manually.
         
         if (!pluginFileName.equals("") && !className.equals(""))
         {
            pluginEntry = loadingPluginURL.toExternalForm() + pathClassSeparator + className + "\n";
            
            // Write new or appending. 
            configurationFile = new File(pluginConfigFileString);
               
            if (!configurationFile.exists())
               WriteDataFile.mainWriteDataString(pluginConfigFileString, pluginEntry.getBytes(), false);
            else
            {
               fileWriter = new FileWriter(pluginConfigFileString, true);
               char[] buffer = new char[pluginEntry.length()];
               pluginEntry.getChars(0, pluginEntry.length(), buffer, 0);
               fileWriter.write(buffer);
               fileWriter.flush();
            }
         }
      }
      catch (MalformedURLException e)
      {
         displayErrors("PluginLoader loadPluginEntry() MalFormedException: " + pluginFileName + "\n"
                        + e.toString());
      }
      catch (IOException e)
      {
         displayErrors("PluginLoader loadPluginEntry() IOException: " + pluginFileName + "\n"
                        + e.toString());
      }
      finally
      {
         try
         {
            if (jarFile != null)
               jarFile.close();
         }
         catch (IOException ioe)
         {
            displayErrors("PluginLoader loadPluginEntry() Failed to close jarFile\n"
                          + ioe.toString());
         }
         finally
         {
            if (fileWriter != null)
               fileWriter.close();
         }
      }
   }
   
   //==============================================================
   // Class Method to cache a JAR file if it is not a local file
   // system resource.
   //==============================================================

   private boolean cacheJAR(String resourceURLString, String cachedJAR_FileName,
                            Proxy proxy, boolean debugMode) throws IOException
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
         URL url = new URL(resourceURLString);
         
         // Handle HTTP
         if (pluginURL.getProtocol().equals(PluginRepository.HTTP))
         {
            HttpURLConnection httpConnection;
            
            if (proxy != null)
               httpConnection = (HttpURLConnection) url.openConnection(proxy);
            else
               httpConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
            
            // Authorization Needed.
            if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
               displayErrors("PluginLoader cacheJAR()\n"
                             + httpConnection.getResponseMessage());
               return false;
            }
            else
               inputStream = httpConnection.getInputStream();
         }
         // Handle HTTPS
         else if (pluginURL.getProtocol().equals(PluginRepository.HTTPS))
         {
              HttpsURLConnection httpsConnection;
              
              if (proxy != null)
                 httpsConnection = (HttpsURLConnection) url.openConnection(proxy);
              else
                 httpsConnection = (HttpsURLConnection) url.openConnection(Proxy.NO_PROXY);
              
              // Check.
              if (httpsConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                 inputStream = httpsConnection.getInputStream();
              else
              {
                 displayErrors("PluginLoader cacheJAR()\n"
                               + httpsConnection.getResponseMessage());
                 return false;
              }
                 
         }
         // Handle File
         else
            inputStream = (new URL(resourceURLString)).openStream();
         
         inputBuffer = new BufferedInputStream(inputStream);

         cacheDirectoryFile = new File(Utils.getCacheDirectory());

         if (!cacheDirectoryFile.isDirectory())
            if (!cacheDirectoryFile.mkdir())
               if (debugMode)
               {
                  System.out.println("PluginLoader" + " cacheJAR()\n"
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
            System.out.println("PluginLoader" + " cacheJAR()\n"
                               + "Failed to Cache JAR.\n" + ioe.toString());
         return cached;
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
               System.out.println("PluginLoader" + " cacheJAR() Failed to Close "
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
                  System.out.println("PluginLoader" + " cacheJAR() Failed to Close "
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
                     System.out.println("PluginLoader" + " cacheJAR() Failed to Close "
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
   // Class Method for reviewing the JAR files in the lib/plugin/
   // directory in search of a PluginModule to be then loaded.
   //==============================================================

   private void loadDefaultPluginEntries() throws IOException
   {
      // Method Instances
      File filePluginsDirectory;
      String[] jarFileNames;
      String pathKey, currentFileName;
      ZipFile jarFile;

      // Create the file for the plugin directory & load
      // directory contents.

      filePluginsDirectory = new File(pluginDirectoryString);
      
      if (!filePluginsDirectory.exists())
         return;
      
      jarFileNames = filePluginsDirectory.list();
      
      if (jarFileNames == null)
         return;
      
      jarFile = null;

      // Cycle through the files in search of plugins.
      for (int i = 0; i < jarFileNames.length; i++)
      {
         if (!jarFileNames[i].toLowerCase(Locale.ENGLISH).endsWith(".jar"))
            continue;

         try
         {
            jarFile = new ZipFile(pluginDirectoryString + jarFileNames[i]);

            for (Enumeration<?> entries = jarFile.entries(); entries.hasMoreElements();)
            {
               currentFileName = ((ZipEntry) entries.nextElement()).getName();

               // Plugin Qualifier
               if (currentFileName.endsWith(".class") && currentFileName.indexOf("$") == -1
                   && currentFileName.indexOf(VALID_PLUGIN_MODULENAME) != -1)
               {
                  pathKey =  (new URL("file:" + pluginDirectoryString + jarFileNames[i])).toExternalForm();
                  // System.out.println("PluginLoader loadDefaultPluginEntries() Located:" + pathKey);
                  
                  currentFileName = currentFileName.replaceAll("/", ".");
                  currentFileName = currentFileName.substring(0, currentFileName.indexOf(".class"));
                  
                  if (currentFileName.startsWith("java.") || currentFileName.startsWith("javax."))
                     continue;
                  
                  pluginEntriesHashMap.put(pathKey, currentFileName);
               }
            }
         }
         catch (MalformedURLException e)
         {
            displayErrors("PluginLoader loadDefaultPluginEntries() MalformedURLException: "
                          + jarFileNames[i] + "\n" + e.toString());
         }
         catch (IOException e)
         {
            displayErrors("PluginLoader loadDefaultPluginEntries() IOException: "
                  + jarFileNames[i] + "\n" + e.toString());
         }
         finally
         {
            if (jarFile != null)
               jarFile.close();
         }
      }
   }
   
   //==============================================================
   // Class Method for reviewing the entries in the configuration
   // file ajqvue_plugin.conf file to be loaded as plugins.
   //==============================================================

   private void loadConfigurationFilePluginEntries() throws IOException
   {
      // Method Instances
      String currentLine, pathKey, className;
      File configurationFile;
      FileReader fileReader;
      BufferedReader bufferedReader;
      
      fileReader = null;
      bufferedReader = null;
      
      try
      {
         // Check to see if file exists.
         configurationFile = new File(pluginConfigFileString);
         try
         { 
            if (!configurationFile.exists())
               return;
         }
         catch (SecurityException e)
         {
            displayErrors("PluginLoader loadConfigurationFilePluginEntries() Security Exception: "
                          + e.toString());
            return;
         }
         
         // Looks like there is a plugin configuration file
         // so collect the entries.
         
         fileReader = new FileReader(pluginConfigFileString);
         bufferedReader = new BufferedReader(fileReader);
            
         while ((currentLine = bufferedReader.readLine()) != null)
         {
            currentLine = currentLine.trim();
            
            if (currentLine.indexOf(pathClassSeparator) != -1)
            {
               pathKey = currentLine.substring(0, currentLine.indexOf(pathClassSeparator));
               className = currentLine.substring(currentLine.indexOf(pathClassSeparator)
                                                 + pathClassSeparator.length());
               
               if (className.startsWith("java.") || className.startsWith("javax."))
                  continue;
              
               pluginEntriesHashMap.put(pathKey, className);
               // System.out.println("PluginLoader loadConfigurationFilePluginEntry() Located:" + pathKey); 
            }
            else
               continue;
         }
      }
      catch (IOException ioe) 
      {
         displayErrors("PluginLoader loadConfigurationFilePluginEntries() File I/O problem, reading "
                       + pluginConfigFileString + "\n" + ioe.toString());
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
            displayErrors("PluginLoader loadConfigurationFilePluginEntries() Failed to close bufferReader\n"
                          +ioe.toString());
         }
         finally
         {
            if (fileReader != null)
               fileReader.close();
         }
      }
   }

   //==============================================================
   // Class Method for loading the actual plugin module class
   // instances
   //==============================================================

   private void loadPluginModules()
   {
      // Method Instances.
      String iconsDirectory;
      ImageIcon defaultModuleIcon;

      // Obtain & create default Image Icon.

      iconsDirectory = Utils.getIconsDirectory() + fileSeparator;
      defaultModuleIcon = Ajqvue.getResourceBundle().getResourceImage(iconsDirectory
                                                                          + "newsiteLeafIcon.png");

      // Iterator through the found plugins and load them.

      Set<Map.Entry<String, String>> keySet = pluginEntriesHashMap.entrySet();
      Iterator<Map.Entry<String, String>> pluginIterator = keySet.iterator();

      while (pluginIterator.hasNext())
      {
         Map.Entry<String, String> pluginEntry = pluginIterator.next();
         
         final String pluginURLString = pluginEntry.getKey();
         // System.out.println("PluginLoader loadPluginModules() pluginURLString: " + pluginURLString);
         
         ClassLoader classLoader = AccessController.doPrivileged(new PriviledgedControl(pluginURLString));
         
         // If looks like a good plugin try to load it.
         
         if (classLoader != null)
         {
            // Create the instance and add to the framework.
            try
            {
               if (Ajqvue.getDebug())
                  System.out.println("PluginLoader loadPluginModules() "
                                     + pluginEntry.getValue());
               
               try
               {
                  Class<?> module = Class.forName(pluginEntry.getValue(), true, classLoader);
                  
                  if (module.newInstance() instanceof Plugin_Module)
                  {
                     Plugin_Module pluginModule = (Plugin_Module) module.newInstance();
                     pluginModule.path_FileName = pluginEntry.getKey() + pathClassSeparator + pluginEntry.getValue();

                     new PluginThread(parentFrame, pluginModule, defaultModuleIcon);
                  }
                  else
                     throw new Exception();
               }
               catch (ClassNotFoundException cnfe)
               {
                  throw new Exception(cnfe);
               }
               catch (NoClassDefFoundError ncdfe)
               {
                  throw new Exception(ncdfe);
               }
            }
            catch (Exception e)
            {
               displayErrors("PluginLoader loadPluginModules() Exception: \n" + e.toString());
               Thread removePluginConfigurationModuleThread = new Thread(new Runnable()
               {
                  public void run()
                  {
                     PluginFrame.removePluginConfigurationModule(pluginURLString);
                  }
               }, "PluginLoader.removePluginConfigurationModuleThread2");
               removePluginConfigurationModuleThread.start();
            }
         }
      }
   }
   
   //==============================================================
   // Class method to display an error to the standard output if
   // the debug option is active.
   //==============================================================

   public void displayErrors(String errorString)
   {
      if (Ajqvue.getDebug())
      {
         System.out.println(errorString);
      }
   }
   
   //==============================================================
   // Inner Class to handle the class loading.
   //==============================================================
   
   class PriviledgedControl implements PrivilegedAction<ClassLoader>
   {
      // Class Instances
      String pluginURLString;
      
      //==============================================================
      // PriviledgedControl Constructor
      //==============================================================
      
      PriviledgedControl(String pluginURLString)
      {
         this.pluginURLString = pluginURLString;
      }
      
      @Override
      public ClassLoader run()
      {
         try
         {
            return new URLClassLoader(new URL[] {new URL(pluginURLString)},
                                      ClassLoader.getSystemClassLoader());
         }
         catch (MalformedURLException mfe)
         {
            displayErrors("PluginLoader classLoader Exception: \n" + mfe.toString());
            Thread removePluginConfigurationModuleThread = new Thread(new Runnable()
            {
               public void run()
               {
                  PluginFrame.removePluginConfigurationModule(pluginURLString);
               }
            }, "PluginLoader.removePluginConfigurationModuleThread1");
            removePluginConfigurationModuleThread.start();
            return null;
         }
      }
   }
}