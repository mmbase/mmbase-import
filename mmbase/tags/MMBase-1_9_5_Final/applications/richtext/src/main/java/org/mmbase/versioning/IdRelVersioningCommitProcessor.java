package org.mmbase.versioning;

import org.mmbase.richtext.builders.IdRel;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.w3c.dom.Document;
import org.mmbase.datatypes.processors.CommitProcessor;

import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import javax.xml.xpath.*;


/**
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since
 */

public class IdRelVersioningCommitProcessor implements CommitProcessor {

    private static final Logger log = Logging.getLoggerInstance(IdRelVersioningCommitProcessor.class);

    private static final long serialVersionUID = 1L;

    private String fields = "body";

    public void setFields(String f) {
        fields = f;
    }

    static NodeList getIdRels(Node node) {
        Cloud cloud = node.getCloud();
        NodeManager objects = cloud.getNodeManager("object");
        NodeQuery q = Queries.createRelationNodesQuery(node, objects, "idrel", "destination");
        return q.getNodeManager().getList(q);
    }

    boolean hasId(Document doc, String id) {
        //return doc.getElementById(id) != null;
        try {
            XPath xp = XPathFactory.newInstance().newXPath();
            return xp.evaluate("//*[@id = '" + id + "']", doc, XPathConstants.NODE) != null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    boolean isRelevant(Node idrel, Node node) {
        String id = idrel.getStringValue(IdRel.ID);
        for (String xmlField : fields.split(",")) {
            if (node.getNodeManager().hasField(xmlField)) {
                Document doc = node.getXMLValue(xmlField);
                if (log.isDebugEnabled()) {
                    log.debug("Checking whether '" + id + "' is mentioned in " + org.mmbase.util.xml.XMLWriter.write(doc, true));
                }
                if (hasId(doc, id)) {
                    log.debug("yes");
                    return true;
                } else {
                    log.debug("no");
                }
            } else {
                log.warn("No such field " + xmlField);
            }
        }
        return false;
    }


    static boolean hasVersionFields(NodeManager idrel) {
        return idrel.hasField(IdRel.VERSION_FROM) &&
            idrel.hasField(IdRel.VERSION_TO);
    }

    static boolean isVersionFieldsFilled(Node idrel) {
        return ! idrel.isNull(IdRel.VERSION_FROM) &&  !idrel.isNull(IdRel.VERSION_TO);
    }


    /**
     * Fills 'version_from', 'version_to' of all idrels of this node for which they are empty.
     */
    void fillFromAndToFields(Node node, Field field) {
        int newVersion = node.getIntValue(field.getName());
        NodeIterator links = getIdRels(node).nodeIterator();
        while (links.hasNext()) {
            Node idrel = links.nextNode();
            log.debug("Considering " + idrel.getFunctionValue("gui", null).toString());
            if(isVersionFieldsFilled(idrel)) {
                log.debug("Versiong fields filled already");
                if (isRelevant(idrel, node)) {
                    if (idrel.getIntValue(IdRel.VERSION_TO) == newVersion - 1) {
                        idrel.setIntValue(IdRel.VERSION_TO, newVersion);
                        idrel.commit();
                    }

                }

            } else {
                log.debug("Versioning fields not filled already");
                boolean found = false;
                NodeIterator i = VersioningCommitProcessor.getVersions(node).nodeIterator();
                while (i.hasNext()) {
                    Node versionNode = i.nextNode();
                    int version = versionNode.getIntValue(VersioningCommitProcessor.VERSION_FIELD);
                    log.debug("Considering versioning node " + versionNode.getNumber() + " version : " + version);
                    if (isRelevant(idrel, versionNode)) {
                        if (!found) {
                            idrel.setIntValue(IdRel.VERSION_FROM, version);
                            found = true;
                        }
                        idrel.setIntValue(IdRel.VERSION_TO, version);
                    } else {
                        if (found) {
                            break;
                        }
                    }
                }
                idrel.commit();

            }
        }
    }

    public void commit(Node node, Field field) {
        log.debug("Version fields of idrels");
        if (node.isChanged()) {
            if (hasVersionFields(node.getCloud().getNodeManager("idrel"))) {
                fillFromAndToFields(node, field);
            } else {
                log.debug("No version fields in idrel builder");
            }
        } else {
            log.debug("Node was not changed");
        }
    }


}
