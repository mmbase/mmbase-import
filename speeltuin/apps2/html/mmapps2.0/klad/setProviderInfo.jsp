<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<% ApplicationManager am = MMBase.getApplicationManager(); 
   Provider provider = am.getProvider();
   String name=request.getParameter("name");
   String description=request.getParameter("description");

   if(!name.equals(provider.getName())) {
   	provider.setName(name);
   }
   if(!description.equals(provider.getDescription())) {
   	provider.setDescription(description);
   }

%>

<jsp:forward page="providerinfo.jsp" />

