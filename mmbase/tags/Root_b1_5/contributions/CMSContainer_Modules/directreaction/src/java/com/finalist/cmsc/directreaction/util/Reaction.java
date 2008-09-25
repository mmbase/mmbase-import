package com.finalist.cmsc.directreaction.util;

import java.util.Date;

public class Reaction {

   private int number;
   private String title;
   private String body;
   private String name;
   private String email;
   private Date creationdate;
   private String contenttitle;


   public Reaction(int number, String title, String body, String name, String email, Date creationdate,
         String contenttitle) {
      this.number = number;
      this.title = title;
      this.body = body;
      this.name = name;
      this.email = email;
      this.creationdate = creationdate;
      this.contenttitle = contenttitle;
   }


   public String getBody() {
      return body;
   }


   public String getEmail() {
      return email;
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


   public Date getCreationdate() {
      return creationdate;
   }


   public String getContentTitle() {
      return contenttitle;
   }
}
