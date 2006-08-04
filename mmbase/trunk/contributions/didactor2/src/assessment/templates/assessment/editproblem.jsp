<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content>
<mm:cloud method="delegate" jspvar="cloud">

  <%@include file="/shared/setImports.jsp" %>

  <mm:redirect page="/assessment/index.jsp" referids="$referids"/>

</mm:cloud>
</mm:content>
