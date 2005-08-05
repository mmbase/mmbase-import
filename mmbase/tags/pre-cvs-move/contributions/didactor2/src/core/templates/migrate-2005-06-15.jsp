<html>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

making all titles visible...<br/>
<mm:listnodes type="images"><mm:setfield name="showtitle">1</mm:setfield></mm:listnodes>
<mm:listnodes type="attachments"><mm:setfield name="showtitle">1</mm:setfield></mm:listnodes>
<mm:listnodes type="audiotapes"><mm:setfield name="showtitle">1</mm:setfield></mm:listnodes>
<mm:listnodes type="videotapes"><mm:setfield name="showtitle">1</mm:setfield></mm:listnodes>
<mm:listnodes type="urls"><mm:setfield name="showtitle">1</mm:setfield></mm:listnodes>
done.
</mm:cloud>
</mm:content>
</html>
