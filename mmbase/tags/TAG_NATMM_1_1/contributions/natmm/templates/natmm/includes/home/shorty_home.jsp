<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String rubriekID = request.getParameter("r");
   String styleSheet = request.getParameter("rs");
   String sID = request.getParameter("s");
   String paginaID = sID;
   String shortyRol = request.getParameter("sr");
   int maxShorties = 20;
   imgFormat = "shorty";
   PaginaHelper ph = new PaginaHelper(cloud);

%>
<%@include file="../../includes/shorty_logic_1.jsp" %>
<% for (int i =0; i<shortyCnt;i++){ %>
	<%@include file="../../includes/shorty_logic_2.jsp" %>
	<mm:node number="<%=shortyID[i]%>">
		<mm:field name="titel" write="false" jspvar="shorty_titel" vartype="String">
		<mm:field name="titel_zichtbaar" write="false" jspvar="shorty_tz" vartype="String">
		<mm:field name="omschrijving" write="false" jspvar="shorty_omschrijving" vartype="String">
		<table style="width:100%;margin-bottom:10px;" cellspacing="0" cellpadding="0">
			<% if(!shorty_titel.equals("")&&!shorty_tz.equals("0")){ %>
			<tr>
				<td style="background-color:#FFE864">
				   <mm:import id="divstyle">margin:2px 3px 2px 3px;line-height:95%;</mm:import>
				   <% // ** to put text in default blue color: class=\"defaulttxt\" 
               linkTXT = "<strong>" + shorty_titel + "</strong>"; %>
				   <%@include file="../../includes/validlink.jsp" %>
               <mm:remove referid="divstyle" />
				</td>
			</tr>
			<% } %>
			<tr> 
				<td class="shorty_home">
   				<% imgFormat = "shorty"; %>
         		<%@include file="../../includes/image_logic.jsp" %>
   				<% if(!shorty_omschrijving.equals("")){ 
   				   %><%= shorty_omschrijving %><% 
   				} %>
   				<mm:import id="divstyle" />
   				<mm:import id="hrefclass" />
   				<% linkTXT = readmoreTXT; %>
   				<%@include file="../../includes/validlink.jsp" %>
   				<mm:remove referid="hrefclass" />
               <mm:remove referid="divstyle" />
				</td>
			</tr>
		</table>
		</mm:field>
		</mm:field>
		</mm:field>
	</mm:node>
<% } %>
</mm:cloud>
