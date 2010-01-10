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
import org.mmbase.module.core.ClusterBuilder;
import org.mmbase.module.core.MMBase;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.BasicSortOrder;
import org.mmbase.storage.search.implementation.BasicStep;
import org.mmbase.storage.search.implementation.BasicStepField;
import org.mmbase.storage.search.legacy.ConstraintParser;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * This class contains various utility methods for manipulating and creating query objects.
 * Most essential methods are available on the Query object itself, but too specific or legacy-ish
 * methods are put here.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see  org.mmbase.bridge.Query
 * @since MMBase-1.7
 */
abstract public class Queries {

    public static final int OPERATOR_BETWEEN = -1; // not a FieldCompareConstraint (numeric)
    public static final int OPERATOR_IN = 10000; // not a FieldCompareConstraint (non numeric)
    public static final int OPERATOR_NULL = 10001; // FieldIsNullConstraint

    private static final Logger log = Logging.getLoggerInstance(Queries.class);

    /**
     * Translates a string to a search direction constant. If the string is <code>null</code> then
     * 'BOTH' is returned.
     * @param search string representation of the searchdir constant
     * @return Searchdir constant (@link RelationStep)
     * @see ClusterBuilder#getSearchDir The same function, only with another return value if String is <code>null</code>
     */
    public static int getRelationStepDirection(String search) {
        if (search == null) {
            return RelationStep.DIRECTIONS_BOTH;
        }
        search = search.toUpperCase();
        if ("DESTINATION".equals(search)) {
            return RelationStep.DIRECTIONS_DESTINATION;
        } else if ("SOURCE".equals(search)) {
            return RelationStep.DIRECTIONS_SOURCE;
        } else if ("BOTH".equals(search)) {
            return RelationStep.DIRECTIONS_BOTH;
        } else if ("ALL".equals(search)) {
            return RelationStep.DIRECTIONS_ALL;
        } else if ("EITHER".equals(search)) {
            return RelationStep.DIRECTIONS_EITHER;
        } else {
            throw new BridgeException("'" + search + "' cannot be converted to a relation-step direction constant");
        }
    }

    /**
     * Creates a Query object using arguments for {@link Cloud#getList(String, String, String, String, String, String, String, boolean)}
     * (this function is of course implemented using this utility). This is useful to convert (legacy) code which uses
     * getList, but you want to use new Query features without rewriting the complete thing.
     *
     * It can also be simply handy to specify things as Strings.
     *
     * @param cloud
     * @param startNodes
     * @param nodePath
     * @param fields
     * @param constraints
     * @param orderby
     * @param directions
     * @param searchDir
     * @param distinct
     * @return New query object
     * @todo Should this method be part of Cloud itself?
     */
    public static Query createQuery(Cloud cloud, String startNodes, String nodePath, String fields, String constraints, String orderby, String directions, String searchDir, boolean distinct) {

        // the bridge test case say that you may also specifiy empty string (why?)
        if ("".equals(startNodes) || "-1".equals(startNodes)) {
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
        if ("".equals(orderby)) {
            orderby = null;
        }

        if ("".equals(directions)) {
            directions = null;
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
            constraints = ConstraintParser.convertClauseToDBS(constraints);
            if (! ConstraintParser.validConstraints(constraints)) {
                throw new BridgeException("invalid constraints:" + constraints);
            }
            if (! constraints.substring(0, 5).equalsIgnoreCase("WHERE")) {
                /// WHERE is used in org.mmbase.util.QueryConvertor
                constraints = "WHERE " + constraints;
            }
        }


        // create query object
        //TODO: remove this code... classes under org.mmbase.bridge.util must not use the core

        // getMultilevelSearchQuery must perhaps move to a utility container org.mmbase.storage.search.Queries or so.

        ClusterBuilder clusterBuilder = MMBase.getMMBase().getClusterBuilder();
        int search = -1;
        if (searchDir != null) {
            search = ClusterBuilder.getSearchDir(searchDir);
        }

        List snodes   = StringSplitter.split(startNodes);
        List tables   = StringSplitter.split(nodePath);
        List f        = StringSplitter.split(fields);
        List orderVec = StringSplitter.split(orderby);
        List d        = StringSplitter.split(directions);
        try {
            // pitty that we can't use cloud.createQuery for this.
            // but all essential methods are on ClusterBuilder
            // XXX need casting here, something's wrong!!!
            Query query = new BasicQuery(cloud, clusterBuilder.getMultiLevelSearchQuery(snodes, f, distinct ? "YES" : "NO", tables, constraints, orderVec, d, search));
            return query;
        } catch (IllegalArgumentException iae) {
            throw new BridgeException(iae.getMessage() + ". (arguments: startNodes='" + startNodes + "', path='" + nodePath + "', fields='" + fields + "', constraints='" + constraints + "' orderby='" + orderby + "', directions='" + directions + "', searchdir='" + searchDir + "')" , iae);
        }
    }


    /**
     * Adds a 'legacy' constraint to the query, i.e. constraint(s) represented
     * by a string. Alreading existing constraints remain ('AND' is used).
     *
     * @param query query to add constraint to
     * @param constraints string representation of constraints
     * @return The new constraint, or null if nothing changed added.
     */
    public static Constraint addConstraints(Query query, String constraints) {
        if (constraints == null || constraints.equals("")) {
            return null;
        }

        // (Try to) parse constraints string to Constraint object.
        Constraint newConstraint = new ConstraintParser(query).toConstraint(constraints);
        addConstraint(query, newConstraint);
        return newConstraint;
    }

    /**
     * Adds a Constraint to the already present constraint (with AND).
     * @param query query to add the constraint to
     * @param newConstraint constraint to add
     * @return The new constraint.
     */
    public static Constraint addConstraint(Query query, Constraint newConstraint) {
        if (newConstraint == null) {
            return null;
        }

        Constraint constraint = query.getConstraint();

        if (constraint != null) {
            log.debug("compositing constraint");
            Constraint compConstraint = query.createConstraint(constraint, CompositeConstraint.LOGICAL_AND, newConstraint);
            query.setConstraint(compConstraint);
        } else {
            query.setConstraint(newConstraint);
        }
        return newConstraint;
    }

    /**
     * Creates a operator constant for use by createConstraint
     * @param s String representation of operator
     * @return FieldCompareConstraint operator constant
     * @see #createConstraint(Query, String, int, Object)
     * @see #createConstraint(Query, String, int, Object, Object, boolean)
     */
    public static int getOperator(String s) {
        String op = s.toUpperCase();
        // first: determine operator:
        if (op.equals("<") || op.equals("LESS") || op.equals("LT")) {
            return FieldCompareConstraint.LESS;
        } else if (op.equals("<=") || op.equals("LESS_EQUAL") || op.equals("LE")) {
            return FieldCompareConstraint.LESS_EQUAL;
        } else if (op.equals("=") || op.equals("EQUAL") || op.equals("") || op.equals("EQ")) {
            return FieldCompareConstraint.EQUAL;
        } else if (op.equals("!=") || op.equals("NOT_EQUAL") || op.equals("NE")) {
            return FieldCompareConstraint.NOT_EQUAL;
        } else if (op.equals(">") || op.equals("GREATER") || op.equals("GT")) {
            return FieldCompareConstraint.GREATER;
        } else if (op.equals(">=") || op.equals("GREATER_EQUAL") || op.equals("GE")) {
            return FieldCompareConstraint.GREATER_EQUAL;
        } else if (op.equals("LIKE")) {
            return FieldCompareConstraint.LIKE;
        } else if (op.equals("BETWEEN")) {
            return OPERATOR_BETWEEN;
        } else if (op.equals("IN")) {
            return OPERATOR_IN;
        } else if (op.equals("NULL")) {
            return OPERATOR_NULL;
            //} else if (op.equals("~") || op.equals("REGEXP")) {
            //  return FieldCompareConstraint.REGEXP;
        } else {
            throw new BridgeException("Unknown Field Compare Operator '" + op + "'");
        }
    }

    /**
     * Creates a part constant for use by createConstraint
     * @param s String representation of a datetime part
     * @return FieldValueDateConstraint part constant
     * @see #createConstraint(Query, String, int, Object, Object, boolean, int)
     */
    public static int getDateTimePart(String s) {
        String sPart = s.toUpperCase();
        if (sPart.equals("")) {
            return -1;
        } else if (sPart.equals("CENTURY")) {
            return FieldValueDateConstraint.CENTURY;
        } else if (sPart.equals("YEAR")) {
            return FieldValueDateConstraint.YEAR;
        } else if (sPart.equals("QUARTER")) {
            return FieldValueDateConstraint.QUARTER;
        } else if (sPart.equals("MONTH")) {
            return FieldValueDateConstraint.MONTH;
        } else if (sPart.equals("WEEK")) {
            return FieldValueDateConstraint.WEEK;
        } else if (sPart.equals("DAYOFYEAR")) {
            return FieldValueDateConstraint.DAY_OF_YEAR;
        } else if (sPart.equals("DAY") || sPart.equals("DAYOFMONTH")) {
            return FieldValueDateConstraint.DAY_OF_MONTH;
        } else if (sPart.equals("DAYOFWEEK")) {
            return FieldValueDateConstraint.DAY_OF_WEEK;
        } else if (sPart.equals("HOUR")) {
            return FieldValueDateConstraint.HOUR;
        } else if (sPart.equals("MINUTE")) {
            return FieldValueDateConstraint.MINUTE;
        } else if (sPart.equals("SECOND")) {
            return FieldValueDateConstraint.SECOND;
        } else if (sPart.equals("MILLISECOND")) {
            return FieldValueDateConstraint.MILLISECOND;
        } else {
            throw new BridgeException("Unknown datetime part '" + sPart + "'");
        }
    }

    /**
     * Used in implementation of createConstraint
     * @param stringValue string representation of a number
     * @return Number object
     * @throws BridgeException when failed to convert the string
     */
    protected static Number getNumberValue(String stringValue) throws BridgeException {
        if (stringValue == null) return null;
        try {
            return new Integer(stringValue);
        } catch (NumberFormatException e) {
            try {
                return new Double(stringValue);
            } catch (NumberFormatException e2) {
                if(stringValue.equalsIgnoreCase("true")) {
                    return new Integer(1);
                } else if(stringValue.equalsIgnoreCase("false")) {
                    return new Integer(0);
                }
                throw new BridgeException("Operator requires number value ('" + stringValue + "' is not)");
            }
        }
    }

    /**
     * Used in implementation of createConstraint
     * @param fieldType Field Type constant (@link Field)
     * @param operator Compare operator
     * @param value value to convert
     * @return new Compare value
     */
    protected static Object getCompareValue(int fieldType, int operator, Object value) {
        return getCompareValue(fieldType, operator, value, -1, null);
    }

    /**
     * Used in implementation of createConstraint
     * @param fieldType Field Type constant (@link Field)
     * @param operator Compare operator
     * @param value value to convert
     * @param cloud The cloud may be used to pass locale sensitive properties which may be needed for comparisions (locales, timezones)
     * @return new Compare value
     * @since MMBase-1.8.2
     */
    protected static Object getCompareValue(int fieldType, int operator, Object value, int datePart, Cloud cloud) {
        if (operator == OPERATOR_IN) {
            SortedSet set;
            if (value instanceof SortedSet) {
                set = (SortedSet)value;
            } else if (value instanceof NodeList) {
                set = new TreeSet();
                NodeIterator i = ((NodeList)value).nodeIterator();
                while (i.hasNext()) {
                    Node node = i.nextNode();
                    set.add(getCompareValue(fieldType, FieldCompareConstraint.EQUAL, new Integer(node.getNumber())));
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
            return set;
        }
        switch(fieldType) {
        case Field.TYPE_STRING:
            return value == null ? null : Casting.toString(value);
        case Field.TYPE_INTEGER:
        case Field.TYPE_FLOAT:
        case Field.TYPE_LONG:
        case Field.TYPE_DOUBLE:
        case Field.TYPE_NODE:
            if (value  instanceof Number) {
                return value;
            } else {
                return getNumberValue(value == null ? null : Casting.toString(value));
            }
        case Field.TYPE_DATETIME:
            //TimeZone     tz = cloud == null ? null : (TimeZone) cloud.getProperty("org.mmbase.timezone");
            if (datePart > -1) {
                return Casting.toInteger(value);
            } else {
                return Casting.toDate(value);
            }
        case Field.TYPE_BOOLEAN:
            return Casting.toBoolean(value) ? Boolean.TRUE : Boolean.FALSE;
        default:
            return value;
        }
    }

    /**
     * Defaulting version of {@link #createConstraint(Query, String, int, Object, Object, boolean, int)}.
     * Casesensitivity defaults to false, value2 to null (so 'BETWEEN' cannot be used), datePart set to -1 (so no date part comparison)
     * @param query      The query to create the constraint for
     * @param fieldName  The field to create the constraint on (as a string, so it can include the step), e.g. 'news.number'
     * @param operator   The operator to use. This constant can be produces from a string using {@link #getOperator(String)}.
     * @param value      The value to compare with, which must be of the right type. If field is number it might also be an alias.
     * @return The new constraint, or <code>null</code> it by chance the specified arguments did not lead to a new actual constraint (e.g. if value is an empty set)
     */
    public static Constraint createConstraint(Query query, String fieldName, int operator, Object value) {
        return createConstraint(query, fieldName, operator, value, null, false, -1);
    }

    /**
     * Defaulting version of {@link #createConstraint(Query, String, int, Object, Object, boolean, int)}.
     * DatePart set to -1 (so no date part comparison)
     * @param query      The query to create the constraint for
     * @param fieldName  The field to create the constraint on (as a string, so it can include the step), e.g. 'news.number'
     * @param operator   The operator to use. This constant can be produces from a string using {@link #getOperator(String)}.
     * @param value      The value to compare with, which must be of the right type. If field is number it might also be an alias.
     * @param value2     The other value (only relevant if operator is BETWEEN, the only terniary operator)
     * @param caseSensitive  Whether it should happen case sensitively (not relevant for number fields)
     * @return The new constraint, or <code>null</code> it by chance the specified arguments did not lead to a new actual constraint (e.g. if value is an empty set)
     */
    public static Constraint createConstraint(Query query, String fieldName, int operator, Object value,  Object value2, boolean caseSensitive) {
        return createConstraint(query, fieldName, operator, value, value2, caseSensitive, -1);
    }

    /**
     * Creates a constraint smartly, depending on the type of the field, the value is cast to the
     * right type, and the right type of constraint is created.
     * This is used in taglib implementation, but could be useful more generally.
     *
     * @param query      The query to create the constraint for
     * @param fieldName  The field to create the constraint on (as a string, so it can include the step), e.g. 'news.number'
     * @param operator   The operator to use. This constant can be produces from a string using {@link #getOperator(String)}.
     * @param value      The value to compare with, which must be of the right type. If field is number it might also be an alias.
     * @param value2     The other value (only relevant if operator is BETWEEN, the only terniary operator)
     * @param caseSensitive  Whether it should happen case sensitively (not relevant for number fields)
     * @param datePart       The part of a DATETIME value that is to be checked
     * @return The new constraint, or <code>null</code> it by chance the specified arguments did not lead to a new actual constraint (e.g. if value is an empty set)
     */
    public static Constraint createConstraint(Query query, String fieldName, int operator, Object value, Object value2, boolean caseSensitive, int datePart) {

        StepField stepField = query.createStepField(fieldName);
        if (stepField == null) {
            throw new BridgeException("Could not create stepfield with '" + fieldName + "'");
        }

        Cloud cloud = query.getCloud();
        FieldConstraint newConstraint;

        if (value instanceof StepField) {
            newConstraint = query.createConstraint(stepField, operator, (StepField)value);
        } else if (operator == OPERATOR_NULL || value == null) {
            newConstraint = query.createConstraint(stepField);
        } else {
            Field field = cloud.getNodeManager(stepField.getStep().getTableName()).getField(stepField.getFieldName());
            int fieldType = field.getType();

            if (fieldName.equals("number") || fieldType == Field.TYPE_NODE) {
                if (value instanceof String) { // it might be an alias!
                    if (cloud.hasNode((String) value)) {
                        Node node = cloud.getNode((String)value);
                        value = new Integer(node.getNumber());
                    } else {
                        value = new Integer(-1);
                    }
                } else if (value instanceof Collection) {  // or even more aliases!
                    Iterator i = ((Collection) value).iterator();
                    value = new ArrayList();
                    List list = (List) value;
                    while (i.hasNext()) {
                        Object v = i.next();
                        if (v instanceof Number) {
                            list.add(v);
                        } else {
                            String s = Casting.toString(v);
                            if (cloud.hasNode(s)) {
                                Node node = cloud.getNode(s);
                                list.add(new Integer(node.getNumber()));
                            } else {
                                list.add(new Integer(-1));
                            }

                        }
                    }

                }
            }
            if (operator != OPERATOR_IN) { // should the elements of the collection then not be cast?

                if (fieldType == Field.TYPE_XML) {
                    // XML's are treated as String in the query-handler so, let's anticipate that here...
                    // a bit of a hack, perhaps we need something like a 'searchCast' or so.
                    value = Casting.toString(value);
                } else if (datePart == -1) {
                    value = field.getDataType().cast(value, null, field);
                }
            }
            Object compareValue = getCompareValue(fieldType, operator, value, datePart, cloud);

            if (operator > 0 && operator < OPERATOR_IN) {
                if (fieldType == Field.TYPE_DATETIME && datePart> -1) {
                    newConstraint = query.createConstraint(stepField, operator, compareValue, datePart);
                } else {
                    if (operator == FieldCompareConstraint.EQUAL  && compareValue == null) {
                        newConstraint = query.createConstraint(stepField);
                    } else {
                        newConstraint = query.createConstraint(stepField, operator, compareValue);
                    }
                }
            } else {
                if (fieldType == Field.TYPE_DATETIME && datePart> -1) {
                    throw new RuntimeException("Cannot apply IN or BETWEEN to a partial date field");
                }
                switch (operator) {
                case OPERATOR_BETWEEN :
                    value2 = field.getDataType().cast(value2, null, field);
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
     * Takes a Constraint of a query, and takes al constraints on 'sourceStep' of it, and copies
     * those Constraints to the given step of the receiving query.
     *
     * Constraints on different steps then the given 'sourceStep' are ignored. CompositeConstraints
     * cause recursion and would work too (but same limitation are valid for the childs).
     *
     * @param c          The constrain to be copied (for example the result of sourceQuery.getConstraint()).
     * @param sourceStep The step in the 'source' query.
     * @param query      The receiving query
     * @param step       The step of the receiving query which must 'receive' the sort orders.
     * @since MMBase-1.7.1
     * @see   org.mmbase.storage.search.implementation.BasicSearchQuery#copyConstraint Functions are similar
     * @throws IllegalArgumentException If the given constraint is not compatible with the given step.
     * @throws UnsupportedOperationException If CompareFieldsConstraints or LegacyConstraints are encountered.
     * @return The new constraint or null
     */
    public static Constraint copyConstraint(Constraint c, Step sourceStep, Query query, Step step) {
        if (c == null) return null;

        if (c instanceof CompositeConstraint) {
            CompositeConstraint constraint = (CompositeConstraint) c;
            List constraints = new ArrayList();
            Iterator i = constraint.getChilds().iterator();
            while (i.hasNext()) {
                Constraint cons = copyConstraint((Constraint) i.next(), sourceStep, query, step);
                if (cons != null) constraints.add(cons);
            }
            int size = constraints.size();
            if (size == 0) return null;
            if (size == 1) return (Constraint) constraints.get(0);
            i = constraints.iterator();
            int op = constraint.getLogicalOperator();
            Constraint newConstraint    = query.createConstraint((Constraint) i.next(), op, (Constraint) i.next());
            while (i.hasNext()) {
                newConstraint = query.createConstraint(newConstraint, op, (Constraint) i.next());
            }
            query.setInverse(newConstraint, constraint.isInverse());
            return newConstraint;
        } else if (c instanceof CompareFieldsConstraint) {
            throw new UnsupportedOperationException("Cannot copy comparison between fields"); // at least not from different steps
        }


        FieldConstraint fieldConstraint = (FieldConstraint) c;
        if (! fieldConstraint.getField().getStep().equals(sourceStep)) return null; // constraint is not for the request step, so don't copy.

        StepField field = query.createStepField(step, fieldConstraint.getField().getFieldName());

        FieldConstraint newConstraint;
        if (c instanceof FieldValueConstraint) {
            newConstraint = query.createConstraint(field, ((FieldValueConstraint) c).getOperator(), ((FieldValueConstraint) c).getValue());
        } else if (c instanceof FieldNullConstraint) {
            newConstraint = query.createConstraint(field);
        } else if (c instanceof FieldValueBetweenConstraint) {
            FieldValueBetweenConstraint constraint = (FieldValueBetweenConstraint) c;
            try {
                newConstraint = query.createConstraint(field, constraint.getLowerLimit(), constraint.getUpperLimit());
            } catch (NumberFormatException e) {
                newConstraint = query.createConstraint(field, constraint.getLowerLimit(), constraint.getUpperLimit());
            }
        } else if (c instanceof FieldValueInConstraint) {
            FieldValueInConstraint constraint = (FieldValueInConstraint) c;

            // sigh
            SortedSet set = new TreeSet();
            int type   =  field.getType();
            Iterator k = constraint.getValues().iterator();
            while (k.hasNext()) {
                Object value = k.next();
                switch(type) {
                case Field.TYPE_INTEGER:
                case Field.TYPE_LONG:
                case Field.TYPE_NODE:
                    value = new Long(Casting.toLong(value));
                    break;
                case Field.TYPE_FLOAT:
                case Field.TYPE_DOUBLE:
                    value = new Double(Casting.toDouble(value));
                    break;
                case Field.TYPE_DATETIME:
                    value = new Date((long) 1000 * Integer.parseInt("" + value));
                    break;
                default:
                    log.debug("Unknown type " + type);
                    break;
                }
                set.add(value);
            }
            newConstraint = query.createConstraint(field, set);
        } else if (c instanceof LegacyConstraint) {
            throw new UnsupportedOperationException("Cannot copy legacy constraint to other step");
        } else {
            throw new RuntimeException("Could not copy constraint " + c);
        }
        query.setInverse(newConstraint, fieldConstraint.isInverse());
        query.setCaseSensitive(newConstraint, fieldConstraint.isCaseSensitive());
        return newConstraint;

    }
    /**
     * Copies SortOrders to a given step of another query. SortOrders which do not sort the given
     * 'sourceStep' are ignored.
     * @param sortOrders A list of SortOrders (for example the result of sourceQuery.getSortOrders()).
     * @param sourceStep The step in the 'source' query.
     * @param query      The receiving query
     * @param step       The step of the receiving query which must 'receive' the sort orders.
     * @since MMBase-1.7.1

     */
    public static void copySortOrders(List sortOrders, Step sourceStep,  Query query, Step step) {
        Iterator i = sortOrders.iterator();
        while (i.hasNext()) {
            SortOrder sortOrder = (SortOrder) i.next();
            StepField sourceField = sortOrder.getField();
            if (! sourceField.getStep().equals(sourceStep)) continue; // for another step
            if (sortOrder instanceof DateSortOrder) {
                query.addSortOrder(query.createStepField(step, sourceField.getFieldName()), sortOrder.getDirection(),
                                   sortOrder.isCaseSensitive(),
                                   ((DateSortOrder)sortOrder).getPart());
            } else {
                query.addSortOrder(query.createStepField(step, sourceField.getFieldName()), sortOrder.getDirection(),
                                   sortOrder.isCaseSensitive());
            }
        }
    }

    /**
     * Converts a String to a SortOrder constant
     * @param dir string representation of direction of sortorder
     * @return SortOrder constant
     * @since MMBase-1.7.1
     */
    public static int getSortOrder(String dir) {
        dir = dir.toUpperCase();
        if (dir.equals("")) {
            return  SortOrder.ORDER_ASCENDING;
        } else if (dir.equals("DOWN")) {
            return SortOrder.ORDER_DESCENDING;
        } else if (dir.equals("UP")) {
            return SortOrder.ORDER_ASCENDING;
        } else if (dir.equals("ASCENDING")) {
            return SortOrder.ORDER_ASCENDING;
        } else if (dir.equals("DESCENDING")) {
            return SortOrder.ORDER_DESCENDING;
        } else {
            throw new BridgeException("Unknown sort-order '" + dir + "'");
        }
    }

    /**
     * Adds sort orders to the query, using two strings. Like in 'getList' of Cloud. Several tag-attributes need this.
     * @param query query to add the sortorders to
     * @param sorted string with comma-separated fields
     * @param directions string with comma-separated directions
     *
     * @todo implement for normal query.
     * @return The new sort orders
     */
    public static List addSortOrders(Query query, String sorted, String directions) {
        // following code was copied from MMObjectBuilder.setSearchQuery (bit ugly)
        if (sorted == null) {
            return query.getSortOrders().subList(0, 0);
        }
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
                dir = getSortOrder(direction);
            }
            query.addSortOrder(sf, dir);
        }

        return list.subList(initialSize, list.size());
    }

    /**
     * Returns substring of given string without the leading digits (used in 'paths')
     * @param complete string with leading digits
     * @return string with digits removed
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
     * @param query extend this query
     * @param path create steps from this path
     * @param searchDirs add steps with these relation directions
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

        StringTokenizer pathTokenizer       = new StringTokenizer(path, ",");
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
                    throw new BridgeException("Path cannot end with a role (path: " + path + " searchdirs:" + searchDirs + ")");
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
                RelationStep step = query.addRelationStep(nodeManager, null /* role */ , searchDir);
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
     * @param query The query where the fields should be added to
     * @param fields a comma separated string of fields
     * @return The new stepfields
     */
    public static List addFields(Query query, String fields) {
        List result = new ArrayList();
        if (fields == null || fields.equals("")) {
            return result;
        }
        List list = StringSplitter.split(fields);
        Iterator i = list.iterator();
        while (i.hasNext()) {
            String fieldName = (String)i.next();
            result.add(query.addField(fieldName));
        }
        return result;

    }



    /**
     * Add startNodes to the first step with the correct type to the given query. The nodes are identified
     * by a String, which could be prefixed with a step-alias, if you want to add the nodes to
     * another then this found step.
     *
     * Furthermore may the nodes by identified by their alias, if they have one.
     * @param query query to add the startnodes
     * @param startNodes start nodes
     *
     * @see org.mmbase.module.core.ClusterBuilder#getMultiLevelSearchQuery(List, List, String, List, String, List, List, int)
     * (this is essentially a 'bridge' version of the startnodes part)
     */
    public static void addStartNodes(Query query, String startNodes) {
        if (startNodes == null || "".equals(startNodes) || "-1".equals(startNodes)) {
            return;
        }

        Step firstStep = null; // the 'default' step to which nodes are added. It is the first step which corresponds with the type of the first node.

        Iterator nodes = StringSplitter.split(startNodes).iterator();
        while (nodes.hasNext()) {
            String nodeAlias = (String) nodes.next(); // can be a string, prefixed with the step alias.
            Step step;                                // the step to which the node must be added (defaults to 'firstStep').
            String nodeNumber;                        // a node number or perhaps also a node alias.
            {
                int dot = nodeAlias.indexOf('.'); // this feature is not in core. It should be considered experimental
                if (dot == -1) {
                    step = firstStep;
                    nodeNumber = nodeAlias;
                } else {
                    step = query.getStep(nodeAlias.substring(0, dot));
                    nodeNumber = nodeAlias.substring(dot + 1);
                }
            }

            if (firstStep == null) { // firstStep not yet determined, do that now.
                Node node;
                try {
                    node = query.getCloud().getNode(nodeNumber);
                } catch (NotFoundException nfe) { // alias with dot?
                    node = query.getCloud().getNode(nodeAlias);
                }
                NodeManager nodeManager = node.getNodeManager();
                Iterator i = query.getSteps().iterator();
                while (i.hasNext()) {
                    Step queryStep = (Step) i.next();
                    NodeManager queryNodeManager = query.getCloud().getNodeManager(queryStep.getTableName());
                    if (queryNodeManager.equals(nodeManager) || queryNodeManager.getDescendants().contains(nodeManager)) {
                        // considering inheritance. ClusterBuilder is not doing that, but I think it is a bug.
                        firstStep = queryStep;
                        break;
                    }
                }
                if (firstStep == null) {
                    // odd..
                    // See also org.mmbase.module.core.ClusterBuilder#getMultiLevelSearchQuery
                    // specified a node which is not of the type of one of the steps.
                    // take as default the 'first' step (which will make the result empty, compatible with 1.6, bug #6440).
                    firstStep = (Step) query.getSteps().get(0);
                }
            }
            if (step == null) {
                step = firstStep;
            }

            try {
                try {
                    query.addNode(step, Integer.parseInt(nodeNumber));
                } catch (NumberFormatException nfe) {
                    query.addNode(step, query.getCloud().getNode(nodeNumber));          // node was specified by alias.
                }
            } catch (NotFoundException nnfe) {
                query.addNode(step, query.getCloud().getNode(nodeAlias)); // perhas an alias containing a dot?
            }
        }
    }

    /**
     * Takes the query, and does a count with the same constraints (so ignoring 'offset' and 'max')
     * @param query query as base for the count
     * @return number of results
     */
    public static int count(Query query) {
        Cloud cloud = query.getCloud();
        Query count = query.aggregatingClone();
        int type = query.isDistinct() ? AggregatedField.AGGREGATION_TYPE_COUNT_DISTINCT : AggregatedField.AGGREGATION_TYPE_COUNT;

        String resultName;
        if (query instanceof NodeQuery) {
            // all fields are present of the node-step, so, we could use the number field simply.
            resultName = "number";
            NodeQuery nq = (NodeQuery) query;
            count.addAggregatedField(nq.getNodeStep(), nq.getNodeManager().getField(resultName), type);
        } else {
            List fields = query.getFields();
            if (fields.size() == 0) { // for non-distinct queries always the number fields would be available
                throw new IllegalArgumentException("Cannot count queries with less than one field: " + query);
            }

            if (query.isDistinct() && fields.size() > 1) {
                // aha hmm. Well, we also find it ok if all fields are of one step, and 'number' is present
                resultName = null;
                Step step = null;
                Iterator i = fields.iterator();
                while (i.hasNext()) {
                    StepField sf = (StepField) i.next();
                    if (step == null) {
                        step = sf.getStep();
                    } else {
                        if (! step.equals(sf.getStep())) {
                            throw new UnsupportedOperationException("Cannot count distinct queries with fields of more than one step. Current fields: " + fields + " " + step + " != " + sf.getStep());
                        }
                    }
                    if (resultName == null) {
                        resultName = sf.getFieldName();
                    }
                }
                if (resultName == null) {
                    throw new UnsupportedOperationException("Cannot count distinct queries with more than one field if 'number' field is missing. Current fields: " + fields);
                }
                count.addAggregatedField(step, cloud.getNodeManager(step.getTableName()).getField(resultName), type);
            } else {
                // simply take this one field
                StepField sf = (StepField) fields.get(0);
                Step step = sf.getStep();
                resultName = sf.getFieldName();
                count.addAggregatedField(step, cloud.getNodeManager(step.getTableName()).getField(resultName), type);
            }
        }
        NodeList r = cloud.getList(count);
        if (r.size() != 1) throw new RuntimeException("Count query " + query + " did not give one result but " + r);
        Node result = r.getNode(0);
        return result.getIntValue(resultName);
    }

    /**
     * @since MMBase-1.8
     */
    protected static Object aggregate(Query query, StepField field, int type) {
        Cloud cloud = query.getCloud();
        Query aggregate = query.aggregatingClone();
        String resultName = field.getFieldName();
        Step step = field.getStep();
        aggregate.addAggregatedField(step, cloud.getNodeManager(step.getTableName()).getField(resultName), type);
        NodeList r = cloud.getList(aggregate);
        if (r.size() != 1) throw new RuntimeException("Aggregated query " + query + " did not give one result but " + r);
        Node result = r.getNode(0);
        return result.getValue(resultName);
    }

    /**
     * @since MMBase-1.8
     */
    public static Object min(Query query, StepField field) {
        return aggregate(query, field, AggregatedField.AGGREGATION_TYPE_MIN);
    }
    /**
     * @since MMBase-1.8
     */
    public static Object max(Query query, StepField field) {
        return aggregate(query, field, AggregatedField.AGGREGATION_TYPE_MAX);
    }

    /**
     * Searches a list of Steps for a step with a certain name. (alias or tableName)
     * @param steps steps to search through
     * @param stepAlias alias to search for
     * @return The Step if found, otherwise null
     * @throws ClassCastException if list does not contain only Steps
     */
    public static Step searchStep(List steps, String stepAlias) {
        if (log.isDebugEnabled()) {
            log.debug("Searching '" + stepAlias + "' in " + steps);
        }
        // first try aliases
        Iterator i = steps.iterator();
        while (i.hasNext()) {
            Step step = (Step)i.next();
            if (stepAlias.equals(step.getAlias())) {
                return step;
            }
        }
        // if no aliases found, try table names
        i = steps.iterator();
        while (i.hasNext()) {
            Step step = (Step)i.next();
            if (stepAlias.equals(step.getTableName())) {
                return step;
            }
        }
        return null;
    }

    /**
     * Returns the NodeQuery returning the given Node. This query itself is not very useful, because
     * you already have its result (the node), but it is convenient as a base query for many other
     * goals.
     *
     * If the node is uncommited, it cannot be queried, and the node query returning all nodes from
     * the currect type will be returned.
     *
     * @param node Node to create the query from
     * @return A new NodeQuery object
     */
    public static NodeQuery createNodeQuery(Node node) {
        NodeManager nm = node.getNodeManager();
        NodeQuery query = node.getCloud().createNodeQuery(); // use the version which can accept
                                                             // more steps

        Step step       = query.addStep(nm);
        query.setNodeStep(step);
        if (! node.isNew()) {
            query.addNode(step, node);
        }
        return query;
    }

    /**
     * Returns a query to find the nodes related to the given node.
     * @param node start node
     * @param otherNodeManager node manager on the other side of the relation
     * @param role role of the relation
     * @param direction direction of the relation
     * @return A new NodeQuery object
     */
    public static NodeQuery createRelatedNodesQuery(Node node, NodeManager otherNodeManager, String role, String direction) {
        NodeQuery query = createNodeQuery(node);
        if (otherNodeManager == null) otherNodeManager = node.getCloud().getNodeManager("object");
        RelationStep step = query.addRelationStep(otherNodeManager, role, direction);
        query.setNodeStep(step.getNext());
        return query;
    }

    /**
     * Returns a query to find the relations nodes of the given node.
     * @param node start node
     * @param otherNodeManager node manager on the other side of the relation
     * @param role role of the relation
     * @param direction direction of the relation
     * @return A new NodeQuery object
     */
    public static NodeQuery createRelationNodesQuery(Node node, NodeManager otherNodeManager, String role, String direction) {
        NodeQuery query = createNodeQuery(node);
        if (otherNodeManager == null) otherNodeManager = node.getCloud().getNodeManager("object");
        RelationStep step = query.addRelationStep(otherNodeManager, role, direction);
        query.setNodeStep(step);
        return query;
    }

    /**
     * Returns a query to find the relations nodes between two given nodes.
     *
     * To test <em>whether</em> to nodes are related you can use e.g.:
     * <code>
     *  if (Queries.count(Queries.createRelationNodesQuery(node1, node2, "posrel", null)) > 0) {
     *    ..
     *  }
     * </code>
     * @param node start node
     * @param otherNode node on the other side of the relation
     * @param role role of the relation
     * @param direction direction of the relation
     * @return A new NodeQuery object
     * @since MMBase-1.8
     */
    public static NodeQuery createRelationNodesQuery(Node node, Node otherNode, String role, String direction) {
        NodeQuery query = createNodeQuery(node);
        NodeManager otherNodeManager = otherNode.getNodeManager();
        RelationStep step = query.addRelationStep(otherNodeManager, role, direction);
        Step nextStep = step.getNext();
        query.addNode(nextStep, otherNode.getNumber());
        query.setNodeStep(step);
        return query;
    }

    /**
     * Queries a list of cluster nodes, using a {@link org.mmbase.bridge.NodeQuery} (so al fields of
     * one step are available), plus some fields of the relation step.  The actual node can be got
     * from the node cache by doing a {@link org.mmbase.bridge.Node#getNodeValue} with the {@link
     * org.mmbase.bridge.NodeList#NODESTEP_PROPERTY} property.  The fields of the relation can be got by
     * prefixing their names by the role and a dot (as normal in multilevel results).
     * @param node start node
     * @param otherNodeManager node manager on the other side of the relation
     * @param role role of the relation
     * @param direction direction of the relation
     * @param relationFields  Comma separated string of fields which must be queried from the relation step
     * @param sortOrders      Comma separated string of fields of sortorders, or the empty string or <code>null</code>
     *                        So, this methods is targeted at the use of 'posrel' and similar fields, because sorting on other fields isn't possible right now.
     * @since MMBase-1.8
     * @todo  EXPERIMENTAL
     */
    public static NodeList getRelatedNodes(Node node, NodeManager otherNodeManager, String role, String direction, String relationFields, String sortOrders) {
        NodeQuery q = Queries.createRelatedNodesQuery(node, otherNodeManager, role, direction);
        addRelationFields(q, role, relationFields, sortOrders);
        return q.getCloud().getList(q);
    }

    /**
     * @since MMBase-1.8
     */
    public static NodeQuery addRelationFields(NodeQuery q, String role, String relationFields, String sortOrders) {
        List list = StringSplitter.split(relationFields);
        List orders = StringSplitter.split(sortOrders);
        Iterator i = list.iterator();
        Iterator j = orders.iterator();
        while (i.hasNext()) {
            String fieldName = (String)i.next();
            StepField sf = q.addField(role + "." + fieldName);
            if (j.hasNext()) {
                String so = (String) j.next();
                q.addSortOrder(sf, getSortOrder(so));
            }
        }
        return q;
    }

    /**
     * Add a sortorder (DESCENDING) on al the'number' fields of the query, on which there is not yet a
     * sortorder. This ensures that the query result is ordered uniquely.
     * @param q query to change
     * @return The changed Query
     */
    public static Query sortUniquely(final Query q) {
        List steps = null;

        // remove the ones which are already sorted
        Iterator i = q.getSortOrders().iterator();
        while (i.hasNext()) {
            SortOrder sortOrder = (SortOrder)i.next();
            if (sortOrder.getField().getFieldName().equals("number")) {
                Step step = sortOrder.getField().getStep();
                if (steps == null) {
                    // instantiate new ArrayList only if really necessary
                    steps = new ArrayList(q.getSteps());
                }
                steps.remove(step);
            }
        }
        if (steps == null) {
            steps = q.getSteps();
        }
        // add sort order on the remaining ones:
        i = steps.iterator();
        while (i.hasNext()) {
            Step step = (Step)i.next();
            assert step != null;
            StepField sf = q.createStepField(step, "number");
            if (sf == null) {
                throw new RuntimeException("Create stepfield for 'number' field returned null!");
            }
            q.addSortOrder(sf, SortOrder.ORDER_DESCENDING);
        }
        return q;
    }

    /**
     * Make sure all sorted fields are queried
     * @since MMBase-1.8
     */
    public static Query addSortedFields(Query q) {
        List fields = q.getFields();
        Iterator i = q.getSortOrders().iterator();
        while (i.hasNext()) {
            SortOrder order = (SortOrder) i.next();
            StepField field = order.getField();
            Step s = field.getStep();
            StepField sf = q.createStepField(s, q.getCloud().getNodeManager(s.getTableName()).getField(field.getFieldName()));
            if (! fields.contains(sf)) {
                q.addField(s, q.getCloud().getNodeManager(s.getTableName()).getField(field.getFieldName()));
            }
        }
        return q;
    }

    /**
     * Obtains a value for the field of a sortorder from a given node.
     * Used to set constraints based on sortorder.
     * @since MMBase-1.8
     */
    public static Object getSortOrderFieldValue(Node node, SortOrder sortOrder) {
        String fieldName = sortOrder.getField().getFieldName();
        if (node == null) throw new IllegalArgumentException("No node given");
        Object value = node.getValue(fieldName);
        if (value == null) {
            Step step = sortOrder.getField().getStep();
            String pref = step.getAlias();
            if (pref == null) {
                pref = step.getTableName();
            }
            value = node.getValue(pref+ "." + fieldName);

        }
        if (value instanceof Node) {
            value = new Integer(((Node)value).getNumber());
        }
        return value;
    }


    /**
     * Compare tho nodes, with a SortOrder. This determins where a certain node is smaller or bigger than a certain other node, with respect to some SortOrder.
     * This is used by {@link #compare(Node, Node, List)}
     *
     * If node2 is only 'longer' then node1, but otherwise equal, then it is bigger.
     *
     * @since MMBase-1.8
     */
    public static int compare(Node node1, Node node2, SortOrder sortOrder) {
        return compare(getSortOrderFieldValue(node1, sortOrder),
                       getSortOrderFieldValue(node2, sortOrder),
                       sortOrder);

    }
    /**
     * @since MMBase-1.8
     */
    public static int compare(Object value, Object value2, SortOrder sortOrder) {
        int result;
        // compare values - if they differ, detemrine whether
        // they are bigger or smaller and return the result
        // remaining fields are not of interest once a difference is found
        if (value == null) {
            if (value2 != null) {
                return 1;
            } else {
                result = 0;
            }
        } else if (value2 == null) {
            return -1;
        } else {
            // compare the results
            try {
                result = ((Comparable)value).compareTo(value2);
            } catch (ClassCastException cce) {
                // This should not occur, and indicates very odd values are being sorted on (i.e. byte arrays).
                // warn and ignore this sortorder
                log.warn("Cannot compare values " + value +" and " + value2 + " in sortorder field " +
                         sortOrder.getField().getFieldName() + " in step " + sortOrder.getField().getStep().getAlias());
                result = 0;
            }
        }
        // if the order of this field is descending,
        // then the result of the comparison is the reverse (the node is 'greater' if the value is 'less' )
        if (sortOrder.getDirection() == SortOrder.ORDER_DESCENDING) {
            result = -result;
        }
        return result;
    }

    /**
     * Does a field-by-field compare of two Node objects, on the fields used to order the nodes.
     * This is used to determine whether a node comes after or before another, in a certain query result.
     *
     * @return -1 if node1 is smaller than node 2, 0 if both nodes are equals, and +1 is node 1 is greater than node 2.
     * @since MMBase-1.8
     */
    public static int compare(Node node1, Node node2, List sortOrders) {
        if (node1 == null) return -1;
        if (node2 == null) return +1;
        int result = 0;
        Iterator i = sortOrders.iterator();
        while (result == 0 && i.hasNext()) {
            SortOrder order = (SortOrder) i.next();
            result = compare(node1, node2, order);
        }
        // if all fields match - return 0 as if equal
        return result;
    }

    /**
     * Explores a query object, and creates a certain new relation object, which would make the
     * given node appear in the query's result.
     *
     * You can read this as 'the query object is a related nodes query, and is used to contain information
     * about the relation (role, startnodes)'. This currently is also the only implemented part of
     * this method.

     * @throw UnsupportedOperationException If it cannot be determined how the node should be related.
     *
     * @since MMBase-1.8.6
     * @returns Newly created node(s)
     */
    public static NodeList addToResult(Query q, Node n) {
        List steps = q.getSteps();

        if (steps.size() < 3) throw new UnsupportedOperationException();

        NodeList result = q.getCloud().createNodeList();
        // First, try if the node can be related to a startNode.
        int start = 0;
        Step startStep = (Step) steps.get(start);
        Cloud cloud = n.getCloud();
        SortedSet startNodes = startStep.getNodes();
        NodeManager nextManager = cloud.getNodeManager(((Step) steps.get(start + 2)).getTableName());
        if (startNodes.size() > 0 && (nextManager.equals(n.getNodeManager()) || nextManager.getDescendants().contains(n.getNodeManager()))) {
            Node startNode = cloud.getNode(((Integer) startNodes.iterator().next()).intValue());
            RelationStep rel = (RelationStep) steps.get(start + 1);
            String role = cloud.getNode(rel.getRole().intValue()).getStringValue("sname");
            switch(rel.getDirectionality()) {
            case RelationStep.DIRECTIONS_SOURCE: {
                Relation newRel = cloud.getRelationManager(n.getNodeManager(), startNode.getNodeManager(), role).createRelation(startNode, n);
                newRel.commit();
                result.add(newRel);
                break;
            }
            default: {
                Relation newRel = cloud.getRelationManager(startNode.getNodeManager(), n.getNodeManager(), role).createRelation(startNode, n);
                newRel.commit();
                result.add(newRel);
            }
            }
            return result;
        } else {
            // deeper queries not yet supported
            throw new UnsupportedOperationException();
        }
    }


    /**
     * Explores a query object, and creates a certain new relation object, which would make the
     * given node appear in the query's result.
     *
     * You can read this as 'the query object is a related nodes query, and is used to contain information
     * about the relation (role, startnodes)'. This currently is also the only implemented part of
     * this method.

     * @throw UnsupportedOperationException If it cannot be determined how the node should be related.
     *
     * @since MMBase-1.8.6
     * @returns Newly created node(s)
     */
    public static NodeList removeFromResult(Query q, Node n) {
        List steps = q.getSteps();

        if (steps.size() < 3) throw new UnsupportedOperationException();

        NodeList result = q.getCloud().createNodeList();

        // First, try if the node is related to a startNode.
        int start = 0;
        Step startStep = (Step) steps.get(start);
        Cloud cloud = n.getCloud();
        for (int step = 2; step < steps.size(); step += 2) {
            Step nextStep = (Step) steps.get(step);
            NodeManager manager = cloud.getNodeManager(nextStep.getTableName());
            if (manager.equals(n.getNodeManager()) || manager.getDescendants().contains(n.getNodeManager())) {
                Query clone = q.cloneWithoutFields();
                BasicStep nextStepClone = (BasicStep) clone.getSteps().get(step);
                nextStepClone.addNode(n.getNumber());
                RelationStep relationStep = (RelationStep) clone.getSteps().get(step - 1); // (also  + 1?)
                BasicStepField relField = (BasicStepField) clone.addField(relationStep, cloud.getNodeManager(relationStep.getTableName()).getField("number"));
                String alias = relField.getAlias();
                if (alias == null) {
                    alias = relationStep.getAlias() + ".number";
                }
                NodeList list = cloud.getList(clone);
                NodeIterator ni = list.nodeIterator();
                while (ni.hasNext()) {
                    Node virtual = ni.nextNode();
                    Node r = cloud.getNode(virtual.getIntValue(alias));
                    result.add(r);
                    r.delete();

                }

            }
        }
        return result;
    }

    /**
     * Returns the string which must be used for {@link Node#getValue} in the result set of the
     * query of the given StepField.
     * @since MMBase-1.8.7
     */
    public static String getFieldAlias(StepField sf) {
        String alias = sf.getAlias();
        if (alias == null) {
            String stepAlias = sf.getStep().getAlias();
            if (stepAlias == null) {
                stepAlias = sf.getStep().getTableName();
            }
            alias = stepAlias + "." + sf.getFieldName();
        }
        return alias;
    }

    /**
     * Creates a constraint that would make the result of the query q empty.
     * Currently implemented by comparing the number field of the first step to a negative integer,
     * but this may be done otherwise.
     *
     * @since MMBase-1.8.7
     */
    public static Constraint createMakeEmptyConstraint(Query q) {
        StepField sf = q.createStepField((Step) q.getSteps().get(0), "number");
        return q.createConstraint(sf, new Integer(-1));
    }

    public static void main(String[] argv) {
        System.out.println(ConstraintParser.convertClauseToDBS("(([cpsettings.status]='[A]' OR [cpsettings.status]='I') AND [users.account] != '') and (lower([users.account]) LIKE '%t[est%' OR lower([users.email]) LIKE '%te]st%' OR lower([users.firstname]) LIKE '%t[e]st%' OR lower([users.lastname]) LIKE '%]test%')"));
    }

}
