<%@include file="/taglibs.jsp" %>
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

      function objClick(el) {
         var href = el.parentElement.getAttribute("href")+"";
         if (href.length<10) return;
         document.location=href;
      }
   </script>
</head>

<body>

<mm:import externid="pollnumber"/>

<table class="body">
   <tr class="itemrow">
      <td>
         <h1>Alle Polls</h1>
      </td>
   </tr>
   <tr>
      <td>
         <table class="listcontent">
              <tr class="listheader">
               <th>&nbsp;Titel&nbsp;</th>
               <th>&nbsp;Stelling&nbsp;</th>
               <th align="left">&nbsp;Periode&nbsp;datum&nbsp;</th>
            </tr>
         <mm:listnodes type="poll" orderby="embargo, verloopdatum">
            <mm:remove referid="objectnumber"/>
            <mm:import id="objectnumber"><mm:field name="number"/></mm:import>
            <tr class="itemrow" onMouseOver="objMouseOver(this);" onMouseOut="objMouseOut(this);"
               href="<mm:url><mm:param name="pollnumber"><mm:write referid="objectnumber"/></mm:param></mm:url>">
               <td onMouseDown="objClick(this);" valign="top" nowrap>&nbsp;<mm:field name="titel"/>&nbsp;</td>
               <td onMouseDown="objClick(this);" valign="top"><mm:field name="stelling"/></td>
               <td onMouseDown="objClick(this);" valign="top" nowrap><mm:field name="embargo"><mm:time format="dd-MM-yyyy"/></mm:field>&nbsp;&nbsp;tot&nbsp;&nbsp;<mm:field name="verloopdatum"><mm:time format="dd-MM-yyyy"/></mm:field>&nbsp;</td>
            </tr>
            <mm:compare referid="pollnumber" value="$objectnumber">
               <tr>
                  <td colspan="3" align="left">
                     <mm:node number="$pollnumber" jspvar="node">
                        <%@ include file="pollresult.jsp"%>
                     </mm:node>
                  <td>
               </tr>
               <tr>
                  <td colspan="3">&nbsp;</td
               </tr>
            </mm:compare>
         </mm:listnodes>
         </table>
      </td>
   </tr>
</table>

</mm:cloud>
</body>
</html>
