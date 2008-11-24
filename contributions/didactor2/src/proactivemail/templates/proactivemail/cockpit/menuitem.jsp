<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- valid only in the 'provider' scope --%>
<mm:compare referid="scope" value="education">
  <mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:import id="cansee">false</mm:import>
  <di:hasrole referid="user" role="systemadministrator">
    <mm:import id="cansee" reset="true">true</mm:import>
  </di:hasrole> 
  <mm:compare referid="cansee" value="true">
    <mm:compare referid="type" value="div">
      <div class="menuSeparator"> </div>
      <div class="menuItem" id="menuChat">
        <a href="<mm:treefile page="/proactivemail/frontoffice/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar"><di:translate key="proactivemail.proactivemail"/></a>
      </div>
    </mm:compare>
  </mm:compare>
  </mm:cloud>
</mm:compare>
