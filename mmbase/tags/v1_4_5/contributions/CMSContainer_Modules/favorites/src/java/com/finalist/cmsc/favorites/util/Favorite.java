package com.finalist.cmsc.favorites.util;

public class Favorite {

   private int number;
   private String name;
   private String url;


   public Favorite(int number, String name, String url) {
      this.number = number;
      this.name = name;
      this.url = url;
   }


   public String getName() {
      return name;
   }


   public int getNumber() {
      return number;
   }


   public String getUrl() {
      return url;
   }

}
