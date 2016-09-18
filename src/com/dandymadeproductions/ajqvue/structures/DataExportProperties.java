//=================================================================
//              DataExportProperties Class
//=================================================================
//	This class provides the structure for the data export
// properties storage.
//
//           << DataExportProperties.java >>
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
// Version 1.0 Production SQLDataExportProperties Class.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.structures;

import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.gui.panels.CSVExportPreferencesPanel;
import com.dandymadeproductions.ajqvue.gui.panels.PDFExportPreferencesPanel;
import com.dandymadeproductions.ajqvue.gui.panels.SQLExportPreferencesPanel;
import com.dandymadeproductions.ajqvue.utilities.Utils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;

/**
 *    The DataExportProperties class provides the structure for the
 * data export properties storage.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class DataExportProperties
{
   // Class Instances.
   
   // SQL
   private boolean tableStructure;
   private boolean tableData;
   private String identifierQuoteString;
   private int insertPluralSize;
   private int replacePluralSize;
   private boolean insertLock;
   private boolean replaceLock;
   private boolean updateLock;
   private String insertExpression;
   private String replaceExpression;
   private boolean autoIncrement;
   private boolean timeStamp;
   private boolean insertType;
   private boolean replaceType;
   private boolean updateType;
   private String insertReplaceUpdate;
   private String insertTypeSetting;
   private String replaceTypeSetting;
   private String updateTypeSetting;
   private boolean sqlSummaryTableUseLimit;
   
   // CSV
   private boolean textInclusion;
   private int textCharsNumber;
   private String dataDelimiter;
   private String csvDateFormat;
   private boolean csvSummaryTableUseLimit;
   
   // PDF
   private String title;
   private int titleFontSize;
   private int titleColor;
   private int headerFontSize;
   private int headerColor;
   private int headerBorderSize;
   private int headerBorderColor;
   private int numberAlignment;
   private int dateAlignment;
   private String pdfDateFormat;
   private String fontName;
   private int pageLayout;
   
   private Preferences dataExportPreferences;
   private TreeMap<String, Font> fontTreeMap;
   
   // These all have to be unique.
   
   // SQL
   private static final String TABLESTRUCTURE = "TableStructure";
   private static final String TABLEDATA = "TableData";
   private static final String INSERTPLURALSIZE = "InsertPluralSize";
   private static final String REPLACEPLURALSIZE = "ReplacePluralSize";
   private static final String INSERTLOCK = "InsertLock";
   private static final String REPLACELOCK = "replaceLock";
   private static final String UPDATELOCK = "UpdateLock";
   private static final String INSERTEXPRESSION = "InsertExpression";
   private static final String REPLACEEXPRESSION = "replaceExpression";
   private static final String AUTOINCREMENT = "AutoIncrement";
   private static final String TIMESTAMP = "TimeStamp";
   private static final String INSERTTYPE = "InsertType";
   private static final String REPLACETYPE = "ReplaceType";
   private static final String UPDATETYPE = "UpdateType";
   private static final String INSERTREPLACEUPDATE = "InsertReplaceUpdate";
   private static final String INSERTTYPESETTING = "InsertTypeSetting";
   private static final String REPLACETYPESETTING = "ReplaceTypeSetting";
   private static final String UPDATETYPESETTING = "UpdateTypeSetting";
   private static final String SQLSUMMARYTABLEUSELIMIT = "SQLSummaryTableUseLimit";

   // CSV
   private static final String TEXTINCLUSION = "TextInclusion";
   private static final String TEXTCHARSNUMBER = "TextCharsNumber";
   private static final String DATADELIMITER = "ExportDataDelimiter";
   private static final String CSVDATEFORMAT = "ExportCSVDateFormat";
   private static final String CSVSUMMARYTABLEUSELIMIT = "CSVSummaryTableUseLimit";
   
   // PDF
   private static final String TITLE = "Title";
   private static final String TITLEFONTSIZE = "TitleFontSize";
   private static final String TITLECOLOR = "TitleColor";
   private static final String HEADERFONTSIZE = "HeaderFontSize";
   private static final String HEADERCOLOR = "HeaderColor";
   private static final String HEADERBORDERSIZE = "HeaderBorderSize";
   private static final String HEADERBORDERCOLOR = "HeaderBorderColor";
   private static final String NUMBERALIGNMENT = "NumberAlignment";
   private static final String DATEALIGNMENT = "DateAlignment";
   private static final String PDFDATEFORMAT = "ExportPDFDateFormat";
   private static final String FONTNAME = "FontName";
   private static final String PAGELAYOUT = "PageLayout";
   
   //==============================================================
   // DataExportProperties Constructor
   //==============================================================

   public DataExportProperties()
   {
      // Set Default State.
      
      // SQL
      tableStructure = SQLExportPreferencesPanel.DEFAULT_TABLE_STRUCTURE;
      tableData = SQLExportPreferencesPanel.DEFAULT_TABLE_DATA;
      identifierQuoteString = ConnectionManager.getIdentifierQuoteString();
      insertPluralSize = SQLExportPreferencesPanel.DEFAULT_PLURAL_SIZE;
      replacePluralSize = SQLExportPreferencesPanel.DEFAULT_PLURAL_SIZE;
      insertLock = SQLExportPreferencesPanel.DEFAULT_INSERT_LOCK;
      replaceLock = SQLExportPreferencesPanel.DEFAULT_REPLACE_LOCK;
      updateLock = SQLExportPreferencesPanel.DEFAULT_UPDATE_LOCK;
      insertExpression = SQLExportPreferencesPanel.EXPRESSION_SINGULAR;
      replaceExpression = SQLExportPreferencesPanel.EXPRESSION_SINGULAR;
      autoIncrement = SQLExportPreferencesPanel.DEFAULT_AUTO_INCREMENT;
      timeStamp = SQLExportPreferencesPanel.DEFAULT_TIMESTAMP;
      insertType = SQLExportPreferencesPanel.DEFAULT_INSERT_TYPE;
      replaceType = SQLExportPreferencesPanel.DEFAULT_REPLACE_TYPE;
      updateType = SQLExportPreferencesPanel.DEFAULT_UPDATE_TYPE;
      insertReplaceUpdate = SQLExportPreferencesPanel.TYPE_INSERT;
      insertTypeSetting = SQLExportPreferencesPanel.PRIORITY_LOW;
      replaceTypeSetting = SQLExportPreferencesPanel.PRIORITY_LOW;
      updateTypeSetting = SQLExportPreferencesPanel.PRIORITY_LOW;
      sqlSummaryTableUseLimit = SQLExportPreferencesPanel.DEFAULT_SUMMARY_TABLE_USE_LIMIT;

      // CSV
      textInclusion = CSVExportPreferencesPanel.DEFAULT_CHAR_INCLUSION;
      textCharsNumber = CSVExportPreferencesPanel.DEFAULT_CHARS_LENGTH;
      dataDelimiter = CSVExportPreferencesPanel.DEFAULT_DATA_DELIMITER;
      csvDateFormat = Utils.MMddyyyy_DASH;
      csvSummaryTableUseLimit = CSVExportPreferencesPanel.DEFAULT_SUMMARY_TABLE_USE_LIMIT;
      
      // PDF
      title = PDFExportPreferencesPanel.DEFAULT_TITLE;
      titleFontSize = PDFExportPreferencesPanel.DEFAULT_TITLE_FONT_SIZE;
      titleColor = PDFExportPreferencesPanel.DEFAULT_COLOR.getRGB();
      headerFontSize = PDFExportPreferencesPanel.DEFAULT_HEADER_FONT_SIZE;
      headerColor = PDFExportPreferencesPanel.DEFAULT_COLOR.getRGB();
      headerBorderSize = PDFExportPreferencesPanel.DEFAULT_BORDER_SIZE;
      headerBorderColor = PDFExportPreferencesPanel.DEFAULT_COLOR.getRGB();
      numberAlignment = PDFExportPreferencesPanel.ALIGNMENT_RIGHT;
      dateAlignment = PDFExportPreferencesPanel.ALIGNMENT_CENTER;
      pdfDateFormat = Utils.MMddyyyy_DASH;
      fontName = PDFExportPreferencesPanel.DEFAULT_FONT;
      pageLayout = PDFExportPreferencesPanel.LAYOUT_PORTRAIT;
      
      // Try to retrieve state from Preferences.
      try
      {
         dataExportPreferences = Preferences.userNodeForPackage(DataExportProperties.class);
      }
      catch (SecurityException se){return;}
      
      try
      {
         // SQL
         tableStructure = dataExportPreferences.getBoolean(TABLESTRUCTURE, tableStructure);
         tableData = dataExportPreferences.getBoolean(TABLEDATA, tableData);
         insertPluralSize = dataExportPreferences.getInt(INSERTPLURALSIZE, insertPluralSize);
         replacePluralSize = dataExportPreferences.getInt(REPLACEPLURALSIZE, replacePluralSize);
         insertLock = dataExportPreferences.getBoolean(INSERTLOCK, insertLock);
         replaceLock = dataExportPreferences.getBoolean(REPLACELOCK, replaceLock);
         updateLock = dataExportPreferences.getBoolean(UPDATELOCK, updateLock);
         insertExpression = dataExportPreferences.get(INSERTEXPRESSION, insertExpression);
         replaceExpression = dataExportPreferences.get(REPLACEEXPRESSION, replaceExpression);
         autoIncrement = dataExportPreferences.getBoolean(AUTOINCREMENT, autoIncrement);
         timeStamp = dataExportPreferences.getBoolean(TIMESTAMP, timeStamp);
         insertType = dataExportPreferences.getBoolean(INSERTTYPE, insertType);
         replaceType = dataExportPreferences.getBoolean(REPLACETYPE, replaceType);
         updateType = dataExportPreferences.getBoolean(UPDATETYPE, updateType);
         insertReplaceUpdate = dataExportPreferences.get(INSERTREPLACEUPDATE, insertReplaceUpdate);
         insertTypeSetting = dataExportPreferences.get(INSERTTYPESETTING, insertTypeSetting);
         replaceTypeSetting = dataExportPreferences.get(REPLACETYPESETTING, replaceTypeSetting);
         updateTypeSetting = dataExportPreferences.get(UPDATETYPESETTING, updateTypeSetting);
         sqlSummaryTableUseLimit = dataExportPreferences.getBoolean(SQLSUMMARYTABLEUSELIMIT,
                                                                    sqlSummaryTableUseLimit);
         
         // CSV
         textInclusion = dataExportPreferences.getBoolean(TEXTINCLUSION, textInclusion);
         textCharsNumber = dataExportPreferences.getInt(TEXTCHARSNUMBER, textCharsNumber);
         dataDelimiter = dataExportPreferences.get(DATADELIMITER, dataDelimiter);
         csvDateFormat = dataExportPreferences.get(CSVDATEFORMAT, csvDateFormat);
         csvSummaryTableUseLimit = dataExportPreferences.getBoolean(CSVSUMMARYTABLEUSELIMIT,
                                                                    csvSummaryTableUseLimit);
         
         // PDF
         title = dataExportPreferences.get(TITLE, title);
         titleFontSize = dataExportPreferences.getInt(TITLEFONTSIZE, titleFontSize);
         titleColor = dataExportPreferences.getInt(TITLECOLOR, titleColor);
         headerFontSize = dataExportPreferences.getInt(HEADERFONTSIZE, headerFontSize);
         headerColor = dataExportPreferences.getInt(HEADERCOLOR, headerColor);
         headerBorderSize = dataExportPreferences.getInt(HEADERBORDERSIZE, headerBorderSize);
         headerBorderColor = dataExportPreferences.getInt(HEADERBORDERCOLOR, headerBorderColor);
         numberAlignment = dataExportPreferences.getInt(NUMBERALIGNMENT, numberAlignment);
         dateAlignment = dataExportPreferences.getInt(DATEALIGNMENT, dateAlignment);
         pdfDateFormat = dataExportPreferences.get(PDFDATEFORMAT, pdfDateFormat);
         fontName = dataExportPreferences.get(FONTNAME, fontName);
         pageLayout = dataExportPreferences.getInt(PAGELAYOUT, pageLayout);
      }
      catch (NullPointerException npe){}
      catch (IllegalStateException ise){}
      
      // Load PDF Export Fonts.
      fontTreeMap = new TreeMap <String, Font>();
      
      Thread fontLoadingThread = new Thread(new Runnable()
      {
         public void run()
         {
            loadPDFFonts();
         }
      }, "DataExportProperties.fontLoadingThread");
      fontLoadingThread.start();
   }
   
   //========================================================
   // Class method to load iText PDF Fonts.
   //========================================================
   
   private void loadPDFFonts()
   {
      // Method Instances.
      String fileSeparator;
      String fontPath, fontName;
      String fontsDirectoryName;
      Iterator<String> fontNameIterator;
      File fontsDirectory;
      File[] fontsSubDirectories;
      String[] fontNames;
      TreeSet<String> fonts;
      
      // Setting up.
      fileSeparator = Utils.getFileSeparator();
      fontsDirectoryName = "fonts" + fileSeparator;
      fonts = new TreeSet<String> ();
      
      // Insure have one default font.
      
      fontTreeMap.put(PDFExportPreferencesPanel.DEFAULT_FONT, new Font(Font.FontFamily.UNDEFINED));
      
      // Create the default registered fonts.
      
      fontNameIterator = FontFactory.getRegisteredFonts().iterator();
      
      while (fontNameIterator.hasNext())
      {
         fontName = fontNameIterator.next();
         fontTreeMap.put(fontName, FontFactory.getFont(fontName));
      }
      
      // Create embedded fonts from fonts directory.
      
      fontsDirectory = new File(fontsDirectoryName);
      
      if (fontsDirectory.exists())
      {
         // Handle one level of sub-directories.
         fontsSubDirectories = fontsDirectory.listFiles(new DirectoriesFilter());
         
         if (fontsSubDirectories != null)
         {
            for (int i = 0; i < fontsSubDirectories.length; i++)
            {
               fontNames = fontsSubDirectories[i].list(new FontNameFilter());
               
               if (fontNames != null)
                  for (int j = 0; j < fontNames.length; j++)
                     fonts.add(fontsSubDirectories[i] + fileSeparator + fontNames[j]);
            }
         }
         
         // Handle all direct font names in the directory.
         fontNames = fontsDirectory.list(new FontNameFilter());
         
         if (fontNames != null)
         {
            for (int i = 0; i < fontNames.length; i++)
               fonts.add(fontsDirectoryName + fontNames[i]);
         }
         
         // Load the found fonts.
         fontNameIterator = fonts.iterator();
         
         while (fontNameIterator.hasNext())
         {
            fontPath = fontNameIterator.next();
            if (fontPath.indexOf(fileSeparator) != -1)
               fontName = fontPath.substring((fontPath.lastIndexOf(fileSeparator) + 1), fontPath.indexOf("."));
            else
               fontName = fontPath.substring(0, fontPath.indexOf("."));
            
            // System.out.println(fontName + " " + fontPath);
            try
            {
               BaseFont currentBaseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
               fontTreeMap.put(fontName, new Font(currentBaseFont, 12));
            }
            catch(DocumentException de){}
            catch(IOException ioe){}
         }  
      }
   }
   
   //==============================================================
   // Inner Class for a File filter to be used to collect the
   // desired directories in the fonts directory.
   //==============================================================
   
   static class DirectoriesFilter implements FileFilter
   {
      @Override
      public boolean accept(File fileName)
      {  
         if (fileName.isDirectory())
            return true;
         else
            return false;
      } 
   }
   
   //==============================================================
   // Innter class for a File Name filter to be used to collect
   // the desired font files, ttf/otf, in the fonts directory.
   //==============================================================

   static class FontNameFilter implements FilenameFilter
   {
      public boolean accept(File dir, String fileName)
      {  
         return fileName.toLowerCase(Locale.ENGLISH).endsWith(".ttf")
               || fileName.toLowerCase(Locale.ENGLISH).endsWith("otf");
      }
   }
   
   //==============================================================
   // Class methods to allow classes to get the data export
   // object components.
   //==============================================================

   //===========
   // SQL
   
   public boolean getTableStructure()
   {
      return tableStructure;
   }

   public boolean getTableData()
   {
      return tableData;
   }
   
   public String getIdentifierQuoteString()
   {
      return identifierQuoteString;
   }
   
   public int getInsertPluralSize()
   {
      return insertPluralSize;
   }
   
   public int getReplacePluralSize()
   {
      return replacePluralSize;
   }

   public boolean getInsertLock()
   {
      return insertLock;
   }

   public boolean getReplaceLock()
   {
      return replaceLock;
   }

   public boolean getUpdateLock()
   {
      return updateLock;
   }

   public String getInsertExpression()
   {
      return insertExpression;
   }

   public String getReplaceExpression()
   {
      return replaceExpression;
   }

   public boolean getAutoIncrement()
   {
      if (insertReplaceUpdate.equals(SQLExportPreferencesPanel.TYPE_INSERT))
         return autoIncrement;
      else
         return false;
   }

   public boolean getTimeStamp()
   {
      if (insertReplaceUpdate.equals(SQLExportPreferencesPanel.TYPE_INSERT))
         return timeStamp;
      else
         return false;
   }

   public boolean getInsertType()
   {
      return insertType;
   }

   public boolean getReplaceType()
   {
      return replaceType;
   }

   public boolean getUpdateType()
   {
      return updateType;
   }

   public String getInsertReplaceUpdate()
   {
      return insertReplaceUpdate;
   }

   public String getInsertTypeSetting()
   {
      return insertTypeSetting;
   }

   public String getReplaceTypeSetting()
   {
      return replaceTypeSetting;
   }

   public String getUpdateTypeSetting()
   {
      return updateTypeSetting;
   }

   public boolean getLock()
   {
      if (insertReplaceUpdate.equals(SQLExportPreferencesPanel.TYPE_INSERT))
         return insertLock;
      else if (insertReplaceUpdate.equals(SQLExportPreferencesPanel.TYPE_REPLACE))
         return replaceLock;
      else
         return updateLock;
   }

   public String getType()
   {
      if (tableStructure)
      {
         if (insertType)
            return " " + insertTypeSetting + " ";
         else
            return " ";
      }
      else
      {
         if (insertReplaceUpdate.equals(SQLExportPreferencesPanel.TYPE_INSERT))
         {
            if (insertType)
               return " " + insertTypeSetting + " ";
            else
               return " ";
         }
         else if (insertReplaceUpdate.equals(SQLExportPreferencesPanel.TYPE_REPLACE))
         {
            if (replaceType)
               return " " + replaceTypeSetting + " ";
            else
               return " ";
         }
         else if (insertReplaceUpdate.equals(SQLExportPreferencesPanel.TYPE_UPDATE))
         {
            if (updateType)
               return " " + updateTypeSetting + " ";
            else
               return " ";
         }
         else
            return " ";
      }
   }

   public boolean getSQLSummaryTableUseLimit()
   {
      return sqlSummaryTableUseLimit;
   }
   
   //==========
   // CSV

   public boolean getTextInclusion()
   {
      return textInclusion;
   }

   public int getTextCharsNumber()
   {
      return textCharsNumber;
   }

   public String getDataDelimiter()
   {
      return dataDelimiter;
   }
   
   public String getCSVDateFormat()
   {
      return csvDateFormat;
   }
   
   public boolean getCSVSummaryTableUseLimit()
   {
      return csvSummaryTableUseLimit;
   }
   
   //==========
   // PDF
   
   public String getTitle()
   {
      return title;
   }

   public int getTitleFontSize()
   {
      return titleFontSize;
   }

   public Color getTitleColor()
   {
      return new Color(titleColor);
   }
   
   public int getHeaderFontSize()
   {
      return headerFontSize;
   }
   
   public Color getHeaderColor()
   {
      return new Color(headerColor);
   }

   public int getHeaderBorderSize()
   {
      return headerBorderSize;
   }

   public Color getHeaderBorderColor()
   {
      return new Color(headerBorderColor);
   }
   
   public int getNumberAlignment()
   {
      return numberAlignment;
   }
   
   public String getPDFDateFormat()
   {
      return pdfDateFormat;
   }
   
   public int getDateAlignment()
   {
      return dateAlignment;
   }
   
   public Font getFont()
   {
      if (fontTreeMap.containsKey(fontName))
         return fontTreeMap.get(fontName);
      else
         return fontTreeMap.get(PDFExportPreferencesPanel.DEFAULT_FONT);
   }
   
   public Object[] getFonts()
   {
      return fontTreeMap.keySet().toArray();
   }
   
   public String getFontName()
   {
      return fontName;
   }
   
   public int getPageLayout()
   {
      return pageLayout;
   }
   
   //==============================================================
   // Class methods to allow classes to set the data export
   // object components.
   //==============================================================

   //===========
   // SQL
   
   public void setTableStructure(boolean value)
   {
      tableStructure = value;
      savePreference(TABLESTRUCTURE, value);
   }

   public void setTableData(boolean value)
   {
      tableData = value;
      savePreference(TABLEDATA, value);
   }
   
   public void setIdentifierQuoteString(String content)
   {
      identifierQuoteString = content;
   }
   
   public void setInsertPluralSize(int value)
   {
      insertPluralSize = value;
      savePreference(INSERTPLURALSIZE, value);
   }
   
   public void setReplacePluralSize(int value)
   {
      replacePluralSize = value;
      savePreference(REPLACEPLURALSIZE, value);
   }

   public void setInsertLock(boolean value)
   {
      insertLock = value;
      savePreference(INSERTLOCK, value);
   }

   public void setReplaceLock(boolean value)
   {
      replaceLock = value;
      savePreference(REPLACELOCK, value);
   }

   public void setUpdateLock(boolean value)
   {
      updateLock = value;
      savePreference(UPDATELOCK, value);
   }

   public void setInsertExpression(String content)
   {
      insertExpression = content;
      savePreference(INSERTEXPRESSION, content);
   }

   public void setReplaceExpression(String content)
   {
      replaceExpression = content;
      savePreference(REPLACEEXPRESSION, content);
   }

   public void setAutoIncrement(boolean value)
   {
      autoIncrement = value;
      savePreference(AUTOINCREMENT, value);
   }

   public void setTimeStamp(boolean value)
   {
      timeStamp = value;
      savePreference(TIMESTAMP, value);
   }

   public void setInsertType(boolean value)
   {
      insertType = value;
      savePreference(INSERTTYPE, value);
   }

   public void setReplaceType(boolean value)
   {
      replaceType = value;
      savePreference(REPLACETYPE, value);
   }

   public void setUpdateType(boolean value)
   {
      updateType = value;
      savePreference(UPDATETYPE, value);
   }

   public void setInsertReplaceUpdate(String content)
   {
      insertReplaceUpdate = content;
      savePreference(INSERTREPLACEUPDATE, content);
   }

   public void setInsertTypeSetting(String content)
   {
      insertTypeSetting = content;
      savePreference(INSERTTYPESETTING, content);
   }

   public void setReplaceTypeSetting(String content)
   {
      replaceTypeSetting = content;
      savePreference(REPLACETYPESETTING, content);
   }

   public void setUpdateTypeSetting(String content)
   {
      updateTypeSetting = content;
      savePreference(UPDATETYPESETTING, content);
   }

   public void setSQLSummaryTableUseLimit(boolean value)
   {
      sqlSummaryTableUseLimit = value;
      savePreference(SQLSUMMARYTABLEUSELIMIT, value);
   }
   
   //===========
   // CSV

   public void setTextInclusion(boolean value)
   {
      textInclusion = value;
      savePreference(TEXTINCLUSION, value);
   }

   public void setTextCharsNumber(int value)
   {
      textCharsNumber = value;
      savePreference(TEXTCHARSNUMBER, value);
   }

   public void setDataDelimiter(String content)
   {
      dataDelimiter = content;
      savePreference(DATADELIMITER, content);
   }
   
   public void setCSVDateFormat(String content)
   {
      csvDateFormat = content;
      savePreference(CSVDATEFORMAT, content);
   }
   
   public void setCSVSummaryTableUseLimit(boolean value)
   {
      csvSummaryTableUseLimit = value;
      savePreference(CSVSUMMARYTABLEUSELIMIT, value);
   }
   
   //===========
   // PDF
   
   public void setTitle(String content)
   {
      title = content;
      savePreference(TITLE, content);
   }

   public void setTitleFontSize(int value)
   {
      titleFontSize = value;
      savePreference(TITLEFONTSIZE, value);
   }

   public void setTitleColor(Color color)
   {
      titleColor = color.getRGB();
      savePreference(TITLECOLOR, color.getRGB());
   }
   
   public void setHeaderFontSize(int value)
   {
      headerFontSize = value;
      savePreference(HEADERFONTSIZE, value);
   }
   
   public void setHeaderColor(Color color)
   {
      headerColor = color.getRGB();
      savePreference(HEADERCOLOR, color.getRGB());
   }

   public void setHeaderBorderSize(int value)
   {
      headerBorderSize = value;
      savePreference(HEADERBORDERSIZE, value);
   }

   public void setHeaderBorderColor(Color color)
   {
      headerBorderColor = color.getRGB();
      savePreference(HEADERBORDERCOLOR, color.getRGB());
   }
   
   public void setNumberAlignment(int value)
   {
      numberAlignment = value;
      savePreference(NUMBERALIGNMENT, value);
   }
   
   public void setPDFDateFormat(String content)
   {
      pdfDateFormat = content;
      savePreference(PDFDATEFORMAT, content);
   }
   
   public void setDateAlignment(int value)
   {
      dateAlignment = value;
      savePreference(DATEALIGNMENT, value);
   }
   
   public void setFontName(String content)
   {
      fontName = content;
      savePreference(FONTNAME, content);
   }
   
   public void setPageLayout(int value)
   {
      pageLayout = value;
      savePreference(PAGELAYOUT, value);
   }
   
   //==============================================================
   // Class methods to try and save the preferences state. 
   //==============================================================

   private void savePreference(String key, boolean value)
   {
      try
      {
         if (dataExportPreferences != null)
            dataExportPreferences.putBoolean(key, value);
      }
      catch (IllegalArgumentException iae){}
      catch (IllegalStateException ise){}
   }
   
   private void savePreference(String key, String content)
   {
      try
      {
         if (dataExportPreferences != null)
            dataExportPreferences.put(key, content);
      }
      catch (IllegalArgumentException iae){}
      catch (IllegalStateException ise){}
   }
   
   private void savePreference(String key, int value)
   {
      try
      {
         if (dataExportPreferences != null)
            dataExportPreferences.putInt(key, value);
      }
      catch (IllegalArgumentException iae){}
      catch (IllegalStateException ise){}
   }
   
   //==============================================================
   // Class method to properly implement the toString() method
   // for the object. Local method overides.
   //==============================================================

   public String toString()
   {
      StringBuffer parameters = new StringBuffer("[DataExportProperties: ");
      
      // SQL
      
      parameters.append("[tableStructure = " + tableStructure + "]");
      parameters.append("[tableData = " + tableData + "]");
      parameters.append("[identifierQuoteString = " + identifierQuoteString + "]");
      parameters.append("[insertPluralSize " + insertPluralSize + "]");
      parameters.append("[replacePluralSize " + replacePluralSize + "]");
      parameters.append("[insertLock = " + insertLock + "]");
      parameters.append("[replaceLock = " + replaceLock + "]");
      parameters.append("[updateLock = " + updateLock + "]");
      parameters.append("[insertExplicit = " + insertExpression + "]");
      parameters.append("[replaceExplicit = " + replaceExpression + "]");
      parameters.append("[autoIncrement = " + autoIncrement + "]");
      parameters.append("[timeStamp = " + timeStamp + "]");
      parameters.append("[insertType = " + insertType + "]");
      parameters.append("[replaceType = " + replaceType + "]");
      parameters.append("[updateType = " + updateType + "]");
      parameters.append("[insertReplaceUpdate = " + insertReplaceUpdate + "]");
      parameters.append("[insertTypeSetting = " + insertTypeSetting + "]");
      parameters.append("[replaceTypeSetting = " + replaceTypeSetting + "]");
      parameters.append("[updateTypeSetting = " + updateTypeSetting + "]");
      parameters.append("[sqlSummaryTableUseLimit = " + sqlSummaryTableUseLimit + "]");
      
      // CSV
      
      parameters.append("[textInclusion = " + textInclusion + "]");
      parameters.append("[textCharsNumber = " + textCharsNumber + "]");
      parameters.append("[dataDelimiter = " + dataDelimiter + "]");
      parameters.append("[csvDataFormat = " + csvDateFormat + "]");
      parameters.append("[csvSummaryTableUseLimit = " + csvSummaryTableUseLimit + "]");
      
      // PDF
      
      parameters.append("[title = " + title + "]");
      parameters.append("[titleFontSize = " + titleFontSize + "]");
      parameters.append("[titleColor = " + titleColor + "]");
      parameters.append("[headerFontSize = " + headerFontSize + "]");
      parameters.append("[headerColor = " + headerColor + "]");
      parameters.append("[headerBorderSize = " + headerBorderSize + "]");
      parameters.append("[headerBorderColor = " + headerBorderColor + "]");
      parameters.append("[numberAlignment = " + numberAlignment + "]");
      parameters.append("[pdfDateFormat = " + pdfDateFormat + "]");
      parameters.append("[dateAlignment = " + dateAlignment + "]");
      parameters.append("[fontName = " + fontName + "]");
      parameters.append("[pageLayout = " + pageLayout + "]");
      
      return parameters.toString();
   }
}
