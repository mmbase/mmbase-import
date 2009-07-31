/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import org.mmbase.security.implementation.cloudcontext.User;
import org.mmbase.framework.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.cache.Cache;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.functions.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since  MMBase-1.7
 */
public class Actions extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(Actions.class);


    public final static String FIELD_COMPONENT      = "component";
    public final static String FIELD_ACTION         = "action";


    @Override
    public boolean init() {
        super.init();
        // make sure with all component actions nodes are associated
        for (Component component : ComponentRepository.getInstance().getComponents()) {
            for (Action action : component.getActions().values()) {
                try {
                    NodeSearchQuery query = new NodeSearchQuery(this);
                    query.setMaxNumber(1);
                    query.setConstraint(new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND, 
                                                                     new BasicFieldValueConstraint(query.getField(getField(FIELD_COMPONENT)), component.getName()),
                                                                     new BasicFieldValueConstraint(query.getField(getField(FIELD_ACTION)),    action.getName())
                                                                     ));
                    List<MMObjectNode> resultList = getNodes(query);
                    if (resultList.size() == 0) {
                        log.service("No node found for action " + action + " creating one now");
                        // create the node
                        MMObjectNode node = getNewNode("security");
                        node.setValue(FIELD_COMPONENT, component.getName());
                        node.setValue(FIELD_ACTION,    action.getName());
                        node.insert("security");
                    }
                } catch (SearchQueryException sqe) {
                    log.warn(sqe);
                }
            }
        }
        return true;
    }

    public static Actions getBuilder() {
        return (Actions) MMBase.getMMBase().getBuilder("mmbaseactions");
    }

    public boolean check(User user, Action ac, Parameters parameters) {
        return ac.getDefault().check(user, ac, parameters);
    }



}
