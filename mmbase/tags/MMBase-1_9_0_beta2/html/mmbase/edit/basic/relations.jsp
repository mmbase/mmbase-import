<mm:context id="relations">
 <%-- 
 Make sure that we are in a node;
 not specifying by 'numer' or 'referid' attribute makes the tag look for a parent 'NodeProvider' 
 --%>
 <mm:node id="this_node" jspvar="node">
   
   <mm:field id="this_node_number" name="number" write="false" />
   
   
   <%-- Determin the number of the nodemanager --%>
   <mm:field  name="otype" id="typedefNumber" write="false" />
   
   <table class="list" summary="relation overview" width="100%">
     <tr>
       <th colspan="8"><%=m.getString("relations.from")%></th>
     </tr>
     
     <%-- list all relation types, where we are the source --%>
     <% { String searchDir = "destination"; %>
     <%@ include file="relation.jsp" %>
     <% } %>
     <tr>
       <th colspan="8"><%=m.getString("relations.to")%></th>
     </tr>
     
     <%-- list all relation types, where we are the destination --%>
     <% { String searchDir = "source"; %>
     <%@ include file="relation.jsp" %>
     <% } %>
     
   </table>
  </mm:node>
</mm:context>
