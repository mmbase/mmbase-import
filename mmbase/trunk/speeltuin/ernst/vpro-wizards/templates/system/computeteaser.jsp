<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<mm:import externid="sectionnr" required="true" />
<mm:import externid="position" required="true" />
<mm:import externid="teasernr" required="true" />

<mm:cloud method="loginpage" loginpage="/edit/login.jsp">

	<%-- Als er al een teaser op de positie staat waar de nieuwe moet komen, zet deze teaser dan op een ongedefineerde positie. --%>
	<mm:list nodes="$sectionnr" path="sections,posrel,teasers" fields="posrel.pos" constraints="posrel.pos =${param.position}">
		<mm:node element="posrel">
			<mm:setfield name="pos"></mm:setfield>
		</mm:node>
	</mm:list>

	<%-- Zet de teaser op de gewenste lokatie --%>
	<mm:list nodes="$sectionnr" path="sections,posrel,teasers" fields="posrel.pos" constraints="teasers.number=${param.teasernr}">
		<mm:node element="posrel">
			<mm:setfield name="pos">${param.position}</mm:setfield>
		</mm:node>
	</mm:list>	
	
</mm:cloud>

<c:set var="referer">${cookie.referer.value}</c:set>
<% Thread.sleep(200);
	String url = ""+pageContext.getAttribute("referer");
	response.sendRedirect(url); %>