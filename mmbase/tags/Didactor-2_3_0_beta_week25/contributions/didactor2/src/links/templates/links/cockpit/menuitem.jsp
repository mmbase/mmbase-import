<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<mm:compare referid="scope" value="provider">
  <mm:cloud>
    <jsp:directive.include file="/shared/setImports.jsp" />
    <mm:treefile page="/links/index.jsp" objectlist="$includePath" referids="$referids"
                 write="false" id="treefile" />
    <mm:compare referid="type" value="div">
      <div class="menuSeperator"> </div>
      <div class="menuItem" id="menuAgenda">
        <a href="${treefile}" class="menubar"><di:translate key="links.title" /></a>
      </div>
    </mm:compare>
    <mm:compare referid="type" value="option">
      <option value="${treefile}"><di:translate key="links.title" /></option>
    </mm:compare>
  </mm:cloud>
</mm:compare>
