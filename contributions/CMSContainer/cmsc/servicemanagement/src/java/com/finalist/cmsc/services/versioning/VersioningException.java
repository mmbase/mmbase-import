package com.finalist.cmsc.services.versioning;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 * @version $Revision: 1.1 $, $Date: 2006-12-12 09:38:20 $
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
