<html>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

making titles of questions visible...<br/>
<mm:listnodes type="questions"><mm:setfield name="showtitle">1</mm:setfield></mm:listnodes>
done.
</mm:cloud>
</mm:content>
</html>

