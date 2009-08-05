<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- 'sources' is only valid in the 'education' scope --%>
<mm:compare referid="scope" value="education">
  <mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:compare referid="type" value="div">
    <div class="menuSeparator"> </div>
    <div class="menuItem" id="menuSources">
      <a href="<mm:treefile page="/sources/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar"><di:translate key="sources.sourcesmenuitem" /></a>
    </div>
  </mm:compare>
  
  <mm:compare referid="type" value="option">
    <option value="<mm:treefile page="/sources/index.jsp" objectlist="$includePath" referids="$referids" />">
      <di:translate key="sources.sourcesmenuitem" />
    </option>
  </mm:compare>
  </mm:cloud>
</mm:compare>
