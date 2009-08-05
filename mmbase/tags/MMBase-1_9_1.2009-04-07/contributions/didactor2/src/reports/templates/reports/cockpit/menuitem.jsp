<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- chat is only valid in the 'provider' scope --%>
<mm:compare referid="scope" value="provider">
  <mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <di:hasrole role="teacher">
    <mm:compare referid="type" value="div">
      <div class="menuSeparator"> </div>
      <div class="menuItem" id="menuReports">
        <a href="<mm:treefile page="/reports/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar"><di:translate key="reports.reportsmenu"/></a>
      </div>
    </mm:compare>
  </di:hasrole>
  </mm:cloud>
</mm:compare>
