/*
 * VtsQueryHandler.java
 *
 * Created on October 17, 2002, 4:46 PM
 */

package org.mmbase.module.database.search.implementation;

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.database.search.*;
import java.util.List;

/**
 * The Vts query handler adds support for Verity Text Search constraints.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
// TODO: add javadoc, elaborate on overwritten methods.
public class VtsQueryHandler extends ChainedSqlHandler implements SqlHandler {
    
    /** Creates a new instance of VtsQueryHandler */
    public VtsQueryHandler(SqlHandler successor) {
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
//    
//    // javadoc is inherited
//    public int getSupportLevel(int feature, SearchQuery query) throws SearchQueryException {
//        // TODO: support maxNumber for query with vts constraint.
//        return successor.getSupportLevel(feature, query);
//    }
//    
//    // javadoc is inherited
//    public int getSupportLevel(Constraint constraint, SearchQuery query) throws SearchQueryException {
//        int support;
//        if (constraint instanceof StringSearchConstraint) {
//            // TODO: test if vts index is created for the tested field.
//            // TODO: test if more than one constraint of this type.
//            support = SearchQueryHandler.SUPPORT_OPTIMAL;
//        } else {
//            support = successor.getSupportLevel(constraint, query);
//        }
//        return support;
//    }
    
}
