package org.mmbase.applications.crontab;

import org.mmbase.bridge.*;

import org.mmbase.applications.config.*;
import org.mmbase.applications.installer.*;

import java.util.*;
import java.util.jar.*;

import java.io.*;
import nanoxml.*;

public class CronTabApplication implements MMBaseApplication{
    
    /** Creates a new instance of CronTabApplication */
    public CronTabApplication() {
    }
    
    public Configuration getApplicationConfiguration()  throws IOException{
        Configuration config = new Configuration();
        //HACK OR NOT???
        
        ClassLoader cLoader = this.getClass().getClassLoader();
        if (cLoader instanceof ApplicationClassLoader){
            ApplicationClassLoader aLoader = (ApplicationClassLoader)cLoader;
            JarFile jf = aLoader.getJarFile();
            Enumeration enum = jf.entries();
            while(enum.hasMoreElements()){
                JarEntry je = (JarEntry)enum.nextElement();
                
                if (!je.isDirectory() && je.getName().endsWith(".xml")){
                    InputStream i = jf.getInputStream(je);
                    BufferedReader br = new BufferedReader(new InputStreamReader(i));
                    StringBuffer data = new StringBuffer();
                    String line =null;
                    while( (line = br.readLine()) != null){
                        data.append(line);
                        data.append("\n");
                    }
                    try {
                        XMLElement xmle = new XMLElement();
                        xmle.parseString(data.toString());
                        if (xmle.getTagName().equals("application")){
                            ApplicationConfiguration appConfig = ConfigurationXMLReader.createApplicationConfiguration(config, data.toString());
			    System.err.println("adding application " +  appConfig.getName());
                            config.addApplicationConfiguration(appConfig);
                        }
                        if (xmle.getTagName().equals("builder")){
                            NodeManagerConfiguration nodeManagerConfiguration = ConfigurationXMLReader.createNodeManagerConfiguration(data.toString());
			    System.err.println("adding nodemanager " +  nodeManagerConfiguration.getName());
                            config.addNodeManagerConfiguration(nodeManagerConfiguration);
                        }
                    } catch (Exception e){
                    };
                }
            }
        } else {
            System.err.println("this application excpets to run in the ApplicationClassLoader");
        }
        return null;
    }
    
    public void startApplication(Cloud cloud) {
        System.err.println("start app");
    }
    
    public String getDescription() {
        return "crontab clone for MMBase";
    }
    
    public String getName() {
        return "crontab";
    }
    
}
