package nl.teleacnot.mmbase.nebo;

import java.util.Map;
import java.util.HashMap;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.module.core.MMBase;
import org.mmbase.applications.email.*;

/**
 * To mail the results of a the import
 *
 * @author Andr\U00e9 vanToly &lt;andre@toly.nl&gt;
 */
public final class Mail {

    private static final Logger log = Logging.getLoggerInstance(Mail.class);

    SendMail sendmail = (SendMail) MMBase.getMMBase().getModule("sendmail");

    /* the email address that should recieve confirmations */
    public static String email_address = "webmaster@teleacnot.nl";
    /* the optional Cc address that should recieve confirmations */
    public static String ccmail_address = "";
    /* the location of the editor to relate t_streams to episodes */
    public static String editor = "http://edit.teleacnot.nl/mmbase/edit/my_editors/index.jsp";

    /**
     * Reads configuration
     *
     * @param configuration configuration properties
     *
     */
    synchronized static void readConfiguration(Map configuration) {
        String tmp = (String) configuration.get("email_address");
        if (tmp != null && !tmp.equals("")) {
            email_address = tmp;
            log.debug("Email address: " + email_address);
        }
        tmp = (String) configuration.get("ccmail_address");
        if (tmp != null && !tmp.equals("")) {
            ccmail_address = tmp;
            log.debug("Cc address: " + ccmail_address);
        }
        tmp = (String) configuration.get("editor");
        if (tmp != null && !tmp.equals("")) {
            editor = tmp;
            log.debug("Editor: " + editor);
        }
    }

    public Mail(Map configuration) {
        log.info("Configuring mail to send a confirmation...");
        readConfiguration(configuration);
    }

    /**
     * Creates and sends an e-mail message.
     *
     * @param   streams the list with fresh stream nodes that were imported
     */
    public void sendMail(NodeList streams) {
        String from = "webmaster@teleacnot.nl";
        String body = makeBody(streams);
        String subject = "Mailtje van de NEBO streams importer";
        HashMap headers = new HashMap();
        headers.put("Reply-To", "webmaster@teleacnot.nl");
        headers.put("CC", ccmail_address);
        headers.put("BCC", "");
        headers.put("Subject", subject);
        log.debug("Sendmail reply code : " + sendmail.sendMail(from, email_address, body, headers));
    }

    /**
     * Makes message body with list of imported streams. Also sends 0 streams imported message.
     *
     * @param   streams the list with fresh stream nodes that were imported
     * @return  the body of a confirmation mail with a list of the streams
     */
    private static String makeBody(NodeList streams) {
        StringBuilder sb = new StringBuilder();
        if (streams.size() > 0) {
            sb.append("De volgende ").append(streams.size()).append(" streams zijn geimporteerd:\n\n");
            NodeIterator ni = streams.nodeIterator();
    		while (ni.hasNext()) {
    		    Node stream = ni.nextNode();
    		    sb.append("* ").append(stream.getStringValue("title")).append("\n");
    		    sb.append("  http://edit.teleacnot.nl/mmbase/edit/my_editors/edit_object.jsp?nr=").append(stream.getNumber()).append("\n");
    		    sb.append("  http://player.omroep.nl/?aflID=").append(stream.getStringValue("id")).append("\n");
    		}

            sb.append("\nVia de editor op de volgende pagina is het mogelijk de streams aan de juiste afleveringen te relateren: ");
            sb.append(editor);
        } else {
            sb.append("Er zijn geen streams geimporteerd.");
        }

        sb.append("\n\n---Groetjes van je vriendelijke NEBO XML importer\n");
        return sb.toString();
    }

}
