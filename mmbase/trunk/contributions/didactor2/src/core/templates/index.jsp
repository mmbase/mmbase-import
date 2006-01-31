<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0" type="text/html" encoding="UTF-8" escaper="entities">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treefile page="cockpit.jsp" objectlist="$includePath" referids="$referids" write="false" id="redirpage" />
<mm:redirect page="$redirpage" />
</mm:cloud>
</mm:content>
