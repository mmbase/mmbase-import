<%--
  This template shows all components in the current education in frame
--%>

<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>


<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<html>
<head>
    <%@include file="/shared/setImports.jsp" %>
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
</head>

<body>
   <%
      String sReturnURL = request.getRequestURL().toString();
   %>
   <mm:import id="components_show_cockpit" reset="true">false</mm:import>
   <%@include file="body.jsp" %>
</body>

</mm:cloud>
</mm:content>
