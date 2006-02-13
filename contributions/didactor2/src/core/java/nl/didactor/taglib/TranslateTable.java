package nl.didactor.taglib;
import org.mmbase.util.FileWatcher;
import java.util.*;
import java.io.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class implements a translation table. This table contains
 * translations for a namespace with different locales.
 * It reads the files in the translation path, and parses them. Based on the 
 * filenames, the namespace and locale are interpreted. For example:
 * <ul>
 *  <li> core.properties, contains the base translations for the 'core' namespace.
 *  <li> core.nl.properties, contains the translations for the 'core' namespace in the dutch locale.
 *  <li> core.nl_eo.properties, contains the translations for the 'core' namespace in the dutch locale with 'eo' specialization.
 * </ul>
 * If a translation cannot be found in a specialization, it's parent will
 * be queried instead, going to the root.
 * <p>
 * The translationtable will walk the current directory and
 * read all files found in it. 
 */
public class TranslateTable {
    private static Logger log = Logging.getLoggerInstance(TranslateTable.class.getName());
    private static Map translationTable = Collections.synchronizedMap(new HashMap());
    private static boolean initialized = false;
    private static TranslationFileWatcher watcher;
    private String translationlocale;

    /**
     * Inner class that watches the translation files and 
     * reloads them into the translation table in case they are
     * changed */
    static class TranslationFileWatcher extends FileWatcher { 
        /**
         * Constructor
         */
        public TranslationFileWatcher() { 
            super(true); 
        } 
        
        /**
         * Change event. Read the file and process it.
         */
        public void onChange(File file) { 
            readFile(file);
        } 
    } 

    /**
     * Initialize the entire Translation Table. This may only be done
     * once. The method is synchronized to prevent concurrent thread
     * to accessing it simultaniously.
     */
    public static synchronized void init(String path) {
        if (initialized) {
            return;
        }

        try {
            path = (new File(path)).getCanonicalPath();
        } catch (IOException e) {}

        watcher = new TranslationFileWatcher();
        watcher.setDelay(10 * 1000);
        addFiles(new File(path), watcher);

        watcher.start();
        initialized = true;
    }

    /**
     * Read a given directory and add all files to the filewatcher
     */
    private static void addFiles(File path, TranslationFileWatcher watcher) {
        if (!path.exists()) {
            return;
        }
        File[] files = path.listFiles();
        for (int i=0; i<files.length; i++) {
            if (files[i].isDirectory()) {
                // ignore
            } else {
                readFile(files[i]);
                watcher.add(files[i]);
            }
        }
    }

    /**
     * Read a file into the inner data structures.
     * @param file the file to read
     */
    protected static synchronized void readFile(File file) {
        // filename has the form: namespace.locale.properties
        String filename = file.getName();
        StringTokenizer st = new StringTokenizer(filename, ".");
        String namespace = st.nextToken();

        // If there is no '.' in the filename then it's not a valid translation file
        if (!st.hasMoreTokens()) {
            return;
        }
        String locale = st.nextToken();
        String properties = "";
        if (st.hasMoreTokens()) {
            properties = st.nextToken();
        } else {
            properties = locale;
            locale = "";
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#"))
                    continue;

                int equalsign = line.indexOf("=");
                if (equalsign == -1)
                    continue;

                String key = line.substring(0, equalsign).trim();
                String value = line.substring(equalsign + 1, line.length()).trim();

                String fkey = namespace;
                if (!"".equals(locale)) {
                    fkey += "." + locale;
                }
                fkey += "." + key;

                if (translationTable.containsKey(fkey)) {
                    translationTable.remove(fkey);
                    log.debug("removed previous definition for '" + fkey + "'");
                }
                translationTable.put(fkey, value);
                log.debug("added translation value for '" + fkey + "': '" + value + "'");
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
    }
    
    /**
     * Public constructor, it initializes the internal data structures.
     */
    public TranslateTable(String translationlocale) {
        this.translationlocale = translationlocale;
    }
   
    /**
     * Fetch a translation from the translation tables.
     */
    public String translate(String tkey) {
        log.debug("translate('" + tkey + "')");
        String locale = translationlocale;
        StringTokenizer st = new StringTokenizer(tkey, ".");
        String namespace = st.nextToken();
        if (!st.hasMoreTokens()) {
            log.error("Cannot translate key with no namespace: '" + tkey + "'");
            return null;
        }
        String key = st.nextToken();

        while (true) {
            String gkey = namespace;
            if (locale != null && !"".equals(locale)) {
                gkey += "." + locale;
            }
            gkey += "." + key;
            log.debug("Looking for translation for [" + gkey + "]");
            if (translationTable.containsKey(gkey)) {
                return (String)translationTable.get(gkey);
            } else {
                if (locale == null || "".equals(locale)) {
                    return null;
                }
            }
            if (locale.lastIndexOf("_") > -1) {
                locale = locale.substring(0, locale.lastIndexOf("_"));
            } else {
                locale = "";
            }
        }
    }

    /**
     * Set a new translation value
     */
    public static void changeTranslation(String tkey, String locale, String newvalue) {
        StringTokenizer st = new StringTokenizer(tkey, ".");
        String namespace = st.nextToken();
        String key = st.nextToken();

        String gkey = namespace;
        if (locale != null && !"".equals(locale)) {
            gkey += "." + locale;
        }
        gkey += "." + key;
        if (translationTable.containsKey(gkey)) {
            translationTable.remove(gkey);
        }
        translationTable.put(gkey, newvalue);
    }
}
