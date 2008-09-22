<%@page import="org.mmbase.util.logging.Logger" 
%><%@include file="/taglibs.jsp" 
%><mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" 
%><%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<mm:log jspvar="log">
<%@include file="includes/header.jsp" 
%><%@include file="includes/calendar.jsp" %>
<td><%@include file="includes/pagetitle.jsp" %></td>
  <td><% 
      String rightBarTitle = "Zoek een activiteit";
      %><%@include file="includes/rightbartitle.jsp" %>
   </td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>" id="infopage">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td style="padding:10px;padding-top:18px;">
      <%@include file="includes/back_print.jsp" %>
      
      <mm:node number="<%= paginaID %>">
        <mm:field name="omschrijving" jspvar="text" vartype="String" write="false">
      	<% 
         	if(text!=null && !HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { 
         	   %>
             <div class="black" style="margin-top: 20px !important; margin-bottom: 20px !important;">
               <mm:write />
             </div>
             <%
            } 
         %>
      	</mm:field>
      </mm:node>   
      
      <%
      ListUtil lu = new ListUtil(cloud);

      if(departmentId.equals("default")) {   departmentId = ""; }
      String sEvents = "";

      if(termSearchId.equals(defaultSearchText)) { termSearchId = ""; }
      boolean bSearchIsOn = !termSearchId.equals("")||!eTypeId.equals("")||!pCategorieId.equals("")||!pAgeId.equals("")||!nReserveId.equals("")
                           ||!eDistanceId.equals("")||!eDurationId.equals("")||!departmentId.equals("");

      String searchUrl = javax.servlet.http.HttpUtils.getRequestURL(request) + "?p=" + paginaID
				 + "&termsearch=" + termSearchId
         + "&evt=" + eTypeId
         + "&pc=" + pCategorieId
         + "&pa=" + pAgeId
         + "&nr=" + nReserveId
         + "&evl=" +  eDistanceId
         + "&evd=" + eDurationId
         + "&department=" + departmentId;

      if(bSearchIsOn) {
      
         // ** first determine the objects that fit the search term criteria
         if (!termSearchId.equals("")){
            String searchConstraint = "(( UPPER(evenement_blueprint.titel) LIKE '%" + termSearchId.toUpperCase() + "%') OR ( UPPER(evenement_blueprint.tekst) LIKE '%" + termSearchId.toUpperCase() + "%') ";
            NodeList nlObjects = cloud.getList("","evenement_blueprint","evenement_blueprint.number",searchConstraint,"evenement_blueprint.titel","UP",null,true);
            StringBuffer sbObjects = new StringBuffer();
            for(int n=0; n<nlObjects.size(); n++) {
               if(n>0) { sbObjects.append(','); }
               sbObjects.append(nlObjects.getNode(n).getStringValue("evenement_blueprint.number"));
            }
            sEvents = sbObjects.toString();
         }
         // then searching activitien using dropdowns only if no search term was entered or some activitien were found using search term
         if (termSearchId.equals("")||(!termSearchId.equals("")&&!sEvents.equals(""))){
            sEvents = lu.getObjects(sEvents,"evenement_blueprint","related","evenement_type",eTypeId);
            sEvents = lu.getObjects(sEvents,"evenement_blueprint","posrel", "deelnemers_categorie",pCategorieId);
            sEvents = lu.getObjects(sEvents,"evenement_blueprint","posrel", "deelnemers_age",pAgeId);
            sEvents = lu.getObjects(sEvents,"evenement_blueprint","related","natuurgebieden_type",nReserveId);
            sEvents = lu.getObjects(sEvents,"evenement_blueprint","related","evenement_distance",eDistanceId);
            sEvents = lu.getObjects(sEvents,"evenement_blueprint","related","evenement_duration",eDurationId);
            sEvents = lu.getObjects(sEvents,"evenement_blueprint","readmore","afdelingen",departmentId);
         }
      }

      NodeList eventTypes = null;
      NodeList participantsCategories = null;
      NodeList participantsAges = null;
      NodeList natureReserveTypes = null;
      NodeList evenementDistances = null;
      NodeList evenementDurations = null;
      NodeList departments = null;

      // *** add the objects that are still possible to the TreeSets
      if (termSearchId.equals("")||(!termSearchId.equals("")&&!sEvents.equals(""))){
         eventTypes = lu.getRelated(sEvents,"evenement_blueprint","related","evenement_type","naam");
         participantsCategories = lu.getRelated(sEvents,"evenement_blueprint","posrel", "deelnemers_categorie","naam");
         participantsAges = lu.getRelated(sEvents,"evenement_blueprint","posrel", "deelnemers_age","name");
         natureReserveTypes = lu.getRelated(sEvents,"evenement_blueprint","related","natuurgebieden_type","name");
         evenementDistances = lu.getRelated(sEvents,"evenement_blueprint","related","evenement_distance","name");
         evenementDurations = lu.getRelated(sEvents,"evenement_blueprint","related","evenement_duration","name");
         departments = lu.getRelated(sEvents,"evenement_blueprint","readmore","afdelingen","naam");
         
         eTypeId = lu.setSelected(eTypeId,eventTypes,"evenement_type.number");
         pCategorieId = lu.setSelected(pCategorieId,participantsCategories,"deelnemers_categorie.number");
         pAgeId = lu.setSelected(pAgeId,participantsAges,"deelnemers_age.number");
         nReserveId = lu.setSelected(nReserveId,natureReserveTypes,"natuurgebieden_type.number");
         eDistanceId = lu.setSelected(eDistanceId,evenementDistances,"evenement_distance.number");
         eDurationId = lu.setSelected(eDurationId,evenementDurations,"evenement_duration.number");
         departmentId = lu.setSelected(departmentId,departments,"afdelingen.number");
      }

		if (actionId.equals("feedback")){
         %><jsp:include page="includes/feedback/form.jsp">
            <jsp:param name="object" value="<%= eventId %>" />
            <jsp:param name="field" value="titel" />
            <jsp:param name="ntype" value="activiteit" />
            <jsp:param name="by" value="het organiserende Bezoekerscentrum" />
            <jsp:param name="param" value="ev" />
         </jsp:include><% 
      } else {

	   	if(!eventId.equals("")) {

			   %><%@include file="includes/event_blueprints/detail.jsp" %><%

			} else { 
			   if(bSearchIsOn) {
           %>
				   <%@include file="includes/event_blueprints/searchresults.jsp" %>
           <%
			   } else { 
            String startnodeId = articleId;
            String articlePath = "artikel";
            String articleOrderby = "";
            if(articleId.equals("-1")) { 
              startnodeId = paginaID;
              articlePath = "pagina,contentrel,artikel";
              articleOrderby = "contentrel.pos";
            } %>
            <mm:list nodes="<%= startnodeId %>"  path="<%= articlePath %>" orderby="<%= articleOrderby %>">
              <%@include file="includes/relatedarticle.jsp"%>
            </mm:list>
            <%@include file="includes/pageowner.jsp"%>
		        <% 
         } 
		   }
	   }
    %>
    </td>
</tr>
</table>
</div>
</td>
<td>
   <%@include file="includes/event_blueprints/searchform.jsp" %>
</td>
</mm:log>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>