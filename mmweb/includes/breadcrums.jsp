<%
int c =0;
while(c<currentPath.size()) {  
	%><mm:node number="<%= (String) currentPath.elementAt(c) %>"><%
		if(c>0) { %> > <% }
		%><a href="/index.jsp?portal=<mm:write referid="portal" />&page=<mm:field name="number" />"><mm:field name="title" /></a>
	</mm:node><%
	c++;
} %>
	
