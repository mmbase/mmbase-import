/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.storage.search.*;

/**
 * Representation of a (database query). 
 *
 * @author Michiel Meeuwissen
 * @version $Id: Query.java,v 1.1 2003-07-21 15:24:49 michiel Exp $
 * @todo Not sure this interface needed, perhaps everything should be done with a query-converter
 */
public interface Query extends SearchQuery {

    /**
     * Adds new step to this SearchQuery.
     *
     * @param builder The builder associated with the step.
     * @return The new step.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    Step addStep(NodeManager nodeManager);

    
    /**
     * Adds new relationstep to this SearchQuery.
     * This adds the next step as well, it can be retrieved by calling <code>
     * {@link org.mmbase.storage.search.RelationStep#getNext getNext()}
     * </code> on the relationstep, and cast to {@link BasicStep BasicStep}.
     *
     * @param builder The builder associated with the relation step.
     * @param nextBuilder The builder associated with the next step.
     * @return The new relationstep.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @throws IllegalStateException when there is no previous step.
     */
    RelationStep addRelationStep(RelationManager relationManager);

    StepField addField(Step step, Field field);


}
