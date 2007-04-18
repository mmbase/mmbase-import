<% if (true) {
%><span class="<%= styleClass %>">
<mm:field name="title" jspvar="title" vartype="String" write="false">
         <mm:isnotempty><b><%= title.toUpperCase() %></b><br/></mm:isnotempty>
</mm:field>
<mm:field name="description" jspvar="text" vartype="String" write="false">
        <% if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { %><mm:write /><br/><% } %>
</mm:field>
<% boolean beginUL = false; %>
<mm:related path="posrel,pagina,gebruikt,paginatemplate" orderby="posrel.pos" directions="UP" searchdir="destination"><%
			   if (!beginUL) { beginUL = true; 
   %><ul type="square" class="<%= styleClass %>" style="margin:0px;margin-left:15px;"><% 
			   } 
   %><li>
      <a href="<mm:field name="paginatemplate.url"/>?p=<mm:field name="pagina.number"/>">
         <span style="text-decoration:underline;" class="<%= styleClassDark %>">
            <mm:field name="pagina.titel"/>
         </span>
      </a>
					</li>
</mm:related>
<mm:related path="posrel,link" orderby="posrel.pos" directions="UP"><%
			   if (!beginUL) { beginUL = true; 
   %><ul type="square" class="<%= styleClass %>" style="margin:0px;margin-left:15px;"><% 
			   }
			   %><li><a href="<mm:field name="link.url"	/>" target="<mm:field name="link.target" />">
         <span style="text-decoration:underline;" class="<%= styleClassDark %>"><mm:field name="link.titel"/></span>
          </a></li>
</mm:related>
<mm:related path="posrel,projects" orderby="posrel.pos" directions="UP"><%
if (!beginUL) { beginUL = true; 
%><ul type="square" class="<%= styleClass %>" style="margin:0px;margin-left:15px;"><% 
} 
%><li><a href="archive.jsp?p=<mm:list path="paginatemplate,gebruikt,pagina" constraints="paginatemplate.url LIKE '%archive.jsp'" max="1"><mm:field name="pagina.number" /></mm:list
                              >&project=<mm:field name="projects.number"
/>"><span style="text-decoration:underline;" class="<%= styleClassDark %>"><mm:field name="projects.titel"/></span></a></li>
</mm:related>
<%	
if (beginUL) { 
%></ul><%
}
int currentGroupId = 0;
String groupIds[]= {	"Alle betrokkenen",
"Opdrachtgever",
"Projectleider",
"Projectmedewerker"
}; %>
<mm:related path="readmore,attachments" orderby="readmore.readmore,readmore.pos" directions="UP,UP"
><mm:first><table></mm:first>
<mm:field name="readmore.readmore" jspvar="dummy" vartype="String" write="false"
><% if (!dummy.equals("")) {
						int thisGroupId = Integer.parseInt(dummy);
						if ( thisGroupId > currentGroupId) { 
   %><tr><td colspan="2"><span class="<%= styleClass %>"><b><%= groupIds[thisGroupId] %></b></span></td></tr><% 
							currentGroupId = thisGroupId;
						}
}
%></mm:field>
<mm:node element="attachments"
><tr><td style="vertical-align:top"><%
			         String imgName = ""; 
						String docType = "";
				%><mm:field name="filename" jspvar="dummy" vartype="String" write="false"
					><%@include file="../includes/attachmentsicon.jsp" 
				%></mm:field
				><% if (!imgName.equals("")) {
						%><a href="<mm:attachment />" target="_blank"><img src="<%= imgName 
								%>" alt="download <%= docType %>: <mm:field name="title"
								/>" border="0" style="vertical-align:text-bottom" /><% } 
                                                         %></a><span class="<%= styleClass %>" style="font:17px;">|</span></td>
				   <td><a href="<mm:attachment />" target="_blank" style="font-weight:normal;"><mm:field name="title"/></a></td></tr>
			</mm:node
><mm:last></table></mm:last
></mm:related><%
} %>