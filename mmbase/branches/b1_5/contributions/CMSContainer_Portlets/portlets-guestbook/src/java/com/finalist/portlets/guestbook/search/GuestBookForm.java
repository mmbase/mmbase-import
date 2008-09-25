package com.finalist.portlets.guestbook.search;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mmbase.storage.search.SortOrder;

import com.finalist.cmsc.resources.forms.SearchForm;

@SuppressWarnings("serial")
public class GuestBookForm extends SearchForm {

   private String name;
   private String email;
   private String title;
   private String body;
   private boolean isRemote;


   public GuestBookForm() {
      // empty
   }


   public GuestBookForm(String contenttypes) {
      super(contenttypes);
   }


   public ActionErrors validate(ActionMapping actionMapping, javax.servlet.http.HttpServletRequest httpServletRequest) {
      // ensure valid direction
      if (getDirection() != SortOrder.ORDER_DESCENDING) {
         setDirection(SortOrder.ORDER_ASCENDING);
      }

      // set default order field
      if (StringUtils.isEmpty(getOrder())) {
         setOrder("title");
      }

      return super.validate(actionMapping, httpServletRequest);
   }


   public String getBody() {
      return body;
   }


   public void setBody(String body) {
      this.body = body;
   }


   public String getEmail() {
      return email;
   }


   public void setEmail(String email) {
      this.email = email;
   }


   public boolean isRemote() {
      return isRemote;
   }


   public void setRemote(boolean isRemote) {
      this.isRemote = isRemote;
   }


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }


   public String getTitle() {
      return title;
   }


   public void setTitle(String title) {
      this.title = title;
   }

}
