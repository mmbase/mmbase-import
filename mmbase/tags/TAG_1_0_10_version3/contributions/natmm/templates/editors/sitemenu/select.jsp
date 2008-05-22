<%@include file="/taglibs.jsp" %>
<%@page import="org.mmbase.bridge.*,java.util.*" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">

<mm:import externid="subsite">rubriek.leocms.nl</mm:import>
<mm:import externid="location" jspvar="location" vartype="String" required="true" />
<mm:node number="$subsite" id="subsiteNode" jspvar="node">
    <mm:import id="subsiteNumber"><mm:field name="number"/></mm:import>
<%
    String action = request.getParameter("action");
    String[] selectedSitemenus = request.getParameterValues("selectedItems");
    if ("save".equals(action)) {
        RelationManager relmgr = cloud.getRelationManager("sitemenurel");
        RelationList sitemenus = node.getRelations("sitemenurel", "sitemenu");

        //delete related sitemenus
        RelationIterator sitemenusIter = sitemenus.relationIterator();
        while (sitemenusIter.hasNext()) {
            Relation element = sitemenusIter.nextRelation();

         String elementlocation = element.getDestination().getStringValue("location");
         if (location != null && location.equals(elementlocation)) {
            boolean found = false;
            if (selectedSitemenus != null) {
               for (int i = 0; i < selectedSitemenus.length; i++) {
                  String itemNumber = String.valueOf(element.getDestination().getNumber());
                  if (itemNumber.equals(selectedSitemenus[i])) {
                     found = true;
                     break;
                  }
               }
            }
            if (!found) {
               element.delete();
            }
         }
        }

        //add sitemenus
        if (selectedSitemenus != null) {

            for (int i = 0; i < selectedSitemenus.length; i++) {
                Relation found = null;

                RelationIterator sitemenusIter2 = sitemenus.relationIterator();
                while (sitemenusIter2.hasNext()) {
                    Relation element = sitemenusIter2.nextRelation();
               String elementlocation = element.getDestination().getStringValue("location");
               if (location != null && location.equals(elementlocation)) {

                  String itemNumber = String.valueOf(element.getDestination().getNumber());
                  if (itemNumber.equals(selectedSitemenus[i])) {
                     found = element;
                     break;
                  }
               }
                }
                if (found == null) {
                    int itemNumber = Integer.valueOf(selectedSitemenus[i]).intValue();
                    Node item = cloud.getNode(itemNumber);
                    Relation rel = node.createRelation(item, relmgr);
                    rel.setIntValue("pos", i);
                    rel.commit();
                }
                else {
                    found.setIntValue("pos", i);
                    found.commit();
                }
            }
        }
    }

%>
</mm:node>

<html>
  <head>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8">
    <script type="text/javascript" src="select.js"></script>

   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/list.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/list.css" type="text/css" rel="stylesheet"/>
   <style>
    tr.itemrow { cursor: default; }
   </style>
   <link rel="stylesheet" type="text/css" href="style.css">
   <title>Beheeromgeving | <mm:compare referid="location" value="sitemenu">Menu items</mm:compare><mm:compare referid="location" value="tab">Tabbladen</mm:compare></title>
  </head>
  <body>
<table class="body">
   <tr class="itemrow">
      <td>
                <h1>
                    Menu voor <mm:node referid="subsiteNode"><mm:field name="naam" /></mm:node>
                </h1>
      </td>
   </tr>
   <tr>
      <td>
        <table class="canvas">
            <tr>
                <td>
                    <form name="SelectForm" method="post">
                        <table class="inputcanvas">
                            <tr>
                                <td colspan="4">
                                    Subsite:
                                    <select name="subsite" onchange="document.SelectForm.submit();">
                                        <mm:listnodes type="rubriek" constraints="[level] = 1">
                                            <mm:context>
                                            <option value="<mm:field name="number" id="listNumber"/>"
                                                <mm:compare referid="listNumber" referid2="subsiteNumber">
                                                selected="true"
                                                </mm:compare>>
                                                <mm:field name="naam" />
                                            </option>
                                            </mm:context>
                                        </mm:listnodes>
                                    </select>
                                </td>
                            </tr>
                        </table>
                    </form>
                    <form name="OperationForm" method="post"
                        onsubmit="deSelectAll(document.OperationForm.availableItems); selectAll(document.OperationForm.selectedItems); return true;">
                        <input type="hidden" name="action" value="save"/>
                        <input type="hidden" name="subsite" value="<mm:write referid="subsiteNumber" />"/>
                        <table class="inputcanvas">
                            <tr>
                                <td>
                                    Beschikbare items.<br/>
                                    <select MULTIPLE="true" size="10" name="availableItems">
                                        <mm:listnodes type="sitemenu" orderby="identifier" constraints="location='$location'" >
                                            <% boolean add = true; %>
                                            <mm:related path="sitemenurel,rubriek" constraints="rubriek.number = $subsiteNumber">
                                                <% add = false; %>
                                            </mm:related>
                                            <% if (add) { %>
                                                <option value="<mm:field name="number" />"><mm:field name="identifier" /></option>
                                            <% } %>
                                        </mm:listnodes>
                                    </select>
                                </td>
                                <td>
                                    <br>
                                    <input onClick="one2two(document.OperationForm.availableItems, document.OperationForm.selectedItems)" value=">>" class="flexbutton" type="button">
                                    <br>
                                    <input onClick="two2one(document.OperationForm.availableItems, document.OperationForm.selectedItems)" value="<<" class="flexbutton" type="button">
                                    <br>
                                </td>
                                <td>
                                    Geselecteerde items.<br/>
                                    <select MULTIPLE="true" size="10" name="selectedItems">
                                        <mm:node referid="subsiteNode">
                                            <mm:related path="sitemenurel,sitemenu" orderby="sitemenurel.pos" constraints="location='$location'">
                                                <option value="<mm:field name="sitemenu.number" />"><mm:field name="sitemenu.identifier" /></option>
                                            </mm:related>
                                        </mm:node>
                                    </select>
                                <td class="width100"></td>
                                <td>
                                    <br>
                                    <input onClick="moveUp(document.OperationForm.selectedItems)" value="UP" class="flexbutton" type="button">
                                    <br>
                                    <input onClick="moveDown(document.OperationForm.selectedItems)" value="DOWN" class="flexbutton" type="button">
                                    <br>
                                </td>
                            </tr>
                        </table>

                        <input class="button" type="submit" value="Verzenden">
                    </form>
                </td>
            </tr>
        </table>
      </td>
   </tr>
</table>

  </body>
</html>

</mm:cloud>