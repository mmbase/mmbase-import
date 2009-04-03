<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/search/constraintBuilder.jsp"%>
<mm:import externid="search_query"/>
<% if ("".equals(request.getParameter("search_component")) || "address".equals(request.getParameter("search_component"))) { %>

    <%-- search addressbooks --%>
	    <mm:list nodes="$user" path="people,addressbooks,contacts" constraints="<%= searchConstraints("CONCAT(contacts.firstname,contacts.lastname, contacts.email)", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="address.contact" /></td>
		<td class="listItem">
		<a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids">
		    <mm:param name="to"><mm:field name="contacts.email"/></mm:param>
		</mm:treefile>">
		<mm:field name="contacts.firstname"/> <mm:field name="contacts.lastname"/>
		</a>
		</td>
	    </tr>
	    </mm:list>

    <%-- search classes --%>
	    <mm:list nodes="$user" path="people1,classes,people2" constraints="<%= "(" + searchConstraints("CONCAT(people2.firstname,people2.lastname, people2.email)", request) + " ) AND people1.number != people2.number" %>" fields="people2.number" distinct="true">
	    <tr>
		<td class="listItem"><di:translate key="address.contact" /></td>
		<td class="listItem">
		<a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids">
                <mm:param name="to"><mm:field name="people2.email"/></mm:param>
		</mm:treefile>">
		<mm:field name="people2.firstname"/> <mm:field name="people2.lastname"/>
		</a>
		</td>
	    </tr>
	    </mm:list>

	    
<% } %>
</mm:cloud>
</mm:content>
