<mm:node number="<%= paginaID %>">
	<mm:field name="omschrijving" jspvar="text" vartype="String" write="false">
	<% 
   	if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { 
   	   %><span class="black"><mm:write /></span><br/><br/><%
      } 
   %>
	</mm:field>
</mm:node>