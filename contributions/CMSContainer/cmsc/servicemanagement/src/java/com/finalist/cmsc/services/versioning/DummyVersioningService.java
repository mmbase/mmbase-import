package com.finalist.cmsc.services.versioning;

import org.mmbase.bridge.Node;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 * @version $Revision: 1.1 $, $Date: 2006-12-12 09:38:20 $
 */
public class DummyVersioningService extends VersioningService {

   @Override
   public void addVersion(Node node) {
   }

   @Override
   public Node restoreVersion(Node node) {
	   return null;
   }

   @Override
   public void removeVersions(Node node) {
   }
}
