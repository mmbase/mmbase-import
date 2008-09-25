package com.finalist.cmsc.portalImpl.headerresource;

public abstract class HeaderResource {

   private boolean dublin;


   public HeaderResource(boolean dublin) {
      this.dublin = dublin;
   }


   public boolean isDublin() {
      return dublin;
   }


   public abstract void render(StringBuffer buffer);
}
