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
 * this class is act as a POJO to present <command> element in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: CommandElm.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class CommandElm extends SchemaElement{

    public Localizable prompt = new Localizable();
    
    public final List searchFilters = new ArrayList();

    public void addChildElement(SchemaElement childElement) {
        super.addChildElement(childElement);
        if (childElement instanceof SearchFilterElm) {
            this.searchFilters.add(childElement);
        }
    }

    /**
     * @return Returns the name.
     */
    public void setName(String name) {
        this.setAttribute(SchemaKeys.ATTR_NAME,name);
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return this.getAttribute(SchemaKeys.ATTR_NAME);
    }

    /**
     * @return Returns the age.
     */
    public void setAge(String age) {
        this.setAttribute(SchemaKeys.ATTR_AGE,age);
    }

    /**
     * @return Returns the age.
     */
    public String getAge() {
        return this.getAttribute(SchemaKeys.ATTR_AGE);
    }

    /**
     * @param wizardName The wizardName to set.
     */
    public void setWizardName(String wizardName) {
        this.setAttribute(SchemaKeys.ATTR_WIZARDNAME,wizardName);
    }

    /**
     * @return Returns the wizardName.
     */
    public String getWizardName() {
        return this.getAttribute(SchemaKeys.ATTR_WIZARDNAME);
    }

    /**
     * @param searchDir The searchDir to set.
     */
    public void setSearchDir(String searchDir) {
        this.setAttribute(SchemaKeys.ATTR_SEARCHDIR, searchDir);
    }

    /**
     * @return Returns the searchDir.
     */
    public String getSearchDir() {
        return this.getAttribute(SchemaKeys.ATTR_SEARCHDIR);
    }

    /**
     * @param objectNumber The objectNumber to set.
     */
    public void setObjectNumber(String objectNumber) {
        this.setAttribute(SchemaKeys.ATTR_OBJECTNUMBER, objectNumber);
    }

    /**
     * @return Returns the objectNumber.
     */
    public String getObjectNumber() {
        return this.getAttribute(SchemaKeys.ATTR_OBJECTNUMBER);
    }

    /**
     * @param orderBy The orderBy to set.
     */
    public void setOrderBy(String orderBy) {
        this.setAttribute(SchemaKeys.ATTR_ORDERBY, orderBy);
    }

    /**
     * @return Returns the orderBy.
     */
    public String getOrderBy() {
        return this.getAttribute(SchemaKeys.ATTR_ORDERBY);
    }

    /**
     * @param origin The origin to set.
     */
    public void setOrigin(String origin) {
        this.setAttribute(SchemaKeys.ATTR_ORIGIN, origin);
    }

    /**
     * @return Returns the origin.
     */
    public String getOrigin() {
        return this.getAttribute(SchemaKeys.ATTR_ORIGIN);
    }

    /**
     * @param nodePath The nodePath to set.
     */
    public void setNodePath(String nodePath) {
        this.setAttribute(SchemaKeys.ATTR_NODEPATH, nodePath);
    }

    /**
     * @return Returns the nodePath.
     */
    public String getNodePath() {
        return this.getAttribute(SchemaKeys.ATTR_NODEPATH);
    }

    /**
     * @param inline The inline to set.
     */
    public void setInline(String inline) {
        this.setAttribute(SchemaKeys.ATTR_INLINE, inline);
    }

    /**
     * @return Returns the inline.
     */
    public String getInline() {
        return this.getAttribute(SchemaKeys.ATTR_INLINE);
    }

    /**
     * @param filterRequired The filterRequired to set.
     */
    public void setFilterRequired(String filterRequired) {
        this.setAttribute(SchemaKeys.ATTR_FILTERREQUIRED, filterRequired);
    }

    /**
     * @return Returns the filterRequired.
     */
    public String getFilterRequired() {
        return this.getAttribute(SchemaKeys.ATTR_FILTERREQUIRED);
    }

    /**
     * @param fields The fields to set.
     */
    public void setFields(String fields) {
        this.setAttribute(SchemaKeys.ATTR_FIELDS, fields);
    }

    /**
     * @return Returns the fields.
     */
    public String getFields() {
        return this.getAttribute(SchemaKeys.ATTR_FIELDS);
    }

    /**
     * @param directions The directions to set.
     */
    public void setDirections(String directions) {
        this.setAttribute(SchemaKeys.ATTR_DIRECTIONS, directions);
    }

    /**
     * @return Returns the directions.
     */
    public String getDirections() {
        return this.getAttribute(SchemaKeys.ATTR_DIRECTIONS);
    }

    /**
     * @param constraints The constraints to set.
     */
    public void setConstraints(String constraints) {
        this.setAttribute(SchemaKeys.ATTR_CONSTRAINTS, constraints);
    }

    /**
     * @return Returns the constraints.
     */
    public String getConstraints() {
        return this.getAttribute(SchemaKeys.ATTR_CONSTRAINTS);
    }

    
    /**
     * @return Returns the command.
     */
    public String getCommand() {
        return this.getAttribute(SchemaKeys.ATTR_COMMAND);
    }

    
    /**
     * @param command The command to set.
     */
    public void setCommand(String command) {
        this.setAttribute(SchemaKeys.ATTR_COMMAND,command);
    }

    
    /**
     * @return Returns the startnodes.
     */
    public String getStartnodes() {
        return this.getAttribute(SchemaKeys.ATTR_STARTNODES);
    }

    
    /**
     * @param startnodes The startnodes to set.
     */
    public void setStartnodes(String startnodes) {
        this.setAttribute(SchemaKeys.ATTR_STARTNODES, startnodes);
    }
}
