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
        <mm:field name="intro" />
        <p>
        
      </td>
      <td class="projectMembers">
        Project members:
        <mm:relatednodes type="persons">
          <mm:first>
            <ul>
          </mm:first>
          <li><a href="/development/people/person.jsp?person=<mm:field name="number" />"><mm:field name="firstname" />&nbsp;<mm:field name="lastname" /></a> </li>
          <mm:last>
            </ul>
          </mm:last>
        </mm:relatednodes>
      </td>
    </tr>
    <tr>
      <td class="projectDownloads">
        <mm:relatednodes type="attachments">
          <mm:first>Related downloads:
            <ul>
          </mm:first>
          <li><a href="<mm:attachment />"><mm:field name="title" /></a></li>
          <mm:last>
            </ul>
          </mm:last>
        </mm:relatednodes>
      </td>
    </tr>
  </table>
</mm:node>
</td>

<%@include file="/includes/footer.jsp" %>
</mm:cloud>
