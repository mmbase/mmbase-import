package org.mmbase.applications.mmbasedoclet;

import com.sun.javadoc.*;
import java.util.*;
import org.mmbase.applications.config.*;
/**
 * MMBaseDoclet is a javadoc doclet to create MMBase an mmbase object model
 * based on javadoc comments in files in a package. The doclet introduces
 * a tags to the standard doclet. defined tags are:<BR>
 *<table BORDER="1" CELLPADDING="3" CELLSPACING="0" WIDTH="100%">
 *<tr BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 *<td COLSPAN=3><font SIZE="+2">
 *<b>Application tags</b></font>that give information about the MMBase application</td>
 *</tr>
 *<tr BGCOLOR="white" CLASS="TableRowColor">
 *<td ALIGN="right" VALIGN="top" WIDTH="1%"><font SIZE="-1">
 *<code>mmbase-application-name</code></font></td>
 *<td>defines the name of the application</td>
 *<td>required</td>
 *</tr>
 *
 *<tr BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 *<td COLSPAN=3><font SIZE="+2">
 *<b>NodeManager tags</b></font> that define node managers</td>
 *</tr>
 *<tr BGCOLOR="white" CLASS="TableRowColor">
 *<td ALIGN="right" VALIGN="top" WIDTH="1%"><font SIZE="-1">
 *<code>mmbase-nodemanager-name</code></font></td>
 *<td>defines the name of a node manager this should always be the first tag to use  when you are trying to define a nodemanager</td>
 *<td>required and unique</td>
 *</tr>
 *<tr BGCOLOR="white" CLASS="TableRowColor">
 *<td ALIGN="right" VALIGN="top" WIDTH="1%"><font SIZE="-1">
 *<code>mmbase-nodemanager-extends</code></font></td>
 *<td>defines what configuration the nodemanager extends values van be <b>object</b> and <b>insrel</b></td>
 *<td>default = "object"</td>
 *</tr>
 *<tr BGCOLOR="white" CLASS="TableRowColor">
 *<td ALIGN="right" VALIGN="top" WIDTH="1%"><font SIZE="-1">
 *<code>mmbase-nodemanager-classfile</code></font></td>
 *<td>defines what java class file the node manager should use. if you haven't writen a special MMObjectBuilder is is best not to define this tag, the class file will be taken from
 * the node manager configuration this object extends</td>
 *<td>not required</td>
 *</tr>
 *<tr BGCOLOR="white" CLASS="TableRowColor">
 *<td ALIGN="right" VALIGN="top" WIDTH="1%"><font SIZE="-1">
 *<code>mmbase-nodemanager-version</code></font></td>
 *<td>integer value giving information about the version of the nodemanager</td>
 *<td>default ="1"</td>
 *</tr>
 *<tr BGCOLOR="white" CLASS="TableRowColor">
 *<td ALIGN="right" VALIGN="top" WIDTH="1%"><font SIZE="-1">
 *<code>mmbase-nodemanager-maintainer</code></font></td>
 *<td>autho/organisation maintaining the node manager</td>
 *<td>default ="mmbase.org"</td>
 *</tr>
 *<tr BGCOLOR="white" CLASS="TableRowColor">
 *<td ALIGN="right" VALIGN="top" WIDTH="1%"><font SIZE="-1">
 *<code>mmbase-nodemanager-searchage</code></font></td>
 *<td>default value in days for wich nodes from this nodemanager should not been show in the editor</td>
 *<td>default ="360"</td>
 *</tr>
 *<tr BGCOLOR="white" CLASS="TableRowColor">
 *<td ALIGN="right" VALIGN="top" WIDTH="1%"><font SIZE="-1">
 *<code>mmbase-nodemanager-field</code></font></td>
 *<td>defines a field for the node manager. this value main contain 3 strings.
 *<ul>
 * <li>the name of the field</li>
 * <li>the type of the field (MMBase types like STRING/INTEGER) default = STRING</li>
 * <li>the size of the field (default is 127 if applicable)
 *</ul>
 * </td>
 *<td>not required</td>
 *</tr>
 *
 *<tr BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 *<td COLSPAN=3><font SIZE="+2">
 *<b>Relation manager tags</b></font> that give information about possible relation between nodemanagers</td>
 *</tr>
 *<tr BGCOLOR="white" CLASS="TableRowColor">
 *<td ALIGN="right" VALIGN="top" WIDTH="1%"><font SIZE="-1">
 *<code>mmbase-relationmanager-name</code></font></td>
 *<td>defines the name of a node manager this should always be the first tag to use  when you are trying to define a nodemanager</td>
 *<td>required but not unique</td>
 *</tr>
 *</table>
 *
 * @author Kees Jongenburger
 * @version $Id: MMBaseDoclet.java,v 1.6 2002-06-28 20:12:58 kees Exp $
 **/
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
