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
 * this class is act as a POJO to present <formschema> element in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: FormSchemaElm.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class FormSchemaElm extends SchemaElement{
    
    public Localizable title = new Localizable();
    
    public Localizable subTitle = new Localizable();
    
    public final List fields = new ArrayList();
    
    public final List fieldSets = new ArrayList();
    
    public final List lists = new ArrayList();

    /**
     * @return Returns the id.
     */
    public String getId() {
        return this.getAttribute(SchemaKeys.ATTR_ID);
    }

}
