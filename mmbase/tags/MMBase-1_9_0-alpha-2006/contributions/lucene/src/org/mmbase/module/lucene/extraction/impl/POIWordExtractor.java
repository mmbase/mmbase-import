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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.mmbase.module.lucene.extraction.Extractor;

import org.mmbase.util.logging.*;
import org.apache.poi.hdf.extractor.WordDocument;

/**
 * Use POI to extract text from a MS Word document
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
public class POIWordExtractor implements Extractor {
    private static final Logger log = Logging.getLoggerInstance(POIWordExtractor.class);
    private String mimetype = "application/msword";

    public void setMimeType(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimeType() {
        return this.mimetype;
    }

    public String extract(InputStream input) {
        log.debug("extract stream");
        String result = null;
        try {
            WordDocument wd = new WordDocument(input);
            if (wd != null) {
                StringWriter writer = new StringWriter();
                if (writer != null) {
                    wd.writeAllText(new PrintWriter(writer));
                    writer.close();
                    result = writer.toString();
                    log.debug("result='" + result.length() + "'");
                } else {
                    log.debug("no writer");
                }
            } else {
                log.debug("no worddoc");
            }
        } catch (IOException e) {
            log.error("IOException " + e.getMessage(), e);
            // throw new Exception("POIWordExtractor, Cannot extract text from a
            // Word document: ");
        }

        return result;
    }
    public static void main(String [] args) throws Exception {
        Extractor e = new POIWordExtractor();
        java.io.FileInputStream file = new java.io.FileInputStream(args[0]);
        System.out.println(e.extract(file));
    }
}
