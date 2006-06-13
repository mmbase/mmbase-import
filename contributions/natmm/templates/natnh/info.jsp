<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/header.jsp" %>
<%
int thisOffset = 0;
try {
	if(offsetId!=null&&!offsetId.equals("")){
		thisOffset = Integer.parseInt(offsetId);
		offsetId ="";
	}
} catch(Exception e) {} 

if(!articleId.equals("-1")) { 
	
	%>
	<a href="index.jsp?r=<%= rubriekId %>&p=<%= paginaID %>">Meer nieuws >></a><br><br>
	<mm:list nodes="<%= articleId %>" path="artikel">
		<%@include file="includes/relatedarticle.jsp" %>
	</mm:list>
	<%

} else {  

	String articleConstraint =	"artikel.embargo < " + (nowSec + 15*60) + "  AND (artikel.use_verloopdatum='0' OR artikel.verloopdatum > '" + nowSec + "' )";
	%><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" 
		orderby="artikel.embargo" directions="DOWN" 
		offset="<%= "" + thisOffset*10 %>" max="10" constraints="<%= articleConstraint %>">
		<%
		String titelClass = "pageheader"; 
		String readmoreUrl = ""; 
		%>
		<mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false">
			<% readmoreUrl = ph.createItemUrl(artikel_number, paginaID, null, request.getContextPath()); %>
		</mm:field>
		<%@include file="includes/summaryrow.jsp" %>
	</mm:list><% 
	
	// show navigation to other pagina if there are more than 10 artikel
	int listSize = 0; 
	%><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" constraints="<%= articleConstraint %>"
		><mm:first><mm:size jspvar="dummy" vartype="Integer" write="false"><% listSize = dummy.intValue();  %></mm:size></mm:first
	></mm:list
	><% 
	if(listSize>10) { 
	
		%><table cellpadding="0" cellspacing="0" border="0" align="center">
			<tr>
				<td><img src="media/spacer.gif" width="10" height="1"></td>
				<td><img src="media/spacer.gif" width="1" height="1"></td>
				<td><div><%
					if(thisOffset>0) { 
						%><a href="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>&o=<%= thisOffset-1 %>">[<<- vorige ]</a>&nbsp;&nbsp;<%
					}
					for(int i=0; i < (listSize/10 + 1); i++) {	
						if(i==thisOffset) {
							%><%= i+1 %>&nbsp;&nbsp;<%
						} else { 
							%><a href="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>&o=<%= i %>"><%= i+1 %></a>&nbsp;&nbsp;<%
						} 
					}
					if(thisOffset+1<(listSize/10 + 1)) { 
						%><a href="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>&o=<%= thisOffset+1 %>">[volgende ->>]</a><%
					} 
				%></div>
				</td>
			</tr>
			<tr>
				<td><img src="media/spacer.gif" width="1" height="10"></td>
				<td><img src="media/spacer.gif" width="1" height="10"></td>
				<td><img src="media/spacer.gif" width="1" height="10"></td>
			</tr>
		</table><%
	} 
} 
%>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>