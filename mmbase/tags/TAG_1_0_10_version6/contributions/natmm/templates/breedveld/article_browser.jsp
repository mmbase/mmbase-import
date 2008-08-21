<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<%@include file="include/inc_language.jsp" %>

<%
	String projectId = request.getParameter("project");
  	String articleId = request.getParameter("article");
%>

<%-- following piece of code depends on projectId, articleId, language and screenSize.width --%>
<% String cacheKey = "articleBrowser_" + projectId + "_" + articleId + "_" + language; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application"><!-- <%= new java.util.Date() %> -->

<mm:cloud>

<% String lang = "";
	String [] alt_langs = {"_eng","_fra"};

	if (language.equals("english")){
		lang = "_eng";
		alt_langs[0] = ""; alt_langs[0] = "_fra";
	} else if (language.equals("french")) {
		lang = "_fra";
		alt_langs[0] = ""; alt_langs[0] = "_eng";
	}%>

<%	int marginWidth = (800/6); 
	String sProject_title = ""; 
	String sProject_subtitle = ""; %> 

<mm:node number="<%= projectId %>">
	<%-- look for translation --%>
<%@include file="include/project_titel.jsp" %>
<% sProject_title = project_title; %>
<%@include file="include/project_subtitle.jsp" %>
<% sProject_subtitle = project_subtitle; %>
</mm:node>
<HTML>
<HEAD>
  <TITLE><%= sProject_title %></TITLE>
  <link rel="stylesheet" type="text/css" href="css/marianbreedveld.css">

  <% String openComment = "<!--"; %>
  <% String closeComment = "//-->"; %>
  <SCRIPT LANGUAGE="JavaScript">
		<%= openComment %>
		<%@include file="script/scr_mouseover.js" %>
		<%= closeComment %>
  </SCRIPT>
</HEAD>

<BODY class="background" topmargin="0" rightmargin="0" leftmargin="0">
<%-- make outertable to set left and rightmargin (netscape does not respond to leftmargin in body tag) --%>
<TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0" ALIGN="center">
<tr>
<td class="background"><img src="media/spacer.gif" width="<%= marginWidth %>" height="1"></td>
<td class="background">
  <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0" ALIGN="center">
  <TR>
  	<TD align="center" class="background">
	<br><br><br><br>
	<span class="bold" style="font-size:14pt"><%= sProject_title %></span><br>
	<%= sProject_subtitle %><br><br>
	<%-- inner table with articles of this project --%>
	<table BORDER="0" CELLSPACING="0" CELLPADDING="0">
	<% String articleConstraint = "artikel.titel_zichtbaar='1'"; %>
	<mm:list nodes="<%= projectId %>" path="projects,posrel,artikel"
		orderby="posrel.pos" directions="UP"
		constraints="<%= articleConstraint %>">
		<mm:node element="artikel">
			<tr>
			<td align="left" class="background">
			<mm:field name="number" jspvar="article_number" vartype="String" write="false">
				<A onmouseover="changeImages('pijl_<%= article_number %>', 'media/arrow_right_dr.gif'); window.status=''; return true;"
			    onmouseout="changeImages('pijl_<%= article_number %>', 'media/arrow_right_lg.gif'); window.status=''; return true;"
			  	 href="article_browser.jsp?project=<%= projectId %>&article=<%= article_number %>"
				 <% if(article_number.equals(articleId)) { %>
			 					class="dark_boldlink"
			 				<% } else { %>
			 					class="light_boldlink"
				<% } %>
				>
				<IMG height=12 alt="" hspace=4 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name=pijl_<%= article_number %> >
			</mm:field>
			<%@include file="include/article_title.jsp" %>
			</A>
			</td>
			</tr>
		</mm:node>
	</mm:list>
	</table>
	<%-- end inner table with articles of this project --%>
	</TD>
 </TR>
 <TR>
  	<td class="background"><img src="media/spacer.gif" width="500" height="20"></td>
 </TR>
 <TR>
	<TD align="left" class="background">
	<mm:list nodes="<%= articleId %>" path="artikel,posrel,paragraaf"
		orderby="posrel.pos" directions="UP">
		<mm:node element="paragraaf">
			<span class="bold">
		<% String paragraf_titel = "";
			String paragraf_text = "";
			String sFieldName = "titel" + lang; 
			String sTextFieldName = "omschrijving" + lang;%>
			<mm:field name="<%= sFieldName %>" jspvar="dummy" vartype="String" write="false">
			<% paragraf_titel = dummy; 
				int i = 0;
				while ((paragraf_titel==null||paragraf_titel.equals(""))&&(i<2)) {
					sFieldName = "titel" + alt_langs[i];
					sTextFieldName = "omschrijving" + alt_langs[i];
					i++;%>
					<mm:field name="<%= sFieldName %>" jspvar="dummy1" vartype="String" write="false">
						<% paragraf_titel = dummy1; %>
					</mm:field>
			<% } %>		
			</mm:field>
			<mm:field name="<%= sTextFieldName %>" jspvar="text" vartype="String" write="false">
				<% paragraf_text = text; %>
			</mm:field>
			<%= paragraf_titel %></span><br>
			<%= paragraf_text %><br><br>
		</mm:node>
	</mm:list>
	</TD>
 </TR>
 <TR>
  	<TD align="center" class="background">
	<%-- repeat inner table with articles of this project --%>
	<table BORDER="0" CELLSPACING="0" CELLPADDING="0">
	<mm:list nodes="<%= projectId %>" path="projects,posrel,artikel"
			orderby="posrel.pos" directions="UP"
			constraints="<%= articleConstraint %>">
		<tr>
		<td align="left" class="background">
		<mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false">
		<A onmouseover="changeImages('pijl_repeat_<%= article_number %>', 'media/arrow_right_dr.gif'); window.status=''; return true;"
		     onmouseout="changeImages('pijl_repeat_<%= article_number %>', 'media/arrow_right_lg.gif'); window.status=''; return true;"
		  	 href="article_browser.jsp?project=<%= projectId %>&article=<%= article_number %>"
			 <% if(article_number.equals(articleId)) { %>
			 					class="dark_boldlink"
			 				<% } else { %>
			 					class="light_boldlink"
			<% } %>
		>
		<IMG height=12 alt="" hspace=4 src="media/arrow_right_lg.gif" width=8 align=absMiddle border=0 name=pijl_repeat_<%= article_number %> >
		</mm:field>
		<%@include file="include/article_title.jsp" %>
		</A>
		</td>
		</tr>
	</mm:list>
	</table>
	<%-- end inner table with articles of this project --%>
	</TD>
 </TR>
 <TR>
  	<td class="background"><img src="media/spacer.gif" width="500" height="20"></td>
 </TR>
 <TR>
	<TD align="center" class="background">
		<A class="light_boldlink" HREF="javascript:self.close()" onClick="self.close(); return false;">
			<%= lan(language,"sluit dit venster") %>
		</A>
	</TD>
 </TR>
 <TR>
  	<td class="background"><img src="media/spacer.gif" width="500" height="100"></td>
 </TR>
 </TABLE>
</td>
<td class="background"><img src="media/spacer.gif" width="<%= marginWidth %>" height="1"></td>
</tr>
</TABLE>
</BODY>
</HTML>
</mm:cloud>

</cache:cache>