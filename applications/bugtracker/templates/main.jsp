<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="asis"> 

  <%@include file="parameters.jsp" %>

  <mm:import externid="sbugid" />
  <mm:import externid="sissue" />
  <mm:import externid="sstatus" />
  <mm:import externid="stype" />
  <mm:import externid="sarea" />
  <mm:import externid="sversion" />
  <mm:import externid="sfixedin" />
  <mm:import externid="spriority" >3</mm:import>
  <mm:import externid="smaintainer">-1</mm:import>
  <mm:import externid="ssubmitter">-1</mm:import>
  <mm:import externid="noffset" vartype="integer">0</mm:import>
  
  <mm:import externid="flap">search</mm:import>
  
  
  
  <%@ include file="mainparts/flaps_index.jsp" %>
  

  <mm:compare referid="flap" value="search">
    <%@ include file="mainparts/search.jsp" %>
  </mm:compare>
  <mm:compare referid="flap" value="lastchanges">
    <%@ include file="mainparts/lastchanges.jsp" %>
  </mm:compare>
  <mm:compare referid="flap" value="stats">
    <%@ include file="mainparts/statistics.jsp" %>
  </mm:compare>

</mm:cloud>
