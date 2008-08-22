<html>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

updating people...

<mm:listnodes type="people">
   <mm:field name="person_status">
      <mm:setfield>1</mm:setfield>
   </mm:field>
</mm:listnodes>
</mm:cloud>
</mm:content>
Ok.
</html>

