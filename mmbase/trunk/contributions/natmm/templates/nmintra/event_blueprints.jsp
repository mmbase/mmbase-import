<%@page import="org.mmbase.util.logging.Logger" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<mm:log jspvar="log">
<%@include file="includes/metadatafunctions.jsp" %>
<%@include file="includes/header.jsp" %>
<%@include file="includes/calendar.jsp" %>
<td><%@include file="includes/pagetitle.jsp" %></td>
  <td><% 
      String rightBarTitle = "Zoek een activiteit";
      %><%@include file="includes/rightbartitle.jsp" %>
   </td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td style="padding:10px;padding-top:18px;">
    <%
      if(!postingStr.equals("|action=print")) {
        %><div align="right" style="letter-spacing:1px;"><a href="javascript:history.go(-1);">terug</a>&nbsp/&nbsp;<a target="_blank" href="ipage.jsp<%= 
                    templateQueryString %>&pst=|action=print">print</a></div><%
      } 
	  
      if(departmentId.equals("default")) {   departmentId = ""; }
      String sEvents = "";      
      boolean bSearchIsOn = !termSearchId.equals("")||!eTypeId.equals("")||!pCategorieId.equals("")||!pAgeId.equals("")||!nReserveId.equals("")
                           ||!eDistanceId.equals("")||!eDurationId.equals("")||!departmentId.equals("");

      String localPath = "";
      if(request.getRequestURI().indexOf("/editors/")!=-1) {
         localPath = "/dev/";
      }
      String searchUrl = localPath + "event_blueprints.jsp?p=" + paginaID
						 +	"&termsearch=" + termSearchId
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
            sEvents = getObjects(cloud,log,sEvents,"evenement_blueprint","related","evenement_type",eTypeId);
            sEvents = getObjects(cloud,log,sEvents,"evenement_blueprint","posrel", "deelnemers_categorie",pCategorieId);
            sEvents = getObjects(cloud,log,sEvents,"evenement_blueprint","posrel", "deelnemers_age",pAgeId);
            sEvents = getObjects(cloud,log,sEvents,"evenement_blueprint","related","natuurgebieden_type",nReserveId);
            sEvents = getObjects(cloud,log,sEvents,"evenement_blueprint","related","evenement_distance",eDistanceId);
            sEvents = getObjects(cloud,log,sEvents,"evenement_blueprint","related","evenement_duration",eDurationId);
            sEvents = getObjects(cloud,log,sEvents,"evenement_blueprint","readmore","afdelingen",departmentId);
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
         eventTypes = getRelated(cloud,log,sEvents,"evenement_blueprint","related","evenement_type","naam");
         participantsCategories = getRelated(cloud,log,sEvents,"evenement_blueprint","posrel", "deelnemers_categorie","naam");
         participantsAges = getRelated(cloud,log,sEvents,"evenement_blueprint","posrel", "deelnemers_age","name");
         natureReserveTypes = getRelated(cloud,log,sEvents,"evenement_blueprint","related","natuurgebieden_type","name");
         evenementDistances = getRelated(cloud,log,sEvents,"evenement_blueprint","related","evenement_distance","name");
         evenementDurations = getRelated(cloud,log,sEvents,"evenement_blueprint","related","evenement_duration","name");
         departments = getRelated(cloud,log,sEvents,"evenement_blueprint","readmore","afdelingen","naam");

			if(eTypeId.equals("")&&eventTypes.size()==1) { eTypeId = (String) eventTypes.getNode(0).getStringValue("evenement_type.number"); }
			if(pCategorieId.equals("")&&participantsCategories.size()==1) { pCategorieId = (String) participantsCategories.getNode(0).getStringValue("deelnemers_categorie.number"); }
			if(pAgeId.equals("")&&participantsAges.size()==1) { pAgeId = (String) participantsAges.getNode(0).getStringValue("deelnemers_age.number"); }
			if(nReserveId.equals("")&&natureReserveTypes.size()==1) { nReserveId = (String) natureReserveTypes.getNode(0).getStringValue("natuurgebieden_type.number");}
			if(eDistanceId.equals("")&&evenementDistances.size()==1) { eDistanceId = (String) evenementDistances.getNode(0).getStringValue("evenement_distance.number"); }
			if(eDurationId.equals("")&&evenementDurations.size()==1) { eDurationId = (String) evenementDurations.getNode(0).getStringValue("evenement_duration.number"); }
			if(departmentId.equals("")&&departments.size()==1) { departmentId = (String) departments.getNode(0).getStringValue("afdelingen.number"); }
      }

		if (actionId.equals("feedback")){
         %><jsp:include page="includes/feedback/form.jsp">
            <jsp:param name="object" value="<%= eventId %>" />
            <jsp:param name="field" value="naam" />
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
