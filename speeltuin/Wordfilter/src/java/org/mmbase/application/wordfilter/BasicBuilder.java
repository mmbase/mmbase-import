/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.application.wordfilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.util.logging.*;

/**
 * Basic builder for our own objecttypes.
 *
 * @author  Rob van Maris
 * @author  Nico Klasens (Finalist IT Group)
 * 
 * @version $Revision: 1.1 $
 * @todo maybe extend InsRel in a simalar way?
 */
public class BasicBuilder extends MMObjectBuilder {
   
	/** Logger instance. */
	private final static Logger log = Logging.getLoggerInstance(BasicBuilder.class.getName());

	/** list of html text fields to clean */
	private List cleanFields = new ArrayList();
   
	/** Creator. */
	public BasicBuilder() {
	}

	/**
	 * @see org.mmbase.module.core.MMObjectBuilder#init()
	 */
	public boolean init() {
		if (!super.init())
			return false;

		String tmp = getInitParameter("cleanfields");
		if (tmp != null) {
			StringTokenizer tokenizer = new StringTokenizer(tmp, ",");
			while(tokenizer.hasMoreTokens()) {
				String field = tokenizer.nextToken();
				log.debug("clean field: " + field.trim());
				cleanFields.add(field);
			}
		}
		return true;
	}

	/**
	 * @see org.mmbase.module.core.MMObjectBuilder#preCommit(org.mmbase.module.core.MMObjectNode)
	 */
	public MMObjectNode preCommit(MMObjectNode node) {
		List fields = getFields(FieldDefs.ORDER_EDIT);
		Iterator iFields = fields.iterator();
		while (iFields.hasNext()) {
			FieldDefs field = (FieldDefs) iFields.next();
			cleanField(node, field);
		}
		return node;
	}
   
	/** Clean html from the field of the node
    * 
    * @param node node to clean field from
    * @param field to be cleaned
    */
   private void cleanField(MMObjectNode node, FieldDefs field) {
		String fieldName = field.getDBName();
		if (field != null
   	   && field.getDBState() == FieldDefs.DBSTATE_PERSISTENT
	  	   && field.getDBType() == FieldDefs.TYPE_STRING
         && cleanFields.contains(fieldName)) {
			
         // Persistent string field.
			String originalValue = (String) node.values.get(fieldName);
			if (originalValue!=null && !"".equals(originalValue.trim())) {
				// Edited value: clean.
				String newValue = WordHtmlCleaner.cleanHtml(originalValue);
				node.setValue(fieldName, newValue);
				if (log.isDebugEnabled()) {
					if (!originalValue.equals(newValue)) {
						log.debug("Replaced " + fieldName + " value \"" + originalValue + "\" by \n\"" + newValue + "\"");
					}
				}
			}
		}
	}
}
