/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

/**
 * Represents the `dimension' of an image, i.e. its height and width.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7.4
 */


public class Dimension {
    protected int x;
    protected int y;

    protected Dimension() {
    }
    public Dimension(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Dimension(Dimension dim) {
        this.x = dim.x;
        this.y = dim.y;
    }

    public int getWidth() {
        return x;
    }
    public int getHeight() {
        return y;
    }

    public int getArea() {
        return x * y;
    }
    
    public String toString() {
        return "" + x + "x" + y;
    }
    public boolean equals(Object o) {
        if (o instanceof Dimension) {
            Dimension dim = (Dimension) o;
            return dim.x == x && dim.y == y;
        } else {
            return false;
        }
    }
    public int hashCode() {
        return (x + 1) * (y + 1);
    }

}
