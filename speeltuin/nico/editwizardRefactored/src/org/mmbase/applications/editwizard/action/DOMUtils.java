/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.action;

import java.util.*;

import org.mmbase.applications.editwizard.schema.*;
import org.mmbase.applications.editwizard.util.XmlUtil;
import org.w3c.dom.Node;


public class DOMUtils {
    
    private DOMUtils() {
        // utility
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

}
