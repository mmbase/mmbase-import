/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

/**
 * Implementation of {@link Query} completely based on other instance of that.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 */

public class NodeQueryWrapper extends AbstractQueryWrapper<NodeQuery> implements NodeQuery {
    public NodeQueryWrapper(NodeQuery q) {
        super(q);
    }


    public Step getNodeStep() {
        return query.getNodeStep();
    }

    public Step setNodeStep(Step step) {
        return query.setNodeStep(step);
    }

    public NodeManager getNodeManager() {
        return query.getNodeManager();
    }
    public StepField getStepField(Field field) {
        return query.getStepField(field);
    }

    public java.util.List<StepField> getExtraFields() {
        return query.getExtraFields();
    }


}
