package com.finalist.cmsc.services.versioning;

import com.finalist.cmsc.services.ServiceManager;
import org.mmbase.bridge.Node;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class Versioning {
   private final static VersioningService cService = (VersioningService) ServiceManager
         .getService(VersioningService.class);


   public static void addVersion(Node node) throws VersioningException {
      cService.addVersion(node);
   }


   public static Node restoreVersion(Node node) throws VersioningException {
      return cService.restoreVersion(node);
   }


   public static void removeVersions(Node node) {
      cService.removeVersions(node);
   }
}
