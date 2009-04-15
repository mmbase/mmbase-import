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

import java.rmi.ConnectException;
import java.rmi.NoSuchObjectException;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.cache.CachePolicy;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.remotepublishing.CloudInfo;
import org.mmbase.remotepublishing.PublishManager;
import org.mmbase.remotepublishing.PublishListener;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Main class that publishes or removes nodes to or from remote clouds.
 */
public class PublishingQueueBuilder extends MMObjectBuilder implements Runnable {

    public static final String FIELD_TIMESTAMP = "timestamp";

    public static final String FIELD_PUBLISHDATE = "publishdate";

    public static final String FIELD_STATUS = "status";

    public static final String FIELD_ACTION = "action";

    public static final String FIELD_DESTINATIONCLOUD = "destinationcloud";

    public static final String FIELD_SOURCENUMBER = "sourcenumber";

    public static final String FIELD_RELATEDNODES = "relatednodes";

    public static final String ACTION_UPDATE = "update";

    public static final String ACTION_UPDATE_NODE = "update-node";

    public static final String ACTION_UPDATE_RELATIONS = "update-relations";

    public static final String ACTION_REMOVE = "remove";

    public static final String STATUS_DONE = "done";

    public static final String STATUS_FAIL = "fail";

    private static Logger log = Logging.getLoggerInstance(PublishingQueueBuilder.class.getName());

    /** thread performing the task */
    private static Thread thread = null;

    private static int maxtrying = 5;

    int remoteCloudNumber = -1;

    /** List of cloud names where to publish to */
    String remoteCloudName;

    /** Milliseconds how long the thread will sleep */
    private int interval = 60 * 1000;

    private static List<PublishListener> publishListeners = new ArrayList<PublishListener>();

    /**
     * MMBase builder init method. This method first looks for the cloudlist property in the builder
     * xml starts a new publishing Tread.
     */
    @Override
    public boolean init() {
        Map<String, String> params = getInitParameters("mmbase/remotepublishing");

        remoteCloudName = params.get("remotecloud");
        if ((remoteCloudName != null) && (!"".equals(remoteCloudName))) {
            log.info("remote cloud will be " + remoteCloudName);
        }
        else {
            remoteCloudName = null;
            log.warn("cloudlist parameter missing publishing disabled");
        }

        // Initialize the module.
        String intervalStr = params.get("interval");
        if (intervalStr == null) { throw new IllegalArgumentException("interval"); }
        interval = Integer.parseInt(intervalStr) * 1000;

        if (thread == null) {
            thread = new Thread(this, "PublishQueue to " + remoteCloudName);
            thread.setDaemon(true);
            thread.start();
        }
        else {
            log.warn("init() method of the PublishingQueueBuilder was called multiple times");
        }

        return super.init();
    }

    @Override
    public boolean commit(MMObjectNode objectNodenode) {
        boolean retval = super.commit(objectNodenode);
        return retval;
    }

    @Override
    public void setDefaults(MMObjectNode node) {
        super.setDefaults(node);
        if ((remoteCloudNumber == -1) && (remoteCloudName != null)) {
            remoteCloudNumber = getCloudNumber(remoteCloudName);
        }

        node.setValue(FIELD_DESTINATIONCLOUD, remoteCloudNumber);
    }

    /**
     * Get number of cloud from local system
     * 
     * @param name
     *            the name of the cloud ( in the cloud list)
     * @return cloud node number
     * @throws BridgeException
     *             if the cloud was not found
     */
    public static int getCloudNumber(String name) throws BridgeException {
        MMObjectBuilder builder = MMBase.getMMBase().getBuilder("cloud");
        NodeSearchQuery query = new NodeSearchQuery(builder);
        StepField nameStepField = query.getField(builder.getField("name"));
        BasicFieldValueConstraint cName = new BasicFieldValueConstraint(nameStepField, name);
        cName.setOperator(FieldCompareConstraint.EQUAL);
        query.setConstraint(cName);

        try {
            List<MMObjectNode> nodes = builder.getNodes(query);
            if (nodes.isEmpty()) { throw new BridgeException("can not find cloud with name(" + name
                    + ") in nameServerCloud"); }
            return nodes.get(0).getNumber();

        }
        catch (SearchQueryException e) {
            throw new BridgeException("can not find cloud with name(" + name
                    + ") in nameServerCloud", e);
        }
    }

    public void run() {
        log.info("Publishing Queue initialised");

        // Wait for mmbase to be up and running.
        MMBase.getMMBase();

        try {
            Thread.sleep(interval);
        }
        catch (InterruptedException e) {
            log.warn("Interupted while sleeping , continuning");
        }

        try {
            linkTypedefs();
        }
        catch (Exception e) {
            log.error("Problem with linking typedefs together", e);
        }

        while (true) {
            try {
                Thread.sleep(interval);
            }
            catch (InterruptedException e) {
                log.warn("Interupted while sleeping , continuning");
            }

            if (remoteCloudName == null) {
                log.warn("publising disabled");
            }
            else {
                CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
                NodeManager nodeManager = localCloudInfo.getCloud().getNodeManager("publishqueue");
                NodeQuery query = createQuery(nodeManager);
                NodeList list = null;
                while (list == null || !list.isEmpty()) {

                    list = nodeManager.getList(query);

                    for (int x = 0; x < list.size(); x++) {
                        Node queueNode = list.getNode(x);
                        if (queueNode != null) {
                            try {
                                // check if node is not removed from the cloud in between query and
                                // retrieval
                                if (localCloudInfo.getCloud().hasNode(queueNode.getNumber())) {
                                    String action = queueNode.getStringValue(FIELD_ACTION);
                                    if (isUpdateAction(action)) {
                                        try {
                                            boolean finished = false;
                                            int trytimes = 0;
                                            while (!finished) {
                                                try {
                                                    update(localCloudInfo, queueNode, action);
                                                    finished = true;
                                                }
                                                catch (BridgeException e) {
                                                    if (handleRmiException(e)
                                                            && ++trytimes <= maxtrying) {
                                                        // if it was caused rmi connection exception
                                                        // and still within
                                                        // the maximal tring times limitation,
                                                        // continue to try publishing it again
                                                    }
                                                    else {
                                                        // otherwise, throw the runtime exception
                                                        throw e;
                                                    }
                                                }
                                            }
                                            queueNode.setStringValue(FIELD_STATUS, STATUS_DONE);
                                            queueNode.commit();
                                        }
                                        catch (BridgeException e) {
                                            log.error("Nodenumber : " + queueNode.getNumber()
                                                    + ", " + e, e);
                                            publishFailed(localCloudInfo, queueNode, e);
                                        }
                                    }
                                    else {
                                        if (isRemoveAction(action)) {
                                            try {
                                                removeNode(localCloudInfo, queueNode);
                                            }
                                            catch (BridgeException e) {
                                                log.error("Removing published node ("
                                                        + queueNode.getNumber() + ") failed", e);
                                                publishFailed(localCloudInfo, queueNode, e);
                                            }
                                        }
                                    }
                                }
                            }
                            catch (Throwable e) {
                                log.error("Throwable error with nodenumber: "
                                        + queueNode.getNumber() + ", " + e.getMessage());
                                log.debug(Logging.stackTrace(e));
                            }
                        }
                    } // end for
                    log.debug("Published total " + list.size() + " nodes");
                }
            }
        }
    }

    private boolean isRemoveAction(String action) {
        return action.equalsIgnoreCase(ACTION_REMOVE);
    }

    private boolean isUpdateAction(String action) {
        return action.equalsIgnoreCase(ACTION_UPDATE)
                || action.equalsIgnoreCase(ACTION_UPDATE_NODE)
                || action.equalsIgnoreCase(ACTION_UPDATE_RELATIONS);
    }

    private NodeQuery createQuery(NodeManager nodeManager) {
        NodeQuery query = nodeManager.createQuery();

        StepField statusField = query.getStepField(nodeManager.getField(FIELD_STATUS));
        FieldValueConstraint failStatus = query.createConstraint(statusField,
                FieldCompareConstraint.NOT_EQUAL, STATUS_FAIL);
        query.setCaseSensitive(failStatus, true);
        FieldValueConstraint doneStatus = query.createConstraint(statusField,
                FieldCompareConstraint.NOT_EQUAL, STATUS_DONE);
        query.setCaseSensitive(doneStatus, true);
        Constraint statusComposite = query.createConstraint(failStatus,
                CompositeConstraint.LOGICAL_AND, doneStatus);

        if (!nodeManager.hasField(FIELD_PUBLISHDATE)) {
            query.setConstraint(statusComposite);
        }
        else {
            StepField publishDateField = query
                    .getStepField(nodeManager.getField(FIELD_PUBLISHDATE));
            Constraint publishNull = query.createConstraint(publishDateField);
            Constraint publishNow = query.createConstraint(publishDateField,
                    FieldCompareConstraint.LESS_EQUAL, new Date());

            Constraint publishDateComposite = query.createConstraint(publishNow,
                    CompositeConstraint.LOGICAL_OR, publishNull);
            Constraint composite = query.createConstraint(statusComposite,
                    CompositeConstraint.LOGICAL_AND, publishDateComposite);
            query.setConstraint(composite);
        }
        query.setMaxNumber(10);
        StepField timestampField = query.getStepField(nodeManager.getField(FIELD_TIMESTAMP));
        query.addSortOrder(timestampField, SortOrder.ORDER_ASCENDING);

        query.setCachePolicy(CachePolicy.NEVER);
        return query;
    }

    /**
     * handle exception, if the root cause is RMI's connection broken, try to set cached cloud
     * informaiton invalid.
     * 
     * @param e
     *            the exception which we should handled.
     * @return true, if the exception is caused by invalid RMI connect; false, otherwise.
     */
    private boolean handleRmiException(BridgeException e) {
        Throwable rootCause = e;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
            if (rootCause.getClass().getPackage().getName().startsWith("java.rmi")) {
                break;
            }
        }

        if (rootCause instanceof ConnectException) {
            // if root cause is rmi connection exception
            String message = e.getCause().getMessage();
            log.debug("rmi connection exception:" + message);
            int beginIndex = message.indexOf("rmi://");
            if (beginIndex >= 0) {
                // if we know exactlly what cloud is died, we just reset this one
                int endIndex = message.indexOf(" ", beginIndex);
                String url = message.substring(beginIndex, endIndex);
                CloudInfo.setCloudInvalid(url);
                return true;
            }
            else {
                // if we aren't sure which cloud is died, reset all cached remote cloud
                CloudInfo.setRemoteCloudsInvalid();
                return true;
            }

        }
        else
            if (rootCause instanceof NoSuchObjectException) {
                CloudInfo.setRemoteCloudsInvalid();
                return true;
            }
        return false;
    }

    private void publishFailed(CloudInfo localCloudInfo, Node node, BridgeException e) {
        node.setStringValue(FIELD_STATUS, STATUS_FAIL);
        node.commit();
        for (PublishListener listener : publishListeners) {
            int number = node.getIntValue(FIELD_SOURCENUMBER);
            if (localCloudInfo.getCloud().hasNode(number)) {
                StringBuffer message = new StringBuffer();
                Throwable t = e;
                while (t != null) {
                    message.append(t.getMessage());
                    t = t.getCause();
                    if (t != null) {
                        message.append(" | ");
                    }
                }

                listener.publishedFailed(localCloudInfo.getCloud().getNode(number), message
                        .toString());
            }
        }
    }

    private void update(CloudInfo localCloudInfo, Node queueNode, String action) {
        int localNodeNumber = queueNode.getIntValue(FIELD_SOURCENUMBER);
        int remoteCloudNumber = queueNode.getIntValue(FIELD_DESTINATIONCLOUD);
        CloudInfo remoteCloudInfo = CloudInfo.getCloudInfo(remoteCloudNumber);
        Node localNode = localCloudInfo.getCloud().getNode(localNodeNumber);
        String nodeManagerName = localNode.getNodeManager().getName();

        try {
            if (!PublishManager.isImported(localCloudInfo, localNode)) {

                if (PublishManager.isPublished(localCloudInfo, localNode)) {
                    if (localNode instanceof Relation) {
                        log.debug(nodeManagerName + " update relation with number "
                                + localNodeNumber);
                    }
                    else {
                        log.debug(nodeManagerName + " update node with number " + localNodeNumber);
                    }
                    if (action.equalsIgnoreCase(ACTION_UPDATE_NODE)) {
                        PublishManager.updateNodesAndRelations(localCloudInfo, localNode, true,
                                false);
                    }
                    if (action.equalsIgnoreCase(ACTION_UPDATE)) {
                        PublishManager.updateNodesAndRelations(localCloudInfo, localNode, true,
                                true);
                    }
                    if (action.equalsIgnoreCase(ACTION_UPDATE_RELATIONS)) {
                        String relatedNodes = queueNode.getStringValue(FIELD_RELATEDNODES);
                        if (relatedNodes != null && relatedNodes.length() > 0) {
                            List<Integer> related = new ArrayList<Integer>();
                            StringTokenizer tokenizer = new StringTokenizer(relatedNodes, ",");
                            while (tokenizer.hasMoreTokens()) {
                                int relatedNodeNumber = Integer.valueOf(tokenizer.nextToken());
                                related.add(relatedNodeNumber);
                            }
                            PublishManager.updateNodesAndRelations(localCloudInfo, localNode,
                                    false, true, related);
                        }
                        else {
                            PublishManager.updateNodesAndRelations(localCloudInfo, localNode,
                                    false, true);
                        }
                    }
                }
                else {
                    if (localNode instanceof Relation) {
                        log.debug(nodeManagerName + " publish relation with number "
                                + localNodeNumber);
                    }
                    else {
                        log.debug(nodeManagerName + " publish node with number " + localNodeNumber);
                    }

                    if (action.equalsIgnoreCase(ACTION_UPDATE_NODE)) {
                        PublishManager.createNodeAndRelations(localCloudInfo, localNode,
                                remoteCloudInfo, false);
                    }
                    if (action.equalsIgnoreCase(ACTION_UPDATE)) {
                        PublishManager.createNodeAndRelations(localCloudInfo, localNode,
                                remoteCloudInfo, true);
                    }
                }

                for (PublishListener listener : publishListeners) {
                    listener.published(localNode);
                }
            }
            else { // if it is imported from other cloud, ignore this node
                log.debug("imported node in publishqueue " + localNode.getNumber());
            }
        }
        catch (Exception e) {
            throw new BridgeException("PublishManager could not publish " + localNode.getNumber(),
                    e);
        }
        finally {
            log.debug("Published one node(" + localNode.getNumber() + ")");
        }
    }

    private void removeNode(CloudInfo localCloudInfo, Node queueNode) {
        int number = queueNode.getIntValue(FIELD_SOURCENUMBER);

        PublishManager.deletePublishedNode(localCloudInfo, number);
        queueNode.setStringValue(FIELD_STATUS, STATUS_DONE);
        queueNode.commit();
    }

    public static void addPublishListener(PublishListener publishListener) {
        publishListeners.add(publishListener);
    }

    public static void removePublishListener(PublishListener publishListener) {
        publishListeners.remove(publishListener);
    }

    private void linkTypedefs() {
        CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
        CloudInfo remoteCloudInfo = CloudInfo.getCloudInfoByName(remoteCloudName);

        NodeList typedefList = SearchUtil.findNodeList(localCloudInfo.getCloud(), "typedef");
        NodeIterator typedefIterator = typedefList.nodeIterator();
        while (typedefIterator.hasNext()) {
            Node typedefNode = typedefIterator.nextNode();
            if (!PublishManager.isPublished(localCloudInfo, typedefNode)) {
                // not published
                Node remoteTypeDefNode = SearchUtil.findNode(remoteCloudInfo.getCloud(), "typedef",
                        "name", typedefNode.getStringValue("name"));
                if (remoteTypeDefNode != null) {
                    PublishManager.createPublishingInfo(localCloudInfo, typedefNode,
                            remoteCloudInfo, remoteTypeDefNode);

                    log.info("Added remoteinfo for typedef with name : "
                            + typedefNode.getStringValue("name"));
                }
                else {
                    log.debug("No remote node found to link with for typedef with name : "
                            + typedefNode.getStringValue("name"));
                }
            }
            else {
                log.debug("Found remoteinfo for typedef with name : "
                        + typedefNode.getStringValue("name"));
            }
        }
    }
}
