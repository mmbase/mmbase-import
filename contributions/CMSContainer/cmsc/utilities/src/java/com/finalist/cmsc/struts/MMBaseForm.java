/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.struts;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForm;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class MMBaseForm extends ActionForm {

   private static final long serialVersionUID = 4264517673164742392L;

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(MMBaseForm.class.getName());

   private int id;


   public int getId() {
      return id;
   }


   public void setId(int nodeNumber) {
      this.id = nodeNumber;
   }


   public void setNumber(int nodeNumber) {
      this.id = nodeNumber;
   }


   public int getNumber() {
      return id;
   }


   protected String convertToString(Date input) {
      if (input != null) {
         return DateFormat.getDateInstance().format(input);
      }
      return null;
   }


   protected Date convertToDate(String deadline) {
      Date input = null;
      if (!StringUtil.isEmpty(deadline)) {
         try {
            input = DateFormat.getDateInstance().parse(deadline);
         }
         catch (ParseException e) {
            log.debug("" + e.getMessage(), e);
         }
      }
      return input;
   }

}
