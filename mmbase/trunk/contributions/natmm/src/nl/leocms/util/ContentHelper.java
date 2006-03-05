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
package nl.leocms.util;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Various utilities for ContentElements
 * 
 */
public class ContentHelper {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(ContentHelper.class.getName());

   private Cloud mmbase;
   
   /**
    * @param mmbase
    */
   public ContentHelper(Cloud mmbase) {
      super();
      this.mmbase = mmbase;
   }
   
   public String getNameWithOtype(String otype) {
      String nodes = "typedef";
      String where = "number='"+otype+"'";
      String showField = "typedef.name";
      NodeList list = mmbase.getList("", nodes, showField, where, "", null, null, true );
      NodeIterator it = list.nodeIterator();
      if (it!=null) {
         while (it.hasNext()) {
            Node node = it.nextNode();
            return node.getStringValue("typedef.name");
         }
      }
      return "onbekend type";
   }

   public String getOtypeWithName(String name) {
      String nodes = "typedef";
      String where = "name='"+name+"'";
      String showField = "typedef.number";
      NodeList list = mmbase.getList("", nodes, showField, where, "", null, null, true );
      NodeIterator it = list.nodeIterator();
      if (it!=null) {
         while (it.hasNext()) {
            Node node = it.nextNode();
            return node.getStringValue("typedef.number");
         }
      }
      return "onbekend type";
   }
}
