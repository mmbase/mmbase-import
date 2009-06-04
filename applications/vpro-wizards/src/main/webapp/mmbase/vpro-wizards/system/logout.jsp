<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<mm:cloud method="logout"/>
<c:set var="referer">${cookie.referer.value}</c:set>
<%
    String url = ""+pageContext.getAttribute("referer");
    response.sendRedirect(java.net.URLDecoder.decode(url));
%>