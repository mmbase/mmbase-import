<%@ include file="page_base.jsp"
%><mm:cloud sessionname="${SESSION}" jspvar="cloud">
<mm:writer referid="style" />
<title>About generic mmbase taglib editors</title>
</head>
<body class="basic">
  <table summary="taglib editors" width="100%" cellspacing="1" cellpadding="10" border="0">
    	<tr>
    	    <th>The MMBase taglib editors</th>
         </tr>
		 <tr>
		   <td class="data">
<p>
These are the <a href="http://www.mmbase.org/" target="_new">MMBase</a> generic editors, based on 
<a href="http://www.mmbase.org/mmbasenew/index3.shtml?development+452+3747+projects" target="_new">
MMCI</a> (version 1.0) with usage of 
<a href="http://www.mmbase.org/mmbasenew/index3.shtml?development+452+6176+projects" target="_new">
Taglibs</a> (version 1.0), created by 
<a href="http://www.mmbase.org/mmbasenew/index3.shtml?about+547+427+organization" target="_new">NOS Internet</a> under the 
<a href="http://www.mmbase.org/mmbasenew/index3.shtml?about+541+3649+documentation" target="_new">Mozilla License 1.0</a>
</p>
<p>
  version of the editors: 2001-11-05
</p>
<p>
  These editors were tested with application servers orion 1.4.5, orion 1.5.2 and tomcat 4.0.
</p>
<p>
  The tested browsers are Mozilla 0.9, Netscape 4.7, Opera 5 (all ok)
  and lynx 2.8.4 (not ok because of HttpPost), in Linux. Internet
  Explorer 5.5 was tested on a Windows 98 computer. You are using <%=
  request.getHeader("user-agent") %> 
</p>
<p>
  Features:
</p>
  <ul>
    <li>Generic editing of MMBase content, using MMBase taglib and little JSP.</li>
	<li>Relations (with directionality).</li>
	<li>Image upload.</li>
	<li>Aliases.</li>
	<li>Searching with search fields and on alias.</li>
  </ul>
<p>
  Known bugs:
</p>
  <ul>
    <li>Uni-directional relations are not very well presented.</li>
	<li>Bugs in HttpPost hinder good working in lynx (and in combination Opera/Tomcat?)</li>
	<li>Cannot search on age like in scan-editors (but is this necessary?)</li>
	<li>There is no last page when presenting search results. We wait for a satisfactory taglib solution for this.</li>
  </ul>
		   </td>
         </tr>
 </table>
<%@ include file="foot.jsp"  %>
</mm:cloud>