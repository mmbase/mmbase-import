package com.finalist.cmsc.favorites.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.favorites.util.Favorite;
import com.finalist.cmsc.favorites.util.FavoritesUtil;

public class GetTag extends SimpleTagSupport {

   private String user;
   private String var;


   public void doTag() throws JspException, IOException {
      List<Favorite> favorites = FavoritesUtil.getUserFavorites(user);
      getJspContext().setAttribute(var, favorites);
   }


   public void setUser(String user) {
      this.user = user;
   }


   public void setVar(String var) {
      this.var = var;
   }

}
