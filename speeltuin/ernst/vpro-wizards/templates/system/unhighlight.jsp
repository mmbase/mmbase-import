<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<mm:import externid="object" required="true"/>
<mm:import externid="sectienr" required="true"/>

<mm:cloud method="loginpage" loginpage="/edit/login.jsp">
	<mm:node number="$object" notfound="skipbody">
		<mm:deletenode />
	</mm:node>
	<mm:listnodes nodes="$sectienr" path="sections,posrel,news"  orderby="posrel.pos" element="posrel">
	    <mm:setfield name="pos"><mm:index jspvar="index"><%=index.intValue()-1%></mm:index></mm:setfield>
	</mm:listnodes>
</mm:cloud>

<c:set var="referer">${cookie.referer.value}</c:set>
<% Thread.sleep(200);
	String url = ""+pageContext.getAttribute("referer");
	response.sendRedirect(url); %>