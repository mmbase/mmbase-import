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
package org.mmbase.remotepublishing.builders;

import org.mmbase.core.event.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.remotepublishing.CloudManager;
import org.mmbase.remotepublishing.action.PublishingAction;
import org.mmbase.remotepublishing.action.PublishingActionDummy;
import org.mmbase.remotepublishing.util.PublishUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This builder is used when a content node should be published when the node is saved. A publish
 * property specifies if the builder should publish nodes in the current cloud.
 * 
 * On a save the builder checks if there is a publishnow field to let the node decide that it should
 * be published. The navigation node uses this mechanisme to preview it in the staging cloud.
 * 
 * Another unused feature at the moment is in plac to perform an action when a node is published to
 * the current cloud. This could be handy when something has to be done in the live site when the
 * node is published
 * 
 * @author keesj
 * @author Nico Klasens (Finalist IT Group)
 * @version PublishingBuilder.java,v 1.2 2003/07/28 09:43:51 nico Exp
 */
public class PublishingBuilder extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(PublishingBuilder.class.getName());

    private boolean publish = false;
    private boolean publishRelations = false;

    /** PublishingAction class to use when a node is published to this cloud */
    private String publishActionClassName = null;

    /**
     * test so see if mmbase doens't mix up builders and java classes
     */
    private String myRealName;

    @Override
    public boolean init() {
        String publishStr = getInitParameter("publish");
        if (publishStr == null || "".equals(publishStr.trim()) 
                || "false".equalsIgnoreCase(publishStr)) {
            log.info(getTableName() + " is inactive");
        }
        else {
            if ("true".equalsIgnoreCase(publishStr)) {
                publish = true;
                log.info(getTableName() + " is active");
            }
            else {
                String defaultCloud = CloudManager.getDefaultCloudName();
                if (publishStr.equals(defaultCloud)) {
                    publish = true;
                    log.info(getTableName() + " is active for local mmbase instance");
                }
            }
        }

        if ("true".equalsIgnoreCase(getInitParameter("publish-relations"))) {
            publishRelations = true;
            log.info(getTableName() + " monitors relations");
        }

        String classname = getInitParameter("publishaction");
        if (classname != null && "".equals(classname.trim())) {
            publishActionClassName = classname;
        }

        myRealName = getTableName();

        return super.init();
    }

    @Override
    public int insert(String owner, MMObjectNode node) {
        int number = super.insert(owner, node);

        if (number != -1) {
            if (publish) {
                PublishUtil.publishOrUpdateNode(number);
            }
            else {
                PublishingAction pa = getPublishingAction();
                pa.inserted(number);
            }
        }
        return number;
    }

    @Override
    public boolean commit(MMObjectNode objectNode) {
        if (!myRealName.equals(getTableName())) {
            log.error("object builder classes and object mixed the builder class "
            		  + "was created for objects of type("
                      + myRealName + ") but is used for (" + getTableName() + ")");
        }

        log.info(getTableName() + " commit");

        boolean retval = super.commit(objectNode);

        if (publish) {
            boolean publishnow = true;

            if (getField("publishnow") != null) {
                publishnow = objectNode.getBooleanValue("publishnow");
            }
            // no publishnow field or publishnow field was true.
            if (publishnow) {
                PublishUtil.publishOrUpdateNode(objectNode);
            }
        }
        else {
            PublishingAction pa = getPublishingAction();
            pa.committed(objectNode);
        }

        return retval;
    }

    @Override
    public void removeNode(MMObjectNode objectNode) {
        log.info(getTableName() + " remove");

        if (publish) {
            PublishUtil.removeNode(objectNode);
        }
        else {
            PublishingAction pa = getPublishingAction();
            pa.removed(objectNode);
        }

        super.removeNode(objectNode);
    }

    /**
     * Get a PublishingAction instance
     * 
     * @return PublishingAction instance
     */
    private PublishingAction getPublishingAction() {
        if (publishActionClassName != null) {
            try {
                Class<?> publishActionClass = Class.forName(publishActionClassName);
                PublishingAction pa = (PublishingAction) publishActionClass.newInstance();
                return pa;
            }
            catch (ClassNotFoundException e) {
                log.warn("PublishingAction class not found: " + publishActionClassName);
            }
            catch (InstantiationException e) {
                log.warn("Unable to instantiate: " + publishActionClassName);
            }
            catch (IllegalAccessException e) {
                log.warn("Not allowed to load: " + publishActionClassName);
            }
        }

        return new PublishingActionDummy();
    }

    @Override
    public void notify(NodeEvent event) {
        super.notify(event);
        if (publish && publishRelations) {
            if (event.getType() == NodeEvent.TYPE_RELATION_CHANGE) {
                PublishUtil.publishOrUpdateRelations(event.getNodeNumber(), null);
            }
        }
    }

    @Override
    public void notify(RelationEvent event) {
        super.notify(event);
        if (publish && publishRelations) {
            if ("object".equals(event.getRelationSourceType())
                    || "object".equals(event.getRelationDestinationType())) {
                // ignore events for object
                return;
            }
            if (event.getRelationSourceType().equals(getTableName())
                    || event.getRelationDestinationType().equals(getTableName())) {
                switch (event.getType()) {
                    case Event.TYPE_CHANGE:
                        if (!event.getNodeEvent().getChangedFields().isEmpty()) {
                            PublishUtil.publishOrUpdateNode(event.getNodeEvent().getNodeNumber());
                        }
                        break;
                    case Event.TYPE_NEW:
                    case NodeEvent.TYPE_RELATION_CHANGE:
                        PublishUtil.publishOrUpdateNode(event.getNodeEvent().getNodeNumber());
                        break;
                    case Event.TYPE_DELETE:
                        PublishUtil.removeNode(event.getNodeEvent().getNodeNumber());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    protected boolean getPublish() {
        return publish;
    }

    protected void setPublish(boolean publish) {
        this.publish = publish;
    }
}
