package com.finalist.cmsc.repository.forms;

import com.finalist.cmsc.struts.PagerForm;

@SuppressWarnings("serial")
public class HighFrequencyImgForm extends PagerForm {

   private String channelid;
   private String show;

   public String getChannelid() {
      return channelid;
   }

   public void setChannelid(String channelid) {
      this.channelid = channelid;
   }

   public String getShow() {
      return show;
   }

   public void setShow(String show) {
      this.show = show;
   }

}
