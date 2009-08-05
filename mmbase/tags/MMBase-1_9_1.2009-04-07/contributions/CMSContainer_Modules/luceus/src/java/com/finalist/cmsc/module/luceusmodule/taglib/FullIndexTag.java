/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule.taglib;

import org.apache.commons.lang.StringUtils;

/**
 * Perform a Luceus FullIndex
 * 
 * @author Wouter Heijke
 */
public class FullIndexTag extends LuceusmoduleTag {

   /**
    * Erase index first, so do a clean-fullindex.
    */
   public boolean erase = false;
   
   /**
    * Optionally give a nodemanager to specifically index the given type
    */
   private String nodemanager = null;


   @Override
   public void doTag() {
      if (isRunning()) {
         if (erase && StringUtils.isNotBlank(nodemanager)) {
            throw new IllegalArgumentException("A *clean* FullIndex can not be executed together with a specific nodemanager type! Please remove either one of the two.");
         }
         getModule().startFullIndex(erase, nodemanager);
      }
   }


   public void setErase(boolean erase) {
      this.erase = erase;
   }


   public void setNodemanager(String nodemanager) {
      this.nodemanager = nodemanager;
   }

}
