//=================================================================
//                    Write Data File Class
//=================================================================
//
//    This class allows data in a selected database table to
// be outputed to specified file. A progress bar may be called
// to allow the control of the data being dumped. The class
// also provides a generic means to output byte[] data to a file.
// 
//                  << WriteDataFile.java >>
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
// Version 1.0 09/18/2016 Production WriteDataFile Class.
//
//-----------------------------------------------------------------
//                  danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.ProgressBar;

/**
 *     The WriteDataFile class allows data in a selected database
 * table to be outputed to specified file. A progress bar may be
 * called to allow the control of the data being dumped. The class
 * also provides a generic means to output byte[] data to a file.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2018
 */

public class WriteDataFile
{
   // =================================================
   // Creation of the data types needed to perform the
   // Writing of an Input File.
   // =================================================

   FileOutputStream fileStream;
   BufferedOutputStream filebuff;
   ProgressBar dumpProgressBar;

   //==============================================================
   // Method for writing the file containing output data.
   //==============================================================

   private void writeDataFileText(String outputFileString, byte[] buf, boolean showDumpProgressBar)
   {
      fileStream = null;
      filebuff = null;
      
      try
      {
         // Setting up OutputStream
         
         fileStream = new FileOutputStream(outputFileString);
         filebuff = new BufferedOutputStream(fileStream);

         // Creating a dump dialog progress bar as needed
         // and writing the specified data to the ouput file.

         int i = 0;

         if (showDumpProgressBar)
         {
            dumpProgressBar = new ProgressBar("Writing to File: " + outputFileString);
            dumpProgressBar.setTaskLength(buf.length);
            dumpProgressBar.pack();
            dumpProgressBar.center();
            dumpProgressBar.setVisible(true);

            while ((i < buf.length) && !dumpProgressBar.isCanceled())
            {
               dumpProgressBar.setCurrentValue(i);
               filebuff.write(buf[i++]);
            }
            dumpProgressBar.dispose();
         }
         else
         {
            while (i < buf.length)
               filebuff.write(buf[i++]);
         }
         filebuff.flush();
         fileStream.flush();
      }
      catch (IOException e)
      {
         String ioExceptionString = e.toString();
         if (ioExceptionString.length() > 200)
            ioExceptionString = e.toString().substring(0, 200);

         String optionPaneStringErrors = "Error Writing File: " + outputFileString 
                                         + "\n" + "IOException: "
                                         + ioExceptionString;

         JOptionPane.showMessageDialog(null, optionPaneStringErrors, "Alert",
                                       JOptionPane.ERROR_MESSAGE);
      }
      finally
      {
         try
         {
            if (filebuff != null)
               filebuff.close();
         }
         catch (IOException ioe)
         {
            if (Ajqvue.getDebug())
               System.out.println("WriteDataFile writeDataFileText() \n"
                                  + "Failed to Close BufferedOutputStream. " + ioe.toString());
         }
         finally
         {
            try
            {
               if (fileStream != null)
                  fileStream.close();
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

   //==============================================================
   // Creation of the main WriteDataFile class that is called
   // by outside classes to perform the write file routine.
   //==============================================================

   public static void mainWriteDataString(String outputFileString, byte[] data, boolean showDumpProgressBar)
   {
      WriteDataFile w = new WriteDataFile();
      {
         w.writeDataFileText(outputFileString, data, showDumpProgressBar);
      }
   }
}
