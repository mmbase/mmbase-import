<!-- START FILE: breadcrumb.jsp -->
<%
Nav bc = currentNav;
StringBuffer sb = new StringBuffer();
while( bc != null && bc.hasParent() ){
	sb.insert(0,"<a href=\""+ bc.baseURL +"&amp;page="+ bc.id +"\">"+ bc.name+" </a>");
	bc  = bc.getParent();
        if (bc != null && bc.hasParent() ){
	    sb.insert(0," > ");
        }
}%>
<%= sb.toString() %>
<!-- END FILE: breadcrumb.jsp -->
