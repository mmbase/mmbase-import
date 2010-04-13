/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import org.mmbase.bridge.Field;
import org.mmbase.core.CoreField;
import org.mmbase.storage.search.QueryContext;
import org.mmbase.storage.search.ClusterQueries;
import org.mmbase.storage.*;
/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: ClusterNode.java 34900 2009-05-01 16:29:42Z michiel $
 * @since MMBase-2.0
 */
public class CoreQueryContext implements QueryContext {

    public static final QueryContext INSTANCE = new CoreQueryContext();

    @Override
    public ClusterQueries getClusterQueries () {
        return CoreClusterQueries.INSTANCE;
    }

    @Override
    public  String getStorageIdentifier(String s) {
        StorageManagerFactory<?> factory = MMBase.getMMBase().getStorageManagerFactory();
        return (String) factory.getStorageIdentifier(s);
    }
    @Override
    public  Field getField(String builder, String fieldName) {
        return MMBase.getMMBase().getBuilder(builder).getField(fieldName);
    }


    @Override
    public Collection<CoreField> getFields(String buil) {
        return MMBase.getMMBase().getBuilder(buil).getFields(org.mmbase.bridge.NodeManager.ORDER_CREATE);
    }

}

