/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.packaging.projects.editors.cloudmodel.gui;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;
import org.mmbase.applications.packaging.installhandlers.*;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;
import org.mmbase.applications.packaging.projects.creators.*;
import org.mmbase.applications.packaging.projects.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;
import org.mmbase.applications.packaging.projects.editors.cloudmodel.*;

/**
 * @author Daniel Ockeloen
 * @version $Id: guiController.java
 */
public class Controller {

    private static Logger log = Logging.getLoggerInstance(Controller.class);
    private static Cloud cloud;
    NodeManager manager;
    CloudContext context;


    public Controller() {
        cloud=LocalContext.getCloudContext().getCloud("mmbase");

        // hack needs to be solved
            manager=cloud.getNodeManager("typedef");
        if (manager==null) log.error("Can't access builder typedef");
        context=LocalContext.getCloudContext();
    }


    public List getNeededBuilders(String cloudmodelfile) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

	Model model = new Model(cloudmodelfile);
        Iterator neededbuilders=model.getNeededBuilders();
        for (Iterator i = neededbuilders; i.hasNext();) {
            NeededBuilder nb=(NeededBuilder)i.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("name",nb.getName());
            virtual.setValue("maintainer",nb.getMaintainer());
            virtual.setValue("version",nb.getVersion());
            list.add(virtual);
        }
        return list;
    }


    public List getNeededRelDefs(String cloudmodelfile) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

	Model model = new Model(cloudmodelfile);
        Iterator neededreldefs=model.getNeededRelDefs();
        for (Iterator i = neededreldefs; i.hasNext();) {
            NeededRelDef nr=(NeededRelDef)i.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("source",nr.getSource());
            virtual.setValue("target",nr.getTarget());
            virtual.setValue("direction",nr.getDirection());
            virtual.setValue("guisourcename",nr.getGuiSourceName());
            virtual.setValue("guitargetname",nr.getGuiTargetName());
            virtual.setValue("buildername",nr.getBuilderName());
            list.add(virtual);
        }
        return list;
    }

    public List getNeededBuilderFields(String cloudmodelfile,String buildername,String language) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
	Model model = new Model(cloudmodelfile);
        NeededBuilder nb=model.getNeededBuilder(buildername);
	if (nb!=null) {
            Iterator nbfl = nb.getFields();
	    int pos = 3;
	    while (nbfl.hasNext()) {
            	MMObjectNode virtual = builder.getNewNode("admin");
		NeededBuilderField nbf = (NeededBuilderField)nbfl.next();
            	virtual.setValue("dbname",nbf.getDBName());
            	virtual.setValue("dbstate",nbf.getDBState());
            	virtual.setValue("dbtype",nbf.getDBType());
            	virtual.setValue("dbsize",nbf.getDBSize());
            	virtual.setValue("dbkey",nbf.getDBKey());
            	virtual.setValue("dbnotnull",nbf.getDBNotNull());
            	virtual.setValue("dbinputpos",nbf.getEditorInputPos());
            	virtual.setValue("dbsearchpos",nbf.getEditorSearchPos());
            	virtual.setValue("dblistpos",nbf.getEditorListPos());
            	virtual.setValue("dbpos",pos++);
            	list.add(virtual);
	    }
	}
        return list;
    }


    public MMObjectNode getNeededBuilderInfo(String cloudmodelfile,String buildername,String language) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

	Model model = new Model(cloudmodelfile);
        NeededBuilder nb=model.getNeededBuilder(buildername);
        virtual.setValue("name",nb.getName());
        virtual.setValue("maintainer",nb.getMaintainer());
        virtual.setValue("version",nb.getVersion());
        virtual.setValue("extends",nb.getExtends());
        virtual.setValue("singularname",nb.getSingularName(language));
        virtual.setValue("pluralname",nb.getPluralName(language));
        virtual.setValue("description",nb.getDescription(language));
        virtual.setValue("extends",nb.getExtends());
        virtual.setValue("status",nb.getStatus());
        virtual.setValue("searchage",nb.getSearchAge());
        virtual.setValue("classname",nb.getClassName());
        return virtual;
    }


    public MMObjectNode getNeededBuilderFieldInfo(String cloudmodelfile,String buildername,String language,String field) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

	Model model = new Model(cloudmodelfile);
	if (model != null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb != null) {
        		NeededBuilderField nbf=nb.getField(field);
			if (nbf != null) {
            			virtual.setValue("dbname",nbf.getDBName());
		            	virtual.setValue("dbstate",nbf.getDBState());
            			virtual.setValue("dbtype",nbf.getDBType());
            			virtual.setValue("dbsize",nbf.getDBSize());
            			virtual.setValue("description",nbf.getDescription(language));
            			virtual.setValue("dbkey",nbf.getDBKey());
		            	virtual.setValue("dbnotnull",nbf.getDBNotNull());
            			virtual.setValue("dbinputpos",nbf.getEditorInputPos());
            			virtual.setValue("dbsearchpos",nbf.getEditorSearchPos());
            			virtual.setValue("dblistpos",nbf.getEditorListPos());
            			virtual.setValue("guiname",nbf.getGuiName(language));
			}
		}
	}
        return virtual;
    }


    public List getAllowedRelations(String cloudmodelfile) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

	Model model = new Model(cloudmodelfile);
        Iterator allowedrelations=model.getAllowedRelations();
        for (Iterator i = allowedrelations; i.hasNext();) {
            AllowedRelation ar=(AllowedRelation)i.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("from",ar.getFrom());
            virtual.setValue("to",ar.getTo());
            virtual.setValue("type",ar.getType());
            list.add(virtual);
        }
        return list;
    }

    public boolean addNeededBuilder(String cloudmodelfile,String builder,String maintainer,String version) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
		model.addNeededBuilder(builder,maintainer,version);
	}
	return false;
    }


    public boolean deleteNeededBuilder(String cloudmodelfile,String builder,String maintainer,String version) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
		model.deleteNeededBuilder(builder,maintainer,version);
		return true;
	}
	return false;
    }

    public boolean deleteNeededBuilderField(String cloudmodelfile,String builder,String field) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(builder);
		if (nb!=null) {
			nb.deleteField(field);
			model.writeModel();
			return true;
		}
	}
	return false;
    }

    public boolean addNeededRelDef(String cloudmodelfile,String source,String target,String direction,String guisourcename,String guitargetname,String builder) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
		model.addNeededRelDef(source,target,direction,guisourcename,guitargetname,builder);
	}
	return false;
    }


    public boolean deleteNeededRelDef(String cloudmodelfile,String source,String target,String direction,String guisourcename,String guitargetname,String builder) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
		model.deleteNeededRelDef(source,target,direction,guisourcename,guitargetname,builder);
	}
	return false;
    }


    public boolean addAllowedRelation(String cloudmodelfile,String from,String to,String type) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
		model.addAllowedRelation(from,to,type);
	}
	return false;
    }


    public boolean deleteAllowedRelation(String cloudmodelfile,String from,String to,String type) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
		model.deleteAllowedRelation(from,to,type);
	}
	return false;
    }


    public boolean setBuilderDescription(String cloudmodelfile,String buildername,String language,String newdescription) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
			nb.setDescription(language,newdescription);
			model.writeModel();
		}
	}
	return false;
    }


    public boolean setBuilderFieldDescription(String cloudmodelfile,String buildername,String field,String language,String newdescription) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
        		NeededBuilderField nbf=nb.getField(field);
			if (nbf != null) {
				nbf.setDescription(language,newdescription);
			}
			model.writeModel();
		}
	}
	return false;
    }


    public boolean addBuilderField(String cloudmodelfile,String buildername,String newname,String newtype,String newstatus,int newsize) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
        		NeededBuilderField nbf=nb.getField(newname);
			if (nbf == null && !newname.equals("")) {
				nb.addField(newname,newtype,newstatus,newsize);
				model.writeModel();
			}
		}
	}
	return false;
    }


    public boolean setBuilderFieldPositions(String cloudmodelfile,String buildername,String field,int inputpos, int searchpos, int listpos) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
        		NeededBuilderField nbf=nb.getField(field);
			if (nbf != null) {
				nbf.setEditorInputPos(inputpos);
				nbf.setEditorSearchPos(searchpos);
				nbf.setEditorListPos(listpos);
			}
			model.writeModel();
		}
	}
	return false;
    }


    public boolean setBuilderFieldDBValues(String cloudmodelfile,String buildername,String field,String dbname,String dbtype,String dbstate,int dbsize,String key,String notnull) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
        		NeededBuilderField nbf=nb.getField(field);
			if (nbf != null) {
				nbf.setDBName(dbname);
				nbf.setDBType(dbtype);
				nbf.setDBState(dbstate);
				nbf.setDBSize(dbsize);
				if (key.equals("true")) {
					nbf.setDBKey(true);
				} else {
					nbf.setDBKey(false);
				}
				if (notnull.equals("true")) {
					nbf.setDBNotNull(true);
				} else {
					nbf.setDBNotNull(false);
				}
			}
			model.writeModel();
		}
	}
	return false;
    }


    public boolean setBuilderFieldGuiName(String cloudmodelfile,String buildername,String field,String language,String newguiname) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
        		NeededBuilderField nbf=nb.getField(field);
			if (nbf != null) {
				nbf.setGuiName(language,newguiname);
			}
			model.writeModel();
		}
	}
	return false;
    }


    public boolean setBuilderSingularName(String cloudmodelfile,String buildername,String language,String newname) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
			nb.setSingularName(language,newname);
			model.writeModel();
		}
	}
	return false;
    }


    public boolean setBuilderPluralName(String cloudmodelfile,String buildername,String language,String newname) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
			nb.setPluralName(language,newname);
			model.writeModel();
		}
	}
	return false;
    }

    public boolean setBuilderStatus(String cloudmodelfile,String buildername,String newstatus) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
			nb.setStatus(newstatus);
			model.writeModel();
		}
	}
	return false;
    }

    public boolean setBuilderSearchAge(String cloudmodelfile,String buildername,String newsearchage) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
			nb.setSearchAge(newsearchage);
			model.writeModel();
		}
	}
	return false;
    }


    public boolean setBuilderClassName(String cloudmodelfile,String buildername,String newclassname) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
			nb.setClassName(newclassname);
			model.writeModel();
		}
	}
	return false;
    }


    public boolean setBuilderName(String cloudmodelfile,String buildername,String newname) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
			nb.setName(newname);
			model.writeModel();
		}
	}
	return false;
    }

    public boolean setBuilderMaintainer(String cloudmodelfile,String buildername,String newmaintainer) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
			nb.setMaintainer(newmaintainer);
			model.writeModel();
		}
	}
	return false;
    }


    public boolean setBuilderVersion(String cloudmodelfile,String buildername,String newversion) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
			nb.setVersion(newversion);
			model.writeModel();
		}
	}
	return false;
    }

    public boolean setBuilderExtends(String cloudmodelfile,String buildername,String newextends) {
	Model model = new Model(cloudmodelfile);
	if (model!=null) {
        	NeededBuilder nb=model.getNeededBuilder(buildername);
		if (nb!=null) {
			nb.setExtends(newextends);
			model.writeModel();
		}
	}
	return false;
    }
}
