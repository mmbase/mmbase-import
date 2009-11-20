<%@ tag body-content="empty"  %>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@ attribute name="name" required="true"  %>
<%@ attribute name="value" required="true"  %>
<%@ attribute name="path" description="default is the current location" %>
<%@ attribute name="maxage" description="default is -1 (expires after session)"  %>

<%  // Zet cookie met referer
    Cookie cookie = new Cookie( (String) jspContext.getAttribute("name"),(String) jspContext.getAttribute("value"));
    String path = (String) jspContext.getAttribute("path");
    if(path != null){
        cookie.setPath(path);
    }
    String maxage = (String) jspContext.getAttribute("maxage");
    if(maxage == null) {
        maxage = "-1";
    }
    cookie.setMaxAge(new Integer(maxage).intValue()); // Ruim cookie op als browser sluit.
    response.addCookie(cookie);
%>
