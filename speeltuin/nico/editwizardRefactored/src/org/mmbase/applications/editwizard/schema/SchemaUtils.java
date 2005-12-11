/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.schema;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mmbase.applications.editwizard.util.XmlUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.*;


public class SchemaUtils {
    
    private static final Logger log = Logging.getLoggerInstance(SchemaUtils.class);
    
    //this pattern is used for resolveXpath(FieldElm) to match fdatapath="field[@name='fieldname']"
    private static Pattern PATTERN_XPATH_FIELDNAME = Pattern.compile("(object/)?field\\[\\@name=['\"](\\w+)['\"]\\]");

    static void fillAttributes(SchemaElement element, Node node) {
        NamedNodeMap namedNodeMap = node.getAttributes();
        if (namedNodeMap != null) {
            for (int i=0;i<namedNodeMap.getLength();i++) {
                Node attrNode = namedNodeMap.item(i);
                String attrName = attrNode.getNodeName();
                String attrValue = attrNode.getNodeValue();
                element.setAttribute(attrName,attrValue);
            }
        }
    }
    
    static ActionElm getActionByNode(Node node) {
        ActionElm element = new ActionElm();
        fillAttributes(element,node);
        
        element.prompt=getLocalizable( node, SchemaKeys.ELEM_PROMPT);
        element.description=getLocalizable( node, SchemaKeys.ELEM_DESCRIPTION);
        
        NodeList nodeList = XmlUtil.selectNodeList(node,SchemaKeys.ELEM_FIELD+"|"
                +SchemaKeys.ELEM_OBJECT+"|"+SchemaKeys.ELEM_RELATION);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            String tagName = childNode.getNodeName();
            if (SchemaKeys.ELEM_FIELD.equals(tagName)) {
                SchemaElement fieldElm = getFieldByNode(childNode);
                fieldElm.setParent(element);
                element.fields.add(fieldElm);
            }
            else if (SchemaKeys.ELEM_OBJECT.equals(tagName)) {
                ObjectElm objectElm = getObjectByNode(childNode);
                objectElm.setParent(element);
                element.object = objectElm;
            }
            else if (SchemaKeys.ELEM_RELATION.equals(tagName)) {
                RelationElm relationElm = getRelationByNode(childNode);
                relationElm.setParent(element);
                element.relations.add(relationElm);
            } else {
                System.err.println("unknow element:"+tagName+" in node "+node.getNodeName());
                
            }
        }
        return element;
    }
    
    static CommandElm getCommandByNode(Node node) {
        CommandElm element = new CommandElm();
        fillAttributes(element,node);
        
        element.prompt=getLocalizable( node,SchemaKeys.ELEM_PROMPT);
        
        NodeList nodeList = XmlUtil.selectNodeList(node,SchemaKeys.ELEM_SEARCHFILTER);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node searchFilterNode = nodeList.item(i);
            SearchFilterElm searchFilterElm = getSearchFilterByNode(searchFilterNode);
            searchFilterElm.setParent(element);
            element.searchFilters.add(searchFilterElm);
        }
        return element;
    }
    
    static SchemaElement getFieldByNode(Node node) {
        FieldElm element = new FieldElm();
        fillAttributes(element,node);
        
        element.prompt=getLocalizable( node, SchemaKeys.ELEM_PROMPT);
        element.description=getLocalizable( node, SchemaKeys.ELEM_DESCRIPTION);
        
        //every field could has only one optionlist 
        Node optionListNode = XmlUtil.selectSingleNode(node,SchemaKeys.ELEM_OPTIONLIST);
        if (optionListNode!=null) {
            OptionListElm optionListElm = getOptionListByNode(optionListNode);
            optionListElm.setParent(element);
            element.optionList = optionListElm;
        }
        
        String value = XmlUtil.getText(node);
        if (value!=null && value.trim().length()>0) {
            element.setDefaultValue(value.trim());
        }
        String prefix = XmlUtil.selectSingleNodeText(node,SchemaKeys.ELEM_PREFIX,null);
        if (prefix!=null) {
            element.setPrefix(prefix);
        }
        String postfix = XmlUtil.selectSingleNodeText(node,SchemaKeys.ELEM_POSTFIX,null);
        if (postfix!=null) {
            element.setPostfix(postfix);
        }
        
        return element;
    }
    
    static FieldSetElm getFieldSetByNode(Node node) {
        FieldSetElm element = new FieldSetElm();
        fillAttributes(element,node);
        
        element.prompt=getLocalizable( node,SchemaKeys.ELEM_PROMPT);
        
        NodeList nodeList = XmlUtil.selectNodeList(node,SchemaKeys.ELEM_FIELD);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            SchemaElement fieldElm = getFieldByNode(childNode);
            fieldElm.setParent(element);
            element.fields.add(fieldElm);
        }
        return element;
    }
    
    static FormSchemaElm getFormSchemaByNode(Node node) {
        FormSchemaElm element = new FormSchemaElm();
        fillAttributes(element,node);
        
        element.title=getLocalizable( node,SchemaKeys.ELEM_TITLE);
        element.subTitle=getLocalizable( node,SchemaKeys.ELEM_SUBTITLE);

        NodeList nodeList = XmlUtil.selectNodeList(node,SchemaKeys.ELEM_FIELD
                +"|"+SchemaKeys.ELEM_FIELDSET+"|"+SchemaKeys.ELEM_LIST);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            String tagName = childNode.getNodeName();
            if (SchemaKeys.ELEM_FIELD.equals(tagName)) {
                SchemaElement fieldElm = getFieldByNode(childNode);
                fieldElm.setParent(element);
                element.fields.add(fieldElm);
            } else if (SchemaKeys.ELEM_FIELDSET.equals(tagName)) {
                FieldSetElm fieldSetElm = getFieldSetByNode(childNode);
                fieldSetElm.setParent(element);
                element.fieldSets.add(fieldSetElm);
            } else if (SchemaKeys.ELEM_LIST.equals(tagName)) {
                ListElm listElm = getListByNode(childNode);
                listElm.setParent(element);
                element.lists.add(listElm);
            }
        }
        return element;
    }
    
    static ListElm getListByNode(Node node) {
        ListElm element = new ListElm();
        fillAttributes(element,node);

        element.title=getLocalizable(node,SchemaKeys.ELEM_TITLE);
        element.description=getLocalizable(node,SchemaKeys.ELEM_DESCRIPTION);
        
        NodeList nodeList = XmlUtil.selectNodeList(node,SchemaKeys.ELEM_ACTION
                +"|"+SchemaKeys.ELEM_COMMAND+"|"+SchemaKeys.ELEM_ITEM);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            String tagName = childNode.getNodeName();
            if (SchemaKeys.ELEM_ACTION.equals(tagName)) {
                ActionElm actionElm = getActionByNode(childNode);
                actionElm.setParent(element);
                element.actions.add(actionElm);
            } else if (SchemaKeys.ELEM_COMMAND.equals(tagName)) {
                CommandElm commandElm = getCommandByNode(childNode);
                commandElm.setParent(element);
                element.commands.add(commandElm);
            } else if (SchemaKeys.ELEM_ITEM.equals(tagName) && 
                    element.item==null) {
                ItemElm itemElm = getItemByNode(childNode);
                itemElm.setParent(element);
                element.item = itemElm;
            } else {
                System.err.println("unknow element:"+tagName+" in node "+node.getNodeName());
            }
        }
        return element;
    }
    
    static ItemElm getItemByNode(Node node) {
        ItemElm element = new ItemElm();
        fillAttributes(element,node);

        element.title=getLocalizable(node,SchemaKeys.ELEM_TITLE);
        element.description=getLocalizable(node,SchemaKeys.ELEM_DESCRIPTION);
        
        NodeList nodeList = XmlUtil.selectNodeList(node,SchemaKeys.ELEM_FIELD
                +"|"+SchemaKeys.ELEM_FIELDSET+"|"+SchemaKeys.ELEM_LIST);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            String tagName = childNode.getNodeName();
            if (SchemaKeys.ELEM_FIELD.equals(tagName)) {
                SchemaElement fieldElm = getFieldByNode(childNode);
                fieldElm.setParent(element);
                element.fields.add(fieldElm);
            } else if (SchemaKeys.ELEM_FIELDSET.equals(tagName)) {
                FieldSetElm fieldSetElm = getFieldSetByNode(childNode);
                fieldSetElm.setParent(element);
                element.fieldSets.add(fieldSetElm);
            } else if (SchemaKeys.ELEM_LIST.equals(tagName)) {
                ListElm listElm = getListByNode(childNode);
                listElm.setParent(element);
                element.lists.add(listElm);
            } else {
                System.err.println("unknow element:"+tagName+" in node "+node.getNodeName());
            }
        }
        return element;
    }
    
    static ObjectElm getObjectByNode(Node node) {
        ObjectElm element = new ObjectElm();
        fillAttributes(element,node);

        NodeList nodeList = XmlUtil.selectNodeList(node,SchemaKeys.ELEM_FIELD+"|"+SchemaKeys.ELEM_RELATION);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            String tagName = childNode.getNodeName();
            if (SchemaKeys.ELEM_FIELD.equals(tagName)) {
                SchemaElement fieldElm = getFieldByNode(childNode);
                fieldElm.setParent(element);
                element.fields.add(fieldElm);
            } else if (SchemaKeys.ELEM_RELATION.equals(tagName)) {
                RelationElm relationElm = getRelationByNode(childNode);
                relationElm.setParent(element);
                element.relations.add(relationElm);
            } else {
                System.err.println("unknow element:"+tagName+" in node "+node.getNodeName());
            }
        }
        return element;
    }
    
    static OptionElm getOptionByNode(Node node) {
        OptionElm element = new OptionElm();
        fillAttributes(element,node);

        element.prompt=getLocalizable( node, SchemaKeys.ELEM_PROMPT);
        element.setTextValue(XmlUtil.getText(node));       
        return element;
    }
    
    static OptionListElm getOptionListByNode(Node node) {
        OptionListElm element = new OptionListElm();
        fillAttributes(element,node);

        Node queryNode = XmlUtil.selectSingleNode(node,SchemaKeys.ELEM_QUERY);
        if (queryNode!=null) {
            element.query = getQueryByNode(queryNode);
            element.query.setParent(element);
        }
        
        NodeList nodeList = XmlUtil.selectNodeList(node,SchemaKeys.ELEM_OPTION);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            OptionElm optionElm = getOptionByNode(childNode);
            optionElm.setParent(element);
            element.options.add(optionElm);
        }
        return element;
    }
    
    static QueryElm getQueryByNode(Node node) {
        QueryElm element = new QueryElm();
        fillAttributes(element,node);

        Node objectNode = XmlUtil.selectSingleNode(node,SchemaKeys.ELEM_OBJECT);
        if (objectNode!=null) {
            ObjectElm objectElm = getObjectByNode(objectNode);
            objectElm.setParent(element);
            element.object = objectElm;
        }
        return element;
    }

    static RelationElm getRelationByNode(Node node) {
        RelationElm element = new RelationElm();
        fillAttributes(element,node);
        
        NodeList nodeList = XmlUtil.selectNodeList(node,SchemaKeys.ELEM_FIELD
                +"|"+SchemaKeys.ELEM_OBJECT);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            String tagName = childNode.getNodeName();
            if (SchemaKeys.ELEM_FIELD.equals(tagName)) {
                SchemaElement fieldElm = getFieldByNode(childNode);
                fieldElm.setParent(element);
                element.fields.add(fieldElm);
            } else if (SchemaKeys.ELEM_OBJECT.equals(tagName)) {
                ObjectElm objectElm = getObjectByNode(childNode);
                objectElm.setParent(element);
                element.object = objectElm;
            } else {
                System.err.println("unknow element:"+tagName+" in node "+node.getNodeName());
            }
        }
        return element;
    }

    static SearchFilterElm getSearchFilterByNode(Node node) {
        SearchFilterElm element = new SearchFilterElm();
        fillAttributes(element,node);
        
        element.name=getLocalizable( node,SchemaKeys.ELEM_NAME);
        
        element.setDefaultValue(XmlUtil.selectSingleNodeText(node,SchemaKeys.ELEM_DEFAULT,null));
        
        element.searchFields.clear();
        
        Node searchFieldsNode = XmlUtil.selectSingleNode(node,SchemaKeys.ELEM_SEARCHFIELDS);
        if (searchFieldsNode!=null) {
            String fields = XmlUtil.getText(searchFieldsNode);
            StringTokenizer st = new StringTokenizer(fields,",");
            while(st.hasMoreTokens()) {
                String field = st.nextToken().trim();
                if ("".equals(field)==false) {
                    element.searchFields.add(field);
                }
            }
            element.setSearchType(XmlUtil.getAttribute(searchFieldsNode,SchemaKeys.ATTR_SEARCHTYPE));
        }
        return element;
    }
    
    static Localizable getLocalizable(Node parentNode, String tagName) {
        Localizable element = new Localizable();
        NodeList nodeList = XmlUtil.selectNodeList(parentNode,"./"+tagName);
        for (int i=0;i<nodeList.getLength();i++){
            Node node = nodeList.item(i);
            String lang = XmlUtil.getAttribute(node,"xml:lang");
            String textValue = XmlUtil.getText(node);
            element.setTextValue(lang,textValue);
        }
        return element;
    }

    public static WizardSchema getWizardSchema(Document document) {
        WizardSchema wizardSchema = new WizardSchema();
        Node rootNode = document.getDocumentElement();
        fillAttributes(wizardSchema,rootNode);
        
        wizardSchema.titles=getLocalizable( rootNode, SchemaKeys.ELEM_TITLE);
        wizardSchema.descriptions=getLocalizable( rootNode, SchemaKeys.ELEM_DESCRIPTION);
        wizardSchema.taskDescriptions=getLocalizable( rootNode, SchemaKeys.ELEM_TASKDESCRIPTION);
        NodeList nodeList = XmlUtil.selectNodeList(rootNode,
                SchemaKeys.ELEM_ACTION+"|"+SchemaKeys.ELEM_LISTS+"|"
                +SchemaKeys.ELEM_FORMSCHEMA);
        List formIdList = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            String tagName = childNode.getNodeName();
            if (SchemaKeys.ELEM_ACTION.equals(tagName)) {
                ActionElm actionElm = getActionByNode(childNode);
                actionElm.setParent(wizardSchema);
                wizardSchema.addAction(actionElm);
            }
            else if (SchemaKeys.ELEM_FORMSCHEMA.equals(tagName)) {
                FormSchemaElm formSchemaElm = getFormSchemaByNode(childNode);
                formSchemaElm.setParent(wizardSchema);
                wizardSchema.addFormSchema(formSchemaElm);
                formIdList.add(formSchemaElm.getId());
            }
            else if (SchemaKeys.ELEM_LISTS.equals(tagName)) {
                NodeList olNodes = childNode.getChildNodes();
                for (int k=0;k<olNodes.getLength();k++){
                    Node optionListNode = olNodes.item(k);
                    OptionListElm optionListElm = getOptionListByNode(optionListNode);
                    optionListElm.setParent(wizardSchema);
                    wizardSchema.addOptionList(optionListElm);
                }
            }
        }
        
        NodeList stepNodes = XmlUtil.selectNodeList(rootNode,SchemaKeys.ELEM_STEPS+"/"+SchemaKeys.ELEM_STEP);
        List stepList = new ArrayList();
        for (int k=0;k<stepNodes.getLength();k++){
            Node stepNode = stepNodes.item(k);
            String formSchemaId = XmlUtil.getAttribute(stepNode,SchemaKeys.ATTR_FORMSCHEMA);
            if (formIdList.contains(formSchemaId)) {
                stepList.add(formSchemaId);
            } else {
                log.warn("Cannot find a form-schema marked as '"+formSchemaId
                        +"' which defined by <step> in wizard schema file.");
            }
        }
        if (stepList.size()>0) {
            wizardSchema.setSteps(stepList);
        } else {
            wizardSchema.setSteps(formIdList);
        }
        return wizardSchema;
    }
    
    /**
     * get field's name from xpath.
     * @param xpath
     * @return
     */
    public static String getFieldNameByXpath(String xpath) {
        String fieldName = null;
        Matcher matcher = PATTERN_XPATH_FIELDNAME.matcher(xpath);
        if (matcher.find()) {
            fieldName = matcher.group(2);   
        }
        if (fieldName==null) {
            fieldName = xpath;
        }
        return fieldName;
    }
    
    /**
     * compare two string,
     * @param s1
     * @param s2
     * @return true, if two parameters is all null or the value is equal; false, otherwise
     */
    public static boolean isEqual(String s1, String s2) {
        if (s1==null && s2==null) {
            return true;
        } else if (s1!=null) {
            return s1.equals(s2);
        }
        return false;
    }

}
