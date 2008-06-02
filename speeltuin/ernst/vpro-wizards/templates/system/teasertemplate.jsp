<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<mm:import externid="sectionnr" required="true" />
<mm:import externid="template" required="true" />

<mm:cloud method="loginpage" loginpage="/edit/login.jsp">
	<mm:list nodes="$sectionnr" path="sections,insrel,templates" >
		<mm:field id="relation" name="insrel.number" write="false" />
		<mm:deletenode referid="relation"  />
	</mm:list>
	<mm:compare referid="template" value="eenTeaser">
		<mm:node number="dvteenteaser" id="eenTeaser"/>
		<mm:createrelation destination="sectionnr" source="eenTeaser" role="related"></mm:createrelation>
	</mm:compare>
	<mm:compare referid="template" value="vierTeasers">
		<mm:node number="dvtvierteasers" id="vierTeasers"/>
		<mm:createrelation destination="sectionnr" source="vierTeasers" role="related"></mm:createrelation>
	</mm:compare>
</mm:cloud>

<c:set var="referer">${cookie.referer.value}</c:set>
<% Thread.sleep(200);
	String url = ""+pageContext.getAttribute("referer");
	response.sendRedirect(url); %>