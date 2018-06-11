//=================================================================
//                    FTP_PluginRepository.
//=================================================================
//
//    This class provides the general framework to create a FTP
// type repository that would be derived from a ftp(s) server. A web
// FTP type repository will try to cache the plugin list as derived
// from a XML file at the resource.
//
//                 << FTP_PluginRepository.java >>
//
//=================================================================
// Copyright (C) 2016-2018 Dana M. Proctor
// Version 1.2 01/30/2017
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
//         1.1 Removed All Class Instances. Set options in Constructor to Parent Class
//             PluginRepository. Removed Class Method configureOptions(). Method
//             loadPluginList() Replaced Technical Aspects of Setting up ftpClient
//             Instance With New Class to Handle the Details, FTP_Client. Retained
//             the Basic I/O Details of Collecting Repository and Creating Cache.
//         1.2 Method loadPluginList() Passed Repository Type Only to FTP_Client
//             Constructor.
//             
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 *    The FTP_PluginRepository class provides the general framework
 * to create a FTP type repository that would be derived from a ftp(s)
 * server. A FTP type repository will try to cache the plugin list as
 * derived from a XML file at the resource.
 * 
 * @author Dana M. Proctor
 * @version 1.2 01/30/2017
 */

public class FTP_PluginRepository extends PluginRepository
{
   // Class Instances
   
   //==============================================================
   // FTP_PluginRepository Constructor
   //
   // !Note: Only supports FTP/FTPS (FTP over SSL).
   // Most hosted servers should support this configuration.
   //
   // Usage: No agrgument constructor excepts defaults. 
   // Possible Parameters: [options]
   //
   // -A - Anonymous login (omit username and password parameters).
   // -E - Encoding to use for control channel, ISO-8859-1, FTP.DEFAULT_CONTROL_ENCODING. 
   // -k secs - Use keep-alive timer (setControlKeepAliveTimeout).
   // -p true|false|protocol[,true|false] - Use FTPSClient with the specified protocol,
   //    ex. TLS/SSL, and/or isImplicit setting.
   // -S - SystemType set server system type (e.g. UNIX VMS WINDOWS).
   // -T all|valid|none - Use one of the built-in TrustManager implementations (none = JVM default).
   //==============================================================

   public FTP_PluginRepository(String type)
   {
      this(null, type);
   }
   
   public FTP_PluginRepository(String[] options, String type)
   {
      super(true);
      
      setType(type);
      setOptions(options);
   }
   
   //==============================================================
   // Class method to download the repository list and in so doing
   // populate the cache.
   //==============================================================
   
   public boolean loadPluginList()
   {
   // Method Instances
      FTPClient ftpClient;
      OutputStream outputStream;
      
      String cacheFileName;
      boolean validDownload;
      
      // Setup
      ftpClient = null;
      outputStream = null;
      validDownload = false;
      
      FTP_Client ftp_Client = new FTP_Client(getRepositoryType(), remoteRepositoryURL, getRepositoryOptions());
      ftpClient = ftp_Client.createFTPClient();
      
      if (ftpClient == null)
         return validDownload;
      
      try
      {
         if (debugMode)
         {
            System.out.println("FTP_PluginRepository loadPluginList() Downloading Repository List");
            System.out.println("FTP_PluginRepository loadPluginList() Repository: " + remoteRepositoryURL);
            System.out.println("FTP_PluginRepository loadPluginList() Type: " + getRepositoryType());
         }
         
         // Collect repository list file.
         cacheFileName = cachedRepositoryURL.replaceFirst("file:", "");
         
         outputStream = new FileOutputStream(cacheFileName);
         ftpClient.retrieveFile(PluginRepository.REPOSITORY_FILENAME, outputStream);
         
         if (ftpClient.getReplyCode() == FTPReply.FILE_UNAVAILABLE)
         {
            displayErrors("FTP_PluginRepository loadPluginList() Repository Not Found:\n"
                          + ftpClient.getReplyCode() + " : "
                          + ftpClient.getReplyString());
         }
         else
            validDownload = true;
         
         ftpClient.noop();
         ftpClient.logout();
      }
      catch (IOException e)
      {
         displayErrors("FTP_PluginRepository loadPluginList() IOException:\n" + e.toString());
      }
      finally
      {
         try
         {
            if (outputStream != null)
               outputStream.close();
         }
         catch (IOException e)
         {
            if (debugMode)
               System.out.println("FTP_PluginRepository loadPluginList() "
                                  + "Failed to close outputStream. " + e.toString());
         }
         finally
         {
            try
            {
               if (ftpClient != null && ftpClient.isConnected())
                  ftpClient.disconnect();
            }
            catch (IOException ioe)
            {
               if (debugMode)
                  System.out.println("FTP_PluginRepository loadPluginList() "
                                     + "Failed to disconnet ftpClient. " + ioe.toString());
            }     
         }
      }
      return validDownload; 
   }
}
