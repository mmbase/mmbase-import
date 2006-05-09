<% 	ddd.setTime(Long.parseLong(timestr)*1000);
	cal.setTime(ddd);
%><%=  days[cal.get(Calendar.DAY_OF_WEEK)-1].toLowerCase() 
%> <%= cal.get(Calendar.DAY_OF_MONTH) 
%> <%= months[cal.get(Calendar.MONTH)].toLowerCase() 
%> <%= cal.get(Calendar.YEAR) %>
