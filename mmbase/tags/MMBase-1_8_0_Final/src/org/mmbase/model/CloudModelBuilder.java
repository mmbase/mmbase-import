/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.model;

import org.mmbase.util.logging.*;
import org.mmbase.core.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.*;

import java.util.*;
import java.io.*;

public class CloudModelBuilder {

    private static Logger log = Logging.getLoggerInstance(CloudModelBuilder.class); 
    private String name;
    private String path;
    private Document document;
    private BuilderReader reader;

    public CloudModelBuilder(String name) {
        this.name =  name;
    }

    public void setPath(String path) {
       this.path = path;
    }

    public boolean writeToFile(String filepath) {
        InputStream in = ResourceLoader.getConfigurationRoot().getResourceAsStream(path);
        if (in!=null) {
           try {                
                FileOutputStream out = new FileOutputStream(filepath);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.flush();
                out.close();
            } catch(Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            log.error("Resource not found : "+path);
        }
        return true;
    }

    public boolean removeField(String name) {
	if (document == null) openDocument();
        Element fe = reader.getElementByPath(document.getDocumentElement(),"builder.fieldlist");
        if (fe != null) {
            Iterator fields = reader.getChildElements(fe,"field");
            while (fields.hasNext()) {
                Element field = (Element)fields.next();
                Element namenode = reader.getElementByPath(field,"field.db.name");
                if (namenode!=null && namenode.getFirstChild().getNodeValue().equals(name)) {
                    fe.removeChild(field);
                    save();
                }
            }
        }
        return true;
    }


    public boolean setGuiName(String fieldname,String country,String value) {
	if (document == null) openDocument();
        Element fe = reader.getElementByPath(document.getDocumentElement(),"builder.fieldlist");
        if (fe != null) {
            Iterator fields = reader.getChildElements(fe,"field");
            while (fields.hasNext()) {
                Element field = (Element)fields.next();
                Element namenode = reader.getElementByPath(field,"field.db.name");
                if (namenode!=null && namenode.getFirstChild().getNodeValue().equals(fieldname)) {
                       // that we have found the correct field find
                       // find the gui names
                       Element guinode = reader.getElementByPath(field,"field.gui");
                       if (guinode != null) {
            	       Iterator  guinames= reader.getChildElements(guinode,"guiname");
                       boolean found =  false;
                       while (guinames.hasNext()) {
                           Element guiname = (Element)guinames.next();
			   String oldcountry = guiname.getAttribute("xml:lang");
			   if (oldcountry!=null && oldcountry.equals(country)) {
				guiname.getFirstChild().setNodeValue(value);
                           	save();
				found =  true;
			   }
                       }
                       if (!found) {
			   String newpart ="      <guiname xml:lang=\""+country+"\">"+value+"</guiname>\r";
	                   mergePart(guinode,newpart);
                           save();
                       }
                       } else {
                           String newpart ="    <gui>\r";
			   newpart +="      <guiname xml:lang=\""+country+"\">"+value+"</guiname>\r";
                           newpart +="    </gui>\r";
	                   mergePart(field,newpart);
                           save();
		       }        
                }
            }
        }
        return true;
    }


    public boolean setBuilderDBState(String fieldname,String value) {
	if (document == null) openDocument();
        Element fe = reader.getElementByPath(document.getDocumentElement(),"builder.fieldlist");
        if (fe != null) {
            Iterator fields = reader.getChildElements(fe,"field");
            while (fields.hasNext()) {
                Element field = (Element)fields.next();
                Element namenode = reader.getElementByPath(field,"field.db.name");
                if (namenode!=null && namenode.getFirstChild().getNodeValue().equals(fieldname)) {
                       // that we have found the correct field find
                       // find the type node
                       Element typenode = reader.getElementByPath(field,"field.db.type");
		       NamedNodeMap nnm = typenode.getAttributes();
		       if (nnm != null) {
				Node dbstate = nnm.getNamedItem("state");
				dbstate.getFirstChild().setNodeValue(value);
                           	save();
		       }
                }
            }
        }
        return true;
    }


    public boolean setBuilderDBKey(String fieldname,String value) {
	if (document == null) openDocument();
        Element fe = reader.getElementByPath(document.getDocumentElement(),"builder.fieldlist");
        if (fe != null) {
            Iterator fields = reader.getChildElements(fe,"field");
            while (fields.hasNext()) {
                Element field = (Element)fields.next();
                Element namenode = reader.getElementByPath(field,"field.db.name");
                if (namenode!=null && namenode.getFirstChild().getNodeValue().equals(fieldname)) {
                       // that we have found the correct field find
                       // find the type node
                       Element typenode = reader.getElementByPath(field,"field.db.type");
		       NamedNodeMap nnm = typenode.getAttributes();
		       if (nnm != null) {
				Node key = nnm.getNamedItem("key");
				key.getFirstChild().setNodeValue(value);
                           	save();
		       }
                }
            }
        }
        return true;
    }


    public boolean setBuilderDBNotNull(String fieldname,String value) {
	if (document == null) openDocument();
        Element fe = reader.getElementByPath(document.getDocumentElement(),"builder.fieldlist");
        if (fe != null) {
            Iterator fields = reader.getChildElements(fe,"field");
            while (fields.hasNext()) {
                Element field = (Element)fields.next();
                Element namenode = reader.getElementByPath(field,"field.db.name");
                if (namenode!=null && namenode.getFirstChild().getNodeValue().equals(fieldname)) {
                       // that we have found the correct field find
                       // find the type node
                       Element typenode = reader.getElementByPath(field,"field.db.type");
		       NamedNodeMap nnm = typenode.getAttributes();
		       if (nnm != null) {
				Node notnull = nnm.getNamedItem("notnull");
				notnull.getFirstChild().setNodeValue(value);
                           	save();
		       }
                }
            }
        }
        return true;
    }


    public boolean setBuilderDBSize(String fieldname,String value) {
	if (document == null) openDocument();
        Element fe = reader.getElementByPath(document.getDocumentElement(),"builder.fieldlist");
        if (fe != null) {
            Iterator fields = reader.getChildElements(fe,"field");
            while (fields.hasNext()) {
                Element field = (Element)fields.next();
                Element namenode = reader.getElementByPath(field,"field.db.name");
                if (namenode!=null && namenode.getFirstChild().getNodeValue().equals(fieldname)) {
                       // that we have found the correct field find
                       // find the type node
                       Element typenode = reader.getElementByPath(field,"field.db.type");
		       NamedNodeMap nnm = typenode.getAttributes();
		       if (nnm != null) {
				Node dbsize = nnm.getNamedItem("size");
				dbsize.getFirstChild().setNodeValue(value);
                           	save();
		       }
                }
            }
        }
        return true;
    }


    public boolean addField(int pos,String name,String type,String guitype,String state,String required,String unique,String size) {
	if (document == null) openDocument();
         Element fe = reader.getElementByPath(document.getDocumentElement(),"builder.fieldlist");
        if (fe!=null) {
            String newpart ="    <field>\r";
            newpart +="      <editor>\r";
            newpart +="        <positions>\r";
            newpart +="          <input>"+pos+"</input>\r";
            newpart +="          <list>"+pos+"</list>\r";
            newpart +="          <search>"+pos+"</search>\r";
            newpart +="        </positions>\r";
            newpart +="      </editor>\r";

            newpart +="      <datatype base=\""+guitype+"\" xmlns=\"http://www.mmbase.org/xmlns/datatypes\"/>\r";
            newpart +="      <db>\r";
            newpart +="        <name>"+name+"</name>\r";
            newpart +="        <type key=\""+unique+"\" notnull=\""+required+"\" size=\""+size+"\" state=\""+state+"\">"+type+"</type>\r";
            newpart +="      </db>\r";
            newpart +="    </field>\r";
	    mergePart(fe,newpart);
        }
        save();
        return true;
    }

    private void mergePart(Element fe,String newpart) {
            try {
                Element nf = (reader.getDocumentBuilder(false,null,null).parse(new InputSource(new StringReader(newpart)))).getDocumentElement();
                fe.appendChild(document.importNode(nf,true));
            } catch(Exception e) {
                log.error("Can't merge new xml code");
            }
    }

    public boolean save() {
        // save the file back using the ResourceLoader
        try {                
            ResourceLoader.getConfigurationRoot().storeDocument(path,document);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void openDocument() {
       try {
           document = ResourceLoader.getConfigurationRoot().getDocument(path);
           reader = new BuilderReader(document,MMBase.getMMBase());
       } catch (Exception e) {
          log.error("missing builderfile file : "+path);
          e.printStackTrace();
       }
   }

}
