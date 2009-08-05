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
 * this class is act as a POJO to present <optionlist> element in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: OptionListElm.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class OptionListElm extends SchemaElement {

    public final List options = new ArrayList();
    
    public QueryElm query = null;

    /**
     * @return Returns the name.
     */
    public String getName() {
        return this.getAttribute(SchemaKeys.ATTR_NAME);
    }

    /**
     * @return Returns the select.
     */
    public String getSelect() {
        return this.getAttribute(SchemaKeys.ATTR_SELECT);
    }

    /**
     * @return Returns the optionid.
     */
    public String getOptionid() {
        return this.getAttribute(SchemaKeys.ATTR_OPTIONID);
    }

    /**
     * @return Returns the optioncontent.
     */
    public String getOptionContent() {
        return this.getAttribute(SchemaKeys.ATTR_OPTIONCONTENT);
    }

}
