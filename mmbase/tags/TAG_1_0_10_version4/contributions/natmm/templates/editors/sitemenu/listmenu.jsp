<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">
<mm:import externid="location" />
<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/list.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/list.css" type="text/css" rel="stylesheet"/>
   <style>
    tr.itemrow { cursor: default; }
   </style>
   <link rel="stylesheet" type="text/css" href="style.css">

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

    function callEdit(linknumber) {
         document.callEditForm.objectnumber.value=linknumber;
         document.callEditForm.submit();
      }
   </script>
</head>
<mm:import externid="sitemenunumber"/>
<mm:present referid="sitemenunumber">
    <mm:deletenode referid="sitemenunumber" deleterelations="true"/>
</mm:present>
<body>

<table class="body">
   <tr class="itemrow">
      <td>
         <mm:compare referid="location" value="sitemenu" ><h1>Overzicht menuitems</h1></mm:compare>
         <mm:compare referid="location" value="tab" ><h1>Overzicht tabbladen</h1></mm:compare>
      </td>
   </tr>
   <tr>
      <td>
         <table class="listcontent">
              <tr class="listheader">
                 <th>&nbsp;</th>
             <th>&nbsp;Naam&nbsp;</th>
          </tr>
         <mm:listnodes type="sitemenu" orderby="naam" constraints="location='$location'">
            <mm:field name="identifier">
            <mm:compare value="Mijn LEOCMS" inverse="true">
            <tr class="itemrow" onMouseOver="objMouseOver(this);" onMouseOut="objMouseOut(this);"
                onClick="javascript:callEdit(<mm:field name="number"/>);">

               <td valign="top" width="1%" nowrap>
                  <a href="<mm:url><mm:param name="sitemenunumber"><mm:field name="number"/></mm:param><mm:param name="location"><mm:write referid="location"/></mm:param></mm:url>"
                        onclick="javascript: return confirm('Weet u zeker dat u dit menuitem wilt verwijderen?');">
                     <img src="../img/remove.gif" border="0" title="Verwijder item"/>
                  </a>
                  &nbsp;
                  <img src="../img/page.gif" alt="Item aanpassen" class="button">
               </td>
               <td valign="top" nowrap>&nbsp;<mm:field name="identifier"/>&nbsp;</td>
            </tr>
          </mm:compare>
          </mm:field>
         </mm:listnodes>
         </table>
      </td>
   </tr>
</table>
</mm:cloud>

<form action="edititem.jsp" method="post" name="callEditForm">
     <input type="hidden" name="objectnumber"/>
</form>

</body>
</html>