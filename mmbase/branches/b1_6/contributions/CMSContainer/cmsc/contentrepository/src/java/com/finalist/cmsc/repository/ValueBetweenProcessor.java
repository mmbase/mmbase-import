/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository;

import java.util.Date;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.datatypes.processors.CommitProcessor;

@SuppressWarnings("serial")
public class ValueBetweenProcessor implements CommitProcessor {

   private String fromField;
   private String toField;


   public ValueBetweenProcessor(String from, String to) {
      fromField = from;
      toField = to;
   }


   public void commit(Node node, Field field) {
      switch (field.getType()) {
         case Field.TYPE_INTEGER:
         case Field.TYPE_LONG: {
            long fromValue = node.getLongValue(fromField);
            long toValue = node.getLongValue(toField);
            long current = node.getLongValue(field.getName());
            if (fromValue > current) {
               node.setLongValue(field.getName(), fromValue);
            }
            else {
               if (toValue < current) {
                  node.setLongValue(field.getName(), toValue);
               }
            }
         }
            break;
         case Field.TYPE_DOUBLE:
         case Field.TYPE_FLOAT: {
            double fromValue = node.getDoubleValue(fromField);
            double toValue = node.getDoubleValue(toField);
            double current = node.getDoubleValue(field.getName());
            if (fromValue > current) {
               node.setDoubleValue(field.getName(), fromValue);
            }
            else {
               if (toValue < current) {
                  node.setDoubleValue(field.getName(), toValue);
               }
            }
         }
            break;
         case Field.TYPE_DATETIME: {
            Date fromValue = node.getDateValue(fromField);
            Date toValue = node.getDateValue(toField);
            Date current = node.getDateValue(field.getName());
            if (fromValue.getTime() > current.getTime()) {
               node.setDateValue(field.getName(), fromValue);
            }
            else {
               if (toValue.getTime() < current.getTime()) {
                  node.setDateValue(field.getName(), toValue);
               }
            }
         }
            break;
         default:
            break;
      }
   }

}
