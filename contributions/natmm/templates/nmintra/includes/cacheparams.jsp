<%
int expireTime =  3600*24; // cache for one day
int newsExpireTime = 3600; // news pages are refreshed every hour
String previewID = request.getParameter("preview");  
boolean isPreview = false;
if(previewID==null) { previewID = ""; }
if(previewID.equals("on")) {
   session.setAttribute("preview","on"); 
} else if(previewID.equals("off")) {
   session.setAttribute("preview","off"); 
} else {
   previewID = (String) session.getAttribute("preview");
   if(previewID==null) { previewID = "off"; }
}
if(previewID.equals("on")) { isPreview = true; }
%><%@include file="../includes/cachekey.jsp" %>