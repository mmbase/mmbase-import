/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import java.util.regex.Pattern;
import org.mmbase.bridge.ContextProvider;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeManager;
import org.mmbase.core.CoreField;
import org.mmbase.bridge.util.DataTypeSetter;
import org.mmbase.core.util.Fields;
import org.mmbase.datatypes.*;
import org.mmbase.datatypes.DataTypes.FieldNotFoundException;
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
public class BuilderReader extends AbstractBuilderReader<CoreField> {


    private static final Logger log = Logging.getLoggerInstance(BuilderReader.class);

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
     * @since MMBase-1.7
     */
    public BuilderReader(InputSource source, MMBase mmb) {
        super(source);
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


    @Override
    protected final boolean resolveInheritance() {
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


    @Override
    protected int getParentSearchAge() {
        return Integer.parseInt(parentBuilder.getSearchAge());
    }


    @Override
    protected String getParentClassName() {
        return parentBuilder.getClass().getName();
    }


    @Override
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
    public List<CoreField> getFields(final MMObjectBuilder builder, final DataTypeCollector collector) {
        List<CoreField> results = new ArrayList<CoreField>();
        Map<String, CoreField> oldset = new HashMap<String, CoreField>();
        int pos = 1;
        if (parentBuilder != null) {
            List<CoreField> parentfields = parentBuilder.getFields(NodeManager.ORDER_CREATE);
            if (parentfields != null) {
                // have to clone the parent fields
                // need clone()!
                for (CoreField f : parentfields) {
                    CoreField newField = f.clone(f.getName());
                    newField.setParent(builder);
                    while(newField.getStoragePosition() >= pos) pos++;
                    newField.finish();
                    results.add(newField);
                    oldset.put(newField.getName(), newField);
                }
            }
        }

        for(Element fieldList : getChildElements("builder", "fieldlist")) {
            for (final Element fieldElement : getChildElements(fieldList,"field")) {
                String fieldName = getElementAttributeValue(fieldElement, "name");
                if ("".equals(fieldName)) {
                    fieldName = getElementValue(getElementByPath(fieldElement,"field.db.name"));
                }
                final CoreField def = oldset.get(fieldName);
                try {
                    if (def != null) {
                        def.rewrite();

                        DataTypeSetter setter = new DataTypeSetter(def) {
                                @Override
                                public void set(DataType dt) {
                                    if (dt != null) {
                                        field.setDataType(dt); // replace datatype
                                    }
                                    decodeFieldDef(fieldElement, def, collector);
                                    decodeFieldAttributes(fieldElement, def);
                                    def.finish();
                                }

                            };
                        decodeDataType(setter,
                                       (builder != null ? builder.getTableName() : null), collector, def.getName(), fieldElement, false);
                    } else {
                        CoreField newDef = decodeFieldDef(builder, collector, fieldElement);
                        newDef.setStoragePosition(pos++);
                        decodeFieldAttributes(fieldElement, newDef);
                        results.add(newDef);
                    }
                } catch (Exception e) {
                    log.error("During parsing of " + XMLWriter.write(fieldElement, true, true) + " " + e.getMessage(), e);
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
                    Index newIndex = new Index(builder, parentIndex.getName());
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
    public Set<Function<?>> getFunctions(final MMObjectBuilder builder) {
        return super.getFunctions(builder);
    }
    /**
     * Construct a FieldDef object using a field Element using information
     * obtained from the builder configuration.
     * @since MMBase-1.8
     */
    private CoreField decodeFieldDef(final MMObjectBuilder builder, final DataTypeCollector collector,
                                     final Element fieldElement) {
        // create a new CoreField we need to fill

        final String fieldName;
        {
            // obtain field name.
            // if both the field name attribute and the <db><name> tag are specified, the attribute takes precedence.
            String fn = getElementAttributeValue(fieldElement, "name");
            String fieldDBName = getElementValue(getElementByPath(fieldElement, "field.db.name"));
            if ("".equals(fn)) {
                if ("".equals(fieldDBName)) {
                    throw new IllegalArgumentException("Field name was not specified for builder " + builder.getTableName() + ".");
                }
                if (log.isDebugEnabled()) {
                    log.debug("<db><name> tag for field '" + fieldDBName + "' is deprecated. Use the name attribute.");
                }
                fn = fieldDBName;
            } else if (!"".equals(fieldDBName)) {
                log.warn("Specified field name twice: once in the name attribute ('" + fn + "') and once in the <name> tag ('" + fieldDBName + "'). Ignoring name tag.");
            }
            fieldName = fn;
        }


        // implied by datatype
        // use db/type to override for legacy database issues
        // (mostly to prevent warnings in the log, as mmbase fixes this anyway)
        final String fieldType;
        final String fieldSize;
        final String fieldNotNull;

        // defined in datatype
        final String fieldRequired;
        final String fieldUnique;


        // type - default unknown (derived from datatype)
        // deprecated db type tag - only use if no other data is given!
        Element dbtype = getElementByPath(fieldElement, "field.db.type");
        if (dbtype != null) {
            if (log.isDebugEnabled()) {
                log.debug("<db><type> tag for field '" + fieldName + "' is deprecated.");
            }
            fieldType = getElementValue(dbtype);
            fieldNotNull = getElementAttributeValue(dbtype, "notnull");
            fieldRequired = getElementAttributeValue(dbtype, "required");
            fieldUnique = getElementAttributeValue(dbtype, "unique");
            fieldSize = getElementAttributeValue(dbtype, "size");
        } else {
            fieldType = "";
            fieldNotNull = "";
            fieldRequired = "";
            fieldUnique = "";
            fieldSize = "";
        }

        final int type;
        final int listItemType;
        {
            int _type         = Field.TYPE_UNKNOWN;
            int _listItemType = Field.TYPE_UNKNOWN;
            if (!"".equals(fieldType)) {
                _type = Fields.getType(fieldType);
                if (_type == Field.TYPE_LIST) {
                    if (fieldType.length() > 5) {
                        _listItemType = Fields.getType(fieldType.substring(5, fieldType.length()));
                    }
                }
            }
            type = _type;
            listItemType = _listItemType;
        }

        final CoreField def = Fields.createField(fieldName, type, listItemType,
                                                 Field.STATE_VIRTUAL,/*temp default, will set by decodeFieldAttributes*/
                                                 Constants.DATATYPE_UNKNOWN);

        DataTypeSetter setter = new DataTypeSetter(def) {
                @Override
                public void set(DataType dataType) {
                    def.setDataType(dataType);
                    // determine type from datatype, if possible)
                    int _type = getType();
                    if (_type == Field.TYPE_UNKNOWN) {
                        setType(dataType.getBaseType());
                        if (_type == Field.TYPE_LIST) {
                            setListItemType(((ListDataType)dataType).getItemDataType().getBaseType());
                        }
                    }

                    dataType = def.getDataType();

                    decodeFieldAttributes(fieldElement, def);

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
                    def.setNotNull("true".equals(fieldNotNull) ||
                                   ("".equals(fieldNotNull) && dataType.isRequired()));

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
                    decodeFieldDef(fieldElement, def, collector);
                    def.finish();

                }
            };
        // datatype
        decodeDataType(setter,
                       (builder != null ? builder.getTableName() : null), collector, fieldName, fieldElement, true);


        return def;
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

    @Override
    protected boolean hasParent() {
        return parentBuilder != null;
    }

    @Override
    protected Map<String, String> getParentProperties() {
        return parentBuilder.getInitParameters();
    }


    @Override
    protected int getParentVersion() {
        return parentBuilder.getVersion();
    }

    @Override
    protected String getParentMaintainer() {
        return parentBuilder.getMaintainer();
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
     * @since MMBase-1.9.6
     * @param fieldAttribute
     * @return
     * @throws FieldNotFoundException
     */
    @Override
    protected BasicDataType getDataTypeForFieldAttribute(String fieldAttribute) throws FieldNotFoundException {
        List<String> elements = Arrays.asList(fieldAttribute.split("\\|", 4));
        Collections.reverse(elements);
        String uri                = elements.size() == 4 ? elements.get(3) : ContextProvider.getDefaultCloudContext().getUri();
        if (uri.equals("local")) {
            MMObjectBuilder ob = mmbase.getBuilder(elements.get(1));
            if (ob == null) throw new FieldNotFoundException("Builder " + elements.get(1) + " not yet available");
            CoreField field = ob.getField(elements.get(0));
            if (field == null) throw new FieldNotFoundException("Builder " + elements.get(1) + " has no field " + elements.get(0));
            return (BasicDataType) field.getDataType();
        } else {
            return super.getDataTypeForFieldAttribute(fieldAttribute);
        }
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

