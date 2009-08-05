/*
 * Created on 2005-6-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mmbase.applications.editwizard.schema;

import java.util.*;

/**
 * this class is act DOM element for <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: WizardSchema.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public class WizardSchema extends SchemaElement{

    public Localizable titles = new Localizable();

    public Localizable descriptions = new Localizable();

    public Localizable taskDescriptions = new Localizable();

    private final Map actions = new HashMap();

    private final Map formSchemas = new HashMap();

    private final List steps = new ArrayList();

    private final Map optionLists = new HashMap();
    
    private final Map elementFidIndex = new HashMap();

    /**
     * get schema element by specified "fid" parameter
     * @param fid the id tagged for the element.
     * @return schema element.
     */
    public SchemaElement findElementByFid(String fid) {
        return (SchemaElement)elementFidIndex.get(fid);
    }

    /**
     * @return Returns the actions.
     */
    public Map getActions() {
        return actions;
    }

    /**
     * @return Returns the formSchemas.
     */
    public Map getFormSchemas() {
        return formSchemas;
    }

    /**
     * @return Returns the steps.
     */
    public List getSteps() {
        return steps;
    }

    /**
     * @return Returns the lists.
     */
    public Map getOptionLists() {
        return optionLists;
    }

    /**
     * 
     * @param actionElm
     */
    public void addAction(ActionElm actionElm) {
        this.actions.put(actionElm.getType(),actionElm);
    }

    /**
     * 
     * @param formSchema
     */
    public void addFormSchema(FormSchemaElm formSchema) {
        this.formSchemas.put(formSchema.getId(),formSchema);
    }

    /**
     * 
     * @param optionList
     */
    public void addOptionList(OptionListElm optionList) {
        this.optionLists.put(optionList.getName(),optionList);
    }
    
    public FormSchemaElm getFormSchema(String id) {
        return (FormSchemaElm)formSchemas.get(id);
    }

    /**
     * get action element by specify value of the attribute "type"
     * @param type
     * @return
     */
    public ActionElm getActionByType(String type) {
        return (ActionElm) this.actions.get(type);
    }
    
    /**
     * get action element by specify value of the attribute "type"
     * @param type
     * @return
     */
    public OptionListElm getOptionListByName(String name) {
        return (OptionListElm)this.optionLists.get(name);
    }

    /**
     * set steps
     * @param steplist
     */
    public void setSteps(List steplist) {
        this.steps.clear();
        this.steps.addAll(steplist);
    }
    
    /**
     * @return Returns the id.
     */
    public String getId() {
        return this.getAttribute(SchemaKeys.ATTR_ID);
    }

    static class FormStepComparator implements Comparator{
        
        private List formSteps = null;
        
        FormStepComparator(List list) {
            this.formSteps = list;
        }

        public int compare(Object o1, Object o2) {
            String n1 = (String)o1;
            String n2 = (String)o2;
            int index1 = this.formSteps.indexOf(n1);
            int index2 = this.formSteps.indexOf(n2);
            return index1-index2;
        }
        
    }

    /**
     * @return Returns the tagNodesMap.
     */
    public Map getElementFidIndex() {
        return elementFidIndex;
    }
    
}
