<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud jspvar="cloud" method="anonymous">
<%@include file="/shared/setImports.jsp" %>
<mm:import id="imageurl" jspvar="imageurl" vartype="String"><mm:treefile page="/pdf/headerimage.png" objectlist="$includePath" referids="$referids"/></mm:import>
<% response.sendRedirect(imageurl); %>
</mm:cloud>
</mm:content>
