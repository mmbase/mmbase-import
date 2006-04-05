<%@include file="/taglibs.jsp" %>
<%@include file="../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<% 
String paginaID = request.getParameter("p"); 
String sID = paginaID;
PaginaHelper ph = new PaginaHelper(cloud);

// *** max 20 faq's (vgv'en) per page. 
// *** first determine whether there are vgv's related to the page
boolean vgvFound = false;
int vgvCnt =0; 
String[] shortyID = new String[20]; %>
<mm:list  nodes="<%=paginaID%>"  path="pagina,contentrel,vgv" 
   fields="vgv.number,vgv.titel"  max="50" orderby="contentrel.pos">
	<mm:field name="vgv.number" write="false" jspvar="vgv_number" vartype="String">
		<%  shortyID[vgvCnt] = vgv_number;
		vgvCnt++;
		vgvFound = true;
		%>
	</mm:field>
</mm:list>
<mm:import id="divstyle" />
<mm:import id="hrefclass" />
<%
// *** show the vgv's ***
for (int i =0; i<vgvCnt;i++){
	%><%@include file="../includes/shorty_logic_2.jsp" %>
	<mm:node number="<%= shortyID[i] %>">
		<mm:field name="size" write="false" jspvar="vgv_size" vartype="String">
		<mm:field name="titel" write="false" jspvar="vgv_titel" vartype="String">
		<mm:field name="intro" write="false" jspvar="vgv_intro" vartype="String">
		<table width="100%" cellspacing="0"  cellpadding="0">
			<tr>
				<td style="width:150;vertical-align:top;">
   				<%= vgv_titel %>
   			</td>
   			<td style="vertical-align:top;">
   				<% if(!vgv_intro.equals("")){ %><span class="colortxt"><%=vgv_intro%></span><br><% } %>
   	         <% linkTXT = readmoreTXT; altTXT=""; %>
   				<%@include file="../includes/validlink.jsp" %>
   		   </td>
   		   <td style="width:27;vertical-align:bottom;horizontal-align:middle;">
   				<a href="#top"><img src="media/arrowup_vragen.gif" border="0" /></a>
   			</td>
			</tr>
		</table>
		</mm:field>
		</mm:field>
		</mm:field>
	</mm:node>
	<table class="dotline"><tr><td height="3"></td></tr></table><% 
} %>
</mm:cloud>