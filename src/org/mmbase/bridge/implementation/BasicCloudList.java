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
	public Cloud get(int index) {
	    return cloudcontext.getCloud((String)getObject(index));
	}

	/**
	*
	*/
//	public CloudIterator iterator() {
//	    return new BasicCloudIterator(this);
//	};
	
	public class BasicCloudIterator { // implements CloudIterator {
	    CloudList list;
	    int index=-1;
	
	    BasicCloudIterator(CloudList list) {
	        this.list = list;
	    }
	
	    public boolean hasNext() {
	        return  index<(list.size()-1);
	    }
	
	    public Cloud next() {
	        index++;
	        if (index>=list.size()) {
	            index = list.size()+1;
	            throw new NoSuchElementException("Cloud does not exits in this list");
	        } else {
    	        return list.get(index);
    	    }
	    }
	
	}
	
}
