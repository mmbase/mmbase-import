/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;

import org.mmbase.module.corebuilders.*;
import org.mmbase.bridge.Field;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.legacy.ConstraintParser;
import org.mmbase.storage.search.legacy.QueryConvertor;

import org.mmbase.util.logging.*;

/**
 * @since MMBase-2.0
 */

class CoreClusterQueries extends ClusterQueries {

    private static final Logger log= Logging.getLoggerInstance(CoreClusterQueries.class);


    public static final CoreClusterQueries INSTANCE = new CoreClusterQueries();

    private CoreClusterQueries() {
    }

    @Override
    public QueryContext getQueryContext() {
        return CoreQueryContext.INSTANCE;
    }

    @Override
    protected int getNumberForAlias(String alias) {
        int nodeNumber = MMBase.getMMBase().getOAlias().getNumber(alias);
        if (nodeNumber < 0) {
            nodeNumber = 0;
        }
        return nodeNumber;
    }

    // Add constraints.
    // QueryConverter supports the old formats for backward compatibility.
    @Override
    protected void setConstraint(BasicSearchQuery query, String where) {
        QueryConvertor.setConstraint(query, where);
    }
    protected StepField getField(String fieldName, BasicSearchQuery query) {
        return ConstraintParser.getField(getQueryContext(), fieldName, query.getSteps());
    }

    @Override
    protected boolean isRelation(String builder) {
        return MMBase.getMMBase().getBuilder(builder) instanceof InsRel;
    }

    @Override
    protected String getBuilder(int nodeNumber) {
        MMObjectNode node = MMBase.getMMBase().getBuilder("object").getNode(nodeNumber);
        if (node == null) {
            return null;
        }
        return node.parent.getTableName();
    }

    @Override
    protected String getParentBuilder(String buil) {
        MMObjectBuilder builder = MMBase.getMMBase().getBuilder(buil);
        MMObjectBuilder parent = builder.getParentBuilder();
        return parent == null ? null : parent.getTableName();
    }

    public FieldDefs getField(String buil, String fieldName) {
        MMObjectBuilder builder = MMBase.getMMBase().getBuilder(buil);
        return builder.getField(fieldName);
    }

    @Override
    public FieldDefs getNodeField(String fieldName) {
        return new FieldDefs(fieldName, Field.TYPE_NODE, -1, Field.STATE_VIRTUAL, org.mmbase.datatypes.DataTypes.getDataType("node"));
    }

    @Override
    public String getTrueTableName(String table) {
        String tab = getTableName(table);
        int rnumber = MMBase.getMMBase().getRelDef().getNumberByName(tab);
        if (rnumber != -1) {
            return MMBase.getMMBase().getRelDef().getBuilderName(rnumber);
        } else {
            return tab;
        }
    }

    @Override
    protected boolean optimizeRelationStep(RelationStep relationStep, int sourceType, int destType, int role, int searchDir) {
        return MMBase.getMMBase().getTypeRel().optimizeRelationStep((BasicRelationStep) relationStep, sourceType, destType, role, searchDir);
    }
    // just changing scope for test-cases
    @Override
    protected String getUniqueTableAlias(String tableAlias, Set<String> tableAliases, Collection<String> originalAliases) {
        return super.getUniqueTableAlias(tableAlias, tableAliases, originalAliases);
    }

    @Override
    protected String getBuilder(String tableAlias, Map<String, Integer> roles) {
        String tableName = getTableName(tableAlias);
        // check builder - should throw exception if builder doesn't exist ?
        MMObjectBuilder bul = null;
        try {
            bul = MMBase.getMMBase().getBuilder(tableName);
        } catch (BuilderConfigurationException e) {
        }
        if (bul == null) {
            // check if it is a role name. if so, use the builder of the
            // rolename and store a filter on rnumber.
            int rnumber = MMBase.getMMBase().getRelDef().getNumberByName(tableName);
            if (rnumber == -1) {
                throw new IllegalArgumentException("Specified builder '" + tableName + "' does not exist.");
            } else {
                bul = MMBase.getMMBase().getRelDef().getBuilder(rnumber); // relation builder
                roles.put(tableAlias, rnumber);
            }
        } else if (bul instanceof InsRel) {
            int rnumber = MMBase.getMMBase().getRelDef().getNumberByName(tableName);
            if (rnumber != -1) {
                roles.put(tableAlias, rnumber);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Resolved table alias \"" + tableAlias + "\" to builder \"" + bul.getTableName() + "\"");
        }
        return bul.getTableName();
    }

    @Override
    public int getBuilderNumber(String buil) {
        return MMBase.getMMBase().getBuilder(buil).getNumber();
    }

    @Override
    protected StepField getField(String fieldName, SearchQuery query) {
        // TODO
        throw new UnsupportedOperationException();
    }

}

