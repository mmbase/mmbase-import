/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.richtext.builders;

import org.mmbase.module.Module;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;

import org.mmbase.util.logging.*;

public class InlineRel extends InsRel {
   static Logger log = Logging.getLoggerInstance(InlineRel.class.getName());
   MMBase mmbaseroot = null;


   @Override
   public void setDefaults(MMObjectNode node) {
      if (mmbaseroot == null) {
         mmbaseroot = (MMBase) Module.getModule("mmbaseroot");
      }

      node.setValue("referid", mmbaseroot.getStorageManager().createKey() + "");
   }
   
   /**
    * This method is here to solve
    * MMB-1713  Bridge transaction always commits nodes even when not changed
    *
    * MMBase always commits nodes even when they are not changed or deleted.
    * @see org.mmbase.module.core.MMObjectBuilder#commit(org.mmbase.module.core.MMObjectNode)
    */
   @Override
   public boolean commit(MMObjectNode node) {
      if (!node.isChanged()) {
         return true;
      }
      return super.commit(node);
   }

}