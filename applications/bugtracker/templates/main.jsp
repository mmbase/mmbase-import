<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:present referid="portal" inverse="true">
<mm:import externid="portal" jspvar="portal">none</mm:import>
<mm:import externid="page" jspvar="page2">none</mm:import>
</mm:present>
<mm:import externid="sbugid" jspvar="sbugid" />
<mm:import externid="sissue" jspvar="sissue" />
<mm:import externid="sstatus" jspvar="sstatus" />
<mm:import externid="stype" jspvar="stype" />
<mm:import externid="sarea" jspvar="sarea" />
<mm:import externid="sversion" jspvar="sversion" />
<mm:import externid="spriority" jspvar="spriority" />
<mm:import externid="noffset" jspvar="noffset">0</mm:import>
<mm:import externid="where" jspvar="where" />
<mm:import externid="flap">search</mm:import>

<mm:import externid="cw" from="cookie" />
<mm:import externid="ca" from="cookie" />
<mm:present referid="ca">
   <mm:present referid="cw">
			<mm:listnodes type="users" constraints="account='$ca' and password='$cw'" max="1">
			 	<mm:import id="user"><mm:field name="number" /></mm:import>
			</mm:listnodes>
   </mm:present>
</mm:present>

<%@include file="actions.jsp" %>

<%-- first the selection part --%>
<body>


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
<mm:compare referid="flap" value="mysettings">
   <%@ include file="mainparts/mysettings.jsp" %>
</mm:compare>
<mm:compare referid="flap" value="mybug">
   <%@ include file="mainparts/mybug.jsp" %>
</mm:compare>
</mm:cloud>
