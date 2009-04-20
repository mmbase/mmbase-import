/**
 * 
 */
package com.finalist.cmsc.typedef.forms;


/**
 * @author Billy
 * 
 */
public class Typedef implements Comparable{

   private int number;

   private String name;

   public Typedef(int number, String name) {
      this.number = number;
      this.name = name;
   }

   public int getNumber() {
      return number;
   }

   public void setNumber(int number) {
      this.number = number;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int compareTo(Object obj) {
      Typedef another = (Typedef) obj;
      return name.compareTo(another.name);
   }

}
