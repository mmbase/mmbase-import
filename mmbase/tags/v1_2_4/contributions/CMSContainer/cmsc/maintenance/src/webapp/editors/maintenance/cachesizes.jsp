<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<%@page import="org.mmbase.util.LRUHashtable.LRUEntry" %>

<% int checkCount = 10; %>

<mm:cloud jspvar="cloud">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Cache Monitor</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link href="../style.css" type="text/css" rel="stylesheet"/>
</head>
<body class="basic" >
<!-- <%= cloud.getUser().getIdentifier()%>/<%=  cloud.getUser().getRank()%> -->
<%
try {
	if(request.getParameter("checkCount") != null) {
		checkCount = Integer.parseInt(request.getParameter("checkCount"));
	}
}
catch(Exception e) {
	%><b>The checkCount parameter should be a valid number!</b><p><%
}
%>

<b>Note!</b><br/>
All values in this table are guestimated!<br/>
Only the size of <%=checkCount%> random nodes will be checked and averaged!<br/>
(use the ?checkCount=... parameter to alter this)
<br>

<table>

<mm:import externid="active" from="parameters" />

<mm:present referid="active">
  <mm:import externid="cache" from="parameters" required="true" />
  <mm:write referid="active" jspvar="active" vartype="String">
  <mm:write referid="cache" jspvar="cache" vartype="String">
  <%
    // have to test if this works...
    org.mmbase.cache.Cache.getCache(cache).setActive(active.equals("on") ? true : false);
  %>
  </mm:write></mm:write>
</mm:present>


<tr>
	<th width="300">Cache</th>
	<td width="100" align="right"><b>Now in use</td>
	<td width="100" align="right"><b>Total when<br>cache is full</td>
</tr>
<%
   long totalBytes = 0;
   long totalFullBytes = 0;
   java.util.Iterator i = org.mmbase.cache.Cache.getCaches().iterator();
   while (i.hasNext()) {
      org.mmbase.cache.Cache cache = org.mmbase.cache.Cache.getCache((String) i.next());
%>

<tr align="left">
  <td class="header"><%= cache.getDescription() %></td>
<%    
	  long bytes = 0;
	  long fullBytes = 0;
      java.util.ArrayList values = new java.util.ArrayList(cache.values());
      java.util.ArrayList checkValues = new java.util.ArrayList();
      
      if(values.size() > 0) {
	      while(checkValues.size() < checkCount) {
	      	checkValues.add(values.get((int)(java.lang.Math.random()*values.size())));
		  }
      	
	      for(java.util.Iterator it = checkValues.iterator(); it.hasNext(); ) {
			 bytes += ((LRUEntry)it.next()).getByteSize();
	      }
	      
	      bytes = bytes * values.size() / checkCount;
      
      
      
	      for(java.util.Iterator it = cache.keySet().iterator(); it.hasNext(); ) {
	         bytes += it.next().toString().length()*2;
	      }
	      
	      fullBytes = bytes * cache.getSize() / values.size();
	      
	      totalBytes += bytes;
	      totalFullBytes += fullBytes;
       }
      %>
  <td class="data" align="right">
	<%=bytes/1024%> kb
  </td>
  <td class="data" align="right">
	<%=fullBytes/1024%> kb
  </td>
</tr>

<% }

 Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");

%>
  <tr>
    <th>Total</th>
    <td class="data" align="right">
  	  <b><%=totalBytes/1024%> kb</b>
    </td>
    <td class="data" align="right">
	  <b><%=totalFullBytes/1024%> kb</b>
    </td>
  </tr>
</table>


</body></html>
</mm:cloud>
