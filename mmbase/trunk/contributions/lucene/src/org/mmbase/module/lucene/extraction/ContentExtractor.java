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

import java.util.*;
import org.xml.sax.Attributes;

import org.mmbase.util.logging.*;
/**
 * Handles content extraction
 *
 * @author Wouter Heijke
 * @version $Revision: 1.3 $
 */
public class ContentExtractor {

    private static final Logger log = Logging.getLoggerInstance(ContentExtractor.class);

    private static ContentExtractor instance = null;

    private Map extractors = new HashMap();

    public ContentExtractor() {
    }

    public static ContentExtractor getInstance() {
        if (instance == null) {
            instance = new ContentExtractor();
        }
        return instance;
    }

    public Extractor findExtractor(String mimetype) {
        log.debug("Find extractor: '" + mimetype + "'");
        if (extractors.containsKey(mimetype)) {
            Extractor ext = (Extractor) extractors.get(mimetype);
            return ext;
        } else {
            // keep track on not found extractors
            extractors.put(mimetype, null);
            log.warn("Can't find extractor for mimetype: '" + mimetype + "'");
        }
        return null;
    }

    public void addExtractor(Extractor extractor, String mimetype) {
        if (mimetype == null) {
            mimetype = extractor.getMimeType();
        }
        if (extractors.containsKey(mimetype)) {
            log.service("Replacing Extractor for mimetype: '" + mimetype + "'");
            extractors.remove(mimetype);
        } else {
            log.service("Adding Extractor for mimetype: '" + mimetype + "'");
        }
        extractors.put(mimetype, extractor);
    }

    public void addExtractor(Class clazz, String mimetype) throws InstantiationException, IllegalAccessException {
        addExtractor((Extractor) clazz.newInstance(), mimetype);
    }

    public void addExtractor(String className, String mimetype) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        addExtractor(Class.forName(className), mimetype);
    }

    public void addExtractor(String className) {
        try {
            addExtractor(className, null);
        } catch (InstantiationException e) {
            log.error("Cannot start extractor " + e);
        } catch (IllegalAccessException e) {
            log.error("Cannot start extractor, illegal access " + e);
        } catch (ClassNotFoundException e) {
            log.error("Cannot start extractor, class not found " + e);
        } catch (Throwable e) {
            log.error("Cannot start extractor " + e);
        }
    }

    public Object createObject(Attributes attributes) throws Exception {
        String className = attributes.getValue("extractorClass");
        if (className != null) {
            String mimetype = attributes.getValue("mimetype");
            addExtractor(className, mimetype);
        }
        return null;
    }

}
