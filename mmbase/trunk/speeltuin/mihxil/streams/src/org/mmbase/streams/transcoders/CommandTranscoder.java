/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;


import java.net.*;
import org.mmbase.util.externalprocess.*;

/**
 * A trancoder base on an external command, like <code>ffmpeg</code> or <code>ffmpeg2theora</code>
 *
 * @author Michiel Meeuwissen
 */

public abstract class CommandTranscoder implements Transcoder {

    protected abstract String getCommand();

    protected String[] getEnvironment() {
        return new String[0];
    }
    protected abstract String[] getArguments(URI in, URI out);

    public void transcode(final URI in, final URI out) throws Exception {
        CommandLauncher cl = new CommandLauncher("Transcoding " + in + " to " + out);
        cl.execute(getCommand(), getArguments(in, out), getEnvironment());
        cl.waitAndRead(System.out, System.err);
    }

}
