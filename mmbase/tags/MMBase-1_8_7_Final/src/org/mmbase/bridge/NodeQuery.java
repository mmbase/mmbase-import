/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.storage.search.*;

/**
 * A Node-Query is a query that queries node-lists, in contradiction to a normal Query which can
 * query 'cluster nodes' and even more generally 'result nodes' too.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */
public interface NodeQuery extends Query {


    /**
     * Returns the step for which the fields are added (or null)
     * @return step for which the fields are added
     */
    Step getNodeStep();

    /**
     * Removes all fields and add all fields of the given step. This also can have an effect on the
     * result of getNodeManager().
     * @param step add all fields of the this step
     * @return the previously associated step (if there was one, otherwise null).
     */
    Step setNodeStep(Step step);

    /**
     * Returns the node-manager. Or 'null' if this is not yet determined.
     * @return node-manager or 'null'
     */
    NodeManager getNodeManager();

    /**
     * Since in a NodeQuery one of the steps is 'exceptional', also this function makes sense now.
     * @param field field to get the StepField for.
     * @return null if field is not of 'the' nodemanager.
     */
    StepField getStepField(Field field);


}
