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
    public String getForwardRole() {
        return role;
    }
    public String getReciprocalRole() {
        return role;
    }
    public String getForwardGUIName() {
        return role;
    }
    public String getReciprocalGUIName() {
        return role;
    }
    public int getDirectionality() {
        return RelationManager.BIDIRECTIONAL;
    }

    public NodeManager getSourceManager() {
        return cloud.getNodeManager(source);
    }

    public NodeManager getDestinationManager() {
        return cloud.getNodeManager(destination);
    }

    public Relation createRelation(Node sourceNode, Node destinationNode) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("snumber", sourceNode.getNumber());
        values.put("dnumber", destinationNode.getNumber());
        values.put("rnumber", vcloud.getCloudContext().roles.get(role).number);
        NodeManager nm = vcloud.getNodeManager(vcloud.getCloudContext().roles.get(role).nodeManager);
        return new MockRelation(values, vcloud, nm, true);
    }


    public RelationList getRelations(Node node) {
        return null;
    }

    public boolean mayCreateRelation(Node sourceNode, Node destinationNode) {
        return true;
    }



}
