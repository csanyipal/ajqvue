//=================================================================
//                    FTP_PluginRepository.
//=================================================================
//
//    This class provides the general framework to create a FTP
// type repository that would be derived from a ftp server. A web
// FTP type repository will try to cache the plugin list as derived
// from a XML file at the resource.
//
//                 << FTP_PluginRepository.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.0 09/19/2016
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
// Version 1.0 Production FTP_PluginRepository Class.
//             
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 *    The FTP_PluginRepository class provides the general framework
 * to create a FTP type repository that would be derived from a server.
 * A FTP type repository will try to cache the plugin list as derived
 * from a XML file at the resource.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/19/2016
 */

public class FTP_PluginRepository extends PluginRepository
{
   // Class Instances
   
   //==============================================================
   // FTP_PluginRepository Constructor
   //==============================================================

   public FTP_PluginRepository()
   {
      super(true);
      
      setType(PluginRepository.FTP);
   }
   
   //==============================================================
   // Class method to download the repository list and in so doing
   // populate the cache.
   //==============================================================
   
   public boolean loadPluginList()
   {
      // Method Instances
      Proxy httpProxy;
      URL downloadURL;
      URLConnection ftpConnection;
      
      InputStream inputStream;
      BufferedInputStream bufferedInputStream;
      FileOutputStream fileOutputStream;
      BufferedOutputStream bufferedOutputStream;
      
      String cacheFileName;
      byte[] inputBytes;
      
      boolean validDownload;
      boolean testftp = false;
      
      // Setup
      inputStream = null;
      bufferedInputStream = null;
      fileOutputStream = null;
      bufferedOutputStream = null;
      validDownload = false;
      
      try
      {
         if (debugMode)
            System.out.println("FTP_PluginRepository loadPluginList() Downloading Repository List");
         
         downloadURL = new URL(remoteRepositoryURL);
         
         // Proxy as Needed.
         if (generalProperties.getEnableProxy())
         {
            httpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
               generalProperties.getProxyAddress(), generalProperties.getProxyPort()));
            ftpConnection = downloadURL.openConnection(httpProxy);
         }
         else
            ftpConnection = downloadURL.openConnection(Proxy.NO_PROXY);
         
         // Code not implemented as of v1.1
      
         // Control
         if (!testftp)
         {
            displayErrors("FTP_PluginRepository loadPlugins()\n"
                          + "Set testftp to true to test.");
         }
         // Looks Good.
         else
         {
            cacheFileName = cachedRepositoryURL.replaceFirst("file:", "");
            
            // Create Streams
            
            inputStream = ftpConnection.getInputStream();
            bufferedInputStream = new BufferedInputStream(inputStream);
            
            fileOutputStream = new FileOutputStream(cacheFileName);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             
            // Setup Buffer, read, write, flush.
            inputBytes = new byte[4096];
            
            int n;
            
            while ((n = bufferedInputStream.read(inputBytes)) != -1)
               bufferedOutputStream.write(inputBytes, 0, n);
            
            bufferedOutputStream.flush();
            fileOutputStream.flush();
            
            validDownload = true;
            
         }
      }
      catch(MalformedURLException e)
      {
         displayErrors("FTP_PluginRepository loadPluginList() Exception:\n" + e.toString());
      }
      catch (UnknownHostException e)
      {
         displayErrors("FILE_PluginRepository loadPluginList() Exception:\n" + e.toString());
      }
      // IOException & FileNotFoundException
      catch (IOException e)
      {
         displayErrors("HTTP_PluginRepository loadPluginList() Exception:\n" + e.toString());
      }
      finally
      {
         try
         {
            if (bufferedOutputStream != null)
               bufferedOutputStream.close();
         }
         catch (IOException ioe1)
         {
            if (debugMode)
               System.out.println("FTP_PluginRepository loadPluginList() "
                                  + "Failed to close BufferedOutputStream. " + ioe1.toString());
         }
         finally
         {
            try
            {
               if (fileOutputStream != null)
                  fileOutputStream.close();
            }
            catch (IOException ioe2)
            {
               if (debugMode)
                  System.out.println("FTP_PluginRepository loadPluginList() "
                                     + "Failed to close FileOutputStream. " + ioe2.toString());
            }
            finally
            {
               try
               {
                  if (bufferedInputStream != null)
                     bufferedInputStream.close();
               }
               catch (IOException ioe3)
               {
                  if (debugMode)
                     System.out.println("FTP_PluginRepository loadPluginList() "
                                        + "Failed to close BufferedInputStream. " + ioe3.toString());
               }
               finally
               {
                  try
                  {
                     if (inputStream != null)
                        inputStream.close();
                  }
                  catch (IOException ioe4)
                  {
                     if (debugMode)
                        System.out.println("FTP_PluginRepository loadPluginList() "
                                           + "Failed to close InputStream. " + ioe4.toString());
                  }
               }
            }
         }
      }
      return validDownload;
   }
}