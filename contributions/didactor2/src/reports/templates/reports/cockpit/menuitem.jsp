<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- chat is only valid in the 'provider' scope --%>
<mm:compare referid="scope" value="provider">
  <mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:compare referid="type" value="div">
    <div class="menuSeperator"> </div>
    <div class="menuItem" id="menuReports">
      <a href="<mm:treefile page="/reports/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar"><di:translate key="reports.reportsmenu"/></a>
    </div>
  </mm:compare>
  </mm:cloud>
</mm:compare>
