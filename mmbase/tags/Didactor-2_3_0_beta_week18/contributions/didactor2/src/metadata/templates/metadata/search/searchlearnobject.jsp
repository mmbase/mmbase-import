<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@page import="java.util.StringTokenizer"%>

<%-- search classes --%>
<mm:import id="learnobject"><%= request.getParameter("learnobject") %></mm:import>
<mm:import id="level" jspvar="level" vartype="Integer"><%= request.getParameter("level") %></mm:import>

<%
   String query = request.getParameter("search_query").toUpperCase();
    String qtype = request.getParameter("search_type");
%>
  


<% if (level.intValue() < 10) { %>
<mm:list nodes="$learnobject" path="learnobjects,metadata,metavalue">

<mm:import jspvar="metaValue" reset="true" vartype="String"><mm:field name="metavalue.value"/></mm:import>
<%

metaValue=metaValue.toUpperCase();
try {
//    System.err.println("matching '"+metaValue+"' against '"+query+"' (learnobject="+ request.getParameter("learnobject")+")");
    boolean hit = false;
    if ("exact".equals(qtype)) {
	hit = metaValue.indexOf(query) > -1;
    }
    else {
	StringTokenizer st = new StringTokenizer(query);
	if ("all".equals(qtype)) {
	    hit = true;
	    while (st.hasMoreTokens()) {
		if (metaValue.indexOf(st.nextToken()) < 0) {
		    hit = false;
		    break;
		}
	    }
	}
	else {
	    while (st.hasMoreTokens()) {
		if (metaValue.indexOf(st.nextToken()) > -1) {
		    hit = true;
		    break;
		}
	    }
	}
    }
    if (hit) {
	%>
	    <tr>
		<td class="listItem"><di:translate key="education.learnobject" /></td>
		<td class="listItem">
		    <mm:node number="$learnobject">
			<mm:import id="learnobjecttype" reset="true"><mm:nodeinfo type="type"/></mm:import>
		        <a href="<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
			<mm:param name="learnobject"><mm:field name="number"/></mm:param>
			<mm:param name="learnobjecttype"><mm:write referid="learnobjecttype"/></mm:param>
                        <mm:param name="class"><mm:write referid="class"/></mm:param>
                        <mm:param name="education"><mm:write referid="education"/></mm:param>
			</mm:treefile>">
		        <mm:field name="name"><mm:isempty><mm:field name="title"/></mm:isempty><mm:isnotempty><mm:write/></mm:isnotempty></mm:field></a>
		    </mm:node>
		    </td>
	    </tr>
<%       
    } 
}
catch (Exception e) {
    System.err.println(e);
}

%>
</mm:list>

<mm:list nodes="$learnobject" path="learnobjects1,learnobjects2" searchdir="destination">
    <mm:include page="searchlearnobject.jsp">
        <mm:param name="learnobject"><mm:field name="learnobjects2.number"/></mm:param>
        <mm:param name="search_query"><%= query %></mm:param>
        <mm:param name="search_type"><%= qtype %></mm:param>
        <mm:param name="level"><%= level.intValue()+1 %></mm:param>
         <mm:param name="education"><mm:write referid="education"/></mm:param>
       <mm:param name="class"><mm:write referid="class"/></mm:param>
    </mm:include>
</mm:list>      

<% } %>

</mm:cloud>
</mm:content>

