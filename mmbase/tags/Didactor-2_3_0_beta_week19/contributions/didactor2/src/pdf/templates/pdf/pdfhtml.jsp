<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud jspvar="cloud" method="anonymous">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="number" required="true"/>
<html>
<head><title></title>
</head>
<body>
<div style="font-size: 11px;">
<mm:treeinclude page="/pdf/pdfpart.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="partnumber"><mm:write referid="number"/></mm:param>
    <mm:param name="level">1</mm:param>
</mm:treeinclude>
</div>
</body>
</html>
</mm:cloud>
</mm:content>
