package nl.didactor.taglib;
import org.mmbase.util.FileWatcher;
import java.util.*;
import java.io.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class implements a translation table. This table contains
 * translations for different levels, that are represented as
 * (unix) pathnames. An example:
 * <ul>
 *  <li> /mediator/frans, contains translations for this path
 *  <li> /mediator, contains translations for the parent of the previous path
 * </ul>
 * If a translation cannot be found in a path, it's parent will
 * be queried instead, going to the root.
 * <p>
 * The translationtable will recursively walk the current directory and
 * read all files found in it. All files together contain the translations
 * for the given path.
 */
public class TranslateTable {
    private static Logger log = Logging.getLoggerInstance(TranslateTable.class.getName());
    private static Map translationTables = Collections.synchronizedMap(new HashMap());
    private static boolean initialized = false;
    private static TranslationFileWatcher watcher;
    private String translationpath;

    /**
     * Inner class that watches the translation files and 
     * reloads them into the translation table in case they are
     * changed */
    static class TranslationFileWatcher extends FileWatcher { 
        private String basepath;

        /**
         * Constructor
         */
        public TranslationFileWatcher(String basepath) { 
            super(true); 
            try {
                this.basepath = (new File(basepath).getCanonicalPath());
            } catch (IOException e) {}
        } 
        
        /**
         * Change event. Read the file and process it.
         */
        public void onChange(File file) { 
            try {
                readFile(basepath, file.getCanonicalPath());
            } catch (IOException e) {}
        } 
    } 

    /**
     * Initialize the entire Translation Table. This may only be done
     * once. The method is synchronized to prevent concurrent thread
     * to accessing it simultaniously.
     */
    public static synchronized void init(String path) {
        if (initialized)
            return;

        try {
            path = (new File(path)).getCanonicalPath();
        } catch (IOException e) {}

        watcher = new TranslationFileWatcher(path);
        watcher.setDelay(10 * 1000);
        recAddFiles(path, new File(path), watcher);

        watcher.start();
        initialized = true;
    }

    /**
     * Recursively descend a given directory and add all files to the filewatcher
     */
    private static void recAddFiles(String basepath, File path, TranslationFileWatcher watcher) {
        if (!path.exists()) {
            return;
        }
        File[] files = path.listFiles();
        for (int i=0; i<files.length; i++) {
            if (files[i].isDirectory()) {
                recAddFiles(basepath, files[i], watcher);
            } else {
                try {
                    readFile(basepath, files[i].getCanonicalPath());
                } catch (IOException e) {}
                watcher.add(files[i]);
            }
        }
    }

    /**
     * Read a file into the inner data structures.
     * @param basepath The base path 
     * @param path the path to read the file from
     */
    protected static synchronized void readFile(String basepath, String path) {
        File root = new File(basepath);
        File file = new File(path);
        String location = "";
        boolean first = true;
        while (root.compareTo(file) != 0) {
            if (first) {
                first = false;
            } else {
                location = "/" + file.getName() + location;
            }
            file = file.getParentFile();
        }
        Map m = (Map) translationTables.get(location);
        if (m == null) {
            m = Collections.synchronizedMap(new HashMap());
            translationTables.put(location, m);
        }        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#"))
                    continue;

                int equalsign = line.indexOf("=");
                if (equalsign == -1)
                    continue;

                String key = line.substring(0, equalsign).trim();
                String value = line.substring(equalsign + 1, line.length()).trim();
                m.put(key, value);
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
       
        log.debug("Adding translations from file [" + path + "] with key [" + location + "]");
    }
    
    /**
     * Public constructor, it initializes the internal data structures.
     */
    public TranslateTable(String translationpath) {
        this.translationpath = translationpath;
    }
   
    /**
     * Fetch a translation from the translation tables.
     */
    public String translate(String text) {
        String path = translationpath;
        while (path.lastIndexOf("/") != -1 || path.equals("")) {
            log.debug("Looking for translation for [" + text + "] and key [" + path + "]");
            Map m = (Map)translationTables.get(path);
            if (m != null) {
                if (m.containsKey(text)) {
                    log.debug("Translation found");
                    return (String)m.get(text);
                }
            }
            if (path.equals(""))
                break;

            path = path.substring(0, path.lastIndexOf("/"));
        }

        return null;
    }
}
