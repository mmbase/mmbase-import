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
 * this class is act as a POJO to present <fieldset> element in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: FieldSetElm.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class FieldSetElm extends SchemaElement{

    public Localizable prompt = new Localizable();
    
    public final List fields = new ArrayList();

}
