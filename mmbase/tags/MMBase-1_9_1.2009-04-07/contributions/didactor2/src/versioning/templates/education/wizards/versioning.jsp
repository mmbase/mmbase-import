<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.Date" %>


<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>


<html>
<head>
<title>File manager</title>
   <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" />' />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/mmbase/edit/wizard/style/layout/list.css" objectlist="$includePath"  />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/mmbase/edit/wizard/style/color/list.css" objectlist="$includePath" />" />
</head>
<script type="text/javascript" src="<mm:treefile page="/mmbase/edit/wizard/javascript/list.js" objectlist="$includePath" /></script>
<body>

<mm:import externid="nodeid" jspvar="nodeId" vartype="String">-1</mm:import>
<mm:node number="<%= nodeId %>" notfound="skip">
   <table width="100%" border="0">
      <tr bgcolor="#CCCCCC">
         <td>
            <di:translate key="versioning.versionmanagement" />: <mm:nodeinfo type="guitype" />
            <% boolean nameShowed = false; %>
            <mm:fieldlist fields="name?">
               <mm:field/>
               <% nameShowed = true; %>
            </mm:fieldlist>
            <% if (!nameShowed) { %>
               <mm:fieldlist fields="title">
                  <mm:field/>
               </mm:fieldlist>
            <% } %>
         </td>
      </tr>
   </table>
<di:translate key="versioning.versioningselectversiontorestore" /><br />
<b><di:translate key="versioning.versioningnote" />:</b> <di:translate key="versioning.versioningnotetext" /><br />
<table class="body">
   <tr class="listcanvas">
      <td>
         <table class="listcontent">
            <tr class="listheader">
               <th>&nbsp;</th>
               <th>#</th>
               <th><di:translate key="versioning.date" /></th>
               <th><di:translate key="versioning.created_by" /></th>
            </tr>
            <% int archiveNum = 0; %>
            <mm:listnodes type="archives" orderby="number" directions="DOWN" constraints="<%= "original_node = '" + nodeId + "'" %>">
               <% archiveNum++; %>
               <tr>
                  <td class="deletebutton">
                     <a
                        href='vers_cmd.jsp?nodeid=<%= nodeId %>&command=delete&archiveid=<mm:field name="number"/>'
                        onclick="return doDelete('<di:translate key="versioning.versioningdeletewarning" />');">
                           <img border="0" src="<%= request.getContextPath() %>/mmbase/edit/wizard/media/remove.gif"/>
                     </a>
                  </td>

                  <td class="field">
                     <%= archiveNum %>
                  </td>
                  <td class="field">
                     <mm:field name="archive_date" jspvar="date" vartype="Long">
                        <% Date archiveDate = new Date(date.longValue()*1000); %>
                        <% SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm"); %>
                     <a
                        href='vers_cmd.jsp?nodeid=<%= nodeId %>&command=restore&archiveid=<mm:field name="number"/>'
                        onclick="return doDelete('<di:translate key="versioning.versioningrestorewarning" />');">
                           <%= df.format(archiveDate) %>
                     </a>
                     </mm:field>
                  </td>
                  <td class="field">
                     <a
                        href='vers_cmd.jsp?nodeid=<%= nodeId %>&command=restore&archiveid=<mm:field name="number"/>'
                        onclick="return doDelete('<di:translate key="versioning.versioningrestorewarning" />');">
                           <mm:field name="archived_by" jspvar="userName" vartype="String">
                              <mm:listnodes type="people" constraints="<%= "username = '" + userName + "'" %>">
                                 <mm:field name="firstname"/> <mm:field name="suffix"/> <mm:field name="lastname"/>
                              </mm:listnodes>
                           </mm:field>
                     </a>
                  </td>
               </tr>
            </mm:listnodes>
         </table>
      </td>
   </tr>
</table>

</mm:node>

</body>
</html>

</mm:cloud>
</mm:content>
