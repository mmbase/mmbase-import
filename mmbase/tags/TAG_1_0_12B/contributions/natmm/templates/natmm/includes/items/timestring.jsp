<% cal.setTimeInMillis(Long.parseLong(timestr)*1000);
%><%=  days[cal.get(Calendar.DAY_OF_WEEK)-1].toLowerCase() 
%> <%= cal.get(Calendar.DAY_OF_MONTH) 
%> <%= months[cal.get(Calendar.MONTH)].toLowerCase() 
%> <%= cal.get(Calendar.YEAR) %>
