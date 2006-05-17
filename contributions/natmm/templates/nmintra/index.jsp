<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/calendar.jsp" %>
<%

String sPageRef = (String) session.getAttribute("pageref");
if(sPageRef!=null&&!sPageRef.equals(pageId)) { // set pagerefminone to sPagRef, set pageref to pageId
   session.setAttribute("pagerefminone",sPageRef);
}
session.setAttribute("pageref",pageId);

if(!pageId.equals("-1")) {

   String template_url = ""; 
   %><mm:list nodes="<%= pageId %>" path="pagina,paginatemplate" fields="paginatemplate.url,paginatemplate.naam"
   ><mm:field name="paginatemplate.url" jspvar="dummy" vartype="String" write="false"
      ><% template_url = dummy; 
   %></mm:field
   ></mm:list><%
   
   if(!template_url.equals("")) { 
      // include the parameters changed in getids.jsp
      %><jsp:include page="<%= template_url
            + "?page=" + pageId
            + "&category=" + categoryId %>" 
      /><% 
   
   } else { 
      %><font color="#CC0000">Error:</font></b><br>Er moet nog een template gekoppeld worden aan de '<mm:node number="<%= pageId 
          %>"><mm:field name="title"/></mm:node>' pagina.<% 
   } 
   
} else { 
   %><font color="#CC0000">Error:</font></b><br>De huidige gebruiker heeft niet voldoende rechten om een pagina van het Intranet te zien.<% 
}    
%>
   
<%--/cache:cache --%>
</mm:cloud>