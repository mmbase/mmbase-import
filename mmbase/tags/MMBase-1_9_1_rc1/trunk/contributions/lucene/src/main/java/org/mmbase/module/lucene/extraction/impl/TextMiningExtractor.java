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
import org.mmbase.module.lucene.extraction.Extractor;
import org.mmbase.util.logging.*;
import org.textmining.extraction.TextExtractor;
import org.textmining.extraction.word.WordTextExtractorFactory;

/**
 * Use textmining lib to extract text from a Word document
 *
 * @author Wouter Heijke
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class TextMiningExtractor implements Extractor {
    private static final Logger log = Logging.getLoggerInstance(TextMiningExtractor.class);

    private String mimetype = "application/msword";

    public void setMimeType(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimeType() {
        return this.mimetype;
    }

    public String extract(InputStream input) throws Exception {
        log.debug("extract stream");
        WordTextExtractorFactory factory = new WordTextExtractorFactory();
        TextExtractor extractor = factory.textExtractor(input);
        return extractor.getText().trim();
    }
}
