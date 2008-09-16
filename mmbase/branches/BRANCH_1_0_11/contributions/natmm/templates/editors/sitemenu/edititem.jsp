<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">

<mm:import externid="objectnumber" required="true" />
<mm:import externid="action" />

<mm:present referid="action">
    <mm:import externid="identifier" />
    <mm:import externid="naam" />
    <mm:import externid="naam_fra" />
    <mm:import externid="naam_eng" />
    <mm:import externid="naam_de" />
    <mm:import externid="type" />
    <mm:import externid="url" />
    <mm:import externid="target" />
    <mm:import externid="pagina" />
    <mm:import externid="rubriek" />
    <mm:import externid="location" jspvar="location" />

    <mm:compare referid="action" value="save">
        <mm:compare referid="objectnumber" value="new" inverse="true">
            <mm:node number="$objectnumber" jspvar="node">
                <mm:setfield name="identifier"><mm:write referid="identifier" /></mm:setfield>              
                <mm:setfield name="naam"><mm:write referid="naam" /></mm:setfield>
                <mm:setfield name="naam_fra"><mm:write referid="naam_fra" /></mm:setfield>
                <mm:setfield name="naam_eng"><mm:write referid="naam_eng" /></mm:setfield>
                <mm:setfield name="naam_de"><mm:write referid="naam_de" /></mm:setfield>
                <mm:setfield name="type"><mm:write referid="type" /></mm:setfield>
            <mm:setfield name="location"><mm:write referid="location" /></mm:setfield>

                <mm:compare referid="type" value="url">
                    <% node.deleteRelations("related"); %>
                    <mm:setfield name="url"><mm:write referid="url" /></mm:setfield>
                    <mm:setfield name="target"><mm:write referid="target" /></mm:setfield>
                </mm:compare>
                <mm:compare referid="type" value="pagina">
                    <mm:setfield name="url"/>
                    <mm:setfield name="target"/>
                    <% boolean exists = false; %>
                    <mm:relatednodes type="pagina" role="related" constraints="number = $pagina"><%
                        exists = true;
                    %></mm:relatednodes><%
                        if (!exists) {
                            node.deleteRelations("related");
                        }
                    %><mm:createrelation source="objectnumber" destination="pagina" role="related"/>
                </mm:compare>
                <mm:compare referid="type" value="rubriek">
                    <mm:setfield name="url"/>
                    <mm:setfield name="target"/>
                    <% boolean exists = false; %>
                    <mm:relatednodes type="rubriek" role="related" constraints="number = $rubriek"><%
                        exists = true;
                    %></mm:relatednodes><%
                        if (!exists) {
                            node.deleteRelations("related");
                        }
                    %><mm:createrelation source="objectnumber" destination="rubriek" role="related"/>
                </mm:compare>
            </mm:node>
        </mm:compare>
        <mm:compare referid="objectnumber" value="new">
            <mm:createnode type="sitemenu" id="creatednode">
                <mm:setfield name="identifier"><mm:write referid="identifier" /></mm:setfield>
                <mm:setfield name="naam"><mm:write referid="naam" /></mm:setfield>
                <mm:setfield name="naam_fra"><mm:write referid="naam_fra" /></mm:setfield>
                <mm:setfield name="naam_eng"><mm:write referid="naam_eng" /></mm:setfield>
                <mm:setfield name="naam_de"><mm:write referid="naam_de" /></mm:setfield>
                <mm:setfield name="type"><mm:write referid="type" /></mm:setfield>
                <mm:setfield name="location"><mm:write referid="location" /></mm:setfield>
                <mm:compare referid="type" value="url">
                    <mm:setfield name="url"><mm:write referid="url" /></mm:setfield>
                    <mm:setfield name="target"><mm:write referid="target" /></mm:setfield>
                </mm:compare>
            </mm:createnode>
            <mm:node referid="creatednode">
                <mm:remove referid="objectnumber" />
                <mm:import id="objectnumber"><mm:field name="number"/></mm:import>
            </mm:node>
         <mm:compare referid="type" value="pagina">
            <mm:createrelation source="objectnumber" destination="pagina" role="related"/>
         </mm:compare>
         <mm:compare referid="type" value="rubriek">
            <mm:createrelation source="objectnumber" destination="rubriek" role="related"/>
         </mm:compare>
        </mm:compare>
    </mm:compare>
<%  response.sendRedirect("listmenu.jsp?location=" + location); %>
</mm:present>

<mm:notpresent referid="action">
    <mm:compare referid="objectnumber" value="new">
        <mm:import id="identifier"></mm:import>
        <mm:import id="naam"></mm:import>
        <mm:import id="naam_fra"></mm:import>
        <mm:import id="naam_eng"></mm:import>
        <mm:import id="naam_de"></mm:import>
        <mm:import id="type"></mm:import>
        <mm:import id="target"></mm:import>
        <mm:import id="url">http://</mm:import>
        <mm:import id="pagina"></mm:import>
        <mm:import id="rubriek"></mm:import>
        <mm:import externid="location"></mm:import>
    </mm:compare>
    <mm:compare referid="objectnumber" value="new" inverse="true">
        <mm:node number="$objectnumber">
            <mm:import id="identifier"><mm:field name="identifier"/></mm:import>
            <mm:import id="naam"><mm:field name="naam"/></mm:import>
            <mm:import id="naam_fra"><mm:field name="naam_fra"/></mm:import>
            <mm:import id="naam_eng"><mm:field name="naam_eng"/></mm:import>
            <mm:import id="naam_de"><mm:field name="naam_de"/></mm:import>
            <mm:import id="type"><mm:field name="type"/></mm:import>
            <mm:import id="target"><mm:field name="target"/></mm:import>
            <mm:import id="url"><mm:compare referid="type" value="url"><mm:field name="url"/></mm:compare></mm:import>
            <mm:import id="pagina"><mm:compare referid="type" value="pagina"><mm:relatednodes type="pagina" role="related"><mm:first><mm:field name="number"/></mm:first></mm:relatednodes></mm:compare></mm:import>
            <mm:import id="rubriek"><mm:compare referid="type" value="rubriek"><mm:relatednodes type="rubriek" role="related"><mm:first><mm:field name="number"/></mm:first></mm:relatednodes></mm:compare></mm:import>
         <mm:import id="location"><mm:field name="location"/></mm:import>
        </mm:node>
    </mm:compare>
<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/list.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/list.css" type="text/css" rel="stylesheet"/>
   <style>
    tr.itemrow { cursor: default; }
   </style>
   <link rel="stylesheet" type="text/css" href="style.css">

   <script type="text/javascript">
   function popupPaginaSelector() {
      var newWindow;
      var urlstring = "../paginamanagement/selector/pagina_selector.jsp?fieldname=paginanaam&fieldnumber=pagina&targetframe=bottompane.rightpane.editscreen";
      newWindow = window.open(urlstring,'_popUpPage','height=500,width=400,left=100,top=100,scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=no');
   }

   function popupRubriekSelector() {
      var newWindow;
      var urlstring = "../rubrieken/selector/rubriek_selector.jsp?refreshtarget=setRubriek&mode=js&refreshframe=bottompane.rightpane.editscreen";
      newWindow = window.open(urlstring,'_popUpPage','height=350,width=500,left=100,top=100,scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=no');
   }

    function setRubriek(nummer,naam) {
        document.forms[0].rubrieknaam.value=naam;
        document.forms[0].rubriek.value=nummer;
    }

   function selectTr(el) {
      if (el.value == "url") {
         showUrl();
      }
      if (el.value == "pagina") {
         showPagina();
      }
      if (el.value == "rubriek") {
         showRubriek();
      }
   }


   function showUrl() {
      showTr("trUrl");
      showTr("trUrlTarget");
      hideTr("trPagina");
      hideTr("trRubriek");
   }

   function showPagina() {
   <mm:compare referid="location" value="sitemenu">
      hideTr("trUrl");
      hideTr("trUrlTarget");
   </mm:compare>
      showTr("trPagina");
      hideTr("trRubriek");
   }

   function showRubriek() {
   <mm:compare referid="location" value="sitemenu">
      hideTr("trUrl");
      hideTr("trUrlTarget");
   </mm:compare>
      hideTr("trPagina");
      showTr("trRubriek");
   }

   /**
    *  Provide a certain Table Row with
    *  a certain style.
    *  @param name of the Table Row
    */

   function showTr(name) {
      document.getElementById(name).style.display = 'block';
   }
   function hideTr(name) {
      document.getElementById(name).style.display = 'none';
   }
   </script>
</head>
<body onload="selectTr(document.getElementById('type'));">
<table class="body">
   <tr class="itemrow">
      <td>
         <h1>
                    <mm:compare referid="objectnumber" value="new">
                  <mm:compare referid="location" value="sitemenu" >
                           Nieuw menuitem
                  </mm:compare>
                  <mm:compare referid="location" value="tab" >
                           Nieuw tabblad
                  </mm:compare>
                    </mm:compare>
                    <mm:compare referid="objectnumber" value="new" inverse="true">
                  <mm:compare referid="location" value="sitemenu" >
                           Bewerk menuitem
                  </mm:compare>
                  <mm:compare referid="location" value="tab" >
                           Bewerk tabblad
                  </mm:compare>
                    </mm:compare>
                </h1>
      </td>
   </tr>
   <tr>
      <td>
                <form method="post" name="editform">
                    <input type="hidden" name="action" value="save"/>
                    <input type="hidden" name="objectnumber" value="<mm:write referid="objectnumber"/>"/>
                    <input type="hidden" name="location" value="<mm:write referid="location"/>"/>

                    <table class="inputcanvas">
                        <tr>
                            <td>Identifier</td>
                            <td><input type="text" name="identifier" value="<mm:write referid="identifier"/>"></td>
                        </tr>
                        <tr>
                            <td>Naam</td>
                            <td><input type="text" name="naam" value="<mm:write referid="naam"/>"></td>
                        </tr>
                        <tr>
                            <td>Naam (Frans)</td>
                            <td><input type="text" name="naam_fra" value="<mm:write referid="naam_fra"/>"></td>
                        </tr>
                        <tr>
                            <td>Naam (English)</td>
                            <td><input type="text" name="naam_eng" value="<mm:write referid="naam_eng"/>"></td>
                        </tr>
                        <tr>
                            <td>Naam (Deutsch)</td>
                            <td><input type="text" name="naam_de" value="<mm:write referid="naam_de"/>"></td>
                        </tr>
                        <tr>
                            <td>type</td>
                            <td>
                                <select id="type" name="type" onchange="selectTr(this);">
                           <mm:compare referid="location" value="sitemenu">
                                    <option value="url" <mm:compare referid="type" value="url">selected</mm:compare> >
                                        Url
                                    </option>
                           </mm:compare>
                                    <option value="pagina" <mm:compare referid="type" value="pagina">selected</mm:compare> >
                                        Pagina
                                    </option>
                                    <option value="rubriek" <mm:compare referid="type" value="rubriek">selected</mm:compare> >
                                        Rubriek
                                    </option>
                                </select>
                            </td>
                        </tr>
                  <mm:compare referid="location" value="sitemenu">
                        <tr id="trUrl">
                            <td>Url *</td>
                            <td><input type="text" name="url" value="<mm:write referid="url"/>"></td>
                        </tr>
                  </mm:compare>
                  <mm:compare referid="location" value="sitemenu">
                        <tr id="trUrlTarget">
                            <td>Target</td>
                            <td>
                                <select name="target">
                                    <option value="_this"<mm:compare referid="target" value="_this">selected</mm:compare> >
                                        Huidige venster
                                    </option>
                                    <option value="_new" <mm:compare referid="target" value="_new">selected</mm:compare> >
                                        Nieuw venster
                                    </option>
                                </select>
                            </td>
                        </tr>
                  </mm:compare>
                  <tr id="trPagina">
                            <td>Pagina</td>
                            <td>
                                <mm:compare referid="type" value="pagina">
                                    <mm:compare referid="pagina" value="" inverse="true">
                                        <mm:node referid="pagina">
                                            <mm:import id="paginanaam"><mm:field name="titel"/></mm:import>
                                        </mm:node>
                                    </mm:compare>
                                </mm:compare>
                                <input type="text" name="paginanaam" class="text" disabled="true" value="<mm:present referid="paginanaam"><mm:write referid="paginanaam"/></mm:present>" >
                                <input type="hidden" name="pagina" value="<mm:write referid="pagina"/>">
                                <img src="../img/search.gif" border="0" onclick="popupPaginaSelector()" alt="Selecteer een pagina">
                            </td>
                        </tr>
                        <tr id="trRubriek">
                            <td>Rubriek</td>
                            <td>
                                <mm:compare referid="type" value="rubriek">
                                    <mm:compare referid="rubriek" value="" inverse="true">
                                        <mm:node referid="rubriek">

                                            <mm:import id="rubrieknaam"><mm:field name="naam"/></mm:import>
                                        </mm:node>
                                    </mm:compare>
                                </mm:compare>
                                <input type='text' name="rubrieknaam" class="text" disabled="true" value="<mm:present referid="rubrieknaam"><mm:write referid="rubrieknaam"/></mm:present>">
                                <input type="hidden" name="rubriek" value="<mm:write referid="rubriek"/>">
                                <img src="../img/rubriek.gif" border="0" onclick="popupRubriekSelector()" alt="Selecteer een rubriek">
                            </td>
                        </tr>
                    </table>
                    <br/>
                    <mm:compare referid="location" value="sitemenu">                    
                    <p>* U kunt javascript code toevoegen door 'http://' te vervangen door 'javascript:'. <b>Let op</b> gebruik alleen enkele quotes ('): dubbele quotes (") werken NIET!</p>
                    </mm:compare>
                    <br/>
                    <input class="button" type="submit" value="Verzenden">
                </form>
      </td>
   </tr>
</table>

</mm:notpresent>

</body>
</html>

</mm:cloud>