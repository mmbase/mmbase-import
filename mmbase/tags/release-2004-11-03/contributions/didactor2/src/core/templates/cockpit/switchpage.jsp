<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="extracomponents" />
<mm:import id="redirpage"><mm:treefile page="$extracomponents" objectlist="$includePath" /></mm:import>
<mm:redirect page="$redirpage" />
</mm:cloud>
