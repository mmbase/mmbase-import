package org.mmbase.versioning;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.processors.CommitProcessor;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This commitprocessor copies on every commit the complete node to a 'versioning' table.
 * @author Sander de Boer
 * @author Michiel Meeuwissen
 * @version $Id: VersioningCommitProcessor.java,v 1.1 2008-03-11 16:02:09 michiel Exp $
 * @since
 */

public class VersioningCommitProcessor implements CommitProcessor {

    private static final Logger log = Logging.getLoggerInstance(VersioningCommitProcessor.class);

    private static final long serialVersionUID = 1L;

    public void commit(Node node, Field arg) {
        NodeManager wo = node.getNodeManager();
        String versionBuilder = wo.getProperty("versionbuilder");
        if (versionBuilder != null && !"".equals(versionBuilder)) {
            NodeManager wv = node.getCloud().getNodeManager(versionBuilder);
            log.debug("Found the version builder: '" + wv.getName() + "'");

            //clone this version to the versions builder
            Cloud cloud = node.getCloud();
            Node version = wv.createNode();
            Node orig = cloud.getNode(node.getNumber());
            log.service("Obtained original node with " + cloud + " " + cloud.getClass() + " " + orig);

            //increase the version of the current node
            int newVersionNo = orig.getIntValue("version") + 1;
            node.setIntValue("version", newVersionNo);

            cloneNode(orig, version);

            version.setDateValue("timestamp", orig.getDateValue("lastmodified"));
            version.setNodeValue("object", orig.getNodeValue("number"));
            version.commit();

        } else {
            log.error("Nodemanager '" + versionBuilder + "' not found");
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
           dest.setIntValue("version", source.getIntValue("version"));
        }
    }
}
