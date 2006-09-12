<mm:node number="<%= paginaID %>">
  <mm:field name="titel_zichtbaar">
     <mm:compare value="0" inverse="true">
       <div class="pageheader"><mm:field name="titel" /></div>
     </mm:compare>
  </mm:field>
  <mm:field name="omschrijving" jspvar="text" vartype="String" write="false">
	<% 
   	if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { 
   	   %><span class="black"><mm:write /></span><br/><br/><%
      } 
   %>
	</mm:field>
</mm:node>