<%@include file="/taglibs.jsp" %>
<% boolean isProduction = false; %>
<mm:cloud jspvar="cloud" method="<%= (isProduction ? "" : "http") %>" rank="<%= (isProduction ? "" : "basic user") %>">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/calendar.jsp" %>
<%

String sPageRef = (String) session.getAttribute("pageref");
if(sPageRef!=null&&!sPageRef.equals(paginaID)) { // set pagerefminone to sPagRef, set pageref to paginaID
   session.setAttribute("pagerefminone",sPageRef);
}
session.setAttribute("pageref",paginaID);

if(!paginaID.equals("-1")) {

   String template_url = ""; 
   %><mm:list nodes="<%= paginaID %>" path="pagina,paginatemplate" fields="paginatemplate.url,paginatemplate.naam"
   ><mm:field name="paginatemplate.url" jspvar="dummy" vartype="String" write="false"
      ><% template_url = dummy; 
   %></mm:field
   ></mm:list><%
   
   if(!template_url.equals("")) { 
      // include the parameters changed in getids.jsp
      %><jsp:include page="<%= template_url
            + "?page=" + paginaID
            + "&category=" + categoryId %>" 
      /><% 
   
   } else { 
      %><font color="#CC0000">Error:</font></b><br>Er moet nog een template gekoppeld worden aan de '<mm:node number="<%= paginaID 
          %>"><mm:field name="title"/></mm:node>' pagina.<% 
   } 
   
} else { 
   %><font color="#CC0000">Error:</font></b><br>De huidige gebruiker heeft niet voldoende rechten om een pagina van het Intranet te zien.<% 
}    
%>
</cache:cache>
</mm:cloud>