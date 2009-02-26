/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.beans.om;

import java.util.Date;

import com.finalist.cmsc.beans.NodeBean;

/**
 * @author kevin shen
 */
@SuppressWarnings("serial")
public class AssetElement extends NodeBean {

   private String title;
   private String description;
   private Date creationdate;
   private Date lastmodifieddate;
   private Date publishdate;
   private String lastmodifier;

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

   public String getLastmodifier() {
      return lastmodifier;
   }

   public void setLastmodifier(String lastmodifier) {
      this.lastmodifier = lastmodifier;
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