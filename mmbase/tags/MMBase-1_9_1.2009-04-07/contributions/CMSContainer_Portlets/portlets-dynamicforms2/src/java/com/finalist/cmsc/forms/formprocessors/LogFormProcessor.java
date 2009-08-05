/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.forms.formprocessors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.forms.value.ValueObject;


public class LogFormProcessor extends FormProcessor {

   private static final Log log = LogFactory.getLog(LogFormProcessor.class);
   
   private StringBuilder sb = new StringBuilder("\n");
   
   public String processForm(ValueObject valueObject) {
      processObject(valueObject);
      log.info(sb.toString());
      return null;
   }

   protected void processField(String path, String value) {
      sb.append(path).append(" = ").append(value).append("\n");
   }

}
