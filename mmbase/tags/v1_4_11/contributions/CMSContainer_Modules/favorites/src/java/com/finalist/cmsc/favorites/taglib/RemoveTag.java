package com.finalist.cmsc.favorites.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.favorites.util.FavoritesUtil;

public class RemoveTag extends SimpleTagSupport {

   private int number;
   private String user;


   public void doTag() throws JspException, IOException {
      FavoritesUtil.removeFavorite(user, number);
   }


   public void setNumber(int number) {
      this.number = number;
   }


   public void setUser(String user) {
      this.user = user;
   }
}
