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
package nl.leocms.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;

// hh: import org.mmbase.applications.wordfilter.WordHtmlCleaner;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import nl.leocms.util.tools.HtmlCleaner;

/**
 * @todo javadoc
 *
 * @author Nico Klasens (Finalist IT Group)
 * @created 23-okt-2003
 * @version $Revision: 1.6 $
 */
public class HtmlBuilder extends MMObjectBuilder {
   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(HtmlBuilder.class.getName());

   /** list of html text fields to clean */
   private List htmlFields = new ArrayList();

   /**
    * @see org.mmbase.module.core.MMObjectBuilder#init()
    */
   public boolean init() {
      if (!super.init())
         return false;

      String tmp = getInitParameter("htmlFields");
      if (tmp != null) {
         StringTokenizer tokenizer = new StringTokenizer(tmp, ", \n");
         while(tokenizer.hasMoreTokens()) {
            String field = tokenizer.nextToken();
            htmlFields.add(field);
         }
         log.debug("html fields for " + getSingularName() + ": " + htmlFields); 
      }
      return true;
   }

   /**
    * @see org.mmbase.module.core.MMObjectBuilder#preCommit(org.mmbase.module.core.MMObjectNode)
    */
   public MMObjectNode preCommit(MMObjectNode node) {
      super.preCommit(node);

      // CLEAN HTML FIELDS
      List fields = getFields(FieldDefs.ORDER_EDIT);
      Iterator iFields = fields.iterator();
      while (iFields.hasNext()) {
         FieldDefs field = (FieldDefs) iFields.next();
         cleanField(node, field);
      }

      return node;
   }

   public void removeNode(MMObjectNode node) {
		
      MMBaseContext mc = new MMBaseContext();
      ServletContext application = mc.getServletContext();
      HashMap hmUnusedItems = (HashMap) application.getAttribute("UnusedItems");
      if (hmUnusedItems!=null){
         Set set = hmUnusedItems.entrySet();
         Iterator it = set.iterator();
         while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            ArrayList alUnusedNodes = (ArrayList) me.getValue();
            if (alUnusedNodes.contains(node.getStringValue("number"))){
               alUnusedNodes.remove(node.getStringValue("number"));
               String account = (String) me.getKey();
			      log.debug("removed node " + node.getNumber() + " from unused items of account " + account);
               if (alUnusedNodes.isEmpty()){
                  hmUnusedItems.remove(account);
               } else {
                  hmUnusedItems.put(account, alUnusedNodes);
               }
            }
         }
      }
      super.removeNode(node);
   }

   /** Cleans a field if it contains html junk.
    *
    * @param node Node of the field to clean
    * @param field Definition of field to clean
    */
   private void cleanField(MMObjectNode node, FieldDefs field) {
      if (field != null) {
         String fieldName = field.getDBName();
         if (field.getDBState() == FieldDefs.DBSTATE_PERSISTENT
            && field.getDBType() == FieldDefs.TYPE_STRING
            && htmlFields.contains(fieldName)) {
            log.debug("cleaning field " + fieldName);
         
            // Persistent string field.
            String originalValue = (String) node.values.get(fieldName);
            if (originalValue!=null && !"".equals(originalValue.trim())) {

               // Edited value: clean.
               // hh: String newValue = WordHtmlCleaner.cleanHtml(originalValue);
               String newValue = HtmlCleaner.cleanHtml(originalValue);

               node.setValue(fieldName, newValue);
               if (log.isDebugEnabled() && !originalValue.equals(newValue)) {
                 log.debug("Replaced " + fieldName + " value \"" + originalValue + "\"\n \t by \n\"" + newValue + "\"");
               }
            }
         }
      }
   }
}
