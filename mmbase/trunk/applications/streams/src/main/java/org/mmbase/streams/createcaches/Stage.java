/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.createcaches;



/**
 * This enum can contain the 'stage' of the transcoding process
 */
public enum Stage {
    /**
     * Nothing happend yet.
     */
    UNSTARTED,
    /**
     * Currently busy with a recognizer
     */
    RECOGNIZER,
    /**
     * Currently busy with a transcoder
     */
    TRANSCODER,
    /**
     * Everything's done!
     */
    READY;
}

