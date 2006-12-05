<% String sHref = (isIE ? "style=\"cursor:hand;text-decoration:underline;\"" : "href='javascript:void(0)'" );
if(thisOffset>0) { 
  %>&nbsp;<a <%= sHref %> onClick="eventForm.offset.value='<%= thisOffset-1 %>';eventForm.submit();"><<</a>  <%
} 
for(int i=0; i < ((listSize-1)/pageSize + 1); i++) { 
     if((i>0)&&((i+1)%15==1)) { %><br/><% } 
     if(i==thisOffset) {
         %>&nbsp;<span style="color:red;"><%= i+1 %></span>  <%
     } else { 
         %>&nbsp;<a <%= sHref %> onClick="eventForm.offset.value='<%= i %>';eventForm.submit();"><%= i+1 %></a>  <%
     }
}
if(thisOffset+1<((listSize-1)/pageSize + 1)) { 
  %>&nbsp;<a <%= sHref %> onClick="eventForm.offset.value='<%= thisOffset+1 %>';eventForm.submit();">>></a><%
}
%>