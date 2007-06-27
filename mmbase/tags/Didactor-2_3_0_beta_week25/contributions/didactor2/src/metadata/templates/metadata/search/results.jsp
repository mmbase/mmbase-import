<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="search_query"/>
<mm:import externid="search_type"/>

<% if ("".equals(request.getParameter("search_component")) || "metadata".equals(request.getParameter("search_component"))) { %>

	<mm:list nodes="$user" path="people,classes,educations">
	    <mm:treeinclude page="/metadata/search/searcheducation.jsp" objectlist="$includePath" referids="$referids">
		 <mm:param name="search_query"><mm:write referid="search_query"/></mm:param>
	         <mm:param name="class"><mm:field name="classes.number"/></mm:param>
                 <mm:param name="education"><mm:field name="educations.number"/></mm:param>
	         <mm:param name="search_type"><mm:write referid="search_type"/></mm:param>
	    </mm:treeinclude>
        </mm:list>
        
    <% } %>
</mm:cloud>
</mm:content>
