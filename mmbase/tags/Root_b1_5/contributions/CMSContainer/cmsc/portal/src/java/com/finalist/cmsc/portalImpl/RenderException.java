package com.finalist.cmsc.portalImpl;

@SuppressWarnings("serial")
public class RenderException extends RuntimeException {

   public RenderException(String message, Exception e) {
      super(message, e);
   }
}
