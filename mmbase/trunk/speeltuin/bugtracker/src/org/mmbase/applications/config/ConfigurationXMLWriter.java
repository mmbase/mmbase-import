package org.mmbase.applications.config;

import nanoxml.*;

public abstract class ConfigurationXMLWriter{
    public static String writeNodeManagerConfiguration(NodeManagerConfiguration nodeManagerConfiguration){
	XMLElement builder = new XMLElement();
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

	return builder.toString();
    }
}
