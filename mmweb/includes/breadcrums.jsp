<!-- START FILE: breadcrumb.jsp -->
<%
int c =0;
while(c<currentPath.size()) {  
	%><mm:node number="<%= (String) currentPath.elementAt(c) %>"><%
		if(c>0) { %> > <% }
// this jsp is included from /index.jsp, which contains the function. See documentation there.
%>
<!-- <mm:write referid="portal" jspvar="myportal" write="false"/> -->
<!-- <mm:field name="number" jspvar="mypage" write="false"/> -->
<%-- <%=createUrlXXX(myportal, "page", mypage)%> --%>
<a href="/index.jsp?portal=<mm:write referid="portal"/>&page=<mm:field name="number"/>">
<mm:field name="title" />
</a>
	</mm:node><%
	c++;
} %>

<!-- END FILE: breadcrumb.jsp -->
