<%@page import="org.mmbase.bridge.*,nl.leocms.util.*,java.util.ArrayList" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">
<html>
<head>    
  <title>Contentpages for content types edit screen</title>
  <script language="JavaScript1.2" type="text/javascript">
    function openWindow() {
      window.open("","createwindow","width=1,height=1,left=0,top=0,scrollbars=no,status=no,toolbar=no,menubar=no,location=no,resizable=no");
    }
  </script>
  <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
  <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>

<body>

<h2>Content page for content types</h2>
  
<table cellpadding="3" cellspacing="3" border="0" width="500">
   <tr>
      <th align="left"><b>Content type</b></th>
      <th align="left"><b>Content pagina</b></th>
      <td>&nbsp;</th>
   </tr>    
   
   <%
      ArrayList contentTypeList = ApplicationHelper.getContentTypes(false);
      for (int i = 0; i < contentTypeList.size(); i++) {
         String contentType = (String) contentTypeList.get(i);
         Node paginaNode = null;
         String currentNodeNumber = "-1";
         try {
            paginaNode = cloud.getNodeByAlias("contentpagina." + contentType);
            currentNodeNumber = String.valueOf(paginaNode.getNumber());
         }
         catch (NotFoundException nfe) {
            // just skip
         }
   %>   
      <form name="contentpage<%= contentType %>" action="create_relation.jsp" target="createwindow">
         <input type="hidden" name="refreshpage" value="content_pages.jsp"/>
         <input type="hidden" name="currentnodenumber" value="<%= currentNodeNumber %>"/>
         <input type="hidden" name="alias" value="<%= "contentpagina." + contentType %>"/>
         <tr>
            <td><%= contentType %></td>
            <td>
               <select name="nodenumber">
                  <option value="-1">-</option>
                  <mm:listnodes type="pagina" constraints="contentpagina = 1">
                     <mm:field name="number" jspvar="pageNodeNumber" vartype="String">
                        <option value="<%= pageNodeNumber %>" <%= (pageNodeNumber.equals(currentNodeNumber)) ? "selected" : "" %>><mm:field name="titel"/></option>
                     </mm:field>
                  </mm:listnodes>
               </select>
            </td>
            <td align="right" colspan="2"><input type="button" value="Wijzig" onclick="openWindow(); document.contentpage<%= contentType %>.submit()"/></td>
         </tr>      
      </form>
   <%
      }
   %>
</table>

</mm:cloud>

</body>
</html>