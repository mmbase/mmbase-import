package org.mmbase.bridge.util;

import org.mmbase.bridge.Node;

import java.util.Comparator;

/**
 * Comparator two nodes based on the string value in the specified field.
 */
public class NodeFieldComparator implements Comparator<Node> {
   protected String field;
   protected boolean ascending = true;

   /**
    * Basic constructor.
    *
    * @param field the node field to compare the nodes on
    */
   public NodeFieldComparator(String field) {
      this(field, true);
   }

   /**
    * Basic constructor.
    *
    * @param field the node field to compare the nodes on
    * @param ascending sort the list ascending or descending
    */
   public NodeFieldComparator(String field, boolean ascending) {
      this.field = field;
      this.ascending = ascending;
   }

   /**
    * Two external source objects are said to be equal only when the two type fields match. The
    * objects to compare can be of type <code>Node</code>.
    *
    * @param n1 the first  node.
    * @param n2 the second  node
    *
    * @return a negative integer, zero, or a positive integer as the first argument is less than,
    *         equal to, or greater than the second.
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   public int compare(Node n1, Node n2) {
       int result = 0;

       Object f1 = n1.getValue(field);
       Object f2 = n2.getValue(field);       
       if (f1 instanceof Comparable) {
           result = ((Comparable)f1).compareTo(f2);
       }
       else {
           result = n1.compareTo(n2);
       }

      if (ascending) {
         return result;
      } else {
         return -result;
      }
   }
}