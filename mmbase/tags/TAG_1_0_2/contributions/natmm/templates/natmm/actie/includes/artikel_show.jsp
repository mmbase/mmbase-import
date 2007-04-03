<mm:node number="<%= artikelID%>" notfound="skip">
   <%@include file="../includes/image_logic.jsp" %>
   <mm:node number="<%= paginaID %>">
	   <div class="colortitle" style="font:bold 110%;"><mm:field name="titel"/></div>
      <div style="padding-bottom:5px;"><b><mm:field name="kortetitel"/></b></div>
   </mm:node>
   <span style="font:bold 110%;color:red"></span>
   <span class="colortitle"><mm:field name="titel"/></span>
   <span class="colortxt"><mm:field name="begindatum" jspvar="artikel_begindatum" vartype="String" write="false"
   ><mm:time time="<%=artikel_begindatum%>" format="d MMM yyyy"/></mm:field></span><br/>
   <mm:field name="intro" jspvar="text" vartype="String" write="false">
   <% if(text!=null && !HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) {
        %><b><%= text %></b><%
      } %>
   </mm:field>
   <mm:field name="tekst" />
   <mm:relatednodes type="attachments" path="related,attachments" orderby="attachments.title">
   <% String imgName = ""; 
      String docType = ""; %>
      <mm:field name="filename" jspvar="dummy" vartype="String" write="false">
          <%@include file="../includes/attachmentsicon.jsp"%>
      </mm:field>
      <mm:first>
          <table class="dotline"><tr><td height="3"></td></tr></table>
      </mm:first>
      <span style="padding-left:5px; padding-right:5px"><a href="<mm:attachment />"><img src="../<%= imgName 
         %>" alt="download <%= docType %>: <mm:field name="title"
         />" border="0" style="vertical-align:text-bottom" /></a></span>
   </mm:relatednodes>
   <% int iParCntr = 1;
      boolean showNextDotLine = false;
      boolean floatingText = true; %>
    <mm:field name="reageer" jspvar="showdotline" vartype="String" write="false"
       ><mm:related path="posrel,paragraaf" fields="paragraaf.number" orderby="posrel.pos"
          ><%@include file="../../includes/relatedparagraph.jsp" 
       %></mm:related
    ><mm:related path="readmore,paragraaf" fields="paragraaf.number" orderby="readmore.pos"
       ><%@include file="../../includes/relatedparagraph.jsp" 
    %></mm:related
   ></mm:field>
</mm:node>