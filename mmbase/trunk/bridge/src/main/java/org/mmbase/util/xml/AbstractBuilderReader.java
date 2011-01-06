/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import java.util.concurrent.*;
import org.mmbase.datatypes.DataTypes.FieldNotFoundException;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.mmbase.bridge.Field;
import org.mmbase.core.util.DataTypeSetter;
import org.mmbase.core.event.*;
import org.mmbase.datatypes.*;
import org.mmbase.datatypes.util.xml.DataTypeReader;
import org.mmbase.datatypes.util.xml.DependencyException;

import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * Abstraction of {@link BuilderReader} with no dependencies on core classes.
 *
 * @since MMBase 1.9.2
 * @author Case Roole
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public abstract class AbstractBuilderReader<F extends Field> extends DocumentReader {

    /** Public ID of the Builder DTD version 1.0 */
    public static final String PUBLIC_ID_BUILDER_1_0 = "-//MMBase//DTD builder config 1.0//EN";
    /** Public ID of the Builder DTD version 1.1 */
    public static final String PUBLIC_ID_BUILDER_1_1 = "-//MMBase//DTD builder config 1.1//EN";

    // deprecated builder dtds
    private static final String PUBLIC_ID_BUILDER_1_0_FAULT = "-//MMBase/DTD builder config 1.0//EN";
    private static final String PUBLIC_ID_BUILDER_OLD = "/MMBase - builder//";
    private static final String PUBLIC_ID_BUILDER_1_1_FAULT = "-//MMBase/DTD builder config 1.1//EN";

    /** DTD resource filename of the Builder DTD version 1.0 */
    public static final String DTD_BUILDER_1_0 = "builder_1_0.dtd";
    /** DTD resource filename of the Builder DTD version 1.1 */
    public static final String DTD_BUILDER_1_1 = "builder_1_1.dtd";

    /** Public ID of the most recent Builder DTD */
    public static final String PUBLIC_ID_BUILDER = PUBLIC_ID_BUILDER_1_1;
    /** DTD respource filename of the most recent Builder DTD */
    public static final String DTD_BUILDER = DTD_BUILDER_1_1;

    public static final String XSD_BUILDER_2_0       = "builder.xsd";
    public static final String NAMESPACE_BUILDER_2_0 = "http://www.mmbase.org/xmlns/builder";
    public static final String NAMESPACE_BUILDER     = NAMESPACE_BUILDER_2_0;

    private static final Logger log = Logging.getLoggerInstance(AbstractBuilderReader.class);

    /**
     * Register the namespace and XSD used by DataTypeConfigurer
     * This method is called by EntityResolver.
     */
    public static void registerSystemIDs() {
        EntityResolver.registerSystemID(NAMESPACE_BUILDER_2_0 + ".xsd", XSD_BUILDER_2_0, AbstractBuilderReader.class);
    }

    /**
     * Register the Public Ids for DTDs used by BuilderReader
     * This method is called by EntityResolver.
     */
    public static void registerPublicIDs() {
        // various builder dtd versions
        EntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_0, DTD_BUILDER_1_0, AbstractBuilderReader.class);
        EntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_1, DTD_BUILDER_1_1, AbstractBuilderReader.class);
        //EntityResolver.registerPublicID("-//MMBase//DTD builder config 2.0//EN", "builder_2_0.dtd", BuilderReader.class);

        //System.out.println("Registered " + PUBLIC_ID_BUILDER_1_1 + " " +  DTD_BUILDER_1_1);


        // legacy public IDs (wrong, don't use these)
        EntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_0_FAULT, DTD_BUILDER_1_0, AbstractBuilderReader.class);
        EntityResolver.registerPublicID(PUBLIC_ID_BUILDER_OLD, DTD_BUILDER_1_0,       AbstractBuilderReader.class);
        EntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_1_FAULT, DTD_BUILDER_1_1, AbstractBuilderReader.class);
    }


    static {
        org.mmbase.bridge.util.xml.query.QueryReader.registerSystemIDs();
        registerPublicIDs();
        registerSystemIDs();
    }

    public static ResourceLoader getBuilderLoader() {
        return ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders");
    }

    private static List<Runnable> postponedDataTypeDecoders = new CopyOnWriteArrayList<Runnable>();

    static {
        EventManager.getInstance().addEventListener(new SystemEventListener() {
                @Override
                public void notify(SystemEvent se) {
                    if (se instanceof BuildersRead) {
                        int iterationCount = 0;
                        int size;
                        do {
                            if (log.isDebugEnabled()) {
                                log.debug(iterationCount + ": Builders are read now, dealing with " + postponedDataTypeDecoders);
                            }
                            size = postponedDataTypeDecoders.size();
                            for (int i = 0; i < size; i++) {
                                Runnable job = postponedDataTypeDecoders.remove(0);
                                job.run();
                            }
                            iterationCount++;
                        } while (postponedDataTypeDecoders.size() < size);

                    }
                }
                @Override
                public int getWeight() {
                    return 0;
                }
            });
    }

    /**
     * If false, the parent builder could not be resolved.
     * A builder with an unresolved parent is set to 'inactive', regardless of actual status
     * The default value is false, as resolving Inheritance is mandatory when loading builders.
     * @since MMbase-1.6
     */
    protected boolean inheritanceResolved = false;


    /**
     * searchPositions and inputPositions are used to adminstrate 'occupied' positions (from
     * editor/positions), which is used to find defaults if not specified.
     * @since MMBase-1.7
     */
    protected final SortedSet<Integer> searchPositions = new TreeSet<Integer>();
    protected final SortedSet<Integer> inputPositions  = new TreeSet<Integer>();

    protected  AbstractBuilderReader(InputSource source) {
        super(source, true, true, AbstractBuilderReader.class);
    }
    protected AbstractBuilderReader(Document doc) {
        super(doc);
    }

    /**
     * Copy everything from overrides to doc. Note that the resulting document will not obey the
     * correct order of elements, but that does not matter, because the java code in the rest of
     * this class does not depend on that.
     * @param doc The receiving builder xml document. This one will be changed.
     * @param overrides The builder xml document that provided overriding information. This one will only
     * be read.
     * @since MMBase-1.9
     */
    protected static void resolveInheritanceByXML(Document doc, Document overrides) {
        {
            // copy every attribute from root element
            NamedNodeMap nnm = overrides.getDocumentElement().getAttributes();
            for (int i = 0 ; i < nnm.getLength() ; i++) {
                Node item = nnm.item(i);
                doc.getDocumentElement().setAttribute(item.getNodeName(), item.getNodeValue());
            }
        }

        for (String name : new String[] {"class", "searchage", "status"}) {
            // these must entirely replace the tag if present
            Element overrideEl = getElementByPath(overrides.getDocumentElement(), "builder." + name);
            if (overrideEl != null) {
                Element newEl = (Element) doc.importNode(overrideEl, true);
                Element docEl = getElementByPath(doc.getDocumentElement(), "builder." + name);
                if (docEl != null) {
                    doc.getDocumentElement().replaceChild(newEl, docEl);
                } else {
                    doc.getDocumentElement().appendChild(newEl);
                }
            }
        }

        // add and replace names and descriptions, identified by xml:lang
        mergeElementLists(doc, overrides, "names", "singular", "xml:lang", true);
        mergeElementLists(doc, overrides, "names", "plural", "xml:lang", true);
        mergeElementLists(doc, overrides, "descriptions", "description", "xml:lang", true);

        // add and replace properties, identified by name
        mergeElementLists(doc, overrides, "properties", "property", "name", true);

        // add and merge fields, functions, and indices, identified by name
        // (or replace them if the override attribute is set to replace)
        mergeElementLists(doc, overrides, "fieldlist", "field", "name", false);
        mergeElementLists(doc, overrides, "functionlist", "function", "name", false);
        mergeElementLists(doc, overrides, "indexlist", "index", "name", false);

        // log.trace("MERGED XML: " + XMLWriter.write(doc.getDocumentElement()));

    }

    /**
     * Copy a list of elements, identified by an attribute, from overrides to doc.
     * Replace or merge those elements if they already exist in doc under that attribute.
     * Merging an element means either adding or replacing the sub tags of that element.
     * @param doc The receiving builder xml document. This one will be changed.
     * @param overrides The builder xml document that provided overriding information. This one will only
     * be read.
     * @param list The tagname of list element(s) containign the items
     * @param item The tagname of the list item element
     * @param attr The attribute identifying the item (i.e. "name" or "xml:lang")
     * @param replace if <code>true</code>, the item is entirely replaced. If <code>false</code>, it is merged.
     * @since MMBase-1.9.3
     */
    protected static void mergeElementLists(Document doc, Document overrides, String list, String item, String attr, boolean replace) {
        Element docListEl = getElementByPath(doc.getDocumentElement(), "builder." + list);
        for (Element listEl : getChildElements(overrides.getDocumentElement(), list)) {
            if (docListEl == null) {
                docListEl = (Element) doc.importNode(listEl, true);
                doc.getDocumentElement().appendChild(docListEl);
            } else for (Element el : getChildElements(listEl, item)) {
                Element newEl = (Element) doc.importNode(el, true);
                String name = newEl.getAttribute(attr);
                // determine if an item should be replaced or merged
                boolean replaceItem = replace;
                String replaceAttr = newEl.getAttribute("override");
                if (replaceAttr != null) {
                    replaceItem = replaceAttr.equals("replace");
                }
                Element docEl = null;
                if (name != null && !(name.equals(""))) {
                    for(Element orgListEl : getChildElements(doc.getDocumentElement(), list)) {
                        for(Element orgEl : getChildElements(docListEl, item)) {
                            if (name.equals(orgEl.getAttribute(attr))) {
                                docListEl = orgListEl;
                                docEl = orgEl;
                                break;
                            }
                        }
                    }
                }
                if (docEl != null) {
                    if (replaceItem) { // replace an item
                        docListEl.replaceChild(newEl, docEl);
                    } else { // merge an item (iow add and or replace subtags)
                        // merge attributes
                        NamedNodeMap attributes = newEl.getAttributes();
                        for (int i = 0; i < attributes.getLength(); i++) {
                            Attr attribute = (Attr) (attributes.item(i).cloneNode(true));
                            docEl.setAttributeNode(attribute);
                        }
                        // merge element tags
                        for (Element newFel : getChildElements(newEl)) {
                            Element docFel = getElementByPath(docEl, item + "." + newFel.getLocalName());
                            if (docFel != null) {
                                docEl.replaceChild(newFel,docFel);
                            } else {
                                docEl.appendChild(newFel);
                            }
                        }
                    }
                } else {
                    docListEl.appendChild(newEl);
                }
            }
        }
    }

    /**
     * Resolves inheritance.
     * If a builder 'extends' another builder, the parser attempts to
     * retrieve a reference to this builder (using getParentBuilder).
     * Note that if inheritance cannot be resolved, the builder cannot be activated.
     * This method returns false if the builder to extend from is inactive.
     * It throws a RuntimeException is the builder to extend from is not allowed as
     * an parent builder.
     *
     * @since MMBase-1.6
     * @return true if inheritance could be resolved, false if the .
     * @see #isInheritanceResolved()
     * @throws RuntimeException when the builder to extend from is not allowed as parent
     */
    protected abstract boolean resolveInheritance();

    /**
     * Determines if inheritance is resolved.
     * This method returns true if a call to resolveInheritance succeeded.
     * it returns false if resolveInheritance failed (returned false or threw an exception)
     *
     * @since MMBase-1.6
     * @return true if inheritance could be resolved
     * @see #resolveInheritance()
     */
    public boolean isInheritanceResolved() {
        return inheritanceResolved;
    }

    /**
     * Get the status of this builder.
     * Note that if inheritance cannot be resolved, this method always returns "inactive".
     * @return a String decribing the status ("active" or "inactive")
     */
    public String getStatus() {
        if (!inheritanceResolved) {
            return "inactive"; // extends an inactive or non-existing builder
        } else {
            String val = getElementValue("builder.status").toLowerCase();
            if (!val.equals("inactive")) {
                val = "active"; // fix invalid values, including empty value, in which case
                                // assume it extends an active builder (i.e. object)
            }
            return val;
        }
    }

    protected abstract int getParentSearchAge();

    /**
     * Retrieves the Search Age.
     * The search age may be used by editors or search forms to determine
     * the maximum age in days of an object to be searched (limiting the resultset
     * of a search)
     * @return the search age in days
     */
    public int getSearchAge() {
        int val = 30;
        String sval = getElementValue("builder.searchage");
        if (sval.equals("") && hasParent()) {
            sval = "" + getParentSearchAge();
        }
        try {
            val = Integer.parseInt(sval);
        } catch(Exception f) {}
        return val;
    }

    protected abstract String getParentClassName();

    /**
     * Get the class name to use for instantiating this builder.
     * Note that it is possible to specify a short-hand format in
     * the builder configuration file.
     * If only the classname (withoput package name) is given, the classname
     * is expanded to fall into the <code>org.mmbase.module.builders</code> package.
     * @return the classname to use.
     */
    public String getClassName() {
        String val = getElementValue("builder.class");
        if (val.equals("")) {
            val = getElementValue("builder.classfile");// deprecated!! (makes no sense, it is no file)
        }

        if (val.equals("")) {
            if (hasParent()) {
                return getParentClassName();
            } else {
                return "";
            }
        }
        // is it a full name or inside the org.mmbase.module.builders.* path
        int pos = val.indexOf('.');
        if (pos == -1) {
            val = "org.mmbase.module.builders." + val;
        }
        if ("org.mmbase.module.corebuilders.ObjectTypes".equals(val)) {
            log.warn("Specified the removed builder 'ObjectTypes', fall back to TypeDef. You can remove all core-builders from your configuration directory (the ones present in mmbase.jar are ok)");
            val = "org.mmbase.module.corebuilders.TypeDef";
        }
        return val;
    }

    /**
     * Get the datatypes defined for this builder.
     * @param collector A DataTypeCollector to which the newly found DataTypes will be added.
     * @return Returns the data-types of the given collector after adding the ones which are configured
     * @since MMBase-1.8
     */
    public Map<String, BasicDataType<?>> getDataTypes(DataTypeCollector collector) {
        Element element = getElementByPath("builder.datatypes");
        if (element != null) {
            DataTypeReader.readDataTypes(element, collector);
        }
        return collector.getDataTypes();
    }

    /**
     * Get the field definitions of this builder.
     * If applicable, this includes the fields inherited from a parent builder.
     * @return a List of all Fields as CoreField
     * @since MMBase-1.8
     */
    public abstract List<F> getFields();



    /**
     * Determine an integer value from an elements body.
     * Used for the List, Search, and Edit position values.
     * @param elm The element containing the value.
     * @return the parsed integer
     */
    protected int getEditorPos(Element elm) {
        try {
            int val = Integer.parseInt(getElementValue(elm));
            return val;
        } catch(Exception e) {
            return -1;
        }
    }



    protected void decodeDataTypeLater(final DataTypeSetter setter,
                                       final String builder,
                                       final DataTypeCollector collector,
                                       final String fieldName,
                                       final Element fieldElement,
                                       final boolean forceInstance) {
        postponedDataTypeDecoders.add(new Runnable() {
            @Override
            public void run() {
                decodeDataType(setter, builder, collector, fieldName, fieldElement, forceInstance);
            }
            @Override
            public String toString() {
                return "Decoding datatype for " + builder + ":" + fieldName;
            }
        });
        log.debug("Scheduling for later " + postponedDataTypeDecoders);

    }

    /**
     * Determine a data type instance based on the given gui element
     * @param builder the MMObjectBuilder to which the field belongs
     * @param collector The DataTypeCollector of the bulider.
     * @param fieldName the name of the field (used in log messages)
     * @param field     The 'field' element of the builder xml
     * @param type      The database type of the field
     * @param listItemType If the database type is a List, there is also a type of its element
     * @param forceInstance If true, it will never return <code>null</code>, but will return (a clone) of the DataType associated with the database type.
     * @since MMBase-1.8
     */
    protected void decodeDataType(final DataTypeSetter setter,
                                  final String builder,
                                  final DataTypeCollector collector,
                                  final String fieldName,
                                  final Element fieldElement,
                                  final boolean forceInstance) {
        BasicDataType baseDataType = null;
        final int type = setter.getType();
        final int listItemType = setter.getListItemType();
        if (type == Field.TYPE_LIST) {
            baseDataType = DataTypes.getListDataType(listItemType);
            log.debug("Type was list, found base datatype " + baseDataType);
        } else if (type != Field.TYPE_UNKNOWN) {
            baseDataType = DataTypes.getDataType(type);
            if (baseDataType == null) {
                log.error("Not found a baseDataType for " +  type);
            }
        }
        BasicDataType dataType = null;
        Element guiTypeElement = getElementByPath(fieldElement, "field.gui.guitype");

        // deprecated tag 'type'
        if (guiTypeElement == null) {
            guiTypeElement = getElementByPath(fieldElement, "field.gui.type");
        }

        // Backwards compatible 'guitype' support
        if (guiTypeElement != null && collector != null) {
            if (baseDataType == null) {
                throw new IllegalArgumentException(getDocument().getDocumentURI() + ": No type defined in field " + fieldName);
            }
            String guiType = getElementValue(guiTypeElement);
            if (!guiType.equals("")) {
                if (guiType.indexOf('.') != -1) {
                    // apparently, this is a class path, which means it is probably an enumeration
                    // (if not, what else?)
                    dataType = (BasicDataType) baseDataType.clone();
                    dataType.getEnumerationFactory().addBundle(guiType, getClass().getClassLoader(), null, dataType.getTypeAsClass(), null);
                    dataType.getEnumerationRestriction().setEnforceStrength(DataType.ENFORCE_NEVER);
                } else {
                    // check for builder names when the type is NODE
                    String enumerationBuilder = null;
                    // The guitype is deprecated. Normally coincides with datatype's id.
                    // The following are exceptions:
                    // 'string' is surrogated with the datatype 'line'.
                    if ("string".equals(guiType)) {
                        guiType = "line";
                        if (log.isDebugEnabled()) {
                            log.debug("Converted deprecated guitype 'string' for field " + (builder != null ? builder + "."  : "") + fieldName + " with datatype 'line'.");
                        }
                    } else
                    // 'eventtime' is surrogated with the datatype 'datetime'.
                    if ("eventtime".equals(guiType)) {
                        guiType = "datetime";
                        if (log.isDebugEnabled()) {
                            log.debug("Converted deprecated guitype 'eventtime' for field " + (builder != null ? builder + "."  : "") + fieldName + " with datatype 'datetime'.");
                        }
                    } else
                    // 'relativetime' is surrogated with the datatype 'line'.
                    if ("relativetime".equals(guiType)) {
                        guiType = "duration";
                        if (log.isDebugEnabled()) {
                            log.debug("Converted deprecated guitype 'relativetime' for field " + (builder != null ? builder + "."  : "") + fieldName + " with datatype 'duration'.");
                        }
                    } else if (type == Field.TYPE_NODE) {
                        if (guiType == null) {
                            if (log.isDebugEnabled()) log.debug("Gui type of NODE field '" + fieldName + "' is null");
                        } else {
                            enumerationBuilder = guiType;
                            if (enumerationBuilder == null) {
                                if (log.isDebugEnabled()) log.debug("Gui type of NODE field is '" + fieldName + "'not a known builder");
                            }
                        }
                    }
                    if (enumerationBuilder != null) {
                        //  Create a query element of the format:
                        //  <query type="[buildername]" xmlns="http://www.mmbase.org/xmlns/searchquery" />
                        // and add it to the enumerationfactory using addQuery()
                        Element queryElement = guiTypeElement.getOwnerDocument().createElementNS("http://www.mmbase.org/xmlns/searchquery", "query");
                        queryElement.setAttribute("type", enumerationBuilder);
                        dataType = (BasicDataType) baseDataType.clone();
                        Document queryDocument = DocumentReader.toDocument(queryElement);
                        dataType.getEnumerationFactory().addQuery(LocalizedString.getLocale(queryElement), queryDocument);
                        dataType.getEnumerationRestriction().setEnforceStrength(DataType.ENFORCE_NEVER);
                    } else {
                        dataType = collector.getDataTypeInstance(guiType, baseDataType);
                        if (dataType == null) {
                            log.warn("Could not find data type for " + baseDataType + " / " + guiType + " for builder: '" + (builder == null ? "NULL" : builder + "'"));

                        }
                    }
                }
            }
        }

        Element dataTypeElement = getElementByPath(fieldElement, "field.datatype");

        if (dataTypeElement != null) {
            if (dataType != null) {
                log.warn("Using both deprecated 'gui/guitype' and 'datatype' subelements in field tag for field '" + fieldName + "', ignoring the first one.");
            }
            BasicDataType requestedBaseDataType; // pointer to the original field's datatype which will be used as a base.
            String fieldAttribute = dataTypeElement.getAttribute("field");
            if (! fieldAttribute.equals("")) {
                try {
                    requestedBaseDataType = DataTypes.getDataTypeForFieldAttribute(fieldAttribute);
                } catch (FieldNotFoundException ex) {
                    decodeDataTypeLater(setter,
                                        builder,
                                        collector,
                                        fieldName,
                                        fieldElement,
                                        forceInstance);
                    log.service(" " + ex.getMessage() + " Will try again later.");
                    return;
                }
            } else {
                String base = dataTypeElement.getAttribute("base");
                if (base.equals("")) {
                    if (log.isDebugEnabled()) {
                        log.debug("No base defined, using '" + baseDataType + "'");
                    }
                    if (baseDataType == null) {
                        throw new IllegalArgumentException(getDocument().getDocumentURI() + ":'" +
                                                           fieldName + "'. No base datatype given, and no field type defined");
                    }
                    requestedBaseDataType = baseDataType;
                } else {
                    requestedBaseDataType = collector == null ? null : collector.getDataType(base, true);
                    if (requestedBaseDataType == null) {
                        log.error("Could not find base datatype for '" + base + "' falling back to " + baseDataType + " in builder '" + (builder == null ?  "NULL" : builder) + "'");
                        requestedBaseDataType = baseDataType;
                    }
                }
            }
            try {
                dataType = DataTypeReader.readDataType(dataTypeElement, requestedBaseDataType, collector).dataType;
            } catch (DependencyException de) {
                dataType = de.fallback();
            }
            if (log.isDebugEnabled()) log.debug("Found datatype " + dataType + " for field " + fieldName);
        }

        // try to resolve any issues where the datatype differs from the database type
        if (dataType != null && baseDataType != null && !baseDataType.getClass().isAssignableFrom(dataType.getClass())) {
            // the thus configured datatype is not compatible with the database type.
            // Fix that as good as possible:
            BasicDataType newDataType = (BasicDataType) dataType.clone();
            newDataType.inherit(baseDataType);
            if (log.isDebugEnabled()) log.debug("" + dataType + " in '" + getSystemId() + "' field " + fieldName + " is not compatible with " + baseDataType + ". Cloning and inheriting to support gracefull fall backs -> " + newDataType);
            dataType = newDataType;
        }

        if (dataType == null && forceInstance) {
            // DataType is null if no data type element was found
            if (baseDataType == null) {
                throw new IllegalArgumentException("No datatype element given, and no type ('" + type + "') defined");
            }
            dataType = (BasicDataType) baseDataType.clone(""); // clone with empty id
        }

        setter.set(dataType);
    }




    protected abstract Map<String, String> getParentProperties();

    /**
     * Get the properties of this builder
     * @return the properties in a Map (as name-value pairs)
     */
    public Map<String,String> getProperties() {
        Map<String,String> results = new HashMap<String,String>();
        if (hasParent()) {
            Map<String,String> parentparams = getParentProperties();
            if (parentparams != null) {
                results.putAll(parentparams);
            }
        }
        for (Element p : getChildElements("builder.properties", "property")) {
            String name = getElementAttributeValue(p, "name");
            String value = getElementValue(p);
            results.put(name, value);
        }
        return results;
    }


    /**
     * Get the descriptions of this module.
     * @return the descriptions as a LocalizedString
     */
    public LocalizedString getLocalizedDescription(LocalizedString description) {
        description.fillFromXml("description", getElementByPath("builder.descriptions"));
        return description;
    }

    /**
     * Get the (gui) names of this module.
     * @return the names as a LocalizedString
     */
    public LocalizedString getLocalizedSingularName(LocalizedString guiName) {
        guiName.fillFromXml("singular", getElementByPath("builder.names"));
        return guiName;
    }

    /**
     * Get the (gui) names of this module.
     * @return the names as a LocalizedString
     */
    public LocalizedString getLocalizedPluralName(LocalizedString guiName) {
        guiName.fillFromXml("plural", getElementByPath("builder.names"));
        return guiName;
    }

    /**
     * Get the descriptions of this builder
     * @deprecated use getLocalizedDescription()
     * @return the descriptions in a Map, accessible by language
     */
    public Map<String,String> getDescriptions() {
        Map<String,String> results = new HashMap<String,String>();
        for (Element desc : getChildElements("builder.descriptions","description")) {
            String lang = getElementAttributeValue(desc,"xml:lang");
            results.put(lang,getElementValue(desc));
        }
        return results;
    }

    /**
     * Get the plural names of this builder
     * @deprecated use getLocalizedPluralName()
     * @return the plural names in a Map, accessible by language
     */
    public Map<String,String> getPluralNames() {
        Map<String,String> results = new HashMap<String, String>();
        for (Element name : getChildElements("builder.names", "plural")) {
            String lang = getElementAttributeValue(name,"xml:lang");
            results.put(lang,getElementValue(name));
        }
        return results;
    }

    /**
     * Get the singular (GUI) names of this builder
     * @deprecated use getLocalizedSingularName()
     * @return the singular names in a Map, accessible by language
     */
    public Map<String,String> getSingularNames() {
        Map<String, String> results = new HashMap<String,String>();
        for (Element name : getChildElements("builder.names","singular")) {
            String lang = getElementAttributeValue(name,"xml:lang");
            results.put(lang,getElementValue(name));
        }
        return results;
    }


    /**
     * Get the name of the builder that this builder extends
     * @since MMBase-1.8
     * @return the name of the parent builder
     */
    public String getExtends() {
        return getElementAttributeValue("builder", "extends");
    }

    /**
     * Get the name of the builder that this builder extends
     * @since MMBase-1.9
     * @return the name of the parent builder
     */
    public String getName() {
        String n = getElementAttributeValue("builder", "name");
        return n;

    }

    protected abstract boolean hasParent();

    protected abstract int getParentVersion();

    /**
     * Retrieve the (major) version number of this builder
     * @since MMBase-1.8
     * @return the version as an integer.
     */
    public int getVersion() {
        String version = document.getDocumentElement().getAttribute("version");
        if (version.equals("") && hasParent()) {
            return getParentVersion();
        } else {
            int n = 0;
            if (!version.equals("")) {
                try {
                    n = Integer.parseInt(version);
                } catch (Exception f) {}
            }
            return n;
        }
    }

    protected abstract String getParentMaintainer();

    /**
     * Retrieve the name of the maintainer of this builder
     * @since MMBase-1.8
     * @return the name fo the maintainer as a String
     */
    public String getMaintainer() {
        String maintainer = getElementAttributeValue("builder", "maintainer");
        if (maintainer.equals("")) {
            if (hasParent()) {
                maintainer = getParentMaintainer();
            } else {
                maintainer = "mmbase.org";
            }
        }
        return maintainer;
    }

    /**
     * {@inheritDoc}
     * @since MMBase-1.7
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractBuilderReader) {
            AbstractBuilderReader b = (AbstractBuilderReader) o;
            List<F> fields = getFields();
            List<F> otherFields = b.getFields();
            return
                fields.equals(otherFields) &&
                getMaintainer().equals(b.getMaintainer()) &&
                getVersion() == b.getVersion() &&
                getExtends().equals(b.getExtends()) &&
                getSingularNames().equals(b.getSingularNames()) &&
                getPluralNames().equals(b.getPluralNames()) &&
                getDescriptions().equals(b.getDescriptions()) &&
                getProperties().equals(b.getProperties()) &&
                getClassName().equals(b.getClassName()) &&
                getName().equals(b.getName())
                ;
        } else {
            return false;
        }
    }

    /**
     * @since MMBase-2.0
     */
    public Set<Function<?>> getFunctions(final Object builder) {
       Map<String, Function<?>> results = new HashMap<String, Function<?>>();
        for (Element functionList : getChildElements("builder","functionlist")) {
            for (Element functionElement : getChildElements(functionList,"function")) {
                final String functionName = functionElement.getAttribute("name");
                String providerKey        = functionElement.getAttribute("key");
                String functionClass      = getNodeTextValue(getElementByPath(functionElement, "function.class"));
                try {


                    Function function;
                    log.debug("Using function class '" + functionClass + "'");
                    final Class claz = Class.forName(functionClass);
                    if (Function.class.isAssignableFrom(claz)) {
                        if (!providerKey.equals("")) {
                            log.warn("Specified a key attribute for a Function " + claz + " in " + getSystemId() + ", this makes only sense for FunctionProviders.");
                        }
                        function = (Function) claz.newInstance();
                    } else if (FunctionProvider.class.isAssignableFrom(claz)) {
                        if ("".equals(providerKey)) providerKey = functionName;
                        if ("".equals(providerKey)) {
                            log.error("FunctionProvider " + claz + " specified in " + getSystemId() + " without key or name");
                            continue;
                        }
                        FunctionProvider provider = (FunctionProvider) claz.newInstance();
                        function = provider.getFunction(providerKey);
                        if (function == null) {
                            log.error("Function provider " + provider + "has no function '" + providerKey + "'");
                            continue;
                        }
                    } else {
                        if ("".equals(providerKey)) providerKey = functionName;
                        if ("".equals(providerKey)) {
                            log.error("Speficied class " + claz + " in " + getSystemId() + "/functionslist/function is not a Function or FunctionProvider and can not be wrapped in a BeanFunction, because neither key nor name attribute were specified.");
                            continue;
                        }
                        java.lang.reflect.Method method = MethodFunction.getMethod(claz, providerKey);
                        if (method == null) {
                            log.error("Could not find  method '" + providerKey + "' in " + claz);
                            continue;
                        } else {
                            if (method.getParameterTypes().length == 0) {
                                function = BeanFunction.getFunction(claz, providerKey, new BeanFunction.Producer() {
                                    @Override
                                    public Object getInstance() {
                                        try {
                                            return BeanFunction.getInstance(claz, builder);
                                        } catch (Exception e) {
                                            log.error(e.getMessage(), e);
                                            return null;
                                        }
                                    }
                                    @Override
                                    public String toString() {
                                        return "" + claz.getName() + "." + builder;
                                    }
                                });
                            } else {
                                if (method.getClass().isInstance(builder)) {
                                    function = MethodFunction.getFunction(method, providerKey, builder);
                                } else {
                                    function = MethodFunction.getFunction(method, providerKey);
                                }
                            }
                        }
                    }
                    if (! functionName.equals("") && ! function.getName().equals(functionName)) {
                        log.debug("Wrapping " + function.getName() + " to " + functionName);
                        function = new WrappedFunction(function) {
                                @Override
                                public String getName() {
                                    return functionName;
                                }
                            };
                    }

                    String key = function.getName();
                    Function existing = results.get(key);

                    if (existing != null) {
                        log.info("Function " + key + " already defined, will combine it");
                        CombinedFunction cf;
                        if (existing instanceof CombinedFunction) {
                            cf = (CombinedFunction) existing;
                        } else {
                            cf = new CombinedFunction(key);
                            cf.addFunction(existing);
                        }
                        cf.addFunction(function);
                        function = cf;
                    }

                    NodeFunction nf = NodeFunction.wrap(function);
                    if (nf != null) function = nf;

                    results.put(key, function);
                    log.debug("functions are now: " + results);
                } catch (ClassNotFoundException cnfe) {
                    log.warn(functionClass + " " + cnfe.getClass() + " " + getSystemId() + " '" + cnfe.getMessage() + "'");
                } catch (Throwable e) {
                    log.error(e.getClass() + " " + getSystemId() + " " + e.getMessage(), e);
                }
            }
        }
        Set<Function<?>> r = new HashSet<Function<?>>();
        r.addAll(results.values());
        log.debug("Found functions " + r);
        return r;


    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.inheritanceResolved ? 1 : 0);
        hash = 53 * hash + (this.searchPositions != null ? this.searchPositions.hashCode() : 0);
        hash = 53 * hash + (this.inputPositions != null ? this.inputPositions.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + getSystemId();
    }




}

