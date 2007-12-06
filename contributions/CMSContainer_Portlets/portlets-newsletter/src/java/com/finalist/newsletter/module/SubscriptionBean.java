package com.finalist.newsletter.module;

import java.util.List;

import com.finalist.newsletter.util.NewsletterSubscriptionUtil;

public class SubscriptionBean {

   private String name = "";
   private int themes = 0;
   private int newsletters = 0;
   private List<String> themeList;

   public SubscriptionBean() {

   }

   public String getName() {
      return name;
   }

   public int getNewsletters() {
      return newsletters;
   }

   public int getThemes() {
      return themes;
   }

   public void setName(String name) {
      this.name = name;
      themeList = NewsletterSubscriptionUtil.getUserSubscribedThemes(name);
      if (themeList != null) {
         themes = themeList.size();
      }

   }

}
