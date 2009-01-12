/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Perform a Luceus FullIndex
 * 
 * @author Wouter Heijke
 */
public class FullIndexTag extends LuceusmoduleTag {
   private static Log log = LogFactory.getLog(FullIndexTag.class);

   /**
    * Erase index first, so do a clean-fullindex.
    */
   public boolean erase = false;


   @Override
   public void doTag() throws JspException, IOException {
      if (isRunning()) {
         getModule().startFullIndex(erase);
      }
   }


   public void setErase(boolean erase) {
      this.erase = erase;
   }

}
