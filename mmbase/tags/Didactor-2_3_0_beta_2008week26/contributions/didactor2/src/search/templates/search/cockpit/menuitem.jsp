<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
  <!-- changing form and div order give a result that search field is very heigh,
     and provider bar became twice higher than normal
 -->
  <form name="searchform" method="post" action="<mm:treefile page="/search/index.jsp" objectlist="$includePath" referids="$referids" />">
    <div class="menuItemSearch">
      <input type="hidden" name="search_type" value="AND"/>
      <input type="hidden" name="search_component" value=""/>
      <di:translate key="search.search" />:&nbsp; <input class="search" type="text" name="search_query" />
      <input type="image" src="<mm:treefile write="true" page="/gfx/icon_search.gif" objectlist="$includePath" />" title="<di:translate key="search.sendsearchrequest" />" alt="<di:translate key="search.sendsearchrequest" />" value="<di:translate key="search.sendsearchrequest" />" name="searchbutton" />
    </div>
  </form>
  
  <div class="spacer"> </div>
</mm:cloud>
</mm:content>
