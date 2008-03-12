package org.mmbase.versioning;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.processors.CommitProcessor;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This commitprocessor copies on every commit the complete node to a 'versioning' table.
 * @author Sander de Boer
 * @author Michiel Meeuwissen
 * @version $Id: VersioningCommitProcessor.java,v 1.3 2008-03-12 10:24:52 michiel Exp $
 * @since
 */

public class VersioningCommitProcessor implements CommitProcessor {

    private static final Logger log = Logging.getLoggerInstance(VersioningCommitProcessor.class);

    private static final long serialVersionUID = 1L;

    public static final String VERSION_FIELD   = "version";
    public static final String OBJECT_FIELD    = "object";
    public static final String TIMESTAMP_FIELD = "timestamp";

    public void commit(Node node, Field field) {
        NodeManager wo = node.getNodeManager();
        String versionBuilder = wo.getProperty("versionbuilder");

        if (versionBuilder == null || "".equals(versionBuilder)) {
            versionBuilder = wo.getName() + "_versions";
        }
        NodeManager wv = node.getCloud().getNodeManager(versionBuilder);
        log.debug("Found the version builder: '" + wv.getName() + "'");

        //clone this version to the versions builder
        Cloud cloud = node.getCloud();
        Node version = wv.createNode();

        //increase the version of the current node
        int newVersionNo = node.getIntValue(field.getName()) + 1;
        node.setIntValue(field.getName(), newVersionNo);

        cloneNode(node, version);

        if (node.getNodeManager().hasField("lastmodified")) {
            version.setDateValue(TIMESTAMP_FIELD, node.getDateValue("lastmodified"));
        }
        version.setNodeValue(OBJECT_FIELD, node);
        version.setIntValue(VERSION_FIELD, newVersionNo);
        if (! node.isNew()) {
            // shit..., node fields don't like new nodes.
            version.commit();
            // could solve it by in this case using the _old values_ of the node.
            // But there are 2 bugs, which make this work around non-feasible:
            //  http://www.mmbase.org/jira/browse/MMB-1522.
            //  http://www.mmbase.org/jira/browse/MMB-1621 // This would also give a way to get  the 'old values'.
        }
    }



    private void cloneNode(Node source, Node dest) {
        NodeManager sourceNm = source.getNodeManager();
        FieldIterator fields = sourceNm.getFields().fieldIterator();
        while (fields.hasNext()) {
           Field field = fields.nextField();
           if (field.getState() != Field.STATE_SYSTEM) {
               String fieldName = field.getName();
               dest.setValueWithoutProcess(fieldName, source.getValue(fieldName));
           }
        }
    }
}
