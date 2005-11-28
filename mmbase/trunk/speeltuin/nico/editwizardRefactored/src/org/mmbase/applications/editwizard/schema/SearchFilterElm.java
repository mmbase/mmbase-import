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
 * this class is act as a POJO to present <search-filter> element in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: SearchFilterElm.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class SearchFilterElm extends SchemaElement{

    public Localizable name = new Localizable();
    
    public final List searchFields = new ArrayList();
    
    /**
     * @param defaultValue The defaultValue to set.
     */
    public void setDefaultValue(String defaultValue) {
        this.setAttribute(SchemaKeys.ELEM_DEFAULT,defaultValue);
    }

    /**
     * @return Returns the defaultValue.
     */
    public String getDefaultValue() {
        return this.getAttribute(SchemaKeys.ELEM_DEFAULT);
    }

    /**
     * @param searchType The searchType to set.
     */
    public void setSearchType(String searchType) {
        this.setAttribute(SchemaKeys.ATTR_SEARCHTYPE,searchType);
    }

    /**
     * @return Returns the searchType.
     */
    public String getSearchType() {
        return this.getAttribute(SchemaKeys.ATTR_SEARCHTYPE);
    }

}
