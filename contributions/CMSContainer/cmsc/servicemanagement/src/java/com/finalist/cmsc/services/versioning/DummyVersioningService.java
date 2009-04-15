package com.finalist.cmsc.services.versioning;

import org.mmbase.bridge.Node;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class DummyVersioningService extends VersioningService {

   @Override
   public void addVersion(Node node) {
      // nothing
   }


   @Override
   public Node restoreVersion(Node node) {
      return null;
   }


   @Override
   public void removeVersions(Node node) {
      // nothing
   }
   
   @Override
   public void setPublishVersion(Node node) {
      
   }
}
