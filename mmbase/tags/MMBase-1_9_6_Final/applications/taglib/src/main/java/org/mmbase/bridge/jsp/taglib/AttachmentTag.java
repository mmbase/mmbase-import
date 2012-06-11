/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.Node;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class AttachmentTag extends ImageTag {


    @Override
    public String getTemplate(Node node, String t, int widthTemplate, int heightTemplate, String cropTemplate) {
        return t;
    }

}

