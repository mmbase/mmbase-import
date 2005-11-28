/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.schema;


/**
 * this class is act as a POJO to present <option> element in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: OptionElm.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class OptionElm extends SchemaElement{
    
//    private String textValue = null;
    
    public Localizable prompt = new Localizable();

    /**
     * @return Returns the id.
     */
    public String getId() {
        return this.getAttribute(SchemaKeys.ATTR_ID);
    }

    /**
     * 
     * @param id
     */
    public void setId(String id) {
        this.setAttribute(SchemaKeys.ATTR_ID,id);
    }

    /**
     * @param textValue The textValue to set.
     */
    public void setTextValue(String textValue) {
        this.setAttribute(SchemaKeys.MISC_VALUE,textValue);
    }

    /**
     * @return Returns the textValue.
     */
    public String getTextValue() {
        return this.getAttribute(SchemaKeys.MISC_VALUE);
    }

}
