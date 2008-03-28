/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.beans.om;

import java.util.Date;

import net.sf.mmapps.commons.beans.NodeBean;

/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class ContentElement extends NodeBean {

   private String title;
   private String description;
   private Date creationdate;
   private Date lastmodifieddate;
   private Date publishdate;
   private Date expirydate;
   private boolean use_expiry;
   private String lastmodifier;
   private Date notificationdate;
   private String source;
   private String keywords;

   public ContentChannel contentchannels;
   public User user;


   public String getTitle() {
      return title;
   }


   public void setTitle(String title) {
      this.title = title;
   }


   public String getDescription() {
      return description;
   }


   public void setDescription(String description) {
      this.description = description;
   }


   public Date getCreationdate() {
      return creationdate;
   }


   public void setCreationdate(Date creationdate) {
      this.creationdate = creationdate;
   }


   public Date getLastmodifieddate() {
      return lastmodifieddate;
   }


   public void setLastmodifieddate(Date lastmodifieddate) {
      this.lastmodifieddate = lastmodifieddate;
   }


   public Date getPublishdate() {
      return publishdate;
   }


   public void setPublishdate(Date publishdate) {
      this.publishdate = publishdate;
   }


   public Date getExpirydate() {
      return expirydate;
   }


   public void setExpirydate(Date expirydate) {
      this.expirydate = expirydate;
   }


   public boolean isUse_expiry() {
      return use_expiry;
   }


   public void setUse_expiry(boolean use_expiry) {
      this.use_expiry = use_expiry;
   }


   public String getLastmodifier() {
      return lastmodifier;
   }


   public void setLastmodifier(String lastmodifier) {
      this.lastmodifier = lastmodifier;
   }


   public Date getNotificationdate() {
      return notificationdate;
   }


   public void setNotificationdate(Date notificationdate) {
      this.notificationdate = notificationdate;
   }


   public String getSource() {
      return source;
   }


   public void setSource(String source) {
      this.source = source;
   }


   public String getKeywords() {
      return keywords;
   }


   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }


   public ContentChannel getContentchannels() {
      return contentchannels;
   }


   public void setContentchannels(ContentChannel contentchannels) {
      this.contentchannels = contentchannels;
   }


   public User getUser() {
      return user;
   }


   public void setUser(User user) {
      this.user = user;
   }

}