<%
int shortyCnt = 0; 
String[] shortyID = new String[maxShorties];
String targetType = "shorty";
String shortyPath = request.getParameter("sp");
if(shortyPath==null) { shortyPath = "pagina,rolerel"; }
String relType = shortyPath.substring(shortyPath.indexOf(",")+1);
// *** first find out whether there are shorties / teasers related to the pagina, natuurgebied, vgv, etc.
%>
<mm:present referid="showteaser"><% targetType = "teaser"; %></mm:present> 
<mm:listcontainer path="<%= shortyPath + "," + targetType %>">
		<% 
		if(relType.equals("rolerel")) { 
		   %><mm:constraint field="rolerel.rol" operator="EQUAL" value="<%= shortyRol %>" /><% 
		} %>
		<mm:list 
         nodes="<%= sID %>"
         fields="<%= targetType + ".number" %>"  
		   max="<%= "" + maxShorties %>"
         orderby="<%= relType + ".pos" %>">
			<mm:field name="<%= targetType + ".number" %>" write="false" jspvar="this_number" vartype="String">
			<% shortyID[shortyCnt] = this_number;
				shortyCnt++;
			%>
			</mm:field>
  		</mm:list>
</mm:listcontainer>
<% if(shortyCnt==0&&relType.equals("rolerel")){
// if no shorties / teasers found, find out whether there are shorties / teasers related to the rubriek
   shortyPath = "rubriek,rolerel";
   %>
   <mm:listcontainer path="<%= shortyPath + "," + targetType %>">
   		<% 
   		if(relType.equals("rolerel")) { 
   		   %><mm:constraint field="rolerel.rol" operator="EQUAL" value="<%= shortyRol %>" /><% 
   		} %>
   		<mm:list 
            nodes="<%= rubriekID %>" 
            fields="<%= targetType + ".number" %>"
   		   max="<%= "" + maxShorties %>"
            orderby="<%= relType + ".pos" %>">
   			<mm:field name="<%= targetType + ".number" %>" write="false" jspvar="this_number" vartype="String">
   			<%  shortyID[shortyCnt] = this_number;
   				 shortyCnt++;
   			%>
   			</mm:field>
     		</mm:list>
   </mm:listcontainer>
<% } %>
<!-- shorty: <%= sID %>,<%= shortyPath + "," + targetType %>,<%= shortyRol %> -> <%= shortyCnt %> -->
