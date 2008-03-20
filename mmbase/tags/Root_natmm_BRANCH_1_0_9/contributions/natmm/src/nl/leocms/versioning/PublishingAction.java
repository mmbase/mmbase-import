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

import org.mmbase.module.core.MMObjectNode;

/**
 * This defines an action which should be performed when a node 
 * from the publisingbuilder type is published 
 * 
 * @author Nico Klasens (Finalist IT Group)
 * @created 8-aug-2003
 * @version $Revision: 1.1 $
 */
public interface PublishingAction {

   /** Node inserted
    * 
    * @param nodenumber number of node
    */
   public void inserted(int nodenumber);
   
   /** Node changed / committed
    * 
    * @param node changed node
    */
   public void committed(MMObjectNode node);
   
   /** Node removed
    * 
    * @param node removed node
    */
   public void removed(MMObjectNode node);

}
