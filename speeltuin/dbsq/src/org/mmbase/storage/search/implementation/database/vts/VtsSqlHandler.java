/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database.vts;

import java.io.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.support.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.database.*;
import org.mmbase.util.logging.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * The Vts query handler adds support for Verity Text Search constraints,
 * when used with an Informix database and a Verity Text Search datablade.
 * This class is provided as a coding example of a ChainedSqlHandler.
 * <p>
 * On initialization, the handler reads a list of vts-indices from a 
 * configuration file.
 * The configurationfile must be named <em>vtsindices.xml</em> and located
 * inside the <em>databases</em> configuration directory. 
 * It's dtd is located in the directory 
 * <code>org.mmbase.storage.search.implementation.database.vts.resources</code>
 * in the MMBase source tree and 
 * <a href="http://www.mmbase.org/dtd/vtsindices.dtd">here</a> online. 
 *
 * @author Rob van Maris
 * @version $Id: VtsSqlHandler.java,v 1.5 2003-12-23 10:59:06 robmaris Exp $
 * @since MMBase-1.7
 */
// TODO RvM: (later) add javadoc, elaborate on overwritten methods.
public class VtsSqlHandler extends ChainedSqlHandler implements SqlHandler {
    
    /** Logger instance. */
    private static Logger log
    = Logging.getLoggerInstance(VtsSqlHandler.class.getName());
    
    /** 
     * The indexed fields, stored as {@link #BuilderField BuilderField}
     *  instances.
     */
    private Set indexedFields = new HashSet();
    
    /** Creates a new instance of VtsQueryHandler */
    public VtsSqlHandler(SqlHandler successor) throws IOException {
        super(successor);
        init();
    }
    
    // javadoc is inherited
    public void appendConstraintToSql(StringBuffer sb, Constraint constraint,
    SearchQuery query, boolean inverse, boolean inComposite)
    throws SearchQueryException {
        // Net effect of inverse setting with constraint inverse property.
        boolean overallInverse = inverse ^ constraint.isInverse();

        if (constraint instanceof StringSearchConstraint) {
            // TODO: test for support, else throw exception
            // TODO: support maxNumber for query with vts constraint.
            StringSearchConstraint stringSearchConstraint 
                = (StringSearchConstraint) constraint;
            StepField field = stringSearchConstraint.getField();
            Map parameters = stringSearchConstraint.getParameters();
            
            // TODO: how to implement inverse, 
            // it is actually more complicated than this:
            if (overallInverse) {
                sb.append("NOT ");
            }
            sb.append("vts_contains(").
            append(getAllowedValue(field.getStep().getAlias())).
            append(".").
            append(getAllowedValue(field.getFieldName())).
            append(", ROW('");

            switch (stringSearchConstraint.getSearchType()) {
                case StringSearchConstraint.SEARCH_TYPE_WORD_ORIENTED:
                    sb.append("<ACCRUE>(");
                    break;
                    
                case StringSearchConstraint.SEARCH_TYPE_PHRASE_ORIENTED:
                    sb.append("<MANY><PHRASE>(");
                    break;
                    
                case StringSearchConstraint.SEARCH_TYPE_PROXIMITY_ORIENTED:
                    Integer proximityLimit 
                        = (Integer) parameters.
                            get(StringSearchConstraint.PARAM_PROXIMITY_LIMIT);
                    if (proximityLimit == null) {
                        throw new IllegalStateException(
                        "Parameter PARAM_PROXIMITY_LIMIT not set " +
                        "while trying to perform proximity oriented search.");
                    }
                    sb.append("<NEAR/").append(proximityLimit).append(">(");
                    break;
                    
                default:
                    throw new IllegalStateException("valid searchtype must be selected");
            }
            
            int matchType = stringSearchConstraint.getMatchType();
            Float fuzziness = 
                (Float) parameters.get(StringSearchConstraint.PARAM_FUZZINESS);
            Iterator iSearchTerms 
                = stringSearchConstraint.getSearchTerms().iterator();
            while (iSearchTerms.hasNext()) {
                String searchTerm = (String) iSearchTerms.next();
                appendSearchTerm(sb, searchTerm, matchType, fuzziness);
                if (iSearchTerms.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append(")', 'CUTOFFSCORE=00'))");
            
        } else {
            getSuccessor().appendConstraintToSql(sb, constraint, query,
            inverse, inComposite);
        }
    }
    
    // javadoc is inherited
    public int getSupportLevel(int feature, SearchQuery query) throws SearchQueryException {
        int support;
        featureswitch:
            switch (feature) {
                case SearchQueryHandler.FEATURE_MAX_NUMBER:
                    // optimal with VTS index on field, and constraint is
                    // StringSearchConstraint, with no additonal constraints.
                    Constraint constraint = query.getConstraint();
                    if (constraint != null
                    && constraint instanceof StringSearchConstraint
                    && hasVtsIndex(((StringSearchConstraint) constraint).getField())
                    && !hasAdditionalConstraints(query)) {
                        support=SearchQueryHandler.SUPPORT_OPTIMAL;
                    } else {
                        support = getSuccessor().getSupportLevel(feature, query);
                    }
                    break;
                default:
                    support = getSuccessor().getSupportLevel(feature, query);
            }
            return support;
    }
    
    // javadoc is inherited
    public int getSupportLevel(Constraint constraint, SearchQuery query) throws SearchQueryException {
        int support;
        
        if (constraint instanceof StringSearchConstraint
        && hasVtsIndex(((StringSearchConstraint) constraint).getField())) {
            // StringSearchConstraint on field with VTS index:
            // - weak support if other stringsearch constraints are present
            // - optimal support if no other stringsearch constraints are present
            if (containsOtherStringSearchConstraints(query.getConstraint(),
            (StringSearchConstraint) constraint)) {
                support = SearchQueryHandler.SUPPORT_WEAK;
            } else {
                support = SearchQueryHandler.SUPPORT_OPTIMAL;
            }
        } else {
            support = getSuccessor().getSupportLevel(constraint, query);
        }
        return support;
    }
    
    /**
     * Tests if a Verity Text Search index has been made for this field.
     *
     * @param field the field.
     * @return true if a Verity Text Search index has been made for this field,
     *         false otherwise.
     */
    public boolean hasVtsIndex(StepField field) {
        boolean result = false;
        if (field.getType() == FieldDefs.TYPE_STRING
        || field.getType() == FieldDefs.TYPE_XML) {
            result = indexedFields.contains(
            field.getStep().getTableName() + "." + field.getFieldName());
        }
        return result;
    }
    
    /**
     * Tests if the query contains additional constraints on relation or nodes.
     *
     * @param query the query.
     * @return true if the query containts additional constraints,
     *         false otherwise.
     */
    protected boolean hasAdditionalConstraints(SearchQuery query) {
        Iterator iSteps = query.getSteps().iterator();
        while (iSteps.hasNext()) {
            Step step = (Step) iSteps.next();
            if (step instanceof RelationStep
            || step.getNodes().size() > 0) {
                // Additional constraints on relations or nodes.
                return true;
            }
        }
        // No additonal constraints:
        return false;
    }
    
    /**
     * Tests if a constaint is/contains another stringsearch constraint than
     * the specified one. Recursively seaches through all childs of composite
     * constraints.
     *
     * @param constraint the constraint.
     * @param searchConstraint the stringsearch constraint.
     * @param true if the constraint is/contains another stringsearch constraint
     *             than the given one, false otherwise.
     */
    protected boolean containsOtherStringSearchConstraints(
    Constraint constraint,
    StringSearchConstraint searchConstraint) {
        if (constraint instanceof CompositeConstraint) {
            // Composite constraint.
            Iterator iChildConstraints
            = ((CompositeConstraint) constraint).getChilds().iterator();
            while (iChildConstraints.hasNext()) {
                Constraint childConstraint 
                = (Constraint) iChildConstraints.next();
                if (containsOtherStringSearchConstraints(
                childConstraint, searchConstraint)) {
                    // Another stringsearch constraint found in childs.
                    return true;
                }
            }
            // No other stringsearch constraint found in childs.
            return false;
            
        } else if (constraint instanceof StringSearchConstraint
        && constraint != searchConstraint) {
            // Anther stringsearch constraint.
            return true;
            
        } else {
            // Not another stringsearch constraint and not a composite.
            return false;
        }
    }
    
    /** 
     * Initializes the handler by reading the vtsindices configuration file
     * to determine which fields have a vts index.
     * <p>
     * The configurationfile must be named <em>vtsindices.xml</em> and located
     * inside the <em>databases</em> configuration directory.
     * 
     * @throw IOException When a failure occurred while trying to read the 
     *        configuration file.
     */
    private void init() throws IOException {
        File vtsConfigFile = new File(
            MMBaseContext.getConfigPath() + "/databases/vtsindices.xml");
        XmlVtsIndicesReader configReader = 
            new XmlVtsIndicesReader(
                new InputSource(
                    new BufferedReader(
                        new FileReader(vtsConfigFile))));
        
        Enumeration eSbspaces = configReader.getSbspaceElements();
        while (eSbspaces.hasMoreElements()) {
            Element sbspace = (Element) eSbspaces.nextElement();
            Enumeration eVtsIndices = configReader.getVtsindexElements(sbspace);
            while (eVtsIndices.hasMoreElements()) {
                Element vtsIndex = (Element) eVtsIndices.nextElement();
                String table = configReader.getVtsindexTable(vtsIndex);
                String field = configReader.getVtsindexField(vtsIndex);
                String index = configReader.getVtsindexValue(vtsIndex);
                try {
                    String builderField = toBuilderField(table, field);
                    indexedFields.add(builderField);
                    log.service("Registered vts index \"" + index + 
                    "\" for builderfield " + builderField);
                } catch (IllegalArgumentException e) {
                    log.error("Failed to register vts index \"" + 
                    index + "\": " + e);
                }
            }
        }
    }
    
    /**
     * Finds builderfield corresponding to the database table and field names.
     *
     * @param dbTable The tablename used in the database.
     * @param dbField The fieldname used in the database.
     * @return The corresponding builderfield represented by a string of the 
     *         form &lt;buildername&gt;.&lt;fieldname&gt;.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    static String toBuilderField(String dbTable, String dbField) {
        MMBase mmbase = MMBase.getMMBase();
        MMJdbc2NodeInterface database = mmbase.getDatabase();
        String tablePrefix = mmbase.getBaseName() + "_";
        
        if (!dbTable.startsWith(tablePrefix)) {
            throw new IllegalArgumentException(
            "Invalid tablename: \"" + dbTable + "\". " +
            "It should start with the prefix \"" + tablePrefix + "\".");
        }
        
        String builderName = dbTable.substring(tablePrefix.length());
        MMObjectBuilder builder;
        try {
            builder = mmbase.getBuilder(builderName);
        } catch (BuilderConfigurationException e){
            // Unknown builder.
            builder = null;
        }
        
        if (builder == null) {
            throw new IllegalArgumentException(
            "Unknown builder: \"" + builderName + "\".");
        }
        
        Iterator iFieldNames = builder.getFieldNames().iterator();
        while (iFieldNames.hasNext()) {
            String fieldName = (String) iFieldNames.next();
            if (database.getAllowedField(fieldName).equals(dbField)) {
                return builderName + "." + fieldName;
            }
        }
        
        throw new IllegalArgumentException(
        "No field corresponding to database field \"" + dbField 
        + "\" found in builder \"" + builderName + "\".");
    }
    
    /**
     * Adds part to query representing one searchterm.
     *
     * @param sb The stringbuffer.
     * @param searchTerm The searchterm.
     * @param matchType The match type.
     * @param fuzziness The value of the fuzziness parameter supplied with 
     *        the string search constraint (may be null).
     */
    private void appendSearchTerm(StringBuffer sb, String searchTerm, 
        int matchType, Float fuzziness) {
            
        // Replace wildcards
        searchTerm = searchTerm.replace('%', '*');
        searchTerm = searchTerm.replace('_', '?');
        
        switch (matchType) {
            case StringSearchConstraint.MATCH_TYPE_FUZZY:
                if (fuzziness == null) {
                    throw new IllegalStateException(
                    "Parameter PARAM_FUZZINESS not set " +
                    "while trying to perform fuzzy matching.");
                }
                int margin = (int) (searchTerm.length()*fuzziness.floatValue());
                if (margin > 0) {
                    // typo, can not be combined with <MANY>
                    sb.append("<TYPO/" + margin + ">").append(searchTerm);
                } else if (searchTerm.indexOf('*') > -1 
                || searchTerm.indexOf('?') > -1) {
                    // wildcard
                    sb.append("<MANY><WILDCARD>").append(searchTerm);
                } else {
                    // margin too small, literal
                    sb.append("<MANY><WORD>").append(searchTerm);
                }
                break;
                
            case StringSearchConstraint.MATCH_TYPE_LITERAL:
                if (searchTerm.indexOf('*') > -1 
                || searchTerm.indexOf('?') > -1) {
                    // wildcard
                    sb.append("<MANY><WILDCARD>").append(searchTerm);
                } else {
                    sb.append("<MANY><WORD>").append(searchTerm);
                }
                break;
                
            case StringSearchConstraint.MATCH_TYPE_SYNONYM:
                // niet te combineren met <MANY>
                sb.append("<THESAURUS>").append(searchTerm);
                break;
                
            default:
                throw new IllegalStateException(
                "valid matchtype must be selected");
        }
    }
    
}