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
import org.mmbase.util.functions.Function;
import org.mmbase.util.functions.Parameters;
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
        @Override
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
        @Override
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
        return bundles.isEmpty() && fallBack.isEmpty() && !usesCloud;
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

        LocalizedEntry local = localized.get(locale);
        if (local == null) {
            Locale loc = locale;
            loc = loc == null ? null : LocalizedString.degrade(loc, locale);
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
        if (locale != null && locale.getVariant() != null && !"".equals(locale.getVariant())) {
            Locale l = new Locale(locale.getLanguage(), locale.getCountry());
            if (!localized.containsKey(l)) {
                add(l, entry);
            }
        }

        // If this locale with country is added, but the parent locale (only language) was not yet in
        // the map, we first add the parent language locale.
        if (locale != null && locale.getCountry() != null && !"".equals(locale.getCountry())) {
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
                    if (locale != null && cloud != null) {
                        cloud.setLocale(locale);
                    }
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
        final List<Map.Entry<C, ? extends CharSequence>> lazy = getLazy(locale, c, node, field);
        return new AbstractSequentialList<Map.Entry<C, String>> () {
            @Override
            public int size() {
                return lazy.size();
            }
            @Override
            public ListIterator<Map.Entry<C, String>> listIterator(final int index) {
                final ListIterator<Map.Entry<C, ? extends CharSequence>> i = lazy.listIterator(index);
                return new ListIterator<Map.Entry<C, String>>() {

                    public boolean hasNext() {
                        return i.hasNext();
                    }
                    protected Map.Entry<C, String> get(Map.Entry<C, ? extends CharSequence> entry) {
                        if (entry.getValue() instanceof String) {
                            return (Map.Entry<C, String>) entry;
                        } else {
                            return new Entry<C, String>(entry.getKey(), entry.getValue().toString());
                        }
                    }

                    public Map.Entry<C, String> next() {
                        return get(i.next());
                    }

                    public boolean hasPrevious() {
                        return i.hasPrevious();
                    }

                    public Map.Entry<C, String> previous() {
                        return get(i.previous());
                    }

                    public int nextIndex() {
                        return i.nextIndex();
                    }

                    public int previousIndex() {
                        return i.previousIndex();
                    }

                    public void remove() {
                        i.remove();
                    }

                    public void set(Map.Entry<C, String> e) {
                        i.set(e);
                    }

                    public void add(Map.Entry<C, String> e) {
                        i.add(e);
                    }
                };
            }
        };
    }

    /**
     * @since MMBase-1.9.6
     */
    public List<C> getKeys(final Locale locale, Cloud c, final org.mmbase.bridge.Node node, final Field field) {
        final List<Map.Entry<C, ? extends CharSequence>> lazy = getLazy(locale, c, node, field);
        return new AbstractSequentialList<C> () {
            public int size() {
                return lazy.size();
            }
            public ListIterator<C> listIterator(final int index) {
                final ListIterator<Map.Entry<C, ? extends CharSequence>> i = lazy.listIterator(index);
                return new ListIterator<C>() {

                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    public C next() {
                        return i.next().getKey();
                    }

                    public boolean hasPrevious() {
                        return i.hasPrevious();
                    }

                    public C previous() {
                        return i.previous().getKey();
                    }

                    public int nextIndex() {
                        return i.nextIndex();
                    }

                    public int previousIndex() {
                        return i.previousIndex();
                    }

                    public void remove() {
                        i.remove();
                    }

                    public void set(C e) {
                        throw new UnsupportedOperationException("Not supported.");
                    }

                    public void add(C e) {
                        throw new UnsupportedOperationException("Not supported.");
                    }
                };
            }
        };
    }

    /**
     * @since MMBase-1.9.6
     */
    protected ChainedIterator getIterator(Locale useLocale) {
        ChainedIterator iterator = new ChainedIterator();
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
            loc = localized.get(null);
        }
        if (loc == null) {

            iterator.addIterator(bundles.iterator());
            iterator.addIterator(fallBack.iterator());
        } else {
            iterator.addIterator(loc.entries.iterator());
            iterator.addIterator(loc.unusedKeys.iterator());
        }
        return iterator;
    }

    /**
     * Return the {@link Query}'s that define this enumeration factory.
     * @throws IllegalStateException If this factory is not only defined by queries.
     * @since MMBase-1.9.6
     */
    public List<Query> getQueries(final Cloud cloud, final org.mmbase.bridge.Node node, final Field field) throws  org.mmbase.storage.search.SearchQueryException  {
        List<Query> result = new ArrayList<Query>();
        ChainedIterator i = getIterator(cloud.getLocale());
        while (i.hasNext()) {
            Object candidate = i.next();
            if (candidate instanceof DocumentSerializable) {
                Element element = ((DocumentSerializable) candidate).getDocument().getDocumentElement();
                final QueryConfigurer qc = new QueryConfigurer();
                if (node != null) {
                    qc.variables.put("_node", node.getNumber());
                }
                final Query query = QueryReader.parseQuery(element, qc, cloud, null).query;
                result.add(query);
            } else {
                throw new IllegalStateException();
            }
        }
        return result;
    }


    /**
     * @since MMBase-1.9.6
     */
    protected List<Map.Entry<C, ? extends CharSequence>> getLazy(final Locale locale, Cloud c, final org.mmbase.bridge.Node node, final Field field) {
        if (c == null) {
            if (node != null) {
                c = node.getCloud();
            } else if (field != null) {
                try {
                    c = field.getNodeManager().getCloud();
                } catch (UnsupportedOperationException use) {
                    // stupid CoreFields
                }
            }
        }
        final Cloud cloud = c;
        return new AbstractSequentialList<Map.Entry<C, ? extends CharSequence>> () {

            @Override
            public int size() {
                return LocalizedEntryListFactory.this.size(cloud);
            }
            @Override
            public ListIterator<Map.Entry<C, String>> listIterator(final int index) {
                return new ListIterator<Map.Entry<C, String>>() {
                        int   i = -1;
                        Locale useLocale = locale;
                        Cloud useCloud = cloud;

                        {
                            if (useLocale == null) {
                                useLocale = useCloud != null ? useCloud.getLocale() : null;
                            }
                            log.debug("using locale " + useLocale);
                        }
                        private final ChainedIterator iterator;
                        private Iterator<Map.Entry<C, ? extends CharSequence>> subIterator = null;
                        private Map.Entry<C, ? extends CharSequence> next = null;

                        {
                            iterator = getIterator(useLocale);
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
                                        subIterator = new Iterator<Map.Entry<C, ? extends CharSequence>>() {
                                            final NodeIterator nodeIterator = list.nodeIterator();
                                            @Override
                                            public boolean hasNext() {
                                                return nodeIterator.hasNext();
                                            }
                                            @Override
                                            public Map.Entry<C, String> next() {
                                                org.mmbase.bridge.Node next = nodeIterator.nextNode();
                                                if (query instanceof NodeQuery) {
                                                    if (next == null) {
                                                        return new Entry<C, String>((C) next, "NULL_NODE");
                                                    } else {
                                                        try {
                                                            Function function = next.getFunction("gui");
                                                            Parameters params = function.createParameters();
                                                            params.set("locale", locale);
                                                            String gui = function.getFunctionValue(params).toString();
                                                            return new Entry<C, String>((C) next, gui);
                                                        } catch (NotFoundException nfe) {
                                                            if (node == null) {
                                                                return new Entry<C, String>((C) next, "-1");
                                                            } else {
                                                                return new Entry<C, String>((C) next, "" + node.getNumber());
                                                            }

                                                        }
                                                    }
                                                } else {
                                                    String alias = Queries.getFieldAlias(query.getFields().get(0));
                                                    log.debug("using field " + alias);
                                                    if (query.getFields().size() == 1) {
                                                        return new Entry<C, String>((C) next.getValue(alias),
                                                                next.getStringValue(alias));
                                                    } else {
                                                        StringBuilder buf = new StringBuilder();
                                                        for (int i = 1; i < query.getFields().size(); i++) {
                                                            String a = Queries.getFieldAlias(query.getFields().get(i));
                                                            if (buf.length() > 0) {
                                                                buf.append(" ");
                                                            }
                                                            buf.append(next.getStringValue(a));
                                                        }
                                                        return new Entry<C, String>((C) next.getValue(alias),
                                                                buf.toString());
                                                    }
                                                }
                                            }
                                            @Override
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
                                    next = new Entry(candidate, Casting.toString(candidate));
                                }
                            }
                    }

                    @Override
                    public boolean hasNext() {
                        return next != null || subIterator != null;
                    }
                    @Override
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
                    @Override
                    public int nextIndex() {
                        return i;
                    }
                    @Override
                    public int previousIndex() {
                        return i - 1;
                    }
                    @Override
                    public boolean hasPrevious() {
                        // TODO
                        throw new UnsupportedOperationException();
                    }
                    @Override
                    public Map.Entry<C, String> previous() {
                        // TODO
                        throw new UnsupportedOperationException();
                    }
                    // this is why we hate java:


                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    @Override
                    public void add(Map.Entry<C, String> o) {
                        throw new UnsupportedOperationException();
                    }
                    @Override
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
        if (cloud == null) {
            cloud = getCloud(LocalizedString.getDefault());
        }
        int queriesSize = size();
        Locale locale = cloud == null ? LocalizedString.getDefault() : cloud.getLocale();
        LocalizedEntry localizedList = localized.get(locale);
        if (localizedList == null) {
            locale = LocalizedString.getDefault();
            localizedList = localized.get(null);
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
                } else if (o instanceof Map.Entry) {
                    // already in size();
                } else {
                    log.service("Unanticipated entry " + o.getClass() + " " + o);
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
        try {
            return castKeyOrException(key, cloud);
        } catch (IllegalArgumentException ia) {
            return key;
        }

    }
    /**
     * @since MMBase-2.0
     */
    public Object castKeyOrException(final Object key, final Cloud cloud) throws IllegalArgumentException {
        if (key == null) {
            return null;
        }
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
                try {
                    if (b.resource != null && b.getMap(null).containsKey(string)) {
                        return nk;
                    }
                } catch (ClassCastException cce) {
                    // that means no
                }
            }
        }
        if (usesCloud && cloud != null) {
            if (string == null) {
                string = Casting.toString(key);
            }
            if (cloud.hasNode(string)) {
                return cloud.getNode(string);
            }
        }
        if (key instanceof SortedBundle.ValueWrapper) {
            return ((SortedBundle.ValueWrapper) key).getKey();
        }
        throw new IllegalArgumentException("'" + key.getClass() + Casting.toString(key) + "' cannot be cast to one of the keys of " + this + " (using" + cloud + " " + usesCloud + ")");
    }

    @Override
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
                    String javaConstants = entryElement.getAttribute("javaconstants");
                    if (! javaConstants.equals("")) {
                        try {
                            Class constantsClass = Class.forName(javaConstants);
                            for (Map.Entry<String, Object> entry : SortedBundle.getConstantsProvider(constantsClass).entrySet()) {
                                add(null, entry.getKey(), (Serializable) entry.getValue());
                            }
                        } catch (Exception e) {
                            log.error(e);
                        }

                    } else {
                        throw new IllegalArgumentException("no 'value', 'basename' or 'javaconstants' attribute on enumeration entry element " + org.mmbase.util.xml.XMLWriter.write(entryElement));
                    }
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

    @Override
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

        Map<D, String> getMap(Locale loc) {
            return  SortedBundle.getResource(resource, loc, classLoader, constantsProvider, wrapper, comparator);
        }

        /**
         * Collection of Map.Entry's
         */
        Collection<Map.Entry<D, String>> get(Locale loc) throws MissingResourceException {
            try {
                Map<D, String> resourceMap = getMap(loc);
                return resourceMap.entrySet();
            } catch (IllegalArgumentException iae) {
                log.error(iae);
                return Collections.emptySet();
            }
        }


        @Override
        public String toString() {
            return resource + " " + constantsProvider + " " + wrapper + " " + comparator;
        }
        @Override
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
        @Override
        public int hashCode() {
            int result = 0;
            result = HashCodeUtil.hashCode(result, resource);
            result = HashCodeUtil.hashCode(result, classLoader);
            result = HashCodeUtil.hashCode(result, constantsProvider);
            result = HashCodeUtil.hashCode(result, wrapper);
            result = HashCodeUtil.hashCode(result, comparator);
            return result;
        }

        @Override
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


}
