<jsp:include page="includes/panno_extratext.jsp">
   <jsp:param name="o" value="<%= paginaID %>" />
</jsp:include>
<mm:node number="<%= paginaID %>" jspvar="thispage">
   <% boolean isFirst = true;
      String title_choice = thispage.getStringValue("titel_zichtbaar");
      if(false&&title_choice.equals("0")) { // this option is switched off 09.09.2005
         // no title
      } else if(title_choice.equals("2")) {
         %><span class="colortitle" <%= (isSubDir? "style='font:bold 110%;'" : "" ) %>><%= thispage.getStringValue("kortetitel").toUpperCase() %></span><br/><%
      } else {
         %><span class="colortitle" <%= (isSubDir? "style='font:bold 110%;'" : "" ) %>><%= thispage.getStringValue("titel").toUpperCase() %></span><br/><%
      }
   %>
   <mm:notpresent referid="nopage_description">
      <mm:field name="omschrijving" jspvar="omschrijving" vartype="String" write="false">
         <% if(omschrijving!=null&&!HtmlCleaner.cleanText(omschrijving,"<",">","").trim().equals("")) { 
            %><div style="margin-top:<%= (isFirst ? "2px" : "0px" ) %>"><%= omschrijving %></div>
            <% isFirst = false; %>
         <% } %>
      </mm:field>
   </mm:notpresent>
   <% if(!isFirst) { %>
   <mm:notpresent referid="nodotline">
      <table class="dotline"><tr><td height="3"></td></tr></table>
   </mm:notpresent>
   <% } %>
</mm:node>