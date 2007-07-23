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
import java.util.regex.*;
import org.xml.sax.Attributes;

import org.mmbase.util.logging.*;
/**
 * Handles content extraction
 *
 * @author Wouter Heijke
 * @version $Revision: 1.5 $
 */
public class ContentExtractor {

    private static final Logger log = Logging.getLoggerInstance(ContentExtractor.class);

    private static ContentExtractor instance = null;

    private final List<Map.Entry<Pattern, Extractor>> extractors = new ArrayList<Map.Entry<Pattern, Extractor>>();
    private final Map<String, Extractor> cache = new HashMap<String, Extractor>();

    public ContentExtractor() {
    }

    public static ContentExtractor getInstance() {
        if (instance == null) {
            instance = new ContentExtractor();
        }
        return instance;
    }

    public void clear() {
        extractors.clear();
        cache.clear();
    }

    public Extractor findExtractor(String mimeType) {
        log.debug("Find extractor: '" + mimeType + "'");
        Extractor ext = cache.get(mimeType);
        if (ext != null) {
            return ext;
        } else {
            if (cache.containsKey(mimeType)) {
                return null;
            }
            for (Map.Entry<Pattern, Extractor> entry : extractors) {
                if (entry.getKey().matcher(mimeType).matches()) {
                    ext = entry.getValue();
                    cache.put(mimeType, ext);
                    return ext;
                }
            }
            // keep track on not found extractors
            cache.put(mimeType, null);
            log.warn("Can't find extractor for mimetype: '" + mimeType + "'");
            return null;
        }

    }

    public void addExtractor(Extractor extractor, String mimeType) {
        if (mimeType == null) {
            mimeType = extractor.getMimeType();
        }
        cache.clear();
        Map.Entry<Pattern, Extractor> entry = new org.mmbase.util.Entry(Pattern.compile(mimeType), extractor);
        ListIterator <Map.Entry<Pattern, Extractor>> i = extractors.listIterator();
        while (i.hasNext()) {
            Map.Entry<Pattern, Extractor> e = i.next();
            if (e.getKey().equals(entry.getKey())) {
                log.service("Replacing Extractor for mimetype: '" + e + "'");
                i.remove();
                break;
            }
        }
        extractors.add(entry);
        log.service("Addded Extractor " + entry.getValue().getClass().getName() + " for mimetype: '" + mimeType + "'");
    }

    public void addExtractor(Class clazz, String mimeType) throws InstantiationException, IllegalAccessException {
        addExtractor((Extractor) clazz.newInstance(), mimeType);
    }

    public void addExtractor(String className, String mimeType) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        addExtractor(Class.forName(className), mimeType);
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
            String mimeType = attributes.getValue("mimetype");
            addExtractor(className, mimeType);
        }
        return null;
    }

}
