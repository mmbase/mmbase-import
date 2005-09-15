<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.cms.CMSMessageBundle">
<mm:compare referid="type" value="div">
  <mm:list nodes="$provider" path="providers,posrel,pages" fields="pages.name,posrel.pos" orderby="posrel.pos">
  <mm:field name="pages.number" id="page" write="false">
  <div class="menuSeperator"> </div>
  <div class="menuItem">
    <a href="<mm:treefile page="/cms/index.jsp" objectlist="$includePath" referids="page,$referids" />" class="menubar">
    <mm:field name="pages.name"/>
    </a>
    </div>
  </mm:field>  
  </mm:list>
</mm:compare>
</fmt:bundle>
</mm:cloud>
