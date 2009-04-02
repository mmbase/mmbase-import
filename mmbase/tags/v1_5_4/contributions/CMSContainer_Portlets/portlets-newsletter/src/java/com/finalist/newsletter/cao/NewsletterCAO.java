package com.finalist.newsletter.cao;

import java.util.List;

import org.mmbase.bridge.Node;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Term;

public interface NewsletterCAO {
   public Newsletter getNewsletterById(int id);

   public List<Term> getALLTerm();

   public List<Newsletter> getNewsletterByConstraint(String property, String constraintType, String value, boolean paging);

   public int getNewsletterIdBySubscription(int id);

   public List<Term> getNewsletterTermsByName(int newsletterId, String name, boolean paging);

   Node getNewsletterNodeById(int newsletterId);

   public void processBouncesOfPublication(String publicationId, String userId, String bounceContent);

}
