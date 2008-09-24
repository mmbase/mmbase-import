/*
 * MMBase Lucene module
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package org.mmbase.module.lucene.extraction.impl;

import java.io.InputStream;
import java.util.Iterator;
import org.mmbase.module.lucene.extraction.Extractor;
import org.mmbase.util.logging.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Use POI to extract text from a MS Excel document
 *
 * @author Wouter Heijke
 * @version $Revision: 1.3 $
 */
public class POIExcelExtractor implements Extractor {
    private static final Logger log = Logging.getLoggerInstance(POIExcelExtractor.class);


    private String mimetype = "application/vnd.ms-excel";

    public void setMimeType(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimeType() {
        return this.mimetype;
    }

    public String extract(InputStream input) throws Exception {
        log.debug("extract stream");
        String result = null;
        try {
            StringBuilder buf = new StringBuilder();
            HSSFWorkbook workbook = new HSSFWorkbook(input);
            int numSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numSheets; i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);
                Iterator rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    HSSFRow row = (HSSFRow) rowIterator.next();
                    Iterator cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        HSSFCell cell = (HSSFCell) cellIterator.next();
                        String cellValue = null;
                        if (cell.getCellType() == 4) {
                            boolean booleanValue = cell.getBooleanCellValue();
                            cellValue = Boolean.toString(booleanValue);
                        } else if (cell.getCellType() == 0) {
                            double doubleValue = cell.getNumericCellValue();
                            cellValue = Double.toString(doubleValue);
                        } else if (cell.getCellType() == 1) {
                            cellValue = cell.getRichStringCellValue().getString();
                        }
                        if (cellValue != null) {
                            buf.append(cellValue);
                            buf.append("\t");
                        }
                    }
                    buf.append("\n");
                }
            }
            result = buf.toString();

        } catch (Exception e) {
            throw new Exception("Cannot extract text from a Excel document", e);
        }
        return result;
    }
}
