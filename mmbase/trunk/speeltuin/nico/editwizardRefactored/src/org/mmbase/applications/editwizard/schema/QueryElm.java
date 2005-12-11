/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.applications.editwizard.schema;

/**
 * this class is act as a POJO to present <query> element in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: QueryElm.java,v 1.2 2005-12-11 11:51:04 nklasens Exp $
 */
public class QueryElm extends SchemaElement{

    // Seconds. TODO: should be added into system propreties
    public static final long DEFAULT_TIMEOUT = 60 * 60;
    
    public ObjectElm object = null;

    /**
     * @return Returns the xpath.
     */
    public String getXpath() {
        return this.getAttribute(SchemaKeys.ATTR_XPATH);
    }

    /**
     * @return Returns the where.
     */
    public String getWhere() {
        return this.getAttribute(SchemaKeys.ATTR_WHERE);
    }

    /**
     * @return Returns the orderBy.
     */
    public String getOrderBy() {
        return this.getAttribute(SchemaKeys.ATTR_ORDERBY);
    }
    
    /**
     * @return Returns the orderBy.
     */
    public String getDirections() {
        return this.getAttribute(SchemaKeys.ATTR_DIRECTIONS);
    }
    
    /**
     * get the last time when the query be executed
     * @return
     */
    public long getLastExecutedTime() {
        String lastTime = this.getAttribute(SchemaKeys.MISC_LASTEXECUTEDTIME);
        if (lastTime!=null) {
            return Long.parseLong(lastTime);
        }
        return 0l;
    }
    
    /**
     * set the last time when the query be executed
     * @param lastExecutedTime
     */
    public void setLastExecutedTime(long lastExecutedTime) {
        this.setAttribute(SchemaKeys.MISC_LASTEXECUTEDTIME,""+lastExecutedTime);
    }

    /**
     * get time out of the list query
     * @return
     */
    public long getQueryTimeout() {
        String timeout = this.getAttribute(SchemaKeys.ATTR_QUERYTIMEOUT);
        if (timeout!=null) {
            return Long.parseLong(timeout);
        }
        return 1000*DEFAULT_TIMEOUT;
    }

}
