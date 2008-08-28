/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.datatypes.processors.CommitProcessor;
import org.mmbase.util.dateparser.DateParser;
import org.mmbase.util.dateparser.ParseException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

@SuppressWarnings("serial")
public class DerivedDateProcessor implements CommitProcessor {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(DerivedDateProcessor.class.getName());

   private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

   private String offset;
   private String derivedField;
   private boolean afterfield;


   public DerivedDateProcessor(String field, String offset) {
      this.derivedField = field;
      this.offset = offset;
      this.afterfield = !offset.trim().startsWith("-");
   }


   public void commit(Node node, Field field) {
      Date sourceDate = node.getDateValue(derivedField);
      Date fieldDate = node.getDateValue(field.getName());

      if (afterfield) {
         if (fieldDate.before(sourceDate)) {
            changeDate(node, field, sourceDate);
         }
         else {
            Date now = new Date();
            if (fieldDate.after(now)) {
               changeDate(node, field, sourceDate);
            }
         }
      }
      else {
         if (fieldDate.after(sourceDate)) {
            changeDate(node, field, sourceDate);
         }
         else {
            Date now = new Date();
            if (fieldDate.before(now)) {
               changeDate(node, field, sourceDate);
            }
         }
      }
   }


   private void changeDate(Node node, Field field, Date date) {
      try {
         String sourceStr = formatter.format(date);
         if (afterfield) {
            sourceStr += " + " + offset;
         }
         else {
            sourceStr += " " + offset;
         }
         DateParser parser = new DateParser(new StringReader(sourceStr));
         parser.start();
         node.setDateValue(field.getName(), parser.toDate());
      }
      catch (ParseException e) {
         log.warn("Failed to generate new date with offset: " + offset, e);
      }
   }

}
