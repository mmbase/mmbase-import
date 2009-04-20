/**
 * 
 */
package com.finalist.cmsc.beans;


/**
 * @author Billy
 * 
 */
public class SortedNodetypeBean extends NodetypeBean implements Comparable{

   public int compareTo(Object obj) {
      SortedNodetypeBean another = (SortedNodetypeBean) obj;
      return name.compareTo(another.name);
   }

}
