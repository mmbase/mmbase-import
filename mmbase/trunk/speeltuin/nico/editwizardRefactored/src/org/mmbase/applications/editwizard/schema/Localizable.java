/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.schema;

public class Localizable extends SchemaElement {
    
    /**
     * get text content of specified language
     * @see org.mmbase.applications.editwizard.schema.Localizable#getTextValue(java.lang.String)
     */
    public String getTextValue(String lang) {
        if (lang==null) {
            lang = "";
        }
        String textValue = getAttribute(lang);
        if (textValue==null && getAttributes().size()>0) {
            textValue = (String)getAttributes().values().iterator().next();
        }
        return textValue;
    }

    /**
     * get text content of default language
     * @see org.mmbase.applications.editwizard.schema.Localizable#getTextValue()
     */
    public String getTextValue() {
        return getTextValue(null);
    }
    
    /**
     * set text value
     * @param lang langueage of the content
     * @param value content
     */
    public void setTextValue(String lang, String value) {
        if (lang==null) {
            lang = "";
        }
        setAttribute(lang,value);
    }

    /**
     * set text value of default language
     * @param value content in default language
     */
    public void setTextValue(String value) {
        setTextValue(null,value);
    }
    
    /**
     * get the text content of default language.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getTextValue();
    }
    
    /**
     * indicate whether this object has text values.
     * @return
     */
    public boolean hasValues() {
        return this.getAttributes().size()>0;
    }
    
}
