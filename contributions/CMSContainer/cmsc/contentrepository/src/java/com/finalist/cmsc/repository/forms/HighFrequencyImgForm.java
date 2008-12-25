package com.finalist.cmsc.repository.forms;

import com.finalist.cmsc.struts.PagerForm;

@SuppressWarnings("serial")
public class HighFrequencyImgForm extends PagerForm {

   private String channelid;
   private String imageShow;

   public String getChannelid() {
      return channelid;
   }

   public void setChannelid(String channelid) {
      this.channelid = channelid;
   }

   public String getImageShow() {
      return imageShow;
   }

   public void setImageShow(String show) {
      this.imageShow = show;
   }

}
