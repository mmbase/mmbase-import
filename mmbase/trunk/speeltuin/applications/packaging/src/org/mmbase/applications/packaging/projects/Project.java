/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.packaging.projects;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.projects.creators.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import org.w3c.dom.*;

/**
 * @author Daniel Ockeloen
 * 
 */
public class Project {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(Project.class.getName()); 

   String path;
   String name;
   String basedir;
   Hashtable targets=new Hashtable();
   Hashtable packagetargets=new Hashtable();
   Hashtable bundletargets=new Hashtable();

    /** DTD resource filename of the packaging DTD version 1.0 */
    public static final String DTD_PACKAGING_1_0 = "packaging_1_0.dtd";

    /** Public ID of the packaging DTD version 1.0 */
    public static final String PUBLIC_ID_PACKAGING_1_0 = "-//MMBase//DTD packaging config 1.0//EN";

    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_PACKAGING_1_0, "DTD_PACKAGING_1_0", Project.class);    }

   public Project(String name,String path) {
	this.name=name;
	this.path=path;
	readTargets();
   }

   public String getPath() {
	return path;
   }

   public String getName() {
	return name;
   }

   public String getBaseDir() {
	return basedir;
   }

    public Enumeration getTargets() {
        return targets.elements();
    }

    public boolean deleteTarget(String name) {
	log.info("DEL="+name+" "+targets);
	// bad bad bad
	targets.remove(name);
	packagetargets.remove(name);
	bundletargets.remove(name);
	save();
	return true;
    }

    public boolean addBundleTarget(String name,String type,String path) {
	// several name tricks to make allow for faster creation by people
	if (name.equals("") || name.equals("[auto]")) {
		name=type.substring(0,type.indexOf("/"));
	}
	if (path.equals("") || path.equals("[auto]")) {
		path="packaging"+File.separator+getName()+"_"+name+".xml";
		path=path.replace(' ','_');
	}

	// check if the dirs are created, if not create them
	String dirsp=basedir+path.substring(0,path.lastIndexOf(File.separator));
	File dirs=new File(dirsp);
	if (!dirs.exists()) {
		dirs.mkdirs();
	}

        Target t=new Target(name);
	t.setBundle(true);
	t.setBaseDir(basedir);
        t.setPath(path);
        t.setType(type);
        // get the handler
        CreatorInterface cr=ProjectManager.getCreatorByType(type);
        if (cr!=null) {
                t.setCreator(cr);
        }
        bundletargets.put(name,t);
	save();
	return true;
    }

    public boolean addPackageTarget(String name,String type,String path) {
	if (name.equals("") || name.equals("[auto]")) {
		name=type.substring(0,type.indexOf("/"));
	}
	if (path.equals("") || path.equals("[auto]")) {
		path="packaging"+File.separator+getName()+"_"+name+".xml";
		path=path.replace(' ','_');
	}
	// check if the dirs are created, if not create them
	String dirsp=basedir+path.substring(0,path.lastIndexOf(File.separator));
	File dirs=new File(dirsp);
	if (!dirs.exists()) {
		dirs.mkdirs();
	}
	Target t=new Target(name);
	t.setPath(path);
	t.setBaseDir(basedir);
	t.setType(type);
	// get the handler
	CreatorInterface cr=ProjectManager.getCreatorByType(type);
	if (cr!=null) {
		t.setCreator(cr);
		t.setDefaults();
	}
	packagetargets.put(name,t);
	save();
	return true;
    }

    public Target getTarget(String name) {
	Object o=targets.get(name);
	if (o!=null) return (Target)o;
	o=packagetargets.get(name);
	if (o!=null) return (Target)o;
	o=bundletargets.get(name);
	if (o!=null) return (Target)o;
	return null;
    }

    public Enumeration getPackageTargets() {
        return packagetargets.elements();
    }

    public Enumeration getBundleTargets() {
        return bundletargets.elements();
    }

    public void readTargets() {
        File file = new File(path);
        if(file.exists()) {
            	XMLBasicReader reader = new XMLBasicReader(path,Project.class);
            	if(reader!=null) {

                       org.w3c.dom.Node n2=(org.w3c.dom.Node)reader.getElementByPath("packaging");
			if (n2!=null) {
   		               	NamedNodeMap nm=n2.getAttributes();
                    		if (nm!=null) {
                        		org.w3c.dom.Node n3=nm.getNamedItem("basedir");
                        		if (n3!=null) {
						basedir=n3.getNodeValue();
						if (basedir.equals(".")) {
							basedir=expandBasedir(basedir);
						}
					}
				}
			}
			
			// decode targets
            		for(Enumeration ns=reader.getChildElements("packaging","target");ns.hasMoreElements(); ) {
                		Element n=(Element)ns.nextElement();
   		               	NamedNodeMap nm=n.getAttributes();
                    		if (nm!=null) {
					String name=null;
					String depends=null;

					// decode name
                        		org.w3c.dom.Node n3=nm.getNamedItem("name");
                        		if (n3!=null) {
						name=n3.getNodeValue();
					}
					// decode depends
                        		n3=nm.getNamedItem("depends");
                        		if (n3!=null) {
						depends=n3.getNodeValue();
					}

					if (name!=null) {
						Target t=new Target(name);
						if (depends!=null) {
							t.setDepends(depends);
						}
						targets.put(name,t);
					}
				}
			}


			// decode packagetargets
            		for(Enumeration ns=reader.getChildElements("packaging","package");ns.hasMoreElements(); ) {
                		Element n=(Element)ns.nextElement();
   		               	NamedNodeMap nm=n.getAttributes();
                    		if (nm!=null) {
					String name=null;
					String type=null;
					String path=null;

					// decode name
                        		org.w3c.dom.Node n3=nm.getNamedItem("name");
                        		if (n3!=null) {
						name=n3.getNodeValue();
					}
					// decode path 
                        		n3=nm.getNamedItem("file");
                        		if (n3!=null) {
						path=n3.getNodeValue();
					}

					// decode type
                        		n3=nm.getNamedItem("type");
                        		if (n3!=null) {
						type=n3.getNodeValue();
					}

					if (name!=null) {
						Target t=new Target(name);
						if (path!=null) {
							t.setBaseDir(basedir);
							t.setPath(path);
						}
						if (type!=null) {
							t.setType(type);
							// get the handler
							CreatorInterface cr=ProjectManager.getCreatorByType(type);
							if (cr!=null) {
								t.setCreator(cr);
							}
						}
						packagetargets.put(name,t);
					}
				}
			}


			// decode bundletargets
            		for(Enumeration ns=reader.getChildElements("packaging","bundle");ns.hasMoreElements(); ) {
                		Element n=(Element)ns.nextElement();
   		               	NamedNodeMap nm=n.getAttributes();
                    		if (nm!=null) {
					String name=null;
					String type="bundle/basic";
					String depends=null;
					String path=null;

					// decode name
                        		org.w3c.dom.Node n3=nm.getNamedItem("name");
                        		if (n3!=null) {
						name=n3.getNodeValue();
					}
					// decode type
                        		n3=nm.getNamedItem("type");
                        		if (n3!=null) {
						type=n3.getNodeValue();
					}
					// decode path 
                        		n3=nm.getNamedItem("file");
                        		if (n3!=null) {
						path=n3.getNodeValue();
					}
					// decode depends
                        		n3=nm.getNamedItem("depends");
                        		if (n3!=null) {
						depends=n3.getNodeValue();
					}

					if (name!=null) {
						Target t=new Target(name);
						t.setBundle(true);
						if (depends!=null) {
							t.setDepends(depends);
						}
						if (path!=null) {
							t.setPath(path);
							t.setBaseDir(basedir);
						}
						if (type!=null) {
							t.setType(type);
							// get the handler
							CreatorInterface cr=ProjectManager.getCreatorByType(type);
							if (cr!=null) {
								t.setCreator(cr);
							}
						}

						bundletargets.put(name,t);
					}
				}
			}

		}

	} else {
            log.error("missing projects file : "+path);
	}
    }

    private String expandBasedir(String basedir) {
	File basefile=new File(path);
	if (basefile!=null) {
		return basefile.getParent()+File.separator;
	}
	return basedir;
    } 


   public boolean save() {
       String body="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
       body+="<!DOCTYPE packaging PUBLIC \"-//MMBase/DTD packaging config 1.0//EN\" \"http://www.mmbase.org/dtd/packaging_1_0.dtd\">\n";
       body+="<packaging basedir=\".\">\n";
	Enumeration e=packagetargets.elements();
	while (e.hasMoreElements()) {
		Target t=(Target)e.nextElement();
		body+="\t<package name=\""+t.getName()+"\" type=\""+t.getType()+"\" file=\""+t.getPath()+"\" />\n";
	}
	e=bundletargets.elements();
	while (e.hasMoreElements()) {
		Target t=(Target)e.nextElement();
		body+="\t<bundle name=\""+t.getName()+"\" type=\""+t.getType()+"\" file=\""+t.getPath()+"\" />\n";
	}
       body+="</packaging>\n";
        File sfile = new File(path);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(body);
            scan.flush();
            scan.close();
        } catch(Exception f) {
	    log.error("Can't save packaging file : "+path);
	    return false;
        }
        return true;
    }


}
