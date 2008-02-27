package com.finalist.newsletter.generator;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.newsletter.publisher.NewsletterPublisher;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;

public class NewsletterGeneratorFactory {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublisher.class.getName());
   private static NewsletterGeneratorFactory instance;

   public static final String AVAILABLE_MIMETYPES = "mimetypeoptions";
   public static final String MIMETYPE_HTML = "text/html";
   public static final String MIMETYPE_PLAIN = "text/plain";

   private static List<String> mimeTypes = new ArrayList<String>();

   public static final String MIMETYPE_DEFAULT = MIMETYPE_HTML;

   static {
      mimeTypes.add(MIMETYPE_HTML);
      mimeTypes.add(MIMETYPE_PLAIN);
   }

   public static NewsletterGeneratorFactory getInstance() {
      if (instance == null) {
         instance = new NewsletterGeneratorFactory();
      }
      return instance;
   }

   public static List<String> getMimeTypes() {
      return mimeTypes;
   }

   private NewsletterGeneratorFactory() {

   }

   public NewsletterGenerator getNewsletterGenerator(int publicationNumber, String mimeType) {
      if (mimeType.equals(NewsletterSubscriptionUtil.MIMETYPE_HTML)) {
         return (new NewsletterGeneratorHtml(publicationNumber));
      } else if (mimeType.equals(NewsletterSubscriptionUtil.MIMETYPE_PLAIN)) {
         return (new NewsletterGeneratorPlain(publicationNumber));
      } else {
         log.debug("No NewsletterGenerator returned because of unsupported mimetype");
         return (null);
      }
   }
}