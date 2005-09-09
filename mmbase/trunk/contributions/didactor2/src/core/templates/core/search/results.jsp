<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/search/constraintBuilder.jsp"%>
<mm:import externid="search_query"/>
<% if ("".equals(request.getParameter("search_component")) || "core".equals(request.getParameter("search_component"))) { %>

    <%-- classes, news, educations, providers (de inhoud van de opleiding) --%>

    <%-- search classes --%>
    <%-- disabled for now, there is no clear resulting page anyway --%>
    <%--
	    <mm:list path="classes" constraints="<%= searchConstraints("classes.name", request) %>">
	    <tr>
		<td class="listItem"><di:translate id="classes">Klas</di:translate></td>
		<td class="listItem"><mm:field name="classes.name"/></td>
	    </tr>
	    </mm:list>
    --%>

    <%-- search news --%>
	    <mm:list path="news" constraints="<%= searchConstraints("CONCAT(news.title, news.intro, news.body)", request) %>">
	    <tr>
		<td class="listItem"><di:translate id="classes">Nieuws</di:translate></td>
		<td class="listItem"><a href="<mm:treefile page="/news.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="newsid"><mm:field name="news.number"/></mm:param>
  </mm:treefile>"><mm:field name="news.title"/></a></td>
	    </tr>
	    </mm:list>

    <%-- search educations --%>
    <%-- disabled for now, there is no clear resulting page anyway --%>
    <%-- <mm:list path="educations" constraints="<%= searchConstraints("CONCAT(educations.name, educations.intro)",request) %>">
	    <tr>
		<td class="listItem"><di:translate id="educations">Opleidingen</di:translate></td>
		<td class="listItem"><mm:field name="educations.name"/></td>
	    </tr>
	    </mm:list> 
    --%>
    
    <%-- search providers --%>
    <%-- disabled for now, there is no clear resulting page anyway --%>
    <%--
    <mm:list path="providers" constraints="<%= searchConstraints("providers.name",request) %>">
	    <tr>
		<td class="listItem"><di:translate id="providers">Aanbieders</di:translate></td>
		<td class="listItem"><mm:field name="providers.name"/></td>
	    </tr>
	    </mm:list>
    --%>
      
<% } %>
</mm:cloud>
</mm:content>
