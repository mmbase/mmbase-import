<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" jspvar="cloud"> 
<% String nType = ""; %>
<mm:listnodes type="archief" orderby="number" directions="DOWN">
   <mm:field name="original_node" jspvar="originalNode" write="false" vartype="String">
      <% nType = ""; %>
      <mm:node number="<%= originalNode %>" notfound="skipbody">
         <mm:nodeinfo type="type" write="false" jspvar="dummy" vartype="String">
            <% nType = dummy; %>
         </mm:nodeinfo>
      </mm:node>
      <% if(!nType.equals("")) { %>
         original node: <%= nType %> <%= originalNode %><br/>
         <mm:field name="node_data" jspvar="nodeData" write="false" vartype="String">
            data: <%= nodeData.replaceAll("<","&lt;").replaceAll(">","&gt;<br/>") %><br/>
         </mm:field>
      <% } else { %>
         original node: <%= originalNode %> does not exist anymore<br/>
      <% } %>
   </mm:field>
</mm:listnodes>

</mm:cloud>