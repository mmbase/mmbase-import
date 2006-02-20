<%
   String link ="";
%>
<mm:node element="urls">
   <mm:field name="url" jspvar="url" vartype="String" write="false">
      <%
         if(url.indexOf("http://") > -1)
         {
            link = url;
            %><img src="<mm:write referid="path_segment"/>gfx/http_url.gif"/> websites: <a href="<%= url %>" class="urls"><mm:field name="name"/></a><%
         }
         if(url.indexOf("mailto:") > -1)
         {
            link = url;
            %>
               <mm:field name="name" jspvar="name" vartype="String" write="false">
                  <mm:isnotempty>
                  <%
                     link += "?subject=" + name;
                  %>
                  </mm:isnotempty>
               </mm:field>
               <img src="<mm:write referid="path_segment"/>gfx/email_url.gif"/> email: <a href="<%= url %>" class="urls"><mm:field name="name"/></a>
            <%
         }
      %>
   </mm:field>
</mm:node>
