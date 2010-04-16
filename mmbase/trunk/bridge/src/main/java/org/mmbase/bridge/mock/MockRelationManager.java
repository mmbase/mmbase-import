/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import java.util.*;
import org.mmbase.bridge.*;

/**
 * Straight-forward implementation of NodeManager based on a Map with DataType's.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: MockNodeManager.java 40297 2009-12-26 15:50:20Z michiel $
 * @since   MMBase-2.0
 */

public class MockRelationManager extends MockNodeManager implements RelationManager{

    protected final String role;
    protected final String source;
    protected final String destination;

    public MockRelationManager(MockCloud cloud, String r, String source, String destination) {
        super(cloud, cloud.getCloudContext().nodeManagers.get(cloud.getCloudContext().getNodeManagerForRole(r)));
        this.role = r;
        this.source = source;
        this.destination = destination;
    }
    @Override
    public String getForwardRole() {
        return role;
    }
    @Override
    public String getReciprocalRole() {
        return role;
    }
    @Override
    public String getForwardGUIName() {
        return role;
    }
    @Override
    public String getReciprocalGUIName() {
        return role;
    }
    @Override
    public int getDirectionality() {
        return RelationManager.BIDIRECTIONAL;
    }

    @Override
    public NodeManager getSourceManager() {
        return cloud.getNodeManager(source);
    }

    @Override
    public NodeManager getDestinationManager() {
        return cloud.getNodeManager(destination);
    }

    @Override
    public Relation createRelation(Node sourceNode, Node destinationNode) {
        Map<String, Object> v = new HashMap<String, Object>();
        v.put("snumber", sourceNode.getNumber());
        v.put("dnumber", destinationNode.getNumber());
        v.put("rnumber", vcloud.getCloudContext().roles.get(role).number);
        NodeManager nm = vcloud.getNodeManager(vcloud.getCloudContext().roles.get(role).nodeManager);
        return new MockRelation(v, vcloud, nm, true);
    }


    @Override
    public RelationList getRelations(Node node) {
        return null;
    }

    @Override
    public boolean mayCreateRelation(Node sourceNode, Node destinationNode) {
        return true;
    }



}
