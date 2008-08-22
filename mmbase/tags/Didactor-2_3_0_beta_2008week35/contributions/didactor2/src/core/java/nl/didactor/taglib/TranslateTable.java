package nl.didactor.taglib;
import org.mmbase.util.*;
import org.mmbase.util.transformers.*;

import java.util.*;
import java.io.*;
import java.text.*;


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
 * @version $Id: TranslateTable.java,v 1.20 2008-08-06 17:14:47 michiel Exp $
 */
public class TranslateTable {
    private static final Logger log = Logging.getLoggerInstance(TranslateTable.class);
    private static final Map<String, String>  translationTable = Collections.synchronizedMap(new HashMap<String, String>());
    private static boolean initialized = false;
    private static TranslationFileWatcher watcher;

    private static TranslateTable defaultTable = null;

    private static TranslateTable getDefault() {
        if (defaultTable == null) defaultTable = new TranslateTable(null);
        return defaultTable;
    }

    private final Locale translationLocale;

    /**
     * Inner class that watches the translation files and
     * reloads them into the translation table in case they are
     * changed
     */
    static class TranslationFileWatcher extends ResourceWatcher {
        public TranslationFileWatcher(ResourceLoader rl) {
            super(rl);
        }

        /**
         * Change event. Read the file and process it.
         */
        public void onChange(String resource) {
            readResource(resourceLoader, resource);
        }
    }

    /**
     * Initialize the entire Translation Table. This may only be done
     * once. The method is synchronized to prevent concurrent thread
     * to accessing it simultaniously.
     */
    public static synchronized void init() {
        if (initialized) {
            return;
        }
        ResourceLoader loader =  ResourceLoader.getConfigurationRoot().getChildResourceLoader("translations");
        watcher = new TranslationFileWatcher(loader);
        watcher.setDelay(10 * 1000);
        addResources(watcher);

        watcher.start();
        initialized = true;
    }

    /**
     * Read a given directory and add all files to the filewatcher
     */
    private static void addResources(TranslationFileWatcher watcher) {
        Set<String> subs =  watcher.getResourceLoader().getResourcePaths(java.util.regex.Pattern.compile(".*\\.properties"), false);
        for (String sub : subs) {
            readResource(watcher.getResourceLoader(), sub);
            watcher.add(sub);
        }
    }

    /**
     * Read a file into the inner data structures.
     * @param file the file to read
     */
    protected static synchronized void readResource(ResourceLoader loader, String resource) {
        // filename has the form: namespace.locale.properties
        StringTokenizer st = new StringTokenizer(resource, ".");
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

            // we want to profit from Properties#load (all kind of handy features),
            // but we also want the property files to be in unicode.
            // Following trick with Transforming readers and so on, arranges that.
            Properties props = new Properties();
            InputStream in = new ReaderInputStream(new TransformingReader(new InputStreamReader(loader.getResourceAsStream(resource), "UTF-8"),
                                                                          new UnicodeEscaper()),
                                                   "ISO-8859-1");
            props.load(in);

            for (Map.Entry entry : props.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();

                String fkey = namespace;
                if (!"".equals(locale)) {
                    fkey += "." + locale;
                }
                fkey += "." + key;

                if (translationTable.containsKey(fkey)) {
                    translationTable.remove(fkey);
                    if (log.isDebugEnabled()) {
                        log.debug("removed previous definition for '" + fkey + "'");
                    }
                }
                translationTable.put(fkey, value);
                log.debug("added translation value for '" + fkey + "': '" + value + "'");
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
    }

    /**
     * Sync the entire translation tables to disk. This is done by iterating
     * over the (sorted) set of translation keys, and parsing them into component,
     * locale and messagekey. The file for this component+locale is emptied, the new
     * keys are saved to that file. During this process the file is unsubscribed from
     * the filewatcher, to make sure that there are no re-reads of the translation table
     * when we are writing it to disk.
     */
    public static void save() {
        Map<String, PrintWriter> seenFiles = new HashMap<String, PrintWriter>();
        synchronized(translationTable) {
            TreeSet<String> ts = new TreeSet<String>(translationTable.keySet());
            String previousFilename = "";
            PrintWriter out = null;
            for (String key : ts) {
                StringTokenizer st = new StringTokenizer(key, ".");
                String component = st.nextToken();
                String locale = st.nextToken();
                String filename = "";
                String completekey = key;

                // There are two options: component.keyname and component.locale.keyname
                if (st.hasMoreTokens()) {
                    key = st.nextToken();
                    filename = component + "." + locale + ".properties";
                } else {
                    key = locale;
                    locale = "";
                    filename = component + ".properties";
                }

                // If we were not already writing to this filename, we have to open up
                // the new file (and close the previous one)
                if (!filename.equals(previousFilename)) {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                    try {

                        // If we have written to this file before, we must append to it!
                        out = seenFiles.get(filename);
                        if (out == null) {
                            // New file, remove it from the filewatcher (so we are sure that there is nobody
                            // reading the file when we are writing it.
                            watcher.remove(filename);
                            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(watcher.getResourceLoader().createResourceAsStream(filename), "UTF-8")));
                            seenFiles.put(filename, out);
                        }
                    } catch (IOException e) {
                        log.error("Exception while trying to write resource '" +  watcher.getResourceLoader() + " " + filename + "': " + e, e);
                    }
                }
                previousFilename = filename;
                out.println(key + "=" + translationTable.get(completekey));
            }
            for (PrintWriter o : seenFiles.values()) {
                o.flush();
                o.close();
            }
        }

        // Final step: add the files to the filewatcher again
        for (String fn : seenFiles.keySet()) {
            watcher.add(fn);
        }
    }

    /**
     * Public constructor, it initializes the internal data structures.
     */
    public TranslateTable(Locale translationLocale) {
        this.translationLocale = translationLocale == null ? org.mmbase.module.core.MMBase.getMMBase().getLocale() : translationLocale;
    }




    protected  static Locale degrade(Locale locale, Locale originalLocale) {
        String language = locale.getLanguage();
        String country  = locale.getCountry();
        String variant  = locale.getVariant();
        if (variant != null && ! "".equals(variant)) {
            String[] var = variant.split("_");
            if (var.length > 1) {
                StringBuilder v = new StringBuilder(var[0]);
                for (int i = 1; i < var.length - 1; i++) {
                    v.append('_');
                    v.append(var[i]);
                }
                return new Locale(language, country, v.toString());
            } else {
                return new Locale(language, country);
            }
        }
        if (! "".equals(country)) {
            String originalVariant = originalLocale.getVariant();
            if (originalVariant  != null && ! "".equals(originalVariant)) {
                return new Locale(language, "", originalVariant);
            } else {
                return new Locale(language);
            }
        }
        // cannot be degraded any more.
        return null;
    }

    /**
     * A bit different then Locale#toString, because country can be skipped.
     */
    protected String toString(Locale locale) {
        StringBuilder loc = new StringBuilder(locale.getLanguage());
        String country = locale.getCountry();
        if (! "".equals(country)) {
            loc.append('_');
            loc.append(country);
        }
        String variant = locale.getVariant();
        if (variant != null && ! "".equals(variant)) {
            loc.append('_');
            loc.append(variant);
        }
        return loc.toString();
    }

    /**
       Default ResourceBundle#getBundle
       # baseName + "_" + language1 + "_" + country1 + "_" + variant1
       # baseName + "_" + language1 + "_" + country1
       # baseName + "_" + language1

       Didactor:

       #0 baseName + "_" + language1 + "_" + country1 + "_" + variant1_variant2
       #1 baseName + "_" + language1 + "_" + country1 + "_" + variant1
       #2 baseName + "_" + language1 + "_" + country1
       #3 baseName + "_" + language1 + "_" + variant1_variant2
       #4 baseName + "_" + language1 + "_" + variant1
       #5 baseName + "_" + language1
       #6 baseName + "_" + language1

     * Fetch a translation from the translation tables.
     */
    public String translate(String tkey) {
        if (log.isDebugEnabled()) {
            log.debug("translate('" + tkey + "')");
        }
        Locale locale = translationLocale;
        StringTokenizer st = new StringTokenizer(tkey, ".");

        if (! st.hasMoreTokens()) {
            return "???[" + tkey + "]";
        }
        String namespace = st.nextToken();
        if (!st.hasMoreTokens()) {
            log.error("Cannot translate key with no namespace: '" + tkey + "'");
            return null;
        }
        String key = st.nextToken();

        while (true) {
            String gkey = namespace + "." + toString(locale) + "." + key;
            if (log.isTraceEnabled()) {
                log.trace("Looking for translation for [" + gkey + "] in " + translationTable);
            }
            String translation = translationTable.get(gkey);
            if (translation != null) {
                return translation;
            } else {
                Locale prev = locale;
                locale = degrade(locale, translationLocale);
                log.debug("degraded " + prev + " to " + locale + " because '" + gkey + "' not found");
            }
            if (locale == null) {
                TranslateTable def = getDefault();
                if (def != this) {
                    return def.translate(tkey);
                } else {
                    String t = translationTable.get(namespace + "." + key);
                    return t != null ? t : key;
                }
            }
        }
    }
    public String translate(String tkey, Object... args) {
        String translation = translate(tkey);
        if (translation == null) return tkey;
        MessageFormat message = new MessageFormat(translation, translationLocale);
        String res = message.format(args, new StringBuffer(), null).toString();
        if (log.isDebugEnabled()) {
            log.debug("Formatting " + translation + " " + Arrays.asList(args) + " (" + translationLocale + ") -> " + res);
        }
        return res;
    }

    /**
     * Set a new translation value
     */
    public static void changeTranslation(String tkey, String locale, String newvalue) {
        synchronized(translationTable) {
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
}
