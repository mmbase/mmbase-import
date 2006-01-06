<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.mmbase.bridge.*,java.util.*" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<% response.setContentType("text/html; charset=utf-8"); %>
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>

<link rel="stylesheet" href="<%= request.getContextPath() %>/mmbase/my_editors/my_editors.css" type="text/css" />

<td>
<div style="margin-left: 50px; margin-right: 50px; margin-top: 50px; margin-bottom: 50px;" align="center">
<mm:import externid="username" from="parameters" />
<mm:import externid="referrer"><%= request.getContextPath() %>/dochangeaccount.jsp</mm:import>
<mm:import externid="reason" />
<mm:import externid="message" />
<mm:write referid="reason">
  <mm:isnotempty>
     <p class="message"><mm:write /></p>
  </mm:isnotempty>
</mm:write>

<form method="post" action="<mm:url page="$referrer" />">
<table border="0" cellspacing="0" cellpadding="4" class="table-left">
  <tr>
	<td width="50"><img src="<mm:url page="/mmbase/style/images/edit-32x32.png" />" alt="edit" width="32" height="32" border="0" hspace="4" vspace="4" /></td>
	<td>
	  <div class="top-title">mmbase.org</div>
	</td>
  </tr>
<mm:write referid="message">
  <mm:isnotempty>
    <tr><td>&nbsp;</td><td class="name"><b><mm:write /></b></td></tr>
  </mm:isnotempty>
</mm:write>

  <tr><td>&nbsp;</td><td class="name"><b>Change email</b></td></tr>
  <tr><td class="name" align="right">Email</td><td><input class="mmbase" type="text" name="new_email" /></td></tr>
  <tr><td>&nbsp;</td><td><input type="submit" name="change_email" value="change" /></td></tr>
  <tr><td>&nbsp;</td><td class="name"><b>Change password</b></td></tr>
  <tr><td class="name" align="right">Old password</td><td><input class="mmbase" type="password" name="passwordold" /></td></tr>
  <tr><td class="name" align="right">New password</td><td><input class="mmbase" type="password" name="passwordnew1" /></td></tr>
  <tr><td class="name" align="right">New password again</td><td><input class="mmbase" type="password" name="passwordnew2" /></td></tr>
  <tr><td>&nbsp;</td><td><input type="submit" name="change_password" value="change" /></td></tr>
</table>
</form>
</div>
</td>
<%@include file="/includes/footer.jsp"%>
</mm:cloud>
