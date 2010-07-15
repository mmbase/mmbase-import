/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import java.util.Map.Entry;

import org.mmbase.module.corebuilders.*;
import org.mmbase.cache.MultilevelCache;
import org.mmbase.core.CoreField;
import org.mmbase.bridge.BridgeException;
import org.mmbase.bridge.Field;
import org.mmbase.core.util.Fields;
import org.mmbase.util.functions.*;
import org.mmbase.datatypes.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.legacy.ConstraintParser;
import org.mmbase.storage.search.legacy.QueryConvertor;

import org.mmbase.util.logging.*;

/**
 * The builder for {@link ClusterNode clusternodes}.
 * <p>
 * Provides these methods to retrieve clusternodes:
 * <ul>
 *      <li>{@link #getClusterNodes(SearchQuery)}
 *          to retrieve clusternodes using a <code>SearchQuery</code> (recommended).
 *      <li>{@link #getMultiLevelSearchQuery(List,List,String,List,String,List,List,int)}
 *            as a convenience method to create a <code>SearchQuery</code>
 *      <li>{@link #searchMultiLevelVector(List,List,String,List,String,List,List,int)}
 *            to retrieve clusternodes using a constraint string.
 * </ul>
 * <p>
 * Individual nodes in a 'cluster' node can be retrieved by calling the node's
 * {@link MMObjectNode#getNodeValue(String) getNodeValue()} method, using
 * the builder name (or step alias) as argument.
 *
 * @todo XXXX. This 'builder' is actually singleton (only one instance is created).  It does
 *             therefore not support getFields, so this is more or less hacked in bridge.  Perhaps in 'core' a
 *             similar approach as now in birdge must be taken, so no ClusterBuilder, but only Virtual builders,
 *             one for every query result.
 *
 *
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @author Rob van Maris
 * @version $Id$
 * @see ClusterNode
 */
public class ClusterBuilder extends VirtualBuilder {

    /**
     * Search for all valid relations.
     * When searching relations, return both relations from source to deastination and from destination to source,
     * provided there is an allowed relation in that directon.
     * @deprecated use {@link RelationStep#DIRECTIONS_BOTH}
     *             In future versions of MMBase (1.8 and up) this will be the default value
     */
    public static final int SEARCH_BOTH = RelationStep.DIRECTIONS_BOTH;

    /**
     * Search for destinations,
     * When searching relations, return only relations from source to deastination.
     * @deprecated use {@link RelationStep#DIRECTIONS_DESTINATION}
     */
    public static final int SEARCH_DESTINATION = RelationStep.DIRECTIONS_DESTINATION;

    /**
     * Seach for sources.
     * When searching a multilevel, return only relations from destination to source, provided directionality allows
     * @deprecated use {@link RelationStep#DIRECTIONS_SOURCE}
     */
    public static final int SEARCH_SOURCE = RelationStep.DIRECTIONS_SOURCE;

    /**
     * Search for all relations.  When searching a multilevel, return both relations from source to
     * deastination and from destination to source.  Allowed relations are not checked - ALL
     * relations are used. This makes more inefficient queries, but it is not really wrong.
     * @deprecated use {@link RelationStep#DIRECTIONS_ALL}
     */
    public static final int SEARCH_ALL = RelationStep.DIRECTIONS_ALL;

    /**
     * Search for either destination or source.
     * When searching a multilevel, return either relations from source to destination OR from destination to source.
     * The returned set is decided through the typerel tabel. However, if both directions ARE somehow supported, the
     * system only returns source to destination relations.
     * This is the default value (for compatibility purposes).
     * @deprecated use {@link RelationStep#DIRECTIONS_EITHER}.
     *             In future versions of MMBase (1.8 and up) the default value will be
     *             {@link RelationStep#DIRECTIONS_BOTH}
     */
    public static final int SEARCH_EITHER = RelationStep.DIRECTIONS_EITHER;

    // logging variable
    private static final Logger log= Logging.getLoggerInstance(ClusterBuilder.class);


    /**
     * @deprecated Use {@link CoreClusterQueries.INSTANCE}
     */
    final CoreClusterQueries clusterQueries = CoreClusterQueries.INSTANCE;

    /**
     * Creates <code>ClusterBuilder</code> instance.
     * Must be called from the MMBase class.
     * @param m the MMbase cloud creating the node
     * @scope package
     */
    public ClusterBuilder(MMBase m) {
        super(m, "clusternodes");
    }

    /**
     * Translates a string to a search direction constant.
     *
     * @since MMBase-1.6
     */
    public static int getSearchDir(String search) {
        if (search == null) {
            return RelationStep.DIRECTIONS_EITHER;
        }
        return org.mmbase.bridge.util.Queries.getRelationStepDirection(search);
    }

    /**
     * Translates a search direction constant to a string.
     *
     * @since MMBase-1.6
     */
    public static String getSearchDirString(int search) {
        return ClusterQueries.getSearchDirString(search);
    }

    /**
     * Get a new node, using this builder as its parent.
     * The new node is a cluster node.
     * Unlike most other nodes, a cluster node does not have a number,
     * owner, or otype fields.
     * @param owner The administrator creating the new node (ignored).
     * @return A newly initialized <code>VirtualNode</code>.
     */
    @Override
    public MMObjectNode getNewNode(String owner) {
        throw new UnsupportedOperationException("One cannot create new ClusterNodes");
    }

    /**
     * What should a GUI display for this node.
     * This version displays the contents of the 'name' field(s) that were retrieved.
     * XXX: should be changed to something better
     * @param node The node to display
     * @return the display of the node as a <code>String</code>
     */
    @Override
    public String getGUIIndicator(MMObjectNode node) {
        // Return "name"-field when available.
        String s = node.getStringValue("name");
        if (s != null) {
            return s;
        }

        // Else "name"-fields of contained nodes.
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> entry : node.getValues().entrySet()) {
            String key = entry.getKey();
            if (key.endsWith(".name")) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(entry.getValue());
            }
        }
        if (sb.length() > 15) {
            return sb.substring(0, 12) + "...";
        } else {
            return sb.toString();
        }
    }

    /**
     * What should a GUI display for this node/field combo.
     * For a multilevel node, the builder tries to determine
     * the original builder of a field, and invoke the method using
     * that builder.
     *
     * @param node The node to display
     * @param pars Parameters, see {@link MMObjectBuilder#GUI_PARAMETERS}
     * @return the display of the node's field as a <code>String</code>, null if not specified
     */
    @Override
    public String getGUIIndicator(MMObjectNode node, Parameters pars) {

        if (node == null) throw new RuntimeException("Tried to get GUIIndicator for  " + pars + " with NULL node");

        ClusterNode clusterNode = (ClusterNode) node;

        String field = pars.getString(Parameter.FIELD);
        if (field == null) {
            return super.getGUIIndicator(node, pars);
        } else {
            int pos = field.indexOf('.');
            if (pos != -1) {
                String bulName = CoreClusterQueries.INSTANCE.getTrueTableName(field.substring(0, pos));
                MMObjectNode n = clusterNode.getRealNode(bulName);
                if (n != null) {
                    MMObjectBuilder bul= n.getBuilder();
                    if (bul != null) {
                        // what are we trying here?
                        String fieldName = field.substring(pos + 1);
                        Parameters newPars = new Parameters(pars.getDefinition(), pars);
                        newPars.set(Parameter.FIELD, fieldName);
                        newPars.set("stringvalue", null);
                        org.mmbase.bridge.Node bnode = pars.get(Parameter.NODE);
                        if (bnode != null) {
                            newPars.set(Parameter.NODE, bnode.getNodeValue(bulName));
                        }
                        newPars.set(Parameter.CORENODE, n);
                        return bul.guiFunction.getFunctionValue(newPars);
                    }
                }
            }
            return super.getGUIIndicator(node, pars);
        }
    }

    /**
     * Determines the builder part of a specified field.
     * @param fieldName the name of the field
     * @return the name of the field's builder
     */
    public String getBuilderNameFromField(String fieldName) {
        return CoreClusterQueries.INSTANCE.getBuilderNameFromField(fieldName);
    }

    /**
     * Determines the fieldname part of a specified field (without the builder name).
     * @param fieldname the name of the field
     * @return the name of the field without its builder
     */
    public static String getFieldNameFromField(String fieldname) {
        return ClusterQueries.getFieldNameFromField(fieldname);
    }

    /**
     * Return a field.
     * @param fieldName the requested field's name
     * @return the field
     */
    public FieldDefs getField(String fieldName) {
        return (FieldDefs) CoreClusterQueries.INSTANCE.getField(fieldName);
    }

    @Override
    public List<CoreField> getFields(int order) {
        throw new UnsupportedOperationException("Cluster-nodes can have any field.");
    }
    @Override
    public Collection<CoreField> getFields() {
        throw new UnsupportedOperationException("Cluster-nodes can have any field.");
    }

    /**
     * @since MMBase-1.8
     */
    public Map<String, CoreField> getFields(MMObjectNode node) {
        Map<String, CoreField> ret = new HashMap<String, CoreField>();
        Iterator<String> i = node.getValues().keySet().iterator();
        DataType<? extends Object> nodeType  = DataTypes.getDataType("node");
        while (i.hasNext()) {
            String name = i.next();
            int pos = name.indexOf(".");
            if (pos != -1) {
                String builderName = name.substring(0, pos);
                if (! ret.containsKey(builderName)) {
                    CoreField fd = Fields.createField(builderName, Field.TYPE_NODE, Field.TYPE_UNKNOWN, Field.STATE_VIRTUAL, nodeType);
                    ret.put(builderName, fd);
                }
            }
            ret.put(name, getField(name));
        }
        return ret;
    }

    /**
     * Same as {@link #searchMultiLevelVector(List,List,String,List,String,List,List,int)
     * searchMultiLevelVector(snodes, fields, pdistinct, tables, where, orderVec, direction, RelationStep.DIRECTIONS_EITHER)},
     * where <code>snodes</code> contains just the number specified by <code>snode</code>.
     *
     * @see #searchMultiLevelVector(List, List, String, List, String, List, List, List)
     */
   public Vector<MMObjectNode> searchMultiLevelVector(
        int snode,
        List<String> fields,
        String pdistinct,
        List<String> tables,
        String where,
        List<String> orderVec,
        List<String> direction) {

        List<String> v= new ArrayList<String>();
        v.add("" + snode);
        return searchMultiLevelVector(v, fields, pdistinct, tables, where, orderVec, direction, RelationStep.DIRECTIONS_EITHER);
    }

    /**
     * Same as {@link #searchMultiLevelVector(List,List,String,List,String,List,List,int)
              * searchMultiLevelVector(snodes, fields, pdistinct, tables, where, orderVec, direction, RelationStep.DIRECTIONS_EITHER)}.
     *
     * @see #searchMultiLevelVector(List,List,String,List,String,List,List,int)
     */
    public Vector<MMObjectNode> searchMultiLevelVector(List<String> snodes,
                                                       List<String> fields,
                                                       String pdistinct,
                                                       List<String> tables,
                                                       String where,
                                                       List<String> orderVec,
                                                       List<String> direction) {
        return searchMultiLevelVector(snodes, fields, pdistinct, tables, where, orderVec, direction, RelationStep.DIRECTIONS_EITHER);
    }

    /**
     * Return all the objects that match the searchkeys.
     * The constraint must be in one of the formats specified by {@link
     * QueryConvertor#setConstraint(BasicSearchQuery,String)
     * QueryConvertor#setConstraint()}.
     *
     * @param snodes The numbers of the nodes to start the search with. These have to be present in the first table
     *      listed in the tables parameter.
     * @param fields The fieldnames to return. This should include the name of the builder. Fieldnames without a builder prefix are ignored.
     *      Fieldnames are accessible in the nodes returned in the same format (i.e. with manager indication) as they are specified in this parameter.
     *      Examples: 'people.lastname'
     * @param pdistinct 'YES' indicates the records returned need to be distinct. Any other value indicates double values can be returned.
     * @param tables The builder chain. A list containing builder names.
     *      The search is formed by following the relations between successive builders in the list. It is possible to explicitly supply
     *      a relation builder by placing the name of the builder between two builders to search.
     *      Example: company,people or typedef,authrel,people.
     * @param where The constraint, must be in one of the formats specified by {@link
     *        QueryConvertor#setConstraint(BasicSearchQuery,String)
     *        QueryConvertor#setConstraint()}.
     *        E.g. "WHERE news.title LIKE '%MMBase%' AND news.title > 100"
     * @param sortFields the fieldnames on which you want to sort.
     * @param directions A list of values containing, for each field in the order parameter, a value indicating whether the sort is
     *      ascending (<code>UP</code>) or descending (<code>DOWN</code>). If less values are syupplied then there are fields in order,
     *      the first value in the list is used for the remaining fields. Default value is <code>'UP'</code>.
     * @param searchDir Specifies in which direction relations are to be
     *      followed, this must be one of the values defined by this class.
     * @return a <code>Vector</code> containing all matching nodes
     * @deprecated use {@link #searchMultiLevelVector(List snodes, List fields, String pdistinct, List tables, String where,
     *               List orderVec, List directions, List searchDirs)}
     */
    public Vector<MMObjectNode> searchMultiLevelVector(List<String> snodes, List<String> fields, String pdistinct, List<String> tables, String where, List<String> sortFields,
            List<String> directions, int searchDir) {
        List<Integer> searchDirs = new ArrayList<Integer>();
        searchDirs.add(searchDir);
        return searchMultiLevelVector(snodes, fields, pdistinct, tables, where, sortFields, directions, searchDirs);
    }

    /**
     * Return all the objects that match the searchkeys.
     * The constraint must be in one of the formats specified by {@link
     * QueryConvertor#setConstraint(BasicSearchQuery,String)
     * QueryConvertor#setConstraint()}.
     *
     * @param snodes The numbers of the nodes to start the search with. These have to be present in the first table
     *      listed in the tables parameter.
     * @param fields The fieldnames to return. This should include the name of the builder. Fieldnames without a builder prefix are ignored.
     *      Fieldnames are accessible in the nodes returned in the same format (i.e. with manager indication) as they are specified in this parameter.
     *      Examples: 'people.lastname'
     * @param pdistinct 'YES' indicates the records returned need to be distinct. Any other value indicates double values can be returned.
     * @param tables The builder chain. A list containing builder names.
     *      The search is formed by following the relations between successive builders in the list. It is possible to explicitly supply
     *      a relation builder by placing the name of the builder between two builders to search.
     *      Example: company,people or typedef,authrel,people.
     * @param where The constraint, must be in one of the formats specified by {@link
     *        QueryConvertor#setConstraint(BasicSearchQuery,String)
     *        QueryConvertor#setConstraint()}.
     *        E.g. "WHERE news.title LIKE '%MMBase%' AND news.title > 100"
     * @param sortFields the fieldnames on which you want to sort.
     * @param directions A list of values containing, for each field in the order parameter, a value indicating whether the sort is
     *      ascending (<code>UP</code>) or descending (<code>DOWN</code>). If less values are syupplied then there are fields in order,
     *      the first value in the list is used for the remaining fields. Default value is <code>'UP'</code>.
     * @param searchDirs Specifies in which direction relations are to be followed. You can specify a direction for each
     *      relation in the path. If you specify less directions than there are relations, the last specified direction is used
     *      for the remaining relations. If you specify an empty list the default direction is BOTH.
     * @return a <code>Vector</code> containing all matching nodes
     */
    public Vector<MMObjectNode> searchMultiLevelVector(List<String> snodes, List<String> fields, String pdistinct, List<String> tables, String where, List<String> sortFields,
        List<String> directions, List<Integer> searchDirs) {
        // Try to handle using the SearchQuery framework.
        try {
            SearchQuery query = getMultiLevelSearchQuery(snodes, fields, pdistinct, tables, where, sortFields, directions, searchDirs);
            List<MMObjectNode> clusterNodes = getClusterNodes(query);
            return new Vector<MMObjectNode>(clusterNodes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Executes query, returns results as {@link ClusterNode clusternodes} or MMObjectNodes if the
     * query is a Node-query.
     * This method uses the MultilevelCache for query results
     *
     * @param query The query.
     * @return The clusternodes.
     * @since MMBase-1.7
     */
    public List<MMObjectNode> getClusterNodes(SearchQuery query) {

        // start multilevel cache
        MultilevelCache multilevelCache = MultilevelCache.getCache();
        // check multilevel cache if needed
        List<MMObjectNode> resultList = null;
        if (query.getCachePolicy().checkPolicy(query)) {
            resultList = multilevelCache.get(query);
        }
        // if unavailable, obtain from database
        if (resultList == null) {
            log.debug("result list is null, getting from database");
            try {
                resultList = getClusterNodesFromQueryHandler(query);
            } catch (SearchQueryException sqe) {
                throw new BridgeException(query.toString() + ":" + sqe.getMessage(), sqe);
            }
            if (query.getCachePolicy().checkPolicy(query)) {
                multilevelCache.put(query, resultList);
            }
        }

        return resultList;
    }

    /**
     * Executes query, returns results as {@link ClusterNode clusternodes} or MMObjectNodes if the
     * query is a Node-query.
     * The results are retrieved directly from storage without the MultilevelCache
     * {@link #getClusterNodes(SearchQuery)} which uses the MultilevelCache
     *
     * @param query The query.
     * @return The clusternodes.
     * @throws org.mmbase.storage.search.SearchQueryException
     *         When an exception occurred while retrieving the results.
     * @since MMBase-1.7
     * @see org.mmbase.storage.search.SearchQueryHandler#getNodes
     */
    public List<MMObjectNode> getClusterNodesFromQueryHandler(SearchQuery query)
            throws SearchQueryException {
        // TODO (later): implement maximum set by maxNodesFromQuery?
        // Execute query, return results.

        return mmb.getSearchQueryHandler().getNodes(query, this);
    }


    /**
     * Get text from a blob field.
     * The text is cut if it is to long.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return a <code>String</code> containing the contents of a field as text
     */
    public String getShortedText(String fieldname, int number) {
        String buildername= getBuilderNameFromField(fieldname);
        if (buildername.length() > 0) {
            MMObjectBuilder bul= mmb.getMMObject(buildername);
            return bul.getShortedText(getFieldNameFromField(fieldname), bul.getNode(number));
        }
        return null;
    }

    /**
     * Get binary data of a database blob field.
     * The data is cut if it is to long.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return an array of <code>byte</code> containing the contents of a field as text
     */
    public byte[] getShortedByte(String fieldname, int number) {
        String buildername= getBuilderNameFromField(fieldname);
        if (buildername.length() > 0) {
            MMObjectBuilder bul= mmb.getMMObject(buildername);
            return bul.getShortedByte(getFieldNameFromField(fieldname), bul.getNode(number));
        }
        return null;
    }

    /**
     * Creates search query that selects all the objects that match the
     * searchkeys.
     * The constraint must be in one of the formats specified by {@link
     * QueryConvertor#setConstraint(BasicSearchQuery,String)
     * QueryConvertor#setConstraint()}.
     *
     * @param snodes <code>null</code> or a list of numbers
     *        of nodes to start the search with.
     *        These have to be present in the first table listed in the
     *        tables parameter.
     * @param fields List of fieldnames to return.
     *        These should be formatted as <em>stepalias.field</em>,
     *        e.g. 'people.lastname'
     * @param pdistinct 'YES' if the records returned need to be
     *        distinct (ignoring case).
     *        Any other value indicates double values can be returned.
     * @param tables The builder chain, a list containing builder names.
     *        The search is formed by following the relations between
     *        successive builders in the list.
     *        It is possible to explicitly supply a relation builder by
     *        placing the name of the builder between two builders to search.
     *        Example: company,people or typedef,authrel,people.
     * @param where The constraint, must be in one of the formats specified by {@link
     *        QueryConvertor#setConstraint(BasicSearchQuery,String)
     *        QueryConvertor#setConstraint()}.
     *        E.g. "WHERE news.title LIKE '%MMBase%' AND news.title > 100"
     * @param sortFields <code>null</code> or a list of  fieldnames on which you want to sort.
     * @param directions <code>null</code> or a list of values containing, for each field in the
     *        <code>sortFields</code> parameter, a value indicating whether the sort is
     *        ascending (<code>UP</code>) or descending (<code>DOWN</code>).
     *        If less values are supplied then there are fields in order,
     *        the first value in the list is used for the remaining fields.
     *        Default value is <code>'UP'</code>.
     * @param searchDir Specifies in which direction relations are to be
     *        followed, this must be one of the values defined by this class.
     * @deprecated use {@link #getMultiLevelSearchQuery(List snodes, List fields, String pdistinct, List tables, String where,
     *               List orderVec, List directions, List searchDir)}
     * @return the resulting search query.
     * @since MMBase-1.7
     */
    public BasicSearchQuery getMultiLevelSearchQuery(List<String> snodes, List<String> fields, String pdistinct, List<String> tables, String where,
            List<String> sortFields, List<String> directions, int searchDir) {
        List<Integer> searchDirs = new ArrayList<Integer>();
        searchDirs.add(searchDir);
        return getMultiLevelSearchQuery(snodes, fields, pdistinct, tables, where, sortFields, directions, searchDirs);
    }

    /**
     * Creates search query that selects all the objects that match the
     * searchkeys.
     * The constraint must be in one of the formats specified by {@link
     * QueryConvertor#setConstraint(BasicSearchQuery,String)
     * QueryConvertor#setConstraint()}.
     *
     * @param snodes <code>null</code> or a list of numbers
     *        of nodes to start the search with.
     *        These have to be present in the first table listed in the
     *        tables parameter.
     * @param fields List of fieldnames to return.
     *        These should be formatted as <em>stepalias.field</em>,
     *        e.g. 'people.lastname'
     * @param pdistinct 'YES' if the records returned need to be
     *        distinct (ignoring case).
     *        Any other value indicates double values can be returned.
     * @param tables The builder chain, a list containing builder names.
     *        The search is formed by following the relations between
     *        successive builders in the list.
     *        It is possible to explicitly supply a relation builder by
     *        placing the name of the builder between two builders to search.
     *        Example: company,people or typedef,authrel,people.
     * @param where The constraint, must be in one of the formats specified by {@link
     *        QueryConvertor#setConstraint(BasicSearchQuery,String)
     *        QueryConvertor#setConstraint()}.
     *        E.g. "WHERE news.title LIKE '%MMBase%' AND news.title > 100"
     * @param sortFields <code>null</code> or a list of  fieldnames on which you want to sort.
     * @param directions <code>null</code> or a list of values containing, for each field in the
     *        <code>sortFields</code> parameter, a value indicating whether the sort is
     *        ascending (<code>UP</code>) or descending (<code>DOWN</code>).
     *        If less values are supplied then there are fields in order,
     *        the first value in the list is used for the remaining fields.
     *        Default value is <code>'UP'</code>.
     * @param searchDirs Specifies in which direction relations are to be
     *        followed, this must be one of the values defined by this class.
     * @return the resulting search query.
     * @since MMBase-1.7
     */
    public BasicSearchQuery getMultiLevelSearchQuery(List<String> snodes, List<String> fields, String pdistinct, List<String> tables, String where,
            List<String> sortFields, List<String> directions, List<Integer> searchDirs) {

        return CoreClusterQueries.INSTANCE.getMultiLevelSearchQuery(snodes, fields, pdistinct, tables, where,
                                                                    sortFields,  directions, searchDirs);
    }

    /**
     * Creates a full chain of steps, adds these to the specified query.
     * This includes adding necessary relation tables when not explicitly
     * specified, and generating unique table aliases where necessary.
     * Optionally adds "number"-fields for all tables in the original chain.
     *
     * @param query The searchquery.
     * @param tables The original chain of tables.
     * @param roles Map of tablenames mapped to <code>Integer</code> values,
     *        representing the nodenumber of a corresponing RelDef node.
     *        This method adds entries for table aliases that specify a role,
     *        e.g. "related" or "related2".
     * @param includeAllReference Indicates if the "number"-fields must
     *        included in the query for all tables in the original chain.
     * @param fieldsByAlias Map, mapping aliases (fieldname prefixed by table
     *        alias) to the stepfields in the query. An entry is added for
     *        each stepfield added to the query.
     * @return Map, maps original table names to steps.
     * @since MMBase-1.7
     */
    // package access!
    Map<String, BasicStep> addSteps(BasicSearchQuery query, List<String> tables, Map<String, Integer> roles, boolean includeAllReference, Map<String, BasicStepField> fieldsByAlias) {
        return CoreClusterQueries.INSTANCE.addSteps(query, tables, roles, includeAllReference, fieldsByAlias);
    }

    /**
     * Gets builder corresponding to the specified table alias.
     * This amounts to removing the optionally appended digit from the table
     * alias, and interpreting the result as either a tablename or a relation
     * role.
     *
     * @param tableAlias The table alias.
     *        Must be tablename or relation role, optionally appended
     *        with a digit, e.g. images, images3, related and related4.
     * @param roles Map of tablenames mapped to <code>Integer</code> values,
     *        representing the nodenumber of a corresponing RelDef node.
     *        This method adds entries for table aliases that specify a role,
     *        e.g. "related" or "related2".
     * @return The builder.
     * @since MMBase-1.7
     */
    // package access!
    MMObjectBuilder getBuilder(String tableAlias, Map<String, Integer> roles) {
        return mmb.getBuilder(CoreClusterQueries.INSTANCE.getBuilder(tableAlias, roles));
    }

    /**
     * Retrieves fieldnames from an expression, and adds these to a search
     * query.
     * The expression may be either a fieldname or a a functionname with a
     * (commaseparated) parameterlist between parenthesis
     * (parameters being expressions themselves).
     * <p>
     * Fieldnames must be formatted as <em>stepalias.field</em>.
     *
     * @param query The query.
     * @param expression The expression.
     * @param stepsByAlias Map, mapping step aliases to the steps in the query.
     * @param fieldsByAlias Map, mapping field aliases (fieldname prefixed by
     *        table alias) to the stepfields in the query.
     *        An entry is added for each stepfield added to the query.
     * @since MMBase-1.7
     */
    // package access!
    void addFields(BasicSearchQuery query, String expression, Map<String, BasicStep> stepsByAlias, Map<String, BasicStepField> fieldsByAlias) {
        CoreClusterQueries.INSTANCE.addFields(query, expression, stepsByAlias, fieldsByAlias);
    }


    /**
     * Adds sorting orders to a search query.
     *
     * @param query The query.
     * @param fieldNames The fieldnames prefixed by the table aliases.
     * @param directions The corresponding sorting directions ("UP"/"DOWN").
     * @param fieldsByAlias Map, mapping field aliases (fieldname prefixed by
     *        table alias) to the stepfields in the query.
     * @since MMBase-1.7
     */
    // package visibility!
    void addSortOrders(BasicSearchQuery query, List<String> fieldNames, List<String> directions, Map<String, BasicStepField> fieldsByAlias) {
        CoreClusterQueries.INSTANCE.addSortOrders(query, fieldNames, directions, fieldsByAlias);
    }



    /**
     * Gets first step from list, that corresponds to the builder
     * of a specified node - or one of its parentbuilders.
     *
     * @param steps The steps.
     * @param nodeNumber The number identifying the node.
     * @return The step, or <code>null</code> when not found.
     * @since MMBase-1.7
     */
    // package visibility!
    Step getNodesStep(List<Step> steps, int nodeNumber) {
        return CoreClusterQueries.INSTANCE.getNodesStep(steps, nodeNumber);
    }

    /**
     * Adds relation directions.
     *
     * @param query The search query.
     * @param searchDirs Specifies in which direction relations are to be followed. You can specify a direction for each
     *      relation in the path. If you specify less directions than there are relations, the last specified direction is used
     *      for the remaining relations. If you specify an empty list the default direction is BOTH.
     * @param roles Map of tablenames mapped to <code>Integer</code> values,
     *        representing the nodenumber of the corresponing RelDef node.
     * @since MMBase-1.7
     */
    // package visibility!
    void addRelationDirections(BasicSearchQuery query, List<Integer> searchDirs, Map<String, Integer> roles) {
        CoreClusterQueries.INSTANCE.addRelationDirections(query, searchDirs, roles);
    }


}
