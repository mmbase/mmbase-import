package com.finalist.cmsc.services.versioning;

import org.mmbase.module.Module;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 * @version $Revision: 1.1 $, $Date: 2006-12-12 09:42:31 $
 */
public class VersioningModule extends Module {

   public void init() {
      new VersioningServiceMMBaseImpl();
   }
}
