<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" constraints="contentrel.pos=1"
		><mm:field name="artikel.intro" jspvar="articles_intro" vartype="String" write="false"
			><p style="padding-top:10px;"><%@include file="../shop/cleanarticleintro.jsp" %></p>
		</mm:field
	></mm:list
></mm:cloud>
