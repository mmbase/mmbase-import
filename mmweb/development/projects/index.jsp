<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=iso8859-1" %>
<mm:cloud>
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>
<td class="white" colspan="2" valign="top">
  <mm:listnodes type="project" orderby="status,number" directions="down" id="projectlist">
    <mm:changed>
      [<a href="#<mm:field name="number" />"><mm:field name="status" /></a>]&nbsp;
    </mm:changed>
  </mm:listnodes>

  <mm:list referid="projectlist">
    <mm:changed>
      <h1><a name="<mm:field name="number" />"><mm:field name="status"/></a></h1>
    </mm:changed>
    <h2><a href="/development/projects/project.jsp?project=<mm:field name="number" />&portal=<mm:write referid="portal" />"><mm:field name="title" /></a></h2>
    <mm:field name="intro" />
  </mm:list>
   <br/><br/>
</td>

<%@include file="/includes/footer.jsp" %>
</mm:cloud>
