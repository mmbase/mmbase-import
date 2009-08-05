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
 * this class is act as a POJO to present <action> element in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: ActionElm.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class ActionElm extends SchemaElement{
    
    public ObjectElm object = null;
    
    public final List fields = new ArrayList(); 
    
    public final List relations = new ArrayList();
    
    public Localizable prompt = new Localizable();
    
    public Localizable description = new Localizable();

    public void addChildElement(SchemaElement childElement) {
        super.addChildElement(childElement);
        if (childElement instanceof ListElm) {
            this.fields.add(childElement);
        } else if (childElement instanceof RelationElm) {
            this.relations.add(childElement);
        }
    }

    /**
     * get type of the action element.
     * @return Returns the type.
     */
    public String getType() {
        return this.getAttribute(SchemaKeys.ATTR_TYPE);
    }
    
    /**
     * retrieve relation element by specify role and destination
     * @param role
     * @param destination
     * @return
     */
    public RelationElm getRelation(String role, String destination) {
        for (int i=0;i<relations.size();i++) {
            RelationElm relationElm = (RelationElm)relations.get(i);
            if (role!=null && role.equals(relationElm.getRole())==false){
                continue;
            }
            if (destination!=null && destination.equals(relationElm.getDestination())==false){
                continue;
            }
            return relationElm;
        }
        return null;
    }
    
}
