/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.sharehandlers;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.applications.packaging.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author     Daniel Ockeloen
 * @created    July 20, 2004
 */
public class ShareClientSession {

    // logger
    private static Logger log = Logging.getLoggerInstance(ShareClientSession.class);

    private String callbackurl;


    /**
     *Constructor for the ShareClientSession object
     *
     * @param  callbackurl  Description of the Parameter
     */
    public ShareClientSession(String callbackurl) {
        this.callbackurl = callbackurl;
    }


    /**
     *  Description of the Method
     *
     * @param  myid  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean sendRemoteSignal(String myid) {
        String url = callbackurl + "?id=" + URLParamEscape.escapeurl(myid);
        log.info("sending signal =" + url);
        try {
            URL includeURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) includeURL.openConnection();
            Object o = connection.getContent();
        } catch (Exception e) {
            //	e.printStackTrace();
            // should we remove them if we get a error ?
        }
        return true;
    }

}

