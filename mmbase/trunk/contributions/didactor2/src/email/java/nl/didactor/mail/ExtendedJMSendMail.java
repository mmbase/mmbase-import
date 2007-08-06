package nl.didactor.mail;
import javax.mail.internet.*;
import javax.mail.*;
import javax.mail.util.ByteArrayDataSource;
import javax.activation.*;
import javax.naming.*;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.applications.email.SendMail;
import org.mmbase.module.smtp.SMTPModule;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;

/**
 * Module providing mail functionality based on JavaMail, mail-resources.
 * Extended by Johannes Verelst to allow attachments to be sent.
 *
 * @todo This class now equals to its super, so it can be dropped.
 *
 * @author Case Roole
 * @author Michiel Meeuwissen
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 * @since  MMBase-1.6
 * @version $Id: ExtendedJMSendMail.java,v 1.25 2007-08-06 12:25:24 michiel Exp $
 */

public class ExtendedJMSendMail extends SendMail {
    private static final Logger log = Logging.getLoggerInstance(ExtendedJMSendMail.class);


}
