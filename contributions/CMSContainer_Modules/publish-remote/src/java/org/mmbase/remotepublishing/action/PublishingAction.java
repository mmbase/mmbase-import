/*
 * MMBase Remote Publishing
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package org.mmbase.remotepublishing.action;

import org.mmbase.module.core.MMObjectNode;

/**
 * This defines an action which should be performed when a node
 * from the publisingbuilder type is published
 *
 * @author Nico Klasens (Finalist IT Group)
 * @version $Revision: 1.1 $
 */
public interface PublishingAction {

    /**
     * Node inserted
     *
     * @param nodenumber number of node
     */
    void inserted(int nodenumber);

    /**
     * Node changed / committed
     * 
     * @param node  changed node
     */
    void committed(MMObjectNode node);

    /**
     * Node removed
     * 
     * @param node removed node
     */
    void removed(MMObjectNode node);

}
