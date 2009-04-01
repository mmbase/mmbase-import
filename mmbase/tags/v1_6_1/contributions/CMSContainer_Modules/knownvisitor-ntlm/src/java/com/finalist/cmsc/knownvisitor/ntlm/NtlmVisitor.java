package com.finalist.cmsc.knownvisitor.ntlm;

import com.finalist.cmsc.knownvisitor.Visitor;

public class NtlmVisitor extends Visitor {

   private String identifier;
   private String displayName;
   private String email;


   public String getIdentifier() {
      return identifier;
   }


   public String getDisplayName() {
      return displayName;
   }


   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }


   public void setIdentifier(String identifier) {
      this.identifier = identifier;
   }


   public String getEmail() {
      return email;
   }


   public void setEmail(String email) {
      this.email = email;
   }

}
