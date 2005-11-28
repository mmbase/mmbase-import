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
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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
    
    public static Node copyElementToNode(Node parentNode,Object obj, boolean recursive) {
        if (obj==null) {
            return null;
        }
        Node node = null;
        Class clazz = obj.getClass();
        if (clazz==ActionElm.class) {
            ActionElm element = (ActionElm)obj;
            node =copyToNode(parentNode,element,recursive);
            
        } else if (clazz==CommandElm.class) {
            CommandElm element = (CommandElm)obj;
            node =copyToNode(parentNode,element,recursive);

        } else if (clazz==FieldElm.class) {
            FieldElm element = (FieldElm)obj;
            node =copyToNode(parentNode,element,recursive);
            
        } else if (clazz==FieldSetElm.class) {
            FieldSetElm element = (FieldSetElm)obj;
            node = copyToNode(parentNode,element,recursive);
            
        } else if (clazz==FormSchemaElm.class) {
            FormSchemaElm element = (FormSchemaElm)obj;
            node = copyToNode(parentNode,element,recursive);
            
        } else if (clazz==ItemElm.class) {
            ItemElm element = (ItemElm)obj;
            node = copyToNode(parentNode,element,recursive);
            
        } else if (clazz==ListElm.class) {
            ListElm element = (ListElm)obj;
            node = copyToNode(parentNode,element,recursive);
            
        } else if (clazz==ObjectElm.class) {
            ObjectElm element = (ObjectElm)obj;
            node = copyToNode(parentNode,element,recursive);
            
        } else if (clazz==OptionElm.class) {
            OptionElm element = (OptionElm)obj;
            node = copyToNode(parentNode,element,recursive);

        } else if (clazz==OptionListElm.class) {
            OptionListElm element = (OptionListElm)obj;
            node = copyToNode(parentNode,element,recursive);

        } else if (clazz==QueryElm.class) {
            QueryElm element = (QueryElm)obj;
            node = copyToNode(parentNode,element,recursive);
            
        } else if (clazz==RelationElm.class) {
            RelationElm element = (RelationElm)obj;
            node = copyToNode(parentNode,element,recursive);
            
        } else if (clazz==SearchFilterElm.class) {
            SearchFilterElm element = (SearchFilterElm)obj;
            node = copyToNode(parentNode,element,recursive);
            
        } else {
            System.err.println("some element type missed:"+clazz);
        }
        
        return node;
    }
    
    public static void copyAttributesToNode(Node node, Map attributes) {
        if (attributes==null){
            return;
        }
        for (Iterator iterator=attributes.entrySet().iterator();iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next(); 
            String key = (String) entry.getKey();
            if (key.startsWith("_")) {
                // if attribute is not come from attribute of schema node, ignore them
                continue;
            }
            XmlUtil.setAttribute(node,key,(String) entry.getValue());
        }
    }
        
    public static void copyToNode(Node parentNode, Collection schemaElmList, boolean recursive) {
        
        for (Iterator iterator=schemaElmList.iterator();iterator.hasNext();){
            Object obj = iterator.next();
            copyElementToNode(parentNode,obj, recursive);
        }
    }
    
    public static void copyToNode(Node parentNode, Localizable localValues, String tagName, String language){
        String lang = language==null? "":language;
        String value = localValues.getTextValue(lang);
        Map attributes = new HashMap();
        attributes.clear();
        attributes.put(SchemaKeys.ATTR_LANG,lang);
        XmlUtil.addChildElement(parentNode,tagName,attributes,value);
    }
    
    public static void copyToNode(Node parentNode, Localizable localValues, String tagName){
        Map attributes = new TreeMap();
        Map langMap = localValues.getAttributes();
        Iterator iterator = langMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next(); 
            String lang = (String) entry.getKey();
            String value = (String) entry.getValue();
            attributes.clear();
            attributes.put(SchemaKeys.ATTR_LANG,lang);
            XmlUtil.addChildElement(parentNode,tagName,attributes,value);
        }
    }
    
    
    public static Node copyToNode(Node parentNode, ActionElm element, boolean recursive) {
        
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());

//        attributes.put(SchemaKeys.ATTR_TYPE,element.type);
        
        Node actionNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_ACTION,
                attributes,null);
        
        if (recursive) {
            copyToNode(actionNode,element.prompt,SchemaKeys.ELEM_PROMPT);
            copyToNode(actionNode,element.description,SchemaKeys.ELEM_DESCRIPTION);

            List childNodeList = new ArrayList();
            childNodeList.addAll(element.fields);
            if (element.object!=null) {
                childNodeList.add(element.object);
            }
            childNodeList.addAll(element.relations);
            copyToNode(actionNode,childNodeList,recursive);
        }
        return actionNode;
    }
    
    public static Node copyToNode(Node parentNode, CommandElm element, boolean recursive) {
        
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());
//        attributes.put(SchemaKeys.ATTR_NAME,element.name);
//        attributes.put(SchemaKeys.ATTR_AGE,element.age);
//        attributes.put(SchemaKeys.ATTR_FIELDS,element.fields);
//        attributes.put(SchemaKeys.ATTR_FILTERREQUIRED,element.filterRequired);
//        attributes.put(SchemaKeys.ATTR_INLINE,element.inline);
//        attributes.put(SchemaKeys.ATTR_NODEPATH,element.nodePath);
//        attributes.put(SchemaKeys.ATTR_OBJECTNUMBER,element.objectNumber);
//        attributes.put(SchemaKeys.ATTR_WIZARDNAME,element.wizardName);
//        attributes.put(SchemaKeys.ATTR_ORDERBY,element.orderBy);
//        attributes.put(SchemaKeys.ATTR_DIRECTIONS,element.directions);
//        attributes.put(SchemaKeys.ATTR_COMMAND,element.command);
//        attributes.put(SchemaKeys.ATTR_STARTNODES,element.startnodes);
//        attributes.put(SchemaKeys.ATTR_CONSTRAINTS,element.constraints);
//        attributes.put(SchemaKeys.ATTR_ORIGIN,element.origin);
//        attributes.put(SchemaKeys.ATTR_SEARCHDIR,element.searchDir);
        
        Node commandNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_COMMAND,
                attributes,null);
        
        if (recursive) {
            copyToNode(commandNode,element.prompt,SchemaKeys.ELEM_PROMPT);
    
            copyToNode(commandNode,element.searchFilters,recursive);
        }
        return commandNode;
    }
    
    public static Node copyToNode(Node parentNode, FieldElm element, boolean recursive) {
        
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());
//        attributes.put(SchemaKeys.ATTR_DTPATTERN,element.dtPattern);
//        attributes.put(SchemaKeys.ATTR_DTMAX,element.dtMax);
//        attributes.put(SchemaKeys.ATTR_DTMIN,element.dtMin);
//        attributes.put(SchemaKeys.ATTR_DTMAXLENGTH,element.dtMaxLength);
//        attributes.put(SchemaKeys.ATTR_DTMINLENGTH,element.dtMinLength);
//        attributes.put(SchemaKeys.ATTR_DTREQUIRED,element.dtRequired);
//        attributes.put(SchemaKeys.ATTR_FDATAPATH,element.fDataPath);
//        attributes.put(SchemaKeys.ATTR_DTTYPE,element.dtType);
//        attributes.put(SchemaKeys.ATTR_FTYPE,element.fType);
//        attributes.put(SchemaKeys.ATTR_SIZE,element.size);
//        attributes.put(SchemaKeys.ATTR_NAME,element.name);
//        attributes.put(SchemaKeys.ATTR_ROWS,element.rows);
//        attributes.put(SchemaKeys.ATTR_INLINE,element.inline);
//        attributes.put(SchemaKeys.ATTR_WIZARDNAME,element.wizardName);
//        attributes.put(SchemaKeys.ATTR_OBJECTNUMBER,element.objectNumber);
//        attributes.put(SchemaKeys.ATTR_HIDE,element.hide);
        
        String nodeValue = element.getDefaultValue()==null?"":element.getDefaultValue();
        Node fieldNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_FIELD,
                attributes,nodeValue);
        
        if (recursive) {
            copyToNode(fieldNode,element.prompt,SchemaKeys.ELEM_PROMPT);
            copyToNode(fieldNode,element.description,SchemaKeys.ELEM_DESCRIPTION);
    
            if (element.optionList!=null) {
                copyToNode(fieldNode,element.optionList,recursive);
            }
            
            if (element.getPrefix()!=null&& element.getPrefix().length()>0) {
                XmlUtil.createAndAppendNode(fieldNode,SchemaKeys.ELEM_PREFIX,element.getPrefix());
            }
            if (element.getPostfix()!=null&& element.getPostfix().length()>0) {
                XmlUtil.createAndAppendNode(fieldNode,SchemaKeys.ELEM_POSTFIX,element.getPostfix());
            }
        }
        return fieldNode;
    }

    public static Node copyToNode(Node parentNode, FieldSetElm element, boolean recursive) {

        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());
        
        Node fieldSetNode = XmlUtil.addChildElement(parentNode,
                SchemaKeys.ELEM_FIELDSET,attributes,null);
        if (recursive) {
            copyToNode(fieldSetNode,element.prompt,SchemaKeys.ELEM_PROMPT);
            copyToNode(fieldSetNode,element.fields,recursive);
        }
        return fieldSetNode;
    }
    
    public static Node copyToNode(Node parentNode, FormSchemaElm element, boolean recursive) {
        
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());

        Node formSchemaNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_FORMSCHEMA,
                attributes,null);
        
        if (recursive) {
            copyToNode(formSchemaNode,element.title,SchemaKeys.ELEM_TITLE);
            copyToNode(formSchemaNode,element.subTitle,SchemaKeys.ELEM_SUBTITLE);
    
            List childNodeList = new ArrayList();
            childNodeList.addAll(element.fields);
            childNodeList.addAll(element.fieldSets);
            childNodeList.addAll(element.lists);
            copyToNode(formSchemaNode,childNodeList,recursive);
        }
        return formSchemaNode;
    }
    
    
    public static Node copyToNode(Node parentNode, ItemElm element, boolean recursive) {
        
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());

        Node itemNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_ITEM,
                attributes,null);
        
        if (recursive) {
            copyToNode(itemNode,element.title,SchemaKeys.ELEM_TITLE);
            copyToNode(itemNode,element.description,SchemaKeys.ELEM_DESCRIPTION);
    
            List childNodeList = new ArrayList();
            childNodeList.addAll(element.fields);
            childNodeList.addAll(element.fieldSets);
            childNodeList.addAll(element.lists);
            copyToNode(itemNode,childNodeList,recursive);
        }
        return itemNode;
    }
    
    public static Node copyToNode(Node parentNode, ListElm element, boolean recursive) {
        
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());

        Node listNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_LIST,
                attributes,null);
        
        if (recursive) {
            copyToNode(listNode,element.title,SchemaKeys.ELEM_TITLE);
            copyToNode(listNode,element.description,SchemaKeys.ELEM_DESCRIPTION);
    
            List childNodeList = new ArrayList();
            childNodeList.addAll(element.actions);
            childNodeList.addAll(element.commands);
            childNodeList.add(element.item);
            copyToNode(listNode,childNodeList,recursive);
        }
        
        return listNode;
    }
    
    public static Node copyToNode(Node parentNode, ObjectElm element, boolean recursive) {
        
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());

        Node objectNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_OBJECT,
                attributes,null);
        
        if (recursive) {
            List childNodeList = new ArrayList();
            childNodeList.addAll(element.fields);
            childNodeList.addAll(element.relations);
            copyToNode(objectNode,childNodeList,recursive);
        }
        
        return objectNode;
    }
    
    public static Node copyToNode(Node parentNode, OptionElm element, boolean recursive) {
        
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());

        Node optionNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_OPTION,
                attributes,element.getTextValue());
        
        if (recursive) {
            copyToNode(optionNode,element.prompt,SchemaKeys.ELEM_PROMPT);
        }
        
        return optionNode;
    }
    
    
    public static Node copyToNode(Node parentNode, OptionListElm element, boolean recursive) {
        
        if (element==null) {
            return null;
        }
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());

        Node optionListNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_OPTIONLIST,
                attributes,null);
        
        if (recursive) {
            List childNodeList = new ArrayList();
            childNodeList.addAll(element.options);
            childNodeList.add(element.query);
            copyToNode(optionListNode,childNodeList,recursive);
        }
        
        return optionListNode;
    }
    
    public static Node copyToNode(Node parentNode, QueryElm element, boolean recursive) {
        
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());

        Node queryNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_QUERY,
                attributes,null);
        
        if (recursive) {
            copyToNode(queryNode,element.object,recursive);
        }
        
        return queryNode;
    }
    
    public static Node copyToNode(Node parentNode, RelationElm element, boolean recursive) {
        
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());

        Node relationNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_RELATION,
                attributes,null);

        if (recursive) {
            List childNodeList = new ArrayList();
            childNodeList.addAll(element.fields);
            childNodeList.add(element.object);
            copyToNode(relationNode,childNodeList,recursive);
        }
            
        return relationNode;
    }
    
    public static Node copyToNode(Node parentNode, SearchFilterElm element, boolean recursive) {
        
        Map attributes = new TreeMap();
        attributes.putAll(element.getAttributes());

        Node searchFilterNode = XmlUtil.addChildElement(parentNode,
                SchemaKeys.ELEM_SEARCHFILTER, attributes, null);
        
        if (recursive) {
            copyToNode(searchFilterNode,element.name,SchemaKeys.ELEM_NAME);
        
            StringBuffer buffer = new StringBuffer();
            for (int i=0;i<element.searchFields.size();i++) {
                if (i>0) {
                    buffer.append(",");
                }
                buffer.append(element.searchFields.get(i));
            }
            
            Node searchFieldsNode = XmlUtil.createAndAppendNode(searchFilterNode,
                    SchemaKeys.ELEM_SEARCHFIELDS, buffer.toString());
            if (element.getSearchType()!=null && "".equals(element.getSearchType())==false) {
                XmlUtil.setAttribute(searchFieldsNode,SchemaKeys.ATTR_SEARCHTYPE,element.getSearchType());
            }
            
            if (element.getDefaultValue()!=null && element.getDefaultValue().length()>0) {
                XmlUtil.createAndAppendNode(searchFilterNode,SchemaKeys.ELEM_DEFAULT,element.getDefaultValue());
            }
        }
        return searchFilterNode;
    }
    
    public static String getDataTypeName(Field field) {
        return getDataTypeName(field.getType());
    }
    
    public static String getDataTypeName(int type) {
        String typeName = null;
        switch (type) {
            case Field.TYPE_INTEGER:
            case Field.TYPE_NODE:
                typeName = "int";
                break;
            case Field.TYPE_LONG:
                typeName="long";
                break;
            case Field.TYPE_FLOAT:
                typeName="float";
                break;
            case Field.TYPE_DOUBLE:
                typeName="double";
                break;
            case Field.TYPE_BINARY:
                typeName="binary";
                break;
            case Field.TYPE_DATETIME:
                typeName = "datetime";
                break;
            case Field.TYPE_BOOLEAN:
                typeName = "boolean";
                break;
            default:
                typeName = "string";
        }
        return typeName;
    }
    
    /**
     * retrieve gui name of the field
     * @param field the field definetion
     * @return
     */
    public static String getGUITypeName(Field field) {
        // guitype
        String guiType = field.getDataType().getName();
        if (guiType.indexOf("/")==-1) {
            if (guiType.equals("field")) {
                guiType = "string/text";
            } else if (guiType.equals("string")) {
                guiType = "string/line";
            } else if (guiType.equals("eventtime")) {
                guiType = "datetime/datetime";
            } else if (guiType.equals("newimage")) {
                guiType = "binary/image";
            } else if (guiType.equals("newfile")) {
                guiType = "binary/file";
            } else {
                String dttype = getDataTypeName(field);
                if (guiType.equals("")) {
                    //TODO: dttype+"/"+dtype is not useful
                    guiType = dttype + "/" +dttype;
                } else {
                    guiType = dttype + "/" + guiType;
                }
            }
        }
        return guiType;
    }
    
    /**
     * get type name (part of the gui type) of the field
     * @param field
     * @return
     */
    public static String getFieldTypeName(Field field) {
        // guitype
        String guiType = field.getDataType().getName();
        if (guiType.indexOf("/")==-1) {
            if (guiType.equals("field")) {
                guiType = "text";
            } else if (guiType.equals("string")) {
                guiType = "line";
            } else if (guiType.equals("eventtime")) {
                guiType = "datetime";
            } else if (guiType.equals("newimage")) {
                guiType = "image";
            } else if (guiType.equals("newfile")) {
                guiType = "file";
            } else {
                guiType = getDataTypeName(field);
            }
        }
        return guiType;
    }
    
    /**
     * merge the constraints defined by builder and wizard-schema
     * @param fieldElm field constraints defined in wizard-schema
     * @param field field constraints defined in object builder
     */
    public static void mergeConstraints(FieldElm fieldElm, Field field){
        if (field==null) {
            //no constraints found. so forget it.
            if (log.isDebugEnabled()) {
                log.debug("WizardSchema.mergeConstraints:  field with name " + fieldElm.getName() +
                        "could not be retrieved from the object.");
            }
            return;
        }
        // load all constraints + merge them with the settings in the schema definition
        String objectType = field.getNodeManager().getName();
        String fieldName = field.getName();

        if ((objectType == null) || (fieldName == null)) {
            if (log.isDebugEnabled()) {
                log.debug("wizard.mergeConstraints: objecttype or fieldname " +
                        "could not be retrieved for this field. Field:");
            }

            return;
        }

        mergeFieldAttributes(fieldElm,field);
    }
    
    private static void mergeFieldAttributes(FieldElm fieldElm, Field field)
    {
        String xmlSchemaType = null;
        String guiType = getGUITypeName(field);
        int pos = guiType.indexOf("/");

        if (pos != -1) {
            xmlSchemaType = guiType.substring(0, pos);
            guiType = guiType.substring(pos + 1);
        }

        // dttype?
        String ftype = fieldElm.getFtype();
        String dttype = fieldElm.getDttype();

        if (dttype == null) {
            dttype = xmlSchemaType;
        }

        if (ftype == null) {
            // import guitype or ftype
            // this is a qualifier, not a real type
            ftype = guiType;
        }

        // backward compatibility.
        // switch old 'upload' to 'binary'
        // The old format used the following convention:
        // ftype="upload" + dttype="image" -> upload an image
        // ftype="upload" + dttype="upload" -> upload a file
        // ftype="image" -> display an image
        // The new format usesd 'binary' a s a dftytype,a nd 'image' or 'file'
        // as a ftype,
        // as follows:
        // ftype="image" + dttype="binary" -> upload an image
        // ftype="file" + dttype="binary" -> upload a file
        // ftype="image" + dttype="data" -> display an image
        // ftype="file" + dttype="data" -> display a link to a file
        // code below changes old format wizards to the new format.
        if ("upload".equals(ftype)) {
            if ("image".equals(dttype)) {
                ftype = "image";
                dttype = "binary";
            }
            else {
                ftype = "file";
                dttype = "binary";
            }
        }
        else
            if ("image".equals(ftype)) {
                // check if dttype is binary, else set to data
                if (!"binary".equals(dttype)) {
                    dttype = "data";
                }
            }

        // in the old format, ftype was date, while dttype was date,datetime, or
        // time
        // In the new format, this is reversed (dttype contains the base
        // datatype,
        // ftype the format in which to enter it)
        if ("date".equals(dttype) || "time".equals(dttype)) {
            ftype = dttype;
            dttype = "datetime";
        }

        // in the old format, 'html' could also be assigned to dttype
        // in the new format this is an ftype (the dttype is string)
        if ("html".equals(dttype)) {
            ftype = "html";
            dttype = "string";
        }

        // fix for old format type 'wizard'
        if ("wizard".equals(ftype)) {
            ftype = "startwizard";
        }
        
        fieldElm.setAttribute(SchemaKeys.ATTR_DTTYPE,dttype);
        fieldElm.setAttribute(SchemaKeys.ATTR_FTYPE, ftype);
        
        // add guiname as prompt
        String guiName = getValue(field.getGUIName(),"");
        if (hasValue(fieldElm.prompt.getTextValue())==false) {
            fieldElm.prompt.setTextValue(guiName);
        }

        // add description as helptext
        String description = getValue(field.getDescription(), "");
        if (hasValue(fieldElm.description.getTextValue())==false) {
            fieldElm.description.setTextValue(description);
        }

        // process requiredness
        String required = field.isRequired() ? "true" : "false";
        if (fieldElm.getDtrequired()==null) {
            // if unknown, determine requiredness according to MMBase
            fieldElm.setAttribute(SchemaKeys.ATTR_DTREQUIRED,required);
        }

        // process min/maxlength for strings
        if ("string".equals(dttype) || "html".equals(dttype)) {
            if (hasValue(fieldElm.getDtminlength()) == false) {
                // manually set minlength if required is true
                if ("true".equals(fieldElm.getDtrequired())) {
                    fieldElm.setAttribute(SchemaKeys.ATTR_DTMINLENGTH,"1");
                }
            }

            if (hasValue(fieldElm.getDtmaxlength()) == false) {
                int maxlen = field.getMaxLength();

                // manually set maxlength if given
                // ignore sizes smaller than 1 and larger than 255
                if ((maxlen > 0) && (maxlen < 256)) {
                    fieldElm.setAttribute(SchemaKeys.ATTR_DTMAXLENGTH,""+maxlen);
                }
            }
        }
    }
    
    /**
     * merge constraints of the field into schema element
     * @param fieldElm
     * @param objectType
     */
    public static void mergeConstraints(FieldElm fieldElm, String objectType, Cloud cloud) {
        // the object type could not be retrieve by schema. so it must be provided
        // by method caller. in most case, it is get by object number or specified 
        // by schema element "action[@type='create']/object@type
        // SchemaElement parentElm = fieldElm.getParent().getAttribute("type");
        NodeManager nm = cloud.getNodeManager(objectType);
        String fieldName = fieldElm.getName();
        if (fieldName==null) {
            if (log.isDebugEnabled()) {
                log.warn("the name of field in object[@type="+objectType+"] is missing, could not retrieve the constraints. ignore this field!");
            }
            return;
        }
        Field field = nm.getField(fieldName);
        SchemaUtils.mergeConstraints(fieldElm,field);
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

    public static boolean hasValue(String value) {
        return value!=null && !"".equals(value);
    }

    public static String getValue(String value, String defaultValue){
        if (hasValue(value)==false) {
            return defaultValue;
        }
        return value;
    }

}
