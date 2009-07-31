/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.cache;

import java.lang.reflect.*;
import java.util.*;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.implementation.BasicQuery;
import org.mmbase.core.CoreField;
import org.mmbase.core.event.*;
import org.mmbase.datatypes.DataType;
import org.mmbase.module.core.*;
import org.mmbase.util.LinkMap;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.implementation.database.BasicSqlHandler;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * This strategy will evaluate the constraint on a a query object against a NodeEvent. It will apply the following rules:<br>
 * <b>new node/delete node</b><br>
 * <ul>
 * <li>If the step of a constraint matches the type of the event, and the values of the node don't fall within the
 * constraint: don't flush.</li>
 * <li>If the step of a constraint matches the type of the event, and the values of the node don't fall within the
 * constraint: flush.</li>
 * <li>If no constraints have a step matching the type of the event: flush.
 * </ul>
 * <b>change node</b> Like the above, but an extra check has to be made:
 * <ul>
 * <li>if the node previously fell within the constraints but now doesn't: flush</li>
 * <li>if the node previously didn't fall within the constraints but now does: flush</li>
 * </ul>
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id$
 *
 */
public class ConstraintsMatchingStrategy extends ReleaseStrategy {

    private static final Logger log = Logging.getLoggerInstance(ConstraintsMatchingStrategy.class);
    private static final BasicSqlHandler sqlHandler = new BasicSqlHandler();
    private final static String escapeChars=".\\?+*$()[]{}^|&";

    /**
    * This field contains the characters that are being escaped for the 'like' comparison of strings,
    * where, the string that should match the other is converted to a regexp
    **/
    private static final Cache<SearchQuery,AbstractConstraintMatcher> constraintWrapperCache;


    static {
        constraintWrapperCache =  new Cache<SearchQuery,AbstractConstraintMatcher>(1000) {
            @Override
            public String getName() {
                return "ConstraintMatcherCache";
            }
            @Override
            public String getDescription() {
                return "Caches query constraint wrappers used by ConstraintsMatchingStrategy";
            }
        };
        CacheManager.putCache(constraintWrapperCache);
    }

    private static final Map<String, Constructor> constraintMatcherConstructors = new HashMap<String, Constructor>();
    static {
        Class[] innerClasses = ConstraintsMatchingStrategy.class.getDeclaredClasses();
        for (Class innerClass : innerClasses) {
            if (innerClass.getName().endsWith("Matcher") && ! Modifier.isAbstract(innerClass.getModifiers())) {
                String matcherClassName = innerClass.getName();
                matcherClassName = matcherClassName.substring(matcherClassName.lastIndexOf("$") + 1);
                Constructor con = null;
                Constructor[] cons = innerClass.getConstructors();
                for (Constructor element0 : cons) {
                    Class [] params = element0.getParameterTypes();
                    if(params.length == 1 && Constraint.class.isAssignableFrom(params[0])) {
                        con = element0;
                        break;
                    }
                }
                if (con == null) {
                    log.error("Class " + innerClass + " has no appropriate constructor");
                    continue;
                }

                constraintMatcherConstructors.put(matcherClassName, con);
                log.debug("** found matcher: " + matcherClassName);
            }
        }
    }

    public ConstraintsMatchingStrategy() {
        super();
    }

    public String getName() {
        return "Constraint matching strategy";
    }

    public String getDescription() {
        return "Checks wether a changed node has a matching step within the constraints of a query, and then checks "
                + "if the node falls within the constraint. For changed nodes a check is made if the node previously "
                + "fell within the constraint and if it does so now. Queries that exclude changed nodes by their constraints "
                + "will not be flushed.";
    }

    protected final boolean doEvaluate(NodeEvent event, SearchQuery query, List<MMObjectNode> cachedResult) {
        //no constraint, we release any way
        Constraint constraint = query.getConstraint();
        if(constraint == null) return true; //should release

        //try to get a wrapper from the cache
        AbstractConstraintMatcher matcher = constraintWrapperCache.get(query);

        //if not found, try to create one
        if(matcher == null){
            try {
                matcher = findMatcherForConstraint(constraint);
                if (log.isTraceEnabled()) {
                    log.trace("created constraint matcher: " + matcher);
                }
                // Unwrapping BasicQuery's. This avoids unnecessary references (mainly to BasicCloud instances).
                if (query instanceof BasicQuery) {
                    constraintWrapperCache.put(((BasicQuery) query).getQuery(), matcher);
                } else {
                    constraintWrapperCache.put(query, matcher);
                }
                //if anything goes wrong constraintMatches is true, which means the query should be flushed
            } catch (Exception e) {
                log.error("Could not create constraint matcher for constraint: " + constraint + "main reason: " + e, e);
            }
        } else {
            if(log.isTraceEnabled()){
                log.trace("found matcher for query in cache. query: " + query);
            }
        }

        //we should have a matcher now
        if(matcher != null){
            switch(event.getType()) {
            case Event.TYPE_NEW: {
                Map<String,Object> newValues = event.getNewValues();
                // we have to compare the constraint value with the new value of the changed field to see if the new
                // node falls within the constraint. it it does: flush
                if(matcher.eventApplies(newValues, event)){
                    boolean eventMatches =  matcher.nodeMatchesConstraint(newValues, event);
                    if (log.isDebugEnabled()) {
                        logResult((eventMatches ? "" : "no ") + "flush: with matcher {" + matcher + "}:", query, event, null);
                    }
                    return eventMatches;
                } else {
                    if (log.isDebugEnabled()) {
                        logResult("flush: event does not apply to wrapper {" + matcher + "}:", query, event, null);
                    }
                    return true;
                }
            }
            case Event.TYPE_CHANGE: {
                Map<String, Object> oldValues;
                Map<String, Object> newValues;
                //because composite constraints can also cover fields that are not in the changed field list of the node
                //let's find the node and get all the values.
                MMObjectNode node = MMBase.getMMBase().getBuilder(event.getBuilderName()).getNode(event.getNodeNumber());
                if(node != null){
                    //put all the (new) values in the value maps
                    Map<String, Object> nodeValues = node.getValues();
                    oldValues = new LinkMap<String, Object>(nodeValues, event.getOldValues());
                    newValues = new LinkMap<String, Object>(nodeValues, event.getNewValues());
                } else {
                    oldValues = event.getOldValues();
                    newValues = event.getNewValues();
                }

                // we have to compare the old value and then the new value of the changed field to see if the status
                // has changed. if the node used to match the constraint but now doesn't or the reverse of this, flush.
                if(matcher.eventApplies(newValues, event)){
                    boolean eventMatches =
                        matcher.nodeMatchesConstraint(oldValues, event) || // used to match
                        matcher.nodeMatchesConstraint(newValues, event); // still matches

                    // It may be important to check whether the changed fields of the node are
                    // present in the field-list of the query. If they are not, and usedToMatch
                    // && stillMaches, then we can still return false, if at least there is no
                    // sort-order on a changed field, because even if the field itself is not in
                    // the result, and it matches the constraint before and after the event, it
                    // can still change the order of the result then.
                    // If we can garantuee that field-values alway come from the node-cache (which we cannot, at the moment),
                    // then things may become a bit different.

                    if (log.isDebugEnabled()) {
                        boolean usedToMatch = matcher.nodeMatchesConstraint(oldValues, event);
                        boolean stillMatches = matcher.nodeMatchesConstraint(newValues, event);
                        log.debug("** match with old values : " + (usedToMatch ? "match" : "no match"));
                        log.debug("** match with new values : " + (stillMatches ? "match" : "no match"));
                        log.debug("**old values: " + oldValues);
                        log.debug("**new values: " + newValues);
                        logResult((eventMatches ? "" : "no ") + "flush: with matcher {" + matcher + "}:", query, event, node);
                    }

                    return eventMatches;
                } else {
                    if (log.isDebugEnabled()) {
                        logResult("flush: event does not apply to wrapper {" + matcher + "}:", query, event, node);
                    }
                    return true;
                }
            }
            case Event.TYPE_DELETE:
                Map<String,Object> oldValues = event.getOldValues();
                // we have to compare the old value of the field to see if the node used to fall within the
                // constraint. If it did: flush
                if(matcher.eventApplies(event.getOldValues(), event)){
                    boolean eventMatches = matcher.nodeMatchesConstraint(oldValues, event);
                    if (log.isDebugEnabled()) {
                        logResult( (eventMatches ? "" : "no ") + "flush: with matcher {"+matcher+"}:", query, event, null);
                    }
                    return eventMatches;
                } else {
                    if (log.isDebugEnabled()) {
                        logResult("flush: event does not apply to wrapper {" + matcher + "}:", query, event, null);
                    }
                    return true;
                }
            default:
                log.error("Unrecognized event-type " + event.getType());
                break;
            }
        }
        return true; //safe: should release
    }

    protected final boolean doEvaluate(RelationEvent event, SearchQuery query, List<MMObjectNode> cachedResult) {
        // TODO I don't think this strategy should handle these events
        //because the node event that preceeds the relation event takes care of it.
        return doEvaluate(event.getNodeEvent(), query, cachedResult);
    }

    /**
     * This method will find a constraint matcher that supports the given constraint, and will return the
     * UnsupportedConstraintMatcher if none is found.
     *
     * @param constraint
     */
    protected final static AbstractConstraintMatcher findMatcherForConstraint(Constraint constraint) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String constraintClassName = constraint.getClass().getName();
        constraintClassName = constraintClassName.substring(constraintClassName.lastIndexOf(".") + 1);


        // MM: I think the idea behind this is questionable.
        // How expensive is it?
        Constructor matcherConstructor = constraintMatcherConstructors.get(constraintClassName + "Matcher");
        if (matcherConstructor == null) {
            log.error("Could not match constraint of type " + constraintClassName);
            matcherConstructor = UnsupportedConstraintMatcher.class.getConstructors()[0];
        }
        if (log.isDebugEnabled()) {
            log.debug("finding matcher for constraint class name: " + constraintClassName + "Matcher");
            log.trace("matcher class found: " + matcherConstructor.getDeclaringClass().getName());
        }

        return (AbstractConstraintMatcher) matcherConstructor.newInstance(new Object[] { constraint });

    }

    protected static abstract class AbstractConstraintMatcher {

        /**
         * @param valuesToMatch the field values that the constraint value will have to be matched against.
         * this will sometimes be the 'oldValues' and sometimes be the 'newValues' from the event.
         * @return true if the values of event falls within the limits of the constraint
         */
        abstract public boolean nodeMatchesConstraint(Map<String,Object> valuesToMatch, NodeEvent event);
        /**
         * @param valuesToMatch map of (changed) fields with their values
         * @param event the event that has occured
         * @return true if the wrapped constraint matches the node event
         */
        abstract public boolean eventApplies(Map<String,Object> valuesToMatch, NodeEvent event);
        @Override
        abstract public String toString();
    }






    private static class BasicCompositeConstraintMatcher extends AbstractConstraintMatcher {
        private final List<AbstractConstraintMatcher> wrappedConstraints;
        private final BasicCompositeConstraint wrappedCompositeConstraint;

        public BasicCompositeConstraintMatcher(BasicCompositeConstraint constraint) throws NoSuchMethodException, InstantiationException, InvocationTargetException, IllegalAccessException  {
            wrappedCompositeConstraint = constraint;
            wrappedConstraints = new ArrayList<AbstractConstraintMatcher>();
            for (Constraint c : wrappedCompositeConstraint.getChilds()) {
                wrappedConstraints.add(findMatcherForConstraint(c));
            }
        }

        public boolean nodeMatchesConstraint(Map<String,Object> valuesToMatch, NodeEvent event) {
            int matches = 0;
            for (AbstractConstraintMatcher acm : findRelevantConstraints(valuesToMatch, event)) {
                if (log.isDebugEnabled()) {
                    log.debug("** relevant constraint found: " + acm);
                }
                if (acm.nodeMatchesConstraint(valuesToMatch, event)){
                    matches ++;
                    if (log.isDebugEnabled()) {
                        log.debug("** constraint created a match on " + valuesToMatch);
                    }
                } else if (log.isDebugEnabled()) {
                    log.debug("** constraint created _NO_ match on " + valuesToMatch);
                }
            }
            if (wrappedCompositeConstraint.getLogicalOperator() == CompositeConstraint.LOGICAL_AND) {
                return (matches == wrappedConstraints.size()) != wrappedCompositeConstraint.isInverse();
            } else {
                return (matches > 0) != wrappedCompositeConstraint.isInverse();
            }
        }

        public String toString(){
            StringBuffer sb = new StringBuffer("Composite Wrapper. type: ");
            sb.append(wrappedCompositeConstraint.getLogicalOperator() == CompositeConstraint.LOGICAL_AND ? "AND" : "OR");
            sb.append(" [");
            for (Iterator<AbstractConstraintMatcher> i = wrappedConstraints.iterator(); i.hasNext();) {
            	sb.append("{");
                sb.append(i.next().toString());
                if(i.hasNext()) sb.append("} {");
            }
            sb.append("}]");
            return sb.toString();
        }

        /**
         * for composite constraint wrappers the rule is that if the operator is AND and all
         * of it's constraints are relevant it is relevant, and if the opreator is OR and one or more of it's
         * constraints are relevant it is relevant
         */
        public boolean eventApplies(Map<String,Object> valuesToMatch, NodeEvent event) {
            List<AbstractConstraintMatcher> relevantConstraints =  findRelevantConstraints(valuesToMatch, event);
            if (log.isDebugEnabled()) {
                log.debug("** relevant constraints:  " + relevantConstraints);
            }
            if(wrappedCompositeConstraint.getLogicalOperator() == CompositeConstraint.LOGICAL_AND){
                if(wrappedConstraints.size() == relevantConstraints.size()) {
                    log.debug("** composite AND: all constraints match, event applies to query");
                    return true;
                } else {
                    log.debug("** composite AND: not all constraints match, so the event does not apply to this constraint");
                }
            } else {
                if(relevantConstraints.size() > 0){
                    log.debug("** composite OR: more than zero constraints match, so event applies to query");
                    return true;
                }else{
                    log.debug("** composite OR: zero constraints match, so event does not apply to query.");
                }

            }
            return false;
        }


        private List<AbstractConstraintMatcher> findRelevantConstraints(Map<String,Object> valuesToMatch, NodeEvent event){
            List<AbstractConstraintMatcher> relevantConstraints = new ArrayList<AbstractConstraintMatcher>();
            for (AbstractConstraintMatcher matcher : wrappedConstraints) {
                if(matcher.eventApplies(valuesToMatch, event))relevantConstraints.add(matcher);
            }
            return relevantConstraints;
        }

    }





    private static class UnsupportedConstraintMatcher extends AbstractConstraintMatcher {

        final Constraint wrappedConstraint;
        public UnsupportedConstraintMatcher(Constraint constraint)  {
            wrappedConstraint = constraint;
        }

        /**
         * Return true here, to make sure the query gets flushed.
         */
        public boolean nodeMatchesConstraint(Map<String,Object> valuesToMatch, NodeEvent event) {
            return true;
        }

        public String toString(){
            return "Unsupported Matcher. masking for constraint: " + wrappedConstraint.getClass().getName();
        }

        public boolean eventApplies(Map<String,Object> valuesToMatch, NodeEvent event) {
            return true;
        }
    }


    private static class BasicLegacyConstraintMatcher extends UnsupportedConstraintMatcher {
        public BasicLegacyConstraintMatcher(Constraint constraint)  {
            super(constraint);
        }
    }




    /**
     * This class is a base for the field comparison constraints. it provides the means to perform all supported
     * comparisons on all supported data types.
     *
     * @author ebunders
     */
    private static abstract class FieldCompareConstraintMatcher extends AbstractConstraintMatcher {

        protected abstract int getOperator();

        protected boolean valueMatches(final Class<?> fieldType, Object constraintValue, Object valueToCompare, final boolean isCaseSensitive) {
            if (log.isDebugEnabled()) {
                log.debug("**method: valueMatches() fieldtype: " + fieldType);
            }
            if (constraintValue == null) return valueToCompare == null;

            int operator =  getOperator();

            if (fieldType.equals(Boolean.class)) {
                boolean constraintBoolean = Casting.toBoolean(constraintValue);
                boolean booleanToCompare = Casting.toBoolean(valueToCompare);
                switch(operator) {
                case FieldCompareConstraint.EQUAL:     return booleanToCompare == constraintBoolean;
                case FieldCompareConstraint.NOT_EQUAL: return booleanToCompare != constraintBoolean;
                default:
                    log.warn("operator " + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[operator] + "is not supported for type Boolean");
                    return true;
                }
            } else if (fieldType.equals(Float.class)) {
                float constraintFloat = Casting.toFloat(constraintValue, Float.MAX_VALUE);
                float floatToCompare = Casting.toFloat(valueToCompare, Float.MAX_VALUE);
                //if either value could not be cast to an int, return true, which is safe
                if(constraintFloat == Float.MAX_VALUE || floatToCompare == Float.MAX_VALUE) {
                    log.warn("either " + constraintValue + " or " + valueToCompare + " could not be cast to type float (while that is supposed to be their type)");
                    return true;
                }
                return floatMatches(constraintFloat, floatToCompare, operator);
            } else if (fieldType.equals(Double.class)) {
                double constraintDouble = Casting.toDouble(constraintValue, Double.MAX_VALUE);
                double doubleToCompare = Casting.toDouble(valueToCompare, Double.MAX_VALUE);
                //if either value could not be cast to an int, return true, which is safe
                if(constraintDouble == Double.MAX_VALUE || doubleToCompare == Double.MAX_VALUE) {
                    log.warn("either " + constraintValue + " or " + valueToCompare + " could not be cast to type double (while that is supposed to be their type)");
                    return true;
                }
                return floatMatches(constraintDouble, doubleToCompare, operator);
            } else if (fieldType.equals(Date.class)) {
                long constraintLong = Casting.toLong(constraintValue, Long.MAX_VALUE);
                long longToCompare = Casting.toLong(valueToCompare, Long.MAX_VALUE);

                //if either value could not be cast to an int, return true, which is safe
                if(constraintLong == Long.MAX_VALUE || longToCompare == Long.MAX_VALUE) {
                    log.warn("either " + constraintValue + " or " + valueToCompare + " could not be cast to type long (while they are supposed to be of type Date supposed to be their type)");
                    return true;
                }
                return intMatches(constraintLong, longToCompare, operator);
            } else if (fieldType.equals(Integer.class)) {
                int constraintInt = Casting.toInt(constraintValue, Integer.MAX_VALUE);
                int intToCompare = Casting.toInt(valueToCompare, Integer.MAX_VALUE);

                //if either value could not be cast to an int, return true, which is safe
                if(constraintInt == Integer.MAX_VALUE || intToCompare == Integer.MAX_VALUE) {
                    log.warn("either " + constraintValue + " or " + valueToCompare + " could not be cast to type int (while that is supposed to be their type)");
                    return true;
                }
                return intMatches(constraintInt, intToCompare, operator);
            } else if (fieldType.equals(Long.class)) {
                long constraintLong = Casting.toLong(constraintValue, Long.MAX_VALUE);
                long longToCompare = Casting.toLong(valueToCompare, Long.MAX_VALUE);
//              if either value could not be cast to a long, return true, which is safe
                if(constraintLong == Long.MAX_VALUE || longToCompare == Long.MAX_VALUE) {
                    // how can this ever happen?
                    log.warn("either [" + constraintValue +"] " + (constraintValue == null ? "": "of type "  + constraintValue.getClass().getName()) +
                             " or [" + valueToCompare + "] of type " + (valueToCompare == null ? "": "of type "  + valueToCompare.getClass().getName()) +
                             " could not be cast to type long (while that is supposed to be their type)");
                    return true;

                }
                return intMatches(constraintLong, longToCompare, operator);
            }  else if (fieldType.equals(Node.class)) {
                if(constraintValue instanceof MMObjectNode) constraintValue = ((MMObjectNode)constraintValue).getNumber();
                if(valueToCompare instanceof MMObjectNode) valueToCompare   = ((MMObjectNode)valueToCompare).getNumber();
                int constraintInt = Casting.toInt(constraintValue, Integer.MAX_VALUE);
                int intToCompare = Casting.toInt(valueToCompare, Integer.MAX_VALUE);
//              if either value could not be cast to a Node, return true, which is safe
                if(constraintInt == Integer.MAX_VALUE || intToCompare == Integer.MAX_VALUE) {
                    log.warn("either [" + constraintValue +"] " + (constraintValue == null ? "": "of type "  + constraintValue.getClass().getName()) +
                             " or [" + valueToCompare + "] of type " + (valueToCompare == null ? "": "of type "  + valueToCompare.getClass().getName()) +
                             " could not be cast to type int  (while they should be type node)");
                    return true;

                }
                return intMatches(constraintInt, intToCompare, operator);
            } else if (fieldType.equals(String.class) || fieldType.equals(org.w3c.dom.Document.class)) {
                String constraintString = Casting.toString(constraintValue);
                String stringToCompare =  Casting.toString(valueToCompare);
                return stringMatches(constraintString, stringToCompare, operator, isCaseSensitive);
            }

            return false;
        }

        private boolean floatMatches(double constraintDouble, double doubleTocompare, int operator)  {
            switch(operator) {
            case FieldCompareConstraint.EQUAL:         return doubleTocompare == constraintDouble;
            case FieldCompareConstraint.GREATER:       return doubleTocompare > constraintDouble;
            case FieldCompareConstraint.GREATER_EQUAL: return doubleTocompare >= constraintDouble;
            case FieldCompareConstraint.LESS:          return doubleTocompare < constraintDouble;
            case FieldCompareConstraint.LESS_EQUAL:    return doubleTocompare <= constraintDouble;
            case FieldCompareConstraint.NOT_EQUAL:     return doubleTocompare != constraintDouble;
            default:
                log.warn("operator " + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[operator] + "for any numeric type");
                return true;

            }
        }

        private boolean intMatches(long constraintLong, long longToCompare, int operator) {
            switch(operator) {
            case FieldCompareConstraint.EQUAL:         return longToCompare == constraintLong;
            case FieldCompareConstraint.GREATER:       return longToCompare > constraintLong;
            case FieldCompareConstraint.GREATER_EQUAL: return longToCompare >= constraintLong;
            case FieldCompareConstraint.LESS:          return longToCompare < constraintLong;
            case FieldCompareConstraint.LESS_EQUAL:    return longToCompare <= constraintLong;
            case FieldCompareConstraint.NOT_EQUAL:     return longToCompare != constraintLong;
            default:
                log.warn("operator " + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[operator] + "for any numeric type");
                return true;
            }
        }

        private boolean stringMatches(String constraintString, String stringToCompare, int operator, boolean isCaseSensitive) {
            switch(operator) {
            case FieldCompareConstraint.EQUAL:         return stringToCompare.equals(constraintString);
                // TODO: MM: I think depending on the database configuration the case-sensitivity may be important in the following 4:
            case FieldCompareConstraint.GREATER:       return stringToCompare.compareTo(constraintString) > 0;
            case FieldCompareConstraint.LESS:          return stringToCompare.compareTo(constraintString) < 0;
            case FieldCompareConstraint.LESS_EQUAL:    return stringToCompare.compareTo(constraintString) <= 0;
            case FieldCompareConstraint.GREATER_EQUAL: return stringToCompare.compareTo(constraintString) >= 0;
            case FieldCompareConstraint.LIKE:          return likeMatches(constraintString, stringToCompare, isCaseSensitive);
            case FieldCompareConstraint.NOT_EQUAL:     return ! stringToCompare.equals(constraintString);
            default:
                log.warn("operator " + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[operator] + "is not supported for type String");
                return true;
            }
        }

        private boolean likeMatches(String constraintString, String stringToCompare, boolean isCaseSensitive){
            if (log.isTraceEnabled()) {
                log.trace("** method: likeMatches() stringToCompare: " + stringToCompare + ", constraintString: " + constraintString );
            }
            if(isCaseSensitive){
                constraintString = constraintString.toLowerCase();
                stringToCompare = stringToCompare.toLowerCase();
            }
            char[] chars = constraintString.toCharArray();
            StringBuffer sb = new StringBuffer();

            for (char element : chars) {
                if(element == '?'){
                    sb.append(".");
                } else if(element == '%'){
                    sb.append(".*");
                } else if(escapeChars.indexOf(element) > -1){
                    sb.append("\\");
                    sb.append(element);
                } else{
                    sb.append(element);
                }
            }
            if (log.isDebugEnabled()) {
                log.trace("** new pattern: " + sb.toString());
            }
            return stringToCompare.matches(sb.toString());
        }

        protected Class<?> getFieldTypeClass(StepField stepField) {
            MMBase mmbase = MMBase.getMMBase();
            // why it this checked anyway?
            CoreField field = mmbase.getBuilder(stepField.getStep().getTableName()).getField(stepField.getFieldName());
            DataType<?> fieldType = field.getDataType();
            Class<?> fieldTypeClass = fieldType.getTypeAsClass();
            if( fieldTypeClass.equals(Boolean.class) ||
                fieldTypeClass.equals(Date.class) ||
                fieldTypeClass.equals(Integer.class) ||
                fieldTypeClass.equals(Long.class) ||
                fieldTypeClass.equals(Float.class) ||
                fieldTypeClass.equals(Double.class) ||
                fieldTypeClass.equals(Node.class) ||
                fieldTypeClass.equals(String.class) ||
                fieldTypeClass.equals(org.w3c.dom.Document.class)) {
                if (log.isDebugEnabled()) {
                    log.debug("** found field type: " + fieldTypeClass.getName());
                }
            } else {
                throw new RuntimeException("Field type " + fieldTypeClass + " is not supported");
            }
            return fieldTypeClass;
        }

    }



    private static class BasicFieldValueConstraintMatcher extends FieldCompareConstraintMatcher {
        private final Class<?> fieldTypeClass;
        protected final StepField stepField;
        protected final BasicFieldValueConstraint wrappedFieldValueConstraint;

        public BasicFieldValueConstraintMatcher(BasicFieldValueConstraint constraint)  {
            stepField = constraint.getField();
            if (log.isDebugEnabled()) {
                log.debug("** builder: " + stepField.getStep().getTableName()+". field: " + stepField.getFieldName());
            }
            fieldTypeClass = getFieldTypeClass(stepField);
            wrappedFieldValueConstraint = constraint;

        }

        protected int getOperator() {
            return wrappedFieldValueConstraint.getOperator();
        }
        /**
         * Check the values to see if the values of the node matches the constraint.
         */
        public boolean nodeMatchesConstraint(Map<String,Object> valuesToMatch, NodeEvent event)  {
            log.debug("**method: nodeMatchesConstraint");
            //if(! eventApplies(valuesToMatch, event)) throw new FieldComparisonException("constraint " + wrappedFieldCompareConstraint.toString() + "does not match event of type " +event.getBuilderName());
            boolean matches = valueMatches(fieldTypeClass,
                                           wrappedFieldValueConstraint.getValue(),
                                           valuesToMatch.get(stepField.getFieldName()),
                                           wrappedFieldValueConstraint.isCaseSensitive());
            return  matches != wrappedFieldValueConstraint.isInverse();
        }

        public String toString(){
            return "Field Value Matcher.  operator: " + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[wrappedFieldValueConstraint.getOperator()] +
            ", value: " + wrappedFieldValueConstraint.getValue().toString() + ", step: " +stepField.getStep().getTableName() +
            ", field name: " + stepField.getFieldName();
        }


        /**
         * An event applies to a field value constraint wrapper if the wrapper is of the same type as the event, and the field
         * that is being checked is in the 'changed' fields map (valuesToMatch)
         */
        public boolean eventApplies(Map<String,Object> valuesToMatch, NodeEvent event) {
            return
                wrappedFieldValueConstraint.getField().getStep().getTableName().equals(event.getBuilderName()) &&
                valuesToMatch.containsKey(wrappedFieldValueConstraint.getField().getFieldName());
        }

    }




    /**
     * @since MMBase-1.8.1
     */
    private static class BasicFieldValueBetweenConstraintMatcher extends AbstractConstraintMatcher {

        protected final StepField stepField;
        protected final BasicFieldValueBetweenConstraint wrappedFieldConstraint;

        public BasicFieldValueBetweenConstraintMatcher(BasicFieldValueBetweenConstraint constraint)  {
            stepField = constraint.getField();
            wrappedFieldConstraint = constraint;
        }

        public boolean nodeMatchesConstraint(Map<String,Object> valuesToMatch, NodeEvent event) {
            return true;
        }

        public boolean eventApplies(Map<String,Object> valuesToMatch, NodeEvent event) {
            return true;
        }

        public String toString() {
            return "Field Value Between Matcher.  operator: " +
            ", step: " +stepField.getStep().getTableName() +
            ", field name: " + stepField.getFieldName();
        }

    }

    /**
     * @since MMBase-1.8.1
     */
    private static class BasicFieldValueInConstraintMatcher extends FieldCompareConstraintMatcher {
        private final Class<?> fieldTypeClass;
        protected final StepField stepField;
        protected final BasicFieldValueInConstraint wrappedFieldValueInConstraint;

        public BasicFieldValueInConstraintMatcher(BasicFieldValueInConstraint constraint)  {
            stepField = constraint.getField();
            fieldTypeClass = getFieldTypeClass(stepField);
            wrappedFieldValueInConstraint = constraint;
        }

        protected int getOperator() {
            return FieldCompareConstraint.EQUAL;
        }

        /**
         * Check the values to see if the node's value matches the constraint.
         */
        public boolean nodeMatchesConstraint(Map<String,Object> valuesToMatch, NodeEvent event) {
            log.debug("**method: nodeMatchesConstraint");
            SortedSet<Object> values = wrappedFieldValueInConstraint.getValues();
            boolean matches = false;
            Iterator<Object> i = values.iterator();
            while (i.hasNext() && !matches) {
                Object value = i.next();
                matches = valueMatches(fieldTypeClass,
                                       value,
                                       valuesToMatch.get(stepField.getFieldName()),
                                       wrappedFieldValueInConstraint.isCaseSensitive());
            }
            return  matches != wrappedFieldValueInConstraint.isInverse();
        }

        public String toString(){
            return "Field Value IN Matcher.  operator: " +
                ", value: " + wrappedFieldValueInConstraint.getValues().toString() + ", step: " +stepField.getStep().getTableName() +
                ", field name: " + stepField.getFieldName();
        }



        public boolean eventApplies(Map<String,Object> valuesToMatch, NodeEvent event) {
            return
                wrappedFieldValueInConstraint.getField().getStep().getTableName().equals(event.getBuilderName()) &&
                valuesToMatch.containsKey(wrappedFieldValueInConstraint.getField().getFieldName());
        }
    }


    private void logResult(String comment, SearchQuery query, Event event, MMObjectNode node){
        if(log.isDebugEnabled()){
            String role="";
            // a small hack to limit the output
            if (event instanceof RelationEvent) {
                //get the role name
                RelationEvent revent = (RelationEvent) event;
                MMObjectNode relDef = MMBase.getMMBase().getBuilder("reldef").getNode(revent.getRole());
                role = " role: " + relDef.getStringValue("sname") + "/" + relDef.getStringValue("dname");
                //filter the 'object' events
                if (revent.getRelationSourceType().equals("object")
                        || revent.getRelationDestinationType().equals("object"))
                    return;
            }
            try {
                log.debug("\n******** \n**" + comment + "\n**" + event.toString() + role + "\n**nodevalues: " + (node == null ? "NODE NULL" : "" + node.getValues()) + "\n**"
                        + sqlHandler.toSql(query, sqlHandler) + "\n******");
            } catch (SearchQueryException e) {
                log.error(e);
            }
        }
    }


}
