<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<%@include file="include/inc_language.jsp" %>
<%@include file="include/inc_initdate.jsp" %>
<%@include file="include/inc_windowwidth.jsp" %>

<%	String pageId = request.getParameter("page") ;
	if (pageId == null ) pageId = "";
	if (!pageId.equals("")){
%>
<%-- following piece of code depends on page, language and windowWidth --%>
<% String cacheKey = "agenda_" + pageId + "_" + language + "_" + windowWidth; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" ><!-- <%= new java.util.Date() %> -->

<mm:cloud>

<% String lang = "";
	if (language.equals("english")){
		lang = "_eng";
	} else if (language.equals("french")) {
		lang = "_fra";
	}
	%>

							
<table width="<%= windowWidth %>" cellpadding="0" cellspacing="0" border="0">
 <tr>
  <td valign="top" align="center">
  
  <%-- begin inner table with page navigation --%>
  <table width="<%= windowWidth -100 %>" cellpadding="0" cellspacing="0" border="0">
   	   	
		<%-- list the projects that fall in the future --%>
		<% int numberOfProjects = 0; %>
		<mm:list path="projects,posrel,projecttypes"
			constraints="(projecttypes.name='Groepstentoonstelling' OR projecttypes.name='Solotentoonstelling' OR projecttypes.name='Publicatie') AND projects.titel_zichtbaar='1'"
			orderby="projects.embargo" directions="DOWN">
			<mm:node element="projects">	
			<% String project_embargo = ""; %>
				<mm:field name="embargo" jspvar="dummy03" vartype="String" write="false">
				<% project_embargo = dummy03; %>
				</mm:field>
			<% if((numberOfProjects<4)||(Integer.parseInt(project_embargo)>(dd.getTime()/1000))){ %> 
				<% String project_number = ""; %>
				<mm:field name="number" jspvar="dummy02" vartype="String" write="false">
					<% project_number = dummy02; %>		
				</mm:field>
				<%-- look for translation of title --%>
				<%@include file="include/project_titel.jsp" %>
				<% String project_verloopdatum = ""; %>
				<mm:field name="verloopdatum" jspvar="dummy02" vartype="String" write="false">
				<% project_verloopdatum = dummy02; %>
				</mm:field>
				<tr>
					<td class="background" valign="top">
					<A onmouseover="changeImages('pijl<%= project_number %>', 'media/arrow_right_dr.gif'); window.status=''; return true;"
					   onmouseout="changeImages('pijl<%= project_number %>', 'media/arrow_right_lg.gif'); window.status=''; return true;"
					   href="#<%= project_number %>" class="light_boldlink">
					<IMG height=12 alt="" hspace=0 vspace=2 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name=pijl<%= project_number %>>&nbsp;<%= project_title %>
					<% timeStamp = project_embargo; %>
					<%@ include file="include/inc_date.jsp" %>
					(<%= thisDay %>&nbsp;<%= monthsStr[thisMonth] %>&nbsp;<%= thisYear %>
					<% timeStamp = project_verloopdatum; %>
					<%@ include file="include/inc_date.jsp" %> 
					<%= lan(language,"t/m") %> <%= thisDay %>&nbsp;<%= monthsStr[thisMonth] %>&nbsp;<%= thisYear %>)
					</A>
					</td>
				</tr>
				<% numberOfProjects++; %>
			<% } %>
			</mm:node>
		</mm:list>
	</table><br>
	<%-- end inner table with page navigation --%>
  
<%-- list projects that fall in the future --%>
<% numberOfProjects = 0; %>
<mm:list path="projects,posrel,projecttypes"
	constraints="(projecttypes.name='Groepstentoonstelling' OR projecttypes.name='Solotentoonstelling' OR projecttypes.name='Publicatie') AND projects.titel_zichtbaar='1'"
	orderby="projects.embargo" directions="DOWN">
	<mm:node element="projects">
	<% String project_embargo = ""; %>
	<mm:field name="embargo" jspvar="dummy09" vartype="String" write="false">
		<% project_embargo = dummy09; %>
	</mm:field>
	<% if((numberOfProjects<4)||(Integer.parseInt(project_embargo)>(dd.getTime()/1000))){ 
		   String project_number = ""; %>
			<mm:field name="number" jspvar="dummy04" vartype="String" write="false">
				<% project_number = dummy04; %>
			</mm:field>
			<%-- look for translation --%>
		<%@include file="include/project_titel.jsp" %>
		<%@include file="include/project_subtitle.jsp" %>
		<%@include file="include/project_text.jsp" %>
		<% String project_verloopdatum = ""; %>
		<mm:field name="verloopdatum" jspvar="dummy08" vartype="String" write="false">
			<% project_verloopdatum = dummy08; %>
		</mm:field>
	
		<%-- start inner table with project --%>
		<a name="<%= project_number %>"></a>
		<table width="<%= windowWidth -100 %>" cellpadding="3" cellspacing="0" border="0">
      	<tr> 
        <%-- show image related to project if any --%>
				<td width="160" valign="top" align="center" class="background">
				<% String imagesList = ""; %>
				<% String images_number = ""; %>
						<mm:related path="posrel,images"
							constraints="images.titel_zichtbaar='1'"
							orderby="posrel.pos" directions="UP"
         	         fields="images.number">
							<mm:field name="images.number" jspvar="dummy" vartype="String" write="false">
							<%	images_number = dummy; 
								if(imagesList.equals("")) { imagesList = images_number; }
								else { imagesList = imagesList + "," + images_number ; }
							%></mm:field>
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
				<% timeStamp = project_embargo; %>
				<%@ include file="include/inc_date.jsp" %> 
				<%= thisDay %>&nbsp;<%= monthsStr[thisMonth] %>&nbsp;<%= thisYear %>
				<% timeStamp = project_verloopdatum; %>
				<%@ include file="include/inc_date.jsp" %> 
				<%= lan(language,"t/m") %> <%= thisDay %>&nbsp;<%= monthsStr[thisMonth] %>&nbsp;<%= thisYear %><br>
				<%= project_title %><br>
				</span>
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
							 class="light_boldlink" href="javascript:popup(
							'article_browser.jsp?project=<%= project_number %>&article=<%= article_number %>',
							'popup<%= article_number %>',
							'yes')">
							<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name="pijl<%= project_number %>_article">&nbsp;<%= lan(language,"Tekst van de publicatie") %>
					</A><br>
					</mm:field>
				</mm:related>
				<%-- contacts --%>
				<mm:related path="readmore,organisatie,contentrel,pagina"
					constraints="pagina.titel = 'Links'"
					fields="organisatie.number,organisatie.naam">
					<A onmouseover="changeDoubleImages('pijl<%= project_number %>_contact', 'media/arrow_right_dr.gif', 'nav_<%= lan(language,"Links") %>', 'media/arrow_down_dr.gif'); window.status=''; return true;"
					    onmouseout="changeDoubleImages('pijl<%= project_number %>_contact', 'media/arrow_right_lg.gif', 'nav_<%= lan(language,"Links") %>', 'spacer.gif'); window.status=''; return true;"
				  	 	 class="light_boldlink" target="_top" href="index.jsp?page=links&<mm:field name="organisatie.number" />">
					   	<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name="pijl<%= project_number %>_contact">&nbsp;<mm:field name="organisatie.naam" />
					</A><br>
				</mm:related>	
				<%-- back to previous page --%>
				<A onmouseover="changeImages('pijl<%= project_number %>_terug', 'media/arrow_right_dr.gif'); window.status=''; return true;" 
			  		onmouseout="changeImages('pijl<%= project_number %>_terug', 'media/arrow_right_lg.gif'); window.status=''; return true;" class="light_boldlink"
					target="_top" href="javascript:history.go(-1);">
		  			<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name=pijl<%= project_number %>_terug>&nbsp;<%= lan(language,"terug") %>
				</A>
				<%-- back to the top of the page --%>
				<A onmouseover="changeImages('pijl<%= project_number %>_top', 'media/arrow_up_dr.gif'); window.status=''; return true;"
			  		onmouseout="changeImages('pijl<%= project_number %>_top', 'media/arrow_up_lg.gif'); window.status=''; return true;" class="light_boldlink"
					href="#top">
					<IMG height=12 alt="" hspace=0 src="media/arrow_up_lg.gif" width=8 align=absMiddle border=0 name=pijl<%= project_number %>_top>&nbsp;<%= lan(language,"top") %>
				</A><br>
				<img src="media/spacer.gif" width="<%= windowWidth -100 -160 %>" height="1">
			</td>
	      </tr>
   	 </table>
		<% numberOfProjects++; %>
	<% } %>
	<%-- end inner table with project --%>
	</mm:node>
</mm:list>

</td></tr>
</table>

</mm:cloud>

</cache:cache>

<% } %>
