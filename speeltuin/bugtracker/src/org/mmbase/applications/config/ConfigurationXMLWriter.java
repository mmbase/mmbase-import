package org.mmbase.applications.config;

import nanoxml.*;

public abstract class ConfigurationXMLWriter{
    public static String writeNodeManagerConfiguration(NodeManagerConfiguration nodeManagerConfiguration){
        XMLElement builder = new XMLElement();
        builder.setComment("nodemanager name=" + nodeManagerConfiguration.getName());
        builder.setTagName("builder");
        builder.addProperty("maintainer",nodeManagerConfiguration.getMaintainer());
        builder.addProperty("version",nodeManagerConfiguration.getVersion());
        builder.addProperty("extends",nodeManagerConfiguration.getExtends());
        
        XMLElement classFileElement = new XMLElement();
        classFileElement.setTagName("classfile");
        classFileElement.setContent(nodeManagerConfiguration.getClassFile());
        builder.addChild(classFileElement);
        
        XMLElement searchAge = new XMLElement();
        searchAge.setTagName("searchage");
        searchAge.setContent(nodeManagerConfiguration.getSearchAge());
        builder.addChild(searchAge);
        XMLElement names = new XMLElement();
        names.setTagName("names");
        XMLElement singular = new XMLElement();
        singular.setTagName("singular");
        singular.addProperty("xml:lang","en");
        singular.setContent(nodeManagerConfiguration.getName());
        names.addChild(singular);
        
        XMLElement plural = new XMLElement();
        plural.setTagName("plural");
        plural.addProperty("xml:lang","en");
        plural.setContent(nodeManagerConfiguration.getName());
        names.addChild(plural);
        builder.addChild(names);
        
        XMLElement descriptions =new XMLElement();
        descriptions.setTagName("descriptions");
        XMLElement description = new XMLElement();
        description.setTagName("description");
        description.addProperty("xml:lang","en");
        description.setContent(nodeManagerConfiguration.getDescription());
        descriptions.addChild(description);
        
        builder.addChild(descriptions);
        
        XMLElement properties = new XMLElement();
        properties.setTagName("properties");
        builder.addChild(properties);
        
        builder.addChild(
        getXMLFieldConfigurations(
        nodeManagerConfiguration.getFieldConfigurations()
        )
        );
        return builder.toString();
    }
    
    public static XMLElement getXMLFieldConfigurations(FieldConfigurations fieldConfigurations){
        XMLElement fieldList = new XMLElement();
        fieldList.setTagName("fieldlist");
        for (int x = 0 ; x < fieldConfigurations.size(); x++){
            FieldConfiguration fieldConfiguration = fieldConfigurations.getFieldConfiguration(x);
            XMLElement field = new XMLElement();
            field.setTagName("field");
            XMLElement gui = new XMLElement();
            gui.setTagName("gui");
            
            XMLElement guiname = new XMLElement();
            guiname.setTagName("guiname");
            guiname.addProperty("xml:lang","en");
            guiname.setContent(fieldConfiguration.getGUIName());
            gui.addChild(guiname);
            
            XMLElement guitype = new XMLElement();
            guitype.setTagName("guitype");
            guitype.setContent(fieldConfiguration.getGUIType());
            gui.addChild(guitype);
            
            field.addChild(gui);
            
            XMLElement editor = new XMLElement();
            editor.setTagName("editor");
            XMLElement positions = new XMLElement();
            positions.setTagName("positions");
            XMLElement input = new XMLElement();
            input.setTagName("input");
            input.setContent(""+ (x +3));
            positions.addChild(input);
            
            XMLElement list  = new XMLElement();
            list.setTagName("list");
            list.setContent("" + (x +3 ));
            positions.addChild(list);
            
            
            XMLElement search = new XMLElement();
            search.setTagName("search");
            search.setContent("" + (x+ 3));
            positions.addChild(search);
            
            editor.addChild(positions);
            field.addChild(editor);
            
            XMLElement db = new XMLElement();
            db.setTagName("db");
            XMLElement name = new XMLElement();
            name.setTagName("name");
            name.setContent(fieldConfiguration.getName());
            db.addChild(name);
            
            XMLElement type = new XMLElement();
            type.setTagName("type");
            type.addProperty("state","persistent");
            type.addProperty("size",fieldConfiguration.getSize());
            type.setContent(fieldConfiguration.getType());
            
            db.addChild(type);
            field.addChild(db);
            fieldList.addChild(field);
        }
        return fieldList;
    }
}
