package org.mmbase.mmget;

import java.io.IOException;
import java.net.*;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Initiates an UrlReader matching the url's contenttype and passes it the initiated
 * URLConnection.
 *
 * @author Andr&eacute; van Toly
 * @version $Id: UrlReaders.java,v 1.4 2009-03-11 08:34:20 andre Exp $
 */
public class UrlReaders {
    private static final Logger log = Logging.getLoggerInstance(UrlReaders.class);
    
    protected static UrlReader reader;
    protected URL url = null;
    protected static int contenttype = -1;

    public static UrlReader getUrlReader(URL url) throws IOException {
        
        URLConnection uc = url.openConnection();
        contenttype = MMGet.contentType(uc);
        
        if (contenttype == MMGet.CONTENTTYPE_CSS) {
            reader = new CSSReader(uc);
        } else {
            reader = new HTMLReader(uc);
        }
        return reader;
    }

}
