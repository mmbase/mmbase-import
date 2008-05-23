<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ page import="org.mmbase.bridge.*,java.util.*,nl.leocms.util.*,nl.leocms.util.tools.*,nl.leocms.applications.*" %>

<html>
   <head>
   </head>
   
   <body>
<%
   String currentNodeNumber = new String("");
   int pos = 0;
%>

   <mm:cloud jspvar="cloud" method="pagelogon" username="admin" password="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>">
      <mm:list path="paragraaf,related,attachments" fields="paragraaf.number,related.number,attachments.number,attachments.titel" orderby="paragraaf.number,attachments.titel">
         <mm:field name="paragraaf.number" jspvar="parNumber" vartype="String" write="false">
            <mm:field name="related.number" jspvar="relNumber"vartype="String"  write="false">
               <mm:field name="attachments.number" jspvar="attNumber" vartype="String" write="false">
                  <%= parNumber %> (<mm:field name="paragraaf.titel"/>) -> <%= relNumber %> -> <%= attNumber %>
                  
                  <%
                  try {
                     if (currentNodeNumber.equals(parNumber)) {
                        pos++;
                     }
                     else {
                        pos = 0;
                     }
                     
                     Node sourceNode = cloud.getNode(parNumber);
                     Relation existingRelation = cloud.getRelation(relNumber);
                     Node destinationNode = cloud.getNode(attNumber);
                     
                     Relation newRelation = null;
                     String newRelationName = "posrel";
                     
                     RelationManager relationManager = cloud.getRelationManager(
                        sourceNode.getNodeManager().getName(),
                        destinationNode.getNodeManager().getName(),
                        newRelationName);
                     
                     newRelation = relationManager.createRelation(sourceNode, destinationNode);
                     
                     if (newRelation != null) {
                        newRelation.setIntValue("pos", pos);
                        newRelation.commit();
                        existingRelation.delete(true);
                        pos++;
                     }
                     
                     currentNodeNumber = parNumber;
                  }
                  catch (Exception e) {
                  %>
                     <%= e.toString() %>
                  <%
                  }
                  %>
                  
                  <br/>
               </mm:field>
            </mm:field>
         </mm:field>
      </mm:list>
      
      <br/>
      is now:<br/>
      
      <mm:list path="paragraaf,posrel,attachments" fields="paragraaf.number,posrel.number,posrel.pos,attachments.number,attachments.titel" orderby="paragraaf.number,attachments.titel">
         <mm:field name="paragraaf.number" jspvar="parNumber" vartype="String" write="false">
            <mm:field name="posrel.number" jspvar="posrelNumber"vartype="String"  write="false">
               <mm:field name="attachments.number" jspvar="attNumber" vartype="String" write="false">
                  <%= parNumber %> (<mm:field name="paragraaf.titel"/>) -> <%= posrelNumber %> -> <%= attNumber %>
                  <mm:field name="posrel.pos" />
                  <br/>
               </mm:field>
            </mm:field>
         </mm:field>
      </mm:list>
   </mm:cloud>
</body>