/**
 *
 */
package com.finalist.util.http;

/**
 * @author hes
 */
public class PageNotFoundException extends Exception {

   private static final long serialVersionUID = -6912379889672427961L;


   /**
    *
    */
   public PageNotFoundException() {
      super();
   }


   /**
    * @param message
    */
   public PageNotFoundException(String message) {
      super(message);
   }


   /**
    * @param message
    * @param cause
    */
   public PageNotFoundException(String message, Throwable cause) {
      super(message, cause);
   }


   /**
    * @param cause
    */
   public PageNotFoundException(Throwable cause) {
      super(cause);
   }

}
