/* 

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.*;
import org.mmbase.module.core.*;

import java.net.*;
import java.io.*;
import java.util.*;

import org.w3c.dom.*;


import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;

/**
 * 
 * @javadoc
 */

public class FunctionSets {

    private static Logger log = Logging.getLoggerInstance(FunctionSets.class.getName()); 
    private static Hashtable functionsets=new Hashtable();
    private static NodeManager manager;
    private static CloudContext context;
    private static Cloud cloud;
    private static String functionsetfile;
    private static boolean init;
   
 
    public static final String DTD_FUNCTIONSET_1_0 = "functionset_1_0.dtd";
    public static final String DTD_FUNCTIONSETS_1_0 = "functionsets_1_0.dtd";

    public static final String PUBLIC_ID_FUNCTIONSET_1_0 = "-//MMBase//DTD functionset config 1.0//EN";
    public static final String PUBLIC_ID_FUNCTIONSETS_1_0 = "-//MMBase//DTD functionsets config 1.0//EN";

    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_FUNCTIONSET_1_0, DTD_FUNCTIONSET_1_0, FunctionSets.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_FUNCTIONSETS_1_0, DTD_FUNCTIONSETS_1_0, FunctionSets.class);
    }


    public static void readSets() {
	init=true;
        functionsetfile="functionsets.xml";
        String filename = MMBaseContext.getConfigPath()+File.separator+"functions"+File.separator+functionsetfile;
        File file = new File(filename);
        if(file.exists()) {
            	XMLBasicReader reader = new XMLBasicReader(filename,FunctionSets.class);
            	if(reader!=null) {
    			functionsets=new Hashtable();
	          	for(Enumeration ns=reader.getChildElements("functionsets","functionset");ns.hasMoreElements(); ) {
            			Element n=(Element)ns.nextElement();

   		        	NamedNodeMap nm=n.getAttributes();
                    		if (nm!=null) {
					String name=null;
					String setfile=null;

					// decode name
                       			org.w3c.dom.Node n3=nm.getNamedItem("name");
                        		if (n3!=null) {
						name=n3.getNodeValue();
					}

					// decode filename
                       			n3=nm.getNamedItem("file");
                        		if (n3!=null) {
        					setfile = MMBaseContext.getConfigPath()+File.separator+"functions"+File.separator+n3.getNodeValue();
						decodeFunctionSet(setfile,name);
					}
					

					
				}
			}
		} else {
			log.error("Can't read/parse functionsetfile : "+filename);
		}
	} else {
		log.error("Can't open functionsetfile : "+filename+" !");
	}
   }


   private static void decodeFunctionSet(String filename,String setname) {

        File file = new File(filename);
	log.info("ARGGG1");
        if(file.exists()) {
	   log.info("ARGGG2");
            XMLBasicReader reader = new XMLBasicReader(filename,FunctionSets.class);
	
	    String status=reader.getElementValue("functionset.status");
	    String version=reader.getElementValue("functionset.version");
	    String description=reader.getElementValue("functionset.description");

   	    FunctionSet functionset=new FunctionSet(setname,version,status,description);
	    functionsets.put(setname,functionset);
	
	    functionset.setFileName(filename);

	    for (Enumeration n = reader.getChildElements("functionset","function");n.hasMoreElements();) {
	            Element element= (Element)n.nextElement();
    		    String name=reader.getElementAttributeValue(element,"name");
		    if (name!=null) {

    			Element a=reader.getElementByPath(element,"function.type");
			String type=reader.getElementValue(a);

    			a=reader.getElementByPath(element,"function.description");
			description=reader.getElementValue(a);

    			a=reader.getElementByPath(element,"function.class");
			String classname=reader.getElementValue(a);

    			a=reader.getElementByPath(element,"function.method");
			String methodname=reader.getElementValue(a);

			// log.info("name="+name+" class="+classname+" method="+methodname);

			// read the return types and values
    			a=reader.getElementByPath(element,"function.return");
    		    	String returntype=reader.getElementAttributeValue(a,"type");
			// if (returntype!=null && !returntype.equals("")) fun.setReturnType(returntype);
	    		for (Enumeration n2 = reader.getChildElements(a,"field");n2.hasMoreElements();) {
	            		Element return_element= (Element)n2.nextElement();
    		    		String returnname=reader.getElementAttributeValue(return_element,"name");
    		    		String returnvaluetype=reader.getElementAttributeValue(return_element,"type");
    		    		description=reader.getElementAttributeValue(return_element,"description");
				// FunctionReturnValue r=new FunctionReturnValue(returnname,returnvaluetype);
				// fun.addReturnValue(returnname,r);
				// r.setDescription(description);
			}


			// read the parameters
			Vector tmpparams=new Vector();
			Parameter par;
	    		for (Enumeration n2 = reader.getChildElements(element,"param");n2.hasMoreElements();) {
	            		Element param_element= (Element)n2.nextElement();
    		    		String paramname=reader.getElementAttributeValue(param_element,"name");
    		    		String paramtype=reader.getElementAttributeValue(param_element,"type");
    		    		description=reader.getElementAttributeValue(param_element,"description");

				// log.info("param="+paramname+" "+paramtype);
				
				// FunctionParam p=new FunctionParam(paramname,paramtype);
				// fun.addParam(paramname,p);
				// p.setDescription(description);
		
				// check for a default value
                       		org.w3c.dom.Node n3=param_element.getFirstChild();
                        	if (n3!=null) {
					String defaultvalue=n3.getNodeValue();
					if (paramtype.equals("String")) {
						par=new Parameter(paramname,String.class,defaultvalue);
						tmpparams.add(par);
					} else if (paramtype.equals("Integer")) {
						try {
							par=new Parameter(paramname,Integer.class,new Integer(Integer.parseInt(defaultvalue)));
						} catch(Exception e) {
							par=new Parameter(paramname,Integer.class,new Integer(-1));
						}
						tmpparams.add(par);

					} else if (paramtype.equals("int")) {
						try {
							par=new Parameter(paramname,int.class,new Integer(Integer.parseInt(defaultvalue)));
						} catch(Exception e) {
							par=new Parameter(paramname,int.class,new Integer(-1));
						}
						tmpparams.add(par);
					} 
				} else {
					if (paramtype.equals("String")) {
						par=new Parameter(paramname,String.class,"");
						tmpparams.add(par);
					} else if (paramtype.equals("Integer")) {
						par=new Parameter(paramname,Integer.class,new Integer(-1));
						tmpparams.add(par);
					} else if (paramtype.equals("int")) {
						par=new Parameter(paramname,int.class,new Integer(-1));
						tmpparams.add(par);
					} 
				}

			}


			// setParameterDefinition(paramdefs);


			Parameter[] params = new Parameter[tmpparams.size()];
			int i=0;
	    		for (Enumeration e2=tmpparams.elements();e2.hasMoreElements();) {
					params[i++]=(Parameter)e2.nextElement();
			}
			//log.info("PARAMS="+params);
			SetFunction fun=new SetFunction(name,params,new ReturnType(Object.class,null));
			if (type!=null && !type.equals("")) fun.setType(type);
			if (description!=null && !description.equals("")) fun.setDescription(description);
			if (classname!=null && !classname.equals("")) fun.setClassName(classname);
			if (methodname!=null && !methodname.equals("")) fun.setMethodName(methodname);
			functionset.addFunction(fun);
		    }
	    }
        } else {
		log.error("Can't read functionset : "+filename);
        }
    }

    public static Function getFunction(String setName, String functionName) {
	if (!init) readSets();

	FunctionSet set=(FunctionSet)functionsets.get(setName);
	if (set==null) { // retry hack for mmpm
		readSets();
		set=(FunctionSet)functionsets.get(setName);
	}

	if (set!=null) {
		Function fun=set.getFunction(functionName);
		if (fun!=null) {
			return fun;
		} else {
			log.error("No function with name : "+functionName+" in set : "+setName);
			log.error("functions : "+set);
		}
	} else {
		log.error("No functionset with name : "+setName);
	}
	return null;
    }

}
