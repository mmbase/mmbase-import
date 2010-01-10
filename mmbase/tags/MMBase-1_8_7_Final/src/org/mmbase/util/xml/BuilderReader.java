/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeManager;
import org.mmbase.core.CoreField;
import org.mmbase.core.util.Fields;
import org.mmbase.datatypes.*;
import org.mmbase.datatypes.util.xml.DataTypeReader;
import org.mmbase.datatypes.util.xml.DependencyException;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.util.Index;

import org.mmbase.util.LocalizedString;
import org.mmbase.util.XMLEntityResolver;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * Used to parse and retrieve data from a builder configuration file.
 * The parser support builders for builder dtd 1.1.
 *
 * @since MMBase 1.7
 * @author Case Roole
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class BuilderReader extends DocumentReader {

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

    public static final String XSD_BUILDER_2_0 = "builder.xsd";
    public static final String NAMESPACE_BUILDER_2_0 = "http://www.mmbase.org/xmlns/builder";
    public static final String NAMESPACE_BUILDER = NAMESPACE_BUILDER_2_0;

    private static final Logger log = Logging.getLoggerInstance(BuilderReader.class);

    /**
     * Register the namespace and XSD used by DataTypeConfigurer
     * This method is called by XMLEntityResolver.
     */
    public static void registerSystemIDs() {
        XMLEntityResolver.registerSystemID(NAMESPACE_BUILDER_2_0 + ".xsd", XSD_BUILDER_2_0, BuilderReader.class);
    }


    /**
     * Register the Public Ids for DTDs used by BuilderReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        // various builder dtd versions
        XMLEntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_0, DTD_BUILDER_1_0, BuilderReader.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_1, DTD_BUILDER_1_1, BuilderReader.class);
        //XMLEntityResolver.registerPublicID("-//MMBase//DTD builder config 2.0//EN", "builder_2_0.dtd", BuilderReader.class);

        // legacy public IDs (wrong, don't use these)
        XMLEntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_0_FAULT, DTD_BUILDER_1_0, BuilderReader.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_BUILDER_OLD, DTD_BUILDER_1_0,       BuilderReader.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_1_FAULT, DTD_BUILDER_1_1, BuilderReader.class);
    }

    /**
     * MMBase instance, used to load parent (extending) builders
     */
    private MMBase mmbase;

    /**
     * Parent builder.
     * If assigned, the properties of this builder are used as 'defaults'
     * and the fields of the builder are inherited.
     * @since MMbase-1.6
     */
    private MMObjectBuilder parentBuilder;

    /**
     * If false, the parent builder could not be resolved.
     * A builder with an unresolved parent is set to 'inactive', regardless of actual status
     * The default value is false, as resolving Inheritance is mandatory when loading builders.
     * @since MMbase-1.6
     */
    private boolean inheritanceResolved = false;


    /**
     * searchPositions and inputPositions are used to adminstrate 'occupied' positions (from
     * editor/positions), which is used to find defaults if not specified.
     * @since MMBase-1.7
     */
    private SortedSet searchPositions = new TreeSet();
    private SortedSet inputPositions  = new TreeSet();

    /**
     * @since MMBase-1.7
     */
    public BuilderReader(InputSource source, MMBase mmb) {
        super(source, BuilderReader.class);
        mmbase = mmb;
        if (getRootElement().getTagName().equals("builder")) {
            resolveInheritance();
        }
    }

    /**
     * @since MMBase-1.8
     */
    public BuilderReader(Document doc, MMBase mmb) {
        super(doc);
        mmbase = mmb;
        if (getRootElement().getTagName().equals("builder")) {
            resolveInheritance();
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
    protected boolean resolveInheritance() {
        String buildername = getExtends();
        if (buildername.equals("")) {
            parentBuilder = null;
            inheritanceResolved = true;
        } else {
            inheritanceResolved = false;
            if (buildername.equals(getElementAttributeValue("builder", "name"))) {

            } else if (mmbase != null) {
                parentBuilder = mmbase.getBuilder(buildername);
            }
            inheritanceResolved = (parentBuilder != null);
            if (inheritanceResolved) { // fill inputPositions, searchPositions
                Iterator fields = parentBuilder.getFields(NodeManager.ORDER_EDIT).iterator();
                while (fields.hasNext()) {
                    CoreField def = (CoreField) fields.next();
                    inputPositions.add(new Integer(def.getEditPosition()));
                }
                fields = parentBuilder.getFields(NodeManager.ORDER_SEARCH).iterator();
                while (fields.hasNext()) {
                    CoreField def = (CoreField) fields.next();
                    searchPositions.add(new Integer(def.getSearchPosition()));
                }
            }
        }
        return inheritanceResolved;
    }

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
        if (sval.equals("") && (parentBuilder != null)) {
            sval = parentBuilder.getSearchAge();
        }
        try {
            val = Integer.parseInt(sval);
        } catch(Exception f) {}
        return val;
    }

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
            if (parentBuilder != null) {
                return parentBuilder.getClass().getName();
            } else {
                return "";
            }
        }
        // is it a full name or inside the org.mmbase.module.builders.* path
        int pos = val.indexOf('.');
        if (pos==-1) {
            val = "org.mmbase.module.builders."+val;
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
    public Map getDataTypes(DataTypeCollector collector) {
        Element element = getElementByPath("builder.datatypes");
        if (element != null) {
            DataTypeReader.readDataTypes(element, collector);
            log.info("Datatypes for this builder " + collector);
        }
        return collector.getDataTypes();
    }

    /**
     * Get the field definitions of this builder.
     * If applicable, this includes the fields inherited from a parent builder.
     *
     * @return a List of all Fields as CoreField
     * @since MMBase-1.8
     */
    public List getFields() {
        return getFields(null, DataTypes.getSystemCollector());
    }

    /**
     * Get the field definitions of this builder.
     * If applicable, this includes the fields inherited from a parent builder.
     *
     * @param builder the MMObjectBuilder to which the fields will be added
     * @param collector the datatype collector used to access the datatypes available for the fields to read.
     * @return a List of all Fields as CoreField
     * @since MMBase-1.8
     */
    public List getFields(MMObjectBuilder builder, DataTypeCollector collector) {
        List results = new ArrayList();
        Map oldset = new HashMap();
        int pos = 1;
        if (parentBuilder != null) {
            List parentfields = parentBuilder.getFields(NodeManager.ORDER_CREATE);
            if (parentfields != null) {
                // have to clone the parent fields
                // need clone()!
                for (Iterator i = parentfields.iterator();i.hasNext();) {
                    CoreField f = (CoreField)i.next();
                    CoreField newField = (CoreField)f.clone(f.getName());
                    newField.setParent(builder);
                    while(newField.getStoragePosition() >= pos) pos++;
                    newField.finish();
                    results.add(newField);
                    oldset.put(newField.getName(), newField);
                }
            }
        }

        for(Iterator ns1 = getChildElements("builder", "fieldlist"); ns1.hasNext(); ) {
            Element fieldList = (Element) ns1.next();

            for(Iterator ns = getChildElements(fieldList, "field"); ns.hasNext(); ) {
                Element field = (Element) ns.next();
                String fieldName = getElementAttributeValue(field, "name");
                if ("".equals(fieldName)) {
                    fieldName = getElementValue(getElementByPath(field,"field.db.name"));
                }
                CoreField def = (CoreField) oldset.get(fieldName);
                try {
                    if (def != null) {

                        boolean parentInStorage = def.inStorage();
                        def.rewrite();
                        DataType dataType = decodeDataType(builder, collector, def.getName(), field, def.getType(), def.getListItemType(), false);
                        if (dataType != null) {
                            def.setDataType(dataType); // replace datatype
                        }
                        decodeFieldDef(field, def, collector);
                        decodeFieldAttributes(field, def);
                        def.finish();
                    } else {
                        def = decodeFieldDef(builder, collector, field);
                        def.setStoragePosition(pos++);
                        def.finish();
                        results.add(def);
                    }
                } catch (Exception e) {
                    log.error("During parsing of " + XMLWriter.write(field, true, true) + " " + e.getMessage(), e);
                }
            }
        }

        return results;
    }

    /**
     * Get the named indices of this builder.
     * Note that the 'default' index (set with the 'key' attribute) is also included
     * in this list (with the name {@link Index#MAIN}).
     *
     * @param builder the MMObjectBuilder to which the fields will be added
     * @return a List of all Indices
     */
    public List getIndices(MMObjectBuilder builder) {
        List results = new ArrayList();
        Index mainIndex = null;
        if (parentBuilder != null) {
            // create the
            Index parentIndex = parentBuilder.getStorageConnector().getIndex(Index.MAIN);
            if (parentIndex != null) {
                mainIndex = new Index(builder, Index.MAIN);
                mainIndex.setUnique(true);
                for (Iterator i = parentIndex.iterator(); i.hasNext(); ) {
                    Field field = (Field)i.next();
                    mainIndex.add(builder.getField(field.getName()));
                }
            }
        }

        for (Iterator fields = getChildElements("builder.fieldlist","field"); fields.hasNext(); ) {
            Element field = (Element)fields.next();
            Element dbtype = getElementByPath(field,"field.db.type");
            if (dbtype != null) {
                String key = getElementAttributeValue(dbtype,"key");
                if (key != null && key.equalsIgnoreCase("true")) {
                    String fieldName = getElementAttributeValue(field, "name");
                    if ("".equals(fieldName)) {
                        fieldName = getElementValue(getElementByPath(field,"field.db.name"));
                    }
                    if (mainIndex == null ) mainIndex = new Index(builder, Index.MAIN);
                    mainIndex.add(builder.getField(fieldName));
                }
            }
        }
        if (mainIndex != null) {
           results.add(mainIndex);
        }

        if (parentBuilder != null) {
            Collection parentIndices = parentBuilder.getStorageConnector().getIndices().values();
            if (parentIndices != null) {
                for (Iterator i = parentIndices.iterator();i.hasNext();) {
                    Index parentIndex = (Index)i.next();
                    Index newIndex = new Index(builder, parentIndex.getName());;
                    newIndex.setUnique(parentIndex.isUnique());
                    for (Iterator parentIndexIter = parentIndex.iterator(); parentIndexIter.hasNext(); ) {
                        Field field = (Field) parentIndexIter.next();
                        newIndex.add(builder.getField(field.getName()));
                    }
                    results.add(newIndex);
                }
            }
        }

        for(Iterator ns1 = getChildElements("builder", "indexlist"); ns1.hasNext(); ) {
            Element indexList = (Element) ns1.next();
            for(Iterator indices = getChildElements(indexList,"index"); indices.hasNext(); ) {
                Element indexElement   = (Element)indices.next();
                String indexName = indexElement.getAttribute("name");
                if (indexName != null && !indexName.equals("")) {
                    String unique = indexElement.getAttribute("unique");
                    Index index = new Index(builder, indexName);
                    index.setUnique(unique != null && unique.equals("true"));
                    for(Iterator fields = getChildElements(indexElement,"indexfield"); fields.hasNext(); ) {
                        Element fieldElement   = (Element)fields.next();
                        String fieldName = fieldElement.getAttribute("name");
                        Field field = builder.getField(fieldName);
                        if (field == null) {
                            log.error("field '" + fieldName +"' in index '" + indexName + "' in builder " + builder.getTableName() + " does not exist");
                        } else {
                            index.add(field);
                        }
                }
                    results.add(index);
                } else {
                    log.error("index in builder " + builder.getTableName() + " has no name");
                }
            }
        }
        return results;
    }

    /**
     * @since MMBase-1.8
     */
    public Set getFunctions(final MMObjectBuilder buil) {
        Set results = new HashSet();
        for(Iterator ns1 = getChildElements("builder", "functionlist"); ns1.hasNext(); ) {
            Element functionList = (Element) ns1.next();

            for(Iterator ns = getChildElements(functionList,"function"); ns.hasNext(); ) {
                try {
                    Element functionElement   = (Element)ns.next();
                    final String functionName = functionElement.getAttribute("name");
                    String providerKey        = functionElement.getAttribute("key");
                    String functionClass      = getNodeTextValue(getElementByPath(functionElement, "function.class"));

                    Function function;
                    log.service("Using " + functionClass);
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
                        function = BeanFunction.getFunction(claz, providerKey, new BeanFunction.Producer() {
                                public Object getInstance() {
                                    try {
                                        return BeanFunction.getInstance(claz, buil);
                                    } catch (Exception e) {
                                        log.error(e.getMessage(), e);
                                        return null;
                                    }
                                }
                                public String toString() {
                                    return "" + claz.getName() + "." + buil.getTableName();
                                }
                            });
                    }
                    if (! functionName.equals("") && ! function.getName().equals(functionName)) {
                        log.service("Wrapping " + function.getName() + " to " + functionName);
                        function = new WrappedFunction(function) {
                                public String getName() {
                                    return functionName;
                                }
                            };
                    }
                    NodeFunction nf = NodeFunction.wrap(function);
                    if (nf != null) function = nf;

                    log.info("Found new function " + function + " for builder " + buil.getTableName());
                    results.add(function);
                } catch (ClassNotFoundException cnfe) {
                    log.warn("No such class "  + cnfe.getMessage());
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }

        }


        return results;

    }

    /**
     * Determine an integer value from an elements body.
     * Used for the List, Search, and Edit position values.
     * @param elm The element containing the value.
     * @return the parsed integer
     */
    private int getEditorPos(Element elm) {
        try {
            int val = Integer.parseInt(getElementValue(elm));
            return val;
        } catch(Exception e) {
            return -1;
        }
    }
    /**
     * Alter a specified, named FieldDef object using information obtained from the buidler configuration.
     * Only GUI information is retrieved and stored (name and type of the field should already be specified).
     * @since MMBase-1.6
     * @param elm The element containing the field information acc. to the buidler xml format
     * @param def The field definition to alter
     */
    private void decodeFieldDef(Element field, CoreField def, DataTypeCollector collector) {
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
            if (inputPos > -1) inputPositions.add(new Integer(inputPos));
            def.setEditPosition(inputPos);
        } else {
            // if not specified, use lowest 'free' position.
            int i = 1;
            while (inputPositions.contains(new Integer(i))) {
                ++i;
            }
            inputPositions.add(new Integer(i));
            def.setEditPosition(i);

        }
        editorpos = getElementByPath(field, "field.editor.positions.list");
        if (editorpos != null) {
            def.setListPosition(getEditorPos(editorpos));
        }
        editorpos = getElementByPath(field, "field.editor.positions.search");
        if (editorpos != null) {
            int searchPos = getEditorPos(editorpos);
            if (searchPos > -1) searchPositions.add(new Integer(searchPos));
            def.setSearchPosition(searchPos);
        } else {
            // if not specified, use lowest 'free' position, unless, db-type is BINARY (non-sensical searching on that)
            // or the field is not in storage at all (search cannot be performed by database)
            if (def.getType() != Field.TYPE_BINARY && !def.isVirtual()) {
                int i = 1;
                while (searchPositions.contains(new Integer(i))) {
                    ++i;
                }
                searchPositions.add(new Integer(i));
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
     * @param collector The DataTypeCollector of the builder.
     * @param fieldName the name of the field (used in log messages)
     * @param field     The 'field' element of the builder xml
     * @param type      The database type of the field
     * @param listItemType If the database type is a List, there is also a type of its element
     * @param forceInstance If true, it will never return <code>null</code>, but will return (a clone) of the DataType associated with the database type.
     * @since MMBase-1.8
     */
    protected DataType decodeDataType(final MMObjectBuilder builder, final DataTypeCollector collector, final String fieldName, final Element field, final int type, final int listItemType, final boolean forceInstance) {
        BasicDataType baseDataType = null;
        if (type == Field.TYPE_LIST) {
            baseDataType = DataTypes.getListDataType(listItemType);
        } else if (type != Field.TYPE_UNKNOWN) {
            baseDataType = DataTypes.getDataType(type);
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
                throw new IllegalArgumentException("No type defined");
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
                    MMObjectBuilder enumerationBuilder = null;
                    // The guitype is deprecated. Normally coincides with datatype's id.
                    // The following are exceptions:
                    // 'string' is surrogated with the datatype 'line'.
                    if ("string".equals(guiType)) {
                        guiType = "line";
                        if (log.isDebugEnabled()) {
                            log.debug("Converted deprecated guitype 'string' for field " + (builder != null ? builder.getTableName() + "."  : "") + fieldName + " with datatype 'line'.");
                        }
                    } else
                    // 'eventtime' is surrogated with the datatype 'datetime'.
                    if ("eventtime".equals(guiType)) {
                        guiType = "datetime";
                        if (log.isDebugEnabled()) {
                            log.debug("Converted deprecated guitype 'eventtime' for field " + (builder != null ? builder.getTableName() + "."  : "") + fieldName + " with datatype 'datetime'.");
                        }
                    } else
                    // 'relativetime' is surrogated with the datatype 'line'.
                    if ("relativetime".equals(guiType)) {
                        guiType = "duration";
                        if (log.isDebugEnabled()) {
                            log.debug("Converted deprecated guitype 'relativetime' for field " + (builder != null ? builder.getTableName() + "."  : "") + fieldName + " with datatype 'duration'.");
                        }
                    } else
                    // check for nodetypes
                    if (type == Field.TYPE_NODE) {
                        try {
                            enumerationBuilder = mmbase.getBuilder(guiType);
                        } catch (RuntimeException re) {
                            log.warn("Exception during parsing of field '" + fieldName + "' of " + (builder != null ? builder.getTableName() : "NULL")  + ":  " + re.getMessage());
                        }
                    }
                    if (enumerationBuilder != null) {
                        //  Create a query element of the format:
                        //  <query type="[buildername]" xmlns="http://www.mmbase.org/xmlns/searchquery" />
                        // and add it to the enumerationfactory using addQuery()
                        Element queryElement = guiTypeElement.getOwnerDocument().createElementNS("http://www.mmbase.org/xmlns/searchquery", "query");
                        queryElement.setAttribute("type", enumerationBuilder.getTableName());
                        dataType = (BasicDataType) baseDataType.clone();
                        Document queryDocument = DocumentReader.toDocument(queryElement);
                        dataType.getEnumerationFactory().addQuery(LocalizedString.getLocale(queryElement), queryDocument);
                        dataType.getEnumerationRestriction().setEnforceStrength(DataType.ENFORCE_NEVER);
                    } else {
                        dataType = collector.getDataTypeInstance(guiType, baseDataType);
                        if (dataType == null) {
                            log.warn("Could not find data type for " + baseDataType + " / " + guiType + " for builder: '" + (builder == null ? "NULL" : builder.getTableName()) + "'");
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
                    throw new IllegalArgumentException("No base datatype given, and no field type defined");
                }
                requestedBaseDataType = baseDataType;
            } else {
                requestedBaseDataType = collector == null ? null : collector.getDataType(base, true);
                if (requestedBaseDataType == null) {
                    log.error("Could not find base datatype for '" + base + "' falling back to " + baseDataType + " in builder '" + (builder == null ?  "NULL" : builder.getTableName()) + "'. Collector: " + collector, new Exception());
                    requestedBaseDataType = baseDataType;
                }
            }
            try {
                dataType = (BasicDataType) DataTypeReader.readDataType(dataTypeElement, requestedBaseDataType, collector).dataType;
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
                throw new IllegalArgumentException("No datatype given, and no type defined");
            }
            dataType = (BasicDataType) baseDataType.clone(""); // clone with empty id
        }

        return dataType;
    }


    /**
     * @since MMBase-1.8.6
     */
    private void decodeFieldAttributes(Element field, CoreField def) {
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

    /**
     * Construct a FieldDef object using a field Element using information
     * obtained from the builder configuration.
     * @since MMBase-1.8
     */
    private CoreField decodeFieldDef(MMObjectBuilder builder, DataTypeCollector collector, Element field) {
        // create a new CoreField we need to fill

        // obtain field name.
        // if both the field name attribute and the <db><name> tag are specified, the attribute takes precedence.
        String fieldName = getElementAttributeValue(field, "name");
        String fieldDBName = getElementValue(getElementByPath(field, "field.db.name"));
        if ("".equals(fieldName)) {
            if ("".equals(fieldDBName)) {
                throw new IllegalArgumentException("Field name was not specified for builder " + builder.getTableName() + ".");
            }
            if (log.isDebugEnabled()) {
                log.debug("<db><name> tag for field '" + fieldDBName + "' is deprecated. Use the name attribute.");
            }
            fieldName = fieldDBName;
        } else if (!"".equals(fieldDBName)) {
            log.warn("Specified field name twice: once in the name attribute ('" + fieldName + "') and once in the <name> tag ('" + fieldDBName + "'). Ignoring name tag.");
        }


        // implied by datatype
        // use db/type to override for legacy database issues
        // (mostly to prevent warnings in the log, as mmbase fixes this anyway)
        String fieldType = "";
        String fieldSize = "";
        String fieldNotNull = "";

        // defined in datatype
        String fieldRequired = "";
        String fieldUnique = "";

        // deprecated db type tag - only use if no other data is given!
        Element dbtype = getElementByPath(field, "field.db.type");
        if (dbtype != null) {
            if (!"".equals(fieldType) || !"".equals(fieldNotNull) || !"".equals(fieldSize)) {
                log.warn("Specified field type info for '" + fieldName + "' twice: once in the field tag attributes and once in the <db><type> tag.");
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("<db><type> tag for field '" + fieldName + "' is deprecated.");
                }
                fieldType = getElementValue(dbtype);
                fieldNotNull = getElementAttributeValue(dbtype, "notnull");
                fieldRequired = getElementAttributeValue(dbtype, "required");
                fieldUnique = getElementAttributeValue(dbtype, "unique");
                fieldSize = getElementAttributeValue(dbtype, "size");
            }
        }

        // type - default unknown (derived from datatype)
        int type = Field.TYPE_UNKNOWN;
        int listItemType = Field.TYPE_UNKNOWN;
        if (!"".equals(fieldType)) {
            type = Fields.getType(fieldType);
            if (type == Field.TYPE_LIST) {
                if (fieldType.length() > 5) {
                    listItemType = Fields.getType(fieldType.substring(5, fieldType.length() - 1));
                }
            }
        }

        // datatype
        DataType dataType = decodeDataType(builder, collector, fieldName, field, type, listItemType, true);

        // determine type from datatype, if possible)
        if (type == Field.TYPE_UNKNOWN) {
            type = dataType.getBaseType();
            if (type == Field.TYPE_LIST) {
                listItemType = ((ListDataType)dataType).getItemDataType().getBaseType();
            }
        }

        CoreField def = Fields.createField(fieldName, type, listItemType,
                                           Field.STATE_VIRTUAL,/*temp default, will set by decodeFieldAttributes*/
                                           dataType);
        dataType = def.getDataType();

        decodeFieldAttributes(field, def);

        def.setParent(builder);

        if (!fieldSize.equals("")) {
            try {
                def.setMaxLength(Integer.parseInt(fieldSize));
            } catch (NumberFormatException e) {
                log.warn("invalid value for size : " + fieldSize);
            }
        }

        // set required property, but only if given
        if (!"".equals(fieldRequired)) {
            dataType.setRequired("true".equalsIgnoreCase(fieldRequired));
        }

        // default for notnull is value of required
        def.setNotNull("true".equals(fieldNotNull) || ("".equals(fieldNotNull) && dataType.isRequired()));

        // set unique property, but only if given
        if ("implied".equalsIgnoreCase(fieldUnique)) {
            dataType.setUnique(true);
            dataType.getUniqueRestriction().setEnforceStrength(DataType.ENFORCE_NEVER);
        } else if ("true".equalsIgnoreCase(fieldUnique)) {
            dataType.setUnique(true);
        }

        decodeFieldDef(field, def, collector);

        return def;
    }

    /**
     * Get the properties of this builder
     * @code-conventions return type should be Map
     * @return the properties in a Hashtable (as name-value pairs)
     */
    public Hashtable getProperties() {
        Hashtable results=new Hashtable();
        if (parentBuilder != null) {
            Map parentparams = parentBuilder.getInitParameters();
            if (parentparams != null) {
                results.putAll(parentparams);
            }
        }
        for(Iterator iter = getChildElements("builder.properties","property");
                        iter.hasNext(); ) {
            Element p = (Element)iter.next();
            String name = getElementAttributeValue(p,"name");
            String value = getElementValue(p);
            results.put(name,value);
        }
        return results;
    }


    /**
     * Get the descriptions of this builder
     * @code-conventions return type should be Map
     * @return the descriptions in a Hashtable, accessible by language
     */
    public Hashtable getDescriptions() {
        Hashtable results=new Hashtable();
        Element tmp;
        String lang;
        for (Iterator iter = getChildElements("builder.descriptions","description");
             iter.hasNext(); ) {
            tmp = (Element)iter.next();
            lang = getElementAttributeValue(tmp,"xml:lang");
            results.put(lang,getElementValue(tmp));
        }
        return results;
    }

    /**
     * Get the plural names of this builder
     * @code-conventions return type should be Map
     * @return the plural names in a Hashtable, accessible by language
     */
    public Hashtable getPluralNames() {
        Hashtable results = new Hashtable();
        for (Iterator iter = getChildElements("builder.names","plural"); iter.hasNext(); ) {
            Element tmp = (Element)iter.next();
            String lang = getElementAttributeValue(tmp,"xml:lang");
            results.put(lang,getElementValue(tmp));
        }
        return results;
    }

    /**
     * Get the singular (GUI) names of this builder
     * @code-conventions return type should be Map
     * @return the singular names in a Hashtable, accessible by language
     */
    public Hashtable getSingularNames() {
        Hashtable results = new Hashtable();
        for (Iterator iter = getChildElements("builder.names","singular"); iter.hasNext(); ) {
            Element tmp = (Element)iter.next();
            String lang = getElementAttributeValue(tmp,"xml:lang");
            results.put(lang,getElementValue(tmp));
        }
        return results;
    }


    /**
     * Get the builder that this builder extends
     *
     * @since MMBase-1.6
     * @return the parent as an MMObjectBuilder, or null if not specified or unresolved
     */
    public MMObjectBuilder getParentBuilder() {
        return parentBuilder;
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
     * Retrieve the (major) version number of this builder
     * @since MMBase-1.8
     * @return the version as an integer.
     */
    public int getVersion() {
        String version = getElementAttributeValue("builder","version");
        if (version.equals("") && parentBuilder != null) {
           return parentBuilder.getVersion();
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

    /**
     * Retrieve the name of the maintainer of this builder
     * @since MMBase-1.8
     * @return the name fo the maintainer as a String
     */
    public String getMaintainer() {
        String maintainer = getElementAttributeValue("builder", "maintainer");
        if (maintainer.equals("")) {
            if (parentBuilder != null) {
                maintainer = parentBuilder.getMaintainer();
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
        if (o instanceof BuilderReader) {
            BuilderReader b = (BuilderReader) o;
            List fields = getFields();
            List otherFields = b.getFields();
            return
                fields.equals(otherFields) &&
                getMaintainer().equals(b.getMaintainer()) &&
                getVersion() == b.getVersion() &&
                getExtends().equals(b.getExtends()) &&
                getSingularNames().equals(b.getSingularNames()) &&
                getPluralNames().equals(b.getPluralNames()) &&
                getDescriptions().equals(b.getDescriptions()) &&
                getProperties().equals(b.getProperties()) &&
                getClassName().equals(b.getClassName())
                ;
        } else {
            return false;
        }
    }

    /**
     * Whether this builderreader object is equal to another for storage purposes (so, ignoring gui and documentation fields)
     * @since MMBase-1.7
     */
    public boolean storageEquals(BuilderReader f) {
        List otherFields = f.getFields();
        List thisFields  = getFields();
        if (otherFields.size() != thisFields.size()) return false;
        for (int i = 0; i < thisFields.size(); i++) {
            CoreField thisField = (CoreField) thisFields.get(i);
            CoreField otherField = (CoreField) otherFields.get(i);
            if (! thisField.storageEquals(otherField)) return false;
        }
        return true;
    }

    /**
     * For testing only
     */
    public static void main(String[] argv) throws Exception {
        org.mmbase.util.ResourceLoader rl = org.mmbase.util.ResourceLoader.getSystemRoot();
        Document doc = rl.getDocument(argv[0], true, BuilderReader.class);
        new BuilderReader(doc, null);
    }

}

