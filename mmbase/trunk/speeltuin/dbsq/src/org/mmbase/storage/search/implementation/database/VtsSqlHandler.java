/*
 * VtsQueryHandler.java
 *
 * Created on October 17, 2002, 4:46 PM
 */

package org.mmbase.storage.search.implementation.database;

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.search.*;
import java.util.*;

/**
 * The Vts query handler adds support for Verity Text Search constraints.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
// TODO: (later) add javadoc, elaborate on overwritten methods.
public class VtsSqlHandler extends ChainedSqlHandler implements SqlHandler {
    
    /** Creates a new instance of VtsQueryHandler */
    public VtsSqlHandler(SqlHandler successor) {
        super(successor);
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
    protected boolean hasVtsIndex(StepField field) {
        // TODO: implement based on configuration file.
        return true; // n.i.y.!
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
}