package org.mmbase.applications.mmbasedoclet;

import com.sun.javadoc.*;
import java.util.*;

public class MMBaseDoclet{
    
    public static boolean start(RootDoc root){
        
        System.out.println("MMBaseDoclet");
        writeContents(root.classes());
        return true;
    }
    
    private static void writeContents(ClassDoc[] classes) {
        //Vector nodeManagers = new Vector();
        //Vector relationManager = new Vector();
        DocletApplicationConfiguration appconfig= new DocletApplicationConfiguration();
        for (int i=0; i < classes.length; i++) {
            System.err.println("class: " +  classes[i].name() );
            Tag[] tags = classes[i].tags();
            for (int tagcount =0 ; tagcount < tags.length; tagcount++){
                String tagName = tags[tagcount].name();
                String tagContent = tags[tagcount].text();
                
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
                    appconfig.addNodeManagerConfiguration(createRelationManagerConfiguration(tags,startIndex,tagcount));
                    //System.out.println("   " + tags[tagcount].name() + ": " + tags[tagcount].text());
                }
            }
        }
        
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
            } else if (name.equals("field")){
                nodeManagerConfig.addFieldConfiguration(createFieldConfiguration(text));
            } else {
                System.out.println("unknown tag   " + name +  ": " + tags[tagcount].text());
            }
        }
        return nodeManagerConfig;
        
    }
    
    public static DocletRelationManagerConfiguration createRelationManagerConfiguration(Tag[] tags,int startIndex, int endIndex){
        
        DocletRelationManagerConfiguration relationManagerConfig = new DocletRelationManagerConfiguration();
        
        for (int tagcount = startIndex ; tagcount <= endIndex;tagcount++){
            System.out.println("   " + tags[tagcount].name() + ": " + tags[tagcount].text());
        }
        return relationManagerConfig;
        
    }
    
    public static DocletFieldConfiguration createFieldConfiguration(String data){
        String name = "empty";
        String type = "STRING";
        String size="";
        
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
