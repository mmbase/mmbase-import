package com.finalist.newsletter.cao.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Term;

public class NewsletterSubscriptionUtil {
   public static Newsletter populateNewsletter(Node node, Newsletter newsletter) {
      newsletter.setTitle(node.getStringValue("title"));
      return newsletter;
   }

   public static Newsletter convertNodeListtoTermList(NodeList list, Newsletter newsletter) {
      Iterator<Node> nodelist = list.iterator();
      Set<Term> termSet = new HashSet<Term>();

      for (int j = 0; j < list.size(); j++) {
         Term term = new Term();
         Node node = nodelist.next();
         term.setName(node.getStringValue("name"));
         termSet.add(term);
      }
      newsletter.setTerms(termSet);
      return newsletter;
   }

}
