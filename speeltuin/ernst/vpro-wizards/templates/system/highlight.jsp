<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<mm:import externid="destination" required="true"/>
<mm:import externid="sectienr" required="true"/>

<mm:cloud method="loginpage" loginpage="/edit/login.jsp">
	<mm:listnodes nodes="$sectienr" path="sections,posrel,news"  orderby="posrel.pos" element="posrel">
		<mm:setfield name="pos"><mm:index/></mm:setfield>
	</mm:listnodes>
	<mm:maycreaterelation role="posrel" source="sectienr" destination="destination">
		<mm:createrelation role="posrel" source="sectienr" destination="destination">
			<mm:setfield name="pos">0</mm:setfield>
		</mm:createrelation>
		<c:set var="referer">${cookie.referer.value}</c:set>
		<%  Thread.sleep(200);
			String url = ""+pageContext.getAttribute("referer");
			response.sendRedirect(url); %>
	</mm:maycreaterelation>
	<mm:maycreaterelation role="posrel" source="sectienr" destination="destination" inverse="true">
		<% response.sendRedirect("/edit/system/error.jsp?error=1"); %>	
	</mm:maycreaterelation>
</mm:cloud>

