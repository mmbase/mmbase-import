<% if (!show_unused) {%>
   <hr noshade size="3">
<% } %>
<input type="hidden" name="orderColumn" value="<%=orderColumn %>" id="orderColumn">
<input type="hidden" name="curPage" value="<%=curPage %>">
<input type="hidden" name="popup" value="<%=popup %>">
<input type="hidden" name="searchIsOn" value="true">
<%
if (searchIsOn) {
   Date start = new Date();
   String queryLog = "";

   String contentElementConstraint = "";
   StringTokenizer st = new StringTokenizer(selectedTypes,",",false);
   if (st.countTokens() > 0 && (allTypesSelected==false)) {
      contentElementConstraint += " contentelement.otype IN(0";
      while(st.hasMoreElements()) {
         String token = st.nextToken();
         contentElementConstraint += "," + contentHelper.getOtypeWithName(token);
      }
      contentElementConstraint += ") ";
   }

   if(metatag.length() > 0) {
      if (contentElementConstraint.length() > 0) { contentElementConstraint += " AND "; }
      contentElementConstraint += "UPPER(contentelement.metatags) LIKE '%" + metatag.toUpperCase() + "%'";
   }

   if(titel.length() > 0) {
      if (contentElementConstraint.length() > 0) { contentElementConstraint += " AND "; }
      if ("nl".equals(language)) {
         contentElementConstraint += "UPPER(contentelement.titel) LIKE '%" + titel.toUpperCase() + "%'";
      } else {
         contentElementConstraint += "contentelement.titel_" + language + " LIKE '%" + titel + "%'";
      }
   }

   if (age!=0) {
      if (contentElementConstraint.length() > 0) { contentElementConstraint += " AND "; }
      long realAge = nu - ( age * 3600 * 24 );
      contentElementConstraint += "contentelement.creatiedatum > "+realAge;
   }

   if (changeAge!=0) {
      if (contentElementConstraint.length() > 0) { contentElementConstraint += " AND "; }
      long realAge = nu - ( changeAge * 3600 * 24 );
      contentElementConstraint += " contentelement.datumlaatstewijziging > "+realAge;
   }

   if (pageNo != 0) {
      // todo: include other possible paths between contentelements and pagina
      String nodes = Integer.toString(pageNo);
      NodeList list = cloud.getList(nodes, "pagina,contentrel,contentelement", "contentelement.number", "", "", null, null, true );

      // The where clause is outside the if, because we have to create a
      // where clause when a page is selected
      if (contentElementConstraint.length() > 0) { contentElementConstraint += " AND "; }
      contentElementConstraint += " contentelement.number IN (0";
      if(list.size() > 0) {
         NodeIterator it = list.nodeIterator();
         while (it.hasNext()) {
            Node node = it.nextNode();
            contentElementConstraint += "," + node.getStringValue("contentelement.number");
         }
      }
      contentElementConstraint += ") ";
   }

   if (show_unused){
      String unused_items = (String) application.getAttribute("unused_items");
      contentElementConstraint = " contentelement.number IN (0, " + unused_items + ") ";
   }

   String objects = "";
   if(!"".equals(contentElementConstraint)||allTypesSelected||show_unused) {
      queryLog += ", using cc=" +  (contentElementConstraint.length()>100 ? contentElementConstraint.substring(0,100) + "..." : contentElementConstraint);
      NodeList nlObjects = cloud.getList("",
                                 "contentelement",
                                 "contentelement.number",
                                 contentElementConstraint,
                                 "contentelement.datumlaatstewijziging","DOWN",null,true);
      StringBuffer sbObjects = new StringBuffer();
      for(int n=0; n<nlObjects.size(); n++) {
         if(n>0) { sbObjects.append(','); }
         sbObjects.append(nlObjects.getNode(n).getStringValue("contentelement.number"));
      }
      objects = sbObjects.toString();
   }

   if(!objects.equals("") && !OPTION_ALLE.equals(auteur)) {
      queryLog += ", using  users.number=" +  auteur;
      NodeList nlObjects = cloud.getList(objects,
                                 "contentelement,schrijver,users",
                                 "contentelement.number",
                                 "users.number="+auteur,
                                 "contentelement.datumlaatstewijziging","DOWN",null,true);
      StringBuffer sbObjects = new StringBuffer();
      for(int n=0; n<nlObjects.size(); n++) {
         if(n>0) { sbObjects.append(','); }
         sbObjects.append(nlObjects.getNode(n).getStringValue("contentelement.number"));
      }
      objects = sbObjects.toString();
   }

   String rubriekConstraint = "";
   String path = "";
   if (!OPTION_ALLE.equals(rubriek)) {
      // search in a specific hoofdrubriek
      path = "contentelement,hoofdrubriek,rubriek";
      rubriekConstraint = "rubriek.number="+rubriek;
   } else {
      // find all contentelements in any creatierubriek
      path = "contentelement,creatierubriek,rubriek";
   }

   boolean filterProductlinks = false;
   if (request.getParameter("linkType")!=null) {
       filterProductlinks = request.getParameter("linkType").equals("productencatalogus");
   }

   int sizeint = 0;
   int lowerBound = 0;
   int pages = 0;
   int upperBound = 0;
   String charsStartPage = "";
   String charsEndPage = "";
   int pageInIteration = 0;
   String pagesLinkString = "";
   String fieldname = "contentelement.titel";
   if (!"nl".equals(language)) {
     fieldname += "_" + language;
   }
   boolean foundResults = false;
   // where fields:
   //    - contentelement.otype, contentelement.metatags, contentelement.titel, contentelement.creatiedatum, contentelement.datumlaatstewijziging, contentelement.number
   //    - users.number,
   //    - rubriek.number
   // orderby fields:
   //    -contentelement.titel, contentelement.otype, contentelement.creatiedatum, contentelement.datumlaatstewijziging
   //    -rubriek.naam

   if(!objects.equals("")) {
      queryLog += ", using path=" +  path + " and rc=" + rubriekConstraint;
      %>
      <mm:list nodes="<%= objects %>" path="<%=path%>" orderby="<%=orderColumn%>" constraints="<%=rubriekConstraint%>" id="contentresults">
         <mm:first>
           <mm:import jspvar="size" vartype="Integer"><mm:size /></mm:import>
           <%
            foundResults = true;
            sizeint = size.intValue();
            lowerBound = Math.max(1,curPage-EXTRA_PAGES);
            pages = Math.round( (float)sizeint/((float)AMOUNT_OF_RESULTS) +0.49f);
            upperBound = Math.min(pages,curPage+EXTRA_PAGES);

            if (lowerBound>1) {
              pagesLinkString += "<a href=\"#\" onClick=\"javascript:pageIterate(1);\"><< </a>&nbsp;";
            }
            if (curPage>1) {
              pagesLinkString += "<a href=\"#\" onClick=\"javascript:pageIterate(" + (curPage-1) + ");\">< </a>&nbsp;";
            } %>
          <mm:import id="itemCountsString">
            Gevonden content elementen: <b><%=((curPage-1)*AMOUNT_OF_RESULTS+1) %> - <%=Math.min(curPage*AMOUNT_OF_RESULTS, sizeint) %> (<mm:size />)
          </mm:import>
        </mm:first>
        <mm:index jspvar="index" vartype="Integer">
        <%
        int indexint = index.intValue();
        // First element of page
        if (indexint % AMOUNT_OF_RESULTS == 1) {
          %>
          <mm:field name="<%= fieldname %>" jspvar="contentTitel" vartype="String"> <% charsStartPage = contentTitel.substring(0, Math.min(2, contentTitel.length())); %>  </mm:field>
          <% pageInIteration++;
        }
        // Last element of page
        if (indexint % AMOUNT_OF_RESULTS == 0 || indexint == sizeint) {
          %>
          <mm:field name="<%= fieldname %>" jspvar="contentTitel" vartype="String"> <% charsEndPage = contentTitel.substring(0, Math.min(2, contentTitel.length())); %>  </mm:field>
          <%
           pagesLinkString += "<a href=\"#\" class=\"nav\" onClick=\"javascript:pageIterate(" + pageInIteration + ");\">";
           if (pageInIteration == curPage) { pagesLinkString += "<font color=\"#FF0000\">"; }
           if (orderColumn.indexOf("titel") > -1) {
              pagesLinkString += charsStartPage + "-" + charsEndPage;
           }
           else {
              pagesLinkString += pageInIteration;
           }
           if (pageInIteration == curPage) { pagesLinkString += "</font>"; }
           pagesLinkString += "</a>&nbsp;";
           if((indexint>0)&&((indexint+1)%1000==1)) { pagesLinkString += "<br/>"; }
        } %>
        </mm:index>
        <mm:last>
          <%
            if (curPage<upperBound) {
              pagesLinkString += "<a href=\"#\" onClick=\"javascript:pageIterate(" + (curPage+1) + ");\">></a>";
            }
            if (curPage<pages - 1) {
              pagesLinkString += "<a href=\"#\" onClick=\"javascript:pageIterate(" + Math.max(pages,upperBound+1) + ");\">>></a>";
            }
          %>
        </mm:last>
      </mm:list>
      <%
   } else {
      %><mm:list nodes="" path="<%=path%>" orderby="<%=orderColumn%>" constraints="<%=rubriekConstraint%>" max="0" id="contentresults" /><%
   }
   int counter = 0;
   %>
   <mm:import id="pagesString">Pagina's <b> <%= pagesLinkString %> </b></mm:import>
   <mm:list referid="contentresults">
      <%
         String thisType = "";
         counter++;
      %>

      <mm:field name="contentelement.otype" jspvar="otype" vartype="String" >
      <% thisType = (String) contentHelper.getNameWithOtype(otype); %>
      </mm:field>

      <mm:first>
         <p><b>Er zijn <mm:size/> content elementen gevonden</b></p>
         <table border="0" cellpadding="0" cellspacing="0" width="876" height="73">
            <tr>
               <td align="left">
                 <mm:write referid="itemCountsString" /><br>
               </td>
               <td align="right">
                 <mm:write referid="pagesString" />
               </td>
            </tr>
         </table>
         <table border="0" cellpadding="0" cellspacing="0" width="976" height="73">
            <tr>
               <th style="width:60;height:13;"></th>
               <th style="width:60;height:13;vertical-align:bottom;text-align:left;"><a class="th" href="#" onclick='javascript:changeOrder("contentelement.otype")'>Type</a><% if("contentelement.otype".equals(orderColumn)) { %><img border="0" src="../img/down.gif"> <% } %>  </th>
               <th style="width:250;height:13;vertical-align:bottom;text-align:left;"><a class="th" href="#" onclick='javascript:changeOrder("contentelement.titel")'>Titel</a><% if("contentelement.titel".equals(orderColumn)) { %><img border="0" src="../img/down.gif"> <% } %> </th>

            <%
            if (thisType.equals("medewerkers")) {%>
            <th style="width:100;height:13;vertical-align:bottom;text-align:left;">
            Afbeelding?
            </th>
               <% } %>

               <th style="width:200;height:13;vertical-align:bottom;text-align:left;"><a class="th" href="#" onclick='javascript:changeOrder("rubriek.naam")'>Rubriek</a><% if("rubriek.naam".equals(orderColumn)) { %><img border="0" src="../img/down.gif"> <% } %> </th>
               <th style="width:90;height:13;vertical-align:bottom;text-align:left;"><a class="th" href="#" onclick='javascript:changeOrder("contentelement.creatiedatum")'>Creatie</a><% if("contentelement.creatiedatum".equals(orderColumn)) { %><img border="0" src="../img/down.gif"> <% } %> </th>
               <th style="width:90;height:13;vertical-align:bottom;text-align:left;"><a class="th" href="#" onclick='javascript:changeOrder("contentelement.datumlaatstewijziging")'>Geupdate</a><% if("contentelement.datumlaatstewijziging".equals(orderColumn)) { %><img border="0" src="../img/down.gif"> <% } %> </th>
               <th style="width:90;height:13;vertical-align:bottom;text-align:left;">Auteur</th>
               <th style="width:50;height:13;vertical-align:bottom;text-align:left;">Versie</th>
            </tr>
       </mm:first>

       <%
         if( (counter > (curPage-1)*AMOUNT_OF_RESULTS ) && (counter < curPage*AMOUNT_OF_RESULTS+1) ) {

         String usedIn = "";
         %><mm:field name="contentelement.number" jspvar="contentelement_number" vartype="String" write="false">
               <% NodeList nl = contentHelper.usedInItems(contentelement_number);
                  if (nl!=null){
                     for (int i = 0; i < nl.size(); i++){
                        usedIn += nl.getNode(i).getNodeManager().getName() + " " + nl.getNode(i).getStringValue("titel") + "\n";
                     }
                  }
                  // todo: not used content elements can not be opened in the editwizard
                  usedIn = (usedIn.equals("") ? "style=\"color:red;\" title=\"Dit object is niet in gebruik.\"" : "title=\"Gebruikt in: " +  usedIn + "\"" );%>
            </mm:field>

         <tr height="11">
            <td valign="top">
               <%
               if ("administrator".equalsIgnoreCase(cloud.getUser().getRank())) {
                  %>
                  <img src="../img/remove.gif" alt="Verwijder object" class="button"
                      onclick="javascript:doDelete('<mm:field name="contentelement.number"/>');">
                  <%
               }
               String cModus = (String) session.getAttribute("contentmodus");
               if ((!popup) && (cModus != null) && (cModus.equals("on"))) {
                  %>
                   <a href="../paginamanagement/frames.jsp?contentnodenumber=<mm:field name="contentelement.number"/>">
                       <img src="../img/multiple.gif" alt="Voeg contentelement toe op meerdere plaatsen" border="0">
                  </a>
                  <%
               }
               %>
               <img src="../img/edit_w.gif" alt="Content element aanpassen" class="button" onClick="javascript:doForward(<mm:field name="contentelement.number" write="true"/>,'<%= thisType %>');">
            </td>
            <td valign="top">
                <%= thisType %>
            </td>
            <td valign="top">
              <%
              if (!filterProductlinks) { %>
                 <a href="#" onClick="javascript:doForward(<mm:field name="contentelement.number" write="true"/>,'<%= thisType %>');" <%= usedIn %>>
                     <mm:field name="contentelement.titel"/>
                 </a>
                 <%
              } else {
                  boolean showLine = true;
                  %>
                  <mm:field name="contentelement.number" jspvar='contentNumber' write='false' vartype="String">
                     <% showLine = "productencatalogus".equals(cloud.getNode( contentNumber ).getStringValue("type").trim()); %>
                  </mm:field>
                  <%
                  if (showLine) {
                     %>
                      <a href="#" onClick="javascript:doForward(<mm:field name="contentelement.number" write="true"/>,'<%= thisType %>');" <%= usedIn %>>
                        <mm:field name="contentelement.titel" />
                     </a>
                     <%
                  } else {
                     %><mm:field name="contentelement.titel" /><%
                  }
               } %>
            </td>

          <%
          if (thisType.equals("medewerkers")) {%>
            <td>
            <% boolean hasPicture = false; %>

            <mm:field name="contentelement.number" jspvar="contentNumber" write="false" vartype="String">
               <mm:listcontainer nodes="<%= contentNumber %>" path="medewerkers,posrel,images">
                  <mm:list max="1">
                     <% hasPicture = true; %>
                  </mm:list>
               </mm:listcontainer>
            </mm:field>

            <% if (hasPicture) { %>
               ja
            <% } else { %>
               nee
            <% } %>

            </td>
            <% } %>
            
            <td valign="top">
                <mm:field name="rubriek.naam" />
            </td>
            <td valign="top">
                <mm:field name="contentelement.creatiedatum" jspvar="date" vartype="String">
                  <mm:isnotempty>
                    <mm:time time="<%=date%>" format="dd-MM-yyyy"/>
                  </mm:isnotempty>
                </mm:field>
            </td>
            <td valign="top">
                <mm:field name="contentelement.datumlaatstewijziging" jspvar="date" vartype="String">
                  <mm:isnotempty>
                    <mm:time time="<%=date%>" format="dd-MM-yyyy"/>
                  </mm:isnotempty>
                </mm:field>
            </td>
            <td valign="top">
               <mm:node element="contentelement">
                  <mm:related path="schrijver,users" orderby="schrijver.number" directions="DOWN" max="1"><mm:field name="users.account"/></mm:related>
               </mm:node>
            </td>
            <td valign="top" align="center">
               <mm:field name="contentelement.number" jspvar="contentElementNumber" vartype="String" write="false">
                  <mm:list nodes="<%= contentElementNumber %>" path="contentelement,creatierubriek,rubriek" max="1">
                     <mm:field name="rubriek.number" jspvar="rubriekNodeNumber" vartype="String" write="false">
                        <%
                        AuthorizationHelper authorizationHelper = new AuthorizationHelper(cloud);
                        Node userNode = authorizationHelper.getUserNode(cloud.getUser().getIdentifier());

                        UserRole userRole = authorizationHelper.getRoleForUser(userNode, cloud.getNode(rubriekNodeNumber));
                        if (userRole.getRol() > Roles.REDACTEUR) {
                           %>
                           <a href="version.jsp?node=<mm:field name='contentelement.number'/>"><img border="0" src="../img/versions.gif" width="16" height="16"></a>
                           <%
                        }
                        %>
                     </mm:field>
                  </mm:list>
               </mm:field>
            </td>
         </tr>
      <% } %>
      <mm:last>
         </table>
         <table border="0" cellpadding="0" cellspacing="0" width="876" height="73">
            <tr>
               <td colspan="2" align="right">
                 <mm:write referid="pagesString" />
               </td>
            </tr>
         </table>
      </mm:last>
   </mm:list>
   <%
   if (!foundResults) {
      %>
      <p><b>Er zijn geen content elementen gevonden</b></p>
      <%
   }
   Date end = new Date();
   log.info("Search for " + cloud.getUser().getIdentifier() + " took " + (end.getTime()-start.getTime())/1000 + " sec " + queryLog);
} // searchIsOn
%>
