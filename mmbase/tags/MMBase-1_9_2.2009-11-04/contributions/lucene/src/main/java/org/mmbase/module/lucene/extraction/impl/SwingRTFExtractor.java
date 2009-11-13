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

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import org.mmbase.module.lucene.extraction.Extractor;
import org.mmbase.util.logging.*;

/**
 * RTF text extractor
 * 
 * @author Wouter Heijke
 * @version $Id$
 */
public class SwingRTFExtractor implements Extractor {
    private static final Logger log = Logging.getLoggerInstance(SwingRTFExtractor.class);

    private String mimetype = "application/rtf";

    public void setMimeType(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimeType() {
        return this.mimetype;
    }

    public String extract(InputStream input) throws Exception {
        log.debug("extract stream");
        String result = null;
        DefaultStyledDocument styledDoc = new DefaultStyledDocument();
        try {
            new RTFEditorKit().read(input, styledDoc, 0);
            result = styledDoc.getText(0, styledDoc.getLength());
        } catch (IOException e) {
            throw new Exception("Cannot extract text from a RTF document", e);
        } catch (BadLocationException e) {
            throw new Exception("Cannot extract text from a RTF document", e);
        }
        return result;
    }
}
