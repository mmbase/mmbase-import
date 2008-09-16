<table border="1" cellpadding="0" cellspacing="0" width="824">
 <tr>
   <td>
     <table border="0" cellspacing="0" width="820">
       <tr>
         <td width="151" valign="top">Type</td>
         <td width="*"><input type="checkbox"
                              name="allTypesSelected"
                              value="all"
                              onClick="xableAllCheckBoxes();"
                              <% if(allTypesSelected) { %>checked<% } %>
                       >Alle onderstaande content-typen
           <hr>
             <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                <%
                Locale locale = new Locale("nl");
                int cTypesSize = cTypes.size();
                for (int i=0; i<cTypesSize; i++) {
                  String ct = (String) cTypes.get(i);
                  %>
                  <td><input type="checkbox" id="<%=ct%>" onClick="updateHiddenValue();"><%= cloud.getNodeManager(ct).getGUIName(NodeManager.GUI_SINGULAR,locale) %></td>
                  <%
                  if((i+1) == cTypesSize/2) {
                     %>
                     </tr><tr>
                     <%
                  }
                }
                %>         
                </tr>
                <!-- hidden pars -->
                <input type="hidden" name="selectedTypes" id="selectedTypes" value="<%=selectedTypes %>">
                <script language="Javascript">
                   setCheckBoxes("<%=selectedTypes %>", "<%=disableTypesModus%>");
                   <% if(allTypesSelected) { %>
                   xableAllCheckBoxes();
                   <% } %>
                </script>
             </table>
          </td>
       </tr>
       <tr>
          <td width="151">Titel bevat</td>
          <td width="*">
             <b><input type="text" name="titel" size="50" value="<%=titel %>"></b>
          </td>
       </tr>
       <tr>
          <td width="151">Hoofdrubriek</td>
          <td width="*"><b>
             <select size="1" name="rubriek" style="width:150px">
                <option value="<%=OPTION_ALLE %>"
                   <% if(OPTION_ALLE.equals(rubriek)) { %>selected<%}%>
                ><%=OPTION_ALLE %></option>
                <mm:list nodes="root" path="rubriek,childrel,rubriek2" fields="rubriek2.number" orderby="childrel.pos" searchdir="DESTINATION">
                   <mm:field jspvar="site" vartype="String" name="rubriek2.number">
                      <mm:list nodes="<%=site %>" path="rubriek,childrel,rubriek2" fields="rubriek2.number,rubriek2.naam" orderby="childrel.pos" searchdir="DESTINATION">
                         <mm:field jspvar="rubriekId" vartype="String" name="rubriek2.number">
                         <option value="<%=rubriekId %>"
                            <% if(rubriekId.equals(rubriek)) { %>selected<%}%>
                            >
                            <mm:field name="rubriek2.naam"/>
                         </option>
                         </mm:field>
                      </mm:list>
                   </mm:field>
                </mm:list>
             </select></b>
          </td>
       </tr>
       <tr>
          <td width="151">Creatiedatum</td>
          <td width="*">
             <select size="1" name="age" style="width:150px">
                <option value="0" <% if(age == 0) { %>selected<%}%> >-</option>
                <option value="1" <% if(age == 1) { %>selected<%}%> >Afgelopen 24 uur</option>
                <option value="7" <% if(age == 7) { %>selected<%}%> >Afgelopen week</option>
                <option value="31" <% if(age == 31) { %>selected<%}%> >Afgelopen maand</option>
                <option value="120" <% if(age == 120) { %>selected<%}%> >Afgelopen kwartaal</option>
                <option value="365" <% if(age == 365) { %>selected<%}%> >Afgelopen jaar</option>
             </select>
          </td>
       </tr>
       <%-- hh <tr>
          <td width="151">Taal</td>
          <td width="*">
             <select size="1" name="language" style="width:150px">
                <option value="nl" <% if ((language != null) && (language.equals("nl"))) { %>selected<%}%> >Nederlands</option>
                <option value="fra" <% if ((language != null) && (language.equals("fra"))) { %>selected<%}%> >Frans</option>
                <option value="eng" <% if ((language != null) && (language.equals("eng"))) { %>selected<%}%> >English</option>
                <option value="de" <% if ((language != null) && (language.equals("de"))) { %>selected<%}%> >Deutsch</option>
             </select>
          </td>
       </tr> --%>
      <% String style = "block";
         String standaardStyle = "none";
         if("simple".equals(modus)) {
            style="none";
            standaardStyle="block";
         }
      %>
      <tr>
         <td width="151" valign="top"></td>
         <td width="*">
            <a href="#" onclick="toggleAdvanced()"><img id="button_geavanceerd" src="pix/geavanceerd.gif" border="0" style="display: <%=standaardStyle%>"/></a>
            <a href="#" onclick="toggleAdvanced()"><img id="button_standaard" src="pix/standaard.gif" border="0" style="display: <%=style%>"/></a>
         </td>
      </tr>
       <input type="hidden" value="<%=modus %>" name="modus" id="modus">
       <input type="hidden" value="<%=request.getParameter("linkType")%>" name="linkType">
       <tr id="ad_1" style="display: <%=style%>">
         <td width="151">Auteur</td>
         <td width="*">
            <select size="1" name="auteur">
               <option value="<%=OPTION_ALLE %>" selelcted>
               <%=OPTION_ALLE %></option>
               <mm:listnodes type="users" orderby="voornaam">
                  <mm:field name='number' jspvar='auteurId' vartype='String'>
                     <option value="<%=auteurId %>"
                        <% if(auteur.equals(auteurId)) { %>selected<% }%>
                     >
                        <mm:field name="voornaam"/> <mm:field name="tussenvoegsel"/> <mm:field name="achternaam"/> (<mm:field name="account"/>)
                     </option>
                  </mm:field>
               </mm:listnodes>
            </select>
         </td>
       </tr>
       <tr id="ad_2" style="display: <%=style%>">
          <td width="151">Redactionele aantekening bevat</td>
          <td width="*">
             <b><input type="text" name="metatag" size="50" value="<%=metatag %>"></b>
          </td>
       </tr>
       <tr id="ad_3" style="display: <%=style%>">
          <td width="151">Laatste wijziging:</td>
          <td width="*">
             <select size="1" name="changeAge" style="width:150px">
                <option value="0" <% if(changeAge == 0) { %>selected<%}%> >-</option>
                <option value="1" <% if(changeAge == 1) { %>selected<%}%> >Afgelopen 24 uur</option>
                <option value="7" <% if(changeAge == 7) { %>selected<%}%> >Afgelopen week</option>
                <option value="31" <% if(changeAge == 31) { %>selected<%}%> >Afgelopen maand</option>
                <option value="120" <% if(changeAge == 120) { %>selected<%}%> >Afgelopen kwartaal</option>
                <option value="365" <% if(changeAge == 365) { %>selected<%}%> >Afgelopen jaar</option>
             </select>
          </td>
       </tr>
       <tr id="ad_4" style="display: <%=style%>">
         <td width="151">Op pagina</td>
         <td width="*">
            <b>
               <input type="text" name="paginanaam" size="34" value="<%=paginanaam %>">
               <input type="hidden" name="pageno" value="<%=pageNo %>">
               <img border="0" src="../img/search.gif" align="bottom" onclick="javascript:popupPaginaSelector()" alt="Selecteer een pagina">
            </b>
         </td>
       </tr>
     </table>
   </td>
 </tr>
</table>
