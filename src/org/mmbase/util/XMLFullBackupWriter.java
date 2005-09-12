/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.File;
import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.ApplicationReader;

/**
 * This is used to export a full backup, by writing all nodes to XML.
 * Based on {@link org.mmbase.util.XMLContextDepthWriterII}.
 *
 * @application Applications
 * @move org.mmbase.util.xml
 * @rename FullBackupWriter
 * @duplicate extend from org.mmbase.util.xml.DocumentWriter * @author Rob van Maris
 * @see org.mmbase.util.XMLContextDepthWriterII
 * @since MMBase-1.7
 */
public class XMLFullBackupWriter extends XMLContextDepthWriterII {

    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(XMLFullBackupWriter.class.getName());

    /**
     * Writes all nodes to XML.
     * @param app A <code>ApplicationReader</code> initialised to read
     *        the application's description (xml) file
     * @param targetpath The path where to save the application
     * @param mmb Reference to the MMbase processormodule. Used to retrieve the nodes to write.
     * @param logger Storage for messages which can be displayed to the user.
     * @return Returns true if succesful, false otherwise.
     */
    public static boolean writeContext(ApplicationReader app, String targetpath, MMBase mmb, Logger logger) {

        try {
            // Create directory for data files.
            String subTargetPath = targetpath + "/" + app.getName() + "/";
            File file = new File(subTargetPath);
            try {
                file.mkdirs();
            } catch (Exception e) {
                log.error("Failed to create dir " + subTargetPath + ": " + e);
            }

            // Write the nodes
            writeNodes(subTargetPath, mmb, logger);

            logger.info("Full backup finished.");

            //            // write DataSources
            //            writeDataSources(app,nodes,targetpath,mmb,logger);
            //            // write relationSources
            //            writeRelationSources(app,relnodes,targetpath,mmb,logger);
        } catch (Exception e) {
            logger.error("Backup failed, exception: " + e);
            log.error("Backup failed", e);
        }

        return true;
    }

    /**
     * Searches the MMBase cloud, colelcting all nodes (and corresponmding relation nodes) that belong to a specific
     * type, and which can be traced up to a certain depth of nodes to a starting node.
     *
     * @param startnodenr the number of the node to start with
     * @param maxdeoth the maximum depth a tree is traversed. A depth of 0 or less means only the sdtartnode is added.
     *			A depth of one includes all teh nodes refernced by the startnode, etc.
     *			Relation nodes are not counted when determining 'depth'.
     * @param fb a <code>HashSet</code> containing the set of types that are allowed for export
     * @param nodesdoneSet  A <code>HashSet</code> which holds all nodes that are already 'done' or 'almost done'. this set is expanded in the method
     *			nodes already in this set are skipped (optimization). After return, the set has been expanded
     *			with all nodes found while traversing the cloud
     * @param mmb MMBase object used to retrieve builder information
     * @param logger
     * @todo update javadoc
     */
    static void writeNodes(String subTargetPath, MMBase mmb, Logger logger) {

        InsRel insrel = mmb.getInsRel();

        // Get all loaded builders.
        Enumeration eBuilders = mmb.getMMObjects();

        // For each builder, add its nodes to the sets.
        while (eBuilders.hasMoreElements()) {
            MMObjectBuilder builder = (MMObjectBuilder) eBuilders.nextElement();

            // Skip virtual builders
            if (builder.isVirtual()) {
                continue;
            }

            // Skip nodes of these builders:
            if (builder.getTableName().equals("reldef")
                || builder.getTableName().equals("typerel")
                || builder.getTableName().equals("versions")
                || builder.getTableName().equals("syncnodes")
                || builder.getTableName().equals("daymarks")
                || builder.getTableName().equals("oalias")
                || builder.getTableName().equals(
                    "icaches") // || builder.getTableName().equals("typedef")
            // || builder.getTableName().equals("object")
            // || builder.getTableName().equals("mmservers")
            ) {
                continue;
            }

            boolean isRelation = builder == insrel || builder.isExtensionOf(insrel);

            // Add this builder's nodes to set (by nodenumber).
            List nodes = builder.searchList("otype==" + builder.getObjectType());
            writeNodes(subTargetPath, mmb, logger, builder, nodes, isRelation);
        }
    }

    /**
     * Writes the nodes of a particular type to the corresponding XML file.
     * @param builder The builder.
     * @param nodes The nodes, must type corresponding to the builder.
     * @param subTargetPath Path where the XML file is written.
     * @param mmb MMBase object used to retrieve builder information
     * @param logger Used to store messages that can be shown to the user
     * @param isRelation Indicates whether the nodes to write are data (false) or relation (true) nodes
     */
    static void writeNodes(String subTargetPath, MMBase mmb, Logger logger, MMObjectBuilder builder, List nodes, boolean isRelation) {

        // Create nodewriter for this builder
        NodeWriter nodeWriter = new NodeWriter(mmb, logger, subTargetPath, builder.getTableName(), isRelation);

        Iterator iNodes = nodes.iterator();
        int nrWritten = 0;
        while (iNodes.hasNext()) {
            MMObjectNode node = (MMObjectNode) iNodes.next();
            // Write node if it's of the correct type.
            if (node.parent == builder) {
                nodeWriter.write(node);
                nrWritten++;
            }
        }
        nodeWriter.done();

        log.debug("Builder " + builder.getTableName() + ": " + nrWritten + (isRelation ? " relations" : " nodes") + " written.");
    }

}
