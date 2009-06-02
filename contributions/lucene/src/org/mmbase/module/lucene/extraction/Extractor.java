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
package org.mmbase.module.lucene.extraction;

import java.io.InputStream;

/**
 * Content Extractor interface
 *
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
public interface Extractor {

    /**
     * Mimetype this Extractor handles
     *
     * @param mimetype String representing the MIME Type
     */
    void setMimeType(String mimetype);

    /**
     * Mimetype this Extractor handles
     *
     * @return String representing the MIME Type
     */
    String getMimeType();

    /**
     * Extract text from a source
     *
     * @param source InputStream where the data comes from
     * @return String representing the extracted text
     * @throws Exception
     */
    String extract(InputStream source) throws Exception;

}
