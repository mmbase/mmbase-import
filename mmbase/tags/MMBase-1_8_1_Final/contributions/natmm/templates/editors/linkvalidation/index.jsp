<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.linkvalidation.LinkValidationUtil" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">

<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/list.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/list.css" type="text/css" rel="stylesheet"/>
   <style>
    tr.itemrow { cursor: default; }
   </style>
   <script>
   
    function objMouseOver(el) {
        objMouseOver(el, false);
    }
    
    function objMouseOver(el, detail) {
        el.className="itemrow-hover";
        if (detail) {
            el.previousSibling.className="itemrow-hover";
        }
    }
   
    function objMouseOut(el) {
        objMouseOut(el, false);
     }
    
    function objMouseOut(el, detail) {
        el.className="itemrow";
        if (detail) {
            el.previousSibling.className="itemrow";
        }
    }
    
    function callEditWizard(linknumber) {
         document.callEditwizardForm.objectnumber.value=linknumber;
         document.callEditwizardForm.submit();
      }
   </script>
</head>
<mm:import externid="linknumber"/>
<mm:present referid="linknumber">
   <mm:write referid="linknumber" jspvar="linkNodeNumber" vartype="String" write="false">
      <%
         LinkValidationUtil.removeLink(linkNodeNumber);
      %>
   </mm:write>
</mm:present>

<body>

<table class="body">
   <tr class="itemrow">
      <td>
         <h1>Link Validatie</h1>
      </td>
   </tr>
   <tr>
      <td>
         <table class="listcontent">
              <tr class="listheader">
                 <th>&nbsp;</th>
               <th>&nbsp;Url&nbsp;</th>
               <th>&nbsp;Titel&nbsp;</th>
            </tr>
         <mm:listnodes type="link" constraints="[type]='extern' AND [valid]=0" orderby="titel">
            <tr class="itemrow" onMouseOver="objMouseOver(this);" onMouseOut="objMouseOut(this);">
               <td valign="top" width="1%" nowrap>
                  <a href="<mm:url><mm:param name="linknumber"><mm:field name="number"/></mm:param></mm:url>"
                        onclick="javascript: return confirm('Weet u zeker dat u deze link wilt verwijderen?');">
                     <img src="../img/remove.gif" border="0" title="Verwijder link"/>
                  </a>
                  &nbsp;
                  <img src="../img/page.gif" alt="Link aanpassen" class="button" onClick="javascript:callEditWizard(<mm:field name="number"/>);">
               </td>
               <td valign="top" nowrap>&nbsp;<a href="<mm:field name="url"/>" target="_blank"><mm:field name="url"/></a>&nbsp;</td>
               <td valign="top" nowrap>&nbsp;<mm:field name="titel"/>&nbsp;</td>
            </tr>
         </mm:listnodes>
         </table>
      </td>
   </tr>
</table>

</mm:cloud>

   <form action="../../editors/WizardInitAction.eb" method="post" name="callEditwizardForm">
      <input type="hidden" name="objectnumber"/>
      <input type="hidden" name="returnurl" value="/editors/linkvalidation/index.jsp">
   </form>

</body>
</html>
