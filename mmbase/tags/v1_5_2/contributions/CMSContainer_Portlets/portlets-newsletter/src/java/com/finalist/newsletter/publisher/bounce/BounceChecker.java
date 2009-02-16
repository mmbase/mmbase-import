package com.finalist.newsletter.publisher.bounce;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.io.IOException;
import java.net.ServerSocket;

import com.finalist.newsletter.services.NewsletterService;

/**
 * Listener thread, that accepts connection on port 25 (default) and
 * delegates all work to its worker threads. It is a minimum implementation,
 * it only implements commands listed in section 4.5.1 of RFC 2821.
 *
 * @author Mark Guo &lt;mark.guo@finalist.cn&gt;
 */
public class BounceChecker extends Thread {
   private Logger log = Logging.getLoggerInstance(BounceChecker.class.getName());

   private static int LISTENINGPORT = 25;
   private static boolean running = false;
   private static boolean stop = false;
   private NewsletterService newsletterService;

   public BounceChecker (NewsletterService newsletterService) {
      this.newsletterService = newsletterService;
   }
   public void run() {
      log.debug("Start Bounce checking .....");
      BounceChecker.running = true;

      try {
         ServerSocket server = new ServerSocket(LISTENINGPORT);

         while (!stop) {
            ReceiveThread receiveThread = new ReceiveThread(server.accept());
            receiveThread.setNewsletterService(newsletterService);
            log.debug("Got a connection,start a thread to check it");
            receiveThread.start();            
         }
      } catch (IOException e) {
         e.printStackTrace();
         log.error("Start Bounce checking failed", e);
      }
   }

   public boolean isRunning() {
      return running;
   }

   public void setRunning(boolean running) {
      BounceChecker.running = running;
   }


   public void setPort(int i) {
      LISTENINGPORT = i;
   }

   public static void shutdown() {
      BounceChecker.stop = true;
   }
}