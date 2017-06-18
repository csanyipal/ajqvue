//=================================================================
//                        FTP_Client.
//=================================================================
//
//    This class provides the ability to setup and create a FTP
// Client bases on the apache.net.commons.ftp library.
//
//                   << FTP_Client.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.0 01/30/2017
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
// Version 1.0 Initial Ajqvue FTP_Client Class.
//           
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.plugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

import javax.swing.JOptionPane;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

import com.dandymadeproductions.ajqvue.Ajqvue;

/**
 *    The FTP_Client class provides the ability to setup and create
 * a FTP Client bases on the apache.net.commons.ftp library.
 * 
 * @author Dana M. Proctor
 * @version 1.0 01/30/2017
 */

public class FTP_Client
{
   // Class Instances
   private String repositoryType;
   private String remoteResourceURL;
   private String[] options;
   private String encoding;
   private long keepAliveTimeout;
   private String protocol;
   private String serverType;
   private String trustmgr;
   
   //==============================================================
   // FTP_Client Constructor
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

   public FTP_Client(String repositoryType, String remoteResourceURL, String[] options)
   {
      this.repositoryType = repositoryType;
      this.remoteResourceURL = remoteResourceURL;
      this.options = options;
      
      // Setup options.
      
      keepAliveTimeout = -1;
      
      if (options != null)
         configureOptions();
   }
   
   //==============================================================
   // Class method to configure the options if called.
   // See Usage.
   //==============================================================
   
   private void configureOptions()
   {
      int optionCount = 0;
      
      for (optionCount = 0; optionCount < options.length; optionCount++)
      {
         if (options[optionCount].equals("-E"))
            encoding = options[++optionCount];
         else if (options[optionCount].equals("-k"))
         {
            try
            { 
               keepAliveTimeout = Long.parseLong(options[++optionCount]);
            }
            catch (NumberFormatException nfe)
            {
               keepAliveTimeout = -1;
            }
         }
         else if (options[optionCount].equals("-p"))
            protocol = options[++optionCount];
         else if (options[optionCount].equals("-S"))
            serverType = options[++optionCount];
         else if (options[optionCount].equals("-T"))
            trustmgr = options[++optionCount];
         else
         {
            // Bad options.
            options = null;
            break;
         }
      }
   }
   
   //==============================================================
   // Class method to creat the FTP Client given the options given
   // in the constructor. Includes initial connect and login with
   // user and password. 
   //==============================================================
   
   public FTPClient createFTPClient()
   {
      // Method Instances
      RepositoryAccess repositoryAccess;
      FTPClient ftpClient;
      FTPClientConfig ftpConfig;
      URL downloadURL;
      
      boolean validClient;
      
      // Setup
      ftpClient = null;
      validClient = false;
      
      // Check
      if (!repositoryType.equals(PluginRepository.FTP)
          &&  !repositoryType.equals(PluginRepository.FTPS))
         return null;
      
      // Collect User & Password
      repositoryAccess = new RepositoryAccess(true);
      
      if (!repositoryAccess.promptUserPassword())
         return null;
      
      // FTP
      if (repositoryType.equals(PluginRepository.FTP))
         ftpClient = new FTPClient();
      
      // FTPS
      else
      {
         FTPSClient ftpsClient;
         
         // Set protocol explicitly.
         if (options != null && protocol != null)
         {
            if (protocol.equals("true"))
               ftpsClient = new FTPSClient(true);
            else if (protocol.equals("false"))
               ftpsClient = new FTPSClient(false);
            else
            {
               String prot[] = protocol.split(",");
               
               if (prot.length == 1)
                  ftpsClient = new FTPSClient(protocol);
               else
                  ftpsClient = new FTPSClient(prot[0], Boolean.parseBoolean(prot[1]));
            } 
         }
         else
            ftpsClient = new FTPSClient();
         
         // Set the trust manager.
         if (options != null && trustmgr != null)
         {
            if (trustmgr.equals("all"))
               ftpsClient.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
            else if (trustmgr.equals("valid"))
               ftpsClient.setTrustManager(TrustManagerUtils.getValidateServerCertificateTrustManager());
            else
               ftpsClient.setTrustManager(null);     
         }
         
         ftpClient = ftpsClient;
      }
      
      // Check for other options & setting.
      if (options != null)
      {
         if (keepAliveTimeout >= 0)
            ftpClient.setControlKeepAliveTimeout(keepAliveTimeout);
         
         if (encoding != null)
            ftpClient.setControlEncoding(encoding);
         
         if (serverType != null)
         {
            ftpConfig = new FTPClientConfig(serverType);
            ftpClient.configure(ftpConfig);
         }
      }
      
      try
      {  
         // FTPS prefix is not valid URL.
         if (repositoryType.equals(PluginRepository.FTPS))
            downloadURL = new URL(PluginRepository.FTP + remoteResourceURL.substring(4));
         else
            downloadURL = new URL(remoteResourceURL);
         
         // Connect
         if (downloadURL.getPort() != -1 && downloadURL.getPort() != ftpClient.getDefaultPort())
            ftpClient.connect(downloadURL.getHost(), downloadURL.getPort());
         else
            ftpClient.connect(downloadURL.getHost());
         
         // Check
         if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
         {
            displayErrors("FTP_Client createFTPClient() Refused Connection:\n"
                          + ftpClient.getReplyCode() + " : "
                          + ftpClient.getReplyString());
            return null;
         }
         
         // Login
         do
         {
            if (!ftpClient.login(repositoryAccess.getUser(), repositoryAccess.getPassword()))
            {
               displayErrors("FTP_Client createFTPClient() Login Error:\n"
                             + ftpClient.getReplyCode() + " : "
                             + ftpClient.getReplyString());
               
               if (!repositoryAccess.promptUserPassword())
               {
                  ftpClient.logout();
                  return null;
               }
            }
            else
               break;
         }
         while (!repositoryAccess.loginAttemptsExceeded());
         
         ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
         ftpClient.enterLocalPassiveMode();
         validClient = true;
         
      }
      catch (MalformedURLException e) 
      {
         displayErrors("FTP_Client createFTPClient() MalformedURLException:\n"
                       + e.toString());
         
      }
      catch (SocketException e)
      {
         displayErrors("FTP_Client createFTPClient() SocketException:\n"
                       + e.toString());
      }
      catch (IOException e)
      {
         displayErrors("FTP_Client createFTPClient() IOException:\n"
                       + e.toString());
      }
      finally
      {
         try
         {
            if (ftpClient != null && ftpClient.isConnected() && !validClient)
               ftpClient.disconnect();
         }
         catch (IOException ioe)
         {
            if (Ajqvue.getDebug())
               System.out.println("FTP_Client createFTPClient() "
                                  + "Failed to disconnet ftpClient. " + ioe.toString());
         }     
      }
      return ftpClient; 
   }
   
   //==============================================================
   // Class method to allow the displaying an error to the user
   // if something goes wrong with the loading of the plugin.
   //==============================================================

   private void displayErrors(String errorString)
   {
      if (errorString.length() > 300)
         errorString = errorString.substring(0, 300);

      String optionPaneStringErrors = "Error Loading Plugin\n" + errorString;

      JOptionPane.showMessageDialog(null, optionPaneStringErrors, "Alert", JOptionPane.ERROR_MESSAGE);
   }
}
