/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.xml.query.*;
import org.mmbase.util.xml.DocumentSerializable;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.logging.*;

/**
 * These factories can produce Collections based on a Locale (The {@link #get} method is
 * essential). The other methods besides get are methods to define these lists. There are two ways
 * to add entries to the produced collections. The first one is to explicitely add them, using the
 * {@link #add} method. This gives precise control, and the collections can have different orders
 * for different languages. The size of the collections are always the same, so if for a certain
 * locale less entries are added, these are completed with the unused keys. If for a certain locale
 * <em>no</em> entries are added, it will behave itself like as the default locale of {@link
 * LocalizedString#getDefault}.
 *
 * It is also possible to add entire 'bundles'. For this use {@link #addBundle}. When a Collection instance
 * for a certain Locale is requested these informations are used to call {@link
 * SortedBundle#getResource}.
 *
 * It is possible to mix both methods, so having an enumeration partially defined by a bundle,
 * partially by explicit values, though this is not recommended.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class LocalizedEntryListFactory<C> implements Serializable, Cloneable {

    private static final Logger log = Logging.getLoggerInstance(LocalizedEntryListFactory.class);
    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    private int size = 0; // number of explicitely added keys
    private boolean usesCloud = false;

    // we don't use interfaces here, because of Serializability
    private static class LocalizedEntry implements Serializable, PublicCloneable {
        private static final long serialVersionUID = 1L;
        ArrayList entries    = new ArrayList(); //  List of Map.Entries, Bundles and DocumentSerializable's
        ArrayList<Serializable> unusedKeys = new ArrayList<Serializable>(); //  List of unused keys;
        public Object clone() {
            try {
                LocalizedEntry clone = (LocalizedEntry) super.clone();
                Iterator<PublicCloneable> i = clone.entries.iterator();
                clone.entries = new ArrayList();
                while(i.hasNext()) {
                    clone.entries.add(i.next().clone());
                }
                clone.unusedKeys = (ArrayList) unusedKeys.clone();
                return clone;
            } catch (CloneNotSupportedException cnse) {
                log.error(cnse);
                return new LocalizedEntry();
            }
        }
        public String toString() {
            return "entries:" + entries + "uu:" + unusedKeys;
        }
    }
    private HashMap<Locale, LocalizedEntry> localized  = new HashMap<Locale, LocalizedEntry>();
    private ArrayList<Bundle> bundles  = new ArrayList<Bundle>(); // contains all Bundles
    private ArrayList<Serializable> fallBack = new ArrayList<Serializable>(); // List of known keys, used as fallback, if nothing defined for a certain locale

    private DocumentSerializable xml = null;

    public LocalizedEntryListFactory() {
        localized.put(LocalizedString.getDefault(), new LocalizedEntry());
    }



    public boolean isEmpty() {
        return bundles.size() == 0 && fallBack.size() == 0 && !usesCloud;
    }

    /**
     * Adds a value for a certain key and Locale
     * @return The created Map.Entry.
     */
    public Map.Entry<Serializable, Serializable> add(Locale locale, Serializable key, Serializable value) {
        if (locale == null) {
            locale = LocalizedString.getDefault();
        }

        Entry<Serializable, Serializable> entry = new Entry(key, value);
        List<Serializable> unused = add(locale, entry);
        if (! fallBack.contains(key)) {
            // this is an as yet unknown key.
            size++;
            fallBack.add(key);
            for (Map.Entry<Locale, LocalizedEntry> e : localized.entrySet()) {
                if (! e.getKey().equals(locale)) {
                    LocalizedEntry loc = e.getValue();
                    loc.unusedKeys.add(key);
                }
            }
        }
        unused.remove(key);
        return entry;
    }

    /**
     * Add entry to 'localized'
     * @param entry the object, which has not Locale support of itself (Entry, DocumentSerializable)
     * @param locale Can be <code>null</code> too, in which case the default locale is used
     * @return List of currently unused keys for this locale.
     */

    protected List<Serializable> add(Locale locale, Object entry) {
        if (locale == null) {
            locale = LocalizedString.getDefault();
        }
        LocalizedEntry local = localized.get(locale);
        if (local == null) {
            Locale loc = locale;
            loc = LocalizedString.degrade(loc, locale);
            while (loc != null && local == null) {
                local = localized.get(loc);
                loc = LocalizedString.degrade(loc, locale);
            }
            if (local == null) {
                local = new LocalizedEntry();
                local.entries.addAll(bundles);
                local.unusedKeys.addAll(fallBack);
            } else {
                local = (LocalizedEntry) local.clone();
            }
            localized.put(locale, local);
        }

        // If this locale with variant is added but the parent locale was not yet in the map, then
        // we first add the parent locale.
        if (locale.getVariant() != null && !"".equals(locale.getVariant())) {
            Locale l = new Locale(locale.getLanguage(), locale.getCountry());
            if (!localized.containsKey(l)) {
                add(l, entry);
            }
        }

        // If this locale with country is added, but the parent locale (only language) was not yet in
        // the map, we first add the parent language locale.
        if (locale.getCountry() != null && !"".equals(locale.getCountry())) {
            Locale l = new Locale(locale.getLanguage());
            if (!localized.containsKey(l)) {
                add(l, entry);
            }
        }
        local.entries.add(entry);
        return local.unusedKeys;
    }

    /**
     * Adds a bundle, to the (current) end of all maintained collections. Actually, only the
     * definition of the bundle is added, it is instantiated only later, when requested for a
     * specific locale.
     */
    public void addBundle(String baseName, ClassLoader classLoader, Class constantsProvider, Class wrapper, Comparator comparator) {
        Bundle b = new Bundle(baseName, classLoader, SortedBundle.getConstantsProvider(constantsProvider), wrapper, comparator);
        if (bundles.contains(b)) {
            log.info("Adding bundle " + b + " for second time in " + b + ", because " + Logging.stackTrace());
        }
        bundles.add(b);
        Iterator<LocalizedEntry> i = localized.values().iterator();
        if (!i.hasNext()) {
            // adding very first localizedlist
            Locale locale = LocalizedString.getDefault();
            LocalizedEntry local = new LocalizedEntry();
            local.entries.add(b);
            local.unusedKeys.addAll(fallBack);
        } else while(i.hasNext()) {
            LocalizedEntry local  = i.next();
            local.entries.add(b);
        }
    }

    /**
     */
    public void addQuery(Locale locale, Document queryElement) {
        DocumentSerializable doc = new DocumentSerializable(queryElement);
        add(locale, doc);
        usesCloud = true;
    }


    /**
     * Defaulting version of {@link #get(Locale, Cloud)}. Using default anonymous cloud.
     */
    public List<Map.Entry<C, String>> get(final Locale locale) {
        return get(locale, usesCloud ? getCloud(locale) : null);
    }

    protected Cloud getCloud(Locale locale) {
        CloudContext context = ContextProvider.getDefaultCloudContext();
        if (context.isUp()) {
            try {
                Cloud cloud = context.getCloud("mmbase", "class", null);
                if (locale != null) cloud.setLocale(locale);
                return cloud;
            } catch (SecurityException se) {
                log.warn("" + se.getMessage(), se);
                try {
                    Cloud cloud = context.getCloud("mmbase");
                    if (locale != null && cloud != null) cloud.setLocale(locale);
                    return cloud;
                } catch (SecurityException se2) {
                    return null;
                }
            }
        } else {
            return null;
        }
    }


    /**
     * @since MMBase-1.9.1
     */
    public List<Map.Entry<C, String>> get(final Locale locale, Cloud c, final org.mmbase.bridge.Node node, final Field field) {
        if (c == null) {
            if (node != null) {
                c = node.getCloud();
            } else if (field != null) {
                c = field.getNodeManager().getCloud();
            }
        }
        final Cloud cloud = c;
        return new AbstractSequentialList<Map.Entry<C, String>> () {

            public int size() {
                return LocalizedEntryListFactory.this.size(cloud);
            }
            public ListIterator<Map.Entry<C, String>> listIterator(final int index) {
                return new ListIterator<Map.Entry<C, String>>() {
                        int   i = -1;
                        Locale useLocale = locale;
                        Cloud useCloud = cloud;

                        {
                            if (useLocale == null) {
                                useLocale = useCloud != null ? useCloud.getLocale() : LocalizedString.getDefault();
                            }
                            log.debug("using locale " + useLocale);
                        }
                        private ChainedIterator iterator = new ChainedIterator();
                        private Iterator<Map.Entry<C, String>> subIterator = null;
                        private Map.Entry<C, String> next = null;

                        {
                            Locale orgLocale = useLocale;

                            LocalizedEntry loc = localized.get(useLocale);
                            while (loc == null && useLocale != null) {
                                useLocale = LocalizedString.degrade(useLocale, orgLocale);
                                if (log.isDebugEnabled()) {
                                    log.debug("Degraded to " + useLocale);
                                }
                                loc = localized.get(useLocale);
                            }
                            if (loc == null) {
                                useLocale = orgLocale;
                                loc = localized.get(LocalizedString.getDefault());
                            }

                            if (loc == null) {

                                iterator.addIterator(bundles.iterator());
                                iterator.addIterator(fallBack.iterator());
                            } else {
                                iterator.addIterator(loc.entries.iterator());
                                iterator.addIterator(loc.unusedKeys.iterator());
                            }

                            findNext();
                            while (i < index) next();
                        }

                        protected void findNext() {
                            next = null;
                            i++;
                            while(next == null && iterator.hasNext()) {
                                Object candidate = iterator.next();
                                if (candidate instanceof Map.Entry) {
                                    next = (Map.Entry) candidate;
                                } else if (candidate instanceof Bundle) {
                                    subIterator = ((Bundle) candidate).get(useLocale).iterator();
                                    if (subIterator.hasNext()) {
                                        break;
                                    } else {
                                        subIterator = null;
                                    }
                                } else if (candidate instanceof DocumentSerializable) {
                                    Element element = ((DocumentSerializable) candidate).getDocument().getDocumentElement();
                                    try {
                                        if (useCloud == null) {
                                            useCloud = getCloud(useLocale);
                                            if (useCloud == null) {
                                                if (log.isDebugEnabled()) {
                                                    log.debug("Defined query for " + this + " but no cloud provided. Skipping results.");
                                                }
                                                continue;
                                            }
                                        }
                                        final QueryConfigurer qc = new QueryConfigurer();
                                        if (node != null) {
                                            qc.variables.put("_node", node.getNumber());
                                        }
                                        final Query query = QueryReader.parseQuery(element, qc, useCloud, null).query;
                                        final org.mmbase.bridge.NodeList list = query.getList();
                                        subIterator = new Iterator<Map.Entry<C, String>>() {
                                                final NodeIterator nodeIterator = list.nodeIterator();
                                                public boolean hasNext() {
                                                    return nodeIterator.hasNext();
                                                }
                                                public Map.Entry<C, String> next() {
                                                    org.mmbase.bridge.Node next = nodeIterator.nextNode();
                                                    if (query instanceof NodeQuery) {
                                                        String gui = next == null ? "NULL_NODE" : next.getFunctionValue("gui", null).toString();
                                                        return new Entry<C, String>((C) next, gui);
                                                    } else {
                                                        String alias = Queries.getFieldAlias(query.getFields().get(0));
                                                        log.debug("using field " + alias);
                                                        if (query.getFields().size() == 1) {
                                                            return new Entry<C, String>((C) next.getValue(alias),
                                                                             next.getStringValue(alias));
                                                        } else {
                                                            StringBuilder buf = new StringBuilder();
                                                            for (int i = 1 ; i < query.getFields().size(); i++) {
                                                                String a = Queries.getFieldAlias(query.getFields().get(i));
                                                                if (buf.length() > 0) buf.append(" ");
                                                                buf.append(next.getStringValue(a));
                                                            }
                                                            return new Entry<C, String>((C) next.getValue(alias),
                                                                                        buf.toString());
                                                        }
                                                    }
                                                }
                                                public void remove() {
                                                    throw new UnsupportedOperationException();
                                                }
                                            };
                                        if (subIterator.hasNext()) {
                                            break;
                                        } else {
                                            subIterator = null;
                                        }
                                    } catch (Exception e) {
                                        log.error(e.getMessage(), e);
                                    }
                                } else {
                                    next = new Entry(candidate, candidate);
                                }
                            }
                        }

                        public boolean hasNext() {
                            return next != null || subIterator != null;
                        }
                        public Map.Entry<C, String> next() {
                            Map.Entry<C, String> res;
                            if (subIterator != null) {
                                res = subIterator.next();
                                Object key = res.getKey();
                                if (key != null && key instanceof SortedBundle.ValueWrapper) {
                                    res = new Entry(((SortedBundle.ValueWrapper) key).getKey(), res.getValue());
                                }
                                if (!subIterator.hasNext()) {
                                    subIterator = null;
                                    findNext();
                                }
                            } else {
                                res = next;
                                findNext();
                            }
                            return res;
                        }
                        public int nextIndex() {
                            return i;
                        }
                        public int previousIndex() {
                            return i - 1;
                        }
                        public boolean hasPrevious() {
                            // TODO
                            throw new UnsupportedOperationException();
                        }
                        public Map.Entry<C, String> previous() {
                            // TODO
                            throw new UnsupportedOperationException();
                        }
                        // this is why we hate java:


                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                        public void add(Map.Entry<C, String> o) {
                            throw new UnsupportedOperationException();
                        }
                        public void set(Map.Entry<C, String> o) {
                            throw new UnsupportedOperationException();
                        }
                    };
            }
        };
    }

    /**
     * Returns a Collection of Map.Entries for the given Locale. The collection is kind of 'virtual',
     * it only reflects the underlying memory structures.
     *
     * This collection does have a well defined iteration order.
     *
     * @param locale The locale of <code>null</code> for the default locale.
     * @param cloud  The cloud to use. Can be <code>null</code> if no queries added (see {@link #addQuery}).
     *               If Locale is <code>null</code>, but cloud isn't, the locale of the cloud is used.
     */
    public List<Map.Entry<C, String>> get(final Locale locale, final Cloud cloud) {
        return get(locale, cloud, null, null);
    }
    /**
     * The size of the collections returned by {@link #get}
     */
    public int size(Cloud cloud) {
        if (cloud == null) cloud = getCloud(LocalizedString.getDefault());
        int queriesSize = size();
        Locale locale = cloud == null ? LocalizedString.getDefault() : cloud.getLocale();
        LocalizedEntry localizedList = localized.get(locale);
        if (localizedList == null) {
            locale = LocalizedString.getDefault();
            localizedList = localized.get(locale);
        }
        if (localizedList != null) {
            Iterator i = localizedList.entries.iterator();
            while (i.hasNext()) {
                Object o = i.next();
                if (o instanceof Bundle) {
                    // already in size();
                } else if (o instanceof DocumentSerializable) {
                    if (cloud == null) {
                        cloud = getCloud(null);
                        if (cloud == null) {
                            log.debug("Found query but didn't provide cloud, skipping");
                            continue;
                        }
                    }
                    Element element = ((DocumentSerializable) o).getDocument().getDocumentElement();
                    try {
                        queriesSize += Queries.count(QueryReader.parseQuery(element, cloud, null).query);
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }
                } else {
                    queriesSize++;
                }
            }
            //queriesSize += localizedList.unusedKeys.size();
        }

        return queriesSize;
    }

    public int size() {
        int bundleSize = 0;
        Iterator i = bundles.iterator();
        while (i.hasNext()) {
            Bundle b = (Bundle) i.next();
            try {
                bundleSize += b.get(null).size();
            } catch (MissingResourceException mre) {
                log.error(mre);
            }
        }
        return size + bundleSize;
    }

    public Object castKey(final Object key) {
        return castKey(key, null);
    }
    /**
     * Since keys may be somehow wrapped, you can also 'unwrap' by this. If e.g. a constants
     * provider was used, that values can be indicated by the name of the constants and this method
     * casts to the value.
     */
    public Object castKey(final Object key, final Cloud cloud) {
        String string = null;
        Iterator<Bundle> i = bundles.iterator();
        if (i.hasNext()) {
            string = Casting.toString(key);
            while (i.hasNext()) {
                Bundle b = i.next();
                Class wrapper = b.wrapper;
                Map<String,Object> constants = b.constantsProvider;
                Object nk = SortedBundle.castKey(string, null, constants, wrapper);
                if (string != nk) {
                    if (log.isDebugEnabled()) {
                        log.debug("Cast " + key + " to " + nk);
                    }
                    return nk;
                }
            }
        }
        if (usesCloud && cloud != null) {
            if (string == null) string = Casting.toString(key);
            if (cloud.hasNode(string)) {
                return cloud.getNode(string);
            }
        }
        if (key != null && key instanceof SortedBundle.ValueWrapper) {
            return ((SortedBundle.ValueWrapper) key).getKey();
        }
        return key;

    }

    public Object clone() {
        try {
            LocalizedEntryListFactory clone = (LocalizedEntryListFactory) super.clone();
            Iterator<Bundle> j = clone.bundles.iterator();
            clone.bundles   = new ArrayList<Bundle>();
            while(j.hasNext()) {
                clone.bundles.add((j.next().clone()));
            }
            Iterator<Map.Entry<Locale, LocalizedEntry>> i = clone.localized.entrySet().iterator();
            clone.localized = new HashMap();
            while(i.hasNext()) {
                Map.Entry<Locale, LocalizedEntry> entry =  i.next();
                clone.localized.put(entry.getKey(), (entry.getValue()).clone());
            }
            clone.fallBack  = (ArrayList) fallBack.clone();
            return clone;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new LocalizedEntryListFactory();
        }
    }

    /**
     * Clears all added keys, bundles and queries.
     */
    public void clear() {
        localized.clear();
        bundles.clear();
        fallBack.clear();
        usesCloud = false;
        size = 0;
    }


    /**
     * Given a certain DOM parent element, it configures this LocalizedEntryListFactory with
     * sub tags of type 'entry' and 'query'
     */

    public void fillFromXml(final Element enumerationElement, Class wrapperDefault) {
        xml = new DocumentSerializable(DocumentReader.toDocument(enumerationElement));
        org.w3c.dom.NodeList childNodes = enumerationElement.getElementsByTagName("query");
        for (int i = 0; i < childNodes.getLength(); i++) {
            Element queryElement = (Element) childNodes.item(i);
            Locale locale = LocalizedString.getLocale(queryElement);
            addQuery(locale, DocumentReader.toDocument(queryElement));
        }


        childNodes = enumerationElement.getElementsByTagName("entry");
        for (int i = 0; i < childNodes.getLength(); i++) {
            Element entryElement = (Element) childNodes.item(i);
            if (entryElement.hasAttribute("value")) {
                String value = entryElement.getAttribute("value");
                Locale locale = LocalizedString.getLocale(entryElement);
                String display = entryElement.getAttribute("display");
                if (display.equals("")) display = value;
                Object key = wrapperDefault != null ? Casting.toType(wrapperDefault, null, value) : value;
                if (key instanceof org.mmbase.bridge.Node) {
                    // if you sepcify node-typed enumeration by entry value=, then this happens.
                    // Perhaps this should happen a bit more generically. Casting.toSerializable, or so.
                    key = new Integer(((org.mmbase.bridge.Node) key).getNumber());
                }
                if (key instanceof Serializable) {
                    if (log.isDebugEnabled()) {
                        log.debug("Added " + key + "/" + display + " for " + locale);
                    }
                    add(locale, (Serializable) key, display);
                } else {
                    log.error("key " + key + " for " + wrapperDefault + " is not serializable, cannot be added to entrylist factory.");
                }
            } else {
                String resource = entryElement.getAttribute("basename");
                if (! resource.equals("")) {
                    Comparator comparator = null;
                    Class wrapper    = wrapperDefault;
                    if (wrapper != null &&
                        (! Comparable.class.isAssignableFrom(wrapper)) &&
                        (! Boolean.class.equals(wrapper)) // in java < 1.5 Boolean is not comparable
                        ) {
                        wrapper = null;
                    }

                    {
                        String sorterClass = entryElement.getAttribute("sorterclass");
                        if (!sorterClass.equals("")) {
                            try {
                                Class sorter = Class.forName(sorterClass);
                                if (Comparator.class.isAssignableFrom(sorter)) {
                                    comparator = (Comparator) sorter.newInstance();
                                } else {
                                    wrapper = sorter;
                                }
                            } catch (Exception e) {
                                log.error(e);
                            }
                        }
                    }
                    Class constantsClass = null;
                    {
                        String javaConstants = entryElement.getAttribute("javaconstants");
                        if (!javaConstants.equals("")) {
                            try {
                                constantsClass = Class.forName(javaConstants);
                            } catch (Exception e) {
                                log.error(e);
                            }
                        }
                    }
                    try {
                        addBundle(resource, getClass().getClassLoader(), constantsClass,
                                  wrapper, comparator);
                    } catch (MissingResourceException mre) {
                        log.error(mre);
                    }
                } else {
                    throw new IllegalArgumentException("no 'value' or 'basename' attribute on enumeration entry element");
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Found enumeration values now " + this);
            }
        }

    }
    public Element toXml() {
        if (xml == null) {
            // TODO: generate xml.
            return null;
        } else {
            return xml.getDocument().getDocumentElement();
        }
    }

    public String toString() {
        return "(localized: " + localized  + "bundles: " + bundles + "fallBack: " + fallBack + ")";
    }

    private static class Bundle<D> implements Serializable, PublicCloneable<Bundle<D>> {
        private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

        private String      resource;
        private ClassLoader classLoader;
        private HashMap<String,Object>     constantsProvider;
        private Class       wrapper;
        private Comparator  comparator;

        // implementation of serializable
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeUTF(resource);
            // dont'r write class-loader, it is not serializable
            out.writeObject(constantsProvider);
            out.writeObject(wrapper);
            if (comparator instanceof Serializable) {
                out.writeObject(comparator);
            } else {
                out.writeObject((Comparator) null);
            }
        }
        // implementation of serializable
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            resource          = in.readUTF();
            classLoader       = getClass().getClassLoader();
            constantsProvider = (HashMap) in.readObject();
            wrapper           = (Class) in.readObject();
            comparator        = (Comparator) in.readObject();
        }



        Bundle(String r, ClassLoader cl, HashMap<String,Object> cp, Class w, Comparator comp) {
            resource = r; classLoader = cl; constantsProvider = cp ; wrapper = w; comparator = comp;
        }
        /**
         * Collection of Map.Entry's
         */
        Collection<Map.Entry<D, String>> get(Locale loc) throws MissingResourceException {
            try {
                Map<D, String> resourceMap = SortedBundle.getResource(resource, loc, classLoader, constantsProvider, wrapper, comparator);
                return resourceMap.entrySet();
            } catch (IllegalArgumentException iae) {
                log.error(iae);
                return Collections.emptySet();
            }
        }


        public String toString() {
            return resource + " " + constantsProvider + " " + wrapper + " " + comparator;
        }
        public boolean equals(Object o) {
            if (o instanceof Bundle) {
                Bundle b = (Bundle) o;
                return
                    (resource == null ? b.resource == null : resource.equals(b.resource)) &&
                    (classLoader == null ? b.classLoader == null : classLoader.equals(b.classLoader)) &&
                    (constantsProvider == null ? b.constantsProvider == null : constantsProvider.equals(b.constantsProvider)) &&
                    (wrapper == null ? b.wrapper == null : wrapper.equals(b.wrapper)) &&
                    (comparator == null ? b.comparator == null : comparator.equals(b.comparator));

            } else {
                return false;
            }
        }
        public int hashCode() {
            int result = 0;
            result = HashCodeUtil.hashCode(result, resource);
            result = HashCodeUtil.hashCode(result, classLoader);
            result = HashCodeUtil.hashCode(result, constantsProvider);
            result = HashCodeUtil.hashCode(result, wrapper);
            result = HashCodeUtil.hashCode(result, comparator);
            return result;
        }

        public Bundle<D> clone() {
            log.debug("Cloning bundle " + this);
            try {
                Bundle clone = (Bundle) super.clone();
                clone.constantsProvider = constantsProvider != null ? (HashMap<String,Object>) constantsProvider.clone() : null;
                return clone;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return this;
            }
        }

    }

    /**
     * For testing only.
     */
    public static void main(String argv[]) {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String resource1 = "org.mmbase.datatypes.resources.boolean.onoff";
        String resource2 = "org.mmbase.datatypes.resources.boolean.yesno";
        Locale nl = new Locale("nl");
        Locale en = new Locale("en");
        Locale dk = new Locale("dk");
        Locale eo = new Locale("eo");
        fact.add(nl, "a", "hallo");
        System.out.println("nou " + fact);
        fact.add(new Locale("nl"), "b", "daag");
        fact.add(en, "b", "hello");
        fact.add(en, "a", "good bye");
        fact.addBundle(resource1, null, null, Boolean.class, SortedBundle.NO_COMPARATOR);
        fact.add(nl, "c", "doegg");
        fact.add(dk, 5, "dk");
        fact.add(null, "e", "oi");
        fact.addBundle(resource2, null, null, String.class, SortedBundle.NO_COMPARATOR);

        System.out.println("size: " + fact.size() + " " + fact);
        System.out.println("en" + fact.get(en));
        System.out.println("nl" + fact.get(nl));
        System.out.println("dk" + fact.get(dk));
        System.out.println("eo" + fact.get(eo));

        LocalizedEntryListFactory fact2 = new LocalizedEntryListFactory();
        fact2.addBundle("org.mmbase.datatypes.resources.states", null, org.mmbase.module.builders.MMServers.class, SortedBundle.NO_WRAPPER, SortedBundle.NO_COMPARATOR);

        System.out.println("size: " + fact2.size());
        System.out.println("" + fact2.get(en));
        System.out.println("" + fact2.get(nl));
        Object error = fact2.castKey("ERROR", null);
        System.out.println("ERROR=" + error.getClass().getName() + " " + error);


    }

}
