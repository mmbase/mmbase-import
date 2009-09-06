/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;


import java.net.*;
import org.mmbase.applications.media.*;
import org.mmbase.util.logging.*;


/**
 * Representation of one transcoding action. Instances should cloned before usage, so the transcoder
 * needs not be stateless.
 *
 * @author Michiel Meeuwissen
 */

public interface Transcoder extends org.mmbase.util.PublicCloneable<Transcoder>, java.io.Serializable {

    String getKey();

    Format getFormat();

    Codec getCodec();

    /**
     * Transcode a file to another, follow the process with a logger.
     * @param in
     * @param out
     * @param logger
     * @return Wether transcoding was sucessfull
     */
    void transcode(URI in, URI out, Logger logger) throws Exception;

    URI getIn();
    URI getOut();

}
