package nl.leocms.util;

import org.mmbase.bridge.*;

import java.util.*;


public class GenericNodeList extends Vector implements NodeList {
   private Map prop = new HashMap();

   public Node getNode(int index) {
      return (Node) get(index);
   }

   public NodeIterator nodeIterator() {
      return new GenericNodeIterator(listIterator());
   }

   public NodeList subNodeList(int fromIndex, int toIndex) {
      GenericNodeList b = new GenericNodeList();
      b.addAll(subList(fromIndex, toIndex));

      return b;
   }

   public Object getProperty(Object key) {
      return prop.get(key);
   }

   public void setProperty(Object key, Object val) {
      prop.put(key, val);
   }

   public void sort() {
      // do nothing..
   }

   public void sort(Comparator comp) {
      Collections.sort(this, comp);
   }
}
