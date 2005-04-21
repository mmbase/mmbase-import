/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.util.*;
/**
 * @author keesj
 * @version $Id: Path.java,v 1.1.1.1 2004-04-02 14:58:47 keesj Exp $
 */
public class Path {
    String fullPath;
    String[] paths;
    //pointer to the current index of the path element
    int currentPointer = 0;

    public Path(String path) {
        this.fullPath = path;
        StringTokenizer st = new StringTokenizer(path, NavigationControl.PATH_SEPARATOR);
        paths = new String[st.countTokens()];
        for (int x = 0; x < paths.length; x++) {
            paths[x] = st.nextToken();
        }
    }
    public void reset(){
    	currentPointer =0; 
    }
    
    public Path newInstance(){
    	return new Path(fullPath);
    }

    public String current() {
        return paths[currentPointer];
    }

    public boolean hasCurrent() {
        return (currentPointer >= 0 && currentPointer < paths.length);
    }

    public boolean hasNext() {
        return currentPointer < (paths.length -1);
    }

    public String next() {
        currentPointer++;
        return current();
    }

    public boolean hasPrevious() {
        return currentPointer > 0;
    }

    public String previous() {
        currentPointer--;
        return current();
    }
    
    /**
     * @return the amount of path elements in the path
     */
    public int size() {
        return paths.length;
    }
    
    public String toString(){
    	return fullPath;
    }
}
