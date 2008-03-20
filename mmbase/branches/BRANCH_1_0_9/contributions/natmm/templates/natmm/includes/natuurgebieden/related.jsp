<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<%
PaginaHelper pHelper = new PaginaHelper(cloud);
int locCnt = 1;
int listSize =0; 
%>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,pos4rel,natuurgebieden" 
   fields="natuurgebieden.naam,natuurgebieden.number,natuurgebieden.bron,provincies.afkorting,provincies.number"
   orderby="natuurgebieden.bron" constraints="natuurgebieden.bron!=''">
	<mm:first>
      <mm:size jspvar="natuurgebiedenCount" vartype="String" write="false">
			<% listSize = Integer.parseInt(natuurgebiedenCount);%>
		</mm:size>
		<mm:node element="provincies">
			<mm:related path="posrel,images" max="1">
				<div style="position:relative">
					<img src="<mm:node element="images"><mm:image /></mm:node>" alt="" border="0">
					<%@include file="relatedcoordinates.jsp" %>	
				</div>	
			</mm:related>
		</mm:node>
      <%@include file="../../scripts/images.js" %>
    	<table width="100%" border="0" cellspacing="0" cellpadding="2">
		<tr>
		<td valign="top">
		<table border="0" cellspacing="0" cellpadding="2" style="width:100%;">
	</mm:first>
	<mm:field name="natuurgebieden.bron" jspvar="c" vartype="String" write="false">
      <% if(c.length()>1&&c.substring(0,1).equals("0")) { c = c.substring(1); } %>
		<tr>
		   <td align="right" valign="top"><%= c %></td><td align="left" valign="top">|</td>
		   <td align="left" valign="top"><a href="<mm:field name="natuurgebieden.number" jspvar="natuurgebieden_number" vartype="String" write="false"
		      ><%= pHelper.createItemUrl(natuurgebieden_number,paginaID,null,request.getContextPath())
		      %></mm:field>" onMouseOver="MM_swapImage('dot<%=c%>','','media/images/ngb/<%=c%>w.gif',1)" onMouseOut="MM_swapImage('dot<%=c%>','','media/images/ngb/<%=c%>.gif',1)"><mm:field name="natuurgebieden.naam" /></a></td>
		</tr>
		<% if((listSize / 2) == locCnt -1) {%>
		</table></td><td valign="top"><table border="0" cellspacing="0" cellpadding="2"  style="width:100%;">
		<%} locCnt++;%>
	</mm:field>
   <mm:last>
		</table>
		</td>
		</tr>
		</table>
		<br><br>
   </mm:last>
</mm:list>
</mm:cloud>