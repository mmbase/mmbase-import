<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="search_query"/>
<mm:import externid="search_type"/>

<% if ("".equals(request.getParameter("search_component")) || "metadata".equals(request.getParameter("search_component"))) { %>

    <%-- classes, news, educations, providers (de inhoud van de opleiding) --%>
	<mm:list nodes="$user" path="people,classes,educations,learnblocks">
	    <mm:treeinclude page="/metadata/search/searchlearnblock.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="learnblock"><mm:field name="learnblocks.number"/></mm:param>
		 <mm:param name="search_query"><mm:write referid="search_query"/></mm:param>
		 <mm:param name="search_type"><mm:write referid="search_type"/></mm:param>
	    </mm:treeinclude>
	</mm:list>
    <% } %>
</mm:cloud>
</mm:content>
