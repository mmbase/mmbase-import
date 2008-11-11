<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<%@include file="include/inc_language.jsp" %>
<%@include file="include/inc_windowwidth.jsp" %>
<%@include file="include/inc_initdate.jsp" %>


<% String pageId = request.getParameter("page") ;
   if (pageId == null ) pageId = "";
   if (!pageId.equals("")){
%>
<%-- following piece of code depends on page, language and windowWidth --%>
<% String cacheKey = "links_" + pageId + "_" + language + "_" + windowWidth; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" ><!-- <%= new java.util.Date() %> -->

<mm:cloud>

<table width="<%= windowWidth %>" cellpadding="0" cellspacing="0" border="0">
 <tr>
  <td valign="top" align="center">
  
  <%-- begin inner table with page navigation --%>
  <table width="<%= windowWidth-100 %>" cellpadding="0" cellspacing="0" border="0">
   	   				
		<%-- list the contact related to this page --%>
		<tr><td class="background"> 
			<mm:list nodes="<%= pageId %>" path="pagina,contentrel,organisatie" fields="organisatie.naam,organisatie.number"
				orderby="organisatie.naam" directions="UP">
				<mm:field name="organisatie.number" jspvar="contact_number" vartype="String" write="false">
				<A onmouseover="changeImages('pijl<%= contact_number %>', 'media/arrow_right_dr.gif'); window.status=''; return true;"
				   onmouseout="changeImages('pijl<%= contact_number %>', 'media/arrow_right_lg.gif'); window.status=''; return true;"
				   class="light_boldlink" href="#<%= contact_number %>">
					<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name=pijl<%= contact_number %>>&nbsp;<mm:field name="organisatie.naam" />
				</A><br>
				</mm:field>
			</mm:list>
		</td></tr>
  </table><br>
  <%-- end inner table with page navigation --%>
	<%-- list contacts --%>
	<mm:list nodes="<%= pageId %>" path="pagina,contentrel,organisatie,posrel,organisatie_type"
				fields="organisatie.number,organisatie.titel_zichtbaar,organisatie.naam,organisatie.bezoekadres,organisatie.bezoekadres_postcode,organisatie.plaatsnaam,organisatie.land,organisatie.telefoonnummer,organisatie.email,organisatie_type.naam"
				orderby="organisatie.naam" directions="UP">
		<% String contact_number = ""; %>
		<mm:field name="organisatie.number" jspvar="dummy01" vartype="String" write="false">
			<% contact_number = dummy01; %>
		</mm:field>	
		<% String contact_status = ""; %>
		<mm:field name="organisatie.titel_zichtbaar" jspvar="dummy01" vartype="String" write="false">
			<% contact_status = dummy01; %>
		</mm:field>	
		<% String contact_companyname = ""; %>
		<mm:field name="organisatie.naam" jspvar="dummy04" vartype="String" write="false">
			<% contact_companyname = dummy04; %>
		</mm:field>	
		<% String contact_address = ""; %>
		<mm:field name="organisatie.bezoekadres" jspvar="dummy08" vartype="String" write="false">
			<% contact_address = dummy08; %>
		</mm:field>	
		<% String contact_postcode = ""; %>
		<mm:field name="organisatie.bezoekadres_postcode" jspvar="dummy09" vartype="String" write="false">
			<% contact_postcode = dummy09; %>
		</mm:field>	
		<% String contact_city = ""; %>
		<mm:field name="organisatie.plaatsnaam" jspvar="dummy10" vartype="String" write="false">
			<% contact_city = dummy10; %>
		</mm:field>	
		<% String contact_country = ""; %>
		<mm:field name="organisatie.land" jspvar="dummy11" vartype="String" write="false">
			<% contact_country = dummy11; %>
		</mm:field>	
		<% String contact_business_phone = ""; %>
		<mm:field name="organisatie.telefoonnummer" jspvar="dummy12" vartype="String" write="false">
			<% contact_business_phone = dummy12; %>
		</mm:field>	
		<% String contact_email = ""; %>
		<mm:field name="organisatie.email" jspvar="dummy13" vartype="String" write="false">
			<% contact_email = dummy13; %>
		</mm:field>	
		<%-- global variable for use to link to collection in cv --%>
		<% String contact_type = ""; %>
		<mm:field name="organisatie_type.naam" jspvar="dummy14" vartype="String" write="false">
			<% contact_type = dummy14; %>
		</mm:field>	

	<%-- start innertable with description of contact --%>
	<a name="<%= contact_number %>"></a>
	<table width="<%= windowWidth-100 %>" cellpadding="3" cellspacing="0" border="0">
      <tr> 
        <%-- show image related to contact if any --%>
		<td width="160" valign="top" align="center" class="background">
		<% String imagesList = ""; %>
		<% String images_number = ""; %>
			<mm:node element="organisatie">
				<mm:related path="posrel,images"
					constraints="images.titel_zichtbaar='1'"
					orderby="posrel.pos" directions="UP"
         	       fields="images.number">
					<mm:field name="images.number" jspvar="dummy" vartype="String" write="false">
						<%	images_number = dummy; 
							if(imagesList.equals("")) { imagesList = images_number; }
							else { imagesList = imagesList + "," + images_number; }
						%>
					</mm:field>
	        </mm:related>
			<%--/mm:node--%>  
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

				<td valign="top" class="background">  
			<% boolean urlFound = false; %>
				<mm:related path="posrel,link"
					fields="link.url"
					constraints="link.titel_zichtbaar='1'">
					<mm:field name="link.url">
						<mm:isnotempty>
						<A onmouseover="changeImages('pijl_url<%= contact_number %>_link', 'media/arrow_right_dr.gif'); window.status=''; return true;"
						onmouseout="changeImages('pijl_url<%= contact_number %>_link', 'media/arrow_right_lg.gif'); window.status=''; return true;"
						class="dark_boldlink" href="<mm:field name="link.url" />" target="_blank">
							<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name=pijl_url<%= contact_number %>_link>&nbsp;<%= contact_companyname %> (<mm:field name="link.url" />)</a>
						<% urlFound = true; %>
						</mm:isnotempty>
					</mm:field>
				</mm:related>
			<% if(!urlFound) { %>
					<span class="bold"><%= contact_companyname %></span>
			<% } %>
				<br>
		
			<% if(contact_status.equals("1")) { %>
		
			<% if(!contact_companyname.equals("null")&&!contact_companyname.equals("")) { %>	
				<%= contact_companyname %><br>
			<% } %>
			<% if(!contact_address.equals("null")&&!contact_address.equals("")) { %>
				<%= contact_address %><br>
			<% } %>
			<% if(!contact_postcode.equals("null")&&!contact_postcode.equals("")
				&&!contact_city.equals("null")&&!contact_city.equals("")){ %>
				<%= contact_postcode %> <%= contact_city %><br>
			<% } %>
			<% if(!contact_country.equals("null")&&!contact_country.equals("")) { %>
				<%= contact_country %><br>
			<% } %>
			<% if(!contact_business_phone.equals("null")&&!contact_business_phone.equals("")) { %>
				<%= contact_business_phone %><br>
			<% } %>
			<% if(!contact_email.equals("null")&&!contact_email.equals("")) { %>
				<A onmouseover="changeImages('pijl_email<%= contact_number %>_email', 'media/arrow_right_dr.gif'); window.status=''; return true;"
				   onmouseout="changeImages('pijl_email<%= contact_number %>_email', 'media/arrow_right_lg.gif'); window.status=''; return true;"
				   class="dark_boldlink" href="mailto:<%= contact_email %>">
				<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name=pijl_email<%= contact_number %>_email>&nbsp;<%= contact_email %>
				</a><br>
			<% } %>

			<% } %> <%-- end if contact_status equals Zichtbaar --%>
		
			<%-- list the links from this link to other parts of the site --%>
			<%-- projects --%>
				<mm:related path="readmore,projects,posrel,projecttypes"
					   constraints="projects.titel_zichtbaar='1'"
					   fields="projects.number,projects.embargo,projecttypes.name">
					<% String project_number = ""; %>
					<mm:field name="projects.number" jspvar="dummy14" vartype="String" write="false">
						<% project_number = dummy14; %>
					</mm:field>	
				<% String project_type = ""; %>
					<mm:field name="projecttypes.name" jspvar="dummy15" vartype="String" write="false">
					<% project_type = dummy15; %>
					</mm:field>	
					<mm:field name="projects.embargo" jspvar="project_embargo" vartype="String" write="false">
					<% timeStamp = project_embargo; %>
					</mm:field>
					<%@ include file="include/inc_date.jsp" %>
					<%-- docentschap implies the categories congres, adviseurschap and werkperiode --%>
				<% String project_category = project_type; %>
				<% if(project_category.equals("Congres")
						|| project_category.equals("Adviseurschap")
						|| project_category.equals("Werkperiode")) {
						project_category = "Docentschap";	 
					} %>
					<A onmouseover="changeDoubleImages('pijl<%= project_number %>', 'media/arrow_right_dr.gif', 'nav_CV', 'media/arrow_down_dr.gif'); window.status=''; return true;"
					 onmouseout="changeDoubleImages('pijl<%= project_number %>', 'media/arrow_right_lg.gif', 'nav_CV', 'spacer.gif'); window.status=''; return true;"
					 class="light_boldlink" target="_top" href="index.jsp?page=cv&iart=<%= project_category %>&<%= project_number %>">
						<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name="pijl<%= project_number %>">&nbsp;<%= lan(language,project_type) %> (<%= thisYear %>)
					</A><br>
				</mm:related>

				<%-- contacts of category collection --%>
				<% if(contact_type.equals("Collectie")) { %>
					<% imagesList = ""; %>
					<% images_number = ""; %>
						<mm:related path="stock,items,posrel,images"
							constraints="items.titel_zichtbaar = '1'"
							orderby="posrel.pos" directions="UP"
      		  	        fields="images.number">
							<mm:field name="images.number" jspvar="dummy" vartype="String" write="false">
								<%	images_number = dummy; 
									if(imagesList.equals("")) { imagesList = images_number; }
									else { imagesList = imagesList + "," + images_number; }
								%>
							</mm:field>
						</mm:related>
					<% if(!imagesList.equals("")) { %>
						<A onmouseover="changeImages('pijl<%= contact_number %>_piece', 'media/arrow_right_dr.gif'); window.status=''; return true;"
						 onmouseout="changeImages('pijl<%= contact_number %>_piece', 'media/arrow_right_lg.gif'); window.status=''; return true;"
						 class="light_boldlink" 
						 href="javascript:popup('image_gallery.jsp?imageId=<%= imagesList %>','popup<%= images_number %>','no')">
						<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name="pijl<%= contact_number %>_piece">&nbsp;<%= lan(language,"Aangekocht werk") %> 
						</A><br>
					<% } %>
						<A onmouseover="changeDoubleImages('pijl<%= contact_number %>_collectie', 'media/arrow_right_dr.gif', 'nav_CV', 'media/arrow_down_dr.gif'); window.status=''; return true;"
						 onmouseout="changeDoubleImages('pijl<%= contact_number %>_collectie', 'media/arrow_right_lg.gif', 'nav_CV', 'spacer.gif'); window.status=''; return true;"
						 class="light_boldlink" target="_top" href="index.jsp?page=cv&iart=Collectie">
						<IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name="pijl<%= contact_number %>_collectie">&nbsp;<%= lan(language,"Andere collecties") %> 
						</A><br>
				<% } %>
			</mm:node>	
		
		<%-- back to the previous page --%>		  
		<A onmouseover="changeImages('pijl<%= contact_number %>_terug', 'media/arrow_right_dr.gif'); window.status=''; return true;"
		     onmouseout="changeImages('pijl<%= contact_number %>_terug', 'media/arrow_right_lg.gif'); window.status=''; return true;"
		  	 class="light_boldlink" href="javascript:history.go(-1);">
		  <IMG height=12 alt="" hspace=0 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name=pijl<%= contact_number %>_terug>&nbsp;<%= lan(language,"terug") %>
		</A>
		<%-- back to the top of the page --%>
		<A onmouseover="changeImages('pijl<%= contact_number %>_top', 'media/arrow_up_dr.gif'); window.status=''; return true;"
  	         onmouseout="changeImages('pijl<%= contact_number %>_top', 'media/arrow_up_lg.gif'); window.status=''; return true;"
  			 class="light_boldlink" href="#top">
  		  <IMG height=12 alt="" hspace=0 src="media/arrow_up_lg.gif" width=8 align=absMiddle border=0 name='pijl<%= contact_number %>_top'>&nbsp;<%= lan(language,"top") %>
  		</A><br>
		<img src="media/spacer.gif" width="<%= windowWidth -100 -160 %>" height="1">
		</td>
      </tr>
    </table>

<%-- end inner table with contact --%>
</mm:list>

</td></tr>
</table>

</mm:cloud>

</cache:cache>
<% } %>

