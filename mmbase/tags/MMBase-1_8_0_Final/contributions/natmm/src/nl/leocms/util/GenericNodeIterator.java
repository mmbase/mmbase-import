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

import org.mmbase.bridge.*;

import java.util.*;


public class GenericNodeIterator implements NodeIterator {
   ListIterator iter;

   public GenericNodeIterator(ListIterator iter) {
      this.iter = iter;
   }

   public boolean hasNext() {
      return iter.hasNext();
   }

   public Object next() {
      return iter.next();
   }

   public boolean hasPrevious() {
      return iter.hasPrevious();
   }

   public Object previous() {
      return iter.previous();
   }

   public int nextIndex() {
      return iter.nextIndex();
   }

   public int previousIndex() {
      return iter.previousIndex();
   }

   public void set(Object o) {
      iter.set(o);
   }

   public void add(Object o) {
      iter.add(o);
   }

   public void remove() {
      iter.remove();
   }

   public Node nextNode() {
      return (Node) iter.next();
   }

   public Node previousNode() {
      return (Node) iter.previous();
   }
}
