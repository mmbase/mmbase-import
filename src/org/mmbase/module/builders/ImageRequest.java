/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.List;
import org.mmbase.module.core.ByteFieldContainer;

/**
 * Defines one Image convert request.
 *
 * @author Rico Jansen
 * @version $Id: ImageRequest.java,v 1.5 2004-02-23 19:05:00 pierre Exp $
 */
public class ImageRequest {

    private String ckey;
    private List params;
    private byte[] in;
    private int id;
    private int count = 0;
    private ByteFieldContainer container = null;

    private boolean outputSet = false;

    /**
     * @javadoc
     */
    public ImageRequest(int id ,String ckey, List params, byte[] in) {
        this.id=id;
        this.ckey=ckey;
        this.in=in;
        this.container=null;
        this.params=params;
        count=0;
    }

    /**
     * @javadoc
     */
    public List getParams() {
        return params;
    }

    /**
     * @javadoc
     */
    public String getKey() {
        return ckey;
    }

    /**
     * @javadoc
     */
    public byte[] getInput() {
        return in;
    }

    /**
     * @javadoc
     */
    public int getId() {
        return id;
    }

    /**
     * @javadoc
     */
    private synchronized byte[] getOutput() {
        if (! outputSet) { // the request is in progress, wait until it is ready.
            count++;
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        return container.value;
    }

    /**
     * @javadoc
     */
    public synchronized ByteFieldContainer getContainer() {
        if (! outputSet) { // the request is in progress, wait until it is ready.
            count++;
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        return container;
    }

    /**
     * @javadoc
     */
    public synchronized void setContainer(ByteFieldContainer container) {
        outputSet = true;
        count = 0;
        this.container = container;
        notifyAll();
    }

    /**
     * Returns how many request are waiting for the result of this image transformation.
     */
    public int count() {
        return count;
    }

    // javadoc inherited (of Object)
    public String toString() {
        return("id=" + id + " : key=" + ckey);
    }
}
