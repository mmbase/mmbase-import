/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.cache;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.xerces.impl.xpath.regex.Match;
import org.mmbase.core.CoreField;
import org.mmbase.core.event.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.Level;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.*;

/**
 * This strategy will evaluate the constraint on a a query object against a NodeEvent. It will appy the following rules:<br>
 * <b>new node/delete node</b><br>
 * <ul>
 * <li>If the step of a constraint matches the type of the event, and the node's values don't fall within the
 * constraint: don't flush.</li>
 * <li>If the step of a constraint matches the type of the event, and the node's values dous fall within the
 * constraint: flush.</li>
 * <li>If no constraints have a step matching the type of the event: flush.
 * </ul>
 * <b>change node</b> Like the abouve, but an extra check has to be made:
 * <ul>
 * <li>if the node preveously fell within the constraints but now dousn't: flush</li>
 * <li>if the node preveously didn't fall within the constraints but now dous: flush</li>
 * </ul>
 * 
 * @author ebunders
 * @since MMBase-1.8
 * @version $Id:
 * 
 */
public class ConstraintsMatchingStrategy extends ReleaseStrategy {

    private static final Logger log = Logging.getLoggerInstance(ConstraintsMatchingStrategy.class);
    
    private static Cache constraintWrapperCache = new Cache(1000){
        public String getName(){return "Constraint Matcher Cache";}
        public String getDescription(){return "Caches wrapped constraints used for matching them to changed node values";}
    };
    
    /*
    static{
        Cache.putCache(constraintWrapperCache);
    }
    */

    private static HashMap constraintMatcherClasses;
    static {
        constraintMatcherClasses = new HashMap();
        Class[] innerClasses = ConstraintsMatchingStrategy.class.getDeclaredClasses();
        for (int i = 0; i < innerClasses.length; i++) {
            Class innerClass = innerClasses[i];
            if (innerClass.getName().endsWith("Matcher")) {
                String matcherClassName = innerClass.getName();
                matcherClassName = matcherClassName.substring(matcherClassName.lastIndexOf("$") + 1);
                constraintMatcherClasses.put(matcherClassName, innerClass);
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
        return "Checks wether a changed node has a matching step within a queries constraint, and then checks "
                + "if the node falls within the constraint. For changed nodes a check is made if the node preveously "
                + "fell within the constraint and if it dous so now. Queries that exclude changed nodes by their constraints "
                + "will not be flushed.";
    }

    protected boolean doEvaluate(NodeEvent event, SearchQuery query, List cachedResult) {
        if(query.getConstraint() == null) return true; //should release
        AbstractConstraintMatcher matcher = (AbstractConstraintMatcher) constraintWrapperCache.get(query);
        if(matcher == null){
            try {
                matcher = findMatcherForConstraint(query.getConstraint());
                log.debug("created constraint matcher: " + matcher.toString());
                //if anything goes wrong constraintMatches is true, which means the query should be flushed
            } catch (ConstraintMatcherCreationException e) {
            log.error("Could not create constraint matcher for constraint: " + query.getConstraint().toString() + "main reason: " + e.toString() , e);
            }
        }
         if(matcher != null){
            try {
                if (event.getType() == NodeEvent.EVENT_TYPE_NEW) {
                    // we have to compare the constraint value with the new value of the changed field to see if the new
                    // node falls within the constraint
                    return matcher.nodeMatchesConstraint(event.getNewValues());

                } else if (event.getType() == NodeEvent.EVENT_TYPE_CHANGED) {
                    // we have to compare the old value and then the new value of the changed field to see if the status
                    // has changed
                    return ( matcher.nodeMatchesConstraint(event.getOldValues()) == matcher.nodeMatchesConstraint(event.getNewValues()) );

                } else if (event.getType() == NodeEvent.EVENT_TYPE_DELETE) {
                    // we have to compare the old value of the field to see if the node used to fall within the
                    // constriant
                    return matcher.nodeMatchesConstraint(event.getOldValues());
                }
            } catch (FieldComparisonException e) {
                log.debug(Logging.stackTrace(e));
            }
         }
        return true; //safe: should release
    }

    /**
     * this method will find a constraint matcher that supports the given constraint, and will return the
     * UnsupportedConstraintMatcher if non is found
     * 
     * @param constraint
     * @return
     * @throws ConstraintMatcherCreationException
     *             when instantiation went wrong
     */
    protected static AbstractConstraintMatcher findMatcherForConstraint(Constraint constraint)
            throws ConstraintMatcherCreationException {
        String constraintClassName = constraint.getClass().getName();
        constraintClassName = constraintClassName.substring(constraintClassName.lastIndexOf(".") + 1);
        
        log.debug("** finding matcher for constraint class name: " + constraintClassName + "Matcher");
        
        Class matcherClass = (Class) constraintMatcherClasses.get(constraintClassName + "Matcher");
        if (matcherClass == null) {
            matcherClass = UnsupportedConstraintMatcher.class;
        }
        log.debug("** matcher class found: " + matcherClass.getName());
        
        Constructor c = null;
        try {
            c = matcherClass.getConstructor(new Class[] { Constraint.class });
            if(c == null)log.debug("** help! constructor is null");
            return (AbstractConstraintMatcher) c.newInstance(new Object[] { constraint });
        }catch(InvocationTargetException e){
            throw new ConstraintMatcherCreationException("During instantiation the constructor of matcher " + matcherClass.toString() + " threw the following exception: " +
                    e.getTargetException().toString());
        }catch(Exception e){
            throw new ConstraintMatcherCreationException("Could not create instance of class " + matcherClass.toString() +
                    ". main reason: " + e.toString());
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Logging.getLoggerInstance(ConstraintsMatchingStrategy.class).setLevel(Level.DEBUG);
        
        Class cl = UnsupportedConstraintMatcher.class;
        try {
            Constructor c = cl.getConstructor(new Class[] { Constraint.class });
            AbstractConstraintMatcher matcherInstance;
            //matcherInstance = (AbstractConstraintMatcher) c.newInstance(new Object[] { constraint });
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        
    }

    private static abstract class AbstractConstraintMatcher {
        protected Constraint wrappedConstraint;

        public AbstractConstraintMatcher(Constraint constraint) throws ConstraintMatcherCreationException {
            wrappedConstraint = constraint;
        }

        /**
         * @param valuesToMatch the field values that the constraint value will have to be matched against. 
         * this will sometimes be the 'oldValues' and sometimes be the 'newValues' from the event.
         * @return true if the values of event falls within the limits of the constraint
         * @throws FieldComparisonException
         */
        abstract public boolean nodeMatchesConstraint(Map valuesToMatch)  throws FieldComparisonException ;
        abstract public String toString();
    }

    
    
    
    
    
    private static class BasicCompositeConstraintMatcher extends AbstractConstraintMatcher {
        private List wrappedConstraints;
        private BasicCompositeConstraint wrappedCompositeConstraint;

        public BasicCompositeConstraintMatcher(Constraint constraint) throws ConstraintMatcherCreationException {
            super(constraint);
            wrappedCompositeConstraint = (BasicCompositeConstraint) wrappedConstraint;
            wrappedConstraints = new ArrayList();
            for (Iterator i = wrappedCompositeConstraint.getChilds().iterator(); i.hasNext();) {
                Constraint c = (Constraint) i.next();
                wrappedConstraints.add(findMatcherForConstraint(c));
            }
        }

        public boolean nodeMatchesConstraint(Map valuesToMatch) {
            int matches = 0;
            for (Iterator i = wrappedConstraints.iterator(); i.hasNext();) {
                AbstractConstraintMatcher acm = (AbstractConstraintMatcher) i.next();
                try {
                    if (acm.nodeMatchesConstraint(valuesToMatch))
                        matches ++;
                } catch (FieldComparisonException e) {
                    //if this happens, we count it as a match, which will couse a fluse (safe)
                    matches ++;
                }
            }
            if (wrappedCompositeConstraint.getLogicalOperator() == BasicCompositeConstraint.LOGICAL_AND) {
                return matches == wrappedConstraints.size();
            } else {
                return matches > 0;
            }
        }
        
        public String toString(){
            StringBuffer sb = new StringBuffer("Composite Wrapper. type: ");
            sb.append(wrappedCompositeConstraint.getLogicalOperator() == BasicCompositeConstraint.LOGICAL_AND ? "AND" : "OR");
            sb.append(" [");
            for (Iterator i = wrappedConstraints.iterator(); i.hasNext();) {
                sb.append(((AbstractConstraintMatcher)i.next()).toString());
                if(i.hasNext()) sb.append(":");
            }
            sb.append("]");
            return sb.toString();
        }

    }

    
    
    
    
    
    
    private static class UnsupportedConstraintMatcher extends AbstractConstraintMatcher {
        
        public UnsupportedConstraintMatcher(Constraint constraint) throws ConstraintMatcherCreationException {
            super(constraint);
        }

        /**
         * Return true here, to make shure the query gets flushed.
         */
        public boolean nodeMatchesConstraint(Map valuesToMatch) {
            return true;
        }

        public String toString(){
            return "Unsupported Matcher. masking for constraint: " + wrappedConstraint.getClass().getName();
        }
    }
    
    
    
    

    /**
     * This class is a base for the field comparison constraints. it provides the means to perform all supported
     * comparisons on all supported data types.
     * 
     * @author ebunders
     */
    private static abstract class FieldCompareConstraintMatcher extends AbstractConstraintMatcher {
        protected FieldCompareConstraint wrappedFieldCompareConstraint;

        public FieldCompareConstraintMatcher(Constraint constraint) throws ConstraintMatcherCreationException {
            super(constraint);
            wrappedFieldCompareConstraint = (FieldCompareConstraint) wrappedConstraint;
        }

        protected boolean valueMatches(Class fieldType, Object constraintValue, Object valueToCompare)
                throws FieldComparisonException {
            int operator = wrappedFieldCompareConstraint.getOperator();

            // handle boolean type
            if (fieldType.equals(Boolean.class)) {
                boolean constraintBoolean = Casting.toBoolean(constraintValue);
                boolean booleanToCompare = Casting.toBoolean(valueToCompare);

                if (operator == FieldCompareConstraint.EQUAL) {
                    return booleanToCompare == constraintBoolean;
                } else if (operator == FieldCompareConstraint.NOT_EQUAL) {
                    return booleanToCompare != constraintBoolean;
                } else {
                    throw new FieldComparisonException("operator "
                            + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[operator] + "is not supported for type Boolean");
                }
            }
            // handle integer type
            if (fieldType.equals(Integer.class)) {
                int constraintInt = Casting.toInt(constraintValue, Integer.MAX_VALUE);
                int intToCompare = Casting.toInt(valueToCompare, Integer.MAX_VALUE);
                //if either value could not be cast to an int, return true, which is safe
                if(constraintInt == Integer.MAX_VALUE || intToCompare == Integer.MAX_VALUE) return true;
                return numberMatches(constraintInt, intToCompare, operator);
            }

            // handle long type
            if (fieldType.equals(Long.class)) {
                long constraintLong = Casting.toLong(constraintValue, Long.MAX_VALUE);
                long longToCompare = Casting.toLong(valueToCompare, Long.MAX_VALUE);
//              if either value could not be cast to a long, return true, which is safe
                if(constraintLong == Long.MAX_VALUE || longToCompare == Long.MAX_VALUE) return true;
                return numberMatches(constraintLong, longToCompare, operator);
            }
            
//          handle float type
            if (fieldType.equals(Float.class)) {
                float constraintFloat = Casting.toFloat(constraintValue, Float.MAX_VALUE);
                float floatToCompare = Casting.toFloat(valueToCompare, Float.MAX_VALUE);
//              if either value could not be cast to a float, return true, which is safe
                if(constraintFloat == Float.MAX_VALUE || floatToCompare == Float.MAX_VALUE) return true;
                return numberMatches(constraintFloat, floatToCompare, operator);
            }
            
            //handle string type
            if (fieldType.equals(String.class)) {
                String constraintString = constraintValue.toString();
                String stringToCompare = valueToCompare.toString();
                if(operator == FieldCompareConstraint.EQUAL){
                    return stringToCompare.equals(constraintString);
                }else if(operator == FieldCompareConstraint.GREATER){
                    return (stringToCompare.compareTo(constraintString) > 0);
                }else if(operator == FieldCompareConstraint.LESS){
                    return (stringToCompare.compareTo(constraintString) < 0);
                }else if(operator == FieldCompareConstraint.LIKE){
                    return (stringToCompare.toLowerCase().indexOf(constraintString.toLowerCase()) > -1 );
                }else{
                    throw new FieldComparisonException("operator "
                            + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[operator] + "is not supported for type String");
                }
            }

            return false;
        }

        private boolean numberMatches(double constraintDouble, double doubleTocompare, int operator)
                throws FieldComparisonException {
            if (operator == FieldCompareConstraint.EQUAL) {
                return (doubleTocompare == constraintDouble);
            } else if (operator == FieldCompareConstraint.GREATER) {
                return (doubleTocompare > constraintDouble);
            } else if (operator == FieldCompareConstraint.GREATER_EQUAL) {
                return (doubleTocompare >= constraintDouble);
            } else if (operator == FieldCompareConstraint.LESS) {
                return (doubleTocompare < constraintDouble);
            } else if (operator == FieldCompareConstraint.LESS_EQUAL) {
                return (doubleTocompare <= constraintDouble);
            } else if (operator == FieldCompareConstraint.NOT_EQUAL) {
                return (doubleTocompare != constraintDouble);
            } else {
                throw new FieldComparisonException("operator " + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[operator]
                        + "for any numeric type");
            }
        }

    }

    
    
    
    
    
    private static class BasicFieldValueConstraintMatcher extends FieldCompareConstraintMatcher {
        private Class fieldType;

        private StepField stepField;

        public BasicFieldValueConstraintMatcher(Constraint constraint) throws ConstraintMatcherCreationException {
            super(constraint);
            MMBase mmbase = MMBase.getMMBase();
            stepField = ((FieldConstraint) constraint).getField();
            log.debug("** builder: " + stepField.getStep().getTableName()+". field: " + stepField.getFieldName());
            CoreField field = mmbase.getBuilder(stepField.getStep().getTableName()).getField(stepField.getFieldName());
            log.debug("** corefield: " + field.toString());
            String type = "";
            switch (field.getStorageType()) {
            case Field.TYPE_BINARY:
                fieldType = null;
                type = "binary";
                break;
            case Field.TYPE_BOOLEAN:
                fieldType = Boolean.class;
                break;
            case Field.TYPE_DATETIME:
                fieldType = Long.class; //dates can only be compared as longs
                break;
            case Field.TYPE_DOUBLE:
                fieldType = Double.class;
                break;
            case Field.TYPE_FLOAT:
                fieldType = Float.class;
                break;
            case Field.TYPE_INTEGER:
                fieldType = Integer.class;
                break;
            case Field.TYPE_LIST:
                fieldType = null;
                type = "List";
                break; // don't know how to compare these
            case Field.TYPE_LONG:
                fieldType = Long.class;
                break;
            case Field.TYPE_NODE:
                fieldType = Integer.class;
                break; // nodes are stored by their number, so Integer
            case Field.TYPE_STRING:
                fieldType = String.class;
                break;
            case Field.TYPE_UNKNOWN:
                fieldType = null;
                type = "unknown";
                break; // don't know how to compare this
            case Field.TYPE_XML:
                fieldType = String.class;
                break; // compare like a string
            }
            if (fieldType == null)
                throw new ConstraintMatcherCreationException("type " + type + " is not supported.");
        }

        public boolean nodeMatchesConstraint(Map valuesToMatch) throws FieldComparisonException {
            Object constraintValue = ((FieldValueConstraint) wrappedConstraint).getValue();
            return valueMatches(fieldType, constraintValue, valuesToMatch.get(stepField.getFieldName())); 
        }
        
        public String toString(){
            return "Field Value Matcher. constranint value: " + ((FieldValueConstraint) wrappedConstraint).getValue() + 
            "operator: " + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[wrappedFieldCompareConstraint.getOperator()];
        }

    }

    
    private static class ConstraintMatcherCreationException extends Exception {
        public ConstraintMatcherCreationException(String string) {
            super(string);
        }
    }

    
    
    
    
    
    private static class FieldComparisonException extends Exception {
        public FieldComparisonException(String string) {
            super(string);
        }
    }

}
