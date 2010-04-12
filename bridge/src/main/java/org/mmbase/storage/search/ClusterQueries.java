/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.*;
import org.mmbase.bridge.Field;
import org.mmbase.storage.search.implementation.*;

import org.mmbase.util.logging.*;

/**
 *
 * @author Rob van Maris (ClusterBuilder)
 * @version $Id$
 */
public abstract class ClusterQueries {


    private static final Logger log= Logging.getLoggerInstance(ClusterQueries.class);


    protected abstract QueryContext getQueryContext();
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

    protected abstract Field getNodeField(String name);

    /**
     * Return a field.
     * @param fieldName the requested field's name
     * @return the field
     */
    public Field getField(String fieldName) {
        String builderName = getBuilderNameFromField(fieldName);
        if (builderName.length() > 0) {
            return getQueryContext().getField(builderName, getFieldNameFromField(fieldName));
        } else {
            //
            String bul = getTrueTableName(fieldName);
            if (bul != null) {
                return getNodeField(fieldName);
            }
        }
        return null;
    }



    /**
     * Returns the name part of a tablename.
     * The name part is the table name minus the numeric digit appended
     * to a name (if appliable).
     * @param table name of the original table
     * @return A <code>String</code> containing the table name
     */
    protected String getTableName(String table) {
        int end = table.length() ;
        if (end == 0) throw new IllegalArgumentException("Table name too short '" + table + "'");
        while (Character.isDigit(table.charAt(end - 1))) {
            --end;
        }
        return table.substring(0, end );
    }


    /**
     * Returns the name part of a tablename, and convert it to a buidler name.
     * This will catch specifying a rolename in stead of a builder name when using relations.
     * @param table name of the original table
     * @return A <code>String</code> containing the table name
     */
    protected abstract String getTrueTableName(String table);


    protected abstract int getNumberForAlias(String alias);

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

        // Create the query.
        BasicSearchQuery query= new BasicSearchQuery();

        // Set the distinct property.
        boolean distinct= pdistinct != null && pdistinct.equalsIgnoreCase("YES");
        query.setDistinct(distinct);

        // Get ALL tables (including missing reltables)
        Map<String, Integer> roles = new HashMap<String, Integer>();
        Map<String, BasicStepField> fieldsByAlias= new HashMap<String, BasicStepField>();
        Map<String, BasicStep> stepsByAlias= addSteps(query, tables, roles, !distinct, fieldsByAlias);

        // Add fields.
        Iterator<String> iFields= fields.iterator();
        while (iFields.hasNext()) {
            String field = iFields.next();
            addFields(query, field, stepsByAlias, fieldsByAlias);
        }

        // Add sortorders.
        addSortOrders(query, sortFields, directions, fieldsByAlias);

        // Supporting more then 1 source node or no source node at all
        // Note that node number -1 is seen as no source node
        if (snodes != null && snodes.size() > 0) {
            Integer nodeNumber= -1;

            // Copy list, so the original list is not affected.
            List<Integer> snodeNumbers = new ArrayList<Integer>();

            // Go trough the whole list of strings (each representing
            // either a nodenumber or an alias), convert all to Integer objects.
            // from last to first,,... since we want snode to be the one that
            // contains the first..
            for (int i= snodes.size() - 1; i >= 0; i--) {
                String str= snodes.get(i);
                try {
                    nodeNumber= Integer.valueOf(str);
                } catch (NumberFormatException e) {
                    // maybe it was not an integer, hmm lets look in OAlias
                    // table then
                    nodeNumber= getNumberForAlias(str);
                }
                snodeNumbers.add(nodeNumber);
            }

            Step nodesStep = getNodesStep(query.getSteps(), nodeNumber.intValue());

            if (nodesStep == null) {
                // specified a node which is not of the type of one of the steps.
                // take as default the 'first' step (which will make the result empty, compatible with 1.6, bug #6440).
                nodesStep = query.getSteps().get(0);
            }

            Iterator<Integer> iNodeNumbers= snodeNumbers.iterator();
            while (iNodeNumbers.hasNext()) {
                Integer number= iNodeNumbers.next();
                nodesStep.addNode(number.intValue());
            }
        }

        addRelationDirections(query, searchDirs, roles);

        setConstraint(query, where);

        return query;
    }

    protected abstract void setConstraint(BasicSearchQuery query, String where);


    protected abstract boolean isRelation(String builder);

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
    public Map<String, BasicStep> addSteps(BasicSearchQuery query, List<String> tables, Map<String, Integer> roles, boolean includeAllReference, Map<String, BasicStepField> fieldsByAlias) {

        final Map<String, BasicStep> stepsByAlias= new HashMap<String, BasicStep>(); // Maps original table names to steps.
        final Set<String> tableAliases= new HashSet<String>(); // All table aliases that are in use.

        Iterator<String> iTables= tables.iterator();
        if (iTables.hasNext()) {
            // First table.
            String tableName= iTables.next();
            String bul= getBuilder(tableName, roles);
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
            String tableName2 = iTables.next();
            String bul2 = getBuilder(tableName2, roles);
            BasicRelationStep relation;
            BasicStep step2;
            String tableName;
            if (isRelation(bul2)) {
                // Explicit relation step.
                tableName = tableName2;
                String bul = bul2;
                tableName2 = iTables.next();
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
                String bul = "insrel";
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
    abstract protected String getBuilder(String tableAlias, Map<String, Integer> roles);


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
    protected String getUniqueTableAlias(String tableAlias, Set<String> tableAliases, Collection<String> originalAliases) {

        // If provided alias is not unique, try alternatives,
        // skipping alternatives that are already in originalAliases.
        if (tableAliases.contains(tableAlias)) {
            String tableName= getTableName(tableAlias);

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
    public void addFields(BasicSearchQuery query, String expression, Map<String, BasicStep> stepsByAlias, Map<String, BasicStepField> fieldsByAlias) {

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
            Iterator<String> iParameters= org.mmbase.util.functions.Utils.parse(parameters).iterator();
            while (iParameters.hasNext()) {
                String parameter= iParameters.next();
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

            BasicStep step = stepsByAlias.get(stepAlias);
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
    private void addField(BasicSearchQuery query, BasicStep step, String fieldName, Map<String, BasicStepField> fieldsByAlias) {

        // Fieldalias = stepalias.fieldname.
        // This value is used to store the field in fieldsByAlias.
        // The actual alias of the field is not set.
        String fieldAlias= step.getAlias() + "." + fieldName;
        if (fieldsByAlias.containsKey(fieldAlias)) {
            // Added already.
            return;
        }

        Field fieldDefs= getQueryContext().getField(step.getTableName(), fieldName);
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
    public void addSortOrders(BasicSearchQuery query, List<String> fieldNames, List<String> directions, Map<String, BasicStepField> fieldsByAlias) {

        // Test if fieldnames are specified.
        if (fieldNames == null || fieldNames.size() == 0) {
            return;
        }

        int defaultSortOrder= SortOrder.ORDER_ASCENDING;
        if (directions != null && directions.size() != 0) {
            if (directions.get(0).trim().equalsIgnoreCase("DOWN")) {
                defaultSortOrder= SortOrder.ORDER_DESCENDING;
            }
        }

        Iterator<String> iFieldNames= fieldNames.iterator();
        Iterator<String> iDirections= directions.iterator();
        while (iFieldNames.hasNext()) {
            String fieldName = iFieldNames.next();
            StepField field= fieldsByAlias.get(fieldName);
            if (field == null) {
                // Field has not been added.
                field = getField(fieldName, query);
            }
            if (field == null) {
                throw new IllegalArgumentException("Invalid fieldname: \"" + fieldName + "\"");
            }

            // Add sort order.
            BasicSortOrder sortOrder= query.addSortOrder(field); // ascending

            // Change direction if needed.
            if (iDirections.hasNext()) {
                String direction = iDirections.next();
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

    protected abstract StepField getField(String fieldName, SearchQuery query);

    protected abstract String getBuilder(int nodeNumber);
    protected abstract int getBuilderNumber(String builderName);
    protected abstract String getParentBuilder(String builder);

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
    public Step getNodesStep(List<Step> steps, int nodeNumber) {
        if (nodeNumber < 0) {
            return null;
        }

        String builder = getBuilder(nodeNumber);
        if (builder == null) {
            return null;
        }
        Step result = null;
        do {
            // Find step corresponding to builder.
            Iterator<Step> iSteps= steps.iterator();
            while (iSteps.hasNext() && result == null) {
                Step step= iSteps.next();
                if (step.getTableName().equals(builder)) {  // should inheritance not be considered?
                    // Found.
                    result = step;
                }
            }
            // Not found, then try again with parentbuilder.
            builder = getParentBuilder(builder);
        } while (builder != null && result == null);

        return result;
    }


    protected abstract boolean optimizeRelationStep(RelationStep rs, int sourceType, int destType, int role, int searchDir);

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
    public void addRelationDirections(BasicSearchQuery query, List<Integer> searchDirs, Map<String, Integer> roles) {

        Iterator<Step> iSteps = query.getSteps().iterator();
        Iterator<Integer> iSearchDirs = searchDirs.iterator();
        int searchDir = RelationStep.DIRECTIONS_BOTH;

        if (! iSteps.hasNext()) return; // nothing to be done.
        Step sourceStep = iSteps.next();
        Step destinationStep = null;

        while (iSteps.hasNext()) {
            if (destinationStep != null) {
                sourceStep = destinationStep;
            }
            BasicRelationStep relationStep= (BasicRelationStep)iSteps.next();
            destinationStep= iSteps.next();
            if (iSearchDirs.hasNext()) searchDir = iSearchDirs.next().intValue();


            // FIXME this cast to BasicStep is ugly and should not be here in a clean implementation

            // Determine typedef number of the source-type.
            int sourceType = getBuilderNumber(sourceStep.getTableName());
            // Determine the typedef number of the destination-type.
            int destinationType = getBuilderNumber(destinationStep.getTableName());

            // Determine reldef number of the role.
            Integer role = roles.get(relationStep.getAlias());

            int roleInt;
            if (role != null) {
                roleInt =  role.intValue();
                relationStep.setRole(role);
            } else {
                roleInt = -1;
            }

            if (!optimizeRelationStep(relationStep, sourceType, destinationType, roleInt, searchDir)) {
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
