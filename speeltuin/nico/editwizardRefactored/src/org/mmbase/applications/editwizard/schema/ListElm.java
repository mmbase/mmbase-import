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
 * this class is act as a POJO to present <list> element in <wizard-schema> xml files.
 * 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: ListElm.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class ListElm extends SchemaElement{

    public final List actions = new ArrayList(); 
    
    public final List commands = new ArrayList();
    
    public Localizable title = new Localizable();
    
    public Localizable description = new Localizable();
    
    public ItemElm item = null;
    
    /**
     * get command element by specify value of the attribute "name"
     * @param name the attrubite name's value
     * @return
     */
    public CommandElm getCommandByName(String name) {
        if (name==null) {
            return null;
        }
        for (int i=0;i<commands.size();i++) {
            CommandElm command = (CommandElm)commands.get(i);
            if (name.equals(command.getName())) {
                return command;
            }
        }
        return null;
    }
    
    /**
     * get action element by specify value of the attribute "type"
     * @param type
     * @return
     */
    public ActionElm getActionByType(String type) {
        if (type==null) {
            return null;
        }
        for (int i=0;i<actions.size();i++) {
            ActionElm action = (ActionElm)actions.get(i);
            if (type.equals(action.getType())) {
                return action;
            }
        }
        return null;
    }

    /**
     * @return Returns the maxoccurs.
     */
    public String getMaxoccurs() {
        return this.getAttribute(SchemaKeys.ATTR_MAXOCCURS);
    }

    /**
     * @return Returns the minoccurs.
     */
    public String getMinoccurs() {
        return this.getAttribute(SchemaKeys.ATTR_MINOCCURS);
    }

    /**
     * get the datafrom attribute's value of list element.
     * @return Returns the datafrom attribute's value of list element.
     */
    public String getDataFrom() {
        return this.getAttribute(SchemaKeys.ATTR_DATAFROM);
    }

    /**
     * @return Returns the fDataPath.
     */
    public String getFDataPath() {
        return this.getAttribute(SchemaKeys.ATTR_FDATAPATH);
    }

    /**
     * @return Returns the fParentDataPath.
     */
    public String getFParentDataPath() {
        return this.getAttribute(SchemaKeys.ATTR_FPARENTDATAPATH);
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
     * @return Returns the hidecommand.
     */
    public String getHideCommand() {
        return this.getAttribute(SchemaKeys.ATTR_HIDECOMMAND);
    }

    /**
     * @return Returns the orderBy.
     */
    public String getOrderBy() {
        return this.getAttribute(SchemaKeys.ATTR_ORDERBY);
    }

    /**
     * @return Returns the orderType.
     */
    public String getOrderType() {
        return this.getAttribute(SchemaKeys.ATTR_ORDERTYPE);
    }

    /**
     * @return Returns the role.
     */
    public String getRole() {
        return this.getAttribute(SchemaKeys.ATTR_ROLE);
    }

    /**
     * @return Returns the searchDir.
     */
    public String getSearchDir() {
        return this.getAttribute(SchemaKeys.ATTR_SEARCHDIR);
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
