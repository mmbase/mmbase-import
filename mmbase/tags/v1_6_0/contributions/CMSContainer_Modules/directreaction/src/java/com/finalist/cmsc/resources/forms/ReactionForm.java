package com.finalist.cmsc.resources.forms;

@SuppressWarnings("serial")
public class ReactionForm extends SearchForm {

   private String title;
   private String email;
   private String name;
   private String body;


   public ReactionForm() {
      super("reaction");
   }


   public String getTitle() {
      return title;
   }


   public void setTitle(String title) {
      this.title = title;
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


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }

}
