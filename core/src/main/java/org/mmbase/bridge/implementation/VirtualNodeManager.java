/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.datatypes.*;
import org.mmbase.core.CoreField;
import org.mmbase.core.util.Fields;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * This class represents a virtual node type information object.
 * It has the same functionality as BasicNodeType, but it's nodes are vitrtual - that is,
 * constructed based on the results of a search over multiple node managers.
 * As such, it is not possible to search on this node type, nor to create new nodes.
 * It's sole function is to provide a type definition for the results of a search.
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id$
 */
public class VirtualNodeManager extends AbstractNodeManager implements NodeManager {
    private static final  Logger log = Logging.getLoggerInstance(VirtualNodeManager.class);

    private static final boolean allowNonQueriedFields = true; // not yet configurable

    // field types
    final protected Map<String, Field> fieldTypes = new HashMap<String, Field>();

    final MMObjectBuilder builder;
    private SearchQuery query;

    /**
     * Instantiated a Virtual NodeManager, and tries its best to find reasonable values for the field-types.
     */
    VirtualNodeManager(org.mmbase.module.core.VirtualNode node, Cloud cloud) {
        super(cloud);
        // determine fields and field types
        if (node.getBuilder() instanceof VirtualBuilder) {
            VirtualBuilder virtualBuilder = (VirtualBuilder) node.getBuilder();;
            Map<String,CoreField> fields = virtualBuilder.getFields(node);
            for (Map.Entry<String, CoreField> entry : fields.entrySet()) {
                Field ft         = new BasicField(entry.getValue(), this);
                fieldTypes.put(entry.getKey(), ft);
            }
            builder = null;
            setStringValue("name", "virtual builder");
            setStringValue("description", "virtual builder");
        } else {
            builder = node.getBuilder();
            BasicNodeManager.sync(builder, fieldTypes, this);
        }
    }

    /**
     * @since MMBase-1.8
     */
    VirtualNodeManager(Query query, Cloud cloud) {
        super(cloud);
        if (query instanceof NodeQuery) {
            builder = BasicCloudContext.mmb.getBuilder(((NodeQuery) query).getNodeManager().getName());
            BasicNodeManager.sync(builder, fieldTypes, this);
        } else {
            builder = null;
            if (log.isDebugEnabled()) {
                log.debug("Creating NodeManager for " + query.toSql());
            }
            // fieldTypes map will be filled 'lazily' on first call to getFieldTypes.
            this.query = query; // query instanceof BasicQuery ? ((BasicQuery ) query).getQuery() : query;
            setStringValue("name", "cluster builder");
            setStringValue("description", "cluster builder");
        }

    }



    private static CoreField UNKNOWN_NODE_TYPE = Fields.createField("unknown_node_type", Field.TYPE_NODE, Field.TYPE_UNKNOWN, Field.STATE_VIRTUAL, DataTypes.getDataType("node"));
    /**
     * Returns the fieldlist of this nodemanager after making sure the manager is synced with the builder.
     * @since MMBase-1.8
     */
    @Override protected Map<String, Field> getFieldTypes() {
        if (builder != null) {
            return fieldTypes;
        } else {
            if (query != null) { // means not yet called (lazy loading of fields)
                // code to solve the fields.
                for (Step step : query.getSteps()) {
                    String name = step.getAlias();
                    if (name == null) {
                        name = step.getTableName();
                    }
                    Field ft = new VirtualNodeManagerField(UNKNOWN_NODE_TYPE, name);
                    fieldTypes.put(name, ft);

                    if (allowNonQueriedFields && ! query.isAggregating()) {
                        /// if hasField returns true also for unqueried fields
                        FieldIterator fields = cloud.getNodeManager(step.getTableName()).getFields().fieldIterator();
                        while (fields.hasNext()) {
                            Field f = fields.nextField();
                            final String fieldName = name + "." + f.getName();
                            fieldTypes.put(fieldName, new VirtualNodeManagerField(f, fieldName));
                        }
                    }
                }
                if (! allowNonQueriedFields || query.isAggregating()) {
                    //hasField only returns true for queried fields
                    for (StepField field : query.getFields()) {
                        Step step = field.getStep();
                        Field f = cloud.getNodeManager(step.getTableName()).getField(field.getFieldName());
                        String name = field.getAlias();
                        if (name == null) {
                            name = step.getAlias();
                            if (name == null) {
                                name = step.getTableName();
                            }
                            name += "." + field.getFieldName();
                        }
                        final String fieldName = name;
                        fieldTypes.put(name, new VirtualNodeManagerField(f, fieldName));

                    }
                }
                query = null;
            }
            return fieldTypes;
        }
    }



    @Override public String getGUIName(int plurality, Locale locale) {
        if (locale == null) locale = cloud.getLocale();
        if (builder != null) {
            if (plurality == NodeManager.GUI_SINGULAR) {
                return builder.getSingularName(locale.getLanguage());
            } else {
                return builder.getPluralName(locale.getLanguage());
            }
        } else {
            return getName();
        }
    }

    @Override
    public String getName() {
        return builder == null ? getStringValue("name") : builder.getTableName();
    }
    @Override
    public String getDescription() {
        return getDescription(null);
    }

    @Override
    public String getDescription(Locale locale) {
        if (builder == null) return getStringValue("description");
        if (locale == null) locale = cloud.getLocale();
        return builder.getDescription(locale.getLanguage());
    }

    /**
     */
    private class VirtualNodeManagerField extends FieldWrapper {

        private final String name;
        VirtualNodeManagerField(Field field, String name)  {
            super(field);
            this.name = name;
        }
        @Override
        public NodeManager getNodeManager() {
            return VirtualNodeManager.this;
        }
        @Override
        public String getName() {
            return name;
        }
        @Override
        public int getState() {
            return Field.STATE_VIRTUAL;
        }

        public int compareTo(Field o) {
            return name.compareTo(o.getName());
        }
    }

}
