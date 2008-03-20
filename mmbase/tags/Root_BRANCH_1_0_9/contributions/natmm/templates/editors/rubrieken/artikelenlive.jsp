<%@page import="nl.leocms.versioning.PublishManager,
                 org.mmbase.bridge.NodeList,
                 org.mmbase.bridge.NodeIterator,
                 org.mmbase.bridge.Node,
                 nl.leocms.util.PaginaHelper"%>
<%@include file="/taglibs.jsp" %>
<mm:import externid="rubriek" required="false" />
<mm:import externid="rubrieknaam" required="false" />
<%
   String refreshFrame = request.getParameter("refreshFrame");
   String refreshFrameJs = "";
   if (refreshFrame != null && !"".equals(refreshFrame)) {
      refreshFrameJs += "&refreshframe=";
      refreshFrameJs += refreshFrame;
   }
   else {
      refreshFrameJs = "&refreshframe=rightpane";
   }
%>
<html>
   <head>
      <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/list.css" type="text/css" rel="stylesheet"/>
      <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/list.css" type="text/css" rel="stylesheet"/>
      <title>Artikelen live</title>
      <script language="javascript" src="../../js/gen_utils.js">
      </script>
      <script>
          function setRubriek(nummer,naam) {
              document.forms[0].rubrieknaam.value=naam;
            document.forms[0].rubriek.value=nummer;
              document.forms[0].submitButton.disabled = false;
          }
      </script>
      <style>
      <!--
         INPUT {width: auto}
         td { font-family: Verdana; font-size: 8pt ;font-weight: normal;}
         th { background-color: #7198B8 ; text-align : left; }
      -->
      </style>
   </head>
   <body style="overflow: auto">
      <h2>Artikelen in live omgeving</h2>

      <form action="artikelenlive.jsp">
         <input type='text' maxlength='30' name='rubrieknaam' readonly='true' value="<mm:write referid="rubrieknaam"/>"><a href="../rubrieken/selector/rubriek_selector.jsp?refreshtarget=setRubriek&mode=js&refreshframe=bottompane.rightpane" onclick="openPopupWindow('selector',350,500)" target='selector'><img src="../img/rubriek.gif" border='0'/></a>
         <input type="hidden" name="rubriek" value="<mm:write referid="rubriek"/>"/>
         <input type="submit" name="submitButton" value="Laat zien!" <mm:compare referid="rubriek" value="">disabled="true"</mm:compare>/>
      </form>
      <table>
         <tr>
            <th width="75%">Titel</th>
            <th width="25%">Objectnummer (live)</th>
         </tr>

         <mm:cloud jspvar="cloud" >
            <mm:compare referid="rubriek" value="" inverse="true" >
               <mm:node number="${rubriek}">
                  <mm:relatednodes type="artikel" role="creatierubriek" jspvar="node">
            <tr>
<%
//   String url = new PaginaHelper(cloud).createUrlForContentElement(node, "/live");
%>
<%--               <td class="fieldname"><a href="<%=url%>"><mm:field name="titel"/></a></td>--%>
               <td class="fieldname"><mm:field name="titel"/></td>
               <td class="fieldname">
 <%

    NodeList publishedNodes = PublishManager.getPublishedNodes(node);
    if (publishedNodes != null) {
       NodeIterator publishedNodesIter = publishedNodes.nodeIterator();
       while (publishedNodesIter != null && publishedNodesIter.hasNext()) {
          Node publishedNode = publishedNodesIter.nextNode();
          out.write(publishedNode.getStringValue("number"));
       }
    }
 %>
               </td>
            </tr>
                  </mm:relatednodes>
               </mm:node>
            </mm:compare>
         </mm:cloud>
      </table>
   </body>
</html>