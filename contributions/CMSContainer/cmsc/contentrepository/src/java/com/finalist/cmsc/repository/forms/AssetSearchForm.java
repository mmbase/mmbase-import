package com.finalist.cmsc.repository.forms;

import com.finalist.cmsc.struts.PagerForm;

@SuppressWarnings("serial")
public class AssetSearchForm extends PagerForm {

   private String assettypes = "assetelement";
   private String expiredate = "0";
   private String creationdate = "0";
   private String lastmodifieddate = "0";
   private String publishdate = "0";
   private String parentchannel;
   private String personal;
   private String useraccount;
   private String title;
   private String objectid;
   private String mode = "basic";
   private String search = "true";
   private String parentchannelpath = "";
   private String searchShow = "list";
   private String insertAsset = "insertAsset";
   
   public String getAssettypes() {
      return assettypes;
   }

   public void setAssettypes(String contenttypes) {
      this.assettypes = contenttypes;
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

   public void setShow(String show) {
      this.searchShow = show;
   }

   public String getShow() {
      return searchShow;
   }

   public void setInsertAsset(String insertAsset) {
      this.insertAsset = insertAsset;
   }

   public String getInsertAsset() {
      return insertAsset;
   }
}
