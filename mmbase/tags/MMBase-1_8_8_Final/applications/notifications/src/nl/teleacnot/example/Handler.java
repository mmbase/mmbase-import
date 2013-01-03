package nl.teleacnot.example;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.util.regex.*;
import org.mmbase.sms.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * THIS IS AN EXAMPLE. THIS CODE IS COPIED FROM TELEAC/NOT AND PROBABLY IS SPECIFIC FOR THEM.
 *
 * This SMS receival handler is the counterpart of the {@link Validator} processor. It will parse
 * the received SMS, and currently do the following:
 *
 * It will check if this SMS is indeed targeted at us (starts with {@link #getPrefix}). If so, it
 * will check if it is a phone number validation request (the remaining matches {@link
 * #VALIDATION}). If so, it will check the correctness of it, and if so, the phone number will be
 * set and sent a SMS back containing the 'sms_succeeded' message.
 *
 * In all other cases the message is ignored.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 **/
public class Handler implements org.mmbase.sms.Handler {

    private static final Logger log = Logging.getLoggerInstance(Handler.class);

    protected static final Pattern VALIDATION = Pattern.compile("(?ms)\\s*C(.+?)(?:\\s|$).*");

    private String prefix = "";

    public void setPrefix(String p) {
        prefix = p;
    }

    /**
     * Prefix of SMS messages, distinghuishing them as for teleac. E.g. 'TELEAC' when using
     * 3669. Can be empty (which is the default) if Teleac has its own number. Set in
     * sms_handlers.xml as an entry in this class.
     */
    public String getPrefix() {
        return prefix;
    }

    public boolean handle(Cloud cloud, org.mmbase.sms.SMS sms) {
        String message = sms.getMessage().toUpperCase().trim();
        if (message.length() > prefix.length() && message.substring(0, prefix.length()).equals(prefix)) {
            message = message.substring(prefix.length());
            log.service("Teleac handling of SMS from " + sms.getMobile());
            if (log.isDebugEnabled()) {
                log.debug("Message: " + sms);
            }
            Matcher m = VALIDATION.matcher(message);
            if (m.matches()) {
                String validationCode = m.group(1);
                // mobile confirmation, find matching people object
                NodeManager people = cloud.getNodeManager("people");
                NodeQuery query = people.createQuery();
                Queries.addConstraint(query, Queries.createConstraint(query, "mobile", FieldCompareConstraint.EQUAL, validationCode.toLowerCase() + ":" + sms.getMobile()));
                NodeList results = people.getList(query);
                if (results.size() == 0) {
                    log.warn("Got unrecognized confirmation SMS " + sms + " (code " + validationCode + ") " + query.toSql());
                } else if (results.size() > 1) {
                    log.warn("Confirmation SMS " + sms + " matched more then 1 people node");
                }
                NodeIterator i = results.nodeIterator();
                while (i.hasNext()) {
                    Node person = i.nextNode();
                    person.setValueWithoutProcess("mobile", sms.getMobile());
                    person.setValueWithoutProcess("mobile_operator", sms.getOperator());
                    person.commit();
                    log.service("Validated phone number " + sms.getMobile() + " of person " + person.getNumber());
                    ResourceBundle bundle = ResourceBundle.getBundle("nl.teleacnot.users", cloud.getLocale());
                    Node chatter = person.getFunctionValue("user", null).toNode();
                    if (chatter == null) {
                        log.warn("Could not find chatter node with " + person.getNumber());
                    }
                    String success = MessageFormat.format(bundle.getString("sms_succeeded"),
                                                          person.getStringValue("firstname"),
                                                          person.getStringValue("lastname"),
                                                          chatter != null ? chatter.getStringValue("username") : "???",
                                                          chatter != null ? chatter.getStringValue("password") : "???");
                    org.mmbase.sms.Sender.getInstance().send(new BasicSMS(sms.getMobile(), sms.getOperator(), success));
                }
                return true;
            } else {
                org.mmbase.util.transformers.CharTransformer e = new org.mmbase.util.transformers.UnicodeEscaper();
                log.service("Could not understand message " + e.transform(message) + " (does not match " + VALIDATION + ")");
                return false;
            }
        } else {
            log.warn("Could not understand message (not for Teleac) " + message);
            return false;
        }

    }

    public static void main(String[] argv) {
        String message = "TELEAC CDUW\n-\nmichiel".toUpperCase().trim();
        String prefix = "TELEAC ";
        if (message.length() > prefix.length() && message.substring(0, prefix.length()).equals(prefix)) {
            message = message.substring(prefix.length());

            Matcher m = VALIDATION.matcher(message);
            org.mmbase.util.transformers.CharTransformer e = new org.mmbase.util.transformers.UnicodeEscaper();
            System.out.println("" + e.transform(message) + " matches  " + VALIDATION + ": " + m.matches());
        }
    }
}

