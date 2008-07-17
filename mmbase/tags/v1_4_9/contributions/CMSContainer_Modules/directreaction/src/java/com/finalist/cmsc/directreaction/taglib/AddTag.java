package com.finalist.cmsc.directreaction.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.directreaction.util.*;

public class AddTag extends SimpleTagSupport {

   private int number;
   private String title;
   private String body;
   private String name;
   private String email;
   private String link;


   public void doTag() throws JspException, IOException {
      ReactionUtil.addReaction(number, title, body, name, email, link);
   }


   public void setBody(String body) {
      this.body = body;
   }


   public void setEmail(String email) {
      this.email = email;
   }


   public void setLink(String link) {
      this.link = link;
   }


   public void setName(String name) {
      this.name = name;
   }


   public void setNumber(int number) {
      this.number = number;
   }


   public void setTitle(String title) {
      this.title = title;
   }
}
