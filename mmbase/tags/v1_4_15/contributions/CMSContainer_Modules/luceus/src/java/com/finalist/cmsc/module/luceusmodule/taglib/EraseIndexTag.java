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
 * Erase all Luceus content from this repository
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
public class EraseIndexTag extends LuceusmoduleTag {
   private static Log log = LogFactory.getLog(EraseIndexTag.class);


   @Override
   public void doTag() throws JspException, IOException {
      if (isRunning()) {
         getModule().startEraseIndex();
      }
   }

}
