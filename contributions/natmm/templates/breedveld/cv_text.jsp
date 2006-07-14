<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<%@include file="include/inc_language.jsp" %>
<%@include file="include/inc_initdate.jsp" %>
<%@include file="include/inc_projectselection.jsp" %>

<% String pageId = request.getParameter("page") ;
   if (pageId == null ) pageId = "";
   if (!pageId.equals("")){
%>

<%-- following piece of code depends on language  --%>
<% String cacheKey = "cvTekstVersie_" + language ; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" ><!-- <%= new java.util.Date() %> -->


<mm:cloud>

<html>
<head><title>Marian Breedveld - CV</title></head>
<body>
<span style="FONT-FAMILY: Courier New, Courier, mono; FONT-SIZE: 14px;">
<div align="right">
	<a href="" onClick="self.print()"><%= lan(language,"print") %></a>&nbsp;|&nbsp; 
	<a href="javascript:self.close()" onClick="self.close(); return false;"><%= lan(language,"sluit dit venster") %></a>
</div>
<b>MARIAN BREEDVELD - Rotterdam (NL)</b><br><br>

<% String lang = "";
	if (language.equals("english")){
		lang = "_eng";
	} else if (language.equals("french")) {
		lang = "_fra";
	}%>
							
<% int categoryCount = -1; %>
<% String projecttypes_title = ""; %>
<mm:list nodes="<%= pageId %>"
	path="pagina,posrel,projecttypes"
	fields="posrel.pos,projecttypes.description"
	orderby="posrel.pos" directions="UP">
	<mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false" >
	<% if(Integer.parseInt(posrel_pos)!=categoryCount) {
			categoryCount=Integer.parseInt(posrel_pos); %>
			<b>
			<mm:field name="projecttypes.description" jspvar="projecttypes_descr" vartype="String" write="false">
				<%= lan(language,projecttypes_descr).toUpperCase() %>
			</mm:field>
			<mm:field name="projecttypes.name" jspvar="dummy" vartype="String" write="false">
				<% projecttypes_title = dummy; %>
				<%= selection(projecttypes_title,language) %>
			</mm:field>
			</b><br><br>
	<% if(projecttypes_title.equals("Collectie")) { %>
	<%-- selected category consists of organisaties; visibility based on status --%>
	<% String constraint = ""; %>
	<mm:list path="organisatie,posrel,organisatie_type"
		fields="organisatie.number,organisatie.naam,organisatie.titel_zichtbaar"
		constraints="organisatie_type.naam = 'Collectie' AND organisatie.titel_zichtbaar = '1'"
		orderby="organisatie.naam" directions="UP">
			<mm:field name="organisatie.naam" /><br>
	</mm:list>
	<br>
	<% } else if(projecttypes_title.equals("Opleiding")) {  %>
	<mm:list path="projecttypes,posrel,projects"
		constraints="projects.titel_zichtbaar='1' AND projecttypes.name = 'Opleiding'"
		orderby="projects.embargo" directions="DOWN">
		<mm:node element="projects">
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
			<%-- look for translation of title --%>
		<%@include file="include/project_titel.jsp" %>
		<% if( fromYear < toYear) { project_title = " - " + toYear + " " + project_title; }
			project_title = fromYear + project_title;
		%>
		<%= project_title %><br>
		</mm:node>
	</mm:list>
	<br>
	<% }  else { %>

<% String constraints = "projects.titel_zichtbaar = '1'"; %>
<mm:list nodes="<%= pageId %>" path="pagina,posrel,projecttypes,posrel,projects"
	constraints="<%= constraints %>"
	orderby="projects.embargo" directions="DOWN">
	<mm:node element="projects">	
	<%-- look for translation --%>
	<%@include file="include/project_titel.jsp" %>
	<%@include file="include/project_subtitle.jsp" %>
	<%@include file="include/project_text.jsp" %>
	<% String project_verloopdatum = ""; %>
		<mm:field name="verloopdatum" jspvar="dummy09" vartype="String" write="false">
			<% project_verloopdatum = dummy09; %>
		</mm:field>
	
		<b>
		<% if(projecttypes_title.indexOf("tentoonstelling")>0) { %>
			<% timeStamp = project_verloopdatum; %>
			<%@ include file="include/inc_date.jsp" %> 
			<%= thisDay %>&nbsp;<%= monthsStr[thisMonth] %>&nbsp;<%= thisYear %>
			<% timeStamp = project_verloopdatum; %>
			<%@ include file="include/inc_date.jsp" %> 
			<%= lan(language,"t/m") %> <%= thisDay %>&nbsp;<%= monthsStr[thisMonth] %>&nbsp;<%= thisYear %><br>
		<% } else { %>
			<% timeStamp = project_verloopdatum; %>
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
		<%= project_title %></b><br>
		<% if(project_subtitle!=null&&!project_subtitle.equals("")) { %> 
				<%= project_subtitle %><br>
		<% } %>
		<% if(project_text!=null&&!project_text.equals("")) { %>  
			<%= project_text %><br>
		<% } %>
  		<br>
	</mm:node>	
</mm:list> <%-- <mm:list path="list,posrel1,projecttypes,posrel2,project" --%>

<% } %> <%-- end list of projects in selected category --%>

<% } %> <%-- end if(Integer.parseInt(posrel_pos)!=categoryCount) --%>

</mm:field> <%-- end <mm:field name="posrel.pos" ..> --%>

</mm:list> <%-- end <mm:list nodes="<%= pageId %>"	path="page,poslang,list,posrel,projecttypes"	 --%>

</span>

</body>
</html>

</mm:cloud>

</cache:cache>

<% } %>
