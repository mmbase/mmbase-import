<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<mm:content postprocessor="reducespace">
<fmt:bundle basename="nl.didactor.component.search.SearchMessageBundle">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<div class="menuItemSearch">
  <div style="display: none">
  <form name="searchform" method="post" action="<mm:treefile page="/search/index.jsp" objectlist="$includePath" referids="$referids" />">
  <input type="hidden" name="search_type" value="AND"/>
  <input type="hidden" name="search_component" value=""/>
  </div>
    zoeken:&nbsp; <input class="search" type="text" name="search_query" />
	<input type="image" src="<mm:treefile write="true" page="/gfx/icon_search.gif" objectlist="$includePath" />" alt="<fmt:message key="SENDSEARCHREQUEST"/>" value="<fmt:message key="SENDSEARCHREQUEST"/>" name="searchbutton" />
  <div style="display:none"></form></div>
</div>
<div class="spacer"> </div>
</mm:cloud>
</fmt:bundle>
</mm:content>
