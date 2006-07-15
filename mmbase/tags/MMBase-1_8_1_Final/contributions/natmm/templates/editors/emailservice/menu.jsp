<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">
<html>
<head>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/list.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/list.css" type="text/css" rel="stylesheet"/>
   <link href="../css/menustyle.css" type="text/css" rel="stylesheet"/>
 </head>

<body>
<mm:import externid="language">nl</mm:import>
<mm:import id="referrer"><%=new java.io.File(request.getServletPath())%>?language=<mm:write  referid="language" /></mm:import>
<mm:import id="jsps"><%= editwizard_location %>/jsp/</mm:import>
<mm:import id="debug">false</mm:import>

<table cellspacing="2" cellpadding="2" border="0">
   <tr>
      <td class="menuitem">
         <a href="maillijst.jsp" target="editscreen">Maillijst</a>&nbsp;&nbsp;
      </td>
      <td class="menuitem">
         <a target="editscreen" href="<mm:url referids="referrer" page="${jsps}list.jsp">
             <mm:param name="wizard">../config/emailtemplate/emailtemplate</mm:param>
             <mm:param name="nodepath">emailtemplate</mm:param>
               <mm:param name="fields">onderwerp,active</mm:param>
             <mm:param name="pagelength">100</mm:param>
             <mm:param name="maxpagecount">50</mm:param>
              </mm:url>">
            Emailtemplates
          </a>
      </td>
   </tr>
</table>

</body>
</html>

</mm:cloud>