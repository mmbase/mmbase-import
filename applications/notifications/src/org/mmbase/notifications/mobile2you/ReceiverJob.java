/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications.mobile2you;
import org.mmbase.applications.crontab.AbstractCronJob;
import org.mmbase.bridge.*;
import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This jobs connects with mobile2you and receives e.g. <em>received</em> SMS-messages. It is
 * implemented as an MMBase CronJob, and should be scheduled regularly, e.g. once every 5 minutes.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: ReceiverJob.java,v 1.1 2007-10-15 13:52:55 michiel Exp $
 **/
public  class ReceiverJob extends AbstractCronJob {

    private static final Logger log = Logging.getLoggerInstance(ReceiverJob.class);

    public void run() {
        try {
            URL url = new URL(cronEntry.getConfiguration());
            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();
            InputSource input = new InputSource(is);

            XMLReader xr = XMLReaderFactory.createXMLReader();
            Handler handler = new Handler();
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);

            xr.parse(input);

            is.close();
        } catch (MalformedURLException mfue) {
            log.error(mfue);
        } catch (IOException ioe) {
            log.error(ioe);
        } catch (SAXException se) {
            log.error(se);
        }

    }
    // TODO
    public static class Handler extends DefaultHandler {
        public void startElement (String uri, String name,
                                  String qName, Attributes atts) {
        }
        public void endElement (String uri, String name, String qName) {

        }
    }



}
