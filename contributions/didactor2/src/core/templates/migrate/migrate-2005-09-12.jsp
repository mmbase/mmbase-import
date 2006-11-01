<html>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:listnodes type="components">
   <mm:field name="mayrelateclasses">
      <mm:compare value="1" inverse="true">
         <mm:setfield>0</mm:setfield>
      </mm:compare>
   </mm:field>
</mm:listnodes>
</mm:cloud>
</mm:content>
Ok.
</html>

