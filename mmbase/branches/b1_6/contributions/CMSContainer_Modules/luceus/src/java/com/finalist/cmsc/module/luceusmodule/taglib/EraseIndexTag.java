/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule.taglib;

/**
 * Erase all Luceus content from this repository
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.3 $
 */
public class EraseIndexTag extends LuceusmoduleTag {

   @Override
   public void doTag() {
      if (isRunning()) {
         getModule().startEraseIndex();
      }
   }

}
