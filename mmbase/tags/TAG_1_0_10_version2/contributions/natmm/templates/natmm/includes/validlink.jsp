<% if(!linkTXT.equals("")) {
   %>
   <div <mm:notpresent referid="divstyle">style="margin-bottom:9px;"</mm:notpresent
        ><mm:present referid="divstyle">style="<mm:write referid="divstyle" />"</mm:present
      >><%
   if(validLink){
      %><a href="<%= readmoreURL %>" <
            mm:notpresent referid="hrefclass">class="maincolor_link_shorty"</mm:notpresent
            ><mm:present referid="hrefclass">class="<mm:write referid="hrefclass" />"</mm:present><% 
            if(!readmoreTarget.equals("")){ %> target="<%= readmoreTarget %>"<% }
            if(!altTXT.equals("")){ %> title="<%= altTXT %>"<% } 
            %>><%= linkTXT %></a><%
   } else {
      %><span class="colortxt"><%= linkTXT %></span><%
   } 
   %></div><% 
} %>