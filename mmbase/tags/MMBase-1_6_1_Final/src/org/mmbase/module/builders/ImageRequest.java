/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.List;

/**
 * @javadoc
 * @author Rico Jansen
 * @version $Id: ImageRequest.java,v 1.3 2002-04-12 08:53:00 pierre Exp $
 */
public class ImageRequest {

    private String ckey;
    private List params;
    private byte[] in;
    private byte[] out;
    private int id;
    private int count=0;

    /**
     * @javadoc
     */
    public ImageRequest(int id,String ckey,List params,byte[] in) {
        this.id=id;
        this.ckey=ckey;
        this.in=in;
        this.out=null;
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
    public synchronized byte[] getOutput() {
        if (out==null) {
            count++;
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        return out;
    }

    /**
     * @javadoc
     */
    public synchronized void setOutput(byte[] output) {
        out=output;
        count=0;
        notifyAll();
    }

    /**
     * @javadoc
     */
    public int count() {
        return count;
    }

    /**
     * @javadoc
     */
    public String toString() {
        return("id="+id+" : key="+ckey);
    }
}
