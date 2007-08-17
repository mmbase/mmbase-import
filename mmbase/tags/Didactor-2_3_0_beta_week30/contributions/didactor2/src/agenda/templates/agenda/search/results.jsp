<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/search/constraintBuilder.jsp"%>
<mm:import externid="search_query"/>
<% if ("".equals(request.getParameter("search_component")) || "agenda".equals(request.getParameter("search_component"))) { %>

    <%-- agenda, agenda item --%>

	<%-- search agendas --%>
	    <mm:list path="people,agendas" constraints="<%= searchConstraints("agendas.name", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="agenda.calendar" /></td>
		<td class="listItem"><a href="<mm:treefile page="/agenda/index.jsp" objectlist="$includePath" referids="$referids"/>"><mm:field name="agendas.name"/></a></td>
	    </tr>
	    </mm:list>

	<%-- search agendas --%>
	    <mm:list path="people,classes,agendas" constraints="<%= searchConstraints("agendas.name", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="agenda.calendar" /></td>
		<td class="listItem"><a href="<mm:treefile page="/agenda/index.jsp" objectlist="$includePath" referids="$referids"/>"><mm:field name="agendas.name"/></a></td>
	    </tr>
	    </mm:list>

	<%-- search agendas --%>
	    <mm:list path="people,workgroups,agendas" constraints="<%= searchConstraints("agendas.name", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="agenda.calendar" /></td>
		<td class="listItem"><a href="<mm:treefile page="/agenda/index.jsp" objectlist="$includePath" referids="$referids"/>"><mm:field name="agendas.name"/></a></td>
	    </tr>
	    </mm:list>

	<%-- search agendaitems --%>
	    <mm:list path="people,agendas,items" constraints="<%= searchConstraints("CONCAT(items.title, items.body)", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="agenda.calendaritem" /></td>
		<td class="listItem"><a href="<mm:treefile page="/agenda/showagendaitem.jsp" objectlist="$includePath" referids="$referids"><mm:param name="currentitem"><mm:field name="items.number"/></mm:param></mm:treefile>"><mm:field name="agendas.name"/> &gt; <mm:field name="items.title"/></a></td>
	    </tr>
	    </mm:list>

	    
	<%-- search agendaitems --%>
	    <mm:list path="people,classes,agendas,items" constraints="<%= searchConstraints("CONCAT(items.title, items.body)", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="agenda.calendaritem" /></td>
		<td class="listItem"><a href="<mm:treefile page="/agenda/showagendaitem.jsp" objectlist="$includePath" referids="$referids"><mm:param name="currentitem"><mm:field name="items.number"/></mm:param></mm:treefile>"><mm:field name="agendas.name"/> &gt; <mm:field name="items.title"/></a></td>
	    </tr>
	    </mm:list>

	<%-- search agendaitems --%>
	    <mm:list path="people,workgroups,agendas,items" constraints="<%= searchConstraints("CONCAT(items.title, items.body)", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="agenda.calendaritem" /></td>
		<td class="listItem"><a href="<mm:treefile page="/agenda/showagendaitem.jsp" objectlist="$includePath" referids="$referids"><mm:param name="currentitem"><mm:field name="items.number"/></mm:param></mm:treefile>"><mm:field name="agendas.name"/> &gt; <mm:field name="items.title"/></a></td>
	    </tr>
	    </mm:list>

<% } %>
</mm:cloud>
</mm:content>
