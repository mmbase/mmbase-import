package com.finalist.cmsc.directreaction.util;

public class Reaction {

   private int number;
   private String title;
   private String body;
   private String name;
   private String email;
   private String link;
   
   public Reaction(int number, String title, String body, String name, String email, String link) {
      this.number = number;
      this.title = title;
      this.body = body;
      this.name = name;
      this.email = email;
      this.link = link;
   }
   public String getBody() {
      return body;
   }
   public String getEmail() {
      return email;
   }
   public String getLink() {
      return link;
   }
   public String getName() {
      return name;
   }
   public int getNumber() {
      return number;
   }
   public String getTitle() {
      return title;
   }
   
   
}
