/*
 * VtsQueryHandler.java
 *
 * Created on October 17, 2002, 4:46 PM
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
 * The Vts query handler adds support for Verity Text Search constraints.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
// TODO: (later) add javadoc, elaborate on overwritten methods.
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
    
    //    // javadoc is inherited
    //    public void appendConstraintToSql(StringBuffer sb, Constraint constraint,
    //    SearchQuery query, boolean inverse, boolean inComposite)
    //    throws SearchQueryException {
    //        if (constraint instanceof StringSearchConstraint) {
    //            // TODO: support maxNumber for query with vts constraint.
    //            // TODO: test if vts index is created for the tested field.
    //            // TODO: implement.
    //            sb.append("vts search not implemented yet!");
    //        } else {
    //            successor.appendConstraintToSql(sb, constraint, query,
    //            inverse, inComposite);
    //        }
    //    }
    
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
     */
    // TODO: provide a way to configure the location vtsindices.xml config file.
    private void init() throws IOException {
        XmlVtsIndicesReader configReader = new XmlVtsIndicesReader(
            new InputSource(
            new BufferedReader(
            new FileReader("C:/projects/konijn/testindices.xml"))));
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
        MMObjectBuilder builder = mmbase.getBuilder(builderName);

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
    
}