<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<%@include file="include/inc_language.jsp" %>
<%@include file="include/inc_initdate.jsp" %>
<%@include file="include/inc_windowwidth.jsp" %>
<%@include file="include/inc_projectselection.jsp" %>

<% String pageId = request.getParameter("page") ;
   if (pageId == null ) pageId = "";
   if (!pageId.equals("")){
   String selectedCategory = request.getParameter("iart") ;
   if (selectedCategory == null) selectedCategory = "";
%>

<%-- following piece of code depends on page, selectedCategory, language and windowWidth --%>
<% String cacheKey = "cv" + selectedCategory + "_" + pageId + "_"  + "_" + language + "_" + windowWidth; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application"><!-- <%= new java.util.Date() %> -->

<mm:cloud>

<%	String lang = "";
	if (language.equals("english")){
		lang = "_eng";
	} else if (language.equals("french")) {
		lang = "_fra";
	}
	String selectedCategoryDescription = "";
%>
							
<table width="<%= windowWidth %>" cellpadding="0" cellspacing="0" border="0">
 <tr>
  <td valign="top" align="center">
  
  <%-- begin inner table with page navigation --%>
   <table width="<%= windowWidth-100 %>" cellpadding="0" cellspacing="0" border="0">
    <tr>
	 <td valign="top" width="<%= (windowWidth -100)/2 %>" class="background">
     <%-- list the categories --%>
	 	<% int categoryCount = -1; %>
		<mm:list nodes="<%= pageId %>"
			path="pagina,posrel,projecttypes"
			fields="posrel.pos,projecttypes.description"
			orderby="posrel.pos" directions="UP">
			<mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false" >
			<% if(Integer.parseInt(posrel_pos)!=categoryCount) {
					categoryCount=Integer.parseInt(posrel_pos); %>
					<mm:field name="projecttypes.name" jspvar="projecttypes_title" vartype="String" write="false">
					<%-- if selectedCategory is undefined, select the first category from the list --%>
					<% if(selectedCategory.equals("")) selectedCategory = projecttypes_title; %>
					<table cellpadding="0" cellspacing="0" border="0"><tr>
						<td class="background" align="left" valign="top"><IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name=pijl<%= categoryCount %>>&nbsp;</td>
						<td class="background">
						<A onmouseover="changeImages('pijl<%= categoryCount %>', 'media/arrow_right_dr.gif'); window.status=''; return true;"
						   onmouseout="changeImages('pijl<%= categoryCount %>', 'media/arrow_right_lg.gif'); window.status=''; return true;"
						   target="_top" href="index.jsp?page=<%= pageId %>&iart=<%= projecttypes_title %>"
						<% if(projecttypes_title.equals(selectedCategory)) { %>
							class="dark_boldlink"
						<% } else { %>
							class="light_boldlink"
						<% } %>
						>
						<mm:field name="projecttypes.description" jspvar="projecttypes_description" vartype="String" write="false">
							<%= lan(language,projecttypes_description) %>
							<% if(projecttypes_title.equals(selectedCategory)) {
								selectedCategoryDescription = lan(language,projecttypes_description);
							} %>
						</mm:field>
						</A></td>
					</tr></table>
					</mm:field>
			<% } %>
			</mm:field>
		</mm:list>
		<% String selectedConstraint = "(projecttypes.name = '" + selectedCategory + "') AND (projects.titel_zichtbaar = '1') "; %>
		<br><br>
		<table cellpadding="0" cellspacing="0" border="0"><tr>
			<td class="background" align="left" valign="top"><IMG height=12 alt="" hspace=0 vspace=0 src="media/arrow_right_lg.gif" width=8 border=0  align="absmiddle" name="pijl_print">&nbsp;</td>
			<td class="background">
			<A onmouseover="changeImages('pijl_print', 'media/arrow_right_dr.gif'); window.status=''; return true;"
				onmouseout="changeImages('pijl_print', 'media/arrow_right_lg.gif'); window.status=''; return true;"
				class="light_boldlink" href="cv_text.jsp?page=<%= pageId %>" target="_blank">
				<%= lan(language,"printbare versie") %></A></td>
		</tr></table>
		<% if(selectedCategory.equals("Collectie")||selectedCategory.equals("Opleiding")) { %>
		<%-- back to previous page --%>
			<table cellpadding="0" cellspacing="0" border="0"><tr>
				<td class="background" align="left" valign="top"><IMG height=12 alt="" hspace=0 vspace=0 src="media/arrow_right_lg.gif" width=8 border=0  align="absmiddle" name="pijl_terug">&nbsp;</td>
				<td class="background">
				<A onmouseover="changeImages('pijl_terug', 'media/arrow_right_dr.gif'); window.status=''; return true;"
					onmouseout="changeImages('pijl_terug', 'media/arrow_right_lg.gif'); window.status=''; return true;"
					class="light_boldlink" href="javascript:history.go(-1);">
					<%= lan(language,"terug") %></A></td>
			</tr></table>
		<% } %>
	</td>
	<td valign="top" width="<%= (windowWidth -100)/2 %>" class="background">
	<%-- list the projects/contacts that fall in the selected category --%>
 	<table cellpadding="0" cellspacing="0" border="0"><tr>
	<td class="background" align="left" valign="top"><IMG height=8 alt="" hspace=2 vspace=3 src="media/arrow_down_dg.gif" width=10 align=absMiddle border=0>&nbsp;</td>
	<td class="background">
	<span class="bold">
		<%= selectedCategoryDescription %> <%= selection(selectedCategory,language) %>
	</span></td>
	</tr></table>
	<% if(selectedCategory.equals("Collectie")) { %>
	<%-- selected category consists of contacts --%>
	<% String constraint = "organisatie_type.naam = '" + selectedCategory + "' AND organisatie.titel_zichtbaar = '1'"; %>
	<mm:list path="pagina,contentrel,organisatie,posrel,organisatie_type"
		fields="organisatie.number,organisatie.naam,organisatie.titel_zichtbaar,pagina.titel"
		constraints="<%= constraint %>" orderby="organisatie.naam" directions="UP">
		<% String contact_number = ""; %>
		<mm:field name="organisatie.number" jspvar="dummy01" vartype="String" write="false">
			<% contact_number = dummy01; %>
		</mm:field>
		<mm:field name="pagina.titel" jspvar="page_title" vartype="String" write="false">
		<% if(page_title.equals("Links")) { %>
			<table cellpadding="0" cellspacing="0" border="0"><tr>
				<td class="background" align="left" valign="top"><IMG height=12 alt="" hspace=4 vspace=0 src="media/arrow_right_lg.gif" width=8 border=0  align="absmiddle" name=pijl_contact<%= contact_number %>>&nbsp;</td>
				<td class="background">
				<A onmouseover="changeImages('pijl_contact<%= contact_number %>', 'media/arrow_right_dr.gif'); window.status=''; return true;"
				   onmouseout="changeImages('pijl_contact<%= contact_number %>', 'media/arrow_right_lg.gif'); window.status=''; return true;"
				   href="index.jsp?page=links&<mm:field name="organisatie.number" />" target="_top" class="light_boldlink">				
					<mm:field name="organisatie.naam" /></A></td>
			</tr></table>
		<% } else { %>
			<table cellpadding="0" cellspacing="0" border="0"><tr>
			<td class="background" align="left" valign="top"><IMG height=12 alt="" hspace=4 vspace=0 src="media/arrow_right_lg.gif" width=8 border=0  align="absmiddle">&nbsp;</td>
			<td class="light"><mm:field name="organisatie.naam" /></td>
			</tr></table>
		<% } %>
		</mm:field>
	</mm:list>
	<% } else { %>
		<%-- selected category consists of projects --%>
		<% String constraints = ""; %>
		<%-- add all projects related to categories which have the same position in the list --%>
		<mm:list nodes="<%= pageId %>"
			path="pagina,posrel1,projecttypes,posrel2,projects"
			constraints="<%= selectedConstraint %>"
			orderby="projects.embargo" directions="DOWN">
			<mm:node element="projects">
				<% String project_number = ""; %>
				<mm:field name="number" jspvar="dummy02" vartype="String" write="false">
				<% project_number = dummy02; %>		
				</mm:field>
				<%-- look for translation of title --%>
				<%@include file="include/project_titel.jsp" %>
			<% if(selectedCategory.equals("Opleiding")) { %>
				<% int fromYear = 0; %>
				<mm:field name="embargo" jspvar="project_embargo" vartype="String" write="false">
					<% timeStamp = project_embargo; %>
					<%@ include file="include/inc_date.jsp" %> 
					<% fromYear = thisYear; %>		
				</mm:field>
				<% int toYear = 0; %>
				<mm:field name="verloopdatum" jspvar="project_verloopdatum" vartype="String" write="false">
					<% timeStamp = project_verloopdatum; %>
					<%@ include file="include/inc_date.jsp" %> 
					<% toYear = thisYear; %>							
				</mm:field>
		  <%  if( fromYear < toYear) { project_title = " - " + toYear + " " + project_title; }
			   project_title = fromYear + project_title;
			   boolean opleidingHasContact = false;%>
				<mm:related path="readmore,organisatie,contentrel,pagina"
					constraints="pagina.titel='Links'"
					fields="organisatie.number,organisatie.naam" max="1">
					<table cellpadding="0" cellspacing="0" border="0"><tr>
					<td class="background" align="left" valign="top"><IMG height=12 alt="" hspace=4 vspace=0 src="media/arrow_right_lg.gif" width=8 border=0  align="absmiddle" name=pijl_project<%= project_number %>>&nbsp;</td>
					<td class="background">
						<A onmouseover="changeImages('pijl_project<%= project_number %>', 'media/arrow_right_dr.gif'); window.status=''; return true;"
						   onmouseout="changeImages('pijl_project<%= project_number %>', 'media/arrow_right_lg.gif'); window.status=''; return true;"
						   href="index.jsp?page=links&<mm:field name="organisatie.number" />" target="_top"  class="light_boldlink">
						<%= project_title %></A></td>
					</tr></table>
					<% opleidingHasContact = true; %>
		 		</mm:related>
				<% if(!opleidingHasContact) { %>
					<table cellpadding="0" cellspacing="0" border="0"><tr>
					<td class="background" align="left" valign="top"><IMG height=12 alt="" hspace=4 vspace=0 src="media/arrow_right_lg.gif" width=8 border=0  align="absmiddle">&nbsp;</td>
					<td class="light"><%= project_title %></td>
					</tr></table>
				<% } %>
			<% } else { %> <%-- project which is not in category opleiding --%>
				<table cellpadding="0" cellspacing="0" border="0"><tr>
				<td class="background" align="left" valign="top"><IMG height=12 alt="" hspace=4 vspace=0 src="media/arrow_right_lg.gif" width=8 border=0  align="absmiddle" name=pijl_project<%= project_number %>>&nbsp;</td>
				<td class="background">
				<A onmouseover="changeImages('pijl_project<%= project_number %>', 'media/arrow_right_dr.gif'); window.status=''; return true;"
				   onmouseout="changeImages('pijl_project<%= project_number %>', 'media/arrow_right_lg.gif'); window.status=''; return true;"
				   href="#<%= project_number %>" class="light_boldlink">				
					<% if(selectedCategory.equals("Solotentoonstelling")
							||selectedCategory.equals("Groepstentoonstelling")
							||selectedCategory.equals("Publicatie")) { %>
							<mm:field name="embargo" jspvar="project_embargo" vartype="String" write="false">
								<% timeStamp = project_embargo; %>
								<%@ include file="include/inc_date.jsp" %> 
								<%= thisYear %>
							</mm:field>
					<% } %>
					<%= project_title %></A></td>
				</tr></table>
			<% } %>
		</mm:node>
	</mm:list>
	
	<% } %>
 	</td>
   </tr>
   <tr>
	<td valign="top" width="<%= (windowWidth -100)/2 %>" class="background">
		<img src="media/spacer.gif" width="<%= (windowWidth -100)/2 %>" height="1">
	</td>
	<td valign="top" width="<%= (windowWidth -100)/2 %>" class="background">
		<img src="media/spacer.gif" width="<%= (windowWidth -100)/2 %>" height="1">
	</td>
   </tr>
</table><br>
<%-- end inner table with page navigation --%>


<%-- list projects/contacts in the selected category --%>
<% if(selectedCategory.equals("Collectie") || selectedCategory.equals("Opleiding")) { %>

<%-- contacts in category collection and projects in category opleiding do not get a description --%>

<% } else { %>
<% String constraints = ""; %>
<%-- add all projects related to categories which have the same position in the list --%>
<mm:list nodes="<%= pageId %>"
			path="pagina,posrel1,projecttypes,posrel2,projects"
			constraints="<%= selectedConstraint %>"
			orderby="projects.embargo" directions="DOWN">
	<mm:node element="projects">		
	<% String project_number = ""; %>
		<mm:field name="number" jspvar="dummy04" vartype="String" write="false">
		<% project_number = dummy04; %>
		</mm:field>
		<%-- look for translation --%>
		<%@include file="include/project_titel.jsp" %>
		<%@include file="include/project_subtitle.jsp" %>
		<%@include file="include/project_text.jsp" %>
	<% String project_embargo = ""; %>
		<mm:field name="embargo" jspvar="dummy08" vartype="String" write="false">
		<% project_embargo = dummy08; %>
		</mm:field>
		<% String project_verloopdatum = ""; %>
		<mm:field name="verloopdatum" jspvar="dummy09" vartype="String" write="false">
		<% project_verloopdatum = dummy09; %>
		</mm:field>
		
<%-- start inner table with project --%>
<a name="<%= project_number %>"></a>
<table width="<%= windowWidth-100 %>" cellpadding="3" cellspacing="0" border="0">
      <tr> 
        <%-- show image related to project if any --%>
		<td width="160" valign="top" align="center" class="background">
		<% String imagesList = ""; %>
		<% String images_number = ""; %>
			<mm:related path="posrel,images"
				constraints="images.titel_zichtbaar='1'"
				fields="images.number">
				<mm:field name="images.number" jspvar="dummy" vartype="String" write="false">
					<%	images_number = dummy; 
						if(imagesList.equals("")) { imagesList = images_number; }
						else { imagesList = images_number + "," +  imagesList ; }
					%>
				</mm:field>
			</mm:related>
		<% if(!imagesList.equals("")) { %>
				<a href="javascript:popup('image_gallery.jsp?imageId=<%= imagesList %>','popup<%= images_number %>','no')">
				<mm:node number="<%= images_number %>">
					<img src=<mm:image template="s(160)" /> width="160" border="0" alt="<%= lan(language,"Klik voor afbeelding op orginele grootte") %>"></a></td>
				</mm:node>				
		<% } else { %>
				-<br>
				<img src="media/spacer.gif" width="160" height="1" border="0" alt=""></td>
        <% } %>

			<td width="10" valign="top" align="center" class="background">
				<img src="media/spacer.gif" width="10" height="1" border="0" alt="">
			</td>
        
			<%-- project description --%>
			<td valign="top" class="background">
			<span class="bold">
		
			<% if(selectedCategory.indexOf("tentoonstelling")>0) { %>
				<% timeStamp = project_embargo; %>
				<%@ include file="include/inc_date.jsp" %> 
				<%= thisDay %>&nbsp;<%= monthsStr[thisMonth] %>&nbsp;<%= thisYear %>
				<% timeStamp = project_verloopdatum; %>
				<%@ include file="include/inc_date.jsp" %> 
				<%= lan(language,"t/m") %> <%= thisDay %>&nbsp;<%= monthsStr[thisMonth] %>&nbsp;<%= thisYear %><br>
			<% } else { %>
				<% timeStamp = project_embargo; %>
				<%@ include file="include/inc_date.jsp" %> 
				<% int fromYear = thisYear; %>		
				<% timeStamp = project_verloopdatum; %>
				<%@ include file="include/inc_date.jsp" %> 
				<% int toYear = thisYear; %>
				<%= fromYear %>
				<% if( fromYear < toYear) { %>
						- <%= toYear %>
				<% } %>
			<% } %>
			<%= project_title %>
			</span>
			<br>
			<% if(project_subtitle!=null&&!project_subtitle.equals("")) { %> 
				<%= project_subtitle %><br>
			<% } %>
			<br>
			<% if(project_text!=null&&!project_text.equals("")) { %>  
				<%= project_text %><br><br>
			<% } %>
  	  		<%-- list the links from this project to other parts of the site --%>
			<%-- articles --%>
			<% String articleConstraint = "artikel.titel_zichtbaar='1'"; %>
			<mm:related path="posrel,artikel"
				   orderby="posrel.pos" directions="UP" max="1"
				   constraints="<%= articleConstraint %>" fields="artikel.number">
		  		<mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false">
				<A onmouseover="changeImages('pijl<%= project_number %>_article', 'media/arrow_right_dr.gif'); window.status=''; return true;"
		         onmouseout="changeImages('pijl<%= project_number %>_article', 'media/arrow_right_lg.gif'); window.status=''; return true;"
				 class="light_boldlink" href="javascript:popup_small(
				 	'article_browser.jsp?project=<%= project_number %>&article=<%= article_number %>',
					'popup<%= article_number %>',
					'yes')">
			  	<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name="pijl<%= project_number %>_article">&nbsp;<%= lan(language,"Tekst van de publicatie") %>
				</A><br>
				</mm:field>
			</mm:related>
			<%-- related projects --%>
			<mm:related path="posrel1,projects2,posrel2,projecttypes"
				   orderby="posrel1.pos" directions="UP">
				<A onmouseover="changeImages('pijl<%= project_number %>_relproject', 'media/arrow_right_dr.gif'); window.status=''; return true;"
				 onmouseout="changeImages('pijl<%= project_number %>_relproject', 'media/arrow_right_lg.gif'); window.status=''; return true;"
				 class="light_boldlink" target="_top" href="index.jsp?page=cv&iart=<mm:field name="projecttypes.title" />&<mm:field name="projects2.number" />">
				<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name="pijl<%= project_number %>_relproject">&nbsp;<mm:field name="projecttypes.name" jspvar="projecttypes_title" vartype="String" write="false"><%= lan(language,projecttypes_title) %></mm:field> <mm:field name="projects2.titel" />
				</A><br>
			</mm:related>
		<%-- contacts --%>
   	   <mm:related path="readmore,organisatie,contentrel,pagina"
					   constraints="pagina.titel='Links'"
	  				   fields="organisatie.number,organisatie.naam" max="1">
	  		  <A onmouseover="changeDoubleImages('pijl<%= project_number %>_contact', 'media/arrow_right_dr.gif', 'nav_<%= lan(language,"Links") %>', 'media/arrow_down_dr.gif'); window.status=''; return true;"
	  	         onmouseout="changeDoubleImages('pijl<%= project_number %>_contact', 'media/arrow_right_lg.gif', 'nav_<%= lan(language,"Links") %>', 'media/spacer.gif'); window.status=''; return true;"
	  			 class="light_boldlink" target="_top" href="index.jsp?page=links&<mm:field name="organisatie.number" />">
	  		  	<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name="pijl<%= project_number %>_contact">&nbsp;<mm:field name="organisatie.naam" />
	  		  </A><br>
		 	</mm:related>
		 <%-- back to previous page --%>
		  <A onmouseover="changeImages('pijl<%= project_number %>_terug', 'media/arrow_right_dr.gif'); window.status=''; return true;"
	         onmouseout="changeImages('pijl<%= project_number %>_terug', 'media/arrow_right_lg.gif'); window.status=''; return true;"
			 class="light_boldlink" href="javascript:history.go(-1);">
		  <IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name="pijl<%= project_number %>_terug">&nbsp;<%= lan(language,"terug") %>
		  </A>
	     <%-- back to the top of the page --%>
		  <A onmouseover="changeImages('pijl<%= project_number %>_top', 'media/arrow_up_dr.gif'); window.status=''; return true;"
  	         onmouseout="changeImages('pijl<%= project_number %>_top', 'media/arrow_up_lg.gif'); window.status=''; return true;"
  			 class="light_boldlink" href="#top">
  		  <IMG height=12 alt="" hspace=0 src="media/arrow_up_lg.gif" width=8 align=absMiddle border=0 name="pijl<%= project_number %>_top">&nbsp;<%= lan(language,"top") %>
  		  </A><br>
		 <img src="media/spacer.gif" width="<%= windowWidth -100 -160 %>" height="1">
		</td>
      </tr>
    </table>
<%-- end inner table with project --%>
   </mm:node> 
</mm:list>
<%-- end list of projects in selected category --%>
<% } %>

</td></tr>
</table>

</mm:cloud>

</cache:cache>

<% } %>

