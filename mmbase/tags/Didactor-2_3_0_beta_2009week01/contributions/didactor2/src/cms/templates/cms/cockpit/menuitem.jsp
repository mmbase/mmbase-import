<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:compare referid="type" value="div">
  <mm:list nodes="$provider" path="providers,posrel,pages" fields="pages.name,posrel.pos" orderby="posrel.pos">
  <mm:field name="pages.number" id="page" write="false">
  <div class="menuSeparator"> </div>
  <div class="menuItem">
    <a href="<mm:treefile page="/cms/index.jsp" objectlist="$includePath" referids="page,$referids" />" class="menubar">
    <mm:field name="pages.name"/>
    </a>
    </div>
  </mm:field>  
  </mm:list>
</mm:compare>
</mm:cloud>
