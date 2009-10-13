/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;

import org.mmbase.bridge.BridgeException;
import org.mmbase.bridge.Field;
import org.mmbase.cache.Cache;
import org.mmbase.cache.MultilevelCache;
import org.mmbase.core.CoreField;
import org.mmbase.core.util.Fields;
import org.mmbase.datatypes.DataType;
import org.mmbase.datatypes.DataTypes;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.legacy.ConstraintParser;
import org.mmbase.util.QueryConvertor;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


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
        if (search == RelationStep.DIRECTIONS_DESTINATION) {
            return "DESTINATION";
        } else if (search == RelationStep.DIRECTIONS_SOURCE) {
            return "SOURCE";
        } else if (search == RelationStep.DIRECTIONS_BOTH) {
            return "BOTH";
        } else if (search == RelationStep.DIRECTIONS_ALL) {
            return "ALL";
        } else {
            return "EITHER";
        }
    }

    /**
     * Get a new node, using this builder as its parent.
     * The new node is a cluster node.
     * Unlike most other nodes, a cluster node does not have a number,
     * owner, or otype fields.
     * @param owner The administrator creating the new node (ignored).
     * @return A newly initialized <code>VirtualNode</code>.
     */
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
    public String getGUIIndicator(MMObjectNode node) {
        // Return "name"-field when available.
        String s = node.getStringValue("name");
        if (s != null) {
            return s;
        }

        // Else "name"-fields of contained nodes.
        StringBuffer sb = new StringBuffer();
        for (Iterator i= node.getValues().entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry)i.next();
            String key = (String) entry.getKey();
            if (key.endsWith(".name")) {
                if (s.length() != 0) {
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
    public String getGUIIndicator(MMObjectNode node, Parameters pars) {

        if (node == null) throw new RuntimeException("Tried to get GUIIndicator for  " + pars + " with NULL node");

        ClusterNode clusterNode = (ClusterNode) node;

        String field = pars.getString(Parameter.FIELD);
        if (field == null) {
            return super.getGUIIndicator(node, pars);
        } else {
            int pos = field.indexOf('.');
            if (pos != -1) {
                String bulName = getTrueTableName(field.substring(0, pos));
                MMObjectNode n = clusterNode.getRealNode(bulName);
                if (n != null) {
                    MMObjectBuilder bul= n.getBuilder();
                    if (bul != null) {
                        // what are we trying here?
                        String fieldName = field.substring(pos + 1);
                        Parameters newPars = new Parameters(pars.getDefinition(), pars);
                        newPars.set(Parameter.FIELD, fieldName);
                        newPars.set("stringvalue", null);
                        org.mmbase.bridge.Node bnode = (org.mmbase.bridge.Node) pars.get(Parameter.NODE);
                        if (bnode != null) {
                            newPars.set(Parameter.NODE, bnode.getNodeValue(bulName));
                        }
                        newPars.set(Parameter.CORENODE, n);
                        return (String) bul.guiFunction.getFunctionValue(newPars);
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
        int pos = fieldName.indexOf(".");
        if (pos != -1) {
            String bulName = fieldName.substring(0, pos);
            return getTrueTableName(bulName);
        }
        return "";
    }

    /**
     * Determines the fieldname part of a specified field (without the builder name).
     * @param fieldname the name of the field
     * @return the name of the field without its builder
     */
    public static String getFieldNameFromField(String fieldname) {
        int pos= fieldname.indexOf(".");
        if (pos != -1) {
            fieldname = fieldname.substring(pos + 1);
        }
        return fieldname;
    }

    /**
     * Return a field.
     * @param fieldName the requested field's name
     * @return the field
     */
    public FieldDefs getField(String fieldName) {
        String builderName = getBuilderNameFromField(fieldName);
        if (builderName.length() > 0) {
            MMObjectBuilder bul = mmb.getBuilder(builderName);
            if (bul == null) {
                throw new RuntimeException("No builder with name '" + builderName + "' found");
            }
            return bul.getField(getFieldNameFromField(fieldName));
        } else {
            //
            MMObjectBuilder bul = mmb.getBuilder(getTrueTableName(fieldName));
            if (bul != null) {
                return new FieldDefs(fieldName, FieldDefs.TYPE_NODE, -1, FieldDefs.STATE_VIRTUAL, org.mmbase.datatypes.DataTypes.getDataType("node"));
            }
        }
        return null;
    }

    public List getFields(int order) {
        throw new UnsupportedOperationException("Cluster-nodes can have any field.");
    }
    public Collection getFields() {
        throw new UnsupportedOperationException("Cluster-nodes can have any field.");
    }

    /**
     * @since MMBase-1.8
     */
    public Map getFields(MMObjectNode node) {
        Map ret = new HashMap();
        Iterator i = node.getValues().keySet().iterator();
        DataType nodeType  = DataTypes.getDataType("node");
        while (i.hasNext()) {
            String name = (String) i.next();
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
   public Vector searchMultiLevelVector(
        int snode,
        Vector fields,
        String pdistinct,
        Vector tables,
        String where,
        Vector orderVec,
        Vector direction) {
        Vector v= new Vector();
        v.addElement("" + snode);
        return searchMultiLevelVector(v, fields, pdistinct, tables, where, orderVec, direction, RelationStep.DIRECTIONS_EITHER);
    }

    /**
     * Same as {@link #searchMultiLevelVector(List,List,String,List,String,List,List,int)
              * searchMultiLevelVector(snodes, fields, pdistinct, tables, where, orderVec, direction, RelationStep.DIRECTIONS_EITHER)}.
     *
     * @see #searchMultiLevelVector(List,List,String,List,String,List,List,int)
     */
    public Vector searchMultiLevelVector(
        Vector snodes,
        Vector fields,
        String pdistinct,
        Vector tables,
        String where,
        Vector orderVec,
        Vector direction) {
        return searchMultiLevelVector(snodes, fields, pdistinct, tables, where, orderVec, direction, RelationStep.DIRECTIONS_EITHER);
    }

    /**
     * Return all the objects that match the searchkeys.
     * The constraint must be in one of the formats specified by {@link
     * org.mmbase.util.QueryConvertor#setConstraint(BasicSearchQuery,String)
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
     *        org.mmbase.util.QueryConvertor#setConstraint(BasicSearchQuery,String)
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
    public Vector searchMultiLevelVector(List snodes, List fields, String pdistinct, List tables, String where, List sortFields,
            List directions, int searchDir) {
        List searchDirs = new ArrayList();
        searchDirs.add(new Integer(searchDir));
        return searchMultiLevelVector(snodes, fields, pdistinct, tables, where, sortFields, directions, searchDirs);
    }

    /**
     * Return all the objects that match the searchkeys.
     * The constraint must be in one of the formats specified by {@link
     * org.mmbase.util.QueryConvertor#setConstraint(BasicSearchQuery,String)
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
     *        org.mmbase.util.QueryConvertor#setConstraint(BasicSearchQuery,String)
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
    public Vector searchMultiLevelVector(List snodes, List fields, String pdistinct, List tables, String where, List sortFields,
        List directions, List searchDirs) {
        // Try to handle using the SearchQuery framework.
        try {
            SearchQuery query = getMultiLevelSearchQuery(snodes, fields, pdistinct, tables, where, sortFields, directions, searchDirs);
            List clusterNodes = getClusterNodes(query);
            return new Vector(clusterNodes);
        } catch (Exception e) {
            log.error(e + Logging.stackTrace(e));
            return null;
        }
    }

    /**
     * Executes query, returns results as {@link ClusterNode clusternodes} or MMObjectNodes if the
     * query is a Node-query.
     *
     * @param query The query.
     * @return The clusternodes.
     * @throws org.mmbase.storage.search.SearchQueryException
     *         When an exception occurred while retrieving the results.
     * @since MMBase-1.7
     * @see org.mmbase.storage.search.SearchQueryHandler#getNodes
     */
    public List getClusterNodes(SearchQuery query) {
        // start multilevel cache
        Cache multilevelCache = MultilevelCache.getCache();

        // check multilevel cache if needed
        List resultList = null;
        if (query.getCachePolicy().checkPolicy(query)) {
            resultList = (List)multilevelCache.get(query);
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

    public List getClusterNodesFromQueryHandler(SearchQuery query) throws SearchQueryException {
        // TODO (later): implement maximum set by maxNodesFromQuery?
        // Execute query, return results.

        return mmb.getSearchQueryHandler().getNodes(query, this);
    }

    /**
     * Returns the name part of a tablename.
     * The name part is the table name minus the numeric digit appended
     * to a name (if appliable).
     * @param table name of the original table
     * @return A <code>String</code> containing the table name
     */
    private String getTableName(String table) {
        int end = table.length() ;
        if (end == 0) throw new IllegalArgumentException("Table name too short '" + table + "'");
        while (Character.isDigit(table.charAt(end -1))) --end;
        return table.substring(0, end );
    }

    /**
     * Returns the name part of a tablename, and convert it to a buidler name.
     * This will catch specifying a rolename in stead of a builder name when using relations.
     * @param table name of the original table
     * @return A <code>String</code> containing the table name
     */
    private String getTrueTableName(String table) {
        String tab = getTableName(table);
        int rnumber = mmb.getRelDef().getNumberByName(tab);
        if (rnumber != -1) {
            return mmb.getRelDef().getBuilderName(new Integer(rnumber));
        } else {
            return tab;
        }
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
     * org.mmbase.util.QueryConvertor#setConstraint(BasicSearchQuery,String)
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
     *        org.mmbase.util.QueryConvertor#setConstraint(BasicSearchQuery,String)
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
     *               List orderVec, List directions, int searchDir)}
     * @return the resulting search query.
     * @since MMBase-1.7
     */
    public BasicSearchQuery getMultiLevelSearchQuery(List snodes, List fields, String pdistinct, List tables, String where,
            List sortFields, List directions, int searchDir) {
        List searchDirs = new ArrayList();
        searchDirs.add(new Integer(searchDir));
        return getMultiLevelSearchQuery(snodes, fields, pdistinct, tables, where, sortFields, directions, searchDirs);
    }

    /**
     * Creates search query that selects all the objects that match the
     * searchkeys.
     * The constraint must be in one of the formats specified by {@link
     * org.mmbase.util.QueryConvertor#setConstraint(BasicSearchQuery,String)
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
     *        org.mmbase.util.QueryConvertor#setConstraint(BasicSearchQuery,String)
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
    public BasicSearchQuery getMultiLevelSearchQuery(List snodes, List fields, String pdistinct, List tables, String where,
            List sortFields, List directions, List searchDirs) {

        // Create the query.
        BasicSearchQuery query= new BasicSearchQuery();

        // Set the distinct property.
        boolean distinct= pdistinct != null && pdistinct.equalsIgnoreCase("YES");
        query.setDistinct(distinct);

        // Get ALL tables (including missing reltables)
        Map roles= new HashMap();
        Map fieldsByAlias= new HashMap();
        Map stepsByAlias= addSteps(query, tables, roles, !distinct, fieldsByAlias);

        // Add fields.
        Iterator iFields= fields.iterator();
        while (iFields.hasNext()) {
            String field = (String) iFields.next();
            addFields(query, field, stepsByAlias, fieldsByAlias);
        }

        // Add sortorders.
        addSortOrders(query, sortFields, directions, fieldsByAlias);

        // Supporting more then 1 source node or no source node at all
        // Note that node number -1 is seen as no source node
        if (snodes != null && snodes.size() > 0) {
            Integer nodeNumber= new Integer(-1);

            // Copy list, so the original list is not affected.
            snodes= new ArrayList(snodes);

            // Go trough the whole list of strings (each representing
            // either a nodenumber or an alias), convert all to Integer objects.
            // from last to first,,... since we want snode to be the one that
            // contains the first..
            for (int i= snodes.size() - 1; i >= 0; i--) {
                String str= (String)snodes.get(i);
                try {
                    nodeNumber= new Integer(str);
                } catch (NumberFormatException e) {
                    // maybe it was not an integer, hmm lets look in OAlias
                    // table then
                    nodeNumber= new Integer(mmb.getOAlias().getNumber(str));
                    if (nodeNumber.intValue() < 0) {
                        nodeNumber= new Integer(0);
                    }
                }
                snodes.set(i, nodeNumber);
            }

            BasicStep nodesStep= getNodesStep(query.getSteps(), nodeNumber.intValue());

            if (nodesStep == null) {
                // specified a node which is not of the type of one of the steps.
                // take as default the 'first' step (which will make the result empty, compatible with 1.6, bug #6440).
                nodesStep = (BasicStep) query.getSteps().get(0);
            }

            Iterator iNodeNumbers= snodes.iterator();
            while (iNodeNumbers.hasNext()) {
                Integer number= (Integer)iNodeNumbers.next();
                nodesStep.addNode(number.intValue());
            }
        }

        addRelationDirections(query, searchDirs, roles);

        // Add constraints.
        // QueryConverter supports the old formats for backward compatibility.
        QueryConvertor.setConstraint(query, where);

        return query;
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
    Map addSteps(BasicSearchQuery query, List tables, Map roles, boolean includeAllReference, Map fieldsByAlias) {

        Map stepsByAlias= new HashMap(); // Maps original table names to steps.
        Set tableAliases= new HashSet(); // All table aliases that are in use.

        Iterator iTables= tables.iterator();
        if (iTables.hasNext()) {
            // First table.
            String tableName= (String)iTables.next();
            MMObjectBuilder bul= getBuilder(tableName, roles);
            String tableAlias= getUniqueTableAlias(tableName, tableAliases, tables);
            BasicStep step= query.addStep(bul);
            step.setAlias(tableAlias);
            stepsByAlias.put(tableName, step);
            if (includeAllReference) {
                // Add number field.
                addField(query, step, "number", fieldsByAlias);
            }
        }
        while (iTables.hasNext()) {
            String tableName2 = (String)iTables.next();
            MMObjectBuilder bul2 = getBuilder(tableName2, roles);
            BasicRelationStep relation;
            BasicStep step2;
            String tableName;
            if (bul2 instanceof InsRel) {
                // Explicit relation step.
                tableName = tableName2;
                InsRel bul = (InsRel)bul2;
                tableName2 = (String)iTables.next();
                bul2 = getBuilder(tableName2, roles);
                relation = query.addRelationStep(bul, bul2);
                step2 = (BasicStep)relation.getNext();

                // MM: setting aliases used to be _inside_ the includeAllReference-if.
                // but I don't see how that would make sense. Trying a while like this.
                relation.setAlias(tableName);
                step2.setAlias(tableName2);
                if (includeAllReference) {
                    // Add number fields.
                    addField(query, relation, "number", fieldsByAlias);
                    addField(query, step2, "number", fieldsByAlias);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Created a relation step " + relation + " (explicit)" +  roles);
                }
            } else {
                // Not a relation, relation step is implicit.
                tableName =  "insrel";
                InsRel bul = mmb.getInsRel();
                relation = query.addRelationStep(bul, bul2);
                step2 = (BasicStep)relation.getNext();
                step2.setAlias(tableName2); //see above
                if (includeAllReference) {
                    // Add number field.
                    addField(query, step2, "number", fieldsByAlias);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Created a relation step " + relation + " (implicit)");
                }
            }
            String tableAlias = getUniqueTableAlias(tableName, tableAliases, tables);
            String tableAlias2 = getUniqueTableAlias(tableName2, tableAliases, tables);
            if (! tableName.equals(tableAlias)) {
                roles.put(tableAlias, roles.get(tableName));
            }
            relation.setAlias(tableAlias);
            step2.setAlias(tableAlias2);
            stepsByAlias.put(tableAlias, relation);
            stepsByAlias.put(tableAlias2, step2);
        }
        return stepsByAlias;
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
    MMObjectBuilder getBuilder(String tableAlias, Map roles) {
        String tableName= getTableName(tableAlias);
        // check builder - should throw exception if builder doesn't exist ?
        MMObjectBuilder bul= null;
        try {
            bul= mmb.getBuilder(tableName);
        } catch (BuilderConfigurationException e) {}

        if (bul == null) {
            // check if it is a role name. if so, use the builder of the
            // rolename and store a filter on rnumber.
            int rnumber= mmb.getRelDef().getNumberByName(tableName);
            if (rnumber == -1) {
                String msg= "Specified builder " + tableName + " does not exist.";
                log.error(msg);
                throw new IllegalArgumentException(msg);
            } else {
                bul = mmb.getRelDef().getBuilder(rnumber); // relation builder
                roles.put(tableAlias, new Integer(rnumber));
            }
        } else if (bul instanceof InsRel) {
            int rnumber= mmb.getRelDef().getNumberByName(tableName);
            if (rnumber != -1) {
                roles.put(tableAlias, new Integer(rnumber));
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Resolved table alias \"" + tableAlias + "\" to builder \"" + bul.getTableName() + "\"");
        }
        return bul;
    }

    /**
     * Returns unique table alias, must be tablename/rolename, optionally
     * appended with a digit.
     * Tests the provided table alias for uniqueness, generates alternative
     * table alias if the provided alias is already in use.
     *
     * @param tableAlias The table alias.
     * @param tableAliases The table aliases that are already in use. The
     *        resulting table alias is added to this collection.
     * @param originalAliases The originally supplied aliases - generated
     *        aliases should not match any of these.
     * @return The resulting table alias.
     * @since MMBase-1.7
     */
    // package access!
    String getUniqueTableAlias(String tableAlias, Set tableAliases, Collection originalAliases) {

        // If provided alias is not unique, try alternatives,
        // skipping alternatives that are already in originalAliases.
        if (tableAliases.contains(tableAlias)) {
            tableName= getTableName(tableAlias);

            tableAlias= tableName;
            char ch= '0';
            while (originalAliases.contains(tableAlias) || tableAliases.contains(tableAlias)) {
                // Can't create more than 11 aliases for same tablename.
                if (ch > '9') {
                    throw new IndexOutOfBoundsException("Failed to create unique table alias, because there "
                                                        + "are already 11 aliases for this tablename: \"" + tableName + "\"");
                }
                tableAlias = tableName + ch;
                ch++;
            }
        }

        // Unique table alias: add to collection, return as result.
        tableAliases.add(tableAlias);
        return tableAlias;
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
    void addFields(BasicSearchQuery query, String expression, Map stepsByAlias, Map fieldsByAlias) {

        // TODO RvM: stripping functions is this (still) necessary?.
        // Strip function(s).
        int pos1= expression.indexOf('(');
        int pos2= expression.indexOf(')');
        if (pos1 != -1 ^ pos2 != -1) {
            // Parenthesis do not match.
            throw new IllegalArgumentException("Parenthesis do not match in expression: \"" + expression + "\"");
        } else if (pos1 != -1) {
            // Function parameter list containing subexpression(s).
            String parameters= expression.substring(pos1 + 1, pos2);
            Iterator iParameters= getFunctionParameters(parameters).iterator();
            while (iParameters.hasNext()) {
                String parameter= (String)iParameters.next();
                addFields(query, parameter, stepsByAlias, fieldsByAlias);
            }
        } else if (!Character.isDigit(expression.charAt(0))) {
            int pos= expression.indexOf('.');
            if (pos < 1 || pos == (expression.length() - 1)) {
                throw new IllegalArgumentException("Invalid fieldname: \"" + expression + "\"");
            }
            int bracketOffset = (expression.startsWith("[") && expression.endsWith("]")) ? 1 : 0;
            String stepAlias= expression.substring(0 + bracketOffset, pos);
            String fieldName= expression.substring(pos + 1 - bracketOffset);

            BasicStep step = (BasicStep)stepsByAlias.get(stepAlias);
            if (step == null) {
                throw new IllegalArgumentException("Invalid step alias: \"" + stepAlias + "\" in fields list");
            }
            addField(query, step, fieldName, fieldsByAlias);
        }
    }

    /**
     * Adds field to a search query, unless it is already added.
     *
     * @param query The query.
     * @param step The non-null step corresponding to the field.
     * @param fieldName The fieldname.
     * @param fieldsByAlias Map, mapping field aliases (fieldname prefixed by
     *        table alias) to the stepfields in the query.
     *        An entry is added for each stepfield added to the query.
     * @since MMBase-1.7
     */
    private void addField(BasicSearchQuery query, BasicStep step, String fieldName, Map fieldsByAlias) {

        // Fieldalias = stepalias.fieldname.
        // This value is used to store the field in fieldsByAlias.
        // The actual alias of the field is not set.
        String fieldAlias= step.getAlias() + "." + fieldName;
        if (fieldsByAlias.containsKey(fieldAlias)) {
            // Added already.
            return;
        }

        MMObjectBuilder builder= mmb.getBuilder(step.getTableName());
        CoreField fieldDefs= builder.getField(fieldName);
        if (fieldDefs == null) {
            throw new IllegalArgumentException("Not a known field of builder " + step.getTableName() + ": \"" + fieldName + "\"");
        }

        // Add the stepfield.
        BasicStepField stepField= query.addField(step, fieldDefs);
        fieldsByAlias.put(fieldAlias, stepField);
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
    void addSortOrders(BasicSearchQuery query, List fieldNames, List directions, Map fieldsByAlias) {

        // Test if fieldnames are specified.
        if (fieldNames == null || fieldNames.size() == 0) {
            return;
        }

        int defaultSortOrder= SortOrder.ORDER_ASCENDING;
        if (directions != null && directions.size() != 0) {
            if (((String)directions.get(0)).trim().equalsIgnoreCase("DOWN")) {
                defaultSortOrder= SortOrder.ORDER_DESCENDING;
            }
        }

        Iterator iFieldNames= fieldNames.iterator();
        Iterator iDirections= directions.iterator();
        while (iFieldNames.hasNext()) {
            String fieldName= (String)iFieldNames.next();
            StepField field= (BasicStepField)fieldsByAlias.get(fieldName);
            if (field == null) {
                // Field has not been added.
                field= ConstraintParser.getField(fieldName, query.getSteps());
            }
            if (field == null) {
                throw new IllegalArgumentException("Invalid fieldname: \"" + fieldName + "\"");
            }

            // Add sort order.
            BasicSortOrder sortOrder= query.addSortOrder(field); // ascending

            // Change direction if needed.
            if (iDirections.hasNext()) {
                String direction= (String)iDirections.next();
                if (direction.trim().equalsIgnoreCase("DOWN")) {
                    sortOrder.setDirection(SortOrder.ORDER_DESCENDING);
                } else if (!direction.trim().equalsIgnoreCase("UP")) {
                    throw new IllegalArgumentException("Parameter directions contains an invalid value ("+direction+"), should be UP or DOWN.");
                }

            } else {
                sortOrder.setDirection(defaultSortOrder);
            }
        }
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
    BasicStep getNodesStep(List steps, int nodeNumber) {
        if (nodeNumber < 0) {
            return null;
        }

        MMObjectNode node= getNode(nodeNumber);
        if (node == null) {
            return null;
        }

        MMObjectBuilder builder = node.parent;
        BasicStep result = null;
        do {
            // Find step corresponding to builder.
            Iterator iSteps= steps.iterator();
            while (iSteps.hasNext() && result == null) {
                BasicStep step= (BasicStep)iSteps.next();
                if (step.getTableName().equals(builder.tableName)) {  // should inheritance not be considered?
                    // Found.
                    result = step;
                }
            }
            // Not found, then try again with parentbuilder.
            builder = builder.getParentBuilder();
        } while (builder != null && result == null);

        /*
          if (result == null) {
          throw new RuntimeException("Node '" + nodeNumber + "' not of one of the types " + steps);
          }
        */

        return result;
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
    void addRelationDirections(BasicSearchQuery query, List searchDirs, Map roles) {

        Iterator iSteps = query.getSteps().iterator();
        Iterator iSearchDirs = searchDirs.iterator();
        int searchDir = RelationStep.DIRECTIONS_BOTH;

        if (! iSteps.hasNext()) return; // nothing to be done.
        BasicStep sourceStep = (BasicStep)iSteps.next();
        BasicStep destinationStep = null;

        while (iSteps.hasNext()) {
            if (destinationStep != null) {
                sourceStep = destinationStep;
            }
            BasicRelationStep relationStep= (BasicRelationStep)iSteps.next();
            destinationStep= (BasicStep)iSteps.next();
            if (iSearchDirs.hasNext()) searchDir = ((Integer)iSearchDirs.next()).intValue();

            // Determine typedef number of the source-type.
            int sourceType = sourceStep.getBuilder().getObjectType();

            // Determine reldef number of the role.
            Integer role = (Integer) roles.get(relationStep.getAlias());

            // Determine the typedef number of the destination-type.
            int destinationType = destinationStep.getBuilder().getObjectType();

            int roleInt;
            if (role != null) {
                roleInt =  role.intValue();
                relationStep.setRole(role);
            } else {
                roleInt = -1;
            }

            if (!mmb.getTypeRel().optimizeRelationStep(relationStep, sourceType, destinationType, roleInt, searchDir)) {
                if (searchDir != RelationStep.DIRECTIONS_SOURCE && searchDir != RelationStep.DIRECTIONS_DESTINATION) {
                    log.warn("No relation defined between " + sourceStep.getTableName() + " and " + destinationStep.getTableName() + " using " + relationStep + " with direction(s) " + getSearchDirString(searchDir) + ". Searching in 'destination' direction now, but perhaps the query should be fixed, because this should always result nothing.");
                } else {
                    log.warn("No relation defined between " + sourceStep.getTableName() + " and " + destinationStep.getTableName() + " using " + relationStep + " with direction(s) " + getSearchDirString(searchDir) + ". Trying anyway, but perhaps the query should be fixed, because this should always result nothing.");
                }
                log.warn(Logging.applicationStacktrace());
            }

        }
    }

}
