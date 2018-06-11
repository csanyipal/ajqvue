//=================================================================
//                       PluginRepository
//=================================================================
//
//    This class provides the general framework and link to the
// PluginRepository Interface inheritance for all PluginReposities
// in the framework. The aspects that are needed in order to properly
// define a file/network repository.
//
//                 << PluginRepository.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.2 02/03/2017
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
// Version 1.0 Production PluginRepository Class.
//         1.1 Corrected Class Instance FTPS Value to ftps.
//         1.2 Added Class Instance repositoryOptions Along With Getter/Setter. Made
//             Method displayErrors() Protected.
//        
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.io.WriteDataFile;
import com.dandymadeproductions.ajqvue.structures.GeneralProperties;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The PluginRepository class provides the general framework and link
 * to the PluginRepository Interface inheritance for all PluginReposities
 * in the framework. The class defines the aspects that are needed in order
 * to properly derive a file/network repository.   
 * 
 * @author Dana M. Proctor
 * @version 1.2 02/03/2017
 */

public abstract class PluginRepository implements PluginRepositoryInterface
{
   // Class Instances
   private String repositoryName;
   private String repositoryPath;
   private String repositoryType;
   private String[] repositoryOptions;
   private ArrayList<Plugin> pluginsList;
   
   protected GeneralProperties generalProperties;
   protected String remoteRepositoryURL;
   protected String cachedRepositoryURL;
   protected String cachedRepositoryDirectoryString;
   protected boolean downloadRepository;
   protected boolean isRepositoryCached;
   protected boolean debugMode;
   
   public static final String FILE = "file";
   public static final String FTP = "ftp";
   public static final String FTPS = "ftps";
   public static final String HTTP = "http";
   public static final String HTTPS = "https";
   public static final String UNKNOWN = "unknown";
   public static final String REPOSITORY_PATH_FILE = ".path";
   
   protected static final int GZIP_MAGIC_1 = 0x1f;
   protected static final int GZIP_MAGIC_2 = 0x8b;
   protected static final String REPOSITORY_FILENAME = "ajqvue_plugin_list.xml.gz";
   protected static final String REPOSITORY_CACHED_FILE = "repository-cache.xml.gz";
   
   //===========================================================
   // PluginRepository Constructor
   //
   // **NOTE**
   //
   // A repository can not be just created from the constructor
   // alone. The repository needs to use the methods setName()
   // and setRepository() to properly set the repository for use.
   // The method setName() can be called from here, but each
   // unique repository type that extends this class should
   // implement their own loadRepositoryList() method.
   //===========================================================
   
   public PluginRepository(boolean downloadRepo)
   {
      repositoryName = "";
      repositoryPath = "";
      repositoryType = UNKNOWN;
      
      downloadRepository = downloadRepo;
      isRepositoryCached = false;
      debugMode = Ajqvue.getDebug();
      
      generalProperties = Ajqvue.getGeneralProperties();
      pluginsList = new ArrayList <Plugin>();
   }
   
   //==============================================================
   // Class method to set the repository name.
   //==============================================================
   
   public void setName(String name)
   {
      repositoryName = name; 
   }
   
   //==============================================================
   // Class method to set the repository path.
   //==============================================================
   
   public void setPath(String path)
   {
      // Method Instances
      String cachedRepositoryPathFile;
      
      repositoryPath = path;
      
      if (!path.isEmpty() && isRepositoryCached)
      {
         cachedRepositoryPathFile = cachedRepositoryDirectoryString
                                    + Utils.getFileSeparator()
                                    + PluginRepository.REPOSITORY_PATH_FILE;
         WriteDataFile.mainWriteDataString(cachedRepositoryPathFile, path.getBytes(), false);
      }
   }
   
   //==============================================================
   // Class method to set the repository type.
   //==============================================================
   
   public void setType(String type)
   {
      repositoryType = type; 
   }
   
   //==============================================================
   // Class method to set the repository options, ftp(s).
   //==============================================================
   
   public void setOptions(String[] options)
   {
      repositoryOptions = options; 
   }
   
   //==============================================================
   // Class method to setup up the repository.
   //
   // File. The file repository could either be locally or lan.
   // ex. C:\Users\Documents\plugin
   // ex. \\pc101\Users\jim\Documents\plugin
   // ex. /home/user/documents/plugin
   //
   // Ftp.
   // ex. ftp(s)://dandymadeproductions.com/
   //
   // Http.
   // ex. http(s)://dandymadeproductions.com/
   //==============================================================

   public boolean setRepository(String path)
   {
      // Method Instances
      File cachedRepositoryDirectory, cachedRepositoryListFile;
      String localSystemFileSeparator;
      boolean validRepository;
      
      // Setup
      
      if (path.endsWith("/"))
         remoteRepositoryURL = path + PluginRepository.REPOSITORY_FILENAME;
      else
         remoteRepositoryURL = path + "/" + PluginRepository.REPOSITORY_FILENAME;
      
      cachedRepositoryDirectoryString = Utils.getCacheDirectory() + getName();
      localSystemFileSeparator = Utils.getFileSeparator();
      validRepository = false;
      
      // Check for a valid existing repository that is cached and if
      // not create at least the directory. A valid repository must
      // exist, isRepositoryCached, and contain the cached file.
      
      cachedRepositoryDirectory = new File(cachedRepositoryDirectoryString);
      
      if (cachedRepositoryDirectory.exists() && cachedRepositoryDirectory.isDirectory())
      {
         isRepositoryCached = true;
         
         cachedRepositoryListFile = new File(cachedRepositoryDirectoryString
                                             + localSystemFileSeparator
                                             + PluginRepository.REPOSITORY_CACHED_FILE);
         
         if (cachedRepositoryListFile.isFile())
            validRepository = true;
         else
            validRepository = false;
      }
      else
      {
         isRepositoryCached = cachedRepositoryDirectory.mkdir();
         validRepository = false;
      }
      
      // Create the cached repository URL String
      
      if (isRepositoryCached)
         cachedRepositoryURL = "file:" + cachedRepositoryDirectoryString
                               + localSystemFileSeparator
                               + PluginRepository.REPOSITORY_CACHED_FILE;
      else
         cachedRepositoryURL = remoteRepositoryURL;
      
      if (debugMode)
         System.out.println("PluginRepository setRepository() Repository: " + getName() + "\n"
                            + "remoteRepositoryURL: " + remoteRepositoryURL + "\n" 
                            + "cachedRepositoryURL: " + cachedRepositoryURL);
      
      // Determine if a download should occur.
      
      if (downloadRepository && isRepositoryCached && !validRepository)
         validRepository = loadPluginList();
      
      // Read the plugin list from the cache & set path to
      // the cache directory for later identification when
      // reloading application's plugin frame.
      
      if (validRepository)
         validRepository = readPluginList(true);
      
      if (validRepository)
      {
         setPath(path);
         
         Iterator<Plugin> pluginIterator = getPluginItems().iterator();
         
         while (pluginIterator.hasNext())
         {
            Plugin currentPlugin = pluginIterator.next();
            
            currentPlugin.setPath_FileName(remoteRepositoryURL.substring(0,
               remoteRepositoryURL.length() - PluginRepository.REPOSITORY_FILENAME.length())
               + currentPlugin.getJAR());
            
            // System.out.println("PluginRepository setRepository() plugin path: "
            //                    + currentPlugin.getPath_FileName()); 
         }
      }
      
      return validRepository;
   }
   
   //==============================================================
   // Class method to refresh the repository by trying to download
   // the plugin list and reading the cache again.
   //==============================================================
   
   public void refresh()
   {
      if (isRepositoryCached && loadPluginList())
         readPluginList(true);
      
      Iterator<Plugin> pluginIterator = getPluginItems().iterator();
      
      while (pluginIterator.hasNext())
      {
         Plugin currentPlugin = pluginIterator.next();
         
         currentPlugin.setPath_FileName(remoteRepositoryURL.substring(0,
            remoteRepositoryURL.length() - PluginRepository.REPOSITORY_FILENAME.length())
            + currentPlugin.getJAR());
         
         // System.out.println("PluginRepository refresh() plugin path: "
         //                     + currentPlugin.getPath_FileName());
      }
   }
   
   //==============================================================
   // Class method to read the repository plugin list from the
   // cache.
   //==============================================================
   
   public boolean readPluginList(boolean allowRetry)
   {
      // Class methods
      XMLReader xmlParser;
      PluginListHandler pluginListHandler;
      
      URL cachedURL;
      URLConnection urlConnection;
      
      InputStream urlInputStream;
      InputStream inputStream;
      InputStreamReader inputStreamReader;
      InputSource inputSource;
      
      boolean validRead;
      
      // Setup
      urlInputStream = null;
      inputStream = null;
      inputStreamReader = null;
      validRead = false;
      
      // Try reading & parsing list.
      try
      {
         /*
         if (!cachedRepositoryURL.equals(remoteRepositoryURL) && debugMode) 
               System.out.println("PluginRepository readPluginList() Using Cached Plugin List.");
         */
         
         cachedURL = new URL(cachedRepositoryURL);
         urlConnection = cachedURL.openConnection(Proxy.NO_PROXY);
         urlInputStream = urlConnection.getInputStream();
         
         xmlParser = XMLReaderFactory.createXMLReader();
         pluginListHandler = new PluginListHandler(this);
         inputStream = new BufferedInputStream(urlInputStream);
         
         // Check to see if list is xml, or zip/gz file.
         if(inputStream.markSupported())
         {
            inputStream.mark(2);
            int b1 = inputStream.read();
            int b2 = inputStream.read();
            inputStream.reset();

            if(b1 == GZIP_MAGIC_1 && b2 == GZIP_MAGIC_2)
               inputStream = new GZIPInputStream(inputStream);
         }
         
         // Setup stream and parse.
         inputStreamReader = new InputStreamReader(inputStream, "UTF8");
         inputSource = new InputSource(inputStreamReader);
         
         inputSource.setSystemId("Ajqvue.jar");
         xmlParser.setContentHandler(pluginListHandler);
         xmlParser.setDTDHandler(pluginListHandler);
         xmlParser.setEntityResolver(pluginListHandler);
         xmlParser.setErrorHandler(pluginListHandler);
         xmlParser.parse(inputSource);
         
         validRead = true;
      }
      catch (MalformedURLException e)
      {
         readPluginListException(e, allowRetry);
      }
      catch (UnknownHostException e)
      {
         readPluginListException(e, allowRetry);
      }
      // IOException, UnsupportedEncodingException
      catch (IOException e)
      {
         readPluginListException(e, allowRetry);
      }
      catch (SAXException e)
      {
         readPluginListException(e, allowRetry);
      }
      finally
      {
         try
         {
            if (urlInputStream != null)
               urlInputStream.close();
         }
         catch (IOException ioe)
         {
            if (debugMode)
               System.out.println("PluginRepository readPluginList() "
                                  + "Failed to close urlInputStream. " + ioe.toString());
         }
         finally
         {
            try
            {
               if (inputStream != null)
                  inputStream.close();  
            }
            catch (IOException ioe1)
            {
               if (debugMode)
                  System.out.println("PluginRepository readPluginList() "
                                     + "Failed to close inputStream. " + ioe1.toString());
            }
            finally
            {
               try
               {
                  if (inputStreamReader != null)
                     inputStreamReader.close();  
               }
               catch (IOException ioe1)
               {
                  if (debugMode)
                     System.out.println("PluginRepository readPluginList() "
                                        + "Failed to close inputStreamReader. " + ioe1.toString());
               }
            }
         }
      }
      return validRead;
   }
   
   //==============================================================
   // Class method to add an plugin item to the repository.
   //==============================================================

   public void addPluginItem(Plugin pluginItem)
   {
      pluginsList.add(pluginItem);
   }
   
   //==============================================================
   // Class method to clear the plugin items in the repository
   // list.
   //==============================================================

   public void clearPluginItems()
   {
      pluginsList.clear();
   }
   
   //==============================================================
   // Class method to allow the collection of a name that will be
   // associated with the repository.
   //==============================================================

   public String getName()
   {
      return repositoryName;
   }
   
   //==============================================================
   // Class method to allow the collection of a path that will be
   // associated with the repository.
   //==============================================================
   
   public String getPath()
   {
      return repositoryPath;
   }
   
   //==============================================================
   // Class method to allow the return of some predifined type,
   // example file, http, ftp, other?
   //==============================================================
   
   public String getRepositoryType()
   {
      return repositoryType;
   }
   
   //==============================================================
   // Class method to allow the return of some predifined type,
   // example file, http, ftp, other?
   //==============================================================
   
   public String[] getRepositoryOptions()
   {
      return repositoryOptions;
   }
   
   //==============================================================
   // Class method to allow the collection of the list of plugins
   // that are associated with the repository.
   //==============================================================

   public ArrayList<Plugin> getPluginItems()
   {
      return pluginsList;
   }
   
   //==============================================================
   // Class method to handle exceptions with the readPluginList().
   //==============================================================
   
   public void readPluginListException(Exception e, boolean allowRetry)
   {
      if (cachedRepositoryURL.startsWith("file:") && allowRetry)
      {
         clearPluginItems();
         
         if (e instanceof SAXException)
            loadPluginList();
         
         readPluginList(false);
      }
      else
         displayErrors("PluginRepository readPluginList() Exception: " + e.toString());
   }
   
   //==============================================================
   // Class method to allow the displaying an error to the user
   // if something goes wrong with the loading of repository.
   //==============================================================

   protected void displayErrors(String errorString)
   {
      if (errorString.length() > 200)
         errorString = errorString.substring(0, 200);

      String optionPaneStringErrors = "Error Loading Repository\n" + errorString;

      JOptionPane.showMessageDialog(null, optionPaneStringErrors, "Alert", JOptionPane.ERROR_MESSAGE);
   }
}
