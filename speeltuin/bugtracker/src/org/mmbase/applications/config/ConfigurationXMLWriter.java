package org.mmbase.applications.config;

import nanoxml.*;
import java.io.*;
import java.util.*;


public abstract class ConfigurationXMLWriter{
    public static void writeApplication(ApplicationConfiguration appconfig) {
        try {
            //make the builders directory
            File builderDir = new File(appconfig.getName() + File.separator + "builders");
            builderDir.mkdirs();
            System.err.println(appconfig.getName());
            NodeManagerConfigurations nodeManagerConfigurations = appconfig.getNodeManagerConfigurations();
            //write the builders
            for (int x =0 ; x < nodeManagerConfigurations.size(); x++){
                NodeManagerConfiguration nodeManagerConfiguration = nodeManagerConfigurations.getNodeManagerConfiguration(x);
                String content = ConfigurationXMLWriter.createNodeManagerConfiguration(nodeManagerConfiguration);
                File file = new File(builderDir,nodeManagerConfiguration.getName() + ".xml");
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		bw.write("<?xml version=\"1.0\"?>\n");
		bw.write("<!DOCTYPE builder PUBLIC \"-//MMBase/DTD builder config 1.1//EN\" \"http://www.mmbase.org/dtd/builder_1_1.dtd\">\n");

                bw.write(content);
                bw.flush();
                bw.close();
                
            }
            //create the application file
            String content = createApplicationConfiguration(appconfig);
            File appFile = new File(appconfig.getName() + ".xml");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(appFile)));
	    bw.write("<?xml version=\"1.0\" ?>\n");
            bw.write("<!DOCTYPE application PUBLIC \"//MMBase - application//\" \"http://www.mmbase.org/dtd/application_1_0.dtd\">\n");
            bw.write(content);
            bw.flush();
            bw.close();
            
            
        } catch (FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch (IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }
    public static String createApplicationConfiguration(ApplicationConfiguration appconfig){
        XMLElement xmle = new XMLElement();
        xmle.setTagName("application");
        xmle.addProperty("name",appconfig.getName());
        xmle.addProperty("maintainer","mmbase.org");
        xmle.addProperty("version","1");
        xmle.addProperty("auto-deploy","true");
        
        
        XMLElement neededBuilderList = new XMLElement();
        neededBuilderList.setTagName("neededbuilderlist");
        NodeManagerConfigurations nodeManagerConfigurations = appconfig.getNodeManagerConfigurations();
        for (int x =0 ; x < nodeManagerConfigurations.size(); x++){
            NodeManagerConfiguration nc = nodeManagerConfigurations.getNodeManagerConfiguration(x);
            XMLElement builder = new XMLElement();
            builder.setTagName("builder");
            builder.addProperty("maintainer",nc.getMaintainer());
            builder.addProperty("version",nc.getVersion());
            builder.setContent(nc.getName());
            neededBuilderList.addChild(builder);
            
        }
        
        xmle.addChild(neededBuilderList);
        
        XMLElement neededRelationDefinitionList = new XMLElement();
        neededRelationDefinitionList.setTagName("neededreldeflist");
        
        //reldefs
        RelationManagerConfigurations relationManagerConfigurations = appconfig.getRelationManagerConfigurations();
        Hashtable  at =new Hashtable();
        for (int x =0 ; x < relationManagerConfigurations.size(); x++){
            RelationManagerConfiguration rc = relationManagerConfigurations.getRelationManagerConfiguration(x);
            if (at.get(rc.getName()) == null){
                XMLElement relation = new XMLElement();
                relation.setTagName("reldef");
                relation.addProperty("source",rc.getName());
                relation.addProperty("target",rc.getName());
                relation.addProperty("guisourcename",rc.getName());
                relation.addProperty("guitargetname",rc.getName());
                relation.addProperty("direction",rc.getDirectionality());
                relation.addProperty("builder",rc.getNodeManagerName());
                neededRelationDefinitionList.addChild(relation);
                at.put(rc.getName(),rc.getName());
            }
            
        }
        xmle.addChild(neededRelationDefinitionList);
        
        XMLElement allowedRelationList = new XMLElement();
        allowedRelationList.setTagName("allowedrelationlist");
        
        for (int x =0 ; x < relationManagerConfigurations.size(); x++){
            RelationManagerConfiguration rc = relationManagerConfigurations.getRelationManagerConfiguration(x);
            
            XMLElement relation = new XMLElement();
            relation.setTagName("relation");
            relation.addProperty("from",rc.getSourceNodeManagerName());
            relation.addProperty("to",rc.getDestinationNodeManagerName());
            relation.addProperty("type",rc.getName());
            allowedRelationList.addChild(relation);
        }
        
        xmle.addChild(allowedRelationList);
        
        //create a list of builders that a not relation builders
        XMLElement datasourceList = new XMLElement();
        datasourceList.setTagName("datasourcelist");
        XMLElement relationsourceList = new XMLElement();
        relationsourceList.setTagName("relationsourcelist");
        
        for (int x =0 ; x < nodeManagerConfigurations.size(); x++){
            NodeManagerConfiguration nc = nodeManagerConfigurations.getNodeManagerConfiguration(x);
            
            XMLElement element = new XMLElement();
            
            element.addProperty("builder",nc.getName());
            element.addProperty("path",appconfig.getName() + File.separator +nc.getName() + ".xml");
            
            if (nc.getExtends().equals("insrel")){
                element.setTagName("relationsource");
                relationsourceList.addChild(element);
            } else {
                element.setTagName("datasource");
                datasourceList.addChild(element);
                
            }
            
        }
        xmle.addChild(datasourceList);
        xmle.addChild(relationsourceList);
        
        XMLElement contextsourceList = new XMLElement();
        contextsourceList.setTagName("contextsourcelist");
        XMLElement contextsource = new XMLElement();
        contextsource.setTagName("contextsource");
        contextsource.addProperty("path",appconfig.getName() + File.separator + "backup.xml");
        contextsource.addProperty("type","depth");
        contextsource.addProperty("goal","backup");
        contextsourceList.addChild(contextsource);
        xmle.addChild(contextsourceList);
        
        XMLElement description = new XMLElement();
        description.setTagName("description");
        description.setContent("desc");
        xmle.addChild(description);
        XMLElement installNotice = new XMLElement();
        installNotice.setTagName("install-notice");
        installNotice.setContent("installed");
        xmle.addChild(installNotice);
        return xmle.toString();
        
    }
    
    public static String createNodeManagerConfiguration(NodeManagerConfiguration nodeManagerConfiguration){
        XMLElement builder = new XMLElement();
        builder.setComment(nodeManagerConfiguration.getName());
        builder.setTagName("builder");
        builder.addProperty("maintainer",nodeManagerConfiguration.getMaintainer());
        builder.addProperty("version",nodeManagerConfiguration.getVersion());
        builder.addProperty("extends",nodeManagerConfiguration.getExtends());
        
	if (nodeManagerConfiguration.getClassFile() != null){
		XMLElement classFileElement = new XMLElement();
		classFileElement.setTagName("classfile");
		classFileElement.setContent(nodeManagerConfiguration.getClassFile());
		builder.addChild(classFileElement);
        }
        
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
            type.addProperty("notnull","false");
            if (fieldConfiguration.getSize() != null){
                type.addProperty("size",fieldConfiguration.getSize());
            }
            type.setContent(fieldConfiguration.getType());
            
            db.addChild(type);
            field.addChild(db);
            fieldList.addChild(field);
        }
        return fieldList;
    }
}
