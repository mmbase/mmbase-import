/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.data;

import java.util.*;

/**
 * 
 * @todo javadoc
 * 
 * @author caicai
 * @created 2005-8-29
 * @version $Id: ObjectData.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class ObjectData extends BaseData{
    
    private final Map relationDataMap = new HashMap();
    
    /**
     * add relation into this object
     * @param listData
     */
    public void addRelationData(RelationData relationData) {
        this.addChild(relationData);
        this.relationDataMap.put(relationData.getDid(),relationData);
    }
    
    /**
     * remove list from object 
     * @param id
     */
    public RelationData removeRelationData(String did){
        RelationData relationData = getRelationData(did);
        if (relationData.getStatus()==BaseData.STATUS_NEW) {
            relationData.setStatus(BaseData.STATUS_NOT_INIT);
            this.relationDataMap.remove(did);
            this.removeChild(relationData);
        }
        relationData.setStatus(BaseData.STATUS_DELETE);
        return relationData;
    }
    
    /**
     * add relations into this object
     * @param relationDataList
     */
    public void addRelationData(List relationDataList) {
        for(Iterator iterator= relationDataList.iterator();iterator.hasNext();){
            RelationData relationData = (RelationData)iterator.next();
            addRelationData(relationData);
        }
    }
    
    /**
     * find relation within this object
     * @param id
     * @return
     */
    public RelationData getRelationData(String did){
        RelationData relation = (RelationData)this.relationDataMap.get(did);
        if (this.children.contains(relation)==false) {
            this.relationDataMap.remove(did);
            relation = null;
        }
        return relation;
    }
    
    /**
     * get relation data list by role and destination
     * @param role
     * @param destination
     * @return
     */
    public List getRelationDataList(String role, String destination) {
        List relationDataList = new ArrayList();
        for(Iterator iterator= this.children.iterator();iterator.hasNext();){
            RelationData relationData = (RelationData)iterator.next();
            if (role!=null && role.equals(relationData.getRole())==false){
                continue;
            }
            if (destination!=null && destination.equals(relationData.getRelatedObject().getType())==false){
                continue;
            }
            relationDataList.add(relationData);
        }
        //TODO: could remove unmodify limitation
        return Collections.unmodifiableList(relationDataList);
    }
    
    /**
     * get list of relations
     * @return
     */
    public List getRelationDataList() {
        return Collections.unmodifiableList(this.children);
        
    }
    
    /**
     * clear relations of current object.
     */
    public void clearRelations() {
        this.children.clear();
        this.relationDataMap.clear();
    }
}
