<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- 'sources' is only valid in the 'education' scope --%>
<fmt:bundle basename="nl.didactor.component.sources.SourcesMessageBundle">
<mm:compare referid="scope" value="education">
  <mm:cloud loginpage="/login.jsp" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:compare referid="type" value="div">
    <div class="menuSeperator"> </div>
    <div class="menuItem" id="menuSources">
      <a href="<mm:treefile page="/sources/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar"><fmt:message key="SOURCESMENUITEM" /></a>
    </div>
  </mm:compare>
  
  <mm:compare referid="type" value="option">
    <option value="<mm:treefile page="/sources/index.jsp" objectlist="$includePath" referids="$referids" />">
      <fmt:message key="SOURCESMENUITEM" />
    </option>
  </mm:compare>
  </mm:cloud>
</mm:compare>
</fmt:bundle>