/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * A list of Clouds
 *
 * @author Pierre van Rooden
 */
public class BasicCloudList extends BasicList implements CloudList {

    private CloudContext cloudcontext;

    /**
    * ...
    */
    BasicCloudList(Collection c, CloudContext cloudcontext) {
        super(c);
        this.cloudcontext=cloudcontext;
    }

    /**
	*
	*/
	public Object get(int index) {
	    return cloudcontext.getCloud((String)super.get(index));
	}

    /**
	*
	*/
	public Cloud getCloud(int index) {
	    return (Cloud)getObject(index);
	}
	
	/**
	*
	*/
	public CloudIterator cloudIterator() {
	    return new BasicCloudIterator(this);
	};
	
	public class BasicCloudIterator extends BasicIterator implements CloudIterator {
	
	    BasicCloudIterator(BasicList list) {
	        super(list);
	    }
	
	    public Cloud nextCloud() {
	        return (Cloud)nextObject();
	    }
	}
	
}
