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

import java.io.*;
import org.mmbase.util.externalprocess.CommandLauncher;
import org.mmbase.module.lucene.extraction.Extractor;
import org.mmbase.util.logging.*;

/**
 * A very simple Extractor based on unix's 'strings'.
 * 
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class StringsExtractor implements Extractor {
    private static final Logger log = Logging.getLoggerInstance(StringsExtractor.class);

    private String mimetype = ".*";
    private String command = "/usr/bin/strings";

    public void setMimeType(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimeType() {
        return this.mimetype;
    }

    public String getCommand() {
        return command;
    }

    public String extract(InputStream inputStream) throws Exception {
        String encoding = System.getProperty("file.encoding");
        CommandLauncher launcher = new CommandLauncher("Transformer");
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        launcher.execute(getCommand());
        launcher.waitAndWrite(inputStream, outputStream, errorStream);        
        return new String(outputStream.toByteArray(), encoding) + new String(errorStream.toByteArray(), encoding);
    }
}
