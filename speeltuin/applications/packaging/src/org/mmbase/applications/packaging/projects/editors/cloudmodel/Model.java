/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.editors.cloudmodel;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.projects.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

import org.w3c.dom.*;

/**
 * DisplayHtmlPackage, Handler for html packages
 *
 * @author     Daniel Ockeloen (MMBased)
 * @created    July 20, 2004
 */
public class Model {

    private static Logger log = Logging.getLoggerInstance(Model.class);

    /**
     *  Description of the Field
     */
    public final static String DTD_PACKAGING_CLOUD_MODEL_1_0 = "packaging_cloud_model_1_0.dtd";
    /**
     *  Description of the Field
     */
    public final static String PUBLIC_ID_PACKAGING_CLOUD_MODEL_1_0 = "-//MMBase//DTD packaging_cloud_model config 1.0//EN";

    private ArrayList neededbuilders = new ArrayList();
    private ArrayList neededreldefs = new ArrayList();
    private ArrayList allowedrelations = new ArrayList();

    private String path;

    /**
     *  Description of the Method
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_PACKAGING_CLOUD_MODEL_1_0, "DTD_PACKAGING_CLOUD_MODEL_1_0", Model.class);
    }

    public Model(String modelfilename) {
	readModel(modelfilename);
	this.path=modelfilename;
    }

    public Iterator getNeededBuilders() {
	return neededbuilders.iterator();
    }

    public Iterator getNeededRelDefs() {
	return neededreldefs.iterator();
    }

    public Iterator getAllowedRelations() {
	return allowedrelations.iterator();
    }

    public boolean addNeededBuilder(String builder,String maintainer,String version) {
	NeededBuilder nb = new NeededBuilder();
	nb.setName(builder);
	nb.setMaintainer(maintainer);
	nb.setVersion(version);
	neededbuilders.add(nb);
	writeModel();
	return true;
    }


    public boolean deleteNeededBuilder(String builder,String maintainer,String version) {
    	Iterator nbl=getNeededBuilders();
	while (nbl.hasNext()) {
		NeededBuilder nb=(NeededBuilder)nbl.next();
		if (nb.getName().equals(builder) && nb.getMaintainer().equals(maintainer) && nb.getVersion().equals(version)) {
			neededbuilders.remove(nb);
			writeModel();
			return true;
		}
	}
	return false;
    }


    public boolean addNeededRelDef(String source,String target,String direction,String guisourcename,String guitargetname,String builder) {
	NeededRelDef nr = new NeededRelDef();
	nr.setSource(source);
	nr.setTarget(target);
	nr.setDirection(direction);
	nr.setGuiSourceName(guisourcename);
	nr.setGuiTargetName(guitargetname);
	nr.setBuilderName(builder);
	neededreldefs.add(nr);
	writeModel();
	return true;
    }


    public boolean deleteNeededRelDef(String source,String target,String direction,String guisourcename,String guitargetname,String builder) {
    	Iterator nrl=getNeededRelDefs();
	while (nrl.hasNext()) {
		NeededRelDef nr=(NeededRelDef)nrl.next();
		if (nr.getSource().equals(source) && nr.getTarget().equals(target) && nr.getDirection().equals(direction) && nr.getGuiSourceName().equals(guitargetname) && nr.getGuiTargetName().equals(guitargetname) && nr.getBuilderName().equals(builder)) {
			allowedrelations.remove(nr);
			writeModel();
			return true;
		}
	}
	return true;
    }


    public boolean addAllowedRelation(String from,String to,String type) {
	AllowedRelation ar = new AllowedRelation();
	ar.setFrom(from);
	ar.setTo(to);
	ar.setType(type);
	allowedrelations.add(ar);
	writeModel();
	return true;
    }


    public boolean deleteAllowedRelation(String from,String to,String type) {
    	Iterator arl=getAllowedRelations();
	while (arl.hasNext()) {
		AllowedRelation ar=(AllowedRelation)arl.next();
		if (ar.getFrom().equals(from) && ar.getTo().equals(to) && ar.getType().equals(type)) {
			allowedrelations.remove(ar);
			writeModel();
			return true;
		}
	}
	return false;
    }

    private void readModel(String path) {
        File file = new File(path);
        if (file.exists()) {
            XMLBasicReader reader = new XMLBasicReader(path, Model.class);
            if (reader != null) {
                for (Enumeration ns = reader.getChildElements("cloudmodel.neededbuilderlist", "builder"); ns.hasMoreElements(); ) {
            		Element n = (Element) ns.nextElement();
            		String name = reader.getElementValue(n);
			NeededBuilder nb = new NeededBuilder();
			nb.setName(name);
                    	NamedNodeMap nm = n.getAttributes();
	                if (nm != null) {
                        	org.w3c.dom.Node n2 = nm.getNamedItem("version");
                        	if (n2 != null) {
                           		nb.setVersion(n2.getNodeValue());
				}
                        	n2 = nm.getNamedItem("maintainer");
                        	if (n2 != null) {
                           		nb.setMaintainer(n2.getNodeValue());
				}
			}
			neededbuilders.add(nb);
	    	}
                for (Enumeration ns = reader.getChildElements("cloudmodel.neededreldeflist", "reldef"); ns.hasMoreElements(); ) {
            		Element n = (Element) ns.nextElement();
			NeededRelDef nr = new NeededRelDef();
                    	NamedNodeMap nm = n.getAttributes();
	                if (nm != null) {
                        	org.w3c.dom.Node n2 = nm.getNamedItem("source");
                        	if (n2 != null) {
                           		nr.setSource(n2.getNodeValue());
				}
                        	n2 = nm.getNamedItem("target");
                        	if (n2 != null) {
                           		nr.setTarget(n2.getNodeValue());
				}
                        	n2 = nm.getNamedItem("direction");
                        	if (n2 != null) {
                           		nr.setDirection(n2.getNodeValue());
				}
                        	n2 = nm.getNamedItem("guisourcename");
                        	if (n2 != null) {
                           		nr.setGuiSourceName(n2.getNodeValue());
				}
                        	n2 = nm.getNamedItem("guitargetname");
                        	if (n2 != null) {
                           		nr.setGuiTargetName(n2.getNodeValue());
				}
                        	n2 = nm.getNamedItem("builder");
                        	if (n2 != null) {
                           		nr.setBuilderName(n2.getNodeValue());
				}
			}
			neededreldefs.add(nr);
	    	}

                for (Enumeration ns = reader.getChildElements("cloudmodel.allowedrelationlist", "relation"); ns.hasMoreElements(); ) {
            		Element n = (Element) ns.nextElement();
			AllowedRelation ar = new AllowedRelation();
                    	NamedNodeMap nm = n.getAttributes();
	                if (nm != null) {
                        	org.w3c.dom.Node n2 = nm.getNamedItem("from");
                        	if (n2 != null) {
                           		ar.setFrom(n2.getNodeValue());
				}
                        	n2 = nm.getNamedItem("to");
                        	if (n2 != null) {
                           		ar.setTo(n2.getNodeValue());
				}
                        	n2 = nm.getNamedItem("type");
                        	if (n2 != null) {
                           		ar.setType(n2.getNodeValue());
				}
			}
			allowedrelations.add(ar);
	    	}
            }
        }
  }

  private boolean writeModel() {
	String body="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	body+="<!DOCTYPE cloudmodel PUBLIC \"-//MMBase/DTD cloudmodel 1.0//EN\" \"http://www.mmbase.org/dtd/cloudmodel_1_0.dtd\">\n";

	body+="<cloudmodel>\n";
	body+="\t<neededbuilderlist>\n";
    	Iterator nbl=getNeededBuilders();
	while (nbl.hasNext()) {
		NeededBuilder nb=(NeededBuilder)nbl.next();
		body+="\t\t<builder maintainer=\""+nb.getMaintainer()+"\" version=\""+nb.getVersion()+"\">"+nb.getName()+"</builder>\n";
	}
	body+="\t</neededbuilderlist>\n\n";


	body+="\t<neededreldeflist>\n";
    	Iterator rdl=getNeededRelDefs();
	while (rdl.hasNext()) {
		NeededRelDef nr=(NeededRelDef)rdl.next();
		body+="\t\t<reldef source=\""+nr.getSource()+"\" target=\""+nr.getTarget()+"\" direction=\""+nr.getDirection()+"\" guisourcename=\""+nr.getGuiSourceName()+"\" guitargetname=\""+nr.getGuiTargetName()+"\" builder=\""+nr.getBuilderName()+"\" />\n";
	}
	body+="\t</neededreldeflist>\n\n";


	body+="\t<allowedrelationlist>\n";
    	Iterator arl=getAllowedRelations();
	while (arl.hasNext()) {
		AllowedRelation ar=(AllowedRelation)arl.next();
		body+="\t\t<relation from=\""+ar.getFrom()+"\" to=\""+ar.getTo()+"\" type=\""+ar.getType()+"\" />\n";

	}
	body+="\t</allowedrelationlist>\n\n";

	body+="</cloudmodel>\n";
	log.info("WRITE MODEL TO : "+path);

        // check if the dirs are created, if not create them
        String dirsp = path.substring(0, path.lastIndexOf(File.separator));
        File dirs = new File(dirsp);
        if (!dirs.exists()) {
            dirs.mkdirs();
        }

        // write back to disk
        File sfile = new File(path);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(body);
            scan.flush();
            scan.close();
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
	return true;
  }

}

