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
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.util.Index;

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
 * @version $Id: BuilderReader.java,v 1.51 2005-11-23 12:21:32 michiel Exp $
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
     * If assigned, the properties of this buidler are used as 'defaults'
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
        resolveInheritance();
    }

    /**
     * @since MMBase-1.8
     */
    public BuilderReader(Document doc, MMBase mmb) {
        super(doc);
        mmbase = mmb;
        resolveInheritance();
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
            if (mmbase != null) {
                parentBuilder = mmbase.getBuilder(buildername);
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
        if (!inheritanceResolved) return "inactive";
        String val=getElementValue("builder.status").toLowerCase();
        if (val.equals("")) {
           if (parentBuilder != null) {
               return "active";
           } else {
               return "";
           }
        }
        // fix invalid values
        if (!val.equals("active")) {
           val="inactive";
        }
        return val;
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
        }
        return collector.getDataTypes();
    }

    /**
     * Get the field definitions of this builder.
     * If applicable, this includes the fields inherited from a parent builder.
     *
     * @return a List of all Fields as CoreField
     */
    public List getFields() {
        return getFields(null, null);
    }

    /**
     * Get the field definitions of this builder.
     * If applicable, this includes the fields inherited from a parent builder.
     *
     * @param builder the MMObjectBuilder to which the fields will be added
     * @param collector the datatype collector used to access the datatypes available for the fields to read.
     * @return a List of all Fields as CoreField
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

        for(Iterator ns = getChildElements("builder.fieldlist", "field"); ns.hasNext(); ) {
            Element field = (Element) ns.next();
            CoreField def = (CoreField) oldset.get(getElementValue(getElementByPath(field, "field.db.name")));
            try {
                if (def != null) {
                    def.rewrite();
                    DataType dataType = decodeDataType(builder, collector, def.getName(), field, def.getType(), def.getListItemType(), false);
                    if (dataType != null) {
                        def.setDataType(dataType); // replace datatype
                    }
                    decodeFieldDef(field, def, collector);
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
            Index parentIndex = parentBuilder.getIndex(Index.MAIN);
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
                    String fieldName = getElementValue(getElementByPath(field,"field.db.name"));
                    if (mainIndex == null ) mainIndex = new Index(builder, Index.MAIN);
                    mainIndex.add(builder.getField(fieldName));
                }
            }
        }
        if (mainIndex != null) {
           results.add(mainIndex);
        }

        for(Iterator indices = getChildElements("builder.indexlist","index"); indices.hasNext(); ) {
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
        return results;
    }

    /**
     * @since MMBase-1.8
     */
    public Set getFunctions() {
        Set results = new HashSet();
        for(Iterator ns = getChildElements("builder.functionlist","function"); ns.hasNext(); ) {
            try {
                Element functionElement   = (Element)ns.next();
                final String functionName = functionElement.getAttribute("name");
                String providerKey        = functionElement.getAttribute("key");
                String functionClass      = getNodeTextValue(getElementByPath(functionElement, "function.class"));

                Function function;
                log.service("Using " + functionClass);
                Class claz = Class.forName(functionClass);
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
                } else {
                    if ("".equals(providerKey)) providerKey = functionName;
                    if ("".equals(providerKey)) {
                        log.error("Speficied class " + claz + " in " + getSystemId() + "/functionslist/function is not a Function or FunctionProvider and can not be wrapped in a BeanFunction, because neither key nor name attribute were specified.");
                        continue;
                    }
                    function = BeanFunction.getFunction(claz, providerKey);
                }
                if (! functionName.equals("") && ! function.getName().equals(functionName)) {
                    log.service("Wrapping " + function.getName() + " to " + functionName);
                    function = new WrappedFunction(function) {
                            public String getName() {
                                return functionName;
                            }
                        };
                }
                if (! (function instanceof NodeFunction)) {
                    // if it contains a 'node' parameter, it can be wrapped into a node-function,
                    // and be available on nodes of this builder.
                    Parameters test = function.createParameters();
                    if (test.containsParameter(Parameter.NODE)) {
                        final Function f = function;
                        function = new NodeFunction(function.getName(), function.getParameterDefinition(), function.getReturnType()) {
                                protected Object getFunctionValue(org.mmbase.bridge.Node node, Parameters parameters) {
                                    if (parameters == null) parameters = createParameters();
                                    parameters.set(Parameter.NODE, node);
                                    return f.getFunctionValue(parameters);
                                }
                                public  Object getFunctionValue(Parameters parameters) {
                                    return f.getFunctionValue(parameters);
                                }
                            };
                    }
                }

                results.add(function);
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
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

    private Locale getLocale(String lang) {
        String[] loc = lang.split("_");
        Locale locale;
        if (loc.length == 3) {
            locale = new Locale(loc[0], loc[1], loc[2]);
        } else if (loc.length == 2) {
            locale = new Locale(loc[0], loc[1]);
        } else {
            locale = new Locale(lang);
        }
        return locale;
    }

    /**
     * Alter a specified, named FieldDef object using information obtained from the buidler configuration.
     * Only GUI information is retrieved and stored (name and type of the field sg=hould already be specified).
     * @since MMBase-1.6
     * @param elm The element containing the field information acc. to the buidler xml format
     * @param def The field definition to alter
     */
    private void decodeFieldDef(Element field, CoreField def, DataTypeCollector collector) {
        // Gui
        Element descriptions = getElementByPath(field, "field.descriptions");
        if (descriptions != null) {
            for (Iterator iter = getChildElements(descriptions, "description"); iter.hasNext(); ) {
                Element tmp = (Element) iter.next();
                String lang = getElementAttributeValue(tmp, "xml:lang");
                def.setDescription(getElementValue(tmp), getLocale(lang));
            }
        }


        // XXX: deprecated tag 'gui'
        Element gui = getElementByPath(field, "field.gui");
        if (gui != null) {
            for (Iterator iter = getChildElements(gui, "guiname"); iter.hasNext(); ) {
                Element tmp = (Element) iter.next();
                String lang = getElementAttributeValue(tmp, "xml:lang");
                def.setGUIName(getElementValue(tmp), getLocale(lang));
            }
            // XXX: even more deprecated
            for(Iterator iter = getChildElements(gui, "name"); iter.hasNext(); ) {
                Element tmp = (Element) iter.next();
                String lang = getElementAttributeValue(tmp, "xml:lang");
                def.setGUIName(getElementValue(tmp), getLocale(lang));
            }
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
            // if not specified, use lowest 'free' position, unless, db-type is BYTE (non-sensical searching on that)
            if (def.getType() != Field.TYPE_BINARY
                && def.isPersistent()  // also if the field is not persistent at all, searching is not trivial (cannot be performed by database)
                ) {
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
     * @param collector The DataTypeCollector of the bulider.
     * @param fieldName unused
     * @param field     The 'field' element of the builder xml
     * @param type      The database type of the field
     * @param listItemType If the database type is a List, there is also a type of its element
     * @param forceInstance If true, it will never return <code>null</code>, but will return (a clone) of the DataType associated with the database type.
     * @since MMBase-1.8
     */
    protected DataType decodeDataType(final MMObjectBuilder builder, final DataTypeCollector collector, final String fieldName, final Element field, final int type, final int listItemType, final boolean forceInstance) {
        final BasicDataType baseDataType;
        if (type == Field.TYPE_LIST) {
            baseDataType = DataTypes.getListDataType(listItemType);
        } else {
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
            String guiType = getElementValue(guiTypeElement);
            // the guitype 'string' is deperecated, and replaced with teh guitype 'line'
            // This only appleid to values entered for tehd eprecated guitype elmenet, as these were used in old builders
            if ("string".equals(guiType)) {
                guiType="line";
                log.warn("Replaced deprecated guitype 'string' for field " + builder.getTableName() + "." + fieldName + " with guitype 'line'.");
            }

            dataType = collector.getDataTypeInstance(guiType, baseDataType);
            if (dataType == null) {
                log.warn("Could not find data type for " + baseDataType + " / " + guiType);
            } else {
                if (log.isDebugEnabled()) log.debug("Found data type for " + baseDataType + " / " + guiType + " " + dataType);
            }
            if (! baseDataType.getClass().isAssignableFrom(dataType.getClass())) {
                // the thus configured datatype is not compatible with the database type.
                // Fix that as good as possible:
                BasicDataType newDataType = (BasicDataType) baseDataType.clone();
                newDataType.inherit(dataType);
                dataType = newDataType;
            }
        }

        Element dataTypeElement = getElementByPath(field, "field.datatype");
        if (dataTypeElement != null) {
            if (dataType != null) {
                log.warn("Using both deprecated 'gui/guitime' and 'datatype' subelements in field tag for field '" + fieldName + "', ignoring the first one.");
            }
            String base = dataTypeElement.getAttribute("base");
            BasicDataType requestedBaseDataType;
            if (base.equals("")) {
                log.debug("No base defined, using '" + baseDataType + "'");
                requestedBaseDataType = baseDataType;
            } else {
                requestedBaseDataType = collector.getDataType(base, true);
                if (requestedBaseDataType == null) {
                    log.error("Could not find base datatype for '" + base + "' falling back to " + baseDataType);
                    requestedBaseDataType = baseDataType;
                } else {
                    if (! baseDataType.getClass().isAssignableFrom(requestedBaseDataType.getClass())) {
                        // the thus configured datatype is not compatible with the database type.
                        // Fix that as good as possible:
                        BasicDataType newDataType = (BasicDataType) baseDataType.clone();
                        newDataType.inherit(requestedBaseDataType);
                        log.warn("" + requestedBaseDataType + " is not compatible with " + baseDataType + ". Repared to " + newDataType);
                        requestedBaseDataType = newDataType;
                    }
                }
            }
            // i'm not sure why it must be clone here.
            dataType = (BasicDataType) DataTypeReader.readDataType(dataTypeElement, requestedBaseDataType, collector).dataType.clone();
        }

        if (dataType == null && forceInstance) {
            dataType = (BasicDataType)baseDataType.clone();
        }

        return dataType;
    }

    /**
     * Construct a FieldDef object using a field Element using information
     * obtained from the builder configuration.
     * @since MMBase-1.8
     */
    private CoreField decodeFieldDef(MMObjectBuilder builder, DataTypeCollector collector, Element field) {
        // create a new CoreField we need to fill
        Element db = getElementByPath(field,"field.db");
        String fieldName = getElementValue(getElementByPath(db,"db.name"));
        Element dbtype = getElementByPath(db,"db.type");
        String baseType = getElementValue(dbtype);
        int type = Fields.getType(baseType);
        int listItemType = Field.TYPE_UNKNOWN;
        if (type == Field.TYPE_LIST) {
            if (baseType.length() > 5) {
                listItemType = Fields.getType(baseType.substring(5, baseType.length() - 1));
            }
        }
        int state = Fields.getState(getElementAttributeValue(dbtype,"state"));

        DataType dataType = decodeDataType(builder, collector, fieldName, field, type, listItemType, true);

        CoreField def = Fields.createField(fieldName, type, listItemType, state, dataType);
        def.setParent(builder);


        String size = getElementAttributeValue(dbtype, "size");
        if (size != null && !size.equals("")) {
            try {
                def.setMaxLength(Integer.parseInt(size));
            } catch (NumberFormatException e) {
                log.warn("invalid value for size : " + size);
            }
        }

        // set required property, but only if given
        String required = getElementAttributeValue(dbtype, "required");
        if ("true".equalsIgnoreCase(required)) {
            def.getDataType().setRequired(true);
        }

        String notnull = getElementAttributeValue(dbtype, "notnull"); // default for notnull is value of required
        def.setNotNull("true".equals(notnull) || ((notnull == null || "".equals(notnull)) && def.getDataType().isRequired()));


        // set unique property, but only if given
        String unique = getElementAttributeValue(dbtype, "unique");
        if ("true".equalsIgnoreCase(unique)) {
            def.getDataType().setUnique(true);
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
        Hashtable results=new Hashtable();
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
        Hashtable results=new Hashtable();
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
     * Get the name of the builder that this builder extends
     * @deprecated-now use getMaintainer()
     * @since MMBase-1.6
     * @return the name of the parent builder
     */
    public String getBuilderExtends() {
        return getExtends();
    }

    /**
     * Retrieve the (major) version number of this builder
     * @deprecated-now use getMaintainer()
     * @return the version as an integer.
     */
    public int getBuilderVersion() {
        return getVersion();
    }

    /**
     * Retrieve the name of the maintainer of this builder
     * @deprecated-now use getMaintainer()
     * @return the name fo the maintainer as a String
     */
    public String getBuilderMaintainer() {
        return getMaintainer();
    }

    /**
     * {@inheritDoc}
     * @since MMBase-1.7
     */
    public boolean equals(Object o) {
        if (o instanceof BuilderReader) {
            BuilderReader b = (BuilderReader) o;
            return
                getFields().equals(b.getFields()) &&
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

