package com.finalist.cmsc.repository.forms;

import com.finalist.cmsc.struts.PagerForm;

@SuppressWarnings("serial")
public class HighFrequencyForm extends PagerForm {

   private String channelid;

   public String getChannelid() {
      return channelid;
   }

   public void setChannelid(String channelid) {
      this.channelid = channelid;

   }

}
