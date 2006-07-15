<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.emailservice.EmailServiceUtil" %>
<%@page import="nl.leocms.util.PropertiesUtil" %>
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
   </script>
</head>
<mm:import externid="artikelnumber"/>
<mm:present referid="artikelnumber">
   <mm:write referid="artikelnumber" jspvar="artikelNodeNumber" vartype="String" write="false">
      <%
         EmailServiceUtil.removeMailLijstItem(artikelNodeNumber);
      %>
   </mm:write>
</mm:present>

<body>

<table class="body">
   <tr class="itemrow">
      <td>
         <h1>Maillijst</h1>
      </td>
      <td align="right">
         <h1>verstuurdatum: <%= PropertiesUtil.getProperty("emailservice.datumtijd") %> uur</h1>
      </td>
   </tr>
   <tr>
      <td colspan="2">
         <table class="listcontent">
              <tr class="listheader">
                 <th>&nbsp;</th>
               <th>&nbsp;Artikel&nbsp;</th>
               <th>&nbsp;Embargo&nbsp;</th>
               <th align="left">&nbsp;Themas&nbsp;</th>
            </tr>
         <mm:listnodes type="artikel" constraints="[status]=1" orderby="titel">
            <tr class="itemrow" onMouseOver="objMouseOver(this);" onMouseOut="objMouseOut(this);">
               <td valign="top" width="1%" nowrap>
                  <a href="<mm:url><mm:param name="artikelnumber"><mm:field name="number"/></mm:param></mm:url>"
                        onclick="javascript: return confirm('Weet u zeker dat u dit artikel uit de mailijst wilt verwijderen?');">
                     <img src="../img/remove.gif" border="0" title="Verwijder artikel uit de maillijst"/>
                  </a>
               </td>
               <td valign="top" nowrap>&nbsp;<mm:field name="titel"/>&nbsp;</td>
               <td valign="top" nowrap>&nbsp;<mm:field name="embargo"><mm:time format="dd-MM-yyyy HH-mm"/></mm:field>&nbsp;</td>
               <td valign="top">
                  <mm:related path="maillijst,thema" orderby="thema.naam" searchdir="DESTINATION">
                     <mm:field name="thema.naam"/>, 
                  </mm:related>
               </td>
            </tr>
         </mm:listnodes>
         </table>
      </td>
   </tr>
</table>

</mm:cloud>
</body>
</html>
