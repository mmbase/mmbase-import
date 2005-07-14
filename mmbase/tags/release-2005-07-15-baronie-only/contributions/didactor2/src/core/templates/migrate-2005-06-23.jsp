<html>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

making titles of questions visible...<br/>
<mm:listnodes type="questions"><mm:setfield name="showtitle">1</mm:setfield></mm:listnodes>
done.
</mm:cloud>
</mm:content>
</html>

