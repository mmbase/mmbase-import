/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.versioning;

import org.mmbase.bridge.*;


/**
 * @author Ronald Kramp
 * @version $Revision: 1.1 $
 * This class is just a container for a shared lock for
 * VersionManager and PublishManager
 */
public abstract class VersioningBase {
   static Object versionLock = new Object();

   /* lock to be shared by VersionManager & PublishManager */

   /**
    * cloneNodeField copies node fields from one node to an other
    * @param sourceNode the source node
    * @param destinationNode destination node
    * @param field the field to clone
    */
   protected static void cloneNodeField(Node sourceNode, Node destinationNode, Field field) {
      String fieldName = field.getName();
      int fieldType = field.getType();

      switch (fieldType) {
      case Field.TYPE_BYTE:
         destinationNode.setByteValue(fieldName,
            sourceNode.getByteValue(fieldName));

         break;

      case Field.TYPE_DOUBLE:
         destinationNode.setDoubleValue(fieldName,
            sourceNode.getDoubleValue(fieldName));

         break;

      case Field.TYPE_FLOAT:
         destinationNode.setFloatValue(fieldName,
            sourceNode.getFloatValue(fieldName));

         break;

      case Field.TYPE_INTEGER:
         destinationNode.setIntValue(fieldName,
            sourceNode.getIntValue(fieldName));

         break;

      case Field.TYPE_LONG:
         destinationNode.setLongValue(fieldName,
            sourceNode.getIntValue(fieldName));

         break;

      case Field.TYPE_STRING:
         destinationNode.setStringValue(fieldName,
            sourceNode.getStringValue(fieldName));

         break;

      default:
         destinationNode.setValue(fieldName, sourceNode.getValue(fieldName));
      }
   }

   // quick test to see if node is a relation by testing fieldnames
   protected static boolean isRelation(Node node) {
      FieldIterator fi = node.getNodeManager().getFields().fieldIterator();
      int count = 0;

      while (fi.hasNext()) {
         String name = fi.nextField().getName();

         if (name.equals("rnumber") || name.equals("snumber") ||
               name.equals("dnumber")) {
            count++;
         }
      }

      if (count == 3) {
         return true;
      }

      return false;
   }
}
