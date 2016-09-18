//=================================================================
//                        ImageUtil
//=================================================================
//
//   This class provides a means to save a JComponent Object graphics
// as png image.    
//
//                   << ImageUtil.java >>
//
//=================================================================
// Copyright (C) 2016 Dana M. Proctor
// Version 1.0 08/17/2016
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
//This class provides a generic panel in the appearance of
// a form for selecting the CSV data export options.
//=================================================================
// Revision History
// Changes to the code should be documented here and reflected
// in the present version number. Author information should
// also be included with the original copyright author.
//=================================================================
// Version 1.0 2006 Original GNU ImageUtil Class, By Vivek Singh
//                  Arrah Technology http://www.arrah.in
//         1.1 09/17/2016 Integration in Ajqvue.
//                        
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.dandymadeproductions.ajqvue.Ajqvue;

/**
 *    The ImageUtil class provides a means to save a JComponent
 * Object graphics as png image.
 * 
 * @author Vivek Singh, Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class ImageUtil
{
   // Class Instances.
   private String lastSaveDirectory = "";
   private AResourceBundle resourceBundle;
   
   //==============================================================
   // ImageUtil Constructor
   //==============================================================

   public ImageUtil(Component component, String lastSaveDirectory, String imageType)
   {
      // Do some checking and setup.
      if (component == null || component.getWidth() == 0 || component.getHeight() == 0)
         return;
      
      if (lastSaveDirectory == null)
         this.lastSaveDirectory = "";
      else
         this.lastSaveDirectory = lastSaveDirectory;
      
      resourceBundle = Ajqvue.getResourceBundle();
      
      // Process.
      saveImage(component, imageType);
   }

   //==============================================================
   // ImageUtil Constructor
   //==============================================================

   private void saveImage(Component component, String itype)
   {
      // Method Instances
      int componentWidth, componentHeight, fileChooserResult;
      JFileChooser fileChooser;
      String resourceTitle, resourceMessage;
      File savedFile;

      Graphics2D g2;
      GraphicsEnvironment graphicsEnvironment;
      GraphicsDevice graphicsDevice;
      GraphicsConfiguration graphicsConfiguration;
      BufferedImage bufferedImage;
      
      // Check to see if any graphics to save.
      if (component == null)
         return;

      // Create a dialog for the user to save the image
      // file to a directory.

      if (lastSaveDirectory.equals(""))
         fileChooser = new JFileChooser();
      else
         fileChooser = new JFileChooser(new File(lastSaveDirectory));

      resourceTitle = resourceBundle.getResourceString("ImageUtil.dialogtitle.PNGImageSaveFile",
                                                       "Save PNG Image File");
      fileChooser.setDialogTitle(resourceTitle);
      fileChooser.setFileFilter(new PNG_FileFilter());

      fileChooserResult = fileChooser.showSaveDialog(null);

      // Proceed on selection and confirmation of saving the
      // file.

      if (fileChooserResult == JFileChooser.APPROVE_OPTION)
      {
         // Save the selected directory and file name so can be used again.
         lastSaveDirectory = fileChooser.getCurrentDirectory().toString();
         
         savedFile = fileChooser.getSelectedFile();

         // Check to see if file ends with .png, and
         // renaming if have to.
         if (!savedFile.getName().toLowerCase(Locale.ENGLISH).endsWith(".png"))
         {
            try
            {
               File renameFile = new File(savedFile.getAbsolutePath() + ".png");
               savedFile = renameFile;
            }
            catch (SecurityException se)
            {
               resourceMessage = resourceBundle.getResourceString("ImageUtil.dialogmessage.ImageSaveError",
                                                                  "Image Save Error");
                
               JOptionPane.showMessageDialog(null, se.getMessage(), resourceMessage,
                                                JOptionPane.ERROR_MESSAGE);
               return;
            }
         }

         // Confirm overwriting to existing file.
         if (savedFile.exists())
         {
            resourceMessage = resourceBundle.getResourceString(
               "ImageUtil.dialogmessage.OverwriteExistingFile", "Overwrite existing file?");
            resourceTitle = resourceBundle.getResourceString("ImageUtil.dialogtitle.ConfirmOverwrite",
                                                             "Confirm Overwite");
            
            int response = JOptionPane.showConfirmDialog(null, resourceMessage, resourceTitle,
                                                         JOptionPane.OK_CANCEL_OPTION,
                                                         JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.CANCEL_OPTION)
               return;
         }

         // Setting up and collecting components graphics and drawing
         // into a buffered image.

         componentWidth = component.getWidth();
         componentHeight = component.getHeight();
         
         // This maybe an alternative to the creation of a Graphics
         // Environment that may throw a heap memory error on createGraphics().
         /*
         bufferedImage = new BufferedImage(componentWidth, componentHeight, BufferedImage.TYPE_INT_RGB);
         g2 = bufferedImage.createGraphics();
         component.paint(g2);
         */

         graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
         graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
         graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
         bufferedImage = graphicsConfiguration.createCompatibleImage(componentWidth, componentHeight,
                                                                     Transparency.BITMASK);
         g2 = bufferedImage.createGraphics();
         component.paint(g2);

         // Attempt to save image.
         try
         {
            ImageIO.write(bufferedImage, "png", savedFile);
         }
         catch (Exception exp)
         {
            resourceMessage = resourceBundle.getResourceString("ImageUtil.dialogmessage.ImageSaveError",
                                                               "Image Save Error");
            
            JOptionPane.showMessageDialog(null, exp.getMessage(), resourceMessage,
                                          JOptionPane.ERROR_MESSAGE);
         }
         g2.dispose();
      }
      else
         return;
   }

   //==============================================================
   // ImageUtil PNG File Filter Inner Class
   //==============================================================

   public static class PNG_FileFilter extends FileFilter
   {
      public boolean accept(File file)
      {
         return file.getName().toLowerCase(Locale.ENGLISH).endsWith(".png")
                || file.isDirectory();
      }

      public String getDescription()
      {
         return "PNG image  (*.png) ";
      }
   }
   
   //==============================================================
   // Getter/Setter Methos for Class Instance lastSaveDirectory.
   //==============================================================
   
   public String getLastSaveDiretory()
   {
      return lastSaveDirectory;
   }
   
   public void setLastSaveDirectory(String lastSave)
   {
      if (lastSave != null)
         lastSaveDirectory = lastSave;
   }  
}
