<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud jspvar="cloud" method="asis">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="extracomponents" />
<mm:import id="redirpage"><mm:treefile page="$extracomponents" objectlist="$includePath" /></mm:import>
<mm:redirect page="$redirpage" />
</mm:cloud>
