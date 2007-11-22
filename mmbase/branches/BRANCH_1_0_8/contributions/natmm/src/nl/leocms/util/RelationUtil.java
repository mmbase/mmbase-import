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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.bridge.RelationList;

/**
 * @author Edwin van der Elst Date :Oct 23, 2003
 *  
 */
public class RelationUtil {

   public static void reorderPosrel(Node parentNode, String childs, String role) {
      StringTokenizer tokenizer = new StringTokenizer(childs, ",");
      List tokens = new ArrayList();
      while (tokenizer.hasMoreTokens()) {
         tokens.add(tokenizer.nextToken());
      }
      RelationList list = parentNode.getRelations(role);
      RelationIterator iter = list.relationIterator();
      while (iter.hasNext()) {
         Relation rel = iter.nextRelation();
         int destination = rel.getDestination().getNumber();
         rel.setIntValue("pos", tokens.indexOf("" + destination));
         rel.commit();
      }
   }
}
