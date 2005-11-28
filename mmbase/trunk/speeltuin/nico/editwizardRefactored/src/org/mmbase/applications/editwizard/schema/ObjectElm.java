/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * this class is act as a POJO to present <object> element in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: ObjectElm.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class ObjectElm extends SchemaElement{
    
    public final List fields = new ArrayList();
    
    public final List relations = new ArrayList();

    /**
     * @return Returns the type.
     */
    public String getType() {
        return this.getAttribute(SchemaKeys.ATTR_TYPE);
    }

}
