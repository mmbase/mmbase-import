package com.finalist.newsletter.publisher;

public enum MIMEType {
   HTML ("text/html"),
   PLAINTEXT ("text/plain");

   private String type;

   MIMEType(String type) {
      this.type = type;
   }

   public String type(){
      return type;
   }
}
