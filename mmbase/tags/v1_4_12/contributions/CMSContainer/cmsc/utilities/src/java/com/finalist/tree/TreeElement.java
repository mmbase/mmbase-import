package com.finalist.tree;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeElement {

   protected String icon;
   protected String id;
   protected String name;
   protected String fragment;
   protected String link;
   protected String target;

   protected List<TreeOption> options = new ArrayList<TreeOption>();


   public TreeElement(String icon, String id, String name, String fragment) {
      this.icon = icon;
      this.id = id;
      this.name = name;
      this.fragment = fragment;
   }


   public TreeElement(String icon, String id, String name, String fragment, String link) {
      this.icon = icon;
      this.id = id;
      this.name = name;
      this.fragment = fragment;
      this.link = link;
   }


   public TreeElement(String icon, String id, String name, String fragment, String link, String target) {
      this.icon = icon;
      this.id = id;
      this.name = name;
      this.fragment = fragment;
      this.link = link;
      this.target = target;
   }


   /**
    * @param name
    *           The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }


   /**
    * @param link
    *           The link to set.
    */
   public void setLink(String link) {
      this.link = link;
   }


   /**
    * @param target
    *           The target to set.
    */
   public void setTarget(String target) {
      this.target = target;
   }


   public void addOption(TreeOption option) {
      if (option != null) {
         options.add(option);
      }
   }

}
