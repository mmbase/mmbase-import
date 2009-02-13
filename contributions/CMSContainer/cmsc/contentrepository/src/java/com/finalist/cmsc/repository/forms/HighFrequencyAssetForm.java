package com.finalist.cmsc.repository.forms;

import com.finalist.cmsc.struts.PagerForm;

@SuppressWarnings("serial")
public class HighFrequencyAssetForm extends PagerForm {

   private String channelid;
   private String assetShow;
   private String assettypes;
   
   public String getAssettypes() {
      return assettypes;
   }

   public void setAssettypes(String assettypes) {
      this.assettypes = assettypes;
   }

   public String getChannelid() {
      return channelid;
   }

   public void setChannelid(String channelid) {
      this.channelid = channelid;
   }

   public String getAssetShow() {
      return assetShow;
   }

   
   public void setAssetShow(String assetShow) {
      this.assetShow = assetShow;
   }

}
