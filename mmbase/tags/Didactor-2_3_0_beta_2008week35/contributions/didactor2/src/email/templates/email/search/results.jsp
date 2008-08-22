<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/search/constraintBuilder.jsp"%>
<mm:import externid="search_query"/>
<% if ("".equals(request.getParameter("search_component")) || "email".equals(request.getParameter("search_component"))) { %>

    <%-- mailboxes en mails (ook attachments?)  --%>

    <%-- search mailboxes --%>
	    <mm:list path="people,mailboxes" nodes="$user" constraints="<%= searchConstraints("mailboxes.name", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="email.mailbox" /></td>
		<td class="listItem"><a href="<mm:treefile page="/email/index.jsp" objectlist="$includePath" referids="$referids">
		<mm:param name="mailbox"><mm:field name="mailboxes.number"/></mm:param>
		</mm:treefile>"><mm:field name="mailboxes.name"/></a></td>
	    </tr>
	    </mm:list>

    <%-- search email --%>
	    <mm:list path="people,mailboxes,emails" nodes="$user" constraints="<%= searchConstraints("CONCAT(emails.m_from, emails.m_to, emails.cc, emails.subject, emails.body )", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="email.email" /></td>
		<td class="listItem"><a href="<mm:treefile page="/email/mailbox/email.jsp" objectlist="$includePath" referids="$referids">
		<mm:param name="mailbox"><mm:field name="mailboxes.number"/></mm:param>
		<mm:param name="email"><mm:field name="emails.number"/></mm:param>
		</mm:treefile>"><mm:field name="emails.from"/>: "<mm:field name="emails.subject"/>"</a></td>
	    </tr>
	    </mm:list>

    <%-- search attachments --%>
	    <mm:list path="people,mailboxes,emails,attachments" constraints="<%= searchConstraints("CONCAT(attachments.title, attachments.description, attachments.filename)",request) %>">
	    <tr>
		<td class="listItem"><di:translate key="email.emailattachment" /></td>
		<td class="listItem"><a href="<mm:treefile page="/email/mailbox/email.jsp" objectlist="$includePath" referids="$referids">
		<mm:param name="mailbox"><mm:field name="mailboxes.number"/></mm:param>
		<mm:param name="email"><mm:field name="emails.number"/></mm:param>
		</mm:treefile>"><mm:field name="emails.from"/>: "<mm:field name="emails.subject"/>"</a></td>
	    </tr>
	    </mm:list>
    
      
<% } %>
</mm:cloud>
</mm:content>
