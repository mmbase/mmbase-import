/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.bridge.*;
import java.util.List;


/**
 * @author Michiel Meeuwissen
 * @version $Id: BasicQuery.java,v 1.1 2003-07-21 15:24:50 michiel Exp $
 */
public class BasicQuery implements Query {

    BasicSearchQuery query = new BasicSearchQuery();

    // SearchQuery impl:

    public List getSteps() {
        return query.getSteps();
    }
    public List getFields() {
        return query.getFields();
    }
    public Constraint getConstraint() {
        return query.getConstraint();
    }
    public int getMaxNumber() {
        return query.getMaxNumber();
    }
    public int getOffset() {
        return query.getOffset();
    }
    public List getSortOrders() {
        return query.getSortOrders();
    }
    public boolean isDistinct() {
        return query.isDistinct();
    }

    // bridge.Query impl.:

    public Step addStep(NodeManager nm) {
        BasicStep step = query.addStep(((BasicNodeManager)nm).builder);
        // Things go horribly wrong if a step does not have an alias. Make sure it has.
        // should this be in BasicStep itself?
        step.setAlias(nm.getName()); 
        return step;
    }

    public RelationStep addRelationStep(RelationManager rm) {
        InsRel insrel =   (InsRel)    BasicCloudContext.mmb.getBuilder("" + ((BasicRelationManager)rm).getBuilder());
        MMObjectBuilder otherBuilder = ((BasicNodeManager) rm.getDestinationManager()).builder;        
        return query.addRelationStep(insrel, otherBuilder);
    }

    
    public StepField addField(Step step, Field field) {
        return query.addField(step, ((BasicField) field).field);
    }



    

}
