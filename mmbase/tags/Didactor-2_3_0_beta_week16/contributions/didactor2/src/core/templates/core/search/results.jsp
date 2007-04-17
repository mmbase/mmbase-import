<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
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
		<td class="listItem"><di:translate key="education.classes" /></td>
		<td class="listItem"><mm:field name="classes.name"/></td>
	    </tr>
	    </mm:list>
    --%>

    <%-- search news --%>
	    <mm:list path="news" constraints="<%= searchConstraints("CONCAT(news.title, news.intro, news.body)", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="education.classes" /></td>
		<td class="listItem"><a href="<mm:treefile page="/news.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="newsid"><mm:field name="news.number"/></mm:param>
  </mm:treefile>"><mm:field name="news.title"/></a></td>
	    </tr>
	    </mm:list>

    <%-- search educations --%>
    <%-- disabled for now, there is no clear resulting page anyway --%>
    <%-- <mm:list path="educations" constraints="<%= searchConstraints("CONCAT(educations.name, educations.intro)",request) %>">
	    <tr>
		<td class="listItem"><di:translate key="education.educations" /></td>
		<td class="listItem"><mm:field name="educations.name"/></td>
	    </tr>
	    </mm:list> 
    --%>
    
    <%-- search providers --%>
    <%-- disabled for now, there is no clear resulting page anyway --%>
    <%--
    <mm:list path="providers" constraints="<%= searchConstraints("providers.name",request) %>">
	    <tr>
		<td class="listItem"><di:translate key="core.providers" /></td>
		<td class="listItem"><mm:field name="providers.name"/></td>
	    </tr>
	    </mm:list>
    --%>
      
<% } %>
</mm:cloud>
</mm:content>
