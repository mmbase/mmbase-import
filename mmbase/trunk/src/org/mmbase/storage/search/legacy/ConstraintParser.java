/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.storage.search.legacy;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.logging.*;

/**
 * Parser, parses SQL search conditions for a query to a
 * {@link org.mmbase.storage.search.Constraint Constraint} object.
 * <p>
 * This class is provided for the sole purpose of alignment of old code with
 * the new {@link org.mmbase.storage.search.SearchQuery SearchQuery} framework,
 * and should not be called by new code.
 *
 * @author  Rob van Maris
 * @version $Revision: 1.3 $
 * @since MMBase-1.7
 */
public class ConstraintParser {
    
    // Logger instance.
    private final static Logger log = 
        Logging.getLoggerInstance(ConstraintParser.class.getName());
    
    private List steps = null;
    
    /**
     * Parses string or numerical value from list of tokens.
     * If the first token is not "'", it is interpreted as a numerical value,
     * otherwise it is required to be the first token of the sequence
     * "'", "value", "'", representing a string value.
     *
     * @param iTokens Tokens iterator, must be positioned before the (first)
     *        token representing the value.
     * @return A <code>String</code> or <code>Double</code> object representing
     *        the value
     */
    // package visibility!
    static Object parseValue(Iterator iTokens) {
        Object result = null;
        String token = (String) iTokens.next();
        if (token.equals("'")) {
            // String value.
            result = (String) iTokens.next();
            token = (String) iTokens.next();
            if (!token.equals("'")) {
                throw new IllegalArgumentException(
                "Unexpected token (expected \"'\"): \""
                + token + "\"");
             }
        } else {
            result = new Double(token);
        }
        return result;
    }
    
    /**
     * Parses SQL search condition string into separate tokens, discarding
     * white spaces, concatenating strings between (single/double) quotes,
     * and replacing escaped (single/double) quotes in strings by the
     * original character.
     *
     * @param sqlConstraint The SQL constraint string.
     * @return List of tokens.
     */
    // package visibility!
    static List tokenize(String sqlConstraint) {
        // Parse into separate tokens.
        List tokens = new ArrayList();
        StringTokenizer st = new StringTokenizer(sqlConstraint, " ()'\"=<>!,", true);
        tokenize:
            while (st.hasMoreTokens()) {
                String token = st.nextToken(" ()'\"=<>!,");
                
                // String, delimited by single or double quotes.
                if (token.equals("'") || token.equals("\"")) {
                    tokens.add("'");
                    StringBuffer sb = new StringBuffer();
                    while (true) {
                        String token2 = st.nextToken(token);
                        if (token2.equals(token)) {
                            if (!st.hasMoreTokens()) {
                                // Token 2 is end delimiter and last token.
                                tokens.add(sb.toString());
                                tokens.add("'");
                                break tokenize;
                            } else {
                                String token3 = st.nextToken(" ()'\"=<>!,");
                                if (token3.equals(token)) {
                                    // Token 2 and 3 are escaped delimiter.
                                    sb.append(token);
                                } else {
                                    // Token 2 is end delimiter, but not last token.
                                    tokens.add(sb.toString());
                                    tokens.add("'");
                                    token = token3;
                                    break;
                                }
                            }
                        } else {
                            // Token 2 is string.
                            sb.append(token2);
                        }
                    }
                }
                
                // Add token, but skip white spaces.
                if (!token.equals(" ")) {
                    tokens.add(token);
                }
            }
            return tokens;
    }
    
    /**
     * Creates <code>StepField</code> corresponding to field indicated by
     * token, of one of the specified steps.
     * <p>
     * The parsed fieldname can be of one of these forms:
     * <ul>
     * <li><em>fieldname</em>, when only one step is specified.
     * <li><em>stepalias.fieldname</em>, when one or more steps are specified.
     * </ul>
     *
     * @param token The token.
     * @param steps The steps.
     * @return The field.
     */
    // TODO RvM: factor this method out to a separate utility class?
    public static StepField getField(String token, List steps) {
        BasicStep step = null;
        int idx = token.indexOf('.');
        if (idx == -1) {
            if (steps.size() > 1) {
                throw new IllegalArgumentException(
                "Fieldname not prefixed with table alias: \"" 
                + token + "\"");
            }
            step = (BasicStep) steps.get(0);
        } else {
            step = getStep(token.substring(0, idx), steps);
        }
        MMObjectBuilder builder = step.getBuilder();
        String fieldName = token.substring(idx + 1);
        FieldDefs fieldDefs = builder.getField(fieldName);
        if (fieldDefs == null) {
            throw new IllegalArgumentException(
            "Unknown field (of builder " + builder.getTableName()
            + "): \"" + fieldName + "\"");
        }
        BasicStepField field = new BasicStepField(step, fieldDefs)
            .setAlias(token);
        return field;
    }
    
    /**
     * Finds step by alias.
     *
     * @param alias The alias.
     * @param steps The steps
     * @return The step.
     */
    private static BasicStep getStep(String alias, List steps) {
        Iterator iSteps = steps.iterator();
        while (iSteps.hasNext()) {
            BasicStep step = (BasicStep) iSteps.next();
            if (step.getAlias().equals(alias)) {
                return step;
            }
        }
        
        // Not found.
        throw new IllegalArgumentException(
        "Unknown table alias: \"" + alias + "\"");
    }
    
    /** Creates a new instance of ConstraintParser */
    public ConstraintParser(BasicSearchQuery query) {
        this.steps = query.getSteps();
    }
    
    /**
     * Parses SQL search condition string into a 
     * {@link org.mmbase.storage.search.Constraint Constraint} object.
     *
     * @param sqlConstraint The SQL constraint string.
     * @return The constraint.
     */
    public Constraint toConstraint(String sqlConstraint) {
        Constraint result = null;
        try {
            ListIterator iTokens = tokenize(sqlConstraint).listIterator();
            result = parseCondition(iTokens);
            
        // If this doesn't work, fall back to legacy code.
        } catch (Exception e) {
            if (log.isServiceEnabled()) {
                log.service(
                    "Failed to parse Constraint from search condition "
                    + "string: \"" + sqlConstraint + "\", exception:\n"
                    + Logging.stackTrace(e)
                    + "\nFalling back to BasicLegacyConstraint...");
            }
            result = new BasicLegacyConstraint(sqlConstraint);
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Parsed constraint \"" + sqlConstraint 
                + "\" to :\n" + result);
        }
        return result;
    }
    
    /**
     * Creates <code>StepField</code> corresponding to field indicated by
     * token.
     * <p>
     * The parsed fieldname can be of one of these forms:
     * <ul>
     * <li><em>fieldname</em>, when the query has only one step.
     * <li><em>stepalias.fieldname</em>, when the query has one or more steps.
     * </ul>
     *
     * @param token The token.
     * @return The field.
     */
    // package visibility!
    StepField getField(String token) {
        return getField(token, steps);
    }
    
    /**
     * Parses simple SQL search condition string from list of tokens, and 
     * produces a corresponding <code>BasicConstraint</code> object.
     * <p>
     * The parsed condition can be of one of these forms:
     * <ul>
     * <li>fieldname LIKE value
     * <li>fieldname NOT LIKE value
     * <li>fieldname IS NULL
     * <li>fieldname IS NOT NULL
     * <li>fieldname IN (value1, value2, ..)
     * <li>fieldname NOT IN (value1, value2, ..)
     * <li>fieldname = value
     * <li>fieldname <= value
     * <li>fielname <> value
     * <li>fieldname < value
     * <li>fieldname >= value
     * <li>fieldname > value
     * <li>fieldname != value
     * </ul>
     * See {@link #getField()} for the format of fieldname, and 
     * {@link #parseValue()} for the format of value.
     *
     * @param iTokens Tokens iterator, must be positioned before the (first)
     *        token representing the condition.
     * @return The constraint.
     */
    // package visibility!
    // TODO handle comparison of fields
    BasicConstraint parseSimpleCondition(ListIterator iTokens) {
        BasicConstraint result = null;
        
        String token = (String) iTokens.next();
        String function = token.toUpperCase();
        if (function.equals("LOWER") || function.equals("UPPER")) {
            if (iTokens.next().equals("(")) {
                // Function.
                token = (String) iTokens.next();
            } else {
                // Not a function.
                iTokens.previous();
                function = null;
            }
        } else {
            function = null;
        }
                
        StepField field = getField(token);
        
        token = (String) iTokens.next();
        if (function != null) {
            if (!token.equals(")")) {
                throw new IllegalArgumentException(
                    "Unexpected token (expected \")\"): \""
                    + token + "\"");
            }
            token = (String) iTokens.next();
        }

        boolean inverse = false;
        if (token.equalsIgnoreCase("NOT")) {
            // NOT LIKE/NOT IN
            inverse = true;
            token = (String) iTokens.next();
            if (!token.equalsIgnoreCase("LIKE")
                && !token.equalsIgnoreCase("IN")) {
                    throw new IllegalArgumentException(
                        "Unexpected token (expected \"LIKE\" OR \"IN\"): \"" 
                        + token + "\"");
            }
        }
        
        if (token.equalsIgnoreCase("LIKE")) {
            // LIKE 'value'
            String value = (String) parseValue(iTokens);
            boolean caseSensitive = true;
            if (function != null) {
                if ((function.equals("LOWER") 
                    && value.equals(value.toLowerCase()))
                || (function.equals("UPPER")
                    && value.equals(value.toUpperCase()))) {
                        caseSensitive = false;
                }
            }
            result = new BasicFieldValueConstraint(field, value)
                .setOperator(FieldValueConstraint.LIKE)
                .setCaseSensitive(caseSensitive);
            
        } else if (token.equalsIgnoreCase("IS")) {
            // IS [NOT] NULL
            token = (String) iTokens.next();
            if (token.equalsIgnoreCase("NOT")) {
                inverse = !inverse;
                token = (String) iTokens.next();
            }
            if (token.equalsIgnoreCase("NULL")) {
                result = new BasicFieldNullConstraint(field);
            } else {
                throw new IllegalArgumentException(
                    "Unexpected token (expected \"NULL\"): \""
                    + token + "\"");
            }
        } else if (token.equalsIgnoreCase("IN")) {
            // IN (value1, value2, ...)
            String separator = (String) iTokens.next();
            if (!separator.equals("(")) {
                throw new IllegalArgumentException(
                    "Unexpected token (expected \"(\"): \""
                    + separator + "\"");
            }
            BasicFieldValueInConstraint fieldValueInConstraint 
                = new BasicFieldValueInConstraint(field);
            if (!iTokens.next().equals(")")) {
                
                iTokens.previous();
                do {
                    Object value = parseValue(iTokens);
                    separator = (String) iTokens.next();
                    if (separator.equals(",") || separator.equals(")")) {
                        fieldValueInConstraint.addValue(value);
                    } else {
                        throw new IllegalArgumentException(
                            "Unexpected token (expected \",\" or \")\"): \""
                            + separator + "\"");
                    }
                } while (separator.equals(","));
            }
            result = fieldValueInConstraint;

        } else if (token.equals("=")) {
            // = value
            Object value = parseValue(iTokens);
            result = new BasicFieldValueConstraint(field, value)
                .setOperator(FieldValueConstraint.EQUAL);
            
        } else if (token.equals("<")) {
            token = (String) iTokens.next();
            if (token.equals("=")) {
                // <= value
                Object value = parseValue(iTokens);
                result = new BasicFieldValueConstraint(field, value)
                    .setOperator(FieldValueConstraint.LESS_EQUAL);

            } else if (token.equals(">")) {
                // <> value
                Object value = parseValue(iTokens);
                result = new BasicFieldValueConstraint(field, value)
                    .setOperator(FieldValueConstraint.NOT_EQUAL);

            } else {
                // < value
                iTokens.previous();
                Object value = parseValue(iTokens);
                result = new BasicFieldValueConstraint(field, value)
                    .setOperator(FieldValueConstraint.LESS);

            }
        } else if (token.equals(">")) {
            token = (String) iTokens.next();
            if (token.equals("=")) {
                // >= value
                Object value = parseValue(iTokens);
                result = new BasicFieldValueConstraint(field, value)
                    .setOperator(FieldValueConstraint.GREATER_EQUAL);
                
            } else {
                // > value
                iTokens.previous();
                Object value = parseValue(iTokens);
                result = new BasicFieldValueConstraint(field, value)
                    .setOperator(FieldValueConstraint.GREATER);
                
            }
        } else if (token.equals("!")) {
            token = (String) iTokens.next();
            if (token.equals("=")) {
                // != value
                Object value = parseValue(iTokens);
                result = new BasicFieldValueConstraint(field, value)
                .setOperator(FieldValueConstraint.NOT_EQUAL);
                
            } else {
                throw new IllegalArgumentException(
                "Unexpected token (expected \"=\"): \""
                + token + "\"");
            }
        } else {
            throw new IllegalArgumentException(
                "Unexpected token: \"" + token + "\"");
        }
        
        if (inverse) {
            result.setInverse(!result.isInverse());
        }
        
        return result;
    }
    
    /**
     * Parses SQL search condition string from list of tokens, and produces a 
     * corresponding <code>BasicConstraint</code> object.
     * <p>
     * The parsed condition can be a simple or composite constraint.
     * 
     * See {@link #parseSimpleCondition()} for the format of a simple condition.
     *
     * @param iTokens Tokens iterator, must be positioned before the (first)
     *        token representing the condition.
     * @return The constraint.
     */
     public BasicConstraint parseCondition(ListIterator iTokens) {
        BasicCompositeConstraint composite = null;
        BasicConstraint constraint= null;
        while (iTokens.hasNext()) {
            boolean inverse = false;
            String token = (String) iTokens.next();
            if (token.equalsIgnoreCase("NOT")) {
                // NOT.
                inverse = true;
                token = (String) iTokens.next();
            }

            if (token.equals("(")) {
                // Start of (simple or composite) constraint 
                // between parenthesis.
                constraint = parseCondition(iTokens);
            } else {
                // Simple condition.
                iTokens.previous();
                constraint = parseSimpleCondition(iTokens);
            }
            if (inverse) {
                constraint.setInverse(!constraint.isInverse());
            }
            if (composite != null) {
                composite.addChild(constraint);
            }
            
            if (iTokens.hasNext()) {
                token = (String) iTokens.next();
                if (token.equals(")")) {
                    // Start of (simple or composite) constraint 
                    // between parenthesis.
                    break;
                }
                int logicalOperator = 0;
                if (token.equalsIgnoreCase("OR")) {
                    logicalOperator = CompositeConstraint.LOGICAL_OR;
                } else if (token.equalsIgnoreCase("AND")) {
                    logicalOperator = CompositeConstraint.LOGICAL_AND;
                } else {
                    throw new IllegalArgumentException(
                    "Unexpected token (expected \"AND\" or \"OR\"): \""
                    + token + "\"");
                }
                if (composite == null) {
                    composite = new BasicCompositeConstraint(logicalOperator).
                    addChild(constraint);
                }
                
                if (composite.getLogicalOperator() != logicalOperator) {
                    composite = new BasicCompositeConstraint(logicalOperator).
                    addChild(composite);
                }
                
                if (!iTokens.hasNext()) {
                    throw new IllegalArgumentException(
                    "Unexpected end of tokens after \"" + token + "\"");
                }
            }
        }
        if (composite != null) {
            return composite;
        } else {
            return constraint;
        }
    }
    
}
