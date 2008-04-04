/*
 * Created on Aug 26, 2004
 *
 */
package com.finalist.tree;

public abstract class TreeOption {

   protected String icon;
   protected String title;
   protected String link;
   protected String target;
   protected boolean showLabel = false;


   public TreeOption(String icon, String title, String link) {
      this.icon = icon;
      this.title = title;
      this.link = link;
   }


   public TreeOption(String icon, String title, String link, boolean showLabel) {
      this.icon = icon;
      this.title = title;
      this.link = link;
      this.showLabel = showLabel;
   }


   public TreeOption(String icon, String title, String link, String target) {
      this.icon = icon;
      this.title = title;
      this.link = link;
      this.target = target;
   }


   public TreeOption(String icon, String title, String link, String target, boolean showLabel) {
      this.icon = icon;
      this.title = title;
      this.link = link;
      this.target = target;
      this.showLabel = showLabel;
   }


   public String getLink() {
      return link;
   }


   public String getIcon() {
      return icon;
   }


   public String getTitle() {
      return title;
   }
}
