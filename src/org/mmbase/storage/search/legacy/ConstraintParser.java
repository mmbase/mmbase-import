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
 * Parser, tries to parse a <em>SQL-search-condition</em> for a query to a
 * {@link org.mmbase.storage.search.Constraint Constraint} object.
 * <p>
 * This class is provided for the sole purpose of alignment of old code with
 * the new {@link org.mmbase.storage.search.SearchQuery SearchQuery} framework,
 * and should not be called by new code.
 * <p>
 * A <em>SQL-search-condition</em> can be one of these forms:
 * <ul>
 * <li>[<b>NOT</b>] <b>(</b><em>SQL-search-condition</em><b>)</b>
 * <li>[<b>NOT</b>] <em>simple-SQL-search-condition</em>
 * <li><em>SQL-search-condition</em> <b>AND</b> <em>SQL-search-condition</em>
 * <li><em>SQL-search-condition</em> <b>OR</b> <em>SQL-search-condition</em>
 * </ul>
 * A <em>simple-SQL-search-condition</em> string can be of one of these forms:
 * <ul>
 * <li><em>field</em> [<b>NOT</b>] <b>LIKE</b> <em>value</em>
 * <li><b>UPPER(</b><em>field</em><b>)</b> [<b>NOT</b>] <b>LIKE</b> <em>value</em>
 * <li><b>LOWER(</b><em>field</em><b>)</b> [<b>NOT</b>] <b>LIKE</b> <em>value</em>
 * <li><em>field</em> <b>IS</b> [<b>NOT</b>] <b>NULL</b>
 * <li><em>field</em> [<b>NOT</b>] <b>IN
 *     (</b><em>value1</em><b>,</b> <em>value2</em><b>,</b> ..<b>)</b>
 * <li><em>field</em> [<b>NOT</b>] <b>BETWEEN</b> <em>value1</em> <b>AND</b> <em>value2</em>
 * <li><b>UPPER(</b><em>field</em><b>)</b> [<b>NOT</b>] <b>BETWEEN</b> <em>value1</em> <b>AND</b> <em>value2</em>
 * <li><b>LOWER(</b><em>field</em><b>)</b> [<b>NOT</b>] <b>BETWEEN</b> <em>value1</em> <b>AND</b> <em>value2</em>
 * <li><em>field</em> <b>=</b> <em>value</em>
 * <li><em>field</em> <b>=</b> <em>field2</em>
 * <li><b>UPPER(</b><em>field</em><b>) =</b> <em>value</em>
 * <li><b>LOWER(</b><em>field</em><b>) =</b> <em>value</em>
 * <li><em>field</em> <b>==</b> <em>value</em>
 * <li><em>field</em> <b>==</b> <em>field2</em>
 * <li><em>field</em> <b>&lt;=</b> <em>value</em>
 * <li><em>field</em> <b>&lt;=</b> <em>field2</em>
 * <li><em>field</em> <b>&lt;</b> <em>value</em>
 * <li><em>field</em> <b>&lt;</b> <em>field2</em>
 * <li><em>field</em> <b>&gt;=</b> <em>value</em>
 * <li><em>field</em> <b>&gt;=</b> <em>field2</em>
 * <li><em>field</em> <b>&gt;</b> <em>value</em>
 * <li><em>field</em> <b>&gt;</b> <em>field2</em>
 * <li><em>field</em> <b>&lt;&gt;</b> <em>value</em>
 * <li><em>field</em> <b>&lt;&gt;</b> <em>field2</em>
 * <li><em>field</em> <b>!=</b> <em>value</em>
 * <li><em>field</em> <b>!=</b> <em>field2</em>
 * <li><em>string-search-condition</em>
 * </ul>
 * A <em>field</em> can be one of these forms:
 * <ul>
 * <li><em>stepalias</em><b>.</b><em>fieldname</em>
 * <li><em>fieldname</em> (only when the query has just one step).
 * </ul>
 * A <em>string-search-condition</em> can be of this form:
 * <ul>
 * <li><b>StringSearch(</b><em>field</em><b>,</b>PHRASE|PROXIMITY|WORD<b>,</b>
 *  FUZZY|LITERAL|SYNONYM<b>,</b>
 *  <em>searchterms</em><b>,</b>
 *  <em>casesensitive</em><b>)</b>
 *  [<b>.set(FUZZINESS,</b><em>fuzziness</em><b>)</b>]
 *  [<b>.set(PROXIMITY_LIMIT,</b><em>proximity</em><b>)</b>]
 * </ul>
 * <em>searchterms</em> can be of one of these forms:
 * <ul>
 * <li><b>'</b>term1<b>'</b>
 * <li><b>"</b>term1<b>"</b>
 * <li><b>'</b>term1 term2<b>'</b>
 * <li><b>"</b>term1 term2<b>"</b>
 * <li> etc...
 * </ul>
 * <em>casesensitive</em> can be of one on these forms:
 * <ul>
 * <li><b>true</b>
 * <li><b>false</b>
 * </ul>
 * <em>fuzziness</em> must be a float value between 0.0 and 1.0,
 * <em>proximity</em> must be a int value &gt; 0<br />
 * <p>
 * See {@link org.mmbase.storage.search.StringSearchConstraint
 * StringSearchConstraint} for more info on string-search constraints.
 * <p>
 * A search condition that is not of one of these forms will be converted to a
 * {@link org.mmbase.storage.search.LegacyConstraint LegacyConstraint}, i.e.
 * in that case the search condition string will not be interpreted, but
 * instead be used "as-is".
 * Each time this occurs is logged with priority <code>service</code> to
 * category <code>org.mmbase.storage.search.legacyConstraintParser.fallback</code>.
 *
 * @author  Rob van Maris
 * @version $Id: ConstraintParser.java,v 1.17 2003-12-23 15:12:52 robmaris Exp $
 * @since MMBase-1.7
 */
public class ConstraintParser {

    /** Logger instance. */
    private final static Logger log = 
        Logging.getLoggerInstance(ConstraintParser.class);
    
    /** Logger instance dedicated to logging fallback to legacy constraint. */
    private final static Logger fallbackLog = 
        Logging.getLoggerInstance(ConstraintParser.class.getName() + ".fallback");

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
     * @throws NumberFormatException when the first token is not (the start of)
     *        a valid value expression (it may be a <em>field</em> instead).
     */
    // package visibility!
    static Object parseValue(Iterator iTokens) throws NumberFormatException {
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
     * A <em>field</em> can be one of these forms:
     * <ul>
     * <li><em>stepalias</em><b>.</b><em>fieldname</em>
     * <li>[<em>stepalias</em><b>.</b><em>fieldname</em>]
     * <li><em>fieldname</em> (only when just one step is specified).
     * <li>[<em>fieldname</em>] (only when just one step is specified).
     * </ul>
     *
     * @param token The token.
     * @param steps The steps.
     * @return The field.
     */
    public static StepField getField(String token, List steps) {
        BasicStep step = null;
        int bracketOffset = (token.startsWith("[") && token.endsWith("]")) ? 1 : 0;
        int idx = token.indexOf('.');
        if (idx == -1) {
            if (steps.size() > 1) {
                throw new IllegalArgumentException( "Fieldname not prefixed with table alias: \"" + token + "\"");
            }
            step = (BasicStep) steps.get(0);
        } else {
            step = getStep(token.substring(bracketOffset, idx), steps);
        }
        MMObjectBuilder builder = step.getBuilder();
        String  fieldName;
        if (idx == -1) {
            fieldName = token.substring(bracketOffset, token.length() - bracketOffset);
        } else {
            fieldName = token.substring(idx + 1, token.length() - bracketOffset);
        }

        FieldDefs fieldDefs = builder.getField(fieldName);
        if (fieldDefs == null) {
            throw new IllegalArgumentException(
            "Unknown field (of builder " + builder.getTableName()
            + "): \"" + fieldName + "\"");
        }
        BasicStepField field = new BasicStepField(step, fieldDefs);
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
            String alias2 = step.getAlias();
            if (alias2 == null) {
                alias2 = step.getTableName();
            }
            if (alias2.equals(alias)) {
                return step;
            }
        }

        // Not found.
        throw new IllegalArgumentException(
        "Unknown table alias: \"" + alias + "\"");
    }

    /** Creates a new instance of ConstraintParser */
    public ConstraintParser(SearchQuery query) {
        this.steps = query.getSteps();
    }

    /**
     * Parses <em>SQL-search-condition</em>, and produces a corresponding
     * {@link org.mmbase.storage.search.Constraint Constraint} object.
     * <p>
     * See {@link ConstraintParser above} for the format of a
     * <em>SQL-search-condition</em>.
     *
     * @param sqlConstraint The non-null SQL constraint string.
     * @return The constraint.
     */
    public Constraint toConstraint(String sqlConstraint) {
        Constraint result = null;
        try {
            ListIterator iTokens = tokenize(sqlConstraint).listIterator();
            result = parseCondition(iTokens);

        // If this doesn't work, fall back to legacy code.
        } catch (Exception e) {
            // Log to fallback logger.
            if (fallbackLog.isServiceEnabled()) {
                fallbackLog.service(
                    "Failed to parse Constraint from search condition string: "
                    + "\n     sqlConstraint = " + sqlConstraint 
                    + "\n     exception: " + e + Logging.stackTrace(e)
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
     * Parses a <em>field</em> string, and produces a corresponding
     * <code>StepField</code> object.
     * <p>
     * See {@link ConstraintParser above} for the format of a
     * <em>field</em>
     *
     * @param token The token.
     * @return The field.
     */
    // package visibility!
    StepField getField(String token) {
        return getField(token, steps);
    }

    /**
     * Parses <em>SQL-search-condition</em> string from list of tokens, and
     * produces a corresponding <code>BasicConstraint</code> object.
     * <p>
     * See {@link ConstraintParser above} for the format of a
     * <em>SQL-search-condition</em>
     *
     * @param iTokens Tokens iterator, must be positioned before the (first)
     *        token representing the condition.
     * @return The constraint.
     */
    // package visibility!
    BasicConstraint parseCondition(ListIterator iTokens) {
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

    /**
     * Parses a <em>simple-SQL-search-condition</em> string from list of tokens,
     * and produces a corresponding <code>BasicConstraint</code> object.
     * <p>
     * See {@link ConstraintParser above} for the format of a
     * <em>simple-SQL-search-condition</em>
     *
     * @param iTokens Tokens iterator, must be positioned before the (first)
     *        token representing the condition.
     * @return The constraint.
     */
    // package visibility!
    BasicConstraint parseSimpleCondition(ListIterator iTokens) {
        BasicConstraint result = null;

        String token = (String) iTokens.next();
        if (token.equalsIgnoreCase("StringSearch")) {
            // StringSearch constraint.
            return parseStringSearchCondition(iTokens);
        }

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
            // NOT LIKE/NOT IN/NOT BETWEEN
            inverse = true;
            token = (String) iTokens.next();
            if (!token.equalsIgnoreCase("LIKE")
                && !token.equalsIgnoreCase("IN")
                && !token.equalsIgnoreCase("BETWEEN")) {
                    throw new IllegalArgumentException(
                        "Unexpected token (expected "
                        + "\"LIKE\" OR \"IN\" OR \"BETWEEN\"): \""
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

        } else if (token.equalsIgnoreCase("BETWEEN")) {
            // BETWEEN value1 AND value2
            Object value1 = parseValue(iTokens);
            String separator = (String) iTokens.next();
            if (!separator.equals("AND")) {
                throw new IllegalArgumentException(
                    "Unexpected token (expected \"AND\"): \""
                    + separator + "\"");
            }
            Object value2 = parseValue(iTokens);
            boolean caseSensitive = true;
            if (function != null
                    && value1 instanceof String && value2 instanceof String) {
                String strValue1 = (String) value1;
                String strValue2 = (String) value2;
                if ((function.equals("LOWER")
                    && strValue1.equals(strValue1.toLowerCase())
                    && strValue2.equals(strValue2.toLowerCase()))
                || (function.equals("UPPER")
                    && strValue1.equals(strValue1.toUpperCase())
                    && strValue2.equals(strValue2.toUpperCase()))) {
                        caseSensitive = false;
                }
            }

            BasicFieldValueBetweenConstraint fieldValueBetweenConstraint
                = (BasicFieldValueBetweenConstraint)
                    new BasicFieldValueBetweenConstraint(field, value1, value2)
                        .setCaseSensitive(caseSensitive);
            result = fieldValueBetweenConstraint;

        } else if (token.equals("=")) {
            token = (String) iTokens.next();
            if (token.equals("=")) {
                try {
                    // == value
                    Object value = parseValue(iTokens);
                    result = new BasicFieldValueConstraint(field, value)
                        .setOperator(FieldValueConstraint.EQUAL);
                } catch (NumberFormatException e) {
                    // == field2
                    iTokens.previous();
                    token = (String) iTokens.next();
                    StepField field2 = getField(token);
                    result = new BasicCompareFieldsConstraint(field, field2)
                        .setOperator(FieldValueConstraint.EQUAL);
                }
            } else {
                iTokens.previous();
                try {
                    // = value
                    Object value = parseValue(iTokens);
                    boolean caseSensitive = true;
                    if (function != null && value instanceof String) {
                        String strValue = (String) value;
                        if ((function.equals("LOWER")
                            && strValue.equals(strValue.toLowerCase()))
                        || (function.equals("UPPER")
                            && strValue.equals(strValue.toUpperCase()))) {
                                caseSensitive = false;
                        }
                    }
                    result = new BasicFieldValueConstraint(field, value)
                        .setOperator(FieldValueConstraint.EQUAL)
                        .setCaseSensitive(caseSensitive);
                } catch (NumberFormatException e) {
                    // = field2
                    iTokens.previous();
                    token = (String) iTokens.next();
                    StepField field2 = getField(token);
                    result = new BasicCompareFieldsConstraint(field, field2)
                        .setOperator(FieldValueConstraint.EQUAL);
                }
            }
        } else if (token.equals("<")) {
            token = (String) iTokens.next();
            if (token.equals("=")) {
                try {
                    // <= value
                    Object value = parseValue(iTokens);
                    result = new BasicFieldValueConstraint(field, value)
                        .setOperator(FieldValueConstraint.LESS_EQUAL);
                } catch (NumberFormatException e) {
                    // <= field2
                    iTokens.previous();
                    token = (String) iTokens.next();
                    StepField field2 = getField(token);
                    result = new BasicCompareFieldsConstraint(field, field2)
                        .setOperator(FieldValueConstraint.LESS_EQUAL);
                }
            } else if (token.equals(">")) {
                try {
                    // <> value
                    Object value = parseValue(iTokens);
                    result = new BasicFieldValueConstraint(field, value)
                        .setOperator(FieldValueConstraint.NOT_EQUAL);
                } catch (NumberFormatException e) {
                    // <> field2
                    iTokens.previous();
                    token = (String) iTokens.next();
                    StepField field2 = getField(token);
                    result = new BasicCompareFieldsConstraint(field, field2)
                        .setOperator(FieldValueConstraint.NOT_EQUAL);
                }
            } else {
                try {
                    // < value
                    iTokens.previous();
                    Object value = parseValue(iTokens);
                    result = new BasicFieldValueConstraint(field, value)
                        .setOperator(FieldValueConstraint.LESS);
                } catch (NumberFormatException e) {
                    // < field2
                    iTokens.previous();
                    token = (String) iTokens.next();
                    StepField field2 = getField(token);
                    result = new BasicCompareFieldsConstraint(field, field2)
                        .setOperator(FieldValueConstraint.LESS);
                }
            }
        } else if (token.equals(">")) {
            token = (String) iTokens.next();
            if (token.equals("=")) {
                try {
                    // >= value
                    Object value = parseValue(iTokens);
                    result = new BasicFieldValueConstraint(field, value)
                        .setOperator(FieldValueConstraint.GREATER_EQUAL);
                } catch (NumberFormatException e) {
                    // >= field2
                    iTokens.previous();
                    token = (String) iTokens.next();
                    StepField field2 = getField(token);
                    result = new BasicCompareFieldsConstraint(field, field2)
                        .setOperator(FieldValueConstraint.GREATER_EQUAL);
                }
            } else {
                try {
                    // > value
                    iTokens.previous();
                    Object value = parseValue(iTokens);
                    result = new BasicFieldValueConstraint(field, value)
                        .setOperator(FieldValueConstraint.GREATER);
                } catch (NumberFormatException e) {
                    // > field2
                    iTokens.previous();
                    token = (String) iTokens.next();
                    StepField field2 = getField(token);
                    result = new BasicCompareFieldsConstraint(field, field2)
                        .setOperator(FieldValueConstraint.GREATER);
                }
            }
        } else if (token.equals("!")) {
            token = (String) iTokens.next();
            if (token.equals("=")) {
                try {
                    // != value
                    Object value = parseValue(iTokens);
                    result = new BasicFieldValueConstraint(field, value)
                        .setOperator(FieldValueConstraint.NOT_EQUAL);
                } catch (NumberFormatException e) {
                    // != field2
                    iTokens.previous();
                    token = (String) iTokens.next();
                    StepField field2 = getField(token);
                    result = new BasicCompareFieldsConstraint(field, field2)
                        .setOperator(FieldValueConstraint.NOT_EQUAL);
                }
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
     * Parses a <em>stringsearch-condition</em> string from list of tokens,
     * and produces a corresponding <code>BasicStringSearchConstraint</code> object.
     * <p>
     * See {@link ConstraintParser above} for the format of a
     * <em>stringsearch-condition</em>
     *
     * @param iTokens Tokens iterator, must be positioned after the (first)
     *        token representing the condition (e.g. after "StringSearch").
     * @return The constraint.
     */
    private BasicStringSearchConstraint parseStringSearchCondition(
            ListIterator iTokens) {

        BasicStringSearchConstraint result = null;

        String token = (String) iTokens.next();
        if (!token.equals("(")) {
            throw new IllegalArgumentException(
                "Unexpected token (expected \"(\"): \""
                + token + "\"");
        }

        // Field
        token = (String) iTokens.next();
        StepField field = getField(token);

        token = (String) iTokens.next();
        if (!token.equals(",")) {
            throw new IllegalArgumentException(
                "Unexpected token (expected \",\"): \""
                + token + "\"");
        }

        // Searchtype
        int searchType;
        token = (String) iTokens.next();
        if (token.equalsIgnoreCase("PHRASE")) {
            searchType = StringSearchConstraint.SEARCH_TYPE_PHRASE_ORIENTED;
        } else if (token.equalsIgnoreCase("PROXIMITY")) {
            searchType = StringSearchConstraint.SEARCH_TYPE_PROXIMITY_ORIENTED;
        } else if (token.equalsIgnoreCase("WORD")) {
            searchType = StringSearchConstraint.SEARCH_TYPE_WORD_ORIENTED;
        } else {
            throw new IllegalArgumentException(
                "Invalid searchtype (expected \"PHRASE\", \"PROXIMITY\" "
                + "or \"WORD\": \"" + token + "\"");
        }

        token = (String) iTokens.next();
        if (!token.equals(",")) {
            throw new IllegalArgumentException(
                "Unexpected token (expected \",\"): \""
                + token + "\"");
        }

        // Matchtype
        int matchType;
        token = (String) iTokens.next();
        if (token.equalsIgnoreCase("FUZZY")) {
            matchType = StringSearchConstraint.MATCH_TYPE_FUZZY;
        } else if (token.equalsIgnoreCase("LITERAL")) {
            matchType = StringSearchConstraint.MATCH_TYPE_LITERAL;
        } else if (token.equalsIgnoreCase("SYNONYM")) {
            matchType = StringSearchConstraint.MATCH_TYPE_SYNONYM;
        } else {
            throw new IllegalArgumentException(
                "Invalid matchtype (expected \"FUZZY\", \"LITERAL\" "
                + "or \"SYNONYM\": \"" + token + "\"");
        }

        token = (String) iTokens.next();
        if (!token.equals(",")) {
            throw new IllegalArgumentException(
                "Unexpected token (expected \",\"): \""
                + token + "\"");
        }

        // SearchTerms
        String searchTerms;
        token = (String) iTokens.next();
        if (!token.equals("'")) {
            throw new IllegalArgumentException(
                "Unexpected token (expected \"'\" or \"\"\"): \""
                + token + "\"");
        }
        searchTerms = (String) iTokens.next();
        token = (String) iTokens.next();
        if (!token.equals("'")) {
            throw new IllegalArgumentException(
            "Unexpected token (expected \"'\" or \"\"\"): \""
            + token + "\"");
        }

        token = (String) iTokens.next();
        if (!token.equals(",")) {
            throw new IllegalArgumentException(
                "Unexpected token (expected \",\"): \""
                + token + "\"");
        }

        // CaseSensitive property
        boolean caseSensitive;
        token = (String) iTokens.next();
        if (token.equalsIgnoreCase("true")) {
            caseSensitive = true;
        } else if (token.equalsIgnoreCase("false")) {
            caseSensitive = false;
        } else {
            throw new IllegalArgumentException(
                "Invalid caseSensitive value (expected \"true\" "
                + "or \"false\": \"" + token + "\"");
        }

        token = (String) iTokens.next();
        if (!token.equals(")")) {
            throw new IllegalArgumentException(
                "Unexpected token (expected \")\"): \""
                + token + "\"");
        }

        result = (BasicStringSearchConstraint)
            new BasicStringSearchConstraint(
                field, searchType, matchType, searchTerms)
                    .setCaseSensitive(caseSensitive);

        // .set(parametername, value)
        while (iTokens.hasNext()) {
            token = (String) iTokens.next();
            if (!token.equalsIgnoreCase(".set")) {
                iTokens.previous();
                break;
            }

            token = (String) iTokens.next();
            if (!token.equals("(")) {
                throw new IllegalArgumentException(
                    "Unexpected token (expected \"(\"): \""
                    + token + "\"");
            }

            String parameterName = (String) iTokens.next();

            token = (String) iTokens.next();
            if (!token.equals(",")) {
                throw new IllegalArgumentException(
                    "Unexpected token (expected \",\"): \""
                    + token + "\"");
            }

            String parameterValue = (String) iTokens.next();

            token = (String) iTokens.next();
            if (!token.equals(")")) {
                throw new IllegalArgumentException(
                    "Unexpected token (expected \")\"): \""
                    + token + "\"");
            }

            if (parameterName.equalsIgnoreCase("FUZZINESS")) {
                result.setParameter(StringSearchConstraint.PARAM_FUZZINESS,
                    new Float(parameterValue));
            } else if (parameterName.equalsIgnoreCase("PROXIMITY_LIMIT")) {
                result.setParameter(
                    StringSearchConstraint.PARAM_PROXIMITY_LIMIT,
                        new Integer(parameterValue));
            } else {
                throw new IllegalArgumentException(
                    "Invalid parameter name (expected \"FUZZINESS\" "
                    + "or \"PROXIMITY\": \"" + parameterName + "\"");
            }
        }

        return result;
    }
}
