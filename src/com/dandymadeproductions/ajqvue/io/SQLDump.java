//=================================================================
//                         SQLDump
//=================================================================
//   This class provides some common methods that are used by the
// Ajqvue IO SQL Data Dump Threads.
//
//                     << SQLDump.java >>
//
//=================================================================
// Copyright (C) 2017 Dana M. Proctor
// Version 1.0 06/29/2017
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
// Version 1.0 Production SQLDump Class.
//                         
//-----------------------------------------------------------------
//                    danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.ConnectionProperties;

public class SQLDump
{
   protected String fileName;
   protected BufferedOutputStream filebuff;
   
   //==============================================================
   // Class method for generating dump header info
   //==============================================================

   public String generateHeaders()
   {
      // Class Method Instances.
      ConnectionProperties connectionProperties;
      String hostName, databaseName;
      String dateTime, headers;
      SimpleDateFormat dateTimeFormat;
      
      // Create Header.
      
      connectionProperties = ConnectionManager.getConnectionProperties();
      hostName = connectionProperties.getProperty(ConnectionProperties.HOST);
      databaseName = connectionProperties.getProperty(ConnectionProperties.DB);

      dateTimeFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss z");
      dateTime = dateTimeFormat.format(new Date());

      headers = "--\n"
                + "-- Ajqvue SQL Dump\n"
                + "-- Version: " + Ajqvue.getVersion()[1] + "\n"
                + "-- WebSite: " + Ajqvue.getWebSite() + "\n"
                + "-- Host: " + hostName + "\n"
                + "-- Generated On: " + dateTime + "\n"
                + "-- SQL version: " + ConnectionManager.getDBProductName_And_Version() + "\n"
                + "-- Database: " + databaseName + "\n"
                + "--\n\n"
                + "-- ------------------------------------------\n";

      // System.out.println(headers);
      return headers;
   }

   //==============================================================
   // Class method for generating comment separator.
   //==============================================================

   public String genCommentSep(String str)
   {
      String res;
      res = "\n--\n";
      res += "-- " + str;
      res += "\n--\n\n";
      return res;
   }

   //==============================================================
   // Class method for escaping a string.
   //==============================================================

   public String addEscapes(String str)
   {
      if (str == null)
         return "";

      // For some reason the sequence ;\n is not
      // able to be properly pulled into either
      // the MySQL or PostgreSQL. So what else hack
      // it, add a space before newline. Could
      // find no reason for this in either manual
      // for characters that need escaping.

      str = str.replaceAll(";\\n", "; \\n");

      // Escape the single quote character which is
      // the character being used to deliminate the
      // content.
      StringBuffer s = new StringBuffer((String) str);
      for (int i = 0; i < s.length(); i++)
      {
         if (s.charAt(i) == '\'')
            s.insert(i++, '\'');
      }
      return s.toString();
   }

   //==============================================================
   // Class Method to dump a chunk of data to the output file.
   //==============================================================

   protected void dumpChunkOfData(Object dumpData)
   {
      // Class Method Instances
      byte[] currentBytes;
      System.out.println("dumping bytes");

      // Dump the Chunk.
      try
      {
         currentBytes = dumpData.toString().getBytes();
         filebuff.write(currentBytes);
         filebuff.flush();
      }
      catch (IOException e)
      {
         String msg = "Error outputing data to: '" + fileName + "'.";
         JOptionPane.showMessageDialog(null, msg, fileName, JOptionPane.ERROR_MESSAGE);
      }
   }
}