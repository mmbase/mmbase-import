/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.struts;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.*;
import org.mmbase.bridge.Node;
import org.mmbase.storage.search.SortOrder;

@SuppressWarnings("serial")
public class PagerForm extends ActionForm {

   private String offset;
   private int resultCount;
   private String order;
   private int direction = 1;
   private List<Node> results = new ArrayList<Node>();


   public PagerForm() {
      this("title");
   }


   public PagerForm(String order) {
      this.order = order;
   }


   @Override
   public ActionErrors validate(ActionMapping actionMapping, javax.servlet.http.HttpServletRequest httpServletRequest) {
      // ensure valid direction
      if (direction != SortOrder.ORDER_DESCENDING) {
         direction = SortOrder.ORDER_ASCENDING;
      }

      return super.validate(actionMapping, httpServletRequest);
   }


   public List<Node> getResults() {
      return results;
   }


   public void setResults(List<Node> results) {
      this.results = results;
   }


   public String getOffset() {
      return offset;
   }


   public int getResultCount() {
      return resultCount;
   }


   public void setResultCount(int resultCount) {
      this.resultCount = resultCount;
   }


   public void setOffset(String offset) {
      this.offset = offset;
   }


   public String getOrder() {
      return order;
   }


   public void setOrder(String order) {
      this.order = order;
   }


   public int getDirection() {
      return direction;
   }


   public void setDirection(int direction) {
      this.direction = direction;
   }

}
