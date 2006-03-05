<%
int expireTime =  3600*24; // cache for one day
String refreshID = request.getParameter("refresh");  
if(refreshID==null) { refreshID = ""; }
if(refreshID.equals("on")) {
   session.setAttribute("refresh","on"); 
} else if(refreshID.equals("off")) {
   session.setAttribute("refresh","off"); 
} else {
   refreshID = (String) session.getAttribute("refresh");
   if(refreshID==null) { refreshID = "off"; }
}
if(refreshID.equals("on")) { expireTime = 0; }
%><%@include file="../includes/cachekey.jsp" %>
