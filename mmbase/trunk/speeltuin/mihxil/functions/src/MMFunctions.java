/* 

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.module.ProcessorModule;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.*;
import org.mmbase.module.core.*;

import java.lang.reflect.*;
import java.io.*;
import java.util.*;

import org.w3c.dom.*;


import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;

/**
 * 
 * @javadoc
 */

public class MMFunctions extends ProcessorModule {

    private static final Logger log = Logging.getLoggerInstance(MMFunctions.class); 
    private static Hashtable functionsets=new Hashtable();
    private static NodeManager manager;
    private static CloudContext context;
    private static Cloud cloud;
    
    public MMFunctions() {
    }

    public void init() {
        String functionsetfile=getInitParameter("functionsetfile");
        String filename = MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator+functionsetfile;
        File file = new File(filename);
        if(file.exists()) {
            XMLBasicReader reader = new XMLBasicReader(filename);
            if(reader!=null) {
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
                            setfile = MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator+n3.getNodeValue();
                            decodeFunctionSet(setfile,name);
                        }
					

					
                    }
                }
            } else {
                log.error("Can't read/parse functionsetfile : "+filename);
            }
	} else {
            log.error("Can't open functionsetfile : "+filename);
	}
    }


    private void decodeFunctionSet(String filename,String setname) {

        File file = new File(filename);
        if(file.exists()) {
            XMLBasicReader reader = new XMLBasicReader(filename);
	
	    String status=reader.getElementValue("functionset.status");
	    String version=reader.getElementValue("functionset.version");
	    String description=reader.getElementValue("functionset.description");

   	    FunctionSet functionset=new FunctionSet(setname,version,status,description);
	    functionsets.put(setname,functionset);
	
	    functionset.setFileName(filename);

	    for (Enumeration n = reader.getChildElements("functionset","function");n.hasMoreElements();) {
                try {

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
                        
                        
                        // read the return types and values
                        a=reader.getElementByPath(element,"function.return");
                        String returntype=reader.getElementAttributeValue(a,"type");
                        
                        // read the parameters
                        List paramList = new ArrayList();
                        for (Enumeration n2 = reader.getChildElements(element,"param");n2.hasMoreElements();) {
                            Element param_element= (Element)n2.nextElement();
                            String paramname=reader.getElementAttributeValue(param_element,"name");
                            String paramtype=reader.getElementAttributeValue(param_element,"type");
                            description=reader.getElementAttributeValue(param_element,"description");
                            Parameter p = new Parameter(paramname, Class.forName(paramtype));
                            paramList.add(p);
                            p.setDescription(description);
                            
                        // check for a default value
                            org.w3c.dom.Node n3=param_element.getFirstChild();
                            if (n3!=null) {
                                String defaultvalue=n3.getNodeValue();
                                p.setDefaultValue(defaultvalue);
                            } else {
                                /*
                                  if (paramtype.equals("String")) {
                                  p.setDefaultValue("");
                                  } else if (paramtype.equals("int")) {
                                  p.setDefaultValue("-1");
                                  } 
                                */
                            }
                            
                        }
                        Class implementor = Class.forName(classname);
                        
                        Parameter[] def = (Parameter[]) paramList.toArray(new Parameter[0]);
                        Parameters temp = new Parameters(def);

                        Method m = implementor.getMethod(methodname, temp.toClassArray());
                        
                        ReflectionFunction fun = new ReflectionFunction(name, def, new ReturnType(Class.forName(returntype), "bla bla"), m);


                        
                        for (Enumeration n2 = reader.getChildElements(a,"field");n2.hasMoreElements();) {
                            Element return_element= (Element)n2.nextElement();
                            String returnname=reader.getElementAttributeValue(return_element,"name");
                            String returnvaluetype=reader.getElementAttributeValue(return_element,"type");
                            description=reader.getElementAttributeValue(return_element,"description");
                            ReturnType r = new ReturnType(Class.forName(returnvaluetype), description);
                            fun.getReturnType().addSubType(returnname, r);
                        }
                        
                        
                        functionset.addFunction(fun);
                    }
                } catch  (ClassNotFoundException cnfe) {
                    log.error(cnfe.getMessage());
                } catch  (NoSuchMethodException nsme) {
                    log.error(nsme.getMessage());
                }
	    }
        } else {
            log.error("Can't read functionset : "+filename);
        }
    }
    /*

    public static Object performFunction(String setName,String functionName,Hashtable params) {
	// get the correct set
	FunctionSet set=(FunctionSet)functionsets.get(setName);
	if (set!=null) {
            ReflectionFunction fun=set.getFunction(functionName);
            if (fun!=null) {
                return fun.performFunction(params);
            } else {
                log.error("No function with name : "+functionName+" in set : "+setName);
                log.error("functions : "+set);
            }
	} else {
            log.error("No functionset with name : "+setName);
	}
	return null;
    }
    */

    public Object getFunctionSets() {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();

	// create a result list
	org.mmbase.bridge.NodeList list=context.createNodeList();

	Enumeration e=functionsets.elements();
	while (e.hasMoreElements()) {
            FunctionSet s=(FunctionSet)e.nextElement();
            newnode=manager.createNode();
            newnode.setStringValue("name",s.getName());
            newnode.setStringValue("status",s.getStatus());
            newnode.setStringValue("version",s.getVersion());
            newnode.setStringValue("description",s.getDescription());
            list.add(newnode);
	}
	return list;
    }


    public Object getFunctionSetInfo(String setname) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();
	FunctionSet set=(FunctionSet)functionsets.get(setname);
	if (set!=null) {
            newnode.setStringValue("name",set.getName());
            newnode.setStringValue("status",set.getStatus());
            newnode.setStringValue("version",set.getVersion());
            newnode.setStringValue("description",set.getDescription());
	}
	return newnode;
    }


    public Object getFunctionInfo(String setname,String functionname) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();
	FunctionSet set=(FunctionSet)functionsets.get(setname);
	if (set!=null) {
            ReflectionFunction fun = (ReflectionFunction)set.getFunction(functionname);

            newnode.setStringValue("description",fun.getDescription());
            newnode.setStringValue("name",fun.getName());
            // newnode.setStringValue("type",fun.getReturnType().getType().getName());
            newnode.setStringValue("class",fun.getClass().getName());
            newnode.setStringValue("method",fun.getMethodName());
            newnode.setStringValue("returntype",fun.getReturnType().getType().getName());
	}
	return newnode;
    }


    /*
    public Object getParamInfo(String setname,String functionname,String paramname) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();
	FunctionSet set=(FunctionSet)functionsets.get(setname);
	if (set!=null) {
            Function fun=(MMFunction)set.getFunction(functionname);
            if (fun!=null) {
                MMFunctionParam p=fun.getParam(paramname);
                if (p!=null) {
                    newnode.setStringValue("name",p.getName());
                    newnode.setStringValue("type",p.getType());
                    Object o=p.getDefaultValue();
                    if (o!=null) {
                        newnode.setStringValue("defaultvalue",""+o);
                    } else {
                        newnode.setStringValue("defaultvalue","undefined");
                    }
                    newnode.setStringValue("description",p.getDescription());
                }
            }
	}
	return newnode;
    }


    public Object getReturnValueInfo(String setname,String functionname,String returnvaluename) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();
	FunctionSet set=(FunctionSet)functionsets.get(setname);
	if (set!=null) {
            ReflectionFunction fun = (ReflectionFunction)set.getFunction(functionname);
            if (fun!=null) {
                MMFunctionReturnValue r=fun.getReturnValue(returnvaluename);
                if (r!=null) {
                    newnode.setStringValue("name",r.getName());
                    newnode.setStringValue("type",r.getType());
                    newnode.setStringValue("description",r.getDescription());
                }
            }
	}
	return newnode;
    }
    */

    public Object changeSet(String setname,String status,String version,String description) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();
	FunctionSet set=(FunctionSet)functionsets.get(setname);
	if (set!=null) {
            set.setStatus(status);
            set.setVersion(version);
            set.setDescription(description);
            set.save();
            newnode.setStringValue("error","");
	}
	return newnode;
    }


    /*
    public Object changeFunction(String setname,String functionname,String classname,String method,String returntype,String description) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();
	FunctionSet set=(FunctionSet)functionsets.get(setname);
	if (set!=null) {
            MMFunction fun=(MMFunction)set.getFunction(functionname);
            if (fun!=null) {
                fun.setClassName(classname);
                fun.setMethodName(method);
                fun.setReturnType(returntype);
                fun.setDescription(description);
                set.save();
            }
            newnode.setStringValue("error","");
	}
	return newnode;
    }


    public Object changeFunctionParam(String setname,String functionname,String paramname,String type,String defaultvalue,String description) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();
	FunctionSet set=(FunctionSet)functionsets.get(setname);
	if (set!=null) {
            MMFunction fun=(MMFunction)set.getFunction(functionname);
            if (fun!=null) {
                MMFunctionParam p=(MMFunctionParam)fun.getParam(paramname);
                if (p!=null) {
                    p.setType(type);
                    if (defaultvalue.equals("undefined")) {
                        p.clearDefaultValue();
                    } else {
                        p.setDefaultValue(defaultvalue);
                    }
                    p.setDescription(description);
                    set.save();
                }
            }
            newnode.setStringValue("error","");
	}
	return newnode;
    }


    public Object changeFunctionReturnValue(String setname,String functionname,String returnvaluename,String type,String description) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();
	FunctionSet set=(FunctionSet)functionsets.get(setname);
	if (set!=null) {
            MMFunction fun=(MMFunction)set.getFunction(functionname);
            if (fun!=null) {
                MMFunctionReturnValue r=(MMFunctionReturnValue)fun.getReturnValue(returnvaluename);
                if (r!=null) {
                    r.setType(type);
                    r.setDescription(description);
                    set.save();
                }
            }
            newnode.setStringValue("error","");
	}
	return newnode;
    }


    public Object removeFunctionReturnValue(String setname,String functionname,String returnvaluename) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();
	FunctionSet set=(FunctionSet)functionsets.get(setname);
	if (set!=null) {
            MMFunction fun=(MMFunction)set.getFunction(functionname);
            if (fun!=null) {
                MMFunctionReturnValue r=(MMFunctionReturnValue)fun.getReturnValue(returnvaluename);
                if (r!=null) {
                    fun.removeReturnValue(r);
                    set.save();
                }
            }
            newnode.setStringValue("error","");
	}
	return newnode;
    }


    public Object addFunctionReturnValue(String setname,String functionname,String returnvaluename,String type,String description) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();
	FunctionSet set=(FunctionSet)functionsets.get(setname);
	if (set!=null) {
            MMFunction fun=(MMFunction)set.getFunction(functionname);
            if (fun!=null) {
                MMFunctionReturnValue r=new MMFunctionReturnValue(returnvaluename,type);
                r.setDescription(description);
                fun.addReturnValue(returnvaluename,r);
                set.save();
            }
            newnode.setStringValue("error","");
	}
	return newnode;
    }


    public Object getFunctions(String setname) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();

	// create a result list
	org.mmbase.bridge.NodeList list=context.createNodeList();

	FunctionSet set=(FunctionSet)functionsets.get(setname);

	if (set!=null) {	
            Enumeration e=set.getFunctions();
            while (e.hasMoreElements()) {
                MMFunction f=(MMFunction)e.nextElement();
                newnode=manager.createNode();
                newnode.setStringValue("name",f.getName());
                newnode.setStringValue("description",f.getDescription());
                newnode.setStringValue("params",f.getParamsString());
                list.add(newnode);
            }
	}
	return list;
    }


    public Object getFunctionParams(String setname,String functionname) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();

	// create a result list
	org.mmbase.bridge.NodeList list=context.createNodeList();

	FunctionSet set=(FunctionSet)functionsets.get(setname);

	if (set!=null) {	
            MMFunction fun=(MMFunction)set.getFunction(functionname);
            if (fun!=null) {
                Enumeration e=fun.getParams().elements();
                while (e.hasMoreElements()) {
                    MMFunctionParam p=(MMFunctionParam)e.nextElement();
                    newnode=manager.createNode();
                    newnode.setStringValue("name",p.getName());
                    newnode.setStringValue("type",p.getType());
                    Object o=p.getDefaultValue();
                    if (o!=null) {
                        newnode.setStringValue("default",""+o);
                    } else {
                        newnode.setStringValue("default","undefined");
                    }
                    newnode.setStringValue("description",p.getDescription());
                    list.add(newnode);
                }
            }
	}
	return list;
    }


    public Object getFunctionReturnValues(String setname,String functionname) {
	if (cloud==null) getCloud();
	org.mmbase.bridge.Node newnode=manager.createNode();

	// create a result list
	org.mmbase.bridge.NodeList list=context.createNodeList();

	FunctionSet set=(FunctionSet)functionsets.get(setname);

	if (set!=null) {	
            ReflectionFunction fun = (ReflectionFunction)set.getFunction(functionname);
            if (fun!=null) {
                Enumeration e=fun.getReturnValues().elements();
                while (e.hasMoreElements()) {
                    MMFunctionReturnValue r=(MMFunctionReturnValue)e.nextElement();
                    newnode=manager.createNode();
                    newnode.setStringValue("name",r.getName());
                    newnode.setStringValue("type",r.getType());
                    newnode.setStringValue("description",r.getDescription());
                    list.add(newnode);
                }
            }
	}
	return list;
    }
    */
    private static void getCloud() {
	cloud=LocalContext.getCloudContext().getCloud("mmbase");
	context=LocalContext.getCloudContext();
       	manager=cloud.getNodeManager("typedef");
    }


}
