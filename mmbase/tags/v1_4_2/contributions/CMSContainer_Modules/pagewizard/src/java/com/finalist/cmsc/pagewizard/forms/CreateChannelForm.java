package com.finalist.cmsc.pagewizard.forms;

import org.apache.struts.action.ActionForm;

@SuppressWarnings("serial")
public class CreateChannelForm extends ActionForm {

   public final static String ACTION_NEW = "new";
   public final static String ACTION_ADD_CONTENT = "add_content";
   public final static String ACTION_REMOVE_CONTENT = "remove_content";

   private String action;

   private int channelNumber;
   private int parentNumber;
   private String channelName;
   private int contentNumber;
   private String contentType;


   public String getAction() {
      return action;
   }


   public void setAction(String action) {
      this.action = action;
   }


   public String getChannelName() {
      return channelName;
   }


   public void setChannelName(String channelName) {
      this.channelName = channelName;
   }


   public int getChannelNumber() {
      return channelNumber;
   }


   public void setChannelNumber(int channelNumber) {
      this.channelNumber = channelNumber;
   }


   public int getParentNumber() {
      return parentNumber;
   }


   public void setParentNumber(int parentNumber) {
      this.parentNumber = parentNumber;
   }


   public int getContentNumber() {
      return contentNumber;
   }


   public void setContentNumber(int contentNumber) {
      this.contentNumber = contentNumber;
   }


   public String getContentType() {
      return contentType;
   }


   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

}
