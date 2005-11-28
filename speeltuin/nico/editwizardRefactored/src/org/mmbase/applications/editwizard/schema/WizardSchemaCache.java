/*
 * Created on 2005-6-28
 *
 */
package org.mmbase.applications.editwizard.schema;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.mmbase.cache.Cache;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.ResourceWatcher;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Caches File to Editwizard schema Document.
 * 
 * @since MMBase-1.6.4
 */
class WizardSchemaCache extends Cache {

    /**
     * 
     */
    private static final long serialVersionUID = 3680074147018339723L;
    
    private static final Logger log = Logging.getLoggerInstance(WizardSchemaCache.class);

    WizardSchemaCache() {
        super(100);
    }

    public String getName() {
        return "Editwizard schemas";
    }

    public String getDescription() {
        return "File -> Editwizard schema Document (resolved includes/shortcuts)";
    }

    synchronized public Object put(URL url, WizardSchema wizardSchema, List dependencies) {
        Object retval = super.get(url);

        if (retval != null) { return retval; }

        return super.put(url, new Entry(url, wizardSchema, dependencies));
    }

    synchronized public Object remove(Object key) {
        URL file = (URL) key;
        Entry entry = (Entry) get(file);

        if ((entry != null) && (entry.fileWatcher != null)) {
            entry.fileWatcher.exit();
        }
        else {
            log.warn("entry: " + entry);
        }

        return super.remove(key);
    }

    synchronized public WizardSchema getWizardSchema(URL key) {
        Entry entry = (Entry) super.get(key);

        if (entry == null) { return null; }

        return entry.schema;
    }

    private class Entry {

        WizardSchema schema; // the document.

        URL file; // the file belonging to this document (key of cache)

        /**
         * Cache entries must be invalidated if (one of the) file(s) changes.
         */
        ResourceWatcher fileWatcher = new ResourceWatcher(ResourceLoader.getWebRoot()) {

            public void onChange(String u) {
                // invalidate this cache entry
                WizardSchemaCache.this.remove(Entry.this.file);
                // stop watching files
            }
        };

        Entry(URL url, WizardSchema wizardSchema, List dependencies) {
            this.file = url;
            this.schema = wizardSchema;
            fileWatcher.add(url);

            if (dependencies != null) {
                Iterator i = dependencies.iterator();

                while (i.hasNext()) {
                    URL ff = (URL) i.next();
                    fileWatcher.add(ff);
                }
            }
            fileWatcher.setDelay(10 * 1000); // check every 10 secs
            fileWatcher.start();
        }
    }
}