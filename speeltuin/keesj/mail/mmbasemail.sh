#!/bin/sh
#! The following hack allows java to reside anywhere in the PATH.
//bin/sh -c "exec /usr/local/java/bin/java -classpath /home/keesj/bsh-2.0b1.jar bsh.Interpreter $0 $*"; exit
addClassPath("/home/keesj/rmmci.jar");

import org.mmbase.bridge.*;

HashMap user = new HashMap();
user.put("username","admin");
user.put("password","admin2k");

Cloud cloud = ContextProvider.getCloudContext("rmi://127.0.0.1:1111/remotecontext").getCloud("mmbase","name/password",user);

NodeManager nodeManager = cloud.getNodeManager("news");
BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
StringBuffer sb = new StringBuffer();
String line = null;

while(  (line = br.readLine()) != null){
		  	sb.append(line);
		  	sb.append("\n");
}

Node node = nodeManager.createNode();
node.setStringValue("body",sb.toString());
node.commit();
