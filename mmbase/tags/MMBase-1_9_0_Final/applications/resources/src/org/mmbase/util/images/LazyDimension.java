/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import org.mmbase.bridge.Node;
import java.util.*;

/**
 * A 'lazy' dimension is a Dimension object which depends on an image-node and conversion
 * template. The actual dimension will only be requested from this node, as soon as {@link
 * #getWidth} or {@link #getHeight} are called for the first time. 
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7.4
 */


public class LazyDimension extends Dimension {

    protected Node    node;
    protected String  template;
    private   boolean loaded = false;
    public LazyDimension(Node n, String t) {
        node = n;
        template = t;
    }
    
    private void getDimension() {
        if (loaded) return;
        List<String> args = new ArrayList<String>();
        if (template != null) {
            args.add(template);
        }
        Dimension dim = (Dimension) node.getFunctionValue("dimension", args).get();
        x = dim.getWidth();
        y = dim.getHeight();
        loaded = true;
    }

    public int getWidth() {
        getDimension();
        return super.getWidth();
    }
    public int getHeight() {
        getDimension();
        return super.getHeight();
    }

    public int getArea() {
        getDimension();
        return super.getArea();
    }

}
