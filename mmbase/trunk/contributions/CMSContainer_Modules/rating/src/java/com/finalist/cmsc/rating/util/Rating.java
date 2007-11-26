package com.finalist.cmsc.rating.util;

public class Rating {

   /** The average rating */
   private float rating;

   /** The number of people who rated */
   private int count;


   public Rating(float rating, int count) {
      this.rating = rating;
      this.count = count;
   }


   public int getCount() {
      return count;
   }


   public float getRating() {
      return rating;
   }

}
