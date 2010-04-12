
package org.mmbase.module.core;



public class NodeSearchQuery extends org.mmbase.storage.search.implementation.NodeSearchQuery {


    public NodeSearchQuery(MMObjectBuilder buil) {
        super(buil.getTableName(), CoreClusterQueries.INSTANCE);
    }

    public MMObjectBuilder getBuilder() {
        return MMBase.getMMBase().getBuilder(getTableName());
    }
}
