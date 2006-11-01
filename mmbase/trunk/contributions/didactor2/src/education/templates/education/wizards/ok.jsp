<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud method="asis">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <img src="<mm:treefile page="/education/wizards/gfx/ok.gif" objectlist="$includePath" referids="$referids" />" title="OK" alt="OK">
</mm:cloud>
</mm:content>
