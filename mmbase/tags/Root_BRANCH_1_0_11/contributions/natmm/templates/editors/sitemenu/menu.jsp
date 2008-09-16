<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">
<mm:import externid="location" required="true" />
<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/list.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/list.css" type="text/css" rel="stylesheet"/>
   <link href="../css/menustyle.css" type="text/css" rel="stylesheet"/>
</head>
<body>
<table cellspacing="2" cellpadding="2" border="0">
   <tr>
      <mm:compare referid="location" value="sitemenu">
         <td class="menuitem">
            <a href="select.jsp?location=sitemenu" target="editscreen">Selecteer menu</a>&nbsp;&nbsp;
         </td>
         <td class="menuitem">
            <a target="editscreen" href="listmenu.jsp?location=sitemenu">Menuitems</a>
         </td>
         <td class="menuitem">
            <a target="editscreen" href="edititem.jsp?objectnumber=new&amp;location=sitemenu">Nieuw menuitem</a>
         </td>
      </mm:compare>
      <mm:compare referid="location" value="tab" >
         <td class="menuitem">
            <a href="select.jsp?location=tab" target="editscreen">Selecteer menu</a>&nbsp;&nbsp;
         </td>
         <td class="menuitem">
            <a target="editscreen" href="listmenu.jsp?location=tab">Tabbladen</a>
         </td>
         <td class="menuitem">
            <a target="editscreen" href="edititem.jsp?objectnumber=new&amp;location=tab">Nieuw tabblad</a>
         </td>
      </mm:compare>
   </tr>
</table>

</body>
</html>

</mm:cloud>