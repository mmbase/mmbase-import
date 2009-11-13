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
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.util.Index;

import org.mmbase.util.*;

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
    private SortedSet<Integer> searchPositions = new TreeSet<Integer>();
    private SortedSet<Integer> inputPositions  = new TreeSet<Integer>();

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
        this(doc, mmb, Integer.MAX_VALUE);
    }
    /**
     * @since MMBase-1.9
     */
    private BuilderReader(Document doc, MMBase mmb, int maxVersion) {
        super(doc);
        mmbase = mmb;
        if (this.getVersion() <= maxVersion) {
            if (getRootElement().getTagName().equals("builder")) {
                resolveInheritance();
            }
        }
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
    protected boolean resolveInheritance() {
        String buildername = getExtends();
        if (buildername.equals("")) {
            parentBuilder = null;
            inheritanceResolved = true;
        } else if (buildername.equals(getName())) {
            int thisVersion = getVersion();
            BuilderReader parent = null;
            int foundVersion = -1;
            ResourceLoader loader = mmbase.getBuilderLoader();
            for (String s :  loader.getResourcePaths(Pattern.compile(getName()+ "\\.xml"), true)) {
                for (java.net.URL url : loader.getResourceList(s)) {
                    try {

                        if (! url.openConnection().getDoInput()) continue;
                        org.w3c.dom.Document doc = ResourceLoader.getDocument(url, true, BuilderReader.class);
                        if (! doc.getDocumentElement().getTagName().equals("builder")) {
                            continue;
                        }
                        BuilderReader prop = new BuilderReader(doc, mmbase, thisVersion - 1);
                        int v = prop.getVersion();
                        if (v < thisVersion && v > foundVersion) {
                            parent = prop;
                            foundVersion = v;
                        }
                    } catch (Exception ioe) {
                        log.warn(ioe);
                    }
                }

            }
            if (parent == null) {
                log.warn("Tried to extend builder " + getSystemId() + " from itself, but no other builder resource found (with smaller version)");
                inheritanceResolved = false;
            } else {
                log.info("Inheriting " + document.getDocumentURI() + " from " + parent.document.getDocumentURI());
                Document inherit = this.document;
                this.document = (Document) parent.document.cloneNode(true);
                inherit.getDocumentElement().removeAttribute("extends");
                resolveInheritanceByXML(document, inherit);
                inheritanceResolved = false;
                return resolveInheritance();

            }


        } else {
            inheritanceResolved = false;
            if (mmbase != null) {
                parentBuilder = mmbase.getBuilder(buildername);
                inheritanceResolved = (parentBuilder != null);
                if (inheritanceResolved) { // fill inputPositions, searchPositions
                    Iterator<CoreField> fields = parentBuilder.getFields(NodeManager.ORDER_EDIT).iterator();
                    while (fields.hasNext()) {
                        CoreField def = fields.next();
                        inputPositions.add(def.getEditPosition());
                    }
                    fields = parentBuilder.getFields(NodeManager.ORDER_SEARCH).iterator();
                    while (fields.hasNext()) {
                        CoreField def = fields.next();
                        searchPositions.add(def.getSearchPosition());
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
     *
     * @return a List of all Fields as CoreField
     * @since MMBase-1.8
     */
    public List<CoreField> getFields() {
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
    public List<CoreField> getFields(MMObjectBuilder builder, DataTypeCollector collector) {
        List<CoreField> results = new ArrayList<CoreField>();
        Map<String, CoreField> oldset = new HashMap<String, CoreField>();
        int pos = 1;
        if (parentBuilder != null) {
            List<CoreField> parentfields = parentBuilder.getFields(NodeManager.ORDER_CREATE);
            if (parentfields != null) {
                // have to clone the parent fields
                // need clone()!
                for (CoreField f : parentfields) {
                    CoreField newField = (CoreField)f.clone(f.getName());
                    newField.setParent(builder);
                    while(newField.getStoragePosition() >= pos) pos++;
                    newField.finish();
                    results.add(newField);
                    oldset.put(newField.getName(), newField);
                }
            }
        }

        for(Element fieldList : getChildElements("builder", "fieldlist")) {
            for (Element field : getChildElements(fieldList,"field")) {
                String fieldName = getElementAttributeValue(field, "name");
                if ("".equals(fieldName)) {
                    fieldName = getElementValue(getElementByPath(field,"field.db.name"));
                }
                CoreField def = oldset.get(fieldName);
                try {
                    if (def != null) {
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
    public List<Index> getIndices(MMObjectBuilder builder) {
        List<Index> results = new ArrayList<Index>();
        Index mainIndex = null;
        if (parentBuilder != null) {
            // create the
            Index parentIndex = parentBuilder.getStorageConnector().getIndex(Index.MAIN);
            if (parentIndex != null) {
                mainIndex = new Index(builder, Index.MAIN);
                mainIndex.setUnique(true);
                for (Field field : parentIndex) {
                    mainIndex.add(builder.getField(field.getName()));
                }
            }
        }

        for (Element field : getChildElements("builder.fieldlist","field")) {
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
            Collection<Index> parentIndices = parentBuilder.getStorageConnector().getIndices().values();
            if (parentIndices != null) {
                for (Index parentIndex : parentIndices) {
                    Index newIndex = new Index(builder, parentIndex.getName());;
                    newIndex.setUnique(parentIndex.isUnique());
                    for (Field field : parentIndex) {
                        newIndex.add(builder.getField(field.getName()));
                    }
                    results.add(newIndex);
                }
            }
        }

        for (Element indexList : getChildElements("builder", "indexlist")) {
            for (Element indexElement : getChildElements(indexList, "index")) {
                String indexName = indexElement.getAttribute("name");
                if (indexName != null && !indexName.equals("")) {
                    String unique = indexElement.getAttribute("unique");
                    Index index = new Index(builder, indexName);
                    index.setUnique(unique != null && unique.equals("true"));
                    for (Element fieldElement : getChildElements(indexElement,"indexfield")) {
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
    public Set<Function> getFunctions(final MMObjectBuilder builder) {
        Map<String, Function> results = new HashMap<String, Function>();
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
                                        public Object getInstance() {
                                            try {
                                                return BeanFunction.getInstance(claz, builder);
                                            } catch (Exception e) {
                                                log.error(e.getMessage(), e);
                                                return null;
                                            }
                                        }
                                        public String toString() {
                                            return "" + claz.getName() + "." + builder.getTableName();
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
        Set<Function> r = new HashSet<Function>();
        for(Function fun : results.values()) {
            r.add(fun);
        }
        log.debug("Found functions " + r);
        return r;

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
     * Only GUI information is retrieved and stored (name and type of the field sg=hould already be specified).
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
                    } else if (type == Field.TYPE_NODE) {
                        if (guiType == null) {
                            if (log.isDebugEnabled()) log.debug("Gui type of NODE field '" + fieldName + "' is null");
                        } else {
                            enumerationBuilder = mmbase.getBuilder(guiType);
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
                    log.error("Could not find base datatype for '" + base + "' falling back to " + baseDataType + " in builder '" + (builder == null ?  "NULL" : builder.getTableName()) + "'");
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

        if (def.isNotNull() && ! "false".equalsIgnoreCase(fieldRequired)) { // If not null, it _must_ be required, unless explicitely not so (MMB-1504)
            dataType.setRequired(true);
        }

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
     * @return the properties in a Map (as name-value pairs)
     */
    public Map<String,String> getProperties() {
        Map<String,String> results = new HashMap<String,String>();
        if (parentBuilder != null) {
            Map<String,String> parentparams = parentBuilder.getInitParameters();
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
     * Get the name of the builder that this builder extends
     * @since MMBase-1.9
     * @return the name of the parent builder
     */
    public String getName() {
        return getElementAttributeValue("builder", "name");
    }

    /**
     * Retrieve the (major) version number of this builder
     * @since MMBase-1.8
     * @return the version as an integer.
     */
    public int getVersion() {
        String version = document.getDocumentElement().getAttribute("version");
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
            List<CoreField> fields = getFields();
            List<CoreField> otherFields = b.getFields();
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
        List<CoreField> otherFields = f.getFields();
        List<CoreField> thisFields  = getFields();
        if (otherFields.size() != thisFields.size()) return false;
        for (int i = 0; i < thisFields.size(); i++) {
            CoreField thisField = thisFields.get(i);
            CoreField otherField = otherFields.get(i);
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

