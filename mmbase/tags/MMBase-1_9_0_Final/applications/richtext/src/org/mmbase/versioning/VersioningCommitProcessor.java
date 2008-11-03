package org.mmbase.versioning;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.datatypes.processors.CommitProcessor;
import org.mmbase.storage.search.FieldCompareConstraint;

import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This commitprocessor copies on every commit the complete node to a 'versioning' table.
 * @author Sander de Boer
 * @author Michiel Meeuwissen
 * @version $Id: VersioningCommitProcessor.java,v 1.10 2008-08-05 16:38:21 michiel Exp $
 * @since
 */

public class VersioningCommitProcessor implements CommitProcessor {

    private static final Logger log = Logging.getLoggerInstance(VersioningCommitProcessor.class);

    private static final long serialVersionUID = 1L;

    public static final String COMMENTS_PROPERTY = "org.mmbase.versioning.comments";

    public static final String VERSION_FIELD   = "version";
    public static final String OBJECT_FIELD    = "object";
    public static final String COMMENTS_FIELD  = "comments";

    private String statusField                 = "status";

    public static NodeManager getVersionsManager(NodeManager nm) {
        String versionBuilder = nm.getProperty("versionbuilder");
        if (versionBuilder == null || "".equals(versionBuilder)) {
            versionBuilder = nm.getName() + "_versions";
        }
        return nm.getCloud().getNodeManager(versionBuilder);
    }

    public static NodeQuery getVersionsQuery(Node node) {
        NodeQuery q = getVersionsManager(node.getNodeManager()).createQuery();
        Queries.addConstraint(q, Queries.createConstraint(q, OBJECT_FIELD, FieldCompareConstraint.EQUAL, node));
        Queries.addSortOrders(q, VERSION_FIELD, "UP");
        return q;
    }
    public static NodeList getVersions(Node node) {
        NodeQuery q = getVersionsQuery(node);
        return q.getNodeManager().getList(q);
    }

    public void setStatusField(String f) {
        statusField = f;
    }

    public void commit(Node node, Field field) {
        if (node.isChanged()) {
            log.debug("Commiting " + node + "setting version in " + field);

            NodeManager wo = node.getNodeManager();
            NodeManager wv = getVersionsManager(wo);

            log.debug("Found the version builder: '" + wv.getName() + "'");

            //clone this version to the versions builder
            Cloud cloud = node.getCloud();

            if (node.getNumber() > 0) { // TODO TODO http://www.mmbase.org/jira/browse/MMB-1632

                Node version = wv.createNode();
                //increase the version of the current node
                int newVersionNo = node.getIntValue(field.getName()) + 1;
                node.setIntValue(field.getName(), newVersionNo);

                if (! "".equals(statusField) && node.getNodeManager().hasField(statusField)) {
                    if (node.getIntValue(statusField) == Status.NEW) {
                        if (! node.getChanged().contains(statusField)) {
                            node.setIntValue(statusField, Status.ONLINE);
                        } else {
                            log.debug("Ignoreing auto-online because explicitely set to new");
                        }
                    }
                }

                cloneNode(node, version);

                version.setNodeValue(OBJECT_FIELD, node);
                version.setIntValue(VERSION_FIELD, newVersionNo);

                version.setStringValue(COMMENTS_FIELD, Casting.toString(cloud.getProperty(COMMENTS_PROPERTY)));
                Object validation = version.getCloud().getProperty(Cloud.PROP_IGNOREVALIDATION);
                version.getCloud().setProperty(Cloud.PROP_IGNOREVALIDATION, Boolean.TRUE);
                // shit..., node fields don't like new nodes.
                try {
                    version.commit();
                } catch (Exception e) {
                    log.error(e);
                }
                version.getCloud().setProperty(Cloud.PROP_IGNOREVALIDATION, validation);
            }
            // could solve it by in this case using the _old values_ of the node.
            // But there are 2 bugs, which make this work around non-feasible:
            //  http://www.mmbase.org/jira/browse/MMB-1522.
            //  http://www.mmbase.org/jira/browse/MMB-1621 // This would also give a way to get  the 'old values'.
        } else {
            log.service("Node not changed");
        }
    }



    private void cloneNode(Node source, Node dest) {
        NodeManager sourceNm = source.getNodeManager();
        FieldIterator fields = sourceNm.getFields().fieldIterator();
        while (fields.hasNext()) {
           Field field = fields.nextField();
           String fn = field.getName();
           if (dest.getNodeManager().hasField(fn) && field.getState() != Field.STATE_SYSTEM) {
               dest.setValueWithoutProcess(fn, source.getValue(fn));
           }
        }
    }
}
