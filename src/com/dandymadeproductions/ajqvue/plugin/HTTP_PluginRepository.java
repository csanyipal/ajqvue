//=================================================================
//                      HTTP_PluginRepository.
//=================================================================
//
//    This class provides the general framework to create a HTTP
// type repository that would be derived from a web server. A web
// HTTP type repository will try to cache the plugin list as derived
// from a XML file at the resource.
//
//                 << HTTP_PluginRepository.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.1 01/31/2017
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
// Version 1.0 Production HTTP_PluginRepository Class.
//         1.1 Method loadPluginList() Added Additional Debug Output & try catch
//             Clause for SSLHandshakeException.
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
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

/**
 *    The HTTP_PluginRepository class provides the general framework to
 * create a HTTP type repository that would be derived from a web server.
 * A web HTTP type repository will try to cache the plugin list as derived
 * from a XML file at the resource.
 * 
 * @author Dana M. Proctor
 * @version 1.1 01/31/2017
 */

public class HTTP_PluginRepository extends PluginRepository
{
   // Class Instances
   
   //==============================================================
   // HTTP_PluginRepository Constructor
   //==============================================================

   public HTTP_PluginRepository(String type)
   {  
      super(true);
      
      setType(type);
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
      
      InputStream inputStream;
      BufferedInputStream bufferedInputStream;
      FileOutputStream fileOutputStream;
      BufferedOutputStream bufferedOutputStream;
      
      String cacheFileName;
      byte[] inputBytes;
      
      boolean validDownload;
      
      // Setup
      httpProxy = null;
      inputStream = null;
      bufferedInputStream = null;
      fileOutputStream = null;
      bufferedOutputStream = null;
      validDownload = false;
      
      try
      {
         if (debugMode)
         {
            System.out.println("HTTP_PluginRepository loadPluginList() Downloading Repository List");
            System.out.println("HTTP_PluginRepository loadPluginList() Repository: " + remoteRepositoryURL);
            System.out.println("HTTP_PluginRepository loadPluginList() Type: " + getRepositoryType());
         }
         
         downloadURL = new URL(remoteRepositoryURL);
         
         // Proxy as Needed.
         if (generalProperties.getEnableProxy())
            httpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
               generalProperties.getProxyAddress(), generalProperties.getProxyPort()));
         
         // Differentiate Between Http/Https.
         
         if (getRepositoryType().equals(PluginRepository.HTTP))
         {
            HttpURLConnection httpConnection;
            
            if (httpProxy != null)
               httpConnection = (HttpURLConnection) downloadURL.openConnection(httpProxy);
            else
               httpConnection = (HttpURLConnection) downloadURL.openConnection(Proxy.NO_PROXY);
            
            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
               inputStream = httpConnection.getInputStream();
            else
            {
               displayErrors("HTTP_PluginRepository loadPluginList() HTTP Error:\n"
                             + httpConnection.getResponseCode() + " : "
                             + httpConnection.getResponseMessage());
            }      
         }
         else if (getRepositoryType().equals(PluginRepository.HTTPS))
         {
            HttpsURLConnection httpsConnection;
            
            try
            {
               if (httpProxy != null)
                  httpsConnection = (HttpsURLConnection) downloadURL.openConnection(httpProxy);
               else
                  httpsConnection = (HttpsURLConnection) downloadURL.openConnection(Proxy.NO_PROXY);
               
               // System.out.println("HTTP_PluginRepository loadPluginList() https response:"
               //                    + httpsConnection.getResponseCode());
               
               if (httpsConnection.getResponseCode() == HttpsURLConnection.HTTP_OK)
               {
                  inputStream = httpsConnection.getInputStream();
                  // System.out.println("HTTP_PluginRepository loadPluginList() Ciper: "
                  //                    + httpsConnection.getCipherSuite());
               }
               else
               {
                  displayErrors("HTTP_PluginRepository loadPluginList() HTTPS Error:\n"
                                + httpsConnection.getResponseCode() + " : "
                                + httpsConnection.getResponseMessage());
               }   
               
            }
            catch (SSLHandshakeException ssle)
            {
               displayErrors("HTTP_PluginRepository loadPluginList() Exception:\n" + ssle.toString());
               return validDownload;
            }   
         }
         // Wrong Type
         else
            return validDownload;
         
         // Proceed with Loading.
         cacheFileName = cachedRepositoryURL.replaceFirst("file:", "");
         
         // Create Buffered Stream
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
      catch (MalformedURLException e)
      {
         displayErrors("HTTP_PluginRepository loadPluginList() Exception:\n" + e.toString());
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
               System.out.println("HTTP_PluginRepository loadPluginList() "
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
                  System.out.println("HTTP_PluginRepository loadPluginList() "
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
                     System.out.println("HTTP_PluginRepository loadPluginList() "
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
                        System.out.println("HTTP_PluginRepository loadPluginList() "
                                           + "Failed to close InputStream. " + ioe4.toString());
                  }
               }
            }
         }
      }
      return validDownload;
   }
}
