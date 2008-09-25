/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.tree;

public class Tree {

   private TreeModel model;
   private TreeInfo info;
   private String imgBaseUrl = "editors/gfx/";


   public Tree(TreeModel model, TreeInfo info) {
      this.model = model;
      this.info = info;
   }


   public String getImgBaseUrl() {
      return imgBaseUrl;
   }


   public TreeModel getModel() {
      return model;
   }


   public TreeInfo getInfo() {
      return info;
   }


   public void setImgBaseUrl(String imgBaseUrl) {
      this.imgBaseUrl = imgBaseUrl;
   }


   protected String buildImgUrl(String image) {
      return getImgBaseUrl() + image;
   }

}
