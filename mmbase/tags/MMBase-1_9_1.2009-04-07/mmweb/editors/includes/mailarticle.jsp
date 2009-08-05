<%@include file="../includes/templateheader.jsp" 
%><%@include file="../includes/cleantext.jsp" 

%><mm:import externid="postit">false</mm:import
><mm:compare referid="postit" value="true"
	><div align="center"><h3>Het volgende artikel is verstuurd naar im@vpro.nl</h3>
	<a href="" onClick="javascript:self.close();">sluit dit venster</a>
	</div></mm:compare
><%
String articleTitle = "";
String articleText = ""; 
%><mm:list nodes="<%= articleId %>" path="article"
	><mm:field name="article.title" jspvar="article_title" vartype="String" write="false"
		><% if(article_title.indexOf("#NZ#")==-1) {
				articleTitle = article_title;
				articleText += article_title + "\n\n";
			} else {
				articleTitle = article_title.substring(4);
			}
	%></mm:field
	><mm:field name="article.subtitle" jspvar="article_subtitle" vartype="String" write="false"
		><% articleText += article_subtitle  + "\n\n"; 
	%></mm:field
	><mm:field name="article.introduction" jspvar="article_introduction" vartype="String" write="false"
		><% articleText += article_introduction  + "\n\n"; 
	%></mm:field
	><mm:field name="article.number" jspvar="article_number" vartype="String" write="false"
	><mm:list nodes="<%= article_number %>" path="article,posrel,paragraph"
			orderby="posrel.pos" directions="UP"
		><mm:field name="paragraph.number" jspvar="paragraph_number" vartype="String" write="false"
			><mm:field name="paragraph.title" jspvar="paragraph_title" vartype="String" write="false"
				><% if(paragraph_title.indexOf("#NZ#")==-1) { articleText += paragraph_title + "\n\n"; } 
			%></mm:field
			><mm:field name="paragraph.body" jspvar="paragraph_body" vartype="String" write="false"
				><% articleText += paragraph_body + "\n\n"; 
			%></mm:field
			><mm:list nodes="<%= paragraph_number %>" path="paragraph,posrel,urls" 
				><mm:field name="urls.pretext" jspvar="urls_pretext" vartype="String" write="false"
					><%  articleText += urls_pretext + " ";
				%></mm:field
				><mm:field name="urls.description" jspvar="urls_description" vartype="String" write="false"
					><%  articleText += urls_description;
				%></mm:field
				><mm:field name="urls.url" jspvar="urls_url" vartype="String" write="false"
					><%  articleText += " ( " + urls_url + " ) ";
				%></mm:field
				><mm:field name="urls.posttext" jspvar="urls_posttext" vartype="String" write="false"
					><%  articleText += urls_posttext + "\n\n";
				%></mm:field
			></mm:list
		></mm:field
	></mm:list
	></mm:field
	><mm:field name="article.creditline" jspvar="date" vartype="String" write="false"
	><mm:field name="article.quote" jspvar="author" vartype="String" write="false"
	><% if(!author.equals("")||!date.equals("")) {
		 articleText += "Laatst bijgewerkt"; 
		if(!date.equals("")) {  articleText += " op " + date; } 
		if(!author.equals("")) { articleText += " door " +  author; } 
	} 
	%></mm:field
	></mm:field
></mm:list
><%= replace(articleText,"\n","<br>") %>
<mm:compare referid="postit" value="true"
	><mm:createnode type="email"
		><mm:setfield name="subject"><%= articleTitle %></mm:setfield
		><mm:setfield name="from">intranet@vpro.nl</mm:setfield
		><mm:setfield name="to">hangyi@xs4all.nl</mm:setfield
		><mm:setfield name="replyto">hangyi@xs4all.nl</mm:setfield
		><mm:setfield name="body"><%= articleText %></mm:setfield
	></mm:createnode
	></mm:compare
><mm:compare referid="postit" value="false"
	><form action="mailarticle.jsp?article=<%= articleId %>&postit=true" method="POST">
		<input type="submit" name="Submit" value="Verstuur"  style="font-weight:bold;">
	</form></mm:compare>
<%@include file="../includes/templatefooter.jsp" %>
