<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,pos4rel,natuurgebieden" 
   fields="natuurgebieden.naam,natuurgebieden.number,natuurgebieden.bron,provincies.afkorting,provincies.number"
   orderby="natuurgebieden.bron" constraints="natuurgebieden.bron!=''">
	<mm:field name="pos4rel.pos1" jspvar="x1" vartype="Integer" write="false"
		><mm:field name="pos4rel.pos2" jspvar="y1" vartype="Integer" write="false"
		><mm:field name="natuurgebieden.bron" jspvar="c" vartype="String" write="false">
     		<% if(c.length()>1&&c.substring(0,1).equals("0")) { c = c.substring(1); } %>
			<div style="position:absolute;left:<%= x1 %>px;top:<%= y1 %>px;width:5px;height:5px;text-align:center;font-size:11px;">
				<a target="_top" href="natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" />"<%
               %> alt="<mm:field name="natuurgebieden.naam" />" style="color:#FF0000;text-decoration:none;"<%
               %> onmouseover="simages('document.dummy','document.dummy','media/images/ngb/<%= c %>.gif','document.dot<%= c %>','document.dot<%= c %>','media/images/ngb/<%= c %>w.gif')"<%
               %> onmouseout="simgr();simgr()"><%
               %><img src="media/images/ngb/<%= c %>.gif" width="13" height="13" border="0" name="dot<%= c %>"></a></div>
		</mm:field
		></mm:field
		></mm:field	
></mm:list>