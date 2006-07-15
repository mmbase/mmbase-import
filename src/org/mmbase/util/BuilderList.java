/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;


/**
 * Gives an xml-representation of a dir structure with builders
 * @since mmbase 1.6
 * @author Gerard van Enk, Pierre van Rooden
 * @version $Id: BuilderList.java,v 1.5 2003-03-04 13:28:50 nico Exp $
 */
public class BuilderList {
    // logger not used at the moment
    //private static Logger log = Logging.getLoggerInstance(BuilderList.class.getName());

   /**
     * Generates the document and writes it to the result object.
     * @param result the StreamResult object where to store the configuration'
     */
    public void write(Document doc, StreamResult result) throws IOException, TransformerException {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        tfactory.setURIResolver(new org.mmbase.util.xml.URIResolver(new java.io.File("")));
        // This creates a transformer that does a simple identity transform,
        // and thus can be used for all intents and purposes as a serializer.
        Transformer serializer = tfactory.newTransformer();
        // sets indent amount for xalan
        // should be done elsewhere, but where?
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        // xml output configuration
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.transform(new DOMSource(doc), result);
    }

    /**
     * Lists all builders within a given path, including builders in sub-paths
     * @param ipath the path to start searching. The path need be closed with a File.seperator character.
     */
    String listBuilders(String path) {
        String result="";
        if (!path.endsWith(File.separator)) path+=File.separator;
        File bdir = new File(path);
        if (bdir.isDirectory()) {
            //these directory-names must be excluded
            if (!bdir.getName().equals("builders")&&!bdir.getName().equals("applications")&&!bdir.getName().equals("tools")) {
                result+="<buildertype name=\""+bdir.getName()+"\"\n>";
            }
            String files[] = bdir.list();
            if (files!=null) {
                for (int i=0;i<files.length;i++) {
                    String bname=files[i];
                    if (bname.endsWith(".xml")) {
                        try {
                            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
                            // get document builder AFTER setting the validation
                            dfactory.setValidating(false);
                            dfactory.setNamespaceAware(true);
                            DocumentBuilder db = dfactory.newDocumentBuilder();
                            db.setEntityResolver(new XMLEntityResolver(false,this.getClass()));
                            Document document = db.parse(path+File.separator+bname);
                            StringWriter strw=new StringWriter(500);
                            //only process builder config files
                            if (document.getDocumentElement().getTagName().equals("builder")) {
                                write(document, new StreamResult(strw));
                                result+=strw.toString();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(""+e);
                        }
                    } else {
                        if (!bname.equals("CVS")) {
                            result=result+listBuilders(path +  bname + File.separator);
                        }
                    }
                }
            } else {
                throw new RuntimeException("Cannot find builders in "+path);
            }
            //these directory-names must be excluded
            if (!bdir.getName().equals("builders")&&!bdir.getName().equals("applications")&&!bdir.getName().equals("tools")) {
                result+="</buildertype>\n";
            }
        }
        return result;
    }

    /**
     * Main method can be called from an Ant build file and will return
     * the xml with a listing of all the builders
     *
     * @param args base dir to start with, it's possible to use more than one dir seperated by ;
     */
    public static void main(String[] args) throws java.io.UnsupportedEncodingException {
        if (args.length != 0) {
            BuilderList bulList = new BuilderList();
            String s="<builders>\n";
            String builderDir = args[0];
            if (builderDir.indexOf(';')==-1) {
                s=s+bulList.listBuilders(builderDir);
            } else {
                int seperatorIndex = -1;
                do {
                    seperatorIndex =  builderDir.indexOf(';');
                    //this will be the last dir
                    if (seperatorIndex==-1) {
                        s=s+bulList.listBuilders(builderDir.substring(0,builderDir.length()));
                    } else {
                        //there are more dirs
                        s=s+bulList.listBuilders(builderDir.substring(0,seperatorIndex));
                        builderDir=builderDir.substring(seperatorIndex+1,builderDir.length());
                    }
                } while (seperatorIndex!=-1) ;
            }
            s=s+"</builders>\n";
            byte[] bytes= s.getBytes("utf-8");
            System.out.write(bytes,0,bytes.length);
        } else {
            System.out.println("usage: java BuilderList <basedirwithbuilderconfig>");
        }
    }
}
