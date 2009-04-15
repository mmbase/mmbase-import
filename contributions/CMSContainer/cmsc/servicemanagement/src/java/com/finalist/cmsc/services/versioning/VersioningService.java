package com.finalist.cmsc.services.versioning;

import com.finalist.cmsc.services.Service;
import org.mmbase.bridge.Node;

/**
 * This service will be used to manage the versions of particular nodes.
 * 
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public abstract class VersioningService extends Service {
   public static final String ARCHIVE = "archive";
   public static final String ORIGINAL_NODE = "original_node";
   public static final String DATE = "date";
   public static final String NODE_DATA = "node_data";


   /**
    * Creates a new version for the given node.
    * 
    * @param node
    *           The node to be versioned.
    */
   public abstract void addVersion(Node node) throws VersioningException;


   /**
    * Restores the version back to the original node
    * 
    * @param node
    *           The version which will be restrored.
    * @return the restored node
    */
   public abstract Node restoreVersion(Node node) throws VersioningException;


   /**
    * Remove all versions for a node.
    * 
    * @param node
    *           The node where all versions need to be removed.
    */
   public abstract void removeVersions(Node node);
   
   /**
    * set publish mark to the current version.
    * 
    * @param node
    *           
    */
   public abstract void setPublishVersion(Node node);
}
