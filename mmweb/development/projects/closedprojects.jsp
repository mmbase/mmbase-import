<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=iso8859-1" %>
<mm:cloud>
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>
<td class="white" colspan="2" valign="top">
    <h1>Archived Projects</h1>
    <mm:listnodes type="project" constraints="m_status = 'finished'" orderby="number" directions="down">
        <h2><a href="/development/projects/project.jsp?project=<mm:field name="number" />&portal=<mm:write referid="portal" />"><mm:field name="title" /></a>
        (<mm:field name="status"/>)</h2>
        <mm:field name="intro" />
    </mm:listnodes>
   <br /><br />
</td>

<%@include file="/includes/footer.jsp" %>
</mm:cloud>
