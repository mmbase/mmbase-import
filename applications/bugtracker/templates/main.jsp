<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>

<mm:import externid="portal" jspvar="portal" />
<mm:notpresent referid="portal"><mm:remove referid="portal" /></mm:notpresent>
<mm:import externid="page"   jspvar="page2" />
<mm:notpresent referid="page"><mm:remove referid="page" /></mm:notpresent>

<mm:import externid="sbugid" />
<mm:import externid="sissue" />
<mm:import externid="sstatus" />
<mm:import externid="stype" />
<mm:import externid="sarea" />
<mm:import externid="sversion" />
<mm:import externid="sfixedin" />
<mm:import externid="spriority" />
<mm:import externid="smaintainer">-1</mm:import>
<mm:import externid="noffset">0</mm:import>

<mm:import externid="flap">search</mm:import>

<mm:import externid="cw" from="cookie" />
<mm:import externid="ca" from="cookie" />


<mm:import externid="base" />


<mm:url id="baseurl" page="$base" write="false" />

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
   %@ include file="mainparts/mysettings.jsp" %>
</mm:compare>
<mm:compare referid="flap" value="mybug">
   <%@ include file="mainparts/mybug.jsp" %>
</mm:compare>
</mm:cloud>
