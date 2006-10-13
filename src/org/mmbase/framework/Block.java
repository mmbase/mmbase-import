/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.io.*;
import org.mmbase.util.functions.Parameters;

/**
 * A Block is a representation of a page within a component. It consists of 3 views, 
 * a 'head', 'body' and 'process' view. 
 *
 * @author Johannes Verelst
 * @version $Id: Block.java,v 1.2 2006-10-13 13:18:51 michiel Exp $
 * @since MMBase-1.9
 */
public class Block {
    Renderer head;
    Renderer body;
    Processor processor;

    String name;
    String mimetype;

    public Block(String name, String mimetype) {
        this.name = name;
        this.mimetype = mimetype;
    }

    Renderer getHead() {
        return head;
    }

    Renderer getBody() {
        return head;
    }
    Processor getProcessor() {
        return processor;
    }

}
