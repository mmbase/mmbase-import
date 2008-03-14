package com.finalist.newsletter.module.bean;

import java.util.List;

public class NewsletterDetailBean {

   private int number;
   private String title;
   private List<NewsletterSubscriberBean> subscribers;

   public NewsletterDetailBean() {

   }

   /**
    * @return the number
    */
   public int getNumber() {
      return number;
   }

   /**
    * @return the subscribers
    */
   public List<NewsletterSubscriberBean> getSubscribers() {
      return subscribers;
   }

   /**
    * @return the title
    */
   public String getTitle() {
      return title;
   }

   /**
    * @param number
    *           the number to set
    */
   public void setNumber(int number) {
      this.number = number;
   }

   /**
    * @param subscribers
    *           the subscribers to set
    */
   public void setSubscribers(List<NewsletterSubscriberBean> subscribers) {
      this.subscribers = subscribers;
   }

   /**
    * @param title
    *           the title to set
    */
   public void setTitle(String title) {
      this.title = title;
   }

}
