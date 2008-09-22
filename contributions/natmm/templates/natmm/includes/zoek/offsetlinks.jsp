<% String sHref = (isIE ? "style=\"cursor:hand;text-decoration:underline;\"" : "href='javascript:void(0)'" );

int pagingOffset = 10; //Show 10 clickable pages to go to, next 10 are accessible by the >> signs.
int i = pagingOffset * (thisOffset / pagingOffset);

if(thisOffset>pagingOffset-1) { 
  %>&nbsp;<a <%= sHref %> onClick="eventForm.offset.value='<%= i-1 %>';eventForm.submit();"><<</a>  <%
} 

for(int b=0; b < 10 && (i < ((listSize-1)/pageSize + 1)); b++) {
   if(i==thisOffset) {
      %>&nbsp;<span style="color:red;"><%= i+1 %></span>  <%
   } else { 
      %>&nbsp;<a <%= sHref %> onClick="eventForm.offset.value='<%= i %>';eventForm.submit();"><%= i+1 %></a>  <%
   }
   i++;
}
 
if((i*pageSize) < listSize) {
  %>&nbsp;<a <%= sHref %> onClick="eventForm.offset.value='<%= i %>';eventForm.submit();">>></a><%
}
%>