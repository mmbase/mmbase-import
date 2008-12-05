/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.beans.om;

import com.finalist.cmsc.beans.NodeBean;

/**
 * @author Wouter Heijke
 */
public class PopUpInfo extends NodeBean {

   private String windowname;

   private boolean resizable;

   private boolean scrollbars;

   private boolean statusbar;

   private boolean toolbars;

   private int width;

   private int height;

   private int left;

   private int top;


   public int getHeight() {
      return height;
   }


   public void setHeight(int height) {
      this.height = height;
   }


   public int getLeft() {
      return left;
   }


   public void setLeft(int left) {
      this.left = left;
   }


   public boolean isResizable() {
      return resizable;
   }


   public void setResizable(boolean resizable) {
      this.resizable = resizable;
   }


   public boolean isScrollbars() {
      return scrollbars;
   }


   public void setScrollbars(boolean scrollbars) {
      this.scrollbars = scrollbars;
   }


   public boolean isStatusbar() {
      return statusbar;
   }


   public void setStatusbar(boolean statusbar) {
      this.statusbar = statusbar;
   }


   public boolean isToolbars() {
      return toolbars;
   }


   public void setToolbars(boolean toolbars) {
      this.toolbars = toolbars;
   }


   public int getTop() {
      return top;
   }


   public void setTop(int top) {
      this.top = top;
   }


   public int getWidth() {
      return width;
   }


   public void setWidth(int width) {
      this.width = width;
   }


   public String getWindowname() {
      return windowname;
   }


   public void setWindowname(String windowname) {
      this.windowname = windowname;
   }
}
