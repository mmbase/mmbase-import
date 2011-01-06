/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import org.mmbase.bridge.*;

/**
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-2.0
 */

public class MockRelation extends MockNode implements Relation {

    MockRelation(MockCloudContext.NodeDescription nd, MockCloud cloud, boolean isNew) {
        super(nd, cloud, isNew);
    }

    @Override
    public RelationManager getRelationManager() {
        int role = getIntValue("rnumber");
        return new MockRelationManager(cloud, cloud.getCloudContext().getRole(role), "object", "object");
    }

    @Override
    public void setSource(Node n) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setDestination(Node n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getSource() {
        return getNodeValue("snumber");
    }
    @Override
    public Node getDestination() {
        return getNodeValue("dnumber");
    }
}