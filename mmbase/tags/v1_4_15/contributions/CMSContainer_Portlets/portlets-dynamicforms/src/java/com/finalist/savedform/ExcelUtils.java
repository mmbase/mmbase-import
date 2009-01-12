package com.finalist.savedform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Utility class for generating xls files
 * 
 * @author Cati Macarov
 */
public class ExcelUtils {

   private static ExcelUtils instance = null;
   private static final int ROW_TITLE = 1;
   private static final Logger log = Logging.getLoggerInstance(ExcelUtils.class.getName());


   /**
    * Private constructor, prevents instantiation
    */
   private ExcelUtils() { /* prevents instantiation */
   }


   /**
    * Singleton access method
    * 
    * @return an instance of the ExcelUtils
    */
   public static synchronized ExcelUtils getInstance() {
      if (instance == null) {
         instance = new ExcelUtils();
      }
      return instance;
   }


   public void generate(String sheetTitle, OutputStream outputStream, Collection<String> headerValues, int linesCount,
         List<String> cellValues) {
      try {

         HSSFWorkbook wb = new HSSFWorkbook();
         HSSFSheet sheet = wb.createSheet();
         wb.setSheetName(0, sheetTitle, HSSFWorkbook.ENCODING_COMPRESSED_UNICODE);

         HSSFRow row = sheet.createRow(ROW_TITLE);
         HSSFCellStyle style = wb.createCellStyle();
         HSSFFont font = wb.createFont();
         font.setFontHeightInPoints((short) 10);
         font.setColor(HSSFFont.COLOR_NORMAL);
         font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
         style.setFont(font);

         int column = 0;
         for (String headerValue : headerValues) {
            setCellValueFormat(row, (short) column, headerValue.toUpperCase(), style);
            column++;
         }

         int columns = headerValues.size();
         int offset = ROW_TITLE + 2;
         for (int i = 0; i < linesCount; i++) {
            for (int j = 0; j < columns; j++) {
               String cellValue = cellValues.get(i * columns + j);
               setCellValueFormat(sheet, offset + i, (short) j, cellValue, null);
            }
         }
         wb.write(outputStream);
      }
      catch (IOException ie) {
         log.error("error generating xls file ", ie);
      }
   }


   private void setCellValueFormat(HSSFRow row, short cellIndex, String value, HSSFCellStyle style) {
      HSSFCell cell = row.createCell(cellIndex);
      cell.setCellValue(value);
      if (style != null) {
         cell.setCellStyle(style);
      }
   }


   private void setCellValueFormat(HSSFSheet sheet, int rowIndex, short cellIndex, String value, HSSFCellStyle style) {
      HSSFRow row = sheet.createRow(rowIndex);
      HSSFCell cell = row.createCell(cellIndex);
      cell.setCellValue(value);
      if (style != null) {
         cell.setCellStyle(style);
      }
   }

}
