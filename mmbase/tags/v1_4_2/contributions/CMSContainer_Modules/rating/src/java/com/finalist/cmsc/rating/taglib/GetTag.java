package com.finalist.cmsc.rating.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.rating.util.Rating;
import com.finalist.cmsc.rating.util.RatingUtil;

public class GetTag extends SimpleTagSupport {

   private int number;
   private String var;
   private String countVar;
   private String userVar;
   private String user;


   public void doTag() throws JspException, IOException {
      Rating rating = RatingUtil.getContentRating(number);
      getJspContext().setAttribute(var, new Float(rating.getRating()));
      if (countVar != null) {
         getJspContext().setAttribute(countVar, new Integer(rating.getCount()));
      }

      if (userVar != null && user != null) {
         int userRating = RatingUtil.getUserRating(number, user);
         getJspContext().setAttribute(userVar, new Integer(userRating));
      }
   }


   public void setCountVar(String countVar) {
      this.countVar = countVar;
   }


   public void setNumber(int number) {
      this.number = number;
   }


   public void setUser(String user) {
      this.user = user;
   }


   public void setUserVar(String userVar) {
      this.userVar = userVar;
   }


   public void setVar(String var) {
      this.var = var;
   }

}
