package com.finalist.cmsc.services.versioning;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class VersioningException extends Exception {
   public VersioningException() {
      super();
   }


   public VersioningException(String message) {
      super(message);
   }


   public VersioningException(String message, Throwable cause) {
      super(message, cause);
   }


   public VersioningException(Throwable cause) {
      super(cause);
   }

}
