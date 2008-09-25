package com.finalist.cmsc.beans.om;

import com.finalist.cmsc.beans.NodeBean;

/**
 * @author Wouter Heijke
 */
public class EditWizards extends NodeBean {

   private String name;

   private String description;

   private String type;

   private String wizard;

   private String objectnumber;

   private String nodepath;

   private String fields;

   private String constraints;

   private int age;

   private String distinctlist;

   private String searchdir;

   private String orderby;

   private String directions;

   private int pagelength;

   private int maxpagecount;

   private int maxupload;

   private String searchfields;

   private String searchtype;

   private String searchvalue;

   private String search;

   private String origin;

   private String title;


   public int getAge() {
      return age;
   }


   public void setAge(int age) {
      this.age = age;
   }


   public String getConstraints() {
      return constraints;
   }


   public void setConstraints(String constraints) {
      this.constraints = constraints;
   }


   public String getDescription() {
      return description;
   }


   public void setDescription(String description) {
      this.description = description;
   }


   public String getDirections() {
      return directions;
   }


   public void setDirections(String directions) {
      this.directions = directions;
   }


   public String getDistinctlist() {
      return distinctlist;
   }


   public void setDistinctlist(String distinctlist) {
      this.distinctlist = distinctlist;
   }


   public String getFields() {
      return fields;
   }


   public void setFields(String fields) {
      this.fields = fields;
   }


   public int getMaxpagecount() {
      return maxpagecount;
   }


   public void setMaxpagecount(int maxpagecount) {
      this.maxpagecount = maxpagecount;
   }


   public int getMaxupload() {
      return maxupload;
   }


   public void setMaxupload(int maxupload) {
      this.maxupload = maxupload;
   }


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }


   public String getNodepath() {
      return nodepath;
   }


   public void setNodepath(String nodepath) {
      this.nodepath = nodepath;
   }


   public String getObjectnumber() {
      return objectnumber;
   }


   public void setObjectnumber(String objectnumber) {
      this.objectnumber = objectnumber;
   }


   public String getOrderby() {
      return orderby;
   }


   public void setOrderby(String orderby) {
      this.orderby = orderby;
   }


   public String getOrigin() {
      return origin;
   }


   public void setOrigin(String origin) {
      this.origin = origin;
   }


   public int getPagelength() {
      return pagelength;
   }


   public void setPagelength(int pagelength) {
      this.pagelength = pagelength;
   }


   public String getSearch() {
      return search;
   }


   public void setSearch(String search) {
      this.search = search;
   }


   public String getSearchdir() {
      return searchdir;
   }


   public void setSearchdir(String searchdir) {
      this.searchdir = searchdir;
   }


   public String getSearchfields() {
      return searchfields;
   }


   public void setSearchfields(String searchfields) {
      this.searchfields = searchfields;
   }


   public String getSearchtype() {
      return searchtype;
   }


   public void setSearchtype(String searchtype) {
      this.searchtype = searchtype;
   }


   public String getSearchvalue() {
      return searchvalue;
   }


   public void setSearchvalue(String searchvalue) {
      this.searchvalue = searchvalue;
   }


   public String getTitle() {
      return title;
   }


   public void setTitle(String title) {
      this.title = title;
   }


   public String getType() {
      return type;
   }


   public void setType(String type) {
      this.type = type;
   }


   public String getWizard() {
      return wizard;
   }


   public void setWizard(String wizard) {
      this.wizard = wizard;
   }
}
