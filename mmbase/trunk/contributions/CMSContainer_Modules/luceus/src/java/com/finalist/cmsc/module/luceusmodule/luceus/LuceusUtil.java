/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule.luceus;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.FieldList;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

import com.luceus.core.om.Envelope;
import com.luceus.core.om.EnvelopeFieldFactory;

/**
 * Utilities to make life with Luceus/CMSC easier
 * 
 * @author Wouter Heijke
 */
public class LuceusUtil {
   private static Log log = LogFactory.getLog(LuceusUtil.class);

   private static final String MMBASE_ATTACHMENT_MIMETYPE_FIELD = "mimetype";


   public static void nodeFields(Node contentElement, Envelope doc, String prefix) {
      NodeManager nm = contentElement.getNodeManager();

      FieldList fields = nm.getFields();
      for (Iterator<Field> fIter = fields.iterator(); fIter.hasNext();) {
         Field managerField = fIter.next();
         String fieldName = managerField.getName();
         StringBuffer sb = new StringBuffer();
         if (prefix != null && prefix.length() > 0) {
            sb.append(prefix);
            sb.append(".");
         }

         // ignore these internal fields
         if (!fieldName.equalsIgnoreCase("number") && !fieldName.equalsIgnoreCase("otype")
               && !fieldName.equalsIgnoreCase("owner")) {

            sb.append(fieldName);
            String prefixedName = sb.toString();

            if (managerField.getType() == Field.TYPE_STRING) {
               String str = contentElement.getStringValue(fieldName).replaceAll("<.+?>", "");
               doc.add(EnvelopeFieldFactory.getStringField(prefixedName, str));
               str = null;
            }
            else if (managerField.getType() == Field.TYPE_DATETIME) {
               doc.add(EnvelopeFieldFactory.getDateField(prefixedName, contentElement.getDateValue(fieldName)));
            }
            else if (managerField.getType() == Field.TYPE_BINARY) {
               String mimetypeCheck = contentElement.getStringValue(MMBASE_ATTACHMENT_MIMETYPE_FIELD);
               if (mimetypeCheck != null && mimetypeCheck.length() > 0) {
                  byte[] rawDoc = contentElement.getByteValue(fieldName);
                  doc.add(EnvelopeFieldFactory.getBinaryField(prefixedName, rawDoc, mimetypeCheck));
                  // log.debug("size='"+rawDoc.length+"'");
                  rawDoc = null;
               }
            }
            else if (managerField.getType() == Field.TYPE_BOOLEAN) {
               doc.add(EnvelopeFieldFactory.getBooleanField(prefixedName, contentElement.getBooleanValue(fieldName)));
            }
            else if (managerField.getType() == Field.TYPE_INTEGER) {
               doc.add(EnvelopeFieldFactory.getIntegerField(prefixedName, contentElement.getIntValue(fieldName)));
            }
            else if (managerField.getType() == Field.TYPE_LONG) {
               doc.add(EnvelopeFieldFactory.getLongField(prefixedName, contentElement.getLongValue(fieldName)));
            }
            else if (managerField.getType() == Field.TYPE_FLOAT) {
               doc.add(EnvelopeFieldFactory.getFloatField(prefixedName, contentElement.getFloatValue(fieldName)));
            }
            else if (managerField.getType() == Field.TYPE_DOUBLE) {
               doc.add(EnvelopeFieldFactory.getDoubleField(prefixedName, contentElement.getDoubleValue(fieldName)));
            }
            else if (managerField.getType() == Field.TYPE_LIST) {
               // log.debug("TODO LIST:'" + fieldName + "'");
            }
            else if (managerField.getType() == Field.TYPE_NODE) {
               // log.debug("TODO NODE:'" + fieldName + "'");
            }
            else if (managerField.getType() == Field.TYPE_XML) {
               // log.debug("TODO XML:'" + fieldName + "'");
            }
            else {
               log.warn("Unsupported type:'" + managerField.getType() + "'");
            }
         }
         else if (fieldName.equalsIgnoreCase("number")) {
            sb.append("id");
            String prefixedName = sb.toString();
            doc.add(EnvelopeFieldFactory.getStringField(prefixedName, contentElement.getStringValue(fieldName)));
         }
         sb = null;
      }
      fields = null;
   }


   public static void nodeFields(Node contentElement, Envelope doc) {
      if (contentElement != null) {
         String prefix = contentElement.getNodeManager().getName();
         nodeFields(contentElement, doc, prefix);
      }
   }

}
