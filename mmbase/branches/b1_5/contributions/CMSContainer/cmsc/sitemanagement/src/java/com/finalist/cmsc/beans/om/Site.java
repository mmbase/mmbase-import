/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.beans.om;

import com.finalist.cmsc.util.ServerUtil;

/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class Site extends Page {

   private String stagingfragment;
   private String language;
   private String creator;
   private String publisher;
   private String source;
   private String rights;
   private String googleanalyticsid;

   public String getStagingfragment() {
      return stagingfragment;
   }


   public void setStagingfragment(String stagingfragment) {
      this.stagingfragment = stagingfragment;
   }


   @Override
   public String getUrlfragment() {
      return ServerUtil.isLive() ? super.getUrlfragment() : stagingfragment;
   }


   public String getLanguage() {
      return language;
   }


   public void setLanguage(String language) {
      this.language = language;
   }


   public String getCreator() {
      return creator;
   }


   public void setCreator(String creator) {
      this.creator = creator;
   }


   public String getPublisher() {
      return publisher;
   }


   public void setPublisher(String publisher) {
      this.publisher = publisher;
   }


   public String getSource() {
      return source;
   }


   public void setSource(String source) {
      this.source = source;
   }


   public String getRights() {
      return rights;
   }


   public void setRights(String rights) {
      this.rights = rights;
   }

   public void setGoogleanalyticsid(String googleanalyticsid) {
      this.googleanalyticsid = googleanalyticsid;
   }

   public String getGoogleanalyticsid() {
      return googleanalyticsid;
   }
}
