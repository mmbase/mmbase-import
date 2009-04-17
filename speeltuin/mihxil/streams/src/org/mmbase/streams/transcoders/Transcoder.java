/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;


import java.net.*;
/**
 * @author Michiel Meeuwissen
 */

public interface Transcoder {


    /**
     *
     */
    public void transcode(URI in, URI out) throws Exception;

}
