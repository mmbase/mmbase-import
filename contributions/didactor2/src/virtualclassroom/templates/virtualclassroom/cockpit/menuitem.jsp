<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- chat is only valid in the 'education' scope --%>
<mm:compare referid="scope" value="education">
  <mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:compare referid="type" value="div">
    <div class="menuSeparator"> </div>
    <div class="menuItem" id="menuChat">
      <a href="<mm:treefile page="/virtualclassroom/frontoffice/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar"><di:translate key="virtualclassroom.virtualclassroommenu"/></a>
    </div>
  </mm:compare>
  </mm:cloud>
</mm:compare>
