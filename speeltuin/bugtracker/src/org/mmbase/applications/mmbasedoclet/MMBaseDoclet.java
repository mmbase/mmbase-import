package org.mmbase.applications.mmbasedoclet;

import com.sun.javadoc.*;
import java.util.*;
import org.mmbase.applications.config.*;

public class MMBaseDoclet{
    
    public static boolean start(RootDoc root){
        
        //System.out.println("MMBaseDoclet");
        writeContents(root.classes());
        return true;
    }
    
    private static void writeContents(ClassDoc[] classes) {
        //Vector nodeManagers = new Vector();
        //Vector relationManager = new Vector();
        DocletApplicationConfiguration appconfig= new DocletApplicationConfiguration();
        for (int i=0; i < classes.length; i++) {
            //System.err.println("class: " +  classes[i].name() );
            Tag[] tags = classes[i].tags();
            for (int tagcount =0 ; tagcount < tags.length; tagcount++){
                String tagName = tags[tagcount].name();
                String tagContent = tags[tagcount].text();
                
                if (tagName.startsWith("@mmbase-application-name")) {
			appconfig.setName(tagContent);
	        }
                if (tagName.startsWith("@mmbase-nodemanager-name")) {
                    int startIndex = tagcount;
                    while(
                    tagcount < tags.length-1 &&
                    tags[tagcount +1].name().startsWith("@mmbase-nodemanager")
                    && ! tags[tagcount +1].name().startsWith("@mmbase-nodemanager-name")){
                        tagcount ++;
                    }
                    appconfig.addNodeManagerConfiguration(createNodeManagerConfiguration(tags,startIndex,tagcount));
                    //System.out.println("   " + tags[tagcount].name() + ": " + tags[tagcount].text());
                }
                if (tagName.startsWith("@mmbase-relationmanager-name")) {
                    int startIndex = tagcount;
                    while(
                    tagcount < tags.length-1 &&
                    tags[tagcount +1].name().startsWith("@mmbase-relationmanager")
                    && ! tags[tagcount +1].name().startsWith("@mmbase-relationmanager-name")){
                        tagcount ++;
                    }
                    appconfig.addRelationManagerConfiguration(createRelationManagerConfiguration(tags,startIndex,tagcount));
                    //System.out.println("   " + tags[tagcount].name() + ": " + tags[tagcount].text());
                }
            }
        }
	ConfigurationXMLWriter.writeApplication(appconfig);
        
    }
    
    public static DocletNodeManagerConfiguration createNodeManagerConfiguration(Tag[] tags,int startIndex, int endIndex){
        
        DocletNodeManagerConfiguration nodeManagerConfig = new DocletNodeManagerConfiguration();
        
        for (int tagcount = startIndex ; tagcount <= endIndex;tagcount++){
            String name = tags[tagcount].name().substring("mmbase-nodemanager-".length() + 1);
            String text = tags[tagcount].text();
            if (name.equals("name")){
                nodeManagerConfig.setName(text);
            } else if (name.equals("maintainer")){
                nodeManagerConfig.setMaintainer(text);
            } else if (name.equals("version")){
                nodeManagerConfig.setVersion(text);
            } else if (name.equals("classfile")){
                nodeManagerConfig.setClassFile(text);
            } else if (name.equals("extends")){
                nodeManagerConfig.setExtends(text);
            } else if (name.equals("searchage")){
                nodeManagerConfig.setSearchAge(text);
            } else if (name.equals("field")){
                nodeManagerConfig.addFieldConfiguration(createFieldConfiguration(text));
            } else {
                System.err.println("unknown tag   " + name +  ": " + tags[tagcount].text());
            }
        }
        return nodeManagerConfig;
    }
    
    public static DocletRelationManagerConfiguration createRelationManagerConfiguration(Tag[] tags,int startIndex, int endIndex){
        
        DocletRelationManagerConfiguration relationManagerConfig = new DocletRelationManagerConfiguration();
        
	//@mmbase-relationmanager-name: maintainer
	//@mmbase-relationmanager-nodemanager: insrel
	//@mmbase-relationmanager-source: bugtracker
	//@mmbase-relationmanager-destination: bugtrackeruser
   	//@mmbase-relationmanager-directionality: unidirectional

        for (int tagcount = startIndex ; tagcount <= endIndex;tagcount++){
            String name = tags[tagcount].name().substring("mmbase-relationmanager-".length() + 1);
            String text = tags[tagcount].text();
            if (name.equals("name")){
                relationManagerConfig.setName(text);
            } else if (name.equals("nodemanager")){
                relationManagerConfig.setNodeManagerName(text);
            } else if (name.equals("source")){
                relationManagerConfig.setSourceNodeManagerName(text);
            } else if (name.equals("destination")){
                relationManagerConfig.setDestinationNodeManagerName(text);
            } else if (name.equals("directionality")){
                relationManagerConfig.setDirectionality(text);
            } else {
                System.err.println("unknown tag   " + name +  ": " + tags[tagcount].text());
            }
        }
        //System.out.println();
        return relationManagerConfig;
        
    }
    
    public static DocletFieldConfiguration createFieldConfiguration(String data){
        String name = "empty";
        String type = "STRING";
        String size = null;
        
        StringTokenizer st = new StringTokenizer(data," ");
        if (st.hasMoreTokens()){
            name = st.nextToken();
        }
        if (st.hasMoreTokens()){
            type = st.nextToken();
        }
        if (st.hasMoreTokens()){
            size = st.nextToken();
        }
        return new DocletFieldConfiguration(name,type,size);
    }
}
