package com.finalist.cmsc.repository.forms;

import com.finalist.cmsc.struts.PagerForm;

@SuppressWarnings("serial")
public class SearchForm extends PagerForm {

   private String contenttypes = "contentelement";
   private String expiredate = "0";
   private String creationdate = "0";
   private String lastmodifieddate = "0";
   private String publishdate = "0";
   private String parentchannel;
   private String linktochannel;
   private String personal;
   private String useraccount;
   private String title;
   private String keywords;
   private String objectid;
   private String mode = "basic";
   private String search = "true";
   private String parentchannelpath = "";


   public String getContenttypes() {
      return contenttypes;
   }


   public void setContenttypes(String contenttypes) {
      this.contenttypes = contenttypes;
   }


   public String getExpiredate() {
      return expiredate;
   }


   public void setExpiredate(String expiredate) {
      this.expiredate = expiredate;
   }


   public String getCreationdate() {
      return creationdate;
   }


   public void setCreationdate(String creationdate) {
      this.creationdate = creationdate;
   }


   public String getLastmodifieddate() {
      return lastmodifieddate;
   }


   public void setLastmodifieddate(String lastmodifieddate) {
      this.lastmodifieddate = lastmodifieddate;
   }


   public String getPublishdate() {
      return publishdate;
   }


   public void setPublishdate(String publishdate) {
      this.publishdate = publishdate;
   }


   public String getParentchannel() {
      return parentchannel;
   }


   public void setParentchannel(String parentchannel) {
      this.parentchannel = parentchannel;
   }


   public String getLinktochannel() {
      return linktochannel;
   }


   public void setLinktochannel(String linktochannel) {
      this.linktochannel = linktochannel;
   }


   public String getTitle() {
      return title;
   }


   public void setTitle(String title) {
      this.title = title;
   }


   public String getObjectid() {
      return objectid;
   }


   public void setObjectid(String objectid) {
      this.objectid = objectid;
   }


   public String getPersonal() {
      return personal;
   }


   public void setPersonal(String personal) {
      this.personal = personal;
   }


   public String getUseraccount() {
      return useraccount;
   }


   public void setUseraccount(String useraccount) {
      this.useraccount = useraccount;
   }


   public String getMode() {
      return mode;
   }


   public void setMode(String mode) {
      this.mode = mode;
   }


   public String getSearch() {
      return search;
   }


   public void setSearch(String search) {
      this.search = search;
   }


   public String getParentchannelpath() {
      return parentchannelpath;
   }


   public void setParentchannelpath(String parentchannelpath) {
      this.parentchannelpath = parentchannelpath;
   }


   public String getKeywords() {
      return keywords;
   }


   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }

}
