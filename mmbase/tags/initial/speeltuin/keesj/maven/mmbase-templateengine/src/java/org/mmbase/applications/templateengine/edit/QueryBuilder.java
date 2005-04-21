/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine.edit;

import org.mmbase.bridge.*;

import java.util.Hashtable;
/**
 * @author keesj
 * @version $Id: QueryBuilder.java,v 1.1.1.1 2004-04-02 14:58:47 keesj Exp $
 */
public class QueryBuilder {
    Node startNode;

    public QueryBuilder(Node node) {
    	this.startNode  = node;
    };

    public RelationManagerList getAllowedRelations() {
        RelationManagerList relationManagerList = startNode.getNodeManager().getAllowedRelations();
        for (int x = 0; x < relationManagerList.size(); x++) {
            RelationManager relman = relationManagerList.getRelationManager(x);
            try {
                relman.getSourceManager();
                relman.getDestinationManager();
                relman.getNodeManager();
            } catch (BridgeException e) {
                relationManagerList.remove(x);
                x--;
                //RelationManager.getDestinationManagber throws an exception
                //if the builder is inactive...
            }
        }
        return relationManagerList;
    }

    public RelationManagerList getUsedRelations() {
        RelationList list = startNode.getRelations();
        RelationManagerList retval = startNode.getCloud().getCloudContext().createRelationManagerList();
        Hashtable hash = new Hashtable();
        for (int x = 0; x < list.size(); x++) {
            Relation rel = list.getRelation(x);
            hash.put("" + rel.getRelationManager().getNumber(), rel.getRelationManager());
        }
		retval.addAll(hash.values());
        return retval;
    }

}
