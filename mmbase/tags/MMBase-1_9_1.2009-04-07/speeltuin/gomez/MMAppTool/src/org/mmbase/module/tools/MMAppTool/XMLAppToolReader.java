/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.tools.MMAppTool;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.xml.parsers.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.*;

/**
 * Class XMLAppToolReader
 *
 * @application MMAppTool
 * @javadoc
 */
public class XMLAppToolReader  {

    Document document;

    public boolean ok=true;

    public XMLAppToolReader(String filename) {

        File file = new File(filename);
        if(!file.exists()) {
            ok=false;
            return;
        }
        try {
            DocumentBuilder db = XMLBasicReader.getDocumentBuilder(false);
            document = db.parse(filename);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getNeededBuilders for this application
     */
    public Color getColor(String name) {
        Vector results=new Vector();
        Node n1=document.getFirstChild();
        if (n1!=null) {
            Node n2=n1.getFirstChild();
            while (n2!=null) {
                if (n2.getNodeName().equals("colors")) {
                    Node n3=n2.getFirstChild();
                    while (n3!=null) {
                        if (n3.getNodeName().equals(name)) {
                            Hashtable bset=new Hashtable();
                            Node n5=n3.getFirstChild();
                            NamedNodeMap nm=n3.getAttributes();
                            try {
                                Node n4=nm.getNamedItem("red");
                                int red=Integer.parseInt(n4.getNodeValue());
                                n4=nm.getNamedItem("green");
                                int green=Integer.parseInt(n4.getNodeValue());
                                n4=nm.getNamedItem("blue");
                                int blue=Integer.parseInt(n4.getNodeValue());
                                return(new Color(red,green,blue));
                            } catch(Exception e) {
                                return(null);
                            }
                        }
                        n3=n3.getNextSibling();
                    }
                }
                n2=n2.getNextSibling();
            }
        }
        return(null);
    }


    /**
     * getNeededBuilders for this application
     */
    public int getX(String name) {
        Vector results=new Vector();
        Node n1=document.getFirstChild();
        if (n1!=null) {
            Node n2=n1.getFirstChild();
            while (n2!=null) {
                if (n2.getNodeName().equals("builderovallist")) {
                    Node n3=n2.getFirstChild();
                    while (n3!=null) {
                        if (n3.getNodeName().equals("builderoval")) {
                            Hashtable bset=new Hashtable();
                            Node n5=n3.getFirstChild();
                            NamedNodeMap nm=n3.getAttributes();
                            Node n4=nm.getNamedItem("name");
                            if (n4.getNodeValue().equals(name)) {
                                try {
                                    n4=nm.getNamedItem("xpos");
                                    return(Integer.parseInt(n4.getNodeValue()));
                                } catch(Exception e) {
                                    return(-1);
                                }
                            }

                        }
                        n3=n3.getNextSibling();
                    }
                }
                n2=n2.getNextSibling();
            }
        }
        return(-1);
    }


    /**
     * getNeededBuilders for this application
     */
    public int getY(String name) {
        Vector results=new Vector();
        Node n1=document.getFirstChild();
        if (n1!=null) {
            Node n2=n1.getFirstChild();
            while (n2!=null) {
                if (n2.getNodeName().equals("builderovallist")) {
                    Node n3=n2.getFirstChild();
                    while (n3!=null) {
                        if (n3.getNodeName().equals("builderoval")) {
                            Hashtable bset=new Hashtable();
                            Node n5=n3.getFirstChild();
                            NamedNodeMap nm=n3.getAttributes();
                            Node n4=nm.getNamedItem("name");
                            if (n4.getNodeValue().equals(name)) {
                                try {
                                    n4=nm.getNamedItem("ypos");
                                    return(Integer.parseInt(n4.getNodeValue()));
                                } catch(Exception e) {
                                    return(-1);
                                }
                            }

                        }
                        n3=n3.getNextSibling();
                    }
                }
                n2=n2.getNextSibling();
            }
        }
        return(-1);
    }

}
