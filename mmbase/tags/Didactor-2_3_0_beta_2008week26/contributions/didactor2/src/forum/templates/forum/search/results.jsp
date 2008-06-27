<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/search/constraintBuilder.jsp"%>
<mm:import externid="search_query"/>
<% if ("".equals(request.getParameter("search_component")) || "forum".equals(request.getParameter("search_component"))) { %>

    <%-- forums, threads, messages --%>

    <%-- search forums --%>
	    <mm:list nodes="$user" path="people,classes,forums" constraints="<%= searchConstraints("forums.name", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="forum.forum" /></td>
		<td class="listItem">
		<a href="<mm:treefile page="/forum/forum.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="class"><mm:field name="classes.number"/></mm:param>
		    <mm:param name="forum"><mm:field name="forums.number"/></mm:param>
		    </mm:treefile>"><mm:field name="forums.name"/></a>
		</td>
	    </tr>
	    </mm:list>
	    
	<%-- search threads --%>
	    <mm:list nodes="$user" path="people,classes,forums,forumthreads" constraints="<%= searchConstraints("forumthreads.name", request) %>">
		<tr>
		    <td class="listItem"><di:translate key="forum.thread" /></td>
		    <td class="listItem">
		    <a href="<mm:treefile page="/forum/thread.jsp" objectlist="$includePath" referids="$referids">
	             <mm:param name="class"><mm:field name="classes.number"/></mm:param>
	            <mm:param name="forum"><mm:field name="forums.number"/></mm:param>
		    <mm:param name="thread"><mm:field name="forumthreads.number"/></mm:param>
		    </mm:treefile>"><mm:field name="forums.name"/> &gt; <mm:field name="forumthreads.name"/></a></td>
		</tr>
	    </mm:list>

	<%-- search messages --%>
	    <mm:list nodes="$user" path="people,classes,forums,forumthreads,forummessages" constraints="<%= searchConstraints("CONCAT(forummessages.title, forummessages.body)", request) %>">
	    <tr>
		<td class="listItem"><di:translate key="forum.message" /></td>
		<td class="listItem"><a href="<mm:treefile page="/forum/thread.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="class"><mm:field name="classes.number"/></mm:param>
		    <mm:param name="forum"><mm:field name="forums.number"/></mm:param>
		    <mm:param name="thread"><mm:field name="forumthreads.number"/></mm:param>
		    </mm:treefile>"><mm:field name="forums.name"/> &gt; <mm:field name="forumthreads.name"/> &gt;  <mm:field name="forummessages.title"/></a></td>
	    </tr>
	    </mm:list>

 
      
<% } %>
</mm:cloud>
</mm:content>
