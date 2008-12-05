package com.finalist.cmsc.favorites.taglib;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.favorites.util.FavoritesUtil;

public class RemoveTag extends SimpleTagSupport {

   private int number;
   private String user;


   public void doTag() {
      FavoritesUtil.removeFavorite(user, number);
   }


   public void setNumber(int number) {
      this.number = number;
   }


   public void setUser(String user) {
      this.user = user;
   }
}
