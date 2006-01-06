<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%! public String getParameter(String parameterStr, String queryStr) {
 	String parameterValue = null;
	int qpos = queryStr.indexOf("&" + parameterStr + "=");
	if(qpos==-1) { qpos = queryStr.indexOf("&amp;" + parameterStr + "="); }
	if(qpos==-1) { qpos = queryStr.indexOf(parameterStr + "="); }
	if(qpos>-1) {
		int vstart = queryStr.indexOf("=",qpos)+1;
		int vend = queryStr.indexOf("&",vstart);
		parameterValue = queryStr.substring(vstart,vend);
	}
	return parameterValue;
}
%><%
String queryString = request.getQueryString()+"&";
String portalId = getParameter("portal",queryString);
String pageId = getParameter("page",queryString);
String personId = getParameter("person",queryString);
String pageUrl = "/index.jsp?portal=" + portalId 
	+ "&page=" + pageId  
	+ "&person=" + personId; 
session.invalidate();
response.sendRedirect(pageUrl);
%>
