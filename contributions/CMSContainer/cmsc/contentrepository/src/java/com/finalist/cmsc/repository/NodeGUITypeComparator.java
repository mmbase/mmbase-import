package com.finalist.cmsc.repository;

import java.text.CollationKey;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Comparator;
import java.util.Locale;

import org.mmbase.bridge.Node;

/**
 * This class implements the Comparator interface for comparing nodes' GUI type name.
 *
 * @author Marco
 *
 */
public class NodeGUITypeComparator implements Comparator<Node> {

   private Collator collator;
   private boolean reverse;

   public NodeGUITypeComparator(boolean reverse) {
      this.collator = (RuleBasedCollator)Collator.getInstance();
      this.reverse = reverse;
   }
   
   public NodeGUITypeComparator(Locale locale, boolean reverse){
      collator = (RuleBasedCollator)Collator.getInstance(locale);
      this.reverse = reverse;
   }
   
   public int compare(Node node1, Node node2) {
       CollationKey key1 = collator.getCollationKey(node1.getNodeManager().getGUIName());
       CollationKey key2 = collator.getCollationKey(node2.getNodeManager().getGUIName());
       return reverse? -key1.compareTo(key2): key1.compareTo(key2);
   }
   
}
