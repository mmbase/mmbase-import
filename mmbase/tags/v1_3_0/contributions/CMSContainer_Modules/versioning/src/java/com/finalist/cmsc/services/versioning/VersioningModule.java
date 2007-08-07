package com.finalist.cmsc.services.versioning;

import org.mmbase.module.Module;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class VersioningModule extends Module {

   public void init() {
      new VersioningServiceMMBaseImpl();
   }
}
