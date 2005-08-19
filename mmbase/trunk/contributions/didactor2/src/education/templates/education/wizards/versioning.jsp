<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.Date" %>


<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<html>
<head>
<title>File manager</title>
   <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/layout/list.css" objectlist="$includePath" referids="$referids" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/color/list.css" objectlist="$includePath" referids="$referids" />" />
</head>
<script type="text/javascript" src="<mm:treefile page="/editwizards/javascript/list.js" objectlist="$includePath" referids="$referids"/>"></script>
<body>

<mm:import externid="nodeid" jspvar="nodeId" vartype="String">-1</mm:import>
<mm:node number="<%= nodeId %>" notfound="skip">
<table class="body">
   <tr class="listcanvas">
      <td>
         <table class="listcontent">
            <tr class="listheader">
               <th>&nbsp;</th>
               <th>#</th>
               <th>Datum</th>
               <th>Created by</th>
            </tr>
            <% int archiveNum = 0; %>
            <mm:listnodes type="archives" orderby="number" directions="DOWN" constraints="<%= "original_node = '" + nodeId + "'" %>">
               <% archiveNum++; %>
               <tr>
                  <td class="deletebutton">
                     <a 
                        href='vers_cmd.jsp?nodeid=<%= nodeId %>&command=delete&archiveid=<mm:field name="number"/>'
                        onclick="return doDelete('delete?');">
                           <img border="0" src="<%= request.getContextPath() %>/editwizards/media/remove.gif"/>
                     </a>
                  </td>

                  <td class="field">
                     <%= archiveNum %>
                  </td>
                  <td class="field">
                     <mm:field name="archive_date" jspvar="date" vartype="Long">
                        <%= date %>ffff
                        <% Date archiveDate = new Date(date.longValue()*1000); %>
                        <% SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm"); %>
                     <a
                        href='vers_cmd.jsp?nodeid=<%= nodeId %>&command=restore&archiveid=<mm:field name="number"/>'
                        onclick="return doDelete('restore?');">
                           <%= df.format(archiveDate) %>
                     </a>
                     </mm:field>
                  </td>
                  <td class="field">
                     <a
                        href='vers_cmd.jsp?nodeid=<%= nodeId %>&command=restore&archiveid=<mm:field name="number"/>'
                        onclick="return doDelete('restore?');">
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
