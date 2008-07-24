package com.finalist.newsletter.cao;

import java.util.List;
import java.util.Set;

import org.mmbase.bridge.Node;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Term;

public interface NewsletterCAO {
   public Newsletter getNewsletterById(int id);

   public List<Term> getALLTerm();

   public List<Newsletter> getNewsletterByConstraint(String property, String constraintType, String value);

   public int getNewsletterIdBySubscription(int id);

   public List<Term> getNewsletterTermsByName(int newsletterId, String name, int pagesize, int offset, String order, String direction);

   public int getNewsletterTermsCountByName(int newsletterId, String name);

   Node getNewsletterNodeById(int newsletterId);
}
