/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;
import org.mmbase.bridge.*;
import java.util.*;

/**
 * MockTransaction for the moment only does't give too much exceptions. It doesn't actually work, so {@link #cancel} still throws UnsupportedOperationException.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

public class MockTransaction extends MockCloud implements Transaction {

    private final NodeList nodes;
    private final MockCloud cloud;
    MockTransaction(String n, MockCloud cloud) {
        super(n, cloud.getCloudContext(), cloud.getUser());
        this.cloud = cloud;
        nodes = cloud.createNodeList();
    }

    @Override
    public boolean commit() {
        return true;
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean isCommitted() {
        return true;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public NodeList getNodes() {
        return nodes;
    }
    @Override
    public String getCloudName() {
        return cloud.getName();
    }


    @Override
    Node getNode(MockCloudContext.NodeDescription nd, boolean n) {
        Node node = super.getNode(nd, n);
        nodes.add(node);
        return node;
    }

    @Override
    public Cloud getNonTransactionalCloud() {
        Cloud result = cloud;
        while (result instanceof Transaction) {
            result = result.getNonTransactionalCloud();
        }
        return result;
    }


}

