/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database.functions;


import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.database.*;

/**
 * An SQL handle also recognizing 'FunctionValueConstraint's.
 *
 * @author Marcel Maatkamp
 * @version $Id$
 * @since MMBase-1.8.5
 */
// TODO RvM: (later) add javadoc, elaborate on overwritten methods.
public class FunctionSqlHandler extends ChainedSqlHandler implements SqlHandler {

    //public InformixSqlHandler(Map disallowedValues) {
    //    super(disallowedValues);
    //    mmbase = MMBase.getMMBase();
    //}
    
    public FunctionSqlHandler(SqlHandler successor) {
        super(successor);
        // log.fatal("MARCEL: FunctionSqlHandler: Instantiated");
       
    }
    
    // javadoc is inherited
    public void appendConstraintToSql(StringBuilder sb, Constraint constraint, SearchQuery query, boolean inverse, boolean inComposite) throws SearchQueryException {
        // Net effect of inverse setting with constraint inverse property.
        boolean overallInverse = inverse ^ constraint.isInverse();

        if (constraint instanceof FunctionValueConstraint) {
            FunctionValueConstraint functionValueConstraint = (FunctionValueConstraint)constraint;
        	
            //StringSearchConstraint stringSearchConstraint 
             //   = (StringSearchConstraint) constraint;
            StepField field = functionValueConstraint.getField();
            // Map parameters = fieldValueConstraint.getParameters();
            
            if (overallInverse) {
                sb.append("NOT ");
            }
            
            // log.fatal("sb("+sb.toString()+"), constraint("+constraint+"), query("+query+"), inverse("+inverse+"), inComposite("+inComposite+"), overallInverse("+overallInverse+")");
            // log.fatal("functionValueConstraint.getFunction("+functionValueConstraint.getFunction()+"), field.getStep().getAlias("+field.getStep().getAlias()+"), field.getStep().getAlias("+field.getStep().getAlias()+"), field.getFieldName("+field.getFieldName()+"), functionValueConstraint.getValue("+functionValueConstraint.getValue()+")");
            
            sb.append(functionValueConstraint.getFunction()).append('(');
            
            // append 'users.email' or just 'email'?
            if(field.getStep().getAlias()!=null)
            	sb.append(getAllowedValue(field.getStep().getAlias())).append('.');
            
            sb.append(getAllowedValue(field.getFieldName())).
            append(')').
            append("='").
            append(functionValueConstraint.getValue()).
            append('\'');
        } else {
            getSuccessor().appendConstraintToSql(sb, constraint, query,
            inverse, inComposite);
        }
    }
    

    
    
}
