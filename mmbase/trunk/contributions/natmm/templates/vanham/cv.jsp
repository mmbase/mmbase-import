<%@taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%> 
<%@include file="includes/templateheader.jsp" %>
<mm:cloud method="http" jspvar="cloud">
<mm:log jspvar="log">
<%@include file="includes/functions.jsp" %>
<mm:import externid="material" jspvar="materialTypeID" vartype="String">-1</mm:import>
<mm:import externid="orgtype" jspvar="organisationTypeID" vartype="String">-1</mm:import>
<mm:import externid="locatie" jspvar="locatieID" vartype="String">-1</mm:import>
<mm:import externid="projtype" jspvar="projectTypeID" vartype="String">-1</mm:import>
<mm:import externid="dur" jspvar="durationType" vartype="String">-1</mm:import>
<html>
<head>
<title>VAN HAM - CV</title>
<link rel="stylesheet" type="text/css" href="css/website.css">
<style>
td { 
   width: 30px;
   height: 30px;
   font-size: 140%;
   text-align: center;
} 
</style>
</head>
<body>
  <script language="javascript" src="scripts/launchcenter.js"></script>
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
<mm:node number="$paginaID" notfound="skip">
	<% 
	if (projectTypeID.equals("-1")) { 
		%>
      <mm:related path="contentrel,projects,posrel,projecttypes" fields="projecttypes.number" max="1">
         <mm:field name="projecttypes.number" jspvar="dummy" vartype="String">
            <% projectTypeID = dummy; %>
         </mm:field>
      </mm:related>
		<%
   } 

   String localPath = "";
   String searchUrl = localPath + "cv.jsp?p=" + paginaID
                   + "&material=" + materialTypeID
                   + "&orgtype=" + organisationTypeID
                   + "&locatie=" + locatieID
                   + "&projtype=" + projectTypeID
                   + "&dur=" + durationType
                   + "&language=" + language;

   String sProjects = "";
   // ** first determine the projects that fit the search criteria
   sProjects = getObjects(cloud,log,sProjects,"projects","contentrel","pagina",paginaID);
   if (checkParam(projectTypeID)) {
      sProjects = getObjects(cloud,log,sProjects,"projects","posrel","projecttypes",projectTypeID);
   }
   if (checkParam(materialTypeID)) {
      sProjects = getObjects(cloud,log,sProjects,"projects","posrel,items,posrel","item_pools",materialTypeID);
   }
   if (checkParam(locatieID)) {
      // wrong: sProjects = getObjects(cloud,log,sProjects,"projects","readmore","organisatie",locatieID);
      String locatieConstraint = "( organisatie.plaatsnaam LIKE '" + cloud.getNode(locatieID).getStringValue("plaatsnaam")
                               + "' AND organisatie.land LIKE '" + cloud.getNode(locatieID).getStringValue("land") + "')";
      sProjects = getObjectsConstraint(cloud,log,sProjects,"projects","projects,readmore,organisatie",locatieConstraint);
   }
   if (checkParam(organisationTypeID)) {
      sProjects = getObjects(cloud,log,sProjects,"projects","readmore,organisatie,posrel","organisatie_type",organisationTypeID);
   }
   if (checkParam(durationType)) {
      Calendar durationCal = Calendar.getInstance();
      durationCal.set(2037,0,1);
      String durationConstraint = "projects.enddate <" + (durationCal.getTimeInMillis()/1000); // temp
      if (durationType.equals("perm")) {
         durationConstraint = "NOT (" + durationConstraint + ")";
      }
      sProjects = getObjectsConstraint(cloud,log,sProjects,"projects","projects",durationConstraint);
   }
%>
<tr>
   <td></td>
   <td></td>
   <td></td>
   <td colspan="21">
      <table cellpadding="3" cellspacing="0" border="1"  class="content">
      <tr class="cv">
         <td style="width:14%;"></td>
         <td class="def" style="width:1%;"></td>
         <form name="selectform" method="post" action="">
            <td class="def" style="width:55%;vertical-align:middle;" colspan="2">
               <%
                  NodeList projectTypes = getRelated(cloud,log,paginaID,"pagina",
                     "contentrel,projects,posrel",
                     "projecttypes","name","",language);
               %>
               <%= getSimpleSelect(cloud,log,projectTypeID,projectTypes,
                     "projecttypes","name","",searchUrl,"projtype",language) %>
            </td>
         </form>
         <td class="def" style="width:30%;vertical-align:middle;">
           <mm:node number="<%=projectTypeID %>" jspvar="dummy">
             <%= getField(dummy,"subtitle",language) %>
           </mm:node>
         </td>
      </tr>
      <tr class="cv_sub">
         <td style="width:14%;"></td>
         <td class="def" style="width:1%;height:0%;"></td>
         <td class="def" style="width:25%;height:0%;vertical-align:middle;">
            <bean:message bundle="<%= "VANHAM." + language %>" key="cv.material" />
         </td>         
         <form name="selectform" method="post" action="">
         <td class="def" style="width:20%;padding:1px;height:0%;text-align:right;">
            <%
               NodeList materialTypes = getRelated(cloud,log,paginaID,"pagina",
                  "contentrel,projects,posrel,items,posrel",
                  "item_pools","name","",language);
            %>
            <%= getSimpleSelect(cloud,log,materialTypeID,materialTypes,
                  "item_pools","name","",searchUrl,"material",language) %>
         </td>
         </form>
         <td class="def" style="width:30%;vertical-align:middle;"></td>
      </tr>
      <tr class="cv_sub">
         <td style="width:14%;"></td>
         <td class="def" style="width:1%;height:0%;"></td>
         <td class="def" style="width:25%;height:0%;vertical-align:middle;">
            <bean:message bundle="<%= "VANHAM." + language %>" key="cv.location" />
         </td>         
         <form name="selectform" method="post" action="">
         <td class="def" style="width:20%;padding:1px;height:0%;text-align:right;">
            <%
               NodeList locatieList = getRelated(cloud,log,paginaID,"pagina",
                  "contentrel,projects,readmore",
                  "organisatie","plaatsnaam","land","nl");
            %>
            <%= getSimpleSelect(cloud,log,locatieID,locatieList,
                  "organisatie","plaatsnaam","land",searchUrl,"locatie","nl") %>
         </td>
         </form>
         <td class="def" style="width:30%;vertical-align:middle;"></td>
      </tr>
      <tr class="cv_sub">
         <td style="width:14%;"></td>
         <td class="def" style="width:1%;height:0%;"></td>
         <td class="def" style="width:25%;height:0%;vertical-align:middle;">
            <bean:message bundle="<%= "VANHAM." + language %>" key="cv.stakeholder" />
         </td>         
         <form name="selectform" method="post" action="">
         <td class="def" style="width:20%;padding:1px;height:0%;text-align:right;">
            <%
               NodeList organisationTypes = getRelated(cloud,log,paginaID,"pagina",
                  "contentrel,projects,readmore,organisatie,posrel",
                  "organisatie_type","naam","",language);
            %>
            <%= getSimpleSelect(cloud,log,organisationTypeID,organisationTypes,
                  "organisatie_type","naam","",searchUrl,"orgtype",language) %>
         </td>
         </form>
         <td class="def" style="width:30%;vertical-align:middle;"></td>
      </tr>
      <tr class="cv_sub">
         <td style="width:14%;"></td>
         <td class="def" style="width:1%;height:0%;"></td>
         <td class="def" style="width:25%;height:0%;vertical-align:middle;">
            <bean:message bundle="<%= "VANHAM." + language %>" key="cv.length" />
         </td>         
         <form name="selectform" method="post" action="">
         <td class="def" style="width:20%;padding:1px;height:0%;text-align:right;">
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
               <% 
					if ("temp".equals(durationType)) { 
						%>
                  <option value="<%= durationUrl + "&dur=temp" %>" selected>TIJDELIJK</option>
						<%
					} else {
						%>
                  <option value="<%= durationUrl + "&dur=temp" %>">TIJDELIJK</option>
						<%
					}
               if ("perm".equals(durationType)) { 
               	%>
                  <option value="<%= durationUrl + "&dur=perm" %>" selected>PERMANENT</option>
						<%
					} else {
						%>
                  <option value="<%= durationUrl + "&dur=perm" %>">PERMANENT</option>
						<%
					} %>
            </select>
         </td>
         </form>
         <td class="def" style="width:30%;vertical-align:middle;"></td>
      </tr>
      <tr class="cv_sub">
         <td style="width:14%;"></td>
         <td class="def" style="width:1%;height:0%;"></td>
         <td class="def" style="width:25%;height:0%;vertical-align:middle;">
            <bean:message bundle="<%= "VANHAM." + language %>" key="cv.bearer" />
         </td>         
         <form name="selectform" method="post" action="">
         <td class="def" style="width:20%;padding:1px;height:0%;text-align:right;">
            <select name="subpage" class="cv_sub" style="width:193px;">
               <option><bean:message bundle="<%= "VANHAM." + language %>" key="cv.select" /></option>
               <option>MUUR</option>
               <option>VLOER</option>
            </select>
         </td>
         </form>
         <td class="def" style="width:30%;vertical-align:middle;"></td>
      </tr>
      <tr>
         <td class="def" style="width:14%;height:20;"></td>
         <td class="def" style="width:1%;height:20;"></td>
         <td class="def" style="width:55%;" colspan="2"></td>
         <td class="def" style="width:30%;height:20;"></td>
      </tr>
      <% if (!sProjects.equals("")) { %>
         <mm:list nodes="<%= sProjects %>" path="projects" orderby="projects.begindate" directions="DOWN">
            <mm:node element="projects" jspvar="thisProject">
               <mm:import id="projectID" jspvar="projectID" vartype="String" reset="true"><mm:field name="number"/></mm:import>
               <%
                  String projectDesc = getField(cloud.getNode(projectID),"omschrijving", language);
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
                           if (beginYear != endYear) {
                              if (endDate.longValue() > now) {
                                 yearString += " - now";
                              } else {
                                 yearString += " - " + endYear;
                              }
                           }
                        %>
                        <td class="def" style="width:14%;"><%=yearString %></td>
                     </mm:field>
                  </mm:field>
                  <td class="def" style="width:1%;">
                     <% if (hasToggle) {  %>
                        <span onClick="toggle(<%=projectID %>);"><img src="media/plus.gif" style="margin-top:3px;" id="toggle_image<%=projectID %>" /></span>
                     <% } else { %>
                        &nbsp;
                     <% } %>
                  </td>
                  <td class="def" style="width:55%;" colspan="2"><%= getField(thisProject,"titel",language) %></td>
                  <mm:relatednodes type="organisatie" path="readmore,organisatie">
                     <td class="def" style="width:30%;"><mm:field name="titel" /></td>
                  </mm:relatednodes>
               </tr>
               <% if (hasToggle) {  %>
                  <tr id="toggle_div<%=projectID %>" style="display:none">
                     <td style="width:14%;"> </td>
                     <td class="def" style="width:1%;height:20;"> </td>
                     <td class="def" style="width:55%;line-height:120%;" colspan="2">
                        <%= projectDesc %><br/><br>
                        <mm:relatednodes type="items" path="posrel,items" jspvar="dummy" max="1">
                           <mm:field name="titel_zichtbaar" jspvar="titelFlag" vartype="String">
                              <% if (!"0".equals(titelFlag)) { %>
                                 <%= getField(dummy,"titel",language) %><br/>
                              <% } %>
                           </mm:field>
                           <mm:field name="year"><mm:compare value="<%= yearString %>" inverse="true"><mm:write /><br/></mm:compare></mm:field>
                           <%= getField(dummy,"material",language) %><br/>
                           <%= getField(dummy,"subtitle",language) %><br/>
									<mm:field name="piecesize"/><br/>
									<%= getField(dummy,"omschrijving",language) %><br/>
                        </mm:relatednodes>
                     </td>
                     <td class="def" style="width:30%;">
                        <mm:related path="posrel,items,posrel,images" max="1"
									><mm:node element="images" jspvar="dummy"
										><mm:field name="number" jspvar="imageID" vartype="String"><% 
											String jsString = "javascript:launchCenter('slideshow.jsp?i="
																 + imageID + "&language=" + language
																 + "', 'center', 550, 740);"; 
                                 %><div style="position:relative;left:185px;top:7px;"><div style="visibility:visible;position:absolute;top:0px;left:0px;"><% 
												%><a href="javascript:void(0);" onclick="<%= jsString %>"  alt="<bean:message bundle="<%= "VANHAM." + language %>" key="cv.click.to.enlarge" />"><img src="media/zoom.gif" border="0" /></a><%
											%></div></div><%
											%><a href="javascript:void(0);" onclick="<%= jsString %>" title="<bean:message bundle="<%= "VANHAM." + language %>" key="cv.click.to.enlarge" />"><% 
												%><img src="<mm:image template="s(207)" />" style="margin-bottom:8px;border-width:0px;" /><%
											%></a><%
										%></mm:field
									></mm:node
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


</body>
</html>
</mm:log>
</mm:cloud>