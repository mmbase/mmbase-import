package com.finalist.cmsc.egemmail.forms;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

@SuppressWarnings("serial")
public class EgemSearchForm extends ActionForm {

   private String author;
   private String keywords;
   private String page;
   private String title;
   private boolean limitToLastWeek = true;
   private boolean selectResults = true;
   private final Set<Integer> nodesOnScreen = new HashSet<Integer>();


   public String getAuthor() {
      return author;
   }


   public String getKeywords() {
      return keywords;
   }


   public Set<Integer> getNodesOnScreen() {
      return nodesOnScreen;
   }


   public String getPage() {
      return page;
   }


   public String getTitle() {
      return title;
   }


   public boolean isLimitToLastWeek() {
      return limitToLastWeek;
   }


   public boolean isSelectResults() {
      return selectResults;
   }


   /*
    * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
    *      javax.servlet.http.HttpServletRequest)
    */
   @Override
   public void reset(ActionMapping mapping, HttpServletRequest request) {
      limitToLastWeek = false;
      selectResults = false;

      super.reset(mapping, request);
   }


   public void setAuthor(String author) {
      this.author = author;
   }


   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }


   public void setLimitToLastWeek(boolean lastWeek) {
      this.limitToLastWeek = lastWeek;
   }


   public void setPage(String page) {
      this.page = page;
   }


   public void setSelectResults(boolean selectResults) {
      this.selectResults = selectResults;
   }


   public void setTitle(String title) {
      this.title = title;
   }
}
