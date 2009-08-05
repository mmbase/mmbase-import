<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@page import="java.util.StringTokenizer"%>
<mm:import externid="search_query"/>
<mm:import externid="search_type"/>

<%-- search classes --%>
<mm:import externid="learnblock"/>
<mm:list nodes="$learnblock" path="learnblocks,learnobjects,metadata,metavalue">
<mm:import jspvar="metaValue" reset="true" vartype="String"><mm:field name="metavalue.value"/></mm:import>
<%
try {
    metaValue=metaValue.toUpperCase();
    String query = request.getParameter("search_query").toUpperCase();
    String qtype = request.getParameter("search_type");

    System.err.println("matching '"+metaValue+"' against '"+query+"'");
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
		    <mm:import id="learnobjectnumber" reset="true"><mm:field name="learnobjects.number"/></mm:import>
		    <mm:node number="$learnobjectnumber">
			<mm:import id="learnobjecttype" reset="true"><mm:nodeinfo type="type"/></mm:import>
		    </mm:node>
		    <a href="<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
			<mm:param name="learnobject"><mm:field name="learnobjects.number"/></mm:param>
			<mm:param name="learnobjecttype"><mm:write referid="learnobjecttype"/></mm:param>
			</mm:treefile>">
		    <mm:field name="learnobjects.name"><mm:isempty><mm:field name="learnobjects.title"/></mm:isempty><mm:isnotempty><mm:write/></mm:isnotempty></mm:field></a>
		    </td>
	    </tr>
<%   } 
}
catch (Exception e) {
    System.err.println(e);
}

%>
</mm:list>
</mm:cloud>
</mm:content>

