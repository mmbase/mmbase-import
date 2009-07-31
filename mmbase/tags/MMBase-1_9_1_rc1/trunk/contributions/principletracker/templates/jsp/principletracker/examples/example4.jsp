<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<%@ page import='org.mmbase.bridge.*' %>
<%@ page import='org.mmbase.bridge.implementation.*' %>
<%@ page import='org.mmbase.module.core.*' %>
<%@ page import='org.mmbase.storage.search.*' %>
<%@ page import='org.mmbase.storage.search.implementation.*' %>


<HTML>   
<HEAD>
<mm:cloud>
   <TITLE>Search Query Bridge Example 3</TITLE>
</HEAD>
  <hr>
   Example of how to create a query using the bridge, This code should normally be in java code and not in a jsp page its a example only. Use the taglibs in jsp pages and bridge code in your backend software.<br /><br />
   example3.html, same as example 2 but adds a contrained based on a number
  <hr>
   <pre>
     // create a cloud so we can work with the bridge.
     Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null); 

     // create all the needed NodeManagers
     NodeManager principlesetmanager = cloud.getNodeManager("principlesets");

     // create a new query object we can fill and execute
     Query query = cloud.createQuery();

     // add the first step (and in this case only step) to the query chain
     Step         step1 = query.addStep(principlesetmanager);

     // add a relation (default so insrel) step to the principlemanager
     RelationStep step2 = query.addRelationStep(principlemanager);

     // ask the relation step for the other side of the relation (principlemanager)
     Step         step3 = step2.getNext();

     // all fields we want to return have to be added to the query. You add them to the correct step
     // in the query chain. The Fields object can be obtained from the manager.
    StepField field1 = query.addField(step1, principlesetmanager.getField("name"));
    StepField field2 = query.addField(step3, principlemanager.getField("name"));
    StepField field3 = query.addField(step3, principlemanager.getField("qualification"));

    // add a number constrained to the query in this case we only want to
    // list the principles in the default set.
    query.addNode(step1,cloud.getNode("Principle.default"));

    // also demand that the principle's are binding
    query.setConstraint(query.createConstraint(field3, "Binding"));

     // execute the query and request the result as a iteration
     NodeIterator i = cloud.getList(query).nodeIterator();

     // loop the resultset 
     while (i.hasNext()) {

       // query the field we added and log it in this example
       log.info(node.getStringValue("principle.qualification")+"-"+node.getStringValue("principlesets.name")+"-"+node.getStringValue("principle.name"));

     }
   </pre>
  <hr>
  <%
    Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null); 

    NodeManager principlesetmanager = cloud.getNodeManager("principlesets");
    NodeManager principlemanager = cloud.getNodeManager("principle");
    Query query = cloud.createQuery();
    Step         step1 = query.addStep(principlesetmanager);
    RelationStep step2 = query.addRelationStep(principlemanager);
    Step         step3 = step2.getNext();

    StepField field1 = query.addField(step1, principlesetmanager.getField("name"));
    StepField field2 = query.addField(step3, principlemanager.getField("name"));
    StepField field3 = query.addField(step3, principlemanager.getField("qualification"));

    query.addNode(step1,cloud.getNode("Principle.default"));
    query.setConstraint(query.createConstraint(field3, "Binding"));

    NodeIterator i = cloud.getList(query).nodeIterator();
    while (i.hasNext()) {
      Node node = i.nextNode();
      %>
      <b>output=</b><%=node.getStringValue("principle.qualification")%> - <%=node.getStringValue("principlesets.name")%> - <%=node.getStringValue("principle.name")%><br />
     <%
    }
  %>
  <hr>
  <b>Cloud=</b><%=cloud%><br /><br />
  <b>Query=</b><%=query%><br /><br />

<body>
<center>
</mm:cloud>
</BODY>
</HTML>
