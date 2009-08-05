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
 * this class is act as a POJO to present <relation> element in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: RelationElm.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class RelationElm extends SchemaElement{
    
    public final List fields = new ArrayList();
    
//    public final List objects = new ArrayList();
    
    //in fact, one relation can only have one destination or source object. 
    public ObjectElm object = null;

    /**
     * @param destination The destination to set.
     */
    public void setDestination(String destination) {
        this.setAttribute(SchemaKeys.ATTR_DESTINATION,destination);
    }

    /**
     * @return Returns the destination.
     */
    public String getDestination() {
        String destination = this.getAttribute(SchemaKeys.ATTR_DESTINATION);
        if (destination==null || "".equals(destination)) {
            destination = this.getAttribute(SchemaKeys.ATTR_DESTINATIONTYPE);
        }
        return destination;
    }

    /**
     * @param role The role to set.
     */
    public void setRole(String role) {
        this.setAttribute(SchemaKeys.ATTR_ROLE,role);
    }

    /**
     * @return Returns the role.
     */
    public String getRole() {
        return this.getAttribute(SchemaKeys.ATTR_ROLE);
    }

    /**
     * @param searchDir The searchDir to set.
     */
    public void setSearchDir(String searchDir) {
        this.setAttribute(SchemaKeys.ATTR_SEARCHDIR,searchDir);
    }

    /**
     * @return Returns the searchDir.
     */
    public String getSearchDir() {
        return this.getAttribute(SchemaKeys.ATTR_SEARCHDIR);
    }

    /**
     * @param createDir The createDir to set.
     */
    public void setCreateDir(String createDir) {
        this.setAttribute(SchemaKeys.ATTR_CREATEDIR,createDir);
    }

    /**
     * @return Returns the createDir.
     */
    public String getCreateDir() {
        return this.getAttribute(SchemaKeys.ATTR_CREATEDIR);
    }

    
}
