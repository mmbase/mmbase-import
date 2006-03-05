<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<%
PaginaHelper pHelper = new PaginaHelper(cloud);
int locCnt = 1;
int listSize =0; 
%>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" 
   fields="natuurgebieden.naam,natuurgebieden.number,natuurgebieden.bron,provincies.afkorting,provincies.number"
   orderby="natuurgebieden.bron" constraints="natuurgebieden.bron!=''">
	<mm:first>
      <mm:size jspvar="natuurgebiedenCount" vartype="String" write="false">
			<% listSize = Integer.parseInt(natuurgebiedenCount);%>
		</mm:size>
	   <mm:field name="provincies.afkorting" jspvar="afk" vartype="String" write="false">
         <% if(afk.equals("DR")){ %><jsp:include page="../../includes/ngb/dr.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
         <% if(afk.equals("FL")){ %><jsp:include page="../../includes/ngb/fl.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
         <% if(afk.equals("FR")){ %><jsp:include page="../../includes/ngb/fr.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
         <% if(afk.equals("GL")){ %><jsp:include page="../../includes/ngb/gl.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
         <% if(afk.equals("GR")){ %><jsp:include page="../../includes/ngb/gr.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
         <% if(afk.equals("LB")){ %><jsp:include page="../../includes/ngb/lb.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
         <% if(afk.equals("NB")){ %><jsp:include page="../../includes/ngb/nb.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
         <% if(afk.equals("NH")){ %><jsp:include page="../../includes/ngb/nh.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
         <% if(afk.equals("OV")){ %><jsp:include page="../../includes/ngb/ov.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
         <% if(afk.equals("UT")){ %><jsp:include page="../../includes/ngb/ut.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
         <% if(afk.equals("ZH")){ %><jsp:include page="../../includes/ngb/zh.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
         <% if(afk.equals("ZL")){ %><jsp:include page="../../includes/ngb/zl.jsp" flush="true"><jsp:param name="p" value="<%= paginaID %>"/></jsp:include><% } %>
	   </mm:field>
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
		      ><%= pHelper.createItemUrl(natuurgebieden_number, paginaID, null ,request.getRequestURI())
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