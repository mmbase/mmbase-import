<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<div class="menuItemSearch">
  <div style="display: none">
  <form name="searchform" method="post" action="<mm:treefile page="/search/index.jsp" objectlist="$includePath" referids="$referids" />">
  <input type="hidden" name="search_type" value="AND"/>
  <input type="hidden" name="search_component" value=""/>
  </div>
    <di:translate key="search.search" />:&nbsp; <input class="search" type="text" name="search_query" />
	<input type="image" src="<mm:treefile write="true" page="/gfx/icon_search.gif" objectlist="$includePath" />" alt="<di:translate key="search.sendsearchrequest" />" value="<di:translate key="search.sendsearchrequest" />" name="searchbutton" />
  <div style="display:none"></form></div>
</div>
<div class="spacer"> </div>
</mm:cloud>
</mm:content>
