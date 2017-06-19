//=================================================================
//                     PDFDataTableDumpThread
//=================================================================
//
//    This class provides a thread to safely dump a TableTabPanel
// summary table data to a local pdf file.
//
//                << PDFDataTableDumpThread.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
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
// Version 1.0 Production PDFDataTableDumpThread Class.
//
//-----------------------------------------------------------------
//                    danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.io;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JTable;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.gui.panels.PDFExportPreferencesPanel;
import com.dandymadeproductions.ajqvue.structures.DataExportProperties;
import com.dandymadeproductions.ajqvue.utilities.ProgressBar;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The PDFDataTableDumpThread class provides a thread to safely
 * dump a TableTabPanel summary table data to a local pdf file.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/18/2016
 */

public class PDFDataTableDumpThread implements PdfPageEvent, Runnable
{
   // Class Instances
   private JTable summaryListTable;
   private HashMap<String, String> tableColumnTypeHashMap;
   private String exportedTable, fileName;
   private DataExportProperties pdfDataExportOptions;
   private PdfTemplate pdfTemplate;

   private Font titleFont, rowHeaderFont, tableDataFont;
   private BaseFont rowHeaderBaseFont;
   private static final Font FONT = new Font();
   private static final BaseFont BASE_FONT = FONT.getCalculatedBaseFont(false);

   //==============================================================
   // PDFDataDumpThread Constructor.
   //==============================================================

   public PDFDataTableDumpThread(JTable summaryListTable, HashMap<String, String> tableColumnTypeHashMap,
                                 String exportedTable, String fileName)
   {
      this.summaryListTable = summaryListTable;
      this.tableColumnTypeHashMap = tableColumnTypeHashMap;
      this.exportedTable = exportedTable;
      this.fileName = fileName;
   }

   //==============================================================
   // Class Method for Normal Start of the Thread.
   //==============================================================

   public void run()
   {
      // Class Method Instances
      String title;
      PdfPTable pdfTable;
      PdfPCell titleCell, rowHeaderCell, bodyCell;
      Document pdfDocument;
      PdfWriter pdfWriter;
      ByteArrayOutputStream byteArrayOutputStream;
      
      int columnCount, rowNumber;
      int[] columnWidths;
      int totalWidth;
      Rectangle pageSize;

      ProgressBar dumpProgressBar;
      HashMap<String, String> summaryListTableNameTypes;
      String currentTableFieldName;
      String currentType, currentString;

      // Setup
      columnCount = summaryListTable.getColumnCount();
      rowNumber = summaryListTable.getRowCount();
      columnWidths = new int[columnCount];

      pdfTable = new PdfPTable(columnCount);
      pdfTable.setWidthPercentage(100);
      pdfTable.getDefaultCell().setPaddingBottom(4);
      pdfTable.getDefaultCell().setBorderWidth(1);

      summaryListTableNameTypes = new HashMap<String, String>();
      pdfDataExportOptions = DBTablesPanel.getDataExportProperties();
      
      titleFont = new Font(pdfDataExportOptions.getFont());
      titleFont.setStyle(Font.BOLD);
      titleFont.setSize((float) pdfDataExportOptions.getTitleFontSize());
      titleFont.setColor(new BaseColor(pdfDataExportOptions.getTitleColor().getRGB()));
      
      rowHeaderFont = new Font(pdfDataExportOptions.getFont());
      rowHeaderFont.setStyle(Font.BOLD);
      rowHeaderFont.setSize((float) pdfDataExportOptions.getHeaderFontSize());
      rowHeaderFont.setColor(new BaseColor(pdfDataExportOptions.getHeaderColor().getRGB()));
      rowHeaderBaseFont = rowHeaderFont.getCalculatedBaseFont(false);
      
      tableDataFont = pdfDataExportOptions.getFont();

      // Constructing progress bar.
      dumpProgressBar = new ProgressBar(exportedTable + " Dump");
      dumpProgressBar.setTaskLength(rowNumber);
      dumpProgressBar.pack();
      dumpProgressBar.center();
      dumpProgressBar.setVisible(true);
      
      // Create a Title if Optioned.
      title = pdfDataExportOptions.getTitle();
      
      if (!title.equals(""))
      {
         if (title.equals("EXPORTED TABLE"))
            title = exportedTable;
         
         titleCell = new PdfPCell(new Phrase(title, titleFont));
         titleCell.setBorder(0);
         titleCell.setPadding(10);
         titleCell.setColspan(summaryListTable.getColumnCount());
         titleCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
         
         pdfTable.addCell(titleCell);
         pdfTable.setHeaderRows(2);
      }
      else
         pdfTable.setHeaderRows(1);
         
      // Create Row Header.
      for (int i = 0; i < columnCount; i++)
      {
         currentTableFieldName = summaryListTable.getColumnName(i);
         rowHeaderCell = new PdfPCell(new Phrase(currentTableFieldName, rowHeaderFont));
         rowHeaderCell.setBorderWidth(pdfDataExportOptions.getHeaderBorderSize());
         rowHeaderCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
         rowHeaderCell.setBorderColor(new BaseColor(pdfDataExportOptions.getHeaderBorderColor().getRGB()));
         pdfTable.addCell(rowHeaderCell);
         columnWidths[i] = Math.min(50000, Math.max(columnWidths[i],
                                    rowHeaderBaseFont.getWidth(currentTableFieldName + " ")));
         if (tableColumnTypeHashMap != null)
            summaryListTableNameTypes.put(Integer.toString(i),
                                          tableColumnTypeHashMap.get(currentTableFieldName));
         else
            summaryListTableNameTypes.put(Integer.toString(i), "String");
      }

      // Create the Body of Data.
      int i = 0;
      while ((i < rowNumber) && !dumpProgressBar.isCanceled())
      {
         dumpProgressBar.setCurrentValue(i);

         // Collecting rows of data & formatting date & timestamps
         // as needed according to the Export Properties.

         if (summaryListTable.getValueAt(i, 0) != null)
         {
            for (int j = 0; j < summaryListTable.getColumnCount(); j++)
            {
               currentString = summaryListTable.getValueAt(i, j) + "";
               currentString = currentString.replaceAll("\n", "");
               currentString = currentString.replaceAll("\r", "");
               currentType = summaryListTableNameTypes.get(Integer.toString(j));

               // Format Date & Timestamp Fields as Needed.
               
               if ((currentType != null)
                   && (currentType.equals("DATE") || currentType.equals("DATETIME")
                       || currentType.indexOf("TIMESTAMP") != -1))
               {
                  if (!currentString.toLowerCase(Locale.ENGLISH).equals("null"))
                  {
                     int firstSpace;
                     String time;
                        
                     // Dates fall through DateTime and Timestamps try
                     // to get the time separated before formatting
                     // the date.
                     
                     if (currentString.indexOf(" ") != -1)
                     {
                        firstSpace = currentString.indexOf(" ");
                        time = currentString.substring(firstSpace);
                        currentString = currentString.substring(0, firstSpace);
                     }
                     else
                        time = "";
                        
                     currentString = Utils.convertViewDateString_To_DBDateString(currentString,
                        DBTablesPanel.getGeneralDBProperties().getViewDateFormat());
                     currentString = Utils.convertDBDateString_To_ViewDateString(currentString,
                        pdfDataExportOptions.getPDFDateFormat()) + time;
                  }
               }
               bodyCell = new PdfPCell(new Phrase(currentString, tableDataFont));
               bodyCell.setPaddingBottom(4);
               
               if (currentType != null)
               {
                  // Set Numeric Fields Alignment.
                  if (currentType.indexOf("BIT") != -1 || currentType.indexOf("BOOL") != -1
                      || currentType.indexOf("NUM") != -1 || currentType.indexOf("INT") != -1
                      || currentType.indexOf("FLOAT") != -1 || currentType.indexOf("DOUBLE") != -1
                      || currentType.equals("REAL") || currentType.equals("DECIMAL")
                      || currentType.indexOf("COUNTER") != -1 || currentType.equals("BYTE")
                      || currentType.equals("CURRENCY"))
                  {
                     bodyCell.setHorizontalAlignment(pdfDataExportOptions.getNumberAlignment());
                     bodyCell.setPaddingRight(4);
                  }
                  // Set Date/Time Field Alignment.
                  if (currentType.indexOf("DATE") != -1 || currentType.indexOf("TIME") != -1
                      || currentType.indexOf("YEAR") != -1)
                     bodyCell.setHorizontalAlignment(pdfDataExportOptions.getDateAlignment());
               }
              
               pdfTable.addCell(bodyCell);
               columnWidths[j] = Math.min(50000, Math.max(columnWidths[j],
                                          BASE_FONT.getWidth(currentString + " ")));
            }
         }
         i++;
      }
      dumpProgressBar.dispose();
      
      // Check to see if any data was in the summary
      // table to even be saved.
      
      if (pdfTable.size() <= pdfTable.getHeaderRows())
         return;
      
      // Create a document of the PDF formatted data
      // to be saved to the given output file.
      
      try
      {
         // Sizing & Layout
         totalWidth = 0;
         for (int width : columnWidths)
            totalWidth += width;
         
         if (pdfDataExportOptions.getPageLayout() == PDFExportPreferencesPanel.LAYOUT_PORTRAIT)
            pageSize = PageSize.A4;
         else
         {
            pageSize = PageSize.A4.rotate();
            pageSize.setRight(pageSize.getRight() * Math.max(1f, totalWidth / 53000f));
            pageSize.setTop(pageSize.getTop() * Math.max(1f, totalWidth / 53000f));
         }
         
         pdfTable.setWidths(columnWidths);

         // Document
         pdfDocument = new Document(pageSize);
         byteArrayOutputStream = new ByteArrayOutputStream();
         pdfWriter = PdfWriter.getInstance(pdfDocument, byteArrayOutputStream);
         pdfDocument.open();
         pdfTemplate = pdfWriter.getDirectContent().createTemplate(100, 100);
         pdfTemplate.setBoundingBox(new com.itextpdf.text.Rectangle(-20, -20, 100, 100));
         pdfWriter.setPageEvent(this);
         pdfDocument.add(pdfTable);
         pdfDocument.close();

         // Outputting
         WriteDataFile.mainWriteDataString(fileName, byteArrayOutputStream.toByteArray(), false);

      }
      catch (DocumentException de)
      {
         if (Ajqvue.getDebug())
         {
            System.out.println("Failed to Create Document Needed to Output Data. \n"
                                + de.toString());
         }
      }
   }
   
   //==============================================================
   // class Methods to meet the requirements for the PdfPageEvent
   // requirements.
   //==============================================================
   
   public void onOpenDocument(PdfWriter pdfWriter, Document document){}
   
   public void onCloseDocument(PdfWriter writer, Document document)
   {
      if (pdfTemplate != null)
      {
         pdfTemplate.beginText();
         pdfTemplate.setFontAndSize(BASE_FONT, 12);
         pdfTemplate.showText("" + (writer.getPageNumber() - 1));
         pdfTemplate.endText();
      }
   }
   
   public void onStartPage(PdfWriter pdfWriter, Document document){}

   public void onEndPage(PdfWriter writer, Document document)
   {
      PdfContentByte cb = writer.getDirectContent();
      String text = "Page " + writer.getPageNumber() + " of ";
      float textSize = BASE_FONT.getWidthPoint(text, 12);
      float textBase = document.bottom() - 20;
      cb.beginText();
      cb.setFontAndSize(BASE_FONT, 12);
      float adjust = BASE_FONT.getWidthPoint("000", 12);
      cb.setTextMatrix(document.right() - textSize - adjust, textBase);
      cb.showText(text);
      cb.endText();
      if (pdfTemplate != null)
         cb.addTemplate(pdfTemplate, document.right() - adjust, textBase);
   }
   
   public void onParagraph(PdfWriter pdfWriter, Document document, float v){}

   public void onParagraphEnd(PdfWriter pdfWriter, Document document, float v){}

   public void onChapter(PdfWriter pdfWriter, Document document, float v, Paragraph paragraph){}

   public void onChapterEnd(PdfWriter pdfWriter, Document document, float v){}

   public void onSection(PdfWriter pdfWriter, Document document, float v, int i, Paragraph paragraph){}

   public void onSectionEnd(PdfWriter pdfWriter, Document document, float v){}

   public void onGenericTag(PdfWriter pdfWriter, Document document, com.itextpdf.text.Rectangle rectangle,
                            String string){}
}
