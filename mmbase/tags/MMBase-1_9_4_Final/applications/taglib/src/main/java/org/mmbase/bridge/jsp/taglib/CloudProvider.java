/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Cloud;

/**
 *
 * Interface designed to make it possible for child tags
 * to access a cloud (or transaction) defined in a tag
 *
 * @author Michiel Meeuwissen
 * @version $Id$ 
 */

public interface CloudProvider extends TagIdentifier {
    /**
     * @return the cloud contained in the tag
     **/
    public Cloud getCloudVar() throws JspTagException;	

}
