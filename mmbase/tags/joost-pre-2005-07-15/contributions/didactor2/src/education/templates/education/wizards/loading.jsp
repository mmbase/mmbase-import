<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
   <%@include file="/shared/setImports.jsp"%>

<%
   session.removeAttribute("show_metadata_in_list");
%>

<html>
<head></head>
    <body>
        <img src="<mm:treefile page="/education/wizards/gfx/loading.gif" objectlist="$includePath" referids="$referids" />" alt="OK">
    </body>
</html>
</mm:cloud>
</mm:content>

