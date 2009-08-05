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
    public final static String DTD_BUILDER_1_1 = "builder_1_1.dtd";

    /**
     *  Description of the Field
     */
    public final static String PUBLIC_ID_PACKAGING_CLOUD_MODEL_1_0 = "-//MMBase//DTD packaging_cloud_model config 1.0//EN";
    public final static String PUBLIC_ID_BUILDER_1_1 = "-//MMBase//DTD builder config 1.1//EN";

    private ArrayList neededbuilders = new ArrayList();
    private ArrayList neededreldefs = new ArrayList();
    private ArrayList allowedrelations = new ArrayList();

    private String path;

    /**
     *  Description of the Method
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_PACKAGING_CLOUD_MODEL_1_0, "DTD_PACKAGING_CLOUD_MODEL_1_0", Model.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_BUILDER_1_1, "DTD_BUILDER_1_1", Model.class);
    }

    public Model(String modelfilename) {
	this.path=modelfilename;
	readModel(modelfilename);
    }

    public Iterator getNeededBuilders() {
	return neededbuilders.iterator();
    }

    public NeededBuilder getNeededBuilder(String buildername) {
    	Iterator nbl=getNeededBuilders();
	while (nbl.hasNext()) {
		NeededBuilder nb=(NeededBuilder)nbl.next();
		if (nb.getName().equals(buildername)) {
			return nb;
		}
	}
	return null;
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
			// try to find if this is defined in a real file
			readBuilder(nb);
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


    private void readBuilder(NeededBuilder nb) {
	String builderpath=path.substring(0,path.length()-4)+"/"+nb.getName()+".xml";
        File file = new File(builderpath);
        if (file.exists()) {
            XMLBasicReader reader = new XMLBasicReader(builderpath, Model.class);
            if (reader != null) {
                org.w3c.dom.Node n = reader.getElementByPath("builder");
		if (n!=null) {
                    	NamedNodeMap nm = n.getAttributes();
	                if (nm != null) {
                        	org.w3c.dom.Node n2 = nm.getNamedItem("name");
                        	if (n2 != null) {
                           		nb.setName(n2.getNodeValue());
				}
                        	n2 = nm.getNamedItem("maintainer");
                        	if (n2 != null) {
                           		nb.setMaintainer(n2.getNodeValue());
				}
                        	n2 = nm.getNamedItem("version");
                        	if (n2 != null) {
                           		nb.setVersion(n2.getNodeValue());
				}
                        	n2 = nm.getNamedItem("extends");
                        	if (n2 != null) {
                           		nb.setExtends(n2.getNodeValue());
				}
			}
		}
                n = reader.getElementByPath("builder.status");
                if (n!=null) {
                    org.w3c.dom.Node n2 = n.getFirstChild();
                    if (n2 != null) {
                       nb.setStatus(n2.getNodeValue());
                    }
                }
                n = reader.getElementByPath("builder.searchage");
                if (n!=null) {
                    org.w3c.dom.Node n2 = n.getFirstChild();
                    if (n2 != null) {
                       nb.setSearchAge(n2.getNodeValue());
                    }
                }
                n = reader.getElementByPath("builder.classfile");
                if (n!=null) {
                    org.w3c.dom.Node n2 = n.getFirstChild();
                    if (n2 != null) {
                       nb.setClassName(n2.getNodeValue());
                    }
                }
                for (Enumeration ns = reader.getChildElements("builder.names", "singular"); ns.hasMoreElements(); ) {
            		Element n4 = (Element) ns.nextElement();
            		String name = reader.getElementValue(n4);
                    	NamedNodeMap nm = n4.getAttributes();
	                if (nm != null) {
				String language="unknown";
                        	org.w3c.dom.Node n2 = nm.getNamedItem("xml:lang");
                        	if (n2 != null) {
                           		language=n2.getNodeValue();
				}
				nb.setSingularName(language,name);
			}
	    	}
                for (Enumeration ns = reader.getChildElements("builder.names", "plural"); ns.hasMoreElements(); ) {
            		Element n4 = (Element) ns.nextElement();
            		String name = reader.getElementValue(n4);
                    	NamedNodeMap nm = n4.getAttributes();
	                if (nm != null) {
				String language="unknown";
                        	org.w3c.dom.Node n2 = nm.getNamedItem("xml:lang");
                        	if (n2 != null) {
                           		language=n2.getNodeValue();
				}
				nb.setPluralName(language,name);
			}
	    	}
                for (Enumeration ns = reader.getChildElements("builder.descriptions", "description"); ns.hasMoreElements(); ) {
            		Element n4 = (Element) ns.nextElement();
            		String description = reader.getElementValue(n4);
                    	NamedNodeMap nm = n4.getAttributes();
	                if (nm != null) {
				String language="unknown";
                        	org.w3c.dom.Node n2 = nm.getNamedItem("xml:lang");
                        	if (n2 != null) {
                           		language=n2.getNodeValue();
				}
				nb.setDescription(language,description);
			}
	    	}
                for (Enumeration ns = reader.getChildElements("builder.fieldlist", "field"); ns.hasMoreElements(); ) {
            		Element n4 = (Element) ns.nextElement();
			decodeField(nb,n4);
	    	}
	    }
        }
  }

  private void decodeField(NeededBuilder nb,Element n) {
	NeededBuilderField nbf = new NeededBuilderField();
	org.w3c.dom.NodeList l=n.getElementsByTagName("description");
	for (int i=0;i<l.getLength();i++) {
		org.w3c.dom.Node n2=l.item(i);
                NamedNodeMap nm = n2.getAttributes();
                if (nm != null) {
                     org.w3c.dom.Node n3 = nm.getNamedItem("xml:lang");
                     if (n3 != null) {
                          nbf.setDescription(n3.getNodeValue(),n2.getFirstChild().getNodeValue());
                     }
		}
	}
	l=n.getElementsByTagName("guiname");
	for (int i=0;i<l.getLength();i++) {
		org.w3c.dom.Node n2=l.item(i);
                NamedNodeMap nm = n2.getAttributes();
                if (nm != null) {
                     org.w3c.dom.Node n3 = nm.getNamedItem("xml:lang");
                     if (n3 != null) {
                          nbf.setGuiName(n3.getNodeValue(),n2.getFirstChild().getNodeValue());
                     }
		}
	}
	l=n.getElementsByTagName("guitype");
	if (l.getLength()>0) {
		org.w3c.dom.Node n2=l.item(0);
                if (n2 != null) {
                       nbf.setGuiType(n2.getFirstChild().getNodeValue());
		}
	}
	l=n.getElementsByTagName("input");
	if (l.getLength()>0) {
		org.w3c.dom.Node n2=l.item(0);
                if (n2 != null) {
		       try {
                       		nbf.setEditorInputPos(Integer.parseInt(n2.getFirstChild().getNodeValue()));
		       } catch(Exception e) {
				log.info("builder.field.editor.input not a number");
		       }
		}
	}
	l=n.getElementsByTagName("list");
	if (l.getLength()>0) {
		org.w3c.dom.Node n2=l.item(0);
                if (n2 != null) {
		       try {
                       		nbf.setEditorListPos(Integer.parseInt(n2.getFirstChild().getNodeValue()));
		       } catch(Exception e) {
				log.info("builder.field.editor.list not a number");
		       }
		}
	}
	l=n.getElementsByTagName("search");
	if (l.getLength()>0) {
		org.w3c.dom.Node n2=l.item(0);
                if (n2 != null) {
		       try {
                       		nbf.setEditorSearchPos(Integer.parseInt(n2.getFirstChild().getNodeValue()));
		       } catch(Exception e) {
				log.info("builder.field.editor.search not a number");
		       }
		}
	}
	l=n.getElementsByTagName("name");
	if (l.getLength()>0) {
		org.w3c.dom.Node n2=l.item(0);
                if (n2 != null) {
                  	nbf.setDBName(n2.getFirstChild().getNodeValue());
		}
	}
	l=n.getElementsByTagName("type");
	if (l.getLength()>0) {
		org.w3c.dom.Node n2=l.item(0);
                if (n2 != null) {
                  	nbf.setDBType(n2.getFirstChild().getNodeValue());
		}
                NamedNodeMap nm = n2.getAttributes();
                if (nm != null) {
                     org.w3c.dom.Node n3 = nm.getNamedItem("state");
                     if (n3 != null) {
                          nbf.setDBState(n3.getNodeValue());
		     }
                     n3 = nm.getNamedItem("size");
                     if (n3 != null) {
			  try {
                          	nbf.setDBSize(Integer.parseInt(n3.getNodeValue()));
		       	  } catch(Exception e) {
				log.info("builder.field.editor.search not a number");
		          }
		     }
                     n3 = nm.getNamedItem("key");
                     if (n3 != null) {
			  if (n3.getNodeValue().equals("true") || n3.getNodeValue().equals("TRUE")) {
                          	nbf.setDBKey(true);
			  } else {
                          	nbf.setDBKey(false);
			  }
		     }
                     n3 = nm.getNamedItem("notnull");
                     if (n3 != null) {
			  if (n3.getNodeValue().equals("true") || n3.getNodeValue().equals("TRUE")) {
                          	nbf.setDBNotNull(true);
			  } else {
                          	nbf.setDBNotNull(false);
			  }
		     }
		}
	}
	nb.addField(nbf);
	

  }

  public boolean writeModel() {
	String body="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	body+="<!DOCTYPE cloudmodel PUBLIC \"-//MMBase/DTD cloudmodel 1.0//EN\" \"http://www.mmbase.org/dtd/cloudmodel_1_0.dtd\">\n";

	body+="<cloudmodel>\n";
	body+="\t<neededbuilderlist>\n";
    	Iterator nbl=getNeededBuilders();
	while (nbl.hasNext()) {
		NeededBuilder nb=(NeededBuilder)nbl.next();
		body+="\t\t<builder maintainer=\""+nb.getMaintainer()+"\" version=\""+nb.getVersion()+"\">"+nb.getName()+"</builder>\n";
		writeBuilder(nb);
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

        // check if the dirs are created, if not create them
        String dirsp = path.substring(0, path.lastIndexOf(File.separator));
        File dirs = new File(dirsp);
        if (!dirs.exists()) {
            dirs.mkdirs();
        }

        // write back to disk
        try {
       	    Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF8"));
            out.write(body);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
	return true;
  }


  private boolean writeBuilder(NeededBuilder nb) {
	String builderpath=path.substring(0,path.length()-4)+"/"+nb.getName()+".xml";
	String body="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	body+="<!DOCTYPE builder PUBLIC \"-//MMBase/DTD builder 1.1//EN\" \"http://www.mmbase.org/dtd/builder_1_1.dtd\">\n";

	body+="<builder name=\""+nb.getName()+"\" maintainer=\""+nb.getMaintainer()+"\" version=\""+nb.getVersion()+"\" extends=\""+nb.getExtends()+"\">\n";
	body+="\t<status>"+nb.getStatus()+"</status>\n";
	if (!nb.getClassName().equals("Dummy")) {
		body+="\t<classfile>"+nb.getClassName()+"</classfile>\n";
	}
	body+="\t<searchage>"+nb.getSearchAge()+"</searchage>\n";
	body+="\t<names>\n";
	body+="\t\t<!-- singles per language as defined by ISO 639 -->\n";
	HashMap sn=nb.getSingularNames();
	Iterator snk=sn.keySet().iterator();
	while (snk.hasNext()) {
		String key=(String)snk.next();
		String value=(String)sn.get(key);
		body+="\t\t<singular xml:lang=\""+key+"\">"+value+"</singular>\n";
	}
	body+="\t\t<!-- singles per language as defined by ISO 639 -->\n";
	HashMap pn=nb.getPluralNames();
	Iterator pnk=pn.keySet().iterator();
	while (pnk.hasNext()) {
		String key=(String)pnk.next();
		String value=(String)pn.get(key);
		body+="\t\t<plural xml:lang=\""+key+"\">"+value+"</plural>\n";
	}
	body+="\t</names>\n";
	body+="\t<!-- <descriptions> small description of the builder for human reading -->\n";
	body+="\t<descriptions>\n";
	HashMap de=nb.getDescriptions();
	Iterator dek=de.keySet().iterator();
	while (dek.hasNext()) {
		String key=(String)dek.next();
		String value=(String)de.get(key);
		body+="\t\t<description xml:lang=\""+key+"\">"+value+"</description>\n";
	}
	body+="\t</descriptions>\n";
	body+="\t<fieldlist>\n";
	Iterator fl=nb.getFields();
	int pos = 3;
	while (fl.hasNext()) {
		NeededBuilderField nbf = (NeededBuilderField)fl.next();
		body+="\t\t<!-- POS "+(pos++)+" : <field> '"+nbf.getDBName()+"'  -->\n";
		body+="\t\t<field>\n";
		body+="\t\t\t<descriptions>\n";

		de=nbf.getDescriptions();
		dek=de.keySet().iterator();
		while (dek.hasNext()) {
			String key=(String)dek.next();
			String value=(String)de.get(key);
			body+="\t\t\t\t<description xml:lang=\""+key+"\">"+value+"</description>\n";
		}
		body+="\t\t\t</descriptions>\n";
		body+="\t\t\t<gui>\n";
		de=nbf.getGuiNames();
		dek=de.keySet().iterator();
		while (dek.hasNext()) {
			String key=(String)dek.next();
			String value=(String)de.get(key);
			body+="\t\t\t\t<guiname xml:lang=\""+key+"\">"+value+"</guiname>\n";
		}
		body+="\t\t\t\t<guitype>"+nbf.getGuiType()+"</guitype>\n";
		body+="\t\t\t</gui>\n";
		body+="\t\t\t<!-- editor related  -->\n";
		body+="\t\t\t<editor>\n";
		body+="\t\t\t\t<positions>\n";
		body+="\t\t\t\t\t<!-- position in the input area of the editor -->\n";
		body+="\t\t\t\t\t<input>"+nbf.getEditorInputPos()+"</input>\n";
		body+="\t\t\t\t\t<!-- position in list area of the editor -->\n";
		body+="\t\t\t\t\t<list>"+nbf.getEditorListPos()+"</list>\n";
		body+="\t\t\t\t\t<!-- position in search area of the editor -->\n";
		body+="\t\t\t\t\t<search>"+nbf.getEditorSearchPos()+"</search>\n";

		body+="\t\t\t\t</positions>\n";
		body+="\t\t\t</editor>\n";
		body+="\t\t\t<!-- database related  -->\n";
		body+="\t\t\t<db>\n";
		body+="\t\t\t\t<!-- name of the field in the database -->\n";
		body+="\t\t\t\t<name>"+nbf.getDBName()+"</name>\n";
		body+="\t\t\t\t<!-- MMBase datatype and demands on it -->\n";
		if (nbf.getDBSize()==-1) {
			body+="\t\t\t\t<type state=\""+nbf.getDBState()+"\" notnull=\""+nbf.getDBNotNull()+"\" key=\""+nbf.getDBKey()+"\">"+nbf.getDBType()+"</type>\n";
		} else {
			body+="\t\t\t\t<type state=\""+nbf.getDBState()+"\" size=\""+nbf.getDBSize()+"\" notnull=\""+nbf.getDBNotNull()+"\" key=\""+nbf.getDBKey()+"\">"+nbf.getDBType()+"</type>\n";
		}
		body+="\t\t\t</db>\n";
		body+="\t\t</field>\n";
	}
	body+="\t</fieldlist>\n";
	body+="</builder>\n";

        // check if the dirs are created, if not create them
        String dirsp = builderpath.substring(0, builderpath.lastIndexOf(File.separator));
        File dirs = new File(dirsp);
        if (!dirs.exists()) {
            dirs.mkdirs();
        }

        // write back to disk
        try {
       	    Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(builderpath), "UTF8"));
            out.write(body);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
	return true;
  }

}

