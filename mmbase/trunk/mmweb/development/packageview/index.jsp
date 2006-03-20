<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<div id="pagecontent">
<mm:import externid="main">bundles</mm:import>
<!-- first the selection part -->
<table cellpadding="0" cellspacing="0" class="list" 
	style="margin-top: 30px; margin-bottom: 30px;" width="95%">
<tr>
 <th>MMBase Packages</th>
</tr><tr>
  <td>
  <h5>Temporary package overview</h5>
  This is a temporary package overview for the MMBase. In time it will be replaced 
  with the package manager from the apps2 project (not done yet).<br />
  As a result this list is maintained by hand. So if your package is not on this page 
  and you feel it should be or you have extra information on your package mail me 
  at <a href="mailto:daniel@mmbase.org">daniel@mmbase.org</a> and I will update this list.
  <h5>Preview</h5>
  At <a href="http://packages.mmbase.org/mmbase/packagemanager/public">packages.mmbase.org</a> 
  you can find a <a href="http://packages.mmbase.org/mmbase/packagemanager/public">preview of the 
  new packaging system (1.8)</a> with extra applications.
  </td>
</tr>
</table>
<%-- <mm:compare referid="main" value="bundles"><%@ include file="bundles.jsp" %></mm:compare>
<mm:compare referid="main" value="bundle"><%@ include file="bundle.jsp" %></mm:compare> --%>
<%@ include file="packages.jsp" %>
</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
