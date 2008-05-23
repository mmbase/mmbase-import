<mm:node element="paragraaf" jspvar="paragraph">
   <%
   if(showdotline.equals("1")&&showNextDotLine) { 
      %><table class='dotline'><tr><td height="3"></td></tr></table><%      
   } else {
      %><table style="width:100%;"><tr><td height="3"></td></tr></table><%
   }
   showNextDotLine = true; 
   %><a name="<%= iParCntr %>" id="<%= iParCntr %>"></a><% iParCntr++; %>
   <% 
   floatingText = true; %>
   <mm:related path="posrel,images" max="1">
      <mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false">
         <% floatingText = !posrel_pos.equals("6"); %>
      </mm:field>
   </mm:related>
   <% if(!floatingText) { %><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="vertical-align:top;"><% } %>
   <%@include file="../includes/image_logic.jsp" %>
   <% if(!floatingText) { %></td><td style="vertical-align:top;"><% } %>
   <mm:field name="titel_zichtbaar">
      <mm:compare value="0" inverse="true">
         <%
         text = LocaleUtil.getField(paragraph,"titel",language, "");
         if(!"".equals(text)) {
            %><span class="colortitle"><%= text %></span><br/><%
         }
         %>
	   </mm:compare>
	</mm:field>
   <%
   text = LocaleUtil.getField(paragraph,"omschrijving",language, "");
   if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { 
      %><%= text %><% 
      if(text.toUpperCase().indexOf("<P>")==-1) { %><br/><% }
   } 
   %>
   <mm:relatednodes type="attachments" orderby="title">
      <a href="<mm:attachment />" title="download <mm:field name="filename" />" class="attachment"><mm:field name="title" /></a><br/>
   </mm:relatednodes>
   <mm:relatednodes type="link" orderby="titel">
      <a href="<mm:field name="url" />" title="<mm:field name="alt_tekst" />" target="<mm:field name="target" />" class="url"><mm:field name="titel" /></a><br/>
	</mm:relatednodes>
   <% if(!floatingText) { %></td></tr></table><% } %>
</mm:node>