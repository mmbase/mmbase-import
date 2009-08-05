/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.schema;

/**
 * this class is act as a POJO to present <field> element in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: FieldElm.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class FieldElm extends SchemaElement{

    public Localizable prompt = new Localizable();
    
    public Localizable description = new Localizable();
    
    public OptionListElm optionList = null;
    
//    private String defaultValue = null;
//    
//    private String prefix = null;
//    
//    private String postfix = null;
//
    /**
     * @return Returns the objectNumber.
     */
    public String getObjectNumber() {
        return this.getAttribute(SchemaKeys.ATTR_OBJECTNUMBER);
    }

    /**
     * @return Returns the wizardName.
     */
    public String getWizardName() {
        return this.getAttribute(SchemaKeys.ATTR_WIZARDNAME);
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return this.getAttribute(SchemaKeys.ATTR_NAME);
    }

    /**
     * @return Returns the dtmax.
     */
    public String getDtmax() {
        return this.getAttribute(SchemaKeys.ATTR_DTMAX);
    }

    /**
     * @return Returns the dtmaxlength.
     */
    public String getDtmaxlength() {
        return this.getAttribute(SchemaKeys.ATTR_DTMAXLENGTH);
    }

    /**
     * @return Returns the dtmin.
     */
    public String getDtmin() {
        return this.getAttribute(SchemaKeys.ATTR_DTMIN);
    }

    /**
     * @return Returns the dtminlength.
     */
    public String getDtminlength() {
        return this.getAttribute(SchemaKeys.ATTR_DTMINLENGTH);
    }

    /**
     * @return Returns the dtpattern.
     */
    public String getDtpattern() {
        return this.getAttribute(SchemaKeys.ATTR_DTPATTERN);
    }

    /**
     * @return Returns the dtrequired.
     */
    public String getDtrequired() {
        return this.getAttribute(SchemaKeys.ATTR_DTREQUIRED);
    }

    /**
     * @return Returns the dttype.
     */
    public String getDttype() {
        return this.getAttribute(SchemaKeys.ATTR_DTTYPE);
    }

    /**
     * @return Returns the fdatapath.
     */
    public String getFdatapath() {
        return this.getAttribute(SchemaKeys.ATTR_FDATAPATH);
    }

    /**
     * @return Returns the ftype.
     */
    public String getFtype() {
        return this.getAttribute(SchemaKeys.ATTR_FTYPE);
    }

    /**
     * @return Returns the hide.
     */
    public String getHide() {
        return this.getAttribute(SchemaKeys.ATTR_HIDE);
    }

    /**
     * @return Returns the inline.
     */
    public String getInline() {
        return this.getAttribute(SchemaKeys.ATTR_INLINE);
    }

    /**
     * @return Returns the rows.
     */
    public String getRows() {
        return this.getAttribute(SchemaKeys.ATTR_ROWS);
    }

    /**
     * @return Returns the size.
     */
    public String getSize() {
        return this.getAttribute(SchemaKeys.ATTR_SIZE);
    }

    /**
     * @param prefix The prefix to set.
     */
    public void setPrefix(String prefix) {
        this.setAttribute(SchemaKeys.MISC_PREFIX, prefix);
    }

    /**
     * @return Returns the prefix.
     */
    public String getPrefix() {
        return this.getAttribute(SchemaKeys.MISC_PREFIX);
    }

    /**
     * @param postfix The postfix to set.
     */
    public void setPostfix(String postfix) {
        this.setAttribute(SchemaKeys.MISC_POSTFIX, postfix);
    }

    /**
     * @return Returns the postfix.
     */
    public String getPostfix() {
        return this.getAttribute(SchemaKeys.MISC_POSTFIX);
    }

    /**
     * @param textValue The textValue to set.
     */
    public void setDefaultValue(String textValue) {
        this.setAttribute(SchemaKeys.MISC_DEFAULT, textValue);
    }

    /**
     * @return Returns the textValue.
     */
    public String getDefaultValue() {
        return this.getAttribute(SchemaKeys.MISC_DEFAULT);
    }

    /**
     * @param fid The fid to set.
     */
    public void setFid(String fid) {
        this.setAttribute(SchemaKeys.ATTR_FID, fid);
    }

    /**
     * @return Returns the fid.
     */
    public String getFid() {
        return this.getAttribute(SchemaKeys.ATTR_FID);
    }
}
