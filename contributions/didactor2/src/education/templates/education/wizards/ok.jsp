<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<img src="<mm:treefile page="/education/wizards/gfx/ok.gif" objectlist="$includePath" referids="$referids" />" alt="OK">
</mm:cloud>
</mm:content>
