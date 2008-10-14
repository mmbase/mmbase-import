package com.finalist.newsletter;

public class NewsletterSendFailException extends RuntimeException {
   public NewsletterSendFailException() {
   }

   public NewsletterSendFailException(String s) {
      super(s);
   }

   public NewsletterSendFailException(String s, Throwable throwable) {
      super(s, throwable);
   }

   public NewsletterSendFailException(Throwable throwable) {
      super(throwable);
   }
}
