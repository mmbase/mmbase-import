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
package nl.leocms.signalering.forms;

import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
/**
 * Form bean for the takenlijst page.
 *
 * @author Ronald Kramp
 * @version $Revision: 1.2 $, $Date: 2006-03-08 22:23:51 $
 *
 * @struts:form name="DeleteSignaleringForm"
 */

public class DeleteSignaleringForm extends ActionForm {

   private static final Logger log = Logging.getLoggerInstance(DeleteSignaleringForm.class);
   
   /** A TreeSet of Integers indicating which signalringen were checked */
   private Set checked;
   
   public DeleteSignaleringForm() {
      resetChecked();
   }
   
   private void resetChecked() {
     checked = new TreeSet();
   }

   /**
    * 
    */
   public boolean getChecked(int i) {
      return checked.contains(new Integer(i));
   }

   /**
    *
    */
   public void setChecked(int i, boolean selected) {
      // selected is always true
      checked.add(new Integer(i));
   }

   /**
    *
    */
   public Collection getChecked() {
      return checked;
   }
}