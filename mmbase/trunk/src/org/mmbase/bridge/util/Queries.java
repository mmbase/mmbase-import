/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.BasicQuery;
import org.mmbase.module.core.*;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;
import org.mmbase.storage.search.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * This class contains various utility methods for manipulating and creating query objecs. Most
 * essential methods are available on the Query object itself, but too specific or legacy-ish
 * methods are put here.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Queries.java,v 1.22 2004-02-12 15:55:56 keesj Exp $
 * @see  org.mmbase.bridge.Query
 * @since MMBase-1.7
 */
public class Queries {
    private static final Logger log = Logging.getLoggerInstance(Queries.class);

    /**
     * Creates a Query object using arguments for {@link Cloud#getList} (this function is of course
     * implemented using this utility). This is usefull to convert (legacy) code which uses
     * getList, but you want to use new Query features without rewriting the complete thing.
     *
     * It can also be simply handy to specify things as Strings.
     */
    public static Query createQuery(Cloud cloud, String startNodes, String nodePath, String fields, String constraints, String orderby, String directions, String searchDir, boolean distinct) {

        // the bridge test case say that you may also specifiy empty string (why?)
        if ("".equals(startNodes)) {
            startNodes = null;
        }
        if ("".equals(fields)) {
            fields = null;
        }
        if ("".equals(constraints)) {
            constraints = null;
        }
        if ("".equals(searchDir)) {
            searchDir = null;
        }
        // check invalid search command
        Encode encoder = new Encode("ESCAPE_SINGLE_QUOTE");
        // if(startNodes != null) startNodes = encoder.encode(startNodes);
        // if(nodePath != null) nodePath = encoder.encode(nodePath);
        // if(fields != null) fields = encoder.encode(fields);
        if (orderby != null) {
            orderby = encoder.encode(orderby);
        }
        if (directions != null) {
            directions = encoder.encode(directions);
        }
        if (searchDir != null) {
            searchDir = encoder.encode(searchDir);
        }
        if (constraints != null) {
            constraints = convertClauseToDBS(constraints);
            if (!validConstraints(constraints)) {
                throw new BridgeException("invalid constraints:" + constraints);
            }
        }

        // create query object
        //TODO: remove this code... classes under org.mmbase.bridge.util must not use the core
        ClusterBuilder clusterBuilder = MMBase.getMMBase().getClusterBuilder();
        int search = -1;
        if (searchDir != null) {
            search = ClusterBuilder.getSearchDir(searchDir);
        }

        List snodes = StringSplitter.split(startNodes);
        List tables = StringSplitter.split(nodePath);
        List f = StringSplitter.split(fields);
        List orderVec = StringSplitter.split(orderby);
        List d = StringSplitter.split(directions);
        try {
            // pitty that we can't use cloud.createQuery for this.
            // but all essential methods are on ClusterBuilder
            Query query = new BasicQuery(cloud, clusterBuilder.getMultiLevelSearchQuery(snodes, f, distinct ? "YES" : "NO", tables, constraints, orderVec, d, search));
            return query;
        } catch (IllegalArgumentException iae) {
            throw new BridgeException(iae);
        }
    }

    /**
     * returns false, when escaping wasnt closed, or when a ";" was found outside a escaped part (to prefent spoofing)
     * This is used by createQuery (i wonder if it still makes sense)
     */
    static private boolean validConstraints(String constraints) {
        // first remove all the escaped "'" ('' occurences) chars...
        String remaining = constraints;
        while (remaining.indexOf("''") != -1) {
            int start = remaining.indexOf("''");
            int stop = start + 2;
            if (stop < remaining.length()) {
                String begin = remaining.substring(0, start);
                String end = remaining.substring(stop);
                remaining = begin + end;
            } else {
                remaining = remaining.substring(0, start);
            }
        }
        // assume we are not escaping... and search the string..
        // Keep in mind that at this point, the remaining string could contain different information
        // than the original string. This doesnt matter for the next sequence...
        // but it is important to realize!
        while (remaining.length() > 0) {
            if (remaining.indexOf('\'') != -1) {
                // we still contain a "'"
                int start = remaining.indexOf('\'');

                // escaping started, but no stop
                if (start == remaining.length()) {
                    log.warn("reached end, but we are still escaping(you should sql-escape the search query inside the jsp-page?)\noriginal:" + constraints);
                    return false;
                }

                String notEscaped = remaining.substring(0, start);
                if (notEscaped.indexOf(';') != -1) {
                    log.warn("found a ';' outside the constraints(you should sql-escape the search query inside the jsp-page?)\noriginal:" + constraints + "\nnot excaped:" + notEscaped);
                    return false;
                }

                int stop = remaining.substring(start + 1).indexOf('\'');
                if (stop < 0) {
                    log.warn("reached end, but we are still escaping(you should sql-escape the search query inside the jsp-page?)\noriginal:" + constraints + "\nlast escaping:" + remaining.substring(start + 1));
                    return false;
                }
                // we added one to to start, thus also add this one to stop...
                stop = start + stop + 1;

                // when the last character was the stop of our escaping
                if (stop == remaining.length()) {
                    return true;
                }

                // cut the escaped part from the string, and continue with resting sting...
                remaining = remaining.substring(stop + 1);
            } else {
                if (remaining.indexOf(';') != -1) {
                    log.warn("found a ';' inside our constrain:" + constraints);
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    /**
     * Converts a constraint by turning all 'quoted' fields into
     * database supported fields.
     * XXX: todo: escape characters for '[' and ']'.
     */
    private static String convertClausePartToDBS(String constraints) {
        // obtain dbs for fieldname checks

        //TODO: remove this code... classes under org.mmbase.bridge.util must not use the core
        MMJdbc2NodeInterface dbs = MMBase.getMMBase().getDatabase();
        StringBuffer result = new StringBuffer();
        int posa = constraints.indexOf('[');
        while (posa > -1) {
            int posb = constraints.indexOf(']', posa);
            if (posb == -1) {
                posa = -1;
            } else {
                String fieldName = constraints.substring(posa + 1, posb);
                int posc = fieldName.indexOf('.');
                if (posc == -1) {
                    fieldName = dbs.getAllowedField(fieldName);
                } else {
                    fieldName = fieldName.substring(0, posc + 1) + dbs.getAllowedField(fieldName.substring(posc + 1));
                }
                result.append(constraints.substring(0, posa)).append(fieldName);
                constraints = constraints.substring(posb + 1);
                posa = constraints.indexOf('[');
            }
        }
        result.append(constraints);
        return result.toString();
    }

    /**
     * Converts a constraint by turning all 'quoted' fields into
     * database supported fields.
     * XXX: todo: escape characters for '[' and ']'.
     */
    private static String convertClauseToDBS(String constraints) {
        if (constraints.startsWith("MMNODE")) {
            //  wil probably not work
            // @todo check
            return constraints;
        } else if (constraints.startsWith("ALTA")) {
            //  wil probably not work
            // @todo check
            return constraints.substring(5);
        } else if (!constraints.substring(0, 5).equalsIgnoreCase("WHERE")) {
            // Must start with "WHERE "
            constraints = "WHERE " + constraints;
        }

        //keesj: here constraints will start with WHERE,ALTA or MMNODE

        //keesj: what does this code do?

        StringBuffer result = new StringBuffer();
        //if there is a quote in the constraints posa will not be equals -1

        int posa = constraints.indexOf('\'');
        while (posa > -1) {
            //keesj: posb can be the same a posa maybe the method should read indexOf("\"",posa) ?
            int posb = constraints.indexOf('\'', 1);
            if (posb == -1) {
                posa = -1;
            } else {
                //keesj:part is now the first part of the constraints if there is a quote in the query
                String part = constraints.substring(0, posa);

                //append to the string buffer "part" the first part
                result.append(convertClausePartToDBS(part)).append(constraints.substring(posa, posb + 1));

                //keesj:obfucation contest?
                constraints = constraints.substring(posb + 1);
                posa = constraints.indexOf('\'');
            }
        }
        result.append(convertClausePartToDBS(constraints));
        return result.toString();
    }

    /**
     * Adds a 'legacy' constraint to the query. Alreading existing constraints remain ('AND' is used)
     * @return the new constraint, or null if nothing changed added.
     */
    public static Constraint addConstraints(Query query, String constraints) {
        if (constraints == null || constraints.equals("")) {
            return null;
        }
        constraints = convertClauseToDBS(constraints);
        if (!validConstraints(constraints)) {
            throw new BridgeException("invalid constraints:" + constraints);
        }
        // Before converting to legacy constraint,
        // the leading "WHERE" must be skipped when present.
        if (constraints.substring(0, 5).equalsIgnoreCase("WHERE")) {
            constraints = constraints.substring(5).trim();
        }
        Constraint newConstraint = query.createConstraint(constraints);
        addConstraint(query, newConstraint);
        return newConstraint;
    }

    /**
     * Adds a Constraint to the already present constraint (with AND)
     */
    public static Constraint addConstraint(Query query, Constraint newConstraint) {
        if (newConstraint == null)
            return null;

        Constraint constraint = query.getConstraint();

        if (constraint != null) {
            log.debug("compositing constraint");
            Constraint compConstraint = query.createConstraint(constraint, CompositeConstraint.LOGICAL_AND, newConstraint);
            query.setConstraint(compConstraint);
            return compConstraint;
        } else {
            query.setConstraint(newConstraint);
            return newConstraint;
        }
    }

    public static final int OPERATOR_BETWEEN = -1; // not a FieldCompareConstraint (numeric)
    public static final int OPERATOR_IN = 10000; // not a FieldCompareConstraint (non numeric)

    /**
     * Creates a operator constant for use by createConstraint
     * @see #createConstraint
     */
    public static int getOperator(String s) {
        String op = s.toUpperCase();
        // first: determin operator:
        if (op.equals("<") || op.equals("LESS")) {
            return FieldCompareConstraint.LESS;
        } else if (op.equals("<=") || op.equals("LESS_EQUAL")) {
            return FieldCompareConstraint.LESS_EQUAL;
        } else if (op.equals("=") || op.equals("EQUAL") || op.equals("")) {
            return FieldCompareConstraint.EQUAL;
        } else if (op.equals(">") || op.equals("GREATER")) {
            return FieldCompareConstraint.GREATER;
        } else if (op.equals(">=") || op.equals("GREATER_EQUAL")) {
            return FieldCompareConstraint.GREATER_EQUAL;
        } else if (op.equals("LIKE")) {
            return FieldCompareConstraint.LIKE;
        } else if (op.equals("BETWEEN")) {
            return OPERATOR_BETWEEN;
        } else if (op.equals("IN")) {
            return OPERATOR_IN;
            //} else if (op.equals("~") || op.equals("REGEXP")) {
            //  return FieldCompareConstraint.REGEXP;
        } else {
            throw new BridgeException("Unknown Field Compare Operator '" + op + "'");
        }

    }
    /**
     * Used in implementation of createConstraint
     */
    protected static Number getNumberValue(String stringValue) throws BridgeException {
        try {
            return new Integer(stringValue);
        } catch (NumberFormatException e) {
            try {
                return new Double(stringValue);
            } catch (NumberFormatException e2) {
                throw new BridgeException("Operator requires number value ('" + stringValue + "' is not)");
            }
        }
    }

    /**
     * Used in implementation of createConstraint
     */
    protected static Object getCompareValue(int fieldType, int operator, Object value) {

        if (fieldType != Field.TYPE_STRING && fieldType != Field.TYPE_XML && operator < FieldCompareConstraint.LIKE) { // numeric compare
            if (value instanceof Number) {
                return value;
            } else {
                return getNumberValue(Casting.toString(value));
            }
        } else {
            if (operator == OPERATOR_IN) {
                SortedSet set;
                if (value instanceof SortedSet) {
                    set = (SortedSet)value;
                } else if (value instanceof NodeList) {
                    set = new TreeSet();
                    NodeIterator i = ((NodeList)value).nodeIterator();
                    while (i.hasNext()) {
                        Node node = i.nextNode();
                        set.add(new Integer(node.getNumber()));
                    }
                } else if (value instanceof Collection) {
                    set = new TreeSet();
                    Iterator i = ((Collection)value).iterator();
                    while (i.hasNext()) {
                        Object o = i.next();
                        set.add(getCompareValue(fieldType, FieldCompareConstraint.EQUAL, o));
                    }
                } else {
                    set = new TreeSet();
                    if (!(value == null || value.equals(""))) {
                        set.add(getCompareValue(fieldType, FieldCompareConstraint.EQUAL, value));
                    }
                }
                value = set;
            }
            return value;
        }
    }

    /**
     * Defaulting version of {@link #createConstraint(Query, String, int, Object, Object, boolean)}.
     * Casesensitivity defaults to false, value2 to null (so 'BETWEEN' cannot be used).
     */
    public static Constraint createConstraint(Query query, String fieldName, int operator, Object value) {
        return createConstraint(query, fieldName, operator, value, null, false);
    }
    /**
     * Creates a constraint smartly, depending on the type of the field, the value is casted to the
     * right type, and the right type of constraint is created.
     * This is used in taglib implementation, but could be useful more generally.
     *
     * @param query      The query to create the constraint for
     * @param fieldName  The field to create the constraint on (as a string, so it can include the step), e.g. 'news.number'
     * @param operator   The operator to use. This constant can be produces from a string using {@link #getOperator(String)}.
     * @param value      The value to compare with, which must be of the right type. If field is number it might also be an alias.
     * @param value2     The other value (only relevant if operator is BETWEEN, the only terniary operator)
     * @param caseSensitive  Whether it should happen case sensitively (not relevant for number fields)
     * @return The new constraint, or <code>null</code> it by chance the specified arguments did not lead to a new actual constraint (e.g. if value is an empty set)
     */

    public static Constraint createConstraint(Query query, String fieldName, int operator, Object value, Object value2, boolean caseSensitive) {

        StepField stepField = query.createStepField(fieldName);
        if (stepField == null)
            throw new BridgeException("Could not create stepfield with '" + fieldName + "'");

        Cloud cloud = query.getCloud();
        FieldConstraint newConstraint;

        if (value instanceof StepField) {
            newConstraint = query.createConstraint(stepField, operator, (StepField)value);
        } else {
            int fieldType = cloud.getNodeManager(stepField.getStep().getTableName()).getField(stepField.getFieldName()).getType();

            if (fieldName.equals("number")) {
                if (value instanceof String) { // it might be an alias!
                    Node node = cloud.getNode((String)value);
                    value = new Integer(node.getNumber());
                }
            }

            Object compareValue = getCompareValue(fieldType, operator, value);

            if (operator > 0 && operator < OPERATOR_IN) {
                newConstraint = query.createConstraint(stepField, operator, compareValue);
            } else {
                switch (operator) {
                    case OPERATOR_BETWEEN :
                        Object compareValue2 = getCompareValue(fieldType, operator, value2);
                        newConstraint = query.createConstraint(stepField, compareValue, compareValue2);
                        break;
                    case OPERATOR_IN :
                        newConstraint = query.createConstraint(stepField, (SortedSet)compareValue);
                        break;
                    default :
                        throw new RuntimeException("Unknown value for operation " + operator);
                }
            }
        }
        query.setCaseSensitive(newConstraint, caseSensitive);
        return newConstraint;

    }

    /**
     * Adds sort orders to the query, using two strings. Like in 'getList' of Cloud. Several tag-attributes need this.
     *
     * @todo implement for normal query.
     * @return The new sort orders
     */
    public static List addSortOrders(Query query, String sorted, String directions) {
        // following code was copied from MMObjectBuilder.setSearchQuery (bit ugly)
        if (sorted == null)
            return query.getSortOrders().subList(0, 0);
        if (directions == null) {
            directions = "";
        }
        List list = query.getSortOrders();
        int initialSize = list.size();

        StringTokenizer sortedTokenizer = new StringTokenizer(sorted, ",");
        StringTokenizer directionsTokenizer = new StringTokenizer(directions, ",");

        while (sortedTokenizer.hasMoreTokens()) {
            String fieldName = sortedTokenizer.nextToken().trim();
            int dot = fieldName.indexOf('.');

            StepField sf;
            if (dot == -1 && query instanceof NodeQuery) {
                NodeManager nodeManager = ((NodeQuery)query).getNodeManager();
                sf = ((NodeQuery)query).getStepField(nodeManager.getField(fieldName));
            } else {
                sf = query.createStepField(fieldName);
            }

            int dir = SortOrder.ORDER_ASCENDING;
            if (directionsTokenizer.hasMoreTokens()) {
                String direction = directionsTokenizer.nextToken().trim();
                if (direction.equalsIgnoreCase("DOWN")) {
                    dir = SortOrder.ORDER_DESCENDING;
                } else {
                    dir = SortOrder.ORDER_ASCENDING;
                }
            }
            query.addSortOrder(sf, dir);
        }

        return list.subList(initialSize, list.size());
    }

    /**
     * Returns substring of given string without the leading digits (used in 'paths')
     */
    protected static String removeDigits(String complete) {
        int end = complete.length() - 1;
        while (Character.isDigit(complete.charAt(end))) {
            --end;
        }
        return complete.substring(0, end + 1);
    }

    /**
     * Adds path of steps to an existing query. The query may contain steps already. Per step also
     * the 'search direction' may be specified.
     * @return The new steps.
     */
    public static List addPath(Query query, String path, String searchDirs) {
        if (path == null || path.equals("")) {
            return query.getSteps().subList(0, 0);
        }
        if (searchDirs == null) {
            searchDirs = "";
        }

        List list = query.getSteps();
        int initialSize = list.size();

        StringTokenizer pathTokenizer = new StringTokenizer(path, ",");
        StringTokenizer searchDirsTokenizer = new StringTokenizer(searchDirs, ",");

        Cloud cloud = query.getCloud();

        if (query.getSteps().size() == 0) { // if no steps yet, first step must be added with addStep
            String completeFirstToken = pathTokenizer.nextToken().trim();
            String firstToken = removeDigits(completeFirstToken);
            //if (cloud.hasRole(firstToken)) {
            // you cannot start with a role.., should we throw exception?
            // naa, the following code will throw exception that node type does not exist.
            //}
            Step step = query.addStep(cloud.getNodeManager(firstToken));
            if (!firstToken.equals(completeFirstToken)) {
                query.setAlias(step, completeFirstToken);
            }
        }

        String searchDir = null; // outside the loop, so defaulting to previous searchDir
        while (pathTokenizer.hasMoreTokens()) {
            String completeToken = pathTokenizer.nextToken().trim();
            String token = removeDigits(completeToken);

            if (searchDirsTokenizer.hasMoreTokens()) {
                searchDir = searchDirsTokenizer.nextToken();
            }

            if (cloud.hasRole(token)) {
                if (!pathTokenizer.hasMoreTokens()) {
                    throw new BridgeException("Path cannot end with a role (" + path + "/" + searchDirs + ")");
                }
                String nodeManagerAlias = pathTokenizer.nextToken().trim();
                String nodeManagerName = removeDigits(nodeManagerAlias);
                NodeManager nodeManager = cloud.getNodeManager(nodeManagerName);
                RelationStep relationStep = query.addRelationStep(nodeManager, token, searchDir);

                /// make it possible to postfix with numbers manually
                if (!cloud.hasRole(completeToken)) {
                    query.setAlias(relationStep, completeToken);
                }
                if (!nodeManagerName.equals(nodeManagerAlias)) {
                    Step next = relationStep.getNext();
                    query.setAlias(next, nodeManagerAlias);
                }
            } else {
                NodeManager nodeManager = cloud.getNodeManager(token);
                RelationStep step = query.addRelationStep(nodeManager, null /* role */
                , searchDir);
                if (!completeToken.equals(nodeManager.getName())) {
                    Step next = step.getNext();
                    query.setAlias(next, completeToken);
                }
            }
        }
        if (searchDirsTokenizer.hasMoreTokens()) {
            throw new BridgeException("Too many search directions (" + path + "/" + searchDirs + ")");
        }
        return list.subList(initialSize, list.size());
    }

    /**
     * Adds a number of fields. Fields is represented as a comma separated string.
     * @return The new stepfields
     */

    public static List addFields(Query query, String fields) {
        List result = new ArrayList();
        if (fields == null)
            return result;
        List list = StringSplitter.split(fields);
        Iterator i = list.iterator();
        while (i.hasNext()) {
            String fieldName = (String)i.next();
            result.add(query.addField(fieldName));
        }
        return result;

    }

    /**
     * Add startNodes as a String to the (first step) of the given query.
     *
     * @return the new constraint, or null if the startNodes list was empty.
     */
    public static Constraint addStartNodes(Query query, String startNodes) {
        if (startNodes == null)
            return null;

        SortedSet startNodeSet = new TreeSet();

        Iterator nodes = StringSplitter.split(startNodes).iterator();
        while (nodes.hasNext()) {
            String node = (String)nodes.next();
            Integer nodeNumber;
            try {
                nodeNumber = new Integer(node);
            } catch (NumberFormatException nfe) {
                nodeNumber = new Integer(query.getCloud().getNode(node).getNumber());
            }
            startNodeSet.add(nodeNumber);
        }

        if (startNodeSet.size() > 0) {
            Step firstStep = (Step)query.getSteps().get(0);
            StepField firstStepField = query.createStepField(firstStep, "number");

            Constraint newConstraint = query.createConstraint(firstStepField, startNodeSet);
            addConstraint(query, newConstraint);
            return newConstraint;
        } else {
            return null;
        }

    }

    /**
     * Takes the query, and does a count with the same constraints.
     *
     */
    public static int count(Query query) {
        Cloud cloud = query.getCloud();
        Query count = query.aggregatingClone();
        Step step = (Step) (count.getSteps().get(0));
        count.addAggregatedField(step, cloud.getNodeManager(step.getTableName()).getField("number"), AggregatedField.AGGREGATION_TYPE_COUNT);
        Node result = (Node)cloud.getList(count).get(0);
        return result.getIntValue("number");
    }

    /**
     * Searches a list of Steps for a step with a certain name. (alias or tableName)
     * @return The Step if found, otherwise null
     * @throws ClassCastException if list does not contain only Steps
     */
    public static Step searchStep(List steps, String stepAlias) {
        if (log.isDebugEnabled()) {
            log.info("Searching '" + stepAlias + "' in " + steps);
        }
        Iterator i = steps.iterator();
        while (i.hasNext()) {
            Step step = (Step)i.next();
            if (stepAlias.equals(step.getAlias())) {
                return step;
            } else if (stepAlias.equals(step.getTableName())) {
                return step;
            }
        }
        return null;
    }

}
