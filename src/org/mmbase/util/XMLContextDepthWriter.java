/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;
import org.mmbase.module.core.*;

import org.mmbase.module.corebuilders.*;

/**
*/
public class XMLContextDepthWriter  {

    public static boolean writeContext(XMLApplicationReader app,XMLContextDepthReader capp,String targetpath,MMBase mmb,Vector resultmsgs) {

	// first get the startnode we need to walk x depth
	int startnode=getStartNode(capp,mmb);

	// get depth
	int depth=capp.getDepth();

	// get valid builders to filter
	Vector fb=getFilterBuilders(app.getNeededBuilders(),mmb.getTypeDef());	

	// the trick is to get all nodes until depth x and filter them
	Vector nodes=getSubNodes(startnode,0,depth,fb,new Vector(),mmb.getTypeDef());
	resultmsgs.addElement("Context found : "+nodes.size()+" nodes to save");

	// create the dir for the Data & resource files
	File file = new File(targetpath+"/"+app.getApplicationName());
	try {
		file.mkdirs();
	} catch(Exception e) {
		System.out.println("Can't create dir : "+targetpath+"/"+app.getApplicationName());
	}

	// write DataSources
	writeDataSources(app,nodes,targetpath,mmb,resultmsgs);


	// write relationSources
	writeRelationSources(app,nodes,targetpath,mmb,resultmsgs);

	return(true);
    }

    static void writeDataSources(XMLApplicationReader app, Vector nodes, String targetpath,MMBase mmb,Vector resultmsgs) {
	Enumeration res=app.getDataSources().elements();
	String subtargetpath=targetpath+"/"+app.getApplicationName()+"/";
	while (res.hasMoreElements()) {
		int nrofnodes=0;
		Hashtable bset=(Hashtable)res.nextElement();
		String name=(String)bset.get("builder");
		int type = mmb.getTypeDef().getIntValue(name);
		MMObjectBuilder bul=mmb.getMMObject(name);
		
		// write the header
		String body="<"+name+" exportsource=\"mmbase://127.0.0.1/install/b1\" timestamp=\"20000602143030\">\n";

		// lets enum the nodes and filter all of this type
		Enumeration nods=nodes.elements();
		while (nods.hasMoreElements()) {
			MMObjectNode node=bul.getNode(((Integer)nods.nextElement()).intValue());
			if (type==node.getIntValue("otype")) {
				int number=node.getIntValue("number");
				String owner=node.getStringValue("owner");

				// start the node
				String tm=mmb.OAlias.getAlias(number);
				if (tm!=null) {
					body+="\t<node number=\""+number+"\" owner=\""+owner+"\">\n";
				} else {
					body+="\t<node number=\""+number+"\" owner=\""+owner+"\" alias=\""+tm+"\">\n";
				}
				// write the values of the node
				Hashtable values=node.getValues();	
				Enumeration nd=values.keys();
				while (nd.hasMoreElements()) {
					String key=(String)nd.nextElement();
					body+=writeXMLField(key,node,subtargetpath,mmb);
				}
	
				// end the node
				body+="\t</node>\n\n";
				nrofnodes++;
			}
		
		}
	
		// write the footer	
		body+="</"+name+">\n";
		String filename=targetpath+"/"+app.getApplicationName()+"/"+name+".xml";
		System.out.println("Writing DataSource="+filename);
		saveFile(filename,body);
	
		resultmsgs.addElement("Saving "+nrofnodes+" "+name+" to : "+filename);
		
	}
   }	


    static void writeRelationSources(XMLApplicationReader app, Vector nodes, String targetpath,MMBase mmb,Vector resultmsgs) {
	Enumeration res=app.getRelationSources().elements();
	while (res.hasMoreElements()) {
		Hashtable bset=(Hashtable)res.nextElement();

	
		int nrofnodes=0;
		String name=(String)bset.get("builder");
		int type = mmb.getTypeDef().getIntValue(name);
		MMObjectBuilder bul=mmb.getMMObject(name);
		String namedrel="related";
		if (!name.equals("insrel")) {
			namedrel=name;
		}

		
		// write the header
		String body="<"+name+" exportsource=\"mmbase://127.0.0.1/install/b1\" timestamp=\"20000602143030\">\n";

		// lets enum the nodes and filter all of this type
		Enumeration nods=nodes.elements();
		while (nods.hasMoreElements()) {
			MMObjectNode node=bul.getNode(((Integer)nods.nextElement()).intValue());
			if (type==node.getIntValue("otype")) {
				int number=node.getIntValue("number");
				String owner=node.getStringValue("owner");
				int snumber=node.getIntValue("snumber");
				int dnumber=node.getIntValue("dnumber");

				// start the node
				body+="\t<node number=\""+number+"\" owner=\""+owner+"\" snumber=\""+snumber+"\" dnumber=\""+dnumber+"\" rtype=\""+namedrel+"\">\n";
				// write the values of the node
				Hashtable values=node.getValues();	
				Enumeration nd=values.keys();
				while (nd.hasMoreElements()) {
					String key=(String)nd.nextElement();
	
					if (!key.equals("number") && !key.equals("owner") && !key.equals("otype") && !key.equals("CacheCount") && !key.equals("snumber") && !key.equals("dnumber") && !key.equals("rnumber")) {
						body+="\t\t<"+key+">"+node.getValue(key)+"</"+key+">\n";
					}
				}
	
				// end the node
				body+="\t</node>\n\n";
				nrofnodes++;
			}
		}
	
		// write the footer	
		body+="</"+name+">\n";
		String filename=targetpath+"/"+app.getApplicationName()+"/"+name+".xml";
		resultmsgs.addElement("Saving "+nrofnodes+" "+name+" to : "+filename);
		System.out.println("Writing RelationSource="+filename);
		saveFile(filename,body);
	}
   }	


    static Vector getSubNodes(int startnode,int curdepth,int maxdepth,Vector fb,Vector nodes,MMObjectBuilder bul ) {
	MMObjectNode node=bul.getNode(startnode);
	if (node!=null) {
		Integer number=new Integer(node.getIntValue("number"));
		Integer type=new Integer(node.getIntValue("otype"));
		if (fb.contains(type) && !nodes.contains(number)) nodes.addElement(number);
		Enumeration rel=node.getRelations();
		while (rel.hasMoreElements()) {
			MMObjectNode node2=(MMObjectNode)rel.nextElement();
			Integer number2=new Integer(node2.getIntValue("number"));
			int snumber=node2.getIntValue("snumber");
			int dnumber=node2.getIntValue("dnumber");

			if (curdepth!=maxdepth) {
				if (snumber==number.intValue()) {
					// this is kinda tricky we need
					// to get the related node to
					// and check its type to be sure if
					// we should add the relation
					MMObjectNode node3=bul.getNode(dnumber);
					type=new Integer(node3.getIntValue("otype"));
					if (fb.contains(type) && !nodes.contains(number2)) nodes.addElement(number2);
					nodes=getSubNodes(dnumber,curdepth+1,maxdepth,fb,nodes,bul);
				} else {
					MMObjectNode node3=bul.getNode(snumber);
					type=new Integer(node3.getIntValue("otype"));
					if (fb.contains(type) && !nodes.contains(number2)) nodes.addElement(number2);
					nodes=getSubNodes(snumber,curdepth+1,maxdepth,fb,nodes,bul);
				}
			}
		}
	}
	return(nodes);
    }

    static Vector getFilterBuilders(Vector filter,TypeDef bul) {
	Vector results=new Vector();
	Enumeration res=filter.elements();
	while (res.hasMoreElements()) {
		Hashtable bset=(Hashtable)res.nextElement();
		String name=(String)bset.get("name");
		int value=bul.getIntValue(name);
		if (value!=-1) {
			results.addElement(new Integer(value));
		} else {
			System.out.println("XMLContextDepthWriter -> can't get intvalue for : "+name);
		}
	}
	return(results);
    }


    static int getStartNode(XMLContextDepthReader capp, MMBase mmb) {
	String builder=capp.getStartBuilder();
	String where=capp.getStartWhere();
	MMObjectBuilder bul=mmb.getMMObject(builder);
	if (bul!=null) {
		Enumeration results=bul.search(where);	
		if (results.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)results.nextElement();
			return(node.getIntValue("number"));
		}
	} else {
		System.out.println("ContextDepthWriter-> can't find builder ("+builder+")");
	}
	return(-1);
    }

	static boolean saveFile(String filename,String value) {
		File sfile = new File(filename);
		try {
			DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
			scan.writeBytes(value);
			scan.flush();
			scan.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return(true);
	}

	static boolean saveFile(String filename,byte[] value) {
		File sfile = new File(filename);
		try {
			DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
			scan.write(value);
			scan.flush();
			scan.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return(true);
	}

    public static boolean writeContextXML(XMLContextDepthReader capp,String filename) {
	String body="<contextdepth>\n";
	body+="\t<startnode>\n";
	body+="\t\t<builder>"+capp.getStartBuilder()+"</builder>\n";
	body+="\t\t<where>"+capp.getStartWhere()+"</where>\n";
	body+="\t</startnode>\n\n";
	body+="\t<depth>"+capp.getDepth()+"</depth>\n";
	body+="</contextdepth>\n";
	saveFile(filename,body);
	return(true);
    }


    private static String writeXMLField(String key,MMObjectNode node, String targetpath,MMBase mmb) {
	if (!key.equals("number") && !key.equals("owner") && !key.equals("otype") && !key.equals("CacheCount")) {
		// this is a bad way of doing it imho
		int type=node.getDBType(key);
		String stype=mmb.getTypeDef().getValue(node.getIntValue("otype"));	
		if (type==FieldDefs.TYPE_BYTE) {
			String body="\t\t<"+key+" file=\""+stype+"/"+node.getIntValue("number")+"."+key+"\" />\n";
			File file = new File(targetpath+stype);
			try {
				file.mkdirs();
			} catch(Exception e) {
				System.out.println("Can't create dir : "+targetpath+stype);
			}
			byte[] value=node.getByteValue(key);
			saveFile(targetpath+stype+"/"+node.getIntValue("number")+"."+key,value);
			return(body);
		} else {
			String body="\t\t<"+key+">"+node.getValue(key)+"</"+key+">\n";
			return(body);
		}

	}
	return("");
  }

}
