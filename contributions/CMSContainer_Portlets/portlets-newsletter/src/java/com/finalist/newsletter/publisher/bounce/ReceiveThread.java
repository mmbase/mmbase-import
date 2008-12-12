package com.finalist.newsletter.publisher.bounce;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.io.*;
import java.net.Socket;

import com.finalist.newsletter.services.NewsletterService;

/**
 * Listener thread, that accepts connection on port 25 (default) and
 * delegates all work to its worker threads. It is a minimum implementation,
 * it only implements commands listed in section 4.5.1 of RFC 2821.
 *
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 * @author Mark guo &lt;mark.guo@finalist.cn&gt;
 */
public class ReceiveThread extends Thread {

   private Logger log = Logging.getLoggerInstance(BounceChecker.class.getName());

   private java.net.Socket socket;
   private BufferedReader reader = null;
   private BufferedWriter writer = null;
   private NewsletterService newsletterService;
   private String[] params;

   private SMTPSTATUS status = SMTPSTATUS.INIT;

   public ReceiveThread() {
   }

   public ReceiveThread(Socket socket) {
      this.socket = socket;
   }

   public void setNewsletterService(NewsletterService newsletterService) {
      this.newsletterService = newsletterService;
   }

   /**
    * The main run method of this thread. It will read data from the given
    * socket line by line, and it will call the parser for this data.
    */
   public void run() {
      // talk to the other party
      try {
         InputStream is = socket.getInputStream();
         OutputStream os = socket.getOutputStream();
         reader = new BufferedReader(new InputStreamReader(is));
         writer = new BufferedWriter(new OutputStreamWriter(os));
      } catch (IOException e) {
         e.printStackTrace();
         log.error("Exception while initializing inputstream to incoming SMTP connection: " + e);
      }

      try {
         writer.write("220 Service ready\r\n");
         writer.flush();

         while (status.getIndex() < SMTPSTATUS.FINISHED.getIndex()) {
            String line = reader.readLine();
            parseLine(line);
         }
      } catch (IOException e) {
         log.warn("Caught IOException: " + e);
      }

      try {
         reader.close();
         writer.close();
         socket.close();
      } catch (IOException e) {
         log.warn("Cannot cleanup my reader, writer or socket: " + e);
      }
   }

   public synchronized void start() {
      super.start();
   }

   /**
    * Parse input received from the client. This method has the following side-effects:
    * <ul>
    * <li> It can alter the 'state' variable
    * <li> It can read extra data from the 'reader'
    * <li> It can write data to the 'writer'
    * <li> It can add Nodes to the 'mailboxes' vector
    * </ul>
    *
    * @param line the input to process
    * @throws java.io.IOException when io operation fails.
    */
   private void parseLine(String line) throws IOException {
      log.debug("Parser line with content:" + line);

      SMTPSTATUS.Action action = status.change(line);

      if (null != action && null != action.getResponse()) {
         writer.write(action.getResponse());
         status = action.getStatus();
         writer.flush();
         return;
      }

      if (line.toUpperCase().startsWith("RCPT TO:")) {
         String recepient[] = parseAddress(line);
         if (recepient.length != 2) {
            writer.write("553 This user format is unknown here\r\n");
            writer.flush();
            return;
         }

         String username = recepient[0];
         if (username.startsWith("bounce-")) {
            params = username.replace("bounce-", "").split("=");
            newsletterService.processBouncesOfPublication(params[0], params[1]);
         }
         writer.write("250 Yeah, OK. Bring on the data!\r\n");
         writer.flush();
         return;
      }

      if (line.toUpperCase().startsWith("DATA")) {
         // start reading all the data, until the '.'
         writer.write("354 Enter mail, end with CRLF.CRLF\r\n");
         writer.flush();

         char[] last5chars = new char[5];

         int c;
         StringBuffer data = new StringBuffer();
         while (verifyEndSymbol(last5chars)) {
            while ((c = reader.read()) == -1) {
               try {
                  Thread.sleep(50);
               } catch (InterruptedException e) {
                  log.warn("Failed to read ", e);
               }
            }
            data.append((char) c);
            System.arraycopy(last5chars, 1, last5chars, 0, last5chars.length - 1);
            last5chars[last5chars.length - 1] = (char) c;
         }
         if (params != null) {
            newsletterService.processBouncesOfPublication(params[0], params[1], data.toString());
         }
         // ignore data for bounce message.
         writer.write("250 Ok, bounce was processed.\r\n");
         writer.flush();
         status = SMTPSTATUS.MAILFROM; // this seems wrong, but the paragraph below does this too

         return;
      }

      writer.write("503 Sorry, sadfasdbut I have no idea what you mean.\r\n");
      writer.flush();
   }

   private boolean verifyEndSymbol(char[] last5chars) {

      boolean isreading;
      char[] endchars = {'\r', '\n', '.', '\r', '\n'};
      isreading = false;
      for (int i = 0; i < last5chars.length; i++) {
         if (last5chars[i] != endchars[i]) {
            isreading = true;
            break;
         }
      }
      return isreading;
   }

   /**
    * Interrupt method, is called only during shutdown
    */
   public void interrupt() {
      log.info("Interrupt() called");
   }

   /**
    * Parse a string of addresses, which are given in an RCPT TO: or MAIL FROM:
    * line by the client. This is a strict RFC implementation.
    *
    * @param address the address to parser
    * @return an array of strings, the first element contains the username, the second element is the domain
    */
   private String[] parseAddress(String address) {
      address = address.substring(8, address.length());
      if (address == null) {
         return new String[0];
      }

      if (address.equals("<>")) {
         return new String[0];
      }

      int leftbracket = address.indexOf("<");
      int rightbracket = address.indexOf(">");
      int colon = address.indexOf(":");

      // If we have source routing, we must ignore everything before the colon
      if (colon > 0) {
         leftbracket = colon;
      }

      // if the left or right brackets are not supplied, we MAY bounce the message. We
      // however try to parse the address still

      if (leftbracket < 0) {
         leftbracket = 0;
      }
      if (rightbracket < 0) {
         rightbracket = address.length();
      }

      // Trim off any whitespace that may be left
      String finaladdress = address.substring(leftbracket, rightbracket).trim();
      int atsign = finaladdress.indexOf("@");
      if (atsign < 0) {
         return new String[0];
      }

      String[] retval = new String[2];
      retval[0] = finaladdress.substring(0, atsign);
      retval[1] = finaladdress.substring(atsign + 1, finaladdress.length());
      return retval;
   }

}
