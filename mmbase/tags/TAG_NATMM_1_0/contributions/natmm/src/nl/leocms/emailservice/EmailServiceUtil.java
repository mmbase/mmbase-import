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
package nl.leocms.emailservice;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.mmbase.util.CloudFactory;

/**
 * @author Ronald Kramp
 * Date :Oct 20, 2003
 * 
 */
public class EmailServiceUtil {

   /** Logger instance. */
   private static Logger log = Logging.getLoggerInstance(EmailServiceUtil.class.getName());

   /**
    * Removes the maillijst item
    *
    * @param mailLijstNumber
    */
   public static void removeMailLijstItem(String artikelNumber) {
      Cloud cloud = CloudFactory.getCloud();
      Node artikelNode = cloud.getNode(artikelNumber);
      artikelNode.deleteRelations("maillijst");
   }
}
