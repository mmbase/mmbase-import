<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=iso8859-1" %>
<mm:cloud>
<mm:import externid="project" />
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>
<td class="white" colspan="2" valign="top">
<mm:node referid="project">
  <h1><mm:field name="title" /></h1>
  <table width="100%">
    <tr>
      <td rowspan=2 width="60%" class="projectIntro">
        <mm:field name="body" />
        <p>
        
      </td>
      <td class="projectMembers">
	  <%@include file="/includes/persons.jsp" %>
      </td>
    </tr>
    <tr>
      <td class="projectDownloads">
	  <%@include file="/includes/attachment.jsp" %>
	  <%@include file="/includes/urls.jsp" %>
	  <%@include file="/includes/documentation.jsp" %>
      </td>
    </tr>
  </table>
</mm:node>
</td>

<%@include file="/includes/footer.jsp" %>
</mm:cloud>
