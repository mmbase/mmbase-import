/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.util.logging.*;
import java.net.*;
import org.mmbase.applications.media.MimeType;


/**
 *
 * @author Michiel Meeuwissen
 */

public interface Recognizer extends org.mmbase.util.PublicCloneable<Recognizer> {


    MimeType getMimeType();
    void setMimeType(String s);

    void analyze(URI in, Logger logger) throws Exception;


}
