<mm:field name="artikel.number" jspvar="articles_number" vartype="String" write="false"
	><mm:notpresent referid="notitle"
		><mm:field name="artikel.titel" jspvar="articles_title" vartype="String" write="false"
			><% if(articles_title.indexOf("#NZ#")==-1) { %><div class="title"><%= articles_title %></div><% } 
		%></mm:field
	></mm:notpresent
><mm:field name="artikel.titel_fra"><mm:isnotempty><div class="subtitle"><mm:write /></div></mm:isnotempty></mm:field
><mm:field name="artikel.intro"
/><mm:list nodes="<%= articles_number %>" path="artikel,posrel,paragraaf"
		orderby="posrel.pos" directions="UP"
		fields="paragraaf.number,paragraaf.titel,paragraaf.omschrijving"
><table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr><td><p><mm:field name="paragraaf.number" jspvar="paragraaf_number" vartype="String" write="false"
		><%-- see the types/images_position at the editwizards
				<option id="1">rechts</option>
				<option id="2">links</option>
				<option id="3">rechts klein</option>
				<option id="4">links klein</option>
				<option id="5">rechts medium</option>
				<option id="6">links medium</option>
				<option id="7">volle breedte</option>
		--%><mm:list nodes="<%= paragraaf_number %>" path="paragraaf,posrel,images"  max="1"
			><mm:field name="posrel.pos" jspvar="posrel_pos" vartype="Integer" write="false"
				><%	int image_position = 3;
					try { image_position = posrel_pos.intValue(); } catch (Exception e) { } 

				// large image, no spacer between table and text
				if(image_position==7) {
					imageTemplate = "+s(368)";
				%><table cellspacing="0" cellpadding="0" border="0" align="center">
					<tr><td colspan="3" class="titlebar"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
					<tr><td class="titlebar"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td>
						<td><img src="<%@include file="../imagessource.jsp" %>" alt="" border="0"></td>
						<td class="titlebar"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
					<tr><td colspan="3" class="titlebar"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
					<tr><td colspan="3"><img src="media/spacer.gif" alt="" border="0" width="1" height="5"></td></tr>
					</table><br><%

				// medium or small image, spacer between table and text
				} else {
					if((2<image_position)&&(image_position<5)) { imageTemplate = "+s(60)"; }
					if((4<image_position)&&(image_position<7)) { imageTemplate = "+s(180)"; }
					boolean rightAlign = false;
					if((image_position%2)==1){	rightAlign = true; }
				%><table cellspacing="0" cellpadding="0" border="0" width="1" <%
					if(rightAlign){	%> align="right" <%	} else { %> align="left" <%	} %>>
					<tr><td colspan="4"><img src="media/spacer.gif" alt="" border="0" width="1" height="4"></td></tr>
					<tr><% if(rightAlign){ %><td rowspan="4"><img src="media/spacer.gif" alt="" border="0" width="10" height="1"></td><% } %>
						<td colspan="3" class="titlebar"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td>
						<% if(!rightAlign){ %><td rowspan="4"><img src="media/spacer.gif" alt="" border="0" width="10" height="1"></td><% } %>
					</tr>
					<tr><td class="titlebar"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td>
						<td><a href="#" onClick="javascript:launchCenter('includes/slideshow.jsp?w=<%= subsiteID 
								%>&p=<%= paginaID %>&u=-1&i=<mm:field name="images.number" />', 'center', 550, 740);setTimeout('newwin.focus();',250);">
							<img src="<%@include file="../imagessource.jsp" %>" alt="" border="0"></a></td>
						<td class="titlebar"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
					<tr><td colspan="3" class="titlebar"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
					<mm:field name="images.title" jspvar="images_title" vartype="String" write="false"
						><% if(!images_title.equals("")&&images_title.indexOf("#NZ#")==-1) {
							%><tr><td colspan="3" class="subtitlebar"><%= images_title %></td></tr><%
						} %></mm:field
					><tr><td colspan="3"><img src="media/spacer.gif" alt="" border="0" width="1" height="5"></td></tr>
					</table><% 
				}%></mm:field
	></mm:list
	><mm:field name="paragraaf.titel" jspvar="paragraaf_title" vartype="String" write="false"
			><% if(paragraaf_title.indexOf("#NZ#")==-1) { %><div class="subtitle"><%= paragraaf_title %></div><% } 
	%></mm:field
	><mm:field name="paragraaf.omschrijving" 
	/><mm:node number="<%= paragraaf_number%>" 
		><%@include file="attachment.jsp" 
	%></mm:node
	><mm:list nodes="<%= paragraaf_number %>" path="paragraaf,posrel,urls" 
		><mm:field name="urls.pretext" />
		<a target="_blank" href="<mm:field name="urls.url" />"><mm:field name="urls.description" /></a>
		<mm:field name="urls.posttext" 
	/></mm:list
	><mm:list nodes="<%= paragraaf_number %>" path="paragraaf,readmore,pagina" 
		fields="pagina.number,pagina.titel,readmore.readmore"
		><mm:field name="readmore.readmore"
		/> <a href="<mm:url page="index.jsp"><mm:param name="p"><mm:field name="pagina.number" /></mm:param></mm:url
			>" ><mm:field name="pagina.titel" /></a>
	</mm:list>
	<br>
</mm:field
></td></tr></table>
</mm:list
></mm:field>