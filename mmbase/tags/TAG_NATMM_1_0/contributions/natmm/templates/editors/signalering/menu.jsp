<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">
<html>
<head>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="../css/menustyle.css" type="text/css" rel="stylesheet"/>
 </head>

<body>

<table cellspacing="2" cellpadding="2" border="0">
   <tr>
      <td class="menuitem">
         <a href="overzicht.jsp" target="editscreen">Overzicht taken</a>&nbsp;&nbsp;
      </td>
      <td class="menuitem">
         <a href="nieuw.jsp" target="editscreen">Nieuwe taak</a>&nbsp;&nbsp;
      </td>
      <td class="menuitem">
         <a href="search.jsp" target="editscreen">Zoek taak</a>
      </td>     
   </tr>
</table>

</body>
</html>

</mm:cloud>