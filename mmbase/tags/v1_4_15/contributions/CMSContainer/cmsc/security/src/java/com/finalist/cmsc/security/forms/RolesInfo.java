/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.security.forms;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.bridge.Node;

import com.finalist.tree.TreeInfo;

public class RolesInfo implements TreeInfo {

   protected List<Integer> openChannels = new ArrayList<Integer>();


   public void expand(Object o) {
      Integer number = null;
      if (o instanceof Node) {
         Node node = (Node) o;
         number = node.getNumber();
      }
      if (o instanceof Integer) {
         number = (Integer) o;
      }
      if (!openChannels.contains(number)) {
         openChannels.add(number);
      }
   }


   public void collapse(Object o) {
      Integer number = null;
      if (o instanceof Node) {
         Node node = (Node) o;
         number = node.getNumber();
      }
      if (o instanceof Integer) {
         number = (Integer) o;
      }

      if (openChannels.contains(number)) {
         openChannels.remove(new Integer(number));
      }
   }


   public boolean isOpen(Object o) {
      if (o instanceof Node) {
         Node node = (Node) o;
         return openChannels.contains(node.getNumber());
      }
      if (o instanceof Integer) {
         Integer integer = (Integer) o;
         return openChannels.contains(integer);
      }
      return false;
   }

}
