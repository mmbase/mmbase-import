/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import java.util.regex.Pattern;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeManager;
import org.mmbase.core.CoreField;
import org.mmbase.core.util.Fields;
import org.mmbase.datatypes.*;
import org.mmbase.datatypes.util.xml.DataTypeReader;
import org.mmbase.datatypes.util.xml.DependencyException;
import org.mmbase.storage.util.Index;

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
 * @version $Id: BuilderReader.java 35350 2009-05-21 15:59:15Z michiel $
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

    private static final Logger log = Logging.getLoggerInstance(BuilderReader.class);

    /**
     * Register the namespace and XSD used by DataTypeConfigurer
     * This method is called by EntityResolver.
     */
    public static void registerSystemIDs() {
        EntityResolver.registerSystemID(NAMESPACE_BUILDER_2_0 + ".xsd", XSD_BUILDER_2_0, BuilderReader.class);
    }

    /**
     * Register the Public Ids for DTDs used by BuilderReader
     * This method is called by EntityResolver.
     */
    public static void registerPublicIDs() {
        // various builder dtd versions
        EntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_0, DTD_BUILDER_1_0, BuilderReader.class);
        EntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_1, DTD_BUILDER_1_1, BuilderReader.class);
        //EntityResolver.registerPublicID("-//MMBase//DTD builder config 2.0//EN", "builder_2_0.dtd", BuilderReader.class);

        // legacy public IDs (wrong, don't use these)
        EntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_0_FAULT, DTD_BUILDER_1_0, BuilderReader.class);
        EntityResolver.registerPublicID(PUBLIC_ID_BUILDER_OLD, DTD_BUILDER_1_0,       BuilderReader.class);
        EntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_1_FAULT, DTD_BUILDER_1_1, BuilderReader.class);
    }


    public static ResourceLoader getBuilderLoader() {
        return ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders");
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
    protected SortedSet<Integer> searchPositions = new TreeSet<Integer>();
    protected SortedSet<Integer> inputPositions  = new TreeSet<Integer>();

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

        for (String list : new String[] {"names", "descriptions", "properties"}) {
            // if these are found, simply all sub-elements must be added.

            List<Element> elementList = getChildElements(doc.getDocumentElement(), list);
            Element element;
            if (elementList.size() == 0) {
                element = doc.createElement(list);
                doc.getDocumentElement().appendChild(element);
            } else {
                element = elementList.get(elementList.size() - 1);
            }
            for (Element overridesList : getChildElements(overrides.getDocumentElement(), list)) {
                for (Element e : getChildElements(overridesList, "*")) {
                    Element newE = (Element) doc.importNode(e, true);
                    element.appendChild(newE);
                }
            }
        }

        for (String list : new String[] {"fieldlist", "functionlist", "indexlist"}) {
            // if these are found, they simply must be added too.

            for(Element el : getChildElements(overrides.getDocumentElement(), list)) {
                Element newEl = (Element) doc.importNode(el, true);
                doc.getDocumentElement().appendChild(newEl);
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
     * Detremines if inheritance is resolved.
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
    /**
     * Alter a specified, named FieldDef object using information obtained from the buidler configuration.
     * Only GUI information is retrieved and stored (name and type of the field sg=hould already be specified).
     * @since MMBase-1.6
     * @param field The element containing the field information according to the buidler xml format
     * @param def The field definition to alter
     */
    protected void decodeFieldDef(Element field, CoreField def, DataTypeCollector collector) {
        // Gui
        Element descriptions = getElementByPath(field, "field.descriptions");
        if (descriptions != null) {
            def.getLocalizedDescription().fillFromXml("description", descriptions);
        }

        // XXX: deprecated tag 'gui'
        Element gui = getElementByPath(field, "field.gui");
        if (gui != null) {
            def.getLocalizedGUIName().fillFromXml("guiname", gui);
            // XXX: even more deprecated
            def.getLocalizedGUIName().fillFromXml("name", gui);
        }

        // Editor
        Element editorpos = getElementByPath(field, "field.editor.positions.input");
        if (editorpos != null) {
            int inputPos = getEditorPos(editorpos);
            if (inputPos > -1) inputPositions.add(inputPos);
            def.setEditPosition(inputPos);
        } else {
            // if not specified, use lowest 'free' position.
            int i = 1;
            while (inputPositions.contains(i)) {
                ++i;
            }
            inputPositions.add(i);
            def.setEditPosition(i);

        }
        editorpos = getElementByPath(field, "field.editor.positions.list");
        if (editorpos != null) {
            def.setListPosition(getEditorPos(editorpos));
        }
        editorpos = getElementByPath(field, "field.editor.positions.search");
        if (editorpos != null) {
            int searchPos = getEditorPos(editorpos);
            if (searchPos > -1) searchPositions.add(searchPos);
            def.setSearchPosition(searchPos);
        } else {
            // if not specified, use lowest 'free' position, unless, db-type is BINARY (non-sensical searching on that)
            // or the field is not in storage at all (search cannot be performed by database)
            if (def.getType() != Field.TYPE_BINARY && !def.isVirtual()) {
                int i = 1;
                while (searchPositions.contains(i)) {
                    ++i;
                }
                searchPositions.add(i);
                def.setSearchPosition(i);
            } else {
                def.setSearchPosition(-1);
            }
        }
    }

    /**
     * Determine a data type instance based on the given gui element
     * @todo  'guitype' may become deprecated in favour of the 'datatype' element
     * @param builder the MMObjectBuilder to which the field belongs
     * @param collector The DataTypeCollector of the bulider.
     * @param fieldName the name of the field (used in log messages)
     * @param field     The 'field' element of the builder xml
     * @param type      The database type of the field
     * @param listItemType If the database type is a List, there is also a type of its element
     * @param forceInstance If true, it will never return <code>null</code>, but will return (a clone) of the DataType associated with the database type.
     * @since MMBase-1.8
     */
    protected DataType decodeDataType(final String builder, final DataTypeCollector collector, final String fieldName, final Element field, final int type, final int listItemType, final boolean forceInstance) {
        BasicDataType baseDataType = null;
        if (type == Field.TYPE_LIST) {
            baseDataType = DataTypes.getListDataType(listItemType);
        } else if (type != Field.TYPE_UNKNOWN) {
            baseDataType = DataTypes.getDataType(type);
            if (baseDataType == null) {
                log.error("Not found a baseDataType for " +  type);
            }
        }
        BasicDataType dataType = null;
        Element guiTypeElement = getElementByPath(field, "field.gui.guitype");

        // XXX: deprecated tag 'type'
        if (guiTypeElement == null) {
            guiTypeElement = getElementByPath(field, "field.gui.type");
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

        Element dataTypeElement = getElementByPath(field, "field.datatype");

        if (dataTypeElement != null) {
            if (dataType != null) {
                log.warn("Using both deprecated 'gui/guitype' and 'datatype' subelements in field tag for field '" + fieldName + "', ignoring the first one.");
            }
            BasicDataType requestedBaseDataType; // pointer to the original field's datatype which will be used as a base.
            String base = dataTypeElement.getAttribute("base");
            if (base.equals("")) {
                if (log.isDebugEnabled()) {
                    log.debug("No base defined, using '" + baseDataType + "'");
                }
                if (baseDataType == null) {
                    throw new IllegalArgumentException(getDocument().getDocumentURI() + ":'" + fieldName + "'. No base datatype given, and no field type defined");
                }
                requestedBaseDataType = baseDataType;
            } else {
                requestedBaseDataType = collector == null ? null : collector.getDataType(base, true);
                if (requestedBaseDataType == null) {
                    log.error("Could not find base datatype for '" + base + "' falling back to " + baseDataType + " in builder '" + (builder == null ?  "NULL" : builder) + "'");
                    requestedBaseDataType = baseDataType;
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

        return dataType;
    }



    /**
     * @since MMBase-1.8.6
     */
    protected void decodeFieldAttributes(Element field, CoreField def) {
        String fieldState = getElementAttributeValue(field, "state");
        String fieldReadOnly = getElementAttributeValue(field, "readonly");
        // deprecated db type tag - only use if no other data is given!
        Element dbtype = getElementByPath(field, "field.db.type");
        if (dbtype != null) {
            if ("".equals(fieldState))    fieldState = getElementAttributeValue(dbtype, "state");
            if ("".equals(fieldReadOnly)) fieldReadOnly = getElementAttributeValue(dbtype, "readonly");
        }

        // state - default peristent
        int state = Field.STATE_PERSISTENT;
        if (!"".equals(fieldState)) { state = Fields.getState(fieldState); }
        if (state != def.getState()) def.setState(state);


        boolean readOnly = false;
        if ("".equals(fieldReadOnly)) {
            readOnly = state == Field.STATE_SYSTEM || state == Field.STATE_SYSTEM_VIRTUAL;
        }
        else {
            readOnly = "true".equalsIgnoreCase(fieldReadOnly);
        }

        if (def.isReadOnly() != readOnly) {
            def.setReadOnly(readOnly);
        }
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

    @Override
    public String toString() {
        return super.toString() + ":" + getSystemId();
    }


}

