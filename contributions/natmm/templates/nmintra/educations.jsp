<%-- hh: see the template event_blueprints.jsp for a more compact implementation --%>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%! 
public String searchResults(TreeSet searchResultList) {
	String searchResults = searchResultList.toString();
	searchResults = searchResults.substring(1,searchResults.length()-1);
	return searchResults;
}
%><%@include file="includes/header.jsp" 
%><%@include file="includes/calendar.jsp" 
%><td><%@include file="includes/pagetitle.jsp" %></td>
  <td><% 
      String rightBarTitle = "Zoek een opleiding";
      %><%@include file="includes/rightbartitle.jsp" %>
   </td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td style="padding:10px;padding-top:18px;">
    <% 
     
      postingStr += "|";
      String action = getResponseVal("action",postingStr);

	   boolean debug = false;
	  	TreeSet educations = new TreeSet();
		TreeSet keywords = new TreeSet();
		TreeSet educationPools = new TreeSet();
		TreeSet providers = new TreeSet();
		TreeSet competencies = new TreeSet();

      String sEducations = "";
      String searchConstraint = "";
      
      boolean bSearchIsOn = !keywordId.equals("")||!poolId.equals("")||!providerId.equals("")||!competenceId.equals("");
      String localPath = "";
      if(request.getRequestURI().indexOf("/editors/")!=-1) {
         localPath = "/dev/";
      }
      
      if(debug) { 
         %>
         Start search with:<br/>
         pool=<%= poolId %><br/>
         pr=<%= providerId %><br/>
         k=<%= keywordId %><br/>
         c=<%= competenceId %><br/>
         <%
      }
      if(bSearchIsOn) {
         // ** first determine the educations that fit the search criteria
   		if (!keywordId.equals("")){ 
   			searchConstraint = "(keywords.number = '" + keywordId + "')"; 	   
            %>
   			<mm:list path="educations,related,keywords" constraints="<%= searchConstraint %>">
   				<%@include file="includes/education/searcheducations.jsp" %>
   			</mm:list>
   	      <%
   	      sEducations = searchResults(educations);
   	      if(debug) { %>keyword: <%= sEducations %><br/><% }
         }
       
   		if (!poolId.equals("")){
   	      educations.clear();
   			searchConstraint = "(education_pools.number = '" + poolId + "')";
            %>
   			<mm:list nodes="<%= sEducations %>" path="educations,posrel,education_pools" constraints="<%= searchConstraint %>">
   				<%@include file="includes/education/searcheducations.jsp" %>
   			</mm:list>
   	      <%
   	      sEducations = searchResults(educations);
   	      if(debug) { %>pool: <%= sEducations %><br/><% }
         }

   		if (!providerId.equals("")){
   	      educations.clear();
   			searchConstraint = "(providers.number = '" + providerId + "')";
            %>
   			<mm:list nodes="<%= sEducations %>" path="educations,related,providers" constraints="<%= searchConstraint %>">
   				<%@include file="includes/education/searcheducations.jsp" %>
   			</mm:list>
   	      <%
   	      sEducations = searchResults(educations);
   	      if(debug) { %>providers: <%= sEducations %><br/><% }
         }

   		if (!competenceId.equals("")){
   	      educations.clear();
   			searchConstraint = "(competencies.number = '" + competenceId + "')";
            %>
   			<mm:list nodes="<%= sEducations %>" path="educations,posrel,competencies" constraints="<%= searchConstraint %>">
   				<%@include file="includes/education/searcheducations.jsp" %>
   			</mm:list>
   	      <%
   	      sEducations = searchResults(educations);
   	      if(debug) { %>competence: <%= sEducations %><br/><% }
         }
      }
      // *** add the objects that are still possible to the TreeSets
      int iEducations = 1;
      int cPos = sEducations.indexOf(",");
      while(cPos>-1) {
         cPos = sEducations.indexOf(",",cPos+1);
         iEducations++;
      }
      %>
		<mm:list nodes="<%= sEducations %>" path="educations,related,keywords">
   		<mm:field name="keywords.number" jspvar="keyword_number" vartype="String" write="false">
	         <% keywords.add(keyword_number); %>
         </mm:field>
		</mm:list>
		<mm:list nodes="<%= sEducations %>" path="educations,posrel,education_pools">
		   <mm:field name="education_pools.number" jspvar="education_pool_number" vartype="String" write="false">
         	<% educationPools.add(education_pool_number); %>
         </mm:field>
		</mm:list>
		<mm:list nodes="<%= sEducations %>" path="educations,related,providers">
		   <mm:field name="providers.number" jspvar="providers_number" vartype="String" write="false">
         	<% providers.add(providers_number); %>
         </mm:field>
		</mm:list>
		<mm:list nodes="<%= sEducations %>" path="educations,posrel,competencies">
			<mm:field name="competencies.number" jspvar="competencies_number" vartype="String" write="false">
			   <% competencies.add(competencies_number); %>
			</mm:field>
		</mm:list>
	   <% 
      if(debug) { 
         %>
         End search with:<br/>
         educations=<%= sEducations %>, count = <%= iEducations %><br/>
         pool=<%= educationPools %><br/>
         pr=<%= providers %><br/>
         k=<%= keywords %><br/>
         c=<%= competencies %><br/>
         <%
      }	
      // all educations should be related to the selected Id (if it is the only one)
      // otherwise the set size of educations will change, with the next click on offset
      // this check would not be necessary if the relation would be mandatory
      if(keywordId.equals("")&&keywords.size()==1) { 
         %>
         <mm:list nodes="<%= sEducations %>" path="educations,related,keywords"
            constraints="<%= "keywords.number='" + (String) keywords.first() + "'" %>">
            <mm:size jspvar="iKeywords" vartype="Integer" write="false">
               <% if(iEducations==iKeywords.intValue()) { keywordId = (String) keywords.first(); } %>
            </mm:size>
         </mm:list>
         <%
      }
		if(poolId.equals("")&&educationPools.size()==1) { 
         %>
         <mm:list nodes="<%= sEducations %>" path="educations,posrel,education_pools"
            constraints="<%= "education_pools.number='" + (String) educationPools.first() + "'" %>">
            <mm:size jspvar="iPools" vartype="Integer" write="false">
               <% if(iEducations==iPools.intValue()) { poolId = (String) educationPools.first(); } %>
            </mm:size>
         </mm:list>
         <%
      }
		if(providerId.equals("")&&providers.size()==1) {
         %>
   		<mm:list nodes="<%= sEducations %>" path="educations,related,providers"
   		    constraints="<%= "providers.number='" + (String) providers.first() + "'" %>">
            <mm:size jspvar="iProviders" vartype="Integer" write="false">
               <% if(iEducations==iProviders.intValue()) { providerId = (String) providers.first(); } %>
            </mm:size>
   		</mm:list>
         <%
      }
		if(competenceId.equals("")&&competencies.size()==1) {
         %>
   		<mm:list nodes="<%= sEducations %>" path="educations,posrel,competencies"
   		    constraints="<%= "competencies.number='" + (String) competencies.first() + "'" %>">
            <mm:size jspvar="iCompetencies" vartype="Integer" write="false">
               <% if(iEducations==iCompetencies.intValue()) { competenceId = (String) competencies.first(); } %>
            </mm:size>
   		</mm:list>
         <%
      }

      if(!postingStr.equals("|action=print")) {
        %><div align="right" style="letter-spacing:1px;"><a href="javascript:history.go(-1);">terug</a>&nbsp/&nbsp;<a target="_blank" href="ipage.jsp<%= 
                    templateQueryString+"&k="+keywordId+"&pool="+poolId+"&pr="+providerId+"&c="+competenceId %>&pst=|action=print">print</a></div><%
      } 
		
		if (actionId.equals("feedback")){
         %><jsp:include page="includes/feedback/form.jsp">
            <jsp:param name="object" value="<%= educationId %>" />
            <jsp:param name="field" value="titel" />
            <jsp:param name="ntype" value="opleiding" />
            <jsp:param name="by" value="de cursusleid(st)er" />
            <jsp:param name="param" value="e" />
         </jsp:include><% 
      } else {

	   	if(!educationId.equals("")) {

			   %><%@include file="includes/education/detail.jsp" %><%

			} else { 
			   if(bSearchIsOn) {
               %>
				   <%@include file="includes/education/searchresults.jsp"%>
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
   <% 
   if(!action.equals("print")) { 
      %>
      <%@include file="includes/education/searchform.jsp" %>
      <table cellpadding="0" cellspacing="0" width="100%";>
      <tr>
         <td style="text-align:right;padding-left:19px;padding-right:9px;">
            <span class="pageheader"><span style="color:#FFFFFF;">Fun</span></div>
         </td>
      </tr>
      </table>
      <%@include file="includes/whiteline.jsp" %>
      <div class="smoelenboeklist" style="height:176px;">
      <table cellpadding="0" cellspacing="0">
      <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,shorty">
         <tr>
             <td style="padding-left:20px;">
             <mm:node element="shorty"
               ><mm:related path="posrel,link" orderby="posrel.pos" directions="UP"
                 ><a target="_blank" href="<mm:field name="link.url" />" class="menuItem"><span class="normal"><mm:field name="link.titel" /></span></a><br>
               </mm:related
             ></mm:node>
            </td>
         </tr>
      </mm:list>
      </table>
      </div>
      <%
      } 
   %>
</td>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
