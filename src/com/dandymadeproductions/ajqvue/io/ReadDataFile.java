//=================================================================
//                    Read Data File Class
//=================================================================
//
//    This class allows the application the means to import data
// that can be placed in the database table in an automated way.
// The class is also used to import data images files. The class
// provides a generic framework to read bytes of data from a given
// input file.
// 
//                   << ReadDataFile.java >>
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
// Version 1.0 09/18/2016 Production ReadDataFile Class.
//
//-----------------------------------------------------------------
//                danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.ProgressBar;

/**
 *    The ReadDataFile class allows the application the means to
 * import data that can be placed in the database table in an
 * automated way. The class is also used to import data images
 * files. The class provides a generic framework to read bytes of
 * data from a given input file.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class ReadDataFile
{
   // =================================================
   // Creation of the data types needed to perform the
   // Reading of an Input File.
   // =================================================

   FileInputStream fileStream;
   BufferedInputStream filebuff;
   ProgressBar dumpProgressBar;
   int inSize;
   byte[] inBytes;
   boolean validRead;

   //==============================================================
   // Method for reading the file containing input data.
   //==============================================================

   private byte[] readInputFileText(String inputFileString, boolean showDumpProgressBar)
   {
      fileStream = null;
      filebuff = null;
      
      try
      {
         // Setting up InputStreams

         fileStream = new FileInputStream(inputFileString);
         filebuff = new BufferedInputStream(fileStream);
         inSize = filebuff.available();
         inBytes = new byte[inSize];
         validRead = true;

         // Creating a dump dialog progress bar as needed;
         // reading the data from the specified input
         // file and placing input the byte array.

         int i = 0;

         if (showDumpProgressBar)
         {
            dumpProgressBar = new ProgressBar("Reading File: " + inputFileString);
            dumpProgressBar.setTaskLength(inSize);
            dumpProgressBar.pack();
            dumpProgressBar.center();
            dumpProgressBar.setVisible(true);

            while (i < inSize)
            {
               // Checking to see if user wishes to
               // quit operation.
               if (dumpProgressBar.isCanceled())
               {
                  validRead = false;
                  i = inSize;
               }
               else
               {
                  dumpProgressBar.setCurrentValue(i);
                  inBytes[i++] = (byte) filebuff.read();
               }
            }
            dumpProgressBar.dispose();
         }
         else
         {
            while (i < inSize)
               inBytes[i++] = (byte) filebuff.read();
         }

         // Check to see if canceled.
         if (validRead)
            return inBytes;
         else
            return null;
      }
      catch (IOException e)
      {
         String ioExceptionString = e.toString();
         if (ioExceptionString.length() > 200)
            ioExceptionString = e.getMessage().substring(0, 200);

         String optionPaneStringErrors = "Error Reading File: " + inputFileString + "\n" + "IOException: "
                                         + ioExceptionString;

         JOptionPane.showMessageDialog(null, optionPaneStringErrors, "Alert", JOptionPane.ERROR_MESSAGE);
         return inBytes = null;
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
               System.out.println("ReadDataFile readInputFileText() \n"
                                  + "Failed to Close BufferedInputStream. " + ioe.toString());
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
                  System.out.println("ReadDataFile readInputFileText() \n"
                                     + "Failed to Close FileInputStream. " + ioe.toString());
            }     
         }
      }
   }

   //==============================================================
   // Creation of the main ReadDataFile class that is called by
   // outside classes to perform the reading of data from a file.
   //==============================================================

   public static byte[] mainReadDataString(String inputFileString, boolean showDumpProgressBar)
   {
      ReadDataFile r = new ReadDataFile();
      {
         return r.readInputFileText(inputFileString, showDumpProgressBar);
      }
   }
}
