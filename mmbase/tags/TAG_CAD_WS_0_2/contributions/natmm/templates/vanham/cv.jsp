<%@include file="includes/templateheader.jsp" %>
<mm:cloud jspvar="cloud">
<mm:log jspvar="log">
  <%@include file="includes/functions.jsp" %>
  <script language="JavaScript" type="text/javascript">
    function toggle(number) {
      if( document.getElementById("toggle_div" + number).style.display=='none' ){
        document.getElementById("toggle_div" + number).style.display = '';
        document.getElementById("toggle_image" + number).src = "media/min.gif";
      } else {
        document.getElementById("toggle_div" + number).style.display = 'none';
        document.getElementById("toggle_image" + number).src = "media/plus.gif";
      }
    }
  
    function MM_jumpMenu(targ,selObj,restore){ //v3.0
      eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
      if (restore) selObj.selectedIndex=0;
    }
    
  </script>
  <table cellpadding="0" cellspacing="0" border="0" style="width:780px;">
  <%@include file="includes/nav.jsp" %>
  <% String pageUrl = ph.createPaginaUrl(paginaID,request.getContextPath()); %>
  <script language="JavaScript" type="text/javascript">
    function clearForm() {
      document.location = "<%= pageUrl %>?language=<%=language%>"; 
      return false; 
    }
  </script>
  <mm:node number="$paginaID" notfound="skip">
    <% 
     boolean debug = false;

     if(projectTypeID.equals("0")) {
      %>
      <mm:node number="commission">
        <mm:field name="number" jspvar="dummy" vartype="String" write="false">
          <% projectTypeID = dummy; %>
        </mm:field>
      </mm:node>
      <%
     }
     
     ListUtil lu = new ListUtil(cloud);
     
     String searchUrl = pageUrl
                     + "?material=" + materialTypeID
                     + "&orgtype=" + organisationTypeID
                     + "&locatie=" + locatieID
                     + "&projtype=" + projectTypeID
                     + "&dur=" + durationType
                     + "&language=" + language;
     
     if(debug) { %>searchUrl = <%= searchUrl %><br/><% }
     
     String sProjects = "";
     // ** first determine the projects that fit the search criteria
     sProjects = lu.getObjects(sProjects,"projects","contentrel","pagina",paginaID);
     if (checkParam(projectTypeID)) {
        sProjects = lu.getObjects(sProjects,"projects","posrel","projecttypes",projectTypeID);
        if(debug) { %>projectType: <%= sProjects %><br/><% }
     }
     if (checkParam(materialTypeID)) {
        sProjects = lu.getObjects(sProjects,"projects","posrel,items,posrel","pools",materialTypeID);
        if(debug) { %>materialType: <%= sProjects %><br/><% }
     }
     if (checkParam(locatieID)) {
        // wrong: sProjects = lu.getObjects(sProjects,"projects","readmore","organisatie",locatieID);
        String locatieConstraint = "( organisatie.plaatsnaam = '" + cloud.getNode(locatieID).getStringValue("plaatsnaam")
                                 + "' AND organisatie.land = '" + cloud.getNode(locatieID).getStringValue("land") + "')";
        sProjects = lu.getObjectsConstraint(sProjects,"projects","projects,readmore,organisatie",locatieConstraint);
        if(debug) { %>locatieConstraint: <%= sProjects %><br/><% }
     }
     if (checkParam(organisationTypeID)) {
        sProjects = lu.getObjects(sProjects,"projects","readmore,organisatie,posrel","organisatie_type",organisationTypeID);
        if(debug) { %>organisationType: <%= sProjects %><br/><% }
     }
     if (checkParam(durationType)) {
        Calendar durationCal = Calendar.getInstance();
        durationCal.set(2037,0,1);
        String durationConstraint = "projects.enddate <" + (durationCal.getTimeInMillis()/1000); // temp
        if (durationType.equals("perm")) {
           durationConstraint = "NOT (" + durationConstraint + ")";
        }
        sProjects = lu.getObjectsConstraint(sProjects,"projects","projects",durationConstraint);
        if(debug) { %>durationConstraint: <%= sProjects %><br/><% }
     }

  %>
  
  <tr>
     <td></td>
     <td></td>
     <td></td>
     <td colspan="21">
        <table cellpadding="3" cellspacing="0" border="1"  class="content">
        <tr class="cv">
           <td style="width:14%;"><a href="cv_text.jsp?language=<%=language%>" target="_blank"><img src="media/print.gif" border="0" title="print"></a></td>
           <td class="def" style="width:1%;"></td>
           <form name="selectform" method="post" action="">
              <td class="def" style="width:55%;vertical-align:middle;" colspan="2">
                 <%
                    NodeList projectTypes = lu.getRelated(paginaID,"pagina",
                       "contentrel,projects,posrel",
                       "projecttypes","name","",language);
                 %>
                 <%= getSimpleSelect(projectTypeID,projectTypes,
                       "projecttypes","name","",searchUrl,"projtype",language) %>
              </td>
           </form>
           <td class="def" style="width:30%;vertical-align:middle;">
             <mm:node number="<%=projectTypeID %>" jspvar="dummy" notfound="skipbody">
               <%= LocaleUtil.getField(dummy,"subtitle",language) %>
             </mm:node>
           </td>
        </tr>
        <tr class="cv_sub">
           <td style="width:14%;"></td>
           <td class="def" style="width:1%;height:0%;"></td>
           <td class="def" style="width:25%;height:0%;vertical-align:middle;">
              <bean:message bundle="<%= "VANHAM." + language %>" key="cv.location" />
           </td>         
           <form name="selectform" method="post" action="">
           <td class="def" style="width:20%;padding:1px;height:0%;text-align:right;vertical-align:middle;">
              <%
                 NodeList locatieList = lu.getRelated(sProjects,"projects",
                    "readmore",
                    "organisatie","plaatsnaam","land","nl");
              %>
              <%= getSimpleSelect(locatieID,locatieList,
                    "organisatie","plaatsnaam","land",searchUrl,"locatie","nl") %>
           </td>
           </form>
           <td class="def" style="width:30%;vertical-align:bottom;" rowspan="4">
              <input type="button" name="submit" value="<bean:message bundle="<%= "VANHAM." + language %>" key="cv.back" />"
                style="font-size:0.9em;text-align:center;width:50px;height:20px;" onClick="javascript:history.go(-1);">
      		    <input type="button" name="submit" value="<bean:message bundle="<%= "VANHAM." + language %>" key="cv.reset" />"
                style="font-size:0.9em;text-align:center;width:50px;height:20px;" onClick="javascript:clearForm();"></td>
           </td>
        </tr>
        <tr class="cv_sub">
           <td style="width:14%;"></td>
           <td class="def" style="width:1%;height:0%;"></td>
           <td class="def" style="width:25%;height:0%;vertical-align:middle;">
              <bean:message bundle="<%= "VANHAM." + language %>" key="cv.stakeholder" />
           </td>         
           <form name="selectform" method="post" action="">
           <td class="def" style="width:20%;padding:1px;height:0%;text-align:right;vertical-align:middle;">
              <%
                 NodeList organisationTypes = lu.getRelated(sProjects,"projects",
                    "readmore,organisatie,posrel",
                    "organisatie_type","naam","",language);
              %>
              <%= getSimpleSelect(organisationTypeID,organisationTypes,
                    "organisatie_type","naam","",searchUrl,"orgtype",language) %>
           </td>
           </form>
        </tr>
        <tr class="cv_sub">
           <td style="width:14%;"></td>
           <td class="def" style="width:1%;height:0%;"></td>
           <td class="def" style="width:25%;height:0%;vertical-align:middle;">
              <bean:message bundle="<%= "VANHAM." + language %>" key="cv.material" />
           </td>         
           <form name="selectform" method="post" action="">
           <td class="def" style="width:20%;padding:1px;height:0%;text-align:right;vertical-align:middle;">
              <%
                 NodeList materialTypes = lu.getRelated(sProjects,"projects",
                    "posrel,items,posrel",
                    "pools","name","",language);
              %>
              <%= getSimpleSelect(materialTypeID,materialTypes,
                    "pools","name","",searchUrl,"material",language) %>
           </td>
           </form>
        </tr>
        <tr class="cv_sub">
           <td style="width:14%;"></td>
           <td class="def" style="width:1%;height:0%;"></td>
           <td class="def" style="width:25%;height:0%;vertical-align:middle;">
              <bean:message bundle="<%= "VANHAM." + language %>" key="cv.length" />
           </td>         
           <form name="selectform" method="post" action="">
           <td class="def" style="width:20%;padding:1px;height:0%;text-align:right;vertical-align:middle;padding-bottom:3px;">
              <select name="dur" class="cv_sub" style="width:193px;" onChange="MM_jumpMenu('document',this,0)">
                 <%
                   String durationUrl = searchUrl;
                   int pPos = durationUrl.indexOf("dur");
                   if(pPos!=-1) {
                      int ampPos = durationUrl.indexOf("&",pPos);
                      if(ampPos==-1) {
                         durationUrl = durationUrl.substring(0,pPos);
                      } else {
                         durationUrl = durationUrl.substring(0,pPos) + durationUrl.substring(ampPos);
                      }
                   }
                 %>
                 <option value="<%= durationUrl %>"><bean:message bundle="<%= "VANHAM." + language %>" key="cv.select" /></option>
                 <option value="<%= durationUrl + "&dur=temp" %>" <%= ( "temp".equals(durationType) ? "selected" : "" ) %>>
                     <bean:message bundle="<%= "VANHAM." + language %>" key="cv.temporarily" />
                 </option>
                 <option value="<%= durationUrl + "&dur=perm" %>" <%= ( "perm".equals(durationType) ? "selected" : "" ) %>>
                     <bean:message bundle="<%= "VANHAM." + language %>" key="cv.permanent" />
                 </option>
              </select>
           </td>
           </form>
        </tr>
        <% if (!sProjects.equals("")) { 
           %>
           <mm:list nodes="<%= sProjects %>" path="projects" orderby="projects.begindate" directions="DOWN">
              <mm:node element="projects" jspvar="thisProject">
                 <mm:import id="projectID" jspvar="projectID" vartype="String" reset="true"><mm:field name="number"/></mm:import>
                 <%
                    String imagesList = "";
                    String projectDesc = LocaleUtil.getField(cloud.getNode(projectID),"omschrijving", language);
                    boolean hasToggle = !projectDesc.equals("");
                 %>
                 <mm:relatednodes type="items" path="posrel,items" jspvar="dummy" max="1">
                    <% hasToggle = true; %>
                 </mm:relatednodes>
                 <tr>
                 <% String yearString = ""; %>
                    <mm:field name="begindate" jspvar="beginDate" vartype="Long">
                       <mm:field name="enddate" jspvar="endDate" vartype="Long">
                          <% Calendar cal = Calendar.getInstance();
                             long now = cal.getTimeInMillis()/1000;
                             cal.setTimeInMillis(beginDate.longValue() * 1000);
                             int beginYear = cal.get(Calendar.YEAR);
                             cal.setTimeInMillis(endDate.longValue() * 1000);
                             int endYear = cal.get(Calendar.YEAR);
                             yearString += "" + beginYear;
                             if (beginYear != endYear && endDate.longValue() <= now) {
                                   yearString += " - " + endYear;
                             }
                          %>
                          <td class="def" style="width:14%;">
                            <%= (hasToggle?"<span onClick='toggle("+projectID+");'>":"") + yearString + (hasToggle?"</span>":"") %></td>
                       </mm:field>
                    </mm:field>
                    <td class="def" style="width:1%;">
                       <% if (hasToggle) {  %>
                          <span onClick="toggle(<%=projectID %>);"><img src="media/plus.gif" style="margin-top:3px;" id="toggle_image<%=projectID %>" /></span>
                       <% } else { %>
                          &nbsp;
                       <% } %>
                    </td>
                    <td class="def" style="width:55%;" colspan="2">
                       <%= (hasToggle?"<span onClick='toggle("+projectID+");'>":"") 
                        + LocaleUtil.getField(thisProject,"titel",language)
                        + (hasToggle?"</span>":"") %>
                    </td>
                    <td class="def" style="width:30%;">
                       <mm:relatednodes type="organisatie" path="readmore,organisatie" jspvar="dummy">
                         <%=(hasToggle?"<span onClick='toggle("+projectID+");'>":"")%><%= LocaleUtil.getField(dummy,"naam",language, "") %><%=(hasToggle?"</span>":"")%>
                       </mm:relatednodes>
                    </td>
                 </tr>
                 <% if (hasToggle) {  %>
                    <tr id="toggle_div<%=projectID %>" style="display:none">
                       <td style="width:14%;"> </td>
                       <td class="def" style="width:1%;height:20;"> </td>
                       <td class="def" style="width:55%;line-height:120%;" colspan="2">
                          <%= (!HtmlCleaner.cleanText(projectDesc,"<",">","").trim().equals("") ? projectDesc : "" ) %>
                          <mm:relatednodes type="items" path="posrel,items" jspvar="dummy" max="1">
                             <mm:field name="titel_zichtbaar" jspvar="titelFlag" vartype="String">
                                <% if (!"0".equals(titelFlag) && ! LocaleUtil.getField(dummy,"titel",language).equals(LocaleUtil.getField(thisProject,"titel",language)) ) { %>
                                   <%= LocaleUtil.getField(dummy,"titel",language, "<br/>") %>
                                <% } %>
                             </mm:field>
                             <mm:field name="year"><mm:compare value="<%= yearString %>" inverse="true"><mm:write /><br/></mm:compare></mm:field>
                             <%= LocaleUtil.getField(dummy,"material",language, "<br/>") %>
                             <%= LocaleUtil.getField(dummy,"subtitle",language, "<br/>") %>
                             <mm:field name="piecesize"/><br/><br/>
                             <%= LocaleUtil.getField(dummy,"omschrijving",language, "<br/>") %>
                          </mm:relatednodes>
                       </td>
                       <td class="def" style="width:30%;">
                         <mm:related path="posrel1,items,posrel2,images" orderby="posrel1.pos,posrel2.pos" directions="DOWN,DOWN">
                         <mm:field name="images.number" jspvar="dummy" vartype="String" write="false">
                               <% if(imagesList.equals("")) {
                                    imagesList = dummy;
                                  } else {
                                    imagesList = dummy + "," + imagesList;
                                  }
                               %>
                            </mm:field
                            ><mm:last
                               ><mm:node element="images" jspvar="dummy"
                                  ><mm:field name="number" jspvar="imageID" vartype="String"><% 
                                  String jsString = "javascript:launchCenter('/vanham/slideshow.jsp?i="
                                     + imagesList + "&language=" + language
                                     + "', 'center', 784, 784, 'resizable=1,scrollbars=1');setTimeout('newwin.focus();',250);"; 
                                  %><div style="position:relative;left:185px;top:7px;"><div style="visibility:visible;position:absolute;top:0px;left:0px;"><% 
                                  %><a href="javascript:void(0);" onclick="<%= jsString %>"  alt="<bean:message bundle="<%= "VANHAM." + language %>" key="cv.click.to.enlarge" />"><img src="media/zoom.gif" border="0" /></a><%
                                  %></div></div><%
                                  %><a href="javascript:void(0);" onclick="<%= jsString %>" title="<bean:message bundle="<%= "VANHAM." + language %>" key="cv.click.to.enlarge" />"><% 
                                  %><img src="<mm:image template="s(207)" />" style="margin-bottom:8px;border-width:0px;" /><%
                                  %></a><%
                                  %></mm:field
                               ></mm:node
                            ></mm:last
                         ></mm:related
                       ></td>
                    </tr>
                 <% } %>
              </mm:node>
           </mm:list>
        <% } %>
        </table>
     </td>
  </tr>
  </mm:node>
  </table>
</mm:log>
</mm:cloud>
<%@include file="includes/templatefooter.jsp" %>